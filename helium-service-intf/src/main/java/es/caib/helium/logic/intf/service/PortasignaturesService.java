package es.caib.helium.logic.intf.service;

import es.caib.helium.commons.dto.ConsultesPortafibFiltreDto;
import es.caib.helium.commons.dto.PaginaDto;
import es.caib.helium.commons.dto.PaginacioParamsDto;
import es.caib.helium.commons.dto.PortasignaturesDto;
import es.caib.helium.commons.exception.PermisDenegatException;

public interface PortasignaturesService {
	PaginaDto<PortasignaturesDto> findAmbFiltrePaginat(PaginacioParamsDto paginacioParams, ConsultesPortafibFiltreDto filtreDto);	
	PortasignaturesDto findById(Long portafirmesId) throws PermisDenegatException;
	boolean processarDocumentCallbackPortasignatures(Integer id, boolean rebujat, String motiuRebuig);
	boolean processarDocumentPendentPortasignatures(Integer id);
}