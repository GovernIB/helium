package es.caib.helium.ejb;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;

import org.springframework.beans.factory.annotation.Autowired;

import es.caib.helium.commons.dto.ConsultesPortafibFiltreDto;
import es.caib.helium.commons.dto.PaginaDto;
import es.caib.helium.commons.dto.PaginacioParamsDto;
import es.caib.helium.commons.dto.PortasignaturesDto;
import es.caib.helium.commons.exception.PermisDenegatException;
import es.caib.helium.logic.intf.service.PortasignaturesService;

@Stateless
//@Interceptors(SpringBeanAutowiringInterceptor.class)
public class PortasignaturesServiceBean implements PortasignaturesService {
	
	@Autowired PortasignaturesService delegate;

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public PaginaDto<PortasignaturesDto> findAmbFiltrePaginat(PaginacioParamsDto paginacioParams, ConsultesPortafibFiltreDto filtreDto) {
		return delegate.findAmbFiltrePaginat(paginacioParams, filtreDto);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public PortasignaturesDto findById(Long portafirmesId) throws PermisDenegatException {
		return delegate.findById(portafirmesId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public boolean processarDocumentCallbackPortasignatures(Integer id, boolean rebujat, String motiuRebuig) {
		return delegate.processarDocumentCallbackPortasignatures(id, rebujat, motiuRebuig);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public boolean processarDocumentPendentPortasignatures(Integer id) {
		return delegate.processarDocumentPendentPortasignatures(id);
	}
}