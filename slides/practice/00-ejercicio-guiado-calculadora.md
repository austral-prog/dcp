---
marp: true
theme: dcp
paginate: true
title: Clase 1 - Ejercicio Guiado - Calculadora de Gastos
---

<!-- _class: cover -->
<!-- _paginate: false -->

# Ejercicio Guiado

## Calculadora de Gastos (Con y Sin IA)

**Práctica Clase 0 · DCP**

---

## Objetivo de la práctica

Vamos a resolver un problema sencillo: una **Calculadora de Gastos**.
Lo haremos en dos fases para experimentar el cambio de paradigma:

1. **A mano (Sin IA)**: Escribiendo código paso a paso, como lo haríamos si recién empezamos.
2. **Con Asistencia (Con IA)**: Usando OpenCode para ver cómo lo resolvería un asistente.

> Al final compararemos los resultados, entenderemos los límites de pedir código "a ciegas" y aprenderemos a registrar nuestra interacción (Prompt Log).

---

## El Problema: Calculadora de Gastos

Queremos un programa de consola que permita:
1. Ingresar gastos (Categoría y Monto).
2. Seguir pidiendo gastos hasta que el usuario escriba "fin".
3. Al terminar, mostrar el **Total** de los gastos ingresados.

_Empecemos resolviéndolo a mano, con un enfoque básico y procedural._

---

<!-- _class: compact -->

## Setup: Tu espacio de trabajo

Antes de empezar, te recomendamos crear una carpeta central para todos los proyectos de la materia. Por ejemplo: `universidad/dcp/`.

Abrí tu terminal y prepará la carpeta para este ejercicio:

```bash
# Creamos la carpeta general y la del proyecto de hoy
mkdir -p ~/universidad/dcp/calculadora

# Navegamos hacia esa carpeta
cd ~/universidad/dcp/calculadora
```

¡Todo el trabajo de hoy lo haremos dentro de esta carpeta!

---

<!-- _class: compact -->

## Parte 1: Resolución a mano (Sin IA)

Estando en la terminal dentro de `calculadora`, **abrí IntelliJ IDEA** (o tu IDE preferido).
Adentro, creá un archivo `Main.kt`. Empecemos definiendo el estado:

```kotlin
import java.util.Scanner

fun main() {
    val scanner = Scanner(System.`in`)
    val categorias = mutableListOf<String>()
    val montos = mutableListOf<Double>()
    var total = 0.0

    // ¿Qué pasa con estas listas separadas? (Complejidad accidental)
}
```

- `categorias` y `montos` son **listas mutables**: pueden crecer con `.add()`. En Python es lo mismo que un `list` y su `append`.
- Chiches de Kotlin: `<String>` es el tipo de cada elemento (en Python no se declara). `val` no se reasigna. ``System.`in``` usa backticks porque `in` es palabra reservada.

---

<!-- _class: compact -->

## Parte 1: El ciclo de ingreso

Agregamos un ciclo `while` para procesar la entrada del usuario.

```kotlin
    while (true) {
        println("Ingresa categoría (o 'fin' para terminar):")
        val categoria = scanner.nextLine()
        if (categoria.lowercase() == "fin") break

        println("Ingresa monto:")
        val monto = scanner.nextLine().toDoubleOrNull() ?: 0.0

        categorias.add(categoria)
        montos.add(monto)
        total += monto
    }
```
_¿Qué pasa si el usuario ingresa texto en el monto? (Lo tratamos como 0.0, silenciando el error)._

---

## Parte 1: Resultados y Reflexión

Finalmente imprimimos el resumen.

```kotlin
    println("\n--- Resumen de Gastos ---")
    for (i in categorias.indices) {
        println("- ${categorias[i]}: $${montos[i]}")
    }
    println("TOTAL: $$total")
```

**Reflexión del código manual:**
- **Estructuras frágiles:** Tenemos los datos sueltos (`categorias` y `montos`). Si borramos un elemento, desalineamos el otro (Listas paralelas).
- **Código espagueti:** En `main` está todo mezclado: pedir datos, sumar y mostrar. La regla de negocio (el total) vive atada al `println`. Si mañana querés el mismo resumen en Excel, PDF o por mail, no hay un “total” reutilizable: hay que copiar o reescribir esa lógica adentro de cada salida.

---

<!-- _class: cover -->

# Y ahora... ¿Cómo lo resolvería la IA?

---

<!-- _class: compact -->

## Parte 2: Preparando el entorno (Terminal)

En lugar de escribir todo a mano, vamos a delegar la construcción a **OpenCode** (el agente de IA de la materia).

1. **Volvé a tu terminal**.
2. **Asegurate de estar en la carpeta** del proyecto:
   ```bash
   cd ~/universidad/dcp/calculadora
   ```
3. **Iniciá el agente** ejecutando:
   ```bash
   opencode
   ```
*(Vas a ver que el prompt de tu consola cambia, indicando que estás chateando con la IA)*

---

<!-- _class: compact -->

## Parte 2: El Prompt en OpenCode

Vamos a darle libertad a la IA para que escriba Kotlin idiomático. En OpenCode escribí:

> "Escribe un script en Kotlin llamado `CalculadoraIA.kt` que actúe como una calculadora de gastos. Debe pedir al usuario una categoría y un monto en un ciclo hasta que ingrese 'fin'. Usa buenas prácticas de Kotlin. Al terminar, muestra todos los gastos y el total acumulado."

**Magia en acción:** OpenCode usará sus herramientas para crear y escribir el archivo `CalculadoraIA.kt` directamente en tu carpeta.

---

<!-- _class: compact -->

## Parte 2: Ejecución y Comparación

1. **Volvé a IntelliJ** y abrí el nuevo archivo `CalculadoraIA.kt`.
2. **Ejecutalo** (click derecho -> _Run_).
3. **Analicemos las diferencias:**
   - **Estructura:** Seguramente la IA ya no usó listas separadas. Habrá creado algo como `data class Gasto(val categoria: String, val monto: Double)`. Eso modela **un** gasto: categoría y monto viajan juntos. En vez de dos listas que hay que mantener alineadas, tenés una sola lista de `Gasto`. Kotlin además te genera `equals`, `toString` y `copy`. ¡Bien!
   - **Manejo de errores:** Es probable que maneje mejor si ingresamos letras en vez de números.
   - **Velocidad:** Tardamos segundos en lugar de minutos.

---

## El límite de la IA (Por qué estás en esta materia)

La IA hizo un código base mucho mejor que nuestras listas paralelas. Pero... todo sigue adentro de una sola función / archivo.

**El pedido malo:**
> "Agregá filtro por fechas, soporte multimoneda, guardalo en una base de datos y ponelo en una interfaz web"

Si se lo pedimos así nomás, la IA va a meter todo en el mismo `main`. Va a generar una **sopa inmanejable** que no podrás testear ni mantener.

---

## Corregir el pedido

Ese pedido ya metió fechas, moneda, base y web. No las sacamos: las **acomodamos**. Ahora hace falta una instrucción precisa para que el agente refactorice esa sopa.

En OpenCode, sobre el mismo archivo:

> "Refactorizá lo que acabás de generar. Un gasto (categoría, monto, fecha, moneda) va en el modelo. El total y el filtro por fechas son reglas, independientes de la consola y de la web. La persistencia solo guarda y lee gastos. La consola y la interfaz web solo muestran; no calculan ni hablan con la base. No mezcles esas responsabilidades en el `main`."

Ahí el agente tiene un mapa: qué quedó del pedido anterior y **dónde tiene que vivir**. Sin eso, sigue apilando todo en el mismo lugar.

> La IA escribe el código. El ingeniero le dice cómo modelarlo.
