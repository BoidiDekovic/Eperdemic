package ar.edu.unq.eperdemic.services

import ar.edu.unq.eperdemic.modelo.Especie
import ar.edu.unq.eperdemic.modelo.Patogeno

interface PatogenoService {
    fun crearPatogeno(patogeno: Patogeno): Patogeno
    fun recuperarPatogeno(id: Long): Patogeno
    fun actualizarPatogeno(patogeno: Patogeno)
    fun recuperarTodosLosPatogenos(): List<Patogeno>
    fun agregarEspecie(idDePatogeno: Long, nombreEspecie: String, ubicacionId : Long) : Especie
    fun especiesDePatogeno(patogenoId: Long ): List<Especie>
    fun esPandemia(especieId: Long): Boolean
}