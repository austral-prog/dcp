# Instrucciones para agentes (DCP TP 2 — TDD)

Plantilla del curso **DCP** (Universidad Austral). Leé este archivo antes de editar el repo.

## Reglas del TP

1. **El contrato no se toca.** No modifiques `src/test/kotlin/com/university/dcp/ContratoSalidaTest.kt`, `salida-esperada.txt` ni `reporte-esperado.txt`. `./gradlew test` tiene que dar verde en `main`.
2. **Tests: seguí [`agente-tests.md`](agente-tests.md).** Si te piden tests, leé ese archivo y `CASOS.md` antes de escribir nada. Los casos de `CASOS.md` los decide la persona: proponé casos nuevos, no los reemplaces.
3. **Rojo antes que verde.** En la Parte 2 (nuevas unidades) el test se escribe y se commitea fallando antes de escribir el código que lo hace pasar. No escribas los dos en el mismo paso.
4. **No agregues funcionalidad** fuera de la pedida en el README.
5. **Trabajo por ramas.** Nada va directo a `main` salvo el commit `Importa el refactor del TP 1`. Parte 1 en `tests/tp-1`, Parte 2 en `feature/unidades`, cada una con su pull request.

## Política obligatoria: `prompt-log/`

**Para CADA prompt** que hagas contra este repositorio con un LLM (Claude, opencode, GPT, Copilot, Cursor, etc.), **debés guardar el prompt de entrada Y la respuesta del modelo** en `prompt-log/`.

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

- Kotlin/JVM 21 + Gradle Wrapper (`./gradlew` / `.\gradlew.bat`), el mismo stack que el TP 1.
- Tests con `kotlin.test` sobre JUnit 5, en `src/test/kotlin/com/university/dcp/`.
- `./gradlew test` · `./gradlew build` · `./gradlew run`
