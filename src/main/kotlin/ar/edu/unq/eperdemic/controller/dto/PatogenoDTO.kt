package ar.edu.unq.eperdemic.controller.dto

import ar.edu.unq.eperdemic.modelo.Patogeno

class PatogenoDTO() {

    var id : Long? = null
    var tipo : String? = null
    var cantidadDeEspecies : Int = 0
    var capContagioPersona : Int = 0
    var capContagioAnimal : Int = 0
    var capContagioInsecto : Int = 0
    var defensa : Int = 0
    var capBiomecanizacion : Int = 0

    companion object {
        fun desdeModelo(patogeno: Patogeno) : PatogenoDTO {
            val dto = PatogenoDTO()
            dto.id                 = patogeno.id
            dto.tipo               = patogeno.tipo
            dto.cantidadDeEspecies = patogeno.cantidadDeEspecies
            dto.capContagioPersona = patogeno.capContagioPersona
            dto.capContagioAnimal  = patogeno.capContagioAnimal
            dto.capContagioInsecto = patogeno.capContagioInsecto
            dto.defensa            = patogeno.defensa
            dto.capBiomecanizacion = patogeno.capBiomecanizacion
            return dto
        }
    }

    fun aModelo(): Patogeno {
        val patogeno = Patogeno()
        patogeno.id                 = this.id
        patogeno.tipo               = this.tipo!!
        patogeno.cantidadDeEspecies = this.cantidadDeEspecies
        patogeno.capContagioPersona = this.capContagioPersona
        patogeno.capContagioAnimal  = this.capContagioAnimal
        patogeno.capContagioInsecto = this.capContagioInsecto
        patogeno.defensa            = this.defensa
        patogeno.capBiomecanizacion = this.capBiomecanizacion

        return patogeno
    }
}