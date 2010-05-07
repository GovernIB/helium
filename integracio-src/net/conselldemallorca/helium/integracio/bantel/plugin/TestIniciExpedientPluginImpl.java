/**
 * 
 */
package net.conselldemallorca.helium.integracio.bantel.plugin;

import java.util.Date;

import net.conselldemallorca.helium.integracio.bantel.client.wsdl.TramiteBTE;

/**
 * Interfície per a iniciar un expedient donat un tràmit de SISTRA.
 * 
 * @author Josep Gayà <josepg@limit.es>
 */
public class TestIniciExpedientPluginImpl implements IniciExpedientPlugin {

	public DadesIniciExpedient obtenirDadesInici(TramiteBTE tramit) {
		DadesIniciExpedient resposta = new DadesIniciExpedient();
		resposta.setEntornCodi("carcim");
		resposta.setTipusCodi("menor");
		Date ara = new Date();
		resposta.setTitol("Expedient iniciat des de SISTRA " + ara.toString());
		resposta.setNumero(ara.toString());
		return resposta;
	}

}
