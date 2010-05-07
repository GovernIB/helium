/**
 * 
 */
package net.conselldemallorca.helium.jbpm3.handlers;

import java.util.ArrayList;
import java.util.List;

import net.conselldemallorca.helium.integracio.dadesexp.service.DadesExpedient;
import net.conselldemallorca.helium.integracio.domini.ParellaCodiValor;

import org.apache.cxf.frontend.ClientProxyFactoryBean;
import org.jbpm.JbpmException;
import org.jbpm.graph.exe.ExecutionContext;

/**
 * Handler per a la notificació de dades d'un expedient a un sistema extern.
 * 
 * @author Josep Gayà <josepg@limit.es>
 */
public class DadesNotificarHandler {

	private String url;
	private String codi;
	private String vars;



	public void execute(ExecutionContext executionContext) throws Exception {
		List<ParellaCodiValor> dades = new ArrayList<ParellaCodiValor>();
		String[] parts = vars.split(",");
		for (String key: parts)
			dades.add(new ParellaCodiValor(key, executionContext.getVariable(key)));
		notificar(dades);
	}

	public void setUrl(String url) {
		this.url = url;
	}
	public void setCodi(String codi) {
		this.codi = codi;
	}
	public void setVars(String vars) {
		this.vars = vars;
	}



	private void notificar(List<ParellaCodiValor> dades) {
		try {
			ClientProxyFactoryBean factory = new ClientProxyFactoryBean();
			factory.setServiceClass(DadesExpedient.class);
			factory.setAddress(url);
			DadesExpedient client = (DadesExpedient)factory.create();
			client.notificacio(codi, dades);
		} catch (Exception ex) {
			throw new JbpmException("Error en la notificació de dades al sistema extern", ex);
		}
	}

}
