package net.conselldemallorca.helium.v3.core.ejb;

import java.util.List;
import java.util.Map;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ejb.interceptor.SpringBeanAutowiringInterceptor;

import es.caib.emiserv.logic.intf.exception.NoTrobatException;
import es.caib.emiserv.logic.intf.exception.ValidacioException;
import es.caib.helium.logic.intf.dto.ReproDto;
import es.caib.helium.logic.intf.service.ReproService;

@Stateless
@Interceptors(SpringBeanAutowiringInterceptor.class)
public class ReproServiceBean implements ReproService {
	
	@Autowired
	ReproService delegate;

	
	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<ReproDto> findReprosByUsuariTipusExpedient(Long expedientTipusId, String tascaCodi) {
		return delegate.findReprosByUsuariTipusExpedient(expedientTipusId, tascaCodi);
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
	public String deleteById(Long id) {
		return delegate.deleteById(id);
	}
	
	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public Map<String,Object> findValorsById(Long id) {
		return delegate.findValorsById(id);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public ReproDto createTasca(Long expedientTipusId, Long tascaId, String nom, Map<String, Object> valors)
			throws NoTrobatException, ValidacioException {
		return delegate.createTasca(expedientTipusId, tascaId, nom, valors);
	}
}
