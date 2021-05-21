package es.caib.helium.integracio.service.tramitacio;

import java.util.HashMap;
import java.util.Map;

import org.apache.cxf.configuration.jsse.TLSClientParameters;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.frontend.ClientProxyFactoryBean;
import org.apache.cxf.interceptor.LoggingInInterceptor;
import org.apache.cxf.interceptor.LoggingOutInterceptor;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.apache.cxf.transport.http.HTTPConduit;
import org.apache.cxf.transports.http.configuration.HTTPClientPolicy;
import org.apache.cxf.ws.security.wss4j.WSS4JOutInterceptor;
import org.apache.wss4j.dom.WSConstants;
import org.apache.wss4j.dom.handler.WSHandlerConstants;

public class WsClientUtils {

	public static Object getWsClientProxy(
			Class<?> clientClass,
			String wsUrl,
			String wsUserName,
			String wsPassword,
			String authType,
			boolean generateTimestamp,
			boolean logCalls,
			boolean disableCnCheck,
			Integer timeout,
			boolean wsClientChunked) {
		
		ClientProxyFactoryBean factory = new JaxWsProxyFactoryBean();
		factory.setAddress(wsUrl);
		factory.setServiceClass(clientClass);
		if (logCalls) {
			factory.getInInterceptors().add(new LoggingInInterceptor());
			factory.getOutInterceptors().add(new LoggingOutInterceptor());
		}
		String authTypeBo = authType;
		if (authTypeBo == null || authTypeBo.length() == 0) {
			if (wsUserName != null && wsUserName.length() > 0) {
				authTypeBo = "BASIC";
			}
		}
		if ("BASIC".equalsIgnoreCase(authTypeBo)) {
			factory.setUsername(wsUserName);
			factory.setPassword(wsPassword);
		} else if ("USERNAMETOKEN".equalsIgnoreCase(authTypeBo)) {
			Map<String, Object> wss4jInterceptorProps = new HashMap<String, Object>();
			if (generateTimestamp) {
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
		
		Client client = ClientProxy.getClient(c);
        HTTPConduit httpConduit = (HTTPConduit)client.getConduit();
        HTTPClientPolicy httpClientPolicy = new HTTPClientPolicy();
        if (timeout != null) {
        	httpClientPolicy.setConnectionTimeout(timeout);
        	httpClientPolicy.setReceiveTimeout(timeout);
        }
        // Envi­o chunked
		httpClientPolicy.setAllowChunking(wsClientChunked);
        httpConduit.setClient(httpClientPolicy);
        
		if (disableCnCheck) {
	        TLSClientParameters tlsParams = new TLSClientParameters();
	        tlsParams.setDisableCNCheck(true);
	        httpConduit.setTlsClientParameters(tlsParams);
		}
		return c;
	}
//	
//	private static boolean isWsClientChunked() {
//		try {
//			String chunked = GlobalProperties.getInstance().getProperty("app.ws.client.chunked");
//			if (chunked == null)
//				chunked = "false";
//			return "true".equalsIgnoreCase(chunked);
//		}catch (Exception ex) {
//			return false;
//		}
//	}
}
