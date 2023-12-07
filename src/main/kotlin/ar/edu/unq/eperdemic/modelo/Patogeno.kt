package ar.edu.unq.eperdemic.modelo

import ar.edu.unq.eperdemic.modelo.exception.PatogenoAtributoFueraDeRangoException
import javax.persistence.*

@Entity
class Patogeno(
    @Column(unique = true, nullable = false)
    var tipo : String,
    var capContagioPersona : Int,
    var capContagioAnimal : Int,
    var capContagioInsecto : Int,
    var defensa : Int,
    var capBiomecanizacion : Int,
    )
{

    constructor() : this("", 1, 1, 1, 1, 1) {

    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id : Long? = null
    var cantidadDeEspecies : Int = 0


    override fun toString(): String {
        return tipo
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Patogeno) return false
        return tipo == other.tipo
    }

    fun crearEspecie(nombreEspecie: String, paisDeOrigen: String) : Especie{
        cantidadDeEspecies++
        return Especie(this, nombreEspecie, paisDeOrigen)
    }

    override fun hashCode(): Int {
        var result = id?.hashCode() ?: 0
        result = 31 * result + (cantidadDeEspecies ?: 0)
        result = 31 * result + (tipo.hashCode() ?: 0)
        result = 31 * result + capContagioPersona
        result = 31 * result + capContagioAnimal
        result = 31 * result + capContagioInsecto
        result = 31 * result + defensa
        result = 31 * result + capBiomecanizacion
        return result
    }

    init {
        if(capContagioPersona < 1 || capContagioPersona > 100){
            throw PatogenoAtributoFueraDeRangoException("capContagioPorPersona")}
        if(capContagioAnimal < 1 || capContagioAnimal > 100){
            throw PatogenoAtributoFueraDeRangoException("capContagioAnimal")}
        if(capContagioInsecto < 1 || capContagioInsecto > 100){
            throw PatogenoAtributoFueraDeRangoException("capContagioInsecto")}
        if(defensa < 1 || defensa > 100){
            throw PatogenoAtributoFueraDeRangoException("defensa")}
        if(capBiomecanizacion < 1 || capBiomecanizacion > 100){
            throw PatogenoAtributoFueraDeRangoException("capBiomecanizacion")}
    }
}