package ar.edu.unq.eperdemic.controller

import ar.edu.unq.eperdemic.controller.dto.ErrorDTO
import ar.edu.unq.eperdemic.controller.dto.TribuDTO
import ar.edu.unq.eperdemic.services.TribuService
import ar.edu.unq.eperdemic.services.exception.Tribu.TribuIdNoExisteException
import ar.edu.unq.eperdemic.services.exception.Tribu.TribuNombreNoExisteException
import ar.edu.unq.eperdemic.services.exception.Tribu.TribuNombreYaExistenteException
import ar.edu.unq.eperdemic.services.exception.VectorConNombreNoExisteException
import ar.edu.unq.eperdemic.services.exception.VectorNoPertenecienteALaTribuException
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@CrossOrigin
@RequestMapping("/tribu")
class TribuControllerREST (private var tribuService: TribuService) {
    @PostMapping("/crear")
    fun crearTribu(@RequestBody tribuDTO: TribuDTO): Any {
        return try {
            TribuDTO.desdeModelo(tribuService.crearTribu(tribuDTO.aModelo()))
        } catch (e: TribuNombreYaExistenteException) {
            ResponseEntity.status(400).body(
                ErrorDTO(400, "Ya existe una tribu con nombre : ${tribuDTO.nombre}").toString()
            )
        }
    }

    @GetMapping("/id/{idTribu}")
    fun recuperarTribu(@PathVariable idTribu: String): Any {
        return try {
            TribuDTO.desdeModelo(tribuService.recuperarTribu(idTribu))
        } catch (e: TribuIdNoExisteException) {
            ResponseEntity.status(404).body(
                ErrorDTO(404, "No existe una tribu con el id : $idTribu").toString()
            )
        }
    }

    @GetMapping("/nombre/{nombreTribu}")
    fun recuperarTribuPorNombre(@PathVariable nombreTribu: String): Any {
        return try {
            TribuDTO.desdeModelo(tribuService.recuperarTribuPorNombre(nombreTribu))
        } catch (e: TribuNombreNoExisteException) {
            ResponseEntity.status(404).body(
                ErrorDTO(404, "No existe una tribu con el nombre : $nombreTribu").toString()
            )
        }
    }

    @PutMapping("/actualizar/{nombreTribu}")
    fun actualizarTribu(@RequestBody tribuDTO: TribuDTO, @PathVariable nombreTribu: String) {
        tribuService.actualizarTribu( tribuDTO.aModelo(), nombreTribu)

}
    @DeleteMapping("/{nombreTribu}")
    fun eliminarTribu(@PathVariable nombreTribu: String) : Any{
        return try {
            tribuService.eliminarTribu(nombreTribu)
        }catch (e: TribuNombreNoExisteException) {
            ResponseEntity.status(404).body(
                ErrorDTO(404, "No existe una tribu con el nombre : $nombreTribu").toString()
            )
        }
    }
    @GetMapping("/todasLasTribus")
    fun todasLasUbicaciones(): List<TribuDTO> {
        return tribuService.recuperarTodasLasTribus().map { tribu -> TribuDTO.desdeModelo(tribu) }
    }

    @PutMapping("/pelearEntreTribus/{nombreTribu}/{nombreOtraTribu}")
    fun peleasEntreTribus(@PathVariable nombreTribu:String , @PathVariable nombreOtraTribu:String ) : Any {
        return try {
            tribuService.pelearEntreTribus(nombreOtraTribu,nombreOtraTribu)
        } catch (e: TribuNombreNoExisteException){
            ResponseEntity.status(404).body(
                ErrorDTO(404, "No existe el nombre de la tribu dada").toString()
            )
        }
    }

    @PutMapping("/pelearEntreIntegrantes/{nombreTribu}/{nombreVector}/{nombreDelOtroVector}")
    fun pelearEntreIntegrantes(@PathVariable nombreTribu: String, @PathVariable nombreVector: String , @PathVariable nombreDelOtroVector: String) : Any {
        return try {
            tribuService.pelearEntreIntegrantes(nombreTribu, nombreVector, nombreDelOtroVector)
        } catch (e: TribuNombreNoExisteException) {
            ResponseEntity.status(404).body(
                ErrorDTO(404, "No existe el nombre de la tribu dada").toString()
            )
        } catch (e: VectorNoPertenecienteALaTribuException) {
            ResponseEntity.status(404).body(
                ErrorDTO(404, "No existe una vector con el nombre dado").toString()
            )

        }
    }

}