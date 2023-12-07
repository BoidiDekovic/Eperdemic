package ar.edu.unq.eperdemic.modelo

import ar.edu.unq.eperdemic.modelo.exception.DistritoAbiertoException
import ar.edu.unq.eperdemic.modelo.exception.DistritoConMenosDeTresCoordenadasException
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.geo.GeoJsonPoint
import org.springframework.data.mongodb.core.geo.GeoJsonPolygon

@Document
class Distrito(var nombre: String?, var forma: GeoJsonPolygon?) {

    @Id
    var id: String? = null
    var ubicaciones: MutableList<UbicacionMongoDB> = mutableListOf()

    constructor() : this(null, null) {}

    fun agregarUbicacion(ubicacion: UbicacionMongoDB) {
        this.ubicaciones.add(ubicacion)
    }

    init {
        if( forma != null
            &&
            !forma!!.points.first().equals(forma!!.points.last())){
            throw DistritoAbiertoException()
        }
        if(  forma != null
                    &&
            forma!!.points.size < 4){
            throw DistritoConMenosDeTresCoordenadasException()
        }
    }
}
