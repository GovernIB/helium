package es.caib.helium.logic.config;

import es.caib.helium.logic.flowable.HeliumBpmnPreDeployer;
import es.caib.helium.logic.flowable.HeliumDadesELResolver;
import org.springframework.beans.factory.ObjectProvider;
import es.caib.helium.logic.flowable.HeliumServiceTaskXMLConverter;
import org.flowable.common.engine.impl.EngineDeployer;
import org.flowable.engine.impl.cfg.ProcessEngineConfigurationImpl;
import es.caib.helium.logic.flowable.FlowableTaskEventListener;
import es.caib.helium.logic.helper.*;
import es.caib.helium.logic.intf.service.TascaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flowable.common.engine.api.delegate.event.*;
import org.flowable.engine.ProcessEngineConfiguration;
import org.flowable.engine.impl.TaskServiceImpl;
import org.flowable.engine.impl.persistence.entity.ExecutionEntity;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.spring.SpringProcessEngineConfiguration;
import org.flowable.spring.boot.EngineConfigurationConfigurer;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Configuration
@RequiredArgsConstructor
public class FlowableConfig implements EngineConfigurationConfigurer<SpringProcessEngineConfiguration> {
	private final ComandaHelper comandaHelper;
	private final TascaService tascaService;
	private final ExpedientHelper expedientHelper;
	private final ObjectProvider<ExpedientDadaHelper> expedientDadaHelperProvider;

	static {
		// Ha d'estar registrat abans que Flowable llegeixi cap BPMN (desplegaments i parseProcess)
		HeliumServiceTaskXMLConverter.registrar();
	}

	@Override
	public void configure(SpringProcessEngineConfiguration processEngineConfiguration) {
		HeliumServiceTaskXMLConverter.registrar();
		afegirHeliumPreDeployer(processEngineConfiguration);
		afegirResolutorDadesHelium(
			processEngineConfiguration,
			processInstanceId -> expedientDadaHelperProvider.getObject().getDadesValors(null, processInstanceId, null));
		processEngineConfiguration.setEventListeners(List.of(new CurrentDeploymentIdEventListener(processEngineConfiguration)));
		List<FlowableEventListener> listeners = new ArrayList<>(((TaskServiceImpl) processEngineConfiguration.getTaskService()).getConfiguration().getEventListeners());
		listeners.add(new FlowableTaskEventListener(processEngineConfiguration, comandaHelper, tascaService, expedientHelper));
		((TaskServiceImpl) processEngineConfiguration.getTaskService()).getConfiguration().setEventListeners(listeners);
		processEngineConfiguration.setUseClassForNameClassLoading(false);
	}

	/**
	 * Fa que les expressions del motor vegin les dades de l'expedient (veure HeliumDadesELResolver). Es consulta
	 * després de les variables del motor i abans dels beans.
	 */
	public static void afegirResolutorDadesHelium(
			ProcessEngineConfigurationImpl processEngineConfiguration,
			java.util.function.Function<String, Map<String, Object>> dadesProces) {
		processEngineConfiguration.addPreDefaultELResolver(new HeliumDadesELResolver(dadesProces));
	}

	/** Afegeix el deployer que tradueix els atributs helium:* als de Flowable (veure HeliumBpmnPreDeployer). */
	public static void afegirHeliumPreDeployer(ProcessEngineConfigurationImpl processEngineConfiguration) {
		List<EngineDeployer> preDeployers = processEngineConfiguration.getCustomPreDeployers() != null
			? new ArrayList<>(processEngineConfiguration.getCustomPreDeployers())
			: new ArrayList<>();
		if (preDeployers.stream().noneMatch(d -> d instanceof HeliumBpmnPreDeployer)) {
			preDeployers.add(0, new HeliumBpmnPreDeployer());
		}
		processEngineConfiguration.setCustomPreDeployers(preDeployers);
	}

	@Slf4j
	@RequiredArgsConstructor
	public static class CurrentDeploymentIdEventListener extends AbstractFlowableEventListener {
		private final ProcessEngineConfiguration processEngineConfiguration;
		@Override
		public void onEvent(FlowableEvent event) {
			if(event.getType() == FlowableEngineEventType.PROCESS_CREATED) {
				Object entity = ((FlowableEntityEvent)event).getEntity();
				if(entity instanceof ExecutionEntity) {
					String rootProcessInstanceId = ((ExecutionEntity) entity).getRootProcessInstanceId();
					String processInstanceId = ((ExecutionEntity) entity).getProcessInstanceId();
					if(rootProcessInstanceId != null) {
						ProcessInstance pi = processEngineConfiguration
							.getRuntimeService()
							.createProcessInstanceQuery()
							.includeProcessVariables()
							.processInstanceId(rootProcessInstanceId)
							.singleResult();
						List<String> expedientVariables = pi.getProcessVariables()
															.keySet()
															.stream()
															.filter(k -> k.startsWith("__expedient_"))
															.collect(Collectors.toList());
						Map<String, Object> variables = new HashMap<String, Object>();
						for (String key : expedientVariables)
							variables.put(key, pi.getProcessVariables().get(key));
						processEngineConfiguration.getRuntimeService().setVariables(processInstanceId, variables);
					}
				}
				return;
			}
			if (!(event instanceof FlowableEngineEvent)) {
				return;
			}
			FlowableEngineEvent engineEvent = (FlowableEngineEvent)event;
			String processDefinitionId = engineEvent.getProcessDefinitionId();
			if (processDefinitionId == null) {
				return;
			}
//			try {
//				RepositoryService repositoryService = processEngineConfiguration.getRepositoryService();
//				ProcessDefinition processDefinition = repositoryService.getProcessDefinition(processDefinitionId);
//				if (processDefinition != null) {
//					WorkflowEngineApiClassLoader.setDeploymentId(processDefinition.getDeploymentId());
//				}
//			} catch (Exception ex) {
//				log.warn("No s'ha pogut resoldre el deploymentId pel processDefinitionId={}", processDefinitionId, ex);
//			}
		}
		@Override
		public boolean isFailOnException() {
			// Si el logging falla, no volem que faci fallar el procés
			return false;
		}
	}

}
