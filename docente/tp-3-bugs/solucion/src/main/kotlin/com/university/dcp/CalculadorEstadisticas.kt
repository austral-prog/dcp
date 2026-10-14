package com.university.dcp

object CalculadorEstadisticas {

    fun calcular(tipo: String, valores: List<Double>): Estadistica? {
        if (valores.isEmpty()) return null

        var suma = 0.0
        var min = Double.MAX_VALUE
        var max = -Double.MAX_VALUE

        for (v in valores) {
            suma += v
            if (v < min) min = v
            if (v > max) max = v
        }

        val prom = Math.round((suma / valores.size) * 100.0) / 100.0
        val minRedondeado = Math.round(min * 100.0) / 100.0
        val maxRedondeado = Math.round(max * 100.0) / 100.0

        return Estadistica(
            tipo = tipo,
            cantidad = valores.size,
            promedio = prom,
            min = minRedondeado,
            max = maxRedondeado,
            unidadBase = ConversorUnidades.unidadBase(tipo)
        )
    }
}
