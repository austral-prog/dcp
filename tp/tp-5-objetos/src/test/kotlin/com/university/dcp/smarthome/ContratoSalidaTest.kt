package com.university.dcp.smarthome

import java.io.ByteArrayOutputStream
import java.io.File
import java.io.PrintStream
import kotlin.test.Test
import kotlin.test.assertEquals

class ContratoSalidaTest {

    @Test
    fun `la consola coincide con la salida de referencia esperada`() {
        val consola = ByteArrayOutputStream()
        val salidaOriginal = System.out
        System.setOut(PrintStream(consola, true, Charsets.UTF_8))
        try {
            main()
        } finally {
            System.setOut(salidaOriginal)
        }

        assertEquals(leer(File("salida-esperada.txt")), normalizar(consola.toString(Charsets.UTF_8)))
    }

    private fun leer(archivo: File) = normalizar(archivo.readText())

    // En Windows println escribe \r\n; las salidas de referencia usan \n.
    private fun normalizar(texto: String) = texto.replace("\r\n", "\n")
}
