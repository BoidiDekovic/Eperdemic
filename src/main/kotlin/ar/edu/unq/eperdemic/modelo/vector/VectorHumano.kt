package ar.edu.unq.eperdemic.modelo.vector

import ar.edu.unq.eperdemic.modelo.Especie
import ar.edu.unq.eperdemic.modelo.Ubicacion
import ar.edu.unq.eperdemic.modelo.mutacion.PropulsionMotora
import ar.edu.unq.eperdemic.services.impl.helper.RNG
import javax.persistence.Entity

@Entity
class VectorHumano : Vector {

    constructor()

    constructor(nombre: String, ubicacion: Ubicacion) : super (nombre, ubicacion)

    fun tieneMutacionPropulsionMotora(): Boolean {
        return this.mutacionesPadecidas.any { it is PropulsionMotora }
    }

    override fun caminosTransitables(): List<String> {
        var caminosTransitables: MutableList<String> = mutableListOf("TERRESTRE", "MARITIMO")
        if(this.tieneMutacionPropulsionMotora()) { caminosTransitables.add("AEREO") }
        return caminosTransitables
    }

    override fun puedeSerContagiadoPor(tipoVector: String): Boolean {
        return true
    }

    override fun serContagiadoPorEspecie(especie: Especie, rng: RNG, vectorContagiado : Vector) {
        this.serContagiado(especie, especie.patogeno.capContagioPersona, rng, vectorContagiado)
    }
}