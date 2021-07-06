package es.caib.helium.client.dada;

import java.util.List;

import org.springframework.stereotype.Service;

import es.caib.helium.client.dada.model.Consulta;
import es.caib.helium.client.dada.model.Dada;
import es.caib.helium.client.dada.model.Expedient;
import es.caib.helium.client.model.PagedList;

@Service
public interface DataService {

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
	
	List<Dada> getDadesByProces(Long expedientId, Long procesId);
	
	Dada getDadaByExpedientIdProcesAndCodi(Long expedientId, Long procesId, String codi);
	
	Dada getDadaByProcesAndCodi(Long procesId, String codi);
	
	Long getDadaExpedientIdByProcesId(Long procesId);
	
	void postDadesByExpedientId(Long expedientId, Long procesId, List<Dada> dada);
	
	void putDadaByExpedientIdAndCodi(Long expedientId, String codi, Dada dada);
	
	public void deleteDadaByExpedientIdAndCodi(Long expedientId, String codi);
	
	public void postDadesByExpedientIdProcesId(Long expedientId, Long procesId, List<Dada> dades);
	
	public void putDadaByExpedientIdProcesIdAndCodi(Long expedientId, Long procesId, String codi, Dada dada);
	
	public void deleteDadaByExpedientIdAndProcesIdAndCodi(Long expedientId, Long procesId, String codi); 
}
