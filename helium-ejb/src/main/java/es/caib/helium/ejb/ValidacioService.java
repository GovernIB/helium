package es.caib.helium.ejb;

import es.caib.helium.logic.intf.dto.PaginaDto;
import es.caib.helium.logic.intf.dto.PaginacioParamsDto;
import es.caib.helium.logic.intf.dto.ValidacioDto;
import es.caib.helium.logic.intf.exception.NoTrobatException;
import es.caib.helium.logic.intf.exception.PermisDenegatException;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;

@Stateless
public class ValidacioService extends AbstractService<es.caib.helium.logic.intf.service.ValidacioService> implements es.caib.helium.logic.intf.service.ValidacioService {
	
	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public ValidacioDto validacioCreate(Long campId, ValidacioDto validacio) throws PermisDenegatException {
		return getDelegateService().validacioCreate(campId, validacio);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public ValidacioDto validacioUpdate(ValidacioDto validacio) throws NoTrobatException, PermisDenegatException {
		return getDelegateService().validacioUpdate(validacio);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void validacioDelete(Long id) throws NoTrobatException, PermisDenegatException {
		getDelegateService().validacioDelete(id);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public ValidacioDto validacioFindAmbId(Long id) throws NoTrobatException {
		return getDelegateService().validacioFindAmbId(id);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public PaginaDto<ValidacioDto> validacioFindPerDatatable(Long campId, String filtre,
			PaginacioParamsDto paginacioParams) throws NoTrobatException {
		return getDelegateService().validacioFindPerDatatable(campId, filtre, paginacioParams);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public boolean validacioMourePosicio(Long id, int posicio) {
		return getDelegateService().validacioMourePosicio(id, posicio);
	}
	
}
