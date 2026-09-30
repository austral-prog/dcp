# Instrucciones de Copilot — DCP TP 2 TDD (Universidad Austral)

Las instrucciones autoritativas para agentes están en `AGENTS.md`. Este archivo replica la política clave para que Copilot la reciba como contexto del repositorio.

## Reglas del TP

- **El contrato no se toca**: no modifiques `ContratoSalidaTest.kt`, `salida-esperada.txt` ni `reporte-esperado.txt`. `./gradlew test` tiene que dar verde.
- **Tests**: seguí `agente-tests.md` y leé `CASOS.md` antes de escribir tests. Proponé casos nuevos, no reemplaces los de la persona.
- **Rojo antes que verde**: en la Parte 2 el test se commitea fallando antes del código que lo hace pasar.
- No agregues funcionalidad fuera de la pedida en el README.

## Política obligatoria de `prompt-log/`

Por **cada** prompt que hagas sobre este repo con un LLM (Copilot, Claude, opencode, GPT, Cursor u otro), el alumno debe guardar **el prompt de entrada y la salida del modelo** en `prompt-log/`.

- Un archivo markdown por interacción.
- Nombre: `prompt-log/YYYY-MM-DD-HHMM-tema-corto.md`
- Incluir: herramienta y modelo usados, una sección **Prompt** y una sección **Respuesta**.
- El formato completo está en `prompt-log/README.md`.
- No saltees interacciones chicas: si usaste un LLM, va al log.

## Secretos

- Nunca commitees API keys, tokens ni credenciales.
- El log no debe contener secretos: reemplazalos por `***`.

## Build

- Kotlin/JVM 21 con Gradle Wrapper. No hace falta Gradle global.
- Comandos: `./gradlew test`, `./gradlew build`, `./gradlew run` (en Windows: `.\gradlew.bat`).
- Tests con `kotlin.test` sobre JUnit 5, en `src/test/kotlin/com/university/dcp/`.
