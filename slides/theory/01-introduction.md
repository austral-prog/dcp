---
marp: true
theme: dcp
paginate: true
title: Clase 1 - De algoritmos a programas: introduccion a Kotlin
---

<!-- _class: cover -->
<!-- _paginate: false -->

# De algoritmos a programas

## Presentacion de la materia e introduccion a Kotlin

**Clase teorica 1 · DCP**

---

## Nuestro punto de partida

Programar no es solamente escribir instrucciones que la computadora pueda ejecutar.

Durante la materia aprenderemos a:

- modelar un problema antes de implementar una solucion;
- distribuir datos y comportamiento de manera responsable;
- controlar estado, dependencias y errores;
- producir codigo que otras personas puedan leer, probar y cambiar.

> El objetivo no es solo que el programa funcione hoy, sino que siga siendo comprensible mañana.

---

## Recorrido de esta primera clase

1. Acuerdos de cursada y uso responsable de IA.
2. Diferencia entre algoritmo y programa.
3. Datos, comportamiento, responsabilidades y estado.
4. Complejidad accidental y limites del enfoque imperativo puro.
5. Primer contraste entre Java y Kotlin.

La progresion sera **problema → modelo → diseño → lenguaje**.

---

## Acuerdos de trabajo

- Las entregas deben ser propias, reproducibles y explicables.
- Toda fuente externa utilizada debe citarse.
- Usar una herramienta no reemplaza comprender ni validar el resultado.
- El codigo entregado debe poder defenderse: decisiones, limites y pruebas.
- Las consignas de cada actividad prevalecen ante cualquier duda.

---

## IA: asistencia, no autoria invisible

La IA generativa puede servir para explorar alternativas, explicar conceptos, revisar codigo o proponer casos de prueba.

Su uso exige:

- declarar **que herramienta** se utilizo y **para que**;
- conservar los prompts e interacciones relevantes en el **Prompt Log**;
- contrastar las respuestas con documentacion, ejecuciones y pruebas;
- identificar que partes fueron adaptadas o descartadas;
- asumir responsabilidad por todo lo presentado.

---

## ¿Que debe registrar el Prompt Log?

Un registro util permite reconstruir el proceso, no solo mostrar el resultado.

| Momento | Evidencia minima |
|---|---|
| Intencion | problema o tarea que se intento resolver |
| Consulta | prompt enviado y contexto relevante |
| Respuesta | salida obtenida o enlace a la conversacion |
| Evaluacion | errores, sesgos, omisiones y verificaciones |
| Decision | que se uso, modifico o descarto y por que |

El Prompt Log es parte de la **trazabilidad del aprendizaje**.

---

<!-- _class: section -->
<!-- _paginate: false -->

# Del problema a la ejecucion

## Algoritmo y programa no son sinonimos

---

## Algoritmo: una estrategia abstracta

> **Un algoritmo es una secuencia finita, precisa y ordenada de pasos que, a partir de ciertas entradas, produce una salida para resolver una clase de problemas.**

Debe poder discutirse sin depender necesariamente de Kotlin, Java o una computadora particular.

Ejemplo: buscar el menor precio.

1. Tomar el primer precio como minimo provisional.
2. Compararlo con cada precio restante.
3. Reemplazar el minimo cuando aparezca uno menor.
4. Informar el minimo encontrado.

---

## Programa: una solucion situada

> **Un programa es la expresion concreta de uno o mas algoritmos y reglas de negocio en un lenguaje de programacion, preparada para ejecutarse dentro de un entorno determinado.**

Ademas de la estrategia, debe resolver decisiones reales:

- tipos y representacion de los datos;
- entrada, salida y persistencia;
- errores, valores ausentes y casos limite;
- uso de memoria, tiempo y otros recursos;
- integracion con usuarios, archivos, redes o servicios.

---

<!-- _class: compact -->

## Un mismo algoritmo, programas diferentes

**Problema:** calcular el promedio de calificaciones.

El algoritmo puede ser el mismo:

```text
si no hay calificaciones, informar que no existe promedio
sumar todas las calificaciones
dividir la suma por la cantidad
```

Pero un programa debe decidir:

- ¿entero o decimal?, ¿que precision?
- ¿lista en memoria, archivo o base de datos?
- ¿que ocurre con datos invalidos o ausentes?
- ¿como se comunica el resultado?

La implementacion agrega **contexto y responsabilidades**.

---

## Datos y comportamiento

Los **datos** representan el estado relevante del dominio. El **comportamiento** expresa operaciones y reglas que consultan o modifican ese estado.

```kotlin
data class Cuenta(val titular: String, private var saldo: Double) {
    fun extraer(monto: Double) {
        require(monto > 0 && monto <= saldo)
        saldo -= monto
    }
}
```

La cuenta no es solo dos valores: tambien protege la regla que determina cuando una extraccion es valida.

**Pregunta de diseño:** ¿que objeto posee la informacion necesaria para garantizar cada regla?

---

## Responsabilidades: quien sabe y quien hace

> **Una responsabilidad es una obligacion de un componente: conocer cierta informacion, realizar una tarea o coordinar a otros componentes.**

Una distribucion clara busca:

- **alta cohesion:** cada componente concentra tareas relacionadas;
- **bajo acoplamiento:** conoce lo minimo necesario de otros componentes;
- reglas cerca de los datos que necesitan;
- coordinadores que coordinan, sin absorber toda la logica.

Si una clase valida usuarios, calcula precios, guarda archivos y envia correos, tiene demasiados motivos para cambiar.

---

<!-- _class: compact -->

## Estado: memoria del programa

> **El estado es el conjunto de valores observables que describen a un sistema u objeto en un instante determinado.**

Una operacion puede:

- consultar el estado sin modificarlo;
- producir un nuevo valor;
- realizar una transicion desde un estado valido hacia otro.

```text
Pedido BORRADOR --confirmar()--> CONFIRMADO
Pedido CONFIRMADO --enviar()----> ENVIADO
```

No toda transicion debe permitirse: las reglas preservan los **invariantes**, condiciones que deben mantenerse siempre verdaderas.

---

<!-- _class: dense -->

## Mutabilidad: el costo de cambiar en el lugar

```kotlin
var total = 0
for (precio in precios) {
    total += precio
}
```

Para comprender el resultado hay que seguir cada cambio de `total` en orden.

```kotlin
val total = precios.sum()
```

La segunda version expresa **que** se obtiene y reduce el estado mutable visible.

- `var`: la referencia puede cambiar.
- `val`: la referencia se asigna una sola vez.

Preferir inmutabilidad disminuye estados posibles y facilita razonar, probar y ejecutar en concurrencia.

---

## Complejidad esencial y accidental

> **La complejidad esencial proviene del problema que debemos resolver; la complejidad accidental surge de las herramientas, estructuras o decisiones usadas para construir la solucion.**

Ejemplo: calcular una tarifa con descuentos puede ser esencialmente complejo.

Son dificultades accidentales:

- variables globales que pueden cambiar desde cualquier lugar;
- duplicacion de reglas en varios modulos;
- `null` sin tratamiento explicito;
- detalles de infraestructura mezclados con logica de negocio;
- secuencias de pasos cuyo orden correcto solo conoce su autor.

El diseño no elimina la complejidad esencial: evita agregar complejidad innecesaria.

---

## Limites del enfoque imperativo puro

El estilo imperativo describe **como** alcanzar el resultado mediante comandos y cambios de estado.

Es adecuado para muchos algoritmos, pero llevado a todo el sistema puede generar:

- estado compartido dificil de rastrear;
- dependencia excesiva del orden de ejecucion;
- funciones extensas con responsabilidades mezcladas;
- efectos secundarios ocultos;
- pruebas que requieren preparar demasiado contexto.

No se trata de eliminar instrucciones, sino de combinarlas con abstracciones que expresen **intencion, dominio y contratos**.

---

## De instrucciones a modelos

Ante un problema, podemos preguntar:

| Mirada centrada en pasos | Mirada centrada en el modelo |
|---|---|
| ¿que instruccion sigue? | ¿que conceptos existen? |
| ¿que variable modifico? | ¿quien es responsable? |
| ¿en que orden llamo funciones? | ¿que transiciones son validas? |
| ¿como proceso todos los casos? | ¿que invariantes deben cumplirse? |

La orientacion a objetos aporta una forma de organizar **estado y comportamiento** alrededor de entidades con responsabilidades claras.

---

<!-- _class: section -->
<!-- _paginate: false -->

# Kotlin y Java

## Mismo ecosistema, distinta expresividad

---

## ¿Por que compararlos?

Kotlin y Java:

- son lenguajes de tipado estatico;
- compilan para la JVM y comparten su ecosistema;
- pueden interoperar dentro del mismo proyecto;
- permiten programacion orientada a objetos;
- cuentan con recoleccion automatica de memoria.

Kotlin busca conservar compatibilidad con Java y, al mismo tiempo, ofrecer sintaxis mas concisa, null-safety en el sistema de tipos y construcciones modernas.

**Menos lineas no garantizan mejor diseño**, pero menos ceremonia puede hacer mas visible la intencion.

---

<!-- _class: dense -->

## Sintaxis: expresar la misma intencion

**Java**

```java
public String saludo(String nombre) {
    return "Hola, " + nombre;
}
```

**Kotlin**

```kotlin
fun saludo(nombre: String): String = "Hola, $nombre"
```

- El tipo se escribe despues del nombre; `fun` declara funciones.
- La inferencia evita repetir tipos cuando son evidentes.
- Las expresiones pueden devolver valores directamente.

---

<!-- _class: dense -->

## Variables y tipos

**Java**

```java
final String materia = "DCP";
int clase = 1;
```

**Kotlin**

```kotlin
val materia = "DCP"  // referencia de solo lectura
var clase = 1        // referencia reasignable
```

---

<!-- _class: compact -->

## Variables y tipos (cont.)

Kotlin infiere `String` e `Int`, pero sigue siendo estaticamente tipado.

```kotlin
val anio: Int = 2026
// anio = "dos mil veintiseis"  // error de compilacion
```

Usaremos `val` por defecto y `var` solo cuando el cambio sea parte necesaria del modelo.

---

<!-- _class: compact -->

## Null-safety: ausencia explicita

En Java, una variable de referencia puede contener `null`; un acceso incorrecto falla en ejecucion con `NullPointerException`.

```java
String nombre = buscarNombre();
int largo = nombre.length();
```

En Kotlin, los tipos no admiten `null` por defecto:

```kotlin
var nombre: String = "Ada"
// nombre = null             // error de compilacion

var apodo: String? = null   // ausencia permitida
```

El `?` convierte la posibilidad de ausencia en parte visible del contrato.

---

## Trabajar con valores nullable

```kotlin
val apodo: String? = buscarApodo()

val largo = apodo?.length       // Int?
val visible = apodo ?: "Sin apodo"
```

- `?.` ejecuta el acceso solo si el receptor no es `null`.
- `?:` provee un valor alternativo, operador Elvis.
- `if (apodo != null)` permite un acceso seguro dentro de la rama.
- `!!` afirma que no es `null`, pero puede lanzar una excepcion.

> Usar `!!` traslada nuevamente el riesgo al tiempo de ejecucion. Debe ser excepcional, no una salida rapida.

---

## Clases para representar datos

Una clase Java suele requerir campos, constructor, getters, `equals`, `hashCode` y `toString`.

```java
public final class Alumno {
    private final String legajo;
    private final String nombre;

    public Alumno(String legajo, String nombre) {
        this.legajo = legajo;
        this.nombre = nombre;
    }
    // getters, equals, hashCode, toString...
}
```

Gran parte de ese codigo es ceremonia tecnica, no una regla del dominio.

---

## Data classes en Kotlin

> **Una `data class` es una clase cuyo proposito principal es almacenar datos; Kotlin genera automaticamente operaciones estructurales como `equals`, `hashCode`, `toString`, `componentN` y `copy`.**

```kotlin
data class Alumno(
    val legajo: String,
    val nombre: String,
)

val ana = Alumno("A-104", "Ana")
val copia = ana.copy(nombre = "Ana Maria")
```

La igualdad compara los valores declarados en el constructor primario, no solamente la identidad de los objetos.

---

## Data class no significa modelo anemico

Una `data class` tambien puede validar y ofrecer comportamiento:

```kotlin
data class Calificacion(val valor: Int) {
    init {
        require(valor in 1..10) { "Calificacion fuera de rango" }
    }

    fun estaAprobada(): Boolean = valor >= 4
}
```

- El constructor rechaza estados invalidos.
- La regla de aprobacion vive junto al dato que necesita.
- El tipo `Calificacion` comunica mas que un `Int` suelto.

La concision del lenguaje debe ayudarnos a construir un modelo mas claro, no solo archivos mas cortos.

---

## Comparacion inicial

| Aspecto | Java | Kotlin |
|---|---|---|
| Declaraciones | mas explicitas y ceremoniales | inferencia y sintaxis concisa |
| Mutabilidad | convencion + `final` | distincion directa `val` / `var` |
| Ausencia | referencias potencialmente `null` | `T` y `T?` son tipos distintos |
| Objetos de datos | metodos repetitivos o `record` | `data class` y `copy` |
| JVM | lenguaje historico principal | interoperabilidad con Java |

Kotlin reduce parte de la complejidad accidental, pero **las buenas responsabilidades y abstracciones siguen siendo decisiones de diseño**.

---

## Ideas para llevarse hoy

1. Un algoritmo describe una estrategia; un programa la vuelve ejecutable en un contexto real.
2. Diseñar implica decidir donde viven los datos, el comportamiento y las responsabilidades.
3. El estado mutable y los efectos ocultos aumentan la dificultad de comprender un sistema.
4. Kotlin hace explicitas decisiones como mutabilidad y ausencia de valores.
5. `data class` reduce ceremonia, pero no reemplaza el modelado.
6. El uso de IA debe ser declarado, trazable, verificado y defendible.

---

<!-- _class: compact -->

## Fuentes y lecturas recomendadas

- JetBrains. [Kotlin documentation: Basic syntax](https://kotlinlang.org/docs/basic-syntax.html)
- JetBrains. [Null safety](https://kotlinlang.org/docs/null-safety.html)
- JetBrains. [Data classes](https://kotlinlang.org/docs/data-classes.html)
- JetBrains. [Calling Java from Kotlin](https://kotlinlang.org/docs/java-interop.html)
- Oracle. [Java Language Basics](https://docs.oracle.com/javase/tutorial/java/nutsandbolts/)
- Brooks, F. P. [No Silver Bullet: Essence and Accidents of Software Engineering](https://www.cs.unc.edu/techreports/86-020.pdf)
