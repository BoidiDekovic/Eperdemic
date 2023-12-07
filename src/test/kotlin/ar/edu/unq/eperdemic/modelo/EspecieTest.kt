package ar.edu.unq.eperdemic.modelo

import ar.edu.unq.eperdemic.modelo.vector.VectorHumano
import ar.edu.unq.eperdemic.modelo.enums.TipoDeVector
import ar.edu.unq.eperdemic.modelo.exception.EspecieAtributoInvalidoException
import ar.edu.unq.eperdemic.modelo.exception.EspecieSinMutacionesException
import ar.edu.unq.eperdemic.modelo.mutacion.BioalteracionGenetica
import ar.edu.unq.eperdemic.modelo.mutacion.SupresionBiomecanica
import ar.edu.unq.eperdemic.services.exception.EspecieYaTieneMutacionException
import ar.edu.unq.eperdemic.services.impl.helper.RNG
import ar.edu.unq.eperdemic.services.impl.helper.RNGImpl
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.springframework.boot.test.mock.mockito.MockBean


@TestInstance(PER_CLASS)
class EspecieTest {


    private lateinit var patogenoVirus: Patogeno
    private lateinit var especieCovid: Especie
    private lateinit var especieMalaria: Especie
    lateinit var bioalteracionGenetica: BioalteracionGenetica
    lateinit var supresionBiomecanica: SupresionBiomecanica
    lateinit var  vectorMartin : VectorHumano
    lateinit var ubicacionUNQ: Ubicacion
    @MockBean
    private  lateinit var  rng : RNG


    @BeforeEach
    fun setUp() {
        rng = Mockito.mock(RNGImpl::class.java)
        patogenoVirus = Patogeno("Virus", 95, 5, 3, 54, 13)
        especieCovid = Especie(patogenoVirus, "COVID", "China")
        especieMalaria = Especie(patogenoVirus, "Malaria", "China")
        ubicacionUNQ = Ubicacion("UNQ")
        vectorMartin = VectorHumano("Martin", ubicacionUNQ)
        bioalteracionGenetica = BioalteracionGenetica(TipoDeVector.HUMANO.toString(), especieCovid.nombre)
        supresionBiomecanica = SupresionBiomecanica(50,especieCovid.nombre)
    }

    @Test
    fun testAlInstanciarUnaEspecieTodosLosColaboradoresInternosSonInicializadosCorrectamente() {
        assertEquals("COVID", especieCovid.nombre)
        assertEquals("China", especieCovid.paisDeOrigen)
        assertTrue(especieCovid.mutacionesPosibles.isEmpty())
        assertNotEquals(null, especieCovid.patogeno)

    }

    @Test
    fun testAlInicializarUnaEspecieSeVerificaQueNoHayaAtributosInvalidos() {
        assertDoesNotThrow { Especie(patogenoVirus, "COVID", "China") }
    }

    @Test
    fun testAlInicializarUnaEspecieConUnAtributoInvalidoSeLanzaUnaEspecieAtributosInvalidosException() {
        assertThrows<EspecieAtributoInvalidoException> { Especie(patogenoVirus, "", "") }
    }

    @Test
    fun testCuandoSeComparanDosObjetosIgualesDeNombrePorEqualsRetornaTrue() {
        assertTrue(especieCovid.equals(especieCovid))
    }

    @Test
    fun testCuandoSeComparanDosObjetosDistintosDeNombrePorEqualsRetornaFalse() {
        assertFalse(especieCovid.equals(especieMalaria))
    }

    @Test
    fun testCuandoSeLlamaAlToStringRetornaElNombreDeLaEspecie() {
        assertEquals("COVID", especieCovid.toString())
    }
    @Test
    fun testCuandoUnaEspecieQuiereConocerSuCapacidadDeBiomecanizacionLlamaAlGetQueLaRetorna(){
        assertEquals(especieCovid.patogeno.capBiomecanizacion , especieCovid.getCapacidadDeBiomecanizacion())
    }

    @Test
    fun testCuandoUnaEspecieAgregaUnaMutacionPosibleLoHaceCorrectamente() {
        especieCovid.agregarMutacionPosible(supresionBiomecanica)

        assertTrue(especieCovid.mutacionesPosibles.isNotEmpty())
        assertTrue(especieCovid.mutacionesPosibles.contains(supresionBiomecanica))
    }

    @Test
    fun testCuandoUnaEspecieQuiereAgregarUnaMutacionPosibleQueYaTieneSeLanzaEspecieYaTieneMutacionException() {
        especieCovid.agregarMutacionPosible(supresionBiomecanica)

        assertThrows<EspecieYaTieneMutacionException> { especieCovid.agregarMutacionPosible(supresionBiomecanica) }
    }

    @Test
    fun testCuandoLePidoUnaMutacionPosibleRandomALaEspecieYNoHaySeLanzaExcepcion(){
        assertThrows<EspecieSinMutacionesException> { especieCovid.getMutacionRandom(rng) }
    }

    @Test
    fun testCuandoLePidoUnaMutacionPosibleRandomALaEspecieEsLaPrimera(){
        especieCovid.agregarMutacionPosible(supresionBiomecanica)
        especieCovid.agregarMutacionPosible(bioalteracionGenetica)
        `when`(rng.getRandomNumber(0, 1)).thenReturn(0)
        val mutacion = especieCovid.getMutacionRandom(rng)
        assertEquals(mutacion, supresionBiomecanica)
    }
    @Test
    fun testCuandoLePidoUnaMutacionPosibleRandomALaEspecieEsLaSegunda(){
        especieCovid.agregarMutacionPosible(supresionBiomecanica)
        especieCovid.agregarMutacionPosible(bioalteracionGenetica)
        `when`(rng.getRandomNumber(0, 1)).thenReturn(1)
        val mutacion = especieCovid.getMutacionRandom(rng)
        assertEquals(mutacion, bioalteracionGenetica)
    }

    @Test
    fun testCuandoLePidoLaCapacidadDeBiomecanizacionEsLaEsperada(){
        assertEquals(patogenoVirus.capBiomecanizacion, especieCovid.getCapacidadDeBiomecanizacion())
    }
}