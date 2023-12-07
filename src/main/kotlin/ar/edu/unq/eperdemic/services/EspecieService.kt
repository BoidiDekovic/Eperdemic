package ar.edu.unq.eperdemic.services

import ar.edu.unq.eperdemic.modelo.Especie

interface EspecieService {

    fun actualizarEspecie(especie: Especie)
    fun recuperarEspecie(especieId: Long): Especie
    fun recuperarTodasLasEspecies(): List<Especie>
    fun cantidadDeInfectadosPorLaEspecie(especieId: Long): Int
}