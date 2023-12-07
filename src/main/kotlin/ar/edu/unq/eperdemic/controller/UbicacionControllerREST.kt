package ar.edu.unq.eperdemic.controller

import ar.edu.unq.eperdemic.controller.dto.ErrorDTO
import ar.edu.unq.eperdemic.controller.dto.UbicacionDTO
import ar.edu.unq.eperdemic.modelo.exception.CaminoInvalidoException
import ar.edu.unq.eperdemic.services.UbicacionService
import ar.edu.unq.eperdemic.services.exception.UbicacionConNombreNoExistenteException
import ar.edu.unq.eperdemic.services.exception.UbicacionNoExistenteException
import ar.edu.unq.eperdemic.services.exception.UbicacionYaExistenteException
import ar.edu.unq.eperdemic.services.exception.VectorIDNoExisteException
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@CrossOrigin
@RequestMapping("/ubicacion")
class UbicacionControllerREST(private val ubicacionService: UbicacionService) {
    @PostMapping("/crear")
    fun crearUbicacion(@RequestBody ubicacionDTO: UbicacionDTO): Any {
        return try {
            UbicacionDTO.desdeModelo(ubicacionService.crearUbicacion(ubicacionDTO.aModelo(),ubicacionDTO.punto!!))
        } catch (e: UbicacionYaExistenteException) {
            ResponseEntity.status(400).body(
                ErrorDTO(400, "Ya existe una ubicacion con nombre : ${ubicacionDTO.nombre}").toString()
            )
        }
    }

    @GetMapping("/{ubicacionId}")
    fun recuperarUbicacion(@PathVariable ubicacionId: Long): Any {
        return try {
            UbicacionDTO.desdeModelo(ubicacionService.recuperarUbicacion(ubicacionId))
        } catch (e: UbicacionNoExistenteException) {
            ResponseEntity.status(404).body(
                ErrorDTO(404, "No existe una ubicacion con el id : $ubicacionId").toString()
            )
        }
    }

    @GetMapping("/todasLasUbicaciones")
    fun todasLasUbicaciones(): List<UbicacionDTO> {
        return ubicacionService.recuperarTodasLasUbicaciones().map { ubicacion -> UbicacionDTO.desdeModelo(ubicacion) }
    }

    @PutMapping
    fun actualizarUbicacion(@RequestBody ubicacionDTO: UbicacionDTO): Any {
        return try {
            ubicacionService.actualizarUbicacion(ubicacionDTO.aModelo(), ubicacionDTO.punto)
        } catch (e: UbicacionNoExistenteException) {
            ResponseEntity.status(404).body(
                ErrorDTO(404, "No existe una ubicacion con el id : ${ubicacionDTO.id}").toString()
            )
        } catch (e: UbicacionYaExistenteException) {
            ResponseEntity.status(400).body(
                ErrorDTO(400, "Ya existe una ubicacion con el nombre : ${ubicacionDTO.nombre}").toString()
            )
        }
    }

    @PutMapping("mover/{vectorID}/{ubicacionId}")
    fun mover(@PathVariable vectorID: Long, @PathVariable ubicacionId: Long): Any {
        return try {
            ubicacionService.mover(vectorID, ubicacionId)
        } catch (e: VectorIDNoExisteException) {
            ResponseEntity.status(404).body(
                ErrorDTO(404, "No existe un vector con el id :$vectorID").toString()
            )
        } catch (e: UbicacionNoExistenteException) {
            ResponseEntity.status(404).body(
                ErrorDTO(404, "No existe una ubicacion con el id : $ubicacionId").toString()
            )
        }
    }

    @PutMapping("/expandir/{ubicacionId}")
    fun expandir(@PathVariable ubicacionId: Long): Any {
        return try {
            ubicacionService.expandir(ubicacionId)
        } catch (e: UbicacionNoExistenteException) {
            ResponseEntity.status(404).body(
                ErrorDTO(404, "No existe una ubicacion con el id : $ubicacionId").toString()
            )
        }
    }
    @PutMapping("/conectar/{nombreUbicacion1}/{nombreUbicacion2}/{tipoCamino}")
    fun conectar(@PathVariable nombreUbicacion1: String,
                 @PathVariable nombreUbicacion2: String,
                 @PathVariable tipoCamino: String
    ): Any {
        return try {
            ubicacionService.conectar(nombreUbicacion1, nombreUbicacion2, tipoCamino)
        } catch (e: UbicacionNoExistenteException) {
            ResponseEntity.status(404).body(
                ErrorDTO(404, "No existe la ubicacion dada").toString()
            )
        }catch (e: CaminoInvalidoException) {
            ResponseEntity.status(400).body(
                ErrorDTO(400, "El camino ingresado es incorrecto,los caminos validos son TERRESTRE,MARITIMO o AEREO ").toString()
            )
        }
    }

    @GetMapping("/conectados/{nombreDeUbicacion}")
    fun conectados(@PathVariable nombreDeUbicacion: String): List<UbicacionDTO> {
        return ubicacionService.conectados(nombreDeUbicacion).map { ubicacion -> UbicacionDTO.desdeModelo(ubicacion) }
    }

    @PutMapping("moverPorCaminoMasCorto/{vectorID}/{nombreUbicacion}")
    fun moverPorCaminoMasCorto(@PathVariable vectorID: Long, @PathVariable nombreUbicacion: String): Any {
        return try {
            ubicacionService.moverPorCaminoMasCorto(vectorID, nombreUbicacion)
        } catch (e: VectorIDNoExisteException) {
            ResponseEntity.status(404).body(
                ErrorDTO(404, "No existe un vector con el id :$vectorID").toString()
            )
        } catch (e: UbicacionConNombreNoExistenteException) {
            ResponseEntity.status(404).body(
                ErrorDTO(404, "No existe una ubicacion con el nombre : $nombreUbicacion").toString()
            )
        }
    }

}