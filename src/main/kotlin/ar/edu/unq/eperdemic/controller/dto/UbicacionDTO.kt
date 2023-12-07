package ar.edu.unq.eperdemic.controller.dto

import ar.edu.unq.eperdemic.modelo.Ubicacion
import ar.edu.unq.eperdemic.modelo.UbicacionMongoDB
import org.springframework.data.mongodb.core.geo.GeoJsonPoint

class UbicacionDTO() {

    var id: String? = null
    var nombre: String? = null
    var punto: GeoJsonPoint? = null

    companion object{
        fun desdeModelo(ubicacion: Ubicacion): UbicacionDTO {
            val dto = UbicacionDTO()
            dto.id = ubicacion.id.toString()
            dto.nombre = ubicacion.nombre
            return dto
        }
        fun desdeModelo(ubicacion: UbicacionMongoDB): UbicacionDTO {
            val dto = UbicacionDTO()
            dto.id = ubicacion.id
            dto.nombre = ubicacion.nombre
            dto.punto = ubicacion.punto
            return dto
        }
    }

    fun aModelo() : Ubicacion {
        val ubicacion = Ubicacion()
        ubicacion.id = this.id!!.toLong()
        ubicacion.nombre = this.nombre
        return ubicacion
    }

    fun aModeloMongoDB() : UbicacionMongoDB {
        val ubicacion = UbicacionMongoDB()
        ubicacion.id = this.id!!
        ubicacion.nombre = this.nombre
        return ubicacion
    }
}