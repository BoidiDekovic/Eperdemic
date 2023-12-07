package ar.edu.unq.eperdemic.persistencia.dao

import ar.edu.unq.eperdemic.modelo.Ubicacion
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository

interface UbicacionDAO : CrudRepository<Ubicacion, Long> {
    @Query( """
                    FROM Ubicacion u
                    WHERE u.nombre = :nombreDeLaUbicacion
        """)
    fun recuperarUbicacionConNombre(nombreDeLaUbicacion: String): Ubicacion?
    fun findByNombre (nombre: String?) : Ubicacion?
    fun countAllByIdNotNull() : Int

    @Query("""
         SELECT DISTINCT u.nombre
         FROM Ubicacion u
        JOIN Vector v ON u.id = v.ubicacion.id
         WHERE v.estaInfectado = TRUE
        """)
    fun ubicacionesInfectadas(): List<String>

}