package net.conselldemallorca.helium.back.rest.model;

import lombok.Builder;
import lombok.Data;
import net.conselldemallorca.helium.api.dto.TipusVar;

@Data
@Builder
public class VariableRest {

    private String nom;
    private TipusVar tipus;
    private String valor;
    private String className;

}