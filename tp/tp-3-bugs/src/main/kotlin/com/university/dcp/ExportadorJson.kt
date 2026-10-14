package com.university.dcp

object ExportadorJson {

    fun exportar(reporte: ReporteJsonData): String {
        val sb = StringBuilder()
        sb.append("{\n")
        sb.append("  \"fecha_generacion\": \"${reporte.fechaGeneracion}\",\n")
        sb.append("  \"lecturas_validas\": \"${reporte.lecturasValidas}\",\n")
        sb.append("  \"total_alarmas\": \"${reporte.totalAlarmas}\",\n")

        sb.append("  \"rafagas\": [\n")
        for (r in reporte.rafagas) {
            sb.append("    {\n")
            sb.append("      \"sensor\": \"${r.idSensor}\",\n")
            sb.append("      \"inicio\": \"${r.inicio}\",\n")
            sb.append("      \"fin\": \"${r.fin}\",\n")
            sb.append("      \"cantidad\": ${r.cantidadAlarmas}\n")
            sb.append("    },\n")
        }
        sb.append("  ],\n")

        sb.append("  \"saltos_termicos\": [\n")
        for (s in reporte.saltosTermicos) {
            sb.append("    {\n")
            sb.append("      \"sensor\": \"${s.idSensor}\",\n")
            sb.append("      \"anterior\": \"${s.timestampAnterior}\",\n")
            sb.append("      \"actual\": \"${s.timestampActual}\",\n")
            sb.append("      \"delta\": ${s.delta}\n")
            sb.append("    },\n")
        }
        sb.append("  ]\n")

        sb.append("}")
        return sb.toString()
    }
}
