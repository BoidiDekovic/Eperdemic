package ar.edu.unq.eperdemic.services

import ar.edu.unq.eperdemic.modelo.Especie
import ar.edu.unq.eperdemic.modelo.vector.Vector

interface VectorService {
    fun crearVector(vector: Vector): Vector
    fun actualizarVector(vector: Vector)
    fun recuperarVector(idDelVector: Long): Vector
    fun recuperarVectorPorNombre(nombreVector: String): Vector
    fun recuperarTodosLosVectores(): List<Vector>
    fun eliminarVector(nombre: String)
    fun infectarVector(vectorId: Long, especieId: Long)
    fun enfermedadesDelVector(vectorId: Long): List<Especie>
    fun contagiar(vectorContagiadoId: Long , vectorAContagiarId: Long)
}