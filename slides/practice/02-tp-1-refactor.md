---
marp: true
theme: dcp
paginate: true
title: TP 1 - Refactor: separar responsabilidades
---

<!-- _class: cover -->
<!-- _paginate: false -->

# TP 1

## Refactor: separar responsabilidades

**Trabajo práctico · DCP**

---

## Objetivo del TP

Recibís un programa que **funciona** y está **desordenado**. Tu trabajo es reorganizarlo sin cambiar lo que hace.

Planificás primero con IA, identificás las responsabilidades mezcladas, y recién después tocás el código.

> Al terminar, el programa produce **exactamente la misma salida** que antes, pero cada cosa vive en un solo lugar.

Entrega: 1 semana, antes de la próxima clase práctica.

---

## Qué es la estación de monitoreo

El programa procesa lecturas crudas de una estación de monitoreo ambiental.

```text
id_sensor;tipo;timestamp;valor;unidad
S-01;temperatura;2026-09-28T08:00;21.4;C
S-02;temperatura;2026-09-28T08:00;70.7;F
S-04;presion;2026-09-28T08:15;101280.0;Pa
```

Normaliza unidades (`F` y `K` a `C`, `Pa` a `hPa`), aplica la calibración de cada sensor, calcula promedios, mínimos y máximos, clasifica las alarmas en `LEVE` y `GRAVE`, imprime un reporte y deja una copia en `reporte.txt`.

---

<!-- _class: compact -->

## Las tres reglas del TP

1. **No cambies el comportamiento.** La salida tiene que ser idéntica.
2. **No agregues tests.** Todavía no los vimos.
3. **No agregues funcionalidad.** Nada de sensores nuevos, ni archivos de configuración, ni cosas "que quedarían buenas".

El programa **no tiene errores**. Si encontrás algo que te parece mal, anotalo, no lo arregles.

---

## Paso 1 — Aceptá el assignment y corré el programa

Aceptá el assignment **TP-1 Refactor**. Se crea tu repo privado con el código de la cátedra.

```text
https://classroom50.org/austral-prog/dcp/assignments/tp-1-refactor/accept
```

Cloná, compilá y correlo:

```bash
git clone <url-del-repositorio>
cd <carpeta-del-assignment>
./gradlew run
```

---

## Paso 2 — Fijá las salidas de referencia

El programa tiene **dos** salidas: la consola y el archivo `reporte.txt`. Ninguna de las dos debería cambiar al refactorizar.

```bash
./gradlew run -q > salida-actual.txt
diff salida-esperada.txt salida-actual.txt
diff reporte-esperado.txt reporte.txt
```

Mientras no tengas tests, estos `diff` son lo único que te avisa si rompiste algo.

**Corrélos después de cada paso del refactor.** No al final.

---

<!-- _class: compact -->

## Paso 3 — Encontrá las responsabilidades

Leé `Main.kt` y respondé, por escrito, para cada bloque de código:

- ¿Qué **decide** este bloque? ¿Qué **calcula**? ¿Qué **muestra**?
- Si cambiara el formato del archivo de entrada, ¿cuántos lugares tendría que tocar?
- Si cambiara un umbral de alarma, ¿cuántos lugares tendría que tocar?
- Si tuviera que mostrar el reporte en otro formato, ¿qué código se mezcla con eso?

La respuesta a "cuántos lugares" es la que te dice dónde está el problema.

---

## Paso 4 — Planificá con IA, todavía no construyas

Abrí un chat y pedile a la IA que **analice**, no que reescriba. Un buen prompt de planificación lleva:

- **contexto**: qué hace el programa y en qué materia estás;
- **restricción**: la salida no puede cambiar, sin tests, sin features nuevas;
- **formato de salida esperado**: una lista de responsabilidades, no código.

Pedile **dos o tres organizaciones posibles** y compará. No aceptes la primera.

> Si la IA te devuelve código en este paso, el prompt estaba mal escrito.

---

## Paso 5 — Dibujá el diagrama

Antes de tocar el código, hacé un diagrama simple de caja y flecha: una caja por responsabilidad, una flecha por cada dato que pasa de una a otra.

```text
  [archivo] -> [parseo] -> [normalizacion] -> [estadisticas] -> [consola]
                                    |                \
                                    v                 -------> [reporte.txt]
                               [alarmas]
```

Si no podés dibujarlo, todavía no entendiste el programa. Volvé al Paso 3.

El diagrama va en `DISENO.md`, en texto o como imagen.

---

<!-- _class: compact -->

## Paso 6 — Refactorizá en pasos chicos

Un cambio, un `diff`, un commit. Nunca un refactor grande de una sola vez.

Podés usar funciones y `data class` (la vimos en la primera clase). **No necesitás clases ni interfaces**: eso llega más adelante en la materia.

Algunas señales de que vas bien:

- desaparecen las variables globales;
- el archivo se lee una sola vez;
- umbrales, calibraciones y conversiones están escritos una sola vez;
- armar el texto del reporte no depende de dónde se va a mostrar;
- ninguna función que calcula también imprime o escribe archivos.

---

## Paso 7 — Documentá el diseño

Escribí `DISENO.md` con tres secciones:

1. **Responsabilidades** — cuáles identificaste y dónde vive cada una ahora.
2. **Diagrama** — el del Paso 5, actualizado a lo que efectivamente entregaste.
3. **Hallazgos** — cosas que el programa hace de forma discutible y que **no** arreglaste porque hubieran cambiado la salida.

La sección de hallazgos se evalúa. Es la que muestra que leíste el código y no sólo lo moviste de lugar.

---

## Paso 8 — Prompt log y entrega

Cada interacción con la IA va a `prompt-log/`: qué le pediste, qué devolvió, qué corregiste y por qué. Sin API keys ni tokens.

Commiteá siguiendo la convención del repo, con un commit por paso del refactor:

```bash
git add -A
git commit -m "Extrae el parseo de lecturas a su propia funcion"
git push
```

Entregar es pushear a la rama principal.

---

<!-- _class: compact -->

## Checklist de entrega

- [ ] Aceptaste el TP-1 y tu repo existe en la organización.
- [ ] Los dos `diff` (consola y `reporte.txt`) no imprimen nada.
- [ ] No agregaste tests ni funcionalidad nueva.
- [ ] Cada responsabilidad vive en un solo lugar; no queda lógica duplicada.
- [ ] Los nombres dicen qué hace cada cosa.
- [ ] `DISENO.md` tiene responsabilidades, diagrama y hallazgos.
- [ ] `prompt-log/` está completo y sin secretos.
- [ ] Hay varios commits chicos, no uno solo con todo.

---

## Cómo se evalúa

| Criterio | Qué miramos |
|---|---|
| Comportamiento preservado | los dos `diff` están vacíos |
| Separación de responsabilidades | cada decisión vive en un lugar |
| Duplicación | umbrales, calibración, conversiones y lectura, una vez |
| Nombres | se entiende sin leer el cuerpo |
| `DISENO.md` | coherente con el código entregado |
| Prompt log | muestra criterio propio, no copiar y pegar |

---

## Link del assignment

```text
https://classroom50.org/austral-prog/dcp/assignments/tp-1-refactor/accept
```

Alternativa CLI:

```bash
gh student accept austral-prog dcp tp-1-refactor
```
