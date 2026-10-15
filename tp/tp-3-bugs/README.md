# TP 3 — Depuración asistida por IA

Seguís con la estación de monitoreo ambiental. En el TP 1 la ordenaste y en el TP 2 le
escribiste tests unitarios y sumaste nuevas unidades de presión con TDD.

Para esta tercera etapa, el proyecto incorpora **dos módulos auxiliares nuevos provistos por la cátedra**:
1. `DetectorRafagas.kt`: detecta ráfagas de alarmas en ventanas temporales ($\ge 3$ alarmas en $\le 45\text{ min}$) y saltos térmicos bruscos ($\Delta T \ge 5.0^\circ\text{C}$).
2. `ExportadorJson.kt`: exporta el estado, métricas y eventos de la estación a un archivo `reporte.json`.

Estos nuevos componentes fueron implementados recientemente pero **contienen bugs inadvertidos**.
El objetivo del TP 3 es **encontrar, diagnosticar y corregir los bugs trabajando en pareja con un agente de IA**,
apoyándote en una suite de tests automatizados provista por la cátedra como red de seguridad.

## Cómo correrlo

Kotlin/JVM 21 con el **Gradle Wrapper**, igual que en los TPs anteriores. Requisitos: **JDK 21**.

```bash
./gradlew test           # corre la suite de tests automatizados (macOS / Linux)
.\gradlew.bat test       # Windows
./gradlew run            # ejecuta el programa principal
```

## Paso 1 — Traé tu código del TP 2 (o usá el provisto)

El repositorio viene por defecto con la **versión base funcional del TP 1** en `src/main/kotlin/com/university/dcp/Main.kt`.

* **Si terminaste el TP 2:** podés reemplazar `Main.kt` por tus archivos refactorizados de `src/main/` y tus tests unitarios de `src/test/` de tu repositorio de TP 2. El test de contrato `ContratoSalidaTest.kt` y tus propios tests deben seguir pasando en verde.
* **Si no terminaste el TP 2:** podés dejar el `Main.kt` provisto por defecto y trabajar directamente sobre los módulos nuevos. No estás bloqueado.

## Paso 2 — Ejecutá los tests provistos

La cátedra entrega el proyecto con una suite completa en `src/test/kotlin/com/university/dcp/`:
* `ContratoSalidaTest.kt`: valida que la estación original siga produciendo las salidas esperadas en consola y `reporte.txt` (este test **pasa** desde el inicio).
* `DetectorRafagasTest.kt`: valida la lógica temporal, el aislamiento por sensor y la detección de saltos de temperatura.
* `ExportadorJsonTest.kt`: valida el formato JSON, la serialización de arrays y el tipado de números.

Al ejecutar `./gradlew test` por primera vez, **los tests de los módulos nuevos van a fallar** debido a los bugs en `DetectorRafagas.kt` y `ExportadorJson.kt`.

> **Regla de oro:** Los tests provistos en `src/test/` **no se modifican**. Tu misión es hacer que pasen corrigiendo el código de producción en `src/main/`.

## Paso 3 — Flujo de trabajo con tu agente de IA

Usá tu agente (Claude Code, GitHub Copilot CLI, OpenCode, Cursor, etc.) para diagnosticar y corregir cada falla:

1. **Aislá un test que falle:** ejecutá `./gradlew test` y seleccioná uno de los tests fallidos.
2. **Presentale el problema al agente:** mostrale el mensaje de error, la aserción y el código involucrado.
3. **Revisá el diagnóstico con criterio:** pedile que explique la causa raíz. No apliques código sin entenderlo: verificá que la solución sea la mínima necesaria y que no altere la API pública.
4. **Validá la corrección:** aplicá el cambio y volvé a correr `./gradlew test`. El test debe pasar sin que otros fallen.
5. **Commiteá chico:** hacé un commit por cada bug corregido con un mensaje descriptivo (por ejemplo: `fix: calcula diferencia en minutos contemplando cambio de hora`).
6. **Repetí** hasta que el 100% de la suite esté en verde.

## Archivos del proyecto

| Archivo | Contenido |
|---|---|
| `src/main/kotlin/com/university/dcp/DetectorRafagas.kt` | detector de ráfagas y anomalías (a corregir) |
| `src/main/kotlin/com/university/dcp/ExportadorJson.kt` | serializador a JSON (a corregir) |
| `src/main/kotlin/com/university/dcp/ModelosIncidentes.kt` | modelos de datos de eventos, ráfagas y saltos |
| `src/main/kotlin/com/university/dcp/Main.kt` | programa principal de la estación de monitoreo |
| `src/test/kotlin/com/university/dcp/` | suite de tests de la cátedra (no modificar) |
| `datos/lecturas.csv` | lecturas crudas de entrada |
| `salida-esperada.txt` | salida de consola de referencia del contrato |
| `reporte-esperado.txt` | contenido de referencia de `reporte.txt` |
| `AGENTS.md` | reglas para el agente de IA |
| `prompt-log/` | registro obligatorio de interacciones con LLMs |

## Registro de prompts (`prompt-log/`)

Cada interacción con un LLM sobre este repositorio debe guardarse en `prompt-log/`. La política completa está en [`AGENTS.md`](AGENTS.md); el formato de cada entrada está en [`prompt-log/README.md`](prompt-log/README.md).

No omitas interacciones. Si usaste un LLM para diagnosticar un error de test o pedir una corrección, va documentado.

## Qué entregás

| Entregable | Dónde |
|---|---|
| Módulos corregidos | `src/main/kotlin/com/university/dcp/` con `./gradlew test` en verde |
| Registro de prompts | [`prompt-log/`](prompt-log/) |

Entregar es pushear a la rama principal (o mergear tu pull request en `main`).

## Checklist de entrega

- [ ] `./gradlew test` da 100% verde en `main`, incluyendo `ContratoSalidaTest`.
- [ ] No modificaste ningún archivo en `src/test/`, ni `salida-esperada.txt`, ni `reporte-esperado.txt`.
- [ ] Cada bug fue corregido en su causa raíz con un cambio mínimo y justificado.
- [ ] Hay commits individuales por cada corrección, no un solo commit masivo.
- [ ] `prompt-log/` está completo y sin secretos.
