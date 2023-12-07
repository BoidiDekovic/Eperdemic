package ar.edu.unq.eperdemic.services.exception

class DistritoIntersectaConOtroException: RuntimeException() {
    override val message: String
        get() = "El distrito intersecta con otro"
}