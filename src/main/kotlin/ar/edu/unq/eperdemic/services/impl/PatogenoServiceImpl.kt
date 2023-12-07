package ar.edu.unq.eperdemic.services.impl

import ar.edu.unq.eperdemic.modelo.Especie
import ar.edu.unq.eperdemic.modelo.Patogeno
import ar.edu.unq.eperdemic.persistencia.dao.EspecieDAO
import ar.edu.unq.eperdemic.persistencia.dao.PatogenoDAO
import ar.edu.unq.eperdemic.persistencia.dao.UbicacionDAO
import ar.edu.unq.eperdemic.persistencia.dao.VectorDAO
import ar.edu.unq.eperdemic.services.EspecieService
import ar.edu.unq.eperdemic.services.PatogenoService
import ar.edu.unq.eperdemic.services.UbicacionService
import ar.edu.unq.eperdemic.services.VectorService
import ar.edu.unq.eperdemic.services.exception.*
import ar.edu.unq.eperdemic.services.impl.helper.RNG
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional


@Service
@Transactional
class PatogenoServiceImpl() : PatogenoService {

    @Autowired private lateinit var patogenoDAO : PatogenoDAO
    @Autowired private lateinit var especieDAO: EspecieDAO
    @Autowired private lateinit var especieService: EspecieService
    @Autowired private lateinit var ubicacionService : UbicacionService
    @Autowired private lateinit var ubicacionDAO : UbicacionDAO
    @Autowired private lateinit var vectorDAO: VectorDAO
    @Autowired private lateinit var vectorService: VectorService
    @Autowired private lateinit var rng: RNG

    override fun crearPatogeno(patogeno: Patogeno): Patogeno {
        if(patogenoDAO.findByTipo(patogeno.tipo) != null) {
            throw TipoDePatogenoExistenteException() }
        return patogenoDAO.save(patogeno)
    }

    override fun actualizarPatogeno(patogeno: Patogeno) {
        patogeno.id ?: throw PatogenoIDNoExisteException()
        var patogenoOrignal = this.recuperarPatogeno(patogeno.id!!)
        if(patogenoDAO.findByTipo(patogeno.tipo) != null
            && patogenoOrignal.tipo != patogeno.tipo ) { throw TipoDePatogenoExistenteException()}
        patogenoDAO.save(patogeno)
    }

    override fun recuperarPatogeno(id: Long): Patogeno {
        return patogenoDAO.findByIdOrNull(id) ?: throw PatogenoIDNoExisteException()
    }

    override fun recuperarTodosLosPatogenos(): List<Patogeno> {
        return patogenoDAO.findAll().toList()
    }

    override fun agregarEspecie(idDePatogeno: Long, nombreEspecie: String, ubicacionId: Long): Especie {
        val patogeno = this.recuperarPatogeno(idDePatogeno)
        val ubicacion = this.ubicacionService.recuperarUbicacion(ubicacionId)
        if(especieDAO.findByNombre(nombreEspecie) != null){ throw EspecieNombreYaExistenteException() }
        val especie = especieDAO.save(patogeno.crearEspecie(nombreEspecie, ubicacion.nombre!!))
        val vectoresDeLaUbicacion = vectorDAO.findAllByUbicacionId(ubicacionId)
        if(vectoresDeLaUbicacion.isEmpty()){ throw UbicacionSinVectoresException() }
        val vectorRandom = vectoresDeLaUbicacion[
            rng.getRandomNumber(0, vectoresDeLaUbicacion.lastIndex)]
        vectorService.infectarVector(vectorRandom.id!!, especie.id!!)
        return especie
    }

    override fun especiesDePatogeno(patogenoId: Long): List<Especie> {
        this.recuperarPatogeno(patogenoId)
        return especieDAO.findAllByPatogenoId(patogenoId)
    }

    override fun esPandemia(especieId: Long): Boolean {
        especieService.recuperarEspecie(especieId)
        val mitadCantUbicaciones = ubicacionDAO.countAllByIdNotNull() / 2
        val cantUbicacionesEspeciePresente = vectorDAO.cantidadEnUbicacionesDiferentesConEspecie(especieId)
        return cantUbicacionesEspeciePresente > mitadCantUbicaciones
    }
}