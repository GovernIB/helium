package net.conselldemallorca.helium.v3.core.ejb;

import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ejb.interceptor.SpringBeanAutowiringInterceptor;

import net.conselldemallorca.helium.v3.core.api.dto.PaginaDto;
import net.conselldemallorca.helium.v3.core.api.dto.PaginacioParamsDto;
import net.conselldemallorca.helium.v3.core.api.dto.procediment.ProcedimentDto;
import net.conselldemallorca.helium.v3.core.api.dto.procediment.ProcedimentFiltreDto;
import net.conselldemallorca.helium.v3.core.api.service.ProcedimentService;


/**
 * Implementació de ProcedimentService com a EJB que empra una clase
 * delegada per accedir a la funcionalitat del servei.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */

@Stateless
@Interceptors(SpringBeanAutowiringInterceptor.class)
public class ProcedimentServiceBean implements ProcedimentService {
	
	@Autowired
	private ProcedimentService delegate;

	@Override
	public PaginaDto<ProcedimentDto> findAmbFiltre(ProcedimentFiltreDto filtre,
			PaginacioParamsDto paginacioParams) {
		return delegate.findAmbFiltre(filtre, paginacioParams);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN"})
	public void findAndUpdateProcediments() throws Exception {
		delegate.findAndUpdateProcediments();
	}

	@Override
	public ProcedimentDto findByCodiSia(String codiSia) {
		return delegate.findByCodiSia(codiSia);		
	}

	@Override
	public List<ProcedimentDto> findByNomOrCodiSia(String nom) {
		return delegate.findByNomOrCodiSia(nom);
	}
}
