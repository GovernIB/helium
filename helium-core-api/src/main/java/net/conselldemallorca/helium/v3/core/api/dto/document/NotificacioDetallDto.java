package net.conselldemallorca.helium.v3.core.api.dto.document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.conselldemallorca.helium.v3.core.api.dto.DocumentEnviamentEstatEnumDto;
import net.conselldemallorca.helium.v3.core.api.dto.DocumentNotificacioTipusEnumDto;
import net.conselldemallorca.helium.v3.core.api.dto.EnviamentTipusEnumDto;
import net.conselldemallorca.helium.v3.core.api.dto.NotificacioEstatEnumDto;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificacioDetallDto {
    private Long id;
    private String concepte;
    private NotificacioEstatEnumDto estat;
    private Date dataEnviament;
    private EnviamentTipusEnumDto tipus;
    private String titularNom;
    private String titularNif;
    private String destinatariNom;
    private String destinatariNif;
    private Long justificantId;
}
