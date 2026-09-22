# Instrucciones para agentes (DCP TP 1 — Refactor)

Plantilla del curso **DCP** (Universidad Austral). Leé este archivo antes de editar el repo.

## Reglas del TP

1. **No cambies el comportamiento.** La salida de consola y el contenido de `reporte.txt` tienen que quedar idénticos a `salida-esperada.txt` y `reporte-esperado.txt`.
2. **No agregues tests.** Todavía no se vieron en la materia.
3. **No agregues funcionalidad.** Nada de sensores nuevos, archivos de configuración ni features "que quedarían buenas".

El programa de partida **no tiene errores**. Si aparece algo discutible, va anotado en `DISENO.md` (sección Hallazgos), no arreglado.

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

- Kotlin/JVM 21 + Gradle Wrapper (`./gradlew` / `.\gradlew.bat`), el mismo stack que el TP 0.
- No hace falta instalar Gradle globalmente.
- `./gradlew build` · `./gradlew run`
- Verificar que el comportamiento se preservó:

```bash
./gradlew run -q > salida-actual.txt
diff salida-esperada.txt salida-actual.txt
diff reporte-esperado.txt reporte.txt
```

El `-q` es obligatorio: sin él Gradle mezcla sus mensajes de progreso con la salida del programa.
