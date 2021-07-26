package es.caib.helium.camunda.controller;

import es.caib.helium.camunda.model.Fitxer;
import es.caib.helium.camunda.model.modeler.InvalidRequestException;
import es.caib.helium.camunda.model.modeler.ModelerDeploymentWithDefinitionsDto;
import es.caib.helium.camunda.service.DeploymentService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.repository.Deployment;
import org.camunda.bpm.engine.repository.DeploymentQuery;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

// Compatibilitat per a desplegar des del Camunda Modeler

@RestController
@Slf4j
@AllArgsConstructor
@RequestMapping(DeploymentModelerController.API_PATH)
public class DeploymentModelerController {

    public static final String API_PATH = "/api/v1";

    private final DeploymentService deploymentService;

    private static final Set<String> RESERVED_KEYWORDS = new HashSet();
    static {
        RESERVED_KEYWORDS.add("deployment-name");
        RESERVED_KEYWORDS.add("enable-duplicate-filtering");
        RESERVED_KEYWORDS.add("deploy-changed-only");
        RESERVED_KEYWORDS.add("deployment-source");
        RESERVED_KEYWORDS.add("tenant-id");
    }

    @GetMapping( value = "/deployment",
            produces = { "application/json" })
    ResponseEntity<Void> getEndpointValid(
            @RequestParam MultiValueMap<String, String> requestParams,
            @QueryParam("firstResult") Integer firstResult,
            @QueryParam("maxResults") Integer maxResults) {
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping( value = "/version",
            produces = { "application/json" })
    ResponseEntity<Versio> getVersio(
            @RequestParam MultiValueMap<String, String> requestParams,
            @QueryParam("firstResult") Integer firstResult,
            @QueryParam("maxResults") Integer maxResults) {

        return new ResponseEntity<>(new Versio("7.15.0"), HttpStatus.OK);
    }

    @PostMapping(value = "/deployment/create",
//            consumes = { "multipart/form-data" },
            produces = { "application/json" })
    public ModelerDeploymentWithDefinitionsDto createModelerDeployment(
            HttpServletRequest httpServletRequest,
            @RequestParam(value = "deployment-name", required = false) String deploymentName,
            @RequestParam(value = "deployment-source", required = false) String deploymentSource,
            @RequestParam(value = "enable-duplicate-filtering", required = false) boolean enableDuplicateFiltering,
            @RequestParam(value = "deploy-changed-only", required = false) boolean deployChangedOnly,
            @RequestParam(value = "tenant-id", required = false) String tenantId
            ) throws Exception {

        enableDuplicateFiltering = enableDuplicateFiltering || deployChangedOnly;

        // Fitxers
        List<Fitxer> fitxers = new ArrayList<>();
        var parts = httpServletRequest.getParts();
        httpServletRequest.getParts().forEach(p -> {
            if (!RESERVED_KEYWORDS.contains(p.getName())) {
                String fileName = p.getSubmittedFileName();
                if (fileName == null) {
                    throw new InvalidRequestException(Response.Status.BAD_REQUEST, "No file name found in the deployment resource described by form parameter '" + fileName + "'.");
                }

                try {
                    fitxers.add(Fitxer.builder()
                            .nom(fileName)
                            .contingut(p.getInputStream())
                            .build());
                } catch (IOException e) {
                    throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "No s'ha pogut llegir el fitxer " + fileName);
                }
            }
        });

        ModelerDeploymentWithDefinitionsDto deploymentDto = deploymentService.desplegarModeler(
                deploymentName,
                enableDuplicateFiltering,
                deploymentSource,
                tenantId,
                fitxers);
        URI uri = Path.of(httpServletRequest.getContextPath(), "/deployment", deploymentDto.getId()).toUri();
        deploymentDto.addReflexiveLink(uri, "GET", "self");
        return deploymentDto;
    }

    private List<Deployment> executePaginatedQuery(DeploymentQuery query, Integer firstResult, Integer maxResults) {
        if (firstResult == null) {
            firstResult = 0;
        }

        if (maxResults == null) {
            maxResults = 2147483647;
        }

        return query.listPage(firstResult, maxResults);
    }


    @Data
    @AllArgsConstructor
    private static class Versio {
        private String version;
    }
}
