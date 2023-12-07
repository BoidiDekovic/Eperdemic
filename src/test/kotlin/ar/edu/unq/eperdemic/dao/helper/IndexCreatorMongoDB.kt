package ar.edu.unq.eperdemic.dao.helper

import ar.edu.unq.eperdemic.modelo.UbicacionMongoDB
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.index.GeoSpatialIndexType
import org.springframework.data.mongodb.core.index.GeospatialIndex
import org.springframework.stereotype.Component

@Component
class IndexCreatorMongoDB(
    @Autowired private val mongoTemplate: MongoTemplate
) {

    fun crearIndiceGeoespacialUbicacionMongoDB() {
        val geoSpatialIndex = GeospatialIndex("punto")
        geoSpatialIndex.typed(GeoSpatialIndexType.GEO_2DSPHERE)

        mongoTemplate.indexOps(UbicacionMongoDB::class.java).ensureIndex(geoSpatialIndex)
    }
}