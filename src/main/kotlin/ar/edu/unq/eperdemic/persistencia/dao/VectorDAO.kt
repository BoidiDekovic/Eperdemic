package ar.edu.unq.eperdemic.persistencia.dao

import ar.edu.unq.eperdemic.modelo.vector.Vector
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository

interface VectorDAO : CrudRepository<Vector, Long> {

    @Query("""
        SELECT COUNT(v)
        FROM Vector v
        JOIN v.especiesPadecidas e
        WHERE e.id = :especieId
        """)
    fun cantidadDeInfectados(especieId: Long): Int

    fun countAllByUbicacionId(ubicacionId : Long) : Int

    fun findAllByUbicacionId(ubicacionId : Long) : List<Vector>

    fun findByNombre(nombre : String) : Vector?

    fun deleteByNombre(nombre: String)

    @Query("""
            FROM Vector v
            WHERE v.ubicacion.id = :ubicacionId AND
                  v.estaInfectado = true
          """)
    fun vectoresInfectadosEn(ubicacionId : Long) : List<Vector>

    fun countAllByEstaInfectadoIsTrueAndUbicacion_Id(ubicacionId : Long) : Int

    @Query("""
            FROM Vector v
            WHERE v.ubicacion.id = :ubicacionId
            AND v.estaInfectado = true
            ORDER BY v.id ASC
        """)
    fun recuperarTodosDeLaUbicacionInfectados(ubicacionId: Long) : List<Vector>

    @Query("""
            SELECT COUNT(DISTINCT v.ubicacion)
            FROM Vector v
            JOIN v.especiesPadecidas esp
            WHERE esp.id = :especieId
            
    """)
    fun cantidadEnUbicacionesDiferentesConEspecie(especieId : Long) : Int
}