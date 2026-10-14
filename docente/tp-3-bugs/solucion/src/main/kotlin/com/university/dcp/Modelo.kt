package com.university.dcp

data class Lectura(
    val idSensor: String,
    val tipo: String,
    val timestamp: String,
    val valor: Double,
    val unidad: String
)

enum class Severidad {
    LEVE,
    GRAVE
}

data class Alarma(
    val severidad: Severidad,
    val idSensor: String,
    val timestamp: String,
    val tipo: String,
    val valor: Double,
    val unidadBase: String,
    val rangoMin: Double,
    val rangoMax: Double
)

data class Estadistica(
    val tipo: String,
    val cantidad: Int,
    val promedio: Double,
    val min: Double,
    val max: Double,
    val unidadBase: String
)

data class RankingItem(
    val idSensor: String,
    val alarmas: Int
)

sealed interface ResultadoLinea {
    data class Valida(val lectura: Lectura) : ResultadoLinea
    data class Descartada(val fila: Int, val motivo: String) : ResultadoLinea
    data class Ignorada(val fila: Int, val motivo: String) : ResultadoLinea
}
