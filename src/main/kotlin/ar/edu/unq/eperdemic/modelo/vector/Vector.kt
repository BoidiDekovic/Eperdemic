package ar.edu.unq.eperdemic.modelo.vector

import ar.edu.unq.eperdemic.modelo.*
import ar.edu.unq.eperdemic.modelo.exception.VectorAtributoInvalidoException
import ar.edu.unq.eperdemic.modelo.exception.VectorNoPuedeSerContagiadoException
import ar.edu.unq.eperdemic.modelo.mutacion.BioalteracionGenetica
import ar.edu.unq.eperdemic.modelo.mutacion.Teletransportacion
import ar.edu.unq.eperdemic.modelo.mutacion.Mutacion
import ar.edu.unq.eperdemic.modelo.mutacion.SupresionBiomecanica
import ar.edu.unq.eperdemic.services.impl.helper.RNG
import com.google.cloud.firestore.annotation.DocumentId
import java.util.*
import javax.persistence.*
import kotlin.collections.HashSet

@Entity
@Inheritance (strategy = InheritanceType.SINGLE_TABLE)
abstract class Vector(
    @Column(unique = true, length = 256)
    var nombre: String,
    @ManyToOne(cascade = [CascadeType.PERSIST], fetch = FetchType.EAGER)
    var ubicacion: Ubicacion? = null) {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
    @Column(nullable = false)
    var estaInfectado: Boolean = false

    @ManyToMany(cascade = [CascadeType.ALL], fetch = FetchType.EAGER)
    var especiesPadecidas: MutableSet<Especie> = HashSet()
    @OneToMany(cascade = [CascadeType.ALL], fetch = FetchType.EAGER)
    var mutacionesPadecidas: MutableSet<Mutacion> = HashSet()

    constructor(): this("-", Ubicacion())

    init {
        if (this.nombre.isEmpty()) { throw VectorAtributoInvalidoException("nombre") }
    }

    abstract fun caminosTransitables(): List<String>

    fun agregarEspeciePadecida(especie: Especie) {
        if (!this.estaInfectado) {
            this.estaInfectado = true
        }
        especiesPadecidas.add(especie)
    }

    fun serContagiado(especie: Especie, capContagio: Int, rng : RNG, vectorContagiado: Vector){
        if(!this.esResistentePorSupresionBiomecanicaA(especie)){
            contagiarseYMutar(capContagio, rng, especie, vectorContagiado)
        }
    }

    private fun contagiarseYMutar(capContagio: Int, rng: RNG, especie: Especie, vectorContagiado: Vector) {
        if (esContagioExitoso(capContagio, rng)) {
            agregarEspeciePadecida(especie)
            vectorContagiado.intentarMutar(especie, rng)
        }
    }

    private fun esResistentePorSupresionBiomecanicaA(especie: Especie): Boolean{
        return supresionBiomecanicaMasPotente()?.puedeAniquilarA(especie) ?: false
    }

    private fun supresionBiomecanicaMasPotente() : SupresionBiomecanica? {
        return this.mutacionesPadecidas.filterIsInstance<SupresionBiomecanica>()
            .maxByOrNull { it.potencia }
    }

    private fun intentarMutar(especie: Especie, rng: RNG){
        if(especie.mutacionesPosibles.isNotEmpty() &&
            this.esMutacionExitosa(especie.getCapacidadDeBiomecanizacion(),rng)){
            this.agregarMutacionPadecida(especie.getMutacionRandom(rng))
        }
    }

    private fun esMutacionExitosa(capacidadDeBiomecanizacion: Int, rng: RNG): Boolean{
        return rng.determinarProbabilidad(capacidadDeBiomecanizacion)
    }

    fun moverseA(ubicacion: Ubicacion){
        this.ubicacion = ubicacion
    }

    fun agregarMutacionPadecida(mutacion: Mutacion) {
        if (!this.tieneMutacionDelMismoTipoYEspecie(mutacion)) {
            mutacionesPadecidas.add(mutacion)
            mutacion.aniquilarEspeciesDe(this)
        }
    }
    private fun tieneMutacionDelMismoTipoYEspecie(mutacion: Mutacion): Boolean{
        return this.mutacionesPadecidas.any { it::class.java == mutacion::class.java &&
                it.nombreEspecie == mutacion.nombreEspecie}
    }

    fun tieneMutacionTeletransportacion(): Boolean {
        return this.mutacionesPadecidas.any { it is Teletransportacion }
    }

    private fun esContagioExitoso(capDeContagio: Int, rng : RNG) : Boolean {
        val nroRandom = rng.getRandomNumber(1 , 10)
        val porcentajeContagioExitoso = nroRandom + capDeContagio

        return  rng.determinarProbabilidad(porcentajeContagioExitoso)
    }

    fun intentarContagiarA(vectorAContagiar: Vector, rng: RNG){
        vectorAContagiar.intentarSerContagiadoPor(this, rng)
    }

    abstract fun puedeSerContagiadoPor(tipoVector: String): Boolean

    private fun intentarSerContagiadoPor(vectorContagiado: Vector, rng: RNG){
        val tipoVector = vectorContagiado.javaClass.simpleName
        if(puedeSerContagiadoPor(tipoVector)){
            this.serContagiadoPorEspecies(vectorContagiado, rng)
        }
        else if(vectorContagiado.tieneBioalteracionGeneticaDeTipo(this.javaClass.simpleName)){
            this.serContagiadoPorEspecieDeBioalteracionGenetica(vectorContagiado, rng)
        }
        else {
            throw VectorNoPuedeSerContagiadoException(this.javaClass.simpleName, tipoVector)
        }
    }

    private fun serContagiadoPorEspecies(vectorContagiado: Vector, rng: RNG){
        val iterator = vectorContagiado.especiesPadecidas.toList().iterator()
        while(iterator.hasNext()){
            val especie = iterator.next()
            if(vectorContagiado.especiesPadecidas.contains(especie)){
                serContagiadoPorEspecie(especie, rng, vectorContagiado)
            }
        }
    }
    private fun serContagiadoPorEspecieDeBioalteracionGenetica(vectorContagiado: Vector,rng: RNG){
        val especiesDeBioalteracionGenetica = vectorContagiado.mutacionesPadecidas.filter { it is BioalteracionGenetica } as List<BioalteracionGenetica>
        val especiesBioalteracionGeneticaDetipo = especiesDeBioalteracionGenetica.filter { it.tipoVector == this.javaClass.simpleName }.map { it.nombreEspecie }
        val especies = vectorContagiado.especiesPadecidas.filter{especiesBioalteracionGeneticaDetipo.contains(it.nombre)}
        especies.map{serContagiadoPorEspecie(it,rng,vectorContagiado)}
    }

    abstract fun serContagiadoPorEspecie(especie : Especie, rng: RNG, vectorContagiado: Vector)

    fun eliminarEspecie(especie: Especie){
        this.especiesPadecidas.remove(especie)
    }

    private fun tieneBioalteracionGeneticaDeTipo(tipoDeVector: String): Boolean{
        return this.mutacionesPadecidas.filterIsInstance<BioalteracionGenetica>()
            .any { it.tipoVector == tipoDeVector }
    }

    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o == null || javaClass != o.javaClass) return false
        val vector = o as Vector?
        return id == vector!!.id
    }

    override fun hashCode(): Int {
        return Objects.hash(id)
    }

    override fun toString() = nombre
}