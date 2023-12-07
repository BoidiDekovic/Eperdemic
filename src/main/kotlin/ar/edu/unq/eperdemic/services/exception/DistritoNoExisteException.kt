package ar.edu.unq.eperdemic.services.exception

class DistritoNoExisteException(private val nombre: String): RuntimeException() {
    override val message: String
        get() = "El distrito con el nombre ${nombre} no existe"
}