package es.caib.helium.camunda.service.bridge;

import es.caib.helium.camunda.config.FeignClientConfig;
import es.caib.helium.camunda.model.bridge.CampTascaDto;
import es.caib.helium.camunda.model.bridge.DocumentTascaDto;
import es.caib.helium.camunda.model.bridge.ReassignacioDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(contextId = "helium",
        name = "helium", url = "${es.caib.helium.url:localhost:8080}",
        configuration = FeignClientConfig.class)
public interface BridgeFeignClient {

    static final String BRIDGE_API = "/bridge/api";
    static final String TASK_API = BRIDGE_API + "/tasques";
    static final String GENERIC_API = BRIDGE_API + "/generic";

    @RequestMapping(method = RequestMethod.GET, value=BRIDGE_API + "/{processInstanceId}/task/{taskName}/camps")
    public ResponseEntity<List<CampTascaDto>> findCampsPerTaskInstance(
            @PathVariable("processInstanceId") String processInstanceId,
            @RequestParam(value = "processDefinitionId") String processDefinitionId,
            @PathVariable("taskName") String taskName);

    @RequestMapping(method = RequestMethod.GET, value=BRIDGE_API + "/{processInstanceId}/task/{taskName}/documents")
    public ResponseEntity<List<DocumentTascaDto>> findDocumentsPerTaskInstance(
            @PathVariable("processInstanceId") String processInstanceId,
            @RequestParam(value = "processDefinitionId") String processDefinitionId,
            @PathVariable("taskName") String taskName);

    @RequestMapping(method = RequestMethod.GET, value=GENERIC_API + "/reassignacio/{usuariCodi}")
    public ResponseEntity<ReassignacioDto> getReassignacio(
            @PathVariable(value = "usuariCodi") String usuariCodi,
            @RequestParam(value = "processInstanceId", required = true) String processInstanceId);
}