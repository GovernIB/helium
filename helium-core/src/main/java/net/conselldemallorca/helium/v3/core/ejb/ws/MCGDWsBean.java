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

import net.conselldemallorca.helium.v3.core.service.ws.callbackmcgd.CallbackRequest;
import net.conselldemallorca.helium.v3.core.service.ws.callbackmcgd.CallbackResponse;
import net.conselldemallorca.helium.v3.core.service.ws.callbackmcgd.MCGDws;
import net.conselldemallorca.helium.v3.core.service.ws.callbackmcgd.MCGDwsImpl;

/**
 * EJB pel servei de callback de portafirmes amb interfície MCGD.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
@WebService(
		name = "MCGDws",
		serviceName = "MCGDwsService",
		portName = "MCGDwsServicePort",
		targetNamespace = "http://www.indra.es/portafirmasmcgdws/mcgdws")
@WebContext(
		contextRoot = "/helium/services",
		urlPattern = "/MCGDws",
		transportGuarantee = "NONE",
		secureWSDLAccess = false)
@Interceptors(SpringBeanAutowiringInterceptor.class)
public class MCGDWsBean implements MCGDws {

	@Autowired
	private MCGDwsImpl delegate;

	@Override
	public CallbackResponse callback(CallbackRequest callbackRequest) {
		return delegate.callback(callbackRequest);
	}

}
