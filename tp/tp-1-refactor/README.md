# TP 1 — Estación de monitoreo (código de partida)

Código provisto por la cátedra para el TP 1. **Funciona correctamente**: el trabajo
no es arreglarlo, es reorganizarlo.

## Qué hace

Lee `datos/lecturas.csv` (lecturas crudas de una estación de monitoreo ambiental),
normaliza las unidades, aplica la calibración de cada sensor, calcula estadísticas
por magnitud, detecta valores fuera de rango clasificándolos por severidad, escribe
un reporte por consola y deja una copia en `reporte.txt`.

## Cómo correrlo

El proyecto es Kotlin/JVM 21 y se construye con el **Gradle Wrapper**, igual que el TP 0.
No hace falta instalar Gradle de forma global: usá `./gradlew` (macOS/Linux) o
`.\gradlew.bat` (Windows). Requisitos: **JDK 21**.

```bash
./gradlew test           # corre la suite de tests automatizados (macOS / Linux)
.\gradlew.bat test       # Windows
./gradlew run            # ejecuta el programa (macOS / Linux)
.\gradlew.bat run        # Windows
```

Para compilar sin ejecutar:

```bash
./gradlew build          # macOS / Linux
.\gradlew.bat build      # Windows
```

## Tests y salidas de referencia

El repositorio incluye tests automatizados en `src/test/kotlin/com/university/dcp/`:

- `ContratoSalidaTest.kt`: valida que la ejecución del programa mantenga intactas las dos salidas de referencia (consola y `reporte.txt`).
- `RefactorEntregaTest.kt`: valida que el código no haya quedado en el estado de partida sin refactorizar (si no modificaste la plantilla, este test falla).

Podés correr los tests en cualquier momento con:

```bash
./gradlew test
```

También podés verificar manualmente con `diff` si lo preferís:

```bash
./gradlew run -q > salida-actual.txt
diff salida-esperada.txt salida-actual.txt
diff reporte-esperado.txt reporte.txt
```

Si ninguno de los dos `diff` imprime nada, el comportamiento se preservó.

> El `-q` importa: sin él, Gradle mezcla sus propios mensajes de progreso con la salida
> del programa y el `diff` da distinto aunque no hayas roto nada.

## Archivos

| Archivo | Contenido |
|---|---|
| `src/main/kotlin/com/university/dcp/Main.kt` | el programa |
| `datos/lecturas.csv` | lecturas crudas, incluye filas inválidas a propósito |
| `salida-esperada.txt` | salida de consola de referencia |
| `reporte-esperado.txt` | contenido de referencia de `reporte.txt` |

## Qué entregás

| Entregable | Dónde |
|---|---|
| El programa refactorizado | `src/main/kotlin/com/university/dcp/` |
| El documento de diseño | [`DISENO.md`](DISENO.md) |
| El registro de prompts | [`prompt-log/`](prompt-log/) |

Entregar es pushear a la rama principal. Hacé varios commits chicos, uno por paso del
refactor, no uno solo con todo.

## Registro de prompts (`prompt-log/`)

Cada interacción con un LLM sobre este repositorio debe guardarse en `prompt-log/`. La
política completa está en [`AGENTS.md`](AGENTS.md); el formato de cada entrada está en
[`prompt-log/README.md`](prompt-log/README.md).

## Checklist de entrega

- [ ] `./gradlew test` da **verde** en la rama principal.
- [ ] No modificaste ningún archivo en `src/test/`, ni `salida-esperada.txt`, ni `reporte-esperado.txt`.
- [ ] Cada responsabilidad vive en un solo lugar; no queda lógica duplicada.
- [ ] Los nombres dicen qué hace cada cosa.
- [ ] `DISENO.md` tiene responsabilidades, diagrama y hallazgos.
- [ ] `prompt-log/` está completo y sin secretos.
- [ ] Hay varios commits chicos, no uno solo con todo.
