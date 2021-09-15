/**
 * 
 */
package es.caib.helium.logic.intf.extern.formulari;

import es.caib.helium.client.model.ParellaCodiValor;

import javax.jws.WebService;
import javax.xml.bind.annotation.XmlSeeAlso;
import java.util.List;

/**
 * Interfície per al servei de formularis externs
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@WebService(targetNamespace="http://forms.integracio.helium.conselldemallorca.net/")
@XmlSeeAlso({Object[].class, Object[][].class})
public interface IniciFormulari {

	/**
	 * Mètode per a l'inicialització del formulari extern
	 * 
	 * @param codi
	 * @param taskId
	 * @param valors
	 * @return URL a on redireccionar l'usuari
	 */
	public RespostaIniciFormulari iniciFormulari(
			String codi,
			String taskId,
			List<ParellaCodiValor> valors);

}
