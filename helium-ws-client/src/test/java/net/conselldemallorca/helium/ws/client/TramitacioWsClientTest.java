/**
 * 
 */
package net.conselldemallorca.helium.ws.client;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Test;

import net.conselldemallorca.helium.integracio.tramitacio.ExpedientInfo;
import net.conselldemallorca.helium.integracio.tramitacio.ParellaCodiValor;
import net.conselldemallorca.helium.integracio.tramitacio.TascaTramitacio;
import net.conselldemallorca.helium.integracio.tramitacio.TramitacioException_Exception;
import net.conselldemallorca.helium.integracio.tramitacio.TramitacioService;

/**
 * Client de prova per a la v1 del servei de tramitació d'expedients.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class TramitacioWsClientTest {

	private static final String SERVICE_URL = "http://localhost:8080/helium/ws/TramitacioService";

	/*private static final String ENTORN_CODI = "CAIB";
	private static final String USUARI_CODI = "user";
	private static final String EXPTIPUS_CODI = "test";*/
	private static final String ENTORN_CODI = "josepg";
	private static final String USUARI_CODI = "josepg";
	private static final String EXPTIPUS_CODI = "test";

	private TramitacioService tramitacioService;

	@Test
	public void test() throws MalformedURLException, TramitacioException_Exception {
		List<ParellaCodiValor> valorsFormulari = new ArrayList<ParellaCodiValor>();
		valorsFormulari.add(
				generarVariable("tipus_test", "variables"));
		valorsFormulari.add(
				generarVariable("var_data", new Date()));
		String processInstanceId = getTramitacioService().iniciExpedient(
				ENTORN_CODI,
				USUARI_CODI,
				EXPTIPUS_CODI,
				(String)null,
				"TEST_WS_" + System.currentTimeMillis(),
				valorsFormulari);
		System.out.println(
				">>> Nou expedient creat (" +
				"processInstanceId=" + processInstanceId + ")");
		ExpedientInfo expedient = getTramitacioService().getExpedientInfo(
				ENTORN_CODI,
				USUARI_CODI,
				processInstanceId);
		System.out.println(
				">>> Expedient consultat amb èxit (" +
				"processInstanceId=" + processInstanceId + ")");
		System.out.println(">>>    - Títol: " + expedient.getTitol());
		System.out.println(">>>    - Número: " + expedient.getNumero());
		List<TascaTramitacio> tasquesPendents = getTramitacioService().consultaTasquesGrupByProces(
				ENTORN_CODI,
				USUARI_CODI,
				processInstanceId);
		System.out.println(">>> Consultada llista de tasques pendents (" +
				"processInstanceId=" + processInstanceId + ", " +
				"tasquesPendents=" + tasquesPendents.size() + ")");
		getTramitacioService().deleteExpedient(
				ENTORN_CODI,
				USUARI_CODI,
				processInstanceId);
		System.out.println(">>> Expedient esborrat (" +
				"processInstanceId=" + processInstanceId + ")");
	}



	private ParellaCodiValor generarVariable(
			String codi,
			Object valor) {
		ParellaCodiValor variable = new ParellaCodiValor();
		variable.setCodi(codi);
		variable.setValor(valor);
		return variable;
	}

	private TramitacioService getTramitacioService() throws MalformedURLException {
		if (tramitacioService == null) {
			tramitacioService = TramitacioWsClientFactory.getWsClient(SERVICE_URL);
		}
		return tramitacioService;
	}

}
