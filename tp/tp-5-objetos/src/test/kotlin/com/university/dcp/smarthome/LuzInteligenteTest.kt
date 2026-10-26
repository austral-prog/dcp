package com.university.dcp.smarthome

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class LuzInteligenteTest {

    @Test
    fun `constructor valida id nombre potencia y brillo inicial`() {
        assertFailsWith<IllegalArgumentException> { LuzInteligente("", "Luz", 60.0) }
        assertFailsWith<IllegalArgumentException> { LuzInteligente("l1", "  ", 60.0) }
        assertFailsWith<IllegalArgumentException> { LuzInteligente("l1", "Luz", -10.0) }
        assertFailsWith<IllegalArgumentException> { LuzInteligente("l1", "Luz", 60.0, brilloInicial = -5) }
        assertFailsWith<IllegalArgumentException> { LuzInteligente("l1", "Luz", 60.0, brilloInicial = 101) }
    }

    @Test
    fun `encender y apagar alternan el estado correctamente`() {
        val luz = LuzInteligente("l1", "Luz Cocina", 50.0)
        assertFalse(luz.encendido)

        luz.encender()
        assertTrue(luz.encendido)

        luz.apagar()
        assertFalse(luz.encendido)
    }

    @Test
    fun `ajustar brillo valida rango permitido 0 a 100`() {
        val luz = LuzInteligente("l1", "Luz Cocina", 50.0)
        luz.ajustarBrillo(75)
        assertEquals(75, luz.nivelBrillo)

        assertFailsWith<IllegalArgumentException> { luz.ajustarBrillo(-1) }
        assertFailsWith<IllegalArgumentException> { luz.ajustarBrillo(150) }
    }

    @Test
    fun `consumo es cero si esta apagada independientemente del brillo`() {
        val luz = LuzInteligente("l1", "Luz Cocina", 100.0, brilloInicial = 80)
        assertEquals(0.0, luz.consumoActualWatts())
    }

    @Test
    fun `consumo es proporcional al brillo cuando esta encendida`() {
        val luz = LuzInteligente("l1", "Luz Cocina", 100.0, brilloInicial = 100)
        luz.encender()
        assertEquals(100.0, luz.consumoActualWatts())

        luz.ajustarBrillo(50)
        assertEquals(50.0, luz.consumoActualWatts())

        luz.ajustarBrillo(0)
        assertEquals(0.0, luz.consumoActualWatts())
    }
}
