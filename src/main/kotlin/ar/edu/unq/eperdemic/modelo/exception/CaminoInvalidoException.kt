package ar.edu.unq.eperdemic.modelo.exception

class CaminoInvalidoException : RuntimeException() {
    override val message: String
        get() =  "El tipo de camino ingresado es incorrecto, los caminos validos son: MARITIMO, TERRESTRE o AREO "

}
