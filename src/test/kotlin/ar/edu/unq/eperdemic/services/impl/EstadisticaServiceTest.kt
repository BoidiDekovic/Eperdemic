package ar.edu.unq.eperdemic.services.impl

import ar.edu.unq.eperdemic.dao.helper.service.DataService
import ar.edu.unq.eperdemic.modelo.*
import ar.edu.unq.eperdemic.modelo.vector.Vector
import ar.edu.unq.eperdemic.modelo.vector.VectorAnimal
import ar.edu.unq.eperdemic.modelo.vector.VectorHumano
import ar.edu.unq.eperdemic.modelo.vector.VectorInsecto
import ar.edu.unq.eperdemic.persistencia.dao.*
import ar.edu.unq.eperdemic.services.EstadisticaService
import ar.edu.unq.eperdemic.services.PatogenoService
import ar.edu.unq.eperdemic.services.UbicacionService
import ar.edu.unq.eperdemic.services.VectorService
import ar.edu.unq.eperdemic.services.exception.EstadisticaNoHayEspeciesException
import ar.edu.unq.eperdemic.services.exception.UbicacionNoExistenteException
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.mongodb.core.geo.GeoJsonPoint
import org.springframework.data.mongodb.core.geo.GeoJsonPolygon
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
@SpringBootTest
@TestInstance(PER_CLASS)
class EstadisticaServiceTest {

    @Autowired private lateinit var dataService: DataService
    @Autowired private lateinit var ubicacionService: UbicacionService
    @Autowired private lateinit var estadisticaService: EstadisticaService
    @Autowired private lateinit var vectorService: VectorService
    @Autowired private lateinit var patogenoService: PatogenoService
    @Autowired private lateinit var distritoServiceImpl: DistritoServiceImpl
    @Autowired private lateinit var distritoDAO: DistritoDAO
    @Autowired private lateinit var ubicacionMongoDBDAO: UbicacionMongoDBDAO

    private lateinit var ubicacionUNQ: Ubicacion
    private lateinit var distritoBernal: Distrito
    private lateinit var coordenadas: List<GeoJsonPoint>
    private lateinit var forma: GeoJsonPolygon

    private lateinit var vectorPichicho: Vector
    private lateinit var vectorCucatrap: Vector
    private lateinit var vectorMantis: Vector
    private lateinit var vectorBenito: Vector
    private lateinit var vectorJulian: Vector

    private lateinit var patogenoBacteria: Patogeno
    private lateinit var patogenoVirus: Patogeno
    private lateinit var patogenoHongo: Patogeno

    private lateinit var especieTuberculosis: Especie
    private lateinit var especieCovid: Especie
    private lateinit var especieCordyceps: Especie
    private lateinit var punto: GeoJsonPoint

    @BeforeEach
    fun setUp() {
        punto = GeoJsonPoint(12.10,22.20)
        ubicacionUNQ = Ubicacion("UNQ")

        vectorBenito = VectorHumano("Benito", ubicacionUNQ)
        vectorJulian = VectorHumano("Julian", ubicacionUNQ)
        vectorCucatrap = VectorInsecto("Cucatrap", ubicacionUNQ)
        vectorMantis = VectorInsecto("Mantis", ubicacionUNQ)
        vectorPichicho = VectorAnimal("Pichicho", ubicacionUNQ)

        patogenoVirus = Patogeno("Virus", 91, 35, 12, 3, 90)
        patogenoBacteria = Patogeno("Bacteria", 13, 59, 1, 53, 93)
        patogenoHongo = Patogeno("Hongo", 62, 9, 3, 83, 33)

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
    fun testCuandoSeSolicitaLaEspecieLiderYNoHayEspeciesSeLanzaException() {
        assertThrows<EstadisticaNoHayEspeciesException> { estadisticaService.especieLider() }
    }
    
    @Test
    fun testCuandoSeSolicitaLaEspecieLiderSeRetornaLaEsperada() {
        ubicacionService.crearUbicacion(ubicacionUNQ,punto)
        vectorService.crearVector(vectorJulian)
        patogenoService.crearPatogeno(patogenoVirus)

        especieCovid = patogenoService.agregarEspecie(patogenoVirus.id!!, "COVID", ubicacionUNQ.id!!)
        val especieLiderObtenida: Especie = estadisticaService.especieLider()

        assertEquals(especieCovid.nombre, especieLiderObtenida.nombre)
    }

    @Test
    fun testCuandoSeSolicitaLaEspecieLiderYHayEmpateSeRetornaLaEsperada() {
        ubicacionService.crearUbicacion(ubicacionUNQ,punto)
        vectorService.crearVector(vectorJulian)
        vectorService.crearVector(vectorBenito)
        patogenoService.crearPatogeno(patogenoVirus)
        patogenoService.crearPatogeno(patogenoHongo)

        especieCovid = patogenoService.agregarEspecie(patogenoVirus.id!!, "COVID", ubicacionUNQ.id!!)
        especieCordyceps = patogenoService.agregarEspecie(patogenoHongo.id!!, "Cordyceps", ubicacionUNQ.id!!)

        val especieLiderObtenida: Especie = estadisticaService.especieLider()

        assertEquals(especieCordyceps.nombre, especieLiderObtenida.nombre)
    }

    @Test
    fun testCuandoSeSolicitaLasEspeciesLideresYNoHaySeRetornaUnaListaVacia() {
        val listaLideres: List<Especie> = estadisticaService.lideres()
        assertTrue(listaLideres.isEmpty())
    }

    @Test
    fun testCuandoSeSolicitaLasEspeciesLideresSeRetornaLaListaEsperada() {
        ubicacionService.crearUbicacion(ubicacionUNQ,punto)
        vectorService.crearVector(vectorJulian)
        vectorService.crearVector(vectorBenito)
        vectorService.crearVector(vectorPichicho)
        vectorService.crearVector(vectorCucatrap)
        patogenoService.crearPatogeno(patogenoVirus)
        patogenoService.crearPatogeno(patogenoBacteria)
        patogenoService.crearPatogeno(patogenoHongo)

        especieCovid = patogenoService.agregarEspecie(patogenoVirus.id!!, "COVID", ubicacionUNQ.id!!)
        especieTuberculosis = patogenoService.agregarEspecie(patogenoBacteria.id!!, "Tuberculosis", ubicacionUNQ.id!!)
        especieCordyceps = patogenoService.agregarEspecie(patogenoHongo.id!!, "Cordyceps", ubicacionUNQ.id!!)

        vectorService.infectarVector(vectorJulian.id!!, especieCordyceps.id!!)
        vectorService.infectarVector(vectorBenito.id!!, especieCordyceps.id!!)
        vectorService.infectarVector(vectorPichicho.id!!, especieCordyceps.id!!)
        vectorService.infectarVector(vectorJulian.id!!, especieCovid.id!!)
        vectorService.infectarVector(vectorPichicho.id!!, especieCovid.id!!)
        vectorService.infectarVector(vectorBenito.id!!, especieTuberculosis.id!!)
        val listaLideres: List<Especie> = estadisticaService.lideres()

        assertEquals(3, listaLideres.size)
        assertEquals("Cordyceps", listaLideres.elementAt(0).nombre)
        assertEquals("COVID", listaLideres.elementAt(1).nombre)
        assertEquals("Tuberculosis", listaLideres.elementAt(2).nombre)
    }

    @Test
    fun testCuandoSeSolicitaunReporteDeContagiosEnUnaUbicacionQueNoExisteSeLanzaUnaUbicacionNoExistenteException() {
        val ubicacionNoExistente = "Groenlandia"
        assertThrows<UbicacionNoExistenteException> { estadisticaService.reporteDeContagios(ubicacionNoExistente) }
    }

    @Test
    fun testCuandoSeSolicitaunReporteDeContagiosEnUnaUbicacionVaciaSeRetornaElReporteEsperado() {
        ubicacionService.crearUbicacion(ubicacionUNQ,punto)
        val reporteDeContagio: ReporteDeContagio = estadisticaService.reporteDeContagios(ubicacionUNQ.nombre!!)

        assertEquals(0, reporteDeContagio.cantidadVectoresPresentes)
        assertEquals(0, reporteDeContagio.cantidadVectoresInfectados)
        assertEquals("-", reporteDeContagio.especieMasInfecciosa)
    }

    @Test
    fun testCuandoSeSolicitaunReporteDeContagiosEnUnaUbicacionSeRetornaElReporteEsperado() {
        ubicacionService.crearUbicacion(ubicacionUNQ,punto)
        patogenoService.crearPatogeno(patogenoHongo)
        patogenoService.crearPatogeno(patogenoBacteria)
        vectorService.crearVector(vectorJulian)
        especieCordyceps = patogenoService.agregarEspecie(patogenoHongo.id!!, "Cordyceps", ubicacionUNQ.id!!)
        especieTuberculosis = patogenoService.agregarEspecie(patogenoBacteria.id!!, "Tuberculosis", ubicacionUNQ.id!!)
        vectorService.crearVector(vectorBenito)
        vectorService.crearVector(vectorPichicho)
        vectorService.crearVector(vectorCucatrap)

        vectorService.infectarVector(vectorJulian.id!!, especieCordyceps.id!!)
        val reporteDeContagio: ReporteDeContagio = estadisticaService.reporteDeContagios(ubicacionUNQ.nombre!!)

        assertEquals(4, reporteDeContagio.cantidadVectoresPresentes)
        assertEquals(1, reporteDeContagio.cantidadVectoresInfectados)
        assertEquals("Cordyceps", reporteDeContagio.especieMasInfecciosa)
    }

    @Test
    fun testCuandoSeSolicitaLasEspeciesLideresLaListaEsVaciaPorqueSoloSeContagiaronVectoresDeTipoInsecto(){
        vectorService.crearVector(vectorCucatrap)
        vectorService.crearVector(vectorMantis)
        patogenoService.crearPatogeno(patogenoVirus)
        patogenoService.crearPatogeno(patogenoBacteria)
        patogenoService.crearPatogeno(patogenoHongo)

        especieCovid = patogenoService.agregarEspecie(patogenoVirus.id!!, "COVID", ubicacionUNQ.id!!)
        especieTuberculosis = patogenoService.agregarEspecie(patogenoBacteria.id!!, "Tuberculosis", ubicacionUNQ.id!!)
        especieCordyceps = patogenoService.agregarEspecie(patogenoHongo.id!!, "Cordyceps", ubicacionUNQ.id!!)
        vectorService.infectarVector(vectorCucatrap.id!!, especieCordyceps.id!!)
        vectorService.infectarVector(vectorMantis.id!!, especieCordyceps.id!!)

        val listaLideres: List<Especie> = estadisticaService.lideres()
        assertTrue(listaLideres.isEmpty())
    }

    @Test
    fun testCuandoSeSolicitaLaEspecieLiderYNoHayEspeciesLideresSeLanzaExceptionNoHayEspecies() {
        vectorService.crearVector(vectorCucatrap)
        patogenoService.crearPatogeno(patogenoVirus)
        especieCovid = patogenoService.agregarEspecie(patogenoVirus.id!!, "COVID", ubicacionUNQ.id!!)

        assertThrows<EstadisticaNoHayEspeciesException> { estadisticaService.especieLider() }
    }

}