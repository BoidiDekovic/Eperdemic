package ar.edu.unq.eperdemic.persistencia.dao

import ar.edu.unq.eperdemic.modelo.UbicacionNeo4j
import org.springframework.data.neo4j.repository.Neo4jRepository
import org.springframework.data.neo4j.repository.query.Query
import org.springframework.stereotype.Repository

@Repository
interface UbicacionNeo4jDAO  : Neo4jRepository<UbicacionNeo4j, Long?> {
    @Query("MATCH(n) DETACH DELETE n")
    fun detachDeleteAll()
    fun findByNombre(nombre: String?): UbicacionNeo4j?

    @Query("""
        MATCH(ubicacion: UbicacionDTO{nombre: ${'$'}nombreDeUbicacion})
        MATCH (ubicacion)-[ :MARITIMO | :AEREO | :TERRESTRE]->(otraUbicacion)
        RETURN DISTINCT otraUbicacion
       """)
    fun conectados(nombreDeUbicacion: String): List<UbicacionNeo4j>

    @Query("""
        MATCH (unaUbicacion :UbicacionDTO {nombre: ${'$'}nombreUbicacionActual})
                                       -[rel: TERRESTRE | MARITIMO | AEREO]->
              (otraUbicacion:UbicacionDTO {nombre: ${'$'}nombreUbicacionDestino})
        RETURN COUNT(rel) > 0
    """)
    fun estaConectadaA(nombreUbicacionActual: String, nombreUbicacionDestino: String): Boolean

    @Query("""
        MATCH (ubicacionActual:UbicacionDTO {nombre: ${'$'}nombreUbicacionActual})
        MATCH (ubicacionDestino:UbicacionDTO {nombre: ${'$'}nombreUbicacionDestino})
        MATCH p = (ubicacionActual)-[r: TERRESTRE | MARITIMO | AEREO]->(ubicacionDestino)
        WHERE TYPE(r) IN ${'$'}caminosPosibles
        RETURN COUNT(p) > 0
    """)
    fun puedeMoverseA(caminosPosibles: List<String>, nombreUbicacionActual: String, nombreUbicacionDestino: String): Boolean

    @Query("""
        MATCH (start:UbicacionDTO {nombre: ${'$'}nombreUbicacionActual}), (end:UbicacionDTO {nombre: ${'$'}nombreUbicacionDestino})
        MATCH path = shortestPath((start)-[r: TERRESTRE | MARITIMO | AEREO *]->(end))
        WHERE all(camino IN relationships(path) WHERE TYPE(camino) IN ${'$'}caminosPosibles )
        RETURN nodes(path)[1..] AS ubicaciones
    """)
    fun caminoMasCorto(caminosPosibles: List<String>, nombreUbicacionActual: String, nombreUbicacionDestino: String) : List<UbicacionNeo4j>
}

