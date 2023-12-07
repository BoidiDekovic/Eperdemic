package ar.edu.unq.eperdemic.services.exception

import javax.persistence.EntityNotFoundException

class VectorConNombreNoExisteException: EntityNotFoundException() {
    override val message: String
        get() = "No existe un vector con el nombre dado"
}
