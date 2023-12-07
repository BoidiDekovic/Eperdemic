package ar.edu.unq.eperdemic.services.exception

import java.lang.RuntimeException

class UbicacionYaExistenteException : RuntimeException(){
    override val message: String
        get() = "Ya existe una ubicacion con este id"

}