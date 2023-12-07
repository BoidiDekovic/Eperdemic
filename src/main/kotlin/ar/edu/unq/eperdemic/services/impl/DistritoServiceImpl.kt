package ar.edu.unq.eperdemic.services.impl

import ar.edu.unq.eperdemic.modelo.Distrito
import ar.edu.unq.eperdemic.modelo.exception.NoHayDistritosConUbicacionesInfectadasException
import ar.edu.unq.eperdemic.persistencia.dao.DistritoDAO
import ar.edu.unq.eperdemic.persistencia.dao.UbicacionDAO
import ar.edu.unq.eperdemic.services.DistritoService
import ar.edu.unq.eperdemic.services.exception.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.*
import javax.transaction.Transactional

@Service
@Transactional
class DistritoServiceImpl: DistritoService {

    @Autowired
    private lateinit var distritoDAO: DistritoDAO
    @Autowired
    private lateinit var ubicacionDAO: UbicacionDAO

    override fun crearDistrito(distrito: Distrito): Distrito {
        val distritoDb = this.distritoDAO.findByNombre(distrito.nombre!!)
        if(distritoDb !== null){
            throw DistritoNombreYaExistenteException(distrito.nombre!!)
        }
        if(this.distritoDAO.findSeIntersectanCon(distrito.forma!!).isNotEmpty()){
            throw DistritoIntersectaConOtroException()
        }
        return this.distritoDAO.save(distrito)
    }

    override fun recuperarDistritoPorNombre(nombreDistrito: String): Distrito {
        return this.distritoDAO.findByNombre(nombreDistrito) ?:
        throw DistritoNoExisteException(nombreDistrito)
    }

    override fun actualizarDistrito(distrito: Distrito) {
        distrito.id ?: throw DistritoNoExisteException(distrito.nombre!!)

        val distritoOriginal: Optional<Distrito> = distritoDAO.findById(distrito.id!!)
        val distritoValor: Distrito = distritoOriginal.orElseThrow {
            DistritoNoExisteException(distrito.nombre!!)
        }

        if(distritoDAO.findByNombre(distrito.nombre!!) != null &&
            distritoValor.nombre != distrito.nombre
        ) {
            throw UbicacionConNombreYaExisteException(distrito.nombre!!)
        }

        if( distrito.forma != distritoValor.forma
            &&
            this.distritoDAO.findSeIntersectanCon(distrito.forma!!).isNotEmpty()){
            throw DistritoIntersectaConOtroException()
        }

        this.distritoDAO.save(distrito)
    }

    override fun deleteAll() {
        distritoDAO.deleteAll()
    }

    override fun distritoMasEnfermo(): Distrito {
       val ubicacionesInfectadas = ubicacionDAO.ubicacionesInfectadas()

       val distritoMasInfectado = distritoDAO.distritoMasInfectado(ubicacionesInfectadas) ?: throw NoHayDistritosConUbicacionesInfectadasException()

       return  distritoDAO.findByNombre(distritoMasInfectado)!!
    }
}
