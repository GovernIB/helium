/**
 *
 */
package es.caib.helium.ejb;

import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;

import es.caib.helium.ejb.base.AbstractServiceEjb;
import lombok.experimental.Delegate;

import es.caib.helium.commons.dto.DominiDto;
import es.caib.helium.commons.dto.PaginaDto;
import es.caib.helium.commons.dto.PaginacioParamsDto;
import es.caib.helium.commons.exception.NoTrobatException;
import es.caib.helium.commons.exception.PermisDenegatException;
import es.caib.helium.logic.intf.service.DominiService;

/**
 * Servei per a gestionar els dominins.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
public class DominiServiceBean extends AbstractServiceEjb<DominiService> implements DominiService {

	@Delegate
	DominiService delegateService;

	protected void setDelegateService(DominiService delegateService) {
		this.delegateService = delegateService;
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public PaginaDto<DominiDto> findPerDatatable(
			Long entornId,
			Long expedientTipusId,
			boolean incloureGlobals,
			String filtre,
			PaginacioParamsDto paginacioParams) throws NoTrobatException {
		return delegateService.findPerDatatable(entornId, expedientTipusId, incloureGlobals, filtre, paginacioParams);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public DominiDto create(Long entornId, Long expedientTipusId, DominiDto domini)
			throws PermisDenegatException {
		return delegateService.create(entornId, expedientTipusId, domini);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public DominiDto findAmbCodi(
			Long entornId,
			Long expedientTipusId, String codi)
			throws NoTrobatException {
		return delegateService.findAmbCodi(entornId, expedientTipusId, codi);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void delete(Long dominiId) throws NoTrobatException, PermisDenegatException {
		delegateService.delete(dominiId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public DominiDto findAmbId(Long expedientTipusId, Long dominiId) throws NoTrobatException {
		return delegateService.findAmbId(expedientTipusId, dominiId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<DominiDto> findGlobals(Long entornId) throws NoTrobatException, PermisDenegatException {
		return delegateService.findGlobals(entornId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public DominiDto update(DominiDto domini)
			throws NoTrobatException, PermisDenegatException {
		return delegateService.update(domini);
	}

}