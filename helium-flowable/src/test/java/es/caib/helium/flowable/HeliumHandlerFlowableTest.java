package es.caib.helium.flowable;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.flowable.bpmn.converter.BpmnXMLConverter;
import org.flowable.bpmn.model.BpmnModel;
import org.flowable.bpmn.model.ImplementationType;
import org.flowable.bpmn.model.ServiceTask;
import org.flowable.common.engine.impl.util.io.BytesStreamSource;
import org.flowable.engine.ProcessEngine;
import org.flowable.engine.ProcessEngineConfiguration;
import org.flowable.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.flowable.engine.runtime.ProcessInstance;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import es.caib.helium.bpmn.api.HeliumApi;
import es.caib.helium.logic.intf.bpmn.MotorHeliumService;
import es.caib.helium.bpmn.handler.HandlerDefinicio;

/**
 * Comprova que un BPMN 2.0 genèric (sense atributs de Flowable) amb handlers de Helium es
 * desplega i s'executa amb Flowable.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
public class HeliumHandlerFlowableTest {

	private static ProcessEngine processEngine;
	private static MotorHeliumService motorHeliumService;
	private static HeliumApi heliumApi;

	@BeforeAll
	static void arrancarMotor() {
		HeliumServiceTaskXMLConverter.registrar();
		heliumApi = mock(HeliumApi.class);
		motorHeliumService = MotorHeliumProva.crear(heliumApi);
		HeliumHandlerJavaDelegate delegate = new HeliumHandlerJavaDelegate(motorHeliumService);
		ProcessEngineConfigurationImpl configuration = (ProcessEngineConfigurationImpl) ProcessEngineConfiguration
			.createStandaloneInMemProcessEngineConfiguration()
			.setJdbcUrl("jdbc:h2:mem:helium-handlers;DB_CLOSE_DELAY=-1");
		Map<Object, Object> beans = new HashMap<>();
		beans.put(HeliumHandlerJavaDelegate.BEAN_NAME, delegate);
		beans.put("handlerProvaBean", new HandlerProva());
		configuration.setBeans(beans);
		es.caib.helium.flowable.FlowableConfig.afegirHeliumPreDeployer(configuration);
		// Les dades de l'expedient (ExpedientDades a Helium) les simula el mapa DADES
		es.caib.helium.flowable.FlowableConfig.afegirResolutorDadesHelium(configuration, pid -> DADES);
		processEngine = configuration.buildProcessEngine();
	}

	@AfterAll
	static void aturarMotor() {
		if (processEngine != null) {
			processEngine.close();
		}
	}

	@BeforeEach
	void setUp() {
		reset(motorHeliumService, heliumApi);
		MotorHeliumProva.configurar(motorHeliumService, heliumApi);
		when(heliumApi.getVariableDefaultValue(any(), any())).thenAnswer(i -> i.getArgument(1));
		HandlerProva.EXECUCIONS.clear();
		DADES.clear();
		org.mockito.Mockito.doAnswer(i -> DADES.put(i.getArgument(0), i.getArgument(1)))
			.when(heliumApi).setVariable(anyString(), any());
	}

	/** Dades de l'expedient que veuen les expressions i on escriu HeliumApi.setVariable. */
	private static final Map<String, Object> DADES = new java.util.concurrent.ConcurrentHashMap<>();

	@Test
	void elConversorTradueixElHandlerHeliumAUnaTascaDeFlowable() throws IOException {
		BpmnModel model = llegir("bpmn/handler-generic.bpmn20.xml");
		ServiceTask tasca = (ServiceTask) model.getMainProcess().getFlowElement("tascaHandlerPropi");
		assertEquals(ImplementationType.IMPLEMENTATION_TYPE_DELEGATEEXPRESSION, tasca.getImplementationType());
		assertEquals(HeliumHandlerJavaDelegate.DELEGATE_EXPRESSION, tasca.getImplementation());
		// La tasca del handler no espera: l'espera de les respostes asíncrones és a una receive task
		assertFalse(tasca.isTriggerable());
		HandlerDefinicio definicio = FlowableHeliumBpmn.llegirHandler(tasca);
		assertNotNull(definicio);
		assertEquals("es.caib.helium.flowable.HandlerProva", definicio.getClasse());
		assertEquals(3, definicio.getParametres().size());
		assertEquals("text", definicio.getParametres().get(0).getNom());
		assertEquals("Valor literal", definicio.getParametres().get(0).getValor());
		assertEquals("${variableEntrada}", definicio.getParametres().get(1).getExpressio());
	}

	@Test
	void elConversorNoModificaLesTasquesAmbAtributsDeFlowable() throws IOException {
		BpmnModel model = llegir("bpmn/handler-flowable-class.bpmn20.xml");
		ServiceTask tasca = (ServiceTask) model.getMainProcess().getFlowElement("tascaClasse");
		assertEquals(ImplementationType.IMPLEMENTATION_TYPE_CLASS, tasca.getImplementationType());
		assertEquals("es.caib.prova.Delegat", tasca.getImplementation());
		assertEquals(1, tasca.getFieldExtensions().size());
		assertNull(FlowableHeliumBpmn.llegirHandler(tasca));
	}

	@Test
	void desplegaIExecutaHandlersDesDeBpmnGeneric() throws IOException {
		desplegar("bpmn/handler-generic.bpmn20.xml");
		ProcessInstance pi = processEngine.getRuntimeService().startProcessInstanceByKey(
			"procesHandlerGeneric",
			Map.of("variableEntrada", "valor de la variable"));

		// Handler propi: paràmetre literal i paràmetres avaluats com a expressions
		assertEquals(1, HandlerProva.EXECUCIONS.size());
		HandlerProva handler = HandlerProva.EXECUCIONS.get(0);
		assertEquals("Valor literal", handler.text);
		assertEquals("valor de la variable", handler.valorVariable);
		assertEquals(Integer.valueOf(5), handler.numero);
		assertSame(heliumApi, handler.heliumApi);

		// Handler predefinit referenciat per la seva interfície de helium-disseny
		verify(heliumApi).expedientComentariModificar("Comentari nou");

		verify(motorHeliumService, org.mockito.Mockito.times(2)).getHeliumApi(pi.getId());
		assertTrue(pi.isEnded());
		// Tots els handlers són a l'aplicació: no s'han consultat els recursos
		// Les classes de l'aplicació no es carreguen dels recursos de Helium
		verify(motorHeliumService, org.mockito.Mockito.never()).getRecursDefinicioProces(any(), any());
	}

	@Test
	void cadaExecucioFaServirUnaInstanciaNovaDelHandler() throws IOException {
		desplegar("bpmn/handler-generic.bpmn20.xml");
		processEngine.getRuntimeService().startProcessInstanceByKey("procesHandlerGeneric", Map.of("variableEntrada", "u"));
		processEngine.getRuntimeService().startProcessInstanceByKey("procesHandlerGeneric", Map.of("variableEntrada", "dos"));
		assertEquals(2, HandlerProva.EXECUCIONS.size());
		assertTrue(HandlerProva.EXECUCIONS.get(0) != HandlerProva.EXECUCIONS.get(1));
		assertEquals("u", HandlerProva.EXECUCIONS.get(0).valorVariable);
		assertEquals("dos", HandlerProva.EXECUCIONS.get(1).valorVariable);
	}

	@Test
	void lesDefinicionsAntiguesAmbFlowableClassDeHandlersHeliumContinuenFuncionant() throws IOException {
		BpmnModel model = llegir("bpmn/handler-flowable-class-predefinit.bpmn20.xml");
		ServiceTask tasca = (ServiceTask) model.getMainProcess().getFlowElement("tascaComentari");
		assertEquals(HeliumHandlerJavaDelegate.DELEGATE_EXPRESSION, tasca.getImplementation());
		assertTrue(tasca.getFieldExtensions().isEmpty());
		HandlerDefinicio definicio = FlowableHeliumBpmn.llegirHandler(tasca);
		assertEquals("es.caib.helium.flowable.ExpedientComentariModificarHandlerProva", definicio.getClasse());
		assertEquals("${comentariNou}", definicio.getParametres().get(0).getExpressio());

		desplegar("bpmn/handler-flowable-class-predefinit.bpmn20.xml");
		ProcessInstance pi = processEngine.getRuntimeService().startProcessInstanceByKey(
			"procesFlowableClassPredefinit",
			Map.of("comentariNou", "Comentari des de variable"));
		verify(heliumApi).expedientComentariModificar("Comentari des de variable");
		assertTrue(pi.isEnded());
	}

	@Test
	void unHandlerSincronALIniciRepLIdPredefinitDeLaInstancia() throws Exception {
		desplegar("bpmn/handler-sincron-inici.bpmn20.xml");
		String processDefinitionId = processEngine.getRepositoryService().createProcessDefinitionQuery()
			.processDefinitionKey("procesHandlerSincron").latestVersion().singleResult().getId();
		FlowableEngineImpl flowableEngine = new FlowableEngineImpl();
		java.lang.reflect.Field camp = FlowableEngineImpl.class.getDeclaredField("processEngine");
		camp.setAccessible(true);
		camp.set(flowableEngine, processEngine);
		// L'expedient ja té assignat aquest identificador abans d'iniciar el procés (ExpedientHelper.iniciar)
		String idExpedient = java.util.UUID.randomUUID().toString();

		es.caib.helium.bpmn.engine.WProcessInstance pi = flowableEngine.startProcessInstanceById(
			null,
			processDefinitionId,
			idExpedient);

		assertEquals(idExpedient, pi.getId());
		// El handler s'ha executat durant l'inici i ha cercat l'expedient amb el mateix identificador
		verify(motorHeliumService).getHeliumApi(idExpedient);
		verify(heliumApi).expedientComentariModificar("Comentari a l'inici");
		assertEquals(1, processEngine.getTaskService().createTaskQuery().processInstanceId(idExpedient).count());
	}

	@Test
	void laInstanciaArrelEsTrobaDesDeSubprocessosDuranteLIniciIDespres() throws Exception {
		desplegar("bpmn/subprocessos-arrel.bpmn20.xml");
		String processDefinitionId = processEngine.getRepositoryService().createProcessDefinitionQuery()
			.processDefinitionKey("procesArrelPare").latestVersion().singleResult().getId();
		FlowableEngineImpl flowableEngine = flowableEngine();
		// El handler del fill s'executa mentre s'inicia el pare: res no s'ha desat encara a la base de dades
		List<String> arrelsVistesPelHandler = new java.util.ArrayList<>();
		List<String> instanciesDelHandler = new java.util.ArrayList<>();
		when(motorHeliumService.getHeliumApi(anyString())).thenAnswer(i -> {
			String pid = i.getArgument(0);
			instanciesDelHandler.add(pid);
			arrelsVistesPelHandler.add(flowableEngine.findRootProcessInstance(pid).getId());
			return heliumApi;
		});
		String idExpedient = java.util.UUID.randomUUID().toString();

		flowableEngine.startProcessInstanceById(null, processDefinitionId, idExpedient);

		assertEquals(1, HandlerProva.EXECUCIONS.size());
		assertEquals(1, instanciesDelHandler.size());
		assertTrue(!idExpedient.equals(instanciesDelHandler.get(0)), "El handler s'executa dins el subprocés");
		assertEquals(List.of(idExpedient), arrelsVistesPelHandler);

		// Un cop desat: des del nét (dos nivells per sota) i des de la mateixa arrel
		org.flowable.engine.runtime.ProcessInstance fill = processEngine.getRuntimeService().createProcessInstanceQuery()
			.superProcessInstanceId(idExpedient).singleResult();
		org.flowable.engine.runtime.ProcessInstance net = processEngine.getRuntimeService().createProcessInstanceQuery()
			.superProcessInstanceId(fill.getId()).singleResult();
		assertEquals(idExpedient, flowableEngine.findRootProcessInstance(net.getId()).getId());
		assertEquals(idExpedient, flowableEngine.findRootProcessInstance(fill.getId()).getId());
		assertEquals(idExpedient, flowableEngine.findRootProcessInstance(idExpedient).getId());

		// Quan tot ha finalitzat es troba per l'historial
		org.flowable.task.api.Task tasca = processEngine.getTaskService().createTaskQuery().processInstanceId(net.getId()).singleResult();
		processEngine.getTaskService().complete(tasca.getId());
		assertEquals(0, processEngine.getRuntimeService().createProcessInstanceQuery().processInstanceId(idExpedient).count());
		assertEquals(idExpedient, flowableEngine.findRootProcessInstance(net.getId()).getId());
		assertNull(flowableEngine.findRootProcessInstance("no-existeix"));
	}

	private FlowableEngineImpl flowableEngine() throws ReflectiveOperationException {
		FlowableEngineImpl flowableEngine = new FlowableEngineImpl();
		java.lang.reflect.Field camp = FlowableEngineImpl.class.getDeclaredField("processEngine");
		camp.setAccessible(true);
		camp.set(flowableEngine, processEngine);
		return flowableEngine;
	}

	@Test
	void expressioDeHeliumIHandlerObtingutDUnaExpressio() throws IOException {
		desplegar("bpmn/expressions-helium.bpmn20.xml");
		// "base" és una dada de l'expedient, no una variable del motor
		DADES.put("base", 21);
		ProcessInstance pi = processEngine.getRuntimeService().startProcessInstanceByKey("procesExpressions");

		// helium:expression + helium:resultVariableName: el resultat va a les dades de l'expedient
		verify(heliumApi).setVariable("resultat", 42L);
		assertEquals(42L, DADES.get("resultat"));
		assertNull(processEngine.getRuntimeService().getVariable(pi.getId(), "resultat"));
		// helium:handler expressio="${handlerProvaBean}": el handler és el bean, amb els paràmetres injectats
		assertEquals(1, HandlerProva.EXECUCIONS.size());
		HandlerProva handler = HandlerProva.EXECUCIONS.get(0);
		assertEquals("per expressio", handler.text);
		assertEquals(Integer.valueOf(42), handler.numero);
		assertSame(heliumApi, handler.heliumApi);
	}

	@Test
	void lesCondicionsDelsFluxosVeuenLesDadesDeLExpedient() throws IOException {
		desplegar("bpmn/dades-helium-expressions.bpmn20.xml");

		DADES.put("estat", "aprovat");
		ProcessInstance aprovat = processEngine.getRuntimeService().startProcessInstanceByKey("procesDadesHelium");
		assertEquals("tascaAprovada", processEngine.getTaskService().createTaskQuery()
			.processInstanceId(aprovat.getId()).singleResult().getTaskDefinitionKey());

		DADES.put("estat", "rebutjat");
		ProcessInstance rebutjat = processEngine.getRuntimeService().startProcessInstanceByKey("procesDadesHelium");
		assertEquals("tascaRebutjada", processEngine.getTaskService().createTaskQuery()
			.processInstanceId(rebutjat.getId()).singleResult().getTaskDefinitionKey());

		// Ordre fixat per Flowable: una variable del motor amb el mateix nom té prioritat sobre la dada
		ProcessInstance variableMotor = processEngine.getRuntimeService().startProcessInstanceByKey(
			"procesDadesHelium",
			Map.of("estat", "aprovat"));
		assertEquals("tascaAprovada", processEngine.getTaskService().createTaskQuery()
			.processInstanceId(variableMotor.getId()).singleResult().getTaskDefinitionKey());
	}

	@Test
	void elsAnticsParametresVarEsTradueixenAExpressionsDeDades() throws IOException {
		desplegar("bpmn/handler-parametres-var.bpmn20.xml");

		DADES.put("motiuDada", "Motiu de la dada");
		DADES.put("comentariDada", "Comentari de la dada");
		processEngine.getRuntimeService().startProcessInstanceByKey("procesParametresVar");
		verify(heliumApi).expedientAturar("Motiu de la dada");
		verify(heliumApi).expedientComentariModificar("Comentari de la dada");

		// Si la dada no té valor s'utilitza el literal del paràmetre (com feia getVariableDefaultValue)
		reset(heliumApi);
		DADES.clear();
		processEngine.getRuntimeService().startProcessInstanceByKey("procesParametresVar");
		verify(heliumApi).expedientAturar("Motiu fix");
		verify(heliumApi).expedientComentariModificar(null);
	}

	@Test
	void unaExpressioDeHandlerQueNoRetornaUnHandlerFaFallarLExecucio() throws IOException {
		desplegar("bpmn/expressions-helium.bpmn20.xml");
		RuntimeException ex = assertThrows(
			RuntimeException.class,
			() -> processEngine.getRuntimeService().startProcessInstanceByKey("procesExpressioNoHandler"));
		assertTrue(missatges(ex).contains("no retorna un " + es.caib.helium.bpmn.handler.HeliumBpmnHandler.class.getName()), missatges(ex));
	}

	@Test
	void unParametreQueElHandlerNoTeFaFallarLExecucio() throws IOException {
		desplegar("bpmn/handler-parametre-inexistent.bpmn20.xml");
		RuntimeException ex = assertThrows(
			RuntimeException.class,
			() -> processEngine.getRuntimeService().startProcessInstanceByKey("procesParametreInexistent"));
		assertTrue(missatges(ex).contains("parametreQueNoExisteix"), missatges(ex));
		assertTrue(HandlerProva.EXECUCIONS.isEmpty());
	}

	private void desplegar(String recurs) throws IOException {
		processEngine.getRepositoryService()
			.createDeployment()
			.addBytes(recurs.substring(recurs.lastIndexOf('/') + 1), bytes(recurs))
			.deploy();
	}

	private BpmnModel llegir(String recurs) throws IOException {
		return new BpmnXMLConverter().convertToBpmnModel(new BytesStreamSource(bytes(recurs)), true, false);
	}

	private byte[] bytes(String recurs) throws IOException {
		try (InputStream is = getClass().getClassLoader().getResourceAsStream(recurs)) {
			assertNotNull(is, recurs);
			return is.readAllBytes();
		}
	}

	private static String missatges(Throwable t) {
		StringBuilder sb = new StringBuilder();
		for (Throwable c = t; c != null; c = c.getCause()) {
			sb.append(c.getMessage()).append(" | ");
		}
		return sb.toString();
	}

}
