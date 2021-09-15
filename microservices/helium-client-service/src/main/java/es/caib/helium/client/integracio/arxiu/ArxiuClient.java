package es.caib.helium.client.integracio.arxiu;

import es.caib.helium.client.integracio.arxiu.model.ConsultaDocument;
import es.caib.helium.client.integracio.arxiu.model.ContingutArxiu;
import es.caib.helium.client.integracio.arxiu.model.Document;
import es.caib.helium.client.integracio.arxiu.model.DocumentArxiu;
import es.caib.helium.client.integracio.arxiu.model.Expedient;
import es.caib.helium.client.integracio.arxiu.model.ExpedientArxiu;
import org.springframework.stereotype.Service;

@Service
public interface ArxiuClient {

	Expedient getExpedientsByUuId(String uuId, Long entornId);
	
	void postExpedient(ExpedientArxiu expedient, Long entornId);

	void putExpedient(ExpedientArxiu expedient, Long entornId);

	void deleteExpedient(String uuId, Long entornId);
	
	void tancarExpedient(String arxiuUuId, Long entornId);
	
	void obrirExpedient(String arxiuUuId, Long entornId);

	Document getDocument(String uuId, ConsultaDocument consulta);
	
	ContingutArxiu postDocument(DocumentArxiu document, Long entornId);
	
	ContingutArxiu putDocument(DocumentArxiu document, Long entornId);
	
	void deleteDocument(String uuId, Long entornId);
}
