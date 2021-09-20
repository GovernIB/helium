package es.caib.helium.client.dada.dades;

import es.caib.helium.client.dada.dades.model.Consulta;
import es.caib.helium.client.dada.dades.model.Dada;
import es.caib.helium.client.dada.dades.model.Expedient;
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
			Consulta body) throws Exception;
	
	List<Expedient> consultaResultatsLlistat(
			Integer entornId,
			Integer expedientTipusId, 
			Consulta consulta) throws Exception;
	
	// Capcalera expedient
	
	Expedient findByExpedientId(Long expedientId) throws Exception;
	
	void crearExpedient(Expedient expedient);

	void crearExpedients(List<Expedient> expedient);
	
	void deleteExpedient(Long expedientId);
	
	void deleteExpedients(List<Long> expedients);
	
	void putExpedient(Expedient expedient, Long expedientId);
	
	void putExpedients(List<Expedient> expedients);
	
	void patchExpedient(Expedient expedient, Long expedientId);
	
	void patchExpedients(List<Expedient> expedients);
	
	// Dades expedient
	
	List<Dada> getDades(Long expedientId) throws Exception;
	
	Dada getDadaByCodi(Long expedientId, String codi) throws Exception;
	
	List<Dada> getDadesByExpedientIdProcesId(Long expedientId, String procesIdId) throws Exception;
	
	Dada getDadaByExpedientIdProcesAndCodi(Long expedientId, String procesIdId, String codi) throws Exception;
	
	Dada getDadaByProcesAndCodi(String procesIdId, String codi);
	
	Long getDadaExpedientIdByProcesId(String procesIdId);
	
	void postDadesByExpedientId(Long expedientId, String procesIdId, List<Dada> dada);
	
	void putDadaByExpedientIdAndCodi(Long expedientId, String codi, Dada dada);
	
	void deleteDadaByExpedientIdAndCodi(Long expedientId, String codi);
	
	void postDadesByExpedientIdProcesId(Long expedientId, String procesIdId, List<Dada> dades);
	
	void putDadaByExpedientIdProcesIdAndCodi(Long expedientId, String procesIdId, String codi, Dada dada);
	
	void deleteDadaByExpedientIdAndProcesIdAndCodi(Long expedientId, String procesIdId, String codi);

	List<Dada> getDadesByProcessInstanceId(String procesId) throws Exception;

	Boolean postDadaByProcesId(String procesId, List<Dada> dades) throws Exception;

	void deleteDadaByProcessInstanceIdAndCodi(String procesId, String codi) throws Exception;

	List<Expedient> findRootProcessInstances(List<String> procesIds) throws Exception;

	Expedient findRootProcessInstances(String procesId) throws Exception;
}
