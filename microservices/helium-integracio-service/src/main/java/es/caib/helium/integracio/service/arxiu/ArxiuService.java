package es.caib.helium.integracio.service.arxiu;

import org.springframework.stereotype.Service;

import es.caib.helium.integracio.domini.arxiu.DocumentArxiu;
import es.caib.helium.integracio.domini.arxiu.ExpedientArxiu;
import es.caib.helium.integracio.excepcions.arxiu.ArxiuServiceException;
import es.caib.plugins.arxiu.api.Document;
import es.caib.plugins.arxiu.api.Expedient;

@Service
public interface ArxiuService {
	
	public Expedient getExpedientByUuId(String uuId) throws ArxiuServiceException;
	public boolean crearExpedient(ExpedientArxiu expedient) throws ArxiuServiceException;
	public boolean modificarExpedient(ExpedientArxiu expedient) throws ArxiuServiceException;
	public boolean deleteExpedient(String uuId) throws ArxiuServiceException;
	public boolean obrirExpedient(String uuId) throws ArxiuServiceException;
	public boolean tencarExpedient(String uuId) throws ArxiuServiceException;
	
	public Document getDocument(String uuId, String versio, boolean ambContingut) throws ArxiuServiceException;
	public boolean crearDocument(DocumentArxiu document) throws ArxiuServiceException;
	public boolean modificarDocument(DocumentArxiu document) throws ArxiuServiceException;
	public boolean deleteDocument(String uuId) throws ArxiuServiceException;
}
