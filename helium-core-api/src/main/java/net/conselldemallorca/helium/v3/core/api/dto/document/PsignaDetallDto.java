package net.conselldemallorca.helium.v3.core.api.dto.document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PsignaDetallDto {
    private Integer documentId;
    private Date dataEnviat;
    private String estat;
    private boolean error;
    private String errorProcessant;
    private String motiuRebuig;
    private Date dataProcessamentPrimer;
    private Date dataProcessamentDarrer;
}
