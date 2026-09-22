# prompt-log

Registro obligatorio de cada interacción con un LLM sobre este repositorio.

## Por qué

El curso pide evidencia de cómo usaste herramientas de IA. Cada prompt y cada respuesta van acá.

## Convención

Un archivo markdown por interacción:

```
prompt-log/YYYY-MM-DD-HHMM-tema-corto.md
```

Ejemplo: `prompt-log/2026-09-11-2015-agregar-modelo-usuario.md`

## Plantilla

```markdown
# <tema corto>

- **Fecha:** YYYY-MM-DD HH:MM
- **Herramienta:** opencode / Claude / Copilot / ChatGPT / otra
- **Modelo:** <id o nombre del modelo, si lo sabés>

## Prompt

<texto exacto (o lo más fiel posible) que enviaste>

## Respuesta

<salida del LLM>
```

## Reglas

- Registrá **todas** las interacciones, no solo las “importantes”.
- **No** incluyas API keys, tokens ni contraseñas. Si aparecen, reemplazalos por `***`.
- No borres entradas anteriores.
- El archivo `.gitkeep` de esta carpeta solo existe para que Git trackee el directorio vacío; no lo uses como log.
