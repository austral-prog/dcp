# Instrucciones para agentes (DCP TP 5 — Abstracción con Objetos)

Plantilla del curso **DCP** (Universidad Austral). Leé este archivo antes de editar el repo.

## Reglas del TP

1. **Los tests y las salidas de referencia no se tocan:** No modifiques ningún archivo dentro de `src/test/kotlin/`, ni `salida-esperada.txt`. Tu trabajo es diseñar y refactorizar clases en `src/main/kotlin/` para que los tests pasen, jamás alterar los tests para ocultar fallas.
2. **Encapsulamiento y responsabilidad única:** Protegé el estado interno (`private set`, listas inmutables defensivas). Cada clase debe tener una sola razón para cambiar y cuidar sus propias reglas de negocio mediante *tell, don't ask*.
3. **Interfaces simples y composición:** No crees jerarquías profundas de herencia para extender comportamiento. Usá interfaces chicas y composición (por ejemplo, para sensores y automatizaciones).
4. **Evitar sobre-diseño de la IA:** Cuestioná jerarquías complejas, managers omnipotentes o factories innecesarias. Diseñá sólo las abstracciones que el dominio requiere hoy.
5. **No alteres las firmas públicas requeridas:** Mantené los contratos e interfaces del paquete `com.university.dcp.smarthome` compatibles con los tests automatizados.
6. **Verificación continua:** Luego de cada cambio, ejecutá `./gradlew test` para asegurar que los tests pasen.

## Política obligatoria: `prompt-log/`

**Para CADA prompt** que hagas contra este repositorio con un LLM (Claude, Copilot, opencode, GPT, Cursor, etc.), **debés guardar el prompt de entrada Y la respuesta del modelo** en `prompt-log/`.

- Convención: un archivo markdown por interacción (`prompt-log/YYYY-MM-DD-HHMM-tema-corto.md`).
- Contenido mínimo: herramienta/modelo, sección **Prompt**, sección **Respuesta** y breve justificación crítica del diseño.
- Ver formato en [`prompt-log/README.md`](prompt-log/README.md).

## Build

- Kotlin/JVM 21 + Gradle Wrapper (`./gradlew`).
- Tests con `kotlin.test` sobre JUnit 5.
- Comandos:
  - `./gradlew test`
  - `./gradlew run`
  - `./gradlew build`
