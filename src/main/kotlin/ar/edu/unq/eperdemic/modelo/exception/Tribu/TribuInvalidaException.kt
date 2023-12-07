package ar.edu.unq.eperdemic.modelo.exception.Tribu

class TribuInvalidaException(var atr: String): RuntimeException() {

    override val message: String
        get() = "La Tribu no puede ser creada con el atributo $atr vacio."
}