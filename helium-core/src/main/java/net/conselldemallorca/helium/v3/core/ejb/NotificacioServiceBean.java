package net.conselldemallorca.helium.v3.core.ejb;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ejb.interceptor.SpringBeanAutowiringInterceptor;

import net.conselldemallorca.helium.v3.core.api.exception.NoTrobatException;
import net.conselldemallorca.helium.v3.core.api.service.NotificacioService;

@Stateless
@Interceptors(SpringBeanAutowiringInterceptor.class)
public class NotificacioServiceBean implements NotificacioService {
	
	@Autowired
	NotificacioService delegate;

	
	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void reenviarRemesa(Long remesaId, Long expedientTipusId) throws NoTrobatException, Exception {
		delegate.reenviarRemesa(remesaId, expedientTipusId);
	}


	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void refrescarRemesa(Long remesaId) throws NoTrobatException, Exception {
		delegate.refrescarRemesa(remesaId);
	}

}
