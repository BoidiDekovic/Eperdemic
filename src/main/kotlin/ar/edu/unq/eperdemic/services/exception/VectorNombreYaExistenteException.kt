package ar.edu.unq.eperdemic.services.exception

class VectorNombreYaExistenteException: RuntimeException() {
    override val message: String
        get() = "Ya existe un Vector con el nombre dado"
}
