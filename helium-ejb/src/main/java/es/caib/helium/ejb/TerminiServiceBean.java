/**
 *
 */
package es.caib.helium.ejb;

import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;

import es.caib.helium.ejb.base.AbstractServiceEjb;
import lombok.experimental.Delegate;

import es.caib.helium.commons.dto.PaginaDto;
import es.caib.helium.commons.dto.PaginacioParamsDto;
import es.caib.helium.commons.dto.TerminiDto;
import es.caib.helium.commons.exception.NoTrobatException;
import es.caib.helium.commons.exception.PermisDenegatException;
import es.caib.helium.logic.intf.service.TerminiService;

/**
 * Servei per a gestionar els tipus d'expedient.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
public class TerminiServiceBean extends AbstractServiceEjb<TerminiService> implements TerminiService {

	@Delegate
	TerminiService delegateService;

	protected void setDelegateService(TerminiService delegateService) {
		this.delegateService = delegateService;
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public TerminiDto findAmbId(Long expedientTipusId, Long terminiId) {
		return delegateService.findAmbId(expedientTipusId, terminiId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public TerminiDto findAmbCodi(
			Long expedientTipusId,
			Long definicioProcesId,
			String codi) {
		return delegateService.findAmbCodi(expedientTipusId, definicioProcesId, codi);
	}


	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<TerminiDto> findAll(
			Long expedientTipusId,
			Long definicioProcesId) throws NoTrobatException, PermisDenegatException {
		return delegateService.findAll(expedientTipusId, definicioProcesId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public TerminiDto create(
			Long expedientTipusId,
			Long definicioProcesId,
			TerminiDto termini) {
		return delegateService.create(expedientTipusId, definicioProcesId, termini);

	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public TerminiDto update(TerminiDto termini) {
		return delegateService.update(termini);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void delete(Long terminiId) throws NoTrobatException, PermisDenegatException {
		delegateService.delete(terminiId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public PaginaDto<TerminiDto> findPerDatatable(
			Long expedientTipusId,
			Long definicioProcesId,
			String filtre,
			PaginacioParamsDto paginacioParams) throws NoTrobatException {
		return delegateService.findPerDatatable(expedientTipusId, definicioProcesId, filtre, paginacioParams);
	}

}