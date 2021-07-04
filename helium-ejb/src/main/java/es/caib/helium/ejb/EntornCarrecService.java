package es.caib.helium.ejb;

import es.caib.helium.logic.intf.dto.CarrecDto;
import es.caib.helium.logic.intf.dto.PaginaDto;
import es.caib.helium.logic.intf.dto.PaginacioParamsDto;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import java.util.List;

/**
 * EJB per a EntornCarrecService.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
public class EntornCarrecService extends AbstractService<es.caib.helium.logic.intf.service.EntornCarrecService> implements es.caib.helium.logic.intf.service.EntornCarrecService {
	
	@Override
	public PaginaDto<CarrecDto> findPerDatatable(PaginacioParamsDto paginacioParams) {
		return getDelegateService().findPerDatatable(paginacioParams);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<CarrecDto> findCarrecsByEntorn(Long entornId) {
		return getDelegateService().findCarrecsByEntorn(entornId);
	}
	
	@Override
	public List<CarrecDto> findCarrecsByEntornAndArea(Long entornId, Long areaId) {
		return getDelegateService().findCarrecsByEntornAndArea(entornId, areaId);
	}
	
	@Override
	public CarrecDto findAmbId(Long entornId, Long id) {
		return getDelegateService().findAmbId(entornId, id);
	}

	@Override
	public CarrecDto findByEntornAndCodi(Long entornId, String codi) {
		return getDelegateService().findByEntornAndCodi(entornId, codi);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public CarrecDto create(Long entornId, CarrecDto entornCarrec) {
		return getDelegateService().create(entornId, entornCarrec);
	}
	
	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public CarrecDto update(Long entornId, CarrecDto entornCarrec) {
		return getDelegateService().create(entornId, entornCarrec);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void delete(Long entornCarrecId) {
		getDelegateService().delete(entornCarrecId);
	}

}
