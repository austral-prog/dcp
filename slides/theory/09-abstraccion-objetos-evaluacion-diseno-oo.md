---
marp: true
theme: dcp
paginate: true
title: Clase 5 - Abstracción con objetos y evaluación crítica de diseño OO
---

<!-- _class: cover -->
<!-- _paginate: false -->

# Abstracción con objetos

## Responsabilidad, encapsulamiento, polimorfismo y evaluación crítica del diseño

**Clase teórica 5 · DCP**

---

## Objetivo de la clase

Una buena abstracción **esconde lo que cambia** y expone sólo lo que los demás necesitan saber. Una mala agrega clases sin resolver ningún problema.

Hoy vamos a:

- Ver a los **objetos como unidades de responsabilidad**.
- Proteger el estado con **encapsulamiento**.
- Usar **interfaces simples** y **polimorfismo** en lugar de `if`/`when` por tipo.
- Entender qué son las **clases abstractas** y cuándo conviene **herencia** o **composición**.
- **Evaluar críticamente** un diseño OO propuesto por una IA: sobre-diseño y jerarquías injustificadas.

> Diseñar no es agregar abstracciones: es agregar **las justas**, en el lugar correcto.

---

## Recorrido de esta clase

1. Objetos y responsabilidad.
2. Encapsulamiento.
3. Interfaces y polimorfismo.
4. Clases abstractas, herencia y composición.
5. Evaluar un diseño propuesto por la IA.
6. Actividad y cierre.

Todos los ejemplos están en **Kotlin** y siguen lo visto en las clases anteriores: tipos que modelan el dominio, funciones puras e inmutabilidad.

---

<!-- _class: section -->
<!-- _paginate: false -->

# Objetos y responsabilidad

## Quién sabe qué y quién hace qué

---

## Qué es un objeto

Un objeto junta **estado** (datos) y **comportamiento** (operaciones sobre esos datos) detrás de un **nombre del dominio**.

| | `data class` (clase 4) | Objeto con comportamiento |
|---|---|---|
| Propósito | Representar **datos** | Representar un **rol** o una **responsabilidad** |
| Ejemplo | `Lectura(estudiante, nota)` | `Cuenta`, `Inscripciones`, `Notificador` |
| Estado | Normalmente inmutable | Puede cambiar, **bajo sus reglas** |
| Interesa | Qué **contiene** | Qué **sabe hacer** |

No todo tiene que ser un objeto: las funciones puras y las `data class` siguen siendo la primera opción para **calcular** y para **representar datos**.

---

## Una clase, una responsabilidad

Una clase tiene **una sola razón para cambiar**. ¿Qué cambia si cambia el negocio, el formato o el almacenamiento?

```kotlin
class Pedido(val items: List<Item>) {
    fun total(): Double = items.sumOf { it.precio }      // regla de negocio

    fun imprimirFactura() { println("Total: ${total()}") } // formato + salida

    fun guardar() { baseDeDatos.insert("pedidos", this) }  // persistencia

    fun avisarPorMail() { smtp.send("Gracias por tu compra") } // red
}
```

`Pedido` cambia si cambia el **impuesto**, el **diseño de la factura**, la **base de datos** o el **proveedor de mail**: cuatro razones distintas.

---

## Separar responsabilidades

```kotlin
class Pedido(val items: List<Item>) {          // sabe calcular su total
    fun total(): Double = items.sumOf { it.precio }
}

interface RepositorioPedidos { fun guardar(pedido: Pedido) }  // persistir
interface Notificador { fun enviar(destino: String, mensaje: String) } // avisar

class ConfirmarPedido(
    private val repositorio: RepositorioPedidos,
    private val notificador: Notificador,
) {
    fun confirmar(pedido: Pedido, cliente: String) {
        repositorio.guardar(pedido)
        notificador.enviar(cliente, "Gracias por tu compra: ${pedido.total()}")
    }
}
```

Cada pieza tiene un **nombre claro**, una **razón para cambiar** y se puede **probar sola**.

---

## Cohesión: ¿pertenece acá?

Una clase **cohesiva** tiene métodos y datos que trabajan juntos para un mismo propósito.

Señales de baja cohesión:

- Nombres genéricos: `Manager`, `Helper`, `Utils`, `Service` que hace de todo.
- Métodos que no usan ninguna propiedad de la clase.
- Propiedades que sólo se usan en **un** método.
- Dificultad para describir la clase **sin usar "y"**: *"calcula el total **y** guarda **y** envía"*.

> Prueba rápida: describí la clase en una frase. Si necesitás una "y", probablemente son dos clases.

---

<!-- _class: section -->
<!-- _paginate: false -->

# Encapsulamiento

## Proteger el estado, exponer el comportamiento

---

## Sin encapsulamiento: cualquiera rompe la regla

```kotlin
class Cuenta {
    var saldo: Double = 0.0
}

fun extraer(cuenta: Cuenta, monto: Double) {
    cuenta.saldo -= monto
}

val c = Cuenta()
extraer(c, 500.0)
println(c.saldo) // -500.0: una cuenta con saldo negativo
```

- La regla "el saldo no puede ser negativo" **no vive en ningún lado**.
- Cada función que toque `saldo` tiene que acordarse de respetarla.
- Si la regla cambia, hay que **buscar todos los usos**.

---

## Con encapsulamiento: el objeto cuida sus reglas

```kotlin
class Cuenta(saldoInicial: Double = 0.0) {
    var saldo: Double = saldoInicial
        private set                       // se lee desde afuera, se escribe sólo acá

    init { require(saldoInicial >= 0) { "El saldo inicial no puede ser negativo" } }

    fun depositar(monto: Double) {
        require(monto > 0) { "El monto debe ser positivo" }
        saldo += monto
    }

    fun extraer(monto: Double) {
        require(monto > 0) { "El monto debe ser positivo" }
        check(monto <= saldo) { "Saldo insuficiente" }
        saldo -= monto
    }
}
```

La invariante se verifica **en un único lugar**.

---

## Principios de encapsulamiento

- **Ocultá el estado**: `private` por defecto; exponé sólo lo necesario.
- **Exponé comportamiento, no datos**: `cuenta.extraer(50.0)` en lugar de tocar `saldo` desde afuera.
- **Protegé las invariantes** al construir y en cada operación que cambia el estado.
- **No devuelvas estructuras mutables internas**: devolvé una `List` de sólo lectura o una copia.
- Menos superficie pública = **menos lugares donde se puede romper** y más libertad para cambiar por dentro.

```kotlin
class Curso {
    private val _alumnos = mutableListOf<String>()
    val alumnos: List<String> get() = _alumnos.toList() // copia defensiva
    fun inscribir(nombre: String) { _alumnos.add(nombre) }
}
```

---

## Getters y setters para todo no es encapsular

```kotlin
class Cuenta {
    var saldo: Double = 0.0     // en Kotlin ya es getter + setter público
}
```

Exponer cada propiedad con acceso total **no protege nada**: sólo agrega un nivel de sintaxis.

| Pregunta | Mala señal | Buena señal |
|---|---|---|
| ¿Quién modifica el estado? | Cualquiera | Sólo métodos de la clase |
| ¿Dónde está la regla? | Repetida en los clientes | En la clase |
| ¿Cómo se usa? | `if (c.saldo >= m) c.saldo -= m` | `c.extraer(m)` |

> *Tell, don't ask*: pedile al objeto que haga algo; no le saques los datos para decidir por él.

---

<!-- _class: section -->
<!-- _paginate: false -->

# Interfaces y polimorfismo

## Depender de lo que hace, no de lo que es

---

## Una interfaz es un contrato

Define **qué** se puede pedir, sin decir **cómo** se hace:

```kotlin
interface Notificador {
    fun enviar(destino: String, mensaje: String)
}

class NotificadorEmail : Notificador {
    override fun enviar(destino: String, mensaje: String) {
        println("Email a $destino: $mensaje")      // acá iría el envío real
    }
}

class NotificadorSms : Notificador {
    override fun enviar(destino: String, mensaje: String) {
        println("SMS a $destino: $mensaje")
    }
}
```

Quien usa un `Notificador` **no sabe ni le importa** si es mail, SMS o cualquier otro.

---

## Polimorfismo: un mensaje, muchas respuestas

El mismo código funciona con **cualquier implementación** del contrato:

```kotlin
class Inscripciones(private val notificador: Notificador) {
    fun confirmar(alumno: String) {
        notificador.enviar(alumno, "Inscripción confirmada")
    }
}

Inscripciones(NotificadorEmail()).confirmar("sofia@mail.com")
Inscripciones(NotificadorSms()).confirmar("+54 11 5555-0000")
```

- `Inscripciones` **no cambia** al agregar un canal nuevo (Slack, push...).
- La decisión de qué implementación usar se toma **afuera**, al armar los objetos.
- El efecto (mail, SMS, red) queda **detrás de una interfaz**, lejos de la lógica del negocio.

---

## Polimorfismo y testing

Como `Inscripciones` depende de la interfaz, en un test usamos una implementación **falsa**:

```kotlin
class NotificadorEnMemoria : Notificador {
    val enviados = mutableListOf<Pair<String, String>>()
    override fun enviar(destino: String, mensaje: String) {
        enviados.add(destino to mensaje)
    }
}

@Test
fun `confirmar avisa al alumno`() {
    val notificador = NotificadorEnMemoria()

    Inscripciones(notificador).confirmar("sofia@mail.com")

    assertEquals(listOf("sofia@mail.com" to "Inscripción confirmada"), notificador.enviados)
}
```

Sin red, sin mail real, sin librerías de mocks.

---

## Sin polimorfismo: un `when` por tipo

```kotlin
fun aplicarDescuento(tipo: String, precio: Double, valor: Double): Double =
    when (tipo) {
        "ninguno" -> precio
        "porcentual" -> precio * (100 - valor) / 100
        "monto" -> maxOf(0.0, precio - valor)
        else -> throw IllegalArgumentException("Tipo desconocido: $tipo")
    }
```

Problemas:

- El tipo es un `String`: un error de tipeo **se descubre en ejecución**.
- Agregar un descuento obliga a **modificar** esta función (y todas las que repitan el `when`).
- Los datos de cada caso (`valor`) se comparten aunque no signifiquen lo mismo.

---

## Con polimorfismo: cada caso, su clase

```kotlin
interface Descuento { fun aplicar(precio: Double): Double }

object SinDescuento : Descuento {
    override fun aplicar(precio: Double) = precio
}
class Porcentual(private val porcentaje: Int) : Descuento {
    override fun aplicar(precio: Double) = precio * (100 - porcentaje) / 100
}
class MontoFijo(private val monto: Double) : Descuento {
    override fun aplicar(precio: Double) = maxOf(0.0, precio - monto)
}

fun total(precios: List<Double>, descuento: Descuento): Double =
    precios.sumOf { descuento.aplicar(it) }
```

`total(precios, Porcentual(10))`: un descuento nuevo es **una clase nueva**, sin tocar `total`.

---

## ¿Interfaz o `sealed class`?

Las dos modelan alternativas. La diferencia es **quién puede agregar casos**.

| | `sealed class` / `sealed interface` | `interface` |
|---|---|---|
| Variantes | **Cerradas**: las conoce el compilador | **Abiertas**: cualquiera puede implementar |
| `when` exhaustivo | Sí | No |
| Agregar una **operación** | Fácil: un `when` nuevo | Difícil: tocar cada clase |
| Agregar una **variante** | Difícil: revisar cada `when` | Fácil: una clase nueva |
| Ideal para | Resultados, estados, datos del dominio | Comportamiento intercambiable, efectos |

Regla práctica: **datos con casos fijos** → `sealed`; **comportamiento que puede variar o reemplazarse** → `interface`.

---

## Interfaces simples y chicas

Una interfaz grande obliga a implementar cosas que no se necesitan:

```kotlin
interface Repositorio {
    fun guardar(pedido: Pedido)
    fun buscar(id: Int): Pedido?
    fun borrar(id: Int)
    fun exportarCsv(): String
    fun enviarResumenPorMail()
}
```

Mejor **interfaces chicas**, definidas por lo que **el cliente necesita**:

```kotlin
interface RepositorioPedidos { fun guardar(pedido: Pedido) }
```

- Pocos métodos, con nombres del dominio.
- Si un cliente sólo guarda, su dependencia sólo expone **guardar**.
- Más fácil de implementar, de falsear en tests y de **entender**.

---

<!-- _class: section -->
<!-- _paginate: false -->

# Clases abstractas, herencia y composición

## Reutilizar sin enredarse

---

## Clase abstracta: comportamiento compartido y un hueco

Una clase **abstracta** no se instancia: define algo **ya implementado** y deja partes **sin implementar** para las subclases.

```kotlin
abstract class Reporte(private val titulo: String) {
    fun generar(): String =
        "== $titulo ==\n${cuerpo()}\n-- fin --"   // el esqueleto es siempre igual

    protected abstract fun cuerpo(): String       // cada reporte completa esto
}

class ReporteVentas(private val ventas: List<Double>) : Reporte("Ventas") {
    override fun cuerpo() = "Total: ${ventas.sum()}"
}

class ReporteAlumnos(private val nombres: List<String>) : Reporte("Alumnos") {
    override fun cuerpo() = nombres.joinToString("\n")
}
```

Es el patrón **Template Method**: el algoritmo fijo en la base, los pasos variables en las subclases.

---

## Interfaz, clase abstracta o clase `open`

| | `interface` | `abstract class` | `open class` |
|---|---|---|---|
| Estado (propiedades con valor) | No | **Sí** | Sí |
| Código compartido | Métodos por defecto, limitado | **Sí** | Sí |
| Instanciable | No | No | **Sí** |
| Una clase puede... | Implementar **varias** | Heredar **una** | Heredar **una** |
| Usala para | **Contratos** y roles | Esqueleto común + pasos a completar | Casi nunca: preferí composición |

En Kotlin las clases son **finales por defecto**: hay que escribir `open` para poder heredar. Es una decisión de diseño explícita, no un descuido.

> Empezá por una **interfaz**. Pasá a clase abstracta sólo si hay **estado y código real** para compartir.

---

## Herencia es "es-un", y debe cumplirse siempre

Una subclase tiene que poder usarse **donde se espera la base**, sin sorpresas (principio de sustitución de Liskov).

```kotlin
open class Ave {
    open fun volar(): String = "Vuela"
}

class Pinguino : Ave() {
    override fun volar(): String =
        throw UnsupportedOperationException("Los pingüinos no vuelan")
}

fun migrar(aves: List<Ave>) = aves.map { it.volar() } // explota con un pingüino
```

Una subclase que **lanza excepciones** o **ignora** métodos heredados está avisando que la jerarquía está mal.

```kotlin
interface Volador { fun volar(): String }   // sólo quien vuela lo implementa
```

---

## Herencia profunda: el problema

```text
Empleado
 └─ Gerente
     └─ GerenteDeVentas
         └─ GerenteDeVentasRemoto
             └─ GerenteDeVentasRemotoPartTime
```

- Cada nivel agrega una **combinación** (rol × modalidad × jornada): crece como un producto cartesiano.
- Un cambio en `Empleado` **afecta a todos** los descendientes (acoplamiento fuerte).
- Para entender `GerenteDeVentasRemotoPartTime` hay que **leer cinco clases**.
- No se puede cambiar de rol en ejecución: el tipo es **para siempre**.

Regla práctica: **más de 2 niveles** de jerarquía merece una revisión seria.

---

## Composición: armar con piezas

Cuando algo **varía**, ponelo en un objeto aparte y **componelo**:

```kotlin
interface Bonificacion { fun sobre(base: Double): Double }

object SinBono : Bonificacion { override fun sobre(base: Double) = base }
class BonoGerencial : Bonificacion { override fun sobre(base: Double) = base * 1.5 }
class Proporcional(private val horas: Int) : Bonificacion {
    override fun sobre(base: Double) = base * horas / 40
}

class Empleado(
    val nombre: String,
    private val sueldoBase: Double,
    private val bonificacion: Bonificacion,
) {
    fun sueldo(): Double = bonificacion.sobre(sueldoBase)
}
```

Una sola clase `Empleado`; las variaciones se **combinan y se cambian** sin crear subclases.

---

## Delegación en Kotlin: `by`

Para reutilizar un contrato **sin heredar**, Kotlin permite delegar a otro objeto:

```kotlin
class NotificadorConLog(private val real: Notificador) : Notificador by real {
    override fun enviar(destino: String, mensaje: String) {
        println("LOG: enviando a $destino")
        real.enviar(destino, mensaje)
    }
}

val notificador: Notificador = NotificadorConLog(NotificadorEmail())
Inscripciones(notificador).confirmar("sofia@mail.com")
```

- Agregamos un comportamiento (log) **sin tocar** `NotificadorEmail` ni `Inscripciones`.
- `by real` delega automáticamente los métodos que no sobrescribimos.
- Es el patrón **Decorator**, sin herencia.

---

## Herencia o composición: cómo decidir

| Preguntate... | Si la respuesta es... | Entonces |
|---|---|---|
| ¿Es **realmente** un "es-un", siempre? | Sí, sin excepciones | Herencia puede servir |
| ¿Sólo quiero **reutilizar código**? | Sí | **Composición** |
| ¿Hay varias dimensiones que varían? | Sí | **Composición** |
| ¿El comportamiento cambia en ejecución? | Sí | **Composición** |
| ¿Hay un esqueleto fijo con pasos a completar? | Sí | Clase **abstracta** |
| ¿Sólo necesito un contrato? | Sí | **Interfaz** |

> *Favorecé la composición sobre la herencia.* La herencia no está prohibida: es la herramienta más **acoplada**, y se usa cuando está justificada.

---

<!-- _class: section -->
<!-- _paginate: false -->

# Evaluar el diseño de una IA

## Útil no es lo mismo que justificado

---

## Por qué una IA tiende a sobre-diseñar

Los modelos aprendieron de mucho código "de libro de patrones" y **suelen** responder con la solución más elaborada:

- Se premia lo que **parece profesional**: interfaces, fábricas, estrategias.
- No conoce **tu** contexto: cuántas variantes hay *de verdad*, cuánto va a crecer.
- Anticipa extensibilidad que **nadie pidió** ("por si acaso").
- Replica el estilo de lenguajes verbosos aunque en Kotlin alcance con una función.

El resultado **compila y se ve prolijo**, pero tiene más código para leer, probar y mantener que el problema.

> Más clases no es más calidad. **Cada abstracción tiene un costo**: alguien tiene que entenderla.

---

## Ejemplo: un requisito simple

> *"Si el total de la compra supera $100, aplicar 10% de descuento."*

Propuesta típica de una IA:

```text
interface DescuentoStrategy
abstract class BaseDescuentoStrategy : DescuentoStrategy
class DescuentoPorMontoStrategy : BaseDescuentoStrategy
class DescuentoNuloStrategy : BaseDescuentoStrategy
enum class TipoDescuento
class DescuentoStrategyFactory
class ContextoDescuento
class ResultadoDescuento
class DescuentoService
interface DescuentoServiceProvider
```

**10 tipos** para una regla de una línea. Hay una sola variante real, y la fábrica, el contexto y el resultado no resuelven ningún problema que hoy exista.

---

## La versión que alcanza

```kotlin
fun descuento(total: Double): Double =
    if (total > 100) total * 0.10 else 0.0
```

- Una función **pura**: se prueba con dos `assertEquals`.
- Se lee en **una mirada**.
- No cierra ninguna puerta: si aparece una segunda regla, se **refactoriza entonces**.

Preguntas clave frente a la propuesta de la IA:

- ¿Qué variación **real** (hoy) justifica la interfaz?
- ¿Qué se rompe si **borro** esta clase?
- ¿Cuántos casos de uso concretos hay?

> El mejor diseño es el **más simple que cumple los requisitos actuales** y que es fácil de cambiar.

---

## Señales de alarma en un diseño propuesto

| Señal | Por qué preocupa |
|---|---|
| Interfaz con **una sola** implementación y sin motivo | Indirección sin beneficio |
| Jerarquía de **3 o más niveles** | Acoplamiento y difícil de entender |
| Clases `Abstract...`, `Base...` vacías o casi vacías | Herencia sólo para "organizar" |
| `Factory`, `Provider`, `Manager`, `Helper` sin necesidad | Nombres que esconden responsabilidades |
| Subclases que **lanzan** `UnsupportedOperationException` | Viola "es-un" |
| Clases sólo con getters/setters | Sin comportamiento: es un `data class` mal usado |
| Configuración y extensibilidad que nadie pidió | *YAGNI*: no lo vas a necesitar |
| Herencia usada sólo para **reutilizar código** | Mejor composición o una función |

---

## Preguntas para evaluar un diseño

1. **Responsabilidad**: ¿puedo describir cada clase en una frase **sin "y"**?
2. **Variación real**: ¿qué cambia hoy o en el próximo requisito concreto que justifique esta abstracción?
3. **Costo**: ¿cuántos archivos debo abrir para entender una regla?
4. **Encapsulamiento**: ¿quién puede modificar el estado? ¿dónde viven las invariantes?
5. **Herencia**: ¿se cumple "es-un" siempre? ¿alcanzaría con composición o una interfaz?
6. **Testabilidad**: ¿puedo probar la regla **sin** armar medio sistema?
7. **Borrado**: si elimino esta clase, ¿qué se rompe? Si nada, **sobra**.

> Un diseño que no podés explicar en voz alta con claridad **no lo aceptés** todavía.

---

## Cuándo SÍ introducir una abstracción

<!-- _class: compact -->

Una abstracción se **gana** cuando hay evidencia, no antes:

- Existen **dos o más variantes reales** hoy (regla de tres: a la tercera, extraé).
- Necesitás **reemplazar** una dependencia con efectos en los tests (archivo, red, base de datos).
- Hay un **punto de variación concreto** que cambia con frecuencia.

Ejemplo: llega un segundo requisito, *"los clientes VIP tienen 20% fijo"*. **Ahora** sí:

```kotlin
interface Descuento { fun aplicar(total: Double): Double }

object PorMonto : Descuento {
    override fun aplicar(total: Double) = if (total > 100) total * 0.9 else total
}
object Vip : Descuento {
    override fun aplicar(total: Double) = total * 0.8
}
```

El diseño **evoluciona** con el problema: no se adivina por adelantado.

---

## Cómo pedírselo a la IA

```text
Necesito aplicar 10% de descuento cuando el total supera $100.

Proponé el diseño MÁS SIMPLE que cumpla este requisito.
Restricciones:
- Preferí funciones y data classes; usá clases sólo si guardan estado.
- No crees interfaces, clases abstractas ni fábricas sin una
  segunda implementación concreta hoy.
- Si proponés una abstracción, justificá qué variación real resuelve.
- Máximo 2 niveles de herencia.
```

Y después, **revisá vos**: aplicá las preguntas de evaluación y pedile que **elimine** lo que no pueda justificar.

> *"Eliminá las clases que no tengan una razón concreta hoy y explicame por qué quedan las demás."*

---

## Actividad (7 minutos): evaluar un diseño de IA

Requisito: *"Una biblioteca presta libros a socios. Un socio puede tener hasta 3 libros."*

Propuesta de la IA:

```text
abstract class EntidadBase
abstract class PersonaBase : EntidadBase
class Socio : PersonaBase
class SocioPremium : Socio
class SocioPremiumPlus : SocioPremium
class LibroFactory
interface LibroRepositoryInterface        // única implementación: LibroRepositoryImpl
class PrestamoManagerHelper
```

En parejas:

1. ¿Qué señales de alarma ven? ¿Qué **eliminarían**?
2. ¿Qué dejarían y **con qué responsabilidad**?
3. ¿Qué pedido le harían a la IA para corregirlo?

---

## Una posible solución

```kotlin
data class Libro(val titulo: String)

class Socio(val nombre: String) {
    private val _prestados = mutableListOf<Libro>()
    val prestados: List<Libro> get() = _prestados.toList()

    fun pedirPrestado(libro: Libro) {
        check(_prestados.size < MAX_LIBROS) { "Máximo $MAX_LIBROS libros" }
        _prestados.add(libro)
    }

    private companion object { const val MAX_LIBROS = 3 }
}
```

- La regla "hasta 3 libros" vive **dentro** de `Socio` (encapsulamiento).
- Sin jerarquía: si aparecen "premium" con otro tope, el tope pasa a ser un **dato** o una `Politica` compuesta.
- El repositorio será una interfaz **cuando** haya que sustituirlo en tests o tener otra implementación.

---

## Para llevarse

- Un objeto es una **unidad de responsabilidad**: una razón para cambiar, un nombre del dominio.
- **Encapsular** es proteger invariantes: estado privado, comportamiento público, *tell, don't ask*.
- **Interfaces chicas** + **polimorfismo** reemplazan los `when` por tipo y aíslan los efectos.
- **Clase abstracta**: esqueleto común con pasos a completar. **Interfaz**: contrato. Empezá por la interfaz.
- Herencia sólo con "es-un" real; ante la duda, **composición** (y `by` en Kotlin).
- Frente a un diseño de la IA: pedí el **más simple**, justificá cada abstracción y **eliminá** lo que no tenga motivo hoy.

> Una abstracción vale lo que **cuesta entenderla**: que se pague sola.

---

## Para seguir aprendiendo

<!-- _class: compact -->

**Libros**

- [Effective Java, 3.ª ed. — Joshua Bloch](https://www.informit.com/store/effective-java-9780134685991): composición sobre herencia, interfaces y diseño de clases (aplica casi igual a Kotlin).
- [Practical Object-Oriented Design — Sandi Metz](https://www.poodr.com/): responsabilidad única, dependencias y diseño sencillo (ejemplos en Ruby, ideas universales).
- [A Philosophy of Software Design — John Ousterhout](https://web.stanford.edu/~ouster/cgi-bin/book.php): complejidad, módulos profundos y abstracciones útiles.
- [Design Patterns — Gamma, Helm, Johnson y Vlissides](https://www.informit.com/store/design-patterns-elements-of-reusable-object-oriented-9780201633610): el catálogo clásico (Strategy, Decorator, Template Method).
- [Refactoring (2.ª ed.) — Martin Fowler](https://martinfowler.com/books/refactoring.html): cómo simplificar y reorganizar un diseño en pasos seguros.
- [Elegant Objects — Yegor Bugayenko](https://www.elegantobjects.org/): una mirada exigente sobre objetos, inmutabilidad y encapsulamiento.
- [Effective Kotlin — Marcin Moskała](https://kt.academy/book/effectivekotlin): herencia, interfaces y diseño de clases en Kotlin.

**Documentación de Kotlin**

- [Interfaces](https://kotlinlang.org/docs/interfaces.html) · [Herencia](https://kotlinlang.org/docs/inheritance.html) · [Delegación](https://kotlinlang.org/docs/delegation.html)
