package ar.edu.unq.eperdemic.services.impl

import ar.edu.unq.eperdemic.modelo.Especie
import ar.edu.unq.eperdemic.persistencia.dao.EspecieDAO
import ar.edu.unq.eperdemic.services.EspecieService
import ar.edu.unq.eperdemic.services.exception.EspecieIDNoExisteException
import ar.edu.unq.eperdemic.services.exception.EspecieNombreYaExistenteException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class EspecieServiceImpl(): EspecieService {

    @Autowired
    private lateinit var especieDAO : EspecieDAO

    override fun actualizarEspecie(especie: Especie) {
        especie.id ?: throw EspecieIDNoExisteException()
        var especieOriginal = this.recuperarEspecie(especie.id!!)
        if(especieDAO.findByNombre(especie.nombre) != null
            && especieOriginal.nombre != especie.nombre){ throw EspecieNombreYaExistenteException()}
        especieDAO.save(especie)
    }

    override fun recuperarEspecie(especieId: Long): Especie {
        return especieDAO.findByIdOrNull(especieId) ?: throw EspecieIDNoExisteException(especieId)
    }

    override fun recuperarTodasLasEspecies(): List<Especie> {
        return especieDAO.findAll().toList()
    }

    override fun cantidadDeInfectadosPorLaEspecie(especieId: Long): Int {
        especieDAO.findByIdOrNull(especieId) ?: throw EspecieIDNoExisteException(especieId)
        return especieDAO.cantidadDeInfectados(especieId)
    }
}