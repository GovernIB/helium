/**
 * 
 */
package net.conselldemallorca.helium.core.security.audit;

/**
 * 
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface ActionLogger {

	public void createLog(
			String accio,
			Object entitat,
			String[] propietats,
			String usuari);

}
