package com.university.dcp.smarthome

/**
 * Contrato base para todos los dispositivos del hogar inteligente.
 * Expone únicamente las operaciones esenciales comunes sin imponer
 * detalles de implementación particulares.
 */
interface Dispositivo {
    val id: String
    val nombre: String
    val encendido: Boolean

    fun encender()
    fun apagar()
    fun consumoActualWatts(): Double
}
