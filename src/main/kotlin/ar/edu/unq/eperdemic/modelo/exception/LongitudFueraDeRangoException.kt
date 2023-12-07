package ar.edu.unq.eperdemic.modelo.exception

class LongitudFueraDeRangoException :RuntimeException() {
    override val message: String
    get() = "el valor de la longitud esta fuera de rango"

}