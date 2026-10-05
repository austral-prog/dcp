---
marp: true
theme: dcp
paginate: true
title: Clase 2 - Testing como contrato - TDD introductorio
---

<!-- _class: cover -->
<!-- _paginate: false -->

# Testing como contrato

## TDD introductorio y framework de testing en Kotlin

**Clase teorica 2 · DCP**

---

## Objetivo de la clase

La IA puede generar codigo que *parece* correcto. ¿Como sabemos que **lo es**? Con tests cuyo criterio ponemos nosotros.

Hoy vamos a:

- Entender los tests como **especificacion** del comportamiento esperado.
- Aplicar el ciclo **rojo → verde → refactor** (TDD).
- Elegir y usar un **framework de testing en Kotlin**.
- Pedirle tests a la IA **sin ceder el criterio propio**.
- Trabajar con **ramas, pull requests y revision de codigo**.

> El hilo sigue siendo tu MVP: hoy definimos cuando decimos que "funciona".

---

## Recorrido de esta clase

1. Testing como contrato: especificar antes de implementar.
2. TDD: el ciclo rojo-verde-refactor.
3. Framework de testing en Kotlin: Kotest vs JUnit5.
4. Tests con IA: casos felices, borde e invalidos.
5. Control de versiones: ramas, PRs y revision de codigo.

---

<!-- _class: section -->
<!-- _paginate: false -->

# Testing como contrato

## Especificar el comportamiento esperado

---

## Un test es una especificacion ejecutable

Un **contrato** dice: *"dada esta entrada, el programa promete esta salida"*.

- Describe **que** debe hacer el codigo, no **como**.
- Es **ejecutable**: se verifica en segundos, cuantas veces haga falta.
- No se desactualiza como un documento: si deja de cumplirse, **falla**.
- Es la **definicion de "terminado"** de una funcionalidad.

> Si no podes escribir el test, todavia no sabes bien que tenes que construir.

---

## Ejemplo de contrato

Requisito: *"El descuento debe estar entre 0% y 100%."*

| Entrada | Salida esperada |
|---|---|
| `aplicarDescuento(200.0, 25)` | `150.0` |
| `aplicarDescuento(200.0, 0)` | `200.0` |
| `aplicarDescuento(200.0, 100)` | `0.0` |
| `aplicarDescuento(200.0, 150)` | error: porcentaje invalido |
| `aplicarDescuento(200.0, -5)` | error: porcentaje invalido |

Antes de escribir una linea de implementacion, **ya sabemos que significa "correcto"**.

---

## Por que importa con IA

- La IA **no conoce tu intencion**: solo ve tu prompt.
- El test es la forma **no ambigua** de comunicar lo que queres.
- Permite **verificar** codigo generado sin leerlo linea por linea.
- Da **red de seguridad** para refactorizar sin romper.

> Codigo generado sin tests es codigo **no verificado**.

---

<!-- _class: section -->
<!-- _paginate: false -->

# TDD

## Rojo · Verde · Refactor

---

## El ciclo TDD

1. 🔴 **Rojo**: escribi **un** test que falle (la funcionalidad todavia no existe).
2. 🟢 **Verde**: escribi el codigo **mas simple** que lo haga pasar.
3. 🔵 **Refactor**: mejora el codigo (nombres, duplicacion) **con los tests en verde**.

Despues se repite con el siguiente comportamiento.

> Ciclos **cortos**: minutos, no horas.

---

## Reglas de oro del ciclo

- **No escribas codigo de produccion sin un test rojo que lo pida.**
- Un test nuevo por vez: **un comportamiento, un test**.
- Verifica que el test **falle por la razon correcta** (si nunca fue rojo, no prueba nada).
- En verde, hace lo **minimo**: no anticipes funcionalidad.
- Refactoriza solo con todo en verde y **corre los tests despues de cada cambio**.

---

## TDD paso a paso: ejemplo

**🔴 Rojo** — el test viene primero (ni siquiera compila):

```kotlin
@Test
fun `descuento del 25 por ciento sobre 200 da 150`() {
    assertEquals(150.0, aplicarDescuento(200.0, 25))
}
```

**🟢 Verde** — lo minimo para pasar:

```kotlin
fun aplicarDescuento(precio: Double, porcentaje: Int): Double =
    precio - precio * porcentaje / 100
```

**🔵 Refactor** — nombres claros, sin duplicacion. Luego, **siguiente test**.

---

## Segundo ciclo: el caso invalido

**🔴 Rojo**

```kotlin
@Test
fun `porcentaje mayor a 100 es invalido`() {
    assertFailsWith<IllegalArgumentException> {
        aplicarDescuento(200.0, 150)
    }
}
```

**🟢 Verde**

```kotlin
fun aplicarDescuento(precio: Double, porcentaje: Int): Double {
    require(porcentaje in 0..100) { "Porcentaje invalido: $porcentaje" }
    return precio - precio * porcentaje / 100
}
```

El test **guio** el diseño: aparecio la validacion porque la especificamos.

---

## Beneficios y limites de TDD

| Beneficios | Limites |
|---|---|
| Diseño guiado por el uso | Requiere practica y disciplina |
| Codigo testeable por construccion | Tests malos dan falsa confianza |
| Refactor sin miedo | No reemplaza pruebas de integracion |
| Menos bugs y regresiones | Mantener tests tiene costo |

> Cobertura alta **no** es lo mismo que tests buenos: importa **que** se verifica.

---

<!-- _class: section -->
<!-- _paginate: false -->

# Framework de testing en Kotlin

## Kotest vs JUnit5

---

## Opciones: JUnit5 (el del curso) y Kotest

| | **JUnit5** | **Kotest** |
|---|---|---|
| Estilo | Clases y metodos con `@Test` | Specs: `FunSpec`, `StringSpec`, `BehaviorSpec` |
| Aserciones | `assertEquals`, `assertThrows` | `shouldBe`, `shouldThrow` |
| Nombres | Funciones con backticks | Strings descriptivos |
| Ecosistema | Estandar de Java, soporte universal | Pensado para Kotlin |
| Extras | Se complementa con librerias | Property testing y data-driven incluidos |

Ambos corren con Gradle (`./gradlew test`) y se integran con IntelliJ.

---

## Mismo test, dos estilos

**JUnit5**

```kotlin
class DescuentoTest {
    @Test
    fun `porcentaje invalido lanza error`() {
        assertThrows<IllegalArgumentException> {
            aplicarDescuento(200.0, 150)
        }
    }
}
```

**Kotest**

```kotlin
class DescuentoTest : FunSpec({
    test("porcentaje invalido lanza error") {
        shouldThrow<IllegalArgumentException> {
            aplicarDescuento(200.0, 150)
        }
    }
})
```

---

## Framework del curso: JUnit5

En todo el curso usamos **JUnit5**, para que todos hablemos el mismo idioma:

- Es el **estandar de la industria** en JVM.
- Hay mucha **documentacion y ejemplos**, y la IA genera buen codigo con JUnit5.
- Se integra sin friccion con **Gradle e IntelliJ**.

**Kotest** lo vemos solo como referencia: existen otras opciones y conviene saber que hay alternativas, pero **no lo usamos** en el curso.

> Lo importante no es la herramienta: es **el contrato y el ciclo**.

---

<!-- _class: section -->
<!-- _paginate: false -->

# Tests con IA

## Pedirle tests sin ceder el criterio propio

---

## El riesgo: delegar el criterio

Si le pedis *"escribi los tests de esta funcion"* sobre **codigo ya escrito**:

- La IA testea **lo que el codigo hace**, no **lo que deberia hacer**.
- Si el codigo tiene un bug, **el test lo consagra**.
- Suele cubrir solo **casos felices**.
- Genera muchos tests que **dan sensacion de cobertura** pero verifican poco.

> El criterio sobre **que es correcto** es tuyo. La IA ayuda a **escribir y ampliar**, no a decidir.

---

## Tres familias de casos

| Tipo | Pregunta | Ejemplo (`aplicarDescuento`) |
|---|---|---|
| ✅ **Feliz** | ¿Funciona en el uso normal? | 25% sobre 200 → 150 |
| ⚠️ **Borde** | ¿Y en los limites? | 0%, 100%, precio 0 |
| ❌ **Invalido** | ¿Rechaza lo que no debe aceptar? | -5%, 150%, precio negativo |

Los **bugs viven en los bordes y en lo invalido**: ahi hay que mirar con mas atencion.

---

## Flujo recomendado con IA

1. **Vos** escribis la especificacion.
2. **Vos** listas los casos que se te ocurren: felices, borde e invalidos.
3. La IA **propone casos adicionales** que se te pasaron.
4. **Revisas** cada caso contra el contrato: descartas o corregis.
5. La IA **escribe los tests**; vos los corres y verificas que **esten en rojo**.
6. Recien ahi se implementa hasta llegar a verde.

> Primero el contrato, despues los tests, **al final** el codigo.

---

## Ejemplo de prompt

```text
Estoy aplicando TDD en Kotlin con JUnit5.
Funcion: aplicarDescuento(precio: Double, porcentaje: Int): Double

Contrato:
- porcentaje en 0..100, si no lanza IllegalArgumentException
- precio no puede ser negativo
- resultado = precio - precio * porcentaje / 100

NO escribas la implementacion.
Escribi solo los tests, agrupados en: casos felices,
casos borde y casos invalidos.
Despues, listame casos que yo no haya considerado.
```

---

## Checklist para revisar tests de la IA

- ¿Cada test verifica **un solo comportamiento** con nombre claro?
- ¿Los valores esperados salen **del contrato**, no de lo que "da el codigo"?
- ¿Estan los **bordes** y los **invalidos**?
- ¿Hay tests **redundantes** o que no verifican nada?
- ¿Probaste que **fallan** cuando el codigo esta mal?

> Un test que no podria fallar no te protege de nada.

---

<!-- _class: section -->
<!-- _paginate: false -->

# Control de versiones

## Buenas practicas para el alumno

---

## Ramas

- `main` siempre **funciona** y tiene los tests en verde.
- Una **rama por tarea**: `feature/descuento`, `fix/validacion-monto`.
- Ramas **cortas** y cambios chicos, faciles de revisar.
- **Commits chicos y descriptivos**: "Agrega test de porcentaje invalido".
- Con TDD: un commit por ciclo.

```sh
git switch -c feature/descuento
git add . && git commit -m "Agrega validacion de porcentaje"
git push -u origin feature/descuento
```

---

## Pull requests

Un **PR** es la propuesta de integrar tu rama a `main`.

- Titulo y descripcion: **que** cambia y **por que**.
- Cambios **acotados**: un PR, un objetivo.
- Los **tests pasan** antes de pedir revision.
- Si usaste IA, **decilo** y explica que revisaste.

> El PR es tu **evidencia de autoria**: muestra el proceso, no solo el resultado.

---

## Revision de codigo

**Como autor**

- Relee tu propio diff antes de pedir revision.
- Estate listo para explicar cada decision, incluso el codigo generado por IA.

**Como revisor**

- ¿Hay tests?, ¿cubren borde e invalidos?, ¿se entiende?
- Comenta de forma **concreta y respetuosa**.
- Revisa el **comportamiento**, no solo el estilo.

> Revisar codigo de la IA es lo mismo que revisar el de un companero: no confies, **verifica**.

---

## Para llevarse

- Un test es un **contrato**: define que significa "correcto".
- **TDD**: 🔴 rojo → 🟢 verde → 🔵 refactor, en ciclos cortos.
- Usamos **JUnit5**; Kotest es otra opcion, pero lo clave es **el ciclo**.
- A la IA le das el **contrato**; ella propone, **vos decidis**.
- **Ramas + PR + revision** para integrar con confianza.

> La IA escribe rapido. **Vos garantizas que sea correcto.**
