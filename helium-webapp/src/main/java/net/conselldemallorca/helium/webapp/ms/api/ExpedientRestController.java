package net.conselldemallorca.helium.webapp.ms.api;

import lombok.Data;
import net.conselldemallorca.helium.core.api.WorkflowBridgeService;
import net.conselldemallorca.helium.v3.core.api.dto.TerminiIniciatDto;
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

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

/**
 * API REST de terminis.
 *
 */
@Controller
@RequestMapping("/api/terminis")
public class ExpedientRestController {
	
	@Autowired
	private WorkflowBridgeService workflowBridgeService;

//	public List<ExpedientDto> findExpedientsConsultaGeneral(
//			Long entornId,
//			String titol,
//			String numero,
//			Date dataInici1,
//			Date dataInici2,
//			Long expedientTipusId,
//			Long estatId,
//			boolean nomesIniciats,
//			boolean nomesFinalitzats);
//
//	public List<ExpedientDto> findExpedientsConsultaDadesIndexades(
//			Long entornId,
//			String expedientTipusCodi,
//			Map<String, Object> filtreValors);
//
//	public ExpedientDto getExpedientAmbEntornITipusINumero(
//			Long entornId,
//			String expedientTipusCodi,
//			String numero);
//
//	public void expedientRelacionar(
//			Long expedientIdOrigen,
//			Long expedientIdDesti);
//
//	public void expedientAturar(
//			String processInstanceId,
//			String motiu);
//
//	public void expedientReprendre(
//			String processInstanceId);
//
//	public void expedientModificarEstat(
//			String processInstanceId,
//			String estatCodi);
//
//	public void expedientModificarComentari(
//			String processInstanceId,
//			String comentari);
//
//	public void expedientModificarNumero(
//			String processInstanceId,
//			String numero);

	@RequestMapping(value="/iniciat", method = RequestMethod.GET)
	@ResponseBody
	public TerminiIniciatDto getIniciat(
			HttpServletRequest request,
			@RequestParam(value = "processDefinitionId", required = true) String processDefinitionId,
			@RequestParam(value = "processInstanceId", required = true) String processInstanceId,
			@RequestParam(value = "terminiCodi", required = true) String terminiCodi) {

		return workflowBridgeService.getTerminiIniciatAmbProcessInstanceITerminiCodi(
				processDefinitionId,
				processInstanceId,
				terminiCodi);
	}

	@RequestMapping(value="/{terminiId}/cancelar", method = RequestMethod.POST)
	@ResponseBody
	public void cancelar(
			HttpServletRequest request,
			@PathVariable("terminiId") Long terminiId,
			@RequestBody Date data) {

		workflowBridgeService.terminiCancelar(
				terminiId,
				data);
	}

	@RequestMapping(value="/{terminiId}/configurar", method = RequestMethod.POST)
	@ResponseBody
	public void configurar(
			HttpServletRequest request,
			@PathVariable("terminiId") Long terminiId,
			@RequestBody TerminiConfigurar terminiConfigurar) {

		workflowBridgeService.configurarTerminiIniciatAmbDadesWf(
				terminiId,
				terminiConfigurar.getTaskInstanceId(),
				terminiConfigurar.getTimerId());
	}

	@RequestMapping(value="/calcular", method = RequestMethod.POST)
	@ResponseBody
	public Date calcularFi(
			HttpServletRequest request,
			@RequestBody TerminiCalcul terminiCalcul) {

		return workflowBridgeService.terminiCalcularDataFi(
				terminiCalcul.getInici(),
				terminiCalcul.getAnys(),
				terminiCalcul.getMesos(),
				terminiCalcul.getDies(),
				terminiCalcul.isLaborable(),
				terminiCalcul.getProcessInstanceId());
	}

	private static final Log logger = LogFactory.getLog(ExpedientRestController.class);
}
