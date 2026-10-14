# TP 3 — Depuración asistida por IA

Seguís con la estación de monitoreo ambiental. En el TP 1 la ordenaste y en el TP 2 le
escribiste tests unitarios y agregaste nuevas unidades de presión con TDD.

Ahora recibís una nueva versión estructurada del sistema que contiene **bugs introducidos
inadvertidamente**. El objetivo del TP 3 es **encontrar, diagnosticar y corregir los bugs
trabajando en pareja con un agente de IA**, utilizando una suite de tests automatizados
como red de seguridad y guía de progreso.

## Cómo correrlo

Kotlin/JVM 21 con el **Gradle Wrapper**, igual que en los TPs anteriores. Requisitos: **JDK 21**.

```bash
./gradlew test           # corre la suite de tests automatizados (macOS / Linux)
.\gradlew.bat test       # Windows
./gradlew run            # ejecuta el programa
```

## Los tests automatizados

A diferencia de los TPs anteriores, este repositorio **ya viene con una suite completa de tests automatizados** en `src/test/kotlin/com/university/dcp/`.

Al clonar el repositorio y correr `./gradlew test`, **varios tests van a fallar**:
- Tests unitarios específicos que detectan fallas en conversión de unidades, calibración de sensores, umbrales de alarma, estadísticas y ordenamiento del ranking.
- El test de contrato `ContratoSalidaTest.kt`, que ejecuta el programa completo sobre `datos/lecturas.csv` y compara la consola y `reporte.txt` con las salidas de referencia `salida-esperada.txt` y `reporte-esperado.txt`.

**Los tests de la cátedra no se tocan:** el objetivo es hacer que pasen corrigiendo el código en `src/main/`, no adaptando los tests a los bugs.

Tu trabajo termina cuando `./gradlew test` da **100% verde**.

## Flujo de trabajo con tu agente de IA

Vas a usar un agente (Claude Code, GitHub Copilot CLI, OpenCode, Cursor, etc.) para que te ayude a diagnosticar y arreglar cada falla:

1. **Corré los tests y aislá un problema:** ejecutá `./gradlew test` y elegí un test que falle.
2. **Presentale la falla al agente:** mostrale el mensaje de error, la aserción incumplida y el código correspondiente.
3. **Analizá el diagnóstico:** pedile que explique la causa raíz. No aceptes código a ciegas: asegurate de entender por qué estaba fallando y si la solución propuesta es la mínima necesaria.
4. **Aplicá y verificá:** aplicá la corrección y volvé a correr `./gradlew test`. El test que fallaba ahora debe pasar sin haber roto otros.
5. **Commiteá chico:** hacé un commit por cada bug solucionado con un mensaje claro (por ejemplo, `fix: corrige calibracion de fabrica de sensor S-02`).
6. **Repetí** hasta que todos los tests unitarios y `ContratoSalidaTest` estén en verde.

## Salidas de referencia

El programa debe producir exactamente las salidas documentadas en:
- `salida-esperada.txt`: salida completa por consola al procesar `datos/lecturas.csv`.
- `reporte-esperado.txt`: contenido generado en `reporte.txt`.

Podés verificar manualmente la salida de consola con:

```bash
./gradlew run -q > salida-actual.txt
diff salida-esperada.txt salida-actual.txt
diff reporte-esperado.txt reporte.txt
```

Si ambos `diff` no imprimen nada, el programa produce el resultado exacto.

## Archivos del proyecto

| Archivo | Contenido |
|---|---|
| `src/main/kotlin/com/university/dcp/` | código fuente de la estación de monitoreo (a corregir) |
| `src/test/kotlin/com/university/dcp/` | suite de tests provista por la cátedra (no modificar) |
| `datos/lecturas.csv` | archivo de entrada con lecturas de prueba |
| `salida-esperada.txt` | salida de consola esperada |
| `reporte-esperado.txt` | contenido esperado de `reporte.txt` |
| `AGENTS.md` | reglas e instrucciones para los agentes de IA |
| `prompt-log/` | registro obligatorio de prompts y respuestas |

## Registro de prompts (`prompt-log/`)

Cada interacción con un LLM sobre este repositorio debe guardarse en `prompt-log/`. La política completa está en [`AGENTS.md`](AGENTS.md); el formato de cada entrada está en [`prompt-log/README.md`](prompt-log/README.md).

No omitas interacciones. Si usaste un LLM para diagnosticar un error de test o pedir una corrección, va documentado.

## Qué entregás

| Entregable | Dónde |
|---|---|
| El programa corregido | `src/main/kotlin/com/university/dcp/` con `./gradlew test` en verde |
| Registro de prompts | [`prompt-log/`](prompt-log/) |

Entregar es pushear a la rama principal (o mergear tu pull request en `main`). Hacé commits claros, uno por bug corregido.

## Checklist de entrega

- [ ] `./gradlew test` da 100% verde en `main`, incluyendo `ContratoSalidaTest`.
- [ ] No modificaste ningún archivo en `src/test/`, ni `salida-esperada.txt`, ni `reporte-esperado.txt`.
- [ ] Los `diff` de consola y `reporte.txt` no arrojan diferencias.
- [ ] Cada bug fue corregido en su causa raíz de forma acotada y justificada.
- [ ] Hay commits individuales por cada corrección, no un solo commit con todo.
- [ ] `prompt-log/` está completo y sin secretos.
