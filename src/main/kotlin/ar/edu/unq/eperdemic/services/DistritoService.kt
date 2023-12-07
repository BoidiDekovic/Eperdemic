package ar.edu.unq.eperdemic.services

import ar.edu.unq.eperdemic.modelo.Distrito

interface DistritoService {
    fun crearDistrito(distrito:Distrito): Distrito
    fun recuperarDistritoPorNombre(nombreDistrito: String): Distrito
    fun actualizarDistrito(distrito: Distrito)
    fun deleteAll()
    fun distritoMasEnfermo(): Distrito
}