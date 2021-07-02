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
import es.caib.helium.logic.intf.dto.ArxiuDto;
import es.caib.helium.logic.intf.dto.DocumentDissenyDto;
import es.caib.helium.logic.intf.dto.DocumentDto;

import java.util.Date;

/**
 * API REST de terminis.
 *
 */
@Controller
@RequestMapping("/bridge/api/documents")
public class DocumentRestController {
	
	@Autowired
	private WorkflowBridgeService workflowBridgeService;

	@RequestMapping(value="/{documentCodi}/proces/{processInstanceId}/disseny", method = RequestMethod.GET)
	@ResponseBody
	public DocumentDissenyDto getDocumentDisseny(
			@RequestParam(value = "definicioProcesId") Long definicioProcesId,
			@PathVariable("processInstanceId") String processInstanceId,
			@PathVariable("documentCodi") String documentCodi) {
		return workflowBridgeService.getDocumentDisseny(
				definicioProcesId,
				processInstanceId,
				documentCodi);
	}

	@RequestMapping(value="/{documentStoreId}/info", method = RequestMethod.GET)
	@ResponseBody
	public DocumentDto getDocumentInfo(@PathVariable("documentStoreId") Long documentStoreId){
		return workflowBridgeService.getDocumentInfo(documentStoreId);
	}

	@RequestMapping(value="/{documentStoreId}/infoFiltre", method = RequestMethod.GET)
	@ResponseBody
	public DocumentDto getDocumentInfo(
			@PathVariable("documentStoreId") Long documentStoreId,
			@RequestParam(value = "ambContingutOriginal") boolean ambContingutOriginal,
			@RequestParam(value = "ambContingutSignat") boolean ambContingutSignat,
			@RequestParam(value = "ambContingutVista") boolean ambContingutVista,
			@RequestParam(value = "perSignar") boolean perSignar,
			@RequestParam(value = "perNotificar") boolean perNotificar,
			@RequestParam(value = "ambSegellSignatura") boolean ambSegellSignatura) {
		return workflowBridgeService.getDocumentInfo(
				documentStoreId,
				ambContingutOriginal,
				ambContingutSignat,
				ambContingutVista,
				perSignar,
				perNotificar,
				ambSegellSignatura);
	}

	@RequestMapping(value="/{documentCodi}/codi", method = RequestMethod.GET)
	@ResponseBody
	public String getCodiVariablePerDocumentCodi(@PathVariable("documentCodi") String documentCodi) {
		return workflowBridgeService.getCodiVariablePerDocumentCodi(documentCodi);
	}

	@RequestMapping(value="/{documentStoreId}", method = RequestMethod.GET)
	@ResponseBody
	public ArxiuDto getArxiuPerMostrar(@PathVariable("documentStoreId") Long documentStoreId) {
		return getArxiuPerMostrar(documentStoreId);
	}

	@RequestMapping(value="/{documentCodi}/generar", method = RequestMethod.POST)
	@ResponseBody
	public ArxiuDto documentGenerarAmbPlantilla(
			@PathVariable("documentCodi") String documentCodi,
			@RequestBody DocumentGenerar documentGenerar) {
		return workflowBridgeService.documentGenerarAmbPlantilla(
				documentGenerar.getTaskInstanceId(),
				documentGenerar.getProcessDefinitionId(),
				documentGenerar.getProcessInstanceId(),
				documentCodi,
				documentGenerar.getDataDocument());
	}

	@RequestMapping(value="/{documentCodi}", method = RequestMethod.POST)
	@ResponseBody
	public Long documentExpedientCrear(
			@PathVariable("documentCodi") String documentCodi,
			@RequestBody DocumentCrear documentCrear) {
		return workflowBridgeService.documentExpedientCrear(
				documentCrear.getTaskInstanceId(),
				documentCrear.getProcessInstanceId(),
				documentCodi,
				documentCrear.getDocumentData(),
				documentCrear.isAdjunt(),
				documentCrear.getAdjuntTitol(),
				documentCrear.getArxiuNom(),
				documentCrear.getArxiuContingut());
	}

	@RequestMapping(value="/{documentCodi}/update", method = RequestMethod.POST)
	@ResponseBody
	public Long documentExpedientGuardar(
			@PathVariable("documentCodi") String documentCodi,
			@RequestBody DocumentGuardar documentGuardar) {
		return workflowBridgeService.documentExpedientGuardar(
				documentGuardar.getProcessInstanceId(),
				documentCodi,
				documentGuardar.getData(),
				documentGuardar.getArxiuNom(),
				documentGuardar.getArxiuContingut());
	}

	@RequestMapping(value="/proces/{processInstanceId}/adjuntar", method = RequestMethod.POST)
	@ResponseBody
	public Long documentExpedientAdjuntar(
			@PathVariable("processInstanceId") String processInstanceId,
			@RequestBody DocumentAdjunt adjunt) {
		return workflowBridgeService.documentExpedientAdjuntar(
				processInstanceId,
				adjunt.getAdjuntId(),
				adjunt.getAdjuntTitol(),
				adjunt.getAdjuntData(),
				adjunt.getArxiuNom(),
				adjunt.getArxiuContingut());
	}

	@RequestMapping(value="/{documentStoreId}/registre", method = RequestMethod.POST)
	@ResponseBody
	public void documentExpedientGuardarDadesRegistre(
			@PathVariable("documentStoreId") Long documentStoreId,
			@RequestBody Registre registre) {
		workflowBridgeService.documentExpedientGuardarDadesRegistre(
				documentStoreId,
				registre.getNumero(),
				registre.getData(),
				registre.getOficinaCodi(),
				registre.getOficinaNom(),
				registre.isEntrada());
	}

	@RequestMapping(value="/{documentStoreId}/esborrar", method = RequestMethod.POST)
	@ResponseBody
	public void documentExpedientEsborrar(
			@PathVariable("documentStoreId") Long documentStoreId,
			@RequestBody String processIntanceId) {
		workflowBridgeService.documentExpedientEsborrar(
				processIntanceId,
				documentStoreId);
	}

	@Data
	public class Registre {
		private String numero;
		private Date data;
		private String oficinaCodi;
		private String oficinaNom;
		private boolean entrada;
	}

	@Data
	public class DocumentAdjunt {
		private String adjuntId;
		private String adjuntTitol;
		private Date adjuntData;
		private String arxiuNom;
		private byte[] arxiuContingut;
	}

	@Data
	public static class DocumentCrear {
		private String taskInstanceId;
		private String processInstanceId;
		private String documentCodi;
		private Date documentData;
		private boolean isAdjunt;
		private String adjuntTitol;
		private String arxiuNom;
		private byte[] arxiuContingut;
	}

	@Data
	public class DocumentGuardar {
		private String processInstanceId;
		private Date data;
		private String arxiuNom;
		private byte[] arxiuContingut;
	}

	@Data
	public class DocumentGenerar {
		private String taskInstanceId;
		private String processDefinitionId;
		private String processInstanceId;
		private Date dataDocument;
	}

	private static final Log logger = LogFactory.getLog(DocumentRestController.class);
}
