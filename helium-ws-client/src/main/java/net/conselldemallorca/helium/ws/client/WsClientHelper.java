/**
 * 
 */
package net.conselldemallorca.helium.ws.client;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.Service;
import javax.xml.ws.handler.Handler;

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
			Class<T> clazz,
			Handler<?>... handlers) throws MalformedURLException {
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
			/*ControladorSesion controlador = new ControladorSesion();
			controlador.autenticar(username, password);
			AuthorizationToken token = controlador.getToken();
			bindingProvider.getRequestContext().put(
					BindingProvider.USERNAME_PROPERTY,
					token.getUser());
			bindingProvider.getRequestContext().put(
					BindingProvider.PASSWORD_PROPERTY,
					token.getPassword());*/
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
		handlerChain.add(new SoapLoggingHandler());
		// Configura handlers addicionals
		for (int i = 0; i < handlers.length; i++) {
			if (handlers[i] != null)
				handlerChain.add(handlers[i]);
		}
		bindingProvider.getBinding().setHandlerChain(handlerChain);
		return bustiaWs;
	}

	public T generarClientWs(
			String endpoint,
			QName qname,
			String username,
			String password,
			Class<T> clazz,
			Handler<?>... handlers) throws MalformedURLException {
		return this.generarClientWs(
				null,
				endpoint,
				qname,
				username,
				password,
				clazz);
	}

	/*public T generarClientWs(
			String endpoint,
			QName qname,
			String userName,
			String password,
			Class<T> clazz) throws MalformedURLException {
		URL url;
		if (!endpoint.endsWith("?wsdl"))
			url = new URL(endpoint + "?wsdl");
		else
			url = new URL(endpoint);
		Service service = Service.create(url, qname);
		T port = service.getPort(clazz);
		BindingProvider bp = (BindingProvider)port;
		@SuppressWarnings("rawtypes")
		List<Handler> handlerChain = new ArrayList<Handler>();
		handlerChain.add(new SoapLoggingHandler());
		bp.getBinding().setHandlerChain(handlerChain);
		if (userName != null && !userName.isEmpty()) {
			bp.getRequestContext().put(
					BindingProvider.USERNAME_PROPERTY,
					userName);
			bp.getRequestContext().put(
					BindingProvider.PASSWORD_PROPERTY,
					password);
		}
		return port;
	}*/

}
