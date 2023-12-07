package ar.edu.unq.eperdemic.modelo

import ar.edu.unq.eperdemic.modelo.exception.Tribu.TribuInvalidaException
import ar.edu.unq.eperdemic.modelo.exception.Tribu.TribuYaTieneIntegranteException
import ar.edu.unq.eperdemic.modelo.exception.Tribu.TribuSinVectorIntegranteException
import ar.edu.unq.eperdemic.modelo.vector.Vector
import ar.edu.unq.eperdemic.modelo.vector.VectorHumano
import com.google.cloud.firestore.annotation.DocumentId
import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
class Tribu(
    var nombre: String,
    vectorLider: Vector) {

    @DocumentId
    var id: String? = null
    var integrantes: ArrayList<String> = arrayListOf(vectorLider.nombre)
    var integranteLider: String = vectorLider.nombre

    constructor() : this("-", VectorHumano("-", Ubicacion("-")))

    init {
        if(nombre.isBlank()) {
            throw TribuInvalidaException("nombre")
        }
        if(integranteLider.isBlank()) {
            throw TribuInvalidaException("integranteLider")
        }
        if(integrantes.isEmpty()) {
            throw TribuInvalidaException("integrantes")
        }
    }

    fun agregarIntegrante(nombre: String) {
        if (this.integrantes.contains(nombre)) {
            throw TribuYaTieneIntegranteException()
        }
        integrantes.add(nombre)
    }

    fun eliminarIntegrante(nombre: String) {
        if (integrantes.contains(nombre)) {
            integrantes.remove(nombre)
        } else {
            throw TribuSinVectorIntegranteException(nombre, this.nombre)
        }
    }

    fun eliminarLider() {
        eliminarIntegrante(integranteLider)
        integranteLider = ""
    }
}