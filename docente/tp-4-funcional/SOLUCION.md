# Documentación Docente — TP 4 (Modelado con Tipos y Transformación Funcional)

Este documento es **exclusivo para el cuerpo docente** y no forma parte del código entregado a los alumnos.

---

## 1. Diseño Pedagógico del TP 4

El TP 4 pone en práctica los conceptos de la **Clase 4** (*Data classes, funciones puras y transformación de colecciones*):
- **Base existente:** igual que en el TP 3, el repositorio incluye por defecto el código funcional del TP 1 en `Main.kt` (con `ContratoSalidaTest.kt` pasando desde el inicio) y permite a quienes deseen traer su código modular de los TPs anteriores.
- **Tres módulos de impronta ingenieril provistos por la cátedra:**
  1. `AnalizadorStock.kt`: gestión de inventario y repuestos de sensores (`ItemStock`, `sealed class EstadoReposicion`).
  2. `ProcesadorAdn.kt`: bioinformática y análisis de muestras ambientales de ADN (`MuestraAdn`, `sealed class ClasificacionGc`).
  3. `MotorGaming.kt`: cómputo de partidas, combos críticos y leaderboard (`EventoPartida`, `ResumenJugador`).
- **Código de partida imperativo / incompleto:** los tres módulos tienen implementaciones iniciales imperativas, sin protección de invariantes (`require`), con mal uso de `reduce` en colecciones vacías, pérdida de elementos al transformar colecciones y variantes de `sealed class` no contempladas.
- **Objetivo del alumno:** guiar a su agente de IA para detectar estos problemas, proteger invariantes en los modelos con `init { require(...) }`, preservar la inmutabilidad con `copy`, y reescribir la lógica como funciones puras usando pipelines de `map`, `filter` y `fold` hasta que los 16 tests pasen en verde.

---

## 2. Detalle de Casos por Módulo

### Módulo 1: `AnalizadorStock.kt` (`AnalizadorStockTest`)

1. **Invariantes de `ItemStock` (`init { require(...) }`)**:
   - **Problema en starter:** `ItemStock` no valida sus propiedades al construirse. Permite `sku` o `categoria` en blanco, `cantidad < 0`, `precioUnitario <= 0.0` y `stockMinimo < 0`.
   - **Solución esperada:** bloque `init` con `require` para cada invariante (que también protege invocaciones a `.copy()`).
2. **Clasificación con `sealed class EstadoReposicion` (`evaluarInventario`)**:
   - **Problema en starter:** bucle imperativo que calcula `costoEstimado = item.precioUnitario` (en vez de `faltante * item.precioUnitario`) y omite por completo la rama `ExcesoStock` cuando `cantidad > stockMinimo * 3` (con `stockMinimo > 0`).
   - **Solución esperada:** transformar con `items.map { when { ... } }` contemplando las tres variantes de `EstadoReposicion`.
3. **Caso borde `fold` vs `reduce` (`costoTotalReposicionPorCategoria`)**:
   - **Problema en starter:** utiliza `.reduce { acumulado, costo -> acumulado + costo }` sobre la lista filtrada. Cuando una categoría no tiene ítems por debajo del stock mínimo, `reduce` lanza `UnsupportedOperationException`.
   - **Solución esperada:** usar `.fold(0.0) { acumulado, costo -> acumulado + costo }`.
4. **Transformación inmutable con `copy` (`aplicarAjustePrecios`)**:
   - **Problema en starter:** descarta los ítems cuya categoría no figura en el mapa `porcentajesPorCategoria` y suma el porcentaje directo (`precioUnitario + pct`) en lugar de aplicar variación porcentual (`precioUnitario * (1.0 + pct / 100.0)`).
   - **Solución esperada:** `items.map { item -> val pct = porcentajesPorCategoria[item.categoria] ?: 0.0; item.copy(...) }`.

---

### Módulo 2: `ProcesadorAdn.kt` (`ProcesadorAdnTest`)

1. **Invariantes de `MuestraAdn` (`init { require(...) }`)**:
   - **Problema en starter:** no valida que `id` no esté en blanco, que `secuencia` no sea vacía ni que contenga únicamente nucleótidos válidos (`'A'`, `'C'`, `'G'`, `'T'`).
   - **Solución esperada:** bloque `init` con `require(id.isNotBlank())`, `require(secuencia.isNotEmpty())` y `require(secuencia.all { it in "ACGT" })`.
2. **Cálculo funcional de contenido GC (`calcularPorcentajeGc`)**:
   - **Problema en starter:** bucle imperativo que solo cuenta `'G'` e ignora `'C'`.
   - **Solución esperada:** contar tanto `'G'` como `'C'` con `fold` o `count` sobre la secuencia.
3. **Transcripción de ADN a ARN (`transcribirARna`)**:
   - **Problema en starter:** reemplaza `'A'` por `'U'` en lugar de reemplazar timina (`'T'`) por uracilo (`'U'`).
   - **Solución esperada:** `muestras.associate { m -> m.id to m.secuencia.map { if (it == 'T') 'U' else it }.joinToString("") }`.
4. **Clasificación con `sealed class ClasificacionGc` (`clasificarMuestrasPorGc`)**:
   - **Problema en starter:** divide únicamente en `< 50.0` (`ContenidoBajo`) y `>= 50.0` (`ContenidoAlto`), omitiendo la variante `ContenidoNormal` (`40.0..60.0`).
   - **Solución esperada:** `muestras.map { ... }` clasificando `< 40.0` como `ContenidoBajo`, `<= 60.0` como `ContenidoNormal` y `> 60.0` como `ContenidoAlto`.
5. **Pipeline `filter` + `map` + `fold` (`contarBasesTotalesConMotivo`)**:
   - **Problema en starter:** suma la longitud de todas las muestras sin filtrar aquellas cuya secuencia contiene el `motivo`.
   - **Solución esperada:** `muestras.filter { it.secuencia.contains(motivo) }.map { it.secuencia.length }.fold(0) { acc, len -> acc + len }`.

---

### Módulo 3: `MotorGaming.kt` (`MotorGamingTest`)

1. **Invariantes de `EventoPartida` (`init { require(...) }`)**:
   - **Problema en starter:** no valida `jugadorId.isNotBlank()`, `puntosBase >= 0` ni `multiplicador > 0.0`.
   - **Solución esperada:** bloque `init` con `require`.
2. **Función pura de puntaje crítico (`calcularPuntajeEfectivo`)**:
   - **Problema en starter:** ignora el flag `esCritico`, omitiendo el multiplicador extra de `1.5x`.
   - **Solución esperada:** aplicar factor `1.5` cuando `evento.esCritico` es `true`.
3. **Agregación segura con `fold` (`puntajeTotalEquipo`)**:
   - **Problema en starter:** utiliza `.reduce` luego de filtrar por `miembrosEquipo`, lanzando `UnsupportedOperationException` cuando el equipo no registra eventos.
   - **Solución esperada:** reemplazar `.reduce` por `.fold(0) { acumulado, puntos -> acumulado + puntos }`.
4. **Pipeline de Leaderboard (`construirLeaderboard`)**:
   - **Problema en starter:** deja `golpesCriticos = 0`, no filtra por `puntajeTotal >= puntajeMinimo` y ordena de menor a mayor (`sortedBy`).
   - **Solución esperada:** agrupar por jugador, resumir con operaciones funcionales (`map`, `filter`, `fold`), filtrar por `puntajeMinimo` y ordenar de mayor a menor `puntajeTotal`.

---

## 3. Solución de Referencia y Verificación

La solución de referencia se encuentra en:
```text
docente/tp-4-funcional/solucion/src/main/kotlin/com/university/dcp/
```

Para verificar automáticamente que la solución de referencia pasa el 100% de los tests (16/16):
```bash
./docente/tp-4-funcional/verificar-solucion.sh
```
