package ar.edu.unq.eperdemic.services.impl

import ar.edu.unq.eperdemic.modelo.Especie
import ar.edu.unq.eperdemic.modelo.vector.Vector
import ar.edu.unq.eperdemic.persistencia.dao.UbicacionDAO
import ar.edu.unq.eperdemic.persistencia.dao.VectorDAO
import ar.edu.unq.eperdemic.services.EspecieService
import ar.edu.unq.eperdemic.services.VectorService
import ar.edu.unq.eperdemic.services.exception.*
import ar.edu.unq.eperdemic.services.impl.helper.RNG
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import javax.persistence.EntityNotFoundException

@Service
@Transactional
class VectorServiceImpl(): VectorService {

    @Autowired
    private lateinit var vectorDAO: VectorDAO

    @Autowired
    private lateinit var ubicacionDAO : UbicacionDAO

    @Autowired
    private lateinit var especieService: EspecieService

    @Autowired
    private lateinit var rng: RNG


    override fun crearVector(vector: Vector): Vector {
        if(vector.ubicacion!!.id != null) {
            val ubicacion = ubicacionDAO.findById(vector.ubicacion!!.id!!).get()
            vector.moverseA(ubicacion)
        }
        if (vectorDAO.findByNombre(vector.nombre) != null) {
            throw VectorYaExistenteException()
        }
            return vectorDAO.save(vector)
    }

    override fun actualizarVector(vector: Vector) {
        vector.id ?: throw VectorIDNoExisteException()
        var vectorOriginal = this.recuperarVector(vector.id!!)
        if (vectorDAO.findByNombre(vector.nombre) != null
            &&
            vectorOriginal.nombre != vector.nombre) { throw VectorNombreYaExistenteException() }
        vectorDAO.save(vector)
    }

    override fun recuperarVector(idDelVector: Long): Vector {
        return vectorDAO.findByIdOrNull(idDelVector) ?: throw VectorIDNoExisteException()
    }

    override fun recuperarVectorPorNombre(nombreVector : String):Vector {
        return vectorDAO.findByNombre(nombreVector) ?: throw VectorConNombreNoExisteException()
    }
    override fun recuperarTodosLosVectores(): List<Vector> {
        return vectorDAO.findAll().toList()
    }

    override fun eliminarVector(nombre: String) {
        vectorDAO.deleteByNombre(nombre)
    }

   override fun infectarVector(vectorId: Long, especieId: Long) {
        try {
            val vector = this.recuperarVector(vectorId)
            val especie = especieService.recuperarEspecie(especieId)
            vector.agregarEspeciePadecida(especie)
            this.actualizarVector(vector)
        } catch (e: EntityNotFoundException) {
            when (e.message) {
                "Vector" -> throw VectorIDNoExisteException()
                "Especie" -> throw EspecieIDNoExisteException(especieId)
                else -> throw e
            }
        }
    }

    override fun enfermedadesDelVector(vectorId: Long): List<Especie> {
        try {
            val vector = this.recuperarVector(vectorId)
            return vector.especiesPadecidas.toList()
        } catch (e: EntityNotFoundException) {
            throw VectorIDNoExisteException()
        }
    }

    override fun contagiar(vectorContagiadoId: Long, vectorAContagiarId: Long) {
        try {
            val vectorContagiado = this.recuperarVector(vectorContagiadoId)
            val vectorAContagiar = this.recuperarVector((vectorAContagiarId))
            val cantidadDeEspeciesDeVectorAContagiar = vectorAContagiar.especiesPadecidas.size
            vectorContagiado.intentarContagiarA(vectorAContagiar, rng)
            if (cantidadDeEspeciesDeVectorAContagiar < vectorAContagiar.especiesPadecidas.size) {
                this.recuperarVector(vectorAContagiar.id!!)
            }
        } catch (e: EntityNotFoundException) {
            throw VectorIDNoExisteException()
        }
    }

}