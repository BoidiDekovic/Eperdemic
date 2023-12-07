package ar.edu.unq.eperdemic.modelo

import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS

@TestInstance(PER_CLASS)
class ReporteDeContagiosTest {

    private lateinit var reporteDeContagio: ReporteDeContagio
    private lateinit var reporteDeContagio2: ReporteDeContagio

    @BeforeEach
    fun setUp() {
        reporteDeContagio = ReporteDeContagio(0, 0, "-")
        reporteDeContagio2 = ReporteDeContagio(2, 0, "-")
    }

    @Test
    fun testAlInstanciarUnReporteDeContagioTodosLosColaboradoresInternosSonInicializadosCorrectamente() {
        assertEquals(0, reporteDeContagio.cantidadVectoresPresentes)
        assertEquals(0, reporteDeContagio.cantidadVectoresInfectados)
        assertEquals("-", reporteDeContagio.especieMasInfecciosa)
    }

    @Test
    fun testCuandoSeComparanDosObjetosIgualesPorEqualsRetornaTrue() {
        assertTrue(reporteDeContagio.equals(reporteDeContagio))
    }

    @Test
    fun testCuandoSeComparanDosObjetosDistintosPorEqualsRetornaFalse() {
        assertFalse(reporteDeContagio.equals(reporteDeContagio2))
    }

    @Test
    fun testCuandoSeSolicitaElToStringAUnReporteDeContagioRetornaLoEsperado() {
        val stringEsperado =
                "ReporteDeContagio(cantidadVectoresPresentes=0, " +
                "cantidadVectoresInfectados=0, " +
                "especieMasInfecciosa='-')"
        assertEquals(stringEsperado, reporteDeContagio.toString())
    }
}