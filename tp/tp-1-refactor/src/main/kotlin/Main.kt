import java.io.File

// Estacion de monitoreo ambiental.
// Lee las lecturas crudas de los sensores, normaliza las unidades, aplica
// la calibracion de cada sensor, calcula estadisticas por tipo de magnitud,
// detecta valores fuera de rango con su severidad, escribe un reporte por
// consola y deja una copia en reporte.txt.
//
// Codigo provisto por la catedra para el TP 1. Funciona: no lo arregles,
// reorganizalo.

var sumaT = 0.0
var cantT = 0
var minT = 9999.0
var maxT = -9999.0

var sumaH = 0.0
var cantH = 0
var minH = 9999.0
var maxH = -9999.0

var sumaP = 0.0
var cantP = 0
var minP = 9999.0
var maxP = -9999.0

var alarmas = 0
var graves = 0
var descartadas = 0
var desconocidas = 0
var total = 0

fun procesarTodo() {
    val f = File("datos/lecturas.csv")
    val ls = f.readLines()

    println("=== ESTACION DE MONITOREO ===")
    println("archivo: datos/lecturas.csv")
    println("filas leidas: " + ls.size)
    println()
    println("--- ALARMAS ---")

    var i = 0
    while (i < ls.size) {
        val l = ls[i]
        i++
        if (l.trim() == "") continue
        if (l.startsWith("#")) continue
        if (l.startsWith("id_sensor")) continue

        val p = l.split(";")
        if (p.size != 5) {
            descartadas++
            println("[descartada] fila " + i + ": se esperaban 5 campos y hay " + p.size)
            continue
        }

        val id = p[0]
        val t = p[1]
        val ts = p[2]
        val u = p[4]

        var v = 0.0
        try {
            v = p[3].toDouble()
        } catch (e: Exception) {
            descartadas++
            println("[descartada] fila " + i + ": valor no numerico '" + p[3] + "'")
            continue
        }

        total++

        if (t == "temperatura") {
            if (u == "F") v = (v - 32.0) * 5.0 / 9.0
            if (u == "K") v = v - 273.15
            // calibracion de fabrica
            if (id == "S-01") v = v + 0.3
            if (id == "S-02") v = v - 0.5
            if (id == "S-04") v = v + 1.2
            sumaT = sumaT + v
            cantT++
            if (v < minT) minT = v
            if (v > maxT) maxT = v
            if (v < 18.0 || v > 27.0) {
                alarmas++
                var sev = "LEVE"
                if (v < 18.0 - 3.0 || v > 27.0 + 3.0) {
                    sev = "GRAVE"
                    graves++
                }
                println("ALARMA " + sev + " " + id + " " + ts + " temperatura " + (Math.round(v * 100.0) / 100.0) + " C fuera de [18.0, 27.0]")
            }
        } else if (t == "humedad") {
            if (id == "S-01") v = v + 0.3
            if (id == "S-02") v = v - 0.5
            if (id == "S-04") v = v + 1.2
            sumaH = sumaH + v
            cantH++
            if (v < minH) minH = v
            if (v > maxH) maxH = v
            if (v < 30.0 || v > 70.0) {
                alarmas++
                var sev = "LEVE"
                if (v < 30.0 - 10.0 || v > 70.0 + 10.0) {
                    sev = "GRAVE"
                    graves++
                }
                println("ALARMA " + sev + " " + id + " " + ts + " humedad " + (Math.round(v * 100.0) / 100.0) + " % fuera de [30.0, 70.0]")
            }
        } else if (t == "presion") {
            if (u == "Pa") v = v / 100.0
            if (id == "S-01") v = v + 0.3
            if (id == "S-02") v = v - 0.5
            if (id == "S-04") v = v + 1.2
            sumaP = sumaP + v
            cantP++
            if (v < minP) minP = v
            if (v > maxP) maxP = v
            if (v < 980.0 || v > 1030.0) {
                alarmas++
                var sev = "LEVE"
                if (v < 980.0 - 15.0 || v > 1030.0 + 15.0) {
                    sev = "GRAVE"
                    graves++
                }
                println("ALARMA " + sev + " " + id + " " + ts + " presion " + (Math.round(v * 100.0) / 100.0) + " hPa fuera de [980.0, 1030.0]")
            }
        } else {
            desconocidas++
            println("[ignorada] fila " + i + ": tipo de sensor desconocido '" + t + "'")
        }
    }

    println()
    println("--- PROMEDIOS POR MAGNITUD ---")
    if (cantT > 0) {
        println("temperatura: n=" + cantT + " prom=" + (Math.round((sumaT / cantT) * 100.0) / 100.0) + " C min=" + (Math.round(minT * 100.0) / 100.0) + " max=" + (Math.round(maxT * 100.0) / 100.0))
    } else {
        println("temperatura: sin datos")
    }
    if (cantH > 0) {
        println("humedad: n=" + cantH + " prom=" + (Math.round((sumaH / cantH) * 100.0) / 100.0) + " % min=" + (Math.round(minH * 100.0) / 100.0) + " max=" + (Math.round(maxH * 100.0) / 100.0))
    } else {
        println("humedad: sin datos")
    }
    if (cantP > 0) {
        println("presion: n=" + cantP + " prom=" + (Math.round((sumaP / cantP) * 100.0) / 100.0) + " hPa min=" + (Math.round(minP * 100.0) / 100.0) + " max=" + (Math.round(maxP * 100.0) / 100.0))
    } else {
        println("presion: sin datos")
    }
}

// Segunda pasada: cuenta cuantas alarmas genero cada sensor para
// poder rankearlos. Vuelve a abrir el archivo porque arriba no
// guardamos nada.
fun rankingDeSensores() {
    val f2 = File("datos/lecturas.csv")
    val ls2 = f2.readLines()

    val ids = ArrayList<String>()
    val cuenta = ArrayList<Int>()

    var j = 0
    while (j < ls2.size) {
        val l = ls2[j]
        j++
        if (l.trim() == "") continue
        if (l.startsWith("#")) continue
        if (l.startsWith("id_sensor")) continue

        val p = l.split(";")
        if (p.size != 5) continue

        val id = p[0]
        val t = p[1]
        val u = p[4]

        var v = 0.0
        try {
            v = p[3].toDouble()
        } catch (e: Exception) {
            continue
        }

        var esAlarma = false
        if (t == "temperatura") {
            if (u == "F") v = (v - 32.0) * 5.0 / 9.0
            if (u == "K") v = v - 273.15
            if (id == "S-01") v = v + 0.3
            if (id == "S-02") v = v - 0.5
            if (id == "S-04") v = v + 1.2
            if (v < 18.0 || v > 27.0) esAlarma = true
        } else if (t == "humedad") {
            if (id == "S-01") v = v + 0.3
            if (id == "S-02") v = v - 0.5
            if (id == "S-04") v = v + 1.2
            if (v < 30.0 || v > 70.0) esAlarma = true
        } else if (t == "presion") {
            if (u == "Pa") v = v / 100.0
            if (id == "S-01") v = v + 0.3
            if (id == "S-02") v = v - 0.5
            if (id == "S-04") v = v + 1.2
            if (v < 980.0 || v > 1030.0) esAlarma = true
        }

        if (!esAlarma) continue

        var pos = -1
        var k = 0
        while (k < ids.size) {
            if (ids[k] == id) pos = k
            k++
        }
        if (pos == -1) {
            ids.add(id)
            cuenta.add(1)
        } else {
            cuenta[pos] = cuenta[pos] + 1
        }
    }

    println()
    println("--- SENSORES CON MAS ALARMAS ---")
    if (ids.size == 0) {
        println("ninguno")
        return
    }
    var n = 0
    while (n < ids.size) {
        var mejor = 0
        var m = 1
        while (m < ids.size) {
            if (cuenta[m] > cuenta[mejor]) mejor = m
            m++
        }
        println(ids[mejor] + ": " + cuenta[mejor] + " alarmas")
        cuenta[mejor] = -1
        n++
    }
}

// Deja una copia del resumen en disco para el informe semanal.
fun escribirReporte() {
    val sb = StringBuilder()
    sb.append("REPORTE DE LA ESTACION DE MONITOREO\n")
    sb.append("===================================\n")
    sb.append("temperatura: n=" + cantT + " prom=" + (Math.round((sumaT / cantT) * 100.0) / 100.0) + " C min=" + (Math.round(minT * 100.0) / 100.0) + " max=" + (Math.round(maxT * 100.0) / 100.0) + "\n")
    sb.append("humedad: n=" + cantH + " prom=" + (Math.round((sumaH / cantH) * 100.0) / 100.0) + " % min=" + (Math.round(minH * 100.0) / 100.0) + " max=" + (Math.round(maxH * 100.0) / 100.0) + "\n")
    sb.append("presion: n=" + cantP + " prom=" + (Math.round((sumaP / cantP) * 100.0) / 100.0) + " hPa min=" + (Math.round(minP * 100.0) / 100.0) + " max=" + (Math.round(maxP * 100.0) / 100.0) + "\n")
    sb.append("\n")
    sb.append("lecturas validas: " + total + "\n")
    sb.append("alarmas: " + alarmas + " (graves: " + graves + ")\n")
    sb.append("filas descartadas: " + descartadas + "\n")
    File("reporte.txt").writeText(sb.toString())
}

fun main() {
    procesarTodo()
    rankingDeSensores()
    escribirReporte()

    println()
    println("--- RESUMEN ---")
    println("lecturas validas: " + total)
    println("alarmas: " + alarmas + " (graves: " + graves + ")")
    println("filas descartadas: " + descartadas)
    println("tipos desconocidos: " + desconocidas)
    println("reporte escrito en reporte.txt")
}
