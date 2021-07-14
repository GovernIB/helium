package es.caib.helium.client.integracio.custodia;

import java.util.List;
import java.util.Objects;

import es.caib.helium.client.model.RespostaValidacioSignatura;
import org.springframework.stereotype.Service;

import es.caib.helium.client.integracio.custodia.model.CustodiaRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class CustodiaClientServiceImpl implements CustodiaClientService {
	
	private final String missatgeLog = "Cridant Integracio Service - Custodia - ";
	
	private CustodiaFeignClient custodiaClient;

	@Override
	public String afegirSignatura(CustodiaRequest request, Long entornId) {
		
		log.debug(missatgeLog + " afegint signatura per l'entorn " + entornId + " amb documentId " + request.getDocumentId());
		var responseEntity = custodiaClient.afegirSignatura(request, entornId);
		var resultat = Objects.requireNonNull(responseEntity.getBody());
    	return resultat;
	}

	@Override
	public List<byte[]> getSignatures(String documentId, Long entornId) {
		
		log.debug(missatgeLog + " obtenint signatures per l'entorn " + entornId + " amb documentId " + documentId);
		var responseEntity = custodiaClient.getSignatures(documentId, entornId);
		var resultat = Objects.requireNonNull(responseEntity.getBody());
    	return resultat;
	}

	@Override
	public void deleteSignatures(String documentId, Long entornId) {
		
		log.debug(missatgeLog + " esborrant signatura per l'entorn " + entornId + " amb documentId " + documentId);
		custodiaClient.deleteSignatures(documentId, entornId);
	}

	@Override
	public byte[] getSignaturesAmbArxiu(String documentId, Long entornId) {

		log.debug(missatgeLog + " obtenint signatures amb arxiu per l'entorn " + entornId + " amb documentId " + documentId);
		var responseEntity = custodiaClient.getSignaturesAmbArxiu(documentId, entornId);
		var resultat = Objects.requireNonNull(responseEntity.getBody());
    	return resultat;
	}

	@Override
	public List<RespostaValidacioSignatura> getDadesValidacioSignatura(String documentId, Long entornId) {

		log.debug(missatgeLog + " afegint signatura per l'entorn " + entornId + " amb documentId " + documentId);
		var responseEntity = custodiaClient.getDadesValidacioSignatura(documentId, entornId);
		var resultat = Objects.requireNonNull(responseEntity.getBody());
		return resultat;
	}

	@Override
	public String getUrlComprovacioSignatures(String documentId, Long entornId) {
		
		log.debug(missatgeLog + " obtenint url de comprovacio signatures per l'entorn " + entornId + " amb documentId " + documentId);
		var responseEntity = custodiaClient.getUrlComprovacioSignatures(documentId, entornId);
		var resultat = Objects.requireNonNull(responseEntity.getBody());
    	return resultat;
	}

}
