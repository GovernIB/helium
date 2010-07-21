/**
 * 
 */
package net.conselldemallorca.helium.integracio.bantel.plugin;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import net.conselldemallorca.helium.integracio.bantel.client.wsdl.TramiteBTE;

/**
 * Interfície per a iniciar un expedient donat un tràmit de SISTRA.
 * 
 * @author Josep Gayà <josepg@limit.es>
 */
public class TestIniciExpedientPluginImpl implements IniciExpedientPlugin {

	public List<DadesIniciExpedient> obtenirDadesInici(TramiteBTE tramit) {
		List<DadesIniciExpedient> resposta = new ArrayList<DadesIniciExpedient>();
		DadesIniciExpedient dadesIniciExpedient = new DadesIniciExpedient();
		dadesIniciExpedient.setEntornCodi("carcim");
		dadesIniciExpedient.setTipusCodi("menor");
		Date ara = new Date();
		dadesIniciExpedient.setTitol("Expedient iniciat des de SISTRA " + ara.toString());
		dadesIniciExpedient.setNumero(ara.toString());
		resposta.add(dadesIniciExpedient);
		return resposta;
	}

}
