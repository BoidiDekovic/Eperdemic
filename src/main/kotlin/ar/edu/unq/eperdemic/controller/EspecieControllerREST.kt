package ar.edu.unq.eperdemic.controller

import ar.edu.unq.eperdemic.controller.dto.EspecieDTO
import ar.edu.unq.eperdemic.services.EspecieService
import org.springframework.web.bind.annotation.*

@RestController
@CrossOrigin
@RequestMapping("/especie")
class EspecieControllerREST(
    private val especieService: EspecieService) {

    @PutMapping("/actualizar")
    fun actualizarEspecie(@RequestBody especieDto: EspecieDTO) {
        val especie = especieDto.aModelo()
        especieService.actualizarEspecie(especie)
    }

    @GetMapping("/{especieId}")
    fun recuperarEspecie(@PathVariable especieId: Long): EspecieDTO {
        return EspecieDTO.desdeModelo(especieService.recuperarEspecie(especieId))
    }

    @GetMapping("/recuperarTodos")
    fun recuperarTodos(): List<EspecieDTO> {
        return especieService.recuperarTodasLasEspecies().map { especie -> EspecieDTO.desdeModelo(especie) }
    }

    @GetMapping("/{especieId}/infectados")
    fun cantidadDeInfectadosPorLaEspecie(@PathVariable especieId: Long): Int {
        return especieService.cantidadDeInfectadosPorLaEspecie(especieId)
    }
}