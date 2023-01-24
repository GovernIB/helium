package net.conselldemallorca.helium.v3.core.api.dto.handlers;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class HandlerParametreDto {
    private String nom;
    private String codi;
    private boolean obligatori = false;
    private String param;
    private String paramDesc;
    private String varParam;
    private String varParamDesc;
}
