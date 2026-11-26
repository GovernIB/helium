package es.caib.helium.flowable;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.flowable.common.engine.api.FlowableIllegalArgumentException;
import org.flowable.engine.ProcessEngine;
import org.flowable.engine.ProcessEngineConfiguration;
import org.flowable.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import es.caib.helium.bpmn.engine.WProcessDefinition;
import es.caib.helium.bpmn.engine.WTaskInstance;
import es.caib.helium.commons.dto.TascaOutcomeDto;
import es.caib.helium.flowable.FlowableConfig;

/**
 * Comprova la tramitació de tasques de {@link FlowableEngineImpl}: completar per una transició concreta,
 * cancel·lar una tasca sense avançar el procés i obtenir la definició de procés d'una instància.
 */
public class HeliumTasquesFlowableTest {

	/**
	 * La tasca "revisar" té tres fluxos de sortida sense condició (amb nom, amb nom i sense nom) i un
	 * esdeveniment de temps de frontera.
	 */
	private static final String BPMN_TRANSICIONS =
		"<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
		"<definitions xmlns=\"http://www.omg.org/spec/BPMN/20100524/MODEL\" targetNamespace=\"http://www.caib.es/helium/test\">" +
		"  <process id=\"procesTransicions\" isExecutable=\"true\">" +
		"    <startEvent id=\"inici\"/>" +
		"    <sequenceFlow id=\"fInici\" sourceRef=\"inici\" targetRef=\"revisar\"/>" +
		"    <userTask id=\"revisar\" name=\"Revisar\"/>" +
		"    <boundaryEvent id=\"temps\" attachedToRef=\"revisar\" cancelActivity=\"true\">" +
		"      <timerEventDefinition><timeDuration>PT1H</timeDuration></timerEventDefinition>" +
		"    </boundaryEvent>" +
		"    <sequenceFlow id=\"fTemps\" sourceRef=\"temps\" targetRef=\"fiTemps\"/>" +
		"    <endEvent id=\"fiTemps\"/>" +
		"    <sequenceFlow id=\"fAcceptar\" name=\"Acceptar\" sourceRef=\"revisar\" targetRef=\"acceptada\"/>" +
		"    <sequenceFlow id=\"fRebutjar\" name=\"Rebutjar\" sourceRef=\"revisar\" targetRef=\"rebutjada\"/>" +
		"    <sequenceFlow id=\"fArxivar\" sourceRef=\"revisar\" targetRef=\"arxivar\"/>" +
		"    <userTask id=\"acceptada\" name=\"Acceptada\"/>" +
		"    <userTask id=\"rebutjada\" name=\"Rebutjada\"/>" +
		"    <userTask id=\"arxivar\" name=\"Arxivar\"/>" +
		"    <sequenceFlow id=\"fFiA\" sourceRef=\"acceptada\" targetRef=\"fiA\"/>" +
		"    <sequenceFlow id=\"fFiR\" sourceRef=\"rebutjada\" targetRef=\"fiR\"/>" +
		"    <sequenceFlow id=\"fFiX\" sourceRef=\"arxivar\" targetRef=\"fiX\"/>" +
		"    <endEvent id=\"fiA\"/><endEvent id=\"fiR\"/><endEvent id=\"fiX\"/>" +
		"  </process>" +
		"</definitions>";

	private static ProcessEngine processEngine;
	private static FlowableEngineImpl engine;

	@BeforeAll
	static void arrancarMotor() throws Exception {
		ProcessEngineConfigurationImpl configuration = (ProcessEngineConfigurationImpl) ProcessEngineConfiguration
			.createStandaloneInMemProcessEngineConfiguration()
			.setJdbcUrl("jdbc:h2:mem:helium-tasques;DB_CLOSE_DELAY=-1");
		FlowableConfig.afegirAgendaHelium(configuration);
		processEngine = configuration.buildProcessEngine();
		processEngine.getRepositoryService()
			.createDeployment()
			.addString("transicions.bpmn20.xml", BPMN_TRANSICIONS)
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

	@Test
	void lesTransicionsSonElsFluxosDeSortida() {
		Task revisar = iniciarIRevisar();
		List<TascaOutcomeDto> outcomes = engine.findTaskInstanceOutcomes(revisar.getId());
		Map<String, String> noms = outcomes.stream()
			.collect(Collectors.toMap(TascaOutcomeDto::getId, TascaOutcomeDto::getName));
		assertEquals(Map.of(
				"fAcceptar", "Acceptar",
				"fRebutjar", "Rebutjar",
				// Sense nom al flux s'agafa el de l'activitat destí
				"fArxivar", "Arxivar"),
			noms);
	}

	@Test
	void completarAmbTransicioNomesSegueixAquellFlux() {
		Task revisar = iniciarIRevisar();
		engine.endTaskInstance(revisar.getId(), "fRebutjar", Map.of("dada", "valor"));

		assertEquals(List.of("rebutjada"), tasquesActives(revisar.getProcessInstanceId()));
		// La tasca s'ha completat (no eliminada) i l'esdeveniment de frontera s'ha cancel·lat
		WTaskInstance historica = engine.getTaskById(revisar.getId());
		assertNotNull(historica.getEndTime());
		assertFalse(historica.isCancelled());
		assertNull(processEngine.getHistoryService().createHistoricTaskInstanceQuery()
			.taskId(revisar.getId()).singleResult().getDeleteReason());
		assertEquals(0, processEngine.getManagementService().createTimerJobQuery()
			.processInstanceId(revisar.getProcessInstanceId()).count());
	}

	@Test
	void completarAmbIdDeLActivitatDestiPerCompatibilitat() {
		Task revisar = iniciarIRevisar();
		engine.endTaskInstance(revisar.getId(), "acceptada", null);
		assertEquals(List.of("acceptada"), tasquesActives(revisar.getProcessInstanceId()));
	}

	@Test
	void completarSenseTransicioSegueixTotsElsFluxos() {
		Task revisar = iniciarIRevisar();
		engine.endTaskInstance(revisar.getId(), null, null);
		assertEquals(List.of("acceptada", "arxivar", "rebutjada"), tasquesActives(revisar.getProcessInstanceId()));
	}

	@Test
	void unaTransicioInexistentNoCompletaLaTasca() {
		Task revisar = iniciarIRevisar();
		assertThrows(
			FlowableIllegalArgumentException.class,
			() -> engine.endTaskInstance(revisar.getId(), "noExisteix", null));
		assertEquals(List.of("revisar"), tasquesActives(revisar.getProcessInstanceId()));
	}

	@Test
	void laTransicioNomesAfectaLaTascaCompletada() {
		// Dues tasques actives en paral·lel: completar-ne una per una transició no afecta l'altra
		Task revisar = iniciarIRevisar();
		engine.endTaskInstance(revisar.getId(), null, null);
		Task acceptada = tasca(revisar.getProcessInstanceId(), "acceptada");
		engine.endTaskInstance(acceptada.getId(), "fFiA", null);
		assertEquals(List.of("arxivar", "rebutjada"), tasquesActives(revisar.getProcessInstanceId()));
	}

	@Test
	void cancelarUnaTascaNoFaAvancarElProces() {
		Task revisar = iniciarIRevisar();
		WTaskInstance cancelada = engine.cancelTaskInstance(revisar.getId());

		assertTrue(cancelada.isCancelled());
		assertFalse(cancelada.isOpen());
		assertNotNull(cancelada.getEndTime());
		// No hi ha tasques noves i l'execució es queda a l'activitat de la tasca
		String processInstanceId = revisar.getProcessInstanceId();
		assertEquals(List.of(), tasquesActives(processInstanceId));
		ProcessInstance pi = processEngine.getRuntimeService().createProcessInstanceQuery()
			.processInstanceId(processInstanceId).singleResult();
		assertNotNull(pi);
		assertTrue(processEngine.getRuntimeService().getActiveActivityIds(processInstanceId).contains("revisar"));
		assertEquals(FlowableEngineImpl.MOTIU_TASCA_CANCELADA, processEngine.getHistoryService()
			.createHistoricTaskInstanceQuery().taskId(revisar.getId()).singleResult().getDeleteReason());
	}

	@Test
	void obteLaDefinicioDeProcesDUnaInstanciaActivaIFinalitzada() {
		Task revisar = iniciarIRevisar();
		String processInstanceId = revisar.getProcessInstanceId();
		WProcessDefinition activa = engine.findProcessDefinitionWithProcessInstanceId(processInstanceId);
		assertEquals("procesTransicions", activa.getKey());
		assertEquals(revisar.getProcessDefinitionId(), activa.getId());

		engine.endTaskInstance(revisar.getId(), "fAcceptar", null);
		engine.endTaskInstance(tasca(processInstanceId, "acceptada").getId(), null, null);
		assertNull(processEngine.getRuntimeService().createProcessInstanceQuery()
			.processInstanceId(processInstanceId).singleResult());
		WProcessDefinition finalitzada = engine.findProcessDefinitionWithProcessInstanceId(processInstanceId);
		assertEquals(revisar.getProcessDefinitionId(), finalitzada.getId());

		assertNull(engine.findProcessDefinitionWithProcessInstanceId("noExisteix"));
		assertNull(engine.findProcessDefinitionWithProcessInstanceId(null));
	}

	private Task iniciarIRevisar() {
		ProcessInstance pi = processEngine.getRuntimeService().startProcessInstanceByKey("procesTransicions");
		return tasca(pi.getId(), "revisar");
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
