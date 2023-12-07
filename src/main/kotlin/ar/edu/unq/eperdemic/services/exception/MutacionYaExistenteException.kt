package ar.edu.unq.eperdemic.services.exception

class MutacionYaExistenteException(): RuntimeException() {

    override val message: String
        get() = "Ya existe la Mutacion dada en la base de datos."
}