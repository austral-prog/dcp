package com.university.dcp

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class MotorGamingTest {

    @Test
    fun `EventoPartida valida sus invariantes al construirse`() {
        assertFailsWith<IllegalArgumentException>("Debe rechazar jugadorId en blanco") {
            EventoPartida(jugadorId = "", puntosBase = 100, multiplicador = 1.0, esCritico = false)
        }
        assertFailsWith<IllegalArgumentException>("Debe rechazar puntosBase negativos") {
            EventoPartida(jugadorId = "player-1", puntosBase = -10, multiplicador = 1.0, esCritico = false)
        }
        assertFailsWith<IllegalArgumentException>("Debe rechazar multiplicador menor o igual a cero") {
            EventoPartida(jugadorId = "player-1", puntosBase = 100, multiplicador = 0.0, esCritico = false)
        }
    }

    @Test
    fun `calcularPuntajeEfectivo aplica bonus de 50 por ciento en golpes criticos`() {
        val normal = EventoPartida(jugadorId = "p1", puntosBase = 100, multiplicador = 2.0, esCritico = false)
        val critico = EventoPartida(jugadorId = "p1", puntosBase = 100, multiplicador = 2.0, esCritico = true)

        assertEquals(200, MotorGaming.calcularPuntajeEfectivo(normal))
        assertEquals(300, MotorGaming.calcularPuntajeEfectivo(critico), "Un golpe critico debe multiplicar por 1.5 adicional")
    }

    @Test
    fun `puntajeTotalEquipo suma los puntajes efectivos de los miembros del equipo`() {
        val eventos = listOf(
            EventoPartida("alice", puntosBase = 100, multiplicador = 1.0, esCritico = true),  // 150
            EventoPartida("bob", puntosBase = 80, multiplicador = 2.0, esCritico = false),    // 160
            EventoPartida("charlie", puntosBase = 200, multiplicador = 1.0, esCritico = false) // otro equipo
        )

        val total = MotorGaming.puntajeTotalEquipo(eventos, setOf("alice", "bob"))
        assertEquals(310, total)
    }

    @Test
    fun `puntajeTotalEquipo devuelve 0 cuando el equipo no registra eventos`() {
        val eventos = listOf(
            EventoPartida("charlie", puntosBase = 200, multiplicador = 1.0, esCritico = false)
        )

        val totalSinEventos = MotorGaming.puntajeTotalEquipo(eventos, setOf("alice", "bob"))
        assertEquals(0, totalSinEventos, "Cuando no hay eventos del equipo debe devolver 0 sin lanzar excepcion")
        assertEquals(0, MotorGaming.puntajeTotalEquipo(emptyList(), setOf("alice")))
    }

    @Test
    fun `construirLeaderboard filtra por puntaje minimo, cuenta criticos y ordena de mayor a menor`() {
        val eventos = listOf(
            EventoPartida("alice", puntosBase = 100, multiplicador = 1.0, esCritico = false), // 100
            EventoPartida("alice", puntosBase = 100, multiplicador = 1.0, esCritico = true),  // 150 -> total 250, 1 critico, mejor 150
            EventoPartida("bob", puntosBase = 200, multiplicador = 1.0, esCritico = true),    // 300 -> total 300, 1 critico, mejor 300
            EventoPartida("charlie", puntosBase = 50, multiplicador = 1.0, esCritico = false) // 50  -> total 50 (< minimo 100)
        )

        val leaderboard = MotorGaming.construirLeaderboard(eventos, puntajeMinimo = 100)
        assertEquals(
            listOf(
                ResumenJugador(jugadorId = "bob", puntajeTotal = 300, golpesCriticos = 1, mejorGolpe = 300),
                ResumenJugador(jugadorId = "alice", puntajeTotal = 250, golpesCriticos = 1, mejorGolpe = 150)
            ),
            leaderboard
        )
    }
}
