# TP 1 — Estación de monitoreo (código de partida)

Código provisto por la cátedra para el TP 1. **Funciona correctamente**: el trabajo
no es arreglarlo, es reorganizarlo.

## Qué hace

Lee `datos/lecturas.csv` (lecturas crudas de una estación de monitoreo ambiental),
normaliza las unidades, aplica la calibración de cada sensor, calcula estadísticas
por magnitud, detecta valores fuera de rango clasificándolos por severidad, escribe
un reporte por consola y deja una copia en `reporte.txt`.

## Cómo correrlo

```bash
kotlinc src/main/kotlin/Main.kt -include-runtime -d build/tp1.jar
java -jar build/tp1.jar
```

El programa busca `datos/lecturas.csv` relativo al directorio desde el que se ejecuta,
así que corré el `java -jar` parado en esta carpeta.

## Salidas de referencia

El programa produce **dos** salidas y el refactor no puede cambiar ninguna de las dos:
lo que imprime por consola y el archivo `reporte.txt`. Para verificar:

```bash
java -jar build/tp1.jar > salida-actual.txt
diff salida-esperada.txt salida-actual.txt
diff reporte-esperado.txt reporte.txt
```

Si ninguno de los dos `diff` imprime nada, el comportamiento se preservó.

## Archivos

| Archivo | Contenido |
|---|---|
| `src/main/kotlin/Main.kt` | el programa |
| `datos/lecturas.csv` | lecturas crudas, incluye filas inválidas a propósito |
| `salida-esperada.txt` | salida de consola de referencia |
| `reporte-esperado.txt` | contenido de referencia de `reporte.txt` |
