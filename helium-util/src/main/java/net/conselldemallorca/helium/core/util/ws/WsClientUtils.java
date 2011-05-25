/**
 * 
 */
package net.conselldemallorca.helium.core.util.ws;

import java.util.HashMap;
import java.util.Map;

import net.conselldemallorca.helium.core.util.GlobalProperties;

import org.apache.cxf.configuration.jsse.TLSClientParameters;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.frontend.ClientProxyFactoryBean;
import org.apache.cxf.interceptor.LoggingInInterceptor;
import org.apache.cxf.interceptor.LoggingOutInterceptor;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.apache.cxf.transport.http.HTTPConduit;
import org.apache.cxf.ws.security.wss4j.WSS4JOutInterceptor;
import org.apache.ws.security.WSConstants;
import org.apache.ws.security.handler.WSHandlerConstants;

/**
 * Utilitat per a configurar de manera centralitzada
 * tots els clients ws
 * 
 * @author Limit Tecnologies
 */
public class WsClientUtils {

	public static Object getWsClientProxy(
			Class<?> clientClass,
			String wsUrl,
			String wsUserName,
			String wsPassword) {
		ClientProxyFactoryBean factory = new JaxWsProxyFactoryBean();
		factory.setAddress(wsUrl);
		factory.setServiceClass(clientClass);
		if (isLogCalls()) {
			factory.getInInterceptors().add(new LoggingInInterceptor());
			factory.getOutInterceptors().add(new LoggingOutInterceptor());
		}
		if ("BASIC".equalsIgnoreCase(getAuth())) {
			factory.setUsername(wsUserName);
			factory.setPassword(wsPassword);
		} else if ("USERNAMETOKEN".equalsIgnoreCase(getAuth())) {
			Map<String, Object> wss4jInterceptorProps = new HashMap<String, Object>();
			if (isGenerateTimestamp()) {
				wss4jInterceptorProps.put(WSHandlerConstants.ACTION, WSHandlerConstants.TIMESTAMP + " " + WSHandlerConstants.USERNAME_TOKEN);
			} else {
				wss4jInterceptorProps.put(WSHandlerConstants.ACTION, WSHandlerConstants.USERNAME_TOKEN);
			}
			wss4jInterceptorProps.put(WSHandlerConstants.USER, wsUserName);
			wss4jInterceptorProps.put(WSHandlerConstants.PASSWORD_TYPE, WSConstants.PW_TEXT);
			ClientPasswordCallback cp = new ClientPasswordCallback(wsPassword);
			wss4jInterceptorProps.put(WSHandlerConstants.PW_CALLBACK_REF, cp);
			factory.getOutInterceptors().add(new WSS4JOutInterceptor(wss4jInterceptorProps));
		}
		Object c = factory.create();
		if (isDisableCnCheck()) {
			Client client = ClientProxy.getClient(c);
	        HTTPConduit httpConduit = (HTTPConduit)client.getConduit();
	        TLSClientParameters tlsParams = new TLSClientParameters();
	        tlsParams.setDisableCNCheck(true);
	        httpConduit.setTlsClientParameters(tlsParams);
		}
		return c;
	}



	private static String getAuth() {
		return GlobalProperties.getInstance().getProperty("app.ws.client.auth");
	}
	private static boolean isLogCalls() {
		String logCalls = GlobalProperties.getInstance().getProperty("app.ws.client.log.calls");
		return "true".equalsIgnoreCase(logCalls);
	}
	private static boolean isGenerateTimestamp() {
		String generateTimestamp = GlobalProperties.getInstance().getProperty("app.ws.client.generate.timestamp");
		return "true".equalsIgnoreCase(generateTimestamp);
	}
	private static boolean isDisableCnCheck() {
		String disableCnCheck = GlobalProperties.getInstance().getProperty("app.ws.client.disable.cn.check");
		return "true".equalsIgnoreCase(disableCnCheck);
	}

}
