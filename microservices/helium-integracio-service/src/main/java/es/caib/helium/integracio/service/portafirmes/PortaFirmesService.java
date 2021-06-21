package es.caib.helium.integracio.service.portafirmes;

import java.util.List;

import org.springframework.stereotype.Service;

import es.caib.helium.integracio.domini.portafirmes.PortaFirma;
import es.caib.helium.integracio.domini.portafirmes.PortaFirmesFlux;
import es.caib.helium.integracio.enums.portafirmes.TipusEstat;
import es.caib.helium.integracio.excepcions.portafirmes.PortaFirmesException;

@Service
public interface PortaFirmesService {

	public PortaFirma getByDocumentId(Long documentId, Long entornId) throws PortaFirmesException;
	public PortaFirma getByProcessInstanceId(String processInstanceId, Long entornId) throws PortaFirmesException;
	public PortaFirma getByProcessInstanceIdAndDocumentStoreId(String processInstanceId, Long documentStoreId, Long entornId) throws PortaFirmesException;
	public List<PortaFirma> getByExpedientIdAndEstat(Long expedientId, TipusEstat estat, Long entornId) throws PortaFirmesException;
	
	public List<PortaFirma> getByProcessInstanceIdAndEstatNotIn(String processInstanceId, List<TipusEstat> estats, Long entornId) throws PortaFirmesException;
	public List<PortaFirma> getPendentsFirmar(String filtre, Long entornId) throws Exception;
	public List<PortaFirma> getPendentsFirmarByProcessInstance(String processInstance, Long entornId) throws Exception;
	public boolean enviarPortaFirmes(PortaFirmesFlux document) throws PortaFirmesException;
	public boolean cancelarEnviament(List<Long> documentId, Long entornId) throws PortaFirmesException;
	public boolean guardar(PortaFirma firma, Long entornId) throws PortaFirmesException;
	
	public boolean processarCallBack(Long documentId, boolean rebutjat, String motiu, Long entornId) throws PortaFirmesException;
}
