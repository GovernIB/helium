package es.caib.helium.ejb;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;

import es.caib.helium.ejb.base.AbstractServiceEjb;
import lombok.experimental.Delegate;

import es.caib.helium.commons.dto.AreaJbpmIdDto;
import es.caib.helium.commons.dto.PaginaDto;
import es.caib.helium.commons.dto.PaginacioParamsDto;
import es.caib.helium.logic.intf.service.AreaService;

/**
 * EJB per a AreaService.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
public class AreaServiceBean extends AbstractServiceEjb<AreaService> implements AreaService {

	@Delegate
	AreaService delegateService;

	protected void setDelegateService(AreaService delegateService) {
		this.delegateService = delegateService;
	}

	@Override
	@RolesAllowed({"HEL_ADMIN"})
	public AreaJbpmIdDto findAmbId(Long id) {
		return delegateService.findAmbId(id);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN"})
	public AreaJbpmIdDto findAmbCodi(String codi) {
		return delegateService.findAmbCodi(codi);
	}

	@Override

	public PaginaDto<AreaJbpmIdDto> findConfigurades(PaginacioParamsDto paginacioParams) {
		return delegateService.findConfigurades(paginacioParams);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN"})
	public PaginaDto<AreaJbpmIdDto> findSenseConfigurar(PaginacioParamsDto paginacioParams) {
		return delegateService.findSenseConfigurar(paginacioParams);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN"})
	public AreaJbpmIdDto create(AreaJbpmIdDto area) {
		return delegateService.create(area);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN"})
	public void delete(Long areaId) {
		delegateService.delete(areaId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN"})
	public AreaJbpmIdDto update(AreaJbpmIdDto area) {
		return delegateService.update(area);
	}

}
