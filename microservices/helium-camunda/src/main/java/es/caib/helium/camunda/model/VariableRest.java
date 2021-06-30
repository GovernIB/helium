package es.caib.helium.camunda.model;

import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Data
@Builder
@Slf4j
public class VariableRest {

    private String nom;
    private TipusVar tipus;
    private String valor;
    private String className;

}
