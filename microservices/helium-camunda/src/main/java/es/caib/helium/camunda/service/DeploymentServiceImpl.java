package es.caib.helium.camunda.service;

import es.caib.helium.camunda.mapper.DeploymentMapper;
import es.caib.helium.camunda.model.Fitxer;
import es.caib.helium.camunda.model.WDeployment;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.camunda.bpm.engine.RepositoryService;
import org.camunda.bpm.engine.repository.Deployment;
import org.camunda.bpm.engine.repository.DeploymentBuilder;
import org.camunda.bpm.engine.repository.DeploymentQuery;
import org.camunda.bpm.engine.repository.DeploymentWithDefinitions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.ws.rs.core.MultivaluedMap;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@ConfigurationProperties(prefix = "es.caib.helium.camunda")
public class DeploymentServiceImpl implements DeploymentService {

    private final RepositoryService repositoryService;
    private final DeploymentMapper deploymentMapper;

    @Setter
    @Value("${es.caib.helium.camunda.deployment.path}")
    private String deploymentPath;

    @Override
    public WDeployment desplegar(
            String deploymentName,
            String tenantId,
            Fitxer fitxer) {

        String nomArxiu = fitxer.getNom();
        if (nomArxiu.endsWith(".bpmn") ||
            nomArxiu.endsWith(".dmn") ||
            nomArxiu.endsWith(".cmmn")) {

            DeploymentBuilder deploymentBuilder = repositoryService.createDeployment();
            deploymentBuilder.name(deploymentName);
            deploymentBuilder.source("Helium");
            deploymentBuilder.addInputStream(nomArxiu, fitxer.getInputStream());
            deploymentBuilder.tenantId(tenantId);
            DeploymentWithDefinitions deployment = deploymentBuilder.deployWithResult();
//            DeploymentWithDefinitionsDto deploymentDto = DeploymentWithDefinitionsDto.fromDeployment(deployment);

            return deploymentMapper.toWDeployment(deployment);

        } else if (nomArxiu.endsWith(".war")) {
            try {
                Path path = Paths.get(deploymentPath, nomArxiu);
                Files.write(path, fitxer.getContingut());
            } catch (IOException e) {
                throw new ResponseStatusException(
                        HttpStatus.INTERNAL_SERVER_ERROR,
                        "No ha estat possible desplegar el fitxer " + nomArxiu + ".");
            }
        } else {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Arxiu amb extensió no suportada " + nomArxiu + ". Només es suporten les extensions .bpmn, .dmn, .cmmn i .war");
        }

        return null;
    }

//    @Override
//    public DeploymentWithDefinitionsDto desplegarModeler(
//            String deploymentName,
//            boolean enableDuplicateFiltering,
//            String deploymentSource,
//            String tenantId,
//            List<Fitxer> fitxers) {
//
//        DeploymentBuilder deploymentBuilder = repositoryService.createDeployment();
//        deploymentBuilder.name(deploymentName);
//        deploymentBuilder.enableDuplicateFiltering(enableDuplicateFiltering);
//        deploymentBuilder.source(deploymentSource);
//        deploymentBuilder.tenantId(tenantId);
//        fitxers.forEach(f -> deploymentBuilder.addInputStream(f.getNom(), f.getInputStream()));
//
//        if (!deploymentBuilder.getResourceNames().isEmpty()) {
//            DeploymentWithDefinitions deployment = deploymentBuilder.deployWithResult();
//            return DeploymentWithDefinitionsDto.fromDeployment(deployment);
//        } else {
//            throw new InvalidRequestException(Response.Status.BAD_REQUEST, "No deployment resources contained in the form upload.");
//        }
//    }


    @Override
    public List<WDeployment> getDesplegaments(
            MultivaluedMap<String, String> queryParams,
            Integer firstResult,
            Integer maxResults) {

        DeploymentQuery query = repositoryService.createDeploymentQuery();

        List<Deployment> desplegaments;
        if (firstResult == null && maxResults == null) {
            desplegaments = query.list();
        } else {
            desplegaments = query.listPage(firstResult, maxResults);
        }
        List<WDeployment> wDesplegaments = desplegaments
                .stream()
                .map(deploymentMapper::toWDeployment)
                .collect(Collectors.toList());
        return wDesplegaments;
    }

    @Override
    public WDeployment getDesplegament(String deploymentId) {
        return null;
    }

    @Override
    public void esborrarDesplegament(String deploymentId) {

    }

    @Override
    public Set<String> getResourceNames(String deploymentId) {
        return null;
    }

    @Override
    public byte[] getResourceBytes(String deploymentId, String resourceName) {
        return new byte[0];
    }

    @Override
    public void updateDeploymentActions(Long deploymentId, Map<String, byte[]> handlers) {

    }

}
