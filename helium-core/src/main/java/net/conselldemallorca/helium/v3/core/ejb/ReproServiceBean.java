package net.conselldemallorca.helium.v3.core.ejb;

import java.util.List;
import java.util.Map;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;

import net.conselldemallorca.helium.v3.core.api.dto.ReproDto;
import net.conselldemallorca.helium.v3.core.api.service.ReproService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ejb.interceptor.SpringBeanAutowiringInterceptor;

@Stateless
@Interceptors(SpringBeanAutowiringInterceptor.class)
public class ReproServiceBean implements ReproService {
	
	@Autowired
	ReproService delegate;

	
	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<ReproDto> findReprosByUsuariEntornTipusExpedient(String usuari, Long entornId, Long expedientTipusId) {
		return delegate.findReprosByUsuariEntornTipusExpedient(usuari, entornId, expedientTipusId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public ReproDto findById(Long id) {
		return delegate.findById(id);
	}
	
	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void create(Long expedientTipusId, String nom, Map<String, Object> valors) {
		delegate.create(expedientTipusId, nom, valors);
	}
}
