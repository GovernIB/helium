package es.caib.helium.client.integracio.arxiu;

import es.caib.helium.client.integracio.arxiu.model.ConsultaDocument;
import es.caib.helium.client.integracio.arxiu.model.ContingutArxiu;
import es.caib.helium.client.integracio.arxiu.model.Document;
import es.caib.helium.client.integracio.arxiu.model.DocumentArxiu;
import es.caib.helium.client.integracio.arxiu.model.Expedient;
import es.caib.helium.client.integracio.arxiu.model.ExpedientArxiu;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class ArxiuClientImpl implements ArxiuClient {
	
	private final String missatgeLog = "Cridant Integracio Service - Arxiu - ";
	
	private final ArxiuFeignClient arxiuClient;

	@Override
	public Expedient getExpedientsByUuId(String uuId, Long entornId) {
		
		log.debug(missatgeLog + " Obtenint expedient segons uuId:" + uuId + " per l'entorn " + entornId);
		return arxiuClient.getExpedientByUuId(uuId, entornId).getBody();
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

		log.debug(missatgeLog + " obrint expedient amb arxiuUuId: " + arxiuUuId + " per l'entorn " + entornId);
		arxiuClient.tancarExpedient(arxiuUuId, entornId);
	}

	@Override
	public Document getDocument(String uuId, ConsultaDocument consulta) {

		log.debug(missatgeLog + " obtinguent document per l'entorn " + consulta.getEntornId()
				+ "-  uuId " + uuId
				+ " versio " + consulta.getVersio()
				+ " ambContingut " + consulta.isAmbContingut()
				+ " isSignat " + consulta.isSignat());
		var response = arxiuClient.getDocument(uuId, consulta);
		return response != null ? response.getBody() : null;
	}

	@Override
	public ContingutArxiu postDocument(DocumentArxiu document, Long entornId) {

		log.debug(missatgeLog + " guardant document per l'entorn " + entornId + "-  Document: " + document.toString());
		var response = arxiuClient.postDocument(document, entornId);
		return response != null ? response.getBody() : null;
	}

	@Override
	public ContingutArxiu putDocument(DocumentArxiu document, Long entornId) {
		
		log.debug(missatgeLog + " modificant document per l'entorn " + entornId + "-  Document: " + document.toString());
		var response = arxiuClient.putDocument(document, entornId);
		return response != null ? response.getBody() : null;
	}

	@Override
	public void deleteDocument(String uuId, Long entornId) {
		
		log.debug(missatgeLog + " esborrant document amb uuId " + uuId + " per l'entorn " + entornId);
		arxiuClient.deleteDocument(uuId, entornId);
	}
}
