package ar.edu.unq.eperdemic.modelo.exception

class MutacionAtributoInvalidoException (private val atributo: String): RuntimeException() {
    override val message: String
        get() = "El atributo: '$atributo' de la Mutacion es invalido, no puede ser vacio."

}