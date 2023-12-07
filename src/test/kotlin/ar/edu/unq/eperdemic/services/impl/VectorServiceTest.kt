package ar.edu.unq.eperdemic.services.impl

import ar.edu.unq.eperdemic.dao.helper.service.DataService
import ar.edu.unq.eperdemic.modelo.*
import ar.edu.unq.eperdemic.modelo.vector.Vector
import ar.edu.unq.eperdemic.modelo.vector.VectorAnimal
import ar.edu.unq.eperdemic.modelo.vector.VectorHumano
import ar.edu.unq.eperdemic.modelo.vector.VectorInsecto
import ar.edu.unq.eperdemic.persistencia.dao.DistritoDAO
import ar.edu.unq.eperdemic.persistencia.dao.UbicacionMongoDBDAO
import ar.edu.unq.eperdemic.services.PatogenoService
import ar.edu.unq.eperdemic.services.UbicacionService
import ar.edu.unq.eperdemic.services.VectorService
import ar.edu.unq.eperdemic.services.exception.EspecieIDNoExisteException
import ar.edu.unq.eperdemic.services.exception.VectorConNombreNoExisteException
import ar.edu.unq.eperdemic.services.exception.VectorIDNoExisteException
import ar.edu.unq.eperdemic.services.exception.VectorYaExistenteException
import ar.edu.unq.eperdemic.services.impl.helper.RNG
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.data.mongodb.core.geo.GeoJsonPoint
import org.springframework.data.mongodb.core.geo.GeoJsonPolygon

@ExtendWith
@SpringBootTest
@TestInstance(PER_CLASS)
class VectorServiceTest {

    @Autowired
    private lateinit var dataService: DataService
    @Autowired
    private lateinit var vectorService: VectorService
    @Autowired
    private lateinit var patogenoService: PatogenoService
    @Autowired
    private lateinit var ubicacionService: UbicacionService
    @Autowired
    private lateinit var distritoServiceImpl: DistritoServiceImpl
    @Autowired
    private lateinit var distritoDAO: DistritoDAO
    @Autowired
    private lateinit var ubicacionMongoDBDAO: UbicacionMongoDBDAO

    @MockBean
    @Autowired
    private lateinit var rng: RNG

    private lateinit var vectorJulian: Vector
    private lateinit var vectorFirulais: Vector
    private lateinit var vectorMosquito: Vector

    private lateinit var ubicacionBurgerKing: Ubicacion
    private lateinit var ubicacionUNQ: Ubicacion
    private lateinit var distritoBernal: Distrito
    private lateinit var coordenadas: List<GeoJsonPoint>
    private lateinit var forma: GeoJsonPolygon

    private lateinit var patogenoVirus: Patogeno
    private lateinit var punto: GeoJsonPoint
    private lateinit var punto1: GeoJsonPoint


    @BeforeEach
    fun setUp() {
        ubicacionBurgerKing = Ubicacion("Burger King")
        ubicacionUNQ = Ubicacion("UNQ")

        vectorJulian = VectorHumano("Julian", ubicacionBurgerKing)
        vectorFirulais = VectorAnimal("Firulais", ubicacionUNQ)
        vectorMosquito = VectorInsecto("Mosquito", ubicacionBurgerKing)

        patogenoVirus = Patogeno("Virus", 20, 20, 20, 40, 30)
        punto = GeoJsonPoint(10.10,20.20)
        punto1 = GeoJsonPoint(10.10,20.20)
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

    @AfterEach
    fun tearDown() {
        dataService.cleanAll()
        ubicacionMongoDBDAO.deleteAll()
        distritoDAO.deleteAll()
    }

    @Test
    fun testCuandoSeCreaYSeRecuperaUnVectorSeObtieneObjetosSimilares() {
        val vector = vectorService.crearVector(vectorJulian)
        val vectorObtenido = vectorService.recuperarVector(vector.id!!)

        assertTrue(vectorObtenido.equals(vector))
        assertEquals(vectorObtenido.id, vector.id)
        assertEquals(vectorObtenido.nombre, vector.nombre)
        assertEquals(vectorObtenido.estaInfectado, vector.estaInfectado)
        assertEquals(vectorObtenido.ubicacion, vector.ubicacion)
        assertEquals(vectorObtenido.especiesPadecidas, vector.especiesPadecidas)
    }

    @Test
    fun testCuandoSeCreaUnVectorQueYaFueCreadoSeLanzaVectorYaExistenteException() {
        vectorService.crearVector(vectorJulian)

        assertThrows<VectorYaExistenteException> { vectorService.crearVector(vectorJulian) }
    }

    @Test
    fun testCuandoSeCreaUnVectorEnUnaUbicacionQueYaTeniaUnVectorSeCreaCorrectamente() {
        val vector1 = vectorService.crearVector(vectorJulian)
        val vector2 = vectorService.crearVector(vectorMosquito)

        assertEquals(vector1.ubicacion, vector2.ubicacion)
    }

    @Test
    fun testCuandoSeQuiereRecuperarUnVectorQueNoExisteSeLanzaVectorIDNoExisteException() {
        assertThrows<VectorIDNoExisteException> { vectorService.recuperarVector(-1) }
    }

    @Test
    fun testCuandoSeQuiereRecuperarUnVectorConNombreQueNoExisteSeLanzaVectorConNombreNoExisteException(){
        assertThrows<VectorConNombreNoExisteException> { vectorService.recuperarVectorPorNombre("coco") }
    }

    @Test
    fun testCuandoQuieroRecuperarUnVectorPorElNombreMeDevuelveElVectorDelNombreDado(){
        vectorJulian = vectorService.crearVector(vectorJulian)

        assertEquals(vectorJulian , vectorService.recuperarVectorPorNombre("Julian"))
    }

    @Test
    fun testCuandoSeActualizaUnVectorEsteLoHaceCorrectamente() {
        val vector = vectorService.crearVector(vectorJulian)
        assertEquals(vector.ubicacion, vectorJulian.ubicacion)
        val ubicacion = ubicacionService.crearUbicacion(ubicacionUNQ,punto)
        vector.moverseA(ubicacion)
        vectorService.actualizarVector(vector)
        val vectorActualizado = vectorService.recuperarVector(vector.id!!)

        assertEquals(vector.ubicacion, vectorActualizado.ubicacion)
    }

    @Test
    fun testCuandoSeQuiereActualizarUnVectorQueNoExisteSeLanzaVectorIDNoExisteException() {
        assertThrows<VectorIDNoExisteException> { vectorService.actualizarVector(vectorFirulais) }
    }

    @Test
    fun testCuandoSeRecuperanTodosLosVectoresSeObtieneLaListaConTodos() {
        vectorService.crearVector(vectorJulian)
        vectorService.crearVector(vectorFirulais)
        vectorService.crearVector(vectorMosquito)
        val listaDeVectoresObtenida = vectorService.recuperarTodosLosVectores()

        assertTrue(listaDeVectoresObtenida.size == 3)
        assertTrue(listaDeVectoresObtenida.contains(vectorJulian))
        assertTrue(listaDeVectoresObtenida.contains(vectorFirulais))
        assertTrue(listaDeVectoresObtenida.contains(vectorMosquito))
    }

    @Test
    fun testCuandoSeRecuperanTodosYNoHayVectoresSeObtieneLaListaVacia() {
        val listaDeVectoresObtenida = vectorService.recuperarTodosLosVectores()

        assertTrue(listaDeVectoresObtenida.isEmpty())
    }

    @Test
    fun testCuandoSeCreaYEliminaUnVectorSeVerificaQueEsteNoContinuaPersistido() {
        vectorJulian = vectorService.crearVector(vectorJulian)
        vectorService.eliminarVector(vectorJulian.nombre)
        assertThrows<VectorIDNoExisteException> { vectorService.recuperarVector(vectorJulian.id!!) }
    }

    @Test
    fun testCuandoUnVectorSeInfectaConUnaEspecieEstaSeAgregaASusEspeciesPadecidas() {
        patogenoService.crearPatogeno(patogenoVirus)
        val vector = vectorService.crearVector(vectorJulian)
        val especie = patogenoService.agregarEspecie(patogenoVirus.id!!, "COVID", ubicacionBurgerKing.id!!)
        assertTrue(vector.especiesPadecidas.isEmpty())
        assertFalse(vector.estaInfectado)

        vectorService.infectarVector(vector.id!!, especie.id!!)
        val vectorActualizado = vectorService.recuperarVector(vector.id!!)
        assertEquals(1, vectorActualizado.especiesPadecidas.size)
        assertTrue(vectorActualizado.estaInfectado)
    }

    @Test
    fun testCuandoSeQuiereInfectarUnVectorConUnaEspeciePeroNoExisteElVectorSeLanzaVectorIDNoExisteException() {
        val patogenoVirus = patogenoService.crearPatogeno(patogenoVirus)
        val ubicacion = ubicacionService.crearUbicacion(ubicacionBurgerKing,punto1)
        vectorService.crearVector(vectorJulian)

        patogenoService.agregarEspecie(patogenoVirus.id!!, "COVID", ubicacion.id!!)

        assertThrows<VectorIDNoExisteException> { vectorService.infectarVector(-2, 5) }
    }

    @Test
    fun testCuandoSeQuiereInfectarUnVectorConUnaEspeciePeroNoExisteLaEspecieSeLanzaEspecieIDNoExisteException() {
        val vector = vectorService.crearVector(vectorJulian)

        assertThrows<EspecieIDNoExisteException> { vectorService.infectarVector(vector.id!!, -1) }
    }

    @Test
    fun testCuandoSePidenLasEnfermedadesDeUnVectorSeDevuelveLaListaCorrectamente() {
        val vector = vectorService.crearVector(vectorJulian)
        val patogenoVirus = patogenoService.crearPatogeno(patogenoVirus)

        val especieCovid = patogenoService.agregarEspecie(patogenoVirus.id!!, "Covid", ubicacionBurgerKing.id!!)
        val especieKrippin = patogenoService.agregarEspecie(patogenoVirus.id!!, "Krippin", ubicacionBurgerKing.id!!)
        vectorService.infectarVector(vector.id!!, especieCovid.id!!)
        vectorService.infectarVector(vector.id!!, especieKrippin.id!!)

        assertEquals(2, vectorService.enfermedadesDelVector(vector.id!!).size)
        assertTrue(vectorService.enfermedadesDelVector(vector.id!!).contains(especieCovid))
        assertTrue(vectorService.enfermedadesDelVector(vector.id!!).contains(especieKrippin))
    }

    @Test
    fun testCuandoSePidenLasEnfermedadesDeUnVectorQueNoPadeceEnfermedadesSeDevuelveLaListaVacia() {
        val vector = vectorService.crearVector(vectorJulian)

        assertTrue(vectorService.enfermedadesDelVector(vector.id!!).isEmpty())
    }

    @Test
    fun testCuandoSePidenLasEnfermedadesDeUnVectorPeroNoExisteSeLanzaVectorIDNoExisteException() {
        assertThrows<VectorIDNoExisteException> { vectorService.enfermedadesDelVector(-1) }
    }

    @Test
    fun testCuandoSeQuiereContagiarPeroElVectorContagiadoNoExisteSeLanzaVectorIDNoExisteException() {
        vectorJulian = vectorService.crearVector(vectorJulian)
        assertThrows<VectorIDNoExisteException> { vectorService.contagiar(-1, vectorJulian.id!!) }
    }
    @Test
    fun testCuandoSeQuiereContagiarPeroElVectorAContagiarNoExisteSeLanzaVectorIDNoExisteException() {
        vectorJulian = vectorService.crearVector(vectorJulian)
        assertThrows<VectorIDNoExisteException> { vectorService.contagiar(vectorJulian.id!!, -1) }
    }

    @Test
    fun testCuandoSeQuiereContagiarPeroAlgunoDeEllosNoExisteSeLanzaVectorIDNoExisteException() {
        assertThrows<VectorIDNoExisteException> { vectorService.contagiar(-1, -2) }
    }

    @Test
    fun testCuandoUnVectorConEnfermedadesQuiereContagiarAOtroVectorLoHaceCorrectamente() {
        val vector1 = vectorService.crearVector(vectorFirulais)
        var vector2 = vectorService.crearVector(vectorJulian)
        patogenoService.crearPatogeno(patogenoVirus)
        assertTrue(vector2.especiesPadecidas.isEmpty())

        `when`(rng.getRandomNumber(1, 10)).thenReturn(10)
        `when`(rng.determinarProbabilidad(anyInt())).thenReturn (true)
        val especie1 = patogenoService.agregarEspecie(patogenoVirus.id!!, "COVID", ubicacionUNQ.id!!)
        vectorService.infectarVector(vector1.id!!, especie1.id!!)
        vectorService.contagiar(vector1.id!!, vector2.id!!)
        vector2 = vectorService.recuperarVector(vector2.id!!)

        assertEquals(1, vector2.especiesPadecidas.size)
        assertTrue(vector2.especiesPadecidas.contains(especie1))
    }

    @Test
    fun testCuandoUnVectorConEnfermedadesQuiereContagiarAOtroVectorYNoTieneProbabilidadesNoLoHace() {
        val vector1 = vectorService.crearVector(vectorFirulais)
        var vector2 = vectorService.crearVector(vectorJulian)
        patogenoService.crearPatogeno(patogenoVirus)
        assertTrue(vector2.especiesPadecidas.isEmpty())

        `when`(rng.getRandomNumber(1, 10)).thenReturn(1)
        `when`(rng.getRandomNumber(1, 100)).thenReturn (100)
        val especie1 = patogenoService.agregarEspecie(patogenoVirus.id!!, "COVID", ubicacionUNQ.id!!)
        vectorService.infectarVector(vector1.id!!, especie1.id!!)
        vectorService.contagiar(vector1.id!!, vector2.id!!)
        vector2 = vectorService.recuperarVector(vector2.id!!)

        assertEquals(0, vector2.especiesPadecidas.size)
        assertFalse(vector2.especiesPadecidas.contains(especie1))
    }
}