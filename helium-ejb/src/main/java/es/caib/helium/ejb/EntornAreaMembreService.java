package es.caib.helium.ejb;

import es.caib.helium.logic.intf.dto.AreaMembreDto;
import es.caib.helium.logic.intf.dto.PaginaDto;
import es.caib.helium.logic.intf.dto.PaginacioParamsDto;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;

/**
 * EJB per a EntornAreaMembreService.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
public class EntornAreaMembreService extends AbstractService<es.caib.helium.logic.intf.service.EntornAreaMembreService> implements es.caib.helium.logic.intf.service.EntornAreaMembreService {

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public PaginaDto<AreaMembreDto> findPerDatatable(Long entornAreaId, PaginacioParamsDto paginacioParams) {
		return getDelegateService().findPerDatatable(entornAreaId, paginacioParams);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public AreaMembreDto create(Long entornId, Long carrecId, AreaMembreDto areaMembre) {
		return getDelegateService().create(entornId, carrecId, areaMembre);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void delete(Long entornAreaId, Long id) {
		getDelegateService().delete(entornAreaId, id);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public AreaMembreDto findAmbCodiAndAreaId(String codi, Long areaId) {
		return getDelegateService().findAmbCodiAndAreaId(codi, areaId);
	}

}
