package com.university.dcp

object DetectorRafagas {

    fun minutosEntre(tsInicio: String, tsFin: String): Long {
        val minInicio = tsInicio.substringAfter("T").substringAfter(":").toInt()
        val minFin = tsFin.substringAfter("T").substringAfter(":").toInt()
        return (minFin - minInicio).toLong()
    }

    fun detectarRafagas(
        alarmas: List<AlarmaEvento>,
        ventanaMinutos: Int = 45,
        minAlarmas: Int = 3
    ): List<Rafaga> {
        if (alarmas.size < minAlarmas) return emptyList()

        val rafagas = mutableListOf<Rafaga>()

        var i = 0
        while (i < alarmas.size) {
            var j = i + 1
            var conteo = 1
            while (j < alarmas.size) {
                val delta = minutosEntre(alarmas[i].timestamp, alarmas[j].timestamp)
                if (delta in 0..ventanaMinutos) {
                    conteo++
                    if (conteo >= minAlarmas) {
                        rafagas.add(
                            Rafaga(
                                idSensor = alarmas[i].idSensor,
                                inicio = alarmas[i].timestamp,
                                fin = alarmas[j].timestamp,
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
                val delta = actual.valor - anterior.valor
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
