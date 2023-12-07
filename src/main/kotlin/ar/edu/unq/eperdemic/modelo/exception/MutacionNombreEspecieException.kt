package ar.edu.unq.eperdemic.modelo.exception

class MutacionNombreEspecieException : RuntimeException() {
    override val message: String
        get() = "El nombre de la Mutacion no coincide con la Especie"
}