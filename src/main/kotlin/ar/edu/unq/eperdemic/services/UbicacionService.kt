package ar.edu.unq.eperdemic.services

import ar.edu.unq.eperdemic.modelo.Ubicacion
import ar.edu.unq.eperdemic.modelo.UbicacionNeo4j
import org.springframework.data.mongodb.core.geo.GeoJsonPoint

interface UbicacionService {

    fun crearUbicacion(ubicacion: Ubicacion, punto: GeoJsonPoint) : Ubicacion
    fun actualizarUbicacion(ubicacion: Ubicacion, punto: GeoJsonPoint?)
    fun recuperarUbicacion(ubicacionId: Long) : Ubicacion
    fun recuperarUbicacionDeNeo4j(nombre: String) : UbicacionNeo4j
    fun recuperarTodasLasUbicaciones() : List<Ubicacion>
    fun mover(vectorId: Long, ubicacionId: Long)
    fun expandir(ubicacionId: Long)
    fun recuperarUbicacionConNombre(nombreDeUbicacion: String) : Ubicacion
    fun conectar(nombreDeUbicacion1:String, nombreDeUbicacion2:String, tipoCamino:String)
    fun conectados(nombreDeUbicacion:String): List<Ubicacion>
    fun moverPorCaminoMasCorto(vectorId:Long, nombreDeUbicacion:String)
}