package es.caib.helium.flowable;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.flowable.common.engine.impl.identity.Authentication;
import org.flowable.engine.ProcessEngine;
import org.flowable.engine.ProcessEngineConfiguration;
import org.flowable.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.flowable.engine.repository.Deployment;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.identitylink.api.IdentityLink;
import org.flowable.job.api.Job;
import org.flowable.task.api.Task;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import es.caib.helium.bpmn.api.HeliumApi;

/**
 * Comprova que les propietats d'execució declarades amb atributs {@code helium:*} i l'assignació
 * estàndard ({@code humanPerformer}/{@code potentialOwner}) tenen el mateix efecte a Flowable que
 * els atributs {@code flowable:*}, i que el XML desat continua sent genèric.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
public class HeliumAtributsFlowableTest {

	private static final String BPMN = "bpmn/atributs-helium.bpmn20.xml";

	private static ProcessEngine processEngine;
	private static Deployment deployment;

	@BeforeAll
	static void arrancarMotor() throws IOException {
		HeliumServiceTaskXMLConverter.registrar();
		HeliumHandlerJavaDelegate delegate = new HeliumHandlerJavaDelegate(
			MotorHeliumProva.crear(mock(HeliumApi.class)));
		ProcessEngineConfigurationImpl configuration = (ProcessEngineConfigurationImpl) ProcessEngineConfiguration
			.createStandaloneInMemProcessEngineConfiguration()
			.setJdbcUrl("jdbc:h2:mem:helium-atributs;DB_CLOSE_DELAY=-1");
		Map<Object, Object> beans = new HashMap<>();
		beans.put(HeliumHandlerJavaDelegate.BEAN_NAME, delegate);
		configuration.setBeans(beans);
		FlowableConfig.afegirHeliumPreDeployer(configuration);
		processEngine = configuration.buildProcessEngine();
		deployment = processEngine.getRepositoryService()
			.createDeployment()
			.addBytes("atributs-helium.bpmn20.xml", bytes(BPMN))
			.deploy();
	}

	@AfterAll
	static void aturarMotor() {
		if (processEngine != null) {
			processEngine.close();
		}
	}

	@BeforeEach
	void setUp() {
		HandlerProva.EXECUCIONS.clear();
	}

	@Test
	void elXmlDesatEsElGeneric() throws IOException {
		try (InputStream is = processEngine.getRepositoryService().getResourceAsStream(deployment.getId(), "atributs-helium.bpmn20.xml")) {
			byte[] desat = is.readAllBytes();
			assertArrayEquals(bytes(BPMN), desat);
			assertFalse(new String(desat, StandardCharsets.UTF_8).contains("flowable.org"));
		}
	}

	@Test
	void propietatsDeProcesIniciITascaUsuari() {
		assertEquals(1, processEngine.getRepositoryService().createProcessDefinitionQuery()
			.processDefinitionKey("procesTasques").startableByUser("joan").count());
		assertEquals("formInici", processEngine.getFormService().getStartFormKey(
			processEngine.getRepositoryService().createProcessDefinitionQuery().processDefinitionKey("procesTasques").latestVersion().singleResult().getId()));

		Date dataLimit = new Date(System.currentTimeMillis() + 86_400_000L);
		Map<String, Object> variables = new HashMap<>();
		variables.put("responsable", "pere");
		variables.put("dataLimit", dataLimit);
		ProcessInstance pi;
		try {
			Authentication.setAuthenticatedUserId("joan");
			pi = processEngine.getRuntimeService().startProcessInstanceByKey("procesTasques", variables);
		} finally {
			Authentication.setAuthenticatedUserId(null);
		}
		assertEquals("joan", processEngine.getRuntimeService().getVariable(pi.getId(), "iniciador"));

		Task tasca = processEngine.getTaskService().createTaskQuery().processInstanceId(pi.getId()).singleResult();
		assertEquals("pere", tasca.getAssignee());
		assertEquals("formTasca", tasca.getFormKey());
		assertEquals(70, tasca.getPriority());
		assertEquals("categoriaProva", tasca.getCategory());
		assertEquals(dataLimit, tasca.getDueDate());
		Set<String> candidats = processEngine.getTaskService().getIdentityLinksForTask(tasca.getId()).stream()
			.filter(l -> "candidate".equals(l.getType()))
			.map(l -> l.getUserId() != null ? "user:" + l.getUserId() : "group:" + l.getGroupId())
			.collect(Collectors.toSet());
		assertEquals(Set.of("user:anna", "group:gestors"), candidats);

		// La service task és asíncrona: en completar la tasca es crea un job i el handler encara no s'ha executat
		processEngine.getTaskService().complete(tasca.getId());
		assertTrue(HandlerProva.EXECUCIONS.isEmpty());
		Job job = processEngine.getManagementService().createJobQuery().processInstanceId(pi.getId()).singleResult();
		assertNotNull(job);
		assertFalse(job.isExclusive());
		processEngine.getManagementService().executeJob(job.getId());
		assertEquals(1, HandlerProva.EXECUCIONS.size());
		assertEquals("asincron", HandlerProva.EXECUCIONS.get(0).text);
		assertNull(processEngine.getRuntimeService().createProcessInstanceQuery().processInstanceId(pi.getId()).singleResult());
	}

	@Test
	void multiInstanciaAmbColleccioIVariableDElement() {
		ProcessInstance pi = processEngine.getRuntimeService().startProcessInstanceByKey(
			"procesMultiInstancia",
			Map.of("llista", List.of("a", "b", "c")));
		List<Task> tasques = processEngine.getTaskService().createTaskQuery().processInstanceId(pi.getId()).list();
		assertEquals(3, tasques.size());
		Set<Object> elements = tasques.stream()
			.map(t -> processEngine.getTaskService().getVariable(t.getId(), "element"))
			.collect(Collectors.toSet());
		assertEquals(Set.of("a", "b", "c"), elements);
	}

	@Test
	void callActivityHeretaVariablesISubprocesAsincron() {
		ProcessInstance pare = processEngine.getRuntimeService().startProcessInstanceByKey(
			"procesPare",
			Map.of("variablePare", "valor"));
		ProcessInstance fill = processEngine.getRuntimeService().createProcessInstanceQuery()
			.superProcessInstanceId(pare.getId()).singleResult();
		assertNotNull(fill);
		assertEquals("valor", processEngine.getRuntimeService().getVariable(fill.getId(), "variablePare"));

		Task tascaFill = processEngine.getTaskService().createTaskQuery().processInstanceId(fill.getId()).singleResult();
		processEngine.getTaskService().complete(tascaFill.getId());

		// El subprocés és asíncron: hi ha un job i encara no hi ha la tasca del subprocés
		assertEquals(0, processEngine.getTaskService().createTaskQuery().processInstanceId(pare.getId()).count());
		Job job = processEngine.getManagementService().createJobQuery().processInstanceId(pare.getId()).singleResult();
		assertNotNull(job);
		processEngine.getManagementService().executeJob(job.getId());
		Task tascaSub = processEngine.getTaskService().createTaskQuery().processInstanceId(pare.getId()).singleResult();
		assertEquals("tascaSub", tascaSub.getTaskDefinitionKey());
	}

	@Test
	void elModelCarregatDeNouDesDeLaBaseDeDadesTambeTeElsAtributs() {
		// Força que Flowable torni a llegir el BPMN de la base de dades (i torni a passar pels deployers)
		((ProcessEngineConfigurationImpl) processEngine.getProcessEngineConfiguration()).getProcessDefinitionCache().clear();
		ProcessInstance pi = processEngine.getRuntimeService().startProcessInstanceByKey(
			"procesTasques",
			Map.of("responsable", "marta", "dataLimit", new Date()));
		Task tasca = processEngine.getTaskService().createTaskQuery().processInstanceId(pi.getId()).singleResult();
		assertEquals("marta", tasca.getAssignee());
		assertEquals("formTasca", tasca.getFormKey());
		List<IdentityLink> links = processEngine.getTaskService().getIdentityLinksForTask(tasca.getId());
		assertEquals(2L, links.stream().filter(l -> "candidate".equals(l.getType())).count());
	}

	private static byte[] bytes(String recurs) throws IOException {
		try (InputStream is = HeliumAtributsFlowableTest.class.getClassLoader().getResourceAsStream(recurs)) {
			assertNotNull(is, recurs);
			return is.readAllBytes();
		}
	}

}
