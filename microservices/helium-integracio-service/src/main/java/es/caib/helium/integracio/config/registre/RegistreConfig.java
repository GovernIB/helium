package es.caib.helium.integracio.config.registre;

import java.net.URL;
import java.util.Map;

import javax.xml.ws.BindingProvider;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import es.caib.helium.integracio.excepcions.ServeisExternsException;
import es.caib.helium.integracio.service.registre.RegistreService;
import es.caib.helium.integracio.service.registre.ApiRegWebWs;
import es.caib.helium.integracio.service.registre.RegistreApi;
import es.caib.helium.integracio.service.registre.ApiRegWeb3;
import es.caib.regweb.ws.services.regwebfacade.RegwebFacadeServiceLocator;
import es.caib.regweb3.ws.api.v3.RegWebAsientoRegistralWs;
import es.caib.regweb3.ws.api.v3.RegWebAsientoRegistralWsService;

@Configuration
@ConfigurationProperties(prefix = "es.caib.helium.integracio")
@ComponentScan("es.caib.helium.integracio.service")
public class RegistreConfig {
	
	@Autowired
	private Environment env;
	
	@Bean(name = "registreService")
	public RegistreService instanciarService() throws ServeisExternsException {
		
		var service = new RegistreService();
		try {
			service.setRegWebApi(crearRegWebService());
		} catch (Exception ex) {
			throw new ServeisExternsException("Error al crear la instancia de RegwebFacade_PortType", ex);
		}

		String pluginClass = env.getRequiredProperty("es.caib.helium.integracio.registre.service.rw3.class");
		try {
			var serviceRegWeb3 = (RegistreApi) Class.forName(pluginClass).getConstructor().newInstance();
			if (serviceRegWeb3 instanceof ApiRegWeb3) {
				
				var rw3 = (ApiRegWeb3) serviceRegWeb3;
				rw3.setAsientoRegistralApi(crearAsientoRegistralApi());
				service.setRegWeb3Api(rw3);
			}
		} catch (Exception ex) {
			throw new ServeisExternsException("Error al crear la instància de RegistreServiceRegWeb3 (" + "pluginClass=" + pluginClass + ")", ex);
		}
			return service;
	}
	
	private ApiRegWebWs crearRegWebService() throws Exception {
		
		var url = env.getRequiredProperty("es.caib.helium.integracio.registre.service.ws.url") + "?wsdl";
		var user = env.getRequiredProperty("es.caib.helium.integracio.registre.service.ws.usuari");
		var password = env.getRequiredProperty("es.caib.helium.integracio.registre.service.ws.password");
	    var service = new RegwebFacadeServiceLocator();
	    service.setRegwebFacadeEndpointAddress(url);
	    var api = service.getRegwebFacade();
	    var regWebService = new ApiRegWebWs();
	    regWebService.setApi(api);
	    regWebService.setUsuarioConexion(user);
	    regWebService.setPassword(password);
	    return regWebService;
	}
	
	private RegWebAsientoRegistralWs crearAsientoRegistralApi() throws Exception {

		var host = env.getRequiredProperty("es.caib.helium.integracio.registre.service.ws.host");
		var registroEntrada = env.getRequiredProperty("es.caib.helium.integracio.registre.service.regweb3.registro.entrada");
		var user = env.getRequiredProperty("es.caib.helium.integracio.registre.service.ws.usuari");
		var password = env.getRequiredProperty("es.caib.helium.integracio.registre.service.ws.password");
		
		final String endpoint = host + registroEntrada;
		final URL wsdl = new URL(endpoint + "?wsdl");
		var service = new RegWebAsientoRegistralWsService(wsdl);

		var api = service.getRegWebAsientoRegistralWs();

		Map<String, Object> reqContext = ((BindingProvider) api).getRequestContext();
		reqContext.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, endpoint);
		reqContext.put(BindingProvider.USERNAME_PROPERTY, user);
		reqContext.put(BindingProvider.PASSWORD_PROPERTY, password);
		
		return api;
	}
//	
//	public RegWebRegistroEntradaWs crearRegistroEntradaApi() {
//		
//        final String endpoint = getEndPoint(REGWEB3_REGISTRO_ENTRADA);
//
//        final URL wsdl = new URL(endpoint + "?wsdl");
//        var service = new RegWebRegistroEntradaWsService(wsdl);
//
//        var api = service.getRegWebRegistroEntradaWs();
//
//        configAddressUserPassword(getAppUserName(), getAppPassword(), endpoint, api);
//
//        return api;
//	}
}
