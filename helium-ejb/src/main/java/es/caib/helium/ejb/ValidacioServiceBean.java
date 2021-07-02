package es.caib.helium.ejb;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ejb.interceptor.SpringBeanAutowiringInterceptor;

import es.caib.emiserv.logic.intf.exception.NoTrobatException;
import es.caib.emiserv.logic.intf.exception.PermisDenegatException;
import es.caib.helium.logic.intf.dto.PaginaDto;
import es.caib.helium.logic.intf.dto.PaginacioParamsDto;
import es.caib.helium.logic.intf.dto.ValidacioDto;
import es.caib.helium.logic.intf.service.ValidacioService;

@Stateless
@Interceptors(SpringBeanAutowiringInterceptor.class)
public class ValidacioServiceBean implements ValidacioService {
	
	@Autowired
	ValidacioService delegate;

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
