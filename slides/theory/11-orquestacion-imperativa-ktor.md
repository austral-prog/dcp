---
marp: true
theme: dcp
paginate: true
title: Clase 7 - Orquestación imperativa con un framework: introducción a Ktor
---

<!-- _class: cover -->
<!-- _paginate: false -->

# Orquestación imperativa con Ktor

## Exponer el MVP como un servicio simple sin duplicar la lógica de negocio

**Clase teórica 7 · DCP**

---

## Objetivo de la clase

Ya sabemos modelar objetos con responsabilidades claras (Clase 5) y separar el cálculo puro de los efectos (Clase 6). Hoy vamos a darle a nuestro MVP una **puerta de entrada desde el mundo exterior**.

Hoy vamos a:

- Entender qué es un **framework**, una **API/servicio**, un **request**, una **ruta** y un **controlador**.
- Conocer **Ktor**, el único framework del curso, para exponer el MVP por HTTP.
- Ubicar a la capa web como **orquestadora imperativa**: conecta entrada y salida usando los objetos y funciones que ya construimos.
- Adoptar una **estructura organizada (MVC + Rutas + Server)** para el proyecto.
- Incorporar **buenas prácticas con IA** al generar código de framework.

> El framework no es tu aplicación: es el **adaptador** que conecta la red con tu modelo.

---

## Recorrido de esta clase

1. Conceptos base: servicio/API, *request*, *response*, rutas, controladores y *framework*.
2. Introducción a **Ktor**: levantar el servidor, declarar rutas y manejar *requests*.
3. El rol de la capa imperativa: orquestar sin duplicar lógica de negocio.
4. Estructura organizada para el MVP: **MVC + Rutas + Server**.
5. Buenas prácticas al generar código de framework con IA.

---

<!-- _class: section -->
<!-- _paginate: false -->

# Conceptos base

## Qué es un servicio, un framework, una ruta y un controlador

---

## De programa de consola a servicio (API)

Hasta ahora nuestros programas leían de un archivo o de la consola y terminaban.

Un **servicio web** (o **API HTTP**) es un programa que queda **escuchando pedidos** a través de la red y devuelve respuestas estructuradas.

```text
  ┌──────────────┐       1. Request (HTTP)       ┌──────────────────────┐
  │   Cliente    │ ────────────────────────────▶ │   Servicio (Ktor)    │
  │ (navegador,  │                               │                      │
  │  curl, app)  │ ◀──────────────────────────── │  Orquesta tu modelo  │
  └──────────────┘       2. Response (JSON)      └──────────────────────┘
```

- **Ventaja clave**: el núcleo de tu MVP (objetos y funciones) **es exactamente el mismo**; sólo cambia cómo entran y salen los datos.

---

## ¿Qué es un Request y un Response?

La comunicación ocurre mediante pares de **pedido (*Request*)** y **respuesta (*Response*)**:

| | **Request (lo que envía el cliente)** | **Response (lo que devuelve el servidor)** |
|---|---|---|
| **Intención / Resultado** | **Método HTTP**: `GET` (consultar), `POST` (crear/accionar), `DELETE` (borrar) | **Status Code**: `200 OK`, `201 Created`, `400 Bad Request`, `404 Not Found` |
| **Dirección** | **Ruta (*Path*)**: `/alumnos`, `/cursadas/10/inscribir` | — |
| **Datos adicionales** | **Parámetros** (`?activo=true`) y **Body** (ej. JSON con datos de entrada) | **Body**: datos de salida (generalmente en formato **JSON** o texto) |

> Un *request* dice **qué** se quiere hacer y **con qué datos**; el *response* informa **cómo salió** y **qué resultado produjo**.

---

## ¿Qué es un Framework? (y por qué Ktor)

| | **Librería** | **Framework** |
|---|---|---|
| **Quién tiene el control** | **Vos** llamás a sus funciones cuando querés | **Él** maneja el ciclo de vida y llama a tu código |
| **Ejemplo** | `kotlin.collections` (`map`, `filter`) | **Ktor** (servidor HTTP) |

Un **framework** resuelve el trabajo repetitivo de infraestructura (abrir un puerto de red, leer bytes HTTP, transformar JSON) y te deja huecos claros para enchufar tu lógica.

En DCP usamos **Ktor** como **único framework del curso** porque:

- Está hecho en **Kotlin** y usa funciones y lambdas que ya conocemos.
- Es **liviano y explícito**: no esconde magia detrás de decenas de anotaciones.

---

## Rutas y Controladores: quién atiende cada pedido

Cuando llega un *request* al servidor, intervienen dos conceptos clave:

1. **Ruta (*Route*)**: es el **mapa de entrada**. Define qué combinación de `Método HTTP + URL` existe en nuestro servicio (por ejemplo, `GET /alumnos/{legajo}`).
2. **Controlador (*Controller*)**: es el **recepcionista**. Recibe el pedido que entró por la ruta, extrae los datos necesarios, llama al **modelo de negocio** y arma la respuesta.

```text
Request HTTP ──▶ [ Ruta ] ──▶ [ Controlador ] ──▶ [ Modelo (Dominio) ]
               "¿A dónde?"    "Extraigo datos     "Valida reglas y
                               y coordino"         hace el cálculo"
```

> La **ruta** sabe de direcciones URL; el **controlador** sabe coordinar el pedido; el **modelo** sabe las reglas del negocio.

---

<!-- _class: section -->
<!-- _paginate: false -->

# Introducción a Ktor

## Servidor, rutas y manejo de requests

---

## 1. Levantar el servidor

En Ktor, el servidor se inicia dentro de un `main` común de Kotlin:

```kotlin
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun main() {
    embeddedServer(Netty, port = 8080) {
        routing {
            get("/salud") {
                call.respondText("Servicio activo")
            }
        }
    }.start(wait = true)
}
```

Al ejecutar `main()`, el programa queda escuchando en `http://localhost:8080/salud`.

---

## 2. Definir rutas (`routing`)

Las rutas agrupan los puntos de entrada (*endpoints*) por recurso y verbo HTTP:

```kotlin
routing {
    route("/alumnos") {
        get {
            // GET /alumnos -> listar todos los alumnos
        }
        get("/{legajo}") {
            // GET /alumnos/A-104 -> buscar un alumno puntual
        }
        post {
            // POST /alumnos -> registrar un alumno nuevo
        }
    }
}
```

- `route("/alumnos")` evita repetir el prefijo en cada operación.
- `{legajo}` define un **parámetro de ruta** variable.

---

## 3. Manejar el Request y devolver el Response (`call`)

Dentro de cada ruta tenemos acceso a `call` (`ApplicationCall`), que representa el diálogo actual (el *request* que llegó y el *response* que vamos a enviar):

```kotlin
// 1. Leer parámetro de la URL: GET /alumnos/A-104
val legajo = call.parameters["legajo"]

// 2. Leer query param: GET /notas?minima=6
val minima = call.request.queryParameters["minima"]?.toIntOrNull() ?: 6

// 3. Leer el cuerpo (JSON) de un POST como una data class
val datos = call.receive<NuevoAlumnoRequest>()

// 4. Responder con código de estado + datos
call.respond(HttpStatusCode.Created, AlumnoResponse(legajo = "A-104", nombre = "Ana"))
```

---

<!-- _class: section -->
<!-- _paginate: false -->

# El rol de la capa imperativa

## Orquestar sin duplicar lógica de negocio

---

## Recordando Clase 6: núcleo puro, cáscara imperativa

En la clase anterior vimos que los **efectos** viven en los bordes y el **cálculo/modelo** en el centro. En un servicio web, **Ktor es esa cáscara imperativa**:

```text
         ┌─────────────────────────────────────────────────────┐
         │  CÁSCARA IMPERATIVA (Ktor: Rutas + Controladores)   │
         │    ┌───────────────────────────────────────────┐    │
 Request ┼───▶│  NÚCLEO DE DOMINIO (Model)                │────┼──▶ Response
  HTTP   │    │  Objetos, invariantes y funciones puras   │    │     HTTP
         │    └───────────────────────────────────────────┘    │
         └─────────────────────────────────────────────────────┘
```

- Tu paquete `model` **no sabe que existe Ktor** (no importa nada de `io.ktor.*`).
- Si mañana cambiás Ktor por consola o por otra interfaz, el modelo **no se entera**.

---

## Qué hace (y qué NO hace) un controlador

El controlador cumple **tres pasos cortos** en orden:

1. **Leer y validar formato de entrada**: sacar parámetros o JSON del `call` (si falta un dato en el request, devolver `400 Bad Request`).
2. **Orquestar el dominio**: llamar a los métodos de tus objetos o funciones puras que ya tienen las reglas de negocio.
3. **Armar la salida**: transformar la respuesta del modelo en una vista/JSON y un código HTTP (`200`, `201`, `404`, `400`).

> Si en un controlador escribís un `if` con una regla del dominio o un cálculo de negocio, **estás poniendo lógica en el lugar equivocado**.

---

<!-- _class: compact -->

## Antipatrón vs. Buena práctica

❌ **Mal: lógica de negocio metida en la ruta/controlador**

```kotlin
post("/cursadas/{id}/inscribir") {
    val cursada = repo.buscar(call.parameters["id"]!!)!!
    val alumno = call.receive<Alumno>()
    if (cursada.inscriptos.size >= cursada.cupo) {          // ❌ regla de negocio en la capa web
        call.respond(HttpStatusCode.BadRequest, "Sin cupo")
    } else {
        cursada.inscriptos.add(alumno)                      // ❌ rompe encapsulamiento
        call.respond(HttpStatusCode.OK, "Inscripto")
    }
}
```

✅ **Bien: la capa imperativa sólo orquesta lo que el modelo ya sabe hacer**

```kotlin
// En el modelo (Cursada.kt) — ya probado con tests unitarios simples:
fun inscribir(alumno: Alumno) {
    check(_inscriptos.size < cupo) { "Sin cupo disponible" }
    _inscriptos.add(alumno)
}

// En el controlador — sólo coordina entrada, modelo y salida:
cursada.inscribir(alumno)
```

---

<!-- _class: section -->
<!-- _paginate: false -->

# Estructura organizada del servicio

## MVC + Server y Rutas para tu MVP

---

<!-- _class: compact -->

## Cómo organizar el proyecto: MVC + Rutas + Server

Para que el código sea prolijo y fácil de mantener, vamos a separar el servicio en **cinco piezas claras**:

```text
src/main/kotlin/
├── Server.kt                     // 1. Server: configura plugins, une las piezas y arranca Ktor
├── routes/
│   └── CursadaRoutes.kt          // 2. Routes: mapa de URLs (GET, POST) -> llama al Controller
├── controller/
│   └── CursadaController.kt      // 3. Controller (C): orquesta request -> model -> view
├── model/
│   ├── Cursada.kt                // 4. Model (M): objetos de dominio, reglas y funciones puras
│   └── CursadaRepository.kt      //    (y almacenamiento en memoria del dominio)
└── view/
    └── CursadaView.kt            // 5. View (V): DTOs (@Serializable) y formato de respuestas
```

| Pieza | Responsabilidad única | ¿Conoce a Ktor? |
|---|---|---|
| **Model** (`model/`) | Reglas de negocio, cálculos puros, estado e invariantes | **No** |
| **View** (`view/`) | Representar qué datos entran y salen (DTOs JSON / formateo) | No (sólo `@Serializable`) |
| **Controller** (`controller/`) | Extraer datos del pedido, invocar al `model` y responder con `view` | Sí (`ApplicationCall`) |
| **Routes** (`routes/`) | Declarar las rutas HTTP y delegar cada una en el `controller` | Sí (`Route`) |
| **Server** (`Server.kt`) | Instanciar dependencias, instalar plugins y levantar el puerto | Sí (`embeddedServer`) |

---

<!-- _class: dense -->

## Ejemplo completo en código (1/2): `Model` y `View`

**1. `model/Cursada.kt` — El dominio puro (sin nada de web)**

```kotlin
package model

data class Alumno(val legajo: String, val nombre: String)

class Cursada(val id: String, val materia: String, val cupo: Int) {
    private val _inscriptos = mutableListOf<Alumno>()
    val inscriptos: List<Alumno> get() = _inscriptos.toList()

    fun inscribir(alumno: Alumno) {
        check(_inscriptos.size < cupo) { "La cursada $materia no tiene cupo" }
        check(_inscriptos.none { it.legajo == alumno.legajo }) { "El alumno ya está inscripto" }
        _inscriptos.add(alumno)
    }
}
```

**2. `view/CursadaView.kt` — Cómo se ven los datos desde afuera (JSON)**

```kotlin
package view
import kotlinx.serialization.Serializable
import model.Cursada

@Serializable data class InscribirRequest(val legajo: String, val nombre: String)
@Serializable data class CursadaResponse(val id: String, val materia: String, val anotados: Int, val cupo: Int)
@Serializable data class ErrorResponse(val error: String)

object CursadaView {
    fun aResponse(c: Cursada) = CursadaResponse(c.id, c.materia, c.inscriptos.size, c.cupo)
}
```

---

<!-- _class: dense -->

## Ejemplo completo en código (2/2): `Controller`, `Routes` y `Server`

**3. `controller/CursadaController.kt` — El orquestador**

```kotlin
class CursadaController(private val repo: CursadaRepository) {
    suspend fun obtener(call: ApplicationCall) {
        val cursada = repo.buscar(call.parameters["id"].orEmpty())
            ?: return call.respond(HttpStatusCode.NotFound, ErrorResponse("Cursada no encontrada"))
        call.respond(HttpStatusCode.OK, CursadaView.aResponse(cursada))
    }

    suspend fun inscribir(call: ApplicationCall) {
        val cursada = repo.buscar(call.parameters["id"].orEmpty())
            ?: return call.respond(HttpStatusCode.NotFound, ErrorResponse("Cursada no encontrada"))
        val req = call.receive<InscribirRequest>()
        try {
            cursada.inscribir(Alumno(req.legajo, req.nombre))          // delega en el Model
            call.respond(HttpStatusCode.Created, CursadaView.aResponse(cursada)) // responde con View
        } catch (e: IllegalStateException) {
            call.respond(HttpStatusCode.BadRequest, ErrorResponse(e.message ?: "Error de negocio"))
        }
    }
}
```

**4. `routes/CursadaRoutes.kt` y 5. `Server.kt` — Mapa de rutas y arranque**

```kotlin
fun Route.cursadaRoutes(controller: CursadaController) {
    route("/cursadas/{id}") {
        get { controller.obtener(call) }
        post("/inscribir") { controller.inscribir(call) }
    }
}

fun main() {
    val controller = CursadaController(CursadaRepository())
    embeddedServer(Netty, port = 8080) {
        install(ContentNegotiation) { json() }
        routing { cursadaRoutes(controller) }
    }.start(wait = true)
}
```

---

## Por qué les pedimos esta estructura en el MVP

Seguir **MVC + Rutas + Server** les resuelve tres problemas reales desde el día uno:

1. **Cada archivo tiene un único propósito**:
   - ¿Falló una regla de negocio? Mirás `model/`.
   - ¿Cambió el nombre de un campo en el JSON? Mirás `view/`.
   - ¿Cambió el código de estado HTTP? Mirás `controller/`.
   - ¿Cambió la URL? Mirás `routes/`.
2. **Los tests del modelo siguen siendo simples y rápidos**: probás `Cursada` con JUnit sin levantar Ktor ni puertos de red.
3. **La IA trabaja mucho mejor**: cuando le das una estructura clara de carpetas, no mezcla todo en un único archivo gigante.

---

<!-- _class: section -->
<!-- _paginate: false -->

# Generar código de Ktor con IA

## Seguir convenciones y cuidar las dependencias

---

## Qué suele salir mal al pedirle Ktor a una IA

Si le decís a una IA *"haceme una API en Ktor para mi MVP"* sin contexto ni límites, suelen pasar tres cosas:

1. **Mezcla versiones de Ktor**: combina código de Ktor 1.x, 2.x y 3.x (cambiaron paquetes e imports) y el proyecto no compila.
2. **Inventa dependencias sin revisar**: agrega al `build.gradle.kts` librerías que no necesitamos ni vimos (Koin, Exposed, Hikari, Jackson, StatusPages, CORS) o versiones incompatibles con tu Kotlin.
3. **Ignora tu diseño**: mete las rutas, el estado en variables globales y la lógica de negocio duplicada adentro de `Application.kt`, tirando a la basura el modelo que ya habías construido.

> La IA conoce muchos tutoriales distintos de Ktor; si no le marcás **la convención de tu proyecto**, elige una al azar.

---

## Tres reglas de oro al usar IA con el framework

1. **Respetar la convención del proyecto (`model`, `view`, `controller`, `routes`, `Server.kt`)**:
   - Pedile explícitamente en qué paquete y archivo va cada pieza.
2. **Prohibido tocar `build.gradle.kts` sin revisar**:
   - La plantilla del curso ya trae Ktor y `kotlinx.serialization` configurados.
   - Si la IA propone agregar una dependencia nueva, **frená y revisá**: casi seguro se resuelve con lo que ya está instalado.
3. **Reutilizar el modelo existente, nunca duplicarlo**:
   - Pasale a la IA las firmas de tus clases de `model/` y exigí que el controlador **sólo las llame**, sin reescribir validaciones ni cálculos.

---

<!-- _class: compact -->

## Cómo pedírselo a la IA: un buen prompt

```text
Contexto:
  Proyecto Kotlin con Ktor ya configurado en build.gradle.kts.
  Usamos esta estructura obligatoria:
  - model/: dominio puro (ya existe Cursada.kt y CursadaRepository.kt)
  - view/: DTOs con @Serializable y funciones de mapeo
  - controller/: recibe ApplicationCall, llama a model/ y responde con view/
  - routes/: extensiones de Route que delegan en el controller
  - Server.kt: configura ContentNegotiation(json) y registra las rutas

Objetivo:
  Creá CursadaView.kt, CursadaController.kt y CursadaRoutes.kt para exponer:
  - GET /cursadas/{id}
  - POST /cursadas/{id}/inscribir

Restricciones:
  - NO modifiques build.gradle.kts ni agregues dependencias nuevas.
  - NO modifiques las clases de model/ ni dupliques sus validaciones en el controller.
  - No pongas lógica dentro de routes/: la ruta sólo debe llamar al controller.
```

---

## Checklist para revisar código de Ktor generado por IA

Antes de dar por bueno el cambio (y guardarlo en `prompt-log/`), verificá:

- [ ] **¿Tocó `build.gradle.kts`?** Si agregó dependencias no pedidas, revertilo.
- [ ] **¿`model/` sigue limpio?** Confirmá que ningún archivo de `model/` tenga `import io.ktor.*`.
- [ ] **¿Las rutas son de una línea?** `routes/` sólo define el verbo, la URL y llama a `controller.metodo(call)`.
- [ ] **¿El controlador sólo orquesta?** Si hay cálculos o `if` de negocio en `controller/`, movelos al `model/` con su test unitario.
- [ ] **¿Compila y pasan los tests?** Ejecutá `./gradlew test` y probá el endpoint levantando el servidor.

---

## Para llevarse

- Un **servicio HTTP** expone tu programa a la red mediante pares de **Request** (método, ruta, datos) y **Response** (código de estado, JSON).
- Un **framework** (como **Ktor**) maneja la infraestructura HTTP y llama a tu código cuando llega un pedido.
- La capa web es una **cáscara imperativa**: su rol es **orquestar** los objetos y funciones puras que ya construiste, **nunca duplicar lógica de negocio**.
- Organizá tu servicio con **MVC + Rutas + Server** (`model/`, `view/`, `controller/`, `routes/`, `Server.kt`) para que cada archivo tenga una responsabilidad clara.
- Con IA: exigí que **siga la convención del proyecto** y **no aceptes dependencias nuevas** en `build.gradle.kts` sin revisar.

> Tu MVP vive en el `model`; Ktor es sólo el puente prolijo para que el mundo pueda usarlo.
