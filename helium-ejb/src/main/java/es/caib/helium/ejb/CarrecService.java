package es.caib.helium.ejb;

import es.caib.helium.logic.intf.dto.CarrecJbpmIdDto;
import es.caib.helium.logic.intf.dto.PaginaDto;
import es.caib.helium.logic.intf.dto.PaginacioParamsDto;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;

/**
 * EJB per a CarrecService.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
public class CarrecService extends AbstractService<es.caib.helium.logic.intf.service.CarrecService> implements es.caib.helium.logic.intf.service.CarrecService {
	
	@Override
	@RolesAllowed({"HEL_ADMIN"})
	public PaginaDto<CarrecJbpmIdDto> findConfigurats(PaginacioParamsDto paginacioParams) {
		return getDelegateService().findConfigurats(paginacioParams);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN"})
	public PaginaDto<CarrecJbpmIdDto> findSenseConfigurar(PaginacioParamsDto params) {
		// TODO Auto-generated method stub
		return getDelegateService().findSenseConfigurar(params);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN"})
	public CarrecJbpmIdDto findAmbId(Long id) {
		return getDelegateService().findAmbId(id);
	}
	
	@Override
	@RolesAllowed({"HEL_ADMIN"})
	public CarrecJbpmIdDto findAmbCodi(String codi) {
		return getDelegateService().findAmbCodi(codi);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN"})
	public CarrecJbpmIdDto create(CarrecJbpmIdDto carrec) {
		// TODO Auto-generated method stub
		return getDelegateService().create(carrec);
	}
	
	@Override
	@RolesAllowed({"HEL_ADMIN"})
	public CarrecJbpmIdDto update(CarrecJbpmIdDto carrec) {
		return getDelegateService().update(carrec);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN"})
	public void delete(Long carrecId) {
		getDelegateService().delete(carrecId);	
	}

}
