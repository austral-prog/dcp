---
marp: true
theme: dcp
paginate: true
title: TP 4 - Modelado con tipos y transformación funcional
---

<!-- _class: cover -->
<!-- _paginate: false -->

# TP 4

## Modelado con tipos y transformación funcional

**Trabajo práctico · DCP**

---

## Objetivo del TP

Contás con herramientas más expresivas de Kotlin (`data class`, `sealed class`, invariantes con `require`, inmutabilidad con `copy` y transformaciones funcionales).

Tu trabajo en este TP:

1. **Detectar código imperativo o frágil:** identificar modelos sin validación, bucles mutables y casos borde no contemplados (como `reduce` en listas vacías).
2. **Modelar y transformar funcionalmente con ayuda de tu agente de IA:** aplicar `require`, `sealed class`, `copy`, `map`, `filter` y `fold`.
3. **Lograr 100% de tests en verde** manteniendo intacto el contrato base de la estación.

Entrega: 1 semana, antes de la próxima clase práctica.

---

## Tres módulos de impronta ingenieril

Además del programa base de la estación meteorológica, el TP suma **tres módulos nuevos** con implementaciones iniciales incompletas o imperativas:

1. **`AnalizadorStock.kt` (Stock e inventario):**
   - Evaluación de reposición de sensores y repuestos (`ReposicionUrgente`, `StockSuficiente`, `ExcesoStock`), costos por categoría y ajustes porcentuales inmutables.
2. **`ProcesadorAdn.kt` (Bioinformática):**
   - Validación de secuencias de ADN (`A`, `C`, `G`, `T`), cálculo de contenido GC, transcripción a ARN y filtrado de motivos genéticos.
3. **`MotorGaming.kt` (Gaming computation):**
   - Puntaje efectivo con golpes críticos, agregación de puntaje por equipo y construcción de leaderboard ordenado.

---

<!-- _class: compact -->

## Paso 1 — Aceptá el assignment

Aceptá el assignment **TP 4 - Funcional** en Classroom 50:

```text
https://classroom50.org/austral-prog/dcp/assignments/tp-4-funcional/accept
```

Alternativa CLI:

```bash
gh student accept austral-prog dcp tp-4-funcional
```

Se crea tu repositorio individual `austral-prog/dcp-tp-4-funcional-<tu-usuario>` con:
- Código de partida (`src/main/kotlin/`).
- Suite de 16 tests automatizados (`src/test/kotlin/`).
- Workflow de evaluación automática configurado con Java 21.

---

## Paso 2 — Cloná y ejecutá los tests

Cloná tu repo y corré la suite de tests de Gradle:

```bash
git clone git@github.com:austral-prog/dcp-tp-4-funcional-<tu-usuario>.git
cd dcp-tp-4-funcional-<tu-usuario>
./gradlew test
```

Resultado inicial:
- **2 tests pasan:** `ContratoSalidaTest` y un caso básico de stock.
- **14 tests fallan:** invariantes sin validar, `reduce` sobre listas vacías y transformaciones defectuosas en `AnalizadorStock`, `ProcesadorAdn` y `MotorGaming`.

> **Puntaje inicial en Classroom 50:** 10 / 100 puntos.

---

## Módulo 1 — Stock e inventario (`AnalizadorStock.kt`)

Qué debés detectar y llevar a un diseño mejor:

1. **Invariantes en `ItemStock`:** proteger con `init { require(...) }` que `sku` y `categoria` no estén en blanco, `cantidad >= 0`, `precioUnitario > 0.0` y `stockMinimo >= 0`.
2. **Alternativas con `sealed class EstadoReposicion`:** transformar con `map` y `when` contemplando `ReposicionUrgente` (`faltante * precioUnitario`), `ExcesoStock` (`cantidad > stockMinimo * 3`) y `StockSuficiente`.
3. **`fold` vs `reduce` en colecciones vacías:** `costoTotalReposicionPorCategoria` usa `reduce` y lanza `UnsupportedOperationException` cuando una categoría no requiere reposición. Reemplazalo por `fold(0.0)`.
4. **Inmutabilidad con `copy`:** en `aplicarAjustePrecios`, transformar con `map` y `copy` aplicando el porcentaje sin descartar los ítems que no tienen ajuste.

---

## Módulo 2 — Bioinformática (`ProcesadorAdn.kt`)

Qué debés detectar y transformar funcionalmente:

1. **Invariantes en `MuestraAdn`:** validar en `init` con `require` que `id` no esté en blanco y que `secuencia` no sea vacía y solo contenga nucleótidos `'A'`, `'C'`, `'G'`, `'T'`.
2. **Contenido GC (`calcularPorcentajeGc`):** contar tanto `'G'` como `'C'` sobre el largo total de la secuencia usando operaciones funcionales (`fold` o `count`).
3. **Transcripción a ARN (`transcribirARna`):** reemplazar timina (`'T'`) por uracilo (`'U'`) en cada secuencia construyendo el mapa de forma declarativa.
4. **Clasificación y motivos (`clasificarMuestrasPorGc` y `contarBasesTotalesConMotivo`):** contemplar la variante `ContenidoNormal` (`40.0..60.0`) y armar el pipeline `filter -> map -> fold(0)` para sumar las bases de muestras que contienen el motivo.

---

## Módulo 3 — Gaming computation (`MotorGaming.kt`)

Qué debés detectar y transformar funcionalmente:

1. **Invariantes en `EventoPartida`:** validar `jugadorId.isNotBlank()`, `puntosBase >= 0` y `multiplicador > 0.0`.
2. **Cálculo puro de críticos (`calcularPuntajeEfectivo`):** aplicar el factor adicional de `1.5x` cuando `esCritico` es `true`.
3. **Agregación de equipo segura (`puntajeTotalEquipo`):** cambiar `reduce` por `fold(0)` para que un equipo sin eventos registrados devuelva `0` en vez de fallar.
4. **Pipeline de Leaderboard (`construirLeaderboard`):** agrupar por jugador, resumir puntaje total, cantidad de críticos y mejor golpe, filtrar por `puntajeMinimo` y ordenar de mayor a menor `puntajeTotal`.

---

<!-- _class: compact -->

## Cómo guiar a tu agente de IA

En este TP vos sos quien guía el diseño hacia programación funcional e inmutabilidad:

1. **Pedile que revise las invariantes del modelo primero:**
   - *"Revisemos las precondiciones que `AnalizadorStockTest` exige para `ItemStock` y agreguemos las validaciones con `require` en `init`."*
2. **Exigile pipelines funcionales en lugar de parches imperativos:**
   - *"Transformemos `evaluarInventario` y `aplicarAjustePrecios` para que usen `map`, `when` y `copy` sin listas mutables."*
3. **Atacá explícitamente los casos borde con listas vacías:**
   - *"¿Por qué `puntajeTotalEquipo` lanza `UnsupportedOperationException` cuando el filtro da vacío? Reemplacemos `reduce` por `fold(0)`."*

---

## Autograder y evaluación en Classroom 50

Classroom 50 evalúa automáticamente tu repositorio en cada `git push`:

| Test Suite | Qué evalúa | Puntos | Inicial |
| :--- | :--- | :---: | :---: |
| `ContratoSalidaTest` | Salida consola y reporte base de la estación | **10 pts** | ✅ Pasa |
| `AnalizadorStockTest` | Invariantes, `sealed class`, `copy` y `fold` en stock | **30 pts** | ❌ Falla |
| `ProcesadorAdnTest` | Invariantes ACGT, ARN, clasificación GC y motivos | **30 pts** | ❌ Falla |
| `MotorGamingTest` | Invariantes, críticos, `fold` de equipo y leaderboard | **30 pts** | ❌ Falla |
| **Total** | | **100 pts** | **10 pts** |

---

<!-- _class: compact -->

## Checklist de entrega

- [ ] Aceptaste el assignment y tu repo está en la organización.
- [ ] `./gradlew test` da **16 passed, 0 failed** en la rama `main`.
- [ ] No modificaste ni deshabilitaste los tests provistos por la cátedra.
- [ ] Las `data class` validan sus invariantes con `require` en `init`.
- [ ] Los módulos usan funciones puras, inmutabilidad (`copy`) y pipelines (`map`, `filter`, `fold`).
- [ ] `prompt-log/` registra las sesiones con la IA.
- [ ] Hiciste commits chicos y descriptivos (por ejemplo, uno por módulo).
