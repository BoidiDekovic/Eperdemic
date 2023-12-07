package ar.edu.unq.eperdemic.services.exception

class UbicacionConNombreNoExistenteException (private val nombreDeUbicacion: String): RuntimeException() {

    override val message: String
        get() = "No existe una Ubicacion con el nombre: $nombreDeUbicacion"
}
