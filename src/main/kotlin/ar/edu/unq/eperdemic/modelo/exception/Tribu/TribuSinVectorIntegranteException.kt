package ar.edu.unq.eperdemic.modelo.exception.Tribu

class TribuSinVectorIntegranteException(
    val nombreVector: String,
    val nombreTribu: String) : RuntimeException() {

    override val message: String
        get() = "El Vector con el nombre: $nombreVector no se encuentra en la Tribu: $nombreTribu"
}