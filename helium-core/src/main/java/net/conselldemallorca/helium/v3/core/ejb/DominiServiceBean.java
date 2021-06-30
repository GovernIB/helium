/**
 * 
 */
package net.conselldemallorca.helium.v3.core.ejb;

import java.util.List;
import java.util.Map;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ejb.interceptor.SpringBeanAutowiringInterceptor;

import es.caib.emiserv.logic.intf.exception.NoTrobatException;
import es.caib.emiserv.logic.intf.exception.PermisDenegatException;
import es.caib.emiserv.logic.intf.extern.domini.FilaResultat;
import es.caib.helium.logic.intf.dto.DominiDto;
import es.caib.helium.logic.intf.dto.PaginaDto;
import es.caib.helium.logic.intf.dto.PaginacioParamsDto;
import es.caib.helium.logic.intf.service.DominiService;

/**
 * Servei per a gestionar els dominins.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
@Interceptors(SpringBeanAutowiringInterceptor.class)
public class DominiServiceBean implements DominiService {

	@Autowired
	DominiService delegate;

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public PaginaDto<DominiDto> findPerDatatable(
			Long entornId,
			Long expedientTipusId,
			boolean incloureGlobals,
			String filtre,
			PaginacioParamsDto paginacioParams) throws NoTrobatException {
		return delegate.findPerDatatable(entornId, expedientTipusId, incloureGlobals, filtre, paginacioParams);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public DominiDto create(Long entornId, Long expedientTipusId, DominiDto domini)
			throws PermisDenegatException {
		return delegate.create(entornId, expedientTipusId, domini);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public DominiDto findAmbCodi(
			Long entornId,
			Long expedientTipusId, String codi)
			throws NoTrobatException {
		return delegate.findAmbCodi(entornId, expedientTipusId, codi);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void delete(Long dominiId) throws NoTrobatException, PermisDenegatException {
		delegate.delete(dominiId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public DominiDto findAmbId(Long expedientTipusId, Long dominiId) throws NoTrobatException {
		return delegate.findAmbId(expedientTipusId, dominiId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<DominiDto> findGlobals(Long entornId) throws NoTrobatException, PermisDenegatException {
		return delegate.findGlobals(entornId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public DominiDto update(DominiDto domini)
			throws NoTrobatException, PermisDenegatException {
		return delegate.update(domini);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<FilaResultat> consultaDomini(Long entornId, Long dominiId, String dominiWsId,
			Map<String, Object> params) {
		return delegate.consultaDomini(entornId, dominiId, dominiWsId, params);
	}
}