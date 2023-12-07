package ar.edu.unq.eperdemic.modelo.exception

class DistritoAbiertoException: RuntimeException() {
    override val message: String
        get() = "El distrito debe estar cerrado"
}