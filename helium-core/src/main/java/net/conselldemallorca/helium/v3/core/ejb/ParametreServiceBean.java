package net.conselldemallorca.helium.v3.core.ejb;

import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ejb.interceptor.SpringBeanAutowiringInterceptor;

import net.conselldemallorca.helium.v3.core.api.dto.PaginaDto;
import net.conselldemallorca.helium.v3.core.api.dto.PaginacioParamsDto;
import net.conselldemallorca.helium.v3.core.api.dto.ParametreDto;
import net.conselldemallorca.helium.v3.core.api.service.ParametreService;

/**
 * Implementació de ParametreService com a EJB que empra una clase
 * delegada per accedir a la funcionalitat del servei.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
@Interceptors(SpringBeanAutowiringInterceptor.class)
public class ParametreServiceBean implements ParametreService {

	@Autowired
	ParametreService delegate;
	
	@Override
	@RolesAllowed({"HEL_ADMIN"})
	public ParametreDto create(ParametreDto parametre) {
		return delegate.create(parametre);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN"})
	public ParametreDto update(ParametreDto parametre) {
		return delegate.update(parametre);
	}
	

	@Override
	@RolesAllowed({"HEL_ADMIN"})
	public ParametreDto delete(Long id) {
		return delegate.delete(id);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN"})
	public ParametreDto findById(Long id) {
		return delegate.findById(id);
	}
	
	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<ParametreDto> findAll() {
		return delegate.findAll();
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public PaginaDto<ParametreDto> findPaginat(PaginacioParamsDto paginacioParams) {
		return delegate.findPaginat(paginacioParams);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public ParametreDto findByCodi(String codi) {
		return delegate.findByCodi(codi);
	}

}
