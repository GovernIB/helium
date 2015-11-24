/**
 * 
 */
package net.conselldemallorca.helium.v3.core.api.service;

/**
 * Servei amb funcionalitat a nivell d'aplicació.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface AplicacioService {

	/**
	 * Retorna un string en format JSON amb les mètriques de l'aplicació.
	 * 
	 * @return l'string amb les mètriques
	 */
	public String metrics();

}
