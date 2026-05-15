package es.caib.helium.ejb;

import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;

import es.caib.helium.ejb.base.AbstractServiceEjb;
import lombok.experimental.Delegate;

import es.caib.helium.commons.dto.AccioDto;
import es.caib.helium.commons.dto.PaginaDto;
import es.caib.helium.commons.dto.PaginacioParamsDto;
import es.caib.helium.commons.exception.NoTrobatException;
import es.caib.helium.commons.exception.PermisDenegatException;
import es.caib.helium.logic.intf.service.AccioService;

/**
 * Servei per a gestionar els tipus d'expedient.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
public class AccioServiceBean extends AbstractServiceEjb<AccioService> implements AccioService {

	@Delegate
	AccioService delegateService;

	protected void setDelegateService(AccioService delegateService) {
		this.delegateService = delegateService;
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public AccioDto create(
			Long expedientTipusId,
			Long definicioProcesId,
			AccioDto accio) throws PermisDenegatException {
		return delegateService.create(expedientTipusId, definicioProcesId, accio);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public AccioDto update(AccioDto accio) throws NoTrobatException, PermisDenegatException {
		return delegateService.update(accio);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void delete(Long accioAccioId) throws NoTrobatException, PermisDenegatException {
		delegateService.delete(accioAccioId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public AccioDto findAmbId(Long expedientTipusId, Long id) throws NoTrobatException {
		return delegateService.findAmbId(expedientTipusId, id);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<AccioDto> findAll(
			Long expedientTipusId,
			Long definicioProcesId) throws NoTrobatException, PermisDenegatException {
		return delegateService.findAll(expedientTipusId, definicioProcesId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public AccioDto findAmbCodi(
			Long tipusExpedientId,
			Long definicioProcesId,
			String codi) throws NoTrobatException {
		return delegateService.findAmbCodi(tipusExpedientId, definicioProcesId, codi);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public PaginaDto<AccioDto> findPerDatatable(
			Long expedientTipusId,
			Long definicioProcesId,
			String filtre,
			PaginacioParamsDto paginacioParams) throws NoTrobatException {
		return delegateService.findPerDatatable(
				expedientTipusId,
				expedientTipusId,
				filtre,
				paginacioParams);
	}

}
