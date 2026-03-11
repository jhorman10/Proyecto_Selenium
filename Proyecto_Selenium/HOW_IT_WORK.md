# Como funciona este proyecto (estado actual)

## Resumen rapido

Hoy este proyecto hace una prueba automatica sencilla usando Selenium directo.

El flujo real actual es este:

1. Abre Chrome.
2. Va a Google.
3. Busca la caja de texto.
4. Hace clic en la caja.
5. Escribe `Serenity BDD`.
6. Presiona Enter.
7. Cierra el navegador.
8. Genera reporte de ejecucion.

## Que cambio frente a versiones anteriores

Antes se usaba un estilo mas narrativo con actor y tareas tipo Screenplay en la prueba principal.

Ahora la prueba principal usa acciones directas del navegador, con estos metodos:

- `driver.get(...)`
- `driver.findElement(...)`
- `click()`
- `sendKeys(...)`

Esto hace que el recorrido sea mas directo de leer para quien quiere ver las acciones tal cual.

## Paso a paso real de la ejecucion

### Paso 1. Gradle inicia la prueba

Cuando ejecutas la prueba, Gradle prepara Java, dependencias y entorno.

Aunque en el proyecto siguen estando librerias de Serenity, el flujo de la prueba principal es Selenium directo.

### Paso 2. Se crea el navegador

Antes de cada prueba se crea una nueva ventana de Chrome.

Esto ocurre en el metodo `setUp()`.

Que hace exactamente:

1. Crea opciones para el navegador.
2. Agrega la opcion `--remote-allow-origins=*`.
3. Inicia `ChromeDriver`.

Resultado: queda una sesion de navegador lista para recibir acciones.

### Paso 3. El navegador abre Google

Ya dentro de la prueba, se ejecuta:

```java
driver.get("https://www.google.com");
```

En lenguaje simple: el robot abre Google igual que una persona al escribir la URL.

### Paso 4. El proyecto localiza la caja de busqueda

Luego usa un localizador guardado en `GooglePage`.

Ese localizador dice: "busca el elemento cuyo nombre es `q`".

Con eso, la prueba encuentra la barra de busqueda con:

```java
driver.findElement(GooglePage.BARRA_BUSQUEDA)
```

### Paso 5. Se hace clic en la caja

Cuando la barra ya fue encontrada, el flujo hace:

```java
barraBusqueda.click();
```

Esto da foco al campo, como cuando una persona pone el cursor antes de escribir.

### Paso 6. Se escribe el texto

Despues se escribe:

```java
barraBusqueda.sendKeys("Serenity BDD");
```

En otras palabras, simula la escritura de ese texto en el teclado.

### Paso 7. Se envia la busqueda con Enter

Finalmente se ejecuta:

```java
barraBusqueda.sendKeys(Keys.ENTER);
```

Eso dispara la busqueda de Google.

### Paso 8. Se cierra el navegador

Al terminar la prueba, se llama `tearDown()`.

Ese metodo verifica que exista navegador y lo cierra con `driver.quit()`.

Asi se evita dejar ventanas abiertas y se liberan recursos.

## Que clases participan hoy y para que sirven

### 1) `GoogleTest`

Es la clase principal en ejecucion.

Aqui estan, de manera explicita, todas las acciones del flujo principal:

- abrir pagina,
- encontrar elemento,
- hacer clic,
- escribir,
- enviar Enter,
- cerrar navegador.

### 2) `GooglePage`

Guarda el localizador de la caja de busqueda:

- `By.name("q")`

Su objetivo es evitar "texto suelto" repetido dentro de la prueba.

### 3) `BuscarEnGoogle`

Esta clase existe y tambien implementa el mismo flujo (buscar campo, clic, escribir, Enter), pero en el estado actual no es llamada por `GoogleTest`.

En este momento es una utilidad lista para reutilizar si decides mover la logica fuera del test principal.

### 4) `ElTituloDePagina`

Esta clase sigue en estilo Screenplay y sirve para leer el titulo de la pagina.

Actualmente no participa en el flujo principal Selenium directo.

Se puede considerar una pieza heredada o reservada para una validacion futura.

## Que valida hoy la prueba

La prueba valida, de forma practica, que el recorrido se puede ejecutar sin romperse:

1. Chrome abre.
2. Google carga.
3. El campo de busqueda se encuentra.
4. Se puede escribir.
5. Se puede enviar la busqueda.

## Que todavia no valida

No hay una verificacion fuerte del resultado final (por ejemplo, revisar texto concreto en la pagina de resultados).

Eso significa que la prueba confirma el camino, pero no compara todavia un resultado esperado detallado.

## Sobre reportes

Al ejecutar, el build sigue generando reporte.

Ruta esperada del reporte principal:

```text
target/site/serenity/index.html
```

## Conclusion

El proyecto hoy esta en un punto claro:

1. Flujo principal simple y directo con Selenium.
2. Acciones visibles tal cual (`get`, `findElement`, `click`, `sendKeys`).
3. Estructura lista para refactorizar despues si quieres volver a separar logica por tareas.

Dicho simple: ya funciona como un "robot de navegador" directo, facil de leer paso a paso y alineado con los metodos de accion que necesitas mostrar.