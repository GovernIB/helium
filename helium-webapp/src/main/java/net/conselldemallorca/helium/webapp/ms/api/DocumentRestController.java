package net.conselldemallorca.helium.webapp.ms.api;

import net.conselldemallorca.helium.core.api.WorkflowBridgeService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * API REST de terminis.
 *
 */
@Controller
@RequestMapping("/api/documents")
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

	private static final Log logger = LogFactory.getLog(DocumentRestController.class);
}
