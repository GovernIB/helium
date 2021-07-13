package es.caib.helium.integracio.config.portafirmes;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.xml.ws.BindingProvider;
import javax.xml.ws.handler.Handler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import es.caib.helium.integracio.excepcions.ServeisExternsException;
import es.caib.helium.integracio.service.portafirmes.LogMessageHandler;
import es.caib.helium.integracio.service.portafirmes.PortaFirmaServicePortaFibImpl;
import es.caib.helium.integracio.service.portafirmes.PortaFirmesService;
import es.caib.helium.integracio.utils.portafirmes.OpenOfficeUtils;
import es.caib.portafib.ws.api.v1.PortaFIBPeticioDeFirmaWs;
import es.caib.portafib.ws.api.v1.PortaFIBPeticioDeFirmaWsService;
import es.caib.portafib.ws.api.v1.PortaFIBUsuariEntitatWs;
import es.caib.portafib.ws.api.v1.PortaFIBUsuariEntitatWsService;

@Configuration
@ConfigurationProperties(prefix = "es.caib.helium.integracio")
@ComponentScan("es.caib.helium.integracio.service")
public class PortaFirmesConfig {
	
	@Autowired
	private Environment env;
	@Autowired
	OpenOfficeUtils openOfficeUtils;

	@Bean(name = "portaFirmesService")
	public PortaFirmesService instanciarService() throws ServeisExternsException {
		
		String pluginClass = env.getRequiredProperty("es.caib.helium.integracio.portafirmes.service.class");
		try {
			var service = (PortaFirmesService) Class.forName(pluginClass).getConstructor().newInstance();
			if (service instanceof PortaFirmaServicePortaFibImpl) {
				var retorn = (PortaFirmaServicePortaFibImpl) service;
				retorn.setUsuariId(env.getProperty("es.caib.helium.integracio.portafirmes.portafib.usuari.id"));
				
				var baseUrl = env.getRequiredProperty("es.caib.helium.integracio.portafirmes.portafib.base.url");
				var path = env.getRequiredProperty("es.caib.helium.integracio.portafirmes.portafib.path.peticio.firma");
				var user = env.getRequiredProperty("es.caib.helium.integracio.portafirmes.portafib.username");
				var password = env.getRequiredProperty("es.caib.helium.integracio.portafirmes.portafib.password"); 
				var isLogActiu = env.getRequiredProperty("es.caib.helium.integracio.portafirmes.portafib.log.actiu", Boolean.class);
				retorn.setPeticioApi(crearApiPeticioPortaFib(baseUrl, path, user, password, isLogActiu));
				path = env.getRequiredProperty("es.caib.helium.integracio.portafirmes.portafib.path.usuari.entitat");
				retorn.setUsuariEntitatApi(crearApiUsuariEntitat(baseUrl, path, user, password, isLogActiu));

				retorn.setOpenOfficeUtils(openOfficeUtils);
			}
			return service;
		} catch (Exception ex) {
			throw new ServeisExternsException("Error al crear la instància del PortaFirmesService (" + "pluginClass=" + pluginClass + ")", ex);
		}
	}
	
	private PortaFIBPeticioDeFirmaWs crearApiPeticioPortaFib(String baseUrl, String path, String user, String password, boolean isMissatgeLogActiu) throws MalformedURLException {
		
		var webServiceUrl = baseUrl + path;
		var wsdlUrl = new URL(webServiceUrl + "?wsdl");
		var service = new PortaFIBPeticioDeFirmaWsService(wsdlUrl);
		var api = service.getPortaFIBPeticioDeFirmaWs();
		BindingProvider bp = (BindingProvider)api;
		Map<String, Object> reqContext = bp.getRequestContext();
		reqContext.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, webServiceUrl);
		reqContext.put(BindingProvider.USERNAME_PROPERTY, user);
		reqContext.put(BindingProvider.PASSWORD_PROPERTY, password);
		if (isMissatgeLogActiu) {
			@SuppressWarnings("rawtypes")
			List<Handler> handlerChain = new ArrayList<Handler>();
			handlerChain.add(new LogMessageHandler());
			bp.getBinding().setHandlerChain(handlerChain);
		}
		
		return api;
	}
	
	private PortaFIBUsuariEntitatWs crearApiUsuariEntitat(String baseUrl, String path, String user, String password, boolean isMissatgeLogActiu) throws MalformedURLException {
		String webServiceUrl = baseUrl + path;
		URL wsdlUrl = new URL(webServiceUrl + "?wsdl");
		var service = new PortaFIBUsuariEntitatWsService(wsdlUrl);
		PortaFIBUsuariEntitatWs api = service.getPortaFIBUsuariEntitatWs();
		BindingProvider bp = (BindingProvider)api;
		Map<String, Object> reqContext = bp.getRequestContext();
		reqContext.put(
				BindingProvider.ENDPOINT_ADDRESS_PROPERTY,
				webServiceUrl);
		reqContext.put(
				BindingProvider.USERNAME_PROPERTY,
				user);
		reqContext.put(
				BindingProvider.PASSWORD_PROPERTY,
				password);
		if (isMissatgeLogActiu) {
			@SuppressWarnings("rawtypes")
			List<Handler> handlerChain = new ArrayList<Handler>();
			handlerChain.add(new LogMessageHandler());
			bp.getBinding().setHandlerChain(handlerChain);
		}
		return api;
	}

}
