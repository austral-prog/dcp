package com.university.dcp

import kotlin.test.Test
import kotlin.test.assertEquals

class CalibradorTest {

    @Test
    fun `sensor S-01 suma 0_3 por calibracion de fabrica`() {
        val obtenido = Calibrador.calibrar("S-01", 20.0)
        assertEquals(20.3, obtenido, 0.001)
    }

    @Test
    fun `sensor S-02 resta 0_5 por calibracion de fabrica`() {
        // S-02 tiene calibracion de -0.5
        val obtenido = Calibrador.calibrar("S-02", 20.0)
        assertEquals(19.5, obtenido, 0.001)
    }

    @Test
    fun `sensor S-04 suma 1_2 por calibracion de fabrica`() {
        val obtenido = Calibrador.calibrar("S-04", 1000.0)
        assertEquals(1001.2, obtenido, 0.001)
    }

    @Test
    fun `sensor sin calibracion especifica no altera el valor`() {
        val obtenido = Calibrador.calibrar("S-03", 50.0)
        assertEquals(50.0, obtenido, 0.001)
    }
}
