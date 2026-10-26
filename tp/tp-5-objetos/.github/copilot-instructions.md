# Instrucciones de GitHub Copilot para DCP TP 5

Al generar o refactorizar código en este repositorio:
1. **No modifiques archivos bajo `src/test/` ni `salida-esperada.txt`.**
2. **Encapsulamiento estricto:** Ocultá el estado mutable con `private set` o estructuras inmutables.
3. **Invariantes protegidas:** Usá `require(...)` en constructores o métodos para evitar estados inválidos.
4. **Composición sobre herencia:** Preferí componer interfaces simples en lugar de herencia profunda.
5. **Tell, don't ask:** Los objetos deben encapsular la toma de decisiones sobre sus propios datos.
6. **No introduzcas sobre-diseño innecesario:** Evitá capas como `Manager`, `Helper`, `Factory` o jerarquías complejas si una clase o interfaz simple resuelve el problema.
