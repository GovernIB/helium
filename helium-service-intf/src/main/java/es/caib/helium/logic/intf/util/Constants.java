/**
 * 
 */
package es.caib.helium.logic.intf.util;

/**
 * Camps relatius als expedients indexats
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class Constants {

	// Indexació
	public static final String EXPEDIENT_PREFIX_SEPARATOR = "$";
	public static final String EXPEDIENT_PREFIX_JSP_SEPARATOR = "__exp__";
	public static final String EXPEDIENT_PREFIX = "expedient" + EXPEDIENT_PREFIX_SEPARATOR;
	public static final String EXPEDIENT_PREFIX_JSP = "expedient" + EXPEDIENT_PREFIX_JSP_SEPARATOR;

	public static final String EXPEDIENT_CAMP_ENTORN = EXPEDIENT_PREFIX + "entorn";
	public static final String EXPEDIENT_CAMP_ID = EXPEDIENT_PREFIX + "id";
	public static final String EXPEDIENT_CAMP_NUMERO = EXPEDIENT_PREFIX + "numero";
	public static final String EXPEDIENT_CAMP_TITOL = EXPEDIENT_PREFIX + "titol";
	public static final String EXPEDIENT_CAMP_COMENTARI = EXPEDIENT_PREFIX + "comentari";
	public static final String EXPEDIENT_CAMP_INFOATUR = EXPEDIENT_PREFIX + "infoatur";
	public static final String EXPEDIENT_CAMP_INICIADOR = EXPEDIENT_PREFIX + "iniciador";
	public static final String EXPEDIENT_CAMP_RESPONSABLE = EXPEDIENT_PREFIX + "responsable";
	public static final String EXPEDIENT_CAMP_GEOX = EXPEDIENT_PREFIX + "geox";
	public static final String EXPEDIENT_CAMP_GEOY = EXPEDIENT_PREFIX + "geoy";
	public static final String EXPEDIENT_CAMP_GEOREF = EXPEDIENT_PREFIX + "georef";
	public static final String EXPEDIENT_CAMP_REGNUM = EXPEDIENT_PREFIX + "regnum";
	public static final String EXPEDIENT_CAMP_REGDATA = EXPEDIENT_PREFIX + "regdata";
	public static final String EXPEDIENT_CAMP_UNIADM = EXPEDIENT_PREFIX + "uniadm";
	public static final String EXPEDIENT_CAMP_IDIOMA = EXPEDIENT_PREFIX + "idioma";
	public static final String EXPEDIENT_CAMP_TRAMIT = EXPEDIENT_PREFIX + "idioma";
	public static final String EXPEDIENT_CAMP_DATA_INICI = EXPEDIENT_PREFIX + "dataInici";
	public static final String EXPEDIENT_CAMP_TIPUS = EXPEDIENT_PREFIX + "tipus";
	public static final String EXPEDIENT_CAMP_ESTAT = EXPEDIENT_PREFIX + "estat";
	public static final String EXPEDIENT_CAMP_ERRORS_REINDEXACIO = EXPEDIENT_PREFIX + "errorsReindexacio";
	public static final String EXPEDIENT_CAMP_ESTAT_JSP = "expedient" + EXPEDIENT_PREFIX_JSP_SEPARATOR + "estat";

	// Motor de Workflow
	public static final String VAR_PREFIX = "H3l1um#";
	public static final String PREFIX_VAR_DESCRIPCIO = VAR_PREFIX + "descripcio.";
	public static final String PREFIX_SIGNATURA = VAR_PREFIX + "signatura.";
	public static final String PREFIX_ADJUNT = VAR_PREFIX + "adjunt.";
	public static final String PREFIX_DOCUMENT = VAR_PREFIX + "document.";
	public static final String VAR_TASCA_PREFIX = VAR_PREFIX + "tasca.";
	public static final String VAR_TASCA_DELEGACIO = VAR_TASCA_PREFIX + "delegacio";
	public static final String VAR_TASCA_VALIDADA = VAR_TASCA_PREFIX + "validada";

	// Propietats
	public static final String APP_PROPERTIES = "es.caib.helium.properties";
	public static final String APP_SYSTEM_PROPERTIES = "es.caib.helium.system.properties";
}
