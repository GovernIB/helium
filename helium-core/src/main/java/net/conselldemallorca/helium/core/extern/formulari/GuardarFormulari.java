/**
 * 
 */
package net.conselldemallorca.helium.core.extern.formulari;

import java.util.List;

import javax.jws.WebService;

/**
 * Servei per a guardar la informació d'un formulari extern
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@WebService
public interface GuardarFormulari {

	public void guardar(String formulariId, List<ParellaCodiValor> valors);

}
