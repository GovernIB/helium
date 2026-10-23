package es.caib.helium.ejb;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;

import es.caib.helium.ejb.base.AbstractServiceEjb;
import lombok.experimental.Delegate;

import es.caib.helium.commons.dto.CarrecBpmIdDto;
import es.caib.helium.commons.dto.PaginaDto;
import es.caib.helium.commons.dto.PaginacioParamsDto;
import es.caib.helium.logic.intf.service.CarrecService;

/**
 * EJB per a CarrecService.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
public class CarrecServiceBean extends AbstractServiceEjb<CarrecService> implements CarrecService {

	@Delegate
	CarrecService delegateService;

	protected void setDelegateService(CarrecService delegateService) {
		this.delegateService = delegateService;
	}


	@Override
	@RolesAllowed({"HEL_ADMIN"})
	public PaginaDto<CarrecBpmIdDto> findConfigurats(PaginacioParamsDto paginacioParams) {
		return delegateService.findConfigurats(paginacioParams);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN"})
	public PaginaDto<CarrecBpmIdDto> findSenseConfigurar(PaginacioParamsDto params) {
		// TODO Auto-generated method stub
		return delegateService.findSenseConfigurar(params);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN"})
	public CarrecBpmIdDto findAmbId(Long id) {
		return delegateService.findAmbId(id);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN"})
	public CarrecBpmIdDto findAmbCodi(String codi) {
		return delegateService.findAmbCodi(codi);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN"})
	public CarrecBpmIdDto create(CarrecBpmIdDto carrec) {
		// TODO Auto-generated method stub
		return delegateService.create(carrec);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN"})
	public CarrecBpmIdDto update(CarrecBpmIdDto carrec) {
		return delegateService.update(carrec);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN"})
	public void delete(Long carrecId) {
		delegateService.delete(carrecId);
	}

	@Override
	public CarrecBpmIdDto findAmbCodiAndGrup(String codi, String grup) {
		return delegateService.findAmbCodiAndGrup(codi, grup);
	}

}
