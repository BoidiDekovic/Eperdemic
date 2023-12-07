package ar.edu.unq.eperdemic.modelo

import ar.edu.unq.eperdemic.modelo.vector.VectorAnimal
import ar.edu.unq.eperdemic.modelo.vector.VectorHumano
import ar.edu.unq.eperdemic.modelo.vector.VectorInsecto
import ar.edu.unq.eperdemic.modelo.enums.TipoDeVector
import org.junit.jupiter.api.*
import ar.edu.unq.eperdemic.modelo.exception.VectorAtributoInvalidoException
import ar.edu.unq.eperdemic.modelo.exception.VectorNoPuedeSerContagiadoException
import ar.edu.unq.eperdemic.modelo.mutacion.BioalteracionGenetica
import ar.edu.unq.eperdemic.modelo.mutacion.Mutacion
import ar.edu.unq.eperdemic.modelo.mutacion.SupresionBiomecanica
import ar.edu.unq.eperdemic.services.impl.helper.RNG
import ar.edu.unq.eperdemic.services.impl.helper.RNGImpl
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS
import org.mockito.Mockito.*


@TestInstance(PER_CLASS)
class VectorTest {


    lateinit var especieCordyceps: Especie
    lateinit var especieGripe: Especie
    lateinit var especieSalmonella : Especie
    lateinit var especieCovid: Especie
    lateinit var patogenoVirus: Patogeno
    lateinit var patogenoBacteria: Patogeno
    lateinit var patogenoHongo: Patogeno

    private lateinit var rng: RNG
    private lateinit var vectorJulian: VectorHumano
    private lateinit var vectorFede: VectorHumano
    private lateinit var vectorFirulais: VectorAnimal
    private lateinit var vectorAyudanteDeSanta: VectorAnimal
    private lateinit var vectorMosquito: VectorInsecto
    private lateinit var vectorCucaracha: VectorInsecto
    private lateinit var ubicacionUNQ: Ubicacion
    private lateinit var ubicacionBurgerKing: Ubicacion

    private lateinit var supresionBiomecanicaDeCovid: SupresionBiomecanica
    private lateinit var supresionBiomecanicaDeCovid2: SupresionBiomecanica
    private lateinit var supresionBiomecanicaDeGripe: SupresionBiomecanica
    private lateinit var supresionBiomecanicaCordyceps: SupresionBiomecanica

    private lateinit var bioalteracionGeneticaAnimalDeCovid: BioalteracionGenetica
    private lateinit var bioalteracionGeneticaAnimalDeSalmonella: BioalteracionGenetica
    private lateinit var bioalteracionGeneticaAnimalDeCordyceps: BioalteracionGenetica
    private lateinit var bioalteracionGeneticaInsectoDeCordyceps2: BioalteracionGenetica

    @BeforeEach
    fun setUp() {
        rng = mock(RNGImpl::class.java)
        patogenoVirus = Patogeno("Virus", 95, 5, 3, 20, 50)
        patogenoBacteria = Patogeno("Bacteria", 95, 5, 3, 30, 19)
        patogenoHongo = Patogeno("Hongo", 95, 5, 3, 40, 23)
        especieCordyceps = Especie(patogenoHongo, "Cordyceps", "Uruguay")
        especieCovid = Especie(patogenoVirus, "Covid", "China")
        especieSalmonella = Especie(patogenoBacteria, "Salmonella", "Bolivia")
        especieGripe  = patogenoVirus.crearEspecie("Gripe", "Brasil")

        ubicacionUNQ = Ubicacion("UNQ")
        ubicacionBurgerKing = Ubicacion("Burger King")

        vectorFede = VectorHumano("Fede", ubicacionUNQ)
        vectorJulian = VectorHumano("Julian", ubicacionUNQ)
        vectorFirulais = VectorAnimal("Firulais", ubicacionUNQ)
        vectorAyudanteDeSanta = VectorAnimal("Ayudante De Santa", ubicacionUNQ)
        vectorMosquito = VectorInsecto("Mosquito", ubicacionUNQ)
        vectorCucaracha = VectorInsecto("Cucaracha", ubicacionUNQ)

        supresionBiomecanicaDeCovid = SupresionBiomecanica(35, especieCovid.nombre)
        supresionBiomecanicaDeCovid2 = SupresionBiomecanica(20, especieCovid.nombre)
        supresionBiomecanicaDeGripe = SupresionBiomecanica(10, especieGripe.nombre)
        supresionBiomecanicaCordyceps = SupresionBiomecanica(50, especieCordyceps.nombre)

        bioalteracionGeneticaAnimalDeCovid = BioalteracionGenetica(TipoDeVector.ANIMAL.toString(), especieCovid.nombre)
        bioalteracionGeneticaAnimalDeSalmonella = BioalteracionGenetica(TipoDeVector.ANIMAL.toString(), especieSalmonella.nombre)
        bioalteracionGeneticaAnimalDeCordyceps = BioalteracionGenetica(TipoDeVector.ANIMAL.toString(), especieCordyceps.nombre)
        bioalteracionGeneticaInsectoDeCordyceps2 = BioalteracionGenetica(TipoDeVector.INSECTO.toString(), especieCordyceps.nombre)

        especieCovid.agregarMutacionPosible(supresionBiomecanicaDeCovid2)
        especieCovid.agregarMutacionPosible(supresionBiomecanicaDeCovid)
        especieGripe.agregarMutacionPosible(supresionBiomecanicaDeGripe)
        especieCordyceps.agregarMutacionPosible(supresionBiomecanicaCordyceps)

        especieCovid.agregarMutacionPosible(bioalteracionGeneticaAnimalDeCovid)
        especieSalmonella.agregarMutacionPosible(bioalteracionGeneticaAnimalDeSalmonella)
        especieSalmonella.agregarMutacionPosible(bioalteracionGeneticaInsectoDeCordyceps2)
    }

    @Test
    fun testCuandoSeInicializaVectoresSeVerificaQueNoHayaAtributosInvalidos() {
        assertDoesNotThrow { VectorHumano("Julian", ubicacionUNQ) }
        assertDoesNotThrow { VectorAnimal("Firulais", ubicacionUNQ) }
        assertDoesNotThrow { VectorInsecto("Mosquito", ubicacionUNQ) }
    }

    @Test
    fun testCuandoSeInicializaVectoresConUnAtributosInvalidosSeLanzaVectorAtributoInvalidoException() {
        assertThrows<VectorAtributoInvalidoException> { VectorHumano("", ubicacionUNQ) }
        assertThrows<VectorAtributoInvalidoException> { VectorAnimal("", ubicacionUNQ) }
        assertThrows<VectorAtributoInvalidoException> { VectorInsecto("", ubicacionUNQ) }
    }

    @Test
    fun testCuandoSeCreaUnVectorNoEstaInfectado(){
        assertFalse(vectorJulian.estaInfectado)
    }

    @Test
    fun testCuandoSeAgregaUnaEspeciePadecidaAUnVectorEsteEstaInfectado(){
        assertFalse(vectorJulian.estaInfectado)
        vectorJulian.agregarEspeciePadecida(especieCovid)
        assertTrue(vectorJulian.estaInfectado)
    }

    @Test
    fun testCuandoSeComparanDosVectoresIgualesPorEqualsRetornaTrue() {
        assertTrue(vectorJulian.equals(vectorJulian))
    }

    @Test
    fun testCuandoSeLlamaAlMetodoToStringRetornaElNombreDelVector() {
        assertEquals("Julian", vectorJulian.toString())
    }

    @Test
    fun testCuandoUnHumanoIntentaContagiarAUnAnimalSeLanzaVectorNoPuedeSerContagiadoException() {
        assertThrows<VectorNoPuedeSerContagiadoException> { vectorJulian.intentarContagiarA(vectorFirulais, rng) }
    }

    @Test
    fun testCuandoUnAnimalIntentaContagiarAOtroAnimalSeLanzaVectorNoPuedeSerContagiadoException() {
        assertThrows<VectorNoPuedeSerContagiadoException> { vectorFirulais.intentarContagiarA(vectorAyudanteDeSanta, rng) }
    }

    @Test
    fun testCuandoUnInsectoIntentaContagiarAOtroInsectoSeLanzaVectorNoPuedeSerContagiadoException() {
        assertThrows<VectorNoPuedeSerContagiadoException> { vectorMosquito.intentarContagiarA(vectorCucaracha, rng) }
    }

    @Test
    fun testCuandoUnVectorConEnfermedadesQuiereContagiarAOtroVectorLoHaceCorrectamente() {
        assertTrue(vectorJulian.especiesPadecidas.isEmpty())

        `when`(rng.getRandomNumber(1, 10)).thenReturn(10)
        `when`(rng.determinarProbabilidad(anyInt())).thenReturn (true)
        vectorFirulais.agregarEspeciePadecida(especieCovid)
        vectorFirulais.intentarContagiarA(vectorJulian, rng)

        assertEquals(1, vectorJulian.especiesPadecidas.size)
        assertTrue(vectorJulian.especiesPadecidas.contains(especieCovid))
    }

    @Test
    fun testCuandoUnVectorConEnfermedadesQuiereContagiarAOtroVectorYNoTieneProbabilidadesNoLoHace() {
        assertTrue(vectorJulian.especiesPadecidas.isEmpty())

        `when`(rng.getRandomNumber(1, 10)).thenReturn(1)
        `when`(rng.getRandomNumber(1, 100)).thenReturn (100)
        vectorFirulais.agregarEspeciePadecida(especieCovid)
        vectorFirulais.intentarContagiarA(vectorJulian, rng)

        assertTrue(vectorJulian.especiesPadecidas.isEmpty())
    }
    @Test
    fun testCuandoUnVectorSeMueveCambiaSuUbicacionCorrectamente(){
        assertEquals(vectorJulian.ubicacion,ubicacionUNQ)
        vectorJulian.moverseA(ubicacionBurgerKing)
        assertEquals(vectorJulian.ubicacion,ubicacionBurgerKing)
    }

    @Test
    fun testCuandoUnVectorAgregaUnaMutacionPadecidaSeAgregaCorrectamente() {
        vectorJulian.agregarMutacionPadecida(supresionBiomecanicaDeCovid)

        assertTrue(vectorJulian.mutacionesPadecidas.isNotEmpty())
        assertTrue(vectorJulian.mutacionesPadecidas.contains(supresionBiomecanicaDeCovid))
    }

    @Test
    fun testCuandoUnVectorAgregaUnaMutacionPadecidaDelMismoTipoPeroDiferenteEspecieSeAgregaCorrectamente() {
        vectorJulian.agregarMutacionPadecida(supresionBiomecanicaDeCovid)
        vectorJulian.agregarMutacionPadecida(supresionBiomecanicaDeGripe)

        assertEquals(2, vectorJulian.mutacionesPadecidas.size)
        assertTrue(vectorJulian.mutacionesPadecidas.contains(supresionBiomecanicaDeCovid))
        assertTrue(vectorJulian.mutacionesPadecidas.contains(supresionBiomecanicaDeGripe))
    }

    @Test
    fun testCuandoUnVectorContagiaAOtroEsteMutaYSuMutacionSeAgregaASusMutaciones(){
        assertTrue(vectorJulian.mutacionesPadecidas.isEmpty())

        `when`(rng.getRandomNumber(0, 1)).thenReturn(0)
        `when`(rng.determinarProbabilidad(anyInt())).thenReturn (true)
        vectorFirulais.agregarEspeciePadecida(especieCovid)

        vectorFirulais.intentarContagiarA(vectorJulian, rng)

        assertEquals(1, vectorFirulais.mutacionesPadecidas.size)
        assertTrue(vectorFirulais.mutacionesPadecidas.contains(supresionBiomecanicaDeCovid))
    }

    @Test
    fun testCuandoUnVectorContagiaAOtroEsteNoMutaYNoSeAgregaNadaASuListaDeMutaciones(){
        assertTrue(vectorJulian.mutacionesPadecidas.isEmpty())

        `when`(rng.getRandomNumber(0, 1)).thenReturn(0)
        `when`(rng.determinarProbabilidad(anyInt())).thenReturn (true)
        `when`(rng.determinarProbabilidad(50)).thenReturn (false)
        vectorFirulais.agregarEspeciePadecida(especieCovid)

        vectorFirulais.intentarContagiarA(vectorJulian, rng)

        assertTrue(vectorFirulais.mutacionesPadecidas.isEmpty())
    }

    @Test
    fun testCuandoUnVectorContagiaAOtroYLaEspecieQueEstaContagiandoNoTieneMutacionesPosiblesNoMuta(){
        assertTrue(vectorJulian.mutacionesPadecidas.isEmpty())

        `when`(rng.getRandomNumber(0, 1)).thenReturn(0)
        `when`(rng.determinarProbabilidad(anyInt())).thenReturn (true)
        especieCovid.mutacionesPosibles = HashSet<Mutacion>()
        assertTrue(especieCovid.mutacionesPosibles.isEmpty())
        vectorFirulais.agregarEspeciePadecida(especieCovid)

        vectorFirulais.intentarContagiarA(vectorJulian, rng)

        assertTrue(vectorFirulais.mutacionesPadecidas.isEmpty())
    }

    @Test
    fun testCuandoUnVectorContagiaAOtroYEsteMutaYYaTieneLasMutacionesDisponiblesNoSeAgregan(){
        assertTrue(vectorJulian.mutacionesPadecidas.isEmpty())

        `when`(rng.getRandomNumber(0, 1)).thenReturn(0)
        `when`(rng.determinarProbabilidad(anyInt())).thenReturn (true)
        especieCovid.mutacionesPosibles = HashSet<Mutacion>()
        especieCovid.agregarMutacionPosible(supresionBiomecanicaDeCovid)
        vectorFirulais.agregarEspeciePadecida(especieCovid)
        vectorFirulais.agregarMutacionPadecida(supresionBiomecanicaDeCovid)

        vectorFirulais.intentarContagiarA(vectorJulian, rng)

        assertEquals(1, vectorFirulais.mutacionesPadecidas.size)
        assertTrue(vectorFirulais.mutacionesPadecidas.contains(supresionBiomecanicaDeCovid))
    }

    @Test
    fun testCuandoUnVectorContraeSupresionBiomecanicaSeEliminanLasEspeciesConDefensaMenorALaPotenciaDeLaMutacionYDeDistintaEspecieDeLaMutacion(){
        vectorFirulais.agregarEspeciePadecida(especieCovid)
        vectorFirulais.agregarEspeciePadecida(especieCordyceps)
        vectorFirulais.agregarEspeciePadecida(especieSalmonella)
        assertEquals(3, vectorFirulais.especiesPadecidas.size)

        `when`(rng.getRandomNumber(0, 1)).thenReturn(0)
        `when`(rng.determinarProbabilidad(anyInt())).thenReturn (true)
        `when`(rng.determinarProbabilidad(23)).thenReturn (false)
        `when`(rng.determinarProbabilidad(19)).thenReturn (false)
        vectorFirulais.intentarContagiarA(vectorJulian, rng)

        assertEquals(1, vectorFirulais.mutacionesPadecidas.size)
        assertTrue(vectorFirulais.mutacionesPadecidas.contains(supresionBiomecanicaDeCovid))
        assertTrue(vectorFirulais.especiesPadecidas.contains(especieCovid))
        assertEquals(2, vectorFirulais.especiesPadecidas.size)
        assertTrue(vectorFirulais.especiesPadecidas.contains(especieCordyceps))
    }

    @Test
    fun testCuandoUnVectorEstaContagiandoYContraeSupresionBiomecanicaYSusEspeciesSeEliminanEstasNoContagian(){
        patogenoHongo.defensa = 10
        patogenoVirus.defensa = 70
        vectorFirulais.agregarEspeciePadecida(especieCovid)
        vectorFirulais.agregarEspeciePadecida(especieSalmonella)
        vectorFirulais.agregarEspeciePadecida(especieCordyceps)
        assertEquals(3, vectorFirulais.especiesPadecidas.size)

        `when`(rng.getRandomNumber(0, 1)).thenReturn(0)
        `when`(rng.determinarProbabilidad(anyInt())).thenReturn (true)

        vectorFirulais.intentarContagiarA(vectorJulian, rng)

        assertEquals(1, vectorFirulais.mutacionesPadecidas.size)
        assertTrue(vectorFirulais.mutacionesPadecidas.contains(supresionBiomecanicaDeCovid))
        assertEquals(1, vectorFirulais.especiesPadecidas.size)
        assertTrue(vectorFirulais.especiesPadecidas.contains(especieCovid))
        assertEquals(1, vectorJulian.especiesPadecidas.size)
        assertTrue(vectorJulian.especiesPadecidas.contains(especieCovid))
        assertTrue(vectorJulian.especiesPadecidas.contains(especieCovid))

    }

    @Test
    fun testCuandoUnVectorConSupresionBiomecanicaSeLeIntentaContagiarConUnaEspecieDebilNoSeContagia(){
        vectorJulian.agregarMutacionPadecida(supresionBiomecanicaDeCovid)
        vectorFirulais.agregarEspeciePadecida(especieSalmonella)

        `when`(rng.getRandomNumber(0, 1)).thenReturn(0)
        `when`(rng.determinarProbabilidad(anyInt())).thenReturn (true)
        vectorFirulais.intentarContagiarA(vectorJulian, rng)

        assertTrue(vectorJulian.especiesPadecidas.isEmpty())
    }

    @Test
    fun testCuandoUnVectorConSupresionBiomecanicaSeLeIntentaContagiarConUnaEspecieFuerteSeContagia(){
        vectorJulian.agregarMutacionPadecida(supresionBiomecanicaDeCovid)
        vectorFirulais.agregarEspeciePadecida(especieCordyceps)

        `when`(rng.getRandomNumber(0, 1)).thenReturn(0)
        `when`(rng.determinarProbabilidad(anyInt())).thenReturn (true)
        vectorFirulais.intentarContagiarA(vectorJulian, rng)

        assertTrue(vectorJulian.especiesPadecidas.contains(especieCordyceps))
        assertEquals(1, vectorJulian.especiesPadecidas.size)
    }

    @Test
    fun testLaEspecieQueGeneraLaMutacionPermanece(){
        vectorFirulais.agregarEspeciePadecida(especieCordyceps)
        assertEquals(1, vectorFirulais.especiesPadecidas.size)

        `when`(rng.getRandomNumber(0, 1)).thenReturn(0)
        `when`(rng.determinarProbabilidad(anyInt())).thenReturn (true)
        vectorFirulais.intentarContagiarA(vectorJulian, rng)

        assertEquals(1, vectorFirulais.mutacionesPadecidas.size)
        assertTrue(vectorFirulais.mutacionesPadecidas.contains(supresionBiomecanicaCordyceps))
        assertTrue(vectorFirulais.especiesPadecidas.isNotEmpty())
        assertTrue(vectorFirulais.especiesPadecidas.contains(especieCordyceps))
    }

    @Test
    fun testCuandoUnVectorContraeBioalteracionGeneticaDeUnTipoQueNoPuedeContagiarPuedeContagiarlo(){
        vectorJulian.agregarEspeciePadecida(especieCovid)
        vectorJulian.agregarMutacionPadecida(bioalteracionGeneticaAnimalDeCovid)

        assertDoesNotThrow { vectorJulian.intentarContagiarA(vectorFirulais, rng) }
    }

    @Test
    fun testCuandoUnVectorTieneBioalteracionGeneticaSoloPuedeContagiarLasEspeciesDeLaMutacionDelTipoDeVectorDeLaMutacionesBioalteracionGenetica(){
        especieSalmonella.mutacionesPosibles = HashSet()
        especieCordyceps.mutacionesPosibles = HashSet()
        vectorJulian.agregarEspeciePadecida(especieSalmonella)
        vectorJulian.agregarMutacionPadecida(bioalteracionGeneticaAnimalDeSalmonella)
        vectorJulian.agregarEspeciePadecida(especieCordyceps)
        vectorJulian.agregarMutacionPadecida(bioalteracionGeneticaInsectoDeCordyceps2)

        `when`(rng.getRandomNumber(0, 1)).thenReturn(0)
        `when`(rng.determinarProbabilidad(anyInt())).thenReturn (true)
        vectorJulian.intentarContagiarA(vectorFirulais,rng)
        assertTrue(vectorJulian.especiesPadecidas.contains(especieCordyceps))
        assertTrue(vectorJulian.especiesPadecidas.contains(especieSalmonella))
        assertTrue(vectorFirulais.especiesPadecidas.contains(especieSalmonella))
        assertFalse(vectorFirulais.especiesPadecidas.contains(especieCordyceps))


    }
    @Test
    fun testCuandoUnVectorTieneBioalteracionGeneticaSoloPuedeContagiarLaEspecieDeLaMutacionDelTipoDeVectorDeLaMutacionBioalteracionGeneticaSiEsDeOtroTipoLanzaException(){
        especieCordyceps.mutacionesPosibles = HashSet()
        vectorJulian.agregarEspeciePadecida(especieCordyceps)
        vectorJulian.agregarMutacionPadecida(bioalteracionGeneticaInsectoDeCordyceps2)

        `when`(rng.getRandomNumber(0, 1)).thenReturn(0)
        `when`(rng.determinarProbabilidad(anyInt())).thenReturn (true)
        assertThrows<VectorNoPuedeSerContagiadoException> { vectorJulian.intentarContagiarA(vectorFirulais, rng) }



    }
}