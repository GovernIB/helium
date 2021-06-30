package es.caib.helium.camunda.config;

import org.camunda.bpm.BpmPlatform;
import org.camunda.bpm.ProcessEngineService;
import org.camunda.bpm.engine.HistoryService;
import org.camunda.bpm.engine.ManagementService;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.ProcessEngineConfiguration;
import org.camunda.bpm.engine.RepositoryService;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.TaskService;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebMvc
@ComponentScan
public class CamundaContextConfig implements WebMvcConfigurer {

    @Bean(destroyMethod = "")
    public ProcessEngine processEngine(){
        ProcessEngine processEngine = BpmPlatform.getDefaultProcessEngine();
        processEngine.getProcessEngineConfiguration().setHistory(ProcessEngineConfiguration.HISTORY_FULL);
//        processEngine.getProcessEngineConfiguration().setHistory(ProcessEngineConfiguration.HISTORY_AUDIT);
        return processEngine;
    }

//    @Bean
//    public SpringProcessApplication processApplication() {
//        return new SpringProcessApplication();
//    }

    @Bean
    public ProcessEngineService processEngineService() {
        return BpmPlatform.getProcessEngineService();
    }

    @Bean
    public ProcessEngineConfiguration processEngineConfiguration(ProcessEngine processEngine) {
        return processEngine.getProcessEngineConfiguration();
    }

    @Bean
    public RepositoryService repositoryService(ProcessEngine processEngine) {
        return processEngine.getRepositoryService();
    }

    @Bean
    public RuntimeService runtimeService(ProcessEngine processEngine) {
        return processEngine.getRuntimeService();
    }

    @Bean
    public TaskService taskService(ProcessEngine processEngine) {
        return processEngine.getTaskService();
    }

    @Bean
    public HistoryService historyService(ProcessEngine processEngine) {
        return processEngine.getHistoryService();
    }

    @Bean
    public ManagementService managementService(ProcessEngine processEngine) {
        return processEngine.getManagementService();
    }

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder.build();
    }
}
