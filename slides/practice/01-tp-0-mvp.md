---
marp: true
theme: dcp
paginate: true
title: TP 0 - Definicion del MVP
---

<!-- _class: cover -->
<!-- _paginate: false -->

# TP 0

## Definicion del MVP

**Trabajo practico · DCP**

---

## Objetivo del TP

Entra al assignment, crea tu repo, pensa tu MVP, escribi su definicion en el `README` y pushea.

Entrega: el **nombre** y la **definicion** del MVP en el `README`, en la rama principal.

> Al terminar, tu repo tiene el MVP definido en el `README`, pusheado a la rama principal.

---

<!-- _class: compact -->

## Paso 1 — Entra al assignment y crea tu repo

Acepta el assignment **TP-0 MVP**. Se crea tu repo privado desde `austral-prog/dcp-mvp-template`.

```text
https://classroom50.org/austral-prog/dcp/assignments/tp-0-mvp/accept
```

Alternativa CLI. El flujo completo esta en la presentacion de instalacion del entorno:

```bash
gh student accept austral-prog dcp tp-0-mvp
```

Resultado: tu repo privado, Kotlin / JVM 21, Gradle Wrapper.

---

## Paso 2 — Clona tu repo

Clona la URL que te da Classroom 50 y entra a la carpeta.

```bash
git clone <url-del-repositorio>
cd <carpeta-del-assignment>
```

Un solo clone. Trabaja sobre ese repo. No hagas fork ni crees otro repositorio.

---

## Paso 3 — Pensa tu MVP

Decide esto antes de escribir:

- **Problema**: uno real de tu disciplina, resoluble con una app simple.
- **Usuario y valor**: quien lo usa y que recibe.
- **Scope**: que entra y que queda afuera.

Cerralo con el profesor. El concepto y los criterios estan en la presentacion de MVP.

---

## Paso 4 — Escribi la definicion en el README

Completa las tres secciones del `README`, con esos nombres exactos. Reemplaza los placeholders.

1. **Nombre del MVP**
2. **Problema, usuario y valor** — un parrafo
3. **Scope** — in / out

No dejes texto de plantilla. La definicion tiene que leerse completa.

---

## Paso 5 — Commitea y pushea

Pushea el nombre y la definicion a la rama principal.

```bash
git add README.md
git commit -m "Define el MVP: nombre, problema, usuario, valor y scope"
git push
```

Entregar es pushear a la rama principal. El flujo `gh student submit` y la nota se ven en la pauta de MVP.

---

<!-- _class: compact -->

## Checklist de entrega

- [ ] Aceptaste el TP-0 y tu repo existe en la organizacion.
- [ ] Clonaste el repo y trabajaste sobre el.
- [ ] Completaste **Nombre del MVP** en el `README`.
- [ ] Completaste **Problema, usuario y valor** (un parrafo).
- [ ] Completaste **Scope** (in / out).
- [ ] Commiteaste y pusheaste; los cambios estan en la rama principal.

```text
https://classroom50.org/austral-prog/dcp/assignments/tp-0-mvp/accept
```
