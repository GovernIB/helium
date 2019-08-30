/**
 * 
 */
package net.conselldemallorca.helium.v3.core.ejb.ws;

import java.util.List;

import javax.ejb.Stateless;
import javax.interceptor.Interceptors;
import javax.jws.WebService;

import org.jboss.wsf.spi.annotation.WebContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ejb.interceptor.SpringBeanAutowiringInterceptor;

import net.conselldemallorca.helium.v3.core.service.ws.formext.GuardarFormulari;
import net.conselldemallorca.helium.v3.core.service.ws.formext.ParellaCodiValor;

/**
 * EJB pel servei de callback de portafirmes amb interfície MCGD.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
@WebService(
		name = "GuardarFormulariImpl",
		serviceName = "GuardarFormulariImplService",
		portName = "GuardarFormulariImplPort",
		targetNamespace = "http://forms.integracio.helium.conselldemallorca.net/")
@WebContext(
		contextRoot = "/helium/ws",
		urlPattern = "/FormulariExtern",
		transportGuarantee = "NONE",
		secureWSDLAccess = false)
@Interceptors(SpringBeanAutowiringInterceptor.class)
public class GuardarFormulariBean implements GuardarFormulari {

	@Autowired
	private GuardarFormulari delegate;

	@Override
	public void guardar(String formulariId, List<ParellaCodiValor> valors) {
		delegate.guardar(formulariId, valors);
	}

}
