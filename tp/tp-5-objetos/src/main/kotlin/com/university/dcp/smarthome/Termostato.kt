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

    override fun encender() {
        encendido = true
    }

    override fun apagar() {
        encendido = false
    }

    fun programar(nuevoModo: ModoClima, nuevaTemperaturaDeseada: Double) {
        // TODO: Proteger invariante: la temperatura deseada debe estar entre 16.0 y 32.0 °C
        modo = nuevoModo
        temperaturaDeseada = nuevaTemperaturaDeseada
    }

    fun actualizarTemperaturaAmbiente(nuevaTemperatura: Double) {
        temperaturaActual = nuevaTemperatura
    }

    fun compresorActivo(): Boolean {
        if (!encendido) return false
        // TODO: Evaluar activación según modo e histéresis:
        // - VENTILACION: siempre false
        // - FRIO: activo si temperaturaActual > (temperaturaDeseada + histeresis)
        // - CALOR: activo si temperaturaActual < (temperaturaDeseada - histeresis)
        return false
    }

    override fun consumoActualWatts(): Double {
        // TODO: Si está apagado el consumo es 0.0.
        // Si está encendido: potenciaVentiladorWatts + (potenciaCompresorWatts si compresorActivo()).
        return 0.0
    }
}
