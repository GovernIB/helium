package es.caib.helium.client.integracio.portafirmes;

import java.util.List;

import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import es.caib.helium.client.integracio.portafirmes.enums.TipusEstat;
import es.caib.helium.client.integracio.portafirmes.model.PortaFirma;
import es.caib.helium.client.integracio.portafirmes.model.PortaFirmesFlux;


public interface PortaFirmesFeignClient {

	@RequestMapping(method = RequestMethod.GET, value = PortaFirmesPath.GET_BY_DOCUMENT_ID)
	public ResponseEntity<PortaFirma> getByDocumentId(
			@PathVariable("documentId") Long documentId, 
			@RequestParam("entornId") Long entornId);
	
	@RequestMapping(method = RequestMethod.GET, value = PortaFirmesPath.GET_BY_PROCESS_INSTANCE_ID)
	public ResponseEntity<PortaFirma> getByProcessInstanceId(
			@PathVariable("processInstanceId") String processInstanceId, 
			@RequestParam("entornId") Long entornId);
	
	@RequestMapping(method = RequestMethod.GET, value = PortaFirmesPath.GET_BY_PROCESS_INSTANCE_ID_AND_DOCUMENT_STORE_ID)
	public ResponseEntity<PortaFirma> getByProcessInstanceIdAndDocumentStoreId(
			@PathVariable("processInstanceId") String processInstanceId, 
			@PathVariable Long documentStoreId, 
			@RequestParam("entornId") Long entornId);
	
	@RequestMapping(method = RequestMethod.GET, value = PortaFirmesPath.GET_PENDENTS_FIRMAR)
	public ResponseEntity<List<PortaFirma>> getPendentsFirmar(
			@RequestParam("filtre") String filtre, 
			@RequestParam("entornId") Long entornId);
	
	@RequestMapping(method = RequestMethod.GET, value = PortaFirmesPath.GET_BY_EXPEDIENT_ID_AND_ESTAT)
	public ResponseEntity<List<PortaFirma>> getByExpedientIdAndEstat(
			@PathVariable("expedientId") Long expedientId, 
			@PathVariable("estat") TipusEstat estat,
			@RequestParam("entornId") Long entornId);
	
	@RequestMapping(method = RequestMethod.GET, value = PortaFirmesPath.GET_BY_PROCESS_INSTANCE_ID_AND_ESTAT_NOT_IN)
	public ResponseEntity<List<PortaFirma>> getByProcessInstanceIdAndEstatNotIn(
			@PathVariable("processInstanceId") String processInstanceId, 
			@RequestParam("estats") List<TipusEstat> estats,
			@RequestParam("entornId") Long entornId);
	
	@RequestMapping(method = RequestMethod.GET, value = PortaFirmesPath.GET_PENDENTS_FIRMAR_BY_PROCESINSTANCE)
	public ResponseEntity<List<PortaFirma>> getPendentsFirmarByProcessInstance(
			@PathVariable("processInstance") String processInstance, 
			@RequestParam("entornId") Long entornId);
	
	@RequestMapping(method = RequestMethod.POST, value = PortaFirmesPath.ENVIAR_PORTA_FIRMES)
	public ResponseEntity<Void> enviarPortaFirmes(
			@Valid @RequestBody PortaFirmesFlux document); 
	
	@RequestMapping(method = RequestMethod.POST, value = PortaFirmesPath.POST_PORTA_FIRMA)
	public ResponseEntity<Void> postPortaFirma(
			@Valid @RequestBody PortaFirma document,
			@RequestParam("entornId") Long entornId);
	
	@RequestMapping(method = RequestMethod.DELETE, value = PortaFirmesPath.CANCELAR_ENVIAMENTS)
	public ResponseEntity<Void> cancelarEnviaments(
			@RequestParam("documents") List<Long> documents, 
			@RequestParam("entornId") Long entornId);
	
	@RequestMapping(method = RequestMethod.GET, value = PortaFirmesPath.PROCESSAR_DOCUMENT_CALL_BACK)
	public ResponseEntity<Boolean> processarDocumentCallBack(
			@PathVariable("documentId") Long documentId,
			@RequestParam("rebutjat") boolean rebutjat, 
			@RequestParam("motiuRebuig") String motiu,
			@RequestParam("entornId") Long entornId);
}
