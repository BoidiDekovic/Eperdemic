import ar.edu.unq.eperdemic.controller.dto.UbicacionDTO
import ar.edu.unq.eperdemic.modelo.Distrito
import org.springframework.data.mongodb.core.geo.GeoJsonPolygon

class DistritoDTO {
    var nombre: String? = null
    var id: String? = null
    var forma: GeoJsonPolygon? = null
    var ubicaciones: MutableList<UbicacionDTO>? = null


    companion object {
        fun desdeModelo(distrito: Distrito): DistritoDTO {
            val dto = DistritoDTO()
            dto.id = distrito.id
            dto.nombre = distrito.nombre
            dto.forma = distrito.forma
            dto.ubicaciones = distrito.ubicaciones.map { ubicacion -> UbicacionDTO.desdeModelo(ubicacion) }.toCollection(mutableListOf())
            return dto
        }
    }

    fun aModelo(): Distrito {
        val distrito = Distrito()
        distrito.id = this.id
        distrito.nombre = this.nombre
        distrito.forma = this.forma!!
        distrito.ubicaciones = this.ubicaciones!!.map { it.aModeloMongoDB() }.toCollection(mutableListOf())
        return distrito
    }
}