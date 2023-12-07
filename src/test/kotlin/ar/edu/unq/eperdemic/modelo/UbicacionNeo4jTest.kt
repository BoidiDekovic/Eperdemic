package ar.edu.unq.eperdemic.modelo

import ar.edu.unq.eperdemic.modelo.exception.CaminoInvalidoException
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class UbicacionNeo4jTest {
    private lateinit var ubicacionUNQ: UbicacionNeo4j
    private lateinit var ubicacionBurgerKing: UbicacionNeo4j

    @BeforeEach
    fun setUp(){
        ubicacionUNQ = UbicacionNeo4j("UNQ")
        ubicacionBurgerKing = UbicacionNeo4j("Burger King")
    }

    @Test
    fun testCuandoDosUbicacionesSeConectanConUnaRutaMaritima(){
        ubicacionUNQ.conectar(ubicacionBurgerKing,"MARITIMO")
        assertTrue(ubicacionUNQ.rutasMaritimas.contains(ubicacionBurgerKing))
        assertFalse(ubicacionBurgerKing.rutasMaritimas.contains(ubicacionUNQ))
    }
    @Test
    fun testCuandoDosUbicacionesSeConectanConUnaRutaTerrestre(){
        ubicacionUNQ.conectar(ubicacionBurgerKing,"TERRESTRE")
        assertTrue(ubicacionUNQ.rutasTerrestres.contains(ubicacionBurgerKing))
        assertFalse(ubicacionBurgerKing.rutasTerrestres.contains(ubicacionUNQ))
    }
    @Test
    fun testCuandoDosUbicacionesSeConectanConUnaRutaArea(){
        ubicacionUNQ.conectar(ubicacionBurgerKing,"AEREO")
        assertTrue(ubicacionUNQ.rutasAereas.contains(ubicacionBurgerKing))
        assertFalse(ubicacionBurgerKing.rutasAereas.contains(ubicacionUNQ))
    }
    @Test
    fun testCuandoConectoDosUbicacionesConMasDeUnCamino(){
        ubicacionUNQ.conectar(ubicacionBurgerKing,"MARITIMO")
        ubicacionUNQ.conectar(ubicacionBurgerKing,"TERRESTRE")
        ubicacionBurgerKing.conectar(ubicacionUNQ,"AEREO")
        assertTrue(ubicacionUNQ.rutasMaritimas.contains(ubicacionBurgerKing))
        assertTrue(ubicacionUNQ.rutasTerrestres.contains(ubicacionBurgerKing))
        assertTrue(ubicacionBurgerKing.rutasAereas.contains(ubicacionUNQ))

    }

    @Test
    fun testCuandoQueresConectarConUnTipoDeCaminoIncorrectoLanzaCaminoIncorrectoException(){
        assertThrows<CaminoInvalidoException> { ubicacionUNQ.conectar(ubicacionBurgerKing,"SUBTERRANEO")}
    }

}