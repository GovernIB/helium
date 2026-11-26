package es.caib.helium.logic.bpmn;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Date;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import es.caib.helium.bpmn.api.HeliumApi;
import es.caib.helium.bpmn.engine.WProcessInstance;
import es.caib.helium.bpmn.exception.HeliumHandlerException;
import es.caib.helium.bpmn.model.TerminiInfo;
import es.caib.helium.logic.helper.AlertaHelper;
import es.caib.helium.logic.helper.DocumentHelperV3;
import es.caib.helium.logic.helper.ExpedientDadaHelper;
import es.caib.helium.logic.helper.ExpedientDocumentHelper;
import es.caib.helium.logic.helper.ExpedientHelper;
import es.caib.helium.logic.helper.ExpedientInteressatHelper;
import es.caib.helium.logic.helper.MailHelper;
import es.caib.helium.logic.helper.PluginHelper;
import es.caib.helium.logic.helper.TerminiHelper;
import es.caib.helium.logic.intf.service.WorkflowEngineApi;
import es.caib.helium.persistence.entity.DefinicioProces;
import es.caib.helium.persistence.entity.Expedient;
import es.caib.helium.persistence.entity.ExpedientTipus;
import es.caib.helium.persistence.entity.Termini;
import es.caib.helium.persistence.entity.TerminiIniciat;
import es.caib.helium.persistence.repository.DefinicioProcesRepository;
import es.caib.helium.persistence.repository.TerminiIniciatRepository;

/**
 * Operacions de terminis de la {@link HeliumApiImpl}, que substitueixen l'antic {@code HeliumBridge}: cerca del
 * termini pel codi, inici, pausa i errors quan el termini no existeix o no s'ha iniciat. També els handlers de
 * terminis que les fan servir.
 */
public class HeliumApiImplTerminisTest {

	private static final String PROCES = "pi-1";
	private static final Date DATA = new Date(0);

	private ExpedientTipus expedientTipus;
	private Termini termini;
	private TerminiHelper terminiHelper;
	private TerminiIniciatRepository terminiIniciatRepository;
	private WorkflowEngineApi workflowEngineApi;
	private DefinicioProcesRepository definicioProcesRepository;
	private HeliumApi api;

	@BeforeEach
	void setUp() {
		expedientTipus = new ExpedientTipus();
		expedientTipus.setId(3L);
		expedientTipus.setAmbInfoPropia(true);
		Expedient expedient = new Expedient();
		expedient.setId(7L);
		expedient.setTipus(expedientTipus);
		expedient.setProcessInstanceId(PROCES);
		termini = new Termini();
		termini.setId(11L);
		termini.setCodi("resposta");

		terminiHelper = mock(TerminiHelper.class);
		terminiIniciatRepository = mock(TerminiIniciatRepository.class);
		workflowEngineApi = mock(WorkflowEngineApi.class);
		definicioProcesRepository = mock(DefinicioProcesRepository.class);
		HeliumApiServeis serveis = mock(HeliumApiServeis.class);
		when(serveis.getTerminiHelper()).thenReturn(terminiHelper);
		when(serveis.getTerminiIniciatRepository()).thenReturn(terminiIniciatRepository);
		when(serveis.getWorkflowEngineApi()).thenReturn(workflowEngineApi);
		when(serveis.getDefinicioProcesRepository()).thenReturn(definicioProcesRepository);
		when(terminiHelper.findAmbExpedientTipusICodi(expedientTipus, "resposta")).thenReturn(termini);

		api = new HeliumApiImpl(
			expedient,
			PROCES,
			null,
			mock(ExpedientHelper.class),
			mock(ExpedientDadaHelper.class),
			mock(ExpedientDocumentHelper.class),
			mock(DocumentHelperV3.class),
			mock(PluginHelper.class),
			mock(AlertaHelper.class),
			mock(ExpedientInteressatHelper.class),
			mock(MailHelper.class),
			serveis);
	}

	@Test
	void iniciaElTerminiDelTipusDExpedientALaInstanciaDelHandler() {
		api.terminiIniciar("resposta", DATA, false);
		verify(terminiHelper).iniciar(11L, PROCES, DATA, false, false);

		api.terminiIniciar("resposta", DATA, 1, 2, 3, true);
		verify(terminiHelper).iniciar(11L, PROCES, DATA, 1, 2, 3, true, false);
	}

	@Test
	void senseInfoPropiaElTerminiEsDeLaDefinicioDeProces() {
		expedientTipus.setAmbInfoPropia(false);
		WProcessInstance processInstance = new WProcessInstance();
		processInstance.setProcessDefinitionId("pd-1");
		DefinicioProces definicioProces = new DefinicioProces();
		when(workflowEngineApi.getProcessInstance(PROCES)).thenReturn(processInstance);
		when(definicioProcesRepository.findByBpmId("pd-1")).thenReturn(definicioProces);
		when(terminiHelper.findAmbDefinicioProcesICodi(definicioProces, "resposta")).thenReturn(termini);

		api.terminiIniciar("resposta", DATA, false);

		verify(terminiHelper).iniciar(11L, PROCES, DATA, false, false);
	}

	@Test
	void unCodiQueNoEsCapTerminiDonaError() {
		HeliumHandlerException ex = assertThrows(
			HeliumHandlerException.class,
			() -> api.terminiIniciar("inexistent", DATA, false));
		assertEquals("No existeix cap termini amb el codi 'inexistent'", ex.getMessage());
		assertThrows(HeliumHandlerException.class, () -> api.getTerminiIniciat("inexistent"));
	}

	@Test
	void retornaElTerminiIniciatONullSiNoSHaIniciat() {
		assertNull(api.getTerminiIniciat("resposta"));

		TerminiIniciat iniciat = new TerminiIniciat(termini, 0, 1, 0, PROCES, DATA, new Date(1000));
		iniciat.setId(5L);
		when(terminiIniciatRepository.findByTerminiAndProcessInstanceId(termini, PROCES)).thenReturn(iniciat);

		TerminiInfo info = api.getTermini("resposta");
		assertEquals(5L, info.getId());
		assertEquals("resposta", info.getCodi());
		assertEquals(1, info.getMesos());
		assertEquals(DATA, info.getDataInici());
	}

	@Test
	void pausaElTerminiIniciatIDonaErrorSiNoSHaIniciat() {
		assertThrows(HeliumHandlerException.class, () -> api.terminiPausar("resposta", DATA));
		verify(terminiHelper, never()).pausar(anyLong(), any(), anyBoolean());

		TerminiIniciat iniciat = new TerminiIniciat(termini, PROCES, DATA, DATA);
		iniciat.setId(5L);
		when(terminiIniciatRepository.findByTerminiAndProcessInstanceId(termini, PROCES)).thenReturn(iniciat);
		api.terminiPausar("resposta", DATA);
		verify(terminiHelper).pausar(5L, DATA, false);

		// Els errors d'estat del termini arriben com a error de handler
		doThrow(new IllegalStateException("no pausat")).when(terminiHelper).continuar(eq(5L), any(), eq(false));
		assertThrows(HeliumHandlerException.class, () -> api.terminiContinuar("resposta", null));
	}

	@Test
	void elsHandlersDeTerminisFanServirLaHeliumApi() throws Exception {
		TerminiIniciarHandler iniciar = new TerminiIniciarHandler();
		iniciar.setTerminiCodi("resposta");
		iniciar.setData("01/01/1970");
		iniciar.setTermini("0/2/10");
		iniciar.execute(api);
		verify(terminiHelper).iniciar(eq(11L), eq(PROCES), any(), eq(0), eq(2), eq(10), eq(false), eq(false));

		// Pausar un termini que no s'ha iniciat no fa res
		TerminiPausarHandler pausar = new TerminiPausarHandler();
		pausar.setTerminiCodi("resposta");
		pausar.execute(api);
		verify(terminiHelper, never()).pausar(anyLong(), any(), anyBoolean());
	}

}
