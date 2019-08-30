/**
 * 
 */
package net.conselldemallorca.helium.core.util;

import net.conselldemallorca.helium.core.helper.PropertiesHelper;

/**
 * Classe per a accedir a les properties de l'aplicació.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class GlobalProperties {

	public static PropertiesHelper getInstance() {
		return PropertiesHelper.getProperties();
	}

}
