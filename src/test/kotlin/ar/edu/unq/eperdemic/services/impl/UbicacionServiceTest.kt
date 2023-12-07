package ar.edu.unq.eperdemic.services.impl

import ar.edu.unq.eperdemic.dao.helper.IndexCreatorMongoDB
import ar.edu.unq.eperdemic.dao.helper.service.DataService
import ar.edu.unq.eperdemic.modelo.*
import ar.edu.unq.eperdemic.modelo.vector.Vector
import ar.edu.unq.eperdemic.modelo.vector.VectorAnimal
import ar.edu.unq.eperdemic.modelo.vector.VectorHumano
import ar.edu.unq.eperdemic.modelo.vector.VectorInsecto
import ar.edu.unq.eperdemic.modelo.exception.CaminoInvalidoException
import ar.edu.unq.eperdemic.modelo.mutacion.ElectroBranqueas
import ar.edu.unq.eperdemic.modelo.mutacion.PropulsionMotora
import ar.edu.unq.eperdemic.modelo.mutacion.Teletransportacion
import ar.edu.unq.eperdemic.persistencia.dao.DistritoDAO
import ar.edu.unq.eperdemic.persistencia.dao.UbicacionDAO
import ar.edu.unq.eperdemic.persistencia.dao.UbicacionMongoDBDAO
import ar.edu.unq.eperdemic.persistencia.dao.UbicacionNeo4jDAO
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
class UbicacionServiceTest {
    @Autowired
    private lateinit var dataService: DataService
    @Autowired
    private lateinit var indexCreatorMongoDB: IndexCreatorMongoDB
    @Autowired
    private lateinit var ubicacionService : UbicacionService
    @Autowired
    private lateinit var distritoServiceImpl: DistritoServiceImpl
    @Autowired
    private lateinit var distritoDAO: DistritoDAO
    @Autowired
    private lateinit var vectorService: VectorService
    @Autowired
    private lateinit var patogenoService: PatogenoService
    @Autowired
    private lateinit var ubicacionNeo4jDAO: UbicacionNeo4jDAO
    @Autowired
    private lateinit var ubicacionMongoDBDAO: UbicacionMongoDBDAO
    @Autowired
    private lateinit var ubicacionDAO: UbicacionDAO
    @MockBean
    lateinit var rng : RNG

    private lateinit var ubicacionUNQ: Ubicacion
    private lateinit var ubicacionBarCroata: Ubicacion

    private lateinit var ubicacionLaBoca: Ubicacion
    private lateinit var ubicacionCordillera: Ubicacion
    private lateinit var ubicacionMachuPicchu: Ubicacion

    private lateinit var distritoBernal: Distrito

    private lateinit var coordenadas: List<GeoJsonPoint>

    private lateinit var forma: GeoJsonPolygon

    private lateinit var vectorMartin: Vector
    private lateinit var vectorTomas: Vector
    private lateinit var vectorBullo: Vector
    private lateinit var vectorOso: Vector
    private lateinit var vectorPerro: Vector
    private lateinit var vectorEscarabajo: Vector
    private lateinit var vectorGaviota: Vector

    private lateinit var patogenoVirus: Patogeno

    private lateinit var punto: GeoJsonPoint
    private lateinit var punto2: GeoJsonPoint

    @BeforeEach
    fun setUp() {
        indexCreatorMongoDB.crearIndiceGeoespacialUbicacionMongoDB()

        coordenadas = listOf(
            GeoJsonPoint(0.0, 0.0),
            GeoJsonPoint(30.0, 60.0),
            GeoJsonPoint(60.0, 10.0),
            GeoJsonPoint(0.0, 0.0),
        )

        forma = GeoJsonPolygon(coordenadas)

        distritoBernal = Distrito("Bernal", forma)
        distritoServiceImpl.crearDistrito(distritoBernal)

        ubicacionBarCroata = Ubicacion("Bar Croata")
        ubicacionUNQ = Ubicacion("UNQ")
        ubicacionLaBoca = Ubicacion("La Boca")
        ubicacionCordillera = Ubicacion("Cordillera de Los Andes")
        ubicacionMachuPicchu = Ubicacion("Machu Picchu")

        vectorMartin = VectorHumano("Martin", ubicacionBarCroata)
        vectorTomas = VectorHumano("Tomas",ubicacionBarCroata)
        vectorBullo = VectorAnimal("Bullo" , ubicacionBarCroata)
        vectorOso = VectorAnimal("Oso" , ubicacionBarCroata)
        vectorPerro = VectorAnimal("Perro" , ubicacionBarCroata)
        vectorEscarabajo = VectorInsecto("Escarabajo" , ubicacionBarCroata)

        patogenoVirus = Patogeno("Virus", 70, 40, 10, 50, 30)

        punto = GeoJsonPoint(0.0,0.0)
        punto2 = GeoJsonPoint(0.01,0.01)
    }

    @Test
    fun testalCrearYRecuperarUnaUbicacionSeObtienenObjetosSimilares() {
        ubicacionBarCroata = ubicacionService.crearUbicacion(ubicacionBarCroata,punto)
        val recuperarUbicacion = ubicacionService.recuperarUbicacion(ubicacionBarCroata.id!!)

        assertEquals(ubicacionBarCroata.nombre, recuperarUbicacion.nombre)
    }

    @Test
    fun testalCrearYRecuperarUnaUbicacionDesdeNeo4jSeObtienenObjetosSimilares() {
        ubicacionBarCroata = ubicacionService.crearUbicacion(ubicacionBarCroata,punto)
        val recuperarUbicacion = ubicacionService.recuperarUbicacionDeNeo4j(ubicacionBarCroata.nombre!!)

        assertEquals(ubicacionBarCroata.nombre, recuperarUbicacion.nombre)
    }

    @Test
    fun testCuandoCreoUnaUbicacionMongoQueEstaDentroDeUnDistritoSeCreaYseAgregaAlaListaDeUbicacionesDelDistrito(){
        ubicacionService.crearUbicacion(ubicacionMachuPicchu, GeoJsonPoint(2.0,2.0))
        val ubicacionMongoDB = ubicacionMongoDBDAO.findByNombre(ubicacionMachuPicchu.nombre!!)
        val distrito = distritoDAO.findByNombre(distritoBernal.nombre!!)
        assertTrue(distrito!!.ubicaciones.contains(ubicacionMongoDB))
    }


    @Test
    fun testAlCrearUnaUbicacionYaCreadaSeLanzaLaExpecionUbicacionYaExistente() {
        ubicacionService.crearUbicacion(ubicacionBarCroata,punto)
        assertThrows<UbicacionConNombreYaExisteException> { ubicacionService.crearUbicacion(ubicacionBarCroata,punto) }
    }

    @Test
    fun testRecuperaUnaUbicacionConIdNoExistenteSeLanzaExpecion() {
        assertThrows<UbicacionNoExistenteException> { ubicacionService.recuperarUbicacion(-1) }
    }

    @Test
    fun testAlCrearUnaUbicacionMongoQueNoEstaDentroDeNingunDistritoLanzaException(){
        assertThrows<ElPuntoNoEntraEnNingunDistritoException> { ubicacionService.crearUbicacion(ubicacionBarCroata,
            GeoJsonPoint(90.90,50.50)
        ) }
    }

    @Test
    fun testAlActualizarUnaUbicacionYRecuperarSeObtieneElObjetoEsperado() {
        ubicacionLaBoca = ubicacionService.crearUbicacion(ubicacionLaBoca,punto2)
        ubicacionLaBoca.nombre = "Obelisco"
        ubicacionService.actualizarUbicacion(ubicacionLaBoca, punto)
        val otraUbicacion = ubicacionService.recuperarUbicacion(ubicacionLaBoca.id!!)

        assertEquals(ubicacionLaBoca.id, otraUbicacion.id)
        assertEquals(ubicacionLaBoca.nombre, otraUbicacion.nombre)
    }
    @Test
    fun testAlActualizarElNombreDeUnaUbicacionSeActualizaEnLasTresBasesDeDatos() {
        ubicacionLaBoca = ubicacionService.crearUbicacion(ubicacionLaBoca,punto2)
        assertEquals("La Boca" ,ubicacionDAO.findByNombre(ubicacionLaBoca.nombre)!!.nombre)
        assertEquals("La Boca" ,ubicacionMongoDBDAO.findByNombre(ubicacionLaBoca.nombre)!!.nombre)
        assertEquals("La Boca" ,ubicacionNeo4jDAO.findByNombre(ubicacionLaBoca.nombre)!!.nombre)

        ubicacionLaBoca.nombre = "Obelisco"
        ubicacionService.actualizarUbicacion(ubicacionLaBoca, punto2)

        assertEquals("Obelisco" ,ubicacionDAO.findByNombre(ubicacionLaBoca.nombre)!!.nombre)
        assertEquals("Obelisco" ,ubicacionMongoDBDAO.findByNombre(ubicacionLaBoca.nombre)!!.nombre)
        assertEquals("Obelisco" ,ubicacionNeo4jDAO.findByNombre(ubicacionLaBoca.nombre)!!.nombre)
    }

    @Test
    fun testActualizarUbicacionQueNoExisteLanzaExcepcionIdNoExiste() {
        assertThrows<UbicacionNoExistenteException> { ubicacionService.actualizarUbicacion(ubicacionBarCroata, null) }
    }

    @Test
    fun testActualizarUnaUbicacionQueYaExisteLanzaExceptionUbicacionYaExiste() {
        ubicacionService.crearUbicacion(ubicacionBarCroata, punto)
        val ubicacionParaActualizar = ubicacionService.crearUbicacion(ubicacionLaBoca,punto2)
        ubicacionParaActualizar.nombre = "Bar Croata"

        assertThrows<UbicacionConNombreYaExisteException> { ubicacionService.actualizarUbicacion(ubicacionParaActualizar, null) }
    }

    @Test
    fun testActualizarUbicacionConUnIdNuloSeLanzaExcepcionIdNoExiste() {
        val ubicacion2 = Ubicacion()
        ubicacion2.id = null
        assertThrows<UbicacionNoExistenteException> {
            ubicacionService.actualizarUbicacion(ubicacion2, null)
        }
    }

    @Test
    fun testRecuperarTodasLasUbicacionesNoHayUbicacionesDevueleArrayVacio(){
        assertTrue(ubicacionService.recuperarTodasLasUbicaciones().isEmpty())
    }
    @Test
    fun testRecuperarTodasLasUbicacionesYDevuelveUnaListaConLasCreadasHastaElMomento(){
        ubicacionService.crearUbicacion(ubicacionBarCroata,punto)
        ubicacionService.crearUbicacion(ubicacionLaBoca,punto2)
        val listaDeUbicaciones = ubicacionService.recuperarTodasLasUbicaciones()

        assertEquals(listaDeUbicaciones.size,2)
        assertTrue(listaDeUbicaciones.contains(ubicacionBarCroata))
        assertTrue(listaDeUbicaciones.contains(ubicacionLaBoca))
    }

    @Test
    fun testAlRecuperarUnaUbicacionPorNombreSeObtieneLaUbicacionEsperada(){
        ubicacionService.crearUbicacion(ubicacionBarCroata,punto)
        val ubicacionRecuperada = ubicacionService.recuperarUbicacionConNombre(ubicacionBarCroata.nombre!!)

        assertEquals(ubicacionRecuperada, ubicacionBarCroata)
    }

    @Test
    fun testCuandoQuieroRecuperarUnaUbicacionPorNombrePeroNoExisteSeLanzaUbicacionConNombreNoExistenteException() {
        ubicacionService.crearUbicacion(ubicacionBarCroata,punto)

        assertThrows<UbicacionConNombreNoExistenteException> { ubicacionService.recuperarUbicacionConNombre("Cualquier cosa") }
    }

    @Test
    fun testCuandoQuieroMoverUnVectorAOtraUbicacion(){
        ubicacionBarCroata = ubicacionService.crearUbicacion(ubicacionBarCroata,punto)
        ubicacionLaBoca = ubicacionService.crearUbicacion(ubicacionLaBoca,punto2)
        vectorMartin = vectorService.crearVector(vectorMartin)
        assertEquals(ubicacionBarCroata, vectorMartin.ubicacion)

        ubicacionService.conectar(ubicacionBarCroata.nombre!!, ubicacionLaBoca.nombre!!, "TERRESTRE")
        ubicacionService.mover(vectorMartin.id!!,ubicacionLaBoca.id!!)
        vectorMartin = vectorService.recuperarVector(vectorMartin.id!!)

        assertEquals(ubicacionLaBoca, vectorMartin.ubicacion)
    }

    @Test
    fun testCuandoUnVectorSeMueveALaMismaUbicacionEnLaQueEstaSeLanzaVectorYaEstaEnLaUbicacionException() {
        ubicacionBarCroata = ubicacionService.crearUbicacion(ubicacionBarCroata,punto)
        vectorMartin   = vectorService.crearVector(vectorMartin)
        vectorEscarabajo  = vectorService.crearVector(vectorEscarabajo)
        assertEquals(ubicacionBarCroata, vectorEscarabajo.ubicacion)
        assertEquals(ubicacionBarCroata, vectorMartin.ubicacion)

        assertThrows<VectorYaEstaEnLaUbicacionException> { ubicacionService.mover(vectorMartin.id!!, ubicacionBarCroata.id!!) }
        assertFalse(vectorEscarabajo.estaInfectado)
    }

    @Test
    fun testCuandoQuieroMoverUnVectorAOtraUbicacionYElIdDelVectorNoExisteLanzaExcepcion(){
        ubicacionBarCroata = ubicacionService.crearUbicacion(ubicacionBarCroata,punto)
        assertThrows<VectorIDNoExisteException> {
            ubicacionService.mover(-1,ubicacionBarCroata.id!!)
        }
    }
    @Test
    fun testCuandoQuieroMoverUnVectorAOtraUbicacionYElIdDeLaUbicacionNoExisteLanzaExcepcion(){
        vectorMartin = vectorService.crearVector(vectorMartin)

        val vectorId = vectorMartin.id!!

        assertThrows<UbicacionNoExistenteException> {
            ubicacionService.mover(vectorId,-1)
        }
    }

    @Test
    fun testCuandoQuieroMoverUnVectorYNoEstaInfectadoLosVectoresDeLaUbicacionNoSeInfectan(){
        ubicacionBarCroata = ubicacionService.crearUbicacion(ubicacionBarCroata,punto)
        ubicacionLaBoca = ubicacionService.crearUbicacion(ubicacionLaBoca,punto2)
        vectorBullo.ubicacion = ubicacionLaBoca
        vectorService.crearVector(vectorBullo)
        vectorService.crearVector(vectorMartin)
        vectorService.crearVector(vectorTomas)

        assertFalse(vectorBullo.estaInfectado)
        assertEquals(ubicacionLaBoca, vectorBullo.ubicacion)

        ubicacionService.conectar(ubicacionLaBoca.nombre!!, ubicacionBarCroata.nombre!!, "TERRESTRE")
        ubicacionService.mover(vectorBullo.id!!, ubicacionBarCroata.id!!)
        vectorBullo = vectorService.recuperarVector(vectorBullo.id!!)
        vectorMartin = vectorService.recuperarVector(vectorBullo.id!!)
        vectorTomas = vectorService.recuperarVector(vectorBullo.id!!)

        assertFalse(vectorMartin.estaInfectado)
        assertFalse(vectorTomas.estaInfectado)
        assertEquals(ubicacionBarCroata, vectorBullo.ubicacion)
    }

    @Test
    fun testCuandoQuieroMoverUnVectorYEstaInfectadoLosVectoresDeLaUbicacionSeInfectan(){
        patogenoVirus = patogenoService.crearPatogeno(patogenoVirus)
        ubicacionBarCroata = ubicacionService.crearUbicacion(ubicacionBarCroata,punto)
        ubicacionLaBoca = ubicacionService.crearUbicacion(ubicacionLaBoca,punto2)
        vectorBullo.ubicacion = ubicacionLaBoca
        vectorService.crearVector(vectorBullo)
        vectorService.crearVector(vectorMartin)
        vectorService.crearVector(vectorTomas)

        assertFalse(vectorBullo.estaInfectado)
        assertEquals(ubicacionLaBoca, vectorBullo.ubicacion)

        val especie1 = patogenoService.agregarEspecie(patogenoVirus.id!!, "COVID", ubicacionLaBoca.id!!)
        `when`(rng.getRandomNumber(1, 10)).thenReturn(10)
        `when`(rng.getRandomNumber(1, 100)).thenReturn(1)

        ubicacionService.conectar(ubicacionLaBoca.nombre!!, ubicacionBarCroata.nombre!!, "TERRESTRE")
        ubicacionService.mover(vectorBullo.id!!, ubicacionBarCroata.id!!)
        vectorBullo = vectorService.recuperarVector(vectorBullo.id!!)
        vectorMartin = vectorService.recuperarVector(vectorBullo.id!!)
        vectorTomas = vectorService.recuperarVector(vectorBullo.id!!)

        assertTrue(vectorMartin.especiesPadecidas.contains(especie1))
        assertTrue(vectorTomas.especiesPadecidas.contains(especie1))
        assertEquals(1, vectorTomas.especiesPadecidas.size)
        assertEquals(1, vectorTomas.especiesPadecidas.size)
        assertEquals(ubicacionBarCroata, vectorBullo.ubicacion)
    }

    @Test
    fun testCuandoQuieroExpandirYElIdDeLaUbicacionNoExisteLanzaExcepcion(){
        assertThrows<UbicacionNoExistenteException> {
            ubicacionService.expandir(-1)
        }
    }

    @Test
    fun testCuandoQuieroExpandirYTodosLosVectoresDeLaUbicacionDadaEstanSanosNoPasaNada(){
        vectorService.crearVector(vectorMartin)
        vectorService.crearVector(vectorTomas)
        ubicacionService.expandir(ubicacionBarCroata.id!!)
        assertFalse(vectorMartin.estaInfectado)
        assertFalse(vectorTomas.estaInfectado)
    }

    @Test
    fun testCuandoSeExpandeYSeContagianLosVectoresQueSePuedenContagiarEnLaUbicacionDada() {
        ubicacionBarCroata = ubicacionService.crearUbicacion(ubicacionBarCroata,punto)
        patogenoVirus = patogenoService.crearPatogeno(patogenoVirus)
        vectorService.crearVector(vectorMartin)
        vectorService.crearVector(vectorTomas)
        vectorService.crearVector(vectorBullo)
        vectorService.crearVector(vectorEscarabajo)

        val especie1 = patogenoService.agregarEspecie(patogenoVirus.id!!, "COVID", ubicacionBarCroata.id!!)
        vectorService.infectarVector(vectorMartin.id!!, especie1.id!!)
        `when`(rng.getRandomNumber(0, 1)).thenReturn(0)
        `when`(rng.getRandomNumber(1, 10)).thenReturn(10)
        `when`(rng.determinarProbabilidad(anyInt())).thenReturn (true)
        ubicacionService.expandir(ubicacionBarCroata.id!!)
        vectorMartin = vectorService.recuperarVector(vectorMartin.id!!)
        vectorTomas = vectorService.recuperarVector(vectorTomas.id!!)
        vectorBullo = vectorService.recuperarVector(vectorBullo.id!!)
        vectorEscarabajo = vectorService.recuperarVector(vectorEscarabajo.id!!)


        assertTrue(vectorMartin.estaInfectado)
        assertTrue(vectorTomas.estaInfectado)
        assertFalse(vectorBullo.estaInfectado)
        assertTrue(vectorEscarabajo.estaInfectado)

    }

    @Test
    fun testSeExpandeYNoSeContagiaNingunVectorPorIncompatibilidad() {
        ubicacionBarCroata = ubicacionService.crearUbicacion(ubicacionBarCroata,punto)
        patogenoVirus = patogenoService.crearPatogeno(patogenoVirus)
        vectorService.crearVector(vectorMartin)
        vectorService.crearVector(vectorOso)
        vectorService.crearVector(vectorBullo)
        vectorService.crearVector(vectorPerro)

        val especie1 = patogenoService.agregarEspecie(patogenoVirus.id!!, "COVID", ubicacionBarCroata.id!!)
        vectorService.infectarVector(vectorMartin.id!!, especie1.id!!)
        `when`(rng.getRandomNumber(0, 3)).thenReturn(0)
        `when`(rng.getRandomNumber(1, 10)).thenReturn(10)
        `when`(rng.getRandomNumber(1, 100)).thenReturn(1)

        ubicacionService.expandir(ubicacionBarCroata.id!!)
        vectorMartin = vectorService.recuperarVector(vectorMartin.id!!)
        vectorOso = vectorService.recuperarVector(vectorOso.id!!)
        vectorBullo = vectorService.recuperarVector(vectorBullo.id!!)
        vectorPerro = vectorService.recuperarVector(vectorPerro.id!!)

        assertTrue(vectorMartin.estaInfectado)
        assertFalse(vectorOso.estaInfectado)
        assertFalse(vectorBullo.estaInfectado)
        assertFalse(vectorPerro.estaInfectado)
    }

    @Test
    fun testSeExpandeYNoHayNingunVectorEnLaUbicacionDadaNoPasaNada(){
        ubicacionBarCroata = ubicacionService.crearUbicacion(ubicacionBarCroata,punto)
        ubicacionLaBoca = ubicacionService.crearUbicacion(ubicacionLaBoca,punto2)
        patogenoVirus = patogenoService.crearPatogeno(patogenoVirus)
        vectorService.crearVector(vectorMartin)
        vectorService.crearVector(vectorTomas)
        vectorService.crearVector(vectorBullo)
        vectorService.crearVector(vectorEscarabajo)

        val especie1 = patogenoService.agregarEspecie(patogenoVirus.id!!, "COVID", ubicacionBarCroata.id!!)
        vectorService.infectarVector(vectorMartin.id!!, especie1.id!!)
        `when`(rng.getRandomNumber(0, 1)).thenReturn(0)
        `when`(rng.getRandomNumber(1, 10)).thenReturn(10)
        `when`(rng.getRandomNumber(1, 100)).thenReturn(1)
        ubicacionService.expandir(ubicacionLaBoca.id!!)
        vectorMartin = vectorService.recuperarVector(vectorMartin.id!!)
        vectorTomas = vectorService.recuperarVector(vectorTomas.id!!)
        vectorBullo = vectorService.recuperarVector(vectorBullo.id!!)
        vectorEscarabajo = vectorService.recuperarVector(vectorEscarabajo.id!!)

        assertTrue(vectorMartin.estaInfectado)
        assertFalse(vectorEscarabajo.estaInfectado)
        assertFalse(vectorBullo.estaInfectado)
        assertFalse(vectorTomas.estaInfectado)
    }

    @Test
    fun testCuandoQuieroConectarDosLugaresYUnLugarNoEstaPersistidoLanzaExceptionUbicacionNoExiste() {
        ubicacionService.crearUbicacion(ubicacionBarCroata,punto)
        assertThrows<UbicacionNoExistenteException> { ubicacionService.conectar(ubicacionBarCroata.nombre!!, ubicacionLaBoca.nombre!!, "Maritinmo")}
    }

    @Test
    fun testCuandoQuieroConectarDosLugaresConUnTipoDeCaminoSeConectan() {
        ubicacionService.crearUbicacion(ubicacionBarCroata,punto)
        ubicacionService.crearUbicacion(ubicacionLaBoca,punto2)

        ubicacionService.conectar(ubicacionBarCroata.nombre!!, ubicacionLaBoca.nombre!!, "MARITIMO")
        ubicacionService.conectar(ubicacionBarCroata.nombre!!, ubicacionLaBoca.nombre!!,"TERRESTRE")
        ubicacionService.conectar(ubicacionLaBoca.nombre!!, ubicacionBarCroata.nombre!!,"TERRESTRE")
        ubicacionService.conectar(ubicacionLaBoca.nombre!!, ubicacionBarCroata.nombre!!,"AEREO")

        val ubicacionNeo4J = ubicacionService.recuperarUbicacionDeNeo4j(ubicacionBarCroata.nombre!!)
        val ubicacionNeo4J2 = ubicacionService.recuperarUbicacionDeNeo4j(ubicacionLaBoca.nombre!!)

        assertEquals(0, ubicacionNeo4J.rutasAereas.size)
        assertEquals(1, ubicacionNeo4J.rutasMaritimas.size)
        assertEquals(1, ubicacionNeo4J.rutasTerrestres.size)
        assertTrue(ubicacionNeo4J.rutasMaritimas.any { it.nombre ==  ubicacionNeo4J2.nombre})
        assertTrue(ubicacionNeo4J.rutasTerrestres.any { it.nombre ==  ubicacionNeo4J2.nombre})

        assertEquals(0, ubicacionNeo4J2.rutasMaritimas.size)
        assertEquals(1, ubicacionNeo4J2.rutasAereas.size)
        assertEquals(1, ubicacionNeo4J2.rutasTerrestres.size)
        assertTrue(ubicacionNeo4J2.rutasAereas.any { it.nombre ==  ubicacionNeo4J.nombre})
        assertTrue(ubicacionNeo4J2.rutasTerrestres.any { it.nombre ==  ubicacionNeo4J.nombre})
    }

    @Test
    fun testCuandoQueremosConectarUnaUbicacionQueNoExisteLanzaUbicacionNoExisteException(){
        ubicacionService.crearUbicacion(ubicacionBarCroata,punto)
        assertThrows<UbicacionNoExistenteException> { ubicacionService.conectar(ubicacionBarCroata.nombre!!, ubicacionLaBoca.nombre!!, "TERRESTRE") }

    }
    @Test
    fun testCuandoQueresConectarConUnTipoDeCaminoIncorrectoLanzaCaminoIncorrectoException(){
        ubicacionService.crearUbicacion(ubicacionBarCroata,punto)
        ubicacionService.crearUbicacion(ubicacionLaBoca,punto2)
        assertThrows<CaminoInvalidoException> { ubicacionService.conectar(ubicacionBarCroata.nombre!!, ubicacionLaBoca.nombre!!,"SUBTERRANEO")}
    }

    @Test
    fun testCuandoSePidenLosConectadosDeUnaUbicacionQueEstaAisladaSeDevuelveLaListaVacia() {
        ubicacionBarCroata  = ubicacionService.crearUbicacion(ubicacionBarCroata,punto)

        assertTrue(ubicacionService.conectados(ubicacionBarCroata.nombre!!).isEmpty())
    }

    @Test
    fun testCuandoSePidenLosConectadosDeUnaUbicacionQueNoExisteSeDevuelveLaListaVacia(){
        assertTrue(ubicacionService.conectados("Malasia").isEmpty())
    }

    @Test
    fun testCuandoSePidenLosConectadosDeUnaUbicacionConUnUnicoCaminoConectadoSeDevuelveLaListaCorrectamente() {
        ubicacionBarCroata  = ubicacionService.crearUbicacion(ubicacionBarCroata,punto)
        ubicacionLaBoca = ubicacionService.crearUbicacion(ubicacionLaBoca,punto2)
        assertTrue(ubicacionService.conectados(ubicacionBarCroata.nombre!!).isEmpty())

        ubicacionService.conectar(ubicacionBarCroata.nombre!!, ubicacionLaBoca.nombre!!, "TERRESTRE")

        val conectados = ubicacionService.conectados(ubicacionBarCroata.nombre!!)

        assertEquals(1, conectados.size)
        assertTrue(conectados.contains(ubicacionLaBoca))
   }

    @Test
    fun testCuandoSePidenLosConectadosDeUnaUbicacionConVariosCaminosConectadosSeDevuelveLaListaCorrectamente() {
        ubicacionBarCroata  = ubicacionService.crearUbicacion(ubicacionBarCroata,punto)
        ubicacionLaBoca = ubicacionService.crearUbicacion(ubicacionLaBoca,punto2)
        ubicacionCordillera = ubicacionService.crearUbicacion(ubicacionCordillera, GeoJsonPoint(13.10,23.20))
        ubicacionMachuPicchu = ubicacionService.crearUbicacion(ubicacionMachuPicchu,GeoJsonPoint(14.10,24.20))
        assertTrue(ubicacionService.conectados(ubicacionBarCroata.nombre!!).isEmpty())

        ubicacionService.conectar(ubicacionBarCroata.nombre!!, ubicacionLaBoca.nombre!!, "TERRESTRE")
        ubicacionService.conectar(ubicacionBarCroata.nombre!!, ubicacionCordillera.nombre!!, "MARITIMO")
        ubicacionService.conectar(ubicacionBarCroata.nombre!!, ubicacionMachuPicchu.nombre!!, "AEREO")

        val conectados = ubicacionService.conectados(ubicacionBarCroata.nombre!!)

        assertEquals(3, conectados.size)
        assertTrue(conectados.contains(ubicacionLaBoca))
        assertTrue(conectados.contains(ubicacionCordillera))
        assertTrue(conectados.contains(ubicacionMachuPicchu))
    }

    @Test
    fun testCuandoSePidenLosConectadosDeUnaUbicacionQueTieneVariosCaminosConectadosALaMismaUbicacionSeDevuelveLaListaCorrectamente() {
        ubicacionBarCroata  = ubicacionService.crearUbicacion(ubicacionBarCroata,punto)
        ubicacionLaBoca = ubicacionService.crearUbicacion(ubicacionLaBoca,punto2)
        assertTrue(ubicacionService.conectados(ubicacionBarCroata.nombre!!).isEmpty())

        ubicacionService.conectar(ubicacionBarCroata.nombre!!, ubicacionLaBoca.nombre!!, "TERRESTRE")
        ubicacionService.conectar(ubicacionBarCroata.nombre!!, ubicacionLaBoca.nombre!!, "MARITIMO")
        ubicacionService.conectar(ubicacionBarCroata.nombre!!, ubicacionLaBoca.nombre!!, "AEREO")

        val conectados = ubicacionService.conectados(ubicacionBarCroata.nombre!!)

        assertEquals(1, conectados.size)
        assertTrue(conectados.contains(ubicacionLaBoca))
    }

    @Test
    fun testCuandoSeIntentaMoverUnVectorHumanoAUnaUbicacionNoProximaSeLanzaUbicacionMuyLejanaException() {
        ubicacionBarCroata  = ubicacionService.crearUbicacion(ubicacionBarCroata,punto)
        ubicacionLaBoca = ubicacionService.crearUbicacion(ubicacionLaBoca,punto2)
        vectorMartin = vectorService.crearVector(vectorMartin)

        assertThrows<UbicacionMuyLejanaException> { ubicacionService.mover(vectorMartin.id!!, ubicacionLaBoca.id!!) }
    }

    @Test
    fun testCuandoSeIntentaMoverUnVectorAnimalAUnaUbicacionNoProximaSeLanzaUbicacionMuyLejanaException() {
        ubicacionBarCroata  = ubicacionService.crearUbicacion(ubicacionBarCroata,punto)
        ubicacionLaBoca = ubicacionService.crearUbicacion(ubicacionLaBoca,punto2)
        vectorPerro = vectorService.crearVector(vectorPerro)

        assertThrows<UbicacionMuyLejanaException> { ubicacionService.mover(vectorPerro.id!!, ubicacionLaBoca.id!!) }
    }

    @Test
    fun testCuandoSeIntentaMoverUnVectorInsectoAUnaUbicacionNoProximaSeLanzaUbicacionMuyLejanaException() {
        ubicacionBarCroata  = ubicacionService.crearUbicacion(ubicacionBarCroata,punto)
        ubicacionLaBoca = ubicacionService.crearUbicacion(ubicacionLaBoca,punto2)
        vectorEscarabajo = vectorService.crearVector(vectorEscarabajo)

        assertThrows<UbicacionMuyLejanaException> { ubicacionService.mover(vectorEscarabajo.id!!, ubicacionLaBoca.id!!) }
    }

    @Test
    fun testCuandoSeIntentaMoverUnVectorHumanoAUnaUbicacionSinCaminoTerrestreOMaritimoSeLanzaUbicacionNoAlcanzableException() {
        ubicacionBarCroata  = ubicacionService.crearUbicacion(ubicacionBarCroata,punto)
        ubicacionLaBoca = ubicacionService.crearUbicacion(ubicacionLaBoca,punto2)
        vectorMartin = vectorService.crearVector(vectorMartin)

        ubicacionService.conectar(ubicacionBarCroata.nombre!!, ubicacionLaBoca.nombre!!, "AEREO")

        assertThrows<UbicacionNoAlcanzableException> { ubicacionService.mover(vectorMartin.id!!, ubicacionLaBoca.id!!) }
    }

    @Test
    fun testCuandoSeIntentaMoverUnVectorInsectoAUnaUbicacionSinCaminoTerrestreOAereoSeLanzaUbicacionNoAlcanzableException() {
        ubicacionBarCroata  = ubicacionService.crearUbicacion(ubicacionBarCroata,punto)
        ubicacionLaBoca = ubicacionService.crearUbicacion(ubicacionLaBoca,punto2)
        vectorEscarabajo = vectorService.crearVector(vectorEscarabajo)

        ubicacionService.conectar(ubicacionBarCroata.nombre!!, ubicacionLaBoca.nombre!!, "MARITIMO")

        assertThrows<UbicacionNoAlcanzableException> { ubicacionService.mover(vectorEscarabajo.id!!, ubicacionLaBoca.id!!) }
    }


    @Test
    fun testCuandoSeIntentaMoverUnVectorHumanoAUnaUbicacionConCaminoTerrestreEsteSeMueve() {
        ubicacionBarCroata  = ubicacionService.crearUbicacion(ubicacionBarCroata,punto)
        ubicacionLaBoca = ubicacionService.crearUbicacion(ubicacionLaBoca,punto2)
        vectorMartin = vectorService.crearVector(vectorMartin)

        ubicacionService.conectar(ubicacionBarCroata.nombre!!, ubicacionLaBoca.nombre!!, "TERRESTRE")
        ubicacionService.mover(vectorMartin.id!!, ubicacionLaBoca.id!!)
        vectorMartin = vectorService.recuperarVector(vectorMartin.id!!)

        assertEquals(vectorMartin.ubicacion!!.id, ubicacionLaBoca.id)
    }

    @Test
    fun testCuandoSeIntentaMoverUnVectorHumanoAUnaUbicacionConCaminoMaritimoEsteSeMueve() {
        ubicacionBarCroata  = ubicacionService.crearUbicacion(ubicacionBarCroata,punto)
        ubicacionLaBoca = ubicacionService.crearUbicacion(ubicacionLaBoca,punto2)
        vectorMartin = vectorService.crearVector(vectorMartin)

        ubicacionService.conectar(ubicacionBarCroata.nombre!!, ubicacionLaBoca.nombre!!, "MARITIMO")
        ubicacionService.mover(vectorMartin.id!!, ubicacionLaBoca.id!!)
        vectorMartin = vectorService.recuperarVector(vectorMartin.id!!)

        assertEquals(vectorMartin.ubicacion!!.id, ubicacionLaBoca.id)
    }

    @Test
    fun testCuandoSeIntentaMoverUnVectorAnimalAUnaUbicacionConCaminoTerrestreEsteSeMueve() {
        ubicacionBarCroata  = ubicacionService.crearUbicacion(ubicacionBarCroata,punto)
        ubicacionLaBoca = ubicacionService.crearUbicacion(ubicacionLaBoca,punto2)
        vectorPerro = vectorService.crearVector(vectorPerro)

        ubicacionService.conectar(ubicacionBarCroata.nombre!!, ubicacionLaBoca.nombre!!, "TERRESTRE")
        ubicacionService.mover(vectorPerro.id!!, ubicacionLaBoca.id!!)
        vectorPerro = vectorService.recuperarVector(vectorPerro.id!!)

        assertEquals(vectorPerro.ubicacion!!.id, ubicacionLaBoca.id)
    }

    @Test
    fun testCuandoSeIntentaMoverUnVectorAnimalAUnaUbicacionConCaminoMaritimoEsteSeMueve() {
        ubicacionBarCroata  = ubicacionService.crearUbicacion(ubicacionBarCroata,punto)
        ubicacionLaBoca = ubicacionService.crearUbicacion(ubicacionLaBoca,punto2)
        vectorPerro = vectorService.crearVector(vectorPerro)

        ubicacionService.conectar(ubicacionBarCroata.nombre!!, ubicacionLaBoca.nombre!!, "MARITIMO")
        ubicacionService.mover(vectorPerro.id!!, ubicacionLaBoca.id!!)
        vectorPerro = vectorService.recuperarVector(vectorPerro.id!!)

        assertEquals(vectorPerro.ubicacion!!.id, ubicacionLaBoca.id)
    }

    @Test
    fun testCuandoSeIntentaMoverUnVectorAnimalAUnaUbicacionConCaminoAereoEsteSeMueve() {
        ubicacionBarCroata  = ubicacionService.crearUbicacion(ubicacionBarCroata,punto)
        ubicacionLaBoca = ubicacionService.crearUbicacion(ubicacionLaBoca,punto2)
        vectorPerro = vectorService.crearVector(vectorPerro)

        ubicacionService.conectar(ubicacionBarCroata.nombre!!, ubicacionLaBoca.nombre!!, "AEREO")
        ubicacionService.mover(vectorPerro.id!!, ubicacionLaBoca.id!!)
        vectorPerro = vectorService.recuperarVector(vectorPerro.id!!)

        assertEquals(vectorPerro.ubicacion!!.id, ubicacionLaBoca.id)
    }

    @Test
    fun testCuandoSeIntentaMoverUnVectorInsectoAUnaUbicacionConCaminoTerrestreEsteSeMueve() {
        ubicacionBarCroata  = ubicacionService.crearUbicacion(ubicacionBarCroata,punto)
        ubicacionLaBoca = ubicacionService.crearUbicacion(ubicacionLaBoca,punto2)
        vectorEscarabajo = vectorService.crearVector(vectorEscarabajo)

        ubicacionService.conectar(ubicacionBarCroata.nombre!!, ubicacionLaBoca.nombre!!, "TERRESTRE")
        ubicacionService.mover(vectorEscarabajo.id!!, ubicacionLaBoca.id!!)
        vectorEscarabajo = vectorService.recuperarVector(vectorEscarabajo.id!!)

        assertEquals(vectorEscarabajo.ubicacion!!.id, ubicacionLaBoca.id)
    }

    @Test
    fun testCuandoSeIntentaMoverUnVectorInsectoAUnaUbicacionConCaminoAereoEsteSeMueve() {
        ubicacionBarCroata  = ubicacionService.crearUbicacion(ubicacionBarCroata,punto)
        ubicacionLaBoca = ubicacionService.crearUbicacion(ubicacionLaBoca,punto2)
        vectorEscarabajo = vectorService.crearVector(vectorEscarabajo)

        ubicacionService.conectar(ubicacionBarCroata.nombre!!, ubicacionLaBoca.nombre!!, "AEREO")
        ubicacionService.mover(vectorEscarabajo.id!!, ubicacionLaBoca.id!!)
        vectorEscarabajo = vectorService.recuperarVector(vectorEscarabajo.id!!)

        assertEquals(vectorEscarabajo.ubicacion!!.id, ubicacionLaBoca.id)
    }

    @Test
    fun testCuandoSeIntentaMoverUnVectorHumanoConMutacionPropulsionMotoraAUnaUbicacionConCaminoAereoEsteSeMueve() {
        ubicacionBarCroata  = ubicacionService.crearUbicacion(ubicacionBarCroata,punto)
        ubicacionLaBoca = ubicacionService.crearUbicacion(ubicacionLaBoca,punto2)
        vectorMartin = vectorService.crearVector(vectorMartin)
        vectorMartin.mutacionesPadecidas.add(PropulsionMotora("COVID"))

        vectorService.actualizarVector(vectorMartin)
        vectorMartin = vectorService.recuperarVector(vectorMartin.id!!)

        ubicacionService.conectar(ubicacionBarCroata.nombre!!, ubicacionLaBoca.nombre!!, "AEREO")
        ubicacionService.mover(vectorMartin.id!!, ubicacionLaBoca.id!!)
        vectorMartin = vectorService.recuperarVector(vectorMartin.id!!)

        assertEquals(vectorMartin.ubicacion!!.nombre, ubicacionLaBoca.nombre)
    }

    @Test
    fun testCuandoSeIntentaMoverUnVectorInsectoConMutacionElectroBranqueasAUnaUbicacionConCaminoMaritimoEsteSeMueve() {
        ubicacionBarCroata = ubicacionService.crearUbicacion(ubicacionBarCroata,punto)
        ubicacionLaBoca = ubicacionService.crearUbicacion(ubicacionLaBoca,punto2)
        vectorEscarabajo = vectorService.crearVector(vectorEscarabajo)
        vectorEscarabajo.mutacionesPadecidas.add(ElectroBranqueas("COVID"))

        vectorService.actualizarVector(vectorEscarabajo)
        vectorEscarabajo = vectorService.recuperarVector(vectorEscarabajo.id!!)

        ubicacionService.conectar(ubicacionBarCroata.nombre!!, ubicacionLaBoca.nombre!!, "MARITIMO")
        ubicacionService.mover(vectorEscarabajo.id!!, ubicacionLaBoca.id!!)
        vectorEscarabajo = vectorService.recuperarVector(vectorEscarabajo.id!!)

        assertEquals(vectorEscarabajo.ubicacion!!.nombre, ubicacionLaBoca.nombre)
    }

    @Test
    fun testCuandoConsultoUbicacionesInfectadasMeRetornaUnaListaConElNombreDeLasUbicaiconesQueTienenVectoresInfectados(){

        ubicacionLaBoca = ubicacionService.crearUbicacion(ubicacionLaBoca,punto)
        ubicacionCordillera = ubicacionService.crearUbicacion(ubicacionCordillera,punto2)
        patogenoVirus = patogenoService.crearPatogeno(patogenoVirus)
        vectorService.crearVector(vectorMartin)
        var vectorRoman = vectorService.crearVector(VectorHumano("Roman", ubicacionLaBoca))
        ubicacionLaBoca = ubicacionService.recuperarUbicacion(ubicacionLaBoca.id!!)


        val especie1 = patogenoService.agregarEspecie(patogenoVirus.id!!, "COVID", ubicacionLaBoca.id!!)
        vectorService.infectarVector(vectorMartin.id!!, especie1.id!!)
        vectorService.infectarVector(vectorRoman.id!!,especie1.id!!)
        `when`(rng.getRandomNumber(0, 1)).thenReturn(0)
        `when`(rng.getRandomNumber(1, 10)).thenReturn(10)
        `when`(rng.determinarProbabilidad(anyInt())).thenReturn (true)
        ubicacionService.expandir(ubicacionLaBoca.id!!)
        ubicacionService.expandir(ubicacionBarCroata.id!!)


        val ubicacionesInfectadas = ubicacionDAO.ubicacionesInfectadas()
        assertEquals(ubicacionesInfectadas.size,2)
        assertTrue(ubicacionesInfectadas.contains(ubicacionLaBoca.nombre))
        assertTrue(ubicacionesInfectadas.contains(ubicacionBarCroata.nombre))

    }

    @Test
    fun testCuandoConsultoUbicacionesInfectadasYNoHayNingunaUbicacionConVectoresInfectadosMeRetornaUnaListaVacia(){
        ubicacionLaBoca = ubicacionService.crearUbicacion(ubicacionLaBoca,punto)
        ubicacionCordillera = ubicacionService.crearUbicacion(ubicacionCordillera,punto2)

        assertEquals(0 , ubicacionDAO.ubicacionesInfectadas().size)


    }

    @Test
    fun testCuandoSeIntentaMoverUnVectorAUnaUbicacionAMasDe100KmSeLanzaUbicacionMuyLejanaException() {

        vectorGaviota = VectorAnimal("Gaviota", ubicacionLaBoca)

        ubicacionLaBoca = ubicacionService.crearUbicacion(ubicacionLaBoca, GeoJsonPoint(0.0, 0.0))
        ubicacionCordillera = ubicacionService.crearUbicacion(ubicacionCordillera, GeoJsonPoint(5.0, 5.0))
        ubicacionService.conectar(ubicacionLaBoca.nombre!!, ubicacionCordillera.nombre!!, "AEREO")

        vectorGaviota = vectorService.crearVector(vectorGaviota)

        assertThrows<UbicacionMuyLejanaException> { ubicacionService.mover(vectorGaviota.id!!, ubicacionCordillera.id!!) }
    }

    @Test
    fun testCuandoSeIntentaMoverUnVectorAUnaUbicacionAMenosDe100KmDeDistanciaElVectorSeMueve() {
        vectorGaviota = VectorAnimal("Gaviota", ubicacionLaBoca)

        ubicacionLaBoca = ubicacionService.crearUbicacion(ubicacionLaBoca, GeoJsonPoint(0.0, 0.0))
        ubicacionCordillera = ubicacionService.crearUbicacion(ubicacionCordillera, GeoJsonPoint(0.01, 0.01))
        ubicacionService.conectar(ubicacionLaBoca.nombre!!, ubicacionCordillera.nombre!!, "AEREO")

        vectorGaviota = vectorService.crearVector(vectorGaviota)

        ubicacionService.mover(vectorGaviota.id!!, ubicacionCordillera.id!!)
        vectorGaviota = vectorService.recuperarVector(vectorGaviota.id!!)

        assertEquals(vectorGaviota.ubicacion!!.nombre, "Cordillera de Los Andes")
    }

    @Test
    fun testCuandoSeIntentaMoverUnVectorConMutacionTeletransportacionAUnaUbicacionAMenosDe100KmPeroSinCaminosElVectorSeMueve() {
        vectorGaviota = VectorAnimal("Gaviota", ubicacionLaBoca)
        vectorGaviota.agregarMutacionPadecida(Teletransportacion("Virus"))
        vectorGaviota = vectorService.crearVector(vectorGaviota)

        ubicacionLaBoca = ubicacionService.crearUbicacion(ubicacionLaBoca, GeoJsonPoint(0.0, 0.0))
        ubicacionCordillera = ubicacionService.crearUbicacion(ubicacionCordillera, GeoJsonPoint(0.1, 0.1))

        ubicacionService.mover(vectorGaviota.id!!, ubicacionCordillera.id!!)
        vectorGaviota = vectorService.recuperarVector(vectorGaviota.id!!)

        assertEquals("Cordillera de Los Andes", vectorGaviota.ubicacion!!.nombre)
    }

    @AfterEach
    fun tearDown() {
        dataService.cleanAll()
        ubicacionNeo4jDAO.detachDeleteAll()
        ubicacionMongoDBDAO.deleteAll()
        distritoDAO.deleteAll()
    }
}