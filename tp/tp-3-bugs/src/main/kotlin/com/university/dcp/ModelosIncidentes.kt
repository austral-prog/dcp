package com.university.dcp

data class AlarmaEvento(
    val idSensor: String,
    val tipo: String,
    val timestamp: String,
    val valor: Double,
    val severidad: String
)

data class Rafaga(
    val idSensor: String,
    val inicio: String,
    val fin: String,
    val cantidadAlarmas: Int
)

data class SaltoTermico(
    val idSensor: String,
    val timestampAnterior: String,
    val timestampActual: String,
    val delta: Double
)

data class MedicionSimple(
    val idSensor: String,
    val tipo: String,
    val timestamp: String,
    val valor: Double
)

data class ReporteJsonData(
    val fechaGeneracion: String,
    val lecturasValidas: Int,
    val totalAlarmas: Int,
    val rafagas: List<Rafaga>,
    val saltosTermicos: List<SaltoTermico>
)
