package ar.edu.unq.eperdemic.controller

import ar.edu.unq.eperdemic.controller.dto.ErrorDTO
import ar.edu.unq.eperdemic.controller.dto.EspecieDTO
import ar.edu.unq.eperdemic.controller.dto.PatogenoDTO
import ar.edu.unq.eperdemic.services.PatogenoService
import ar.edu.unq.eperdemic.services.exception.*
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
@RequestMapping("/patogeno")
class PatogenoControllerREST(
    private val patogenoService: PatogenoService
) {

    @PostMapping
    fun crearPatogeno(@RequestBody patogenoDTO : PatogenoDTO): Any {
        return try {
            val patogeno = patogenoService.crearPatogeno(patogenoDTO.aModelo())
            PatogenoDTO.desdeModelo(patogeno)
        } catch (e : TipoDePatogenoExistenteException){
            ResponseEntity.status(400).body(
                ErrorDTO(400, "Ya existe un Patogeo con el tipo: ${patogenoDTO.tipo}").toString())
        }
    }

    @PutMapping
    fun actualizarPatogeno(@RequestBody patogenoDTO : PatogenoDTO) : Any {
        return try {
            patogenoService.actualizarPatogeno(patogenoDTO.aModelo())
            patogenoDTO
        } catch (e : PatogenoIDNoExisteException){
            ResponseEntity.status(404).body(
                ErrorDTO(404, "No existe un Patogeo con el Id: ${patogenoDTO.id}").toString())
        }
    }

    @GetMapping ("/{patogenoId}")
    fun recuperarPatogeno(@PathVariable patogenoId : Long) : Any {
        return try {
            PatogenoDTO.desdeModelo(patogenoService.recuperarPatogeno(patogenoId))
        } catch (e: PatogenoIDNoExisteException){
            ResponseEntity.status(404).body(
                ErrorDTO(404, "No existe un Patogeo con el Id: ${patogenoId}").toString())
        }
    }

    @GetMapping("/todos")
    fun recuperarTodosLosPatogenos() = patogenoService.recuperarTodosLosPatogenos().map { PatogenoDTO.desdeModelo(it) }

    @PostMapping("/agregarEspecie/{patogenoId}/{ubicacionId}")
    fun agregarEspecie(@RequestBody especieDTO : EspecieDTO,
                       @PathVariable patogenoId : Long,
                       @PathVariable ubicacionId : Long) : Any {
           return try {
               val especie =  patogenoService.agregarEspecie(patogenoId, especieDTO.nombre!!, ubicacionId)
               EspecieDTO.desdeModelo(especie)
           } catch (e: PatogenoIDNoExisteException){
               ResponseEntity.status(404).body(
                   ErrorDTO(404, "No existe un Patogeno con el Id: ${patogenoId}").toString())
           } catch (e: EspecieNombreYaExistenteException){
               ResponseEntity.status(400).body(
                   ErrorDTO(400, "Ya existe una Especie con el Nombre: ${especieDTO.nombre}").toString())
           } catch (e: UbicacionNoExistenteException){
               ResponseEntity.status(404).body(
                   ErrorDTO(404, "No existe una Ubicacion con el Id: ${ubicacionId}").toString())
           }
    }

    @GetMapping("/especies/{patogenoId}")
    fun especiesDePatogeno(@PathVariable patogenoId: Long) : Any {
        return try {
            patogenoService.especiesDePatogeno(patogenoId).map { EspecieDTO.desdeModelo(it) }
        } catch (e: PatogenoIDNoExisteException){
            ResponseEntity.status(404).body(
                ErrorDTO(404, "No existe un Patogeno con el Id: ${patogenoId}").toString())
        }
    }

    @GetMapping("/esPandemia/{especieId}")
    fun esPandemia(@PathVariable especieId : Long) : Any {
        return try {
            patogenoService.esPandemia(especieId)
        } catch (e: EspecieIDNoExisteException){
            ResponseEntity.status(404).body(
                ErrorDTO(404, "No existe la especie con el Id: ${especieId}").toString())
        }
    }
}