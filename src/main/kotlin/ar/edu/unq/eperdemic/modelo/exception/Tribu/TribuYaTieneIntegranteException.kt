package ar.edu.unq.eperdemic.modelo.exception.Tribu

class TribuYaTieneIntegranteException: RuntimeException() {
    override val message: String
        get() = "La tribu ya tiene al integrante dado"
}