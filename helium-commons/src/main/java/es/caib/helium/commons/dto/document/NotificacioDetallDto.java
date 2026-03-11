package es.caib.helium.commons.dto.document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

import es.caib.helium.commons.dto.DocumentEnviamentEstatEnumDto;
import es.caib.helium.commons.dto.DocumentNotificacioTipusEnumDto;
import es.caib.helium.commons.dto.EnviamentTipusEnumDto;
import es.caib.helium.commons.dto.NotificacioEstatEnumDto;

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
