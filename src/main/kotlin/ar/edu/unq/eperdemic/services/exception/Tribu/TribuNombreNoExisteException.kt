package ar.edu.unq.eperdemic.services.exception.Tribu

class TribuNombreNoExisteException: RuntimeException() {
    override val message: String
        get() = "La tribu con el nombre dado no existe"
}