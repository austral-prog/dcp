package com.university.dcp.smarthome

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class TermostatoTest {

    @Test
    fun `constructor valida invariantes basicas`() {
        assertFailsWith<IllegalArgumentException> { Termostato("", "Termo") }
        assertFailsWith<IllegalArgumentException> { Termostato("t1", "Termo", potenciaCompresorWatts = 0.0) }
        assertFailsWith<IllegalArgumentException> { Termostato("t1", "Termo", potenciaVentiladorWatts = -5.0) }
        assertFailsWith<IllegalArgumentException> { Termostato("t1", "Termo", histeresis = -0.1) }
    }

    @Test
    fun `programar valida temperatura deseada entre 16 y 32 grados`() {
        val termo = Termostato("t1", "Termo Living")
        termo.programar(ModoClima.FRIO, 22.0)
        assertEquals(22.0, termo.temperaturaDeseada)
        assertEquals(ModoClima.FRIO, termo.modo)

        assertFailsWith<IllegalArgumentException> { termo.programar(ModoClima.FRIO, 15.0) }
        assertFailsWith<IllegalArgumentException> { termo.programar(ModoClima.CALOR, 33.0) }
    }

    @Test
    fun `en modo ventilacion el compresor nunca se activa`() {
        val termo = Termostato("t1", "Termo Living", temperaturaInicial = 35.0)
        termo.programar(ModoClima.VENTILACION, 24.0)
        termo.encender()
        assertFalse(termo.compresorActivo())
        assertEquals(termo.potenciaVentiladorWatts, termo.consumoActualWatts())
    }

    @Test
    fun `en modo frio el compresor activa solo si supera deseada mas histeresis`() {
        val termo = Termostato("t1", "Termo Living", temperaturaInicial = 24.0, histeresis = 0.5)
        termo.programar(ModoClima.FRIO, 24.0)
        termo.encender()

        // Temperatura exactamente igual: dentro del margen, no prende compresor
        assertFalse(termo.compresorActivo())
        assertEquals(termo.potenciaVentiladorWatts, termo.consumoActualWatts())

        // Sube a 24.5 (justo en el límite): no activa
        termo.actualizarTemperaturaAmbiente(24.5)
        assertFalse(termo.compresorActivo())

        // Sube a 24.6 (supera deseada + histeresis): activa compresor
        termo.actualizarTemperaturaAmbiente(24.6)
        assertTrue(termo.compresorActivo())
        assertEquals(termo.potenciaVentiladorWatts + termo.potenciaCompresorWatts, termo.consumoActualWatts())
    }

    @Test
    fun `en modo calor el compresor activa solo si cae por debajo de deseada menos histeresis`() {
        val termo = Termostato("t1", "Termo Dormitorio", temperaturaInicial = 22.0, histeresis = 0.5)
        termo.programar(ModoClima.CALOR, 22.0)
        termo.encender()

        termo.actualizarTemperaturaAmbiente(21.6)
        assertFalse(termo.compresorActivo())

        termo.actualizarTemperaturaAmbiente(21.4)
        assertTrue(termo.compresorActivo())
        assertEquals(termo.potenciaVentiladorWatts + termo.potenciaCompresorWatts, termo.consumoActualWatts())
    }

    @Test
    fun `si esta apagado el consumo es siempre cero`() {
        val termo = Termostato("t1", "Termo", temperaturaInicial = 30.0)
        termo.programar(ModoClima.FRIO, 20.0)
        // Apagado
        assertFalse(termo.encendido)
        assertFalse(termo.compresorActivo())
        assertEquals(0.0, termo.consumoActualWatts())
    }
}
