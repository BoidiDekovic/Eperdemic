package ar.edu.unq.eperdemic.modelo

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.data.mongodb.core.geo.GeoJsonPoint

class UbicacionMongDBTest {

    private lateinit var ubicacionUNQ: UbicacionMongoDB
    private lateinit var ubicacionPlazaDelMaestro: UbicacionMongoDB
    private lateinit var punto : GeoJsonPoint

    @BeforeEach
    fun setUp(){
        punto = GeoJsonPoint(12.1,20.20)

        ubicacionUNQ = UbicacionMongoDB("Universidad Nacional de Quilmes", punto)
        ubicacionPlazaDelMaestro = UbicacionMongoDB("Plaza Del Maestro", punto)
    }

    @Test
    fun testCuandoCreoUnaUbicacionMongoDBTieneElNombreCorrecto() {
        assertEquals("Plaza Del Maestro", ubicacionPlazaDelMaestro.nombre)
    }

    @Test
    fun testCuandoUnaUbicacionMongoDBSeCreaTieneLaLongitudCorrecta() {
        assertEquals(20.20, ubicacionUNQ.longitud())
    }

    @Test
    fun testCuandoUnaUbicacionMongoDBSeCreaTieneLaLatitudCorrecta() {
        assertEquals(12.1, ubicacionUNQ.latitud())
    }

    @Test
    fun testCuandoSeComparan2UbicacionMongoDBIgualesPorEqualsRetornaTrue() {
        assertTrue(ubicacionUNQ.equals(ubicacionUNQ))
    }

    @Test
    fun testCuandoSeComparan2UbicacionMongoDBDistintasPorEqualsRetornaFalse() {
        assertFalse(ubicacionUNQ.equals(ubicacionPlazaDelMaestro))
    }
}