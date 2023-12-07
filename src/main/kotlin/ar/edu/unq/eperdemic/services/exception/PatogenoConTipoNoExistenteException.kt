package ar.edu.unq.eperdemic.services.exception

class PatogenoConTipoNoExistenteException(private val tipoDePatogeno: String): RuntimeException() {

    override val message: String
        get() = "No existe un Patogeno con el tipo: $tipoDePatogeno"
}