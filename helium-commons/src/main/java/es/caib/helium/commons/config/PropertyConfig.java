package es.caib.helium.commons.config;

/**
 * Configuració de les propietats de l'aplicació. Conté constants per les entrades de les diferents propietats.
 *
 * @author Limit Tecnologies
 */
public class PropertyConfig {

	/** Prefix de les propietats es.caib.helium." */
	private static final String PROPERTY_PREFIX =  BaseConfig.BASE_PACKAGE + ".";

	public static final String PERSISTENCE_CONTAINER_TRANSACTIONS_DISABLED = PROPERTY_PREFIX + "persist.container-transactions-disabled";

}
