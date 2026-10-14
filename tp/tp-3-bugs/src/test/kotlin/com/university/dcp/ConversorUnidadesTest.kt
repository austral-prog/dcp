package com.university.dcp

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class ConversorUnidadesTest {

    @Test
    fun `conversion de Celsius mantiene el valor`() {
        val obtenido = ConversorUnidades.convertir("temperatura", 25.0, "C")
        assertEquals(25.0, obtenido, 0.001)
    }

    @Test
    fun `conversion de Fahrenheit a Celsius aplica formula correcta`() {
        // 68 F = (68 - 32) * 5 / 9 = 20.0 C
        val obtenido = ConversorUnidades.convertir("temperatura", 68.0, "F")
        assertEquals(20.0, obtenido, 0.001)
    }

    @Test
    fun `conversion de Kelvin a Celsius resta 273_15`() {
        // 300.0 K = 300 - 273.15 = 26.85 C
        val obtenido = ConversorUnidades.convertir("temperatura", 300.0, "K")
        assertEquals(26.85, obtenido, 0.001)
    }

    @Test
    fun `conversion de Pa a hPa divide por 100`() {
        val obtenido = ConversorUnidades.convertir("presion", 101300.0, "Pa")
        assertEquals(1013.0, obtenido, 0.001)
    }

    @Test
    fun `conversion de mbar a hPa mantiene el valor`() {
        val obtenido = ConversorUnidades.convertir("presion", 1013.2, "mbar")
        assertEquals(1013.2, obtenido, 0.001)
    }

    @Test
    fun `conversion de kPa a hPa multiplica por 10`() {
        // 1 kPa = 10 hPa -> 101.3 kPa = 1013.0 hPa
        val obtenido = ConversorUnidades.convertir("presion", 101.3, "kPa")
        assertEquals(1013.0, obtenido, 0.001)
    }

    @Test
    fun `unidad desconocida para tipo conocido lanza excepcion`() {
        assertFailsWith<IllegalArgumentException> {
            ConversorUnidades.convertir("presion", 760.0, "mmHg")
        }
    }
}
