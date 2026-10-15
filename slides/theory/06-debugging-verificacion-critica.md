---
marp: true
theme: dcp
paginate: true
title: Clase 3 - Debugging y verificacion critica de codigo generado por IA
---

<!-- _class: cover -->
<!-- _paginate: false -->

# Debugging y verificacion critica

## Del codigo generado por IA al codigo en el que podemos confiar

**Clase teorica 3 · DCP**

---

## Objetivo de la clase

La IA te entrega codigo en segundos. **Vos sos quien responde por el.**

Hoy vamos a:

- **Leer e interpretar** errores y stack traces en Kotlin.
- Reconocer las **debilidades tipicas** del codigo generado por IA.
- Aplicar un **checklist de verificacion critica** antes de aceptar codigo.
- Integrarlo en el ciclo de trabajo de tu MVP.

> Construir con IA no elimina el trabajo de ingenieria: lo desplaza de **escribir** a **verificar**.

---

## Recorrido de esta clase

1. El ciclo del MVP con IA: donde entra la verificacion.
2. Depuracion: errores y stack traces en Kotlin.
3. Debilidades tipicas del codigo generado por IA.
4. Checklist de verificacion critica.
5. Reportar bugs a la IA: prompts que funcionan.

---

<!-- _class: section -->
<!-- _paginate: false -->

# El ciclo del MVP con IA

## Generar es barato, verificar es el trabajo

---

## Un MVP con IA: cuatro pasos

1. **Especificar**: que problema, que entradas, que salidas (clase 2).
2. **Generar**: la IA propone codigo.
3. **Verificar**: lo ejecutamos, lo leemos y lo probamos.
4. **Iterar**: corregimos con evidencia y volvemos a generar.

El paso 3 es **el que no se puede delegar**.

> Si saltas la verificacion, no estas construyendo un MVP: estas acumulando codigo que no entendes.

---

## ¿Por que el codigo de la IA falla?

- Un modelo **predice texto plausible**; no ejecuta ni "sabe" que funciona.
- Solo ve **tu prompt**: no conoce tu intencion ni tus datos reales.
- Su conocimiento tiene **fecha de corte**: librerias y versiones cambian.
- Suena **igual de seguro** cuando acierta que cuando se equivoca.

> Plausible ≠ correcto. La confianza en el tono **no es evidencia**.

---

<!-- _class: section -->
<!-- _paginate: false -->

# Depuracion en Kotlin

## Leer errores y stack traces

---

## Tres tipos de error, tres momentos

| Tipo | Cuando aparece | Ejemplo |
|---|---|---|
| **Compilacion** | Antes de ejecutar | `Unresolved reference`, `Type mismatch` |
| **Ejecucion** | Corriendo, el programa se cae | `NullPointerException`, `NumberFormatException` |
| **Logico** | No se cae, pero el resultado es **incorrecto** | Total mal calculado |

Los dos primeros **avisan**. El tercero es silencioso: solo lo detectan los **tests**.

---

## Errores de compilacion

El compilador te dice **archivo, linea y motivo**:

```text
e: Reporte.kt:7:23 Unresolved reference: toIntOrZero
e: Reporte.kt:9:30 Type mismatch: inferred type is String
   but Int was expected
```

- Empeza por el **primer** error: los siguientes suelen ser consecuencia.
- Lee el mensaje completo antes de tocar codigo.
- `Unresolved reference` casi siempre es: import faltante, typo o **API que no existe**.

---

## Anatomia de un stack trace

```text
Exception in thread "main" java.lang.NumberFormatException:
    For input string: "abc"
	at java.base/java.lang.Integer.parseInt(Integer.java:661)
	at kotlin.text.StringsKt.toInt(StringNumberConversionsJVM.kt:..)
	at ReporteKt.parsearMonto(Reporte.kt:12)
	at ReporteKt.main(Reporte.kt:5)
```

1. **Tipo y mensaje**: *que* paso (`NumberFormatException`, input `"abc"`).
2. **Pila de llamadas**: *por donde* paso, de la mas reciente a la mas antigua.
3. **Archivo:linea**: *donde* mirar primero.

---

## Como leerlo: de arriba hacia abajo

- Lo **primero** es el tipo de excepcion y su mensaje.
- Salta las lineas de librerias (`java.base`, `kotlin.*`): **busca la primera linea de tu codigo**.
- En el ejemplo: `Reporte.kt:12` → ahi se llamo a `toInt()` con un dato malo.
- Los frames de abajo (`main`) dicen **quien lo llamo**.

> La causa real suele estar **un poco antes** de la linea que explota: de donde vino ese dato?

---

## Excepciones frecuentes en Kotlin

| Excepcion | Causa tipica |
|---|---|
| `NumberFormatException` | Convertir texto no numerico con `toInt()` |
| `NullPointerException` | Valor `null` desde Java o uso de `!!` |
| `IndexOutOfBoundsException` | Indice fuera de la lista |
| `NoSuchElementException` | `first()` o `single()` sobre coleccion vacia |
| `ArithmeticException` | Division entera por cero |
| `IllegalArgumentException` / `IllegalStateException` | Fallo de `require` / `check` |
| `ClassCastException` | Cast `as` a un tipo incorrecto |

---

## `Caused by`: la causa raiz

```text
Exception in thread "main" java.lang.IllegalStateException:
    No se pudo cargar el archivo
	at CargadorKt.cargar(Cargador.kt:8)
	at MainKt.main(Main.kt:4)
Caused by: java.io.FileNotFoundException: datos/ventas.csv
	at java.base/java.io.FileInputStream.open0(Native Method)
	...
```

- La excepcion de arriba es un **sintoma**; el `Caused by` del final es la **raiz**.
- Siempre baja hasta el **ultimo** `Caused by`.

---

## Metodo de depuracion

1. **Reproducir**: mismo dato, mismo error, siempre.
2. **Leer** el error completo (tipo, mensaje, primera linea tuya).
3. **Hipotesis**: "creo que falla porque X".
4. **Comprobar**: `println`, test o debugger del IDE (breakpoint).
5. **Corregir** una cosa por vez y **volver a correr**.
6. **Fijarlo con un test** para que no reaparezca.

> No cambies codigo "a ver si anda". Cada cambio responde a una hipotesis.

---

## El debugger del IDE

En IntelliJ IDEA:

- **Breakpoint** (clic en el margen): el programa se frena ahi.
- **Step over / Step into**: avanzar linea a linea o entrar a la funcion.
- **Variables**: ver el valor real de cada una en ese momento.
- **Evaluate expression**: probar una expresion sin editar codigo.

`println` sirve para empezar; el debugger muestra **estado**, no solo texto.

---

<!-- _class: section -->
<!-- _paginate: false -->

# Debilidades de la IA

## Tres fallas tipicas

---

## Mapa de debilidades

| Debilidad | Como se manifiesta | Quien la detecta |
|---|---|---|
| **Alucinacion de APIs** | Usa funciones o librerias que no existen | Compilador (a veces) |
| **Sobre-ingenieria** | Mucha estructura para un problema simple | Tu criterio de diseño |
| **"Compila pero no sirve"** | Corre, pero el resultado es incorrecto | Tests y casos borde |

La primera **avisa**. Las otras dos **pasan desapercibidas** si no verificas.

---

## 1 · Alucinacion de APIs

La IA inventa un metodo, una clase o una dependencia que **suena bien**.

```kotlin
val monto = texto.toIntOrZero()          // no existe en Kotlin
val ultimo = lista.second()              // no existe
import kotlin.io.csv.CsvReader           // paquete inexistente
```

- El compilador lo marca: `Unresolved reference`.
- Mas grave: usa una API **real pero obsoleta** o de **otra version** (`sumBy` fue reemplazada por `sumOf`).
- O un parametro que **no existe** en tu version de la libreria.

---

## Como enfrentar las alucinaciones

- **No pidas mas codigo**: abri la **documentacion oficial** (kotlinlang.org, KDoc).
- Chequea que el import exista y que la **version** coincida con tu `build.gradle.kts`.
- Preferi la **libreria estandar** antes que dependencias que no conoces.
- Desconfia de dependencias nuevas: ¿existe? ¿esta mantenida? ¿la necesito?

> Si no podes encontrarla en la documentacion, **probablemente no existe**.

---

## 2 · Sobre-ingenieria

Pediste sumar dos precios y recibis esto:

```kotlin
interface CalculadorStrategy { fun calcular(a: Double, b: Double): Double }
class SumaStrategy : CalculadorStrategy { /* ... */ }
class CalculadorFactory { fun crear(): CalculadorStrategy = SumaStrategy() }
class CalculadorService(private val factory: CalculadorFactory) { /* ... */ }
```

En vez de:

```kotlin
fun sumar(a: Double, b: Double) = a + b
```

---

## Por que es un problema

- Mas codigo = mas **superficie de error** y mas para **entender y mantener**.
- Resuelve problemas que **no tenes** (extensibilidad, patrones "por si acaso").
- Va contra la idea misma de **MVP**: lo minimo que sirve.
- Esconde la logica real entre capas.

**Pregunta de control:** ¿puedo explicar **por que existe cada clase**? Si no, sobra.

> Pedile a la IA: *"la solucion mas simple, sin abstracciones que no necesite hoy"*.

---

## 3 · Codigo que "compila pero no sirve"

Compila, corre y **parece** funcionar:

```kotlin
fun promedio(notas: List<Int>): Int = notas.sum() / notas.size
```

```kotlin
promedio(listOf(7, 8))   // devuelve 7, no 7.5
promedio(emptyList())    // ArithmeticException: / by zero
```

- **Division entera**: trunca sin avisar.
- **Lista vacia**: no estaba prevista.
- Con el "caso feliz" del prompt, todo luce bien.

---

## Mas formas de "compilar pero no servir"

| Sintoma | Ejemplo |
|---|---|
| **Errores tragados** | `catch (e: Exception) { return 0.0 }` oculta el fallo |
| **Resultado hardcodeado** | Devuelve justo el valor que el test espera |
| **Ignora reglas del dominio** | No valida porcentaje entre 0 y 100 |
| **Resuelve otro problema** | Cumple el prompt, no tu necesidad |
| **Tests que no prueban** | Assertions triviales o que replican la implementacion |

> El compilador valida **sintaxis y tipos**, no que el programa haga lo que necesitas.

---

<!-- _class: section -->
<!-- _paginate: false -->

# Verificacion critica

## Checklist antes de aceptar codigo

---

## Tres preguntas que no se negocian

1. **¿Entiendo cada linea?**
2. **¿Hay tests que lo prueben?**
3. **¿Que pasa si cambio un dato borde?**

Si alguna respuesta es **"no"**, el codigo **todavia no esta aceptado**.

> Es la version corta de tu responsabilidad como autor del MVP.

---

## 1 · ¿Entiendo cada linea?

- Puedo **explicar con mis palabras** que hace cada funcion y por que.
- Reconozco cada **funcion, import y dependencia** y verifique que existen.
- No hay codigo "magico" que quedo porque "la IA lo puso".
- Hay **partes que sobran** (clases, parametros, codigo muerto): las elimino.

Tecnica: pedile a la IA que **explique** el codigo y **contrastalo** con lo que lees. Si no coinciden, investiga.

> Si no lo podes explicar en la defensa del TP, no lo entregues.

---

## 2 · ¿Hay tests que lo prueben?

- Existe al menos un test por **comportamiento** importante.
- Los tests **fallan** si el codigo esta mal (probalos rompiendo el codigo a proposito).
- Cubren casos **felices, borde e invalidos** (clase 2).
- **No** los escribio la IA a partir de su propio codigo sin que los revises: pueden validar el bug.
- Corren con un comando: `./gradlew test`.

> Un test que nunca estuvo en rojo no demuestra nada.

---

## 3 · ¿Que pasa si cambio un dato borde?

Probar a proposito los extremos:

| Categoria | Ejemplos |
|---|---|
| **Vacio** | Lista vacia, texto `""`, archivo sin filas |
| **Limites** | `0`, `1`, `-1`, maximo permitido, `Int.MAX_VALUE` |
| **Invalido** | Texto en lugar de numero, `null`, fecha imposible |
| **Formato** | Espacios, mayusculas, coma vs punto decimal |
| **Cantidad** | Un solo elemento, miles de elementos |

Para cada uno: ¿el programa **responde con sentido** o se rompe?

---

## Checklist completo (para imprimir)

- [ ] Compila **sin warnings relevantes** y lo ejecute yo.
- [ ] Verifique que **cada API / import / dependencia existe** en la documentacion.
- [ ] **Entiendo** cada linea y puedo explicarla.
- [ ] No hay **abstracciones de mas** para el alcance del MVP.
- [ ] Hay **tests** de caso feliz, borde e invalido, y **pasan**.
- [ ] Probe los **datos borde** a mano o con tests.
- [ ] Los **errores se manejan** (no se tragan en silencio).
- [ ] Hace lo que pide **mi especificacion**, no solo lo que dijo el prompt.

---

## Aplicado: revisando un codigo de la IA

```kotlin
fun descuento(precio: Double, porcentaje: Int): Double {
    try {
        return precio * (1 - porcentaje / 100)
    } catch (e: Exception) {
        return precio
    }
}
```

¿Que falla? Usa el checklist.

- `porcentaje / 100` es **division entera**: da `0` salvo para `100`.
- `descuento(200.0, 25)` devuelve `200.0` en lugar de `150.0`.
- El `catch` **esconde** errores y no hay validacion de rango 0..100.

Un solo test del **caso feliz** lo habria detectado.

---

<!-- _class: section -->
<!-- _paginate: false -->

# Trabajar con la IA

## Reportar el bug con evidencia

---

## Prompts de depuracion

**Mal:** *"no anda, arreglalo"*.

**Bien:** darle el **contexto minimo y la evidencia**:

```text
Tengo esta funcion en Kotlin 2.x y este test que falla.

Codigo: <funcion>
Test: promedio(listOf(7, 8)) deberia dar 7.5
Resultado actual: 7
Stack trace / error: <pegado completo>

Explica la causa antes de proponer el cambio y
cambia lo minimo necesario.
```

- Pega el **error completo**, no un resumen.
- Pedi la **causa** antes que el arreglo.

---

## Reglas para iterar con IA

- Un problema por vez; un cambio por vez.
- **Volve a correr** los tests despues de cada cambio.
- Si el mismo error vuelve despues de 2-3 intentos: **frena y pensa** vos.
- Guarda en el **prompt-log** los prompts importantes y como los verificaste.
- **Commits chicos**: si la IA rompe algo, podes volver atras.

---

## Para llevarse

- La IA **genera**; vos **verificas**. La responsabilidad es tuya.
- Un **stack trace** dice que paso y donde: busca **la primera linea de tu codigo** y el ultimo `Caused by`.
- Tres fallas tipicas: **APIs inventadas**, **sobre-ingenieria** y codigo que **compila pero no sirve**.
- Antes de aceptar codigo: **¿lo entiendo? ¿hay tests? ¿que pasa con los bordes?**
- Reporta bugs a la IA con **evidencia**, no con "no anda".

> Compilar es el primer filtro, no el ultimo.
