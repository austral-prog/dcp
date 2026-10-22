package com.university.dcp

data class EventoPartida(
    val jugadorId: String,
    val puntosBase: Int,
    val multiplicador: Double,
    val esCritico: Boolean
)

data class ResumenJugador(
    val jugadorId: String,
    val puntajeTotal: Int,
    val golpesCriticos: Int,
    val mejorGolpe: Int
)

object MotorGaming {

    fun calcularPuntajeEfectivo(evento: EventoPartida): Int {
        return (evento.puntosBase * evento.multiplicador).toInt()
    }

    fun puntajeTotalEquipo(eventos: List<EventoPartida>, miembrosEquipo: Set<String>): Int {
        return eventos
            .filter { it.jugadorId in miembrosEquipo }
            .map { calcularPuntajeEfectivo(it) }
            .reduce { acumulado, puntos -> acumulado + puntos }
    }

    fun construirLeaderboard(
        eventos: List<EventoPartida>,
        puntajeMinimo: Int = 0
    ): List<ResumenJugador> {
        val porJugador = eventos.groupBy { it.jugadorId }
        val resumenes = mutableListOf<ResumenJugador>()

        for ((jugador, listaEventos) in porJugador) {
            var suma = 0
            var maximo = 0
            for (ev in listaEventos) {
                val pts = calcularPuntajeEfectivo(ev)
                suma += pts
                if (pts > maximo) {
                    maximo = pts
                }
            }
            resumenes.add(
                ResumenJugador(
                    jugadorId = jugador,
                    puntajeTotal = suma,
                    golpesCriticos = 0,
                    mejorGolpe = maximo
                )
            )
        }

        return resumenes.sortedBy { it.puntajeTotal }
    }
}
