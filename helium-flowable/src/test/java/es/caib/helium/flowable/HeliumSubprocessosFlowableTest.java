package es.caib.helium.flowable;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.flowable.engine.ProcessEngine;
import org.flowable.engine.ProcessEngineConfiguration;
import org.flowable.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import es.caib.helium.bpmn.api.HeliumApi;
import es.caib.helium.logic.intf.bpmn.MotorHeliumService;

/**
 * Dades de l'expedient entre un procés i un subprocés ({@code helium:entrada} i {@code helium:sortida} a la
 * {@code callActivity}): les entrades arriben al subprocés abans que s'hi executi res, i les sortides tornen al procés
 * abans que continuï. Les dades de cada instància de procés es desen per separat, com a {@code ExpedientDades}.
 */
public class HeliumSubprocessosFlowableTest {

	private static final String BPMN_PARE =
		"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
		"<bpmn:definitions xmlns:bpmn=\"http://www.omg.org/spec/BPMN/20100524/MODEL\"\n" +
		"                  xmlns:helium=\"http://www.caib.es/helium/bpmn\" targetNamespace=\"http://www.caib.es/helium/test\">\n" +
		"  <bpmn:process id=\"procesPare\" isExecutable=\"true\">\n" +
		"    <bpmn:startEvent id=\"inici\"/>\n" +
		"    <bpmn:sequenceFlow id=\"f1\" sourceRef=\"inici\" targetRef=\"crida\"/>\n" +
		"    <bpmn:callActivity id=\"crida\" calledElement=\"procesFill\">\n" +
		"      <bpmn:extensionElements>\n" +
		"        <helium:entrada origen=\"nom\" desti=\"nomFill\"/>\n" +
		"        <helium:entrada origen=\"import\"/>\n" +
		"        <helium:entrada expressio=\"${import * 2}\" desti=\"doble\"/>\n" +
		"        <helium:entrada origen=\"inexistent\" desti=\"noCopiada\"/>\n" +
		"        <helium:entrada expressio=\"${inexistent}\" desti=\"noCopiadaExpressio\"/>\n" +
		"        <helium:entrada origen=\"registreMultiple\"/>\n" +
		"        <helium:entrada expressio=\"${registreMultiple[1][0]}\" desti=\"segonaFila\"/>\n" +
		"        <helium:sortida origen=\"resultat\" desti=\"resultatFill\"/>\n" +
		"        <helium:sortida expressio=\"${doble + 1}\" desti=\"mesUn\"/>\n" +
		"        <helium:sortida origen=\"inexistent\" desti=\"noRetornada\"/>\n" +
		"      </bpmn:extensionElements>\n" +
		"    </bpmn:callActivity>\n" +
		"    <bpmn:sequenceFlow id=\"f2\" sourceRef=\"crida\" targetRef=\"despres\"/>\n" +
		"    <bpmn:serviceTask id=\"despres\" helium:expression=\"${resultatFill}\" helium:resultVariableName=\"vistDespres\"/>\n" +
		"    <bpmn:sequenceFlow id=\"f3\" sourceRef=\"despres\" targetRef=\"espera\"/>\n" +
		"    <bpmn:userTask id=\"espera\"/>\n" +
		"    <bpmn:sequenceFlow id=\"f4\" sourceRef=\"espera\" targetRef=\"fi\"/>\n" +
		"    <bpmn:endEvent id=\"fi\"/>\n" +
		"  </bpmn:process>\n" +
		"</bpmn:definitions>";

	/** El subprocés fa servir les entrades en una tasca que s'executa en començar, i espera en una tasca d'usuari. */
	private static final String BPMN_FILL =
		"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
		"<bpmn:definitions xmlns:bpmn=\"http://www.omg.org/spec/BPMN/20100524/MODEL\"\n" +
		"                  xmlns:helium=\"http://www.caib.es/helium/bpmn\" targetNamespace=\"http://www.caib.es/helium/test\">\n" +
		"  <bpmn:process id=\"procesFill\" isExecutable=\"true\">\n" +
		"    <bpmn:startEvent id=\"inici\"/>\n" +
		"    <bpmn:sequenceFlow id=\"f1\" sourceRef=\"inici\" targetRef=\"calcular\"/>\n" +
		"    <bpmn:serviceTask id=\"calcular\" helium:expression=\"${nomFill.toUpperCase()}\" helium:resultVariableName=\"resultat\"/>\n" +
		"    <bpmn:sequenceFlow id=\"f2\" sourceRef=\"calcular\" targetRef=\"tascaFill\"/>\n" +
		"    <bpmn:userTask id=\"tascaFill\"/>\n" +
		"    <bpmn:sequenceFlow id=\"f3\" sourceRef=\"tascaFill\" targetRef=\"fi\"/>\n" +
		"    <bpmn:endEvent id=\"fi\"/>\n" +
		"  </bpmn:process>\n" +
		"</bpmn:definitions>";

	/** Dades de l'expedient per instància de procés. */
	private static final Map<String, Map<String, Object>> DADES = new ConcurrentHashMap<>();

	private static ProcessEngine processEngine;

	@BeforeAll
	static void arrancarMotor() {
		HeliumServiceTaskXMLConverter.registrar();
		MotorHeliumService motor = mock(MotorHeliumService.class);
		when(motor.getDadesProces(anyString())).thenAnswer(i -> dades(i.getArgument(0)));
		when(motor.getHeliumApi(anyString())).thenAnswer(i -> heliumApi(i.getArgument(0)));
		ProcessEngineConfigurationImpl configuration = (ProcessEngineConfigurationImpl) ProcessEngineConfiguration
			.createStandaloneInMemProcessEngineConfiguration()
			.setJdbcUrl("jdbc:h2:mem:helium-subprocessos;DB_CLOSE_DELAY=-1");
		Map<Object, Object> beans = new HashMap<>();
		beans.put(HeliumHandlerJavaDelegate.BEAN_NAME, new HeliumHandlerJavaDelegate(motor));
		configuration.setBeans(beans);
		FlowableConfig.afegirHeliumPreDeployer(configuration);
		FlowableConfig.afegirAgendaHelium(configuration);
		FlowableConfig.afegirResolutorDadesHelium(configuration, HeliumSubprocessosFlowableTest::dades);
		FlowableConfig.afegirDadesSubprocessos(configuration, () -> motor);
		processEngine = configuration.buildProcessEngine();
		processEngine.getRepositoryService().createDeployment()
			.addString("pare.bpmn20.xml", BPMN_PARE)
			.addString("fill.bpmn20.xml", BPMN_FILL)
			.deploy();
	}

	@AfterAll
	static void aturarMotor() {
		if (processEngine != null) {
			processEngine.close();
		}
	}

	@Test
	void lesEntradesArribenAlSubprocesILesSortidesTornenAlProces() {
		// Les dades del procés pare s'han de poder llegir abans d'iniciar-lo: es desen amb l'id predefinit
		String pidPare = "pare-" + System.nanoTime();
		Object[] registreMultiple = new Object[] {new Object[] {"a", 1}, new Object[] {"b", 2}};
		dades(pidPare).putAll(Map.of("nom", "joan", "import", 21L, "registreMultiple", registreMultiple));
		ProcessInstance pare = processEngine.getRuntimeService().createProcessInstanceBuilder()
			.processDefinitionKey("procesPare")
			.predefineProcessInstanceId(pidPare)
			.start();

		ProcessInstance fill = processEngine.getRuntimeService().createProcessInstanceQuery()
			.superProcessInstanceId(pare.getId())
			.singleResult();
		assertNotNull(fill);
		Map<String, Object> dadesFill = dades(fill.getId());
		assertEquals("joan", dadesFill.get("nomFill"));
		assertEquals(21L, dadesFill.get("import"));
		assertEquals(42L, dadesFill.get("doble"));
		assertFalse(dadesFill.containsKey("noCopiada"));
		assertFalse(dadesFill.containsKey("noCopiadaExpressio"));
		// Dades múltiples i registres: es copia el valor sencer, i les expressions poden accedir als elements
		assertEquals(registreMultiple, dadesFill.get("registreMultiple"));
		assertEquals("b", dadesFill.get("segonaFila"));
		// La tasca d'inici del subprocés ja ha vist les entrades
		assertEquals("JOAN", dadesFill.get("resultat"));

		Task tascaFill = processEngine.getTaskService().createTaskQuery().processInstanceId(fill.getId()).singleResult();
		processEngine.getTaskService().complete(tascaFill.getId());

		Map<String, Object> dadesPare = dades(pare.getId());
		assertEquals("JOAN", dadesPare.get("resultatFill"));
		assertEquals(43L, dadesPare.get("mesUn"));
		assertFalse(dadesPare.containsKey("noRetornada"));
		// El procés pare continua amb les sortides ja copiades
		assertEquals("JOAN", dadesPare.get("vistDespres"));
		// Les dades d'un procés no es barregen amb les de l'altre
		assertFalse(dadesPare.containsKey("nomFill"));
		assertFalse(dadesFill.containsKey("resultatFill"));
	}

	private static Map<String, Object> dades(String processInstanceId) {
		return DADES.computeIfAbsent(processInstanceId, k -> new ConcurrentHashMap<>());
	}

	private static HeliumApi heliumApi(String processInstanceId) {
		HeliumApi api = mock(HeliumApi.class);
		doAnswer(i -> {
			Object valor = i.getArgument(1);
			if (valor == null) {
				dades(processInstanceId).remove(i.getArgument(0));
			} else {
				dades(processInstanceId).put(i.getArgument(0), valor);
			}
			return null;
		}).when(api).setVariable(anyString(), org.mockito.ArgumentMatchers.any());
		return api;
	}

}
