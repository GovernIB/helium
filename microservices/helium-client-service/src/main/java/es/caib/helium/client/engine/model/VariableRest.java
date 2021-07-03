package es.caib.helium.client.engine.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class VariableRest {

    private String nom;
    private TipusVar tipus;
    private String valor;
    private String className;

}