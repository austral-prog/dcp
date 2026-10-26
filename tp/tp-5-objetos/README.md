# TP 5 — Abstracción con objetos y modelado orientado a objetos

En las etapas anteriores trabajaste con funciones puras, inmutabilidad y modelado de datos con `data class` y `sealed class`.

En este TP avanzamos hacia la **abstracción con objetos**:
- **Objetos como unidades de responsabilidad:** una razón para cambiar, un nombre claro del dominio.
- **Encapsulamiento estricto:** estado privado, invariantes protegidas al construir y al mutar, *tell, don't ask*.
- **Interfaces simples y polimorfismo:** contratos chicos que reemplazan `when` por tipo.
- **Composición sobre herencia:** evitar jerarquías rígidas y extensas.
- **Evaluación crítica del diseño con IA:** podar el sobre-diseño y justificar cada abstracción.

El trabajo práctico tiene **dos partes complementarias**:
1. **Parte A (Código y tests automáticos):** Modelado de un sistema de domótica (**Smart Home**) en este repositorio, logrando que el 100% de los tests den verde.
2. **Parte B (Puente al MVP personal):** Aplicación de los conceptos aprendidos al modelo de objetos de tu propio MVP, documentado en el repo de tu proyecto.

---

## Cómo correrlo

Kotlin/JVM 21 con el **Gradle Wrapper**. Requisitos: **JDK 21**.

```bash
./gradlew test           # corre la suite de tests automatizados (macOS / Linux)
.\gradlew.bat test       # Windows
./gradlew run            # ejecuta la simulación de consola del hogar inteligente
```

---

## Parte A — Sistema Smart Home (Hogar Inteligente)

### Paso 1 — Ejecutá los tests iniciales

La cátedra entrega el proyecto con la suite completa ya incluida en `src/test/kotlin/com/university/dcp/smarthome/`:

* `ContratoSalidaTest.kt`: valida que la simulación por consola produzca la salida de referencia esperada (pasa inicialmente).
* `LuzInteligenteTest.kt`: valida encapsulamiento del brillo (0..100), invariantes y cálculo de consumo proporcional en watts.
* `TermostatoTest.kt`: valida rangos de temperatura deseada (16°C a 32°C), modos de climatización (`FRIO`, `CALOR`, `VENTILACION`), histéresis del compresor y consumo diferenciado.
* `EnchufeInteligenteTest.kt`: valida control de carga, corte automático por sobrecarga de seguridad y rearmado condicional.
* `CasaInteligenteTest.kt`: valida encapsulamiento defensivo de colecciones, encendido/apagado polimórfico, suma de consumos heterogéneos y automatizaciones basadas en sensores por composición.

Al ejecutar `./gradlew test` por primera vez, **18 de los 23 tests van a fallar**.

> **Regla de oro:** Los tests provistos en `src/test/` **no se modifican**. Tu misión es hacer que pasen modelando y protegiendo las clases en `src/main/kotlin/com/university/dcp/smarthome/`.

### Paso 2 — Desafío de diseño con IA: detectar sobre-diseño

1. Pedile a tu agente de IA que proponga un diseño orientado a objetos para el sistema Smart Home.
2. Observá su respuesta críticamente: ¿creó una jerarquía de herencia profunda (`AbstractDevice`, `BaseSwitchableDevice`)? ¿agregó clases accesorias innecesarias como `DeviceManagerFactory` o `LifecycleHandler`? ¿dejó propiedades públicas mutables (`var`) sin validación?
3. Guiá a la IA para descartar el sobre-diseño:
   * Mantener una **interfaz simple** (`Dispositivo`) para comportamiento intercambiable.
   * Usar **composición** para reglas y sensores (`Sensor`, `ReglaAutomatizacion`), sin crear subclases como `LuzConSensor`.
   * Proteger **invariantes** con `require(...)` en constructores y operaciones.
   * Exponer colecciones defensivas (`toList()`).

Registrá la interacción en `prompt-log/`.

---

## Parte B — Puente a tu MVP Personal

En tu propio repositorio de MVP:

1. **Revisá el modelo de clases:**
   * Abrí `DISENO.md` (o el archivo de arquitectura de tu MVP).
   * Identificá las entidades clave del negocio como **objetos de responsabilidad** (no sólo estructuras pasivas de datos).
2. **Aplicá encapsulamiento y contratos:**
   * Asegurá que cada clase cuide sus invariantes con `require` y que sus campos mutables tengan `private set`.
   * Modelá al menos **una interfaz simple** con polimorfismo justificado (por ejemplo: canales de notificación, métodos de pago, estrategias de precios o exportación).
   * Verificá que no existan jerarquías de herencia innecesarias ni clases "Dios" (`Manager`, `Utils` con lógica mezclada).
3. **Justificación escrita (1 párrafo por entidad principal):**
   * Explicá qué decide cada objeto y por qué esa responsabilidad le pertenece a él y no a otro.
4. **Tests del modelo:**
   * Agregá tests unitarios en tu MVP que verifiquen las invariantes y el comportamiento de las clases principales.

---

## Archivos del proyecto

| Archivo | Contenido |
|---|---|
| `src/main/kotlin/com/university/dcp/smarthome/Dispositivo.kt` | Interfaz común de dispositivos inteligentes |
| `src/main/kotlin/com/university/dcp/smarthome/LuzInteligente.kt` | Modelo de lámpara regulable y consumo |
| `src/main/kotlin/com/university/dcp/smarthome/Termostato.kt` | Modelo de climatización con modos e histéresis |
| `src/main/kotlin/com/university/dcp/smarthome/EnchufeInteligente.kt` | Modelo de enchufe con protección de sobrecarga |
| `src/main/kotlin/com/university/dcp/smarthome/Automatizacion.kt` | Sensores y automatizaciones por composición |
| `src/main/kotlin/com/university/dcp/smarthome/CasaInteligente.kt` | Contenedor, agregación y operaciones polimórficas |
| `src/main/kotlin/com/university/dcp/smarthome/Main.kt` | Simulación ejecutable por consola |
| `src/test/kotlin/com/university/dcp/smarthome/` | Suite de tests automatizados (no modificar) |
| `salida-esperada.txt` | Salida de consola de referencia del contrato |
| `AGENTS.md` | Reglas e instrucciones para el agente de IA |
| `prompt-log/` | Registro obligatorio de interacciones con LLMs |

---

## Registro de prompts (`prompt-log/`)

Cada interacción con un LLM sobre este repositorio debe guardarse en `prompt-log/`. Ver formato en [`prompt-log/README.md`](prompt-log/README.md).

---

## Checklist de entrega

- [ ] `./gradlew test` da **23/23 tests passed** en la rama principal.
- [ ] No modificaste ningún archivo en `src/test/` ni `salida-esperada.txt`.
- [ ] El estado mutable de las clases está protegido (`private set`, listas inmutables defensivas).
- [ ] Las invariantes se validan con `require` y las operaciones inválidas fallan con `check`.
- [ ] No se introdujeron jerarquías profundas de herencia injustificadas.
- [ ] `prompt-log/` contiene el registro del proceso con la IA y la discusión del sobre-diseño.
- [ ] **Parte B:** Tu repo de MVP tiene el modelo actualizado con invariantes, interfaces justificadas y tests unitarios funcionando.
