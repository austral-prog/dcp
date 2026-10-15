---
marp: true
theme: dcp
paginate: true
title: TP 3 - Depuración asistida por IA (Bug fixing)
---

<!-- _class: cover -->
<!-- _paginate: false -->

# TP 3

## Depuración asistida por IA

**Trabajo práctico · DCP**

---

## Objetivo del TP

En la industria más del 50% del tiempo de desarrollo se dedica a **diagnosticar y corregir bugs** en código ajeno o nuevo sin romper lo que ya funciona.

Tu trabajo en este TP:

1. **Diagnosticar bugs con asistencia de IA:** usar los tests que fallan y los stack traces como guía.
2. **Corregir los errores en los nuevos módulos:** lograr que todos los tests den verde.
3. **Preservar el contrato:** asegurar que el código base siga funcionando sin regresiones.

Entrega: 1 semana, antes de la próxima clase práctica.

---

## La estación suma nuevos módulos

Seguís con el dominio de la estación meteorológica. Para este TP agregamos dos componentes nuevos provistos por la cátedra:

1. **`DetectorRafagas.kt`**:
   - Detecta ráfagas de alarmas (mínimo de alarmas consecutivas en una ventana de minutos).
   - Detecta saltos térmicos abruptos entre lecturas sucesivas de temperatura.

2. **`ExportadorJson.kt`**:
   - Serializa las estadísticas climáticas y la lista de incidentes a formato JSON estándar.

> Los nuevos módulos vienen con **bugs intencionales** introducidos por la cátedra.

---

## ¿Y mi código de los TPs anteriores?

El repositorio viene con dos alternativas pensadas para todos:

- **Por defecto:** incluye la versión funcional original del TP 1 en `Main.kt`. Si no terminaste o querés partir de una base limpia, el TP funciona directamente sin bloquearte.
- **Si hiciste el TP 2:** podés traer tu refactor modular y tus tests unitarios propios.

En ambos casos, el contrato base y los nuevos módulos se comportan de la misma manera.

---

<!-- _class: compact -->

## Paso 1 — Aceptá el assignment

Aceptá el assignment **TP 3 - Bugs** en Classroom 50:

```text
https://classroom50.org/austral-prog/dcp/assignments/tp-3-bugs/accept
```

Alternativa CLI:

```bash
gh student accept austral-prog dcp tp-3-bugs
```

Se crea tu repositorio individual `austral-prog/dcp-tp-3-bugs-<tu-usuario>` con:
- Código base + nuevos módulos (`src/main/kotlin/`).
- Suites de tests automatizados (`src/test/kotlin/`).
- Workflow de evaluación automática configurado con Java 21.

---

## Paso 2 — Cloná y ejecutá los tests

Cloná tu repo y corré la suite de tests de Gradle:

```bash
git clone git@github.com:austral-prog/dcp-tp-3-bugs-<tu-usuario>.git
cd dcp-tp-3-bugs-<tu-usuario>
./gradlew test
```

Resultado inicial:
- **5 tests pasan:** `ContratoSalidaTest` (el contrato existente no se rompió).
- **5 tests fallan:** 3 en `DetectorRafagasTest` y 2 en `ExportadorJsonTest`.

> **Puntaje inicial en Classroom 50:** 20 / 100 puntos.

---

## Módulo 1 — Detector de ráfagas

`DetectorRafagasTest` tiene 3 tests en rojo:

1. **Ventanas temporales entre horas distintas:**
   ¿Qué pasa si una ráfaga empieza a las `08:45` y sigue a las `09:15`? Si solo restás los minutos, la cuenta da negativa.
2. **Aislamiento por sensor:**
   Una ráfaga corresponde a un sensor específico. Dos alarmas de sensores distintos no deben sumarse en una misma ráfaga.
3. **Saltos térmicos simétricos:**
   Un salto térmico abrupto puede ser tanto un aumento repentino como una caída brusca de temperatura.

---

## Módulo 2 — Exportador JSON

`ExportadorJsonTest` tiene 2 tests en rojo:

1. **Comas al final de listas (*trailing commas*):**
   El JSON generado coloca una coma antes de cerrar corchetes (`[..., ]`), lo que invalida el formato JSON estándar.
2. **Tipos de datos numéricos vs strings:**
   Los contadores enteros (`lecturas_validas`, `total_alarmas`) aparecen entre comillas (`"12"`) en lugar de como números crudos (`12`).

---

<!-- _class: compact -->

## Cómo interactuar con tu agente de IA

La IA no adivina el problema si no le das el contexto adecuado:

1. **Compartí el test que falla y el mensaje de aserción:**
   - `"El test 'salto termico abrupto hacia abajo' falla con expected: 1, actual: 0."`
2. **Compartí la porción de código sospechosa:**
   - `"Revisemos la función detectarSaltosTermicos en DetectorRafagas.kt."`
3. **Pedí análisis de causa raíz antes de pedir código:**
   - *"¿Por qué esta condición ignora caídas de temperatura? Explicámelo paso a paso antes de sugerir el fix."*
4. **Verificá que no haya regresiones:**
   - Cada fix debe mantener verdes los tests anteriores.

---

## Ramas y flujo de trabajo

Trabajá en ramas chicas para cada módulo:

```bash
git switch -c fix/detector-rafagas
# ... investigás, corregís con IA, `./gradlew test` ...
git commit -am "Corrige cálculo de minutos entre horas y aislamiento por sensor"
git push -u origin fix/detector-rafagas
gh pr create --fill
gh pr merge --merge
```

Luego hacé lo mismo con `fix/exportador-json`:

```bash
git switch main && git pull
git switch -c fix/exportador-json
# ... corregís exportador, verificás 10/10 tests verdes ...
```

---

## Autograder y evaluación en Classroom 50

Classroom 50 evalúa automáticamente tu repositorio en cada `git push`:

| Test Suite | Qué evalúa | Puntos | Inicial |
| :--- | :--- | :---: | :---: |
| `ContratoSalidaTest` | Salida consola y reporte base del TP 1 | **20 pts** | ✅ Pasa |
| `DetectorRafagasTest` | Ventanas temporales, sensores y saltos | **40 pts** | ❌ Falla |
| `ExportadorJsonTest` | Formato JSON válido y tipos numéricos | **40 pts** | ❌ Falla |
| **Total** | | **100 pts** | **20 pts** |

En tu Feedback PR verás cómo sube la nota: `20` → `60` → `100`.

---

<!-- _class: compact -->

## Checklist de entrega

- [ ] Aceptaste el assignment y tu repo está en la organización.
- [ ] `./gradlew test` da **10 passed, 0 failed** en la rama `main`.
- [ ] No modificaste ni deshabilitaste los tests provistos por la cátedra.
- [ ] `ContratoSalidaTest` sigue dando verde (la salida de la estación es idéntica).
- [ ] Se utilizó el flujo de ramas y Pull Requests para cada bloque de correcciones.
- [ ] `prompt-log/` registra las sesiones con la IA (prompts, diagnóstico y validación).
- [ ] Todo está pusheado a la rama `main` de tu repositorio.

---

## Cómo se evalúa

| Criterio | Qué miramos |
|---|---|
| **Tests en verde** | 100/100 en el autograder de Classroom 50 |
| **Integridad del contrato** | Salida esperada y tests de la cátedra intactos |
| **Calidad de los fixes** | Soluciones limpias, legibles y que atacan la causa raíz |
| **Flujo de trabajo** | Ramas temáticas, PRs revisados y commits explicativos |
| **Prompt log** | Registro honesto de cómo la IA ayudó a diagnosticar y resolver |

---

## Link del assignment

```text
https://classroom50.org/austral-prog/dcp/assignments/tp-3-bugs/accept
```

Alternativa CLI:

```bash
gh student accept austral-prog dcp tp-3-bugs
```
