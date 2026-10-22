package com.university.dcp

data class EventoPartida(
    val jugadorId: String,
    val puntosBase: Int,
    val multiplicador: Double,
    val esCritico: Boolean
) {
    init {
        require(jugadorId.isNotBlank()) { "El ID del jugador no puede estar en blanco" }
        require(puntosBase >= 0) { "Los puntos base no pueden ser negativos" }
        require(multiplicador > 0.0) { "El multiplicador debe ser mayor a cero" }
    }
}

data class ResumenJugador(
    val jugadorId: String,
    val puntajeTotal: Int,
    val golpesCriticos: Int,
    val mejorGolpe: Int
)

object MotorGaming {

    fun calcularPuntajeEfectivo(evento: EventoPartida): Int {
        val factorCritico = if (evento.esCritico) 1.5 else 1.0
        return (evento.puntosBase * evento.multiplicador * factorCritico).toInt()
    }

    fun puntajeTotalEquipo(eventos: List<EventoPartida>, miembrosEquipo: Set<String>): Int =
        eventos
            .filter { it.jugadorId in miembrosEquipo }
            .map { calcularPuntajeEfectivo(it) }
            .fold(0) { acumulado, puntos -> acumulado + puntos }

    fun construirLeaderboard(
        eventos: List<EventoPartida>,
        puntajeMinimo: Int = 0
    ): List<ResumenJugador> =
        eventos
            .groupBy { it.jugadorId }
            .map { (jugador, listaEventos) ->
                val puntajes = listaEventos.map { calcularPuntajeEfectivo(it) }
                val total = puntajes.fold(0) { acc, pts -> acc + pts }
                val criticos = listaEventos.filter { it.esCritico }.size
                val mejor = puntajes.fold(0) { max, pts -> if (pts > max) pts else max }
                ResumenJugador(
                    jugadorId = jugador,
                    puntajeTotal = total,
                    golpesCriticos = criticos,
                    mejorGolpe = mejor
                )
            }
            .filter { it.puntajeTotal >= puntajeMinimo }
            .sortedWith(compareByDescending<ResumenJugador> { it.puntajeTotal }.thenBy { it.jugadorId })
}
