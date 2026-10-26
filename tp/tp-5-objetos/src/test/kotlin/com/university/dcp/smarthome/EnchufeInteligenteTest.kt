package com.university.dcp.smarthome

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class EnchufeInteligenteTest {

    @Test
    fun `constructor valida potencia maxima positiva`() {
        assertFailsWith<IllegalArgumentException> { EnchufeInteligente("", "Enchufe") }
        assertFailsWith<IllegalArgumentException> { EnchufeInteligente("e1", "Enchufe", potenciaMaximaSoportadaWatts = 0.0) }
        assertFailsWith<IllegalArgumentException> { EnchufeInteligente("e1", "Enchufe", potenciaMaximaSoportadaWatts = -100.0) }
    }

    @Test
    fun `consumo refleja la carga conectada cuando esta encendido`() {
        val enchufe = EnchufeInteligente("e1", "Enchufe TV", 2000.0)
        enchufe.conectarCarga(150.0)
        assertEquals(0.0, enchufe.consumoActualWatts()) // apagado aún

        enchufe.encender()
        assertTrue(enchufe.encendido)
        assertEquals(150.0, enchufe.consumoActualWatts())

        enchufe.desconectarCarga()
        assertEquals(0.0, enchufe.consumoActualWatts())
    }

    @Test
    fun `conectar carga negativa lanza excepcion`() {
        val enchufe = EnchufeInteligente("e1", "Enchufe", 2000.0)
        assertFailsWith<IllegalArgumentException> { enchufe.conectarCarga(-50.0) }
    }

    @Test
    fun `sobrecarga apaga el enchufe y bloquea reencendido`() {
        val enchufe = EnchufeInteligente("e1", "Enchufe Caloventor", potenciaMaximaSoportadaWatts = 2000.0)
        enchufe.encender()
        assertTrue(enchufe.encendido)

        // Conectar carga excesiva (2500W > 2000W)
        enchufe.conectarCarga(2500.0)
        assertTrue(enchufe.sobrecarga)
        assertFalse(enchufe.encendido)
        assertEquals(0.0, enchufe.consumoActualWatts())

        // Intentar encender con sobrecarga debe fallar
        assertFailsWith<IllegalStateException> { enchufe.encender() }
    }

    @Test
    fun `rearmar solo funciona cuando la carga conectada es segura`() {
        val enchufe = EnchufeInteligente("e1", "Enchufe Taller", potenciaMaximaSoportadaWatts = 2000.0)
        enchufe.conectarCarga(2400.0)
        assertTrue(enchufe.sobrecarga)

        // No se puede rearmar si la carga sigue conectada por encima del límite
        assertFailsWith<IllegalStateException> { enchufe.rearmar() }

        // Bajamos la carga a un valor admisible y rearmamos
        enchufe.conectarCarga(800.0)
        enchufe.rearmar()
        assertFalse(enchufe.sobrecarga)

        // Ahora sí se puede encender
        enchufe.encender()
        assertTrue(enchufe.encendido)
        assertEquals(800.0, enchufe.consumoActualWatts())
    }
}
