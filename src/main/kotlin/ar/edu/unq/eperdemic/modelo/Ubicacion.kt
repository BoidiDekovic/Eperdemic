package ar.edu.unq.eperdemic.modelo

import java.util.*
import javax.persistence.*

@Entity
class Ubicacion() {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
    @Column (nullable = false, unique = true)
    var nombre: String? = null

    constructor(nombre: String) : this() {
        this.nombre = nombre
    }

    override fun equals( o: Any?): Boolean {
        if (this ===  o) return true
        if ( o == null || javaClass !=  o.javaClass) return false
        val ubicacion =  o as Ubicacion?
        return nombre == ubicacion!!.nombre
    }

    override fun hashCode(): Int {
        return Objects.hash(id)
    }

    override fun toString() = nombre!!
}