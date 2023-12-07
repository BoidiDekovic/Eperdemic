package ar.edu.unq.eperdemic.controller.exception

class MutacionInvalidaException() : RuntimeException() {
    override val message: String
        get() = "La Mutación es inválida"
}