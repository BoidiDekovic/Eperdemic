package ar.edu.unq.eperdemic.modelo.exception

class VectorNoPuedeSerContagiadoException(val aSerContagiado:String, val contagiado:String) : RuntimeException(){
    override val message: String
        get() = "$aSerContagiado No puede ser contagiado por $contagiado"
}