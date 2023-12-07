package ar.edu.unq.eperdemic.services

import ar.edu.unq.eperdemic.modelo.Tribu

interface TribuService {

    fun crearTribu(tribu: Tribu): Tribu
    fun recuperarTribu(id: String): Tribu
    fun recuperarTribuPorNombre(nombre: String): Tribu
    fun eliminarTribu(nombreDeTribu: String)
    fun actualizarTribu(tribu: Tribu, nombreDeTribu: String)
    fun recuperarTodasLasTribus() : List<Tribu>
    fun deleteAll()
    fun pelearEntreTribus(tribu: String, otraTribu: String)
    fun pelearEntreIntegrantes(nombreDeTribu: String, vectorAtacante: String, vectorDefensor: String)
}