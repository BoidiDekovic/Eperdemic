package ar.edu.unq.eperdemic.services.impl

import ar.edu.unq.eperdemic.dao.helper.service.DataService
import ar.edu.unq.eperdemic.modelo.*
import ar.edu.unq.eperdemic.modelo.vector.Vector
import ar.edu.unq.eperdemic.modelo.vector.VectorAnimal
import ar.edu.unq.eperdemic.persistencia.dao.DistritoDAO
import ar.edu.unq.eperdemic.persistencia.dao.UbicacionMongoDBDAO
import ar.edu.unq.eperdemic.services.EspecieService
import ar.edu.unq.eperdemic.services.PatogenoService
import ar.edu.unq.eperdemic.services.UbicacionService
import ar.edu.unq.eperdemic.services.VectorService
import ar.edu.unq.eperdemic.services.exception.EspecieIDNoExisteException
import ar.edu.unq.eperdemic.services.exception.EspecieNombreYaExistenteException
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.mongodb.core.geo.GeoJsonPoint
import org.springframework.data.mongodb.core.geo.GeoJsonPolygon

@ExtendWith
@SpringBootTest
@TestInstance(PER_CLASS)
class EspecieServiceTest {

    @Autowired
    private lateinit var dataService: DataService
    @Autowired
    private lateinit var ubicacionService: UbicacionService
    @Autowired
    private lateinit var especieService: EspecieService
    @Autowired
    private lateinit var vectorService: VectorService
    @Autowired
    private lateinit var patogenoService: PatogenoService
    @Autowired
    private lateinit var distritoServiceImpl: DistritoServiceImpl
    @Autowired
    private lateinit var ubicacionMongoDBDAO: UbicacionMongoDBDAO
    @Autowired
    private lateinit var distritoDAO: DistritoDAO

    private lateinit var punto: GeoJsonPoint

    private lateinit var ubicacionArgentina: Ubicacion

    private lateinit var distritoBernal: Distrito
    private lateinit var coordenadas: List<GeoJsonPoint>
    private lateinit var forma: GeoJsonPolygon

    private lateinit var vectorPichicho: Vector

    private lateinit var patogenoVirus: Patogeno

    private lateinit var especieCovid: Especie
    private lateinit var especieMalaria: Especie


    @BeforeEach
    fun setUp() {
        coordenadas = listOf(
            GeoJsonPoint(0.0, 0.0),
            GeoJsonPoint(30.0, 60.0),
            GeoJsonPoint(60.0, 10.0),
            GeoJsonPoint(0.0, 0.0),
        )
        forma = GeoJsonPolygon(coordenadas)
        distritoBernal = Distrito("Bernal", forma)
        distritoServiceImpl.crearDistrito(distritoBernal)
        punto = GeoJsonPoint(10.10,20.20)
        ubicacionArgentina = Ubicacion("Argentina")
        ubicacionService.crearUbicacion(ubicacionArgentina,punto)

        vectorPichicho = VectorAnimal("Pichicho", ubicacionArgentina)
        vectorService.crearVector(vectorPichicho)

        patogenoVirus = Patogeno("Virus", 6, 73, 52, 32, 33)
        patogenoService.crearPatogeno(patogenoVirus)

        especieCovid = patogenoService.agregarEspecie(patogenoVirus.id!!, "COVID", ubicacionArgentina.id!!)
        especieMalaria = patogenoService.agregarEspecie(patogenoVirus.id!!, "Malaria", ubicacionArgentina.id!!)

    }

    @AfterEach
    fun tearDown() {
        dataService.cleanAll()
        ubicacionMongoDBDAO.deleteAll()
        distritoDAO.deleteAll()
    }

    @Test
    fun testCuandoSeRecuperaUnaEspecieQueNoExisteSeLanzaEspecieIDNoExisteException() {
        val idNoExistente: Long = 900000
        assertThrows<EspecieIDNoExisteException> { especieService.recuperarEspecie(idNoExistente) }
    }

    @Test
    fun testCuandoSeRecuperaUnaEspecieEsLaEsperada() {
        val especieObtenida: Especie = especieService.recuperarEspecie(especieCovid.id!!)

        assertEquals(especieObtenida.id, especieCovid.id)
        assertEquals(especieObtenida.nombre, especieCovid.nombre)
        assertEquals(especieObtenida.patogeno, especieCovid.patogeno)
        assertEquals(especieObtenida.paisDeOrigen, especieCovid.paisDeOrigen)
        assertTrue(especieObtenida.mutacionesPosibles.isEmpty())
    }

    @Test
    fun testAlActualizarUnaEspecieQueNoExisteSeLanzaEspecieIDNoExisteException() {
        val especie = Especie()
        especie.id = -1
        assertThrows<EspecieIDNoExisteException> { especieService.actualizarEspecie(especie) }
    }
@Test
fun testAlActualizarUnaEspecieConElNombreDeUnaEspecieQueYaExisteLanzaEspecieYaExisteException(){
    especieMalaria.nombre = "COVID"
    assertThrows<EspecieNombreYaExistenteException> { especieService.actualizarEspecie(especieMalaria) }
}


    @Test
    fun testCuandoSeActualizaUnaEspecieEstaSeModificaCorrectamente() {
        assertEquals("Argentina", especieCovid.paisDeOrigen)

        especieCovid.paisDeOrigen = "Tailandia"
        especieService.actualizarEspecie(especieCovid)
        val especieCovidRecuperada: Especie = especieService.recuperarEspecie(especieCovid.id!!)

        assertEquals("Tailandia", especieCovidRecuperada.paisDeOrigen)
    }

    @Test
    fun testCuandoSeRecuperanTodasLasEspeciesSeObtieneLaListaVacia() {
        dataService.cleanAll()

        val listaResultante: List<Especie> = especieService.recuperarTodasLasEspecies()
        assertTrue(listaResultante.isEmpty())
    }

    @Test
    fun testCuandoSeRecuperanTodasLasEspeciesSeObtieneLaListaEsperada() {
        val listaResultante: List<Especie> = especieService.recuperarTodasLasEspecies()

        assertTrue(listaResultante.size == 2)
        assertTrue(listaResultante.contains(especieCovid))
        assertTrue(listaResultante.contains(especieMalaria))
    }

    @Test
    fun testCuandoSeRecuperaLaCantidadDeInfectadosPorCOVIDSeObtieneLaCantidadEsperada2() {
        val idNoExistente: Long = 90000

        assertThrows<EspecieIDNoExisteException> { especieService.cantidadDeInfectadosPorLaEspecie(idNoExistente) }
    }

    @Test
    fun testCuandoSeRecuperaLaCantidadDeInfectadosPorCOVIDSeObtieneLaCantidadEsperada() {
        vectorService.infectarVector(vectorPichicho.id!!, especieCovid.id!!)

        val infectadosEsperados = 1
        val infectadosObtenidos: Int = especieService.cantidadDeInfectadosPorLaEspecie(especieCovid.id!!)

        assertEquals(infectadosEsperados, infectadosObtenidos)
    }
}

