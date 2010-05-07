/**
 * 
 */
package net.conselldemallorca.helium.security.audit;

/**
 * 
 * 
 * @author Josep Gayà <josepg@limit.es>
 */
public interface ActionLogger {

	public void createLog(
			String accio,
			Object entitat,
			String[] propietats,
			String usuari);

}
