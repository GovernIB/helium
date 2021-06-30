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

import es.caib.helium.logic.intf.exception.NoTrobatException;
import es.caib.helium.logic.intf.exception.PermisDenegatException;
import es.caib.helium.logic.intf.dto.AccioDto;
import es.caib.helium.logic.intf.dto.PaginaDto;
import es.caib.helium.logic.intf.dto.PaginacioParamsDto;
import es.caib.helium.logic.intf.service.AccioService;

/**
 * Servei per a gestionar els tipus d'expedient.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
@Interceptors(SpringBeanAutowiringInterceptor.class)
public class AccioServiceBean implements AccioService {

	@Autowired
	AccioService delegate;
	
	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public AccioDto create(
			Long expedientTipusId,
			Long definicioProcesId, 
			AccioDto accio) throws PermisDenegatException {
		return delegate.create(expedientTipusId, definicioProcesId, accio);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public AccioDto update(AccioDto accio) throws NoTrobatException, PermisDenegatException {
		return delegate.update(accio);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void delete(Long accioAccioId) throws NoTrobatException, PermisDenegatException {
		delegate.delete(accioAccioId);		
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public AccioDto findAmbId(Long expedientTipusId, Long id) throws NoTrobatException {
		return delegate.findAmbId(expedientTipusId, id);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<AccioDto> findAll(
			Long expedientTipusId,
			Long definicioProcesId) throws NoTrobatException, PermisDenegatException {
		return delegate.findAll(expedientTipusId, definicioProcesId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public AccioDto findAmbCodi(
			Long tipusExpedientId,
			Long definicioProcesId,
			String codi) throws NoTrobatException {
		return delegate.findAmbCodi(tipusExpedientId, definicioProcesId, codi);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public PaginaDto<AccioDto> findPerDatatable(
			Long expedientTipusId,
			Long definicioProcesId,			
			String filtre,
			PaginacioParamsDto paginacioParams) throws NoTrobatException {
		return delegate.findPerDatatable(
				expedientTipusId,
				expedientTipusId,
				filtre, 
				paginacioParams);
	}
}