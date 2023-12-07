package ar.edu.unq.eperdemic.modelo

import ar.edu.unq.eperdemic.modelo.exception.EspecieAtributoInvalidoException
import ar.edu.unq.eperdemic.modelo.exception.EspecieSinMutacionesException
import ar.edu.unq.eperdemic.modelo.mutacion.Mutacion
import ar.edu.unq.eperdemic.services.exception.EspecieYaTieneMutacionException
import ar.edu.unq.eperdemic.services.impl.helper.RNG
import java.util.*
import javax.persistence.*

@Entity
class Especie(
    @ManyToOne(cascade = [CascadeType.ALL], fetch = FetchType.EAGER)
    var patogeno: Patogeno,
    @Column(unique = true, nullable = false, length = 256)
    var nombre: String,
    @Column(nullable = false, length = 128)
    var paisDeOrigen: String) {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    @ManyToMany(cascade = [CascadeType.ALL], fetch = FetchType.EAGER)
    var mutacionesPosibles: MutableSet<Mutacion> = HashSet()

    constructor(): this(Patogeno(), "-", "-")

    init {
        if(this.nombre.isEmpty()) { throw EspecieAtributoInvalidoException("nombre") }
        if(this.paisDeOrigen.isEmpty()) { throw EspecieAtributoInvalidoException("paisDeOrigen") }
    }

    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o == null || javaClass != o.javaClass) return false
        val especie = o as Especie?
        return nombre == especie!!.nombre
    }

    override fun toString() = nombre

    override fun hashCode(): Int {
        return Objects.hash(nombre)
    }
    fun getCapacidadDeBiomecanizacion() : Int {
        return patogeno.capBiomecanizacion
    }
    fun getMutacionRandom(rng: RNG) : Mutacion {
        if(mutacionesPosibles.isEmpty()){
            throw EspecieSinMutacionesException()
        }
        var indiceRandom = rng.getRandomNumber(0,mutacionesPosibles.indices.last)
        return mutacionesPosibles.toList()[indiceRandom]
    }

    fun agregarMutacionPosible(mutacion: Mutacion) {
        if(this.mutacionesPosibles.contains(mutacion)){
            throw EspecieYaTieneMutacionException()
        }
        this.mutacionesPosibles.add(mutacion)
    }
}