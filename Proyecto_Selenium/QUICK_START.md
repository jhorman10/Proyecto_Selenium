# Levantar el proyecto

Este proyecto usa Gradle Wrapper, Java 21, Serenity BDD, JUnit 5 y Selenium.

## 1. Ubicarse en la raiz del proyecto

Abre una terminal en la carpeta donde se encuentra este proyecto.

## 2. Verificar prerequisitos

Validar que Java 21 este instalado:

```bash
java -version
```

Si necesitas configurar `JAVA_HOME`, usa la forma correspondiente a tu sistema operativo y shell.

Validar que un navegador compatible este disponible. La ejecucion actual usa Chrome.

Ejemplos de verificacion:

```bash
chrome --version
google-chrome --version
chromium --version
```

## 3. Ejecutar el Gradle Wrapper

Usa el comando segun tu entorno:

### Linux o macOS

Si el archivo no tiene permisos de ejecucion:

```bash
chmod +x gradlew
```

Ejecutar pruebas:

```bash
./gradlew clean test
```

### Windows

Ejecutar pruebas:

```powershell
gradlew.bat clean test
```

## 4. Ejecutar solo la prueba actual

### Linux o macOS

```bash
./gradlew test --tests com.automation.GoogleTest
```

### Windows

```powershell
gradlew.bat test --tests com.automation.GoogleTest
```

## 5. Generar o refrescar el reporte de Serenity

### Linux o macOS

```bash
./gradlew test aggregate
```

### Windows

```powershell
gradlew.bat test aggregate
```

## 6. Abrir el reporte de Serenity

El reporte se genera en:

```text
target/site/serenity/index.html
```

Puedes abrirlo con el navegador o explorador de archivos de tu sistema operativo.

Ejemplos:

### Linux

```bash
xdg-open target/site/serenity/index.html
```

### macOS

```bash
open target/site/serenity/index.html
```

### Windows

```powershell
start target/site/serenity/index.html
```

## Flujo minimo recomendado

### Linux o macOS

```bash
chmod +x gradlew
./gradlew clean test aggregate
```

### Windows

```powershell
gradlew.bat clean test aggregate
```

Luego abre el archivo `target/site/serenity/index.html`.

## Notas

- El proyecto esta configurado para usar Java 21 desde Gradle.
- La prueba actual abre Google y ejecuta una busqueda automatizada.
- Si en tu entorno no existe Chrome con ese nombre, usa el navegador compatible que tengas configurado para Selenium.