# Proyecto Selenium Automation

Proyecto de automatizacion web en Java con Selenium, JUnit 5 y Gradle Wrapper.

## Objetivo

Este proyecto ejecuta una prueba automatica simple sobre Google:

1. Abre el navegador.
2. Navega a https://www.google.com.
3. Encuentra la barra de busqueda.
4. Escribe "Serenity BDD".
5. Presiona Enter.
6. Cierra el navegador.

## Stack

- Java 21
- Gradle (Wrapper)
- JUnit 5
- Selenium WebDriver
- Serenity BDD (plugin y reportes)

## Estructura

- src/test/java/com/automation/GoogleTest.java: flujo principal de prueba.
- src/test/java/com/automation/ui/GooglePage.java: localizadores de pagina.
- src/test/java/com/automation/tasks/BuscarEnGoogle.java: utilidad de busqueda reutilizable.
- src/test/java/com/automation/questions/ElTituloDePagina.java: lectura de titulo (pieza auxiliar).
- HOW_IT_WORK.md: explicacion funcional paso a paso.

## Requisitos

- Java 21 instalado y disponible en PATH.
- Navegador Chrome/Chromium instalado.

Verificar Java:

```bash
java -version
```

Verificar navegador:

```bash
chrome --version
google-chrome --version
chromium --version
```

## Ejecucion

Linux/macOS:

```bash
chmod +x gradlew
./gradlew clean test
```

Windows:

```powershell
gradlew.bat clean test
```

Ejecutar solo la prueba principal:

Linux/macOS:

```bash
./gradlew test --tests com.automation.GoogleTest
```

Windows:

```powershell
gradlew.bat test --tests com.automation.GoogleTest
```

## Reportes

Generar reporte:

Linux/macOS:

```bash
./gradlew test aggregate
```

Windows:

```powershell
gradlew.bat test aggregate
```

Ruta del reporte:

```text
target/site/serenity/index.html
```

Abrir reporte:

Linux:

```bash
xdg-open target/site/serenity/index.html
```

macOS:

```bash
open target/site/serenity/index.html
```

Windows:

```powershell
start target/site/serenity/index.html
```

## Flujo minimo recomendado

Linux/macOS:

```bash
chmod +x gradlew
./gradlew clean test aggregate
```

Windows:

```powershell
gradlew.bat clean test aggregate
```

## Estado actual

- La prueba usa metodos directos de Selenium en GoogleTest (get, findElement, click, sendKeys).
- El reporte Serenity se genera al ejecutar pruebas.

## Notas

- El proyecto esta configurado para usar Java 21 desde Gradle.
- La prueba actual abre Google y ejecuta una busqueda automatizada.
- Si en tu entorno no existe Chrome con ese nombre, usa el navegador compatible que tengas configurado para Selenium.

## Subida a GitHub

Este repositorio incluye un .gitignore para evitar subir artefactos temporales y de compilacion.

Flujo sugerido:

```bash
git add .
git status
git commit -m "Initial project setup"
```
