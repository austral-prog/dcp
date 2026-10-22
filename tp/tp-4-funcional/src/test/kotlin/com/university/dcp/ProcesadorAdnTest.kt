package com.university.dcp

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class ProcesadorAdnTest {

    @Test
    fun `MuestraAdn valida que el id no este en blanco y que la secuencia solo contenga ACGT`() {
        assertFailsWith<IllegalArgumentException>("Debe rechazar id en blanco") {
            MuestraAdn(id = "  ", secuencia = "ACGT")
        }
        assertFailsWith<IllegalArgumentException>("Debe rechazar secuencia vacia") {
            MuestraAdn(id = "M-01", secuencia = "")
        }
        assertFailsWith<IllegalArgumentException>("Debe rechazar caracteres fuera de A, C, G, T") {
            MuestraAdn(id = "M-01", secuencia = "ACGTX")
        }
        assertFailsWith<IllegalArgumentException>("Debe rechazar minusculas") {
            MuestraAdn(id = "M-01", secuencia = "acgt")
        }
    }

    @Test
    fun `calcularPorcentajeGc contabiliza tanto G como C sobre el total de bases`() {
        val muestra = MuestraAdn(id = "M-01", secuencia = "AACCGGTT") // 2 C + 2 G = 4 de 8 -> 50.0%
        assertEquals(50.0, ProcesadorAdn.calcularPorcentajeGc(muestra), 0.01)

        val soloC = MuestraAdn(id = "M-02", secuencia = "CCCCAAAA") // 4 C de 8 -> 50.0%
        assertEquals(50.0, ProcesadorAdn.calcularPorcentajeGc(soloC), 0.01)
    }

    @Test
    fun `transcribirARna reemplaza timina T por uracilo U en cada muestra`() {
        val muestras = listOf(
            MuestraAdn(id = "M-01", secuencia = "ATGC"),
            MuestraAdn(id = "M-02", secuencia = "TTTTAAAA")
        )

        val rnaPorMuestra = ProcesadorAdn.transcribirARna(muestras)
        assertEquals("AUGC", rnaPorMuestra["M-01"])
        assertEquals("UUUUAAAA", rnaPorMuestra["M-02"])
    }

    @Test
    fun `clasificarMuestrasPorGc distingue contenido bajo, normal y alto`() {
        val muestras = listOf(
            MuestraAdn(id = "BAJO", secuencia = "AAATTTAAAC"),     // 1/10 = 10.0% (< 40%)
            MuestraAdn(id = "NORMAL", secuencia = "AACCGGTTAA"),   // 4/10 = 40.0% (40%..60%)
            MuestraAdn(id = "ALTO", secuencia = "CCGGCCGGAT")      // 8/10 = 80.0% (> 60%)
        )

        val clasificaciones = ProcesadorAdn.clasificarMuestrasPorGc(muestras)
        assertEquals(
            listOf(
                ClasificacionGc.ContenidoBajo("BAJO", 10.0),
                ClasificacionGc.ContenidoNormal("NORMAL", 40.0),
                ClasificacionGc.ContenidoAlto("ALTO", 80.0)
            ),
            clasificaciones
        )
    }

    @Test
    fun `contarBasesTotalesConMotivo filtra por motivo y suma las longitudes con fold`() {
        val muestras = listOf(
            MuestraAdn(id = "M-01", secuencia = "ATGCCGTA"), // contiene ATG (len 8)
            MuestraAdn(id = "M-02", secuencia = "CCGGTTAA"), // NO contiene ATG (len 8)
            MuestraAdn(id = "M-03", secuencia = "GGATGCCC")  // contiene ATG (len 8)
        )

        val totalBases = ProcesadorAdn.contarBasesTotalesConMotivo(muestras, "ATG")
        assertEquals(16, totalBases, "Solo deben sumarse las longitudes de las muestras que contienen el motivo")
        assertEquals(0, ProcesadorAdn.contarBasesTotalesConMotivo(emptyList(), "ATG"))
    }
}
