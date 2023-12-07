package ar.edu.unq.eperdemic.services.impl

import ar.edu.unq.eperdemic.dao.helper.service.DataService
import ar.edu.unq.eperdemic.modelo.*
import ar.edu.unq.eperdemic.modelo.vector.Vector
import ar.edu.unq.eperdemic.modelo.vector.VectorHumano
import ar.edu.unq.eperdemic.modelo.enums.TipoDeVector
import ar.edu.unq.eperdemic.modelo.exception.MutacionNombreEspecieException
import ar.edu.unq.eperdemic.modelo.mutacion.BioalteracionGenetica
import ar.edu.unq.eperdemic.modelo.mutacion.Mutacion
import ar.edu.unq.eperdemic.modelo.mutacion.SupresionBiomecanica
import ar.edu.unq.eperdemic.services.EspecieService
import ar.edu.unq.eperdemic.services.exception.EspecieIDNoExisteException
import ar.edu.unq.eperdemic.services.exception.MutacionNoExisteException
import ar.edu.unq.eperdemic.services.exception.MutacionYaExistenteException
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit.jupiter.SpringExtension


@ExtendWith(SpringExtension::class)
@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MutacionServiceTest {

    @Autowired
    private lateinit var mutacionService: MutacionServiceImpl
    @Autowired
    private lateinit var dataService: DataService
    @Autowired
    private lateinit var vectorService: VectorServiceImpl
    @Autowired
    private lateinit var patogenoService: PatogenoServiceImpl
    @Autowired
    private lateinit var especieService: EspecieService

    private lateinit var bioalteracionGenetica: Mutacion
    private lateinit var supresionBiomecanica : Mutacion

    private lateinit var vectorMartin : Vector

    private lateinit var ubicacionUNQ : Ubicacion

    private lateinit var patogenoVirus: Patogeno

    private lateinit var especieMalaria: Especie
    private lateinit var especieCovid: Especie

    @BeforeEach
    fun setUp() {
        ubicacionUNQ = Ubicacion("UNQ")

        vectorMartin = VectorHumano("Martin", ubicacionUNQ)
        vectorService.crearVector(vectorMartin)

        patogenoVirus = Patogeno("Virus", 6, 73, 52, 32, 33)
        patogenoService.crearPatogeno(patogenoVirus)

        especieMalaria = patogenoService.agregarEspecie(patogenoVirus.id!!, "Malaria", ubicacionUNQ.id!!)
        especieCovid = patogenoService.agregarEspecie(patogenoVirus.id!!, "COVID", ubicacionUNQ.id!!)

        supresionBiomecanica = SupresionBiomecanica(45 , especieCovid.nombre)
        bioalteracionGenetica = BioalteracionGenetica(TipoDeVector.HUMANO.toString(),especieCovid.nombre)
    }

    @AfterEach
    fun tearDown() {
        dataService.cleanAll()
    }

    @Test
    fun testAlCrearYRecuperarUnaMutacionDeSupresionBiomecanicaSeObtienenObjetosSimilares(){
        supresionBiomecanica = mutacionService.crearMutacion(supresionBiomecanica)
        val mutacionRecuperada = mutacionService.recuperarMutacion(supresionBiomecanica.id!!)
        assertEquals(supresionBiomecanica.nombreEspecie, mutacionRecuperada.nombreEspecie)
    }

    @Test
    fun testAlCrearYRecuperarUnaMutacionDeBioalteracionGeneticaSeObtienenObjetosSimilares() {
        bioalteracionGenetica = mutacionService.crearMutacion(bioalteracionGenetica)
        val recuperarMutacion = mutacionService.recuperarMutacion(bioalteracionGenetica.id!!)
        assertEquals(bioalteracionGenetica.nombreEspecie, recuperarMutacion.nombreEspecie)
    }

    @Test
    fun testAlCrearUnaMutacionQueYaFueCreadaSeLanzaMutacionYaExistenteException() {
        mutacionService.crearMutacion(supresionBiomecanica)
        assertThrows<MutacionYaExistenteException> { mutacionService.crearMutacion(supresionBiomecanica) }
    }

    @Test
    fun testCuandoSeRecuperaUnaMutacionQueNoExisteLanzaMutacionNoExisteException() {
        val especieIDNoExistente: Long = -1
        assertThrows<MutacionNoExisteException> { mutacionService.recuperarMutacion(especieIDNoExistente) }
    }

    @Test
    fun testCuandoSeActualizaUnaMutacionQueNoExisteSeLanzaMutacionNoExisteException(){
        assertThrows<MutacionNoExisteException> { mutacionService.actualizarMutacion(supresionBiomecanica) }
    }

    @Test
    fun testCuandoSeActualizaUnaMutacionEstaSeActualizaCorrectamente(){
        val mutacion = mutacionService.crearMutacion(supresionBiomecanica) as SupresionBiomecanica
        assertEquals(45, mutacion.potencia)

        mutacion.cambiarPotencia(5)
        mutacionService.actualizarMutacion(mutacion)
        val mutacionRecuperada = mutacionService.recuperarMutacion(mutacion.id!!) as SupresionBiomecanica
        assertEquals(5, mutacionRecuperada.potencia)
    }

    @Test
    fun testSeRecuperanTodasLasMutacionesYNoHayNingunaSeDevuelveUnaListaVacia(){
        assertTrue(mutacionService.recuperarTodasLasMutaciones().isEmpty())
    }

    @Test
    fun testSeRecuperanTodasLasMutacionesYHayMutacionesSeDevuelveUnaListaConLasEsperadas(){
        mutacionService.crearMutacion(bioalteracionGenetica)
        mutacionService.crearMutacion(supresionBiomecanica)
        val listaDeMutaciones = mutacionService.recuperarTodasLasMutaciones()

        assertEquals(listaDeMutaciones.size , 2)
        assertTrue(listaDeMutaciones.contains(bioalteracionGenetica))
        assertTrue(listaDeMutaciones.contains(supresionBiomecanica))
    }

    @Test
    fun testCuandoUnaMutacionSeAgregaAUnaEspecieQueNoExisteSeLanzaEspecieIDNoExisteException() {
        val idEspecieNoExistente: Long = -1
        assertThrows<EspecieIDNoExisteException> {
            mutacionService.agregarMutacion(idEspecieNoExistente,supresionBiomecanica) }
    }

    @Test
    fun testCuandoUnaMutacionSeAgregaAUnaEspecieQueNoCorrespondeSeLanzaMutacionNombreEspecieException() {
        supresionBiomecanica = mutacionService.crearMutacion(supresionBiomecanica)

        assertThrows<MutacionNombreEspecieException> {
            mutacionService.agregarMutacion(especieMalaria.id!!,supresionBiomecanica) }
    }

    @Test
    fun testCuandoUnaMutacionSeAgregaAUnaEspecieEstaSeAgregaASuListaDeMutaciones() {
        mutacionService.agregarMutacion(especieCovid.id!!,supresionBiomecanica)
        especieCovid = especieService.recuperarEspecie(especieCovid.id!!)

        assertTrue(especieCovid.mutacionesPosibles.isNotEmpty())
        assertTrue(especieCovid.mutacionesPosibles.contains(supresionBiomecanica))
    }
}
