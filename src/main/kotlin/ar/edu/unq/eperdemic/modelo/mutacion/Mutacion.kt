package ar.edu.unq.eperdemic.modelo.mutacion

import ar.edu.unq.eperdemic.modelo.vector.Vector
import ar.edu.unq.eperdemic.modelo.exception.MutacionAtributoInvalidoException
import java.util.*
import javax.persistence.*

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
abstract class Mutacion(
    var nombreEspecie: String
){
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    var id: Long? = null

    init {
        if(this.nombreEspecie.isEmpty()) { throw MutacionAtributoInvalidoException("nombre") }
    }

    override fun hashCode(): Int {
        return Objects.hash(id)
    }
    open fun aniquilarEspeciesDe(vector: Vector){}
}
