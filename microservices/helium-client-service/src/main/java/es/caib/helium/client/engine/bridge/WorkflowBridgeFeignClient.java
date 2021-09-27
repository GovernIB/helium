package es.caib.helium.client.engine.bridge;

import es.caib.helium.client.engine.model.CampTascaRest;
import es.caib.helium.client.engine.model.DocumentTasca;
import es.caib.helium.client.engine.model.Reassignacio;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(contextId = "helium", name = "helium", url = "${es.caib.helium.url:localhost:8080/heliumback}", configuration = WorkflowBridgeFeignClientConfig.class)
public interface WorkflowBridgeFeignClient {

    static final String BRIDGE_API = "/bridge/api";
    static final String TASK_API = BRIDGE_API + "/tasques";
    static final String GENERIC_API = BRIDGE_API + "/generic";

    @RequestMapping(method = RequestMethod.GET, value=TASK_API + "/{processInstanceId}/task/{taskName}/camps")
    public ResponseEntity<List<CampTascaRest>> findCampsPerTaskInstance(
            @PathVariable("processInstanceId") String processInstanceId,
            @RequestParam(value = "processDefinitionId") String processDefinitionId,
            @PathVariable("taskName") String taskName);

    @RequestMapping(method = RequestMethod.GET, value=BRIDGE_API + "/{processInstanceId}/task/{taskName}/documents")
    public ResponseEntity<List<DocumentTasca>> findDocumentsPerTaskInstance(
            @PathVariable("processInstanceId") String processInstanceId,
            @RequestParam(value = "processDefinitionId") String processDefinitionId,
            @PathVariable("taskName") String taskName);

    @RequestMapping(method = RequestMethod.GET, value=GENERIC_API + "/reassignacio/{usuariCodi}")
    public ResponseEntity<Reassignacio> getReassignacio(
            @PathVariable(value = "usuariCodi") String usuariCodi,
            @RequestParam(value = "processInstanceId") String processInstanceId);
}