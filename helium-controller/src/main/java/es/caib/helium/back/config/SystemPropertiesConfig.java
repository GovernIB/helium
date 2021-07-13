/**
 * 
 */
package es.caib.helium.back.config;

import es.caib.helium.logic.intf.util.Constants;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * Configuració de les propietats de l'aplicació a partir de la configuració
 * de les propietats de sistema (System.getProperty).
 * 
 * @author Limit Tecnologies
 */
@Configuration
@PropertySource(ignoreResourceNotFound = true, value = {
	"file://${" + Constants.APP_PROPERTIES + "}",
	"file://${" + Constants.APP_SYSTEM_PROPERTIES + "}"})
public class SystemPropertiesConfig {

}
