/**
 * 
 */
package net.conselldemallorca.helium.integracio.forms;

import java.util.List;

import javax.jws.WebService;
import javax.xml.bind.annotation.XmlSeeAlso;

/**
 * Interfície per al servei de formularis externs
 * 
 * @author Josep Gayà <josepg@limit.es>
 */
@WebService
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
