package net.conselldemallorca.helium.v3.core.ejb;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ejb.interceptor.SpringBeanAutowiringInterceptor;

import net.conselldemallorca.helium.v3.core.api.dto.CarrecJbpmIdDto;
import net.conselldemallorca.helium.v3.core.api.dto.PaginaDto;
import net.conselldemallorca.helium.v3.core.api.dto.PaginacioParamsDto;
import net.conselldemallorca.helium.v3.core.api.service.CarrecService;

/**
 * EJB per a CarrecService.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
@Interceptors(SpringBeanAutowiringInterceptor.class)
public class CarrecServiceBean implements CarrecService {
	
	@Autowired
	CarrecService delegate;

	
	@Override
	@RolesAllowed({"HEL_ADMIN"})
	public PaginaDto<CarrecJbpmIdDto> findConfigurats(PaginacioParamsDto paginacioParams) {
		return delegate.findConfigurats(paginacioParams);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN"})
	public PaginaDto<CarrecJbpmIdDto> findSenseConfigurar(PaginacioParamsDto params) {
		// TODO Auto-generated method stub
		return delegate.findSenseConfigurar(params);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN"})
	public CarrecJbpmIdDto findAmbId(Long id) {
		return delegate.findAmbId(id);
	}
	
	@Override
	@RolesAllowed({"HEL_ADMIN"})
	public CarrecJbpmIdDto findAmbCodi(String codi) {
		return delegate.findAmbCodi(codi);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN"})
	public CarrecJbpmIdDto create(CarrecJbpmIdDto carrec) {
		// TODO Auto-generated method stub
		return delegate.create(carrec);
	}
	
	@Override
	@RolesAllowed({"HEL_ADMIN"})
	public CarrecJbpmIdDto update(CarrecJbpmIdDto carrec) {
		return delegate.update(carrec);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN"})
	public void delete(Long carrecId) {
		delegate.delete(carrecId);	
	}

	@Override
	public CarrecJbpmIdDto findAmbCodiAndGrup(String codi, String grup) {
		return delegate.findAmbCodiAndGrup(codi, grup);
	}
	

}
