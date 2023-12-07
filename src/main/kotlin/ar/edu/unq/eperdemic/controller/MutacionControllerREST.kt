package ar.edu.unq.eperdemic.controller

import ar.edu.unq.eperdemic.controller.dto.*
import ar.edu.unq.eperdemic.controller.exception.MutacionInvalidaException
import ar.edu.unq.eperdemic.modelo.exception.MutacionNombreEspecieException
import ar.edu.unq.eperdemic.services.EspecieService
import ar.edu.unq.eperdemic.services.MutacionService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@CrossOrigin
@RequestMapping("/mutacion")
class MutacionControllerREST(private val mutacionService: MutacionService , private val especieService: EspecieService) {
    @PostMapping("/agregarMutacion/{especieId}")
    fun agregarMutacion(
        @PathVariable especieId: Long,
        @RequestBody mutacionDTO: MutacionDTO
    ): Any {
        return try {

            mutacionService.agregarMutacion(especieId, mutacionDTO.aModelo())
        } catch (e: MutacionInvalidaException) {
            ResponseEntity.status(400).body("Tipo de mutación no válido")
        } catch (e: MutacionNombreEspecieException) {
            ResponseEntity.status(400).body("El nombre de la especie del id  no coincide con la especie de la mutacion")
        }
    }



}
