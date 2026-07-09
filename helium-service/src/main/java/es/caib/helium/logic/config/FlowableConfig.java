package es.caib.helium.logic.config;

import es.caib.helium.logic.classloader.WorkflowEngineApiClassLoader;
import es.caib.helium.logic.flowable.FlowableTaskEventListener;
import es.caib.helium.logic.helper.ComandaHelper;
import es.caib.helium.logic.helper.TascaHelper;
import es.caib.helium.logic.intf.service.ExpedientService;
import es.caib.helium.logic.intf.service.ExpedientTascaService;
import es.caib.helium.logic.intf.service.WorkflowEngineApi;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flowable.common.engine.api.delegate.event.*;
import org.flowable.engine.ProcessEngineConfiguration;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.impl.TaskServiceImpl;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.spring.SpringProcessEngineConfiguration;
import org.flowable.spring.boot.EngineConfigurationConfigurer;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.stream.Collectors;

@Configuration
@RequiredArgsConstructor
public class FlowableConfig implements EngineConfigurationConfigurer<SpringProcessEngineConfiguration> {

	private final WorkflowEngineApi workflowEngineApi;
	private final ComandaHelper comandaHelper;
	private final TascaHelper tascaHelper;

	@Override
	public void configure(SpringProcessEngineConfiguration processEngineConfiguration) {
		processEngineConfiguration.setClassLoader(
			new WorkflowEngineApiClassLoader(workflowEngineApi));
		processEngineConfiguration.setEventListeners(List.of(new CurrentDeploymentIdEventListener(processEngineConfiguration)));
		List<FlowableEventListener> listeners = ((TaskServiceImpl) processEngineConfiguration.getTaskService()).getConfiguration().getEventListeners().stream().collect(Collectors.toList());
		listeners.add(new FlowableTaskEventListener(processEngineConfiguration, comandaHelper, tascaHelper));
		((TaskServiceImpl) processEngineConfiguration.getTaskService()).getConfiguration().setEventListeners(listeners);
		processEngineConfiguration.setUseClassForNameClassLoading(false);
	}

	@Slf4j
	@RequiredArgsConstructor
	public static class CurrentDeploymentIdEventListener extends AbstractFlowableEventListener {
		private final ProcessEngineConfiguration processEngineConfiguration;
		@Override
		public void onEvent(FlowableEvent event) {
			if (!(event instanceof FlowableEngineEvent)) {
				return;
			}
			FlowableEngineEvent engineEvent = (FlowableEngineEvent)event;
			String processDefinitionId = engineEvent.getProcessDefinitionId();
			if (processDefinitionId == null) {
				return;
			}
			try {
				RepositoryService repositoryService = processEngineConfiguration.getRepositoryService();
				ProcessDefinition processDefinition = repositoryService.getProcessDefinition(processDefinitionId);
				if (processDefinition != null) {
					WorkflowEngineApiClassLoader.setDeploymentId(processDefinition.getDeploymentId());
				}
			} catch (Exception ex) {
				log.warn("No s'ha pogut resoldre el deploymentId pel processDefinitionId={}", processDefinitionId, ex);
			}
		}
		@Override
		public boolean isFailOnException() {
			// Si el logging falla, no volem que faci fallar el procés
			return false;
		}
	}

}
