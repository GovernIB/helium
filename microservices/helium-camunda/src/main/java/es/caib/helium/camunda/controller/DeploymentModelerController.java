package es.caib.helium.camunda.controller;

import es.caib.helium.camunda.service.DeploymentService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.repository.Deployment;
import org.camunda.bpm.engine.repository.DeploymentQuery;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

// Compatibilitat per a desplegar des del Camunda Modeler

@RestController
@Slf4j
@AllArgsConstructor
@RequestMapping(DeploymentModelerController.API_PATH)
public class DeploymentModelerController {

    public static final String API_PATH = "/api/v1/deployment";

    private final DeploymentService deploymentService;

    private static final Set<String> RESERVED_KEYWORDS = new HashSet();
    static {
        RESERVED_KEYWORDS.add("deployment-name");
        RESERVED_KEYWORDS.add("enable-duplicate-filtering");
        RESERVED_KEYWORDS.add("deploy-changed-only");
        RESERVED_KEYWORDS.add("deployment-source");
        RESERVED_KEYWORDS.add("tenant-id");
    }

//    @GetMapping(
//            produces = { "application/json" })
//    List<org.camunda.bpm.engine.rest.dto.repository.DeploymentDto> getDeployments(
//            @RequestParam MultiValueMap<String, String> requestParams,
//            @QueryParam("firstResult") Integer firstResult,
//            @QueryParam("maxResults") Integer maxResults) {
//        ObjectMapper objectMapper = new ObjectMapper();
//        MultivaluedMap<String, String> queryParams = new MultivaluedHashMap<>();
//        requestParams.toSingleValueMap().forEach((k, v) -> queryParams.add(k, v));
//        DeploymentQueryDto queryDto = new DeploymentQueryDto(objectMapper, queryParams);
//        ProcessEngine engine = ProcessEngines.getDefaultProcessEngine();
//        DeploymentQuery query = (DeploymentQuery)queryDto.toQuery(engine);
//        List matchingDeployments;
//        if (firstResult == null && maxResults == null) {
//            matchingDeployments = query.list();
//        } else {
//            matchingDeployments = this.executePaginatedQuery(query, firstResult, maxResults);
//        }
//
//        List<org.camunda.bpm.engine.rest.dto.repository.DeploymentDto> deployments = new ArrayList();
//        Iterator var9 = matchingDeployments.iterator();
//
//        while(var9.hasNext()) {
//            Deployment deployment = (Deployment)var9.next();
//            org.camunda.bpm.engine.rest.dto.repository.DeploymentDto def = org.camunda.bpm.engine.rest.dto.repository.DeploymentDto.fromDeployment(deployment);
//            deployments.add(def);
//        }
//
//        return deployments;
//    }
//
//    @PostMapping(
//            value = "create",
//            consumes = { "multipart/form-data" },
//            produces = { "application/json" })
//    public DeploymentWithDefinitionsDto createModelerDeployment(
//            HttpServletRequest httpServletRequest,
//            @RequestParam(value = "deployment-name", required = false) String deploymentName,
//            @RequestParam(value = "enable-duplicate-filtering", required = false) boolean enableDuplicateFiltering,
//            @RequestParam(value = "deploy-changed-only", required = false) boolean deployChangedOnly,
//            @RequestParam(value = "deployment-source", required = false) String deploymentSource,
//            @RequestParam(value = "tenant-id", required = false) String tenantId
//            ) throws Exception {
//
//        enableDuplicateFiltering = enableDuplicateFiltering || deployChangedOnly;
//
//        // Fitxers
//        List<Fitxer> fitxers = new ArrayList<>();
//        Collection<Part> parts = httpServletRequest.getParts();
//        httpServletRequest.getParts().forEach(p -> {
//            if (!RESERVED_KEYWORDS.contains(p.getName())) {
//                String fileName = p.getSubmittedFileName();
//                if (fileName == null) {
//                    throw new InvalidRequestException(Response.Status.BAD_REQUEST, "No file name found in the deployment resource described by form parameter '" + fileName + "'.");
//                }
//
//                try {
//                    fitxers.add(Fitxer.builder()
//                            .nom(fileName)
//                            .contingut(p.getInputStream())
//                            .build());
//                } catch (IOException e) {
//                    throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "No s'ha pogut llegir el fitxer " + fileName);
//                }
//            }
//        });
//
//        DeploymentWithDefinitionsDto deploymentDto = deploymentService.desplegarModeler(
//                deploymentName,
//                enableDuplicateFiltering,
//                deploymentSource,
//                tenantId,
//                fitxers);
//        URI uri = Path.of(httpServletRequest.getContextPath(), "/deployment", deploymentDto.getId()).toUri();
//        deploymentDto.addReflexiveLink(uri, "GET", "self");
//        return deploymentDto;
//    }

    private List<Deployment> executePaginatedQuery(DeploymentQuery query, Integer firstResult, Integer maxResults) {
        if (firstResult == null) {
            firstResult = 0;
        }

        if (maxResults == null) {
            maxResults = 2147483647;
        }

        return query.listPage(firstResult, maxResults);
    }

}
