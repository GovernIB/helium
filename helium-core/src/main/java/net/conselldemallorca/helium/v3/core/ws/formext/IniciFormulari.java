/**
 * 
 */
package net.conselldemallorca.helium.v3.core.ws.formext;

import java.util.List;

import javax.jws.WebService;

/**
 * Interfície per al servei de formularis externs
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@WebService(
		name = "IniciFormulari",
		serviceName = "IniciFormulariService",
		portName = "IniciFormulariServicePort",
		targetNamespace="http://forms.integracio.helium.conselldemallorca.net/")
		//targetNamespace = "http://www.caib.es/helium/ws/formext")
public interface IniciFormulari {

	/**
	 * Mètode per a l'inicialització del formulari extern
	 * 
	 * @param codi Codi del formulari extern.
	 * @param taskId Id de la tasca a Helium.
	 * @param valors Valors de les variables de la tasca.
	 * @return URL del formulari extern.
	 */
	public RespostaIniciFormulari iniciFormulari(
			String codi,
			String taskId,
			List<ParellaCodiValor> valors);

}
