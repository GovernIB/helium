package es.caib.helium.ejb;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;

import es.caib.helium.ejb.base.AbstractServiceEjb;
import lombok.experimental.Delegate;

import es.caib.helium.commons.dto.AreaMembreDto;
import es.caib.helium.commons.dto.PaginaDto;
import es.caib.helium.commons.dto.PaginacioParamsDto;
import es.caib.helium.logic.intf.service.EntornAreaMembreService;

/**
 * EJB per a EntornAreaMembreService.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
public class EntornAreaMembreServiceBean extends AbstractServiceEjb<EntornAreaMembreService> implements EntornAreaMembreService {

	@Delegate
	EntornAreaMembreService delegateService;

	protected void setDelegateService(EntornAreaMembreService delegateService) {
		this.delegateService = delegateService;
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public PaginaDto<AreaMembreDto> findPerDatatable(Long entornAreaId, PaginacioParamsDto paginacioParams) {
		return delegateService.findPerDatatable(entornAreaId, paginacioParams);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public AreaMembreDto create(Long entornId, Long carrecId, AreaMembreDto areaMembre) {
		return delegateService.create(entornId, carrecId, areaMembre);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void delete(Long entornAreaId, Long id) {
		delegateService.delete(entornAreaId, id);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public AreaMembreDto findAmbCodiAndAreaId(String codi, Long areaId) {
		return delegateService.findAmbCodiAndAreaId(codi, areaId);
	}

}
