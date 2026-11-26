package es.caib.helium.logic.bpmn;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Date;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import es.caib.helium.bpmn.api.HeliumApi;
import es.caib.helium.bpmn.exception.HeliumHandlerException;
import es.caib.helium.bpmn.model.ConsultaPinbal;
import es.caib.helium.bpmn.model.DocumentInfo;
import es.caib.helium.commons.dto.ArxiuDto;
import es.caib.helium.logic.helper.AlertaHelper;
import es.caib.helium.logic.helper.ConsultaPinbalHelper;
import es.caib.helium.logic.helper.DocumentHelperV3;
import es.caib.helium.logic.helper.ExpedientDadaHelper;
import es.caib.helium.logic.helper.ExpedientDocumentHelper;
import es.caib.helium.logic.helper.ExpedientHelper;
import es.caib.helium.logic.helper.ExpedientInteressatHelper;
import es.caib.helium.logic.helper.MailHelper;
import es.caib.helium.logic.helper.PluginHelper;
import es.caib.helium.persistence.entity.Document;
import es.caib.helium.persistence.entity.DocumentStore;
import es.caib.helium.persistence.entity.Expedient;

/**
 * Comprova a quina instància de procés van les operacions de documents de la {@link HeliumApiImpl}: la del handler
 * en els expedients per flux i la de l'expedient en les accions dels expedients per estats (sense instància de
 * procés).
 */
public class HeliumApiImplDocumentsTest {

	private static final String PROCES_EXPEDIENT = "pi-expedient";
	private static final String PROCES_SUBPROCES = "pi-subproces";

	private Expedient expedient;
	private ExpedientDadaHelper expedientDadaHelper;
	private ExpedientDocumentHelper expedientDocumentHelper;
	private DocumentHelperV3 documentHelperV3;
	private ConsultaPinbalHelper consultaPinbalHelper;
	private HeliumApiServeis serveis;

	@BeforeEach
	void setUp() {
		expedient = new Expedient();
		expedient.setId(7L);
		expedient.setProcessInstanceId(PROCES_EXPEDIENT);
		expedientDadaHelper = mock(ExpedientDadaHelper.class);
		expedientDocumentHelper = mock(ExpedientDocumentHelper.class);
		documentHelperV3 = mock(DocumentHelperV3.class);
		consultaPinbalHelper = mock(ConsultaPinbalHelper.class);
		serveis = mock(HeliumApiServeis.class);
		when(serveis.getConsultaPinbalHelper()).thenReturn(consultaPinbalHelper);
		DocumentStore desat = new DocumentStore();
		desat.setId(1L);
		when(documentHelperV3.crearActualitzarDocument(
				any(), any(), any(), any(), any(), any(), any(), anyBoolean(), anyBoolean(), any(), any(), any(), any(), any()))
			.thenReturn(desat);
	}

	@Test
	void enUnExpedientPerEstatsElsDocumentsSonALaInstanciaDeLExpedient() {
		HeliumApi api = heliumApi(null);

		api.setDocument("informe", "informe.pdf", new byte[] {1}, new Date(), false, false, null);
		verify(documentHelperV3).crearActualitzarDocument(
			isNull(), eq(PROCES_EXPEDIENT), eq("informe"), any(), eq("informe.pdf"), any(), isNull(),
			eq(false), eq(false), isNull(), isNull(), isNull(), isNull(), isNull());

		// Abans es cercava el document només pel codi, sense filtrar per expedient
		api.deleteDocument("informe");
		verify(documentHelperV3).esborrarDocument(null, PROCES_EXPEDIENT, "informe");
	}

	@Test
	void enUnExpedientPerEstatsEsGeneraElDocumentAmbPlantilla() {
		Document document = new Document();
		document.setPlantilla(true);
		when(documentHelperV3.findDocumentDisseny(7L, PROCES_EXPEDIENT, "ofici")).thenReturn(document);
		when(documentHelperV3.generarDocumentAmbPlantillaIConvertir(eq(expedient), eq(document), isNull(), eq(PROCES_EXPEDIENT), any()))
			.thenReturn(new ArxiuDto("ofici.pdf", new byte[] {1}));

		DocumentInfo generat = heliumApi(null).documentGenerar("ofici", null);

		assertEquals(PROCES_EXPEDIENT, generat.getProcessInstanceId());
		verify(documentHelperV3).crearActualitzarDocument(
			isNull(), eq(PROCES_EXPEDIENT), eq("ofici"), any(), eq("ofici.pdf"), any(), isNull(),
			eq(false), eq(false), isNull(), isNull(), isNull(), isNull(), isNull());
	}

	@Test
	void enUnExpedientPerEstatsLaConsultaPinbalDesaElJustificantALExpedientISenseEspera() {
		ConsultaPinbal consulta = new ConsultaPinbal();
		consulta.setAsincrona(true);

		heliumApi(null).consultaPinbal(consulta);

		// Fora del motor no hi ha cap execució que hagi d'esperar la resposta
		verify(consultaPinbalHelper).consultaDesDeHandler(expedient, PROCES_EXPEDIENT, null, consulta, null);
	}

	@Test
	void enUnExpedientPerEstatsLesOperacionsDeProcesNoEsPodenFer() {
		HeliumApi api = heliumApi(null);
		assertThrows(HeliumHandlerException.class, () -> api.procesTitolModificar("Títol"));
		assertThrows(HeliumHandlerException.class, () -> api.documentEscriureProcesPare("informe"));
		assertThrows(HeliumHandlerException.class, () -> api.documentLlegirProcesPare("informe"));
	}

	@Test
	void enUnExpedientPerFluxElsDocumentsSonALaInstanciaDelHandler() {
		HeliumApi api = heliumApi(PROCES_SUBPROCES);

		api.deleteDocument("informe");
		verify(documentHelperV3).esborrarDocument(null, PROCES_SUBPROCES, "informe");

		ExecucioHandler execucio = ExecucioHandler.obrir("execucio-1", PROCES_SUBPROCES);
		try {
			ConsultaPinbal consulta = new ConsultaPinbal();
			consulta.setAsincrona(true);
			api.consultaPinbal(consulta);
			verify(consultaPinbalHelper).consultaDesDeHandler(expedient, PROCES_SUBPROCES, "execucio-1", consulta, null);
		} finally {
			execucio.tancar();
		}
	}

	private HeliumApi heliumApi(String processId) {
		return new HeliumApiImpl(
			expedient,
			processId,
			null,
			mock(ExpedientHelper.class),
			expedientDadaHelper,
			expedientDocumentHelper,
			documentHelperV3,
			mock(PluginHelper.class),
			mock(AlertaHelper.class),
			mock(ExpedientInteressatHelper.class),
			mock(MailHelper.class),
			serveis);
	}

}
