package com.university.dcp.smarthome

class EnchufeInteligente(
    override val id: String,
    override val nombre: String,
    val potenciaMaximaSoportadaWatts: Double = 2200.0,
) : Dispositivo {

    override var encendido: Boolean = false
        private set

    var potenciaConectadaWatts: Double = 0.0
        private set

    var sobrecarga: Boolean = false
        private set

    override fun encender() {
        // TODO: Proteger invariante: no permitir encender si está en estado de sobrecarga (check(!sobrecarga))
        encendido = true
    }

    override fun apagar() {
        encendido = false
    }

    fun conectarCarga(watts: Double) {
        // TODO: Validar que watts >= 0 con require().
        // Si watts > potenciaMaximaSoportadaWatts:
        //   marcar sobrecarga = true y apagar el enchufe automáticamente.
        potenciaConectadaWatts = watts
    }

    fun desconectarCarga() {
        potenciaConectadaWatts = 0.0
    }

    fun rearmar() {
        // TODO: Proteger invariante: no permitir rearmar si la potenciaConectadaWatts todavía supera el máximo soportado.
        sobrecarga = false
    }

    override fun consumoActualWatts(): Double {
        if (!encendido) return 0.0
        return potenciaConectadaWatts
    }
}
