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

    init {
        require(id.isNotBlank()) { "El id no puede estar vacío" }
        require(nombre.isNotBlank()) { "El nombre no puede estar vacío" }
        require(potenciaMaximaSoportadaWatts > 0) { "La potencia máxima debe ser mayor a 0" }
    }

    override fun encender() {
        check(!sobrecarga) { "No se puede encender un enchufe en estado de sobrecarga" }
        encendido = true
    }

    override fun apagar() {
        encendido = false
    }

    fun conectarCarga(watts: Double) {
        require(watts >= 0) { "La potencia conectada no puede ser negativa: $watts" }
        potenciaConectadaWatts = watts
        if (watts > potenciaMaximaSoportadaWatts) {
            sobrecarga = true
            apagar()
        }
    }

    fun desconectarCarga() {
        potenciaConectadaWatts = 0.0
    }

    fun rearmar() {
        check(potenciaConectadaWatts <= potenciaMaximaSoportadaWatts) {
            "No se puede rearmar mientras la carga conectada supere el límite máximo ($potenciaConectadaWatts W > $potenciaMaximaSoportadaWatts W)"
        }
        sobrecarga = false
    }

    override fun consumoActualWatts(): Double {
        if (!encendido) return 0.0
        return potenciaConectadaWatts
    }
}
