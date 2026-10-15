package com.university.dcp

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ExportadorJsonTest {

    private fun reportePrueba() = ReporteJsonData(
        fechaGeneracion = "2026-09-28T12:00",
        lecturasValidas = 49,
        totalAlarmas = 23,
        rafagas = listOf(
            Rafaga("S-01", "2026-09-28T08:45", "2026-09-28T09:30", 3)
        ),
        saltosTermicos = listOf(
            SaltoTermico("S-02", "2026-09-28T09:45", "2026-09-28T10:00", 14.0)
        )
    )

    @Test
    fun `estructura base contiene claves esperadas`() {
        val json = ExportadorJson.exportar(reportePrueba())
        assertTrue(json.contains("\"fecha_generacion\": \"2026-09-28T12:00\""))
        assertTrue(json.contains("\"rafagas\":"))
        assertTrue(json.contains("\"saltos_termicos\":"))
    }

    @Test
    fun `el json generado no contiene trailing commas en los arrays`() {
        val json = ExportadorJson.exportar(reportePrueba())
        // En JSON estandar, una coma antes del cierre de array ']' es un error de sintaxis invalido
        assertFalse(
            json.contains(",\n  ]"),
            "El JSON generado contiene una coma final (trailing comma) antes de cerrar un array"
        )
    }

    @Test
    fun `los campos numericos se serializan como numeros sin comillas`() {
        val json = ExportadorJson.exportar(reportePrueba())
        // Debe ser "lecturas_validas": 49 y no "lecturas_validas": "49"
        assertTrue(
            json.contains("\"lecturas_validas\": 49"),
            "lecturas_validas debe ser un entero numerico sin comillas"
        )
        assertTrue(
            json.contains("\"total_alarmas\": 23"),
            "total_alarmas debe ser un entero numerico sin comillas"
        )
    }
}
