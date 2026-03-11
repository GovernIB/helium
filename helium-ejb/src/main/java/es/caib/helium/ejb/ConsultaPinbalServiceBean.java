package es.caib.helium.ejb;

import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;

import org.springframework.beans.factory.annotation.Autowired;

import es.caib.helium.commons.dto.PaginaDto;
import es.caib.helium.commons.dto.PaginacioParamsDto;
import es.caib.helium.commons.dto.PeticioPinbalDto;
import es.caib.helium.commons.dto.PeticioPinbalFiltreDto;
import es.caib.helium.commons.dto.ServeiPinbalDto;
import es.caib.helium.logic.intf.service.ConsultaPinbalService;

@Stateless
//@Interceptors(SpringBeanAutowiringInterceptor.class)
public class ConsultaPinbalServiceBean implements ConsultaPinbalService {

	@Autowired ConsultaPinbalService delegate;

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public PaginaDto<PeticioPinbalDto> findAmbFiltrePaginat(PaginacioParamsDto paginacioParams, PeticioPinbalFiltreDto filtreDto) {
		return delegate.findAmbFiltrePaginat(paginacioParams, filtreDto);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public PeticioPinbalDto findById(Long peticioPinbalId) {
		return delegate.findById(peticioPinbalId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<PeticioPinbalDto> findConsultesPinbalPerExpedient(Long expedientId) {
		return delegate.findConsultesPinbalPerExpedient(expedientId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public PeticioPinbalDto findByExpedientAndDocumentStore(Long expedientId, Long documentStoreId) {
		return delegate.findByExpedientAndDocumentStore(expedientId, documentStoreId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN"})
	public PaginaDto<ServeiPinbalDto> findServeisPinbalAmbFiltrePaginat(PaginacioParamsDto paginacioParams) {
		return delegate.findServeisPinbalAmbFiltrePaginat(paginacioParams);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN"})
	public ServeiPinbalDto findServeiPinbalById(Long id) {
		return delegate.findServeiPinbalById(id);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN"})
	public ServeiPinbalDto updateServeiPinbal(ServeiPinbalDto serveiPinbalDto) {
		return delegate.updateServeiPinbal(serveiPinbalDto);
	}
}