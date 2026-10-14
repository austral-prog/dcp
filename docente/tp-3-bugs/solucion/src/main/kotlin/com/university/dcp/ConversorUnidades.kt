package com.university.dcp

object ConversorUnidades {

    private val unidadesValidas = mapOf(
        "temperatura" to setOf("C", "F", "K"),
        "humedad" to setOf("%"),
        "presion" to setOf("hPa", "Pa", "kPa", "mbar")
    )

    fun esUnidadValida(tipo: String, unidad: String): Boolean {
        val validas = unidadesValidas[tipo] ?: return true
        return unidad in validas
    }

    fun unidadBase(tipo: String): String = when (tipo) {
        "temperatura" -> "C"
        "humedad" -> "%"
        "presion" -> "hPa"
        else -> ""
    }

    fun convertir(tipo: String, valor: Double, unidad: String): Double {
        if (!esUnidadValida(tipo, unidad)) {
            throw IllegalArgumentException("unidad desconocida '$unidad' para $tipo")
        }
        return when (tipo) {
            "temperatura" -> when (unidad) {
                "C" -> valor
                "F" -> (valor - 32.0) * 5.0 / 9.0
                "K" -> valor - 273.15
                else -> valor
            }
            "humedad" -> valor
            "presion" -> when (unidad) {
                "hPa" -> valor
                "Pa" -> valor / 100.0
                "kPa" -> valor * 10.0
                "mbar" -> valor
                else -> valor
            }
            else -> valor
        }
    }
}
