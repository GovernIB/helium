package es.caib.helium.ejb;

import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;

import org.springframework.beans.factory.annotation.Autowired;

import es.caib.helium.commons.dto.PaginaDto;
import es.caib.helium.commons.dto.PaginacioParamsDto;
import es.caib.helium.commons.dto.ParametreDto;
import es.caib.helium.logic.intf.service.ParametreService;

/**
 * Implementació de ParametreService com a EJB que empra una clase
 * delegada per accedir a la funcionalitat del servei.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
//@Interceptors(SpringBeanAutowiringInterceptor.class)
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

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public Long getMidaMaximaFitxerInBytes() {
		return delegate.getMidaMaximaFitxerInBytes();
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public String getMidaMaximaFitxer() {
		return delegate.getMidaMaximaFitxer();
	}

}
