package ar.edu.unq.eperdemic.services.exception

class EspecieNombreYaExistenteException(): RuntimeException() {
    override val message: String
        get() = "Ya existe una Especie con el nombre dado"
}