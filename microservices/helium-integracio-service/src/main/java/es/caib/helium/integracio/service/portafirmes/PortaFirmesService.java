package es.caib.helium.integracio.service.portafirmes;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import es.caib.helium.integracio.domini.portafirmes.PortaFirma;
import es.caib.helium.integracio.domini.portafirmes.PortaFirmesFlux;
import es.caib.helium.integracio.enums.portafirmes.TipusEstat;
import es.caib.helium.integracio.excepcions.portafirmes.PortaFirmesException;

@Service
public interface PortaFirmesService {

	public PortaFirma getByDocumentId(Long documentId) throws PortaFirmesException;
	public PortaFirma getByProcessInstanceId(String processInstanceId) throws PortaFirmesException;
	public PortaFirma getByProcessInstanceIdAndDocumentStoreId(String processInstanceId, Long documentStoreId) throws PortaFirmesException;
	public List<PortaFirma> getByExpedientIdAndEstat(Long expedientId, TipusEstat estat) throws PortaFirmesException;
	
	@Query("from Portasignatures p where p.processInstanceId = :processInstanceId and p.estat not in :estats ")
	public List<PortaFirma> getByProcessInstanceIdAndEstatNotIn(String processInstanceId, List<TipusEstat> estats) throws PortaFirmesException;
	public List<PortaFirma> getPendentsFirmar(String filtre) throws Exception;
	public List<PortaFirma> getPendentsFirmarByProcessInstance(String processInstance) throws Exception;
	public boolean enviarPortaFirmes(@RequestBody PortaFirmesFlux document) throws PortaFirmesException;
	public boolean cancelarEnviament(List<Long> documentId) throws PortaFirmesException;
	public boolean guardar(PortaFirma firma) throws PortaFirmesException;
	
	public PortaFirma processarCallBack(Long documentId, boolean rebutjat, String motiu) throws PortaFirmesException;
}
