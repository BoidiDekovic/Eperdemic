package ar.edu.unq.eperdemic.services.exception.Tribu

class TribuNombreYaExistenteException: RuntimeException() {
    override val message: String
        get() = "La tribu con el nombre dado ya existe"
}