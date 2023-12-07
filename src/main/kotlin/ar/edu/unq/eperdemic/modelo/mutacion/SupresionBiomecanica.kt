package ar.edu.unq.eperdemic.modelo.mutacion

import ar.edu.unq.eperdemic.modelo.Especie
import ar.edu.unq.eperdemic.modelo.vector.Vector
import ar.edu.unq.eperdemic.modelo.exception.MutacionAtributoFueraDeRangoException
import javax.persistence.Column
import javax.persistence.Entity

@Entity
class SupresionBiomecanica(
    @Column(nullable = false, length = 100)
    var potencia: Int, nombreEspecie: String
) : Mutacion(nombreEspecie) {

    fun puedeAniquilarA(especie: Especie): Boolean{
        return this.potencia > especie.patogeno.defensa && this.nombreEspecie != especie.nombre
    }

    init {
        if (!esPotenciaValida(potencia)) { throw MutacionAtributoFueraDeRangoException("potencia") }
    }

    override fun toString(): String {
        return "SupresionBiomecanica(nombreEspecie=$nombreEspecie, potencia=$potencia)"
    }

    fun cambiarPotencia(nuevaPotencia: Int) {
        if(!esPotenciaValida(nuevaPotencia)) throw MutacionAtributoFueraDeRangoException("potencia")
        this.potencia = nuevaPotencia
    }

    override fun aniquilarEspeciesDe(vector : Vector){
        val especiesAniquilables = vector.especiesPadecidas.filter { puedeAniquilarA(it) }
        especiesAniquilables.map { vector.eliminarEspecie(it) }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as SupresionBiomecanica

        return potencia == other.potencia &&
                nombreEspecie == other.nombreEspecie
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + potencia
        return result
    }

    private fun esPotenciaValida(potencia: Int): Boolean {
        return potencia in 1..100
    }
}