package es.caib.helium.integracio.service.arxiu;

import org.springframework.stereotype.Service;

import es.caib.helium.integracio.domini.arxiu.DocumentArxiu;
import es.caib.helium.integracio.domini.arxiu.ExpedientArxiu;
import es.caib.helium.integracio.excepcions.arxiu.ArxiuException;
import es.caib.plugins.arxiu.api.Document;
import es.caib.plugins.arxiu.api.Expedient;

@Service
public interface ArxiuService {
	
	public Expedient getExpedient(String uuId) throws ArxiuException;
	public boolean crearExpedient(ExpedientArxiu expedient) throws ArxiuException;
	public boolean modificarExpedient(ExpedientArxiu expedient) throws ArxiuException;
	public boolean deleteExpedient(String uuId) throws ArxiuException;
	public boolean obrirExpedient(String uuId) throws ArxiuException;
	public boolean tancarExpedient(String uuId) throws ArxiuException;
	
	public Document getDocument(String uuId, String versio, boolean ambContingut, boolean isSignat) throws ArxiuException;
	public boolean crearDocument(DocumentArxiu document) throws ArxiuException;
	public boolean modificarDocument(DocumentArxiu document) throws ArxiuException;
	public boolean deleteDocument(String uuId) throws ArxiuException;
}
