package es.caib.helium.client.dada;

import es.caib.helium.client.dada.model.Consulta;
import es.caib.helium.client.dada.model.Dada;
import es.caib.helium.client.dada.model.Expedient;
import es.caib.helium.client.model.PagedList;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface DadaClient {

	// Consultes filtrades
	
	PagedList<Expedient> consultaResultats(
			Integer entornId, 
			Integer expedientTipusId, 
			Integer page, 
			Integer size, 
			Consulta body);
	
	List<Expedient> consultaResultatsLlistat(
			Integer entornId,
			Integer expedientTipusId, 
			Consulta consulta);
	
	// Capcalera expedient
	
	Expedient findByExpedientId(Long expedientId);
	
	void crearExpedient(Expedient expedient);

	void crearExpedients(List<Expedient> expedient);
	
	void deleteExpedient(Long expedientId);
	
	void deleteExpedients(List<Long> expedients);
	
	void putExpedient(Expedient expedient, Long expedientId);
	
	void putExpedients(List<Expedient> expedients); 
	
	void patchExpedient(Expedient expedient, Long expedientId);
	
	void patchExpedients(List<Expedient> expedients);
	
	// Dades expedient
	
	List<Dada> getDades(Long expedientId);
	
	Dada getDadaByCodi(Long expedientId, String codi);
	
	List<Dada> getDadesByProces(Long expedientId, String procesIdId);
	
	Dada getDadaByExpedientIdProcesAndCodi(Long expedientId, String procesIdId, String codi);
	
	Dada getDadaByProcesAndCodi(String procesIdId, String codi);
	
	Long getDadaExpedientIdByProcesId(String procesIdId);
	
	void postDadesByExpedientId(Long expedientId, String procesIdId, List<Dada> dada);
	
	void putDadaByExpedientIdAndCodi(Long expedientId, String codi, Dada dada);
	
	public void deleteDadaByExpedientIdAndCodi(Long expedientId, String codi);
	
	public void postDadesByExpedientIdProcesId(Long expedientId, String procesIdId, List<Dada> dades);
	
	public void putDadaByExpedientIdProcesIdAndCodi(Long expedientId, String procesIdId, String codi, Dada dada);
	
	public void deleteDadaByExpedientIdAndProcesIdAndCodi(Long expedientId, String procesIdId, String codi); 
}
