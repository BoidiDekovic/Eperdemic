package ar.edu.unq.eperdemic.dao.helper.dao

import org.springframework.stereotype.Component
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext

@Component
open class SpringDataDAO : DataDAO {

    @PersistenceContext
    lateinit var entityManager: EntityManager

    // Conseguimos todas las tablas con showTables
    // Desactivamos el checkeo por foreign keys para que no rompa.
    // Limpiamos todas las tablas.
    // volvemos a setear el checkeo de foreign keys.
    override fun clear() {
        val nombreDeTablas = entityManager.createNativeQuery("show tables").resultList
        entityManager.createNativeQuery("SET FOREIGN_KEY_CHECKS=0;").executeUpdate()
        nombreDeTablas.forEach { result ->
            val tabla = when (result) {
                is String -> result
                is Array<*> -> result[0].toString()
                else -> throw IllegalArgumentException("Tipo de resultado inesperado")
            }
            entityManager.createNativeQuery("truncate table $tabla").executeUpdate()
        }
        entityManager.createNativeQuery("SET FOREIGN_KEY_CHECKS=1;").executeUpdate()
    }
}