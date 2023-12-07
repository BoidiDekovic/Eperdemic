package ar.edu.unq.eperdemic.services.impl

import ar.edu.unq.eperdemic.modelo.Tribu
import ar.edu.unq.eperdemic.persistencia.dao.TribuDAO
import ar.edu.unq.eperdemic.services.TribuService
import ar.edu.unq.eperdemic.services.VectorService
import ar.edu.unq.eperdemic.services.exception.Tribu.TribuIdNoExisteException
import ar.edu.unq.eperdemic.services.exception.Tribu.TribuNombreNoExisteException
import ar.edu.unq.eperdemic.services.exception.Tribu.TribuNombreYaExistenteException
import ar.edu.unq.eperdemic.services.exception.VectorNoPertenecienteALaTribuException
import ar.edu.unq.eperdemic.services.impl.helper.RNG
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class TribuServiceImpl(): TribuService {

    @Autowired
    private lateinit var rng: RNG

    @Autowired
    private lateinit var vectorService: VectorService

    @Autowired
    private lateinit var tribuDAO: TribuDAO

    override fun crearTribu(tribu: Tribu): Tribu {
        if(tribuDAO.getByName(tribu.nombre) != null){
            throw TribuNombreYaExistenteException()
        }
        for (integrante in tribu.integrantes){
            vectorService.recuperarVectorPorNombre(integrante)
        }
        return tribuDAO.saveData(tribu)
    }

    override fun recuperarTribu(id: String): Tribu{
        return tribuDAO.getById(id) ?: throw TribuIdNoExisteException()
    }

    override fun recuperarTribuPorNombre(nombre: String): Tribu {
        return tribuDAO.getByName(nombre) ?: throw TribuNombreNoExisteException()
    }

    override fun eliminarTribu(nombreDeTribu: String) {
        recuperarTribuPorNombre(nombreDeTribu)
        tribuDAO.deleteTribuByNombre(nombreDeTribu)
    }

    override fun actualizarTribu(tribu: Tribu, nombreDeTribu: String) {
        recuperarTribuPorNombre(tribu.nombre)
        tribuDAO.updateTribu(tribu, nombreDeTribu)
    }

    override fun recuperarTodasLasTribus() : List<Tribu> {
       return tribuDAO.findAll()
    }

    override fun deleteAll() {
        tribuDAO.deleteAll()
    }

    override fun pelearEntreTribus(tribu: String, otraTribu: String) {
        val tribuPeleadora1 = recuperarTribuPorNombre(tribu)
        val tribuPeleadora2 = recuperarTribuPorNombre(otraTribu)
        val tribus = mutableListOf(tribuPeleadora1, tribuPeleadora2)
        var tribuPerdedora: Tribu
        var nombreVectorAEliminar: String

        while(tribuPeleadora1.integranteLider.isNotBlank() && tribuPeleadora2.integranteLider.isNotBlank()) {
            tribuPerdedora = tribus[rng.getRandomNumber(0, 1)]

            if (tribuPerdedora.integrantes.size == 1) {
                vectorService.eliminarVector(tribuPerdedora.integranteLider)
                tribuPerdedora.eliminarLider()
                tribuDAO.deleteTribuByNombre(tribuPerdedora.nombre)
            } else {
                val liderTemp = tribuPerdedora.integranteLider
                tribuPerdedora.integrantes.remove(liderTemp)
                nombreVectorAEliminar = tribuPerdedora.integrantes[rng.getRandomNumber(
                    0, tribuPerdedora.integrantes.lastIndex)]
                vectorService.eliminarVector(nombreVectorAEliminar)
                tribuPerdedora.eliminarIntegrante(nombreVectorAEliminar)
                tribuPerdedora.agregarIntegrante(liderTemp)
                tribuDAO.updateTribu(tribuPerdedora, tribuPerdedora.nombre)
            }
        }
    }

    override fun pelearEntreIntegrantes(nombreDeTribu: String, vectorAtacante: String, vectorDefensor: String) {
        val tribu = recuperarTribuPorNombre(nombreDeTribu)
        val vectores = mutableListOf(vectorAtacante, vectorDefensor)
        if (tribu.integrantes.contains(vectorAtacante) && tribu.integrantes.contains(vectorDefensor)) {
            val vectorPerdedor = vectores[rng.getRandomNumber(0, 1)]
            val vectorGanador = vectores.find { it != vectorPerdedor }
            if (vectorPerdedor == tribu.integranteLider) {
                tribu.integranteLider = vectorGanador!!
            }
            vectorService.eliminarVector(vectorPerdedor)
            tribu.eliminarIntegrante(vectorPerdedor)
            tribuDAO.updateTribu(tribu, tribu.nombre)
        } else {
            throw VectorNoPertenecienteALaTribuException()
        }
    }
}