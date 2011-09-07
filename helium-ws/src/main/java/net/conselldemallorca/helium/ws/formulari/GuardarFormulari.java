/**
 * 
 */
package net.conselldemallorca.helium.ws.formulari;

import java.util.List;

import javax.jws.WebService;

/**
 * Servei per a guardar la informació d'un formulari extern
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@WebService(
		name="GuardarFormulari",
		targetNamespace = "http://forms.integracio.helium.conselldemallorca.net/")
public interface GuardarFormulari {

	public void guardar(String formulariId, List<ParellaCodiValor> valors);

}
