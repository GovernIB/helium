/**
 * 
 */
package es.caib.helium.logic.intf.service;

import org.springframework.security.access.prepost.PreAuthorize;

/**
 * Servei d'exemple.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface ExempleService {

	/**
	 * Retorna el text amb "Hola " davant.
	 * 
	 * @param text
	 *            Text a retornar
	 * @return "Hola " + text
	 */
	@PreAuthorize("hasRole('EMS_ADMIN')")
	public String hola(String text);
}
