package net.conselldemallorca.helium.v3.core.ejb;

import javax.ejb.Stateless;
import javax.interceptor.Interceptors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ejb.interceptor.SpringBeanAutowiringInterceptor;

import net.conselldemallorca.helium.v3.core.api.dto.PaginaDto;
import net.conselldemallorca.helium.v3.core.api.dto.PaginacioParamsDto;
import net.conselldemallorca.helium.v3.core.api.dto.PeticioPinbalDto;
import net.conselldemallorca.helium.v3.core.api.dto.PeticioPinbalFiltreDto;
import net.conselldemallorca.helium.v3.core.api.service.ConsultaPinbalService;

@Stateless
@Interceptors(SpringBeanAutowiringInterceptor.class)
public class ConsultaPinbalServiceBean implements ConsultaPinbalService {

	@Autowired ConsultaPinbalService delegate;

	@Override
	public PaginaDto<PeticioPinbalDto> findAmbFiltrePaginat(PaginacioParamsDto paginacioParams,
			PeticioPinbalFiltreDto filtreDto) {
		return delegate.findAmbFiltrePaginat(paginacioParams, filtreDto);
	}

	@Override
	public PeticioPinbalDto findById(Long peticioPinbalId) {
		return delegate.findById(peticioPinbalId);
	}
}