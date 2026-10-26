package com.university.dcp.smarthome

interface Sensor {
    val id: String
    fun detectar(): Boolean
}

class SensorPresencia(
    override val id: String,
    var hayMovimiento: Boolean = false,
) : Sensor {
    init {
        require(id.isNotBlank()) { "El id del sensor no puede estar vacío" }
    }

    override fun detectar(): Boolean = hayMovimiento
}

class SensorTemperatura(
    override val id: String,
    var temperaturaActual: Double = 20.0,
    val umbral: Double,
    val dispararSiMayor: Boolean = true,
) : Sensor {
    init {
        require(id.isNotBlank()) { "El id del sensor no puede estar vacío" }
    }

    override fun detectar(): Boolean =
        if (dispararSiMayor) temperaturaActual > umbral else temperaturaActual < umbral
}

class ReglaAutomatizacion(
    val nombre: String,
    val sensor: Sensor,
    val accion: () -> Unit,
) {
    init {
        require(nombre.isNotBlank()) { "El nombre de la regla no puede estar vacío" }
    }

    fun evaluar(): Boolean {
        if (sensor.detectar()) {
            accion()
            return true
        }
        return false
    }
}
