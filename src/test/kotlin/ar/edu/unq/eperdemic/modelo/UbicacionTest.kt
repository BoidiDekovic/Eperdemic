package ar.edu.unq.eperdemic.modelo

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS

@TestInstance(PER_CLASS)
class UbicacionTest {

    lateinit var ubicacionUNQ: Ubicacion

    @BeforeEach
    fun setUp(){
        ubicacionUNQ = Ubicacion("UNQ")
    }
    @Test
    fun testCuandoHagoToStringRetornaElNombreDeLaUbicacion(){
        assertEquals("UNQ", ubicacionUNQ.toString())
    }
    @Test
    fun testCuandoSeComparanDosObjetosIgualesConEqualsRetornaVerdadero(){
        assertTrue(ubicacionUNQ.equals(ubicacionUNQ))
    }

}