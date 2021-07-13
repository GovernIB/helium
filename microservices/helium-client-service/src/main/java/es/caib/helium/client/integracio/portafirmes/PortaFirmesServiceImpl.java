package es.caib.helium.client.integracio.portafirmes;

import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Service;

import es.caib.helium.client.integracio.portafirmes.enums.TipusEstat;
import es.caib.helium.client.integracio.portafirmes.model.PortaFirma;
import es.caib.helium.client.integracio.portafirmes.model.PortaFirmesFlux;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class PortaFirmesServiceImpl implements PortaFirmesService {

	private final String missatgeLog = "Cridant Integracio Service - PortaFirmes - ";
	
	private PortaFirmesFeignClient portaFirmesClient;
	
	@Override
	public PortaFirma getByDocumentId(Long documentId, Long entornId) {
		
		log.debug(missatgeLog + " obtinguent portafirma amb documentId " + documentId + " per l'entorn " + entornId);
		var responseEntity = portaFirmesClient.getByDocumentId(documentId, entornId);
	 	var resultat = Objects.requireNonNull(responseEntity.getBody());
    	return resultat;
	}

	@Override
	public PortaFirma getByProcessInstanceId(String processInstanceId, Long entornId) {
		
		log.debug(missatgeLog + " obtinguent portafirma amb processInstanceId " + processInstanceId + " per l'entorn " + entornId);
		var responseEntity = portaFirmesClient.getByProcessInstanceId(processInstanceId, entornId);
	 	var resultat = Objects.requireNonNull(responseEntity.getBody());
    	return resultat;
	}

	@Override
	public PortaFirma getByProcessInstanceIdAndDocumentStoreId(String processInstanceId, Long documentStoreId, Long entornId) {
		
		log.debug(missatgeLog + " obtinguent portafirma amb processInstanceId " + processInstanceId 
				+ " i documentStoreId " + documentStoreId + " per l'entorn " + entornId);
		var responseEntity = portaFirmesClient.getByProcessInstanceId(processInstanceId, entornId);
	 	var resultat = Objects.requireNonNull(responseEntity.getBody());
    	return resultat;
	}

	@Override
	public List<PortaFirma> getPendentsFirmar(String filtre, Long entornId) {
		
		log.debug(missatgeLog + " obtinguent pendents de firmar amb filtre " + filtre + " per l'entorn " + entornId);
		var responseEntity = portaFirmesClient.getPendentsFirmar(filtre, entornId);
	 	var resultat = Objects.requireNonNull(responseEntity.getBody());
    	return resultat;
	}

	@Override
	public List<PortaFirma> getByExpedientIdAndEstat(Long expedientId, TipusEstat estat, Long entornId) {
		
		log.debug(missatgeLog + " obtinguent amb expedientId " + expedientId + " amb estat " + estat.name() + " per l'entorn " + entornId);
		var responseEntity = portaFirmesClient.getByExpedientIdAndEstat(expedientId, estat, entornId);
	 	var resultat = Objects.requireNonNull(responseEntity.getBody());
    	return resultat;
	}

	@Override
	public List<PortaFirma> getByProcessInstanceIdAndEstatNotIn(String processInstanceId, List<TipusEstat> estats, Long entornId) {
		
		log.debug(missatgeLog + " obtinguent amb processInstanceId " + processInstanceId 
				+ " amb estats " + estats.toString() + " per l'entorn " + entornId);
		var responseEntity = portaFirmesClient.getByProcessInstanceIdAndEstatNotIn(processInstanceId, estats, entornId);
	 	var resultat = Objects.requireNonNull(responseEntity.getBody());
    	return resultat;
	}

	@Override
	public List<PortaFirma> getPendentsFirmarByProcessInstance(String processInstance, Long entornId) {
		
		log.debug(missatgeLog + " obtinguent amb processInstanceId " + processInstance + " per l'entorn " + entornId);
		var responseEntity = portaFirmesClient.getPendentsFirmarByProcessInstance(processInstance, entornId);
	 	var resultat = Objects.requireNonNull(responseEntity.getBody());
    	return resultat;
	}

	@Override
	public void enviarPortaFirmes(PortaFirmesFlux document) {
		
		log.debug(missatgeLog + " enviant al portafirmes el document amb Id" + document.getDocumentId());
		portaFirmesClient.enviarPortaFirmes(document);
	}

	@Override
	public void postPortaFirma(PortaFirma document, Long entornId) {

		log.debug(missatgeLog + " enviant al portafirmes el document amb Id" + document.getDocumentId() + " per l'entorn " + entornId);
		portaFirmesClient.postPortaFirma(document, entornId);
	}

	@Override
	public void cancelarEnviaments(List<Long> documents, Long entornId) {
 
		log.debug(missatgeLog + " cancelar enviaments " + documents.toString() + " per l'entorn " + entornId);
		portaFirmesClient.cancelarEnviaments(documents, entornId);
	}

	@Override
	public Boolean processarDocumentCallBack(Long documentId, boolean rebutjat, String motiu, Long entornId) {
		
		log.debug(missatgeLog + " processar docuemnt callback documentId " + documentId 
				+ " rebutjat " + rebutjat + " motiu " + motiu + " per l'entorn " + entornId);
		var responseEntity = portaFirmesClient.processarDocumentCallBack(documentId, rebutjat, motiu, entornId);
	 	var resultat = Objects.requireNonNull(responseEntity.getBody());
    	return resultat;
	}
}
