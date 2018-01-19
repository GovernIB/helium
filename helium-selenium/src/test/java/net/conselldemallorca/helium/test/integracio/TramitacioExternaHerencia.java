package net.conselldemallorca.helium.test.integracio;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import net.conselldemallorca.helium.core.util.ws.WsClientUtils;
import net.conselldemallorca.helium.test.BaseTest;
import net.conselldemallorca.helium.ws.tramitacio.CampProces;
import net.conselldemallorca.helium.ws.tramitacio.ExpedientInfo;
import net.conselldemallorca.helium.ws.tramitacio.ParellaCodiValor;
import net.conselldemallorca.helium.ws.tramitacio.TascaTramitacio;
import net.conselldemallorca.helium.ws.tramitacio.TramitacioException;
import net.conselldemallorca.helium.ws.tramitacio.TramitacioService;

/** Test específic de tramitació externa per provar la tramitació externa d'un expedient amb herència.
 *  Aquest test no s'executa en la test suite de proves d'integració.
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TramitacioExternaHerencia extends BaseTest {
	
	private static final String SELENIUM_TRAMITACIO_EXTERNA_EXPEDIENT_TITOL = "Tramitació externa herencia";
	// Client al WS de tramitació
	private TramitacioService ws = null;
			
	@Before
	public void setUp() throws Exception {
		// No importa obrir el navegador
		// Carrega el magatzem de certificats de confiança
		String trustStoreFilePath = propietats.getTrustStoreFilePath();
		if(trustStoreFilePath == null)
			trustStoreFilePath = getTrustStoreFilePath();
		System.setProperty("javax.net.ssl.trustStore", trustStoreFilePath);
	}
	
	@After
	public void tearDown() throws Exception {
		// No importa tancar el driver
	}

	/** Prova la creació remota d'un expedient.
	 */
	@Test
	public void a0_iniciExpedient(){
		// Esborra els possibles expedients existents
		esborrarExpedientTramitacioExterna();
		
		// Crea l'expedient via WS
		TramitacioService ws = getClientTramitacio();
		List<ParellaCodiValor> valorsFormulari = new ArrayList<ParellaCodiValor>();
		valorsFormulari.add(new ParellaCodiValor("var_pare_domini", "1"));
		valorsFormulari.add(new ParellaCodiValor("var_fill_1", "valor per text area tramitació externa"));
		valorsFormulari.add(new ParellaCodiValor("var_sobreescrita", "valor des de tramitació externa"));
		String processInstanceId = null;
		try {
			processInstanceId = ws.iniciExpedient(
					propietats.getEntornTestCodi(), 
					propietats.getUsuariTestCodi(), 
					propietats.getTipusExpedientCodi(), 
					"1", 
					SELENIUM_TRAMITACIO_EXTERNA_EXPEDIENT_TITOL, 
					valorsFormulari);
		} catch(TramitacioException e) {
			e.printStackTrace();
			fail("Error iniciant l'expedient: " + e.getMessage());
		}		
		if (processInstanceId == null)
			fail("No hi ha cap valor de processInstanceId");
		
		// Valida via WS que s'ha creat amb el valor que toca
		List<CampProces> variables = null;
		try {
			variables = ws.consultarVariablesProces(
					propietats.getEntornTestCodi(), 
					propietats.getUsuariTestCodi(), 
					processInstanceId);
		} catch (TramitacioException e) {
			e.printStackTrace();
			fail("Error consultant les variables: " + e.getMessage());
		}
		assertNotNull("No s'han pogut consultar les variables de l'expedient", variables);

		System.out.println("Variables de l'expedient:");
		boolean validat = false;
		for (CampProces var : variables) {
			validat = validat 
					|| ("var_pare_domini".equals(var.getCodi()) && "1".equals(var.getValor()))
					|| ("1".equals(var.getCodi()) && "valor per text area tramitació externa".equals(var.getValor()))
					|| ("var_sobreescrita".equals(var.getCodi()) && "valor des de tramitació externa".equals(var.getValor()));
			System.out.println(var.getCodi() + "= " + var.getValor());
		}
		if (!validat)
			fail("El valor per a \"tipus_test\" no s'ha fixat bé");
	}
	
	
	/** Consulta la tasca de l'expedient creat */
	@Test
	public void a1_tramitaTasca() throws TramitacioException {
		TramitacioService ws = getClientTramitacio();
		
		List<TascaTramitacio> tasques = new ArrayList<TascaTramitacio>();
		try {
			tasques = ws.consultaTasquesPersonals(
					propietats.getEntornTestCodi(), 
					propietats.getUsuariTestCodi());
		} catch (TramitacioException e) {
			e.printStackTrace();
			fail("Error consultant les tasques personals");
		};
		
		ExpedientInfo expedient = getExpedientProva();
		
		// Si s'ha creat l'expedient de proves continua amb les proves sobre la serva tasca "tasca1"
		if (expedient != null) {
			// Cerca tasca "tasca1"
			TascaTramitacio tasca = null;
			for (TascaTramitacio t : tasques) 
				if ("tasca1".equals(t.getCodi()) 
					&& Long.valueOf(expedient.getProcessInstanceId()).toString().equals(t.getProcessInstanceId()) ) 
				{
					tasca = t;
				}
			if (tasca == null)
				fail("No s'ha trobat la tasca variables de l'expedient de proves");

			// Informa els valors de la tasca 1
			List<ParellaCodiValor> valorsFormulari = new ArrayList<ParellaCodiValor>();
			valorsFormulari.add(new ParellaCodiValor("var_pare_domini", "1"));
			valorsFormulari.add(new ParellaCodiValor("var_fill_1", "valor per text area tramitació externa XXX"));
			valorsFormulari.add(new ParellaCodiValor("var_sobreescrita", "valor des de tramitació externa XXX"));
			ws.setDadesFormulariTasca(
					propietats.getEntornTestCodi(), 
					propietats.getUsuariTestCodi(), 
					tasca.getId(), 
					valorsFormulari);
			
			// Finalitza la tasca 1
			ws.finalitzarTasca(
					propietats.getEntornTestCodi(), 
					propietats.getUsuariTestCodi(), 
					tasca.getId(), 
					null);

		} else
			fail("No es pot cercar la tasca variables si l'expedient no s'ha creat");
	}
	

	/** Esborra l'expedient creat per fer les proves. */
	@Test
	public void a4_esborrarExpedientTramitacioExterna() {
		if (propietats.isEsborrarExpedientTest())
			esborrarExpedientTramitacioExterna();
	}
	
	/** Mètode per iniciar la instància del WS de tramitació. */
	private TramitacioService getClientTramitacio() {
		if (ws == null) {
			String urlEndPoint = propietats.getUrlWsTramitacio();
			ws = (TramitacioService) WsClientUtils.getWsClientProxy(
				TramitacioService.class,
				urlEndPoint,
				null,
				null,
				"NONE",
				false,
				true,
				false,
				60000);
		}
		return ws;
	}
	  
	
	/** Mètode per retornar la informació de l'expedient trobat segons el tipus d'expedient.*/
	private ExpedientInfo getExpedientProva() {

		ExpedientInfo ret = null;
		
		TramitacioService ws = getClientTramitacio();
		// Consulta els expedients
		List<ExpedientInfo> expedients = new ArrayList<ExpedientInfo>();
		try {
			expedients = ws.consultaExpedients(
					propietats.getEntornTestCodi(), 
					propietats.getUsuariTestCodi(), 
					SELENIUM_TRAMITACIO_EXTERNA_EXPEDIENT_TITOL, 
					null, 
					null, 
					null, 
					propietats.getTipusExpedientCodi(), 
					null,
					true, // Iniciats
					false, // Finalitzats
					null, 
					null, 
					null);
			if (expedients != null && !expedients.isEmpty())
				ret = expedients.get(0);
		} catch (TramitacioException e) {
			e.printStackTrace();
			fail("Error consultant l'expedient: " + e.getMessage());
		}		
		if (ret == null)
			fail("No s'ha trobat cap expedient iniciat del tipus " + propietats.getTipusExpedientCodi() + " amb títol \"" + SELENIUM_TRAMITACIO_EXTERNA_EXPEDIENT_TITOL + "\"" );
		
		return ret;
	}	
	
	/** Esborra l'expedient creat per fer les proves. */
	private void esborrarExpedientTramitacioExterna() {
		TramitacioService ws = getClientTramitacio();
		// Consulta els expedients
		List<ExpedientInfo> expedients = new ArrayList<ExpedientInfo>();
		List<ExpedientInfo> expedientsIniciats, expedientsFinalitzats;
		try {
			expedientsIniciats = ws.consultaExpedients(
					propietats.getEntornTestCodi(), 
					propietats.getUsuariTestCodi(), 
					SELENIUM_TRAMITACIO_EXTERNA_EXPEDIENT_TITOL, 
					null, 
					null, 
					null, 
					propietats.getTipusExpedientCodi(), 
					null,
					true, // Iniciats
					false, // Finalitzats
					null, 
					null, 
					null);
			if (expedientsIniciats != null)
				expedients.addAll(expedientsIniciats);

			expedientsFinalitzats = ws.consultaExpedients(
					propietats.getEntornTestCodi(), 
					propietats.getUsuariTestCodi(), 
					SELENIUM_TRAMITACIO_EXTERNA_EXPEDIENT_TITOL, 
					null, 
					null, 
					null, 
					propietats.getTipusExpedientCodi(), 
					null,
					false, // Iniciats
					true, // Finalitzats
					null, 
					null, 
					null);
			if (expedientsFinalitzats != null)
				expedients.addAll(expedientsFinalitzats);
		} catch (TramitacioException e) {
			e.printStackTrace();
			fail("Error consultant els expedients: " + e.getMessage());
		}
		// Esborra els expedients
		if(expedients != null && expedients.size() > 0)
			for (ExpedientInfo expedient : expedients) {
				try {
					ws.deleteExpedient(
							propietats.getEntornTestCodi(), 
							propietats.getUsuariTestCodi(), 
							String.valueOf(expedient.getProcessInstanceId()));
				} catch (TramitacioException e) {
					e.printStackTrace();
					fail("Error esborrant l'expedient: " + e.getMessage());
				}
			}
		
	}

}
