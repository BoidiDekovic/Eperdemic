package ar.edu.unq.eperdemic.services.impl
import ar.edu.unq.eperdemic.dao.helper.service.DataService
import ar.edu.unq.eperdemic.modelo.*
import ar.edu.unq.eperdemic.modelo.vector.Vector
import ar.edu.unq.eperdemic.modelo.vector.VectorAnimal
import ar.edu.unq.eperdemic.modelo.vector.VectorHumano
import ar.edu.unq.eperdemic.modelo.vector.VectorInsecto
import ar.edu.unq.eperdemic.persistencia.dao.DistritoDAO
import ar.edu.unq.eperdemic.persistencia.dao.UbicacionMongoDBDAO
import ar.edu.unq.eperdemic.persistencia.dao.UbicacionNeo4jDAO
import ar.edu.unq.eperdemic.services.PatogenoService
import ar.edu.unq.eperdemic.services.UbicacionService
import ar.edu.unq.eperdemic.services.VectorService
import ar.edu.unq.eperdemic.services.exception.*
import ar.edu.unq.eperdemic.services.impl.helper.RNG
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.data.mongodb.core.geo.GeoJsonPoint
import org.springframework.data.mongodb.core.geo.GeoJsonPolygon
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MoverPorCaminoMasCortoTest {
    @Autowired
    private lateinit var dataService: DataService
    @Autowired
    private lateinit var ubicacionService : UbicacionService
    @Autowired
    private lateinit var vectorService: VectorService
    @Autowired
    private lateinit var patogenoService: PatogenoService
    @Autowired
    private lateinit var ubicacionNeo4jDAO: UbicacionNeo4jDAO
    @Autowired
    private lateinit var distritoServiceImpl: DistritoServiceImpl
    @Autowired
    private lateinit var distritoDAO: DistritoDAO
    @Autowired
    private lateinit var ubicacionMongoDBDAO: UbicacionMongoDBDAO
    @MockBean
    lateinit var rng : RNG

    private lateinit var ubicacionElPiave: Ubicacion
    private lateinit var ubicacionSerBazares: Ubicacion
    private lateinit var ubicacionBurgerKing: Ubicacion
    private lateinit var ubicacionUNQ: Ubicacion
    private lateinit var ubicacionElCharro: Ubicacion
    private lateinit var distritoBernal: Distrito
    private lateinit var coordenadas: List<GeoJsonPoint>
    private lateinit var forma: GeoJsonPolygon
    private lateinit var vectorPerro: Vector
    private lateinit var vectorVinchuca: Vector
    private lateinit var vectorJuan: Vector
    private lateinit var vectorPedro: Vector
    private lateinit var vectorCarlos: Vector
    private lateinit var vectorGeorge: Vector
    private lateinit var vectorJohn: Vector
    private lateinit var patogenoVirus: Patogeno
    private lateinit var especieCovid: Especie


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

        ubicacionElPiave = Ubicacion("El Piave")
        ubicacionBurgerKing   = Ubicacion("Burger King")
        ubicacionSerBazares  = Ubicacion("Ser Bazares")
        ubicacionUNQ   = Ubicacion("UNQ")
        ubicacionElCharro    = Ubicacion("El Charro")
        ubicacionElPiave = ubicacionService.crearUbicacion(ubicacionElPiave, GeoJsonPoint(10.10,20.20))
        ubicacionElCharro    = ubicacionService.crearUbicacion(ubicacionElCharro,GeoJsonPoint(11.10,21.20))
        ubicacionSerBazares  = ubicacionService.crearUbicacion(ubicacionSerBazares,GeoJsonPoint(12.10,22.20))
        ubicacionBurgerKing   = ubicacionService.crearUbicacion(ubicacionBurgerKing,GeoJsonPoint(13.10,23.20))
        ubicacionUNQ   = ubicacionService.crearUbicacion(ubicacionUNQ,GeoJsonPoint(14.10,24.20))

        //El contagio del vector siempre va a ser exitoso
        Mockito.`when`(rng.determinarProbabilidad(Mockito.anyInt())).thenReturn (true)

        patogenoVirus = Patogeno("Virus", 70, 40, 10, 50, 30)
        patogenoVirus = patogenoService.crearPatogeno(patogenoVirus)
        vectorJuan   = vectorService.crearVector(VectorHumano("Juan", ubicacionElPiave))
        especieCovid = patogenoService.agregarEspecie(patogenoVirus.id!!, "COVID", ubicacionElPiave.id!!)

        vectorVinchuca = VectorInsecto("Vinchuca", ubicacionElPiave)
        vectorPerro    = VectorAnimal("Perro", ubicacionElPiave)
        vectorVinchuca = vectorService.crearVector(vectorVinchuca)
        vectorPerro    = vectorService.crearVector(vectorPerro)
        vectorVinchuca.agregarEspeciePadecida(especieCovid)
        vectorPerro.agregarEspeciePadecida(especieCovid)
        vectorService.actualizarVector(vectorVinchuca)
        vectorService.actualizarVector(vectorPerro)
        vectorPerro = vectorService.recuperarVector(vectorPerro.id!!)
        vectorVinchuca = vectorService.recuperarVector(vectorVinchuca.id!!)


        vectorJuan = vectorService.recuperarVector(vectorJuan.id!!)
        vectorPedro  = vectorService.crearVector(VectorHumano("Pedro", ubicacionBurgerKing))
        vectorCarlos = vectorService.crearVector(VectorHumano("Carlos", ubicacionSerBazares))
        vectorGeorge = vectorService.crearVector(VectorHumano("George", ubicacionUNQ))
        vectorJohn = vectorService.crearVector(VectorHumano("John", ubicacionElCharro))



    }

    @Test
    fun testCuandoSePideMoverPorCaminoMasCortoYElVectorNoExisteSeLanzaException(){
        assertThrows<VectorIDNoExisteException> { ubicacionService.moverPorCaminoMasCorto(-1, ubicacionBurgerKing.nombre!!) }
    }

    @Test
    fun testCuandoSePideMoverPorCaminoMasCortoYLaUbicacionDestinoNoExisteSeLanzaException(){
        assertThrows<UbicacionConNombreNoExistenteException> { ubicacionService.moverPorCaminoMasCorto(-1, "Panama") }
    }

    @Test
    fun testCuandoSePideMoverPorCaminoMasCortoElVectorSeMuevePorElCaminoDirectoADestino(){
        ubicacionService.conectar(ubicacionElPiave.nombre!!, ubicacionBurgerKing.nombre!!, "TERRESTRE")
        ubicacionService.conectar(ubicacionBurgerKing.nombre!!, ubicacionElCharro.nombre!!, "TERRESTRE")
        ubicacionService.conectar(ubicacionElPiave.nombre!!, ubicacionElCharro.nombre!!, "TERRESTRE")

        assertEquals(ubicacionElPiave, vectorJuan.ubicacion)
        assertFalse(vectorPedro.estaInfectado)
        assertFalse(vectorJohn.estaInfectado)
        assertTrue(vectorJuan.especiesPadecidas.contains(especieCovid))

        ubicacionService.moverPorCaminoMasCorto(vectorJuan.id!!, ubicacionElCharro.nombre!!)

        vectorJuan = vectorService.recuperarVector(vectorJuan.id!!)
        vectorPedro = vectorService.recuperarVector(vectorPedro.id!!)
        vectorJohn = vectorService.recuperarVector(vectorJohn.id!!)

        assertEquals(ubicacionElCharro, vectorJuan.ubicacion)
        assertFalse(vectorPedro.estaInfectado)
        assertTrue(vectorJohn.especiesPadecidas.contains(especieCovid))
    }

    @Test
    fun testCuandoSePideMoverPorCaminoMasCortoYNoHayCaminosALaUbicacionSeLanzaUbicacionNoAlcanzableException(){
        ubicacionService.conectar(ubicacionBurgerKing.nombre!!, ubicacionElCharro.nombre!!, "TERRESTRE")

        assertEquals(ubicacionElPiave, vectorJuan.ubicacion)
        assertFalse(vectorPedro.estaInfectado)
        assertFalse(vectorJohn.estaInfectado)
        assertTrue(vectorJuan.especiesPadecidas.contains(especieCovid))

        assertThrows<UbicacionNoAlcanzableException> { ubicacionService.moverPorCaminoMasCorto(vectorJuan.id!!, ubicacionElCharro.nombre!!) }

        vectorJuan = vectorService.recuperarVector(vectorJuan.id!!)
        vectorPedro = vectorService.recuperarVector(vectorPedro.id!!)
        vectorJohn = vectorService.recuperarVector(vectorJohn.id!!)

        assertEquals(ubicacionElPiave, vectorJuan.ubicacion)
        assertFalse(vectorPedro.estaInfectado)
        assertFalse(vectorJohn.estaInfectado)
    }

    @Test
    fun testCuandoSePideMoverPorCaminoMasCortoYElVectorNoPuedeAtravesarElTipoDelCaminoSeLanzaUbicacionNoAlcanzableException(){
        ubicacionService.conectar(ubicacionElPiave.nombre!!, ubicacionBurgerKing.nombre!!, "TERRESTRE")
        ubicacionService.conectar(ubicacionElPiave.nombre!!, ubicacionSerBazares.nombre!!, "TERRESTRE")
        ubicacionService.conectar(ubicacionBurgerKing.nombre!!, ubicacionElCharro.nombre!!, "AEREO")
        ubicacionService.conectar(ubicacionSerBazares.nombre!!, ubicacionElCharro.nombre!!, "AEREO")

        assertEquals(ubicacionElPiave, vectorJuan.ubicacion)
        assertFalse(vectorPedro.estaInfectado)
        assertFalse(vectorCarlos.estaInfectado)
        assertFalse(vectorJohn.estaInfectado)
        assertTrue(vectorJuan.especiesPadecidas.contains(especieCovid))

        assertThrows<UbicacionNoAlcanzableException> { ubicacionService.moverPorCaminoMasCorto(vectorJuan.id!!, ubicacionElCharro.nombre!!) }

        vectorJuan = vectorService.recuperarVector(vectorJuan.id!!)
        vectorPedro = vectorService.recuperarVector(vectorPedro.id!!)
        vectorCarlos = vectorService.recuperarVector(vectorPedro.id!!)
        vectorJohn = vectorService.recuperarVector(vectorJohn.id!!)

        assertEquals(ubicacionElPiave, vectorJuan.ubicacion)
        assertFalse(vectorPedro.estaInfectado)
        assertFalse(vectorCarlos.estaInfectado)
        assertFalse(vectorJohn.estaInfectado)
    }

    @Test
    fun testCuandoSePideMoverPorCaminoMasCortoElVectorSeMuevePorLaRutaMasCorta(){
        ubicacionService.conectar(ubicacionElPiave.nombre!!, ubicacionBurgerKing.nombre!!, "TERRESTRE")
        ubicacionService.conectar(ubicacionElPiave.nombre!!, ubicacionSerBazares.nombre!!, "MARITIMO")
        ubicacionService.conectar(ubicacionBurgerKing.nombre!!, ubicacionElCharro.nombre!!, "TERRESTRE")
        ubicacionService.conectar(ubicacionSerBazares.nombre!!, ubicacionUNQ.nombre!!, "TERRESTRE")
        ubicacionService.conectar(ubicacionUNQ.nombre!!, ubicacionElCharro.nombre!!, "TERRESTRE")

        assertEquals(ubicacionElPiave, vectorJuan.ubicacion)
        assertFalse(vectorPedro.estaInfectado)
        assertFalse(vectorCarlos.estaInfectado)
        assertFalse(vectorJohn.estaInfectado)
        assertFalse(vectorGeorge.estaInfectado)
        assertTrue(vectorJuan.especiesPadecidas.contains(especieCovid))

        ubicacionService.moverPorCaminoMasCorto(vectorJuan.id!!, ubicacionElCharro.nombre!!)

        vectorJuan = vectorService.recuperarVector(vectorJuan.id!!)
        vectorPedro = vectorService.recuperarVector(vectorPedro.id!!)
        vectorCarlos = vectorService.recuperarVector(vectorCarlos.id!!)
        vectorJohn = vectorService.recuperarVector(vectorJohn.id!!)
        vectorGeorge = vectorService.recuperarVector(vectorGeorge.id!!)

        assertEquals(ubicacionElCharro, vectorJuan.ubicacion)
        assertTrue(vectorPedro.estaInfectado)
        assertTrue(vectorJohn.estaInfectado)
        assertFalse(vectorCarlos.estaInfectado)
        assertFalse(vectorGeorge.estaInfectado)
    }

    @Test
    fun testCuandoSePideMoverPorCaminoMasCortoElVectorSeMuevePorLaRutaMasLargaAlNoPoderAtravesarLaRutaCorta(){
        ubicacionService.conectar(ubicacionElPiave.nombre!!, ubicacionBurgerKing.nombre!!, "TERRESTRE")
        ubicacionService.conectar(ubicacionElPiave.nombre!!, ubicacionSerBazares.nombre!!, "MARITIMO")
        ubicacionService.conectar(ubicacionBurgerKing.nombre!!, ubicacionElCharro.nombre!!, "AEREO")
        ubicacionService.conectar(ubicacionSerBazares.nombre!!, ubicacionUNQ.nombre!!, "TERRESTRE")
        ubicacionService.conectar(ubicacionUNQ.nombre!!, ubicacionElCharro.nombre!!, "TERRESTRE")

        assertEquals(ubicacionElPiave, vectorJuan.ubicacion)
        assertFalse(vectorPedro.estaInfectado)
        assertFalse(vectorCarlos.estaInfectado)
        assertFalse(vectorJohn.estaInfectado)
        assertFalse(vectorGeorge.estaInfectado)
        assertTrue(vectorJuan.especiesPadecidas.contains(especieCovid))

        ubicacionService.moverPorCaminoMasCorto(vectorJuan.id!!, ubicacionElCharro.nombre!!)

        vectorJuan = vectorService.recuperarVector(vectorJuan.id!!)
        vectorPedro = vectorService.recuperarVector(vectorPedro.id!!)
        vectorCarlos = vectorService.recuperarVector(vectorCarlos.id!!)
        vectorJohn = vectorService.recuperarVector(vectorJohn.id!!)
        vectorGeorge = vectorService.recuperarVector(vectorGeorge.id!!)

        assertEquals(ubicacionElCharro, vectorJuan.ubicacion)
        assertFalse(vectorPedro.estaInfectado)
        assertTrue(vectorJohn.estaInfectado)
        assertTrue(vectorCarlos.estaInfectado)
        assertTrue(vectorGeorge.estaInfectado)
    }

    @Test
    fun testCuandoSePideMoverPorCaminoMasCortoElVectorDeTipoAnimalPuedeAtravesarLaRutaMasCorta(){

        ubicacionService.conectar(ubicacionElPiave.nombre!!, ubicacionBurgerKing.nombre!!, "TERRESTRE")
        ubicacionService.conectar(ubicacionElPiave.nombre!!, ubicacionSerBazares.nombre!!, "MARITIMO")
        ubicacionService.conectar(ubicacionBurgerKing.nombre!!, ubicacionElCharro.nombre!!, "AEREO")
        ubicacionService.conectar(ubicacionSerBazares.nombre!!, ubicacionUNQ.nombre!!, "TERRESTRE")
        ubicacionService.conectar(ubicacionUNQ.nombre!!, ubicacionElCharro.nombre!!, "TERRESTRE")

        assertEquals(ubicacionElPiave, vectorPerro.ubicacion)
        assertFalse(vectorPedro.estaInfectado)
        assertFalse(vectorCarlos.estaInfectado)
        assertFalse(vectorJohn.estaInfectado)
        assertFalse(vectorGeorge.estaInfectado)
        assertTrue(vectorPerro.especiesPadecidas.contains(especieCovid))

        ubicacionService.moverPorCaminoMasCorto(vectorPerro.id!!, ubicacionElCharro.nombre!!)

        vectorPerro     = vectorService.recuperarVector(vectorPerro.id!!)
        vectorPedro  = vectorService.recuperarVector(vectorPedro.id!!)
        vectorCarlos = vectorService.recuperarVector(vectorCarlos.id!!)
        vectorJohn = vectorService.recuperarVector(vectorJohn.id!!)
        vectorGeorge = vectorService.recuperarVector(vectorGeorge.id!!)

        assertEquals(ubicacionElCharro, vectorPerro.ubicacion)
        assertTrue(vectorPedro.estaInfectado)
        assertTrue(vectorJohn.estaInfectado)
        assertFalse(vectorCarlos.estaInfectado)
        assertFalse(vectorGeorge.estaInfectado)
    }

    @Test
    fun testCuandoSePideMoverPorCaminoMasCortoElVectorDeTipoInsectoPuedeAtravesarLaRutaMasCorta(){

        ubicacionService.conectar(ubicacionElPiave.nombre!!, ubicacionBurgerKing.nombre!!, "TERRESTRE")
        ubicacionService.conectar(ubicacionElPiave.nombre!!, ubicacionSerBazares.nombre!!, "MARITIMO")
        ubicacionService.conectar(ubicacionBurgerKing.nombre!!, ubicacionElCharro.nombre!!, "AEREO")
        ubicacionService.conectar(ubicacionSerBazares.nombre!!, ubicacionUNQ.nombre!!, "TERRESTRE")
        ubicacionService.conectar(ubicacionUNQ.nombre!!, ubicacionElCharro.nombre!!, "TERRESTRE")

        assertEquals(ubicacionElPiave, vectorVinchuca.ubicacion)
        assertFalse(vectorPedro.estaInfectado)
        assertFalse(vectorCarlos.estaInfectado)
        assertFalse(vectorJohn.estaInfectado)
        assertFalse(vectorGeorge.estaInfectado)
        assertTrue(vectorVinchuca.especiesPadecidas.contains(especieCovid))

        ubicacionService.moverPorCaminoMasCorto(vectorVinchuca.id!!, ubicacionElCharro.nombre!!)

        vectorVinchuca     = vectorService.recuperarVector(vectorVinchuca.id!!)
        vectorPedro  = vectorService.recuperarVector(vectorPedro.id!!)
        vectorCarlos = vectorService.recuperarVector(vectorCarlos.id!!)
        vectorJohn = vectorService.recuperarVector(vectorJohn.id!!)
        vectorGeorge = vectorService.recuperarVector(vectorGeorge.id!!)

        assertEquals(ubicacionElCharro, vectorVinchuca.ubicacion)
        assertTrue(vectorPedro.estaInfectado)
        assertTrue(vectorJohn.estaInfectado)
        assertFalse(vectorCarlos.estaInfectado)
        assertFalse(vectorGeorge.estaInfectado)
    }

    @Test
    fun testCuandoSePideMoverPorCaminoMasCortoYLosCaminosVanEnAmbosSentidosElVectorSeMueveDeIgualManeraPorElCaminoMasCorto(){
        ubicacionService.conectar(ubicacionElPiave.nombre!!, ubicacionBurgerKing.nombre!!, "TERRESTRE")
        ubicacionService.conectar(ubicacionBurgerKing.nombre!!, ubicacionElPiave.nombre!!, "TERRESTRE")

        ubicacionService.conectar(ubicacionBurgerKing.nombre!!, ubicacionElCharro.nombre!!, "TERRESTRE")
        ubicacionService.conectar(ubicacionElCharro.nombre!!, ubicacionBurgerKing.nombre!!, "TERRESTRE")

        ubicacionService.conectar(ubicacionElPiave.nombre!!, ubicacionSerBazares.nombre!!, "MARITIMO")
        ubicacionService.conectar(ubicacionSerBazares.nombre!!, ubicacionElPiave.nombre!!, "MARITIMO")

        ubicacionService.conectar(ubicacionSerBazares.nombre!!, ubicacionUNQ.nombre!!, "TERRESTRE")
        ubicacionService.conectar(ubicacionUNQ.nombre!!, ubicacionSerBazares.nombre!!, "TERRESTRE")

        ubicacionService.conectar(ubicacionUNQ.nombre!!, ubicacionElCharro.nombre!!, "TERRESTRE")
        ubicacionService.conectar(ubicacionElCharro.nombre!!, ubicacionUNQ.nombre!!, "TERRESTRE")

        assertEquals(ubicacionElPiave, vectorJuan.ubicacion)
        assertFalse(vectorPedro.estaInfectado)
        assertFalse(vectorCarlos.estaInfectado)
        assertFalse(vectorJohn.estaInfectado)
        assertFalse(vectorGeorge.estaInfectado)
        assertTrue(vectorJuan.especiesPadecidas.contains(especieCovid))

        ubicacionService.moverPorCaminoMasCorto(vectorJuan.id!!, ubicacionElCharro.nombre!!)

        vectorJuan = vectorService.recuperarVector(vectorJuan.id!!)
        vectorPedro = vectorService.recuperarVector(vectorPedro.id!!)
        vectorCarlos = vectorService.recuperarVector(vectorCarlos.id!!)
        vectorJohn = vectorService.recuperarVector(vectorJohn.id!!)
        vectorGeorge = vectorService.recuperarVector(vectorGeorge.id!!)

        assertEquals(ubicacionElCharro, vectorJuan.ubicacion)
        assertTrue(vectorPedro.estaInfectado)
        assertTrue(vectorJohn.estaInfectado)
        assertFalse(vectorCarlos.estaInfectado)
        assertFalse(vectorGeorge.estaInfectado)
    }

    @Test
    fun testCuandoSePideMoverPorCaminoMasCortoYHayDosIgualDeCortosSeEligeLaUltimaPersistidaParaguayBrasil(){
        ubicacionService.conectar(ubicacionElPiave.nombre!!, ubicacionBurgerKing.nombre!!, "TERRESTRE")
        ubicacionService.conectar(ubicacionElPiave.nombre!!, ubicacionSerBazares.nombre!!, "TERRESTRE")
        ubicacionService.conectar(ubicacionBurgerKing.nombre!!, ubicacionElCharro.nombre!!, "TERRESTRE")
        ubicacionService.conectar(ubicacionSerBazares.nombre!!, ubicacionElCharro.nombre!!, "TERRESTRE")

        assertEquals(ubicacionElPiave, vectorJuan.ubicacion)
        assertFalse(vectorPedro.estaInfectado)
        assertFalse(vectorCarlos.estaInfectado)
        assertFalse(vectorJohn.estaInfectado)
        assertTrue(vectorJuan.especiesPadecidas.contains(especieCovid))

        ubicacionService.moverPorCaminoMasCorto(vectorJuan.id!!, ubicacionElCharro.nombre!!)

        vectorJuan = vectorService.recuperarVector(vectorJuan.id!!)
        vectorPedro = vectorService.recuperarVector(vectorPedro.id!!)
        vectorCarlos = vectorService.recuperarVector(vectorCarlos.id!!)
        vectorJohn = vectorService.recuperarVector(vectorJohn.id!!)

        assertEquals(ubicacionElCharro, vectorJuan.ubicacion)
        assertFalse(vectorPedro.estaInfectado)
        assertTrue(vectorJohn.estaInfectado)
        assertTrue(vectorCarlos.estaInfectado)
    }

    @Test
    fun testCuandoSePideMoverPorCaminoMasCortoYHayDosIgualDeCortosSeEligeLaUltimaPersistidaUruguayBrasil(){
        ubicacionService.conectar(ubicacionElPiave.nombre!!, ubicacionSerBazares.nombre!!, "TERRESTRE")
        ubicacionService.conectar(ubicacionElPiave.nombre!!, ubicacionBurgerKing.nombre!!, "TERRESTRE")
        ubicacionService.conectar(ubicacionSerBazares.nombre!!, ubicacionElCharro.nombre!!, "TERRESTRE")
        ubicacionService.conectar(ubicacionBurgerKing.nombre!!, ubicacionElCharro.nombre!!, "TERRESTRE")

        assertEquals(ubicacionElPiave, vectorJuan.ubicacion)
        assertFalse(vectorPedro.estaInfectado)
        assertFalse(vectorCarlos.estaInfectado)
        assertFalse(vectorJohn.estaInfectado)
        assertTrue(vectorJuan.especiesPadecidas.contains(especieCovid))

        ubicacionService.moverPorCaminoMasCorto(vectorJuan.id!!, ubicacionElCharro.nombre!!)

        vectorJuan = vectorService.recuperarVector(vectorJuan.id!!)
        vectorPedro = vectorService.recuperarVector(vectorPedro.id!!)
        vectorCarlos = vectorService.recuperarVector(vectorCarlos.id!!)
        vectorJohn = vectorService.recuperarVector(vectorJohn.id!!)

        assertEquals(ubicacionElCharro, vectorJuan.ubicacion)
        assertTrue(vectorPedro.estaInfectado)
        assertTrue(vectorJohn.estaInfectado)
        assertFalse(vectorCarlos.estaInfectado)
    }




    @AfterEach
    fun tearDown(){
        dataService.cleanAll()
        ubicacionNeo4jDAO.detachDeleteAll()
        ubicacionMongoDBDAO.deleteAll()
        distritoDAO.deleteAll()
    }

}
