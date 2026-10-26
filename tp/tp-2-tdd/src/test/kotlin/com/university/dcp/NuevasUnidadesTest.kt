package com.university.dcp

import java.io.ByteArrayOutputStream
import java.io.File
import java.io.PrintStream
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Tests de la Parte 2 del TP 2 (TDD - Nuevas unidades):
 * Valida la incorporación de kPa, mbar y el descarte de unidades inválidas
 * para magnitudes conocidas según los escenarios de referencia de la cátedra.
 */
class NuevasUnidadesTest {

    @Test
    fun `soporta presion en kPa convirtiendo a hPa`() {
        val (consola, reporte) = correrEscenario("kpa")
        val consolaEsperada = leerRecurso("kpa.salida.txt")
        val reporteEsperado = leerRecurso("kpa.reporte.txt")

        assertEquals(consolaEsperada, consola)
        assertEquals(reporteEsperado, reporte)
    }

    @Test
    fun `descarta lecturas con unidades invalidas para magnitudes conocidas`() {
        val (consola, reporte) = correrEscenario("unidad-invalida")
        val consolaEsperada = leerRecurso("unidad-invalida.salida.txt")
        val reporteEsperado = leerRecurso("unidad-invalida.reporte.txt")

        assertEquals(consolaEsperada, consola)
        assertEquals(reporteEsperado, reporte)
    }

    @Test
    fun `soporta presion en mbar convirtiendo a hPa`() {
        val (consola, reporte) = correrEscenario("mbar")
        val consolaEsperada = leerRecurso("mbar.salida.txt")
        val reporteEsperado = leerRecurso("mbar.reporte.txt")

        assertEquals(consolaEsperada, consola)
        assertEquals(reporteEsperado, reporte)
    }

    private fun correrEscenario(nombre: String): Pair<String, String> {
        val csv = leerRecurso("$nombre.csv")
        val archivoDatos = File("datos/lecturas.csv")
        val backupDatos = archivoDatos.readBytes()
        val archivoReporte = File("reporte.txt")
        if (archivoReporte.exists()) archivoReporte.delete()

        val consola = ByteArrayOutputStream()
        val salidaOriginal = System.out
        System.setOut(PrintStream(consola, true, Charsets.UTF_8))
        try {
            archivoDatos.writeText(csv, Charsets.UTF_8)
            main()
        } finally {
            System.setOut(salidaOriginal)
            archivoDatos.writeBytes(backupDatos)
            resetStarterStateIfPresent()
        }

        val consolaStr = normalizar(consola.toString(Charsets.UTF_8))
        val reporteStr = if (archivoReporte.exists()) normalizar(archivoReporte.readText(Charsets.UTF_8)) else ""
        return Pair(consolaStr, reporteStr)
    }

    private fun leerRecurso(nombre: String): String {
        val stream = javaClass.getResourceAsStream("/escenarios/$nombre")
            ?: error("Recurso no encontrado: /escenarios/$nombre")
        return normalizar(stream.bufferedReader(Charsets.UTF_8).readText())
    }

    private fun normalizar(s: String): String = s.replace("\r\n", "\n")

    private fun resetStarterStateIfPresent() {
        try {
            val clazz = Class.forName("com.university.dcp.MainKt")
            for (field in clazz.declaredFields) {
                if (java.lang.reflect.Modifier.isStatic(field.modifiers) && !java.lang.reflect.Modifier.isFinal(field.modifiers)) {
                    field.isAccessible = true
                    when (field.type) {
                        Double::class.javaPrimitiveType -> {
                            val name = field.name
                            if (name.startsWith("min")) field.set(null, 9999.0)
                            else if (name.startsWith("max")) field.set(null, -9999.0)
                            else field.set(null, 0.0)
                        }
                        Int::class.javaPrimitiveType -> field.set(null, 0)
                    }
                }
            }
        } catch (_: Throwable) {
            // No-op si el código ya fue refactorizado y no tiene variables estáticas
        }
    }
}
