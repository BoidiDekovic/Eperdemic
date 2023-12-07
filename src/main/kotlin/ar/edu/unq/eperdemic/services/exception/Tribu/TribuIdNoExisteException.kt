package ar.edu.unq.eperdemic.services.exception.Tribu

class TribuIdNoExisteException: RuntimeException() {
    override val message: String
        get() = "La tribu con el id dado no existe"
}