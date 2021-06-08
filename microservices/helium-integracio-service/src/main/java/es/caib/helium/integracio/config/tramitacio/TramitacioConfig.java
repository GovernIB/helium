package es.caib.helium.integracio.config.tramitacio;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import es.caib.bantel.ws.v2.services.BackofficeFacade;
import es.caib.helium.integracio.excepcions.ServeisExternsException;
import es.caib.helium.integracio.service.tramitacio.TramitacioService;
import es.caib.helium.integracio.service.tramitacio.TramitacioServiceSistraImpl;
import es.caib.helium.integracio.service.tramitacio.WsClientUtils;

@Configuration
@ConfigurationProperties(prefix = "es.caib.helium.integracio")
@ComponentScan("es.caib.helium.integracio.service")
public class TramitacioConfig {

	@Autowired
	private Environment env;
	
	@Bean(name = "tramitacioService")
	public TramitacioService instanciarService() throws ServeisExternsException {
		
		String pluginClass = env.getRequiredProperty("es.caib.helium.integracio.tramitacio.service.class");
		try {
			var service = (TramitacioService) Class.forName(pluginClass).getConstructor().newInstance();
			if (service instanceof TramitacioServiceSistraImpl) {
				var retorn = (TramitacioServiceSistraImpl) service;
				retorn.setBantelClient(crearClientBantel());
				retorn.setRegtelClient(crearClientRegtel());
				retorn.setZonaPerClient(crearZonaperClient());
				retorn.setRedoseClient(crearClientRedose());
			}
			return service;
		} catch (Exception ex) {
			throw new ServeisExternsException("Error al crear la instància de TramitacioService (" + "pluginClass=" + pluginClass + ")", ex);
		}
	}
	
	private BackofficeFacade crearClientBantel() {
		
			var url = env.getRequiredProperty("es.caib.helium.integracio.tramitacio.bantel.sistra.url");
			var user = env.getRequiredProperty("es.caib.helium.integracio.tramitacio.bantel.sistra.username");
			var password = env.getRequiredProperty("es.caib.helium.integracio.tramitacio.bantel.sistra.password");
			var wsClientAuthType = env.getProperty("es.caib.helium.integracio.tramitacio.bantel.sistra.authType", String.class, null);
			var wsClientGenerateTimestamp = env.getProperty("es.caib.helium.integracio.tramitacio.bantel.sistra.wsClientGenerateTimestamp", Boolean.class, false);
			var wsClientLogCalls = env.getProperty("es.caib.helium.integracio.tramitacio.bantel.sistra.wsClientLogCalls", Boolean.class, false);
			var wsClientDisableCnCheck = env.getProperty("es.caib.helium.integracio.tramitacio.bantel.sistra.wsClientDisableCnCheck", Boolean.class, false);
			var wsClientChunked = env.getProperty("es.caib.helium.integracio.tramitacio.bantel.sistra.wsClientDisableChunked", Boolean.class, false);

			return (BackofficeFacade) WsClientUtils.getWsClientProxy(
				BackofficeFacade.class,
				url,
				user,
				password,
				wsClientAuthType,
				wsClientGenerateTimestamp,
				wsClientLogCalls,
				wsClientDisableCnCheck,
				null,
				wsClientChunked);
	}
	
	private es.caib.zonaper.ws.v2.services.BackofficeFacade crearZonaperClient() {
		
		var url = env.getRequiredProperty("es.caib.helium.integracio.tramitacio.sistra.client.zonaper.url");
		var userName = env.getRequiredProperty("es.caib.helium.integracio.tramitacio.sistra.client.zonaper.username");
		var password = env.getRequiredProperty("es.caib.helium.integracio.tramitacio.sistra.client.zonaper.password");
		var wsClientAuthType = env.getProperty("es.caib.helium.integracio.tramitacio.bantel.sistra.authType", String.class, null);
		var wsClientGenerateTimestamp = env.getProperty("es.caib.helium.integracio.tramitacio.bantel.sistra.wsClientGenerateTimestamp", Boolean.class, false);
		var wsClientLogCalls = env.getProperty("es.caib.helium.integracio.tramitacio.bantel.sistra.wsClientLogCalls", Boolean.class, false);
		var wsClientDisableCnCheck = env.getProperty("es.caib.helium.integracio.tramitacio.bantel.sistra.wsClientDisableCnCheck", Boolean.class, false);
		var wsClientChunked = env.getProperty("es.caib.helium.integracio.tramitacio.bantel.sistra.wsClientDisableChunked", Boolean.class, false);
		Object wsClientProxy = WsClientUtils.getWsClientProxy(
				es.caib.zonaper.ws.v2.services.BackofficeFacade.class,
				url,
				userName,
				password,
				wsClientAuthType,
				wsClientGenerateTimestamp,
				wsClientLogCalls,
				wsClientDisableCnCheck,
				null,
				wsClientChunked);
		return (es.caib.zonaper.ws.v2.services.BackofficeFacade)wsClientProxy;
	}
	
	private es.caib.regtel.ws.v2.services.BackofficeFacade crearClientRegtel() {
		
		var url = env.getRequiredProperty("es.caib.helium.integracio.tramitacio.sistra.client.regtel.url");
		var userName = env.getRequiredProperty("es.caib.helium.integracio.tramitacio.sistra.client.regtel.username");
		var password = env.getRequiredProperty("es.caib.helium.integracio.tramitacio.sistra.client.regtel.password");
		var auth = env.getRequiredProperty("es.caib.helium.integracio.tramitacio.sistra.client.auth");
		var generateTimpestamp = env.getRequiredProperty("es.caib.helium.integracio.tramitacio.sistra.client.generate.timestamp", Boolean.class);
		var logCalls = env.getRequiredProperty("es.caib.helium.integracio.tramitacio.sistra.client.log.calls", Boolean.class);
		var disableCnCheck = env.getRequiredProperty("es.caib.helium.integracio.tramitacio.sistra.client.log.calls", Boolean.class);
		var wsClientChunked = env.getProperty("es.caib.helium.integracio.tramitacio.bantel.sistra.wsClientDisableChunked", Boolean.class, false);
		Object wsClientProxy = WsClientUtils.getWsClientProxy(
				es.caib.regtel.ws.v2.services.BackofficeFacade.class,
				url,
				userName,
				password,
				auth,
				generateTimpestamp,
				logCalls,
				disableCnCheck,
				null,
				wsClientChunked);
		return (es.caib.regtel.ws.v2.services.BackofficeFacade) wsClientProxy;
	}
	
	private es.caib.redose.ws.v2.services.BackofficeFacade crearClientRedose() {
		
		String url = env.getRequiredProperty("es.caib.helium.integracio.tramitacio.sistra.client.redose.url");
		String userName = env.getRequiredProperty("es.caib.helium.integracio.tramitacio.sistra.client.redose.username");
		String password = env.getRequiredProperty("es.caib.helium.integracio.tramitacio.sistra.client.redose.password");
		var wsClientAuthType = env.getProperty("es.caib.helium.integracio.tramitacio.bantel.sistra.authType", String.class, null);
		var wsClientGenerateTimestamp = env.getProperty("es.caib.helium.integracio.tramitacio.bantel.sistra.wsClientGenerateTimestamp", Boolean.class, false);
		var wsClientLogCalls = env.getProperty("es.caib.helium.integracio.tramitacio.bantel.sistra.wsClientLogCalls", Boolean.class, false);
		var wsClientDisableCnCheck = env.getProperty("es.caib.helium.integracio.tramitacio.bantel.sistra.wsClientDisableCnCheck", Boolean.class, false);
		var wsClientChunked = env.getProperty("es.caib.helium.integracio.tramitacio.bantel.sistra.wsClientDisableChunked", Boolean.class, false);

		Object wsClientProxy = WsClientUtils.getWsClientProxy(
				es.caib.redose.ws.v2.services.BackofficeFacade.class,
				url,
				userName,
				password,
				wsClientAuthType,
				wsClientGenerateTimestamp,
				wsClientLogCalls,
				wsClientDisableCnCheck,
				null,
				wsClientChunked);
		return (es.caib.redose.ws.v2.services.BackofficeFacade)wsClientProxy;
	}
}
