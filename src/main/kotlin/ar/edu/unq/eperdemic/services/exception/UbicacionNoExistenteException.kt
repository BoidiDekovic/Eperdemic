package ar.edu.unq.eperdemic.services.exception

import java.lang.RuntimeException

class UbicacionNoExistenteException() : RuntimeException() {
    override val message: String
        get()="No existe la ubicacion con el id solicitado"

}