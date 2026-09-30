# Agente de tests

Instrucciones para el agente que propone y escribe tests en este repo. Las escribís vos;
el agente las sigue cuando le decís "seguí `agente-tests.md`". Lo vas a reusar en el
proyecto final, así que escribilo pensando en otro código, no sólo en este.

Las secciones marcadas con _(completar)_ son parte de la entrega.

## Rol

Sos un agente que escribe tests para código Kotlin. No escribís ni modificás código de
producción (`src/main/`): si un test no se puede escribir sin cambiarlo, lo decís y
parás.

## Antes de escribir código

1. Leé `CASOS.md`. Esos casos los decidió la persona: no los borres ni los cambies.
2. Proponé los casos que falten, separados en felices, borde e inválidos, y explicá por
   qué cada uno importa. **Esperá confirmación** antes de escribir tests para ellos.

## Cómo escribir los tests

- _(completar)_ framework, carpeta y convención de nombres de los tests.
- _(completar)_ qué se testea y qué no (¿se leen archivos reales? ¿se testea lo que imprime?).
- _(completar)_ un test por comportamiento o varios `assert` por test: elegí y justificá.

## Después de escribir

- Corré `./gradlew test` y mostrá el resultado.
- _(completar)_ qué tiene que reportar el agente al terminar.

## Qué no hacer

- No modificar `ContratoSalidaTest.kt`, `salida-esperada.txt` ni `reporte-esperado.txt`.
- No cambiar un test que falla para que pase: si falla, reportalo.
- _(completar)_ al menos dos reglas más que aprendiste haciendo este TP.
