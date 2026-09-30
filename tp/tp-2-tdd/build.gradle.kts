plugins {
    kotlin("jvm") version "2.0.21"
    application
}

group = "com.university.dcp"
version = "1.0.0"

repositories {
    mavenCentral()
}

kotlin {
    jvmToolchain(21)
}

application {
    mainClass.set("com.university.dcp.MainKt")
}

dependencies {
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
    // El test de contrato lee estos archivos: si cambian, los tests se vuelven a correr.
    inputs.dir("datos")
    inputs.files("salida-esperada.txt", "reporte-esperado.txt")
    testLogging {
        events("passed", "skipped", "failed")
        showExceptions = true
        showStackTraces = true
        showStandardStreams = true
    }
}
