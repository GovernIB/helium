package net.conselldemallorca.helium.v3.core.ejb;

import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ejb.interceptor.SpringBeanAutowiringInterceptor;

import net.conselldemallorca.helium.v3.core.api.dto.PaginaDto;
import net.conselldemallorca.helium.v3.core.api.dto.PaginacioParamsDto;
import net.conselldemallorca.helium.v3.core.api.dto.PeticioPinbalDto;
import net.conselldemallorca.helium.v3.core.api.dto.PeticioPinbalFiltreDto;
import net.conselldemallorca.helium.v3.core.api.dto.ScspRespostaPinbal;
import net.conselldemallorca.helium.v3.core.api.service.ConsultaPinbalService;

@Stateless
@Interceptors(SpringBeanAutowiringInterceptor.class)
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
	public ScspRespostaPinbal tractamentPeticioAsincronaPendentPinbal(Long peticioPinbalId) {
		return delegate.tractamentPeticioAsincronaPendentPinbal(peticioPinbalId);
	}
}