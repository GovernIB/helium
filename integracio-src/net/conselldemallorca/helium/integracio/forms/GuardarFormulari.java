/**
 * 
 */
package net.conselldemallorca.helium.integracio.forms;

import java.util.List;

import javax.jws.WebService;

/**
 * Servei per a guardar la informació d'un formulari extern
 * 
 * @author Josep Gayà <josepg@limit.es>
 */
@WebService
public interface GuardarFormulari {

	public void guardar(String formulariId, List<ParellaCodiValor> valors);

}
