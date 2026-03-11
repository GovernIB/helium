package es.caib.helium.ejb;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;

import org.springframework.beans.factory.annotation.Autowired;

import es.caib.helium.commons.dto.PaginaDto;
import es.caib.helium.commons.dto.PaginacioParamsDto;
import es.caib.helium.commons.dto.ValidacioDto;
import es.caib.helium.commons.exception.NoTrobatException;
import es.caib.helium.commons.exception.PermisDenegatException;
import es.caib.helium.logic.intf.service.ValidacioService;

@Stateless
//@Interceptors(SpringBeanAutowiringInterceptor.class)
public class ValidacioServiceBean implements ValidacioService {
	
	@Autowired
	private ValidacioService delegate;

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public ValidacioDto validacioCreate(Long campId, ValidacioDto validacio) throws PermisDenegatException {
		return delegate.validacioCreate(campId, validacio);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public ValidacioDto validacioUpdate(ValidacioDto validacio) throws NoTrobatException, PermisDenegatException {
		return delegate.validacioUpdate(validacio);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void validacioDelete(Long id) throws NoTrobatException, PermisDenegatException {
		delegate.validacioDelete(id);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public ValidacioDto validacioFindAmbId(Long id) throws NoTrobatException {
		return delegate.validacioFindAmbId(id);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public PaginaDto<ValidacioDto> validacioFindPerDatatable(Long campId, String filtre,
			PaginacioParamsDto paginacioParams) throws NoTrobatException {
		return delegate.validacioFindPerDatatable(campId, filtre, paginacioParams);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public boolean validacioMourePosicio(Long id, int posicio) {
		return delegate.validacioMourePosicio(id, posicio);
	}
	
}
