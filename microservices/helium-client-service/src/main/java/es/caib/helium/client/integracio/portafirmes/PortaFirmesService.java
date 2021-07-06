package es.caib.helium.client.integracio.portafirmes;

import java.util.List;

import es.caib.helium.client.integracio.portafirmes.enums.TipusEstat;
import es.caib.helium.client.integracio.portafirmes.model.PortaFirma;
import es.caib.helium.client.integracio.portafirmes.model.PortaFirmesFlux;

public interface PortaFirmesService {
	
	public PortaFirma getByDocumentId(Long documentId, Long entornId);
	
	public PortaFirma getByProcessInstanceId(String processInstanceId, Long entornId);
	
	public PortaFirma getByProcessInstanceIdAndDocumentStoreId(String processInstanceId, Long documentStoreId, Long entornId);
	
	public List<PortaFirma> getPendentsFirmar(String filtre, Long entornId);
	
	public List<PortaFirma> getByExpedientIdAndEstat(Long expedientId, TipusEstat estat, Long entornId);
	
	public List<PortaFirma> getByProcessInstanceIdAndEstatNotIn(String processInstanceId, List<TipusEstat> estats, Long entornId);
	
	public List<PortaFirma> getPendentsFirmarByProcessInstance(String processInstance, Long entornId);
	
	public void enviarPortaFirmes(PortaFirmesFlux document); 
	
	public void postPortaFirma(PortaFirma document, Long entornId);
	
	public void cancelarEnviaments(List<Long> documents, Long entornId);
	
	public Boolean processarDocumentCallBack(Long documentId, boolean rebutjat, String motiu, Long entornId);
}
