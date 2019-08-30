/**
 * 
 */
package net.conselldemallorca.helium.v3.core.ejb.ws;

import javax.ejb.Stateless;
import javax.interceptor.Interceptors;
import javax.jws.WebService;

import org.jboss.wsf.spi.annotation.WebContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ejb.interceptor.SpringBeanAutowiringInterceptor;

import es.caib.bantel.ws.v2.model.referenciaentrada.ReferenciasEntrada;
import es.caib.bantel.ws.v2.services.BantelFacade;
import es.caib.bantel.ws.v2.services.BantelFacadeException;

/**
 * EJB pel servei de callback de portafirmes amb interfície MCGD.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
@WebService(
		name = "BantelFacade",
		serviceName = "BantelV2BackofficeService",
		portName = "BantelV2BackofficePort",
		targetNamespace = "http://backoffice.ws.helium.conselldemallorca.net/")
@WebContext(
		contextRoot = "/helium/ws",
		urlPattern = "/NotificacioEntradaV2",
		transportGuarantee = "NONE",
		secureWSDLAccess = false)
@Interceptors(SpringBeanAutowiringInterceptor.class)
public class BantelBackofficeBean implements BantelFacade {

	@Autowired
	private BantelFacade delegate;

	@Override
	public void avisoEntradas(ReferenciasEntrada referencias) throws BantelFacadeException {
		delegate.avisoEntradas(referencias);
	}

}
