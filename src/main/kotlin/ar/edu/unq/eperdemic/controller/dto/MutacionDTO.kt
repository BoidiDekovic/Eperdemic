package ar.edu.unq.eperdemic.controller.dto

import ar.edu.unq.eperdemic.modelo.enums.TipoDeMutacion
import ar.edu.unq.eperdemic.modelo.mutacion.*

open class MutacionDTO() {
    var id: Long? = null
    var tipoDeMutacion: TipoDeMutacion? = null
    var nombreEspecie: String? = null
    var potencia: Int = 0
    var tipoDeVector: String? = null

    companion object {
        fun desdeModelo(bioalteracionGenetica: BioalteracionGenetica): MutacionDTO {
            val dto = MutacionDTO()
            dto.id = bioalteracionGenetica.id
            dto.tipoDeMutacion = TipoDeMutacion.BIOALTERACIONGENETICA
            dto.tipoDeVector = bioalteracionGenetica.tipoVector
            dto.nombreEspecie = bioalteracionGenetica.nombreEspecie
            return dto
        }

        fun desdeModelo(supresionBiomecanica: SupresionBiomecanica): MutacionDTO {
            val dto = MutacionDTO()
            dto.id = supresionBiomecanica.id
            dto.tipoDeMutacion = TipoDeMutacion.SUPRESIONBIOMECANICA
            dto.nombreEspecie = supresionBiomecanica.nombreEspecie
            dto.potencia = supresionBiomecanica.potencia
            return dto
        }

        fun desdeModelo(propulsionMotora: PropulsionMotora): MutacionDTO {
            val dto = MutacionDTO()
            dto.id = propulsionMotora.id
            dto.tipoDeMutacion = TipoDeMutacion.PROPULSIONMOTORA
            dto.nombreEspecie = propulsionMotora.nombreEspecie
            return dto
        }

        fun desdeModelo(electroBranqueas: ElectroBranqueas): MutacionDTO {
            val dto = MutacionDTO()
            dto.id = electroBranqueas.id
            dto.tipoDeMutacion = TipoDeMutacion.ELECTROBRANQUEAS
            dto.nombreEspecie = electroBranqueas.nombreEspecie
            return dto
        }

        fun desdeModelo(teletransportacion: Teletransportacion): MutacionDTO {
            val dto = MutacionDTO()
            dto.id = teletransportacion.id
            dto.tipoDeMutacion = TipoDeMutacion.TELETRANSPORTACION
            dto.nombreEspecie = teletransportacion.nombreEspecie
            return dto
        }
    }

    fun aModelo(): Mutacion {
        val mutacion : Mutacion = when (this.tipoDeMutacion.toString()) {
            "BioalteracionGenetica" -> BioalteracionGenetica(this.tipoDeVector!!, this.nombreEspecie!!)
            "SupresionBiomecanica"  -> SupresionBiomecanica(this.potencia, this.nombreEspecie!!)
            "PropulsionMotora"      -> PropulsionMotora(this.nombreEspecie!!)
            "Teletransportacion"    -> Teletransportacion(this.nombreEspecie!!)
            else                    -> ElectroBranqueas(this.nombreEspecie!!)
        }

        mutacion.id = this.id
        return mutacion
    }
}