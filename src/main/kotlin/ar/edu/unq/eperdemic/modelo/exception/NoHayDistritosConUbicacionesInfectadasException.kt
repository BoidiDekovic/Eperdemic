package ar.edu.unq.eperdemic.modelo.exception

class NoHayDistritosConUbicacionesInfectadasException : RuntimeException() {
    override val message: String
        get() = "No se encuentran distritos con ubicaciones infectadas"
}