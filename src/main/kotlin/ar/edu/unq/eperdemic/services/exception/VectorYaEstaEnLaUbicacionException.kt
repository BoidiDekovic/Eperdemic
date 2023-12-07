package ar.edu.unq.eperdemic.services.exception

class VectorYaEstaEnLaUbicacionException : RuntimeException() {
    override val message: String
        get() = "El vector ya está en la ubicación dada"
}