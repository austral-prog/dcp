package com.university.dcp.smarthome

enum class ModoClima {
    FRIO,
    CALOR,
    VENTILACION,
}

class Termostato(
    override val id: String,
    override val nombre: String,
    temperaturaInicial: Double = 24.0,
    val potenciaCompresorWatts: Double = 1200.0,
    val potenciaVentiladorWatts: Double = 80.0,
    val histeresis: Double = 0.5,
) : Dispositivo {

    override var encendido: Boolean = false
        private set

    var temperaturaActual: Double = temperaturaInicial
        private set

    var temperaturaDeseada: Double = 24.0
        private set

    var modo: ModoClima = ModoClima.FRIO
        private set

    init {
        require(id.isNotBlank()) { "El id no puede estar vacío" }
        require(nombre.isNotBlank()) { "El nombre no puede estar vacío" }
        require(potenciaCompresorWatts > 0) { "La potencia del compresor debe ser positiva" }
        require(potenciaVentiladorWatts > 0) { "La potencia del ventilador debe ser positiva" }
        require(histeresis >= 0) { "La histéresis no puede ser negativa" }
    }

    override fun encender() {
        encendido = true
    }

    override fun apagar() {
        encendido = false
    }

    fun programar(nuevoModo: ModoClima, nuevaTemperaturaDeseada: Double) {
        require(nuevaTemperaturaDeseada in 16.0..32.0) {
            "Temperatura deseada fuera de rango permitido (16°C a 32°C): $nuevaTemperaturaDeseada"
        }
        modo = nuevoModo
        temperaturaDeseada = nuevaTemperaturaDeseada
    }

    fun actualizarTemperaturaAmbiente(nuevaTemperatura: Double) {
        temperaturaActual = nuevaTemperatura
    }

    fun compresorActivo(): Boolean {
        if (!encendido) return false
        return when (modo) {
            ModoClima.VENTILACION -> false
            ModoClima.FRIO -> temperaturaActual > (temperaturaDeseada + histeresis)
            ModoClima.CALOR -> temperaturaActual < (temperaturaDeseada - histeresis)
        }
    }

    override fun consumoActualWatts(): Double {
        if (!encendido) return 0.0
        val base = potenciaVentiladorWatts
        return if (compresorActivo()) base + potenciaCompresorWatts else base
    }
}
