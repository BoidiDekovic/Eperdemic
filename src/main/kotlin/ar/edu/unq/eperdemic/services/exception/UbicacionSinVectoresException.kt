package ar.edu.unq.eperdemic.services.exception

import java.lang.RuntimeException

class UbicacionSinVectoresException : RuntimeException(){
    override val message: String
        get() = "No hay vectores en la ubicacion"

}