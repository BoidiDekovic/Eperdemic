package ar.edu.unq.eperdemic.services.impl.helper

interface RNG {
    fun getRandomNumber(umbralInferior: Int, umbralSuperior: Int): Int
    fun determinarProbabilidad(porcentajeDeExito: Int) : Boolean
}