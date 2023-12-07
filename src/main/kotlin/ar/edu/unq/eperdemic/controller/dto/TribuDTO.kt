package ar.edu.unq.eperdemic.controller.dto

import ar.edu.unq.eperdemic.modelo.Tribu

class TribuDTO {
    var id: String? = null
    var nombre: String? = null
    var intregantes: ArrayList<String>? = null
    var integranteLider: String? = null

    companion object{
        fun desdeModelo(tribu : Tribu): TribuDTO{
            val dto = TribuDTO()
            dto.id = tribu.id
            dto.nombre = tribu.nombre
            dto.intregantes = tribu.integrantes
            dto.integranteLider = tribu.integranteLider

            return dto
        }
    }
    fun aModelo(): Tribu {
        val tribu = Tribu()
        tribu.id = this.id
        tribu.nombre = this.nombre!!
        tribu.integrantes = this.intregantes?: arrayListOf()
        tribu.integranteLider = this.integranteLider!!

        return tribu
    }


}