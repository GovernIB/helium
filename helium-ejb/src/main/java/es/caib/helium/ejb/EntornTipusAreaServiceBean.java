package es.caib.helium.ejb;

import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;

import es.caib.helium.ejb.base.AbstractServiceEjb;
import lombok.experimental.Delegate;

import es.caib.helium.commons.dto.EntornTipusAreaDto;
import es.caib.helium.commons.dto.PaginaDto;
import es.caib.helium.commons.dto.PaginacioParamsDto;
import es.caib.helium.logic.intf.service.EntornTipusAreaService;

/**
 * EJB per a EntornTipusAreaService.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
public class EntornTipusAreaServiceBean extends AbstractServiceEjb<EntornTipusAreaService> implements EntornTipusAreaService {

	@Override
	public PaginaDto<EntornTipusAreaDto> findPerDatatable(PaginacioParamsDto paginacioParams) {
		return delegateService.findPerDatatable(paginacioParams);
	}

	@Delegate
	EntornTipusAreaService delegateService;

	protected void setDelegateService(EntornTipusAreaService delegateService) {
		this.delegateService = delegateService;
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom" })
	public List<EntornTipusAreaDto> findTipusAreaByEntorn(Long entornId) {
		return delegateService.findTipusAreaByEntorn(entornId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom" })
	public EntornTipusAreaDto create(Long entornId, EntornTipusAreaDto entornTipusArea) {
		return delegateService.create(entornId, entornTipusArea);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom" })
	public void delete(Long entornTipusAreaId) {
		delegateService.delete(entornTipusAreaId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom" })
	public EntornTipusAreaDto findAmbCodi(String codi) {
		return delegateService.findAmbCodi(codi);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public EntornTipusAreaDto findAmbId(Long entornId, Long id) {
		return delegateService.findAmbId(entornId, id);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public EntornTipusAreaDto update(Long entornId, EntornTipusAreaDto entornTipusArea) {
		return delegateService.update(entornId, entornTipusArea);
	}

}
