package ar.edu.unq.eperdemic.services.exception

class PatogenoIDNoExisteException() : RuntimeException() {
    override val message: String
        get() = "No existe el patogeno con el id solicitado"
}