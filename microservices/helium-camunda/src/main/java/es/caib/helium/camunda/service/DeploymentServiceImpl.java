package es.caib.helium.camunda.service;

import es.caib.helium.camunda.mapper.DeploymentMapper;
import es.caib.helium.camunda.model.Fitxer;
import es.caib.helium.camunda.model.WDeployment;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.camunda.bpm.engine.RepositoryService;
import org.camunda.bpm.engine.impl.persistence.entity.DeploymentEntity;
import org.camunda.bpm.engine.impl.persistence.entity.ResourceEntity;
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

                // TODO: Consultar deployment ... tardarà un cert temps en desplegar-se
                // | Filename Suffix    | Description                                                           |
                // |--------------------|-----------------------------------------------------------------------|
                // | .dodeploy 	        | El contingut s'ha de deplegar o redesplegar (Marker que crea l'usuari)|
                // |--------------------|-----------------------------------------------------------------------|
                // | .skipdeploy 	    | Disabilita l'auto-deploy (Marker que crea l'usuari)                   |
                // |--------------------|-----------------------------------------------------------------------|
                // | .isdeploying 	    | S'ha iniciat el desplegament                                          |
                // |--------------------|-----------------------------------------------------------------------|
                // | .deployed 	        | El contingut ha estat desplegat                                       |
                // |--------------------|-----------------------------------------------------------------------|
                // | .failed 	        | El desplegament ha fallat                                             |
                // |--------------------|-----------------------------------------------------------------------|
                // | .isundeploying     | S'ha iniciat l'eliminació del desplegament                            |
                // |--------------------|-----------------------------------------------------------------------|
                // | .undeployed 	    | S'ha eliminat el desplegament                                         |
                // |--------------------|-----------------------------------------------------------------------|
                // | .pending 	        | El desplegament està pausat pendent de resoldre algun error           |
                // |--------------------|-----------------------------------------------------------------------|

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
        // TODO: Aplicar filtres
//                .deploymentId("")
//                .deploymentName("")
//                .deploymentNameLike("")
//                .deploymentAfter(new Date())
//                .deploymentBefore(new Date())
//                .deploymentSource("")
//                .tenantIdIn("");

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
        return deploymentMapper.toWDeployment(getDeployment(deploymentId));
    }

    @Override
    public void esborrarDesplegament(String deploymentId) {

    }

    @Override
    public Set<String> getResourceNames(String deploymentId) {
        DeploymentEntity deployment = (DeploymentEntity) getDeployment(deploymentId);
        Map<String, ResourceEntity> resources = deployment.getResources();
        if (resources == null || resources.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Not found. Desplegament: " + deploymentId);
        }
        return resources.keySet();
    }

    @Override
    public byte[] getResourceBytes(String deploymentId, String resourceName) {
        DeploymentEntity deployment = (DeploymentEntity) getDeployment(deploymentId);
        ResourceEntity resource = deployment.getResource(resourceName);
        if (resource == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Not found. Desplegament: " + deploymentId + "Recurs: " + resourceName);
        }
        return resource.getBytes();
    }

    @Override
    public void updateDeploymentActions(Long deploymentId, Map<String, byte[]> handlers) {

    }



    private Deployment getDeployment(String deploymentId) {
        Deployment deployment = null;
        try {
            deployment = repositoryService.createDeploymentQuery()
                    .deploymentId(deploymentId)
                    .singleResult();
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error obtenint el desplegament amb id: " + deploymentId, ex);
        }
        if (deployment == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Not found. Desplegament: " + deploymentId);
        return deployment;
    }

}
