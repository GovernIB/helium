package net.conselldemallorca.helium.webapp.ms.api;

import lombok.Data;
import net.conselldemallorca.helium.core.api.WorkflowBridgeService;
import net.conselldemallorca.helium.jbpm3.handlers.tipus.ExpedientInfo;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientDadaDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientDto;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * API REST de terminis.
 *
 */
@Controller
@RequestMapping("/bridge/api/expedients")
public class ExpedientRestController {
	
	@Autowired
	private WorkflowBridgeService workflowBridgeService;

	@RequestMapping(value="/{entornId}", method = RequestMethod.GET)
	@ResponseBody
	public List<ExpedientInfo> findExpedientsConsultaGeneral(
			@PathVariable("entornId") Long entornId,
			@RequestParam(value = "titol", required = false) String titol,
			@RequestParam(value = "numero", required = false) String numero,
			@RequestParam(value = "dataInici1", required = false) Date dataInici1,
			@RequestParam(value = "dataInici2", required = false) Date dataInici2,
			@RequestParam(value = "expedientTipusId", required = false) Long expedientTipusId,
			@RequestParam(value = "estatId", required = false) Long estatId,
			@RequestParam(value = "nomesIniciats") boolean nomesIniciats,
			@RequestParam(value = "nomesFinalitzats") boolean nomesFinalitzats) {
		return workflowBridgeService.findExpedientsConsultaGeneral(
				entornId,
				titol,
				numero,
				dataInici1,
				dataInici2,
				expedientTipusId,
				estatId,
				nomesIniciats,
				nomesFinalitzats);
	}

	@RequestMapping(value="/{entornId}/byExpedientTipus/{expedientTipusCodi}", method = RequestMethod.GET)
	@ResponseBody
	public List<ExpedientInfo> findExpedientsConsultaDades(
			@PathVariable("entornId") Long entornId,
			@PathVariable("expedientTipusCodi") String expedientTipusCodi,
			@RequestParam Map<String,String> allParams) {

		return workflowBridgeService.findExpedientsConsultaDadesIndexades(
				entornId,
				expedientTipusCodi,
				allParams);
	}

	@RequestMapping(value="/{entornId}/byNumero", method = RequestMethod.GET)
	@ResponseBody
	public ExpedientDto getExpedientAmbEntornITipusINumero(
			@PathVariable("entornId") Long entornId,
			@RequestParam(value = "expedientTipusCodi") String expedientTipusCodi,
			@RequestParam(value = "numero") String numero) {
		return workflowBridgeService.getExpedientAmbEntornITipusINumero(
				entornId,
				expedientTipusCodi,
				numero);
	}

	@RequestMapping(value="/{processInstanceId}/arrel", method = RequestMethod.GET)
	@ResponseBody
	public ExpedientDto getExpedientArrelAmbProcessInstanceId(
			@PathVariable("processInstanceId") String processInstanceId) {
		return workflowBridgeService.getExpedientArrelAmbProcessInstanceId(processInstanceId);
	}


	@RequestMapping(value="/{processInstanceId}/aturar", method = RequestMethod.POST)
	@ResponseBody
	public void expedientAturar(
			@PathVariable("processInstanceId") String processInstanceId,
			@RequestBody String motiu) {
		workflowBridgeService.expedientAturar(processInstanceId, motiu);
	}

	@RequestMapping(value="/{processInstanceId}/reprendre", method = RequestMethod.POST)
	@ResponseBody
	public void expedientReprendre(@PathVariable("processInstanceId") String processInstanceId) {
		workflowBridgeService.expedientReprendre(processInstanceId);
	}

	@RequestMapping(value="/{processInstanceId}/finalitzar", method = RequestMethod.POST)
	@ResponseBody
	public void finalitzarExpedient(@PathVariable("processInstanceId") String processInstanceId) {
		workflowBridgeService.finalitzarExpedient(processInstanceId);
	}

	@RequestMapping(value="/{processInstanceId}/desfinalitzar", method = RequestMethod.POST)
	@ResponseBody
	public void desfinalitzarExpedient(@PathVariable("processInstanceId") String processInstanceId) {
		workflowBridgeService.desfinalitzarExpedient(processInstanceId);
	}

	@RequestMapping(value="/{expedientIdOrigen}/relacionar/{expedientIdDesti}", method = RequestMethod.POST)
	@ResponseBody
	public void expedientRelacionar(
			@PathVariable("expedientIdOrigen") Long expedientIdOrigen,
			@PathVariable("expedientIdDesti") Long expedientIdDesti) {
		workflowBridgeService.expedientRelacionar(expedientIdOrigen, expedientIdDesti);
	}

	@RequestMapping(value="/{processInstanceId}/estat", method = RequestMethod.POST)
	@ResponseBody
	public void expedientModificarEstat(
			@PathVariable("processInstanceId") String processInstanceId,
			@RequestBody String estatCodi) {
		workflowBridgeService.expedientModificarEstat(processInstanceId, estatCodi);
	}

	@RequestMapping(value="/{processInstanceId}/estatId", method = RequestMethod.POST)
	@ResponseBody
	public void expedientModificarEstat(
			@PathVariable("processInstanceId") String processInstanceId,
			@RequestBody Long estatId) {
		workflowBridgeService.expedientModificarEstatId(processInstanceId, estatId);
	}

	@RequestMapping(value="/{processInstanceId}/comentari", method = RequestMethod.POST)
	@ResponseBody
	public void expedientModificarComentari(
			@PathVariable("processInstanceId") String processInstanceId,
			@RequestBody String comentari) {
		workflowBridgeService.expedientModificarComentari(processInstanceId, comentari);
	}

	@RequestMapping(value="/{processInstanceId}/numero", method = RequestMethod.POST)
	@ResponseBody
	public void expedientModificarNumero(
			@PathVariable("processInstanceId") String processInstanceId,
			@RequestBody String numero) {
		workflowBridgeService.expedientModificarNumero(processInstanceId, numero);
	}

	@RequestMapping(value="/{processInstanceId}/titol", method = RequestMethod.POST)
	@ResponseBody
	public void expedientModificarTitol(
			@PathVariable("processInstanceId") String processInstanceId,
			@RequestBody String titol) {
		workflowBridgeService.expedientModificarTitol(processInstanceId, titol);
	}

	@RequestMapping(value="/{processInstanceId}/georef", method = RequestMethod.POST)
	@ResponseBody
	public void expedientModificarGeoref(
			@PathVariable("processInstanceId") String processInstanceId,
			@RequestBody Georeferencia georeferencia) {
		workflowBridgeService.expedientModificarGeoref(
				processInstanceId,
				georeferencia.getPosx(),
				georeferencia.getPosy(),
				georeferencia.getReferencia());
	}

	@RequestMapping(value="/{processInstanceId}/georeferencia", method = RequestMethod.POST)
	@ResponseBody
	public void expedientModificarGeoreferencia(
			@PathVariable("processInstanceId") String processInstanceId,
			@RequestBody String referencia) {
		workflowBridgeService.expedientModificarGeoreferencia(processInstanceId, referencia);
	}

	@RequestMapping(value="/{processInstanceId}/posx", method = RequestMethod.POST)
	@ResponseBody
	public void expedientModificarGeoX(
			@PathVariable("processInstanceId") String processInstanceId,
			@RequestBody Double posx) {
		workflowBridgeService.expedientModificarGeoX(processInstanceId, posx);
	}

	@RequestMapping(value="/{processInstanceId}/posy", method = RequestMethod.POST)
	@ResponseBody
	public void expedientModificarGeoY(
			@PathVariable("processInstanceId") String processInstanceId,
			@RequestBody Double posy) {
		workflowBridgeService.expedientModificarGeoY(processInstanceId, posy);
	}

	@RequestMapping(value="/{processInstanceId}/dataInici", method = RequestMethod.POST)
	@ResponseBody
	public void expedientModificarDataInici(
			@PathVariable("processInstanceId") String processInstanceId,
			@RequestBody Date dataInici) {
		workflowBridgeService.expedientModificarDataInici(processInstanceId, dataInici);
	}

	@RequestMapping(value="/{processInstanceId}/grup", method = RequestMethod.POST)
	@ResponseBody
	public void expedientModificarGrup(
			@PathVariable("processInstanceId") String processInstanceId,
			@RequestBody String grupCodi) {
		workflowBridgeService.expedientModificarGrup(processInstanceId, grupCodi);
	}

	@RequestMapping(value="/{processInstanceId}/responsable", method = RequestMethod.POST)
	@ResponseBody
	public void expedientModificarResponsable(
			@PathVariable("processInstanceId") String processInstanceId,
			@RequestBody String responsableCodi) {
		workflowBridgeService.expedientModificarResponsable(processInstanceId, responsableCodi);
	}

	@RequestMapping(value="/{expedientId}/error", method = RequestMethod.POST)
	@ResponseBody
	public void updateExpedientError(
			@PathVariable("expedientId") Long expedientId,
			@RequestBody ExpedientError expedientError) {
		workflowBridgeService.updateExpedientError(
				expedientId,
				expedientError.getJobId(),
				expedientError.getErrorDesc(),
				expedientError.getErrorFull());
	}

	@RequestMapping(value="/process/{processInstanceId}/dada/{codi}", method = RequestMethod.GET)
	@ResponseBody
	public ExpedientDadaDto getExpedientAmbEntornITipusINumero(
			@PathVariable("processInstanceId") String processInstanceId,
			@PathVariable(value = "codi") String codi) {
		return workflowBridgeService.getDadaPerProcessInstance(
				processInstanceId,
				codi);
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
