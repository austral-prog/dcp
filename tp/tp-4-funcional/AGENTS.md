# Instrucciones para agentes (DCP TP 4 — Modelado con Tipos y Transformación Funcional)

Plantilla del curso **DCP** (Universidad Austral). Leé este archivo antes de editar el repo.

## Reglas del TP

1. **Los tests y las salidas de referencia no se tocan:** No modifiques ningún archivo dentro de `src/test/kotlin/`, ni `salida-esperada.txt`, ni `reporte-esperado.txt`. Tu trabajo es modelar invariantes y transformar la lógica en `src/main/kotlin/` para que los tests existentes pasen, jamás alterar los tests para ocultar fallas.
2. **Inmutabilidad, tipos e invariantes:** Protegé las reglas del dominio en las `data class` usando bloques `init` con `require(...)`, contemplá todas las variantes de las `sealed class` y preservá la inmutabilidad utilizando `val`, colecciones de solo lectura y `copy()`.
3. **Transformación funcional pura:** Reemplazá los bucles imperativos mutables y los usos inseguros de `reduce` por funciones puras y pipelines de colecciones (`map`, `filter`, `fold`, etc.) que manejen correctamente los casos borde (como colecciones vacías).
4. **No alteres las firmas públicas:** Mantené los nombres de clases, propiedades y firmas de métodos públicos de `AnalizadorStock`, `ProcesadorAdn` y `MotorGaming` para no romper el contrato con la suite de tests.
5. **Verificación continua:** Luego de cada cambio, ejecutá `./gradlew test` para asegurar que los tests del módulo pasen sin introducir regresiones.

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
