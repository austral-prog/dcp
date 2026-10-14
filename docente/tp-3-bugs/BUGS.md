# Documentación Docente — TP 3 (Depuración Asistida por IA)

Este documento es **exclusivo para el cuerpo docente** y no forma parte del código entregado a los alumnos.

---

## 1. Objetivo Pedagógico del TP 3

El TP 3 busca que los estudiantes ejerciten el flujo de trabajo de **detección, diagnóstico y corrección de bugs asistido por agentes de IA**.
A diferencia de los TPs anteriores (donde el código funcionaba y se refactorizaba o extendía con TDD), en este TP el repositorio de partida contiene una base de código estructurada pero con **bugs intencionales**.

Los alumnos disponen de:
1. Una suite de pruebas unitarias y de integración en `src/test/kotlin/` que falla en el código de partida (`./gradlew test`).
2. Las salidas esperadas de referencia (`salida-esperada.txt` y `reporte-esperado.txt`), junto con el test de contrato `ContratoSalidaTest`.
3. Un agente de IA (siguiendo las reglas de `AGENTS.md` y `CLAUDE.md`) con el que deben interactuar para diagnosticar cada falla, diseñar la corrección mínima y verificar que los tests pasen sin introducir regresiones.
4. La obligación de registrar todo el diálogo en `prompt-log/`.

---

## 2. Inventario de Bugs Intencionales

El código de partida contiene **5 bugs intencionales** distribuidos en distintos módulos del dominio:

### Bug 1: Error en la conversión de unidades de presión (`kPa` a `hPa`)
- **Archivo**: `src/main/kotlin/com/university/dcp/ConversorUnidades.kt`
- **Función**: `convertir(tipo: String, valor: Double, unidad: String)`
- **Descripción**: La relación física es $1\text{ kPa} = 10\text{ hPa} = 1000\text{ Pa}$. El código de partida divide por 10 (`valor / 10.0`) en lugar de multiplicar por 10 (`valor * 10.0`).
- **Síntoma**: Un valor de $101.3\text{ kPa}$ se transforma en $10.13\text{ hPa}$ en lugar de $1013.0\text{ hPa}$.
- **Test que falla**: `ConversorUnidadesTest."conversion de kPa a hPa multiplica por 10"`.
- **Corrección**:
  ```kotlin
  // Antes (bug):
  "kPa" -> valor / 10.0
  // Corrección:
  "kPa" -> valor * 10.0
  ```

---

### Bug 2: Signo invertido en la calibración de fábrica de sensor `S-02`
- **Archivo**: `src/main/kotlin/com/university/dcp/Calibrador.kt`
- **Función**: `offsetCalibracion(idSensor: String)`
- **Descripción**: La especificación histórica de la estación ambiental indica que el sensor `S-02` debe restar $0.5$ (offset de $-0.5$). En el código de partida el offset está seteado como $+0.5$.
- **Síntoma**: Todas las lecturas del sensor `S-02` quedan $1.0^\circ\text{C}$ por encima de su valor calibrado real, alterando severidades de alarma y estadísticas globales de temperatura.
- **Test que falla**: `CalibradorTest."sensor S-02 resta 0_5 por calibracion de fabrica"`.
- **Corrección**:
  ```kotlin
  // Antes (bug):
  "S-02" -> 0.5
  // Corrección:
  "S-02" -> -0.5
  ```

---

### Bug 3: Condición de borde inclusiva en umbral superior de temperatura
- **Archivo**: `src/main/kotlin/com/university/dcp/ClasificadorAlarmas.kt`
- **Función**: `evaluar(lectura: Lectura, valorCalibrado: Double)`
- **Descripción**: El rango de temperatura normal es $[18.0, 27.0]^\circ\text{C}$. Un valor de exactamente $27.0^\circ\text{C}$ se encuentra dentro del rango admisible y **no** es alarma. El código de partida utiliza `>= 27.0` para temperatura en lugar de `> 27.0`.
- **Síntoma**: Una lectura de $27.0^\circ\text{C}$ genera erróneamente una `ALARMA LEVE`.
- **Test que falla**: `ClasificadorAlarmasTest."borde - 27 grados exactos no genera alarma"`.
- **Corrección**:
  ```kotlin
  // Antes (bug):
  "temperatura" -> valorCalibrado < umbral.minNormal || valorCalibrado >= umbral.maxNormal
  // Corrección:
  valorCalibrado < umbral.minNormal || valorCalibrado > umbral.maxNormal
  ```

---

### Bug 4: Inicialización de valor mínimo en `0.0` al acumular estadísticas
- **Archivo**: `src/main/kotlin/com/university/dcp/CalculadorEstadisticas.kt`
- **Función**: `calcular(tipo: String, valores: List<Double>)`
- **Descripción**: La variable `min` se inicializa en `0.0` en lugar de `Double.MAX_VALUE` (o `valores.first()`). Dado que todas las magnitudes analizadas (temperaturas $\approx 14\text{ a }34^\circ\text{C}$, humedad $\approx 28\text{ a }82\%$, presión $\approx 979\text{ a }1033\text{ hPa}$) son estrictamente positivas, la condición `v < min` nunca se cumple.
- **Síntoma**: En el reporte impreso y en `reporte.txt`, el mínimo de todas las magnitudes figura incorrectamente como `min=0.0`.
- **Test que falla**: `CalculadorEstadisticasTest."minimo de lecturas no queda fijado en cero para valores positivos"`.
- **Corrección**:
  ```kotlin
  // Antes (bug):
  var min = 0.0
  // Corrección:
  var min = Double.MAX_VALUE
  ```

---

### Bug 5: Orden de ordenamiento ascendente en el ranking de sensores por alarma
- **Archivo**: `src/main/kotlin/com/university/dcp/RankingSensores.kt`
- **Función**: `generarRanking(alarmas: List<Alarma>)`
- **Descripción**: La consigna de la estación requiere listar los sensores de mayor a menor cantidad de alarmas registradas (orden descendente). El código de partida ordena con `compareBy` ascendente.
- **Síntoma**: El reporte muestra primero el sensor con menor cantidad de alarmas (`S-05: 2 alarmas`) y al final el más crítico (`S-01: 7 alarmas`).
- **Test que falla**: `RankingSensoresTest."ordena sensores por cantidad de alarmas de mayor a menor"`.
- **Corrección**:
  ```kotlin
  // Antes (bug):
  return items.sortedWith(compareBy({ it.alarmas }, { it.idSensor }))
  // Corrección:
  return items.sortedWith(compareByDescending<RankingItem> { it.alarmas }.thenBy { it.idSensor })
  ```

---

## 3. Test de Contrato (`ContratoSalidaTest`)

El archivo `src/test/kotlin/com/university/dcp/ContratoSalidaTest.kt` corre el programa completo sobre `datos/lecturas.csv` y compara la consola y `reporte.txt` con las salidas de referencia `salida-esperada.txt` y `reporte-esperado.txt`.
Debido a la presencia de los bugs 2, 4 y 5, este test falla en el código de partida y pasa al 100% cuando los bugs están corregidos.

---

## 4. Solución de Referencia y Verificación

La solución de referencia con todos los bugs corregidos se encuentra en:
```text
docente/tp-3-bugs/solucion/src/main/kotlin/com/university/dcp/
```

Para verificar automáticamente que la solución de referencia pasa todos los tests (22 de 22):
```bash
./docente/tp-3-bugs/verificar-solucion.sh
```
El script copia temporalmente la solución de referencia en `tp/tp-3-bugs/`, ejecuta `./gradlew test --rerun`, confirma que no hay fallas y restaura el código de partida sin alterar el árbol de trabajo.
