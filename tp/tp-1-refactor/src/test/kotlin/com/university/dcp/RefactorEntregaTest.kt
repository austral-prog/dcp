package com.university.dcp

import java.io.File
import java.security.MessageDigest
import kotlin.test.Test
import kotlin.test.assertTrue

class RefactorEntregaTest {

    @Test
    fun `el codigo de partida debe haber sido refactorizado y no estar intacto`() {
        val mainFile = File("src/main/kotlin/com/university/dcp/Main.kt")
        assertTrue(mainFile.exists(), "src/main/kotlin/com/university/dcp/Main.kt debe existir")

        val normalizado = mainFile.readText().replace("\r\n", "\n")
        val md = MessageDigest.getInstance("SHA-256")
        val sha256 = md.digest(normalizado.toByteArray(Charsets.UTF_8)).joinToString("") { "%02x".format(it) }

        val plantillaSha256 = "811acbf1daf00657b6d2a305e07af160ec4c2867acd60b4588f7867bdf83b64b"
        assertTrue(
            sha256 != plantillaSha256,
            "El código de src/main/kotlin/com/university/dcp/Main.kt sigue siendo exactamente la plantilla monolítica inicial de la cátedra sin refactorizar. Para completar el TP 1 debés reorganizar el código modularmente en funciones o clases con responsabilidades claras manteniendo el comportamiento."
        )
    }
}
