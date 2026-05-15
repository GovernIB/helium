package es.caib.helium.ejb;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;

import es.caib.helium.ejb.base.AbstractServiceEjb;
import lombok.experimental.Delegate;

import es.caib.helium.commons.dto.ConsultesPortafibFiltreDto;
import es.caib.helium.commons.dto.PaginaDto;
import es.caib.helium.commons.dto.PaginacioParamsDto;
import es.caib.helium.commons.dto.PortasignaturesDto;
import es.caib.helium.commons.exception.PermisDenegatException;
import es.caib.helium.logic.intf.service.PortasignaturesService;

@Stateless
public class PortasignaturesServiceBean extends AbstractServiceEjb<PortasignaturesService> implements PortasignaturesService {

	@Delegate PortasignaturesService delegateService;

	protected void setDelegateService(PortasignaturesService delegateService) {
		this.delegateService = delegateService;
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public PaginaDto<PortasignaturesDto> findAmbFiltrePaginat(PaginacioParamsDto paginacioParams, ConsultesPortafibFiltreDto filtreDto) {
		return delegateService.findAmbFiltrePaginat(paginacioParams, filtreDto);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public PortasignaturesDto findById(Long portafirmesId) throws PermisDenegatException {
		return delegateService.findById(portafirmesId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public boolean processarDocumentCallbackPortasignatures(Integer id, boolean rebujat, String motiuRebuig) {
		return delegateService.processarDocumentCallbackPortasignatures(id, rebujat, motiuRebuig);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public boolean processarDocumentPendentPortasignatures(Integer id) {
		return delegateService.processarDocumentPendentPortasignatures(id);
	}

}