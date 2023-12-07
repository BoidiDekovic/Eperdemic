package ar.edu.unq.eperdemic.modelo.exception

class EspecieAtributoInvalidoException(private val atributo: String): RuntimeException() {

    override val message: String
        get() = "El atributo: '$atributo' de Especie es invalido, no puede ser vacio."
}