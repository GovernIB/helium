package es.caib.helium.camunda.service;

import es.caib.helium.camunda.mapper.DeploymentMapper;
import org.camunda.bpm.engine.HistoryService;
import org.camunda.bpm.engine.RepositoryService;
import org.camunda.bpm.engine.RuntimeService;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DeploymentServiceImplTest {

    @Mock
    RepositoryService repositoryService;
    @Mock
    RuntimeService runtimeService;
    @Mock
    HistoryService historyService;
    @Spy
    DeploymentMapper deploymentMapper = Mappers.getMapper(DeploymentMapper.class);

    @InjectMocks
    DeploymentService deploymentService;

//    @Test
//    void deploymentAddTenantId() throws IOException {
//        byte[] contingutOriginal = Files.readAllBytes(Path.of("/home/siona/Feina/helium-process-app-demo-1.0.0.war"));
//        Fitxer war = Fitxer.builder()
//                .nom("proces.war")
//                .contingut(contingutOriginal)
//                .build();
//
//        byte[] contingutModificat = deploymentService.deploymentAddTenantId(war, "13");
//
//        Files.write(Path.of("/home/siona/Feina/proces.war"), contingutModificat);
//    }
}