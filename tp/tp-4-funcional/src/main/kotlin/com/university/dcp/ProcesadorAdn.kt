package com.university.dcp

data class MuestraAdn(
    val id: String,
    val secuencia: String
)

sealed class ClasificacionGc {
    data class ContenidoBajo(val idMuestra: String, val porcentaje: Double) : ClasificacionGc()
    data class ContenidoNormal(val idMuestra: String, val porcentaje: Double) : ClasificacionGc()
    data class ContenidoAlto(val idMuestra: String, val porcentaje: Double) : ClasificacionGc()
}

object ProcesadorAdn {

    fun calcularPorcentajeGc(muestra: MuestraAdn): Double {
        var conteo = 0
        for (base in muestra.secuencia) {
            if (base == 'G') {
                conteo++
            }
        }
        val porcentaje = (conteo.toDouble() / muestra.secuencia.length) * 100.0
        return Math.round(porcentaje * 100.0) / 100.0
    }

    fun transcribirARna(muestras: List<MuestraAdn>): Map<String, String> {
        val resultado = mutableMapOf<String, String>()
        for (muestra in muestras) {
            var rna = ""
            for (base in muestra.secuencia) {
                rna += if (base == 'A') 'U' else base
            }
            resultado[muestra.id] = rna
        }
        return resultado
    }

    fun clasificarMuestrasPorGc(muestras: List<MuestraAdn>): List<ClasificacionGc> {
        val clasificaciones = mutableListOf<ClasificacionGc>()
        for (muestra in muestras) {
            val gc = calcularPorcentajeGc(muestra)
            if (gc < 50.0) {
                clasificaciones.add(ClasificacionGc.ContenidoBajo(muestra.id, gc))
            } else {
                clasificaciones.add(ClasificacionGc.ContenidoAlto(muestra.id, gc))
            }
        }
        return clasificaciones
    }

    fun contarBasesTotalesConMotivo(muestras: List<MuestraAdn>, motivo: String): Int {
        var total = 0
        for (muestra in muestras) {
            total += muestra.secuencia.length
        }
        return total
    }
}
