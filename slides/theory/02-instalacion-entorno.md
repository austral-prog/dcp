---
marp: true
theme: dcp
paginate: true
title: Clase 1 - Instalacion del entorno y Classroom 50
---

<!-- _class: cover -->
<!-- _paginate: false -->

# Entorno de trabajo

## Instalacion, verificacion y Classroom 50

**Kotlin · JDK 21 · IntelliJ IDEA · Git · GitHub · Classroom 50 · OpenCode**

---

## Objetivo de esta guia

Al terminar esta guia vas a poder:

- ejecutar Java 21 y un proyecto Kotlin con Gradle;
- abrir, ejecutar y probar el proyecto en IntelliJ IDEA;
- identificarte ante GitHub desde la terminal;
- unirte al classroom, aceptar un assignment y clonar tu repositorio;
- realizar un cambio, crear un commit y entregarlo con `gh student submit`;
- de forma opcional, verificarte como estudiante y usar GitHub Copilot gratis;
- iniciar OpenCode y conectarlo con un proveedor de IA.

> La instalacion termina cuando verificamos el flujo completo, no cuando desaparece el instalador.

---

## Mapa del entorno

| Herramienta | Responsabilidad |
|---|---|
| JDK 21 | compilar y ejecutar sobre la JVM |
| Kotlin | lenguaje utilizado en la materia |
| Gradle Wrapper | construir y probar con una version compartida |
| IntelliJ IDEA | editar, ejecutar, depurar y navegar el codigo |
| Git | registrar versiones localmente |
| GitHub | alojar el repositorio remoto de cada TP |
| Classroom 50 | aceptar assignments, entregar y ver la nota |
| OpenCode | asistir desde el contexto real del proyecto |

No instalaremos Gradle ni el compilador de Kotlin globalmente: el proyecto proveera el **Gradle Wrapper**.

---

<!-- _class: compact -->

## Antes de comenzar

Necesitamos:

- Windows 10/11 con PowerShell, macOS o Linux de 64 bits;
- permisos para instalar aplicaciones;
- al menos 10 GB libres y 8 GB de RAM recomendados;
- una cuenta personal de GitHub con el correo verificado;
- la invitacion de GitHub al classroom, cuando la catedra la envie;
- acceso a un proveedor compatible con OpenCode;
- navegador y conexion a internet.

Crea una carpeta de trabajo sin sincronizacion en nube, por ejemplo:

```text
Windows: C:\Users\TU_USUARIO\code
macOS:   /Users/TU_USUARIO/code
Linux:   /home/TU_USUARIO/code
```

---

<!-- _class: section -->
<!-- _paginate: false -->

# Paso 1

## Java 21 y Kotlin

---

<!-- _class: dense -->

## Instalar JDK 21 · Windows

Abri **PowerShell** y ejecuta:

```powershell
winget install EclipseAdoptium.Temurin.21.JDK
```

Cerra y volve a abrir PowerShell para actualizar el `PATH`.

Alternativa grafica: descarga **Temurin 21 (LTS)** desde Adoptium y elige el instalador `.msi` para la arquitectura correcta.

Verifica:

```powershell
java -version
javac -version
```

Ambos comandos deben informar una version que comience con `21`.

---

<!-- _class: compact -->

## Instalar JDK 21 · macOS

Con Homebrew:

```bash
brew install --cask temurin@21
```

Alternativa grafica: descarga **Temurin 21 (LTS)** desde Adoptium y ejecuta el instalador `.pkg` correspondiente a Intel o Apple Silicon.

Verifica:

```bash
java -version
javac -version
/usr/libexec/java_home -V
```

Si existe mas de un JDK, IntelliJ debera usar especificamente el JDK 21.

---

<!-- _class: dense -->

## Instalar JDK 21 · Linux

```bash
# Ubuntu / Debian
sudo apt update
sudo apt install openjdk-21-jdk

# Fedora
sudo dnf install java-21-openjdk-devel

# Verificar
java -version
javac -version
```

Si la distribucion no ofrece Java 21, instala Temurin 21 siguiendo la documentacion de Adoptium.

---

<!-- _class: dense -->

## ¿Donde se instala Kotlin?

El starter del assignment declara el plugin de Kotlin y el proyecto incluye `gradlew` (macOS/Linux), `gradlew.bat` (Windows) y `gradle/wrapper/` (version exacta de Gradle).

Al ejecutar el wrapper, Gradle descarga lo necesario para compilar Kotlin.

```text
# Windows
.\gradlew.bat --version

# macOS / Linux
./gradlew --version
```

> No uses una instalacion global de Gradle: el wrapper mantiene un entorno reproducible.

---

<!-- _class: section -->
<!-- _paginate: false -->

# Paso 2

## Git, GitHub e IntelliJ IDEA

---

<!-- _class: dense -->

## Instalar Git

```text
# Windows · PowerShell
winget install --id Git.Git -e --source winget

# macOS
xcode-select --install

# Ubuntu / Debian · Fedora
sudo apt update && sudo apt install git
sudo dnf install git
```

Cerra y abri la terminal; luego ejecuta `git --version`.

---

<!-- _class: compact -->

## Configurar la identidad de Git

Usa el nombre real y un correo asociado a la cuenta de GitHub:

```bash
git config --global user.name "Nombre Apellido"
git config --global user.email "correo@ejemplo.com"
git config --global init.defaultBranch main
```

Comproba la configuracion:

```bash
git config --global --list
```

Esta identidad queda guardada en los commits. **No coloques tokens ni contraseñas** en la configuracion o en archivos del proyecto.

---

<!-- _class: dense -->

## Instalar GitHub CLI

GitHub CLI simplifica la autenticacion, el clonado y Classroom 50.

```text
# Windows
winget install --id GitHub.cli

# macOS
brew install gh
```

**Linux:** instala `gh` desde el repositorio oficial indicado en <https://cli.github.com/>.

Verifica: `gh --version`.

`gh auth login` puede hacerse ahora o mas adelante: `gh student login` (Paso 4) configura los scopes que Classroom 50 necesita.

---

## Preparar la cuenta de GitHub

1. Inicia sesion en <https://github.com/>.
2. Verifica el correo de la cuenta.
3. Activa autenticacion de dos factores, 2FA.
4. Acepta la invitacion de GitHub al classroom, cuando la catedra la envie.
5. Configura una forma de recuperar la cuenta.
6. Ejecuta `gh --version` en la terminal.

Classroom 50 corre enteramente sobre GitHub. La invitacion llega por correo; tambien se puede aceptar desde classroom50.org o al abrir el link del assignment.

GitHub ya no acepta la contraseña de la cuenta para operaciones Git por HTTPS. No pegues tokens ni claves en comandos, `README` o Prompt Log.

---

## GitHub Copilot gratis para estudiantes

1. **Aplica a GitHub Education.** En <https://github.com/settings/education/benefits> pulsa **Start an application** (correo academico o constancia de alumno).
2. **Activa Copilot.** Cuando te aprueben, volve a <https://github.com/settings/education/benefits> y pulsa **Learn more**. Puede tardar unos dias; si solo ves opciones pagas, no compres: espera y reintenta.
3. **Usa Copilot en cualquier repo.** Con Copilot activo, podes usarlo en cualquier repositorio, incluidos los de Classroom 50.

> Opcional, pero muy recomendado: te deja usar Copilot Student sin pagar mientras GitHub te mantenga verificado.

---

## Instalar IntelliJ IDEA

Metodo recomendado para los tres sistemas:

1. Descarga **JetBrains Toolbox App** desde <https://www.jetbrains.com/toolbox/app/>.
2. Instala y abri Toolbox.
3. Instala la version estable de **IntelliJ IDEA**.
4. Inicia IntelliJ y conserva habilitado el plugin **Kotlin** incluido.

No hace falta una suscripcion para las funciones esenciales del curso.

En el primer proyecto comprobaremos:

```text
File → Project Structure → Project SDK → JDK 21
Settings → Build Tools → Gradle → Gradle JVM → JDK 21
```

---

<!-- _class: section -->
<!-- _paginate: false -->

# Paso 3

## OpenCode y proveedor de IA

---

<!-- _class: dense -->

## Instalar OpenCode · Windows

En esta materia usaremos **PowerShell nativo**. Una opcion reproducible es instalar Node.js LTS y luego OpenCode:

```powershell
winget install OpenJS.NodeJS.LTS
```

Cerra y abri PowerShell:

```powershell
node --version
npm install -g opencode-ai
opencode --version
```

Tambien existen instalaciones oficiales con Chocolatey, Scoop o binarios publicados en GitHub.

> La documentacion de OpenCode recomienda WSL para la experiencia mas completa, pero no es un requisito de esta cursada.

---

<!-- _class: dense -->

## Instalar OpenCode · macOS y Linux

```bash
# Metodo oficial
curl -fsSL https://opencode.ai/install | bash

# Alternativa con Homebrew
brew install anomalyco/tap/opencode

# Verificar (cerra y abri la terminal)
opencode --version
```

Si el comando no aparece, segui la indicacion del instalador para agregar OpenCode al `PATH`.

---

## Conectar un proveedor

OpenCode no incluye automaticamente acceso a un modelo. Vas a usar un proveedor compatible de tu eleccion.

1. Ejecuta `opencode`.
2. Escribi `/connect`.
3. Elegi el proveedor.
4. Segui el flujo de autenticacion o ingresa tu API key.
5. Selecciona un modelo disponible.

Las claves deben permanecer fuera de Git:

- no las escribas en el codigo, `README` o Prompt Log;
- no las agregues a `opencode.json` si el archivo se versiona;
- si una clave se publica, revocarla inmediatamente.

---

## Primera verificacion de OpenCode

En este paso solo se comprueba instalacion y conexion. La consulta sobre el proyecto Kotlin se hace en el Paso 4, una vez clonado el assignment.

```bash
opencode
```

Confirma que arranca y que un modelo esta disponible. Si falta el proveedor, ejecuta `/connect`.

Registra proveedor, prompt, respuesta, evaluacion y decision en el **Prompt Log**. Nunca incluyas la API key.

---

<!-- _class: section -->
<!-- _paginate: false -->

# Paso 4

## Classroom 50

---

## ¿Que es Classroom 50?

Los TPs se entregan con **Classroom 50**. Corre enteramente sobre GitHub: aceptar un assignment crea un repositorio privado; entregar es commitear, pushear y (cuando corresponda) ejecutar `gh student submit`.

Hay dos caminos equivalentes:

| Camino | Para que sirve |
|---|---|
| Web | unirse, aceptar y ver assignments en classroom50.org |
| CLI | instalar `gh student`, aceptar, clonar y entregar |

La catedra invita al classroom (organizacion de GitHub) y comparte el link o los slugs del assignment. **No crees otro repositorio ni hagas fork.**

Documentacion: <https://classroom50.org>

---

## Unirse al classroom

El classroom pertenece a una organizacion de GitHub. La catedra invita y GitHub envia un correo.

Acepta la invitacion de cualquiera de estas formas:

- segui el link del correo;
- inicia sesion en classroom50.org: la home lista invitaciones pendientes → **Accept and open**;
- abri el link del assignment que comparte la catedra: la pagina acepta la invitacion antes de mostrar el TP.

Si el link dice **Not a member yet**, pedi a la catedra que agregue el usuario al roster y reenvie la invitacion. Cuando llegue, acepta y pulsa **Check again**.

`gh student accept` tambien acepta la invitacion pendiente de la organizacion.

---

## Iniciar sesion en la web

En <https://classroom50.org> pulsa **Sign in with GitHub** y autoriza Classroom 50.

Si el navegador falla:

1. expandi **Other sign-in methods**;
2. elegi **Use a device code instead**;
3. copia el codigo de un solo uso;
4. pegalo en la pagina de verificacion de GitHub.

Classroom 50 detecta la autorizacion en unos segundos.

---

<!-- _class: compact -->

## Encontrar y aceptar un assignment

En la home: organizaciones de Classroom 50 → **Open** → **My classrooms** → **View assignments**.

Estados: **Not accepted** / **Accepted** / **Submitted**.

Para aceptar: abri el link de la catedra o pulsa **Accept assignment**. Se crea un repositorio privado con el nombre `classroom-assignment-usuario`, por ejemplo `cs50-fall-2026-hello-alice`.

Al terminar: **Open repository** o **Go to my classroom**. Si se abre el link de nuevo, la pagina indica que ya fue aceptado.

> El repositorio es privado salvo que la catedra configure lo contrario. No publiques tokens, API keys ni secretos.

---

<!-- _class: compact -->

## Instalar la extension `gh student`

Requisito: GitHub CLI, ya instalado en el Paso 2. No hace falta ejecutar `gh auth login` antes: el login de Classroom 50 lo resuelve.

```bash
gh extension install foundation50/gh-student
gh student --help
```

Actualiza mas adelante:

```bash
gh extension upgrade gh-student
```

Los comandos son iguales en Windows, macOS y Linux.

---

<!-- _class: dense -->

## Login y aceptar por CLI

```bash
gh student login
gh student accept <org> <classroom> <assignment>
```

- `<org>`: organizacion de GitHub del classroom;
- `<classroom>`: slug del classroom;
- `<assignment>`: slug del assignment.

Si el classroom es unlisted, la catedra entrega una access key:

```bash
gh student accept <org> <classroom> <assignment> --key <access-key>
```

Esto crea el repo privado `<org>/<classroom>-<assignment>-<username>` a partir del starter y muestra el `git clone`. Volver a ejecutar `accept` es seguro: repara un setup interrumpido.

---

## Clonar y trabajar

Anda a la carpeta `code` y ejecuta el `git clone` que imprime `accept` (o el de **My submission** en la web). Un solo clone:

```bash
cd <carpeta-code>
git clone <url-del-repositorio>
cd <carpeta-del-assignment>
git remote -v
```

Edita, commitea y pushea a la rama por defecto como siempre.

1. En IntelliJ elegi **Open** y selecciona la carpeta clonada.
2. Confia en el proyecto solo porque proviene de la catedra.
3. Espera la sincronizacion de Gradle y elegi JDK 21 si lo pide.
4. No abras solo `src/`: la raiz debe contener `settings.gradle.kts` y `gradlew`.

---

<!-- _class: dense -->

## Verificar el proyecto

Ejecuta desde la raiz del repositorio, o segui el `README` del assignment.

```text
# Windows
.\gradlew.bat clean check

# macOS / Linux
./gradlew clean check
```

Resultado esperado: `BUILD SUCCESSFUL`. La primera descarga de Gradle y las dependencias puede tardar varios minutos.

---

<!-- _class: dense -->

## Entregar el trabajo

Desde adentro del repositorio clonado:

```bash
gh student submit
```

Toma un snapshot, pushea y dispara el autograder: crea el tag `submit/<UTC-timestamp>-<short-sha>` y publica un Release con la nota. Tambien actualiza `.gitignore` y `.github/` si la catedra los cambio.

En la mayoria de los assignments, un `git push` tambien califica. **My submission** trae los comandos listos.

```bash
git status
git diff
```

Nunca incluyas tokens, API keys ni archivos ajenos al ejercicio.

---

## Submit-only y puntaje

Algunos assignments son **submit-only**: el push guarda el trabajo pero no califica. Ejecuta `gh student submit` o pushea un tag `submit/`:

```bash
git tag submit/final && git push origin submit/final
```

Si la catedra define milestones (`phase1`, etc.), pushear ese tag califica ese commit.

Las notas viven en **Releases** del repositorio: cada entrega calificada publica un Release con el puntaje y el detalle por test. En Classroom 50, **View autograder details** y **View score** abren ese Release.

---

<!-- _class: compact -->

## Checklist final del estudiante

- [ ] `java -version` y `javac -version` informan Java 21.
- [ ] `git --version`, `gh --version` y `opencode --version` responden.
- [ ] `gh student --help` responde.
- [ ] La invitacion al classroom esta aceptada.
- [ ] El assignment esta en estado **Accepted** o **Submitted**.
- [ ] IntelliJ abre el proyecto con JDK 21 y sincroniza Gradle.
- [ ] El Gradle Wrapper finaliza `clean check` correctamente.
- [ ] `gh student submit` entrega el trabajo (o el tag `submit/` si aplica).
- [ ] El Release o la pagina **My submission** muestran la entrega.
- [ ] OpenCode responde usando el contexto del proyecto.
- [ ] La interaccion de IA esta registrada en el Prompt Log, sin secretos.

---

<!-- _class: compact -->

## Diagnostico rapido

| Problema | Comprobacion |
|---|---|
| `java` no existe | reabrir terminal; revisar `PATH` / `JAVA_HOME` |
| Gradle usa otro Java | seleccionar JDK 21 como Gradle JVM |
| `Permission denied` en wrapper | ejecutar `chmod +x gradlew` |
| `gh student` no existe | `gh extension install foundation50/gh-student` |
| `accept` pide membresia | aceptar la invitacion de GitHub al classroom |
| classroom unlisted | agregar `--key` con la access key de la catedra |
| `gh student submit` falla | `submit` se corre desde el clone; si falla auth, `gh student login` (cualquier carpeta); si no es repo de estudiante, `cd` al clone |
| OpenCode no aparece | reabrir terminal y revisar su instalacion en `PATH` |
| OpenCode no tiene modelos | ejecutar `/connect` y revisar proveedor/cuota |

Si continua el error, guarda el comando, salida completa, sistema operativo y version de la herramienta.

---

<!-- _class: compact -->

## Fuentes y enlaces oficiales

- Eclipse Adoptium. [Temurin 21](https://adoptium.net/temurin/releases/?version=21)
- JetBrains. [Install IntelliJ IDEA](https://www.jetbrains.com/help/idea/installation-guide.html)
- Kotlin. [Get started with JVM and Gradle](https://kotlinlang.org/docs/get-started-with-jvm-gradle-project.html)
- Gradle. [Gradle Wrapper](https://docs.gradle.org/current/userguide/gradle_wrapper.html)
- Git. [Install Git](https://git-scm.com/install/)
- GitHub. [Set up Git](https://docs.github.com/en/get-started/git-basics/set-up-git)
- GitHub CLI. [Installation](https://cli.github.com/)
- GitHub Education. [Apply as a student](https://docs.github.com/en/education/about-github-education/github-education-for-students/apply-to-github-education-as-a-student)
- GitHub Copilot. [Free access for students](https://docs.github.com/copilot/how-tos/manage-your-account/free-access-with-copilot-student)
- Classroom 50. [Web app](https://classroom50.org)
- Classroom 50. [Web Student Guide](https://github.com/foundation50/classroom50/wiki/Web-Student-Guide)
- Classroom 50. [CLI Student Guide](https://github.com/foundation50/classroom50/wiki/CLI-Student-Guide)
- Classroom 50. [Installation](https://github.com/foundation50/classroom50/wiki/Installation)
- OpenCode. [Installation and configuration](https://opencode.ai/docs/)
- OpenCode. [Providers](https://opencode.ai/docs/providers/)
