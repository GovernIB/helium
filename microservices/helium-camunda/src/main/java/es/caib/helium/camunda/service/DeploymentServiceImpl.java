package es.caib.helium.camunda.service;

import es.caib.helium.camunda.mapper.DeploymentMapper;
import es.caib.helium.camunda.model.Fitxer;
import es.caib.helium.camunda.model.WDeployment;
import es.caib.helium.camunda.model.modeler.InvalidRequestException;
import es.caib.helium.camunda.model.modeler.ModelerDeploymentWithDefinitionsDto;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.camunda.bpm.engine.HistoryService;
import org.camunda.bpm.engine.RepositoryService;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.repository.Deployment;
import org.camunda.bpm.engine.repository.DeploymentBuilder;
import org.camunda.bpm.engine.repository.DeploymentQuery;
import org.camunda.bpm.engine.repository.DeploymentWithDefinitions;
import org.camunda.bpm.engine.repository.ProcessDefinition;
import org.camunda.bpm.engine.repository.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

@Service
@RequiredArgsConstructor
@ConfigurationProperties(prefix = "es.caib.helium.camunda")
public class DeploymentServiceImpl implements DeploymentService {

    private static final int DELETE_MAX_WAIT = 30000;
    private static enum DeploymentStatus { DEPLOYED, FAILED, TIMEOUT, INTERRUPTED }


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
    @Transactional
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

            return deploymentMapper.toWDeployment(deployment);

        } else if (nomArxiu.endsWith(".war")) {
            try {
                // Modificam el fitxer META-INF/processes.xml per afegir l'entorn
                byte[] contingutAmbEntorn = deploymentAddTenantId(fitxer, tenantId);
                Path path = Paths.get(deploymentPath, nomArxiu);
                Files.write(path, contingutAmbEntorn);

                ExecutorService executorService = Executors.newSingleThreadExecutor();
                Future<DeploymentStatus> fEstatDesplegament = executorService.submit(new WaitForDeployment(path.toString()));
                try {
                    DeploymentStatus deploymentStatus = fEstatDesplegament.get();
                    switch (deploymentStatus) {
                        case DEPLOYED:
                            return deploymentMapper.toWDeployment(getLastDeployment());
                        case FAILED:
                            throw new ResponseStatusException(
                                    HttpStatus.BAD_REQUEST,
                                    "El desplegament de l'arxiu " + nomArxiu + "ha fallat. " +
                                            "Per a saber l'origen de l'error serà necessari consultar les traces del motor.");
                        case TIMEOUT:
                            throw new ResponseStatusException(
                                    HttpStatus.REQUEST_TIMEOUT,
                                    "S'ha esgotat el temps d'espera del desplegament del fitxer " + nomArxiu + ". " +
                                            "El desplegament encara esta en curs, i es desconeix si acabara correctament. " +
                                            "Provi de consultar passat un temps si ha estat creat.");
                        case INTERRUPTED:
                            throw new ResponseStatusException(
                                    HttpStatus.INTERNAL_SERVER_ERROR,
                                    "El procés de comprovació de l'estat del desplegament del fitxer " + nomArxiu + " ha estat interromput. ");
                        default:
                            throw new ResponseStatusException(
                                    HttpStatus.INTERNAL_SERVER_ERROR,
                                    "No ha estat possible comprovar l'estat del desplegant del fitxer " + nomArxiu + ". ");
                    }
                } catch (Exception e) {
                    throw new ResponseStatusException(
                            HttpStatus.INTERNAL_SERVER_ERROR,
                            "S'ha produit un error comprovant l'estat del desplegant del fitxer " + nomArxiu + ". ",
                            e);
                } finally {
                    executorService.shutdown();
                }

//                return waitForDeployment(deploymentName, nomArxiu);

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

    @Override
    @Transactional
    public ModelerDeploymentWithDefinitionsDto desplegarModeler(
            String deploymentName,
            boolean enableDuplicateFiltering,
            String deploymentSource,
            String tenantId,
            List<Fitxer> fitxers) {

        DeploymentBuilder deploymentBuilder = repositoryService.createDeployment();
        deploymentBuilder.name(deploymentName);
        deploymentBuilder.enableDuplicateFiltering(enableDuplicateFiltering);
        deploymentBuilder.source(deploymentSource);
        deploymentBuilder.tenantId(tenantId);
        fitxers.forEach(f -> deploymentBuilder.addInputStream(f.getNom(), f.getInputStream()));

        if (!deploymentBuilder.getResourceNames().isEmpty()) {
            DeploymentWithDefinitions deployment = deploymentBuilder.deployWithResult();
            return ModelerDeploymentWithDefinitionsDto.fromDeployment(deployment);
        } else {
            throw new InvalidRequestException(Response.Status.BAD_REQUEST, "No deployment resources contained in the form upload.");
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

    @Override
    @Transactional(readOnly = true)
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
    @Transactional(readOnly = true)
    public WDeployment getDesplegament(String deploymentId) {
        return deploymentMapper.toWDeployment(getDeployment(deploymentId));
    }

    @Override
    @Transactional
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
    @Transactional(readOnly = true)
    public Set<String> getResourceNames(String deploymentId) {
        var resources = repositoryService.getDeploymentResourceNames(deploymentId);
        if (resources == null || resources.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Not found. Desplegament: " + deploymentId);
        }
        return new HashSet<>(resources);
    }

    @Override
    @Transactional(readOnly = true)
    public byte[] getResourceBytes(String deploymentId, String resourceName) {
        var resources = repositoryService.getDeploymentResources(deploymentId);
         Optional<Resource> oResource = Optional.empty();
        if (resources != null && !resources.isEmpty()) {
            oResource = resources.stream().filter(r -> r.getName().equals(resourceName)).findFirst();
        }
        return oResource.orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Not found. Desplegament: " + deploymentId + "Recurs: " + resourceName)
        ).getBytes();
    }

    @Override
    @Transactional
    public void updateDeploymentActions(Long deploymentId, Map<String, byte[]> handlers) {

    }

    @Override
    @Transactional(readOnly = true)
    public String getDeploymentStatus(String deploymentFileName) {

        String filePath = Paths.get(deploymentPath, deploymentFileName).toString();

        File deployedFile = new File( filePath + ".deployed");
        File failedFile = new File(filePath + ".failed");
        File pendingFile = new File(filePath + ".pending");
        File deployingFile = new File(filePath + ".isdeploying");

        if (deployedFile.exists()) {
            return "DESPLEGAMENT CORRECTE";
        } else if (failedFile.exists()) {
            return "DESPLEGAMENT AMB ERROR";
        } else if (pendingFile.exists()) {
            return "PENDENT DE DESPLEGAR";
        } else if (deployingFile.exists()) {
            return "DESPLEGANT";
        }
        throw new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "No s'ha pogut obtenir l'estat del desplegament del fitxer " + deploymentFileName);
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
        try {
            var deployment = repositoryService.createDeploymentQuery()
                    .orderByDeploymentTime().desc()
                    .listPage(0, 1).get(0);
            if (deployment == null)
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "No s'ha pogut obtenir l'últim desplegament");
            return deployment;
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "No s'ha pogut obtenir l'últim desplegament", ex);
        }
    }


    public static class WaitForDeployment implements Callable<DeploymentStatus> {

        String deploymentFilePath;

        public WaitForDeployment(String deploymentFilePath) {
            this.deploymentFilePath = deploymentFilePath;
        }

        @Override
        public DeploymentStatus call() {

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

            File deployedFile = new File( deploymentFilePath + ".deployed");
            File failedFile = new File(deploymentFilePath + ".failed");

            while (count < maxRetry) {
                try {
                    if (deployedFile.exists()) {
                        return DeploymentStatus.DEPLOYED;
                    } else if (failedFile.exists()) { //|| pendingFile.exists()) {
                        return DeploymentStatus.FAILED;
                    }
                    count++;
                    Thread.sleep(delay);
                } catch (InterruptedException ie) {
                    return DeploymentStatus.INTERRUPTED;
                }
            }

            return DeploymentStatus.TIMEOUT;
        }
    }
}
