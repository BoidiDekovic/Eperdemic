package ar.edu.unq.eperdemic.persistencia.dao

import ar.edu.unq.eperdemic.modelo.Especie
import org.springframework.data.domain.PageRequest
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository

interface EspecieDAO : CrudRepository<Especie, Long> {

    @Query("""
                SELECT COUNT(e)
                FROM Vector v
                JOIN v.especiesPadecidas e
                WHERE e.id = :especieId
        """)
    fun cantidadDeInfectados(especieId: Long): Int

    @Query("""
            SELECT e
            FROM Vector v
            JOIN v.especiesPadecidas e
            WHERE TYPE(v) = VectorHumano
            GROUP BY e.nombre
            ORDER BY COUNT(v) DESC, e.nombre ASC
           """)
    fun especieLider(pageRequest: PageRequest): Especie?

    @Query("""
            SELECT e
            FROM Vector v
            JOIN v.especiesPadecidas e
            WHERE TYPE(v) = VectorHumano AND v.ubicacion.id = :ubicacionId
            GROUP BY e.nombre
            ORDER BY COUNT(v) DESC, e.nombre ASC
           """)
    fun especieLiderEn(ubicacionId: Long, pageRequest: PageRequest) : Especie?

    @Query("""
            SELECT e
            FROM Vector v
            JOIN v.especiesPadecidas e
            WHERE TYPE(v) = VectorHumano or TYPE(v) = VectorAnimal
            GROUP BY e.nombre
            ORDER BY COUNT(v) DESC, e.nombre ASC
           """)
    fun lideres(): List<Especie>

    fun findByNombre(nombre : String) : Especie?

    fun findAllByPatogenoId(patogenoId: Long) : List<Especie>
}