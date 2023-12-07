package ar.edu.unq.eperdemic.services.exception

import javax.persistence.EntityNotFoundException

class VectorIDNoExisteException : EntityNotFoundException() {
    override val message: String
        get() = "No existe un vector con el ID dado"
}