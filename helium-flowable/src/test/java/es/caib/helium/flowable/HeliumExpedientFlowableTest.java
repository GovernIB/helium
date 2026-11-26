package es.caib.helium.flowable;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

import java.lang.reflect.Field;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.flowable.engine.ProcessEngine;
import org.flowable.engine.ProcessEngineConfiguration;
import org.flowable.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import es.caib.helium.bpmn.api.HeliumApi;
import es.caib.helium.bpmn.engine.WTaskInstance;
import es.caib.helium.bpmn.engine.WToken;

/**
 * Comprova les operacions de {@link FlowableEngineImpl} sobre les instàncies de procés d'un expedient (esborrar,
 * anul·lar, finalitzar i desfinalitzar) i l'espera d'una resposta externa des d'un handler (portasignatures, PINBAL).
 */
public class HeliumExpedientFlowableTest {

	private static final String XMLNS =
		"xmlns=\"http://www.omg.org/spec/BPMN/20100524/MODEL\" xmlns:helium=\"http://www.caib.es/helium/bpmn\" " +
		"targetNamespace=\"http://www.caib.es/helium/test\"";

	/** Tasca d'usuari amb un timer de frontera, i un subprocés (activitat de crida) en paral·lel. */
	private static final String BPMN_EXPEDIENT =
		"<?xml version=\"1.0\" encoding=\"UTF-8\"?><definitions " + XMLNS + ">" +
		"  <process id=\"procesExpedient\" isExecutable=\"true\">" +
		"    <startEvent id=\"inici\"/>" +
		"    <sequenceFlow id=\"f1\" sourceRef=\"inici\" targetRef=\"divisio\"/>" +
		"    <parallelGateway id=\"divisio\"/>" +
		"    <sequenceFlow id=\"f2\" sourceRef=\"divisio\" targetRef=\"tramitar\"/>" +
		"    <sequenceFlow id=\"f3\" sourceRef=\"divisio\" targetRef=\"crida\"/>" +
		"    <userTask id=\"tramitar\" name=\"Tramitar\"/>" +
		"    <boundaryEvent id=\"temps\" attachedToRef=\"tramitar\" cancelActivity=\"true\">" +
		"      <timerEventDefinition><timeDuration>PT1H</timeDuration></timerEventDefinition>" +
		"    </boundaryEvent>" +
		"    <sequenceFlow id=\"f4\" sourceRef=\"temps\" targetRef=\"fiTemps\"/>" +
		"    <endEvent id=\"fiTemps\"/>" +
		"    <sequenceFlow id=\"f5\" sourceRef=\"tramitar\" targetRef=\"fi\"/>" +
		"    <endEvent id=\"fi\"/>" +
		"    <callActivity id=\"crida\" calledElement=\"subproces\"/>" +
		"    <sequenceFlow id=\"f6\" sourceRef=\"crida\" targetRef=\"fiCrida\"/>" +
		"    <endEvent id=\"fiCrida\"/>" +
		"  </process>" +
		"  <process id=\"subproces\" isExecutable=\"true\">" +
		"    <startEvent id=\"iniciSub\"/>" +
		"    <sequenceFlow id=\"s1\" sourceRef=\"iniciSub\" targetRef=\"tascaSub\"/>" +
		"    <userTask id=\"tascaSub\" name=\"Tasca del subprocés\"/>" +
		"    <sequenceFlow id=\"s2\" sourceRef=\"tascaSub\" targetRef=\"fiSub\"/>" +
		"    <endEvent id=\"fiSub\"/>" +
		"  </process>" +
		"</definitions>";

	/**
	 * Handler que envia una petició amb resposta asíncrona (com el de portasignatures) i, després, una receive task
	 * que espera la resposta amb dues transicions de sortida.
	 */
	private static final String BPMN_ESPERA =
		"<?xml version=\"1.0\" encoding=\"UTF-8\"?><definitions " + XMLNS + ">" +
		"  <process id=\"procesEspera\" isExecutable=\"true\">" +
		"    <startEvent id=\"inici\"/>" +
		"    <sequenceFlow id=\"f1\" sourceRef=\"inici\" targetRef=\"enviar\"/>" +
		"    <serviceTask id=\"enviar\" name=\"Enviar a firmar\" implementation=\"http://www.caib.es/helium/bpmn/handler\">" +
		"      <extensionElements><helium:handler classe=\"es.caib.helium.flowable.HandlerEsperaProva\"/></extensionElements>" +
		"    </serviceTask>" +
		"    <sequenceFlow id=\"fEnviat\" sourceRef=\"enviar\" targetRef=\"esperarFirma\"/>" +
		"    <receiveTask id=\"esperarFirma\" name=\"Esperar la firma\"/>" +
		"    <sequenceFlow id=\"fSignat\" name=\"Signat\" sourceRef=\"esperarFirma\" targetRef=\"signat\"/>" +
		"    <sequenceFlow id=\"fRebutjat\" name=\"Rebutjat\" sourceRef=\"esperarFirma\" targetRef=\"rebutjat\"/>" +
		"    <userTask id=\"signat\" name=\"Signat\"/>" +
		"    <userTask id=\"rebutjat\" name=\"Rebutjat\"/>" +
		"    <sequenceFlow id=\"f2\" sourceRef=\"signat\" targetRef=\"fiS\"/>" +
		"    <sequenceFlow id=\"f3\" sourceRef=\"rebutjat\" targetRef=\"fiR\"/>" +
		"    <endEvent id=\"fiS\"/><endEvent id=\"fiR\"/>" +
		"  </process>" +
		"  <process id=\"procesSenseEspera\" isExecutable=\"true\">" +
		"    <startEvent id=\"iniciSE\"/>" +
		"    <sequenceFlow id=\"se1\" sourceRef=\"iniciSE\" targetRef=\"enviarSE\"/>" +
		"    <serviceTask id=\"enviarSE\" implementation=\"http://www.caib.es/helium/bpmn/handler\">" +
		"      <extensionElements><helium:handler classe=\"es.caib.helium.flowable.HandlerEsperaProva\"/></extensionElements>" +
		"    </serviceTask>" +
		"    <sequenceFlow id=\"se2\" sourceRef=\"enviarSE\" targetRef=\"revisarSE\"/>" +
		"    <userTask id=\"revisarSE\"/>" +
		"    <sequenceFlow id=\"se3\" sourceRef=\"revisarSE\" targetRef=\"fiSE\"/>" +
		"    <endEvent id=\"fiSE\"/>" +
		"  </process>" +
		"</definitions>";

	private static ProcessEngine processEngine;
	private static FlowableEngineImpl engine;

	@BeforeAll
	static void arrancarMotor() throws Exception {
		HeliumServiceTaskXMLConverter.registrar();
		HeliumHandlerJavaDelegate delegate = new HeliumHandlerJavaDelegate(
			MotorHeliumProva.crear(mock(HeliumApi.class)));
		ProcessEngineConfigurationImpl configuration = (ProcessEngineConfigurationImpl) ProcessEngineConfiguration
			.createStandaloneInMemProcessEngineConfiguration()
			.setJdbcUrl("jdbc:h2:mem:helium-expedient;DB_CLOSE_DELAY=-1");
		Map<Object, Object> beans = new HashMap<>();
		beans.put(HeliumHandlerJavaDelegate.BEAN_NAME, delegate);
		configuration.setBeans(beans);
		FlowableConfig.afegirHeliumPreDeployer(configuration);
		FlowableConfig.afegirAgendaHelium(configuration);
		processEngine = configuration.buildProcessEngine();
		processEngine.getRepositoryService().createDeployment()
			.addString("expedient.bpmn20.xml", BPMN_EXPEDIENT)
			.addString("espera.bpmn20.xml", BPMN_ESPERA)
			.deploy();
		engine = new FlowableEngineImpl();
		Field camp = FlowableEngineImpl.class.getDeclaredField("processEngine");
		camp.setAccessible(true);
		camp.set(engine, processEngine);
	}

	@AfterAll
	static void aturarMotor() {
		if (processEngine != null) {
			processEngine.close();
		}
	}

	@BeforeEach
	void setUp() {
		HandlerEsperaProva.EXECUCIONS_EN_ESPERA.clear();
	}

	// Esborrar (punt 1)

	@Test
	void esborrarEliminaLaInstanciaElsSubprocessosILHistorial() {
		ProcessInstance pi = iniciarExpedient();
		List<String> arbre = idsArbre(pi.getId());
		assertEquals(2, arbre.size());

		// L'esborrat de l'expedient recorre l'arbre: en esborrar el pare els fills ja no hi són
		for (String id : arbre) {
			engine.deleteProcessInstance(id);
		}

		for (String id : arbre) {
			assertNull(processEngine.getRuntimeService().createProcessInstanceQuery().processInstanceId(id).singleResult());
			assertNull(processEngine.getHistoryService().createHistoricProcessInstanceQuery().processInstanceId(id).singleResult());
		}
		assertEquals(0, processEngine.getManagementService().createTimerJobQuery().processInstanceId(pi.getId()).count());
	}

	// Subprocessos: procés pare i títol (handlers de documents del procés pare i ProcesTitolModificarHandler)

	@Test
	void unSubprocesEnCursConeixElSeuPareIPotCanviarDeTitol() {
		ProcessInstance pi = iniciarExpedient();
		String subproces = idsArbre(pi.getId()).stream().filter(id -> !id.equals(pi.getId())).findFirst().orElseThrow();
		assertEquals(pi.getId(), engine.getProcessInstance(subproces).getParentProcessInstanceId());
		assertNull(engine.getProcessInstance(pi.getId()).getParentProcessInstanceId());

		engine.setProcessInstanceTitol(subproces, "Al·legació 1");

		assertEquals("Al·legació 1", engine.getProcessInstance(subproces).getDescription());
		assertTrue(engine.getProcessInstanceTree(pi.getId()).stream()
			.anyMatch(p -> p.getId().equals(subproces) && "Al·legació 1".equals(p.getDescription())));
	}

	// Anul·lar / desanul·lar (punt 2)

	@Test
	void anullarSuspenLesInstanciesITimersIDesanullarLesReactiva() {
		ProcessInstance pi = iniciarExpedient();
		String[] arbre = idsArbre(pi.getId()).toArray(new String[0]);
		Task tramitar = tasca(pi.getId(), "tramitar");

		engine.suspendProcessInstances(arbre);
		// Es pot cridar dues vegades (instàncies ja suspeses)
		engine.suspendProcessInstances(arbre);
		for (String id : arbre) {
			assertTrue(processEngine.getRuntimeService().createProcessInstanceQuery().processInstanceId(id).singleResult().isSuspended());
		}
		assertTrue(engine.getTaskById(tramitar.getId()).isSuspended());
		assertEquals(0, processEngine.getManagementService().createTimerJobQuery().processInstanceId(pi.getId()).count());
		assertEquals(1, processEngine.getManagementService().createSuspendedJobQuery().processInstanceId(pi.getId()).count());

		engine.resumeProcessInstances(arbre);
		engine.resumeProcessInstances(arbre);
		for (String id : arbre) {
			assertFalse(processEngine.getRuntimeService().createProcessInstanceQuery().processInstanceId(id).singleResult().isSuspended());
		}
		assertFalse(engine.getTaskById(tramitar.getId()).isSuspended());
		assertEquals(1, processEngine.getManagementService().createTimerJobQuery().processInstanceId(pi.getId()).count());
	}

	// Finalitzar / desfinalitzar (punt 4)

	@Test
	void finalitzarCancelaLesTasquesISuspenSenseEsborrar() {
		ProcessInstance pi = iniciarExpedient();
		List<String> arbre = idsArbre(pi.getId());
		Task tramitar = tasca(pi.getId(), "tramitar");

		engine.finalitzarExpedient(arbre.toArray(new String[0]), new Date());

		// Les tasques obertes s'han cancel·lat, sense fer avançar el procés
		assertEquals(0, tasquesArbre(arbre));
		WTaskInstance cancelada = engine.getTaskById(tramitar.getId());
		assertTrue(cancelada.isCancelled());
		// Les instàncies continuen existint, suspeses i aturades a les activitats
		for (String id : arbre) {
			ProcessInstance instancia = processEngine.getRuntimeService().createProcessInstanceQuery().processInstanceId(id).singleResult();
			assertNotNull(instancia);
			assertTrue(instancia.isSuspended());
		}
		assertTrue(processEngine.getRuntimeService().getActiveActivityIds(pi.getId()).contains("tramitar"));

		engine.desfinalitzarExpedient(pi.getId());
		for (String id : arbre) {
			assertFalse(processEngine.getRuntimeService().createProcessInstanceQuery().processInstanceId(id).singleResult().isSuspended());
		}
		// Les tasques cancel·lades no es tornen a crear (com amb jBPM)
		assertEquals(0, tasquesArbre(arbre));
	}

	@Test
	void desfinalitzarUnProcesAcabatNoFaRes() {
		ProcessInstance pi = processEngine.getRuntimeService().startProcessInstanceByKey("subproces");
		processEngine.getTaskService().complete(tasca(pi.getId(), "tascaSub").getId());
		assertNotNull(engine.getProcessInstance(pi.getId()).getEndTime());

		engine.desfinalitzarExpedient(pi.getId());

		assertNull(processEngine.getRuntimeService().createProcessInstanceQuery().processInstanceId(pi.getId()).singleResult());
	}

	// Espera de resposta externa (punts 15 i 16)

	@Test
	void lExecucioDelHandlerEsperaALaReceiveTaskQueVeDespres() {
		ProcessInstance pi = processEngine.getRuntimeService().startProcessInstanceByKey("procesEspera");

		assertEquals(1, HandlerEsperaProva.EXECUCIONS_EN_ESPERA.size());
		String executionId = HandlerEsperaProva.EXECUCIONS_EN_ESPERA.get(0);
		assertEquals(List.of("esperarFirma"), processEngine.getRuntimeService().getActiveActivityIds(pi.getId()));
		// L'execució que ha desat el handler és la que espera a la receive task
		WToken token = engine.getTokenById(executionId);
		assertTrue(token.isReceiveTask());
		assertEquals("Esperar la firma", token.getNodeName());
		assertEquals(List.of("fSignat", "fRebutjat"), token.getSortides());
	}

	@Test
	void unaTascaDeHandlerNoEsperaSiNoHiHaReceiveTask() {
		ProcessInstance pi = processEngine.getRuntimeService().startProcessInstanceByKey("procesSenseEspera");

		// El procés continua: la resposta no trobarà l'execució esperant a cap receive task
		assertEquals(List.of("revisarSE"), tasquesActives(pi.getId()));
		WToken token = engine.getTokenById(HandlerEsperaProva.EXECUCIONS_EN_ESPERA.get(0));
		assertFalse(token.isReceiveTask());
	}

	@Test
	void laRespostaRepenLExecucioPerLaTransicioIndicada() {
		ProcessInstance pi = processEngine.getRuntimeService().startProcessInstanceByKey("procesEspera");
		String executionId = HandlerEsperaProva.EXECUCIONS_EN_ESPERA.get(0);

		engine.signalToken(executionId, "fRebutjat");

		assertEquals(List.of("rebutjat"), tasquesActives(pi.getId()));
	}

	@Test
	void laRespostaAcceptaLIdDeLActivitatDesti() {
		ProcessInstance pi = processEngine.getRuntimeService().startProcessInstanceByKey("procesEspera");
		engine.signalToken(HandlerEsperaProva.EXECUCIONS_EN_ESPERA.get(0), "signat");
		assertEquals(List.of("signat"), tasquesActives(pi.getId()));
	}

	@Test
	void laRespostaSenseTransicioSegueixElsFluxosHabituals() {
		ProcessInstance pi = processEngine.getRuntimeService().startProcessInstanceByKey("procesEspera");
		engine.signalToken(HandlerEsperaProva.EXECUCIONS_EN_ESPERA.get(0), null);
		assertEquals(List.of("rebutjat", "signat"), tasquesActives(pi.getId()));
	}

	private ProcessInstance iniciarExpedient() {
		return processEngine.getRuntimeService().startProcessInstanceByKey("procesExpedient");
	}

	private List<String> idsArbre(String processInstanceId) {
		return engine.getProcessInstanceTree(processInstanceId)
			.stream()
			.map(es.caib.helium.bpmn.engine.WProcessInstance::getId)
			.collect(Collectors.toList());
	}

	private long tasquesArbre(List<String> processInstanceIds) {
		return processInstanceIds.stream()
			.mapToLong(id -> processEngine.getTaskService().createTaskQuery().processInstanceId(id).count())
			.sum();
	}

	private Task tasca(String processInstanceId, String codi) {
		return processEngine.getTaskService().createTaskQuery()
			.processInstanceId(processInstanceId)
			.taskDefinitionKey(codi)
			.singleResult();
	}

	private List<String> tasquesActives(String processInstanceId) {
		return processEngine.getTaskService().createTaskQuery()
			.processInstanceId(processInstanceId)
			.list()
			.stream()
			.map(Task::getTaskDefinitionKey)
			.sorted()
			.collect(Collectors.toList());
	}

}
