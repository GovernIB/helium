package es.caib.helium.client.integracio.arxiu;

import es.caib.helium.client.integracio.arxiu.model.DocumentArxiu;
import es.caib.helium.client.integracio.arxiu.model.Expedient;
import es.caib.helium.client.integracio.arxiu.model.ExpedientArxiu;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@Slf4j
@RequiredArgsConstructor
public class ArxiuClientImpl implements ArxiuClient {
	
	private final String missatgeLog = "Cridant Integracio Service - Arxiu - ";
	
	private final ArxiuFeignClient arxiuClient;

	@Override
	public Expedient getExpedientsByUuId(String uuId, Long entornId) {
		
		log.debug(missatgeLog + " Obtenint expedient segons uuId:" + uuId + " per l'entorn " + entornId);
		var responseEntity = arxiuClient.getExpedientsByUuId(uuId, entornId);
	 	var resultat = Objects.requireNonNull(responseEntity.getBody());
    	return resultat;
	}

	@Override
	public void postExpedient(ExpedientArxiu expedient, Long entornId) {
		// TODO Auto-generated method stub
		log.debug(missatgeLog + " guardant expedient l'entorn " + entornId + "-  Expedient: " + expedient.toString());;
		arxiuClient.postExpedient(expedient, entornId);
	}

	@Override
	public void putExpedient(ExpedientArxiu expedient, Long entornId) {
		
		log.debug(missatgeLog + " modificant expedient per l'entorn " + entornId + "-  Expedient: " + expedient.toString());;
		arxiuClient.putExpedient(expedient, entornId);
	}

	@Override
	public void deleteExpedient(String uuId, Long entornId) {

		log.debug(missatgeLog + " esborrant expedient amb uuId: " + uuId + " per l'entorn " + entornId);
		arxiuClient.deleteExpedient(uuId, entornId);
	}

	@Override
	public void tancarExpedient(String arxiuUuId, Long entornId) {
		// TODO Auto-generated method stub
		log.debug(missatgeLog + " tancant expedient amb arxiuUuId: " + arxiuUuId + " per l'entorn " + entornId);
		arxiuClient.tancarExpedient(arxiuUuId, entornId);
	}

	@Override
	public void obrirExpedient(String arxiuUuId, Long entornId) {
		// TODO Auto-generated method stub
		
		log.debug(missatgeLog + " obrint expedient amb arxiuUuId: " + arxiuUuId + " per l'entorn " + entornId);
		arxiuClient.tancarExpedient(arxiuUuId, entornId);
	}

	@Override
	public void postDocument(DocumentArxiu document, Long entornId) {
		// TODO Auto-generated method stub
		
		log.debug(missatgeLog + " guardant document per l'entorn " + entornId + "-  Document: " + document.toString());
		arxiuClient.postDocument(document, entornId);
	}

	@Override
	public void putExpedient(DocumentArxiu document, Long entornId) {
		
		log.debug(missatgeLog + " modificant document per l'entorn " + entornId + "-  Document: " + document.toString());
		arxiuClient.putDocument(document, entornId);
	}

	@Override
	public void deleteDocument(String uuId, Long entornId) {
		
		log.debug(missatgeLog + " esborrant document amb uuId " + uuId + " per l'entorn " + entornId);
		arxiuClient.deleteDocument(uuId, entornId);
	}

}
