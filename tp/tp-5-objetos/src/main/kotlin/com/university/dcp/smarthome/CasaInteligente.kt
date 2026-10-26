package com.university.dcp.smarthome

class CasaInteligente(val nombre: String) {

    // TODO: Encapsular _dispositivos y exponer lista inmutable de sólo lectura defensiva (toList())
    val dispositivos: MutableList<Dispositivo> = mutableListOf()

    private val _automatizaciones = mutableListOf<ReglaAutomatizacion>()
    val automatizaciones: List<ReglaAutomatizacion> get() = _automatizaciones.toList()

    init {
        require(nombre.isNotBlank()) { "El nombre de la casa no puede estar vacío" }
    }

    fun registrarDispositivo(dispositivo: Dispositivo) {
        // TODO: Validar que no exista un dispositivo con el mismo id antes de agregarlo
        dispositivos.add(dispositivo)
    }

    fun buscarDispositivo(id: String): Dispositivo? =
        dispositivos.find { it.id == id }

    fun encenderTodo() {
        for (d in dispositivos) {
            try {
                d.encender()
            } catch (_: IllegalStateException) {
                // Si un dispositivo no puede encender (ej. sobrecarga), se ignora
            }
        }
    }

    fun apagarTodo() {
        dispositivos.forEach { it.apagar() }
    }

    fun consumoTotalWatts(): Double {
        // TODO: Aprovechar polimorfismo para calcular el consumo total de todos los dispositivos
        return 0.0
    }

    fun dispositivosEncendidos(): List<Dispositivo> =
        dispositivos.filter { it.encendido }

    fun registrarAutomatizacion(regla: ReglaAutomatizacion) {
        _automatizaciones.add(regla)
    }

    fun ejecutarAutomatizaciones(): Int =
        _automatizaciones.count { it.evaluar() }
}
