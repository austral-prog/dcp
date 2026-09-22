# Instrucciones de Copilot — DCP TP 1 Refactor (Universidad Austral)

Las instrucciones autoritativas para agentes están en `AGENTS.md`. Este archivo replica la política clave para que Copilot la reciba como contexto del repositorio.

## Reglas del TP

- **No cambies el comportamiento**: la consola y `reporte.txt` tienen que seguir coincidiendo con `salida-esperada.txt` y `reporte-esperado.txt`.
- **No agregues tests** ni funcionalidad nueva.
- El código de partida no tiene errores: lo discutible se anota en `DISENO.md`, no se arregla.

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

- Kotlin/JVM 21 con Gradle Wrapper, el mismo stack que el TP 0. No hace falta Gradle global.
- Comandos: `./gradlew build`, `./gradlew run` (en Windows: `.\gradlew.bat`).
- Para comparar contra las salidas de referencia, `./gradlew run -q` (sin `-q` Gradle ensucia stdout).
- Preferí Kotlin idiomático: funciones y `data class`. No hacen falta clases ni interfaces.
