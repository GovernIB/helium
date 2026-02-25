/**
 * 
 */
package es.caib.helium.back.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import es.caib.helium.logic.intf.config.BaseConfig;

/**
 * Configuració de les propietats de l'aplicació a partir de les propietats de
 * sistema (System.getProperty).
 * 
 * @author Límit Tecnologies
 */
@Configuration
@PropertySource(ignoreResourceNotFound = true, value = {
	"file://${" + BaseConfig.APP_PROPERTIES + "}",
	"file://${" + BaseConfig.APP_SYSTEM_PROPERTIES + "}"})
public class SystemPropertiesConfig {

}
