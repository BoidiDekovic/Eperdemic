package ar.edu.unq.eperdemic.persistencia.dao

import ar.edu.unq.eperdemic.modelo.Distrito
import org.springframework.data.mongodb.core.geo.GeoJsonPoint
import org.springframework.data.mongodb.core.geo.GeoJsonPolygon
import org.springframework.data.mongodb.repository.Aggregation
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.data.mongodb.repository.Query

interface DistritoDAO: MongoRepository<Distrito, String> {

    @Query("{nombre:'?0'}")
    fun findByNombre(nombre: String): Distrito?

    @Query(
        "{'forma': " +
                "{ '\$geoIntersects': " +
                "{ '\$geometry': ?0 }}}"
    )
    fun findSeIntersectanCon(forma: GeoJsonPolygon): List<Distrito>

    @Query("{'forma': { '\$geoIntersects': { '\$geometry': ?0 } } }")
    fun findDistritoIdConPunto(punto: GeoJsonPoint): Distrito?

    @Aggregation(
        pipeline = [
            "{\$project: {_id: 1, distrito: '\$nombre', ubicacionesContagiadas: {\$size: {\$setIntersection: ['\$ubicaciones.nombre', ?0]}}}}",
            "{\$match: {ubicacionesContagiadas: {\$gt: 0}}}",
            "{\$sort: {ubicacionesContagiadas: -1}}",
            "{\$limit: 1}"
        ]
    )
    fun distritoMasInfectado(ubicaciones: List<String>): String?

}