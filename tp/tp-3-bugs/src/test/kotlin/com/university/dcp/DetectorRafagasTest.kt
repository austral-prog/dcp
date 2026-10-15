package com.university.dcp

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class DetectorRafagasTest {

    private fun crearAlarma(sensor: String, ts: String) = AlarmaEvento(
        idSensor = sensor,
        tipo = "temperatura",
        timestamp = ts,
        valor = 31.0,
        severidad = "LEVE"
    )

    @Test
    fun `minutosEntre con cambio de hora calcula la diferencia real`() {
        // De 08:45 a 09:15 pasaron exactamente 30 minutos
        val minutos = DetectorRafagas.minutosEntre("2026-09-28T08:45", "2026-09-28T09:15")
        assertEquals(30L, minutos)
    }

    @Test
    fun `minutosEntre dentro de la misma hora calcula la diferencia`() {
        val minutos = DetectorRafagas.minutosEntre("2026-09-28T08:10", "2026-09-28T08:25")
        assertEquals(15L, minutos)
    }

    @Test
    fun `alarmas de distintos sensores no se mezclan en una rafaga`() {
        // 3 alarmas en 20 minutos pero cada una pertenece a un sensor distinto
        val alarmas = listOf(
            crearAlarma("S-01", "2026-09-28T08:00"),
            crearAlarma("S-02", "2026-09-28T08:10"),
            crearAlarma("S-03", "2026-09-28T08:20")
        )

        val rafagas = DetectorRafagas.detectarRafagas(alarmas, ventanaMinutos = 45, minAlarmas = 3)
        // No debe haber ninguna rafaga porque ningun sensor individual tuvo >= 3 alarmas
        assertTrue(rafagas.isEmpty(), "Alarmas de distintos sensores no deben agruparse en la misma rafaga")
    }

    @Test
    fun `detecta rafaga valida de un mismo sensor dentro de la ventana de tiempo`() {
        val alarmas = listOf(
            crearAlarma("S-01", "2026-09-28T08:00"),
            crearAlarma("S-01", "2026-09-28T08:15"),
            crearAlarma("S-01", "2026-09-28T08:30")
        )

        val rafagas = DetectorRafagas.detectarRafagas(alarmas, ventanaMinutos = 45, minAlarmas = 3)
        assertEquals(1, rafagas.size)
        assertEquals("S-01", rafagas[0].idSensor)
        assertEquals(3, rafagas[0].cantidadAlarmas)
    }

    @Test
    fun `caida brusca de temperatura se detecta como salto termico`() {
        // La temperatura cae bruscamente de 30.0 C a 22.0 C (delta absoluto de 8.0 C >= umbral 5.0 C)
        val lecturas = listOf(
            MedicionSimple("S-01", "temperatura", "2026-09-28T08:00", 30.0),
            MedicionSimple("S-01", "temperatura", "2026-09-28T08:15", 22.0)
        )

        val saltos = DetectorRafagas.detectarSaltosTermicos(lecturas, umbralDelta = 5.0)
        assertEquals(1, saltos.size, "Una caida brusca de temperatura debe ser detectada como salto termico")
        assertEquals(8.0, saltos[0].delta, 0.01)
    }

    @Test
    fun `aumento brusco de temperatura se detecta como salto termico`() {
        val lecturas = listOf(
            MedicionSimple("S-01", "temperatura", "2026-09-28T08:00", 20.0),
            MedicionSimple("S-01", "temperatura", "2026-09-28T08:15", 26.5)
        )

        val saltos = DetectorRafagas.detectarSaltosTermicos(lecturas, umbralDelta = 5.0)
        assertEquals(1, saltos.size)
        assertEquals(6.5, saltos[0].delta, 0.01)
    }
}
