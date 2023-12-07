package ar.edu.unq.eperdemic.services.exception

class DistritoNombreYaExistenteException(private val nombre: String):
    RuntimeException() {
    override val message: String
        get() = "El distrito con el nombre ${nombre} ya existe"
}