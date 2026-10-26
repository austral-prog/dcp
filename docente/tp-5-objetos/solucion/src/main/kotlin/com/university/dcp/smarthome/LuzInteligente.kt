package com.university.dcp.smarthome

class LuzInteligente(
    override val id: String,
    override val nombre: String,
    val potenciaBaseWatts: Double = 60.0,
    brilloInicial: Int = 100,
) : Dispositivo {

    override var encendido: Boolean = false
        private set

    var nivelBrillo: Int = brilloInicial
        private set

    init {
        require(id.isNotBlank()) { "El id no puede estar vacío" }
        require(nombre.isNotBlank()) { "El nombre no puede estar vacío" }
        require(potenciaBaseWatts > 0) { "La potencia base debe ser mayor a 0" }
        require(brilloInicial in 0..100) { "El brillo inicial debe estar entre 0 y 100" }
    }

    override fun encender() {
        encendido = true
    }

    override fun apagar() {
        encendido = false
    }

    fun ajustarBrillo(nuevoBrillo: Int) {
        require(nuevoBrillo in 0..100) { "El brillo debe estar entre 0 y 100" }
        nivelBrillo = nuevoBrillo
    }

    override fun consumoActualWatts(): Double {
        if (!encendido) return 0.0
        return potenciaBaseWatts * (nivelBrillo / 100.0)
    }
}
