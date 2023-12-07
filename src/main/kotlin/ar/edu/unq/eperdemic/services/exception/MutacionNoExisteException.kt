package ar.edu.unq.eperdemic.services.exception

class MutacionNoExisteException() : RuntimeException() {
    override val message: String
        get() = "No existe la Mutacion con el ID dado"
}