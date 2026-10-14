package com.university.dcp

import java.time.Duration
import java.time.LocalDateTime

object DetectorRafagas {

    fun minutosEntre(tsInicio: String, tsFin: String): Long {
        val t1 = LocalDateTime.parse(tsInicio)
        val t2 = LocalDateTime.parse(tsFin)
        return Duration.between(t1, t2).toMinutes()
    }

    fun detectarRafagas(
        alarmas: List<AlarmaEvento>,
        ventanaMinutos: Int = 45,
        minAlarmas: Int = 3
    ): List<Rafaga> {
        val rafagas = mutableListOf<Rafaga>()
        val porSensor = alarmas.groupBy { it.idSensor }

        for ((sensor, listaAlarmas) in porSensor) {
            if (listaAlarmas.size < minAlarmas) continue
            var i = 0
            while (i < listaAlarmas.size) {
                var j = i + 1
                var conteo = 1
                while (j < listaAlarmas.size) {
                    val delta = minutosEntre(listaAlarmas[i].timestamp, listaAlarmas[j].timestamp)
                    if (delta in 0..ventanaMinutos) {
                        conteo++
                        if (conteo >= minAlarmas) {
                            rafagas.add(
                                Rafaga(
                                    idSensor = sensor,
                                    inicio = listaAlarmas[i].timestamp,
                                    fin = listaAlarmas[j].timestamp,
                                    cantidadAlarmas = conteo
                                )
                            )
                            break
                        }
                    }
                    j++
                }
                i++
            }
        }

        return rafagas
    }

    fun detectarSaltosTermicos(
        lecturas: List<MedicionSimple>,
        umbralDelta: Double = 5.0
    ): List<SaltoTermico> {
        val saltos = mutableListOf<SaltoTermico>()
        val porSensor = lecturas.filter { it.tipo == "temperatura" }.groupBy { it.idSensor }

        for ((sensor, lista) in porSensor) {
            for (i in 1 until lista.size) {
                val anterior = lista[i - 1]
                val actual = lista[i]
                val delta = Math.abs(actual.valor - anterior.valor)
                if (delta >= umbralDelta) {
                    val deltaRedondeado = Math.round(delta * 100.0) / 100.0
                    saltos.add(
                        SaltoTermico(
                            idSensor = sensor,
                            timestampAnterior = anterior.timestamp,
                            timestampActual = actual.timestamp,
                            delta = deltaRedondeado
                        )
                    )
                }
            }
        }

        return saltos
    }
}
