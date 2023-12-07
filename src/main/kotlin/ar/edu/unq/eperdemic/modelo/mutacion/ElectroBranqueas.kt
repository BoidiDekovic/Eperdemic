package ar.edu.unq.eperdemic.modelo.mutacion

import javax.persistence.Entity

@Entity
class ElectroBranqueas(nombreEspecie: String): Mutacion(nombreEspecie) {

    override fun toString(): String {
        return "ElectroBranqueas(nombreEspecie = $nombreEspecie)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ElectroBranqueas

        return nombreEspecie == other.nombreEspecie
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result *= 31
        return result
    }
}