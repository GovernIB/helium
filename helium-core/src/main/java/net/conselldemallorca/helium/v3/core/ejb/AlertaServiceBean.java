package net.conselldemallorca.helium.v3.core.ejb;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ejb.interceptor.SpringBeanAutowiringInterceptor;

import es.caib.helium.logic.intf.dto.AlertaDto;
import es.caib.helium.logic.intf.service.AlertaService;

@Stateless
@Interceptors(SpringBeanAutowiringInterceptor.class)
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
