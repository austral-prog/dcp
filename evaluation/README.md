# Evaluación automática de TPs (Classroom 50)

`grade-tp1.mjs` corrige el **TP 1 – Refactor** de toda la comisión y genera un CSV.

## Requisitos

- Node 20+, JDK 21, `git` y GitHub CLI (`gh`) con las extensiones `gh teacher` (foundation50/gh-teacher).
- Una cuenta de `gh` con acceso de lectura a la organización `austral-prog` (docente).

## Uso

```sh
node evaluation/grade-tp1.mjs --gh-user <tu-usuario-docente> --deadline "2026-10-05T23:59"
```

Atajo: `npm run grade:tp1 -- --gh-user <usuario> --deadline "2026-10-05T23:59"`.

`--deadline` es obligatorio porque Classroom 50 no guarda fecha límite en el manifiesto de este assignment.
Sin zona horaria se asume Argentina (-03:00). Otras opciones: `node evaluation/grade-tp1.mjs --help`.

Para probar con pocos alumnos: `--only usuario1,usuario2`.

## Qué hace

1. Lee el manifiesto y el roster de `austral-prog/classroom50` (`gh teacher`): solo alumnos (no docentes).
2. Para cada alumno busca `austral-prog/dcp-tp-1-refactor-<usuario>` y lo clona en `evaluation/output/.work/`.
3. Recorre los commits de la rama principal (primer padre). Un commit "toca el código" si el árbol
   `src/main/kotlin` difiere del de la plantilla (`dcp-refactor-template`).
4. Ejecuta los dos `diff` de la consigna sobre cada commit (`./gradlew run -q` vs `salida-esperada.txt`
   y `reporte.txt` vs `reporte-esperado.txt`). Usa los archivos de referencia y `datos/lecturas.csv` **de la
   plantilla**, no los del alumno.

## Criterios

| Columna | Criterio |
|---|---|
| `entregado` | `si` si el HEAD de la rama principal modificó el código **y** pasa ambos `diff` |
| `timestamp` | fecha del **primer commit andando**: el más antiguo que modificó el código y pasa ambos `diff` |
| `entrego_a_tiempo` | `si` si ese `timestamp` es menor o igual a `--deadline`. Si no entregó, `no` |
| `detalle` | motivo: repo inexistente, código sin cambios o fuera de `src/main/kotlin`, no compila, diff distinto |

Los timestamps son la fecha de commit (`committer date`) en hora de Argentina. Un commit puede tener fecha
manipulada localmente; si hiciera falta una cota confiable, revisar el momento del push en GitHub.

## Salida

- `evaluation/output/reporte-tp-1-refactor.csv`: `nombre, repo, timestamp, entrego_a_tiempo, entregado, usuario, detalle`.
  Lleva BOM UTF-8 para abrirse bien en Excel y Google Sheets.
- Los resultados por commit se cachean en `evaluation/output/.work/cache.json`; `--fresh` los ignora.
- `evaluation/output/` está en `.gitignore`: contiene nombres de alumnos y clones de repos privados.

## Limitaciones conocidas

- Evalúa comportamiento (los `diff`), no calidad del refactor, `DISENO.md` ni `prompt-log/`.
- No detecta cambios triviales (p. ej. agregar un espacio) como "no tocó el código".
- El nombre sale del roster; si está vacío, del perfil público de GitHub; si no, el usuario.

---

# TP 2 – TDD (`grade-tp2.mjs`)

```sh
node evaluation/grade-tp2.mjs --gh-user <tu-usuario-docente>     # límite por defecto: 2026-10-15T09:00 (-03:00)
npm run grade:tp2 -- --gh-user <usuario> --only usuario1,usuario2
```

Lee el manifiesto directo de `austral-prog/classroom50` (no usa `gh teacher assignment list`, que falla si
alguna entrada del manifiesto es inválida). Mismas opciones que el TP 1; `--deadline` es opcional.

## Qué evalúa en cada commit

Se evalúan dos commits de la rama principal (primer padre): **HEAD** y **el último commit con fecha ≤ límite**.
En ambos se pisan `ContratoSalidaTest.kt`, `salida-esperada.txt`, `reporte-esperado.txt` y `datos/lecturas.csv`
con los de la plantilla (`dcp-tdd-template`) antes de correr nada.

| Criterio | Cómo | Puntos |
|---|---|---|
| Tests | `./gradlew test` en verde, contrato sin modificar (blob igual a la plantilla) y al menos un test propio (fuera de `ContratoSalidaTest`). Si pasa pero no hay tests propios: 1 punto | 4 |
| Parte 2: kPa | escenario `tp2/escenarios/kpa.csv`: consola y `reporte.txt` idénticos a lo esperado | 1.5 |
| Parte 2: unidad inválida | escenario `unidad-invalida.csv` (mmHg, inHg, `hPa` en temperatura, `C` y `g/m3` en humedad; `caudal` sigue ignorado) | 2 |
| Parte 2: mbar | escenario `mbar.csv` (el código original ya lo pasa; detecta a quien valida unidades y olvida `mbar`) | 0.5 |
| `CASOS.md` | filas de la tabla con tipo `feliz` / `borde` / `inválido` y caso no vacío; proporcional hasta 3 de cada uno | 1 |
| `agente-tests.md` | modificado y sin `(completar)`: 1; modificado con pendientes: 0.5 | 1 |

Si `src/main/kotlin` quedó igual a la plantilla, tests y escenarios valen 0 (la plantilla sola ya pasa el
contrato y `mbar`).

Las salidas esperadas de los escenarios (`*.salida.txt`, `*.reporte.txt`) se generaron con una solución de
referencia sobre el código original, que también pasa el test de contrato. Los escenarios evitan casos
ambiguos (p. ej. una fila con valor no numérico **y** unidad inválida).

## Salida

`evaluation/output/reporte-tp-2-tdd.csv`:

| Columna | Significado |
|---|---|
| `entregado` | HEAD modificó `src/main/kotlin`, tests en verde y contrato intacto |
| `entrego_a_tiempo` | lo mismo, pero en el último commit con fecha ≤ límite |
| `nota_sugerida` | puntaje de HEAD sobre 10 (redondeado a 0.5) |
| `nota_al_limite` | puntaje del último commit antes del límite |
| `ultimo_commit` | fecha del HEAD |
| `detalle` | resumen por criterio de cada commit evaluado |

## No evalúa (revisar a mano)

- Que la Parte 2 tenga commits `rojo:` / `verde:` / `refactor:` ni que el rojo falle de verdad.
- Que los PRs estén mergeados ni que no haya commits directos a `main`.
- Que cada caso de `CASOS.md` tenga su test, ni la calidad de los tests o del agente.
- `prompt-log/`.
