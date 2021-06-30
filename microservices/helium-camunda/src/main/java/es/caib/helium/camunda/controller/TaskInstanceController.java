package es.caib.helium.camunda.controller;

import es.caib.helium.camunda.model.WTaskInstance;
import es.caib.helium.camunda.service.TaskInstanceService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@AllArgsConstructor
@RequestMapping(TaskInstanceController.API_PATH)
public class TaskInstanceController {

    public static final String API_PATH = "/api/v1/taskInstances";

    private final TaskInstanceService taskInstanceService;

    @GetMapping(value="/taskInstances/{taskId}")
    @ResponseBody
    public ResponseEntity<WTaskInstance> getTaskById(
            @PathVariable("taskId") String taskId) {
        return new ResponseEntity<>(
                taskInstanceService.getTaskById(taskId),
                HttpStatus.OK);
    }

    @GetMapping(value="/taskInstances/byProcessInstance/{processInstanceId}")
    @ResponseBody
    public ResponseEntity<List<WTaskInstance>> findTaskInstancesByProcessInstanceId(
            @PathVariable("processInstanceId") String processInstanceId) {
        var tasques = taskInstanceService.findTaskInstancesByProcessInstanceId(processInstanceId);
        if (tasques == null || tasques.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(tasques, HttpStatus.OK);
    }

    @GetMapping(value="/taskInstances/byExecution/{executionTokenId}/id")
    @ResponseBody
    public ResponseEntity<Long> getTaskInstanceIdByExecutionTokenId(
            @PathVariable("executionTokenId") Long executionTokenId) {
        return new ResponseEntity<>(
                taskInstanceService.getTaskInstanceIdByExecutionTokenId(executionTokenId),
                HttpStatus.OK);
    }

    // TODO: Consultes a realitzar al MS d'expedients i tasques
//    @RequestMapping(value="/taskInstances/byFiltrePaginat", method = RequestMethod.GET)
//    @ResponseBody
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
//    @RequestMapping(value="/taskInstances/byFiltrePaginat/ids", method = RequestMethod.GET)
//    @ResponseBody
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

    @PostMapping(value="/taskInstances/{taskId}/take/{actorId}")
    @ResponseBody
    public ResponseEntity<Void> takeTaskInstance(
            @PathVariable("taskId") String taskId,
            @PathVariable("actorId") String actorId) {
        taskInstanceService.takeTaskInstance(taskId, actorId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping(value="/taskInstances/{taskId}/release")
    @ResponseBody
    public ResponseEntity<Void> releaseTaskInstance(
            @PathVariable("taskId") String taskId) {
        taskInstanceService.releaseTaskInstance(taskId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping(value="/taskInstances/{taskId}/start")
    @ResponseBody
    public ResponseEntity<WTaskInstance> startTaskInstance(
            @PathVariable("taskId") String taskId) {
        return new ResponseEntity<>(
                taskInstanceService.startTaskInstance(taskId),
                HttpStatus.OK);
    }

    @PostMapping(value="/taskInstances/{taskId}/end")
    @ResponseBody
    public ResponseEntity<Void> endTaskInstance(
            @PathVariable("taskId") String taskId,
            @RequestParam(value = "outcome", required = false) String outcome) {
        taskInstanceService.completeTaskInstance(taskId, outcome);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping(value="/taskInstances/{taskId}/cancel")
    @ResponseBody
    public ResponseEntity<WTaskInstance> cancelTaskInstance(
            @PathVariable("taskId") String taskId) {
        return new ResponseEntity<>(
                taskInstanceService.cancelTaskInstance(taskId),
                HttpStatus.OK);
    }

    @PostMapping(value="/taskInstances/{taskId}/suspend")
    @ResponseBody
    public ResponseEntity<WTaskInstance> suspendTaskInstance(
            @PathVariable("taskId") String taskId) {
        return new ResponseEntity<>(
                taskInstanceService.suspendTaskInstance(taskId),
                HttpStatus.OK);
    }

    @PostMapping(value="/taskInstances/{taskId}/resume")
    @ResponseBody
    public ResponseEntity<WTaskInstance> resumeTaskInstance(
            @PathVariable("taskId") String taskId) {
        return new ResponseEntity<>(
                taskInstanceService.resumeTaskInstance(taskId),
                HttpStatus.OK);
    }

    @PostMapping(value="/taskInstances/{taskId}/reassign")
    @ResponseBody
    public ResponseEntity<WTaskInstance> reassignTaskInstance(
            @PathVariable("taskId") String taskId,
            @RequestBody ReassignTask reassignTask) {
        return new ResponseEntity<>(
                taskInstanceService.reassignTaskInstance(
                        taskId,
                        reassignTask.getExpression(),
                        reassignTask.getEntornId()),
                HttpStatus.OK);
    }

    @PutMapping(value="/taskInstances/{taskId}")
    @ResponseBody
    public ResponseEntity<Void> updateTaskInstanceInfoCache(
            @PathVariable("taskId") String taskId,
            @RequestBody InfoCache info) {
        taskInstanceService.updateTaskInstanceInfoCache(
                taskId,
                info.getTitol(),
                info.getInfo());
        return new ResponseEntity<>(HttpStatus.OK);
    }


    @Data
    public static class ReassignTask {
        private String expression;
        private Long entornId;
    }

    @Data
    public static class InfoCache {
        private String titol;
        private String info;
    }

}