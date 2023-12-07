package ar.edu.unq.eperdemic.modelo

import ar.edu.unq.eperdemic.modelo.exception.PatogenoAtributoFueraDeRangoException
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class PatogenoTest {
    lateinit var patogeno : Patogeno

    @BeforeEach
    fun setUp(){
        patogeno = Patogeno()
    }

    @Test
    fun testAlCrearPatogenoSinParametrosSusCapacidadesYDefensaEstanEn1(){
        assertEquals(1,patogeno.capContagioPersona)
        assertEquals(1,patogeno.capContagioAnimal)
        assertEquals(1,patogeno.capContagioInsecto)
        assertEquals(1,patogeno.defensa)
        assertEquals(1,patogeno.capBiomecanizacion)
    }

    @Test
    fun testAlCrearPatogenoConParametrosSusCapacidadesYDefensaSonLasEsperadas(){
        patogeno = Patogeno("Virus", 12,
            2, 60, 39, 2)
        assertEquals(12,patogeno.capContagioPersona)
        assertEquals(2,patogeno.capContagioAnimal)
        assertEquals(60,patogeno.capContagioInsecto)
        assertEquals(39,patogeno.defensa)
        assertEquals(2,patogeno.capBiomecanizacion)
    }

    @Test
    fun testAlCrearPatogenoConCapacidadDeContagioPorPersonaMenorA1SeLanzaExcepcion(){
        val e = assertThrows<PatogenoAtributoFueraDeRangoException> {
            Patogeno("Virus", -12,
                2, 60, 39, 1) }
        assertEquals("El valor del atributo capContagioPorPersona debe estar entre 1 y 100", e.message)
    }

    @Test
    fun testAlCrearUnPatogenoConCapacidadDeContagioAPersonasMayorA100SeLanzaExcepcion(){
        val e = assertThrows<PatogenoAtributoFueraDeRangoException> {
            Patogeno("Virus", 101,
                2, 60, 39, 1) }
        assertEquals("El valor del atributo capContagioPorPersona debe estar entre 1 y 100", e.message)
    }
    @Test
    fun testAlCrearUnPatogenoConCapacidadDeContagioAAnimalesMenorA1SeLanzaExcepcion(){
        val e = assertThrows<PatogenoAtributoFueraDeRangoException> {
            Patogeno("Virus", 50,
                -1, 60, 39, 1) }
        assertEquals("El valor del atributo capContagioAnimal debe estar entre 1 y 100", e.message)
    }
    @Test
    fun testAlCrearUnPatogenoConCapacidadDeContagioAAnimalesMayorA100SeLanzaExcepcion(){
        val e = assertThrows<PatogenoAtributoFueraDeRangoException> {
            Patogeno("Virus", 50,
                101, 60, 39, 1) }
        assertEquals("El valor del atributo capContagioAnimal debe estar entre 1 y 100", e.message)
    }
    @Test
    fun testAlCrearUnPatogenoConCapacidadDeContagioAInsectosMenorA1SeLanzaExcepcion(){
        val e = assertThrows<PatogenoAtributoFueraDeRangoException> {
            Patogeno("Virus", 50,
                11, -1, 39, 1) }
        assertEquals("El valor del atributo capContagioInsecto debe estar entre 1 y 100", e.message)
    }

    @Test
    fun testAlCrearUnPatogenoConCapacidadDeContagioAInsectosMayorA100SeLanzaExcepcion(){
        val e = assertThrows<PatogenoAtributoFueraDeRangoException> {
            Patogeno("Virus", 50,
                11, 101, 39, 1) }
        assertEquals("El valor del atributo capContagioInsecto debe estar entre 1 y 100", e.message)
    }

    @Test
    fun testAlCrearUnPatogenoConDefensaMenorA1SeLanzaExcepcion(){
        val e = assertThrows<PatogenoAtributoFueraDeRangoException> {
            Patogeno("Virus", 50,
                11, 2, -1, 1) }
        assertEquals("El valor del atributo defensa debe estar entre 1 y 100", e.message)
    }
    @Test
    fun testAlCrearUnPatogenoConDefensaMayorA100SeLanzaExcepcion(){
        val e = assertThrows<PatogenoAtributoFueraDeRangoException> {
            Patogeno("Virus", 50,
                11, 2, 101, 1) }
        assertEquals("El valor del atributo defensa debe estar entre 1 y 100", e.message)
    }

    @Test
    fun testAlCrearUnPatogenoConCapacidadDeBiomecanizacionMenorA1SeLanzaExcepcion(){
        val e = assertThrows<PatogenoAtributoFueraDeRangoException> {
            Patogeno("Virus", 50,
                11, 12, 39, -1) }
        assertEquals("El valor del atributo capBiomecanizacion debe estar entre 1 y 100", e.message)
    }
    @Test
    fun testAlCrearUnPatogenoConCapacidadDeBiomecanizacionMayorA100SeLanzaExcepcion(){
        val e = assertThrows<PatogenoAtributoFueraDeRangoException> {
            Patogeno("Virus", 50,
                11, 12, 39, 101) }
        assertEquals("El valor del atributo capBiomecanizacion debe estar entre 1 y 100", e.message)
    }

    @Test
    fun testAlAgregarUnaEspecieAlPatogenoLaEspecieCreadaEsLaEsperada(){
        val especie = patogeno.crearEspecie("Covid", "China")
        assertEquals("Covid", especie.nombre)
        assertEquals("China", especie.paisDeOrigen)
    }

    @Test
    fun testAlCrearUnaEspecieAlPatogenoSuCantidadDeEspeciesAumenta(){
        patogeno.cantidadDeEspecies = 4
        assertEquals(4, patogeno.cantidadDeEspecies)
        patogeno.crearEspecie("Covid", "China")
        assertEquals(5, patogeno.cantidadDeEspecies)
    }


}