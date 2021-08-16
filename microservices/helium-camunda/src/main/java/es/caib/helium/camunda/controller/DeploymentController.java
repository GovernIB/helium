package es.caib.helium.camunda.controller;

import es.caib.helium.camunda.model.Fitxer;
import es.caib.helium.camunda.model.WDeployment;
import es.caib.helium.camunda.service.DeploymentService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
import java.util.Set;

@RestController
@Slf4j
@AllArgsConstructor
@RequestMapping(DeploymentController.API_PATH)
public class DeploymentController {

    public static final String API_PATH = "/api/v1/desplegaments";

    private final DeploymentService deploymentService;


    /**
     * Obté les dades d'un desplegament concret
     *
     * @param deploymentId Identificador del desplegament
     * @return Informació del desplegament
     */
    @GetMapping(value = "/{deploymentId}", produces = { "application/json" })
    ResponseEntity<WDeployment> getDesplegament(@PathVariable String deploymentId) {
        return new ResponseEntity<>(deploymentService.getDesplegament(deploymentId), HttpStatus.OK);
    }


    @GetMapping(
            produces = { "application/json" })
    ResponseEntity<List<WDeployment>> getDeployments(
            @RequestParam MultiValueMap<String, String> requestParams,
            @QueryParam("firstResult") Integer firstResult,
            @QueryParam("maxResults") Integer maxResults) {
//        ObjectMapper objectMapper = new ObjectMapper();
        MultivaluedMap<String, String> queryParams = new MultivaluedHashMap<>();
        requestParams.toSingleValueMap().forEach(queryParams::add);

        List<WDeployment> deploymentList = deploymentService.getDesplegaments(queryParams, firstResult, maxResults);
        if (deploymentList == null || deploymentList.isEmpty())
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        return new ResponseEntity<>(deploymentService.getDesplegaments(queryParams, firstResult, maxResults), HttpStatus.OK);
    }

    /**
     * Obté els noms dels recursos desplegats en un desplegament concret
     *
     * @param deploymentId Identificador del desplegament
     * @return Llista amb els noms dels recursos del desplegament
     */
    @GetMapping(value = "/{deploymentId}/resourceNames",
            produces = { "application/json" })
    ResponseEntity<Set<String>> getResourceNames(@PathVariable String deploymentId) {
        return new ResponseEntity<>(deploymentService.getResourceNames(deploymentId), HttpStatus.OK);
    }

    /**
     * Obté el contingut d'un recurs d'un desplegament concret. El recurs s'identifica amb el nom
     *
     * @param deploymentId Identificador del desplegament
     * @param resourceName Nom del recurs que es vol consultar
     * @return Contingut binari del recurs consultat
     */
    @GetMapping(value = "/{deploymentId}/resources/{resourceName}",
            produces = { MediaType.APPLICATION_OCTET_STREAM_VALUE })
    ResponseEntity<byte[]> getResource(
            @PathVariable String deploymentId,
            @PathVariable String resourceName) {
        return new ResponseEntity<>(
//                Base64.getEncoder().encodeToString(deploymentService.getResourceBytes(deploymentId, resourceName)),
                deploymentService.getResourceBytes(deploymentId, resourceName),
                HttpStatus.OK);
    }


    /**
     * Desplega un model BPMN2.0
     *
     * @param deploymentName Nom que es vol assignar al desplegament
     * @param tenantId  Identificador de l'entitat a la que pertany el desplegament
     * @param deploymentFile Fitxer a desplegar
     * @return Informació del desplegament
     */
//    @CircuitBreaker(name = DEPLOYMENT, fallbackMethod = "deployFallback")
//    @RateLimiter(name = BACKEND)
//    @Bulkhead(name = BACKEND)
//    @Retry(name = DEPLOYMENT, fallbackMethod = "deployFallback")
//    @TimeLimiter(name = BACKEND)
    @PostMapping(
            consumes = { MediaType.MULTIPART_FORM_DATA_VALUE },
            produces = { "application/json" })
    public ResponseEntity<WDeployment> createDeployment(
            @RequestPart(value = "deploymentName") String deploymentName,
            @RequestPart(value = "tenantId", required = false) String tenantId,
            @RequestPart(value = "deploymentFile") MultipartFile deploymentFile) throws Exception {

        log.debug("[CTR] create deployment: " + deploymentName);

        WDeployment deployment = deploymentService.desplegar(
                deploymentName,
                tenantId,
                Fitxer.builder()
                        .nom(deploymentFile.getOriginalFilename())
                        .contingut(deploymentFile.getBytes())
                        .build());

        return new ResponseEntity<>(deployment, HttpStatus.CREATED);

    }

    /**
     * @param deploymentFileName Nom del fitxer utilitzat per fer el desplegament, inclosa l'extensió
     * @return Estat del desplegament
     */
    @GetMapping(value = "/{deploymentFileName}/status",
            produces = { MediaType.APPLICATION_OCTET_STREAM_VALUE })
    ResponseEntity<String> getDeploymentStatus(
            @PathVariable String deploymentFileName) {
        return new ResponseEntity<>(
                deploymentService.getDeploymentStatus(deploymentFileName),
                HttpStatus.OK);
    }


    // TODO: Actualment fa un desplegament normal. S'ha de fer alguna altre cosa?
    /**
     * Actualitza els recursos de tipus acció, sense canviar la versió d'un desplegament
     *
     * @param deploymentId Identificador del desplegament
     * @param deploymentFile Fitxer a desplegar
     */
    @PostMapping(value = "/{deploymentId}/actions",
            consumes = { "multipart/form-data" },
            produces = { "application/json" })
    public ResponseEntity<Void> updateDeploymentActions(
            @PathVariable String deploymentId,
//            @RequestPart(value = "handlers", required = false) List<MultipartFile> handlers,
            @RequestPart("deploymentFile") MultipartFile deploymentFile) throws Exception {
        WDeployment desplegament = deploymentService.getDesplegament(deploymentId);
        deploymentService.desplegar(
                desplegament.getName(),
                desplegament.getTenantId(),
                Fitxer.builder()
                        .nom(deploymentFile.getOriginalFilename())
                        .contingut(deploymentFile.getBytes())
                        .build());
        return new ResponseEntity<>(HttpStatus.OK);
//        throw new ResponseStatusException(HttpStatus.NOT_IMPLEMENTED, "Mètode no disponible en Camunda");
    }


    /**
     * Elimina un desplegament concret
     *
     * @param deploymentId Identificador del desplegament a eliminar
     */
    @DeleteMapping(value = "/{deploymentId}")
    public ResponseEntity<Void>  esborrarDesplegament(@PathVariable String deploymentId) {
        deploymentService.esborrarDesplegament(deploymentId);
        return new ResponseEntity<>(new HttpHeaders(), HttpStatus.OK);
    }


}