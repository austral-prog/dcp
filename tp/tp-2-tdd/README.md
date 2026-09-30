# TP 2 — Testing como contrato (TDD)

Seguís con la estación de monitoreo del TP 1. Ahora el programa ya está ordenado, así
que le escribís el **contrato**: los tests que dicen qué tiene que hacer. Después
agregás una funcionalidad nueva al revés de como venías trabajando: **primero el test,
después el código**.

El TP tiene dos partes:

1. **Tests sobre lo que ya existe.** Fijás el comportamiento del TP 1 con tests unitarios.
2. **TDD sobre algo nuevo.** Agregás una funcionalidad con el ciclo rojo → verde → refactor.

> En el TP 3 vas a arreglar un bug con ayuda de la IA y **todos estos tests tienen que
> seguir pasando**. Escribilos contra el comportamiento, no contra detalles internos que
> vas a querer cambiar.

## Cómo correrlo

Kotlin/JVM 21 con Gradle Wrapper, igual que el TP 1. Requisitos: **JDK 21**.

```bash
./gradlew test           # macOS / Linux
.\gradlew.bat test       # Windows
./gradlew run            # corre el programa
```

Los tests usan `kotlin.test` sobre JUnit 5; ya está configurado en `build.gradle.kts`.
Van en `src/test/kotlin/com/university/dcp/`.

## El test de contrato

`src/test/kotlin/com/university/dcp/ContratoSalidaTest.kt` lo da la cátedra. Corre el
programa completo y compara la consola y `reporte.txt` con `salida-esperada.txt` y
`reporte-esperado.txt`. Reemplaza a los `diff` del TP 1.

**No lo modifiques.** Tiene que estar en verde en cada commit de `main`.

Llama a `main()` sin argumentos. Si tu `main` recibe `args`, dejá también una versión
sin argumentos.

## Paso 1 — Traé tu código del TP 1

El repo viene con el código **original** del TP 1 (el desordenado). Reemplazalo por tu
refactor:

1. Copiá tu `src/main/kotlin/` y tu `DISENO.md` del repo del TP 1 a este.
2. Corré `./gradlew test`: el test de contrato tiene que dar verde.
3. Commiteá eso directo en `main` con el mensaje `Importa el refactor del TP 1`.

Es el **único** commit que va directo a `main`. Todo lo demás entra por pull request.

> Sobre el código original no se pueden escribir tests unitarios: todo pasa en una sola
> función que imprime. Si no terminaste el TP 1, terminá el refactor primero.

## Paso 2 — Escribí los casos antes que los tests

Antes de escribir un test, y antes de pedirle nada a la IA, completá
[`CASOS.md`](CASOS.md) con los casos que **vos** considerás importantes:

- **Felices**: entradas normales, el camino esperado.
- **Borde**: justo en el límite (un valor igual al umbral, un solo dato, ningún dato).
- **Inválidos**: lo que el programa tiene que rechazar o descartar.

Mínimo **tres de cada tipo**. Esta lista es tu criterio: la IA te puede sugerir casos
que te faltaron, pero no reemplaza la lista.

## Paso 3 — Construí tu agente de tests

Vas a usar un agente para proponer y escribir tests. Sus instrucciones las escribís vos
en [`agente-tests.md`](agente-tests.md), que viene a medio completar. Este agente lo vas
a reusar en el proyecto final.

Cuando le pidas tests, indicale que siga `agente-tests.md`. En Claude Code, opencode o
Copilot alcanza con decirle "seguí `agente-tests.md`"; `AGENTS.md` ya lo menciona.

## Parte 1 — Tests sobre el comportamiento del TP 1

Rama: `tests/tp-1`. Escribí tests unitarios para las funciones de tu refactor: parseo,
conversión de unidades, calibración, clasificación de alarmas, estadísticas y ranking.

- Cada caso de `CASOS.md` tiene al menos un test.
- El nombre del test dice el tipo de caso y qué se espera:

```kotlin
@Test
fun `borde - 27 grados exactos no es alarma`() {
    // ...
}
```

- Los tests no leen `datos/lecturas.csv`: arman sus propios datos. El archivo real ya
  lo cubre el test de contrato.
- Si para testear algo tenés que cambiar tu código, está bien, siempre que el contrato
  siga en verde. Anotalo en `DISENO.md`.

Abrí un pull request de `tests/tp-1` a `main`, revisalo vos (o pedile al agente que lo
revise) y mergealo.

## Parte 2 — TDD: nuevas unidades

Rama: `feature/unidades`. La estación va a recibir sensores nuevos. Requisito:

1. La presión también puede venir en `kPa` (1 kPa = 10 hPa) y en `mbar` (1 mbar = 1 hPa).
2. Si un tipo conocido viene con una unidad que no le corresponde, la fila se descarta:
   ```text
   [descartada] fila 12: unidad desconocida 'mmHg' para presion
   ```
   Cuenta en `filas descartadas` y no cuenta como lectura válida.

Unidades válidas: temperatura `C`, `F`, `K`; humedad `%`; presión `hPa`, `Pa`, `kPa`,
`mbar`. Los tipos desconocidos (como `caudal`) se siguen ignorando igual que antes.

Los datos actuales no usan ninguna de estas unidades, así que **las salidas de
referencia no cambian**: el test de contrato te avisa si rompiste algo.

Trabajá con el ciclo **rojo → verde → refactor**, un commit por paso:

```text
rojo: kPa se convierte a hPa           <- el test falla (o no compila)
verde: convierte kPa a hPa             <- el mínimo código para que pase
refactor: unifica tabla de unidades    <- mejora sin cambiar comportamiento
```

El commit `rojo:` tiene que fallar de verdad: corré los tests antes de escribir el
código y confirmá que al menos uno falla.

> Ojo con `mbar`: probablemente su test **ya pasa** sin tocar nada, porque el programa
> trata una unidad que no conoce como si fuera la unidad base, y 1 mbar = 1 hPa. No lo
> borres: fija un comportamiento que antes era casualidad. Pero no alcanza como paso
> rojo; el rojo te lo dan `kPa` y la unidad inválida.

Abrí el pull request a `main` y mergealo.

## Registro de prompts (`prompt-log/`)

Cada interacción con un LLM sobre este repositorio va a `prompt-log/`, igual que en el
TP 1. Traé también las entradas de tu TP 1. La política completa está en
[`AGENTS.md`](AGENTS.md) y el formato en [`prompt-log/README.md`](prompt-log/README.md).

## Qué entregás

| Entregable | Dónde |
|---|---|
| Tu refactor del TP 1 | commit `Importa el refactor del TP 1` en `main` |
| Los casos | [`CASOS.md`](CASOS.md) |
| El agente de tests | [`agente-tests.md`](agente-tests.md) |
| Tests de la Parte 1 | pull request `tests/tp-1` mergeado |
| TDD de la Parte 2 | pull request `feature/unidades` mergeado, con commits `rojo:` / `verde:` / `refactor:` |
| El registro de prompts | [`prompt-log/`](prompt-log/) |

Entregar es tener los dos pull requests mergeados en `main`.

## Checklist de entrega

- [ ] `./gradlew test` da verde en `main`, incluido el test de contrato.
- [ ] `ContratoSalidaTest.kt`, `salida-esperada.txt` y `reporte-esperado.txt` no cambiaron.
- [ ] `CASOS.md` tiene al menos tres casos felices, tres de borde y tres inválidos.
- [ ] Cada caso de `CASOS.md` tiene al menos un test.
- [ ] `agente-tests.md` está completo.
- [ ] La Parte 2 tiene commits `rojo:`, `verde:` y `refactor:`, en ese orden.
- [ ] Los dos pull requests están mergeados; no hay commits directos a `main` salvo el import.
- [ ] `prompt-log/` está completo y sin secretos.
