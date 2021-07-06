package es.caib.helium.camunda.controller;

import es.caib.helium.camunda.model.WTaskInstance;
import es.caib.helium.camunda.service.TaskInstanceService;
import es.caib.helium.client.engine.model.InfoCacheData;
import es.caib.helium.client.engine.model.ReassignTaskData;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Slf4j
@AllArgsConstructor
@RequestMapping(TaskController.API_PATH)
public class TaskController {

    public static final String API_PATH = "/api/v1/taskInstances";

    private final TaskInstanceService taskInstanceService;

    @GetMapping(value="/{taskId}")
    public ResponseEntity<WTaskInstance> getTaskById(
            @PathVariable("taskId") String taskId) {
        return new ResponseEntity<>(
                taskInstanceService.getTaskById(taskId),
                HttpStatus.OK);
    }

    @GetMapping(value="/byProcessInstance/{processInstanceId}")
    public ResponseEntity<List<WTaskInstance>> findTaskInstancesByProcessInstanceId(
            @PathVariable("processInstanceId") String processInstanceId) {
        var tasques = taskInstanceService.findTaskInstancesByProcessInstanceId(processInstanceId);
        if (tasques == null || tasques.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(tasques, HttpStatus.OK);
    }

    @GetMapping(value="/byExecution/{executionTokenId}/id")
    public ResponseEntity<String> getTaskInstanceIdByExecutionTokenId(
            @PathVariable("executionTokenId") String executionTokenId) {
        return new ResponseEntity<>(
                taskInstanceService.getTaskInstanceIdByExecutionTokenId(executionTokenId),
                HttpStatus.OK);
    }

    // TODO: Consultes a realitzar al MS d'expedients i tasques
//    @RequestMapping(value="/byFiltrePaginat", method = RequestMethod.GET)
//    public ResponseEntity<ResultatConsultaPaginada<WTaskInstance>> tascaFindByFiltrePaginat(
//            @RequestParam(value = "entornId", required = false) Long entornId,
//            @RequestParam(value = "actorId", required = false) String actorId,
//            @RequestParam(value = "taskName", required = false) String taskName,
//            @RequestParam(value = "titol", required = false) String titol,
//            @RequestParam(value = "expedientId", required = false) Long expedientId,
//            @RequestParam(value = "expedientTitol", required = false) String expedientTitol,
//            @RequestParam(value = "expedientNumero", required = false) String expedientNumero,
//            @RequestParam(value = "expedientTipusId", required = false) Long expedientTipusId,
//            @RequestParam(value = "dataCreacioInici", required = false) Date dataCreacioInici,
//            @RequestParam(value = "dataCreacioFi", required = false) Date dataCreacioFi,
//            @RequestParam(value = "prioritat", required = false) Integer prioritat,
//            @RequestParam(value = "dataLimitInici", required = false) Date dataLimitInici,
//            @RequestParam(value = "dataLimitFi", required = false) Date dataLimitFi,
//            @RequestParam(value = "mostrarAssignadesUsuari", required = false) boolean mostrarAssignadesUsuari,
//            @RequestParam(value = "mostrarAssignadesGrup", required = false) boolean mostrarAssignadesGrup,
//            @RequestParam(value = "nomesPendents", required = false) boolean nomesPendents,
//            @RequestParam(value = "paginacioParams") PaginacioParamsDto paginacioParams,
//            @RequestParam(value = "nomesCount", required = false) boolean nomesCount) {
//        return new ResponseEntity<>(
//                taskInstanceService.tascaFindByFiltrePaginat(
//                        entornId,
//                        actorId,
//                        taskName,
//                        titol,
//                        expedientId,
//                        expedientTitol,
//                        expedientNumero,
//                        expedientTipusId,
//                        dataCreacioInici,
//                        dataCreacioFi,
//                        prioritat,
//                        dataLimitInici,
//                        dataLimitFi,
//                        mostrarAssignadesUsuari,
//                        mostrarAssignadesGrup,
//                        nomesPendents,
//                        paginacioParams,
//                        nomesCount),
//                HttpStatus.OK);
//    }
//
//    @RequestMapping(value="/byFiltrePaginat/ids", method = RequestMethod.GET)
//    public ResponseEntity<LlistatIds> tascaIdFindByFiltrePaginat(
//            @RequestParam(value = "responsable", required = false) String responsable,
//            @RequestParam(value = "tasca", required = false) String tasca,
//            @RequestParam(value = "tascaSel", required = false) String tascaSel,
//            @RequestParam(value = "idsPIExpedients", required = false) List<Long> idsPIExpedients,
//            @RequestParam(value = "dataCreacioInici", required = false) Date dataCreacioInici,
//            @RequestParam(value = "dataCreacioFi", required = false) Date dataCreacioFi,
//            @RequestParam(value = "prioritat", required = false) Integer prioritat,
//            @RequestParam(value = "dataLimitInici", required = false) Date dataLimitInici,
//            @RequestParam(value = "dataLimitFi", required = false) Date dataLimitFi,
//            @RequestParam(value = "paginacioParams") PaginacioParamsDto paginacioParams,
//            @RequestParam(value = "nomesTasquesPersonals", required = false) boolean nomesTasquesPersonals,
//            @RequestParam(value = "nomesTasquesGrup", required = false) boolean nomesTasquesGrup,
//            @RequestParam(value = "nomesAmbPendents", required = false) boolean nomesAmbPendents) {
//        return new ResponseEntity<>(
//                taskInstanceService.tascaIdFindByFiltrePaginat(
//                        responsable,
//                        tasca,
//                        tascaSel,
//                        idsPIExpedients,
//                        dataCreacioInici,
//                        dataCreacioFi,
//                        prioritat,
//                        dataLimitInici,
//                        dataLimitFi,
//                        paginacioParams,
//                        nomesTasquesPersonals,
//                        nomesTasquesGrup,
//                        nomesAmbPendents),
//                HttpStatus.OK);
//    }

    @PostMapping(value="/{taskId}/take/{actorId}")
    public ResponseEntity<Void> takeTaskInstance(
            @PathVariable("taskId") String taskId,
            @PathVariable("actorId") String actorId) {
        taskInstanceService.takeTaskInstance(taskId, actorId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping(value="/{taskId}/release")
    public ResponseEntity<Void> releaseTaskInstance(
            @PathVariable("taskId") String taskId) {
        taskInstanceService.releaseTaskInstance(taskId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping(value="/{taskId}/start")
    public ResponseEntity<WTaskInstance> startTaskInstance(
            @PathVariable("taskId") String taskId) {
        return new ResponseEntity<>(
                taskInstanceService.startTaskInstance(taskId),
                HttpStatus.OK);
    }

    @PostMapping(value="/{taskId}/end")
    public ResponseEntity<Void> endTaskInstance(
            @PathVariable("taskId") String taskId,
            @RequestParam(value = "outcome", required = false) String outcome) {
        taskInstanceService.completeTaskInstance(taskId, outcome);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping(value="/{taskId}/cancel")
    public ResponseEntity<WTaskInstance> cancelTaskInstance(
            @PathVariable("taskId") String taskId) {
        return new ResponseEntity<>(
                taskInstanceService.cancelTaskInstance(taskId),
                HttpStatus.OK);
    }

    @PostMapping(value="/{taskId}/suspend")
    public ResponseEntity<WTaskInstance> suspendTaskInstance(
            @PathVariable("taskId") String taskId) {
        return new ResponseEntity<>(
                taskInstanceService.suspendTaskInstance(taskId),
                HttpStatus.OK);
    }

    @PostMapping(value="/{taskId}/resume")
    public ResponseEntity<WTaskInstance> resumeTaskInstance(
            @PathVariable("taskId") String taskId) {
        return new ResponseEntity<>(
                taskInstanceService.resumeTaskInstance(taskId),
                HttpStatus.OK);
    }

    @PostMapping(value="/{taskId}/reassign")
    public ResponseEntity<WTaskInstance> reassignTaskInstance(
            @PathVariable("taskId") String taskId,
            @RequestBody ReassignTaskData reassignTask) {
        return new ResponseEntity<>(
                taskInstanceService.reassignTaskInstance(
                        taskId,
                        reassignTask.getExpression(),
                        reassignTask.getEntornId()),
                HttpStatus.OK);
    }

    @PutMapping(value="/{taskId}")
    public ResponseEntity<Void> updateTaskInstanceInfoCache(
            @PathVariable("taskId") String taskId,
            @RequestBody InfoCacheData info) {
        taskInstanceService.updateTaskInstanceInfoCache(
                taskId,
                info.getTitol(),
                info.getInfo());
        return new ResponseEntity<>(HttpStatus.OK);
    }

}