package ar.edu.unq.eperdemic.modelo.enums

enum class TipoDeVector {
    HUMANO,
    ANIMAL,
    INSECTO;

    override fun toString(): String {
        return when (this) {
            HUMANO -> "VectorHumano"
            ANIMAL -> "VectorAnimal"
            INSECTO -> "VectorInsecto"
        }
    }
}