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

import es.caib.portafib.ws.callback.api.v1.CallBackException;
import es.caib.portafib.ws.callback.api.v1.PortaFIBCallBackWs;
import es.caib.portafib.ws.callback.api.v1.PortaFIBEvent;
import net.conselldemallorca.helium.v3.core.service.ws.PortaFIBCallBackWsImpl;

/**
 * EJB pel servei de callback de portafirmes amb interfície PORTAFIB.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
@WebService(
		name = "PortaFIBCallBackWs",
		serviceName = "PortaFIBCallBackWsService",
		portName = "PortaFIBCallBackWs",
		targetNamespace = "http://v1.server.callback.ws.portafib.caib.es/")
@WebContext(
		contextRoot = "/ws/v1",
		urlPattern = "/PortaFIBCallBack",
		transportGuarantee = "NONE",
		secureWSDLAccess = false)
@Interceptors(SpringBeanAutowiringInterceptor.class)
public class PortaFIBCallbackWsBean implements PortaFIBCallBackWs {

	@Autowired
	private PortaFIBCallBackWsImpl delegate;

	@Override
	public int getVersionWs() {
		return delegate.getVersionWs();
	}

	@Override
	public void event(PortaFIBEvent event) throws CallBackException {
		delegate.event(event);
	}

}
