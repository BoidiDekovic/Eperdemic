package ar.edu.unq.eperdemic.modelo.enums

enum class TipoDeMutacion {
    BIOALTERACIONGENETICA,
    SUPRESIONBIOMECANICA,
    PROPULSIONMOTORA,
    ELECTROBRANQUEAS,
    TELETRANSPORTACION;

    override fun toString(): String {
        return when (this) {
            BIOALTERACIONGENETICA -> "BioalteracionGenetica"
            SUPRESIONBIOMECANICA -> "SupresionBiomecanica"
            PROPULSIONMOTORA -> "PropulsionMotora"
            ELECTROBRANQUEAS -> "ElectroBranqueas"
            TELETRANSPORTACION -> "Teletransportacion"
        }
    }
}