/**
 * 
 */
package es.caib.helium.logic.intf.extern.formulari;

import java.util.List;

import javax.jws.WebService;
import javax.xml.bind.annotation.XmlSeeAlso;

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
	 * @param id
	 * @param valors
	 * @return URL a on redireccionar l'usuari
	 */
	public RespostaIniciFormulari iniciFormulari(
			String codi,
			String taskId,
			List<ParellaCodiValor> valors);

}
