# Instrucciones para agentes (DCP TP 3 — Depuración asistida por IA)

Plantilla del curso **DCP** (Universidad Austral). Leé este archivo antes de editar el repo.

## Reglas del TP

1. **Los tests y las salidas de referencia no se tocan:** No modifiques ningún archivo dentro de `src/test/kotlin/`, ni `salida-esperada.txt`, ni `reporte-esperado.txt`. Tu trabajo es diagnosticar y reparar los errores en `src/main/kotlin/` para que los tests existentes pasen, jamás alterar los tests para ocultar fallas.
2. **Correcciones mínimas y precisas:** Atacá la causa raíz de cada bug con el cambio más acotado y seguro posible. No reescribas clases completas, no alteres interfaces públicas arbitrariamente ni introduzcas refactors masivos no solicitados.
3. **No agregues funcionalidad extra:** No inventes sensores, formatos ni dependencias que no figuren en la especificación.
4. **Verificación continua:** Luego de cada cambio, ejecutá `./gradlew test` para asegurar que el test correspondiente pase y que no se hayan introducido regresiones en otros componentes.

## Política obligatoria: `prompt-log/`

**Para CADA prompt** que hagas contra este repositorio con un LLM (Claude, Copilot, opencode, GPT, Cursor, etc.), **debés guardar el prompt de entrada Y la respuesta del modelo** en `prompt-log/`.

- Convención recomendada: un archivo markdown por interacción.
- Nombre: `prompt-log/YYYY-MM-DD-HHMM-tema-corto.md`
- Contenido mínimo:
  - herramienta y modelo usados
  - sección **Prompt** (el texto que enviaste)
  - sección **Respuesta** (la salida del LLM)
- Ver el formato detallado en [`prompt-log/README.md`](prompt-log/README.md).

No omitas interacciones “chicas”. Si usaste un LLM sobre este repo, va al log.

## Secretos

- Nunca commitees API keys, tokens ni credenciales.
- El log **no debe contener secretos**. Redactalos (`***`) si aparecen en un prompt o respuesta.

## Build

- Kotlin/JVM 21 + Gradle Wrapper (`./gradlew` / `.\gradlew.bat`), el mismo stack que los TPs anteriores.
- Tests con `kotlin.test` sobre JUnit 5, en `src/test/kotlin/com/university/dcp/`.
- Comandos:
  - `./gradlew test` (ejecuta la suite de pruebas)
  - `./gradlew run` (ejecuta el programa principal)
  - `./gradlew build` (compila y verifica)
