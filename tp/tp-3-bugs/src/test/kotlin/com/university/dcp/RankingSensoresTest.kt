package com.university.dcp

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class RankingSensoresTest {

    private fun crearAlarma(idSensor: String) = Alarma(
        severidad = Severidad.LEVE,
        idSensor = idSensor,
        timestamp = "2026-09-28T12:00",
        tipo = "temperatura",
        valor = 28.0,
        unidadBase = "C",
        rangoMin = 18.0,
        rangoMax = 27.0
    )

    @Test
    fun `ranking de lista vacia retorna vacio`() {
        val ranking = RankingSensores.generarRanking(emptyList())
        assertTrue(ranking.isEmpty())
    }

    @Test
    fun `ordena sensores por cantidad de alarmas de mayor a menor`() {
        val alarmas = listOf(
            crearAlarma("S-01"),
            crearAlarma("S-01"),
            crearAlarma("S-01"),
            crearAlarma("S-02"),
            crearAlarma("S-03"),
            crearAlarma("S-03")
        )

        val ranking = RankingSensores.generarRanking(alarmas)
        assertEquals(3, ranking.size)
        // S-01 tiene 3 alarmas, S-03 tiene 2 alarmas, S-02 tiene 1 alarma
        assertEquals("S-01", ranking[0].idSensor)
        assertEquals(3, ranking[0].alarmas)
        assertEquals("S-03", ranking[1].idSensor)
        assertEquals(2, ranking[1].alarmas)
        assertEquals("S-02", ranking[2].idSensor)
        assertEquals(1, ranking[2].alarmas)
    }
}
