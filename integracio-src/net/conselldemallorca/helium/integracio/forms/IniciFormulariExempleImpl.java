/**
 * 
 */
package net.conselldemallorca.helium.integracio.forms;

import java.util.List;

import javax.jws.WebService;

/**
 * Exemple d'inici de formulari extern
 * 
 * @author Josep Gayà <josepg@limit.es>
 */
@WebService(endpointInterface = "net.conselldemallorca.helium.integracio.forms.IniciFormulari")
public class IniciFormulariExempleImpl implements IniciFormulari {

	public RespostaIniciFormulari iniciFormulari(
			String codi,
			String taskId,
			List<ParellaCodiValor> valors) {
		System.out.println(">>> CODI FORMULARI: " + codi);
		System.out.println(">>> CODI TASCA: " + taskId);
		if (valors != null) {
			for (ParellaCodiValor parella: valors)
				System.out.println(">>> VALOR [" + parella.getCodi() + "]: " + parella.getValor());
		}
		return new RespostaIniciFormulari(taskId, "http://www.limit.es");
	}

}
