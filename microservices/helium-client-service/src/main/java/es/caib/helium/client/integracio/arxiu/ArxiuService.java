package es.caib.helium.client.integracio.arxiu;

import org.springframework.stereotype.Service;

import es.caib.helium.client.integracio.arxiu.model.DocumentArxiu;
import es.caib.helium.client.integracio.arxiu.model.Expedient;
import es.caib.helium.client.integracio.arxiu.model.ExpedientArxiu;

@Service
public interface ArxiuService {

	public Expedient getExpedientsByUuId(String uuId, Long entornId); 
	
	public void postExpedient(ExpedientArxiu expedient, Long entornId);

	public void putExpedient(ExpedientArxiu expedient, Long entornId);

	public void deleteExpedient(String uuId, Long entornId);
	
	public void tancarExpedient(String arxiuUuId, Long entornId);
	
	public void obrirExpedient(String arxiuUuId, Long entornId);
	
	//TODO EL DOCUMENT ÉS UN OBJECTE BASTANT COMPLEXT. VEURE SI PASAR-LO TOT O IMPORTAR
//	public Document getDocument(String uuId,
//			String versio,
//			boolean ambContingut,
//			boolean isSignat,
//			Long entornId);
	
	public void postDocument(DocumentArxiu document, Long entornId);
	
	public void putExpedient(DocumentArxiu document, Long entornId);
	
	public void deleteDocument(String uuId, Long entornId);
}
