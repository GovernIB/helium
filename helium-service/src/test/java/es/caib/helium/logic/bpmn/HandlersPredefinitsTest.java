package es.caib.helium.logic.bpmn;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import es.caib.helium.bpmn.api.HeliumApi;
import es.caib.helium.bpmn.exception.BpmnException;
import es.caib.helium.bpmn.exception.HeliumHandlerException;
import es.caib.helium.bpmn.handler.HandlerParametres;
import es.caib.helium.bpmn.handler.HeliumBpmnHandler;
import es.caib.helium.bpmn.model.ConsultaPinbal;
import es.caib.helium.bpmn.model.DadesRegistreSortida;
import es.caib.helium.bpmn.model.RespostaRegistre;
import es.caib.helium.logic.helper.PluginHelper;

/**
 * Comprova que els handlers predefinits converteixen els paràmetres i criden l'operació de la {@link HeliumApi}.
 */
public class HandlersPredefinitsTest {

	private HeliumApi heliumApi;

	@BeforeEach
	void setUp() {
		heliumApi = mock(HeliumApi.class);
	}

	@Test
	void georefAdmetComaDecimalIValorsBuits() {
		executar(new ExpedientGeorefModificarHandler(), Map.of("posx", "2,5", "posy", "3.25", "referencia", " "));
		verify(heliumApi).expedientGeorefModificar(2.5, 3.25, null);
	}

	@Test
	void georefAmbUnaCoordenadaNoNumericaFalla() {
		assertThrows(
			HeliumHandlerException.class,
			() -> executar(new ExpedientGeorefModificarHandler(), Map.of("posx", "nord")));
	}

	@Test
	void reprendreIModificarTitolDelProces() {
		executar(new ExpedientReprendreHandler(), Map.of());
		verify(heliumApi).expedientReprendre();
		executar(new ProcesTitolModificarHandler(), Map.of("titol", "Al·legació 2"));
		verify(heliumApi).procesTitolModificar("Al·legació 2");
	}

	@Test
	void documentsDelProcesPare() {
		executar(new DocumentEscriureProcesPareHandler(), Map.of("documentCodi", "resolucio"));
		verify(heliumApi).documentEscriureProcesPare("resolucio");
		executar(new DocumentLlegirProcesPareHandler(), Map.of("documentCodi", "sollicitud"));
		verify(heliumApi).documentLlegirProcesPare("sollicitud");
	}

	@Test
	void copiarDocumentsEntreExpedients() {
		executar(new DocumentExpedientCopiarDestiHandler(), Map.of(
			"documentCodi", "informe",
			"destiExpedientTipus", "",
			"destiExpedientNumero", "2026/15"));
		verify(heliumApi).documentCopiarAExpedient("informe", null, "2026/15", null);

		executar(new DocumentExpedientCopiarOrigenHandler(), Map.of(
			"documentCodi", "informe",
			"origenExpedientTipus", "SUBV",
			"origenExpedientNumero", "2026/7",
			"origenExpedientDocument", "informeTecnic"));
		verify(heliumApi).documentCopiarDesDExpedient("SUBV", "2026/7", "informeTecnic", "informe");
	}

	@Test
	void generarDocumentAmbDataOSenseData() throws Exception {
		executar(new DocumentGenerarHandler(), Map.of("documentCodi", "ofici", "data", "15/03/2026"));
		verify(heliumApi).documentGenerar("ofici", new SimpleDateFormat("dd/MM/yyyy").parse("15/03/2026"));
		executar(new DocumentGenerarHandler(), Map.of("documentCodi", "resolucio"));
		verify(heliumApi).documentGenerar("resolucio", null);
	}

	@Test
	void registreDeSortidaDesaElNumeroILaData() {
		RespostaRegistre resposta = new RespostaRegistre();
		resposta.setNumero("GOIBS123/2026");
		resposta.setData(new Date(0));
		when(heliumApi.registreSortida(any())).thenReturn(resposta);
		Map<String, Object> parametres = new HashMap<>();
		parametres.put("entitatCodi", "A04003003");
		parametres.put("oficinaCodi", "O00000001");
		parametres.put("codiDocument", "ofici");
		parametres.put("documentOrigen", "1");
		parametres.put("interessatTipus", "PERSONA_FISICA");
		parametres.put("interessatNom", "Joana");
		parametres.put("representantNom", "Pere");
		parametres.put("varNumeroRegistre", "numRegistre");
		parametres.put("varDataRegistre", "dataRegistre");

		executar(new RegistreSortidaRegWeb3Handler(), parametres);

		ArgumentCaptor<DadesRegistreSortida> dades = ArgumentCaptor.forClass(DadesRegistreSortida.class);
		verify(heliumApi).registreSortida(dades.capture());
		assertEquals("A04003003", dades.getValue().getEntitatCodi());
		assertEquals("ofici", dades.getValue().getDocumentCodi());
		assertEquals(1, dades.getValue().getDocumentOrigen());
		assertEquals("PERSONA_FISICA", dades.getValue().getInteressat().getTipus());
		assertEquals("Joana", dades.getValue().getInteressat().getNom());
		// Sense tipus, l'API no l'afegeix com a representant
		assertNull(dades.getValue().getRepresentant().getTipus());
		assertEquals("Pere", dades.getValue().getRepresentant().getNom());
		verify(heliumApi).setVariable("numRegistre", "GOIBS123/2026");
		verify(heliumApi).setVariable("dataRegistre", new Date(0));
	}

	@Test
	void consultaPinbalEspecificaAsincrona() {
		executar(new PinbalSvddgpciws02Handler(), Map.of(
			"documentCodi", "justificant",
			"finalitat", "Tramitació",
			"consentiment", "SI",
			"titularTipusDocumentacio", "NIF",
			"titularDocumentacio", "12345678Z",
			"titularNom", "Joana Riera",
			"anyNaixement", "1980",
			"asincrona", "true",
			"transicioOK", "fRebuda"));
		ConsultaPinbal consulta = consultaEnviada();
		assertEquals(PluginHelper.serveiConsultaDades, consulta.getServeiCodi());
		assertTrue(consulta.isAsincrona());
		assertEquals("Joana Riera", consulta.getTitularNomComplet());
		assertEquals("1980", consulta.getAnyNaixement());
		assertEquals("fRebuda", consulta.getTransicioOK());
	}

	@Test
	void consultaPinbalGenericaNecessitaElServei() {
		executar(new PinbalConsultaGenericaHandler(), Map.of(
			"serveiCodi", "SVDSCDDWS01",
			"documentCodi", "justificant",
			"dadesEspecifiques", "<DatosEspecificos/>"));
		ConsultaPinbal consulta = consultaEnviada();
		assertEquals("SVDSCDDWS01", consulta.getServeiCodi());
		assertEquals("<DatosEspecificos/>", consulta.getDadesEspecifiques());
		assertFalse(consulta.isAsincrona());

		assertThrows(
			HeliumHandlerException.class,
			() -> executar(new PinbalConsultaGenericaHandler(), Map.of("documentCodi", "justificant")));
	}

	@Test
	void consultaPinbalVerificacioIObligacionsTributaries() {
		executar(new PinbalSvddgpviws02Handler(), Map.of("documentCodi", "a", "titularNom", "Joana"));
		ConsultaPinbal verificacio = consultaEnviada();
		assertEquals(PluginHelper.serveiVerificacioDades, verificacio.getServeiCodi());
		// Aquest servei no accepta el nom complet
		assertNull(verificacio.getTitularNomComplet());

		heliumApi = mock(HeliumApi.class);
		executar(new PinbalSvdccaacpasws01Handler(), Map.of("documentCodi", "a"));
		assertEquals(PluginHelper.serveiObligacionsTributaries, consultaEnviada().getServeiCodi());
	}

	private ConsultaPinbal consultaEnviada() {
		ArgumentCaptor<ConsultaPinbal> consulta = ArgumentCaptor.forClass(ConsultaPinbal.class);
		verify(heliumApi).consultaPinbal(consulta.capture());
		return consulta.getValue();
	}

	private void executar(HeliumBpmnHandler handler, Map<String, ?> parametres) {
		HandlerParametres.injectar(handler, parametres);
		try {
			handler.execute(heliumApi);
		} catch (BpmnException ex) {
			throw new AssertionError(ex);
		}
	}

}
