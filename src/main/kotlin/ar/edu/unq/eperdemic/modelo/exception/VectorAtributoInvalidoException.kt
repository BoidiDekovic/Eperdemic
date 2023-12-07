package ar.edu.unq.eperdemic.modelo.exception

class VectorAtributoInvalidoException(private val atributo: String): RuntimeException() {
    override val message: String
        get() = "El atributo: '$atributo' de Vector es invalido, no puede ser vacio."
}