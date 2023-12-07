package ar.edu.unq.eperdemic.modelo.vector

import ar.edu.unq.eperdemic.modelo.Especie
import ar.edu.unq.eperdemic.modelo.Ubicacion
import ar.edu.unq.eperdemic.modelo.mutacion.ElectroBranqueas
import ar.edu.unq.eperdemic.services.impl.helper.RNG
import javax.persistence.Entity

@Entity
class VectorInsecto : Vector {

    constructor()

    constructor(nombre: String, ubicacion: Ubicacion) : super (nombre, ubicacion)

    fun tieneMutacionElectroBranqueas(): Boolean {
        return this.mutacionesPadecidas.any { it is ElectroBranqueas }
    }

    override fun caminosTransitables(): List<String> {
        var caminosTransitables: MutableList<String> = mutableListOf("TERRESTRE", "AEREO")
        if(this.tieneMutacionElectroBranqueas()) { caminosTransitables.add("MARITIMO") }
        return caminosTransitables
    }

    override fun puedeSerContagiadoPor(tipoVector: String): Boolean {
        return tipoVector != "VectorInsecto"
    }

    override fun serContagiadoPorEspecie(especie: Especie, rng: RNG, vectorContagiado: Vector) {
        this.serContagiado(especie, especie.patogeno.capContagioInsecto, rng, vectorContagiado)
    }
}