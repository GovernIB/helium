package net.conselldemallorca.helium.v3.core.ejb;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ejb.interceptor.SpringBeanAutowiringInterceptor;

import es.caib.helium.logic.intf.dto.AreaMembreDto;
import es.caib.helium.logic.intf.dto.PaginaDto;
import es.caib.helium.logic.intf.dto.PaginacioParamsDto;
import es.caib.helium.logic.intf.service.EntornAreaMembreService;

/**
 * EJB per a EntornAreaMembreService.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
@Interceptors(SpringBeanAutowiringInterceptor.class)
public class EntornAreaMembreServiceBean implements EntornAreaMembreService {

	@Autowired
	EntornAreaMembreService delegate;

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public PaginaDto<AreaMembreDto> findPerDatatable(Long entornAreaId, PaginacioParamsDto paginacioParams) {
		return delegate.findPerDatatable(entornAreaId, paginacioParams);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public AreaMembreDto create(Long entornId, Long carrecId, AreaMembreDto areaMembre) {
		return delegate.create(entornId, carrecId, areaMembre);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void delete(Long entornAreaId, Long id) {
		delegate.delete(entornAreaId, id);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public AreaMembreDto findAmbCodiAndAreaId(String codi, Long areaId) {
		return delegate.findAmbCodiAndAreaId(codi, areaId);
	}

}
