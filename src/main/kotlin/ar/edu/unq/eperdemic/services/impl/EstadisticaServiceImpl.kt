package ar.edu.unq.eperdemic.services.impl

import ar.edu.unq.eperdemic.modelo.Especie
import ar.edu.unq.eperdemic.modelo.ReporteDeContagio
import ar.edu.unq.eperdemic.persistencia.dao.EspecieDAO
import ar.edu.unq.eperdemic.persistencia.dao.UbicacionDAO
import ar.edu.unq.eperdemic.persistencia.dao.VectorDAO
import ar.edu.unq.eperdemic.services.EstadisticaService
import ar.edu.unq.eperdemic.services.exception.EstadisticaNoHayEspeciesException
import ar.edu.unq.eperdemic.services.exception.UbicacionNoExistenteException
import ar.edu.unq.eperdemic.services.exception.UbicacionSinEspeciesException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class EstadisticaServiceImpl() : EstadisticaService {

    @Autowired private lateinit var vectorDAO: VectorDAO
    @Autowired private lateinit var especieDAO: EspecieDAO
    @Autowired private lateinit var ubicacionDAO: UbicacionDAO

    override fun especieLider(): Especie {
        return especieDAO.especieLider(PageRequest.of(0, 1)) ?: throw EstadisticaNoHayEspeciesException()
    }

    override fun lideres(): List<Especie> {
        return especieDAO.lideres()
    }

    override fun reporteDeContagios(nombreDeLaUbicacion: String): ReporteDeContagio {
        val ubicacionRecuperada = ubicacionDAO.recuperarUbicacionConNombre(nombreDeLaUbicacion)
            ?: throw UbicacionNoExistenteException()

        val reporte = ReporteDeContagio()
        reporte.cantidadVectoresPresentes = vectorDAO.countAllByUbicacionId(ubicacionRecuperada.id!!)
        reporte.cantidadVectoresInfectados = vectorDAO.countAllByEstaInfectadoIsTrueAndUbicacion_Id(ubicacionRecuperada.id!!)

        try {
            reporte.especieMasInfecciosa =
                especieDAO.especieLiderEn(ubicacionRecuperada.id!!, PageRequest.of(0, 1))?.nombre
                    ?: throw UbicacionSinEspeciesException()
        } catch (e: UbicacionSinEspeciesException) {
            reporte.especieMasInfecciosa = "-"
        }
        return reporte
    }
}