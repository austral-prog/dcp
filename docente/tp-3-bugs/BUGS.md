# Documentación Docente — TP 3 (Depuración Asistida por IA)

Este documento es **exclusivo para el cuerpo docente** y no forma parte del código entregado a los alumnos.

---

## 1. Diseño Pedagógico del TP 3

El TP 3 está diseñado para que el alumno trabaje sobre una situación típica de la industria:
* **Base existente:** el alumno puede importar su código refactorizado y tests del TP 2. Para aquellos alumnos que no hayan completado el TP 2 o hayan tenido dificultades, el repositorio **incluye por defecto el código original y funcional del TP 1**, garantizando que nadie quede bloqueado.
* **Componentes nuevos provistos por la cátedra:** la cátedra introduce dos módulos nuevos con API cerrada en `src/main/kotlin/com/university/dcp/`:
  1. `DetectorRafagas.kt`: para detección de ráfagas de alarmas en ventanas temporales y saltos térmicos bruscos.
  2. `ExportadorJson.kt`: para exportar métricas e incidentes a formato JSON estricto (`reporte.json`).
* **Bugs intencionales:** estos dos módulos provistos contienen **5 bugs intencionales**.
* **Suite de tests:** la cátedra provee tests automatizados en `src/test/kotlin/com/university/dcp/`. El test de contrato histórico (`ContratoSalidaTest.kt`) pasa desde el inicio, mientras que los tests de los nuevos módulos fallan debido a los bugs.
* **Objetivo:** el alumno usa un agente de IA para diagnosticar la causa raíz de cada falla, aplicar correcciones mínimas y lograr que el 100% de la suite pase (`./gradlew test`).

---

## 2. Inventario de Bugs Intencionales

### Bug 1: Cálculo erróneo de minutos entre timestamps con cambio de hora
- **Archivo**: `src/main/kotlin/com/university/dcp/DetectorRafagas.kt`
- **Función**: `minutosEntre(tsInicio: String, tsFin: String)`
- **Descripción**: La función extrae manualmente los minutos posteriores a los dos puntos (`:`) y los resta (`minFin - minInicio`), ignorando por completo la diferencia de horas.
- **Síntoma**: Entre `2026-09-28T08:45` y `2026-09-28T09:15` (diferencia real de 30 minutos), la función calcula $15 - 45 = -30$, arruinando la evaluación de la ventana de 45 minutos.
- **Test que falla**: `DetectorRafagasTest."minutosEntre con cambio de hora calcula la diferencia real"`.
- **Corrección**:
  ```kotlin
  // Antes (bug):
  val minInicio = tsInicio.substringAfter("T").substringAfter(":").toInt()
  val minFin = tsFin.substringAfter("T").substringAfter(":").toInt()
  return (minFin - minInicio).toLong()

  // Corrección:
  val t1 = java.time.LocalDateTime.parse(tsInicio)
  val t2 = java.time.LocalDateTime.parse(tsFin)
  return java.time.Duration.between(t1, t2).toMinutes()
  ```

---

### Bug 2: Falta de aislamiento por sensor al detectar ráfagas de alarmas
- **Archivo**: `src/main/kotlin/com/university/dcp/DetectorRafagas.kt`
- **Función**: `detectarRafagas(alarmas: List<AlarmaEvento>, ventanaMinutos: Int, minAlarmas: Int)`
- **Descripción**: La especificación exige que una ráfaga esté compuesta por $\ge 3$ alarmas de un **mismo sensor**. El código recorre la lista completa de alarmas sin agrupar ni verificar que `alarmas[i].idSensor == alarmas[j].idSensor`.
- **Síntoma**: Alarmas aisladas emitidas por sensores diferentes en un intervalo corto se combinan erróneamente en una ráfaga falsa atribuida al primer sensor.
- **Test que falla**: `DetectorRafagasTest."alarmas de distintos sensores no se mezclan en una rafaga"`.
- **Corrección**:
  Agrupar previamente por sensor (`alarmas.groupBy { it.idSensor }`) o exigir que las alarmas de la ventana compartan el mismo `idSensor`.

---

### Bug 3: Falta de valor absoluto en la detección de saltos térmicos bruscos
- **Archivo**: `src/main/kotlin/com/university/dcp/DetectorRafagas.kt`
- **Función**: `detectarSaltosTermicos(lecturas: List<MedicionSimple>, umbralDelta: Double)`
- **Descripción**: Calcula la variación entre lecturas consecutivas como `actual.valor - anterior.valor`. Al no aplicar `Math.abs()`, si la temperatura sufre una caída drástica (por ejemplo, de $30^\circ\text{C}$ a $22^\circ\text{C}$, delta $-8^\circ\text{C}$), el valor resulta menor al umbral positivo ($5.0^\circ\text{C}$) y la anomalía se ignora.
- **Síntoma**: Solo se detectan aumentos bruscos de temperatura; las caídas térmicas quedan sin registrar.
- **Test que falla**: `DetectorRafagasTest."caida brusca de temperatura se detecta como salto termico"`.
- **Corrección**:
  ```kotlin
  // Antes (bug):
  val delta = actual.valor - anterior.valor

  // Corrección:
  val delta = Math.abs(actual.valor - anterior.valor)
  ```

---

### Bug 4: Coma final (*trailing comma*) en arrays JSON
- **Archivo**: `src/main/kotlin/com/university/dcp/ExportadorJson.kt`
- **Función**: `exportar(reporte: ReporteJsonData)`
- **Descripción**: Al serializar las listas de ráfagas y saltos térmicos, el bucle concatena una coma `,` luego de cada elemento, dejando una coma inmediatamente antes de cerrar el array con `]`. En el estándar JSON (RFC 8259), las *trailing commas* son inválidas.
- **Síntoma**: Cualquier parser estricto de JSON rechaza el documento generado.
- **Test que falla**: `ExportadorJsonTest."el json generado no contiene trailing commas en los arrays"`.
- **Corrección**:
  Utilizar `joinToString(",\n")` o controlar que el último elemento no lleve coma.

---

### Bug 5: Campos numéricos serializados con comillas como strings
- **Archivo**: `src/main/kotlin/com/university/dcp/ExportadorJson.kt`
- **Función**: `exportar(reporte: ReporteJsonData)`
- **Descripción**: Los campos de conteo entero `lecturas_validas` y `total_alarmas` se formatean entre comillas dobles: `"lecturas_validas": "${reporte.lecturasValidas}"`.
- **Síntoma**: El JSON exportado tipifica métricas cuantitativas como texto en vez de enteros numéricos.
- **Test que falla**: `ExportadorJsonTest."los campos numericos se serializan como numeros sin comillas"`.
- **Corrección**:
  ```kotlin
  // Antes (bug):
  sb.append("  \"lecturas_validas\": \"${reporte.lecturasValidas}\",\n")
  sb.append("  \"total_alarmas\": \"${reporte.totalAlarmas}\",\n")

  // Corrección:
  sb.append("  \"lecturas_validas\": ${reporte.lecturasValidas},\n")
  sb.append("  \"total_alarmas\": ${reporte.totalAlarmas},\n")
  ```

---

## 3. Solución de Referencia y Verificación

La solución de referencia con todos los bugs corregidos se encuentra en:
```text
docente/tp-3-bugs/solucion/src/main/kotlin/com/university/dcp/
```

Para verificar automáticamente que la solución de referencia pasa el 100% de los tests:
```bash
./docente/tp-3-bugs/verificar-solucion.sh
```
El script copia temporalmente la solución de referencia en `tp/tp-3-bugs/`, ejecuta `./gradlew test --rerun`, confirma que no hay fallas (10/10 tests pasan) y restaura el código de partida sin alterar el árbol de trabajo.
