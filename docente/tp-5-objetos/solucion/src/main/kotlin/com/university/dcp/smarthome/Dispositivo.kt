package com.university.dcp.smarthome

interface Dispositivo {
    val id: String
    val nombre: String
    val encendido: Boolean

    fun encender()
    fun apagar()
    fun consumoActualWatts(): Double
}
