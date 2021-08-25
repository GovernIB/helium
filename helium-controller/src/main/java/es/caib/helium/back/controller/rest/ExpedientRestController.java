package es.caib.helium.back.controller.rest;

import es.caib.helium.logic.intf.dto.ExpedientDadaDto;
import es.caib.helium.logic.intf.dto.ExpedientDto;
import es.caib.helium.logic.intf.dto.expedient.ExpedientInfoDto;
import es.caib.helium.logic.intf.service.WorkflowBridgeService;
import lombok.Data;
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
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * API REST de terminis.
 *
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/bridge/api/expedients")
public class ExpedientRestController {
	
	private final WorkflowBridgeService workflowBridgeService;

	@GetMapping(value="/{entornId}")
	public ResponseEntity<List<ExpedientInfoDto>> findExpedientsConsultaGeneral(
			@PathVariable("entornId") Long entornId,
			@RequestParam(value = "titol", required = false) String titol,
			@RequestParam(value = "numero", required = false) String numero,
			@RequestParam(value = "dataInici1", required = false) Date dataInici1,
			@RequestParam(value = "dataInici2", required = false) Date dataInici2,
			@RequestParam(value = "expedientTipusId", required = false) Long expedientTipusId,
			@RequestParam(value = "estatId", required = false) Long estatId,
			@RequestParam(value = "nomesIniciats") boolean nomesIniciats,
			@RequestParam(value = "nomesFinalitzats") boolean nomesFinalitzats) {
		return new ResponseEntity<>(
				workflowBridgeService.findExpedientsConsultaGeneral(
						entornId,
						titol,
						numero,
						dataInici1,
						dataInici2,
						expedientTipusId,
						estatId,
						nomesIniciats,
						nomesFinalitzats),
				HttpStatus.OK);
	}

	@GetMapping(value="/{entornId}/byExpedientTipus/{expedientTipusCodi}")
	public ResponseEntity<List<ExpedientInfoDto>> findExpedientsConsultaDades(
			@PathVariable("entornId") Long entornId,
			@PathVariable("expedientTipusCodi") String expedientTipusCodi,
			@RequestParam Map<String,String> allParams) {

		return new ResponseEntity<>(
				workflowBridgeService.findExpedientsConsultaDadesIndexades(
						entornId,
						expedientTipusCodi,
						allParams),
				HttpStatus.OK);
	}

	@GetMapping(value="/{entornId}/byNumero")
	public ResponseEntity<ExpedientDto> getExpedientAmbEntornITipusINumero(
			@PathVariable("entornId") Long entornId,
			@RequestParam(value = "expedientTipusCodi") String expedientTipusCodi,
			@RequestParam(value = "numero") String numero) {
		return new ResponseEntity<>(
				workflowBridgeService.getExpedientAmbEntornITipusINumero(
						entornId,
						expedientTipusCodi,
						numero),
				HttpStatus.OK);
	}

	@GetMapping(value="/{entornId}/processInstance")
	public ResponseEntity<String> getProcessInstanceIdAmbEntornITipusINumero(
			@PathVariable("entornId") Long entornId,
			@RequestParam(value = "expedientTipusCodi") String expedientTipusCodi,
			@RequestParam(value = "numero") String numero) {
		return new ResponseEntity<>(
				workflowBridgeService.getProcessInstanceIdAmbEntornITipusINumero(
						entornId,
						expedientTipusCodi,
						numero),
				HttpStatus.OK);
	}

	@GetMapping(value="/{processInstanceId}/arrel")
	public ResponseEntity<ExpedientDto> getExpedientArrelAmbProcessInstanceId(
			@PathVariable("processInstanceId") String processInstanceId) {
		return new ResponseEntity<>(
				workflowBridgeService.getExpedientArrelAmbProcessInstanceId(processInstanceId),
				HttpStatus.OK);
	}


	@PostMapping(value="/{processInstanceId}/aturar")
	public ResponseEntity<Void> expedientAturar(
			@PathVariable("processInstanceId") String processInstanceId,
			@RequestBody String motiu) {
		workflowBridgeService.expedientAturar(processInstanceId, motiu);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@PostMapping(value="/{processInstanceId}/reprendre")
	public ResponseEntity<Void> expedientReprendre(@PathVariable("processInstanceId") String processInstanceId) {
		workflowBridgeService.expedientReprendre(processInstanceId);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@PostMapping(value="/{processInstanceId}/finalitzar")
	public ResponseEntity<Void> finalitzarExpedient(@PathVariable("processInstanceId") String processInstanceId,
									@RequestBody Date dataFinalitzacio) {
		workflowBridgeService.finalitzarExpedient(
				processInstanceId,
				dataFinalitzacio);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@PostMapping(value="/{processInstanceId}/desfinalitzar")
	public ResponseEntity<Void> desfinalitzarExpedient(@PathVariable("processInstanceId") String processInstanceId) {
		workflowBridgeService.desfinalitzarExpedient(processInstanceId);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@PostMapping(value="/{expedientIdOrigen}/relacionar/{expedientIdDesti}")
	public ResponseEntity<Void> expedientRelacionar(
			@PathVariable("expedientIdOrigen") Long expedientIdOrigen,
			@PathVariable("expedientIdDesti") Long expedientIdDesti) {
		workflowBridgeService.expedientRelacionar(expedientIdOrigen, expedientIdDesti);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@PostMapping(value="/{processInstanceId}/estat")
	public ResponseEntity<Void> expedientModificarEstat(
			@PathVariable("processInstanceId") String processInstanceId,
			@RequestBody String estatCodi) {
		workflowBridgeService.expedientModificarEstat(processInstanceId, estatCodi);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@PostMapping(value="/{processInstanceId}/estatId")
	public ResponseEntity<Void> expedientModificarEstat(
			@PathVariable("processInstanceId") String processInstanceId,
			@RequestBody Long estatId) {
		workflowBridgeService.expedientModificarEstatId(processInstanceId, estatId);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@PostMapping(value="/{processInstanceId}/comentari")
	public ResponseEntity<Void> expedientModificarComentari(
			@PathVariable("processInstanceId") String processInstanceId,
			@RequestBody String comentari) {
		workflowBridgeService.expedientModificarComentari(processInstanceId, comentari);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@PostMapping(value="/{processInstanceId}/numero")
	public ResponseEntity<Void> expedientModificarNumero(
			@PathVariable("processInstanceId") String processInstanceId,
			@RequestBody String numero) {
		workflowBridgeService.expedientModificarNumero(processInstanceId, numero);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@PostMapping(value="/{processInstanceId}/titol")
	public ResponseEntity<Void> expedientModificarTitol(
			@PathVariable("processInstanceId") String processInstanceId,
			@RequestBody String titol) {
		workflowBridgeService.expedientModificarTitol(processInstanceId, titol);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@PostMapping(value="/{processInstanceId}/georef")
	public ResponseEntity<Void> expedientModificarGeoref(
			@PathVariable("processInstanceId") String processInstanceId,
			@RequestBody Georeferencia georeferencia) {
		workflowBridgeService.expedientModificarGeoref(
				processInstanceId,
				georeferencia.getPosx(),
				georeferencia.getPosy(),
				georeferencia.getReferencia());
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@PostMapping(value="/{processInstanceId}/georeferencia")
	public ResponseEntity<Void> expedientModificarGeoreferencia(
			@PathVariable("processInstanceId") String processInstanceId,
			@RequestBody String referencia) {
		workflowBridgeService.expedientModificarGeoreferencia(processInstanceId, referencia);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@PostMapping(value="/{processInstanceId}/posx")
	@ResponseBody
	public ResponseEntity<Void> expedientModificarGeoX(
			@PathVariable("processInstanceId") String processInstanceId,
			@RequestBody Double posx) {
		workflowBridgeService.expedientModificarGeoX(processInstanceId, posx);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@PostMapping(value="/{processInstanceId}/posy")
	public ResponseEntity<Void> expedientModificarGeoY(
			@PathVariable("processInstanceId") String processInstanceId,
			@RequestBody Double posy) {
		workflowBridgeService.expedientModificarGeoY(processInstanceId, posy);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@PostMapping(value="/{processInstanceId}/dataInici")
	public ResponseEntity<Void> expedientModificarDataInici(
			@PathVariable("processInstanceId") String processInstanceId,
			@RequestBody Date dataInici) {
		workflowBridgeService.expedientModificarDataInici(processInstanceId, dataInici);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@PostMapping(value="/{processInstanceId}/grup")
	public ResponseEntity<Void> expedientModificarGrup(
			@PathVariable("processInstanceId") String processInstanceId,
			@RequestBody String grupCodi) {
		workflowBridgeService.expedientModificarGrup(processInstanceId, grupCodi);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@PostMapping(value="/{processInstanceId}/responsable")
	public ResponseEntity<Void> expedientModificarResponsable(
			@PathVariable("processInstanceId") String processInstanceId,
			@RequestBody String responsableCodi) {
		workflowBridgeService.expedientModificarResponsable(processInstanceId, responsableCodi);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@PostMapping(value="/{expedientId}/error")
	public ResponseEntity<Void> updateExpedientError(
			@PathVariable("expedientId") Long expedientId,
			@RequestBody ExpedientError expedientError) {
		workflowBridgeService.updateExpedientError(
				expedientId,
				expedientError.getJobId(),
				expedientError.getErrorDesc(),
				expedientError.getErrorFull());
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@GetMapping(value="/process/{processInstanceId}/dada/{codi}")
	public ResponseEntity<ExpedientDadaDto> getExpedientAmbEntornITipusINumero(
			@PathVariable("processInstanceId") String processInstanceId,
			@PathVariable(value = "codi") String codi) throws Exception {
		return new ResponseEntity<>(
				workflowBridgeService.getDadaPerProcessInstance(
						processInstanceId,
						codi),
				HttpStatus.OK);
	}

	@Data
	public class Georeferencia {
		private Double posx;
		private Double posy;
		private String referencia;
	}

	@Data
	public class ExpedientError {
		private Long jobId;
		private String errorDesc;
		private String errorFull;
	}

	private static final Log logger = LogFactory.getLog(ExpedientRestController.class);
}
