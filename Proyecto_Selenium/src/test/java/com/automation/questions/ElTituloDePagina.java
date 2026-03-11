package com.automation.questions;

import net.serenitybdd.screenplay.Question;
import net.serenitybdd.screenplay.abilities.BrowseTheWeb;

public class ElTituloDePagina implements Question<String> {

    public static ElTituloDePagina es() {
        // Crea una instancia reutilizable de la pregunta para usarla en aserciones.
        return new ElTituloDePagina();
    }

    @Override
    public String answeredBy(net.serenitybdd.screenplay.Actor actor) {
        // Lee y devuelve el titulo de la pestaña actual abierta por el actor.
        return BrowseTheWeb.as(actor).getTitle();
    }
}
