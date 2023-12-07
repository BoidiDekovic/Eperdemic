package ar.edu.unq.eperdemic.services.exception

class UbicacionNoAlcanzableException (private val ubicacionActual: String,
                                      private val ubicacionDestino: String): RuntimeException() {

    override val message: String
        get() = "No es posible viajar desde $ubicacionActual a $ubicacionDestino, no hay caminos disponibles " +
                "para el Vector dado."
}
