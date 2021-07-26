package es.caib.helium.client.integracio.arxiu;

import es.caib.helium.client.integracio.arxiu.model.DocumentArxiu;
import es.caib.helium.client.integracio.arxiu.model.Expedient;
import es.caib.helium.client.integracio.arxiu.model.ExpedientArxiu;
import org.springframework.stereotype.Service;

@Service
public interface ArxiuClientService {

	Expedient getExpedientsByUuId(String uuId, Long entornId);
	
	void postExpedient(ExpedientArxiu expedient, Long entornId);

	void putExpedient(ExpedientArxiu expedient, Long entornId);

	void deleteExpedient(String uuId, Long entornId);
	
	void tancarExpedient(String arxiuUuId, Long entornId);
	
	void obrirExpedient(String arxiuUuId, Long entornId);
	
	//TODO EL DOCUMENT ÉS UN OBJECTE BASTANT COMPLEX. VEURE SI PASAR-LO TOT O IMPORTAR
//	Document getDocument(String uuId,
//			String versio,
//			boolean ambContingut,
//			boolean isSignat,
//			Long entornId);
	
	void postDocument(DocumentArxiu document, Long entornId);
	
	void putExpedient(DocumentArxiu document, Long entornId);
	
	void deleteDocument(String uuId, Long entornId);
}
