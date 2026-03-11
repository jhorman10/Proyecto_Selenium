package com.automation.tasks;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.automation.ui.GooglePage;

public class BuscarEnGoogle {
    // Evita que esta clase se instancie; se usa solo con el metodo estatico.
    private BuscarEnGoogle() {
    }

    public static void conLaPalabra(WebDriver driver, String palabra) {
        // Ejecuta el flujo completo de busqueda: ubicar campo, enfocar, escribir y enviar.
        // Busca en la pagina el elemento de la caja de busqueda usando el localizador definido en GooglePage.
        WebElement barraBusqueda = driver.findElement(GooglePage.BARRA_BUSQUEDA);
        // Hace clic en la caja para darle foco, como lo haria un usuario antes de escribir.
        barraBusqueda.click();
        // Escribe el texto recibido en el parametro "palabra" dentro de la caja de busqueda.
        barraBusqueda.sendKeys(palabra);
        // Simula presionar Enter para ejecutar la busqueda.
        barraBusqueda.sendKeys(Keys.ENTER);
    }
}
