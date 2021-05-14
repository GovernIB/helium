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
public class DocumentRestController {
	
	@Autowired
	private WorkflowBridgeService workflowBridgeService;

//	public DocumentDissenyDto getDocumentDisseny(
//			Long definicioProcesId,
//			String processInstanceId,
//			String documentCodi);
//
//	public DocumentDto getDocumentInfo(Long documentStoreId);
//
//	public DocumentDto getDocumentInfo(Long documentStoreId,
//									   boolean ambContingutOriginal,
//									   boolean ambContingutSignat,
//									   boolean ambContingutVista,
//									   boolean perSignar,
//									   boolean perNotificar,
//									   boolean ambSegellSignatura);
//
//	public String getCodiVariablePerDocumentCodi(String documentCodi);
//
//	public ArxiuDto getArxiuPerMostrar(Long documentStoreId);
//
//	public ArxiuDto documentGenerarAmbPlantilla(
//			String taskInstanceId,
//			String processDefinitionId,
//			String processInstanceId,
//			String documentCodi,
//			Date dataDocument);
//
//	public void documentExpedientGuardarDadesRegistre(
//			Long documentStoreId,
//			String registreNumero,
//			Date registreData,
//			String registreOficinaCodi,
//			String registreOficinaNom,
//			boolean registreEntrada);
//
//	public void documentExpedientEsborrar(
//			String taskInstanceId,
//			String processInstanceId,
//			String documentCodi);

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

	private static final Log logger = LogFactory.getLog(DocumentRestController.class);
}
