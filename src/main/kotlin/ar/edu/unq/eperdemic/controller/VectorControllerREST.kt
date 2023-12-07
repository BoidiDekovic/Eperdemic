package ar.edu.unq.eperdemic.controller

import ar.edu.unq.eperdemic.controller.dto.*
import ar.edu.unq.eperdemic.services.UbicacionService
import ar.edu.unq.eperdemic.services.VectorService
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
@RequestMapping("/vector")
class VectorControllerREST(
    private val vectorService: VectorService,
    private val ubicacionService: UbicacionService) {

    @PostMapping("/crear")
    fun crearVector(@RequestBody vectorDto: VectorDTO): VectorDTO {
        val ubicacion = ubicacionService.recuperarUbicacionConNombre(vectorDto.ubicacion!!.nombre!!)
        val vector = vectorService.crearVector(vectorDto.aModelo(ubicacion))
        return VectorDTO.desdeModelo(vector)

    }

    @PutMapping("/actualizar")
    fun actualizarVector(@RequestBody vectorDto: VectorDTO) {
        val ubicacion = ubicacionService.recuperarUbicacionConNombre(vectorDto.ubicacion!!.nombre!!)
        val vector = vectorDto.aModelo(ubicacion)
        vectorService.actualizarVector(vector)
    }

    @GetMapping("/{vectorId}")
    fun recuperarVector(@PathVariable vectorId: Long): VectorDTO {
        return VectorDTO.desdeModelo(vectorService.recuperarVector(vectorId))
    }

    @GetMapping("/recuperarTodos")
    fun recuperarTodos(): List<VectorDTO> {
        return vectorService.recuperarTodosLosVectores().map { vector -> VectorDTO.desdeModelo(vector) }
    }

    @GetMapping("/enfermedades/{vectorId}")
    fun enfermedades(@PathVariable vectorId: Long) : List<EspecieDTO> {
        return vectorService.enfermedadesDelVector(vectorId).map { especie -> EspecieDTO.desdeModelo(especie) }
    }

    @PutMapping("/infectar/{vectorId}/{especieId}")
    fun infectar(@PathVariable vectorId: Long, @PathVariable especieId: Long) {
        vectorService.infectarVector(vectorId, especieId)
    }

    @PutMapping("/contagiar/{vectorId1}/{vectorId2}")
    fun contagiar(@PathVariable vectorId1: Long, @PathVariable vectorId2: Long) {
         vectorService.contagiar(vectorId1, vectorId2)
    }
}