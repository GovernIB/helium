package net.conselldemallorca.helium.webapp.ms.api;

import lombok.Data;

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

import es.caib.helium.logic.intf.WorkflowBridgeService;
import es.caib.helium.logic.intf.dto.TerminiDto;
import es.caib.helium.logic.intf.dto.TerminiIniciatDto;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

/**
 * API REST de terminis.
 *
 */
@Controller
@RequestMapping("/bridge/api/terminis")
public class TerminiRestController {
	
	@Autowired
	private WorkflowBridgeService workflowBridgeService;

	@RequestMapping(value="/{terminiCodi}", method = RequestMethod.GET)
	@ResponseBody
	public TerminiDto getByCodi(
			HttpServletRequest request,
			@PathVariable("terminiCodi") String terminiCodi,
			@RequestParam(value = "processInstanceId", required = true) String processInstanceId) {

		return workflowBridgeService.getTerminiAmbProcessInstanceICodi(
				processInstanceId,
				terminiCodi);
	}

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

	@RequestMapping(value="/{terminiCodi}/iniciar", method = RequestMethod.POST)
	@ResponseBody
	public void iniciar(
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
	}

	@RequestMapping(value="/{terminiId}/pausar", method = RequestMethod.POST)
	@ResponseBody
	public void pausar(
			HttpServletRequest request,
			@PathVariable("terminiId") Long terminiId,
			@RequestBody Date data) {

		workflowBridgeService.terminiPausar(
				terminiId,
				data);
	}

	@RequestMapping(value="/{terminiId}/continuar", method = RequestMethod.POST)
	@ResponseBody
	public void continuar(
			HttpServletRequest request,
			@PathVariable("terminiId") Long terminiId,
			@RequestBody Date data) {

		workflowBridgeService.terminiContinuar(
				terminiId,
				data);
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

	@RequestMapping(value="/calcularInici", method = RequestMethod.POST)
	@ResponseBody
	public Date calcularInici(
			HttpServletRequest request,
			@RequestBody TerminiCalcul terminiCalcul) {

		return workflowBridgeService.terminiCalcularDataInici(
				terminiCalcul.getData(),
				terminiCalcul.getAnys(),
				terminiCalcul.getMesos(),
				terminiCalcul.getDies(),
				terminiCalcul.isLaborable(),
				terminiCalcul.getProcessInstanceId());
	}

	@RequestMapping(value="/calcularFi", method = RequestMethod.POST)
	@ResponseBody
	public Date calcularFi(
			HttpServletRequest request,
			@RequestBody TerminiCalcul terminiCalcul) {

		return workflowBridgeService.terminiCalcularDataFi(
				terminiCalcul.getData(),
				terminiCalcul.getAnys(),
				terminiCalcul.getMesos(),
				terminiCalcul.getDies(),
				terminiCalcul.isLaborable(),
				terminiCalcul.getProcessInstanceId());
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
