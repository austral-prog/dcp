package com.university.dcp.smarthome

class CasaInteligente(val nombre: String) {

    private val _dispositivos = mutableListOf<Dispositivo>()
    val dispositivos: List<Dispositivo> get() = _dispositivos.toList()

    private val _automatizaciones = mutableListOf<ReglaAutomatizacion>()
    val automatizaciones: List<ReglaAutomatizacion> get() = _automatizaciones.toList()

    init {
        require(nombre.isNotBlank()) { "El nombre de la casa no puede estar vacío" }
    }

    fun registrarDispositivo(dispositivo: Dispositivo) {
        require(_dispositivos.none { it.id == dispositivo.id }) {
            "Ya existe un dispositivo registrado con el id '${dispositivo.id}'"
        }
        _dispositivos.add(dispositivo)
    }

    fun buscarDispositivo(id: String): Dispositivo? =
        _dispositivos.find { it.id == id }

    fun encenderTodo() {
        for (d in _dispositivos) {
            try {
                d.encender()
            } catch (_: IllegalStateException) {
                // Si un dispositivo no puede encenderse (ej. enchufe en sobrecarga), se omite
            }
        }
    }

    fun apagarTodo() {
        _dispositivos.forEach { it.apagar() }
    }

    fun consumoTotalWatts(): Double =
        _dispositivos.sumOf { it.consumoActualWatts() }

    fun dispositivosEncendidos(): List<Dispositivo> =
        _dispositivos.filter { it.encendido }

    fun registrarAutomatizacion(regla: ReglaAutomatizacion) {
        _automatizaciones.add(regla)
    }

    fun ejecutarAutomatizaciones(): Int =
        _automatizaciones.count { it.evaluar() }
}
