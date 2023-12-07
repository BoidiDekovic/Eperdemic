package ar.edu.unq.eperdemic.services.impl.helper

import org.springframework.stereotype.Component
import kotlin.random.Random

@Component
class RNGImpl(): RNG {
    override fun getRandomNumber(umbralInferior: Int, umbralSuperior: Int): Int {
        return Random.nextInt(umbralInferior, umbralSuperior + 1)
    }

    override fun determinarProbabilidad(porcentajeDeExito: Int) : Boolean {
        val nroProbabilidad = this.getRandomNumber(1 , 100)
        return nroProbabilidad <= porcentajeDeExito
    }
}