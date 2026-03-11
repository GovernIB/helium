package es.caib.helium.ejb;

import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;

import org.springframework.beans.factory.annotation.Autowired;

import es.caib.helium.commons.dto.AvisDto;
import es.caib.helium.commons.dto.PaginaDto;
import es.caib.helium.commons.dto.PaginacioParamsDto;
import es.caib.helium.logic.intf.service.AvisService;

/**
 * Implementació de AvisService com a EJB que empra una clase
 * delegada per accedir a la funcionalitat del servei.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
//@Interceptors(SpringBeanAutowiringInterceptor.class)
public class AvisServiceBean implements AvisService {

	@Autowired
	AvisService delegate;
	
	@Override
	@RolesAllowed({"HEL_ADMIN"})
	public AvisDto create(AvisDto avis) {
		return delegate.create(avis);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN"})
	public AvisDto update(AvisDto avis) {
		return delegate.update(avis);
	}
	
	@Override
	@RolesAllowed({"HEL_ADMIN"})
	public AvisDto updateActiva(Long id, boolean activa) {
		return delegate.updateActiva(id, activa);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN"})
	public AvisDto delete(Long id) {
		return delegate.delete(id);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN"})
	public AvisDto findById(Long id) {
		return delegate.findById(id);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public PaginaDto<AvisDto> findPaginat(PaginacioParamsDto paginacioParams) {
		return delegate.findPaginat(paginacioParams);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<AvisDto> findActive() {
		return delegate.findActive();
	}

}
