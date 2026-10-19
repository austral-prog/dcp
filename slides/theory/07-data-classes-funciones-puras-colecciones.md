---
marp: true
theme: dcp
paginate: true
title: Clase 4 - Data classes, funciones puras y transformación de colecciones
---

<!-- _class: cover -->
<!-- _paginate: false -->

# Modelar y transformar datos

## Data classes, funciones puras y colecciones en Kotlin

**Clase teorica 4 · DCP**

---

## Objetivo de la clase

Un buen modelo hace que los datos validos sean faciles de representar y los errores, dificiles de introducir.

Hoy vamos a:

- Modelar datos y reglas del dominio con **tipos**.
- Representar alternativas con **sealed classes**.
- Proteger **invariantes** desde la construccion.
- Escribir funciones **puras** y trabajar con **inmutabilidad**.
- Transformar colecciones con `map`, `filter`, `fold` y `reduce`.

> El objetivo no es usar mas clases: es expresar mejor lo que el programa sabe.

---

## Recorrido de esta clase

1. Modelar el dominio: `data class`, `sealed class` e invariantes.
2. Funciones puras e inmutabilidad.
3. Transformaciones de colecciones: `map`, `filter`, `fold` y `reduce`.
4. Componer transformaciones y revisar sus casos borde.

---

<!-- _class: section -->
<!-- _paginate: false -->

# Modelar con tipos

## Que valores permite representar nuestro programa

---

## De datos sueltos a un concepto del dominio

Una lectura de una materia podria aparecer como dos valores sin relacion:

```kotlin
val estudiante = "Sofia"
val nota = 8
```

Una **data class** los agrupa bajo un concepto con nombre:

```kotlin
data class Lectura(
    val estudiante: String,
    val nota: Int
)
```

El tipo documenta que significan los datos y permite pasarlos juntos entre funciones.

---

## Que aporta una `data class`

```kotlin
val original = Lectura("Sofia", 8)
val otra = original.copy(nota = 9)
```

Kotlin genera operaciones utiles a partir de las propiedades del constructor principal:

- `equals` y `hashCode`: comparar por contenido.
- `toString`: mostrar el contenido.
- `copy`: crear una copia con algunos valores cambiados.
- `componentN`: destructurar los valores.

```kotlin
val (nombre, calificacion) = original
```

Elegila para **representar datos**; no es una razon para crear una clase por cada variable.

---

## Modelar alternativas con `sealed class`

Un resultado no siempre es un dato exitoso. Puede ser un exito o un error:

```kotlin
sealed class Resultado

data class Exito(val lecturas: List<Lectura>) : Resultado()
data class ErrorDeCarga(val mensaje: String) : Resultado()
```

Quien consume el resultado tiene que contemplar las alternativas:

```kotlin
fun describir(resultado: Resultado): String =
    when (resultado) {
        is Exito -> "Se cargaron ${resultado.lecturas.size} lecturas"
        is ErrorDeCarga -> "Error: ${resultado.mensaje}"
    }
```

Al ser una jerarquia cerrada, el compilador puede verificar que el `when` cubra todos los casos.

---

## Alternativas explicitas, no valores magicos

Un `Boolean` o un `null` aislado suele ocultar que significa cada caso:

```kotlin
fun cargar(): List<Lectura>? // ¿null significa archivo ausente, vacio o invalido?
```

Un tipo con alternativas con nombre comunica mejor el contrato:

```kotlin
sealed class EstadoCarga
data class Cargada(val lecturas: List<Lectura>) : EstadoCarga()
data class ArchivoInvalido(val motivo: String) : EstadoCarga()
object ArchivoAusente : EstadoCarga()
```

- Cada variante representa un caso real del dominio.
- Los datos asociados van en la variante que los necesita.
- No agregues variantes que el sistema no distingue realmente.

---

## Invariantes: reglas que siempre deben cumplirse

Una **invariante** es una condicion que debe ser cierta para todo objeto valido.

Para una lectura, acordamos que la nota esta entre 0 y 10:

```kotlin
data class Lectura(
    val estudiante: String,
    val nota: Int
) {
    init {
        require(estudiante.isNotBlank()) { "El estudiante es obligatorio" }
        require(nota in 0..10) { "La nota debe estar entre 0 y 10" }
    }
}
```

`require` rechaza una entrada invalida con `IllegalArgumentException`.

> Validar al construir evita que objetos invalidos circulen por el resto del programa.

---

## Invariantes y limites del modelo

- Elegi el tipo mas preciso posible: `Int` para una nota entera, no `String`.
- Valida las reglas del dominio en el punto de construccion.
- `copy` vuelve a construir el objeto y ejecuta su inicializacion: tambien respeta el `require`.
- No confundas una precondicion del modelo con una regla que cambia segun la operacion.

```kotlin
val lectura = Lectura("Sofia", 8)
lectura.copy(nota = 12) // falla: la copia viola la invariante
```

Un tipo ayuda a hacer visibles las reglas; no reemplaza decidir cuales son correctas.

---

<!-- _class: section -->
<!-- _paginate: false -->

# Funciones puras e inmutabilidad

## Hacer que la logica sea predecible

---

## Una funcion pura

Una funcion pura cumple dos propiedades:

1. Con los mismos argumentos, siempre devuelve el mismo resultado.
2. No produce efectos observables fuera de ella.

```kotlin
fun aprobar(nota: Int): Boolean = nota >= 6
```

No lee la hora, no consulta un archivo, no modifica una variable global ni imprime.

```kotlin
fun aprobarHoy(nota: Int): Boolean =
    nota >= leerNotaMinimaDeArchivo()
```

La segunda depende de algo externo: con el mismo argumento puede dar resultados distintos.

---

## Efectos afuera, transformaciones adentro

Separar entrada/salida de la logica facilita entender y probar:

```kotlin
fun promedio(notas: List<Int>): Double? =
    notas.takeIf { it.isNotEmpty() }?.let { it.average() }
```

Esta funcion transforma datos y no hace entrada/salida. La capa que la llama puede:

1. Leer las notas del archivo o de la consola.
2. Llamar a `promedio`.
3. Mostrar el resultado o informar que no hay notas.

> La logica pura se prueba con argumentos y resultados, sin archivos, red ni configuracion.

---

## Inmutabilidad: crear un valor nuevo

Preferi `val` para referencias que no necesitan reasignacion:

```kotlin
val notas = listOf(7, 8, 10)
val nuevasNotas = notas + 9
```

`notas` sigue apuntando a la lista original; `+` produce otra lista.

```kotlin
var total = 0
for (nota in notas) {
    total += nota
}
```

El bucle cambia `total`. No siempre esta mal, pero introduce estado que hay que seguir.

> Inmutabilidad reduce los cambios invisibles y hace mas facil razonar sobre el flujo.

---

## `val` no significa inmutabilidad profunda

`val` impide reasignar una referencia, pero no vuelve inmutable al objeto referenciado:

```kotlin
val nombres = mutableListOf("Sofia")
nombres.add("Tomas") // permitido: se modifica la lista
```

Ademas, `List<T>` es una **interfaz de solo lectura**, no una garantia de que nadie pueda modificar la lista original:

```kotlin
val mutable = mutableListOf("Sofia")
val soloLectura: List<String> = mutable
mutable.add("Tomas")
// soloLectura ahora tambien contiene "Tomas"
```

Usa colecciones de solo lectura en las interfaces y evita compartir referencias mutables sin necesidad.

---

<!-- _class: section -->
<!-- _paginate: false -->

# Transformar colecciones

## Expresar el que, no administrar cada paso

---

## `map`: transformar cada elemento

`map` aplica una funcion a cada elemento y devuelve una lista con los resultados:

```kotlin
val notas = listOf(7, 8, 10)
val porcentajes = notas.map { nota -> nota * 10 }
// [70, 80, 100]
```

- Mantiene el orden y la cantidad de elementos.
- No cambia la coleccion de entrada.
- El tipo de salida puede ser distinto del tipo de entrada.

```kotlin
val estudiantes = lecturas.map { it.estudiante }
```

---

## `filter`: conservar lo que cumple una condicion

`filter` recibe una condicion y devuelve los elementos para los que da `true`:

```kotlin
val aprobadas = lecturas.filter { lectura -> lectura.nota >= 6 }
```

- Mantiene los elementos originales que cumplen el predicado.
- Mantiene el orden relativo.
- Puede devolver una lista vacia.

`filter` no transforma los elementos; para eso podes encadenarlo con `map`:

```kotlin
val nombresAprobados = lecturas
    .filter { it.nota >= 6 }
    .map { it.estudiante }
```

---

## `fold`: resumir desde un valor inicial

`fold` recorre los elementos y actualiza un acumulador. El valor inicial define el resultado para una lista vacia:

```kotlin
val total = listOf(7, 8, 10).fold(0) { acumulado, nota ->
    acumulado + nota
}
// 25
```

```kotlin
emptyList<Int>().fold(0) { acumulado, nota -> acumulado + nota } // 0
```

El acumulador puede ser de otro tipo; por ejemplo, construir un texto o agrupar datos.

---

## `reduce`: resumir sin valor inicial

`reduce` usa el primer elemento como acumulador inicial:

```kotlin
val total = listOf(7, 8, 10).reduce { acumulado, nota ->
    acumulado + nota
}
// 25
```

**Diferencia importante:** `reduce` no puede operar sobre una lista vacia y lanza `UnsupportedOperationException`.

| Elegi... | Cuando... |
|---|---|
| `fold(inicial)` | Hay un valor inicial natural o la coleccion puede estar vacia |
| `reduce` | La coleccion tiene al menos un elemento, garantizado por el contrato |

Si no podes garantizar que haya elementos, no uses `reduce` despreocupadamente.

---

## Un pipeline completo

Requisito: obtener la suma de las notas aprobadas de una lista de lecturas:

```kotlin
fun totalAprobadas(lecturas: List<Lectura>): Int =
    lecturas
        .filter { it.nota >= 6 }
        .map { it.nota }
        .fold(0) { total, nota -> total + nota }
```

1. `filter`: conserva las lecturas aprobadas.
2. `map`: extrae la nota de cada lectura.
3. `fold`: suma las notas; si no hay aprobadas, devuelve `0`.

Cada paso tiene una responsabilidad y se puede leer de arriba hacia abajo.

---

## Componer sin ocultar el costo

Encadenar transformaciones mejora la expresividad, pero cada `map` o `filter` sobre una `List` crea una lista intermedia.

Para colecciones chicas y medianas, prioriza que la intencion se entienda:

```kotlin
val nombres = lecturas
    .filter { it.nota >= 6 }
    .map { it.estudiante }
```

Para grandes volumenes, Kotlin ofrece `Sequence` para evaluar operaciones de manera diferida:

```kotlin
val nombres = lecturas.asSequence()
    .filter { it.nota >= 6 }
    .map { it.estudiante }
    .toList()
```

No optimices por intuicion: primero identifica una necesidad y mide.

---

## Casos borde para revisar

Antes de aceptar una transformacion, pregunta:

- ¿Que devuelve con una lista vacia?
- ¿`reduce` podria recibir una lista vacia?
- ¿La operacion conserva o cambia el orden?
- ¿El resultado puede ser de otro tipo o quedar vacio?
- ¿Hay division por cero al calcular un promedio?
- ¿La funcion del `map` o `fold` tiene efectos laterales?

Un promedio necesita definir que hacer cuando no hay datos:

```kotlin
fun promedio(notas: List<Int>): Double? =
    notas.takeIf { it.isNotEmpty() }?.average()
```

`null` expresa que no existe promedio; no inventa un cero que podria confundirse con una nota real.

---

## Para llevarse

- `data class` modela datos; `sealed class` representa alternativas conocidas.
- Las **invariantes** protegen las reglas al construir los valores.
- Una funcion pura da el mismo resultado para los mismos argumentos y no produce efectos externos.
- `val` y `List` ayudan a limitar cambios, pero no garantizan inmutabilidad profunda.
- `map` transforma; `filter` selecciona; `fold` y `reduce` resumen.
- `fold` define un caso vacio con su valor inicial; `reduce` requiere elementos.

> Modelar bien hace que la logica sea mas clara, testeable y dificil de romper.

---

<!-- _class: section -->
<!-- _paginate: false -->

# Para seguir aprendiendo

## Libros y documentacion oficial

---

## Fuentes y lecturas complementarias

**Libros**

- [Kotlin in Action, 2nd Edition — Roman Elizarov, Svetlana Isakova, Sebastian Aigner y Dmitry Jemerov](https://www.manning.com/books/kotlin-in-action-second-edition) — Kotlin moderno, tipos y colecciones.
- [Functional Programming in Kotlin — Marco Vermeulen, Rúnar Bjarnason y Paul Chiusano](https://www.manning.com/books/functional-programming-in-kotlin) — funciones puras, inmutabilidad y composicion.
- [Effective Kotlin — Marcin Moskała](https://kt.academy/book/effectivekotlin) — practicas y decisiones de diseño en Kotlin.

**Documentacion oficial**

- [Data classes](https://kotlinlang.org/docs/data-classes.html)
- [Sealed classes and interfaces](https://kotlinlang.org/docs/sealed-classes.html)
- [Collections overview](https://kotlinlang.org/docs/collections-overview.html)
- [Collection operations](https://kotlinlang.org/docs/collection-operations.html)

Las versiones de los libros pueden variar; revisa los capitulos sobre clases, funciones, colecciones y programacion funcional.
