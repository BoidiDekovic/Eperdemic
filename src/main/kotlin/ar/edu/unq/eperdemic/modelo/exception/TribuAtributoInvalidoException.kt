package ar.edu.unq.eperdemic.modelo.exception

class TribuAtributoInvalidoException(private val atributo: String): RuntimeException() {
    override val message: String
        get() = "El atributo: '$atributo' de Tribu es invalido, no puede ser vacio."
}