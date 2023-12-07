package ar.edu.unq.eperdemic.modelo

import org.springframework.data.mongodb.core.geo.GeoJsonPoint
import org.springframework.data.mongodb.core.index.GeoSpatialIndexed
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.neo4j.core.schema.Id

@Document("UbicacionMongoDB")
class UbicacionMongoDB() {

    @Id
    var id: String? = null
    var nombre: String? = null

    @GeoSpatialIndexed
    var punto: GeoJsonPoint? = null

    constructor(nombre: String , punto: GeoJsonPoint) : this() {
        this.nombre = nombre
        this.punto = punto
    }

    fun latitud(): Double {
        return punto!!.x
    }

    fun longitud():Double{
        return punto!!.y
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as UbicacionMongoDB

        if (id != other.id) return false
        if (nombre != other.nombre) return false
        if (punto != other.punto) return false
        return true
    }

    override fun hashCode(): Int {
        var result = id?.hashCode() ?: 0
        result = 31 * result + (nombre?.hashCode() ?: 0)
        result = 31 * result + (punto?.hashCode() ?: 0)
        return result
    }
}