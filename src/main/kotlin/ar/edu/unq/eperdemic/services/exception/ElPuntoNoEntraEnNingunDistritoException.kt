package ar.edu.unq.eperdemic.services.exception

class ElPuntoNoEntraEnNingunDistritoException : RuntimeException() {
    override val message: String?
        get() = "El punto dado no pertence a ningun distrito"
}