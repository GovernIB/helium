package es.caib.helium.logic.flowable;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.flowable.engine.ProcessEngine;
import org.flowable.engine.ProcessEngineConfiguration;
import org.flowable.engine.runtime.ProcessInstance;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import es.caib.helium.bpmn.engine.WToken;

/**
 * Comprova la conversió de les execucions de Flowable a {@link WToken} fora d'una comanda de Flowable
 * (sense CommandContext), com es fa des del servei de tokens de l'expedient.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
public class FlowableEngineImplTokensTest {

	private static final String BPMN =
		"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
		"<definitions xmlns=\"http://www.omg.org/spec/BPMN/20100524/MODEL\" targetNamespace=\"http://www.caib.es/helium\">\n" +
		"  <process id=\"procesTokens\" name=\"Procés de tokens\" isExecutable=\"true\">\n" +
		"    <startEvent id=\"inici\"/>\n" +
		"    <sequenceFlow id=\"f1\" sourceRef=\"inici\" targetRef=\"bifurcacio\"/>\n" +
		"    <parallelGateway id=\"bifurcacio\"/>\n" +
		"    <sequenceFlow id=\"f2\" sourceRef=\"bifurcacio\" targetRef=\"esperaA\"/>\n" +
		"    <sequenceFlow id=\"f3\" sourceRef=\"bifurcacio\" targetRef=\"esperaB\"/>\n" +
		"    <receiveTask id=\"esperaA\" name=\"Espera A\"/>\n" +
		"    <receiveTask id=\"esperaB\"/>\n" +
		"    <sequenceFlow id=\"sortidaA\" sourceRef=\"esperaA\" targetRef=\"unio\"/>\n" +
		"    <sequenceFlow id=\"sortidaB\" sourceRef=\"esperaB\" targetRef=\"unio\"/>\n" +
		"    <parallelGateway id=\"unio\"/>\n" +
		"    <sequenceFlow id=\"f4\" sourceRef=\"unio\" targetRef=\"fi\"/>\n" +
		"    <endEvent id=\"fi\"/>\n" +
		"  </process>\n" +
		"</definitions>\n";

	private static ProcessEngine processEngine;
	private static FlowableEngineImpl engine;

	@BeforeAll
	static void arrancarMotor() throws ReflectiveOperationException {
		processEngine = ProcessEngineConfiguration
			.createStandaloneInMemProcessEngineConfiguration()
			.setJdbcUrl("jdbc:h2:mem:helium-tokens;DB_CLOSE_DELAY=-1")
			.buildProcessEngine();
		processEngine.getRepositoryService()
			.createDeployment()
			.addString("tokens.bpmn20.xml", BPMN)
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
	void getAllTokensConverteixLesExecucions() {
		ProcessInstance pi = processEngine.getRuntimeService().startProcessInstanceByKey("procesTokens", "EXP-1");

		Map<String, WToken> tokens = engine.getAllTokens(pi.getId());

		// L'execució de la instància de procés més una per cada branca de la bifurcació
		assertEquals(3, tokens.size());
		WToken arrel = tokens.get(pi.getId());
		assertNotNull(arrel);
		assertTrue(arrel.isRoot());
		assertNull(arrel.getParent());
		assertNull(arrel.getNodeName());
		assertEquals("EXP-1", arrel.getProcessInstanceKey());

		List<WToken> fills = tokens.values().stream().filter(t -> !t.isRoot()).collect(Collectors.toList());
		assertEquals(2, fills.size());
		for (WToken fill: fills) {
			assertSame(arrel, fill.getParent());
			assertEquals(pi.getId(), fill.getProcessInstanceId());
			assertEquals("EXP-1", fill.getProcessInstanceKey());
			assertEquals("procesTokens", fill.getProcessDefinitionKey());
			assertEquals("Procés de tokens", fill.getProcessDefinitionName());
			assertEquals(1, fill.getProcessDefinitionversion());
			assertNotNull(fill.getStart());
			assertNotNull(fill.getNodeEnter());
			assertNull(fill.getEnd());
			assertTrue(fill.isReceiveTask());
			assertFalse(fill.isSuspended());
		}
		// Si el node no té nom, es mostra el seu id
		Map<String, WToken> perNode = fills.stream().collect(Collectors.toMap(WToken::getNodeName, t -> t));
		assertEquals(List.of("sortidaA"), perNode.get("Espera A").getSortides());
		assertEquals(List.of("sortidaB"), perNode.get("esperaB").getSortides());

		// getTokenById fa la mateixa conversió
		WToken fill = perNode.get("Espera A");
		WToken perId = engine.getTokenById(fill.getId());
		assertEquals("Espera A", perId.getNodeName());
		assertEquals(fill.getNodeEnter(), perId.getNodeEnter());
		assertFalse(perId.isRoot());

		processEngine.getRuntimeService().suspendProcessInstanceById(pi.getId());
		assertTrue(engine.getAllTokens(pi.getId()).values().stream().allMatch(WToken::isSuspended));
	}
}
