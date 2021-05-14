package net.conselldemallorca.helium.v3.core.ejb;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ejb.interceptor.SpringBeanAutowiringInterceptor;

import net.conselldemallorca.helium.v3.core.api.dto.AreaJbpmIdDto;
import net.conselldemallorca.helium.v3.core.api.dto.PaginaDto;
import net.conselldemallorca.helium.v3.core.api.dto.PaginacioParamsDto;
import net.conselldemallorca.helium.v3.core.api.service.AreaService;

/**
 * EJB per a AreaService.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
@Interceptors(SpringBeanAutowiringInterceptor.class)
public class AreaServiceBean implements AreaService {

	@Autowired
	AreaService delegate;

	@Override
	@RolesAllowed({"HEL_ADMIN"})
	public AreaJbpmIdDto findAmbId(Long id) {
		return delegate.findAmbId(id);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN"})
	public AreaJbpmIdDto findAmbCodi(String codi) {
		return delegate.findAmbCodi(codi);
	}
	
	@Override
	
	public PaginaDto<AreaJbpmIdDto> findConfigurades(PaginacioParamsDto paginacioParams) {
		return delegate.findConfigurades(paginacioParams);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN"})
	public PaginaDto<AreaJbpmIdDto> findSenseConfigurar(PaginacioParamsDto paginacioParams) {
		return delegate.findSenseConfigurar(paginacioParams);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN"})
	public AreaJbpmIdDto create(AreaJbpmIdDto area) {
		return delegate.create(area);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN"})
	public void delete(Long areaId) {
		delegate.delete(areaId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN"})
	public AreaJbpmIdDto update(AreaJbpmIdDto area) {
		// TODO Auto-generated method stub
		return null;
	}

}
