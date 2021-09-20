package es.caib.helium.dada.enums;

import lombok.Getter;

@Getter
public enum TipusDocument {

    DOCUMENT("DOCUMENT"),
    ADJUNT("ADJUNT");

    private String tipus;

    TipusDocument(String tipus) {
        this.tipus = tipus;
    }
}
