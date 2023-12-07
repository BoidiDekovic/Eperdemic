package ar.edu.unq.eperdemic.controller.exception

import ar.edu.unq.eperdemic.controller.dto.ErrorDTO
import ar.edu.unq.eperdemic.modelo.exception.VectorNoPuedeSerContagiadoException
import ar.edu.unq.eperdemic.services.exception.*
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

@ControllerAdvice
class ExceptionControllerAdvice {

    @ExceptionHandler(EspecieIDNoExisteException::class)
    fun handleEspecieIDNoExisteException(ex: EspecieIDNoExisteException): ResponseEntity<String> {

        return ResponseEntity.status(404).body(
            ErrorDTO(404, ex.message).toString())
    }

    @ExceptionHandler(PatogenoConTipoNoExistenteException::class)
    fun handlePatogenoConTipoNoExistenteException(ex: PatogenoConTipoNoExistenteException): ResponseEntity<String> {

        return ResponseEntity.status(404).body(
            ErrorDTO(404, ex.message).toString())
    }
    @ExceptionHandler(VectorIDNoExisteException::class)
    fun handleVectorIDNoExisteException(ex: VectorIDNoExisteException): ResponseEntity<String> {

        return ResponseEntity.status(404).body(
            ErrorDTO(404, ex.message).toString())
    }

    @ExceptionHandler(VectorNombreYaExistenteException::class)
    fun handleVectorNombreYaExistenteException(ex: VectorNombreYaExistenteException): ResponseEntity<String> {

        return ResponseEntity.status(400).body(
            ErrorDTO(400, ex.message).toString())
    }

    @ExceptionHandler(VectorYaExistenteException::class)
    fun handleVectorYaExistenteException(ex: VectorYaExistenteException): ResponseEntity<String> {

        return ResponseEntity.status(400).body(
            ErrorDTO(400, ex.message).toString())
    }

    @ExceptionHandler(VectorNoPuedeSerContagiadoException::class)
    fun handleVectorNoPuedeSerContagiadoException(ex: VectorNoPuedeSerContagiadoException): ResponseEntity<String> {

        return ResponseEntity.status(400).body(
            ErrorDTO(400, ex.message).toString())
    }

    @ExceptionHandler(UbicacionConNombreNoExistenteException::class)
    fun handleUbicacionConNombreNoExistenteException(ex: UbicacionConNombreNoExistenteException): ResponseEntity<String> {

        return ResponseEntity.status(404).body(
            ErrorDTO(404, ex.message).toString())
    }
}