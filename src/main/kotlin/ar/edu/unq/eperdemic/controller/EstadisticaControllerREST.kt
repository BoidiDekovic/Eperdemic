package ar.edu.unq.eperdemic.controller

import ar.edu.unq.eperdemic.controller.dto.ErrorDTO
import ar.edu.unq.eperdemic.controller.dto.EspecieDTO
import ar.edu.unq.eperdemic.controller.dto.ReporteDeContagioDTO
import ar.edu.unq.eperdemic.services.EstadisticaService
import ar.edu.unq.eperdemic.services.exception.EstadisticaNoHayEspeciesException
import ar.edu.unq.eperdemic.services.exception.UbicacionNoExistenteException
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@CrossOrigin
@RequestMapping("/estadistica")
class EstadisticaControllerREST(private val estadisticaService: EstadisticaService) {

    @GetMapping("/lider")
    fun especieLider(): Any {
        return try {
            EspecieDTO.desdeModelo(estadisticaService.especieLider())
        } catch (e: EstadisticaNoHayEspeciesException) {
            ResponseEntity.status(404).body(
                ErrorDTO(404, "No hay ninguna Especie.").toString())
        }
    }

    @GetMapping("/lideres")
    fun lideres() = estadisticaService.lideres().map { lider -> EspecieDTO.desdeModelo(lider) }

    @GetMapping("/reporte/{ubicacionNombre}")
    fun reporteDeContagio(@PathVariable ubicacionNombre: String): Any {
        return try {
            ReporteDeContagioDTO.desdeModelo(
                estadisticaService.reporteDeContagios(ubicacionNombre))
        } catch (e: UbicacionNoExistenteException) {
            ResponseEntity.status(404).body(
                ErrorDTO(404, "No existe una Ubicacion con el Nombre: $ubicacionNombre.").toString())
        }
    }
}