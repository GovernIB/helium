/**
 * 
 */
package net.conselldemallorca.helium.v3.core.ejb;

import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ejb.interceptor.SpringBeanAutowiringInterceptor;

import es.caib.emiserv.logic.intf.exception.NoTrobatException;
import es.caib.emiserv.logic.intf.exception.PermisDenegatException;
import es.caib.helium.logic.intf.dto.PaginaDto;
import es.caib.helium.logic.intf.dto.PaginacioParamsDto;
import es.caib.helium.logic.intf.dto.TerminiDto;
import es.caib.helium.logic.intf.service.TerminiService;

/**
 * Servei per a gestionar els tipus d'expedient.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
@Interceptors(SpringBeanAutowiringInterceptor.class)
public class TerminiServiceBean implements TerminiService {

	@Autowired
	TerminiService delegate;

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public TerminiDto findAmbId(Long expedientTipusId, Long terminiId) {
		return delegate.findAmbId(expedientTipusId, terminiId);
	}
	
	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public TerminiDto findAmbCodi(
			Long expedientTipusId, 
			Long definicioProcesId, 
			String codi) {
		return delegate.findAmbCodi(expedientTipusId, definicioProcesId, codi);
	}

	
	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<TerminiDto> findAll(
			Long expedientTipusId,
			Long definicioProcesId) throws NoTrobatException, PermisDenegatException {
		return delegate.findAll(expedientTipusId, definicioProcesId);
	}
	
	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public TerminiDto create(
			Long expedientTipusId,
			Long definicioProcesId, 
			TerminiDto termini) {
		return delegate.create(expedientTipusId, definicioProcesId, termini);
		
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public TerminiDto update(TerminiDto termini) {
		return delegate.update(termini);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void delete(Long terminiId) throws NoTrobatException, PermisDenegatException {
		delegate.delete(terminiId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public PaginaDto<TerminiDto> findPerDatatable(
			Long expedientTipusId,
			Long definicioProcesId, 
			String filtre,
			PaginacioParamsDto paginacioParams) throws NoTrobatException {
		return delegate.findPerDatatable(expedientTipusId, definicioProcesId, filtre, paginacioParams);
	}
}