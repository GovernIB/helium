/**
 * 
 */
package es.caib.helium.logic.util.ws;

import javax.naming.NamingException;
import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.Service;
import javax.xml.ws.handler.Handler;
import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

/**
 * Utilitat per a instanciar clients per al servei d'enviament
 * de contingut a bústies.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class WsClientHelper<T> {

	public T generarClientWs(
			URL wsdlResourceUrl,
			String endpoint,
			QName qname,
			String username,
			String password,
			String soapAction,
			Class<T> clazz,
			Handler<?>... handlers) throws MalformedURLException, RemoteException, NamingException {
		URL url = wsdlResourceUrl;
		if (url == null) {
			if (!endpoint.endsWith("?wsdl"))
				url = new URL(endpoint + "?wsdl");
			else
				url = new URL(endpoint);
		}
		Service service = Service.create(url, qname);
		T bustiaWs = service.getPort(clazz);
		BindingProvider bindingProvider = (BindingProvider)bustiaWs;
		// Configura l'adreça del servei
		String endpointAddress;
		if (!endpoint.endsWith("?wsdl"))
			endpointAddress = endpoint;
		else
			endpointAddress = endpoint.substring(0, endpoint.length() - "?wsdl".length());
		bindingProvider.getRequestContext().put(
				BindingProvider.ENDPOINT_ADDRESS_PROPERTY,
				endpointAddress);
		// Configura l'autenticació si és necessària
		if (username != null && !username.isEmpty()) {
			bindingProvider.getRequestContext().put(
					BindingProvider.USERNAME_PROPERTY,
					username);
			bindingProvider.getRequestContext().put(
					BindingProvider.PASSWORD_PROPERTY,
					password);
		}
		// Configura el log de les peticions
		@SuppressWarnings("rawtypes")
		List<Handler> handlerChain = new ArrayList<Handler>();
		handlerChain.add(new SOAPLoggingHandler());
		// Configura handlers addicionals
		for (int i = 0; i < handlers.length; i++) {
			if (handlers[i] != null)
				handlerChain.add(handlers[i]);
		}
		bindingProvider.getBinding().setHandlerChain(handlerChain);
		if (soapAction != null) {
			bindingProvider.getRequestContext().put(
					BindingProvider.SOAPACTION_USE_PROPERTY,
					true);
			bindingProvider.getRequestContext().put(
					BindingProvider.SOAPACTION_URI_PROPERTY,
					soapAction);
		}
		return bustiaWs;
	}

	public T generarClientWs(
			String endpoint,
			QName qname,
			String userName,
			String password,
			String soapAction,
			Class<T> clazz,
			Handler<?>... handlers) throws MalformedURLException, RemoteException, NamingException {
		return this.generarClientWs(
				null,
				endpoint,
				qname,
				userName,
				password,
				soapAction,
				clazz,
				handlers);
	}

}
