package es.caib.helium.commons.dto.document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

import es.caib.helium.commons.dto.AnotacioDto;
import es.caib.helium.commons.dto.ArxiuDetallDto;
import es.caib.helium.commons.dto.DadesNotificacioDto;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DocumentDetallDto {
    private Long documentStoreId;
    private String documentNom;
    private String arxiuNom;
    private String extensio;
    private boolean adjunt;
    private String adjuntTitol;
    private Date dataCreacio;
    private Date dataModificacio;
    private Date dataDocument;
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
    private NtiDetallDto ntiDetall;
    private AnotacioDto anotacio;
    private List<DadesNotificacioDto> notificacions;
}