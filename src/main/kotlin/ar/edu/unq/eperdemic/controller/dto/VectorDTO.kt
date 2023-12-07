package ar.edu.unq.eperdemic.controller.dto
import ar.edu.unq.eperdemic.controller.exception.MutacionInvalidaException
import ar.edu.unq.eperdemic.modelo.*
import ar.edu.unq.eperdemic.modelo.enums.TipoDeVector
import ar.edu.unq.eperdemic.modelo.mutacion.BioalteracionGenetica
import ar.edu.unq.eperdemic.modelo.mutacion.SupresionBiomecanica
import ar.edu.unq.eperdemic.modelo.vector.Vector
import ar.edu.unq.eperdemic.modelo.vector.VectorAnimal
import ar.edu.unq.eperdemic.modelo.vector.VectorHumano
import ar.edu.unq.eperdemic.modelo.vector.VectorInsecto

class VectorDTO() {

    var id: Long? = null
    var nombre: String? = null
    var tipo: TipoDeVector? = null
    var ubicacion: UbicacionDTO? = null
    var estaInfectado: Boolean? = null
    var especiesPadecidas: MutableSet<EspecieDTO>? = null
    var mutacionesPadecidas: MutableSet<MutacionDTO>? = null

    companion object {
        fun desdeModelo(vector: Vector): VectorDTO {
            val dto = VectorDTO()

            dto.id = vector.id
            dto.nombre = vector.nombre
            dto.tipo = when (vector::class.java.simpleName) {
                "VectorHumano" -> TipoDeVector.HUMANO
                "VectorAnimal" -> TipoDeVector.ANIMAL
                else           -> TipoDeVector.INSECTO
            }
            dto.ubicacion = UbicacionDTO.desdeModelo(vector.ubicacion!!)
            dto.estaInfectado = vector.estaInfectado
            dto.especiesPadecidas = vector.especiesPadecidas.map { especie -> EspecieDTO.desdeModelo(especie) }
                                                            .toCollection(HashSet())
            dto.mutacionesPadecidas = vector.mutacionesPadecidas.map { mutacion -> when (mutacion) {
                                                                                        is BioalteracionGenetica -> MutacionDTO.desdeModelo(mutacion)
                                                                                        is SupresionBiomecanica -> MutacionDTO.desdeModelo(mutacion)
                                                                                        else                     -> throw MutacionInvalidaException() } }
                                                                .toCollection(HashSet())
            return dto
        }
    }

    fun aModelo(ubicacion: Ubicacion): Vector {
        val vector = when (this.tipo) {
                        TipoDeVector.HUMANO  -> { VectorHumano() }
                        TipoDeVector.INSECTO -> { VectorInsecto() }
                            else             -> { VectorAnimal() }
        }

        vector.id = this.id
        vector.nombre = this.nombre!!
        vector.ubicacion = ubicacion
        vector.especiesPadecidas = this.especiesPadecidas?.map { especieDTO -> especieDTO.aModelo() }?.toCollection(HashSet())  ?: HashSet()
        vector.mutacionesPadecidas = this.mutacionesPadecidas?.map {it.aModelo()}?.toHashSet() ?: HashSet()
        return vector
    }
}