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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@Slf4j
@AllArgsConstructor
@RequestMapping(DeploymentController.API_PATH)
public class DeploymentController {

    public static final String API_PATH = "/api/v1/deployments";

    private final DeploymentService deploymentService;



    /**
     * Obté les dades d'un desplegament concret
     *
     * @param deploymentId
     * @return
     */
    @GetMapping(value = "/{deploymentId}",
            produces = { "application/json" })
    ResponseEntity<WDeployment> getDesplegament(@PathVariable String deploymentId) {
        return new ResponseEntity<>(deploymentService.getDesplegament(deploymentId), HttpStatus.OK);
    }


    @GetMapping(
            produces = { "application/json" })
    ResponseEntity<List<WDeployment>> getDeployments(
            @RequestParam MultiValueMap<String, String> requestParams,
            @QueryParam("firstResult") Integer firstResult,
            @QueryParam("maxResults") Integer maxResults) {
        ObjectMapper objectMapper = new ObjectMapper();
        MultivaluedMap<String, String> queryParams = new MultivaluedHashMap<>();
        requestParams.toSingleValueMap().forEach((k, v) -> queryParams.add(k, v));

        List<WDeployment> deploymentList = deploymentService.getDesplegaments(queryParams, firstResult, maxResults);
        if (deploymentList == null || deploymentList.isEmpty())
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        return new ResponseEntity<>(deploymentService.getDesplegaments(queryParams, firstResult, maxResults), HttpStatus.OK);
    }

    /**
     * Obté els noms dels recursos desplegats en un desplegament concret
     *
     * @param deploymentId
     * @return
     */
    @GetMapping(value = "/{deploymentId}/resourceNames",
            produces = { "application/json" })
    ResponseEntity<Set<String>> getResourceNames(@PathVariable String deploymentId) {
        return new ResponseEntity<>(deploymentService.getResourceNames(deploymentId), HttpStatus.OK);
    }

    /**
     * Obté el contingut d'un recurs d'un desplegament concret. El recurs s'identifica amb el nom
     *
     * @param deploymentId
     * @param resourceName
     * @return
     */
    @GetMapping(value = "/{deploymentId}/resources/{resourceName}",
            produces = { "application/json" })
    ResponseEntity<String> getResource(
            @PathVariable String deploymentId,
            @PathVariable String resourceName) {
        return new ResponseEntity<>(
                Base64.getEncoder().encodeToString(deploymentService.getResourceBytes(deploymentId, resourceName)),
                HttpStatus.OK);
    }


    /**
     * Desplega un model BPMN2.0
     *
     * @param nomArxiu
     * @param contingut
     * @return
     */
//    public WDeployment desplegar(
//            String nomArxiu,
//            byte[] contingut);

    @PostMapping(
            consumes = { "multipart/form-data" },
            produces = { "application/json" })
    public ResponseEntity<Void> createDeployment(
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


    /**
     * Actualitza els recursos de tipus acció, sense canviar la versió d'un desplagament
     *
     * @param deploymentId
     * @param handlers
     */
    public void updateDeploymentActions(
            Long deploymentId,
            Map<String,
                    byte[]> handlers) {
        throw new ResponseStatusException(HttpStatus.NOT_IMPLEMENTED, "Mètode no disponible en Camunda");
    }


    /**
     * Elimina un desplegament concret
     *
     * @param deploymentId
     */
    public void esborrarDesplegament(String deploymentId);


}
