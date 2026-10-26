# Documentación Docente — TP 5 (Abstracción con Objetos)

Este documento es **exclusivo para el cuerpo docente** y no forma parte del código entregado a los alumnos.

---

## 1. Diseño Pedagógico del TP 5

El TP 5 pone en práctica los conceptos de la **Clase 5** (*Abstracción con objetos: clases, interfaces y responsabilidades*):
- **Objetos como unidades de responsabilidad:** una razón para cambiar, un nombre claro del dominio, separación entre datos y comportamiento de negocio.
- **Encapsulamiento estricto:** estado privado (`private set`), colecciones defensivas (`toList()`), validación de invariantes en constructores y en métodos mutadores (*tell, don't ask*).
- **Interfaces simples y polimorfismo:** contrato `Dispositivo` con operaciones mínimas comunes que reemplaza bifurcaciones por tipo (`when`/`is`) al operar sobre conjuntos heterogéneos.
- **Composición sobre herencia:** evitar jerarquías rígidas y frágiles. Las automatizaciones y disparadores se modelan mediante la interfaz `Sensor` y la clase `ReglaAutomatizacion`, sin requerir subclases como `LuzConSensor`.
- **Evaluación crítica del diseño asistido por IA:** los alumnos se enfrentan deliberadamente al sobre-diseño habitual de los LLMs (jerarquías con 4 niveles de herencia abstracta, clases `Manager`/`Helper`, patrones de diseño innecesarios) para podarlo y justificar la versión más simple.
- **Puente con el MVP (Parte B):** traslado directo de estos criterios al proyecto individual del estudiante.

---

## 2. Detalle de Casos por Componente

### Componente 1: `LuzInteligente.kt` (`LuzInteligenteTest`)
1. **Validación de invariantes al construir:** `require(id.isNotBlank())`, `require(nombre.isNotBlank())`, `require(potenciaBaseWatts > 0)` y `require(brilloInicial in 0..100)`.
2. **Encapsulamiento de `nivelBrillo`:** `var nivelBrillo: Int` debe tener `private set` para que cualquier cambio pase obligatoriamente por `ajustarBrillo(nuevoBrillo)`.
3. **Validación en `ajustarBrillo`:** `require(nuevoBrillo in 0..100)` para impedir estados inválidos en tiempo de ejecución.
4. **Cálculo de consumo:** si `!encendido` retorna `0.0`. Si está encendida retorna `potenciaBaseWatts * (nivelBrillo / 100.0)`.

### Componente 2: `Termostato.kt` (`TermostatoTest`)
1. **Validación de invariantes:** constructores con `require`, `histeresis >= 0`, `potencias > 0`.
2. **Programación segura (`programar`):** valida que la temperatura objetivo se encuentre en el rango admisible de confort (16.0 a 32.0 °C).
3. **Lógica de histéresis del compresor (`compresorActivo`):**
   - Modo `VENTILACION`: siempre `false`.
   - Modo `FRIO`: activo sólo si `temperaturaActual > (temperaturaDeseada + histeresis)`.
   - Modo `CALOR`: activo sólo si `temperaturaActual < (temperaturaDeseada - histeresis)`.
4. **Consumo segmentado:** si `!encendido` consume `0.0`. Si está encendido consume `potenciaVentiladorWatts` en reposo, o `potenciaVentiladorWatts + potenciaCompresorWatts` si el compresor está activo.

### Componente 3: `EnchufeInteligente.kt` (`EnchufeInteligenteTest`)
1. **Invariante de carga positiva:** `require(watts >= 0)` en `conectarCarga`.
2. **Protección automática ante sobrecarga:** si `watts > potenciaMaximaSoportadaWatts`, el enchufe marca `sobrecarga = true` y se apaga de inmediato.
3. **Invariante de encendido:** `encender()` lanza `IllegalStateException` si `sobrecarga == true`.
4. **Rearmado condicional (`rearmar`):** `check(potenciaConectadaWatts <= potenciaMaximaSoportadaWatts)` para impedir levantar la protección mientras la carga conectada continúe siendo riesgosa.

### Componente 4: `Automatizacion.kt`
1. **Contrato `Sensor`:** interfaz chica con `detectar(): Boolean`.
2. **Composición de reglas:** `ReglaAutomatizacion` evalúa `sensor.detectar()` y ejecuta la acción `accion()` sólo si se cumple la condición, devolviendo `true` o `false`.

### Componente 5: `CasaInteligente.kt` (`CasaInteligenteTest`)
1. **Colección defensiva:** expone `val dispositivos: List<Dispositivo> get() = _dispositivos.toList()`.
2. **Unicidad de identificadores:** `registrarDispositivo` valida que no exista otro dispositivo con el mismo ID.
3. **Consumo polimórfico:** `_dispositivos.sumOf { it.consumoActualWatts() }` invoca transparentemente la lógica de consumo de cada dispositivo concreto.
4. **Manejo de excepciones en lote:** `encenderTodo()` captura `IllegalStateException` para permitir que los dispositivos sanos enciendan aunque uno (ej. enchufe en sobrecarga) falle.

---

## 3. Verificación de la Solución de Referencia

Ejecutar:
```bash
./docente/tp-5-objetos/verificar-solucion.sh
```
El script copia temporalmente la solución de referencia a `tp/tp-5-objetos/src/main/kotlin/`, corre `./gradlew test --rerun` y restituye el código de partida del alumno. Debe reportar **23 tests pasados**.
