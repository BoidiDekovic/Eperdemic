package ar.edu.unq.eperdemic.modelo

import ar.edu.unq.eperdemic.modelo.exception.CaminoInvalidoException
import org.springframework.data.neo4j.core.schema.GeneratedValue
import org.springframework.data.neo4j.core.schema.Id
import org.springframework.data.neo4j.core.schema.Node
import org.springframework.data.neo4j.core.schema.Relationship
import java.util.*

@Node("UbicacionDTO")
class UbicacionNeo4j() {
    @Id
    @GeneratedValue
    var id: Long? = null
    var nombre: String? = null

    constructor(nombre: String) : this() {
        this.nombre = nombre
    }

    @Relationship(type = "MARITIMO")
    var rutasMaritimas:  MutableSet<UbicacionNeo4j> = mutableSetOf()

    @Relationship(type = "TERRESTRE")
    var rutasTerrestres:  MutableSet<UbicacionNeo4j> = mutableSetOf()

    @Relationship(type = "AEREO")
    var rutasAereas:  MutableSet<UbicacionNeo4j> = mutableSetOf()


    fun conectar(ubicacion: UbicacionNeo4j, tipoCamino: String) {
            when(tipoCamino.uppercase()){
                "TERRESTRE" -> rutasTerrestres.add(ubicacion)
                "MARITIMO" -> rutasMaritimas.add(ubicacion)
                "AEREO" -> rutasAereas.add(ubicacion)
                else -> throw CaminoInvalidoException()
            }
        }

    companion object {
        fun desdeModelo(ubicacion: Ubicacion): UbicacionNeo4j {
            val dto = UbicacionNeo4j()
            dto.id = ubicacion.id
            dto.nombre = ubicacion.nombre

            return dto
        }
    }

    fun aModelo(): Ubicacion {
        val ubicacion = Ubicacion(nombre!!)
        ubicacion.id = id

        return ubicacion
    }

    override fun hashCode(): Int {
        return Objects.hash(id)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as UbicacionNeo4j

        if (id != other.id) return false
        if (nombre != other.nombre) return false
        if (rutasMaritimas != other.rutasMaritimas) return false
        if (rutasTerrestres != other.rutasTerrestres) return false
        if (rutasAereas != other.rutasAereas) return false

        return true
    }

}