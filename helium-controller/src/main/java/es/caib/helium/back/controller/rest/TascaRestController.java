package es.caib.helium.back.controller.rest;

import es.caib.helium.client.engine.model.CampTascaRest;
import es.caib.helium.client.expedient.tasca.TascaClientService;
import es.caib.helium.client.expedient.tasca.model.TascaDto;
import es.caib.helium.client.helper.PatchHelper;
import es.caib.helium.logic.intf.dto.CampTascaDto;
import es.caib.helium.logic.intf.dto.DocumentTascaDto;
import es.caib.helium.logic.intf.service.WorkflowBridgeService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.json.Json;
import javax.json.JsonPatchBuilder;
import java.util.Date;
import java.util.List;

/**
 * API REST de terminis.
 *
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/bridge/api/tasques")
public class TascaRestController {
	
	private final WorkflowBridgeService workflowBridgeService;
	
	private final TascaClientService tascaClientService;


	@GetMapping(value="/{processInstanceId}/task/{taskName}/camps")
	public ResponseEntity<List<CampTascaRest>> findCampsPerTaskInstance(
			@PathVariable("processInstanceId") String processInstanceId,
			@RequestParam(value = "processDefinitionId") String processDefinitionId,
			@PathVariable("taskName") String taskName) {
		return new ResponseEntity<>(
				workflowBridgeService.findCampsPerTaskInstance(
						processInstanceId,
						processDefinitionId,
						taskName),
				HttpStatus.OK);
	}

	@GetMapping(value="/{processInstanceId}/task/{taskName}/documents")
	public ResponseEntity<List<DocumentTascaDto>> findDocumentsPerTaskInstance(
			@PathVariable("processInstanceId") String processInstanceId,
			@RequestParam(value = "processDefinitionId") String processDefinitionId,
			@PathVariable("taskName") String taskName) {
		return new ResponseEntity<>(
				workflowBridgeService.findDocumentsPerTaskInstance(
						processInstanceId,
						processDefinitionId,
						taskName),
				HttpStatus.OK);
	}

	@GetMapping(value="/{processInstanceId}/dades/{varCodi}")
	public ResponseEntity<String> getDadaPerTaskInstance(
			@PathVariable("processInstanceId") String processInstanceId,
			@RequestParam(value = "taskInstanceId") String taskInstanceId,
			@PathVariable("varCodi") String varCodi) throws Exception {
		return new ResponseEntity<>(
				workflowBridgeService.getDadaPerTaskInstance(
						processInstanceId,
						taskInstanceId,
						varCodi),
				HttpStatus.OK);
	}

	@GetMapping(value="/{processInstanceId}/campTasca/{varCodi}")
	public ResponseEntity<CampTascaDto> getDadaPerTaskInstance(
			@PathVariable("processInstanceId") String processInstanceId,
			@RequestParam(value = "taskName") String taskName,
			@RequestParam(value = "processDefinitionId") String processDefinitionId,
			@PathVariable("varCodi") String varCodi) {
		return new ResponseEntity<>(
				workflowBridgeService.getCampTascaPerInstanciaTasca(
						taskName,
						processDefinitionId,
						processInstanceId,
						varCodi),
				HttpStatus.OK);
	}

//	@RequestMapping(value="/{taskId}/isSegonPla")
//	@ResponseBody
//	public boolean isTascaEnSegonPla(@PathVariable("taskId") Long taskId) {
//		return workflowBridgeService.isTascaEnSegonPla(taskId);
//	}

	@PostMapping(value="/{taskId}/missatge")
	public ResponseEntity<Void> addMissatgeExecucioTascaSegonPla(
			@PathVariable("taskId") String taskId,
			@RequestBody String[] message) {
		workflowBridgeService.addMissatgeExecucioTascaSegonPla(taskId, message);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@PostMapping(value="/{taskId}/error")
	public ResponseEntity<Void> setErrorTascaSegonPla(
			@PathVariable("taskId") String taskId,
			@RequestBody(required = false) String error ) {
		workflowBridgeService.setErrorTascaSegonPla(taskId, error);
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	@PostMapping
	public ResponseEntity<Void> crear(@RequestBody TascaDto tasca) {
		tascaClientService.createTascaV1(tasca);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@PostMapping("{tascaId}/finalitzar")
	public ResponseEntity<Void> finalitzar(
			@PathVariable("tascaId") String tascaId,
			@RequestBody Date end) {

		// Informa la data de fi
        JsonPatchBuilder jpb = Json.createPatchBuilder();
        PatchHelper.replaceDateProperty(jpb, "dataFi", end);
		tascaClientService.patchTascaV1(
				tascaId,
				PatchHelper.toJsonNode(jpb));
		return new ResponseEntity<>(HttpStatus.OK);
	}

	
	@PostMapping("{tascaId}/assignar")
	public ResponseEntity<Void> assignar(
			@PathVariable("tascaId") String tascaId,
			@RequestBody TascaDto tasca) {

		// Informa l'usuari assignat
		try {
	        JsonPatchBuilder jpb = Json.createPatchBuilder();
	        PatchHelper.replaceStringProperty(jpb, "usuariAssignat", tasca.getUsuariAssignat());
			tascaClientService.patchTascaV1(
					tascaId,
					PatchHelper.toJsonNode(jpb));
//			tascaClientService.setResponsablesV1(tascaId, tasca.getResponsables());
//			tascaClientService.setGrupsV1(tascaId, tasca.getGrups());
		} catch(Exception e) {
			System.err.println("Error assignant: " + e.getMessage());
			e.printStackTrace();
		}
		return new ResponseEntity<>(HttpStatus.OK);
	}

	
	private static final Log logger = LogFactory.getLog(TascaRestController.class);
}
