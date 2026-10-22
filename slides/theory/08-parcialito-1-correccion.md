---
marp: true
theme: dcp
paginate: true
title: Parcialito 1 - Correccion
---

<!-- _class: cover -->
<!-- _paginate: false -->

# Parcialito 1

## Corrección en clase

**Teoría · DCP**

---

## Cómo se lee cada pregunta

- Cada pregunta tiene tres afirmaciones: **A**, **B** y **C**.
- Las opciones 1 a 3 son una sola afirmación; las opciones 4 a 6, una combinación de dos.
- La respuesta correcta es la opción que contiene **todas y solamente** las afirmaciones verdaderas.

> Si A y B son verdaderas, marcar solo "A" está mal: le falta B. Si solo A es verdadera, "A y B" también está mal: incluye una falsa.

Para cada pregunta vamos a ver: **qué afirmaciones son verdaderas, por qué, y cuál es la opción correcta**.

---

<!-- _class: compact -->

## 1 · Algoritmo, programa, responsabilidades y complejidad

- **A.** Un algoritmo es una secuencia finita, precisa y ordenada de pasos independiente del lenguaje; un programa es su expresión concreta en un lenguaje e incorpora decisiones sobre tipos, entrada/salida, errores, casos límite y recursos.
- **B.** Un diseño con alta cohesión y bajo acoplamiento busca que cada componente concentre tareas relacionadas, conozca lo mínimo necesario de los demás y mantenga las reglas cerca de los datos que necesitan.
- **C.** La complejidad esencial surge de las herramientas y decisiones técnicas elegidas (por ejemplo, variables globales o reglas duplicadas), mientras que la complejidad accidental proviene del dominio del problema en sí.

1 A · 2 B · 3 C · 4 A y B · 5 B y C · 6 A y C

---

<!-- _class: dense -->

## 1 · Respuesta: opción 4 (A y B)

| | | Por qué |
|---|---|---|
| **A** | ✅ | Un **algoritmo** es la estrategia abstracta; un **programa** es una solución situada que resuelve representación de datos, E/S, persistencia, errores, bordes y recursos. |
| **B** | ✅ | **Alta cohesión** = tareas relacionadas juntas; **bajo acoplamiento** = conocer lo mínimo de otros componentes y poner las reglas cerca de los datos. |
| **C** | ❌ | Están **invertidas**. La complejidad **esencial** viene del problema que hay que resolver; la **accidental** surge de nuestras herramientas, estructuras o decisiones (variables globales, duplicación, `null` sin control). |

**Trampa:** C usa los ejemplos exactos de la clase pero invierte las definiciones de Brooks.

> Clase 1 · *"El diseño no elimina la complejidad esencial: evita agregar complejidad innecesaria."*

---

<!-- _class: compact -->

## 2 · Planificar vs. construir con IA

- **A.** Al **planificar** con IA, la salida esperada son archivos de código que compilan; al **construir** con IA, la salida esperada son diagramas, alternativas y decisiones de diseño.
- **B.** Un buen prompt de planificación no pide escribir código todavía, sino pensar el problema, y se estructura en tres partes: **contexto**, **restricciones** y **formato de salida esperado**.
- **C.** Pedir código a ciegas sin modelar antes delega las decisiones de diseño en la IA: suele producir soluciones que parecen funcionar en el caso feliz, pero fallan en casos límite y son difíciles de explicar o modificar.

1 A · 2 B · 3 C · 4 A y B · 5 A y C · 6 B y C

---

<!-- _class: dense -->

## 2 · Respuesta: opción 6 (B y C)

| | | Por qué |
|---|---|---|
| **A** | ❌ | Están **invertidos**. Planificar produce **texto, diagramas y decisiones** (vos decidís, la IA propone); construir produce **archivos que compilan** (vos revisás, la IA ejecuta). |
| **B** | ✅ | Anatomía de un prompt de planificación: **contexto** (qué, para quién, stack), **restricciones** (alcance, sin frameworks pesados, testeable) y **formato de salida** (responsabilidades, alternativas, diagrama). |
| **C** | ✅ | Sin modelo previo, la IA decide por vos: funciona en la demo pero se rompe en los bordes y cada cambio descoloca otra parte. |

**Trampa:** A cruza las columnas de la tabla *"Planificar con IA / Construir con IA"*.

> Clase 2 · *"Primero cerrás **qué** vas a construir y **por qué**. Recién después pedís el **cómo**."*

---

<!-- _class: compact -->

## 3 · Agentes y subagentes

- **A.** A diferencia de un chat pasivo que solo responde texto, un agente trabaja en un bucle autónomo sobre un objetivo: lee archivos del repositorio, razona, ejecuta herramientas o comandos y vuelve a observar el resultado hasta completar la tarea.
- **B.** Un subagente comparte exactamente la misma ventana de contexto que el agente principal para que todas las búsquedas e intentos intermedios queden guardados en la conversación principal.
- **C.** El rol del subagente es tomar las decisiones globales de arquitectura y coordinar el flujo general, mientras que el agente principal se limita a resolver búsquedas acotadas y descartables.

1 A · 2 B · 3 C · 4 A y B · 5 B y C · 6 A y C

---

<!-- _class: dense -->

## 3 · Respuesta: opción 1 (A)

| | | Por qué |
|---|---|---|
| **A** | ✅ | Un **agente** tiene un objetivo, acceso a herramientas (leer/editar archivos, correr comandos, buscar) y trabaja en un bucle: *mira → decide → actúa → vuelve a mirar*. |
| **B** | ❌ | Es al revés: el **subagente** tiene un **contexto propio y descartable**, y devuelve solo su **conclusión** justamente para **no llenar de ruido** el contexto principal. |
| **C** | ❌ | Los roles están invertidos: el **agente principal** coordina la tarea grande y mantiene el hilo general; el **subagente** explora y resuelve una parte específica y acotada. |

**Trampa:** la opción 4 (A y B), para quien piensa que "compartir todo el contexto" siempre es mejor.

> Clase 2 · *"Subagente: contexto propio y descartable; devuelve solo su conclusión, sin llenar el contexto principal de ruido."*

---

<!-- _class: compact -->

## 4 · Qué es TDD y el ciclo Rojo → Verde → Refactor

- **A.** En TDD los tests actúan como un **contrato** o especificación ejecutable: definen qué significa que el código sea correcto antes de escribir la implementación, describiendo el *qué* y no el *cómo*.
- **B.** En el paso **Verde** se escribe el código mínimo y más simple que haga pasar el test, sin anticipar funcionalidad; recién en **Refactor** se mejora la estructura o legibilidad manteniendo los tests en verde.
- **C.** Si escribís un test nuevo y pasa de entrada sin tocar el código de producción, igual es válido como paso **Rojo** porque demuestra que el contrato ya se cumplía.

1 A · 2 B · 3 C · 4 A y C · 5 A y B · 6 B y C

---

<!-- _class: dense -->

## 4 · Respuesta: opción 5 (A y B)

| | | Por qué |
|---|---|---|
| **A** | ✅ | Un test es una **especificación ejecutable**: promete qué salida corresponde a cada entrada, no se desactualiza como un documento y define cuándo una funcionalidad está terminada. |
| **B** | ✅ | En **Verde** se hace lo mínimo para pasar; en **Refactor** se limpian nombres y duplicación sin cambiar el comportamiento y con toda la suite en verde. |
| **C** | ❌ | Regla de oro de TDD: *"Verificá que el test falle por la razón correcta (si nunca fue rojo, no prueba nada)."* Si pasa de entrada, no sirve como paso rojo para guiar código nuevo. |

**Trampa:** C suena tentadora porque "si pasa, mejor". Pero si nunca falló, ni siquiera sabés si el test está verificando lo que creés.

> Clase 2 · *"No escribas código de producción sin un test rojo que lo pida."*

---

<!-- _class: compact -->

## 5 · Generación de tests con LLMs

- **A.** Si le pedís a un LLM *"escribí los tests de esta función"* sobre código ya implementado, el modelo suele testear lo que el código **hace** en lugar de lo que **debería hacer**, llegando a consagrar bugs existentes como comportamiento esperado.
- **B.** Si la suite de tests alcanza el 100 % de cobertura de líneas y pasa en verde, queda garantizado que el contrato del dominio y los casos borde e inválidos están bien verificados.
- **C.** Los valores esperados de un test siempre pueden obtenerse ejecutando primero la función y copiando su salida en el `assertEquals`, ya que eso asegura que el test sea consistente con el programa.

1 A · 2 B · 3 C · 4 A y B · 5 B y C · 6 A y C

---

<!-- _class: dense -->

## 5 · Respuesta: opción 1 (A)

| | | Por qué |
|---|---|---|
| **A** | ✅ | El LLM no conoce tu intención si solo ve el código: copia la lógica actual en los tests y **consagra el bug**. Por eso primero van el contrato y tus casos (felices, borde, inválidos). |
| **B** | ❌ | *"Cobertura alta no es lo mismo que tests buenos: importa qué se verifica."* Podés ejecutar todas las líneas con un caso feliz sin probar un solo borde ni caso inválido. |
| **C** | ❌ | Es el mismo error que A hecho a mano: los valores esperados deben salir **del contrato**, nunca de "lo que da el código". Si copiás la salida de una función rota, el test pasa y oculta el defecto. |

**Trampas:** la opción 4 (A y B) por confundir cobertura con calidad de contrato, y la 6 (A y C) por no ver que C repite el vicio de A.

> Clase 2 · *"El criterio sobre qué es correcto es tuyo. La IA ayuda a escribir y ampliar, no a decidir."*

---

<!-- _class: compact -->

## 6 · Cómo funcionan los LLMs y sus debilidades al generar código

- **A.** Como un LLM predice texto plausible sin ejecutar el programa, cuando inventa una función o librería inexistente (alucinación de API) el tono de su respuesta se vuelve dubitativo y advierte que no está seguro.
- **B.** Tres debilidades típicas del código generado por IA son la **alucinación de APIs** (métodos o paquetes inexistentes u obsoletos), la **sobre-ingeniería** (capas y patrones innecesarios para un problema simple) y el código que **"compila pero no sirve"** (falla en bordes, traga errores o ignora reglas del dominio).
- **C.** El compilador verifica sintaxis y tipos, pero no que el programa cumpla la especificación; por eso los errores lógicos son silenciosos y requieren tests (especialmente en casos borde e inválidos) para detectarse.

1 A · 2 B · 3 C · 4 B y C · 5 A y B · 6 A y C

---

<!-- _class: dense -->

## 6 · Respuesta: opción 4 (B y C)

| | | Por qué |
|---|---|---|
| **A** | ❌ | Al contrario: el modelo *"suena igual de seguro cuando acierta que cuando se equivoca"*. Plausible ≠ correcto; la confianza del tono no es evidencia. |
| **B** | ✅ | Son exactamente las tres fallas típicas vistas en la Clase 3: APIs inventadas (`toIntOrZero()`), sobre-ingeniería (`Strategy` + `Factory` para sumar) y código que compila pero falla en la lógica. |
| **C** | ✅ | Los errores de compilación y de ejecución avisan; el **error lógico** no tira el programa abajo, da un resultado incorrecto en silencio y solo lo atrapan los tests. |

**Trampa:** A arranca con una premisa verdadera (*"predice texto plausible sin ejecutar"*) para hacer pasar una conclusión falsa.

> Clase 3 · *"Plausible ≠ correcto. La confianza en el tono no es evidencia."*

---

<!-- _class: compact -->

## 7 · Depuración y lectura de errores

```kotlin
fun promedio(notas: List<Int>): Double {
    try {
        return (notas.sum() / notas.size).toDouble()
    } catch (e: Exception) {
        return 0.0
    }
}
```

- **A.** En `promedio`, `notas.sum() / notas.size` hace **división entera** (para `listOf(7, 8)` da `7.0` en vez de `7.5`) y el `catch` **traga el error** de lista vacía devolviendo `0.0`, un valor engañoso que oculta la falla.
- **B.** En un stack trace con excepciones encadenadas, la excepción de más arriba es la causa raíz y el último `Caused by` del final es solo el síntoma superficial.
- **C.** Al reportar un bug a la IA, conviene pasarle el error o stack trace completo junto con el valor esperado y el obtenido, y pedirle que explique la causa antes de proponer el arreglo mínimo.

1 A · 2 B · 3 C · 4 A y B · 5 B y C · 6 A y C

---

<!-- _class: dense -->

## 7 · Respuesta: opción 6 (A y C)

| | | Por qué |
|---|---|---|
| **A** | ✅ | `15 / 2` entre `Int` trunca a `7` antes de `.toDouble()` (`7.0`). Y con `emptyList()` el `catch (e: Exception)` oculta la `ArithmeticException` devolviendo `0.0` como si fuera un promedio real. |
| **B** | ❌ | Es al revés: la excepción de arriba es el **síntoma**; el **último `Caused by`** del final es la **causa raíz** (por ejemplo, `FileNotFoundException`). |
| **C** | ✅ | Un buen prompt de depuración incluye evidencia completa (código, caso que falla, resultado actual, stack trace) y pide diagnosticar la **causa** antes de tocar código. |

**Trampa:** la opción 4 (A y B), por leer el `Caused by` al revés.

> Clase 3 · *"La excepción de arriba es un síntoma; el `Caused by` del final es la raíz."*

---

<!-- _class: compact -->

## 8 · Modelado de datos: `data class`, `sealed class` e invariantes

- **A.** Una `sealed class` define una jerarquía abierta a la que cualquier módulo externo puede agregar subtipos libremente, por lo que un `when` sobre ella siempre obliga a escribir una rama `else`.
- **B.** Proteger las **invariantes** en la construcción (por ejemplo, con `require` dentro de `init`) evita que circulen objetos inválidos por el programa, y `copy` también respeta esas invariantes porque vuelve a construir el objeto.
- **C.** En una `data class`, Kotlin genera `equals`, `hashCode`, `toString`, `copy` y `componentN` tomando únicamente las propiedades declaradas en el **constructor primario**.

1 A · 2 B · 3 C · 4 A y B · 5 B y C · 6 A y C

---

<!-- _class: dense -->

## 8 · Respuesta: opción 5 (B y C)

| | | Por qué |
|---|---|---|
| **A** | ❌ | Una `sealed class` es una **jerarquía cerrada**: todas las variantes son conocidas al compilar, por lo que el compilador verifica que el `when` sea exhaustivo **sin** necesitar un `else` genérico. |
| **B** | ✅ | Una invariante es una condición que todo objeto válido debe cumplir. Validar con `require` en `init` rechaza entradas inválidas con `IllegalArgumentException`, incluso al usar `copy(...)`. |
| **C** | ✅ | Las operaciones estructurales generadas por `data class` usan solo las propiedades del constructor principal; las propiedades declaradas en el cuerpo quedan afuera. |

**Trampa:** A describe el problema de una jerarquía abierta común, justamente lo que `sealed class` viene a resolver.

> Clase 4 · *"Al ser una jerarquía cerrada, el compilador puede verificar que el `when` cubra todos los casos."*

---

<!-- _class: compact -->

## 9 · Programación funcional: funciones puras e inmutabilidad

- **A.** Una función que calcula si un alumno aprueba leyendo la nota mínima desde un archivo de configuración o consultando la fecha actual es **pura**, siempre que no modifique ese archivo ni imprima en consola.
- **B.** Una **función pura** siempre devuelve el mismo resultado para los mismos argumentos y no produce efectos observables fuera de ella (no muta estado externo, no lee ni escribe archivos, red o consola).
- **C.** Declarar `val xs = mutableListOf(1, 2)` garantiza **inmutabilidad profunda**: como se usa `val`, no es posible agregar ni quitar elementos de la lista.

1 A · 2 B · 3 C · 4 A y B · 5 B y C · 6 A y C

---

<!-- _class: dense -->

## 9 · Respuesta: opción 2 (B)

| | | Por qué |
|---|---|---|
| **A** | ❌ | No alcanza con "no modificar": si lee un archivo o el reloj, con el **mismo argumento** puede dar **resultados distintos** según el entorno. Viola la primera propiedad de pureza (es exactamente el contraejemplo `aprobarHoy` de la Clase 4). |
| **B** | ✅ | Definición de función pura: **1)** mismos argumentos → mismo resultado; **2)** sin efectos observables afuera. Eso permite probarla solo con argumentos y resultados. |
| **C** | ❌ | `val` solo impide **reasignar la referencia** `xs`, pero la lista referenciada es mutable (`xs.add(3)` es válido y cambia su contenido). Tampoco `List<T>` congela una `MutableList` subyacente: es solo una interfaz de solo lectura. |

**Trampas:** la opción 4 (A y B), por creer que "puro = no escribe", y la 5 (B y C), por confundir `val` con inmutabilidad del objeto.

> Clase 4 · *"La lógica pura se prueba con argumentos y resultados, sin archivos, red ni configuración."*

---

<!-- _class: compact -->

## 10 · Transformación de colecciones: `map`, `filter`, `fold` y `reduce`

```kotlin
val numeros = listOf(1, 2, 3, 4)
val resultado = numeros.filter { it % 2 == 0 }.map { it * 10 }
```

- **A.** `filter` conserva los elementos que cumplen el predicado sin transformarlos (puede cambiar la cantidad de elementos), mientras que `map` aplica una función a cada elemento conservando la cantidad; ninguna de las dos modifica la lista original `numeros`, y `resultado` vale `listOf(20, 40)`.
- **B.** `fold` y `reduce` se comportan igual ante una colección vacía: ambos devuelven `0` (o `null`) sin lanzar excepción.
- **C.** `fold` recibe un valor inicial que define el resultado cuando la colección está vacía y permite que el acumulador sea de otro tipo; `reduce` no recibe valor inicial, usa el primer elemento y lanza `UnsupportedOperationException` si la colección está vacía.

1 A · 2 B · 3 C · 4 A y B · 5 B y C · 6 A y C

---

<!-- _class: dense -->

## 10 · Respuesta: opción 6 (A y C)

| | | Por qué |
|---|---|---|
| **A** | ✅ | `filter` selecciona `[2, 4]`; `map` transforma cada uno en `[20, 40]` manteniendo la cantidad de elementos que recibió. Ambas devuelven listas nuevas sin mutar `numeros`. |
| **B** | ❌ | Sobre una colección vacía, `reduce` **no tiene primer elemento** para usar como acumulador y lanza `UnsupportedOperationException`. |
| **C** | ✅ | `fold(inicial)` arranca desde `inicial` (que además es el resultado si la lista está vacía y puede ser de cualquier tipo); `reduce` exige al menos un elemento por contrato. |

**Trampa:** la opción 1 (solo A), si no recuerdan la diferencia exacta entre `fold` y `reduce` en el caso borde de lista vacía.

> Clase 4 · *"Si no podés garantizar que haya elementos, no uses `reduce` despreocupadamente."*

---

<!-- _class: compact -->

## Clave de respuestas

| Pregunta | Tema | Opción | Afirmaciones verdaderas |
|---|---|---|---|
| 1 | Algoritmo, programa, responsabilidades y complejidad | **4** | A y B |
| 2 | Planificar vs. construir con IA | **6** | B y C |
| 3 | Agentes y subagentes | **1** | A |
| 4 | Qué es TDD y el ciclo Rojo → Verde → Refactor | **5** | A y B |
| 5 | Generación de tests con LLMs | **1** | A |
| 6 | Cómo funcionan los LLMs y sus debilidades | **4** | B y C |
| 7 | Depuración y lectura de errores | **6** | A y C |
| 8 | `data class`, `sealed class` e invariantes | **5** | B y C |
| 9 | Funciones puras e inmutabilidad | **2** | B |
| 10 | Colecciones: `map`, `filter`, `fold` y `reduce` | **6** | A y C |
