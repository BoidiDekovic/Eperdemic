package ar.edu.unq.eperdemic.services.impl

import ar.edu.unq.eperdemic.modelo.mutacion.Mutacion
import ar.edu.unq.eperdemic.modelo.exception.MutacionNombreEspecieException
import ar.edu.unq.eperdemic.persistencia.dao.MutacionDAO
import ar.edu.unq.eperdemic.services.EspecieService
import ar.edu.unq.eperdemic.services.MutacionService
import ar.edu.unq.eperdemic.services.exception.MutacionNoExisteException
import ar.edu.unq.eperdemic.services.exception.MutacionYaExistenteException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class MutacionServiceImpl() : MutacionService{
    @Autowired
    private lateinit var mutacionDAO: MutacionDAO
    @Autowired
    private lateinit var especieService: EspecieService

    override fun crearMutacion(mutacion: Mutacion): Mutacion {
        if (mutacion.id != null) {
            throw MutacionYaExistenteException()
        }
        return mutacionDAO.save(mutacion)
    }

    override fun actualizarMutacion(mutacion: Mutacion) {
        mutacion.id ?: throw MutacionNoExisteException()
        this.recuperarMutacion(mutacion.id!!)
        mutacionDAO.save(mutacion)
    }

    override fun recuperarMutacion(mutacionId: Long): Mutacion {
        return mutacionDAO.findByIdOrNull(mutacionId) ?: throw MutacionNoExisteException()
    }

    override fun recuperarTodasLasMutaciones(): List<Mutacion> {
       return mutacionDAO.findAll().toList()
    }

    override fun agregarMutacion(especieId: Long, mutacion: Mutacion) {
        val especie = especieService.recuperarEspecie(especieId)
        if(especie.nombre != mutacion.nombreEspecie){
            throw MutacionNombreEspecieException()
        }
        especie.agregarMutacionPosible(mutacion)
        especieService.actualizarEspecie(especie)
    }
}