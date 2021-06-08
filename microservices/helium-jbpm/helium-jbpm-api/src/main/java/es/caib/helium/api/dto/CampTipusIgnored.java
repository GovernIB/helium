package es.caib.helium.api.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CampTipusIgnored {
    private CampTipusDto tipus;
    private Boolean ignored;
}
