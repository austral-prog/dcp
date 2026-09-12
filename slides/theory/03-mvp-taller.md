---
marp: true
theme: dcp
paginate: true
title: Clase 1 - MVP: taller de ideacion y pauta del trabajo final
---

<!-- _class: cover -->
<!-- _paginate: false -->

# MVP

## Taller de ideacion y pauta del trabajo final

**Clase 1 · DCP**

---

## Objetivo de la clase

Hoy vamos a:

- entender que es un MVP, para que sirve y como se relaciona con DCP;
- idear un MVP propio a partir de un problema real de tu disciplina;
- scopearlo con el profesor hasta que sea alcanzable en la cursada;
- conocer la pauta y los criterios del trabajo final.

> El hilo de la materia es tu MVP: de la idea al modelo, del modelo al codigo.

---

## ¿Que es un MVP?

**Minimum Viable Product**: la version mas simple del producto que resuelve el problema central y entrega valor real.

> Un MVP sirve para validar una idea con el menor esfuerzo, aprender rapido y evitar construir de mas.

No es un prototipo a medias ni un trabajo mal hecho.

| No es | Si es |
|---|---|
| "lo que alcance a codear" | lo minimo que sirve |
| una demo decorativa | un producto usable |
| deuda tecnica a proposito | algo acotado y bien hecho |

---

## ¿Para que sirve?

- Validar el **problema** y la **solucion** con un recorte real.
- Enfocar el **scope**: que entra, que queda afuera y por que.
- Iterar con evidencia, no con suposiciones.
- Exponer el modelo temprano: entidades, reglas, estado e invariantes.
- Evitar construir de mas antes de saber si la idea vale.

> Menos superficie, mas aprendizaje. El recorte es parte del diseño.

---

## Relacion con la materia

El MVP es el hilo que une todo lo que enseña DCP. No es solo "hacer que ande": es el vehiculo para practicar diseño.

| Concepto de la materia | Como aparece en el MVP |
|---|---|
| modelar el problema antes de codificar | entidades, reglas y limites del dominio |
| datos, comportamiento y responsabilidades | diseño de clases y fronteras claras |
| estado e invariantes | el modelo no se rompe en casos limite |
| null-safety e inmutabilidad | Kotlin usado con intencion |
| codigo legible y comprobable | tests, README y decisiones explicables |

---

<!-- _class: section -->
<!-- _paginate: false -->

# Taller de ideacion

## De un problema real a un scope acordado

---

## La dinamica del taller

Cada alumno propone un problema **real** de su disciplina — Ing. Industrial, Ing. Informatica, Ciencias de Datos, Bioingenieria, etc. — resoluble con una app simple.

El profesor ayuda a **acotar** la idea para que sea alcanzable en la cursada.

1. Pensar un problema concreto de tu disciplina.
2. Conversarlo con el profesor.
3. Acordar el scope (in / out).
4. Registrarlo en el `README` del repo.

> La idea nace del alumno; el alcance se cierra con el profesor.

---

## ¿Que hace a un buen problema de MVP?

Checklist para traer a la conversacion:

- [ ] **Real y concreto**: ocurre en tu disciplina, no es un ejemplo de tutorial.
- [ ] **Usuario claro**: se sabe quien lo usa y que valor recibe.
- [ ] **Resoluble con una app simple**: no hace falta una plataforma enorme.
- [ ] **Modelable**: hay entidades, estado y reglas que se pueden diseñar.
- [ ] **Acotable a un cuatrimestre**: entra en el tiempo de la cursada.
- [ ] **Testeable**: se puede demostrar que funciona, no solo "se ve bien".

---

<!-- _class: section -->
<!-- _paginate: false -->

# Pauta del trabajo final

## Que se entrega y como se evalua

---

## El trabajo final de un vistazo

El MVP **es** el trabajo final de la materia. Entregables:

- el **proyecto**: codigo del MVP, que buildea y con tests;
- el **README** completo: nombre, problema / usuario / valor, y scope;
- el **prompt-log** completo, sin secretos;
- la **presentacion de MVP** y la demo.

La plantilla ya trae el esqueleto Kotlin y la politica de `prompt-log/`. El resto lo construis vos, con el scope acordado.

---

## Prompt log (obligatorio)

Cada interaccion con un LLM sobre el repo se guarda en `prompt-log/`: prompt de entrada + salida del modelo.

- Aplica a Claude, OpenCode, GPT, Copilot, Cursor y equivalentes.
- Sin API keys, tokens ni credenciales. Si aparecen, redactarlos.
- Es requisito de entrega **y** se evalua: forma parte de la nota.

La politica vive en `AGENTS.md`, `CLAUDE.md` y `.github/copilot-instructions.md`. El formato esta en `prompt-log/README.md`.

> Si usaste un LLM sobre el repo, va al log. Tambien las interacciones "chicas".

---

## La evaluacion

La evaluacion es en la **ultima semana de cursada**.

- Posiblemente el curso se divide en **2 grupos** para presentar en **2 dias** distintos.
- La presentacion es **individual**.
- Es la **unica evaluacion** de toda la cursada.
- Si se desaprueba, hay **recuperatorio**: correcciones de los profesores y/o agregado de scope si hace falta.

> Presentacion individual · unica evaluacion de la cursada.

---

<!-- _class: compact -->

## Criterios de evaluacion

Se evalua con estos cuatro criterios:

1. El **modelo** y la **resolucion** del MVP.
2. La **presentacion y demo** el dia de la entrega.
3. La **completitud del scope** acordado al dia de la entrega: si se pudo implementar todo lo pedido.
4. La entrega del **prompt log**: el `prompt-log` sera evaluado y es parte de la nota.

El scope que cuenta es el acordado con el profesor, no una lista improvisada el dia de la demo.

---

## Cierre y proximos pasos

1. Segui el **TP 0** para aceptar el assignment y crear tu repo.
2. Pensa un problema real de tu disciplina y cerra el scope con el profesor.
3. Escribi la definicion del MVP en el `README` y pushea (nombre + definicion).
4. Registrar desde el dia uno cada interaccion de IA en `prompt-log/`.

**Recursos.** Los pasos operativos estan en el **TP 0**. El flujo de Classroom 50 y `gh student` esta en la presentacion de instalacion del entorno. La plantilla y la politica de prompts estan en el repo que se crea al aceptar.

```text
https://classroom50.org/austral-prog/dcp/assignments/tp-0-mvp/accept
```
