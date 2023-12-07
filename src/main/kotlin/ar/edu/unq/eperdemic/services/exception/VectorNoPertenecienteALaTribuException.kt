package ar.edu.unq.eperdemic.services.exception

class VectorNoPertenecienteALaTribuException: RuntimeException() {
    override val message: String
        get() = "El vector dado no pertenece a la tribu dada"
}