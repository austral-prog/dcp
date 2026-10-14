package com.university.dcp

object LectorLecturas {

    private val tiposConocidos = setOf("temperatura", "humedad", "presion")

    fun procesarLinea(numeroFila: Int, linea: String): ResultadoLinea? {
        val trimmed = linea.trim()
        if (trimmed.isEmpty() || trimmed.startsWith("#") || trimmed.startsWith("id_sensor")) {
            return null
        }

        val partes = linea.split(";")
        if (partes.size != 5) {
            return ResultadoLinea.Descartada(numeroFila, "se esperaban 5 campos y hay ${partes.size}")
        }

        val id = partes[0]
        val tipo = partes[1]
        val ts = partes[2]
        val valorStr = partes[3]
        val unidad = partes[4]

        val valor = valorStr.toDoubleOrNull()
            ?: return ResultadoLinea.Descartada(numeroFila, "valor no numerico '$valorStr'")

        if (tipo !in tiposConocidos) {
            return ResultadoLinea.Ignorada(numeroFila, "tipo de sensor desconocido '$tipo'")
        }

        if (!ConversorUnidades.esUnidadValida(tipo, unidad)) {
            return ResultadoLinea.Descartada(numeroFila, "unidad desconocida '$unidad' para $tipo")
        }

        return ResultadoLinea.Valida(Lectura(id, tipo, ts, valor, unidad))
    }
}
