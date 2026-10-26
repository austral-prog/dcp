package com.university.dcp.smarthome

fun main() {
    val casa = CasaInteligente("Hogar Central")

    val luzLiving = LuzInteligente("luz-1", "Luz Living", potenciaBaseWatts = 60.0)
    val termostato = Termostato("termo-1", "Termostato Principal", temperaturaInicial = 26.0)
    val enchufeLavarropas = EnchufeInteligente("ench-1", "Enchufe Lavarropas", potenciaMaximaSoportadaWatts = 2500.0)

    casa.registrarDispositivo(luzLiving)
    casa.registrarDispositivo(termostato)
    casa.registrarDispositivo(enchufeLavarropas)

    println("=== Simulación Smart Home ===")
    println("Casa: ${casa.nombre}")
    println("Dispositivos registrados: ${casa.dispositivos.size}")

    casa.encenderTodo()
    println("Dispositivos encendidos tras encenderTodo(): ${casa.dispositivosEncendidos().size}")

    println("Simulando escenario de control...")
    println("Fin de la simulación.")
}
