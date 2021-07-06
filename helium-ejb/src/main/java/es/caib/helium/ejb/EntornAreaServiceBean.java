package es.caib.helium.ejb;

import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ejb.interceptor.SpringBeanAutowiringInterceptor;

import es.caib.helium.logic.intf.dto.EntornAreaDto;
import es.caib.helium.logic.intf.dto.PaginaDto;
import es.caib.helium.logic.intf.dto.PaginacioParamsDto;
import es.caib.helium.logic.intf.dto.PersonaDto;
import es.caib.helium.logic.intf.service.EntornAreaService;

/**
 * EJB per a EntornAraeService.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
@Interceptors(SpringBeanAutowiringInterceptor.class)
public class EntornAreaServiceBean implements EntornAreaService {

	@Autowired
	EntornAreaService delegate;

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<EntornAreaDto> findAreesByEntorn(Long entornId) {
		return delegate.findAreesByEntorn(entornId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<EntornAreaDto> findPossiblesParesByEntorn(Long entornId, Long id) {
		return delegate.findPossiblesParesByEntorn(entornId, id);
	}
	
	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<PersonaDto> findPersones() {
		return delegate.findPersones();
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public EntornAreaDto findAmbId(Long entornId, Long id) {
		return delegate.findAmbId(entornId, id);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public EntornAreaDto create(Long entornId, EntornAreaDto entornArea) {
		return delegate.create(entornId, entornArea);
	}
	
	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public EntornAreaDto update(Long entornId, EntornAreaDto entornArea) {
		return delegate.update(entornId, entornArea);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void delete(Long entornAreaId) {
		delegate.delete(entornAreaId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public EntornAreaDto findAmbCodiByEntorn(String codi, Long entornId) {
		return delegate.findAmbCodiByEntorn(codi, entornId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public PaginaDto<EntornAreaDto> findPerDatatable(PaginacioParamsDto paginacioParams) {
		return delegate.findPerDatatable(paginacioParams);
	}
}
