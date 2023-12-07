package ar.edu.unq.eperdemic.services.impl

import ar.edu.unq.eperdemic.dao.helper.service.DataService
import ar.edu.unq.eperdemic.modelo.Tribu
import ar.edu.unq.eperdemic.modelo.Ubicacion
import ar.edu.unq.eperdemic.modelo.vector.Vector
import ar.edu.unq.eperdemic.modelo.vector.VectorAnimal
import ar.edu.unq.eperdemic.modelo.vector.VectorHumano
import ar.edu.unq.eperdemic.modelo.vector.VectorInsecto
import ar.edu.unq.eperdemic.services.TribuService
import ar.edu.unq.eperdemic.services.VectorService
import ar.edu.unq.eperdemic.services.exception.Tribu.TribuIdNoExisteException
import ar.edu.unq.eperdemic.services.exception.Tribu.TribuNombreNoExisteException
import ar.edu.unq.eperdemic.services.exception.Tribu.TribuNombreYaExistenteException
import ar.edu.unq.eperdemic.services.exception.VectorConNombreNoExisteException
import ar.edu.unq.eperdemic.services.exception.VectorIDNoExisteException
import ar.edu.unq.eperdemic.services.exception.VectorNoPertenecienteALaTribuException
import ar.edu.unq.eperdemic.services.impl.helper.RNG
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.*
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito.`when`
import org.mockito.invocation.InvocationOnMock
import org.mockito.stubbing.Answer
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.context.junit.jupiter.SpringExtension
import kotlin.random.Random

@ExtendWith(SpringExtension::class)
@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TribuServiceTest {

    @MockBean
    private lateinit var rng : RNG
    @Autowired
    private lateinit var dataService: DataService
    @Autowired
    private lateinit var tribuService: TribuService
    @Autowired
    private lateinit var vectorService: VectorService

    private lateinit var tribuMutarachil: Tribu
    private lateinit var tribuVudokai: Tribu
    private lateinit var tribuUnionKotlin: Tribu

    private lateinit var mutarachaRadioactiva: Vector
    private lateinit var mutarachaCriogenica: Vector

    private lateinit var abejorroCosmico: Vector
    private lateinit var cuervoNejo: Vector

    private lateinit var julianGuerrero: Vector
    private lateinit var ubicacionBernal: Ubicacion
    private lateinit var ubicacionEzpeleta: Ubicacion

    @BeforeEach
    fun setUp() {
        ubicacionBernal = Ubicacion("Bernal")
        ubicacionEzpeleta = Ubicacion("Ezpeleta")
        mutarachaRadioactiva = VectorInsecto("Mutaracha", ubicacionBernal)
        mutarachaCriogenica = VectorInsecto("Mutarachon", ubicacionEzpeleta)

        abejorroCosmico = VectorAnimal("Abejorro Cosmico", Ubicacion("Hudson"))
        cuervoNejo = VectorAnimal("CuervoNejo", Ubicacion("City Bell"))

        julianGuerrero = VectorHumano("Julian", Ubicacion("Platanos"))
        julianGuerrero = VectorHumano("Martin", Ubicacion("Avellaneda"))

        tribuMutarachil = Tribu("Hermandad Mutarachil", mutarachaRadioactiva)
        tribuVudokai = Tribu("Vudokai", abejorroCosmico)
        tribuUnionKotlin = Tribu("Liberadores de kotlin", julianGuerrero)
    }

    @AfterEach
    fun tearDown() {
        tribuService.deleteAll()
        dataService.cleanAll()
    }

    @Test
    fun testAlCrearYRecuperarUnaTribuSeObtienenObjetosSimilares(){
        vectorService.crearVector(mutarachaRadioactiva)
        val tribuCreada = tribuService.crearTribu(tribuMutarachil)
        val tribuRecuperada = tribuService.recuperarTribu(tribuCreada.id!!)

        assertEquals(tribuCreada.id, tribuRecuperada.id)
        assertEquals(tribuMutarachil.nombre, tribuRecuperada.nombre)
        assertEquals(tribuMutarachil.integrantes, tribuRecuperada.integrantes)
    }

    @Test
    fun testAlCrearYRecuperarUnaTribuPorSuNombreSeObtienenObjetosSimilares(){
        vectorService.crearVector(mutarachaRadioactiva)
        val tribuCreada = tribuService.crearTribu(tribuMutarachil)
        val tribuRecuperada = tribuService.recuperarTribuPorNombre(tribuCreada.nombre)

        assertEquals(tribuMutarachil.id, tribuRecuperada.id)
        assertEquals(tribuMutarachil.nombre, tribuRecuperada.nombre)
        assertEquals(tribuMutarachil.integrantes, tribuRecuperada.integrantes)
    }

    @Test
    fun testAlCrearUnaTribuSeValidanQueLosVectoresQueEstanDentroExistan(){
        val abejorroCosmico = vectorService.crearVector(abejorroCosmico)
        val vectorJulian = vectorService.crearVector(julianGuerrero)
        val mutarachaCriogenica = vectorService.crearVector(mutarachaCriogenica)
        val vuduokai = tribuService.crearTribu(tribuVudokai)

        vuduokai.agregarIntegrante(vectorJulian.nombre)
        vuduokai.agregarIntegrante(mutarachaCriogenica.nombre)

        assertTrue(vuduokai.integrantes.contains(vectorJulian.nombre))
        assertTrue(vuduokai.integrantes.contains(mutarachaCriogenica.nombre))
        assertTrue(vuduokai.integrantes.contains(abejorroCosmico.nombre))
    }

    @Test
    fun testAlCrearUnaTribuSiNoExiteElVectorLiderOAlgunoDeSusIntegrantesLanzaVectorConNombreNoExisteException(){
        assertThrows<VectorConNombreNoExisteException> { tribuService.crearTribu(tribuVudokai) }
    }

    @Test
    fun testAlRecuperarUnaTribuPorNombreQueNoExisteSeLanzaException(){
        assertThrows<TribuNombreNoExisteException> { tribuService.recuperarTribuPorNombre("a") }
    }

    @Test
    fun testAlRecuperarUnaTribuPorIdQueNoExisteSeLanzaException(){
        assertThrows<TribuIdNoExisteException> { tribuService.recuperarTribu("a") }
    }

    @Test
    fun testAlCrearUnaTribuConNombreYaExistenteSeLanzaException(){
        vectorService.crearVector(mutarachaRadioactiva)
        tribuService.crearTribu(tribuMutarachil)
        assertThrows<TribuNombreYaExistenteException> { tribuService.crearTribu(tribuMutarachil) }
    }

    @Test
    fun testAlEliminarUnaTribuNoPersistidaSeLanzaTribuNombreNoExisteException() {
        assertThrows<TribuNombreNoExisteException> { tribuService.eliminarTribu(tribuMutarachil.nombre) }
    }

    @Test
    fun testAlCrearYEliminarUnaTribuSeVerificaQueEstaNoContinuaPersistida() {
        vectorService.crearVector(mutarachaRadioactiva)
        tribuService.crearTribu(tribuMutarachil)
        tribuService.eliminarTribu(tribuMutarachil.nombre)
        assertThrows<TribuNombreNoExisteException> { tribuService.recuperarTribuPorNombre(tribuMutarachil.nombre) }
    }

    @Test
    fun testAlActualizarLosIntegrantesDeUnaTribuEstosSeActualizanCorrectamente() {
        vectorService.crearVector(mutarachaRadioactiva)
        val tribu = tribuService.crearTribu(tribuMutarachil)
        assertEquals(1, tribu.integrantes.size)
        assertFalse(tribu.integrantes.contains(mutarachaCriogenica.nombre))

        tribuMutarachil.agregarIntegrante(mutarachaCriogenica.nombre)
        tribuService.actualizarTribu(tribu, tribuMutarachil.nombre)

        assertEquals(2, tribu.integrantes.size)
        assertTrue(tribu.integrantes.contains(mutarachaCriogenica.nombre))
    }

    @Test
    fun testAlActualizarElNombreDeUnaTribuEsteSeActualizaCorrectamente1() {
        vectorService.crearVector(mutarachaRadioactiva)
        var tribu = tribuService.crearTribu(tribuMutarachil)
        assertEquals("Hermandad Mutarachil", tribu.nombre)

        tribuService.actualizarTribu(tribu, "Mutarachas Unidas")

        tribu = tribuService.recuperarTribu(tribu.id!!)

        assertEquals("Mutarachas Unidas", tribu.nombre)
    }

    @Test
    fun testAlActualizarElIntegranteLiderDeUnaTribuEsteSeActualizaCorrectamente() {
        vectorService.crearVector(mutarachaRadioactiva)
        var tribu = tribuService.crearTribu(tribuMutarachil)
        assertEquals("Mutaracha" , tribu.integranteLider)

        tribuMutarachil.integranteLider = "Julian"
        tribuService.actualizarTribu(tribu, "Hermandad Mutarachil")

        tribu = tribuService.recuperarTribu(tribu.id!!)
        assertEquals("Julian", tribu.integranteLider)
    }

    @Test
    fun testAlActualizarUnaTribuQueNoEstaPersistidaSeLanzaTribuNombreNoExisteException() {
        assertThrows<TribuNombreNoExisteException> { tribuService.actualizarTribu(tribuUnionKotlin, tribuUnionKotlin.nombre) }
    }

    @Test
    fun testAlRecuperarTodasLasTribusNosDevuelveUnaListaConLasTribusQueHay(){
        vectorService.crearVector(mutarachaRadioactiva)
        vectorService.crearVector(julianGuerrero)
        mutarachaCriogenica = vectorService.crearVector(abejorroCosmico)
        tribuService.crearTribu(tribuVudokai)
        tribuService.crearTribu(tribuUnionKotlin)
        tribuService.crearTribu(tribuMutarachil)

        val tribus = tribuService.recuperarTodasLasTribus().map { it.nombre }

        assertEquals(tribus.size,3)
        assertTrue(tribus.contains(tribuVudokai.nombre))
        assertTrue(tribus.contains(tribuMutarachil.nombre))
        assertTrue(tribus.contains(tribuUnionKotlin.nombre))
    }

    @Test
    fun testSiAlRecuperarTodasLasTribusNoHayTribusPersistidasDevuelveUnaListaVacia(){
        val tribus = tribuService.recuperarTodasLasTribus()
        assertTrue(tribus.isEmpty())
    }

    @Test
    fun testAlIntentarHacerPelearDosTribusConUnaNoPersistidaSeLanzaTribuNombreNoExisteException() {
        assertThrows<TribuNombreNoExisteException> { tribuService.pelearEntreTribus(tribuMutarachil.nombre, tribuVudokai.nombre) }
    }

    @Test
    fun testAlPelearDosTribusSinIntegrantesSeVerificaQueElLiderYLaTribuSonEliminadosCorrectamente() {
        `when`(rng.getRandomNumber(0, 1)).thenReturn(0)
        mutarachaRadioactiva = vectorService.crearVector(mutarachaRadioactiva)
        vectorService.crearVector(abejorroCosmico)
        tribuMutarachil = tribuService.crearTribu(tribuMutarachil)
        tribuVudokai = tribuService.crearTribu(tribuVudokai)

        tribuService.pelearEntreTribus(tribuMutarachil.nombre, tribuVudokai.nombre)
        assertThrows<TribuNombreNoExisteException> { tribuService.recuperarTribuPorNombre(tribuMutarachil.nombre) }
        assertThrows<VectorIDNoExisteException> { vectorService.recuperarVector(mutarachaRadioactiva.id!!) }
    }

    @Test
    fun testAlPelearDosTribusUnaConIntegrantesYLaOtraSinSeVerificaQueElLiderYLaTribuPerdedoraSonEliminadosCorrectamente() {
        `when`(rng.getRandomNumber(0, 1)).thenReturn(0)
        mutarachaRadioactiva = vectorService.crearVector(mutarachaRadioactiva)
        mutarachaCriogenica = vectorService.crearVector(mutarachaCriogenica)
        vectorService.crearVector(abejorroCosmico)
        tribuMutarachil.agregarIntegrante(mutarachaCriogenica.nombre)
        tribuMutarachil = tribuService.crearTribu(tribuMutarachil)
        tribuVudokai = tribuService.crearTribu(tribuVudokai)

        tribuService.pelearEntreTribus(tribuMutarachil.nombre, tribuVudokai.nombre)

        assertThrows<TribuNombreNoExisteException> { tribuService.recuperarTribuPorNombre(tribuMutarachil.nombre) }
        assertThrows<VectorIDNoExisteException> { vectorService.recuperarVector(mutarachaRadioactiva.id!!) }
        assertThrows<VectorIDNoExisteException> { vectorService.recuperarVector(mutarachaCriogenica.id!!) }
    }

    @Test
    fun testAlPelearDosTribusUnaSinIntegrantesYLaOtraConSeVerificaQueElLiderYLaTribuPerdedoraSonEliminadosCorrectamente() {
        `when`(rng.getRandomNumber(0, 1)).thenReturn(1)
        mutarachaCriogenica = vectorService.crearVector(mutarachaRadioactiva)
        abejorroCosmico = vectorService.crearVector(abejorroCosmico)
        tribuMutarachil = tribuService.crearTribu(tribuMutarachil)
        tribuVudokai = tribuService.crearTribu(tribuVudokai)

        tribuService.pelearEntreTribus(tribuMutarachil.nombre, tribuVudokai.nombre)

        assertThrows<TribuNombreNoExisteException> { tribuService.recuperarTribuPorNombre(tribuVudokai.nombre) }
        assertThrows<VectorIDNoExisteException> { vectorService.recuperarVector(abejorroCosmico.id!!) }
    }

    @Test
    fun testAlPelearDosTribusConIntegrantesSeVerificaQueElLiderYLaTribuPerdedoraSonEliminadosCorrectamente() {
        `when`(rng.getRandomNumber(0, 1)).thenReturn(0)
        mutarachaRadioactiva = vectorService.crearVector(mutarachaRadioactiva)
        mutarachaCriogenica = vectorService.crearVector(mutarachaCriogenica)
        tribuMutarachil.agregarIntegrante(mutarachaCriogenica.nombre)
        tribuMutarachil = tribuService.crearTribu(tribuMutarachil)

        abejorroCosmico = vectorService.crearVector(abejorroCosmico)
        cuervoNejo = vectorService.crearVector(cuervoNejo)
        tribuVudokai.agregarIntegrante(cuervoNejo.nombre)
        tribuVudokai = tribuService.crearTribu(tribuVudokai)

        tribuService.pelearEntreTribus(tribuMutarachil.nombre, tribuVudokai.nombre)

        assertThrows<TribuNombreNoExisteException> { tribuService.recuperarTribuPorNombre(tribuMutarachil.nombre) }
        assertThrows<VectorIDNoExisteException> { vectorService.recuperarVector(mutarachaRadioactiva.id!!) }
        assertThrows<VectorIDNoExisteException> { vectorService.recuperarVector(mutarachaCriogenica.id!!) }
    }

    @Test
    fun testAlPelearDosTribusConIntegrantesSeVerificaQueElLiderYLaTribuPerdedoraSonEliminadosCorrectamente2() {
        `when`(rng.getRandomNumber(0, 1)).thenReturn(1)
        mutarachaRadioactiva = vectorService.crearVector(mutarachaRadioactiva)
        mutarachaCriogenica = vectorService.crearVector(mutarachaCriogenica)
        tribuMutarachil.agregarIntegrante(mutarachaCriogenica.nombre)
        tribuMutarachil = tribuService.crearTribu(tribuMutarachil)

        mutarachaCriogenica = vectorService.crearVector(abejorroCosmico)
        mutarachaCriogenica = vectorService.crearVector(cuervoNejo)
        tribuVudokai.agregarIntegrante(cuervoNejo.nombre)
        tribuVudokai = tribuService.crearTribu(tribuVudokai)

        tribuService.pelearEntreTribus(tribuMutarachil.nombre, tribuVudokai.nombre)

        assertThrows<TribuNombreNoExisteException> { tribuService.recuperarTribuPorNombre(tribuVudokai.nombre) }
        assertThrows<VectorIDNoExisteException> { vectorService.recuperarVector(abejorroCosmico.id!!) }
        assertThrows<VectorIDNoExisteException> { vectorService.recuperarVector(cuervoNejo.id!!) }
    }

    @Test
    fun testAlPelearDosIntegrantesDeUnaTribuSeVerificaQueElVectorPerdedorEsEliminadoCorrectamente() {
        `when`(rng.getRandomNumber(0, 1)).thenReturn(0)
        mutarachaRadioactiva = vectorService.crearVector(mutarachaRadioactiva)
        mutarachaCriogenica = vectorService.crearVector(mutarachaCriogenica)
        abejorroCosmico = vectorService.crearVector(abejorroCosmico)
        tribuMutarachil.agregarIntegrante(mutarachaCriogenica.nombre)
        tribuMutarachil.agregarIntegrante(abejorroCosmico.nombre)
        tribuMutarachil = tribuService.crearTribu(tribuMutarachil)
        assertEquals(3, tribuMutarachil.integrantes.size)

        tribuService.pelearEntreIntegrantes(tribuMutarachil.nombre, mutarachaCriogenica.nombre, mutarachaRadioactiva.nombre)
        tribuMutarachil = tribuService.recuperarTribu(tribuMutarachil.id!!)
        assertEquals(2, tribuMutarachil.integrantes.size)
    }

    @Test
    fun testAlPelearElLiderConUnIntegranteDeUnaTribuEnLaQueGanaElIntegranteSeVerificaQueElIntegranteSeConviertaEnLiderYElLiderSeaEliminado() {
        `when`(rng.getRandomNumber(0, 1)).thenReturn(1)
        mutarachaRadioactiva = vectorService.crearVector(mutarachaRadioactiva)
        mutarachaCriogenica = vectorService.crearVector(mutarachaCriogenica)
        tribuMutarachil.agregarIntegrante(mutarachaCriogenica.nombre)
        tribuMutarachil = tribuService.crearTribu(tribuMutarachil)
        assertEquals(2, tribuMutarachil.integrantes.size)
        assertEquals(mutarachaRadioactiva.nombre, tribuMutarachil.integranteLider)

        tribuService.pelearEntreIntegrantes(tribuMutarachil.nombre, mutarachaCriogenica.nombre, mutarachaRadioactiva.nombre)
        tribuMutarachil = tribuService.recuperarTribu(tribuMutarachil.id!!)
        assertEquals(1, tribuMutarachil.integrantes.size)
        assertEquals(mutarachaCriogenica.nombre, tribuMutarachil.integranteLider)
    }

    @Test
    fun testAlPelearElLiderConUnIntegranteDeUnaTribuEnLaQueGanaElLiderSeVerificaQueElIntegranteSeaEliminadoYElLiderSigaSiendoLider() {
        `when`(rng.getRandomNumber(0, 1)).thenReturn(0)
        mutarachaRadioactiva = vectorService.crearVector(mutarachaRadioactiva)
        mutarachaCriogenica = vectorService.crearVector(mutarachaCriogenica)
        tribuMutarachil.agregarIntegrante(mutarachaCriogenica.nombre)
        tribuMutarachil = tribuService.crearTribu(tribuMutarachil)
        assertEquals(2, tribuMutarachil.integrantes.size)
        assertEquals(mutarachaRadioactiva.nombre, tribuMutarachil.integranteLider)

        tribuService.pelearEntreIntegrantes(tribuMutarachil.nombre, mutarachaCriogenica.nombre, mutarachaRadioactiva.nombre)
        tribuMutarachil = tribuService.recuperarTribu(tribuMutarachil.id!!)
        assertEquals(1, tribuMutarachil.integrantes.size)
        assertEquals(mutarachaRadioactiva.nombre, tribuMutarachil.integranteLider)
    }

    @Test
    fun testAlPelearConVectoresQueNoExistenEnUnaTribuDadaSeLanzaVectorNoPertenecienteALaTribuException() {
        mutarachaRadioactiva = vectorService.crearVector(mutarachaRadioactiva)
        tribuMutarachil = tribuService.crearTribu(tribuMutarachil)
        assertThrows<VectorNoPertenecienteALaTribuException> { tribuService.pelearEntreIntegrantes(tribuMutarachil.nombre, mutarachaCriogenica.nombre, mutarachaRadioactiva.nombre) }
    }

    @Test
    fun testAlPelearIntegrantesDeUnaTribuQueNoExisteSeLanzaTribuNombreNoExisteException() {
        mutarachaRadioactiva = vectorService.crearVector(mutarachaRadioactiva)
        mutarachaCriogenica  = vectorService.crearVector(mutarachaCriogenica)
        assertThrows<TribuNombreNoExisteException> { tribuService.pelearEntreIntegrantes(tribuMutarachil.nombre, mutarachaCriogenica.nombre, mutarachaRadioactiva.nombre) }
    }

    @Test
    fun demo(){
        demoPelearEntre(50, 50)
    }

    @Test
    fun clean(){
        tribuService.deleteAll()
        dataService.cleanAll()
    }

    fun demoPelearEntre(integrantesMutarachil: Int, integrantesVudokai: Int) {
        `when`(rng.getRandomNumber(0, 1)).thenAnswer { Random.nextInt(2) }
        for(i in 0 until integrantesMutarachil){
            val nombreIntegrante = tribuMutarachil.nombre + "_integrante_$i"
            val integrante = vectorService.crearVector(VectorInsecto(nombreIntegrante, ubicacionBernal))
            tribuMutarachil.agregarIntegrante(integrante.nombre)
        }
        for(i in 0 until integrantesVudokai){
            val nombreIntegrante = tribuVudokai.nombre + "_integrante_$i"
            val integrante = vectorService.crearVector(VectorInsecto(nombreIntegrante, ubicacionEzpeleta))
            tribuVudokai.agregarIntegrante(integrante.nombre)
        }
        vectorService.crearVector(abejorroCosmico)
        vectorService.crearVector(mutarachaRadioactiva)
        tribuService.crearTribu(tribuMutarachil)
        tribuService.crearTribu(tribuVudokai)
        tribuService.pelearEntreTribus(tribuMutarachil.nombre, tribuVudokai.nombre)
    }
}