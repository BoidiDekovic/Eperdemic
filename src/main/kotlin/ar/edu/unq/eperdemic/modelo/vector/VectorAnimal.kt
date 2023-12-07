package ar.edu.unq.eperdemic.modelo.vector

import ar.edu.unq.eperdemic.modelo.Especie
import ar.edu.unq.eperdemic.modelo.Ubicacion
import ar.edu.unq.eperdemic.services.impl.helper.RNG
import javax.persistence.Entity

@Entity
class VectorAnimal : Vector {

    constructor()

    constructor(nombre: String, ubicacion: Ubicacion) : super (nombre, ubicacion)

    override fun caminosTransitables(): List<String> {
        return listOf("TERRESTRE", "MARITIMO", "AEREO")
    }

    override fun puedeSerContagiadoPor(tipoVector: String): Boolean {
       return tipoVector == "VectorInsecto"
    }

    override fun serContagiadoPorEspecie(especie: Especie, rng: RNG, vectorContagiado: Vector) {
        this.serContagiado(especie, especie.patogeno.capContagioAnimal, rng, vectorContagiado)
    }

}