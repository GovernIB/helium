/**
 * 
 */
package es.caib.helium.commons.config;

/**
 * Propietats de configuració de l'aplicació.
 * 
 * @author Límit Tecnologies
 */
public class BaseConfig {

	public static final String APP_NAME = "helium";
	public static final String DB_PREFIX = "hel_";

	public static final String BASE_PACKAGE = "es.caib." + APP_NAME;

	public static final String APP_PROPERTIES = BASE_PACKAGE + ".properties";
	public static final String APP_SYSTEM_PROPERTIES = BASE_PACKAGE + ".system.properties";

	public static final String ROLE_SUPER = "HEL_ADMIN";
	public static final String ROLE_COMANDA = "HEL_COM";
	public static final String ROLE_USER = "tothom";

}
