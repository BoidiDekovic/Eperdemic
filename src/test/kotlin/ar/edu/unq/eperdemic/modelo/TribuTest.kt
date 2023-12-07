package ar.edu.unq.eperdemic.modelo


import ar.edu.unq.eperdemic.modelo.exception.Tribu.TribuInvalidaException
import ar.edu.unq.eperdemic.modelo.exception.Tribu.TribuSinVectorIntegranteException
import ar.edu.unq.eperdemic.modelo.exception.Tribu.TribuYaTieneIntegranteException
import ar.edu.unq.eperdemic.modelo.vector.Vector
import ar.edu.unq.eperdemic.modelo.vector.VectorAnimal
import ar.edu.unq.eperdemic.modelo.vector.VectorInsecto
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TribuTest {
    private lateinit var tribuMutarachil: Tribu

    private lateinit var mutarachaRadioactiva: VectorInsecto
    private lateinit var mutarachaDespiadada: VectorInsecto

    @BeforeEach
    fun setUp() {
        mutarachaRadioactiva = VectorInsecto("Mutaracha Radioactiva", Ubicacion("Bernal"))
        mutarachaDespiadada = VectorInsecto("Mutaracha Despiadada", Ubicacion("Ezpeleta"))

        tribuMutarachil = Tribu("Hermandad Mutarachil", mutarachaRadioactiva)
    }

    @Test
    fun testAlCrearUnaTribuSinNombreSeLanzaTribuInvalidaException() {
        assertThrows<TribuInvalidaException> { Tribu("",
            VectorAnimal("Gaviota Mutante", Ubicacion("Avellaneda"))) }
    }

    @Test
    fun testAlCrearUnaTribuSinLiderSeLanzaTribuInvalidaException() {
        val vector: Vector = VectorAnimal("Gaviota Mutante", Ubicacion("Avellaneda"))
        vector.nombre = ""
        assertThrows<TribuInvalidaException> { Tribu("Mutantes Vengadores", vector) }
    }

    @Test
    fun testAlCrearUnaTribuSeCreaConLosAtributosEsperados(){
        assertEquals("Hermandad Mutarachil", tribuMutarachil.nombre)
        assertEquals("Mutaracha Radioactiva", tribuMutarachil.integranteLider)
        assertEquals(1, tribuMutarachil.integrantes.size)
        assertTrue(tribuMutarachil.integrantes.contains(mutarachaRadioactiva.nombre))
    }

    @Test
    fun testAlAgregarUnIntegranteALaTribuSeAgregaALaListaDeIntegrantes(){
        assertEquals(1, tribuMutarachil.integrantes.size)
        tribuMutarachil.agregarIntegrante(mutarachaDespiadada.nombre)
        assertEquals(2, tribuMutarachil.integrantes.size)
        assertTrue(tribuMutarachil.integrantes.contains(mutarachaDespiadada.nombre))
    }

    @Test
    fun testAlAgregarUnIntegranteQueYaEstaSeLanzaException(){
        tribuMutarachil.agregarIntegrante(mutarachaDespiadada.nombre)
        assertThrows<TribuYaTieneIntegranteException> { tribuMutarachil.agregarIntegrante(mutarachaDespiadada.nombre) }
    }

    @Test
    fun testAlIntentarEliminarUnIntegranteQueNoEstabaSeLanzaTribuSinVectorIntegranteException() {
        assertThrows<TribuSinVectorIntegranteException> { tribuMutarachil.eliminarIntegrante(mutarachaDespiadada.nombre) }
    }

    @Test
    fun testAlEliminarUnIntegranteSeVerificaQueYaNoEsParteDeLaTribu() {
        tribuMutarachil.agregarIntegrante(mutarachaDespiadada.nombre)
        tribuMutarachil.eliminarIntegrante(mutarachaDespiadada.nombre)
        assertFalse(tribuMutarachil.integrantes.contains(mutarachaDespiadada.nombre))
    }

    @Test
    fun testAlEliminarElLiderSeVerificaQueYaNoEsParteDeLaTribu() {
        tribuMutarachil.eliminarLider()
        assertTrue(tribuMutarachil.integranteLider.isBlank())
    }
}