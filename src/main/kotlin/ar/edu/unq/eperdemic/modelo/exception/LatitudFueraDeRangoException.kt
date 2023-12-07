package ar.edu.unq.eperdemic.modelo.exception

class LatitudFueraDeRangoException :RuntimeException() {
    override val message: String
        get() = "el valor de la latitud esta fuera de rango"
}
