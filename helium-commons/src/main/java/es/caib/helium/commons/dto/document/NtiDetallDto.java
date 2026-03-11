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
import es.caib.helium.commons.dto.NtiDocumentoFormato;
import es.caib.helium.commons.dto.NtiEstadoElaboracionEnumDto;
import es.caib.helium.commons.dto.NtiOrigenEnumDto;
import es.caib.helium.commons.dto.NtiTipoDocumentalEnumDto;
import es.caib.helium.commons.dto.NtiTipoFirmaEnumDto;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NtiDetallDto {
    private String ntiVersion;
    private String ntiIdentificador;
    private String ntiOrgano;
    private NtiOrigenEnumDto ntiOrigen;
    private NtiEstadoElaboracionEnumDto ntiEstadoElaboracion;
    private NtiDocumentoFormato ntiNombreFormato;
    private NtiTipoDocumentalEnumDto ntiTipoDocumental;
    private String ntiIdOrigen;
    private String ntiIdDocumentoOrigen;
    private NtiTipoFirmaEnumDto ntiTipoFirma;
    private String ntiCsv;
    private String ntiDefinicionGenCsv;
    private String arxiuUuid;
}
