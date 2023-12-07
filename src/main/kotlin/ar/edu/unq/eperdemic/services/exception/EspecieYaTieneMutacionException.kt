package ar.edu.unq.eperdemic.services.exception

class EspecieYaTieneMutacionException : RuntimeException() {
    override val message: String
        get() = "Esta especie ya contiene la mutacion"
}