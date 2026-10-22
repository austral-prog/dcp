# Instrucciones de Copilot — DCP TP 4 Modelado con Tipos y Transformación Funcional (Universidad Austral)

Las instrucciones autoritativas para agentes están en `AGENTS.md`. Este archivo replica la política clave para que Copilot la reciba como contexto del repositorio.

## Reglas del TP

- **Los tests no se tocan:** no modifiques ningún archivo en `src/test/`, ni `salida-esperada.txt`, ni `reporte-esperado.txt`. Tu misión es hacer que `./gradlew test` pase mejorando el código de producción en `src/main/`.
- **Invariantes y programación funcional:** validá las reglas de dominio en `init { require(...) }` de las `data class`, usá `sealed class` para las alternativas de dominio y transformá los bucles imperativos o usos frágiles de `reduce` en pipelines funcionales puros e inmutables (`map`, `filter`, `fold`, `copy`).
- **No alteres firmas públicas ni inventes dependencias.**
- **Verificación continua:** corré `./gradlew test` para validar cada módulo sin generar regresiones.

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
