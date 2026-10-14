# Instrucciones de Copilot — DCP TP 3 Depuración asistida por IA (Universidad Austral)

Las instrucciones autoritativas para agentes están en `AGENTS.md`. Este archivo replica la política clave para que Copilot la reciba como contexto del repositorio.

## Reglas del TP

- **Los tests no se tocan:** no modifiques ningún archivo en `src/test/`, ni `salida-esperada.txt`, ni `reporte-esperado.txt`. Tu misión es hacer que `./gradlew test` pase corrigiendo el código de producción en `src/main/`.
- **Correcciones mínimas:** diagnosticá la causa raíz y aplicá el cambio más acotado y seguro posible. No hagas refactors masivos no solicitados.
- **No inventes funcionalidad ni alteres contratos.**
- **Verificación continua:** corré `./gradlew test` para validar cada corrección sin generar regresiones.

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
