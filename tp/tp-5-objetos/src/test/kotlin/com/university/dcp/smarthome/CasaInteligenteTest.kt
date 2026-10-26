package com.university.dcp.smarthome

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class CasaInteligenteTest {

    @Test
    fun `constructor valida nombre no vacio`() {
        assertFailsWith<IllegalArgumentException> { CasaInteligente("") }
        assertFailsWith<IllegalArgumentException> { CasaInteligente("   ") }
    }

    @Test
    fun `registrar y buscar dispositivos evita duplicados de id`() {
        val casa = CasaInteligente("Mi Casa")
        val luz1 = LuzInteligente("luz-1", "Luz 1")
        val luz2 = LuzInteligente("luz-1", "Luz Duplicada")

        casa.registrarDispositivo(luz1)
        assertEquals(luz1, casa.buscarDispositivo("luz-1"))
        assertNull(casa.buscarDispositivo("otra"))

        assertFailsWith<IllegalArgumentException> { casa.registrarDispositivo(luz2) }
    }

    @Test
    fun `lista de dispositivos expuesta es defensiva de solo lectura`() {
        val casa = CasaInteligente("Mi Casa")
        val luz = LuzInteligente("luz-1", "Luz 1")
        casa.registrarDispositivo(luz)

        val lista = casa.dispositivos
        assertEquals(1, lista.size)
        // Intentar mutar la lista externa no debe alterar la colección de la casa
        val copiaMutable = lista as? MutableCollection<*>
        try {
            copiaMutable?.clear()
        } catch (_: UnsupportedOperationException) {
            // Colección inmutable nativa, esperado
        }
        assertEquals(1, casa.dispositivos.size)
    }

    @Test
    fun `encenderTodo y apagarTodo manejan dispositivos polimorficos y errores`() {
        val casa = CasaInteligente("Mi Casa")
        val luz = LuzInteligente("l1", "Luz", 50.0)
        val termo = Termostato("t1", "Termo")
        val enchufe = EnchufeInteligente("e1", "Enchufe", 1000.0)
        enchufe.conectarCarga(1500.0) // provocamos sobrecarga

        casa.registrarDispositivo(luz)
        casa.registrarDispositivo(termo)
        casa.registrarDispositivo(enchufe)

        casa.encenderTodo()
        assertTrue(luz.encendido)
        assertTrue(termo.encendido)
        assertFalse(enchufe.encendido) // no encendió por sobrecarga
        assertEquals(2, casa.dispositivosEncendidos().size)

        casa.apagarTodo()
        assertFalse(luz.encendido)
        assertFalse(termo.encendido)
        assertEquals(0, casa.dispositivosEncendidos().size)
    }

    @Test
    fun `consumoTotalWatts suma polimorficamente consumos heterogeneos`() {
        val casa = CasaInteligente("Hogar Sustentable")
        val luz = LuzInteligente("l1", "Luz", 100.0, brilloInicial = 50) // 50W al encender
        val enchufe = EnchufeInteligente("e1", "Enchufe Heladera", 2000.0) // 120W al encender
        enchufe.conectarCarga(120.0)

        val termo = Termostato("t1", "Termo", temperaturaInicial = 28.0, potenciaCompresorWatts = 1000.0, potenciaVentiladorWatts = 50.0)
        termo.programar(ModoClima.FRIO, 24.0) // 28 > 24 + 0.5 -> compresor activo (1050W al encender)

        casa.registrarDispositivo(luz)
        casa.registrarDispositivo(enchufe)
        casa.registrarDispositivo(termo)

        assertEquals(0.0, casa.consumoTotalWatts()) // todo apagado

        luz.encender()
        assertEquals(50.0, casa.consumoTotalWatts())

        enchufe.encender()
        assertEquals(170.0, casa.consumoTotalWatts())

        termo.encender()
        assertEquals(1220.0, casa.consumoTotalWatts(), 0.001)
    }

    @Test
    fun `automatizaciones por composicion ejecutan acciones al activarse sensores`() {
        val casa = CasaInteligente("Hogar Automatizado")
        val luzPasillo = LuzInteligente("luz-p", "Luz Pasillo", 40.0)
        val sensorMov = SensorPresencia("sen-1", hayMovimiento = false)

        val regla = ReglaAutomatizacion("Encender si hay movimiento", sensorMov) {
            luzPasillo.encender()
        }
        casa.registrarDispositivo(luzPasillo)
        casa.registrarAutomatizacion(regla)

        // Sin movimiento: la regla no dispara
        val disparadas1 = casa.ejecutarAutomatizaciones()
        assertEquals(0, disparadas1)
        assertFalse(luzPasillo.encendido)

        // Se detecta movimiento: la regla dispara y enciende la luz
        sensorMov.hayMovimiento = true
        val disparadas2 = casa.ejecutarAutomatizaciones()
        assertEquals(1, disparadas2)
        assertTrue(luzPasillo.encendido)
    }
}
