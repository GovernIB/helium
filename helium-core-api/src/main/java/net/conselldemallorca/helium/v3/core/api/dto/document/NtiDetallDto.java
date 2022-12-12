package net.conselldemallorca.helium.v3.core.api.dto.document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.conselldemallorca.helium.v3.core.api.dto.AnotacioDto;
import net.conselldemallorca.helium.v3.core.api.dto.ArxiuDetallDto;
import net.conselldemallorca.helium.v3.core.api.dto.DadesNotificacioDto;
import net.conselldemallorca.helium.v3.core.api.dto.NtiDocumentoFormato;
import net.conselldemallorca.helium.v3.core.api.dto.NtiEstadoElaboracionEnumDto;
import net.conselldemallorca.helium.v3.core.api.dto.NtiOrigenEnumDto;
import net.conselldemallorca.helium.v3.core.api.dto.NtiTipoDocumentalEnumDto;
import net.conselldemallorca.helium.v3.core.api.dto.NtiTipoFirmaEnumDto;

import java.util.Date;
import java.util.List;

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
