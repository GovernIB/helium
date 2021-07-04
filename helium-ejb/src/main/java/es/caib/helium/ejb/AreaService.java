package es.caib.helium.ejb;

import es.caib.helium.logic.intf.dto.AreaJbpmIdDto;
import es.caib.helium.logic.intf.dto.PaginaDto;
import es.caib.helium.logic.intf.dto.PaginacioParamsDto;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;

/**
 * EJB per a AreaService.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
public class AreaService extends AbstractService<es.caib.helium.logic.intf.service.AreaService> implements es.caib.helium.logic.intf.service.AreaService {

	@Override
	@RolesAllowed({"HEL_ADMIN"})
	public AreaJbpmIdDto findAmbId(Long id) {
		return getDelegateService().findAmbId(id);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN"})
	public AreaJbpmIdDto findAmbCodi(String codi) {
		return getDelegateService().findAmbCodi(codi);
	}
	
	@Override
	
	public PaginaDto<AreaJbpmIdDto> findConfigurades(PaginacioParamsDto paginacioParams) {
		return getDelegateService().findConfigurades(paginacioParams);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN"})
	public PaginaDto<AreaJbpmIdDto> findSenseConfigurar(PaginacioParamsDto paginacioParams) {
		return getDelegateService().findSenseConfigurar(paginacioParams);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN"})
	public AreaJbpmIdDto create(AreaJbpmIdDto area) {
		return getDelegateService().create(area);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN"})
	public void delete(Long areaId) {
		getDelegateService().delete(areaId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN"})
	public AreaJbpmIdDto update(AreaJbpmIdDto area) {
		// TODO Auto-generated method stub
		return null;
	}

}
