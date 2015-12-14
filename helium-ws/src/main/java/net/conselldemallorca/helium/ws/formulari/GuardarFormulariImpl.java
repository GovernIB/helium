/**
 * 
 */
package net.conselldemallorca.helium.ws.formulari;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jws.WebService;
import javax.xml.datatype.XMLGregorianCalendar;

import net.conselldemallorca.helium.core.model.service.TascaService;

import org.springframework.beans.factory.annotation.Autowired;

/**
 * Implementació del servei per guardar les dades dels formularis externs
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@WebService(
		endpointInterface = "net.conselldemallorca.helium.ws.formulari.GuardarFormulari",
		targetNamespace = "http://forms.integracio.helium.conselldemallorca.net/")
public class GuardarFormulariImpl implements GuardarFormulari {

	private TascaService tascaService;



	public void guardar(String formulariId, List<ParellaCodiValor> valors) {
		Map<String, Object> valorsTasca = new HashMap<String, Object>();
		if (valors != null) {
			for (ParellaCodiValor parella: valors) {
				/*String tipus = null;
				if (parella.getValor() != null)
					tipus = parella.getValor().getClass().getName();*/
				if (parella.getValor() != null) {
					if (parella.getValor() instanceof XMLGregorianCalendar) {
						valorsTasca.put(
								parella.getCodi(),
								((XMLGregorianCalendar)parella.getValor()).toGregorianCalendar().getTime());
					} else {
						valorsTasca.put(parella.getCodi(), parella.getValor());
					}
				} else {
					valorsTasca.put(parella.getCodi(), parella.getValor());
				}
			}
		}
		tascaService.guardarFormulariExtern(formulariId, valorsTasca);
	}

	@Autowired
	public void setTascaService(TascaService tascaService) {
		this.tascaService = tascaService;
	}

}
