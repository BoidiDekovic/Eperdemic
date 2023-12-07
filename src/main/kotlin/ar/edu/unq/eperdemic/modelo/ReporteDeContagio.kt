package ar.edu.unq.eperdemic.modelo

import java.util.*

class ReporteDeContagio() {

    var cantidadVectoresPresentes: Int = 0
    var cantidadVectoresInfectados: Int = 0
    var especieMasInfecciosa: String = "-"

    constructor(cantidadVectoresPresentes: Int, cantidadVectoresInfectados: Int, especieMasInfecciosa: String): this() {
        this.cantidadVectoresPresentes = cantidadVectoresPresentes
        this.cantidadVectoresInfectados = cantidadVectoresInfectados
        this.especieMasInfecciosa = especieMasInfecciosa
    }

    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o == null || javaClass != o.javaClass) return false
        val reporte = o as ReporteDeContagio?
        return (cantidadVectoresPresentes == reporte!!.cantidadVectoresPresentes &&
               cantidadVectoresInfectados == reporte.cantidadVectoresInfectados &&
               especieMasInfecciosa == reporte.especieMasInfecciosa)
    }

    override fun hashCode(): Int {
        return Objects.hash(cantidadVectoresPresentes, cantidadVectoresInfectados, especieMasInfecciosa)
    }

    override fun toString(): String {
        return "ReporteDeContagio(cantidadVectoresPresentes=$cantidadVectoresPresentes, " +
                "cantidadVectoresInfectados=$cantidadVectoresInfectados, " +
                "especieMasInfecciosa='$especieMasInfecciosa')"
    }
}