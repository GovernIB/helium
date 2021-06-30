package es.caib.helium.camunda.service;

import es.caib.helium.camunda.mapper.DeploymentMapper;
import es.caib.helium.camunda.model.Fitxer;
import es.caib.helium.camunda.model.WDeployment;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.camunda.bpm.engine.HistoryService;
import org.camunda.bpm.engine.RepositoryService;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.impl.persistence.entity.DeploymentEntity;
import org.camunda.bpm.engine.impl.persistence.entity.ResourceEntity;
import org.camunda.bpm.engine.repository.Deployment;
import org.camunda.bpm.engine.repository.DeploymentBuilder;
import org.camunda.bpm.engine.repository.DeploymentQuery;
import org.camunda.bpm.engine.repository.DeploymentWithDefinitions;
import org.camunda.bpm.engine.repository.ProcessDefinition;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.ws.rs.core.MultivaluedMap;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

@Service
@RequiredArgsConstructor
@ConfigurationProperties(prefix = "es.caib.helium.camunda")
public class DeploymentServiceImpl implements DeploymentService {

    private static final int DELETE_MAX_WAIT = 10000;
    private final RepositoryService repositoryService;
    private final RuntimeService runtimeService;
    private final HistoryService historyService;
    private final DeploymentMapper deploymentMapper;

    @Setter
    @Value("${es.caib.helium.camunda.deployment.path}")
    private String deploymentPath;

    @Override
    @CacheEvict(value = {
            "processDefinitionCache",
            "subProcessDefinitionCache",
            "processDefinitionTasksCache"},
            allEntries = true)
    public WDeployment desplegar(
            String deploymentName,
            String tenantId,    // EntornId
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
                // Modificam el fitxer META-INF/processes.xml per afegir l'entorn
                byte[] contingutAmbEntorn = deploymentAddTenantId(fitxer, tenantId);
                Path path = Paths.get(deploymentPath, nomArxiu);
                Files.write(path, fitxer.getContingut());

                return waitForDeployment(deploymentName, nomArxiu);

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

    }

    private byte[] deploymentAddTenantId(Fitxer fitxer, String tenantId) throws IOException {
        ZipInputStream zis = new ZipInputStream(new ByteArrayInputStream(fitxer.getContingut()));

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final ZipOutputStream zos = new ZipOutputStream(baos);
        byte[] buffer = new byte[1024];

        ZipEntry zipEntry = null;
        while((zipEntry = zis.getNextEntry()) != null) {
            if (!zipEntry.getName().endsWith("processes.xml")) {
                zos.putNextEntry(zipEntry);
                int len;
                while((len = zis.read(buffer)) > 0) {
                    zos.write(buffer, 0, len);
                }
            } else {
                zos.putNextEntry(new ZipEntry(zipEntry.getName()));

//                InputStream is = zipFile.getInputStream(entryIn);
                byte[] deploymentDescriptorBytes = getModifiedDeploymentDescriptor(zis.readAllBytes(), tenantId);
                zos.write(deploymentDescriptorBytes, 0, deploymentDescriptorBytes.length);
            }
            zos.closeEntry();
        }

        zos.close();
        return baos.toByteArray();
    }

    private byte[] getModifiedDeploymentDescriptor(byte[] deploymentDescriptor, String tenantId) {

        String deploymentDescriptorFile = new String(deploymentDescriptor);
        var processArchiveStartIndex = deploymentDescriptorFile.indexOf("<process-archive");
        while (processArchiveStartIndex > -1) {
            var processArchiveEndIndex = deploymentDescriptorFile.indexOf(">", processArchiveStartIndex);
            deploymentDescriptorFile = insertString(deploymentDescriptorFile, " tenantId=\"" + tenantId + "\"", processArchiveEndIndex);
            processArchiveStartIndex = deploymentDescriptorFile.indexOf("<process-archive", processArchiveEndIndex);
        }

        // TODO Afegir tenantId (entornId)
        return deploymentDescriptorFile.getBytes(StandardCharsets.UTF_8);
    }

    private String insertString(
            String originalString,
            String stringToBeInserted,
            int index) {
        StringBuffer newString = new StringBuffer(originalString);
        newString.insert(index, stringToBeInserted);
        return newString.toString();
    }

    private WDeployment waitForDeployment(String deploymentName, String nomArxiu) {

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

        int count = 0;
        int delay = 100;
        int maxRetry = DELETE_MAX_WAIT / delay;

        String fileName = deploymentName.substring(0, deploymentName.length() - 3);
        File deployedFile = new File(fileName + "deployed");
        File failedFile = new File(fileName + "failed");
        File pendingFile = new File(fileName + "pending");

        while (count < maxRetry) {
            try {
                if (deployedFile.exists()) {
                    return deploymentMapper.toWDeployment(getLastDeployment());
                } else if (failedFile.exists()) { //|| pendingFile.exists()) {
                    throw new ResponseStatusException(
                            HttpStatus.INTERNAL_SERVER_ERROR,
                            "S'ha produit un error al desplegar el fitxer " + nomArxiu + ".");
                }
                count++;
                Thread.sleep(delay);
            } catch (InterruptedException ie) {
                throw new ResponseStatusException(
                        HttpStatus.REQUEST_TIMEOUT,
                        "S'ha produit un error desplegant del fitxer " + nomArxiu + ". ",
                        ie);
            }
        }

        throw new ResponseStatusException(
                HttpStatus.REQUEST_TIMEOUT,
                "S'ha esgotat el temps d'espera del desplegament del fitxer " + nomArxiu + ". " +
                        "El desplegament encara esta en curs, i es desconeix si acabara correctament. " +
                        "Provi de consultar passat un temps si ha estat creat.");
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
        Deployment deployment = getDeployment(deploymentId);
        boolean deploymentCanBeDeleted = true;

        List<ProcessDefinition> processDefinitions = repositoryService
                .createProcessDefinitionQuery()
                .deploymentId(deployment.getId()).list();
        for (ProcessDefinition processDefinition : processDefinitions) {
//            ProcessDefinition latestProcessDefiniton = repositoryService
//                    .createProcessDefinitionQuery()
//                    .processDefinitionKey(processDefinition.getKey())
//                    .latestVersion().singleResult();
//            boolean isLatest = latestProcessDefiniton.getId().equals(processDefinition.getId());
            boolean hasRunningInstances = runtimeService
                    .createProcessInstanceQuery()
                    .processDefinitionId(processDefinition.getId()).count() > 0;
            boolean hasHistoricInstances = historyService
                    .createHistoricProcessInstanceQuery()
                    .processDefinitionId(processDefinition.getId()).count() > 0;
            if (hasRunningInstances || hasHistoricInstances) {
                deploymentCanBeDeleted = false;
                break;
            }
        }

        if (deploymentCanBeDeleted) {
            repositoryService.deleteDeployment(deployment.getId());
        }
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

    private Deployment getLastDeployment() {
        List<Deployment> deployments = null;
        try {
            deployments = repositoryService.createDeploymentQuery()
                    .orderByDeploymentId()
                    .listPage(0, 1);
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "No s'han pogut obtenir els desplegametns", ex);
        }
        if (deployments == null || deployments.isEmpty())
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "No s'han pogut obtenir els desplegametns");
        return deployments.get(0);
    }

}
