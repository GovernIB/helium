package es.caib.helium.logic.config;

import es.caib.helium.logic.classloader.WorkflowEngineApiClassLoader;
import es.caib.helium.logic.intf.service.WorkflowEngineApi;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flowable.common.engine.api.delegate.event.*;
import org.flowable.engine.ProcessEngineConfiguration;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.spring.SpringProcessEngineConfiguration;
import org.flowable.spring.boot.EngineConfigurationConfigurer;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class FlowableConfig implements EngineConfigurationConfigurer<SpringProcessEngineConfiguration> {

	private final WorkflowEngineApi workflowEngineApi;

	@Override
	public void configure(SpringProcessEngineConfiguration processEngineConfiguration) {
		processEngineConfiguration.setClassLoader(
			new WorkflowEngineApiClassLoader(workflowEngineApi));
		processEngineConfiguration.setEventListeners(List.of(new CurrentDeploymentIdEventListener(processEngineConfiguration)));
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
