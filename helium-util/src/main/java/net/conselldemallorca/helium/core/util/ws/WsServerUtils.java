/**
 * 
 */
package net.conselldemallorca.helium.core.util.ws;

import java.util.HashMap;
import java.util.Map;

import javax.xml.ws.Endpoint;

import net.conselldemallorca.helium.core.util.GlobalProperties;

import org.apache.cxf.interceptor.LoggingInInterceptor;
import org.apache.cxf.interceptor.LoggingOutInterceptor;
import org.apache.cxf.jaxws.EndpointImpl;
import org.apache.cxf.ws.security.wss4j.WSS4JOutInterceptor;
import org.apache.ws.security.WSConstants;
import org.apache.ws.security.handler.WSHandlerConstants;

/**
 * Utilitat per a configurar de manera centralitzada
 * tots els clients ws
 * 
 * @author Limit Tecnologies
 */
public class WsServerUtils {

	public Endpoint publish(
			String address,
			Object implementor,
			String wsUserName,
			String wsPassword) {
		Endpoint jaxwsEndpoint = Endpoint.publish(address, implementor);
		EndpointImpl jaxwsEndpointImpl = (EndpointImpl)jaxwsEndpoint;
		org.apache.cxf.endpoint.Server server = jaxwsEndpointImpl.getServer();
		org.apache.cxf.endpoint.Endpoint endpoint = server.getEndpoint();
		if (isLogCalls()) {
			endpoint.getInInterceptors().add(new LoggingInInterceptor());
			endpoint.getOutInterceptors().add(new LoggingOutInterceptor());
		}
		if ("USERNAMETOKEN".equalsIgnoreCase(getAuth())) {
			Map<String, Object> wss4jInterceptorProps = new HashMap<String, Object>();
			if (isGenerateTimestamp()) {
				wss4jInterceptorProps.put(WSHandlerConstants.ACTION, WSHandlerConstants.TIMESTAMP + " " + WSHandlerConstants.USERNAME_TOKEN);
			} else {
				wss4jInterceptorProps.put(WSHandlerConstants.ACTION, WSHandlerConstants.USERNAME_TOKEN);
			}
			wss4jInterceptorProps.put(WSHandlerConstants.PASSWORD_TYPE, WSConstants.PW_TEXT);
			ServerPasswordCallback sp = new ServerPasswordCallback();
			sp.setUserName(wsUserName);
			sp.setPassword(wsPassword);
			wss4jInterceptorProps.put(WSHandlerConstants.PW_CALLBACK_REF, sp);
			endpoint.getOutInterceptors().add(new WSS4JOutInterceptor(wss4jInterceptorProps));
		}
		return jaxwsEndpoint;
	}



	private static String getAuth() {
		return GlobalProperties.getInstance().getProperty("app.ws.server.auth");
	}
	private static boolean isLogCalls() {
		String logCalls = GlobalProperties.getInstance().getProperty("app.ws.server.log.calls");
		return "true".equalsIgnoreCase(logCalls);
	}
	private static boolean isGenerateTimestamp() {
		String generateTimestamp = GlobalProperties.getInstance().getProperty("app.ws.server.generate.timestamp");
		return "true".equalsIgnoreCase(generateTimestamp);
	}

}
