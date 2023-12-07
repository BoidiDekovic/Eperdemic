package ar.edu.unq.eperdemic.controller

import DistritoDTO
import ar.edu.unq.eperdemic.services.DistritoService
import org.springframework.web.bind.annotation.*

@RestController
@CrossOrigin
@RequestMapping("/distrito")
class DistritoControllerREST (
    private var distritoService: DistritoService) {

    @PostMapping("/crear")
    fun crearDistrito(@RequestBody distritoDto: DistritoDTO) : DistritoDTO {
        val distrito = distritoService.crearDistrito(distritoDto.aModelo())
        return DistritoDTO.desdeModelo(distrito)
    }

    @PutMapping("/actualizar")
    fun actualizarDistrito(@RequestBody distritoDto: DistritoDTO) {
        val distrito = distritoDto.aModelo()
        distritoService.actualizarDistrito(distrito)
    }

    @GetMapping("/{nombreDeDistrito}")
    fun recuperarDistrito(@PathVariable nombreDeDistrito: String): DistritoDTO {
        return DistritoDTO.desdeModelo(distritoService.recuperarDistritoPorNombre(nombreDeDistrito))
    }

    @GetMapping("/distritoMasEnfermo")
    fun distritoMasEnfermo(): DistritoDTO {
        return DistritoDTO.desdeModelo(distritoService.distritoMasEnfermo())
    }
}