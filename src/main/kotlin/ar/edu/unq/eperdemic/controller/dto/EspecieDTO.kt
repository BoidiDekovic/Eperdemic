package ar.edu.unq.eperdemic.controller.dto

import ar.edu.unq.eperdemic.controller.exception.MutacionInvalidaException
import ar.edu.unq.eperdemic.modelo.mutacion.BioalteracionGenetica
import ar.edu.unq.eperdemic.modelo.Especie
import ar.edu.unq.eperdemic.modelo.mutacion.SupresionBiomecanica

class EspecieDTO() {

    var id: Long? = null
    var nombre: String? = null
    var paisDeOrigen: String? = null
    var patogeno: PatogenoDTO? = null
    var mutacionesPosibles: MutableSet<MutacionDTO>? = null

    companion object {
        fun desdeModelo(especie: Especie): EspecieDTO {
            val dto = EspecieDTO()
            dto.id                 = especie.id
            dto.nombre             = especie.nombre
            dto.paisDeOrigen       = especie.paisDeOrigen
            dto.patogeno           = PatogenoDTO.desdeModelo(especie.patogeno)
            dto.mutacionesPosibles = especie.mutacionesPosibles.map { mutacion -> when (mutacion) {
                                                                                    is BioalteracionGenetica -> MutacionDTO.desdeModelo(mutacion)
                                                                                    is SupresionBiomecanica -> MutacionDTO.desdeModelo(mutacion)
                                                                                    else                     -> throw MutacionInvalidaException() } }
                                                               .toCollection(HashSet())
            return dto
        }
    }

    fun aModelo(): Especie {
        val especie = Especie()
        especie.id                 = this.id
        especie.nombre             = this.nombre!!
        especie.paisDeOrigen       = this.paisDeOrigen!!
        especie.patogeno           = this.patogeno!!.aModelo()
        especie.mutacionesPosibles = this.mutacionesPosibles?.map {it.aModelo()}?.toHashSet() ?: HashSet()
        return especie
    }
}