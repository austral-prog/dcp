package com.university.dcp

object ExportadorJson {

    fun exportar(reporte: ReporteJsonData): String {
        val sb = StringBuilder()
        sb.append("{\n")
        sb.append("  \"fecha_generacion\": \"${reporte.fechaGeneracion}\",\n")
        sb.append("  \"lecturas_validas\": ${reporte.lecturasValidas},\n")
        sb.append("  \"total_alarmas\": ${reporte.totalAlarmas},\n")

        sb.append("  \"rafagas\": [\n")
        val rafagasStr = reporte.rafagas.joinToString(",\n") { r ->
            """    {
      "sensor": "${r.idSensor}",
      "inicio": "${r.inicio}",
      "fin": "${r.fin}",
      "cantidad": ${r.cantidadAlarmas}
    }"""
        }
        sb.append(rafagasStr)
        if (reporte.rafagas.isNotEmpty()) sb.append("\n")
        sb.append("  ],\n")

        sb.append("  \"saltos_termicos\": [\n")
        val saltosStr = reporte.saltosTermicos.joinToString(",\n") { s ->
            """    {
      "sensor": "${s.idSensor}",
      "anterior": "${s.timestampAnterior}",
      "actual": "${s.timestampActual}",
      "delta": ${s.delta}
    }"""
        }
        sb.append(saltosStr)
        if (reporte.saltosTermicos.isNotEmpty()) sb.append("\n")
        sb.append("  ]\n")

        sb.append("}")
        return sb.toString()
    }
}
