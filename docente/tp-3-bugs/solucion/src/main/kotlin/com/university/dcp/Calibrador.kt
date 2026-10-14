package com.university.dcp

object Calibrador {

    fun offsetCalibracion(idSensor: String): Double = when (idSensor) {
        "S-01" -> 0.3
        "S-02" -> -0.5
        "S-04" -> 1.2
        else -> 0.0
    }

    fun calibrar(idSensor: String, valor: Double): Double {
        return valor + offsetCalibracion(idSensor)
    }
}
