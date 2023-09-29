package net.conselldemallorca.helium.v3.core.api.dto.regles;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CampFormProperties {
    private boolean visible;
    private boolean editable;
    private boolean obligatori;
}
