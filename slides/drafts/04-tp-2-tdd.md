---
marp: true
theme: dcp
paginate: true
title: TP 2 - Testing como contrato (TDD)
---

<!-- _class: cover -->
<!-- _paginate: false -->

# TP 2

## Testing como contrato (TDD)

**Trabajo práctico · DCP**

---

## Objetivo del TP

Seguís con la estación de monitoreo del TP 1. Ahora le escribís el **contrato**: los tests que dicen qué tiene que hacer el programa.

1. **Tests sobre lo que ya existe.** Fijás el comportamiento de tu refactor.
2. **TDD sobre algo nuevo.** Primero el test, después el código.

> En el TP 3 vas a arreglar un bug con IA y **estos tests tienen que seguir pasando**.

Entrega: 1 semana, antes de la próxima clase práctica.

---

## Un test es un contrato

Un test dice: **con esta entrada, espero este resultado**. Si alguien cambia el código y el resultado cambia, el test se lo avisa.

En el TP 1 el contrato eran los `diff`. En el TP 2 lo corre Gradle:

```bash
./gradlew test
```

`ContratoSalidaTest.kt` lo da la cátedra: corre el programa y compara la consola y `reporte.txt` con las salidas de referencia. **No se modifica.**

---

## Rojo → verde → refactor

1. **Rojo.** Escribís un test para algo que el código todavía no hace. Lo corrés: **tiene que fallar**.
2. **Verde.** Escribís el mínimo código para que pase. Nada más.
3. **Refactor.** Mejorás el código con los tests en verde como red de seguridad.

Si el test pasa en el paso rojo, no estaba probando nada nuevo.

---

## Paso 1 — Aceptá el assignment y traé tu TP 1

Aceptá el assignment **TP-2 TDD**. El repo viene con el código **original** del TP 1.

```text
https://classroom50.org/austral-prog/dcp/assignments/tp-2-tdd/accept
```

Reemplazalo por tu refactor:

1. Copiá tu `src/main/kotlin/` y tu `DISENO.md` del TP 1.
2. `./gradlew test` → el contrato tiene que dar **verde**.
3. Commit directo en `main`: `Importa el refactor del TP 1`.

Es el **único** commit directo a `main`.

---

<!-- _class: compact -->

## Paso 2 — Los casos los decidís vos

Antes de escribir un test, **y antes de pedirle nada a la IA**, completá `CASOS.md`:

| Tipo | Ejemplo en la estación |
|---|---|
| **Feliz** | una lectura en `F` se convierte bien a `C` |
| **Borde** | 27.0 °C exactos: ¿es alarma o no? |
| **Inválido** | una fila con 4 campos se descarta |

Mínimo **tres de cada tipo**.

> La IA te puede sugerir casos que te faltaron. No te puede decir cuáles importan.

---

## Paso 3 — Tu agente de tests

`agente-tests.md` son las instrucciones de un agente que propone y escribe tests. Viene a medio completar: **lo terminás vos**.

- Lee `CASOS.md` y **no lo reemplaza**: propone casos nuevos y espera confirmación.
- No toca `src/main/` ni el test de contrato.
- Si un test falla, lo reporta: **no lo cambia para que pase**.

Lo vas a reusar en el proyecto final.

---

## Parte 1 — Tests sobre el TP 1

Rama `tests/tp-1`. Tests unitarios para las funciones de tu refactor: parseo, unidades, calibración, alarmas, estadísticas, ranking.

```kotlin
@Test
fun `borde - 27 grados exactos no es alarma`() {
    assertNull(alarma(lectura(valor = 27.0)))
}
```

- El nombre dice el tipo de caso y qué se espera.
- Los tests arman sus propios datos, no leen `lecturas.csv`.
- Si no podés testear una función, tu refactor mezcla responsabilidades.

---

<!-- _class: compact -->

## Parte 2 — TDD: nuevas unidades

Rama `feature/unidades`. Llegan sensores nuevos:

1. Presión en `kPa` (1 kPa = 10 hPa) y en `mbar` (1 mbar = 1 hPa).
2. Un tipo conocido con una unidad que no le corresponde se **descarta**:

```text
[descartada] fila 12: unidad desconocida 'mmHg' para presion
```

Los datos actuales no usan estas unidades: **las salidas de referencia no cambian**.

Un commit por paso: `rojo: ...`, `verde: ...`, `refactor: ...`.

---

## Ojo con el rojo

Escribí el test de `mbar` y corrélo antes de tocar el código.

Probablemente **ya pasa**. El programa trata cualquier unidad que no conoce como si fuera la unidad base, y `mbar` equivale a `hPa`.

- ¿Es un test inútil? No: fija un comportamiento que antes era casualidad.
- ¿Es un paso rojo? Tampoco. El rojo lo da el caso de la unidad inválida.

> Correr el test antes de escribir el código es lo que te enseña esto.

---

## Ramas y pull requests

```bash
git switch -c tests/tp-1
# ... tests, commits chicos ...
git push -u origin tests/tp-1
gh pr create --fill
```

En el PR revisá el diff completo antes de mergear, o pedile al agente que lo revise. Después:

```bash
gh pr merge --merge
git switch main && git pull
```

Mismo camino para `feature/unidades`.

---

<!-- _class: compact -->

## Checklist de entrega

- [ ] `./gradlew test` da verde en `main`, incluido el test de contrato.
- [ ] El test de contrato y las salidas de referencia no cambiaron.
- [ ] `CASOS.md` tiene al menos tres casos felices, tres de borde y tres inválidos.
- [ ] Cada caso de `CASOS.md` tiene al menos un test.
- [ ] `agente-tests.md` está completo.
- [ ] La Parte 2 tiene commits `rojo:`, `verde:` y `refactor:`, en ese orden.
- [ ] Los dos pull requests están mergeados; no hay commits directos a `main` salvo el import.
- [ ] `prompt-log/` está completo y sin secretos.

---

## Cómo se evalúa

| Criterio | Qué miramos |
|---|---|
| Contrato | `./gradlew test` en verde, contrato intacto |
| Casos | felices, borde e inválidos, elegidos con criterio |
| Tests | un comportamiento por test, nombres que se entienden |
| TDD | el commit `rojo:` falla de verdad; el `verde:` es mínimo |
| Agente de tests | reglas propias, reusables en el proyecto final |
| Flujo de trabajo | ramas, PRs revisados, commits chicos |
| Prompt log | muestra criterio propio, no copiar y pegar |

---

## Link del assignment

```text
https://classroom50.org/austral-prog/dcp/assignments/tp-2-tdd/accept
```

Alternativa CLI:

```bash
gh student accept austral-prog dcp tp-2-tdd
```
