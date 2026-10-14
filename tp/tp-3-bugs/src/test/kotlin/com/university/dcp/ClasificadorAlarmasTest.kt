package com.university.dcp

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class ClasificadorAlarmasTest {

    private fun crearLectura(tipo: String, valor: Double) =
        Lectura(idSensor = "S-01", tipo = tipo, timestamp = "2026-09-28T12:00", valor = valor, unidad = "C")

    @Test
    fun `temperatura dentro de rango no genera alarma`() {
        val lectura = crearLectura("temperatura", 22.0)
        assertNull(ClasificadorAlarmas.evaluar(lectura, 22.0))
    }

    @Test
    fun `borde - 27 grados exactos no genera alarma`() {
        val lectura = crearLectura("temperatura", 27.0)
        assertNull(ClasificadorAlarmas.evaluar(lectura, 27.0))
    }

    @Test
    fun `borde - 18 grados exactos no genera alarma`() {
        val lectura = crearLectura("temperatura", 18.0)
        assertNull(ClasificadorAlarmas.evaluar(lectura, 18.0))
    }

    @Test
    fun `temperatura apenas mayor a 27 genera alarma leve`() {
        val lectura = crearLectura("temperatura", 27.1)
        val alarma = ClasificadorAlarmas.evaluar(lectura, 27.1)
        assertNotNull(alarma)
        assertEquals(Severidad.LEVE, alarma.severidad)
    }

    @Test
    fun `temperatura mayor a 30 genera alarma grave`() {
        val lectura = crearLectura("temperatura", 30.5)
        val alarma = ClasificadorAlarmas.evaluar(lectura, 30.5)
        assertNotNull(alarma)
        assertEquals(Severidad.GRAVE, alarma.severidad)
    }
}
