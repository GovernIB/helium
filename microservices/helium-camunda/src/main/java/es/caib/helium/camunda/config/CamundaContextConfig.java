package es.caib.helium.camunda.config;

import es.caib.helium.camunda.listener.HeliumHistoryEventHandler;
import es.caib.helium.camunda.plugin.HeliumIdentityPlugin;
import es.caib.helium.camunda.plugin.HeliumListenerPlugin;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.BpmPlatform;
import org.camunda.bpm.ProcessEngineService;
import org.camunda.bpm.engine.HistoryService;
import org.camunda.bpm.engine.IdentityService;
import org.camunda.bpm.engine.ManagementService;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.ProcessEngineConfiguration;
import org.camunda.bpm.engine.RepositoryService;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.impl.history.handler.CompositeDbHistoryEventHandler;
import org.camunda.bpm.engine.impl.persistence.StrongUuidGenerator;
import org.camunda.bpm.engine.spring.ProcessEngineFactoryBean;
import org.camunda.bpm.engine.spring.SpringProcessEngineConfiguration;
import org.camunda.bpm.engine.spring.container.ManagedProcessEngineFactoryBean;
import org.camunda.bpm.spring.boot.starter.event.EventPublisherPlugin;
import org.camunda.bpm.spring.boot.starter.property.CamundaBpmProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.sql.DataSource;

@Slf4j
@Configuration
@EnableWebMvc
@ComponentScan
public class CamundaContextConfig implements WebMvcConfigurer {

    @Autowired
    private DataSource dataSource;
    @Autowired
    private CamundaBpmProperties properties;
    @Autowired
    private ApplicationEventPublisher publisher;
    @Autowired
    private HeliumListenerPlugin heliumListenerPlugin;
    @Autowired
    private HeliumIdentityPlugin heliumIdentityPlugin;

//    @Bean
//    public DataSource dataSource() {
//        // Use a JNDI data source or read the properties from
//        // env or a properties file.
//        // Note: The following shows only a simple data source
//        // for In-Memory H2 database.
//
//        SimpleDriverDataSource dataSource = new SimpleDriverDataSource();
//        dataSource.setDriverClass(org.h2.Driver.class);
//        dataSource.setUrl("jdbc:h2:mem:camunda;DB_CLOSE_DELAY=-1");
//        dataSource.setUsername("sa");
//        dataSource.setPassword("");
//        return dataSource;
//    }

    @Bean
    public PlatformTransactionManager transactionManager() {
        return new DataSourceTransactionManager(dataSource);
    }

    @Bean
    public SpringProcessEngineConfiguration processEngineConfiguration() {
        log.info("Iniciant configuració - log");
        System.out.println("Iniciant configuració - sout");

        SpringProcessEngineConfiguration config = new SpringProcessEngineConfiguration();

        config.setDataSource(dataSource);
        config.setTransactionManager(transactionManager());
        config.setJdbcBatchProcessing(false);
        config.setIdGenerator(new StrongUuidGenerator());

        // Custom identity provider
        config.getProcessEnginePlugins().add(heliumIdentityPlugin);

        // Capturar els events
        config.getProcessEnginePlugins().add(heliumListenerPlugin);
        var compositeDbHistoryEventHandler = new CompositeDbHistoryEventHandler(new HeliumHistoryEventHandler());
        config.setHistoryEventHandler(compositeDbHistoryEventHandler);
        config.getProcessEnginePlugins().add(new EventPublisherPlugin(properties.getEventing(), publisher));

        config.setDatabaseSchemaUpdate("true");
        config.setHistory(ProcessEngineConfiguration.HISTORY_FULL);
        config.setJobExecutorActivate(true);
        config.setProcessEngineName("HeliumProcessEngine");
        return config;
    }

    @Bean
    public ProcessEngineFactoryBean processEngine() throws Exception {
//        ProcessEngineFactoryBean factoryBean = new ProcessEngineFactoryBean();
//        factoryBean.setProcessEngineConfiguration(processEngineConfiguration());
        ManagedProcessEngineFactoryBean factoryBean = new ManagedProcessEngineFactoryBean();
        factoryBean.setProcessEngineConfiguration(processEngineConfiguration());
        return factoryBean;
    }

//    @Bean(destroyMethod = "")
//    public ProcessEngine processEngine(){
//        ProcessEngine processEngine = ContainerManaged
//        log.info(">>> CAMUNDA ENGINE: Instanciant processEngine...");
//        ProcessEngine processEngine = BpmPlatform.getDefaultProcessEngine();
//        log.info(">>> CAMUNDA ENGINE: OK");
//        log.info(">>> CAMUNDA ENGINE: Process Engine: " + processEngine.getName());
//        log.info(">>> CAMUNDA ENGINE: Configurant processEngine...");
//        ProcessEngineConfigurationImpl processEngineConfiguration = (ProcessEngineConfigurationImpl)processEngine.getProcessEngineConfiguration();
//        processEngineConfiguration.setHistory(ProcessEngineConfiguration.HISTORY_FULL);
//        processEngineConfiguration.getProcessEnginePlugins().add(CamundaReactor.plugin());
////        processEngine.getProcessEngineConfiguration().setHistory(ProcessEngineConfiguration.HISTORY_FULL);
////        processEngine.getProcessEngineConfiguration().setHistory(ProcessEngineConfiguration.HISTORY_AUDIT);
//        log.info(">>> CAMUNDA ENGINE: OK");
//
//        return processEngine;
//    }

//    @Bean
//    public SpringProcessApplication processApplication() {
//        return new SpringProcessApplication();
//    }

    @Bean
    public ProcessEngineService processEngineService() {
        log.info(">>> CAMUNDA ENGINE: Instanciant ProcessEngineService...");
        ProcessEngineService processEngineService = BpmPlatform.getProcessEngineService();
        log.info(">>> CAMUNDA ENGINE: OK");
        return processEngineService;
    }

    @Bean
    public ProcessEngineConfiguration processEngineConfiguration(ProcessEngine processEngine) {
        log.info(">>> CAMUNDA ENGINE: Instanciant ProcessEngineConfiguration...");
        ProcessEngineConfiguration processEngineConfiguration = processEngine.getProcessEngineConfiguration();
        log.info(">>> CAMUNDA ENGINE: OK");
        return processEngineConfiguration;
    }

    @Bean
    public RepositoryService repositoryService(ProcessEngine processEngine) {
        log.info(">>> CAMUNDA ENGINE: Instanciant RepositoryService...");
        RepositoryService repositoryService = processEngine.getRepositoryService();
        log.info(">>> CAMUNDA ENGINE: OK");
        return repositoryService;
    }

    @Bean
    public RuntimeService runtimeService(ProcessEngine processEngine) {
        log.info(">>> CAMUNDA ENGINE: Instanciant RuntimeService...");
        RuntimeService runtimeService = processEngine.getRuntimeService();
        log.info(">>> CAMUNDA ENGINE: OK");
        return runtimeService;
    }

    @Bean
    public TaskService taskService(ProcessEngine processEngine) {
        log.info(">>> CAMUNDA ENGINE: Instanciant TaskService...");
        TaskService taskService = processEngine.getTaskService();
        log.info(">>> CAMUNDA ENGINE: OK");
        return taskService;
    }

    @Bean
    public HistoryService historyService(ProcessEngine processEngine) {
        log.info(">>> CAMUNDA ENGINE: Instanciant HistoryService...");
        HistoryService historyService = processEngine.getHistoryService();
        log.info(">>> CAMUNDA ENGINE: OK");
        return historyService;
    }

    @Bean
    public ManagementService managementService(ProcessEngine processEngine) {
        log.info(">>> CAMUNDA ENGINE: Instanciant ManagementService...");
        ManagementService managementService = processEngine.getManagementService();
        log.info(">>> CAMUNDA ENGINE: OK");
        return managementService;
    }

    @Bean
    public IdentityService identityService(ProcessEngine processEngine) {
        log.info(">>> CAMUNDA ENGINE: Instanciant IdentityService...");
        IdentityService identityService = processEngine.getIdentityService();
        log.info(">>> CAMUNDA ENGINE: OK");
        return identityService;
    }

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        log.info(">>> CAMUNDA ENGINE: Instanciant RestTemplate...");
        RestTemplate restTemplate = builder.build();
        log.info(">>> CAMUNDA ENGINE: OK");
        return restTemplate;
    }

}
