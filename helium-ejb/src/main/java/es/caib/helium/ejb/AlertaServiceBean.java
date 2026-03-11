package es.caib.helium.ejb;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;

import org.springframework.beans.factory.annotation.Autowired;

import es.caib.helium.commons.dto.AlertaDto;
import es.caib.helium.logic.intf.service.AlertaService;

@Stateless
//@Interceptors(SpringBeanAutowiringInterceptor.class)
public class AlertaServiceBean implements AlertaService {
	
	@Autowired
	AlertaService delegate;
	
	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public AlertaDto marcarLlegida(Long alertaId) {
		return delegate.marcarLlegida(alertaId);
	}
	
	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public AlertaDto marcarNoLlegida(Long alertaId) {
		return delegate.marcarNoLlegida(alertaId);
	}
	
	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public AlertaDto marcarEsborrada(Long alertaId) {
		return delegate.marcarEsborrada(alertaId);
	}
}
