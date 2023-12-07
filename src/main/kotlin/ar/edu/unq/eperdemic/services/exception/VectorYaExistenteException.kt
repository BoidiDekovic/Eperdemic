package ar.edu.unq.eperdemic.services.exception

class VectorYaExistenteException : RuntimeException() {
    override val message: String
        get() = "Ya existe el vector dado en la base de datos"
}