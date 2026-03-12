package com.automation.questions;

import net.serenitybdd.screenplay.Question;
import net.serenitybdd.screenplay.abilities.BrowseTheWeb;

public class ElTituloDePagina implements Question<String> {

    public static ElTituloDePagina es() {
        return new ElTituloDePagina();
    }

    @Override
    public String answeredBy(net.serenitybdd.screenplay.Actor actor) {
        return BrowseTheWeb.as(actor).getTitle();
    }
}
