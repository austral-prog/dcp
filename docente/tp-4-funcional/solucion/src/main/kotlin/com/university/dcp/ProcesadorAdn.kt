package com.university.dcp

data class MuestraAdn(
    val id: String,
    val secuencia: String
) {
    init {
        require(id.isNotBlank()) { "El identificador de la muestra es obligatorio" }
        require(secuencia.isNotEmpty()) { "La secuencia de ADN no puede estar vacia" }
        require(secuencia.all { it == 'A' || it == 'C' || it == 'G' || it == 'T' }) {
            "La secuencia solo puede contener nucleotidos validos: A, C, G, T"
        }
    }
}

sealed class ClasificacionGc {
    data class ContenidoBajo(val idMuestra: String, val porcentaje: Double) : ClasificacionGc()
    data class ContenidoNormal(val idMuestra: String, val porcentaje: Double) : ClasificacionGc()
    data class ContenidoAlto(val idMuestra: String, val porcentaje: Double) : ClasificacionGc()
}

object ProcesadorAdn {

    fun calcularPorcentajeGc(muestra: MuestraAdn): Double {
        val conteoGc = muestra.secuencia.fold(0) { acc, base ->
            if (base == 'G' || base == 'C') acc + 1 else acc
        }
        val porcentaje = (conteoGc.toDouble() / muestra.secuencia.length) * 100.0
        return Math.round(porcentaje * 100.0) / 100.0
    }

    fun transcribirARna(muestras: List<MuestraAdn>): Map<String, String> =
        muestras.associate { muestra ->
            val rna = muestra.secuencia
                .map { base -> if (base == 'T') 'U' else base }
                .joinToString("")
            muestra.id to rna
        }

    fun clasificarMuestrasPorGc(muestras: List<MuestraAdn>): List<ClasificacionGc> =
        muestras.map { muestra ->
            val gc = calcularPorcentajeGc(muestra)
            when {
                gc < 40.0 -> ClasificacionGc.ContenidoBajo(muestra.id, gc)
                gc <= 60.0 -> ClasificacionGc.ContenidoNormal(muestra.id, gc)
                else -> ClasificacionGc.ContenidoAlto(muestra.id, gc)
            }
        }

    fun contarBasesTotalesConMotivo(muestras: List<MuestraAdn>, motivo: String): Int =
        muestras
            .filter { it.secuencia.contains(motivo) }
            .map { it.secuencia.length }
            .fold(0) { acumulado, longitud -> acumulado + longitud }
}
