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

import net.conselldemallorca.helium.core.extern.domini.DominiHelium;
import net.conselldemallorca.helium.core.extern.domini.FilaResultat;
import net.conselldemallorca.helium.core.extern.domini.ParellaCodiValor;

/**
 * EJB pel servei de callback de portafirmes amb interfície MCGD.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
@WebService(
		name = "DominiIntern",
		serviceName = "DominiInternService",
		portName = "DominiInternPort",
		targetNamespace = "http://domini.integracio.helium.conselldemallorca.net/")
@WebContext(
		contextRoot = "/helium/ws",
		urlPattern = "/DominiIntern",
		transportGuarantee = "NONE",
		secureWSDLAccess = false)
@Interceptors(SpringBeanAutowiringInterceptor.class)
public class DominiInternBean implements DominiHelium {

	@Autowired
	private DominiHelium delegate;

	@Override
	public List<FilaResultat> consultaDomini(String id, List<ParellaCodiValor> parametres) throws Exception {
		return delegate.consultaDomini(id, parametres);
	}

}
