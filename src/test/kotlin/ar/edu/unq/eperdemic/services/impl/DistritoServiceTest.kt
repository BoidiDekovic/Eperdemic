package ar.edu.unq.eperdemic.services.impl

import ar.edu.unq.eperdemic.dao.helper.service.DataService
import ar.edu.unq.eperdemic.modelo.*
import ar.edu.unq.eperdemic.modelo.exception.NoHayDistritosConUbicacionesInfectadasException
import ar.edu.unq.eperdemic.modelo.vector.Vector
import ar.edu.unq.eperdemic.modelo.vector.VectorAnimal
import ar.edu.unq.eperdemic.modelo.vector.VectorHumano
import ar.edu.unq.eperdemic.persistencia.dao.UbicacionMongoDBDAO
import ar.edu.unq.eperdemic.persistencia.dao.UbicacionNeo4jDAO
import ar.edu.unq.eperdemic.services.DistritoService
import ar.edu.unq.eperdemic.services.PatogenoService
import ar.edu.unq.eperdemic.services.UbicacionService
import ar.edu.unq.eperdemic.services.VectorService
import ar.edu.unq.eperdemic.services.exception.DistritoIntersectaConOtroException
import ar.edu.unq.eperdemic.services.exception.DistritoNoExisteException
import ar.edu.unq.eperdemic.services.exception.DistritoNombreYaExistenteException
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.mongodb.core.geo.GeoJsonPoint
import org.springframework.data.mongodb.core.geo.GeoJsonPolygon

@ExtendWith
@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class DistritoServiceTest {

    @Autowired
    private lateinit var distritoService: DistritoService
    @Autowired
    private lateinit var ubicacionServiceImpl: UbicacionService
    @Autowired
    private lateinit var vectorService: VectorService
    @Autowired
    private lateinit var dataService: DataService
    @Autowired
    private lateinit var ubicacionNeo4jDAO: UbicacionNeo4jDAO
    @Autowired
    private lateinit var ubicacionMongoDBDAO: UbicacionMongoDBDAO
    @Autowired
    private lateinit var patogenoService: PatogenoService
    private lateinit var distritoBernal: Distrito
    private lateinit var distritoQuilmes: Distrito
    private lateinit var distritoBerazategui: Distrito
    private lateinit var coordenadas: List<GeoJsonPoint>
    private lateinit var coordenadas2: List<GeoJsonPoint>
    private lateinit var coordenadas3: List<GeoJsonPoint>
    private lateinit var forma1: GeoJsonPolygon
    private lateinit var forma2: GeoJsonPolygon
    private lateinit var forma3: GeoJsonPolygon
    private lateinit var ubicacionElPiave: UbicacionMongoDB
    private lateinit var ubicacionBurgerKing: Ubicacion
    private lateinit var ubicacionSubway: Ubicacion
    private lateinit var ubicacionMostaza: Ubicacion
    private lateinit var vectorMartin: Vector
    private lateinit var vectorTomas: Vector
    private lateinit var vectorBullo: Vector
    private lateinit var vectorFirulais: Vector
    private lateinit var ubicacionMcDonals: Ubicacion
    private lateinit var patogenoVirus: Patogeno
    private lateinit var especieCovid: Especie
    @BeforeEach
    fun setUp(){
        ubicacionElPiave = UbicacionMongoDB("Berazategui", GeoJsonPoint(2.0, 2.0))

        coordenadas = listOf(
            GeoJsonPoint(0.0, 0.0),
            GeoJsonPoint(3.0, 6.0),
            GeoJsonPoint(6.0, 1.0),
            GeoJsonPoint(0.0, 0.0)
        )

        coordenadas2 = listOf(
            GeoJsonPoint(10.0, 10.0),
            GeoJsonPoint(13.0, 16.0),
            GeoJsonPoint(16.0, 11.0),
            GeoJsonPoint(10.0, 10.0)
        )

        coordenadas3 = listOf(
            GeoJsonPoint(20.0, 0.0),
            GeoJsonPoint(23.0, 6.0),
            GeoJsonPoint(26.0, 1.0),
            GeoJsonPoint(20.0, 0.0)
        )

        forma1 = GeoJsonPolygon(coordenadas)
        forma2 = GeoJsonPolygon(coordenadas2)
        forma3 = GeoJsonPolygon(coordenadas3)
        distritoBernal = Distrito("Bernal", forma1)
        distritoQuilmes = Distrito("Quilmes", forma2)
        distritoBerazategui = Distrito ("Berazategui", forma3)

        ubicacionMcDonals = Ubicacion("McDonals")
        ubicacionBurgerKing = Ubicacion("BurgerKing")
        ubicacionSubway = Ubicacion("Subway")
        ubicacionMostaza = Ubicacion("Mostaza")

        patogenoVirus = Patogeno("Virus", 6, 73, 52, 32, 33)
        patogenoService.crearPatogeno(patogenoVirus)
    }

    @Test
    fun testAlCrearUnDistritoYRecuperarloSeObtienenObjetosSimilares() {
        distritoService.crearDistrito(distritoBernal)
        val distritoRecuperado = distritoService.recuperarDistritoPorNombre(distritoBernal.nombre!!)

        assertEquals(distritoBernal.nombre, distritoRecuperado.nombre)
        assertEquals(distritoBernal.forma, distritoRecuperado.forma)
        assertEquals(distritoBernal.ubicaciones, distritoRecuperado.ubicaciones)
    }

    @Test
    fun testAlCrearUnDistritoConUbicacionesYRecuperarloSeObtienenObjetosSimilares() {
        distritoBernal.agregarUbicacion(ubicacionElPiave)
        distritoService.crearDistrito(distritoBernal)
        val distritoRecuperado = distritoService.recuperarDistritoPorNombre(distritoBernal.nombre!!)

        assertEquals(distritoBernal.nombre, distritoRecuperado.nombre)
        assertEquals(distritoBernal.forma, distritoRecuperado.forma)
        assertEquals(distritoBernal.ubicaciones, distritoRecuperado.ubicaciones)
    }

    @Test
    fun testAlCrearUnDistritoConMismoNombreSeLanzaException() {
        distritoService.crearDistrito(distritoBernal)
        assertThrows<DistritoNombreYaExistenteException> { distritoService.crearDistrito(distritoBernal) }
    }

    @Test
    fun testAlCrearUnDistritoQueIntersectaConOtroSeLanzaExcepcion(){
        val coordenadasQueIntersectan = listOf(
            GeoJsonPoint(0.0, 0.0),
            GeoJsonPoint(1.0, 6.0),
            GeoJsonPoint(6.0, 1.0),
            GeoJsonPoint(0.0, 0.0))

        distritoQuilmes = Distrito("Quilmes", GeoJsonPolygon(coordenadasQueIntersectan))
        distritoService.crearDistrito(distritoBernal)
        assertThrows<DistritoIntersectaConOtroException> { distritoService.crearDistrito(distritoQuilmes) }
    }

    @Test
    fun testAlIntentarRecuperarUnDistritoPorNombreNoPersistidoSeLanzaDistritoNoExisteException() {
        assertThrows<DistritoNoExisteException> { distritoService.recuperarDistritoPorNombre("Belgrano") }
    }

    @Test
    fun testAlIntentarActualizarUnDistritoNoPersistidoSeLanzaDistritoNoExisteException() {
        val distritoBelgrano = Distrito("Belgrano", forma1)
        assertThrows<DistritoNoExisteException> { distritoService.actualizarDistrito(distritoBelgrano) }
    }

    @Test
    fun testAlIntentarActualizarUnDistritoConUnNombreYaPersistidoSeLanzaDistritoConNombreYaExisteException() {
        distritoBernal.nombre = "Berazategui"
        assertThrows<DistritoNoExisteException> { distritoService.actualizarDistrito(distritoBernal) }
    }

    @Test
    fun testAlIntentarActualizarUnDistritoQueIntersectariaConOtroSeLanzaDistritoIntersectaConOtroException() {
        distritoService.crearDistrito(distritoBernal)
        distritoService.crearDistrito(distritoQuilmes)
        distritoQuilmes.forma = forma1

        assertThrows<DistritoIntersectaConOtroException> { distritoService.actualizarDistrito(distritoQuilmes) }
    }

    @Test
    fun testAlActualizarUnDistritoSeActualizaCorrectamente() {
        distritoService.crearDistrito(distritoBernal)
        var distritoRecuperado = distritoService.recuperarDistritoPorNombre(distritoBernal.nombre!!)

        assertEquals(distritoBernal.nombre, distritoRecuperado.nombre)
        assertEquals(distritoBernal.forma, distritoRecuperado.forma)
        assertEquals(distritoBernal.ubicaciones, distritoRecuperado.ubicaciones)

        distritoBernal.agregarUbicacion(ubicacionElPiave)
        distritoService.actualizarDistrito(distritoBernal)
        distritoRecuperado = distritoService.recuperarDistritoPorNombre(distritoBernal.nombre!!)

        assertEquals(1, distritoRecuperado.ubicaciones.size)
        assertTrue(distritoRecuperado.ubicaciones.contains(ubicacionElPiave))
    }

    @Test
    fun testCuandoPreguntoCualEsElDistritoMasEnfermoYNoHayInfectadosSeLanzaNoHayDistritosConUbicacionesInfectadasException(){
        distritoService.crearDistrito(distritoBernal)
        distritoService.crearDistrito(distritoQuilmes)
        distritoService.crearDistrito(distritoBerazategui)

        ubicacionServiceImpl.crearUbicacion(ubicacionBurgerKing, GeoJsonPoint(2.0, 3.0))
        ubicacionServiceImpl.crearUbicacion(ubicacionSubway, GeoJsonPoint(2.0, 3.0))
        ubicacionServiceImpl.crearUbicacion(ubicacionMostaza,  GeoJsonPoint(12.0, 13.0))

        vectorMartin = VectorHumano("Martin", ubicacionBurgerKing)
        vectorTomas = VectorHumano("Tomas",ubicacionBurgerKing)
        vectorBullo = VectorAnimal("Bullo" , ubicacionSubway)

        assertThrows<NoHayDistritosConUbicacionesInfectadasException> { distritoService.distritoMasEnfermo() }
    }

    @Test
    fun testCuandoPreguntoCualEsElDistritoMasEnfermoYHayUnoSoloConInfectadosMeDevuelveElMismoDistrito() {
        distritoQuilmes = distritoService.crearDistrito(distritoQuilmes)

        vectorMartin = VectorHumano("Martin", ubicacionSubway)

        ubicacionSubway =  ubicacionServiceImpl.crearUbicacion(ubicacionSubway, GeoJsonPoint(13.0, 16.0))

        vectorMartin = vectorService.crearVector(vectorMartin)

        especieCovid = patogenoService.agregarEspecie(patogenoVirus.id!!, "COVID", ubicacionSubway.id!!)

        vectorService.infectarVector(vectorMartin.id!!, especieCovid.id!!)

        assertEquals(distritoQuilmes.nombre, distritoService.distritoMasEnfermo().nombre)
    }
    @Test
    fun testCuandoPreguntoCualEsElDistritoMasEnfermoEntreVariosMeDevuelveElDistritoEsperado() {
        val distritoBernal = distritoService.crearDistrito(distritoBernal)
        distritoService.crearDistrito(distritoQuilmes)
        distritoService.crearDistrito(distritoBerazategui)

        vectorMartin = VectorHumano("Martin", ubicacionBurgerKing)
        vectorTomas = VectorHumano("Tomas", ubicacionMcDonals)
        vectorFirulais = VectorAnimal("Firulais" , ubicacionMcDonals)
        vectorBullo = VectorAnimal("Bullo" , ubicacionMostaza)

        ubicacionBurgerKing = ubicacionServiceImpl.crearUbicacion(ubicacionBurgerKing, GeoJsonPoint(2.0, 3.0))
        ubicacionSubway = ubicacionServiceImpl.crearUbicacion(ubicacionSubway, GeoJsonPoint(11.0, 12.0))
        ubicacionMcDonals =  ubicacionServiceImpl.crearUbicacion(ubicacionMcDonals,GeoJsonPoint(2.0,3.0))
        ubicacionMostaza = ubicacionServiceImpl.crearUbicacion(ubicacionMostaza,  GeoJsonPoint(12.0, 13.0))

        vectorMartin = vectorService.crearVector(vectorMartin)
        vectorTomas = vectorService.crearVector(vectorTomas)
        vectorFirulais = vectorService.crearVector(vectorFirulais)
        vectorBullo = vectorService.crearVector(vectorBullo)

        especieCovid = patogenoService.agregarEspecie(patogenoVirus.id!!, "COVID", ubicacionMcDonals.id!!)

        vectorService.infectarVector(vectorMartin.id!!, especieCovid.id!!)
        vectorService.infectarVector(vectorTomas.id!!, especieCovid.id!!)
        vectorService.infectarVector(vectorBullo.id!!, especieCovid.id!!)
        vectorService.infectarVector(vectorFirulais.id!!, especieCovid.id!!)

        assertEquals(distritoBernal.nombre,distritoService.distritoMasEnfermo().nombre)
    }

    @Test
    fun testCuandoPreguntoCualEsElDistritoMasEnfermoYHayDosConLaMismaCantidadDeInfectadosSeDevuelveElPrimeroQueSePersistio() {
        distritoBerazategui = distritoService.crearDistrito(distritoBerazategui)
        distritoService.crearDistrito(distritoBernal)


        vectorMartin = VectorHumano("Martin", ubicacionBurgerKing)
        vectorTomas = VectorHumano("Tomas",ubicacionSubway)
        vectorFirulais = VectorAnimal("Firulais" , ubicacionMcDonals)
        vectorBullo = VectorAnimal("Bullo" , ubicacionMostaza)

        ubicacionBurgerKing = ubicacionServiceImpl.crearUbicacion(ubicacionBurgerKing, GeoJsonPoint(2.0, 3.0))
        ubicacionSubway = ubicacionServiceImpl.crearUbicacion(ubicacionSubway, GeoJsonPoint(23.0, 6.0))
        ubicacionMcDonals =  ubicacionServiceImpl.crearUbicacion(ubicacionMcDonals, GeoJsonPoint(2.0,3.0))
        ubicacionMostaza = ubicacionServiceImpl.crearUbicacion(ubicacionMostaza,  GeoJsonPoint(26.0, 1.0))

        vectorMartin = vectorService.crearVector(vectorMartin)
        vectorTomas = vectorService.crearVector(vectorTomas)
        vectorFirulais = vectorService.crearVector(vectorFirulais)
        vectorBullo = vectorService.crearVector(vectorBullo)

        especieCovid = patogenoService.agregarEspecie(patogenoVirus.id!!, "COVID", ubicacionMcDonals.id!!)

        vectorService.infectarVector(vectorMartin.id!!, especieCovid.id!!)
        vectorService.infectarVector(vectorTomas.id!!, especieCovid.id!!)
        vectorService.infectarVector(vectorBullo.id!!, especieCovid.id!!)
        vectorService.infectarVector(vectorFirulais.id!!, especieCovid.id!!)

        assertEquals(distritoBerazategui.nombre,distritoService.distritoMasEnfermo().nombre)
    }

    @AfterEach
    fun tearDown() {
        distritoService.deleteAll()
        dataService.cleanAll()
        ubicacionNeo4jDAO.detachDeleteAll()
        ubicacionMongoDBDAO.deleteAll()
    }
}