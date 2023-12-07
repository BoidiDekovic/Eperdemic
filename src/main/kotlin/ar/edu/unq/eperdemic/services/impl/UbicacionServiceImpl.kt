package ar.edu.unq.eperdemic.services.impl

import ar.edu.unq.eperdemic.modelo.Ubicacion
import ar.edu.unq.eperdemic.modelo.UbicacionMongoDB
import ar.edu.unq.eperdemic.modelo.vector.Vector
import ar.edu.unq.eperdemic.modelo.exception.VectorNoPuedeSerContagiadoException
import ar.edu.unq.eperdemic.modelo.UbicacionNeo4j
import ar.edu.unq.eperdemic.modelo.exception.CaminoInvalidoException
import ar.edu.unq.eperdemic.persistencia.dao.*
import ar.edu.unq.eperdemic.services.UbicacionService
import ar.edu.unq.eperdemic.services.VectorService
import ar.edu.unq.eperdemic.services.exception.*
import ar.edu.unq.eperdemic.services.impl.helper.RNG
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.mongodb.core.geo.GeoJsonPoint
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import javax.persistence.EntityNotFoundException
@Service
@Transactional
class UbicacionServiceImpl() : UbicacionService {


    @Autowired
    private lateinit var ubicacionDAO: UbicacionDAO

    @Autowired
    private lateinit var ubicacionNeo4jDAO: UbicacionNeo4jDAO

    @Autowired
    private lateinit var ubicacionMongoDBDAO : UbicacionMongoDBDAO

    @Autowired
    private lateinit var vectorDAO: VectorDAO

    @Autowired
    private lateinit var distritoDAO: DistritoDAO

    @Autowired
    private lateinit var vectorService: VectorService


    @Autowired
    private lateinit var rng: RNG

    override fun crearUbicacion(ubicacion: Ubicacion, punto: GeoJsonPoint): Ubicacion {
        if (ubicacionDAO.findByNombre(ubicacion.nombre) != null
            && ubicacionNeo4jDAO.findByNombre(ubicacion.nombre) != null
        ) {
            throw UbicacionConNombreYaExisteException(ubicacion.nombre!!)
        }
        ubicacionNeo4jDAO.save(UbicacionNeo4j.desdeModelo(ubicacion))

        val distrito = distritoDAO.findDistritoIdConPunto(punto)
        if (distrito != null) {
            val ubicacionMongoDB = UbicacionMongoDB(ubicacion.nombre!!, punto)
            ubicacionMongoDBDAO.save(ubicacionMongoDB)
            distrito.agregarUbicacion(ubicacionMongoDB)
            distritoDAO.save(distrito)

        } else {
            throw ElPuntoNoEntraEnNingunDistritoException()
        }

        return ubicacionDAO.save(ubicacion)
    }

    override fun actualizarUbicacion(ubicacion: Ubicacion, punto: GeoJsonPoint?) {
        ubicacion.id ?: throw UbicacionNoExistenteException()
        var ubicacionOriginal = this.recuperarUbicacion(ubicacion.id!!)
        if(ubicacionDAO.findByNombre(ubicacion.nombre) != null
            && ubicacionOriginal.nombre != ubicacion.nombre ) { throw UbicacionConNombreYaExisteException(ubicacion.nombre!!)}

        ubicacionMongoDBDAO.save(UbicacionMongoDB(ubicacion.nombre!!, ubicacionMongoDBDAO.findByNombre(ubicacionOriginal.nombre)!!.punto!!))
        ubicacionDAO.save(ubicacion)
        ubicacionNeo4jDAO.save(UbicacionNeo4j(ubicacion.nombre!!))
    }

    override fun recuperarUbicacion(ubicacionId: Long): Ubicacion {
        return ubicacionDAO.findByIdOrNull(ubicacionId) ?: throw UbicacionNoExistenteException()
    }

    override fun recuperarUbicacionDeNeo4j(nombre: String): UbicacionNeo4j {
        val ubicacion = ubicacionNeo4jDAO.findByNombre(nombre) ?: throw UbicacionNoExistenteException()
        return ubicacion
    }

    override fun recuperarTodasLasUbicaciones(): List<Ubicacion> {
        return ubicacionDAO.findAll().toList()
    }

    override fun mover(vectorId: Long, ubicacionId: Long) {
        val vector = vectorService.recuperarVector(vectorId)
        val ubicacionActual = vector.ubicacion!!
        val ubicacionDestino = this.recuperarUbicacion(ubicacionId)

        if (ubicacionDestino == vector.ubicacion) {
            throw VectorYaEstaEnLaUbicacionException()
        }

        val ubicacionActualMongo = ubicacionMongoDBDAO.findByNombre(ubicacionActual.nombre)

        if (ubicacionMongoDBDAO.ubicacionesAMenosDe100Km(ubicacionActualMongo!!.punto!!, ubicacionDestino.nombre!!)
                .isEmpty()
                ||
            (!puedeViajarA(vector, ubicacionActual.nombre!!, ubicacionDestino.nombre!!))
        ) {
            throw UbicacionMuyLejanaException(ubicacionActual.nombre!!, ubicacionDestino.nombre!!)
        }

        if (!vector.tieneMutacionTeletransportacion()
            &&
            !ubicacionNeo4jDAO.puedeMoverseA(vector.caminosTransitables(), ubicacionActual.nombre!!, ubicacionDestino.nombre!!)
            ) {
            throw UbicacionNoAlcanzableException(ubicacionActual.nombre!!, ubicacionDestino.nombre!!)
        }

        moverVectorA(vector, ubicacionDestino)
    }

    override fun expandir(ubicacionId: Long) {
        try {
            val ubicacion = this.recuperarUbicacion(ubicacionId)
            val vectoresEnUbicacion = vectorDAO.findAllByUbicacionId(ubicacion.id!!)
            val vectoresInfectadosEnUbicacion = vectorDAO.recuperarTodosDeLaUbicacionInfectados(ubicacion.id!!)
            if (vectoresInfectadosEnUbicacion.isNotEmpty()) {
                val vectorRandom = vectoresInfectadosEnUbicacion[
                    rng.getRandomNumber(0, vectoresInfectadosEnUbicacion.lastIndex)]
                intentarContagiarAVectoresEnLaUbicacion(vectoresEnUbicacion, vectorRandom)
            }
        } catch (e: EntityNotFoundException) {
            throw UbicacionNoExistenteException()
        }
    }

    private fun intentarContagiarAVectoresEnLaUbicacion(vectoresEnUbicacion: List<Vector>, vector: Vector) {
        for (vectorEnUbicacion in vectoresEnUbicacion) {
            try {
                vector.intentarContagiarA(vectorEnUbicacion, rng)
            } catch (_: VectorNoPuedeSerContagiadoException) {
            }
        }
    }

    override fun recuperarUbicacionConNombre(nombreDeUbicacion: String): Ubicacion {
        return ubicacionDAO.findByNombre(nombreDeUbicacion) ?: throw UbicacionConNombreNoExistenteException(
            nombreDeUbicacion
        )
    }

    override fun conectar(nombreDeUbicacion1: String, nombreDeUbicacion2: String, tipoCamino: String) {
        try {
            val ubicacion1 = ubicacionNeo4jDAO.findByNombre(nombreDeUbicacion1) ?: throw UbicacionNoExistenteException()
            val ubicacion2 = ubicacionNeo4jDAO.findByNombre(nombreDeUbicacion2) ?: throw UbicacionNoExistenteException()
            ubicacion1.conectar(ubicacion2, tipoCamino)
            ubicacionNeo4jDAO.save(ubicacion1)
        } catch (e: CaminoInvalidoException) {
            throw CaminoInvalidoException()
        }

    }

    override fun conectados(nombreDeUbicacion: String): List<Ubicacion> {
        val ubicacion1 = ubicacionNeo4jDAO.findByNombre(nombreDeUbicacion) ?: return ArrayList()
        return ubicacionNeo4jDAO.conectados(ubicacion1.nombre!!).map { it.aModelo() }
    }

    override fun moverPorCaminoMasCorto(vectorId: Long, nombreDeUbicacion: String) {
        this.recuperarUbicacionConNombre(nombreDeUbicacion)
        val vector          = vectorService.recuperarVector(vectorId)
        val ubicacionActual = vector.ubicacion!!.nombre!!
        val rutaMasCorta    = ubicacionNeo4jDAO.caminoMasCorto(vector.caminosTransitables(), ubicacionActual, nombreDeUbicacion)
        if(rutaMasCorta.isEmpty()){
            throw UbicacionNoAlcanzableException(ubicacionActual, nombreDeUbicacion)
        }
        for (ubicacion in rutaMasCorta){
            val proximaUbicacion = this.recuperarUbicacionConNombre(ubicacion.nombre!!)
            moverVectorA(vector, proximaUbicacion)
        }
    }

    private fun moverVectorA(vector: Vector, destino: Ubicacion){
        vector.moverseA(destino)
        if (vector.estaInfectado) {
            val vectoresEnUbicacion = vectorDAO.findAllByUbicacionId(destino.id!!)
            intentarContagiarAVectoresEnLaUbicacion(vectoresEnUbicacion, vector)
        }
        vectorService.actualizarVector(vector)
    }

    private fun puedeViajarA(vector: Vector, nombreUbicacionActual: String, nombreUbicacionDestino: String): Boolean {
        return vector.tieneMutacionTeletransportacion()
                ||
                ubicacionNeo4jDAO.estaConectadaA(nombreUbicacionActual, nombreUbicacionDestino)
    }
}





