package ar.edu.unq.eperdemic.modelo.exception

class PatogenoAtributoFueraDeRangoException(val atr: String) : RuntimeException() {
    override val message: String
        get() = "El valor del atributo $atr debe estar entre 1 y 100"
}
