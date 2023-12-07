package ar.edu.unq.eperdemic.services.exception

class UbicacionSinEspeciesException(): RuntimeException() {
    override val message: String
        get() = "No hay Especies en la Ubicacion dada."

}
