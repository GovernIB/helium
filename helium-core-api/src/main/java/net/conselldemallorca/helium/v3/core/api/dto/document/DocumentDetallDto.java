package net.conselldemallorca.helium.v3.core.api.dto.document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.conselldemallorca.helium.v3.core.api.dto.AnotacioDto;
import net.conselldemallorca.helium.v3.core.api.dto.ArxiuDetallDto;
import net.conselldemallorca.helium.v3.core.api.dto.DadesNotificacioDto;

import java.util.Date;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DocumentDetallDto {
    private Long documentStoreId;
    private String documentNom;
    private String arxiuNom;
    private boolean adjunt;
    private String adjuntTitol;
    private Date dataCreacio;
    private Date dataModificacio;
    private Date dataDocument;
    private boolean notificable;
    private String arxiuUuid;
    private String ntiCsv;
    private boolean signat;
    private boolean registrat;
    private boolean psignaPendent;
    private boolean deAnotacio;
    private boolean notificat;
    private boolean nti;
    private boolean arxiu;
    boolean errorArxiuNoUuid;
    boolean errorMetadadesNti;

    private RegistreDetallDto registreDetall;
    private SignaturaValidacioDetallDto signaturaValidacioDetall;
    private PsignaDetallDto psignaDetall;
    private ArxiuDetallDto arxiuDetall;
    private AnotacioDto anotacio;
    private List<DadesNotificacioDto> notificacions;

}
