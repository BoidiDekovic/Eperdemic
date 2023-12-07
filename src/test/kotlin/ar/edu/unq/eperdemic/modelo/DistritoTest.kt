package ar.edu.unq.eperdemic.modelo

import ar.edu.unq.eperdemic.modelo.exception.DistritoAbiertoException
import ar.edu.unq.eperdemic.modelo.exception.DistritoConMenosDeTresCoordenadasException
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.springframework.data.mongodb.core.geo.GeoJsonPoint
import org.springframework.data.mongodb.core.geo.GeoJsonPolygon

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class DistritoTest {

    private lateinit var distritoBernal: Distrito
    private lateinit var coordenadas: List<GeoJsonPoint>
    private lateinit var forma: GeoJsonPolygon
    private lateinit var ubicacionBernal: UbicacionMongoDB

    @BeforeEach
    fun setUp(){
        ubicacionBernal = UbicacionMongoDB("Bernal", GeoJsonPoint(2.0, 2.0))
        coordenadas = listOf(
            GeoJsonPoint(0.0, 0.0),
            GeoJsonPoint(3.0, 6.0),
            GeoJsonPoint(6.0, 1.0),
            GeoJsonPoint(0.0, 0.0),
        )
        forma = GeoJsonPolygon(coordenadas)
        distritoBernal = Distrito("Bernal", forma)
    }

    @Test
    fun testCuandoSeCreaUnDistritoEsteTieneLosValoresEsperados(){
        assertEquals("Bernal", distritoBernal.nombre)
        assertEquals(distritoBernal.forma, forma)
        assertTrue(distritoBernal.ubicaciones.isEmpty())
    }

    @Test
    fun testCuandoSeCreaUnDistritoAbiertoSeLanzaException(){
        val coordenadasIncorrectas = listOf(
            GeoJsonPoint(0.0, 0.0),
            GeoJsonPoint(3.0, 6.0),
            GeoJsonPoint(6.0, 1.0)
        )
        val formaIncorrecta = GeoJsonPolygon(coordenadasIncorrectas)
        assertThrows<DistritoAbiertoException> { Distrito("Bernal", formaIncorrecta) }
    }

    @Test
    fun testCuandoSeCreaUnDistritoConMenosDeTresCoordenadasSeLanzaException(){
        val coordenadasIncorrectas = listOf(
            GeoJsonPoint(0.0, 0.0),
            GeoJsonPoint(3.0, 6.0),
            GeoJsonPoint(0.0, 0.0)
        )
        val formaIncorrecta = GeoJsonPolygon(coordenadasIncorrectas)
        assertThrows<DistritoConMenosDeTresCoordenadasException> { Distrito("Bernal", formaIncorrecta) }
    }

    @Test
    fun testCuandoSeAgregaUnaUbicacionAlDistritoEstaEnLaListaDeUbicaciones(){
        distritoBernal.agregarUbicacion(ubicacionBernal)
        assertEquals(1, distritoBernal.ubicaciones.size)
        assertTrue(distritoBernal.ubicaciones.contains(ubicacionBernal))
    }
}