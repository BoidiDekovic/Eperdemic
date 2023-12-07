package ar.edu.unq.eperdemic.services.exception

class EspecieIDNoExisteException(private val especieId: Long? = null): RuntimeException() {
    override val message: String
        get() = "No existe una especie con el ID: $especieId"
}