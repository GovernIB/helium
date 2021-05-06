package es.caib.helium.camunda.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import es.caib.helium.camunda.model.Fitxer;
import es.caib.helium.camunda.model.WDeployment;
import es.caib.helium.camunda.service.DeploymentService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import java.util.List;

@RestController
@Slf4j
@AllArgsConstructor
@RequestMapping(DeploymentController.API_PATH)
public class DeploymentController {

    public static final String API_PATH = "/api/v1/deployments";

    private final DeploymentService deploymentService;

        @GetMapping(
            produces = { "application/json" })
    List<WDeployment> getDeployments(
            @RequestParam MultiValueMap<String, String> requestParams,
            @QueryParam("firstResult") Integer firstResult,
            @QueryParam("maxResults") Integer maxResults) {
        ObjectMapper objectMapper = new ObjectMapper();
        MultivaluedMap<String, String> queryParams = new MultivaluedHashMap<>();
        requestParams.toSingleValueMap().forEach((k, v) -> queryParams.add(k, v));

        return deploymentService.getDesplegaments(queryParams, firstResult, maxResults);
    }

    @PostMapping(
            consumes = { "multipart/form-data" },
            produces = { "application/json" })
    public ResponseEntity<Void> createDeploymentV1(
            @RequestPart(value = "deployment-name") String deploymentName,
            @RequestPart(value = "tenant-id", required = false) String tenantId,
            @RequestPart(value = "file") MultipartFile file) throws Exception {

        log.debug("[CTR] create deployment: " + deploymentName);

        WDeployment deployment = deploymentService.desplegar(
                deploymentName,
                tenantId,
                Fitxer.builder()
                        .nom(file.getOriginalFilename())
                        .contingut(file.getBytes())
                        .build());

        HttpHeaders httpHeaders = new HttpHeaders();
        if (deployment != null)
            httpHeaders.add("Location", API_PATH + "/" + deployment.getId());
        return new ResponseEntity<>(httpHeaders, HttpStatus.CREATED);

    }
}
