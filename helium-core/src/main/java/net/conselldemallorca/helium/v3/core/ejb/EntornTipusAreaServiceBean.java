package net.conselldemallorca.helium.v3.core.ejb;

import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ejb.interceptor.SpringBeanAutowiringInterceptor;

import es.caib.helium.logic.intf.dto.EntornTipusAreaDto;
import es.caib.helium.logic.intf.dto.PaginaDto;
import es.caib.helium.logic.intf.dto.PaginacioParamsDto;
import es.caib.helium.logic.intf.service.EntornTipusAreaService;

/**
 * EJB per a EntornTipusAreaService.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
@Interceptors(SpringBeanAutowiringInterceptor.class)
public class EntornTipusAreaServiceBean implements EntornTipusAreaService {

	@Override
	public PaginaDto<EntornTipusAreaDto> findPerDatatable(PaginacioParamsDto paginacioParams) {
		return delegate.findPerDatatable(paginacioParams);
	}

	@Autowired
	EntornTipusAreaService delegate;

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom" })
	public List<EntornTipusAreaDto> findTipusAreaByEntorn(Long entornId) {
		return delegate.findTipusAreaByEntorn(entornId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom" })
	public EntornTipusAreaDto create(Long entornId, EntornTipusAreaDto entornTipusArea) {
		return delegate.create(entornId, entornTipusArea);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom" })
	public void delete(Long entornTipusAreaId) {
		delegate.delete(entornTipusAreaId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom" })
	public EntornTipusAreaDto findAmbCodi(String codi) {
		return delegate.findAmbCodi(codi);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public EntornTipusAreaDto findAmbId(Long entornId, Long id) {
		return delegate.findAmbId(entornId, id);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public EntornTipusAreaDto update(Long entornId, EntornTipusAreaDto entornTipusArea) {
		return delegate.update(entornId, entornTipusArea);
	}

}
