package es.caib.helium.commons.dto.regles;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CampFormProperties {
    private boolean visible;
    private boolean editable;
    private boolean obligatori;
    private boolean obligatoriEntrada;
    private boolean obligatoriSignat;
    private boolean obligatoriNotificat;
}
