package es.caib.helium.ejb;

import es.caib.helium.logic.intf.dto.EntornTipusAreaDto;
import es.caib.helium.logic.intf.dto.PaginaDto;
import es.caib.helium.logic.intf.dto.PaginacioParamsDto;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import java.util.List;

/**
 * EJB per a EntornTipusAreaService.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
public class EntornTipusAreaService extends AbstractService<es.caib.helium.logic.intf.service.EntornTipusAreaService> implements es.caib.helium.logic.intf.service.EntornTipusAreaService {

	@Override
	public PaginaDto<EntornTipusAreaDto> findPerDatatable(PaginacioParamsDto paginacioParams) {
		return getDelegateService().findPerDatatable(paginacioParams);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom" })
	public List<EntornTipusAreaDto> findTipusAreaByEntorn(Long entornId) {
		return getDelegateService().findTipusAreaByEntorn(entornId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom" })
	public EntornTipusAreaDto create(Long entornId, EntornTipusAreaDto entornTipusArea) {
		return getDelegateService().create(entornId, entornTipusArea);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom" })
	public void delete(Long entornTipusAreaId) {
		getDelegateService().delete(entornTipusAreaId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom" })
	public EntornTipusAreaDto findAmbCodi(String codi) {
		return getDelegateService().findAmbCodi(codi);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public EntornTipusAreaDto findAmbId(Long entornId, Long id) {
		return getDelegateService().findAmbId(entornId, id);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public EntornTipusAreaDto update(Long entornId, EntornTipusAreaDto entornTipusArea) {
		return getDelegateService().update(entornId, entornTipusArea);
	}

}
