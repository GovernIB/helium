package es.caib.helium.integracio.service.arxiu;

import es.caib.distribucio.backoffice.utils.arxiu.ArxiuPluginListener;
import es.caib.distribucio.backoffice.utils.arxiu.ArxiuResultat;
import es.caib.helium.integracio.domini.arxiu.Anotacio;
import es.caib.helium.integracio.domini.arxiu.ConsultaDocument;
import es.caib.helium.integracio.domini.arxiu.DocumentArxiu;
import es.caib.helium.integracio.domini.arxiu.ExpedientArxiu;
import es.caib.helium.integracio.excepcions.arxiu.ArxiuException;
import es.caib.plugins.arxiu.api.ContingutArxiu;
import es.caib.plugins.arxiu.api.Document;
import es.caib.plugins.arxiu.api.Expedient;
import org.springframework.stereotype.Service;

@Service
public interface ArxiuService {
	
	Expedient getExpedient(String uuId, Long entornId) throws ArxiuException;
	boolean crearExpedient(ExpedientArxiu expedient, Long entornId) throws ArxiuException;
	boolean modificarExpedient(ExpedientArxiu expedient, Long entornId) throws ArxiuException;
	boolean deleteExpedient(String uuId, Long entornId) throws ArxiuException;
	boolean obrirExpedient(String uuId, Long entornId) throws ArxiuException;
	boolean tancarExpedient(String uuId, Long entornId) throws ArxiuException;
	
	Document getDocument(String uuId, ConsultaDocument consulta) throws ArxiuException;
	ContingutArxiu crearDocument(DocumentArxiu document, Long entornId) throws ArxiuException;
	ContingutArxiu modificarDocument(DocumentArxiu document, Long entornId) throws ArxiuException;
	boolean deleteDocument(String uuId, Long entornId) throws ArxiuException;

	ArxiuResultat crearExpedientAmbAnotacioRegistre(
			String arxiuUuId,
			Long entornId,
			ArxiuPluginListener arxiuPluginListener,
			Anotacio anotacio) throws ArxiuException;
}
