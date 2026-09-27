---
marp: true
theme: dcp
paginate: true
title: Practica Clase 2 - Diseño inicial del MVP
---

<!-- _class: cover -->
<!-- _paginate: false -->

# Diseño inicial del MVP

## Explorar arquitecturas con IA y documentar el diseño

**Práctica Clase 2 · DCP**

---

## Objetivo de la práctica

Antes de escribir una línea de código de tu MVP, vas a **planificar su diseño** con ayuda de la IA.

Hoy vas a:

1. Usar un chat de IA para explorar **2-3 arquitecturas** posibles de tu MVP y compararlas.
2. Escribir un **documento de diseño inicial** de una página.
3. Redactar una **reflexión individual** sobre una decisión que cambió al hablar con la IA.

> Es el puente entre el MVP que definiste (TP 0) y el código que vas a escribir después.

---

## El entregable

Un **documento de diseño inicial** (una página) con tres secciones + una reflexión individual.

| Sección | Contenido |
|---|---|
| Responsabilidades | Las piezas del sistema y qué hace cada una |
| Historias de usuario | Lo mínimo que el usuario necesita poder hacer |
| Boceto de arquitectura | Un diagrama caja / flecha de la opción elegida |
| Reflexión (individual) | 5 líneas sobre una decisión que cambió con la IA |

> Todo lo relacionado con la IA (prompts y respuestas) va al `prompt-log/` desde el primer prompt.

---

<!-- _class: section -->
<!-- _paginate: false -->

# Parte 1

## Explorar arquitecturas con IA

---

## Paso 1 — Prepará el contexto de tu MVP

La IA solo ayuda si le das buen contexto. Antes de pedir nada, tené a mano:

- **Qué** resuelve tu MVP y **quién** lo usa (lo del `README` del TP 0).
- El **stack**: Kotlin / JVM, consola, sin servicios externos.
- Las **restricciones**: alcanzable en el cuatrimestre, testeable, sin frameworks pesados.

> Si no podés explicar tu MVP en dos frases, cerralo con el profesor antes de seguir.

---

<!-- _class: compact -->

## Paso 2 — Pedí 2-3 arquitecturas

Usá un prompt de **planificación**, no de construcción. Pedí alternativas, no código.

```text
Contexto:
  Estoy diseñando un MVP de <mi problema> para <mi usuario>.
  Kotlin/JVM, consola, sin base de datos externa.

Objetivo:
  Ayudame a PLANIFICAR. No escribas código todavía.

Salida esperada:
  - 2 o 3 arquitecturas posibles para este MVP.
  - Para cada una: responsabilidades, un diagrama caja/flecha
    en texto, y sus pros y contras.
  - Todo acotado a lo alcanzable en un cuatrimestre.
```

> Guardá este prompt y la respuesta en `prompt-log/`.

---

## Paso 3 — Compará las alternativas

No te quedes con la primera respuesta. Poné las opciones lado a lado con criterios claros:

| Criterio | Arq. A | Arq. B | Arq. C |
|---|---|---|---|
| Simplicidad | | | |
| Responsabilidades claras | | | |
| Fácil de testear | | | |
| Alcanzable en el cuatrimestre | | | |
| Fácil de extender | | | |

> La mejor arquitectura no es la más completa: es la más simple que resuelve tu problema.

---

## Paso 4 — Elegí y justificá

De la comparación sale **una** arquitectura elegida.

- Marcá cuál elegís y **por qué** (dos o tres razones).
- Anotá qué opciones descartaste y **qué te hizo descartarlas**.
- Si la IA propuso algo que no entendés, preguntale hasta entenderlo.

> Si no podés defender por qué elegiste esa arquitectura, todavía no la elegiste: la aceptaste.

---

<!-- _class: section -->
<!-- _paginate: false -->

# Parte 2

## El documento de diseño

---

## Sección 1 — Responsabilidades

Listá las piezas de tu MVP y qué hace cada una. Una línea por responsabilidad.

```text
- Entrada:        lee lo que ingresa el usuario
- Validación:     rechaza datos inválidos
- Almacenamiento: guarda y recupera los registros
- Cálculo:        produce totales y estadísticas
- Presentación:   muestra resultados al usuario
```

> Buscá que cada pieza cambie por **un solo** motivo. Si cambia por dos, probablemente sean dos.

---

## Sección 2 — Historias de usuario mínimas

Describí lo mínimo que el usuario necesita poder hacer. Un formato simple:

> **Como** `<tipo de usuario>`, **quiero** `<acción>`, **para** `<beneficio>`.

Ejemplos para una app de gastos:

- Como usuario, quiero registrar un gasto, para llevar la cuenta.
- Como usuario, quiero ver el total, para saber cuánto gasté.
- Como usuario, quiero ver el gasto por categoría, para entender en qué se me va.

> Mínimas de verdad: solo lo que el MVP necesita para entregar valor.

---

## Sección 3 — Boceto de arquitectura

Un diagrama caja / flecha de la arquitectura que elegiste. Una caja por responsabilidad, una flecha por dato.

```text
  [entrada] -> [validacion] -> [almacenamiento] -> [calculo] -> [pantalla]
                    |
                    v
                [errores]
```

> Puede ser texto (como este) o una foto de un dibujo a mano. Lo importante es que se entienda el flujo.

---

<!-- _class: compact -->

## La reflexión individual (5 líneas)

Además del documento, cada alumno escribe una reflexión **individual** de cinco líneas:

> Contá **una decisión de diseño que cambió** después de hablar con la IA.
> ¿Qué pensabas al principio? ¿Qué te hizo cambiar? ¿Qué decidiste al final?

- Es individual: la escribís vos, con tus palabras.
- No es sobre la herramienta, es sobre **tu decisión**.
- Cinco líneas alcanzan: se busca criterio, no extensión.

---

## Checklist de entrega

Antes de dar por terminada la práctica, verificá:

- [ ] Exploraste **2-3 arquitecturas** con la IA y las comparaste.
- [ ] El documento de diseño tiene **responsabilidades**, **historias de usuario** y **boceto de arquitectura**.
- [ ] Entra en **una página**.
- [ ] Escribiste la **reflexión de 5 líneas** (individual).
- [ ] Cada prompt y respuesta quedó en `prompt-log/`, sin secretos.

---

## Dónde va y qué sigue

- El documento de diseño va en tu **repo del MVP** (por ejemplo `DISENO.md`, junto al `README`).
- Los prompts y respuestas, en `prompt-log/`.
- Pusheá todo a la rama principal.

**Próximo paso.** Con el diseño cerrado, empezás a **construir con IA**: prompts de construcción sobre el plan que ya aprobaste.

> De la idea al modelo, del modelo al código. Este documento es tu punto de partida.
