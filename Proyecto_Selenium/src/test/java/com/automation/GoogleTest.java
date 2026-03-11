package com.automation;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import com.automation.ui.GooglePage;

public class GoogleTest {
    // Mantiene la referencia al navegador durante cada prueba.
    private WebDriver driver;

    @BeforeEach
    void setUp() {
        // Define opciones de arranque para Chrome antes de crear el navegador.
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--remote-allow-origins=*");
        // Inicia una nueva ventana de navegador para la prueba actual.
        driver = new ChromeDriver(options);
    }

    @Test
    void buscarEnGoogle() {
        // Abre la URL indicada en el navegador.
        driver.get("https://www.google.com");

        // Ubica el cuadro de busqueda en la pagina de Google.
        WebElement barraBusqueda = driver.findElement(GooglePage.BARRA_BUSQUEDA);
        // Da foco al campo para comenzar a escribir.
        barraBusqueda.click();
        // Escribe el texto que se desea buscar.
        barraBusqueda.sendKeys("Serenity BDD");
        // Simula Enter para lanzar la busqueda.
        barraBusqueda.sendKeys(Keys.ENTER);
    }

    @AfterEach
    void tearDown() {
        // Cierra el navegador al terminar para liberar recursos.
        if (driver != null) {
            driver.quit();
        }
    }
}
