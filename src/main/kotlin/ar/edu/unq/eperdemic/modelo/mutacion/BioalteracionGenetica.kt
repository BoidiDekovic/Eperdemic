package ar.edu.unq.eperdemic.modelo.mutacion

import javax.persistence.Entity


@Entity
class BioalteracionGenetica(
    var tipoVector: String,
    nombreEspecie: String
) : Mutacion(nombreEspecie) {
    override fun toString(): String {
        return "BioalteracionGenetica(nombreEspecie=$nombreEspecie, tipoVector=$tipoVector)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as BioalteracionGenetica

        return tipoVector == other.tipoVector &&
                nombreEspecie == other.nombreEspecie
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + tipoVector.hashCode()
        return result
    }

}