package ar.edu.unq.eperdemic.services.impl

import ar.edu.unq.eperdemic.dao.helper.service.DataService
import ar.edu.unq.eperdemic.modelo.*
import ar.edu.unq.eperdemic.modelo.vector.Vector
import ar.edu.unq.eperdemic.modelo.vector.VectorAnimal
import ar.edu.unq.eperdemic.modelo.vector.VectorHumano
import ar.edu.unq.eperdemic.modelo.vector.VectorInsecto
import ar.edu.unq.eperdemic.persistencia.dao.DistritoDAO
import ar.edu.unq.eperdemic.persistencia.dao.UbicacionMongoDBDAO
import ar.edu.unq.eperdemic.services.EspecieService
import ar.edu.unq.eperdemic.services.PatogenoService
import ar.edu.unq.eperdemic.services.UbicacionService
import ar.edu.unq.eperdemic.services.VectorService
import ar.edu.unq.eperdemic.services.exception.*
import ar.edu.unq.eperdemic.services.impl.helper.RNG
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.data.mongodb.core.geo.GeoJsonPoint
import org.springframework.data.mongodb.core.geo.GeoJsonPolygon
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
@SpringBootTest
@TestInstance(PER_CLASS)
class PatogenoServiceTest {

    @Autowired
    lateinit var dataService : DataService
    @Autowired
    lateinit var ubicacionService : UbicacionService
    @Autowired
    lateinit var vectorService : VectorService
    @Autowired
    lateinit var especieService : EspecieService
    @Autowired
    lateinit var patogenoService : PatogenoService
    @Autowired
    private lateinit var distritoServiceImpl: DistritoServiceImpl
    @Autowired
    private lateinit var distritoDAO: DistritoDAO
    @Autowired
    private lateinit var ubicacionMongoDBDAO: UbicacionMongoDBDAO
    @MockBean
    lateinit var rng : RNG

    lateinit var patogeno : Patogeno
    lateinit var patogeno2 : Patogeno
    lateinit var especie : Especie
    lateinit var ubicacionUNQ : Ubicacion
    lateinit var vector : Vector
    lateinit var punto : GeoJsonPoint
    private lateinit var distritoBernal: Distrito
    private lateinit var coordenadas: List<GeoJsonPoint>
    private lateinit var forma: GeoJsonPolygon
    private lateinit var especieCovid: Especie
    private lateinit var especieMalaria: Especie
    private lateinit var especieSarampion: Especie
    private lateinit var especieMononucleosis: Especie

    @BeforeEach
    fun setUp(){
        patogeno = Patogeno("Virus", 70,
            40, 10, 50,
            30)
        patogeno2 = Patogeno("Bacteria", 50,
            80, 20, 10,
            20)
        ubicacionUNQ = Ubicacion("UNQ")
        punto = GeoJsonPoint(10.10,20.20)
        coordenadas = listOf(
            GeoJsonPoint(0.0, 0.0),
            GeoJsonPoint(30.0, 60.0),
            GeoJsonPoint(60.0, 10.0),
            GeoJsonPoint(0.0, 0.0),
        )
        forma = GeoJsonPolygon(coordenadas)
        distritoBernal = Distrito("Bernal", forma)
        distritoServiceImpl.crearDistrito(distritoBernal)
    }

    @Test
    fun testAlCrearYRecuperarUnPatogenoSeObtieneObjetosSimilares(){
        patogeno = patogenoService.crearPatogeno(patogeno)
        val patogenoRec = patogenoService.recuperarPatogeno(patogeno.id!!)

        assertEquals(patogeno.id, patogenoRec.id)
        assertEquals(patogeno.tipo, patogenoRec.tipo)
        assertEquals(patogeno.capContagioPersona, patogenoRec.capContagioPersona)
        assertEquals(patogeno.capContagioAnimal, patogenoRec.capContagioAnimal)
        assertEquals(patogeno.capContagioInsecto, patogenoRec.capContagioInsecto)
        assertEquals(patogeno.defensa, patogenoRec.defensa)
        assertEquals(patogeno.capBiomecanizacion, patogenoRec.capBiomecanizacion)
    }

    @Test
    fun testAlCrearUnPatogenoConUnTipoYaCreadoSeLanzaLaExcepcionTipoYaExistente() {
        patogenoService.crearPatogeno(patogeno)
        assertThrows<TipoDePatogenoExistenteException> { patogenoService.crearPatogeno(patogeno) }
    }

    @Test
    fun testRecuperarUnPatogenoConUnIDNoExistenteSeLanzaExcepcion(){
        assertThrows<PatogenoIDNoExisteException> { patogenoService.recuperarPatogeno(-1) }
    }

    @Test
    fun testAlActualizarUnPatogenoYRecuperarSeObtieneElObjetoEsperado(){
        patogeno = patogenoService.crearPatogeno(patogeno)
        patogeno.cantidadDeEspecies = 17
        patogeno.defensa = 20
        patogeno.capContagioInsecto = 2

        patogenoService.actualizarPatogeno(patogeno)
        val otroPatogeno = patogenoService.recuperarPatogeno(patogeno.id!!)

        assertEquals(patogeno.cantidadDeEspecies, otroPatogeno.cantidadDeEspecies)
        assertEquals(patogeno.defensa, otroPatogeno.defensa)
        assertEquals(patogeno.capContagioInsecto, otroPatogeno.capContagioInsecto)
    }

    @Test
    fun testAlActualizarUnPatogenoConUnIDNuloSeLanzaExcepcionIDNoExiste(){
        val patogeno2 = Patogeno()
        patogeno2.id = null
        assertThrows<PatogenoIDNoExisteException> { patogenoService.actualizarPatogeno(patogeno2) }
    }

    @Test
    fun testAlActualizarUnPatogenoConUnIDQueNoExisteSeLanzaExcepcionIDNoExiste(){
        val patogeno2 = Patogeno()
        patogeno2.id = -1
        assertThrows<PatogenoIDNoExisteException> { patogenoService.actualizarPatogeno(patogeno2) }
    }

    @Test
    fun testAlActualizarUnPatogenoConUnTipoDePatogenoQueYaExisteLanzaTipoDePatogenoExisteException(){
        patogenoService.crearPatogeno(patogeno)
        var patogenoAActualizar = patogenoService.crearPatogeno(patogeno2)
        patogenoAActualizar.tipo = "Virus"
        assertThrows<TipoDePatogenoExistenteException> { patogenoService.actualizarPatogeno(patogenoAActualizar) }
    }

    @Test
    fun testAlRecuperarTodosLosPatogenosSeObtieneLaListaConTodos(){
        patogenoService.crearPatogeno(patogeno)
        patogenoService.crearPatogeno(patogeno2)

        val patogenos = patogenoService.recuperarTodosLosPatogenos()

        assertEquals(2, patogenos.size)
        assertTrue(patogenos.contains(patogeno))
        assertTrue(patogenos.contains(patogeno2))
    }

    @Test
    fun testAlRecuperarTodosSiNoHayPatogenosSeObtieneListaVacia(){
        assertTrue(patogenoService.recuperarTodosLosPatogenos().isEmpty())
    }

    @Test
    fun testAlAgregarUnaEspecieAUnPatogenoYRecuperarlaSeObtienenObjetosSimilares(){
        patogeno = patogenoService.crearPatogeno(patogeno)
        vector = vectorService.crearVector(VectorHumano("Pepe", ubicacionUNQ))
        especie = patogenoService.agregarEspecie(patogeno.id!!, "COVID", ubicacionUNQ.id!!)

        val especieRecuperada = especieService.recuperarEspecie(especie.id!!)

        assertEquals(especie.id, especieRecuperada.id)
        assertEquals(especie.nombre, especieRecuperada.nombre)
        assertEquals(especie.paisDeOrigen, especieRecuperada.paisDeOrigen)
    }

    @Test
    fun testAlAgregarUnaEspecieAUnPatogenoSeInfectaAUnVectorAlAzarEnLaUbicacionDada(){
        patogeno = patogenoService.crearPatogeno(patogeno)
        ubicacionUNQ = ubicacionService.crearUbicacion(ubicacionUNQ,punto)
        vector = vectorService.crearVector(VectorHumano("Pepe", ubicacionUNQ))
         var vector2 = vectorService.crearVector(VectorHumano("Pepa", ubicacionUNQ))
        assertTrue(vector.especiesPadecidas.isEmpty())
        assertTrue(vector2.especiesPadecidas.isEmpty())
        `when`(rng.getRandomNumber(0, 1)).thenReturn(0)

        especie = patogenoService.agregarEspecie(patogeno.id!!, "COVID", ubicacionUNQ.id!!)
        vector = vectorService.recuperarVector(vector.id!!)
        vector2 = vectorService.recuperarVector(vector2.id!!)

        assertEquals(1, vector.especiesPadecidas.size)
        assertTrue(vector.especiesPadecidas.contains(especie))
        assertTrue(vector2.especiesPadecidas.isEmpty())
    }

    @Test
    fun testAlAgregarUnaEspecieAUnPatogenoSeInfectaAUnVectorAlAzarEnLaUbicacionDada2(){
        patogeno = patogenoService.crearPatogeno(patogeno)
        ubicacionUNQ = ubicacionService.crearUbicacion(ubicacionUNQ,punto)
        vector = vectorService.crearVector(VectorHumano("Pepe", ubicacionUNQ))
        var vector2 = vectorService.crearVector(VectorHumano("Pepa", ubicacionUNQ))
        assertTrue(vector.especiesPadecidas.isEmpty())
        assertTrue(vector2.especiesPadecidas.isEmpty())
        `when`(rng.getRandomNumber(0, 1)).thenReturn(1)

        especie = patogenoService.agregarEspecie(patogeno.id!!, "COVID", ubicacionUNQ.id!!)
        vector = vectorService.recuperarVector(vector.id!!)
        vector2 = vectorService.recuperarVector(vector2.id!!)

        assertEquals(1, vector2.especiesPadecidas.size)
        assertTrue(vector2.especiesPadecidas.contains(especie))
        assertTrue(vector.especiesPadecidas.isEmpty())
    }

    @Test
    fun testCuandoSeAgregaUnaEspecieAUnPatogenoNoPersistidoSeLanzaExcepcionPatogenoIDNoExiste() {
        ubicacionService.crearUbicacion(ubicacionUNQ,punto)
        assertThrows<PatogenoIDNoExisteException> { patogenoService.agregarEspecie(-1, "Zika", 1) }
    }

    @Test
    fun testCuandoSeAgregaUnaEspecieEnUnaUbicacionNoPersistidaSeLanzaExcepcionUbicacionNoExistente() {
        patogeno = patogenoService.crearPatogeno(patogeno)
        assertThrows<UbicacionNoExistenteException> { patogenoService.agregarEspecie(patogeno.id!!, "Zika", -1) }
    }
    @Test
    fun testCuandoSeAgregaUnaEspecieEnUnaUbicacionQueNoHayVectoresSeLanzaExcepcionUbicacionSinVectores() {
        patogeno = patogenoService.crearPatogeno(patogeno)
        ubicacionUNQ = ubicacionService.crearUbicacion(ubicacionUNQ,punto)
        assertThrows<UbicacionSinVectoresException> { patogenoService.agregarEspecie(patogeno.id!!, "Zika", ubicacionUNQ.id!!) }
    }

    @Test
    fun testCuandoSeIntentaAgregarUnaEspecieConUnNombreYaExistenteSeLanzaExcepcionEspecieNombreYaExistente(){
        patogeno = patogenoService.crearPatogeno(patogeno)
        ubicacionUNQ = ubicacionService.crearUbicacion(ubicacionUNQ,punto)
        vector = vectorService.crearVector(VectorHumano("Pepe", ubicacionUNQ))
        especie = patogenoService.agregarEspecie(patogeno.id!!, "Covid", ubicacionUNQ.id!!)
        assertThrows<EspecieNombreYaExistenteException> {
            patogenoService.agregarEspecie(patogeno.id!!, "Covid", ubicacionUNQ.id!!) }
    }

    @Test
    fun testCuandoSeRecuperanLasEspeciesDelPatogenoSonLasEspeciesQueTienenAlPatogeno(){
        patogeno = patogenoService.crearPatogeno(patogeno)
        ubicacionUNQ = ubicacionService.crearUbicacion(ubicacionUNQ,punto)
        vector = vectorService.crearVector(VectorHumano("Pepe", ubicacionUNQ))
        especie = patogenoService.agregarEspecie(patogeno.id!!, "Covid", ubicacionUNQ.id!!)
        val especie2 = patogenoService.agregarEspecie(patogeno.id!!, "Resfrio", ubicacionUNQ.id!!)
        val especiesDePatogeno = patogenoService.especiesDePatogeno(patogeno.id!!)
        assertTrue(especiesDePatogeno.contains(especie))
        assertTrue(especiesDePatogeno.contains(especie2))
        assertEquals(2, especiesDePatogeno.size)
    }

    @Test
    fun testCuandoSeRecuperanLasEspeciesDelPatogenoYNoTieneDevuelveListaVacia(){
        patogeno = patogenoService.crearPatogeno(patogeno)
        assertTrue(patogenoService.especiesDePatogeno(patogeno.id!!).isEmpty())
    }

    @Test
    fun testCuandoSeRecuperanLasEspeciesDeUnPatogenoQueNoExisteSeLanzaExcepcion(){
        assertThrows<PatogenoIDNoExisteException> { patogenoService.especiesDePatogeno(-1) }
    }

    @Test
    fun testCuandoSeVerificaSiEsPandemiaEsFalso(){
        patogeno = patogenoService.crearPatogeno(patogeno)
        ubicacionUNQ = ubicacionService.crearUbicacion(ubicacionUNQ,punto)
        vector = vectorService.crearVector(VectorHumano("Pepe", ubicacionUNQ))
        val ubicacion2 = ubicacionService.crearUbicacion(Ubicacion("Polonia"),GeoJsonPoint(17.10,27.20))
        val ubicacion3 = ubicacionService.crearUbicacion(Ubicacion("Rusia"),GeoJsonPoint(18.10,28.20))
        ubicacionService.crearUbicacion(Ubicacion("Indonesia"),GeoJsonPoint(19.10,29.20))
        vectorService.crearVector(VectorAnimal("Pepita", ubicacion2))
        vectorService.crearVector(VectorInsecto("Sangu", ubicacion3))

        especie = patogenoService.agregarEspecie(patogeno.id!!, "Gripe", ubicacionUNQ.id!!)
        assertFalse(patogenoService.esPandemia(especie.id!!))
    }

    @Test
    fun testCuandoSeVerificaSiEsPandemiaEsVerdadero(){
        patogeno = patogenoService.crearPatogeno(patogeno)
        ubicacionUNQ = ubicacionService.crearUbicacion(ubicacionUNQ,punto)
        val ubicacion2 = ubicacionService.crearUbicacion(Ubicacion("Polonia"),GeoJsonPoint(17.10,27.20))
        val ubicacion3 =ubicacionService.crearUbicacion(Ubicacion("Rusia"),GeoJsonPoint(18.10,28.20))
        ubicacionService.crearUbicacion(Ubicacion("Indonesia"),GeoJsonPoint(15.10,25.20))
        vector = vectorService.crearVector(VectorHumano("Pepe", ubicacionUNQ))
        val vector2 = vectorService.crearVector(VectorAnimal("Pepita", ubicacion2))
        val vector3 = vectorService.crearVector(VectorInsecto("Sangu", ubicacion3))
        especie = patogenoService.agregarEspecie(patogeno.id!!, "Gripe", ubicacionUNQ.id!!)
        vectorService.infectarVector(vector2.id!!, especie.id!!)
        vectorService.infectarVector(vector3.id!!, especie.id!!)

        assertTrue(patogenoService.esPandemia(especie.id!!))
    }

    @Test
    fun testCuandoSeVerificaSiEsPandemiaConUnIdDeEspecieNoExistenteSeLanzaExcepcion(){
       assertThrows<EspecieIDNoExisteException> { patogenoService.esPandemia(-1) }
    }

    @AfterEach
    fun tearDown(){
        dataService.cleanAll()
        ubicacionMongoDBDAO.deleteAll()
        distritoDAO.deleteAll()
    }
}