package com.university.dcp

import java.io.ByteArrayOutputStream
import java.io.File
import java.io.PrintStream
import kotlin.test.Test
import kotlin.test.assertEquals

class ContratoSalidaTest {

    @Test
    fun `la consola y reporte-txt coinciden con las salidas de referencia`() {
        val consola = ByteArrayOutputStream()
        val salidaOriginal = System.out
        System.setOut(PrintStream(consola, true, Charsets.UTF_8))
        try {
            main()
        } finally {
            System.setOut(salidaOriginal)
        }

        assertEquals(leer(File("salida-esperada.txt")), normalizar(consola.toString(Charsets.UTF_8)))
        assertEquals(leer(File("reporte-esperado.txt")), leer(File("reporte.txt")))
    }

    private fun leer(archivo: File) = normalizar(archivo.readText())

    // En Windows println escribe \r\n; las salidas de referencia usan \n.
    private fun normalizar(texto: String) = texto.replace("\r\n", "\n")
}
