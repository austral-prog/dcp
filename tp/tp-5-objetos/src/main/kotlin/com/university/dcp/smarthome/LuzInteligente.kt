package com.university.dcp.smarthome

class LuzInteligente(
    override val id: String,
    override val nombre: String,
    val potenciaBaseWatts: Double = 60.0,
    brilloInicial: Int = 100,
) : Dispositivo {

    override var encendido: Boolean = false
        private set

    // TODO: Encapsular el estado interno y proteger la invariante de brillo (0..100)
    var nivelBrillo: Int = brilloInicial

    override fun encender() {
        encendido = true
    }

    override fun apagar() {
        encendido = false
    }

    fun ajustarBrillo(nuevoBrillo: Int) {
        // TODO: Validar que nuevoBrillo esté entre 0 y 100 con require()
        nivelBrillo = nuevoBrillo
    }

    override fun consumoActualWatts(): Double {
        // TODO: Corregir cálculo: si está apagada consume 0.0.
        // Si está encendida, el consumo es proporcional al nivel de brillo (potenciaBaseWatts * (nivelBrillo / 100.0)).
        return potenciaBaseWatts
    }
}
