package ar.edu.unq.eperdemic.services.exception

class EstadisticaNoHayEspeciesException(): RuntimeException() {

    override val message: String
        get() = "No hay ninguna Especie."
}