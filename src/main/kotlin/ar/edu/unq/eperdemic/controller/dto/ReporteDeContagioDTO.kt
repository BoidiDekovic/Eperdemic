package ar.edu.unq.eperdemic.controller.dto

import ar.edu.unq.eperdemic.modelo.ReporteDeContagio

class ReporteDeContagioDTO(
    val cantidadVectoresPresentes: Int,
    val cantidadVectoresInfectados: Int,
    val especieMasInfecciosa: String) {

    companion object {
        fun desdeModelo(reporteDeContagio: ReporteDeContagio): ReporteDeContagioDTO {
            return ReporteDeContagioDTO(
                cantidadVectoresPresentes  = reporteDeContagio.cantidadVectoresPresentes,
                cantidadVectoresInfectados = reporteDeContagio.cantidadVectoresInfectados,
                especieMasInfecciosa       = reporteDeContagio.especieMasInfecciosa)
        }
    }

    fun aModelo(): ReporteDeContagio {
        val reporte = ReporteDeContagio()
        reporte.cantidadVectoresPresentes  = this.cantidadVectoresPresentes
        reporte.cantidadVectoresInfectados = this.cantidadVectoresInfectados
        reporte.especieMasInfecciosa       = this.especieMasInfecciosa

        return reporte
    }
}