package com.university.dcp

import java.io.File

fun procesarArchivo(archivo: File = File("datos/lecturas.csv")) {
    val lineas = if (archivo.exists()) archivo.readLines() else emptyList()

    println("=== ESTACION DE MONITOREO ===")
    println("archivo: ${archivo.path}")
    println("filas leidas: ${lineas.size}")
    println()
    println("--- ALARMAS ---")

    val lecturasValidas = mutableListOf<Lectura>()
    val lecturasCalibradas = mutableMapOf<String, MutableList<Double>>()
    val alarmas = mutableListOf<Alarma>()

    var total = 0
    var descartadas = 0
    var ignoradas = 0

    for ((index, linea) in lineas.withIndex()) {
        val numeroFila = index + 1
        when (val res = LectorLecturas.procesarLinea(numeroFila, linea)) {
            null -> Unit
            is ResultadoLinea.Descartada -> {
                descartadas++
                println("[descartada] fila ${res.fila}: ${res.motivo}")
            }
            is ResultadoLinea.Ignorada -> {
                total++
                ignoradas++
                println("[ignorada] fila ${res.fila}: ${res.motivo}")
            }
            is ResultadoLinea.Valida -> {
                total++
                val lectura = res.lectura
                lecturasValidas.add(lectura)

                val valorNormalizado = ConversorUnidades.convertir(lectura.tipo, lectura.valor, lectura.unidad)
                val valorCalibrado = Calibrador.calibrar(lectura.idSensor, valorNormalizado)

                lecturasCalibradas.getOrPut(lectura.tipo) { mutableListOf() }.add(valorCalibrado)

                val alarma = ClasificadorAlarmas.evaluar(lectura, valorCalibrado)
                if (alarma != null) {
                    alarmas.add(alarma)
                    val valorRedondeado = Math.round(alarma.valor * 100.0) / 100.0
                    println("ALARMA ${alarma.severidad} ${alarma.idSensor} ${alarma.timestamp} ${alarma.tipo} $valorRedondeado ${alarma.unidadBase} fuera de [${alarma.rangoMin}, ${alarma.rangoMax}]")
                }
            }
        }
    }

    println()
    println("--- PROMEDIOS POR MAGNITUD ---")
    val tiposMagnitud = listOf("temperatura", "humedad", "presion")
    val estadisticas = mutableListOf<Estadistica>()

    for (tipo in tiposMagnitud) {
        val valores = lecturasCalibradas[tipo] ?: emptyList()
        val est = CalculadorEstadisticas.calcular(tipo, valores)
        if (est != null) {
            estadisticas.add(est)
            println("$tipo: n=${est.cantidad} prom=${est.promedio} ${est.unidadBase} min=${est.min} max=${est.max}")
        } else {
            println("$tipo: sin datos")
        }
    }

    println()
    println("--- SENSORES CON MAS ALARMAS ---")
    val ranking = RankingSensores.generarRanking(alarmas)
    if (ranking.isEmpty()) {
        println("ninguno")
    } else {
        for (item in ranking) {
            println("${item.idSensor}: ${item.alarmas} alarmas")
        }
    }

    val graves = alarmas.count { it.severidad == Severidad.GRAVE }

    // Generacion de reporte en disco
    val sb = StringBuilder()
    sb.append("REPORTE DE LA ESTACION DE MONITOREO\n")
    sb.append("===================================\n")
    for (est in estadisticas) {
        sb.append("${est.tipo}: n=${est.cantidad} prom=${est.promedio} ${est.unidadBase} min=${est.min} max=${est.max}\n")
    }
    sb.append("\n")
    sb.append("lecturas validas: $total\n")
    sb.append("alarmas: ${alarmas.size} (graves: $graves)\n")
    sb.append("filas descartadas: $descartadas\n")
    File("reporte.txt").writeText(sb.toString())

    println()
    println("--- RESUMEN ---")
    println("lecturas validas: $total")
    println("alarmas: ${alarmas.size} (graves: $graves)")
    println("filas descartadas: $descartadas")
    println("tipos desconocidos: $ignoradas")
    println("reporte escrito en reporte.txt")
}

fun main() {
    procesarArchivo()
}
