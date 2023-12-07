package ar.edu.unq.eperdemic.services

import ar.edu.unq.eperdemic.modelo.Especie
import ar.edu.unq.eperdemic.modelo.ReporteDeContagio

interface EstadisticaService {

    fun especieLider(): Especie
    fun lideres(): List<Especie>
    fun reporteDeContagios(nombreDeLaUbicacion: String): ReporteDeContagio
}