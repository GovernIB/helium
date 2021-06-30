package net.conselldemallorca.helium.v3.core.ejb;

import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ejb.interceptor.SpringBeanAutowiringInterceptor;

import es.caib.helium.logic.intf.dto.CarrecDto;
import es.caib.helium.logic.intf.dto.PaginaDto;
import es.caib.helium.logic.intf.dto.PaginacioParamsDto;
import es.caib.helium.logic.intf.service.EntornCarrecService;

/**
 * EJB per a EntornCarrecService.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
@Interceptors(SpringBeanAutowiringInterceptor.class)
public class EntornCarrecServiceBean implements EntornCarrecService {
	
	@Autowired
	EntornCarrecService delegate;

	@Override
	public PaginaDto<CarrecDto> findPerDatatable(PaginacioParamsDto paginacioParams) {
		return delegate.findPerDatatable(paginacioParams);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<CarrecDto> findCarrecsByEntorn(Long entornId) {
		return delegate.findCarrecsByEntorn(entornId);
	}
	
	@Override
	public List<CarrecDto> findCarrecsByEntornAndArea(Long entornId, Long areaId) {
		return delegate.findCarrecsByEntornAndArea(entornId, areaId);
	}
	
	@Override
	public CarrecDto findAmbId(Long entornId, Long id) {
		return delegate.findAmbId(entornId, id);
	}

	@Override
	public CarrecDto findByEntornAndCodi(Long entornId, String codi) {
		return delegate.findByEntornAndCodi(entornId, codi);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public CarrecDto create(Long entornId, CarrecDto entornCarrec) {
		return delegate.create(entornId, entornCarrec);
	}
	
	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public CarrecDto update(Long entornId, CarrecDto entornCarrec) {
		return delegate.create(entornId, entornCarrec);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void delete(Long entornCarrecId) {
		delegate.delete(entornCarrecId);
	}

}
