package es.caib.helium.ejb;

import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;

import es.caib.helium.ejb.base.AbstractServiceEjb;
import lombok.experimental.Delegate;

import es.caib.helium.commons.dto.EntornAreaDto;
import es.caib.helium.commons.dto.PaginaDto;
import es.caib.helium.commons.dto.PaginacioParamsDto;
import es.caib.helium.commons.dto.PersonaDto;
import es.caib.helium.logic.intf.service.EntornAreaService;

/**
 * EJB per a EntornAraeService.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
public class EntornAreaServiceBean extends AbstractServiceEjb<EntornAreaService> implements EntornAreaService {

	@Delegate
	EntornAreaService delegateService;

	protected void setDelegateService(EntornAreaService delegateService) {
		this.delegateService = delegateService;
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<EntornAreaDto> findAreesByEntorn(Long entornId) {
		return delegateService.findAreesByEntorn(entornId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<EntornAreaDto> findPossiblesParesByEntorn(Long entornId, Long id) {
		return delegateService.findPossiblesParesByEntorn(entornId, id);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<PersonaDto> findPersones() {
		return delegateService.findPersones();
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public EntornAreaDto findAmbId(Long entornId, Long id) {
		return delegateService.findAmbId(entornId, id);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public EntornAreaDto create(Long entornId, EntornAreaDto entornArea) {
		return delegateService.create(entornId, entornArea);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public EntornAreaDto update(Long entornId, EntornAreaDto entornArea) {
		return delegateService.update(entornId, entornArea);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void delete(Long entornAreaId) {
		delegateService.delete(entornAreaId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public EntornAreaDto findAmbCodiByEntorn(String codi, Long entornId) {
		return delegateService.findAmbCodiByEntorn(codi, entornId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public PaginaDto<EntornAreaDto> findPerDatatable(PaginacioParamsDto paginacioParams) {
		return delegateService.findPerDatatable(paginacioParams);
	}

}
