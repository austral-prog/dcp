---
marp: true
theme: dcp
paginate: true
title: Clase 2 - Planificar vs construir con IA, diagramas y agentes
---

<!-- _class: cover -->
<!-- _paginate: false -->

# Planificar vs construir con IA

## Modelado inicial, diagramas y agentes / subagentes

**Clase teorica 2 · DCP**

---

## Objetivo de la clase

Antes de pedirle codigo a una IA hay un paso que casi siempre nos salteamos: **pensar el problema**.

Hoy vamos a:

- Separar **planificar con IA** de **construir con IA** y ver por que conviene diseñar antes de generar.
- Identificar **componentes y responsabilidades** de un sistema chico.
- Modelar con **diagramas simples** (caja / flecha) y una pizca de UML.
- Escribir un buen **prompt de planificacion**.
- Entender que es un **agente** y un **subagente** en opencode, y como planificar con ellos.

> El hilo sigue siendo tu MVP: hoy pasamos de la idea al modelo.

---

## Recorrido de esta clase

1. Dos formas de usar la IA: planificar y construir.
2. Modelar antes de codificar: responsabilidades y limites.
3. Diagramas caja / flecha y una intro breve a UML.
4. Anatomia de un prompt de planificacion.
5. Agentes y subagentes en opencode.

La progresion sera **problema → responsabilidades → diagrama → prompt → codigo**.

---

<!-- _class: section -->
<!-- _paginate: false -->

# Planificar vs construir con IA

## Dos modos distintos de trabajar con el modelo

---

## El problema de pedir codigo a ciegas

Es tentador abrir la IA y escribir *"haceme una app de X"*. Lo que suele pasar:

- Genera algo que **funciona en la demo** pero no resiste casos limite.
- Toma decisiones de diseño **por vos**, sin que las veas.
- Cuando algo falla, no sabes **por que** esta hecho asi.
- Cada cambio pedido descoloca otra parte: no hay un modelo detras.

> Si no entendes el diseño, no podes defenderlo ni cambiarlo. Y en la materia se evalua que puedas.

---

## Dos modos de usar la IA

La misma herramienta sirve para dos cosas muy distintas. El error es mezclarlas.

| Planificar con IA | Construir con IA |
|---|---|
| Explorar el problema y el dominio | Generar codigo concreto |
| Comparar alternativas de diseño | Implementar una decision ya tomada |
| Buscar responsabilidades y limites | Escribir funciones, clases, tests |
| Salida: texto, diagramas, decisiones | Salida: archivos que compilan |
| Vos decidis; la IA propone | Vos revisas; la IA ejecuta |

> Primero cerras **que** vas a construir y **por que**. Recien despues pedis el **como**.

---

## Por que diseñar antes de generar

- **Barato equivocarse**: cambiar un diagrama cuesta minutos; refactorizar codigo, horas.
- **Decisiones visibles**: si el diseño esta escrito, se puede discutir y defender.
- **Prompts mejores**: cuando sabes que queres, el prompt de construccion es preciso.
- **Menos deuda**: no generas de mas ni features "que quedarian buenas".
- **Autoria real**: el modelo es tuyo aunque la IA ayude a redactarlo.

> Planificar no es perder tiempo antes de programar: es la parte del trabajo que decide si el codigo va a servir.

---

<!-- _class: section -->
<!-- _paginate: false -->

# Modelar antes de codificar

## Componentes, responsabilidades y limites

---

## Que es una responsabilidad

> **Una responsabilidad es una razon para que una parte del sistema exista y cambie.**

Un sistema chico bien diseñado se parte en piezas donde **cada una hace una cosa** y se sabe **quien hace que**.

Ejemplo — una app de gastos:

- Leer lo que el usuario ingresa.
- Validar que el monto sea valido.
- Guardar y recuperar los gastos.
- Calcular totales y estadisticas.
- Mostrar los resultados.

Cinco responsabilidades, no un solo bloque que "hace todo".

---

## Como identificar responsabilidades

Preguntas para hacerte (o para preguntarle a la IA) sobre tu MVP:

- ¿Que **datos** entran, se transforman y salen?
- ¿Que **decisiones** o **reglas** toma el sistema?
- ¿Que cosas cambian **por motivos distintos**? Esas van separadas.
- ¿Que pasaria si cambia la interfaz? ¿Y si cambia el almacenamiento?
- ¿Que parte podria **testearse sola**?

> Si una pieza cambia por dos motivos que no tienen que ver entre si, probablemente sean dos responsabilidades.

---

## Diagramas caja / flecha

La forma mas simple de modelar antes de codificar:

- **Una caja** por responsabilidad.
- **Una flecha** por cada dato que pasa de una caja a otra.

```text
  [entrada] -> [validacion] -> [almacenamiento] -> [estadisticas] -> [pantalla]
                    |                                     |
                    v                                     v
                [errores]                            [reporte.txt]
```

Se lee en voz alta: *"la entrada pasa a validacion, que guarda en almacenamiento; estadisticas lee de ahi y muestra en pantalla y en un reporte"*.

> Si no podes dibujar el flujo, todavia no entendes el problema.

---

## Una pizca de UML: cuando ayuda

No necesitas UML completo. Alcanza con dos diagramas cuando el caja / flecha se queda corto:

| Diagrama | Responde | Cuando usarlo |
|---|---|---|
| **De clases** | ¿Que entidades hay y como se relacionan? | Al modelar el dominio |
| **De secuencia** | ¿En que orden pasan las cosas? | Al modelar un flujo con varios pasos |

```text
Gasto                 Registro
+ categoria: String   + agregar(g: Gasto)
+ monto: Double       + total(): Double
                      + porCategoria(): Map
```

> UML es una herramienta de comunicacion, no un tramite. Usa el minimo que aclare la idea.

---

<!-- _class: section -->
<!-- _paginate: false -->

# Prompts de planificacion

## Como pedirle a la IA que piense con vos

---

## Anatomia de un buen prompt de planificacion

Un prompt de planificacion no pide codigo: pide **pensar el problema**. Tres partes:

1. **Contexto** — que estas construyendo, para quien, con que tecnologia.
2. **Restricciones** — que si, que no, limites de scope, tiempo, stack.
3. **Formato de salida esperado** — lista de responsabilidades, tabla comparativa, diagrama en texto, etc.

> Un buen prompt de planificacion es reproducible: otra persona con el mismo prompt obtiene una respuesta util.

---

<!-- _class: compact -->

## Plantilla de prompt de planificacion

```text
Contexto:
  Estoy diseñando un MVP de <problema> para <usuario>.
  Va a estar en Kotlin/JVM, consola, sin base de datos externa.

Objetivo:
  Ayudame a PLANIFICAR, no a escribir codigo todavia.

Restricciones:
  - Alcanzable en un cuatrimestre.
  - Sin frameworks pesados.
  - Debe poder testearse.

Salida esperada:
  1. Lista de responsabilidades del sistema.
  2. 2-3 arquitecturas posibles, con pros y contras.
  3. Un diagrama caja/flecha en texto para cada una.
```

---

## Prompt pobre vs prompt de planificacion

| Prompt pobre | Prompt de planificacion |
|---|---|
| "Haceme una app de gastos en Kotlin" | Da contexto, usuario y stack |
| No dice restricciones | Acota scope, tiempo y tecnologia |
| No dice que espera de vuelta | Pide responsabilidades + alternativas + diagrama |
| Devuelve codigo que hay que aceptar o tirar | Devuelve material para **decidir** |

> El primero delega el diseño en la IA. El segundo lo mantiene en tus manos.

---

<!-- _class: section -->
<!-- _paginate: false -->

# Agentes y subagentes

## Planificar con opencode

---

## Que es un agente

En opencode (y en asistentes similares), un **agente** es la IA trabajando de forma autonoma sobre una tarea: lee, razona, usa herramientas y produce un resultado.

- Tiene un **objetivo** y un **contexto** (tu prompt, los archivos del repo).
- Puede **leer y editar** archivos, correr comandos, buscar en el codigo.
- Trabaja en un **bucle**: mira, decide, actua, vuelve a mirar.

> Un agente no es solo un chat: puede ejecutar pasos por su cuenta hasta cumplir el objetivo.

---

## Que es un subagente

Un **subagente** es un agente que el agente principal lanza para una **tarea acotada**, con su propio contexto.

- Sirve para **delegar** trabajo paralelo o de exploracion.
- Devuelve solo su **conclusion**, sin llenar el contexto principal de ruido.
- Ejemplos: "explora como esta organizado el repo", "buscame donde se valida la entrada".

| Agente principal | Subagente |
|---|---|
| Coordina la tarea grande | Resuelve una parte especifica |
| Mantiene el hilo general | Contexto propio y descartable |
| Decide y arma la respuesta | Explora y reporta |

---

## Como planificar con agentes

Un flujo recomendado para tu MVP:

1. **Modo plan primero**: pedile al agente que proponga un plan y responsabilidades, **sin tocar codigo**.
2. **Subagentes para explorar**: delega busquedas ("¿que patrones hay?", "¿que alternativas existen?").
3. **Revisas el plan**: ajustas, sacas lo que sobra, cerras el scope.
4. **Recien ahi construis**: el mismo agente implementa el plan que ya aprobaste.

> Separar plan de ejecucion con un agente es lo mismo que hicimos hoy: planificar antes de construir, pero automatizado.

---

## Buenas practicas con agentes

- Dale **contexto explicito**: que es el MVP, restricciones, formato de salida.
- Pedile el **plan antes** que el codigo, y leelo de verdad.
- Usa **subagentes** para explorar sin ensuciar el hilo principal.
- Guarda cada interaccion en el **prompt-log**: entra en la nota.
- No aceptes un plan que no entendes: si no lo podes explicar, no es tuyo.

> El agente acelera el trabajo; el criterio y la responsabilidad siguen siendo tuyos.

---

## Cierre y puente a la practica

Hoy vimos por que **planificar** antes de **construir**, como **modelar** con responsabilidades y diagramas, como escribir un **prompt de planificacion** y como usar **agentes y subagentes**.

En la practica vas a:

- Explorar **2-3 arquitecturas** de tu MVP con un chat de IA y compararlas.
- Escribir el **documento de diseño inicial** (1 pagina).
- Reflexionar sobre una **decision que cambio** al hablar con la IA.

> De la idea al modelo. El proximo paso es el codigo.
