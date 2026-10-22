# TP 4 — Modelado con tipos y transformación funcional

Seguís en el repositorio de la estación de monitoreo ambiental. En los TPs anteriores trabajaste sobre diseño modular (TP 1), TDD (TP 2) y depuración asistida por IA (TP 3).

En esta cuarta etapa contás con nuevas herramientas del lenguaje (**`data class` con invariantes `require`, `sealed class`, inmutabilidad con `copy`, funciones puras y transformaciones de colecciones con `map`, `filter` y `fold`**). El proyecto incorpora **tres módulos de corte ingenieril**:

1. `AnalizadorStock.kt`: gestión de inventario y reposición de repuestos para sensores (`ItemStock`, `EstadoReposicion`).
2. `ProcesadorAdn.kt`: bioinformática y análisis de muestras ambientales de ADN (`MuestraAdn`, `ClasificacionGc`).
3. `MotorGaming.kt`: cómputo de telemetría de partidas, golpes críticos y tabla de clasificación (`EventoPartida`, `ResumenJugador`).

Estos módulos tienen una **implementación inicial imperativa, incompleta y frágil** (datos sin validación de invariantes, bucles con errores de lógica, mal uso de `reduce` sobre colecciones que pueden quedar vacías y variantes de `sealed class` ignoradas).

Tu objetivo en el TP 4 es **detectar estos problemas y guiar a tu agente de IA para transformar los tres módulos en código funcional, inmutable y bien modelado**, haciendo pasar el 100% de la suite de tests automatizados.

## Cómo correrlo

Kotlin/JVM 21 con el **Gradle Wrapper**, igual que en los TPs anteriores. Requisitos: **JDK 21**.

```bash
./gradlew test           # corre la suite de tests automatizados (macOS / Linux)
.\gradlew.bat test       # Windows
./gradlew run            # ejecuta el programa principal de la estación
```

## Paso 1 — Traé tu código de los TPs anteriores (o usá el provisto)

El repositorio viene por defecto con la **versión base funcional del TP 1** en `src/main/kotlin/com/university/dcp/Main.kt`.

* **Si completaste los TPs anteriores:** podés reemplazar `Main.kt` por tus archivos refactorizados de `src/main/`. El test `ContratoSalidaTest.kt` debe seguir pasando en verde.
* **Si preferís partir de una base limpia:** podés dejar el `Main.kt` provisto por defecto y concentrarte directamente en los tres módulos funcionales nuevos.

## Paso 2 — Ejecutá y obtené los tests provistos

La cátedra entrega el proyecto con la suite completa ya incluida en `src/test/kotlin/com/university/dcp/` (se descarga automáticamente al aceptar el assignment y clonar el repositorio):

* `ContratoSalidaTest.kt`: valida que la estación siga produciendo las salidas esperadas en consola y `reporte.txt` (este test **pasa** desde el inicio).
* `AnalizadorStockTest.kt`: valida invariantes de `ItemStock` (incluyendo `copy`), clasificación con `EstadoReposicion`, cálculo de costos con `fold` sobre colecciones vacías y ajuste inmutable de precios.
* `ProcesadorAdnTest.kt`: valida invariantes de nucleótidos (`A`, `C`, `G`, `T`) en `MuestraAdn`, porcentaje GC, transcripción a ARN, clasificación con `ClasificacionGc` y conteo de motivos.
* `MotorGamingTest.kt`: valida invariantes de `EventoPartida`, cálculo puro de golpes críticos, puntaje de equipo con `fold` en listas vacías y construcción del leaderboard.

Al ejecutar `./gradlew test` por primera vez, **14 de los 16 tests van a fallar**.

> **Regla de oro:** Los tests provistos en `src/test/` **no se modifican**. Tu misión es hacer que pasen mejorando el modelado y las transformaciones en `src/main/`.

## Paso 3 — Qué debés aplicar en cada módulo

Guiá a tu agente de IA para revisar y refactorizar cada módulo aplicando las herramientas vistas en clase:

1. **Invariantes en el modelo (`init { require(...) }`):**
   * `ItemStock`: SKU y categoría no vacíos (`isNotBlank()`), `cantidad >= 0`, `precioUnitario > 0.0`, `stockMinimo >= 0`.
   * `MuestraAdn`: ID no vacío (`isNotBlank()`), `secuencia` no vacía y compuesta únicamente por `'A'`, `'C'`, `'G'`, `'T'`.
   * `EventoPartida`: `jugadorId` no vacío (`isNotBlank()`), `puntosBase >= 0`, `multiplicador > 0.0`.
2. **Modelado de alternativas con `sealed class`:**
   * En `AnalizadorStock.evaluarInventario`: contemplar `ReposicionUrgente` (`cantidad < stockMinimo`, con `costoEstimado = faltante * precioUnitario`), `ExcesoStock` (`stockMinimo > 0 && cantidad > stockMinimo * 3`) y `StockSuficiente`.
   * En `ProcesadorAdn.clasificarMuestrasPorGc`: contemplar `ContenidoBajo` (`< 40.0%`), `ContenidoNormal` (`40.0%..60.0%`) y `ContenidoAlto` (`> 60.0%`).
3. **Transformaciones funcionales (`map`, `filter`, `fold`) e inmutabilidad (`copy`):**
   * Reemplazá los bucles imperativos mutables por pipelines expresivos con `map`, `filter` y `fold`.
   * Reemplazá los usos frágiles de `reduce` por `fold(valorInicial)` para manejar con seguridad colecciones vacías sin lanzar `UnsupportedOperationException`.
   * Usá `copy` en `aplicarAjustePrecios` para actualizar precios porcentuales sin mutar ni descartar ítems cuya categoría no tenga ajuste.

## Archivos del proyecto

| Archivo | Contenido |
|---|---|
| `src/main/kotlin/com/university/dcp/AnalizadorStock.kt` | modelo y transformaciones de inventario y stock (a transformar) |
| `src/main/kotlin/com/university/dcp/ProcesadorAdn.kt` | modelo y análisis funcional de secuencias de ADN (a transformar) |
| `src/main/kotlin/com/university/dcp/MotorGaming.kt` | modelo y cómputo funcional de partidas y leaderboard (a transformar) |
| `src/main/kotlin/com/university/dcp/Main.kt` | programa principal de la estación de monitoreo |
| `src/test/kotlin/com/university/dcp/` | suite de tests de la cátedra (no modificar) |
| `datos/lecturas.csv` | lecturas crudas de entrada de la estación |
| `salida-esperada.txt` | salida de consola de referencia del contrato |
| `reporte-esperado.txt` | contenido de referencia de `reporte.txt` |
| `AGENTS.md` | reglas para el agente de IA |
| `prompt-log/` | registro obligatorio de interacciones con LLMs |

## Registro de prompts (`prompt-log/`)

Cada interacción con un LLM sobre este repositorio debe guardarse en `prompt-log/`. La política completa está en [`AGENTS.md`](AGENTS.md); el formato de cada entrada está en [`prompt-log/README.md`](prompt-log/README.md).

## Qué entregás

| Entregable | Dónde |
|---|---|
| Módulos modelados y transformados funcionalmente | `src/main/kotlin/com/university/dcp/` con `./gradlew test` en verde |
| Registro de prompts | [`prompt-log/`](prompt-log/) |

Entregar es pushear a la rama principal (o mergear tus pull requests en `main`). Hacé commits chicos (por ejemplo, un commit o rama por módulo), no uno solo con todo.

## Checklist de entrega

- [ ] `./gradlew test` da 100% verde (16/16 tests) en `main`, incluyendo `ContratoSalidaTest`.
- [ ] No modificaste ningún archivo en `src/test/`, ni `salida-esperada.txt`, ni `reporte-esperado.txt`.
- [ ] Las `data class` protegen sus invariantes con `require` en `init`.
- [ ] Las funciones de `AnalizadorStock`, `ProcesadorAdn` y `MotorGaming` son puras, inmutables y usan transformaciones funcionales (`map`, `filter`, `fold`, `copy`).
- [ ] Hay commits individuales por módulo o paso, no un solo commit masivo.
- [ ] `prompt-log/` está completo y sin secretos.
