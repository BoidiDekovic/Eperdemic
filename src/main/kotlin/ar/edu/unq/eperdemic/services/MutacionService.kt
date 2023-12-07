package ar.edu.unq.eperdemic.services

import ar.edu.unq.eperdemic.modelo.mutacion.Mutacion

interface MutacionService {

    fun crearMutacion(mutacion: Mutacion): Mutacion
    fun actualizarMutacion(mutacion: Mutacion)
    fun recuperarMutacion(mutacionId: Long): Mutacion
    fun recuperarTodasLasMutaciones(): List<Mutacion>
    fun agregarMutacion(especieId:Long, mutacion: Mutacion)
}