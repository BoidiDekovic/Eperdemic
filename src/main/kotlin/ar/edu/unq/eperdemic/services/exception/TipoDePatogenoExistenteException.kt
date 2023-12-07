package ar.edu.unq.eperdemic.services.exception

class TipoDePatogenoExistenteException() : RuntimeException() {
    override val message: String
        get() = "Ya existe un patógeno con el tipo dado"
}