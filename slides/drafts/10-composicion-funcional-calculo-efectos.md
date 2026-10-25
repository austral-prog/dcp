---
marp: true
theme: dcp
paginate: true
title: Clase 6 - Composición funcional y separación cálculo/efectos
---

<!-- _class: cover -->
<!-- _paginate: false -->

# Componer y separar

## Composición funcional y separación entre cálculo y efectos

**Clase teórica 6 · DCP**

---

## Objetivo de la clase

Código que se puede **probar sin el mundo exterior** es código que se puede **cambiar sin miedo**, también cuando lo cambia una IA.

Hoy vamos a:

- **Componer** funciones pequeñas para armar transformaciones más grandes.
- Distinguir **cálculo** (lógica pura) de **efectos** (I/O, red, base de datos, reloj).
- Ver un ejemplo chico: **antes y después** de separarlos.
- Entender por qué esta separación hace más **seguro** modificar código con ayuda de IA.

> No se trata de eliminar los efectos: se trata de saber **dónde viven**.

---

## Recorrido de esta clase

1. Composición funcional: de funciones chicas a pipelines.
2. Cálculo vs. efectos: qué es cada uno y cómo reconocerlos.
3. Ejemplo en Kotlin: de una función mezclada a un núcleo puro con una capa de efectos fina.
4. Testear y modificar con IA sin romper lo que ya funciona.
5. Actividad rápida y cierre.

---

<!-- _class: section -->
<!-- _paginate: false -->

# Composición funcional

## Armar lo grande con piezas chicas

---

## Repaso: una función pura

Misma entrada, misma salida, sin efectos observables:

```kotlin
fun aprobada(nota: Int): Boolean = nota >= 6
fun porcentaje(nota: Int): Int = nota * 10
fun etiqueta(porcentaje: Int): String = "$porcentaje%"
```

Cada una es chica, tiene un nombre que dice **qué** hace y se entiende sin leer el resto del programa.

La pregunta de hoy: **¿cómo las combinamos?**

---

## Componer: la salida de una es la entrada de otra

Si `f` va de `A` a `B` y `g` va de `B` a `C`, entonces `g(f(a))` va de `A` a `C`.

```text
 Int ──porcentaje──▶ Int ──etiqueta──▶ String
 nota                porcentaje         "80%"
```

```kotlin
val texto = etiqueta(porcentaje(8)) // "80%"
```

Lo que hace posible componer son los **tipos**: la salida de un paso tiene que encajar con la entrada del siguiente.

> Componer es construir una función nueva a partir de otras, sin escribir lógica nueva.

---

## Componer en Kotlin

Kotlin no trae un operador de composición, pero se escribe en tres líneas:

```kotlin
infix fun <A, B, C> ((A) -> B).luego(g: (B) -> C): (A) -> C =
    { a -> g(this(a)) }
```

```kotlin
val notaAEtiqueta = ::porcentaje luego ::etiqueta

notaAEtiqueta(8) // "80%"
```

Más habitual en el día a día: `let` y las **cadenas de colecciones**:

```kotlin
val texto = 8.let(::porcentaje).let(::etiqueta)
```

La composición se lee **en el orden en que ocurren los pasos**.

---

## Los pipelines de colecciones ya son composición

Cada operación es una función de colección a colección:

```kotlin
fun nombresAprobados(lecturas: List<Lectura>): List<String> =
    lecturas
        .filter { it.nota >= 6 }
        .map { it.estudiante }
        .sorted()
```

- Cada paso tiene una responsabilidad.
- Se puede **probar un paso por separado** o el conjunto.
- Se puede **reordenar, agregar o quitar** un paso sin tocar los demás.

Esto sólo funciona bien si cada paso es **puro**: si uno imprime o lee un archivo, el pipeline deja de ser predecible.

---

## Cuando un paso puede fallar

No todo dato de entrada es válido. Modelamos esa posibilidad en el **tipo**, no con excepciones sorpresa:

```kotlin
fun parsear(linea: String): Lectura? { /* ... */ }
```

```kotlin
val lecturas: List<Lectura> =
    lineas.mapNotNull(::parsear) // descarta las líneas inválidas
```

- `T?` indica "puede no haber resultado".
- `mapNotNull` compone "transformar" y "descartar los `null`" en un solo paso.
- El caso de falla es **parte del contrato**, visible para quien llama.

---

<!-- _class: section -->
<!-- _paginate: false -->

# Cálculo y efectos

## Qué decide y qué toca el mundo

---

## Dos tipos de código

| | **Cálculo** (lógica pura) | **Efecto** |
|---|---|---|
| Depende de | Sus argumentos | Algo externo o el estado del mundo |
| Produce | Un valor de retorno | Un cambio fuera de la función |
| Mismo input, mismo resultado | **Siempre** | No necesariamente |
| Ejemplos | Calcular un promedio, validar, clasificar, formatear | Leer un archivo, `println`, HTTP, base de datos, reloj |
| Para probarlo | Argumentos y resultado | Archivos, red, mocks, configuración |

> El **cálculo decide** qué hay que hacer; el **efecto lo ejecuta**.

---

## Cómo reconocer un efecto

Señales de que una función **no** es pura:

- `File(...)`, `readLine()`, `println(...)`: entrada y salida.
- Llamadas HTTP, consultas SQL, colas de mensajes.
- `System.currentTimeMillis()`, `LocalDate.now()`, `Random`: dependen del momento.
- Modificar una `var` global o un objeto compartido.
- Lanzar una excepción que depende del entorno (archivo inexistente, sin red).

```kotlin
fun esHoyFeriado(): Boolean = LocalDate.now() in feriados // efecto: lee el reloj
fun esFeriado(dia: LocalDate): Boolean = dia in feriados  // cálculo
```

La segunda hace lo mismo, pero el efecto (**qué día es hoy**) se pasa **como argumento**.

---

## El problema: cálculo y efectos mezclados

```kotlin
fun reportarAprobados(ruta: String) {
    var aprobados = 0
    for (linea in File(ruta).readLines()) {          // efecto: leer archivo
        val partes = linea.split(",")
        val nota = partes[1].trim().toInt()          // cálculo (frágil)
        if (nota >= 6) {                             // cálculo: regla de negocio
            aprobados++
            println("${partes[0]} aprobó")           // efecto: escribir
        }
    }
    println("Total: $aprobados")                     // efecto: escribir
}
```

Funciona, pero **la regla de negocio no se puede probar sola**.

---

## Por qué cuesta tanto probarla

Para verificar que "con la nota 5 no aprueba" necesitamos:

1. Crear un archivo real (o un mock del sistema de archivos).
2. Ejecutar la función.
3. **Capturar** lo que se imprimió en consola.
4. Limpiar el archivo.

Y además:

- Una línea mal formada (`"Sofía"` sin nota) rompe con una excepción en la mitad del proceso.
- Si cambia el formato de salida, hay que tocar el código que lee.
- Una modificación en cualquier parte puede romper otra: **todo está acoplado**.

---

<!-- _class: section -->
<!-- _paginate: false -->

# Ejemplo en vivo

## Separar cálculo y efectos en 4 pasos

---

## El requisito

Dado un archivo `notas.csv`:

```text
Sofia, 8
Tomas, 4
Julia, 10
esto-no-es-una-nota
```

Queremos imprimir:

```text
Sofia aprobó
Julia aprobó
Total: 2
```

Reglas: aprueba con **6 o más**; las líneas inválidas se **ignoran**.

Vamos a resolverlo separando **qué se calcula** de **qué se hace con el mundo**.

---

## Paso 1 — Cálculo: interpretar una línea

```kotlin
data class Lectura(val estudiante: String, val nota: Int)

fun parsear(linea: String): Lectura? {
    val partes = linea.split(",").map { it.trim() }
    if (partes.size != 2) return null
    val nota = partes[1].toIntOrNull() ?: return null
    if (nota !in 0..10) return null
    return Lectura(partes[0], nota)
}
```

- Recibe un `String`, devuelve un valor: **no toca archivos ni consola**.
- La línea inválida no explota: devuelve `null`.
- Se prueba con una línea y un resultado esperado.

---

## Paso 2 — Cálculo: componer el reporte

```kotlin
fun aprobo(lectura: Lectura): Boolean = lectura.nota >= 6

fun armarReporte(lineas: List<String>): String {
    val aprobados = lineas
        .mapNotNull(::parsear)
        .filter(::aprobo)

    return (aprobados.map { "${it.estudiante} aprobó" } +
            "Total: ${aprobados.size}")
        .joinToString("\n")
}
```

- Entra una `List<String>`, sale un `String`.
- Es **composición**: `parsear` → `aprobo` → formato.
- Toda la regla de negocio está acá, sin `var` ni bucles.

---

## Paso 3 — Efectos: una cáscara fina

```kotlin
fun main(args: Array<String>) {
    val lineas = File(args[0]).readLines()   // efecto de entrada
    val reporte = armarReporte(lineas)       // cálculo
    println(reporte)                         // efecto de salida
}
```

El patrón es siempre el mismo:

1. **Leer** del mundo (efecto).
2. **Calcular** con funciones puras.
3. **Escribir** al mundo (efecto).

La capa de efectos **no decide nada**: sólo conecta el mundo con el cálculo.

---

## Paso 4 — Probar el cálculo, sin archivos ni consola

```kotlin
class ReporteTest {
    @Test
    fun `lista solo los aprobados y los cuenta`() {
        val reporte = armarReporte(listOf("Sofia, 8", "Tomas, 4"))

        assertEquals("Sofia aprobó\nTotal: 1", reporte)
    }

    @Test
    fun `ignora lineas invalidas`() {
        assertEquals("Total: 0", armarReporte(listOf("basura", "Ana, 11")))
    }
}
```

- Se ejecuta en **milisegundos**, sin preparar nada.
- Cada regla del requisito tiene su test.
- Si mañana la entrada viene de **red o base de datos**, `armarReporte` no cambia.

---

## Antes y después

| | **Mezclado** | **Separado** |
|---|---|---|
| Regla de negocio | Dentro de un bucle con I/O | Función pura `aprobo` |
| Líneas inválidas | Excepción en tiempo de ejecución | `parsear` devuelve `null` |
| Probar | Archivo real + capturar consola | `assertEquals` con un `String` |
| Cambiar la fuente de datos | Reescribir la función | Sólo cambia la capa de efectos |
| Cambiar la regla | Riesgo de romper la lectura/escritura | Sólo cambia una función pura |

Esta estructura se conoce como **núcleo funcional, cáscara imperativa** (*functional core, imperative shell*).

---

## El esquema general

```text
        ┌─────────────────────────────────────────┐
        │  CÁSCARA (efectos): archivos, red, BD   │
        │   ┌─────────────────────────────────┐   │
 mundo ─┼──▶│  NÚCLEO (cálculo puro)          │───┼──▶ mundo
        │   │  parsear → filtrar → formatear  │   │
        │   └─────────────────────────────────┘   │
        └─────────────────────────────────────────┘
```

- Los **datos** entran, se transforman y salen.
- Las dependencias apuntan **hacia adentro**: el núcleo no conoce la cáscara.
- Cuanto más grande el núcleo y más fina la cáscara, **más código testeable**.

---

<!-- _class: section -->
<!-- _paginate: false -->

# Trabajar con IA

## Cambiar código sin romper lo que ya funciona

---

## El riesgo de pedir cambios a una IA

Una IA puede producir un cambio que **compila y parece razonable** pero rompe otra cosa:

- Reescribe una función entera para un cambio de una línea.
- Toca el formato de salida mientras "mejora" la lectura del archivo.
- Agrega un `println` de depuración dentro de la lógica.
- Cambia un comportamiento que nadie le pidió cambiar.

Con el código mezclado, **la única forma de verificarlo es correr todo el sistema** y mirar a ojo.

> Con cualquier asistente, el límite no es lo que puede escribir, sino lo que **podemos verificar**.

---

## Lo que la separación nos da con IA

| Propiedad | Qué ganamos |
|---|---|
| **Contexto chico** | La IA recibe una función pura y su test: alcanza para entender el cambio |
| **Verificación automática** | Los tests del núcleo corren en segundos y detectan regresiones |
| **Cambios acotados** | Un cambio de regla toca **una función**, no la lectura ni la escritura |
| **Diff fácil de revisar** | Sin efectos escondidos, el diff se lee y se razona |
| **Red de seguridad** | Los tests existentes dicen si la IA rompió algo previo |

---

## Ejemplo: agregar un requisito

> Nuevo requisito: la nota mínima para aprobar **debe ser configurable**.

El cambio queda en el **núcleo**, con valor por defecto:

```kotlin
fun aprobo(lectura: Lectura, notaMinima: Int = 6): Boolean =
    lectura.nota >= notaMinima

fun armarReporte(lineas: List<String>, notaMinima: Int = 6): String {
    val aprobados = lineas.mapNotNull(::parsear).filter { aprobo(it, notaMinima) }
    // ... igual que antes
}
```

- Los tests **existentes** siguen pasando sin modificarse.
- Sumamos un test: con `notaMinima = 8`, `Sofia, 7` no aprueba.
- La cáscara (`main`) cambia como mucho **una línea** para leer el argumento.

---

## Cómo pedírselo a la IA

Un prompt que aprovecha la separación:

```text
En armarReporte y aprobo (archivo Reporte.kt), agregá una nota
mínima configurable con valor por defecto 6.

Restricciones:
- No modifiques parsear ni main.
- No agregues I/O (println, File) en las funciones puras.
- Los tests existentes deben pasar sin cambios.
- Agregá un test para notaMinima = 8.
```

Y después **verificá vos**: `./gradlew test` y revisá el diff.

---

## Checklist para revisar un cambio de la IA

- ¿Los **tests que ya existían** siguen pasando, **sin haberlos editado**?
- ¿El diff toca sólo lo pedido (función pura o cáscara, no ambas sin motivo)?
- ¿Apareció un `println`, `File`, `now()` o `Random` dentro del núcleo?
- ¿Hay un test nuevo que **falla sin el cambio** y pasa con él?
- ¿Se manejan los casos borde: lista vacía, entrada inválida?
- ¿Entendés cada línea que se agregó?

> Si el efecto se coló en el núcleo, pedile a la IA que lo **mueva a la cáscara** y pase el valor como argumento.

---

## Actividad (5 minutos): ¿cálculo o efecto?

En parejas, marquen cada línea y propongan cómo separar:

```kotlin
fun enviarAlertas(sensores: List<Sensor>) {
    val ahora = LocalDateTime.now()
    for (s in sensores) {
        if (s.temperatura > 40.0 && ahora.hour in 8..20) {
            println("ALERTA: ${s.id} a ${s.temperatura}°C")
            api.enviarSms(s.responsable, "Temperatura alta en ${s.id}")
        }
    }
}
```

Preguntas:

1. ¿Qué líneas son **efectos**? ¿Cuáles son **cálculo**?
2. ¿Cómo probarían la regla "temperatura alta en horario laboral"?
3. ¿Qué firma tendría la función pura?

---

## Una posible solución

```kotlin
data class Alerta(val sensorId: String, val responsable: String, val mensaje: String)

fun calcularAlertas(sensores: List<Sensor>, ahora: LocalDateTime): List<Alerta> =
    sensores
        .filter { it.temperatura > 40.0 && ahora.hour in 8..20 }
        .map { Alerta(it.id, it.responsable, "Temperatura alta en ${it.id}") }

fun enviarAlertas(sensores: List<Sensor>) {
    val alertas = calcularAlertas(sensores, LocalDateTime.now()) // efecto: reloj
    alertas.forEach { api.enviarSms(it.responsable, it.mensaje) } // efecto: red
}
```

- El **reloj** se vuelve un argumento: el test elige la hora.
- La función pura devuelve **datos que describen qué hacer**; la cáscara los ejecuta.
- Probamos `calcularAlertas` con sensores y una hora fija, sin SMS ni consola.

---

## Para llevarse

- **Componer** es combinar funciones cuyos tipos encajan; los pipelines de colecciones son composición.
- El **cálculo** depende sólo de sus argumentos; el **efecto** toca el mundo.
- Separar: **leer → calcular → escribir**, con la lógica en el medio y los efectos en los bordes.
- El núcleo puro se prueba con argumentos y resultados, **sin mocks ni archivos**.
- Con IA, la separación da **contexto chico, cambios acotados y verificación rápida**.
- Pedí cambios con restricciones claras y revisá siempre el diff con los tests existentes en verde.

> Lo que no podés verificar rápido, no lo podés cambiar con confianza.

---

## Para seguir aprendiendo

<!-- _class: compact -->

**Libros**

- [Grokking Simplicity — Eric Normand](https://www.manning.com/books/grokking-simplicity): separar *acciones*, *cálculos* y *datos*; el más cercano al tema de hoy.
- [Functional Programming in Kotlin — Vermeulen, Bjarnason y Chiusano](https://www.manning.com/books/functional-programming-in-kotlin): funciones puras, composición y manejo de efectos en Kotlin.
- [Unit Testing: Principles, Practices, and Patterns — Vladimir Khorikov](https://www.manning.com/books/unit-testing): arquitectura funcional y tests que no se rompen al refactorizar.
- [Domain Modeling Made Functional — Scott Wlaschin](https://pragprog.com/titles/swdddf/domain-modeling-made-functional/): modelar el dominio con tipos y *pipelines* de funciones.
- [Refactoring (2.ª ed.) — Martin Fowler](https://martinfowler.com/books/refactoring.html): cambiar código existente en pasos pequeños y seguros.
- [Effective Kotlin — Marcin Moskała](https://kt.academy/book/effectivekotlin): buenas prácticas de diseño en Kotlin.

**Para profundizar**

- [Functional Core, Imperative Shell — Gary Bernhardt](https://www.destroyallsoftware.com/screencasts/catalog/functional-core-imperative-shell) (screencast)
- [Kotlin: funciones de orden superior y lambdas](https://kotlinlang.org/docs/lambdas.html)
