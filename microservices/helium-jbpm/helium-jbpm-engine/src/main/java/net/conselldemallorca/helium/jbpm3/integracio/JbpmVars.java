/**
 * 
 */
package net.conselldemallorca.helium.jbpm3.integracio;

/**
 * Prefixos per a emmagatzemar informació com a variables jBPM.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class JbpmVars {

	public static final String VAR_PREFIX = "H3l1um#";
	public static final String VAR_TASCA_PREFIX = VAR_PREFIX + "tasca.";
	public static final String VAR_TASCA_VALIDADA = VAR_TASCA_PREFIX + "validada";
	public static final String VAR_TASCA_DELEGACIO = VAR_TASCA_PREFIX + "delegacio";

	public static final String PREFIX_DOCUMENT = VAR_PREFIX + "document.";
	public static final String PREFIX_ADJUNT = VAR_PREFIX + "adjunt.";
	public static final String PREFIX_SIGNATURA = VAR_PREFIX + "signatura.";
	public static final String PREFIX_VAR_DESCRIPCIO = VAR_PREFIX + "descripcio.";

}
