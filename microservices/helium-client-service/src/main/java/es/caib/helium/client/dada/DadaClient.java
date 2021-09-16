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
			Consulta body) throws Exception;
	
	List<Expedient> consultaResultatsLlistat(
			Integer entornId,
			Integer expedientTipusId, 
			Consulta consulta) throws Exception;
	
	// Capcalera expedient
	
	Expedient findByExpedientId(Long expedientId) throws Exception;
	
	void crearExpedient(Expedient expedient) throws Exception;

	void crearExpedients(List<Expedient> expedient) throws Exception;
	
	void deleteExpedient(Long expedientId) throws Exception;
	
	void deleteExpedients(List<Long> expedients) throws Exception;
	
	void putExpedient(Expedient expedient, Long expedientId) throws Exception;
	
	void putExpedients(List<Expedient> expedients) throws Exception;
	
	void patchExpedient(Expedient expedient, Long expedientId) throws Exception;
	
	void patchExpedients(List<Expedient> expedients) throws Exception;
	
	// Dades expedient
	
	List<Dada> getDades(Long expedientId) throws Exception;
	
	Dada getDadaByCodi(Long expedientId, String codi) throws Exception;
	
	List<Dada> getDadesByExpedientIdProcesId(Long expedientId, String procesIdId) throws Exception;
	
	Dada getDadaByExpedientIdProcesAndCodi(Long expedientId, String procesIdId, String codi) throws Exception;
	
	Dada getDadaByProcesAndCodi(String procesIdId, String codi) throws Exception;
	
	Long getDadaExpedientIdByProcesId(String procesIdId) throws Exception;
	
	void postDadesByExpedientId(Long expedientId, String procesIdId, List<Dada> dada) throws Exception;
	
	void putDadaByExpedientIdAndCodi(Long expedientId, String codi, Dada dada) throws Exception;
	
	void deleteDadaByExpedientIdAndCodi(Long expedientId, String codi) throws Exception;
	
	void postDadesByExpedientIdProcesId(Long expedientId, String procesIdId, List<Dada> dades) throws Exception;
	
	void putDadaByExpedientIdProcesIdAndCodi(Long expedientId, String procesIdId, String codi, Dada dada) throws Exception;
	
	void deleteDadaByExpedientIdAndProcesIdAndCodi(Long expedientId, String procesIdId, String codi) throws Exception;

	List<Dada> getDadesByProcessInstanceId(String procesId) throws Exception;

	Boolean postDadaByProcesId(String procesId, List<Dada> dades) throws Exception;

	void deleteDadaByProcessInstanceIdAndCodi(String procesId, String codi) throws Exception;

	List<Expedient> findRootProcessInstances(List<String> procesIds) throws Exception;

	Expedient findRootProcessInstances(String procesId) throws Exception;
}
