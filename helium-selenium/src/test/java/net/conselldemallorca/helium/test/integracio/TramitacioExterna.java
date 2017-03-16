package net.conselldemallorca.helium.test.integracio;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import net.conselldemallorca.helium.core.util.ws.WsClientUtils;
import net.conselldemallorca.helium.test.BaseTest;
import net.conselldemallorca.helium.ws.tramitacio.CampProces;
import net.conselldemallorca.helium.ws.tramitacio.CampTasca;
import net.conselldemallorca.helium.ws.tramitacio.ExpedientInfo;
import net.conselldemallorca.helium.ws.tramitacio.ParellaCodiValor;
import net.conselldemallorca.helium.ws.tramitacio.TascaTramitacio;
import net.conselldemallorca.helium.ws.tramitacio.TramitacioException;
import net.conselldemallorca.helium.ws.tramitacio.TramitacioService;

/** Tests per probar el webservice de tramitació externa a través del qual es pot iniciar
 * un expedient, informar les seves variables, tramitar tasques i altres.
 * En aquest cap no és necessari utilitzar selenium per interaccionar amb el navegador ja
 * que totes les crides es fan a través del web service de tramitació així com les comprovacions.
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TramitacioExterna extends BaseTest {
	
	private static final String SELENIUM_TRAMITACIO_EXTERNA_EXPEDIENT_TITOL = "Selenium tramitació externa";
	// Client al WS de tramitació
	private TramitacioService ws = null;
			
	@Before
	public void setUp() throws Exception {
		// No importa obrir el navegador
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
		valorsFormulari.add(new ParellaCodiValor("tipus_test", "variables"));
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
		boolean validat = false;
		for (CampProces var : variables)
			validat = validat || ("tipus_test".equals(var.getCodi()) && "variables".equals(var.getValor()));
		if (!validat)
			fail("El valor per a \"tipus_test\" no s'ha fixat bé");
	}
	
	
	/** Consulta la tasca de l'expedient creat */
	@Test
	public void a1_consultaTasca() throws TramitacioException {
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
		
		// Si s'ha creat l'expedient de proves continua amb les proves sobre la serva tasca "variables"
		if (expedient != null) {
			// Cerca tasca "variables"
			TascaTramitacio tasca = null;
			for (TascaTramitacio t : tasques) 
				if ("variables".equals(t.getCodi()) 
					&& Long.valueOf(expedient.getProcessInstanceId()).toString().equals(t.getProcessInstanceId()) ) 
				{
					tasca = t;
				}
			if (tasca == null)
				fail("No s'ha trobat la tasca variables de l'expedient de proves");
		} else
			fail("No es pot cercar la tasca variables si l'expedient no s'ha creat");
	}
	
	/** Prova a modificar el valor d'una variable. */
	@Test
	public void a2_modificarVariables() {
		TramitacioService ws = getClientTramitacio(); 
		// Recupera la tasca
		TascaTramitacio tasca = getTascaVariables();
		// Posa el valor de la variable 
		String text = "text " + new Date().toString();
		try {
			List<ParellaCodiValor> valors = new ArrayList<ParellaCodiValor>();
			valors.add(new ParellaCodiValor("text", text));
			ws.setDadesFormulariTasca(
						propietats.getEntornTestCodi(), 
						propietats.getUsuariTestCodi(),
						tasca.getId(), 
						valors);
		} catch (TramitacioException e) {
			e.printStackTrace();
			fail("No s'ha pogut fixar el valor de la variable text: " + e.getMessage());
		}
		
		// Comprova que s'ha fixat bé el valor
		boolean trobada = false;
		try {
			List<CampTasca> camps = ws.consultaFormulariTasca(
					propietats.getEntornTestCodi(), 
					propietats.getUsuariTestCodi(),
					tasca.getId());
			for (CampTasca camp : camps)
				if ("text".equals(camp.getCodi()) ) {
					assertTrue("El valor de la variable text és diferent a l'esperat: \"" + text + "\" != \"" + camp.getValor() + "\"",
							text.equals(camp.getValor()));
					trobada = true;
					break;
				}
		} catch (TramitacioException e) {
			e.printStackTrace();
			fail("Error consultant les variables del procés " + tasca.getProcessInstanceId());
		}
		if (!trobada)
			fail("No s'ha trobar la variable var_data");

	}
	
	
	/** Comprova que es pugui finalitzar una tasca remotament
	 * 
	 */
	@Test
	public void a3_finalitzarTasca() {
		TramitacioService ws = getClientTramitacio();
		// Recupera la tasca
		TascaTramitacio tasca = getTascaVariables();		
		try {
			ws.finalitzarTasca(
					propietats.getEntornTestCodi(), 
					propietats.getUsuariTestCodi(), 
					tasca.getId(), 
					null);
		} catch (TramitacioException e) {
			e.printStackTrace();
			fail("Error finalitzant la tasca: " + e);
		}
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
	
	/** Mètode per consultar i trobar la tasca "variables" d'entre les tasques personals de l'usuari. */
	private TascaTramitacio getTascaVariables() {
		List<TascaTramitacio> tasques = new ArrayList<TascaTramitacio>();
		TascaTramitacio tasca = null;
		try {
			tasques = ws.consultaTasquesPersonals(
					propietats.getEntornTestCodi(), 
					propietats.getUsuariTestCodi());
		} catch (TramitacioException e) {
			e.printStackTrace();
			fail("Error consultant les tasques personals");
		};
		if (tasques != null)
			for (TascaTramitacio t : tasques)
				if ("variables".equals(t.getCodi() ) 
					&& t.getResponsable() != null
					&& t.getResponsable().equals(propietats.getUsuariTestCodi())) {
					tasca = t;
					break;
				}
		if (tasca == null)
			fail("No s'ha trobat una tasca \"variables\" assignada a l'usuari " + propietats.getUsuariTestCodi());
		return tasca;
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
