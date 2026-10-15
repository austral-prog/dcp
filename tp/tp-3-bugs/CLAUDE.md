# Claude

Las instrucciones autoritativas de este repositorio están en [`AGENTS.md`](AGENTS.md).

**Regla de prompt-log:** por cada prompt contra este repo, guardá el input y la respuesta del LLM en `prompt-log/` (ver `AGENTS.md` y `prompt-log/README.md`).

**Tests:** no modifiques ningún archivo bajo `src/test/` ni las salidas de referencia (`salida-esperada.txt`, `reporte-esperado.txt`). Diagnosticá y corregí los bugs en `src/main/` con cambios mínimos y verificá con `./gradlew test`.
