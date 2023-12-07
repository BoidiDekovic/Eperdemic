package ar.edu.unq.eperdemic.services.exception

import java.lang.RuntimeException

class UbicacionConNombreYaExisteException (private val nombreDeUbicacion: String): RuntimeException() {
    override val message: String
        get() = "No existe una Ubicacion con el nombre: $nombreDeUbicacion"
}
