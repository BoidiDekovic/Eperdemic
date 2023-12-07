package ar.edu.unq.eperdemic.modelo.exception

class DistritoConMenosDeTresCoordenadasException: RuntimeException() {
    override val message: String
        get() = "El distrito debe tener al menos 3 coordenadas"
}