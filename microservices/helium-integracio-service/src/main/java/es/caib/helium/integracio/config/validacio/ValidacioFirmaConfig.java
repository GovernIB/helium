package es.caib.helium.integracio.config.validacio;

import java.util.Properties;

import org.fundaciobit.plugins.validatesignature.api.IValidateSignaturePlugin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import com.netflix.servo.util.Strings;

import es.caib.helium.integracio.domini.validacio.AfirmaUtils;
import es.caib.helium.integracio.excepcions.ServeisExternsException;
import es.caib.helium.integracio.service.validacio.SignaturaPlugin;
import es.caib.helium.integracio.service.validacio.SignaturaPluginAfirma;
import es.caib.helium.integracio.service.validacio.ValidacioFirmaService;
import es.caib.helium.integracio.service.validacio.ValidacioFirmaServiceImpl;

@Configuration
@ConfigurationProperties(prefix = "es.caib.helium.integracio")
@ComponentScan("es.caib.helium.integracio.service")
public class ValidacioFirmaConfig {

	@Autowired
	private Environment env;
	
	@Bean(name = "validacioFirmaService")
	public ValidacioFirmaService instanciarService() throws ServeisExternsException {
		
		String pluginClass = env.getRequiredProperty("es.caib.helium.integracio.validacio.firma.service.class");
		try {
			var service = (ValidacioFirmaService) Class.forName(pluginClass).getConstructor().newInstance();
			if (service instanceof ValidacioFirmaServiceImpl) {
				var retorn = (ValidacioFirmaServiceImpl) service;
				retorn.setApiValidate(crearIValidateSignatureApi());
				retorn.setSignaturaPlugin(crearSignaturaPlugin());
			}
			return service;
		} catch (Exception ex) {
			throw new ServeisExternsException("Error al crear la instància de ValidacioFirmaService (" + "pluginClass=" + pluginClass + ")", ex);
		}
	}
	
	private SignaturaPlugin crearSignaturaPlugin() throws ServeisExternsException {
	
		String pluginClass = env.getRequiredProperty("es.caib.helium.integracio.validacio.signatura.class");
		try {
			var service = (SignaturaPlugin) Class.forName(pluginClass).getConstructor().newInstance();
			if (service instanceof SignaturaPluginAfirma) {

				var retorn = (SignaturaPluginAfirma) service;
				var url = env.getRequiredProperty("es.caib.helium.integracio.validacio.signatura.afirma.urlbase");
				var appId = env.getRequiredProperty("es.caib.helium.integracio.validacio.signatura.afirma.appid");
				var usuari = env.getRequiredProperty("es.caib.helium.integracio.validacio.signatura.afirma.usuari");
				var contrasenya = env.getRequiredProperty("es.caib.helium.integracio.validacio.signatura.afirma.contrasenya");
				var afirmaUtils = new AfirmaUtils(url, appId, usuari, contrasenya);
				if (Strings.isNullOrEmpty(usuari)) {
					afirmaUtils = new AfirmaUtils(url, appId);
				}
				retorn.setAfirmaUtils(afirmaUtils);
			}
			return service;
		} catch (Exception ex) {
			
			throw new ServeisExternsException("Error al crear la instància de SignaturaPlugin (" + "pluginClass=" + pluginClass + ")", ex);
		}
	}
	
	private IValidateSignaturePlugin crearIValidateSignatureApi() throws ServeisExternsException {
		
		String pluginClass = env.getRequiredProperty("es.caib.helium.integracio.validacio.firma.plugin.class");
		try {
			var properties = new Properties();
			properties.put("es.caib.helium.integracio.validacio.firma.afirmacxf.applicationID", 
					env.getRequiredProperty("es.caib.helium.integracio.validacio.firma.afirmacxf.applicationID"));
			properties.put("es.caib.helium.integracio.validacio.firma.afirmacxf.ignoreservercertificates", 
					env.getRequiredProperty("es.caib.helium.integracio.validacio.firma.afirmacxf.ignoreservercertificates"));
			properties.put("es.caib.helium.integracio.validacio.firma.afirmacxf.TransformersTemplatesPath", 
					env.getRequiredProperty("es.caib.helium.integracio.validacio.firma.afirmacxf.TransformersTemplatesPath"));
			properties.put("es.caib.helium.integracio.validacio.firma.afirmacxf.endpoint", 
					env.getRequiredProperty("es.caib.helium.integracio.validacio.firma.afirmacxf.endpoint"));
			properties.put("es.caib.helium.integracio.validacio.firma.afirmacxf.authorization.username", 
					env.getProperty("es.caib.helium.integracio.validacio.firma.afirmacxf.authorization.username"));
			properties.put("es.caib.helium.integracio.validacio.firma.afirmacxf.authorization.password", 
					env.getProperty("es.caib.helium.integracio.validacio.firma.afirmacxf.authorization.password"));
			properties.put("es.caib.helium.integracio.validacio.firma.afirmacxf.authorization.method", 
					env.getProperty("es.caib.helium.integracio.validacio.firma.afirmacxf.authorization.method"));
			
			Class<?> clazz = Class.forName(pluginClass);
			return (IValidateSignaturePlugin)clazz.getDeclaredConstructor(String.class, Properties.class)
					.newInstance("es.caib.helium.integracio.validacio.firma.", properties);
		} catch (Exception ex) {
			throw new ServeisExternsException("Error al crear la instància de IValidateSignaturePlugin (" + "pluginClass=" + pluginClass + ")",
					ex);
		}
	}
}
