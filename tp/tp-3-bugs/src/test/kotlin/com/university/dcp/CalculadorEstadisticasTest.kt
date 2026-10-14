package com.university.dcp

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class CalculadorEstadisticasTest {

    @Test
    fun `lista vacia retorna null`() {
        assertNull(CalculadorEstadisticas.calcular("temperatura", emptyList()))
    }

    @Test
    fun `calcula cantidad promedio y maximo correctamente`() {
        val valores = listOf(20.0, 30.0)
        val est = CalculadorEstadisticas.calcular("temperatura", valores)
        assertNotNull(est)
        assertEquals(2, est.cantidad)
        assertEquals(25.0, est.promedio)
        assertEquals(30.0, est.max)
    }

    @Test
    fun `minimo de lecturas no queda fijado en cero para valores positivos`() {
        val valores = listOf(21.4, 25.0, 19.5)
        val est = CalculadorEstadisticas.calcular("temperatura", valores)
        assertNotNull(est)
        assertEquals(19.5, est.min)
    }
}
