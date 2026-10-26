---
marp: true
theme: dcp
paginate: true
title: TP 5 - Abstracción con objetos y evaluación crítica de diseño OO
---

<!-- _class: cover -->
<!-- _paginate: false -->

# TP 5

## Abstracción con objetos y evaluación crítica de diseño OO

**Trabajo práctico · DCP**

---

## Objetivo del TP

En las clases anteriores trabajaste con modelado funcional (`data class`, inmutabilidad, `sealed class` y transformaciones de colecciones).

En este TP avanzamos a la **abstracción con objetos** y el **diseño orientado a objetos**:

1. **Objetos como unidades de responsabilidad:** encapsular estado y proteger invariantes (*tell, don't ask*).
2. **Polimorfismo e interfaces simples:** desacoplar llamadas concretas sin recurrir a `when` por tipo.
3. **Composición sobre herencia:** evitar jerarquías rígidas e infladas.
4. **Evaluación crítica del diseño propuesto por IA:** detectar sobre-diseño y podar clases accesorias.
5. **Puente al MVP:** aplicar estos criterios de diseño sobre el modelo central de tu propio proyecto.

---

## Estructura de la entrega

El TP 5 se compone de dos partes articuladas:

| Parte | Alcance | Dónde se entrega |
|---|---|---|
| **Parte A: Smart Home** | Implementación orientada a objetos en Kotlin con 100% de tests en verde | Repositorio individual en Classroom 50 (`tp-5-objetos`) |
| **Parte B: Puente al MVP** | Modelo de dominio con invariantes, encapsulamiento, polimorfismo justificado y tests | En el repositorio propio de tu MVP (`DISENO.md` + código) |

> **Plazo de entrega:** 1 semana, antes de la próxima clase práctica.

---

<!-- _class: section -->
<!-- _paginate: false -->

# Parte A: Sistema Smart Home

## Un dominio chico con desafíos de diseño reales

---

## El dominio: Domótica y Dispositivos Inteligentes

Un hogar inteligente necesita gestionar dispositivos heterogéneos, monitorear consumos y disparar acciones automáticas:

```text
                  ┌──────────────────────┐
                  │ interface Dispositivo│
                  └──────────▲───────────┘
                             │
       ┌─────────────────────┼─────────────────────┐
       │                     │                     │
┌──────────────┐     ┌───────────────┐     ┌───────────────┐
│LuzInteligente│     │   Termostato  │     │EnchufeIntelig.│
└──────────────┘     └───────────────┘     └───────────────┘
```

* **Interfaz común:** `id`, `nombre`, `encendido`, `encender()`, `apagar()`, `consumoActualWatts()`.
* **Polimorfismo:** `CasaInteligente` opera sobre colecciones de `Dispositivo` sin conocer sus tipos concretos.

---

## Encapsulamiento estricto (*Tell, don't ask*)

Cada clase es responsable de **cuidar sus propias reglas**:

* **`LuzInteligente`:**
  * Invariantes en constructor y `ajustarBrillo(0..100)`.
  * `nivelBrillo` con `private set`: ningún cliente externo puede saltear la validación.
  * Consumo apagada = `0.0`; encendida = proporcional al brillo.
* **`Termostato`:**
  * Rango de temperatura de confort (16°C a 32°C).
  * Histéresis para el compresor: en frío sólo arranca si supera `deseada + histeresis`.
  * Modos `FRIO`, `CALOR` y `VENTILACION`.
* **`EnchufeInteligente`:**
  * Control de carga conectada: si excede `potenciaMaximaSoportadaWatts`, corta por sobrecarga.
  * Invariante de seguridad: impide encender mientras continúe en sobrecarga.

---

## Composición sobre herencia

¿Cómo modelar sensores y automatizaciones?

> ❌ **El anti-patrón de herencia:**  
> `class LuzConSensorPresencia : LuzInteligente()`  
> `class TermostatoConSensorTemperatura : Termostato()`

¿Qué pasa si queremos una luz con sensor de presencia *y* de luminosidad? La jerarquía explota.

> ✅ **Composición limpia:**  
> Interfaz `Sensor` (`detectar(): Boolean`) + `ReglaAutomatizacion(sensor, accion)`.  
> La regla colabora con el dispositivo mediante una lambda, sin crear subclases.

---

<!-- _class: section -->
<!-- _paginate: false -->

# El sobre-diseño de la IA

## Cómo pedirle un diseño y cómo evaluarlo críticamente

---

## Las señales de alarma típicas en la IA

Cuando le pedís a un LLM *"Diseñame una arquitectura orientada a objetos para un Smart Home"*, tiende a devolver:

```text
abstract class AbstractBaseDevice
abstract class AbstractPowerConsumingDevice : AbstractBaseDevice
class SwitchableDeviceImpl : AbstractPowerConsumingDevice
class DeviceManagerFactory
interface IDeviceLifecycleHandlerRepository
class DeviceActionExecutorServiceHelper
```

**Señales de sobre-diseño:**
- 4 niveles de herencia abstracta antes de llegar a la primera clase útil.
- Interfaces con una única implementación (`IDeviceRepository` -> `DeviceRepositoryImpl`).
- Clases `Manager` o `Service` que despojan a los objetos de sus datos y métodos.
- Propiedades públicas mutables (`var`) sin validación.

---

<!-- _class: compact -->

## Guía de prompts para evaluar a tu agente

1. **Pedile un primer boceto:**
   * *"Proponé un modelo de clases e interfaces en Kotlin para un Smart Home con lámparas, termostatos y enchufes protegidos."*
2. **Cuestioná cada abstracción:**
   * *"¿Por qué creaste `AbstractPowerConsumingDevice` si solo tenemos 3 dispositivos concretos? Reemplazalo por la interfaz simple `Dispositivo`."*
   * *"No quiero clases `Manager` ni `Helper`. La propia clase `CasaInteligente` debe tener una lista de `Dispositivo` y calcular el consumo polimórficamente."*
3. **Exigile encapsulamiento real:**
   * *"Cambiá las propiedades públicas a `private set` y asegurá que `require` proteja las invariantes en el constructor."*

> Registrá todo el ida y vuelta en `prompt-log/`.

---

<!-- _class: section -->
<!-- _paginate: false -->

# Dinámica en clase

## Comparación de paradigmas: Objetos vs Funcional

---

## Ejercicio guiado en clase

1. **Implementamos un slice en vivo (POO):**
   * Dispositivo con estado mutable interno protegido y operaciones de negocio.
2. **Pedimos a la IA la traducción al paradigma funcional puro:**
   * Estado inmutable en `data class`, transiciones de estado como funciones puras `(Estado, Comando) -> Estado`.
3. **Debate y contraste en el pizarrón:**

| Dimensión | Enfoque con Objetos (POO) | Enfoque Funcional Puro (FP) |
|---|---|---|
| **¿Dónde vive el estado?** | Encapsulado dentro del objeto | En datos inmutables que fluyen |
| **Efectos colaterales** | Métodos mutan estado interno | Retorno de un nuevo estado copiado |
| **Testing** | Se testea el comportamiento y contrato | Se testean entradas y salidas puras |
| **Casos de conveniencia** | Sistemas con ciclo de vida e identidad | Transformación y pipelines de datos |

---

<!-- _class: section -->
<!-- _paginate: false -->

# Paso a paso del TP

## Cómo trabajar en Classroom 50

---

<!-- _class: compact -->

## Paso 1 — Aceptá el assignment

Aceptá el assignment **TP 5 - Objetos** en Classroom 50:

```text
https://classroom50.org/austral-prog/dcp/assignments/tp-5-objetos/accept
```

Alternativa CLI:

```bash
gh student accept austral-prog dcp tp-5-objetos
```

Se crea tu repositorio individual `austral-prog/dcp-tp-5-objetos-<tu-usuario>` con:
* Código de partida (`src/main/kotlin/com/university/dcp/smarthome/`).
* Suite de 23 tests automatizados (`src/test/kotlin/com/university/dcp/smarthome/`).
* Salida esperada de simulación de consola (`salida-esperada.txt`).

---

## Paso 2 — Ejecutá los tests

Cloná tu repo y corré los tests:

```bash
git clone git@github.com:austral-prog/dcp-tp-5-objetos-<tu-usuario>.git
cd dcp-tp-5-objetos-<tu-usuario>
./gradlew test
```

**Resultado inicial:**
* **5 tests pasan:** `ContratoSalidaTest`, toggle básico y validaciones iniciales.
* **18 tests fallan:** invariantes de brillo, histeresis de termostato, corte por sobrecarga, colecciones defensivas y reglas de automatización.

> **Puntaje inicial en Classroom 50:** 15 / 100 puntos.

---

## Autograder en Classroom 50

| Test Suite | Qué evalúa | Puntos | Inicial |
| :--- | :--- | :---: | :---: |
| `ContratoSalidaTest` | Salida de consola de la simulación base | **10 pts** | ✅ Pasa |
| `LuzInteligenteTest` | Encapsulamiento de brillo, invariantes y consumo proporcional | **20 pts** | ❌ Falla |
| `TermostatoTest` | Climatización, histeresis en frío/calor y consumos | **25 pts** | ❌ Falla |
| `EnchufeInteligenteTest` | Protección ante sobrecarga, rearmado condicional | **20 pts** | ❌ Falla |
| `CasaInteligenteTest` | Agregación, polimorfismo y reglas por composición | **25 pts** | ❌ Falla |
| **Total** | | **100 pts** | **15 pts** |

---

<!-- _class: section -->
<!-- _paginate: false -->

# Parte B: Puente al MVP

## Aplicá los conceptos a tu proyecto personal

---

## Consigna para tu MVP

En el repositorio individual de tu MVP:

1. **Revisión del modelo de clases (`DISENO.md`):**
   * Actualizá el diagrama Mermaid con las entidades reales de tu solución.
   * Evitá "clases bolsa" (`Manager`, `Utils`, `Handler`).
2. **Encapsulamiento e invariantes:**
   * Usá `private set` en propiedades mutables.
   * Agregá validaciones con `require` en los constructores.
3. **Al menos una interfaz polimórfica:**
   * Diseñá un contrato simple con al menos dos implementaciones (o una real y una en memoria para tests).
   * Ejemplos: pasarela de cobro, notificador, motor de reglas, cálculo de recargos.
4. **Justificación escrita (1 párrafo por entidad):**
   * *"¿Qué decide cada objeto y por qué no lo delega a otro?"*.
5. **Tests:** al menos 3 tests unitarios verificando reglas de negocio del modelo.

---

<!-- _class: compact -->

## Checklist de entrega

- [ ] Aceptaste el assignment del TP 5 y clonaste tu repositorio.
- [ ] `./gradlew test` da **23 passed, 0 failed** en la rama principal.
- [ ] No modificaste ningún archivo bajo `src/test/` ni `salida-esperada.txt`.
- [ ] Las clases protegen su estado (`private set`) y sus invariantes con `require`.
- [ ] No hay jerarquías de herencia artificiales ni sobre-diseño.
- [ ] `prompt-log/` registra la interacción con la IA y la discusión del diseño.
- [ ] **Parte B:** Tu repositorio de MVP contiene el diagrama actualizado, la justificación de responsabilidades y tests unitarios en verde.

---

## Cómo se evalúa

| Criterio | Qué miramos |
|---|---|
| **Autograder Smart Home** | 100/100 en los tests automáticos de Classroom 50 |
| **Calidad de diseño OO** | Responsabilidad única, cohesión, invariantes y encapsulamiento |
| **Composición sobre herencia** | Uso correcto de interfaces y sensores sin subclases rígidas |
| **Evaluación crítica (Prompt Log)** | Registro de cómo se guió a la IA evitando el sobre-diseño |
| **Aplicación al MVP** | Modelo consistente con invariantes y justificación de decisiones |
