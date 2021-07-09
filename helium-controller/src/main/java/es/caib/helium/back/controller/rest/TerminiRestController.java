package es.caib.helium.back.controller.rest;

import es.caib.helium.logic.intf.dto.TerminiDto;
import es.caib.helium.logic.intf.dto.TerminiIniciatDto;
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
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

/**
 * API REST de terminis.
 *
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/bridge/api/terminis")
public class TerminiRestController {
	
	private final WorkflowBridgeService workflowBridgeService;

	@GetMapping(value="/{terminiCodi}")
	public ResponseEntity<TerminiDto> getByCodi(
			HttpServletRequest request,
			@PathVariable("terminiCodi") String terminiCodi,
			@RequestParam(value = "processInstanceId", required = true) String processInstanceId) {

		return new ResponseEntity<>(
				workflowBridgeService.getTerminiAmbProcessInstanceICodi(
						processInstanceId,
						terminiCodi),
				HttpStatus.OK);
	}

	@GetMapping(value="/iniciat")
	public ResponseEntity<TerminiIniciatDto> getIniciat(
			HttpServletRequest request,
			@RequestParam(value = "processDefinitionId", required = true) String processDefinitionId,
			@RequestParam(value = "processInstanceId", required = true) String processInstanceId,
			@RequestParam(value = "terminiCodi", required = true) String terminiCodi) {

		return new ResponseEntity<>(
				workflowBridgeService.getTerminiIniciatAmbProcessInstanceITerminiCodi(
						processDefinitionId,
						processInstanceId,
						terminiCodi),
				HttpStatus.OK);
	}

	@PostMapping(value="/{terminiCodi}/iniciar")
	public ResponseEntity<Void> iniciar(
			HttpServletRequest request,
			@PathVariable("terminiCodi") String terminiCodi,
			@RequestBody TerminiInici termini) {

		workflowBridgeService.terminiIniciar(
				terminiCodi,
				termini.getProcessInstanceId(),
				termini.getData(),
				termini.getAnys(),
				termini.getMesos(),
				termini.getDies(),
				termini.isEsDataFi());
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@PostMapping(value="/{terminiId}/pausar")
	public ResponseEntity<Void> pausar(
			HttpServletRequest request,
			@PathVariable("terminiId") Long terminiId,
			@RequestBody Date data) {

		workflowBridgeService.terminiPausar(
				terminiId,
				data);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@PostMapping(value="/{terminiId}/continuar")
	public ResponseEntity<Void> continuar(
			HttpServletRequest request,
			@PathVariable("terminiId") Long terminiId,
			@RequestBody Date data) {

		workflowBridgeService.terminiContinuar(
				terminiId,
				data);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@PostMapping(value="/{terminiId}/cancelar")
	public ResponseEntity<Void> cancelar(
			HttpServletRequest request,
			@PathVariable("terminiId") Long terminiId,
			@RequestBody Date data) {

		workflowBridgeService.terminiCancelar(
				terminiId,
				data);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@PostMapping(value="/{terminiId}/configurar")
	public ResponseEntity<Void> configurar(
			HttpServletRequest request,
			@PathVariable("terminiId") Long terminiId,
			@RequestBody TerminiConfigurar terminiConfigurar) {

		workflowBridgeService.configurarTerminiIniciatAmbDadesWf(
				terminiId,
				terminiConfigurar.getTaskInstanceId(),
				terminiConfigurar.getTimerId());
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@PostMapping(value="/calcularInici")
	public ResponseEntity<Date> calcularInici(
			HttpServletRequest request,
			@RequestBody TerminiCalcul terminiCalcul) {

		return new ResponseEntity<>(
				workflowBridgeService.terminiCalcularDataInici(
						terminiCalcul.getData(),
						terminiCalcul.getAnys(),
						terminiCalcul.getMesos(),
						terminiCalcul.getDies(),
						terminiCalcul.isLaborable(),
						terminiCalcul.getProcessInstanceId()),
				HttpStatus.OK);
	}

	@PostMapping(value="/calcularFi")
	public ResponseEntity<Date> calcularFi(
			HttpServletRequest request,
			@RequestBody TerminiCalcul terminiCalcul) {

		return new ResponseEntity<>(
				workflowBridgeService.terminiCalcularDataFi(
						terminiCalcul.getData(),
						terminiCalcul.getAnys(),
						terminiCalcul.getMesos(),
						terminiCalcul.getDies(),
						terminiCalcul.isLaborable(),
						terminiCalcul.getProcessInstanceId()),
				HttpStatus.OK);
	}


	@Data
	public class TerminiInici {
		private String processInstanceId;
		private Date data;
		private int anys;
		private int mesos;
		private int dies;
		private boolean esDataFi;
	}

	@Data
	public class TerminiCalcul {
		private Date data;
		private int anys;
		private int mesos;
		private int dies;
		private boolean laborable;
		private String processInstanceId;
	}

	@Data
	public class TerminiConfigurar {
		private String taskInstanceId;
		private Long timerId;
	}

	private static final Log logger = LogFactory.getLog(TerminiRestController.class);
}
