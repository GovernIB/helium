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
	public List<ReproDto> findReprosByUsuariTipusExpedient(Long expedientTipusId) {
		return delegate.findReprosByUsuariTipusExpedient(expedientTipusId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public ReproDto findById(Long id) {
		return delegate.findById(id);
	}
	
	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public ReproDto create(Long expedientTipusId, String nom, Map<String, Object> valors) {
		return delegate.create(expedientTipusId, nom, valors);
	}
	
	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void deleteById(Long id) {
		delegate.deleteById(id);
	}
}
