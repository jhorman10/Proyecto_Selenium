package com.automation.tasks;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.automation.ui.GooglePage;

public class BuscarEnGoogle {
    private BuscarEnGoogle() {
    }

    public static void conLaPalabra(WebDriver driver, String palabra) {
        WebElement barraBusqueda = driver.findElement(GooglePage.BARRA_BUSQUEDA);
        barraBusqueda.click();
        barraBusqueda.sendKeys(palabra);
        barraBusqueda.sendKeys(Keys.ENTER);
    }
}
