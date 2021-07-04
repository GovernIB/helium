package es.caib.helium.ejb;

import es.caib.helium.logic.intf.dto.EntornAreaDto;
import es.caib.helium.logic.intf.dto.PaginaDto;
import es.caib.helium.logic.intf.dto.PaginacioParamsDto;
import es.caib.helium.logic.intf.dto.PersonaDto;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import java.util.List;

/**
 * EJB per a EntornAraeService.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
public class EntornAreaService extends AbstractService<es.caib.helium.logic.intf.service.EntornAreaService> implements es.caib.helium.logic.intf.service.EntornAreaService {

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<EntornAreaDto> findAreesByEntorn(Long entornId) {
		return getDelegateService().findAreesByEntorn(entornId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<EntornAreaDto> findPossiblesParesByEntorn(Long entornId, Long id) {
		return getDelegateService().findPossiblesParesByEntorn(entornId, id);
	}
	
	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<PersonaDto> findPersones() {
		return getDelegateService().findPersones();
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public EntornAreaDto findAmbId(Long entornId, Long id) {
		return getDelegateService().findAmbId(entornId, id);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public EntornAreaDto create(Long entornId, EntornAreaDto entornArea) {
		return getDelegateService().create(entornId, entornArea);
	}
	
	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public EntornAreaDto update(Long entornId, EntornAreaDto entornArea) {
		return getDelegateService().update(entornId, entornArea);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void delete(Long entornAreaId) {
		getDelegateService().delete(entornAreaId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public EntornAreaDto findAmbCodiByEntorn(String codi, Long entornId) {
		return getDelegateService().findAmbCodiByEntorn(codi, entornId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public PaginaDto<EntornAreaDto> findPerDatatable(PaginacioParamsDto paginacioParams) {
		return getDelegateService().findPerDatatable(paginacioParams);
	}
}
