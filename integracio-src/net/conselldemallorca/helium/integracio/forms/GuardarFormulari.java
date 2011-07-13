/**
 * 
 */
package net.conselldemallorca.helium.integracio.forms;

import java.util.List;

import javax.jws.WebService;
import javax.xml.bind.annotation.XmlSeeAlso;

/**
 * Servei per a guardar la informació d'un formulari extern
 * 
 * @author Josep Gayà <josepg@limit.es>
 */
@WebService
@XmlSeeAlso({Object[].class, Object[][].class})
public interface GuardarFormulari {

	public void guardar(String formulariId, List<ParellaCodiValor> valors);

}
