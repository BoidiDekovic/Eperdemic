package ar.edu.unq.eperdemic.modelo

import ar.edu.unq.eperdemic.modelo.vector.VectorHumano
import ar.edu.unq.eperdemic.modelo.enums.TipoDeVector
import ar.edu.unq.eperdemic.modelo.exception.MutacionAtributoFueraDeRangoException
import ar.edu.unq.eperdemic.modelo.exception.MutacionAtributoInvalidoException
import ar.edu.unq.eperdemic.modelo.mutacion.*
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MutacionTest {

    private lateinit var bioalteracionGenetica: BioalteracionGenetica
    private lateinit var supresionBiomecanica: SupresionBiomecanica
    private lateinit var propulsionMotora: PropulsionMotora
    private lateinit var electroBranqueas: ElectroBranqueas
    private lateinit var teletransportacion: Teletransportacion

    private lateinit var vectorMartin : VectorHumano
    private lateinit var ubicacionUNQ: Ubicacion

    private lateinit var especieCordyceps: Especie
    private lateinit var especieSalmonella : Especie
    private lateinit var especieCovid: Especie

    private lateinit var patogenoVirus: Patogeno
    private lateinit var patogenoBacteria: Patogeno
    private lateinit var patogenoHongo: Patogeno
    @BeforeEach
    fun setUp(){
        patogenoVirus = Patogeno("Virus", 95, 5, 3, 20, 13)
        patogenoBacteria = Patogeno("Bacteria", 95, 5, 3, 30, 13)
        patogenoHongo = Patogeno("Hongo", 95, 5, 3, 40, 13)

        especieCovid = Especie(patogenoVirus, "Covid", "China")
        especieCordyceps = Especie(patogenoHongo, "Cordyceps", "Uruguay")
        especieSalmonella = Especie(patogenoBacteria, "Salmonella", "Bolivia")

        ubicacionUNQ = Ubicacion("UNQ")

        vectorMartin = VectorHumano("Martin", ubicacionUNQ)

        bioalteracionGenetica = BioalteracionGenetica(TipoDeVector.HUMANO.toString(), especieCovid.nombre)
        supresionBiomecanica = SupresionBiomecanica(35,especieCovid.nombre)
        propulsionMotora = PropulsionMotora(especieCovid.nombre)
        electroBranqueas = ElectroBranqueas(especieCovid.nombre)
        teletransportacion = Teletransportacion(especieCovid.nombre)
    }

   @Test
   fun testAlIniciarUnaMutacionBiolteracionGeneticaTodosSusColaboradoresSeInicianCorrectamente(){
       assertEquals(especieCovid.nombre, bioalteracionGenetica.nombreEspecie)
       assertEquals(bioalteracionGenetica.tipoVector, TipoDeVector.HUMANO.toString())
   }
    @Test
    fun testAlIniciarUnaMutacionSupresionBiomecanicaTodosSusColaboradoresSeInicianCorrectamente(){
        assertEquals(especieCovid.nombre, supresionBiomecanica.nombreEspecie)
        assertEquals(supresionBiomecanica.potencia, 35)
    }

    @Test
    fun testAlInicializarUnaMutacionBioalteracionGeneticaConAtributosInvalidosSeLanzaMutacionAtributoInvalidoException() {
        assertThrows<MutacionAtributoInvalidoException>  { BioalteracionGenetica(TipoDeVector.HUMANO.toString(),"") }
    }
    @Test
    fun testAlInicializarUnaMutacionSupresionBiomecanicaConAtributosInvalidosSeLanzaMutacionAtributoInvalidoException() {
        assertThrows<MutacionAtributoInvalidoException> { SupresionBiomecanica(50, "") }
    }
    @Test
    fun testAlInicializarUnaMutacionSupresionBiomecanicaConUnaPotenciaFueraDeRangoLazaException(){
        assertThrows<MutacionAtributoFueraDeRangoException> { SupresionBiomecanica(123, especieCovid.nombre) }
    }

    @Test
    fun testAlInicializarUnaMutacionSupresionBiomecanicaConUnaPotenciaValidaEstaFuncionaCorrectamente(){
        assertDoesNotThrow { SupresionBiomecanica(80, especieCovid.nombre) }
    }

    @Test
    fun testAlCambiarLaPotenciaDeUnaMutacionEstaSeCambiaCorrectamente() {
        val potenciaAntigua = 35
        assertEquals(potenciaAntigua, supresionBiomecanica.potencia)

        val potenciaEsperada = 50
        supresionBiomecanica.cambiarPotencia(50)

        assertEquals(potenciaEsperada, supresionBiomecanica.potencia)
    }

    @Test
    fun testAlIntentarCambiarLaPotenciaDeUnaMutacionAUnValorInvalidoSeLanzaMutacionAtributoInvalidoException() {
        val potenciaInvalida = 150
        assertThrows<MutacionAtributoFueraDeRangoException> { supresionBiomecanica.cambiarPotencia(potenciaInvalida) }
    }

    @Test
    fun testCuandoSeComparanDosObjetosIgualesDeNombrePorEqualsRetornaTrue() {
        assertTrue(supresionBiomecanica.equals(supresionBiomecanica))
    }

    @Test
    fun testCuandoSeLlamaAlToStringRetornaElNombreDeLaEspecie() {
        assertFalse(supresionBiomecanica.equals(bioalteracionGenetica))
    }

    @Test
    fun testCuandoUnaSupresionBiomecanicaPuedeAniquilarUnaEspecieEsVerdadero(){
        assertTrue(supresionBiomecanica.puedeAniquilarA(especieSalmonella))
    }

    @Test
    fun testCuandoUnaSupresionBiomecanicaNoPuedeAniquilarUnaEspecieEsFalso(){
        assertFalse(supresionBiomecanica.puedeAniquilarA(especieCordyceps))
    }

    @Test
    fun testLaEspecieQueGeneraLASupresionBiomecanicaNoPuedeSerEliminada(){
        assertFalse(supresionBiomecanica.puedeAniquilarA(especieCovid))
    }

    @Test
    fun testCuandoUnaSupresionBiomecanicaEliminaLasEspeciesQuePuedeDelVectorSonLasQueSeEsperaIncludaLaEspecieDeLaMutacion(){
        vectorMartin.agregarEspeciePadecida(especieCovid)
        vectorMartin.agregarEspeciePadecida(especieCordyceps)
        vectorMartin.agregarEspeciePadecida(especieSalmonella)

        assertEquals(3, vectorMartin.especiesPadecidas.size)

        supresionBiomecanica.aniquilarEspeciesDe(vectorMartin)

        assertEquals(2, vectorMartin.especiesPadecidas.size)
        assertTrue(vectorMartin.especiesPadecidas.contains(especieCordyceps))
        assertTrue(vectorMartin.especiesPadecidas.contains(especieCovid))
    }

    @Test
    fun testCuandoSePasaUnaBioalteracionGeneticaAStringEsElEsperado(){
        assertEquals("BioalteracionGenetica(nombreEspecie=Covid, tipoVector=VectorHumano)" , bioalteracionGenetica.toString())
    }

    @Test
    fun testCuandoSePasaUnaSupresionBiomecanicaAStringEsElEsperado(){
        assertEquals("SupresionBiomecanica(nombreEspecie=Covid, potencia=35)" , supresionBiomecanica.toString())
    }

    @Test
    fun testCuandoSePasaUnaPropulsionMotoraAStringEsElEsperado(){
        assertEquals("PropulsionMotora(nombreEspecie = Covid)", propulsionMotora.toString())
    }

    @Test
    fun testCuandoSePasaUnaElectroBranqueasAStringEsElEsperado(){
        assertEquals("ElectroBranqueas(nombreEspecie = Covid)", electroBranqueas.toString())
    }

    @Test
    fun testCuandoSePasaUnaTeletranspotacionAStringEsElEsperado(){
        assertEquals("Teletransportacion(nombreEspecie = Covid)", teletransportacion.toString())
    }

    @Test
    fun testCuandoSeComparanDosPropulsionesMotorasIgualesPorEqualsRetornaTrue() {
        assertTrue(propulsionMotora.equals(propulsionMotora))
    }

    @Test
    fun testCuandoSeComparanDosElectroBranqueasIgualesPorEqualsRetornaTrue() {
        assertTrue(electroBranqueas.equals(electroBranqueas))
    }

    @Test
    fun testCuandoSeComparanDosTeletranspotacionIgualesPorEqualsRetornaTrue() {
        assertTrue(teletransportacion.equals(teletransportacion))
    }
}