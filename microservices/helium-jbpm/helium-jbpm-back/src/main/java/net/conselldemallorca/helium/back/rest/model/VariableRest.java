package net.conselldemallorca.helium.back.rest.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.conselldemallorca.helium.api.dto.TipusVar;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VariableRest {

    private String nom;
    private TipusVar tipus;
    private String valor;
    private String className;

}