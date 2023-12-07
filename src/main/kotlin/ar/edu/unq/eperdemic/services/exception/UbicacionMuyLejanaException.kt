package ar.edu.unq.eperdemic.services.exception

class UbicacionMuyLejanaException (private val ubicacionActual: String,
                                   private val ubicacionDestino: String): RuntimeException() {

    override val message: String
        get() = "No es posible viajar desde $ubicacionActual a $ubicacionDestino, no son proximas."
}
