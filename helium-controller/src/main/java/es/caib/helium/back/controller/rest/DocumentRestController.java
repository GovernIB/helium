package es.caib.helium.back.controller.rest;

import es.caib.helium.logic.intf.dto.ArxiuDto;
import es.caib.helium.logic.intf.dto.DocumentDissenyDto;
import es.caib.helium.logic.intf.dto.DocumentDto;
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

import java.util.Date;

/**
 * API REST de terminis.
 *
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/bridge/api/documents")
public class DocumentRestController {
	
	private final WorkflowBridgeService workflowBridgeService;

	@GetMapping(value="/{documentCodi}/proces/{processInstanceId}/disseny")
	public ResponseEntity<DocumentDissenyDto> getDocumentDisseny(
			@RequestParam(value = "definicioProcesId") Long definicioProcesId,
			@PathVariable("processInstanceId") String processInstanceId,
			@PathVariable("documentCodi") String documentCodi) {
		return new ResponseEntity<>(
				workflowBridgeService.getDocumentDisseny(
						definicioProcesId,
						processInstanceId,
						documentCodi),
				HttpStatus.OK);
	}

	@GetMapping(value="/{documentStoreId}/info")
	public ResponseEntity<DocumentDto> getDocumentInfo(@PathVariable("documentStoreId") Long documentStoreId){
		return new ResponseEntity<>(
				workflowBridgeService.getDocumentInfo(documentStoreId),
				HttpStatus.OK);
	}

	@GetMapping(value="/{documentStoreId}/infoFiltre")
	public ResponseEntity<DocumentDto> getDocumentInfo(
			@PathVariable("documentStoreId") Long documentStoreId,
			@RequestParam(value = "ambContingutOriginal") boolean ambContingutOriginal,
			@RequestParam(value = "ambContingutSignat") boolean ambContingutSignat,
			@RequestParam(value = "ambContingutVista") boolean ambContingutVista,
			@RequestParam(value = "perSignar") boolean perSignar,
			@RequestParam(value = "perNotificar") boolean perNotificar,
			@RequestParam(value = "ambSegellSignatura") boolean ambSegellSignatura) {
		return new ResponseEntity<>(
				workflowBridgeService.getDocumentInfo(
						documentStoreId,
						ambContingutOriginal,
						ambContingutSignat,
						ambContingutVista,
						perSignar,
						perNotificar,
						ambSegellSignatura),
				HttpStatus.OK);
	}

	@GetMapping(value="/{documentCodi}/codi")
	public ResponseEntity<String> getCodiVariablePerDocumentCodi(@PathVariable("documentCodi") String documentCodi) {
		return new ResponseEntity<>(
				workflowBridgeService.getCodiVariablePerDocumentCodi(documentCodi),
				HttpStatus.OK);
	}

	@GetMapping(value="/{documentStoreId}")
	public ResponseEntity<ArxiuDto> getArxiuPerMostrar(@PathVariable("documentStoreId") Long documentStoreId) {
		return new ResponseEntity<>(
				workflowBridgeService.getArxiuPerMostrar(documentStoreId),
				HttpStatus.OK);
	}

	@PostMapping(value="/{documentCodi}/generar")
	public ResponseEntity<ArxiuDto> documentGenerarAmbPlantilla(
			@PathVariable("documentCodi") String documentCodi,
			@RequestBody DocumentGenerar documentGenerar) {
		return new ResponseEntity<>(
				workflowBridgeService.documentGenerarAmbPlantilla(
						documentGenerar.getTaskInstanceId(),
						documentGenerar.getProcessDefinitionId(),
						documentGenerar.getProcessInstanceId(),
						documentCodi,
						documentGenerar.getDataDocument()),
				HttpStatus.OK);
	}

	@PostMapping(value="/{documentCodi}")
	public ResponseEntity<Long> documentExpedientCrear(
			@PathVariable("documentCodi") String documentCodi,
			@RequestBody DocumentCrear documentCrear) {
		return new ResponseEntity<>(
				workflowBridgeService.documentExpedientCrear(
						documentCrear.getTaskInstanceId(),
						documentCrear.getProcessInstanceId(),
						documentCodi,
						documentCrear.getDocumentData(),
						documentCrear.isAdjunt(),
						documentCrear.getAdjuntTitol(),
						documentCrear.getArxiuNom(),
						documentCrear.getArxiuContingut()),
				HttpStatus.OK);
	}

	@PostMapping(value="/{documentCodi}/update")
	public ResponseEntity<Long> documentExpedientGuardar(
			@PathVariable("documentCodi") String documentCodi,
			@RequestBody DocumentGuardar documentGuardar) throws Exception {
		return new ResponseEntity<>(
				workflowBridgeService.documentExpedientGuardar(
						documentGuardar.getProcessInstanceId(),
						documentCodi,
						documentGuardar.getData(),
						documentGuardar.getArxiuNom(),
						documentGuardar.getArxiuContingut()),
				HttpStatus.OK);
	}

	@PostMapping(value="/proces/{processInstanceId}/adjuntar")
	public ResponseEntity<Long> documentExpedientAdjuntar(
			@PathVariable("processInstanceId") String processInstanceId,
			@RequestBody DocumentAdjunt adjunt) {
		return new ResponseEntity<>(
				workflowBridgeService.documentExpedientAdjuntar(
						processInstanceId,
						adjunt.getAdjuntId(),
						adjunt.getAdjuntTitol(),
						adjunt.getAdjuntData(),
						adjunt.getArxiuNom(),
						adjunt.getArxiuContingut()),
				HttpStatus.OK);
	}

	@PostMapping(value="/{documentStoreId}/registre")
	public ResponseEntity<Void> documentExpedientGuardarDadesRegistre(
			@PathVariable("documentStoreId") Long documentStoreId,
			@RequestBody Registre registre) {
		workflowBridgeService.documentExpedientGuardarDadesRegistre(
				documentStoreId,
				registre.getNumero(),
				registre.getData(),
				registre.getOficinaCodi(),
				registre.getOficinaNom(),
				registre.isEntrada());
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@PostMapping(value="/{documentStoreId}/esborrar")
	public ResponseEntity<Void> documentExpedientEsborrar(
			@PathVariable("documentStoreId") Long documentStoreId,
			@RequestBody String processIntanceId) {
		workflowBridgeService.documentExpedientEsborrar(
				processIntanceId,
				documentStoreId);
		return new ResponseEntity<>(HttpStatus.OK);
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
