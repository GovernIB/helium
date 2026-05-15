package es.caib.helium.ejb;

import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;

import es.caib.helium.ejb.base.AbstractServiceEjb;
import lombok.experimental.Delegate;

import es.caib.helium.commons.dto.CarrecDto;
import es.caib.helium.commons.dto.PaginaDto;
import es.caib.helium.commons.dto.PaginacioParamsDto;
import es.caib.helium.logic.intf.service.EntornCarrecService;

/**
 * EJB per a EntornCarrecService.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
public class EntornCarrecServiceBean extends AbstractServiceEjb<EntornCarrecService> implements EntornCarrecService {

	@Delegate
	EntornCarrecService delegateService;

	protected void setDelegateService(EntornCarrecService delegateService) {
		this.delegateService = delegateService;
	}

	@Override
	public PaginaDto<CarrecDto> findPerDatatable(PaginacioParamsDto paginacioParams) {
		return delegateService.findPerDatatable(paginacioParams);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<CarrecDto> findCarrecsByEntorn(Long entornId) {
		return delegateService.findCarrecsByEntorn(entornId);
	}

	@Override
	public List<CarrecDto> findCarrecsByEntornAndArea(Long entornId, Long areaId) {
		return delegateService.findCarrecsByEntornAndArea(entornId, areaId);
	}

	@Override
	public CarrecDto findAmbId(Long entornId, Long id) {
		return delegateService.findAmbId(entornId, id);
	}

	@Override
	public CarrecDto findByEntornAndCodi(Long entornId, String codi) {
		return delegateService.findByEntornAndCodi(entornId, codi);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public CarrecDto create(Long entornId, CarrecDto entornCarrec) {
		return delegateService.create(entornId, entornCarrec);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public CarrecDto update(Long entornId, CarrecDto entornCarrec) {
		return delegateService.create(entornId, entornCarrec);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void delete(Long entornCarrecId) {
		delegateService.delete(entornCarrecId);
	}

}
