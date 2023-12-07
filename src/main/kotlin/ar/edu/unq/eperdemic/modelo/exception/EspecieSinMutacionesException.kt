package ar.edu.unq.eperdemic.modelo.exception

class EspecieSinMutacionesException : RuntimeException() {
    override val message: String
        get() = "Esta especie no tiene mutaciones"

}