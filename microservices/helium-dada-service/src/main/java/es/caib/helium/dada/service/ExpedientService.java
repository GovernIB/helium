package es.caib.helium.dada.service;

import java.util.List;

import es.caib.helium.dada.domain.Dada;
import es.caib.helium.dada.domain.ExpedientCapcalera;
import es.caib.helium.dada.model.PagedList;

public interface ExpedientService {
	
	PagedList<Dada> consultaResultats(int page, int size);
 		
	public ExpedientCapcalera findByExpedientId(Long expedientId);
	public void createExpedient(ExpedientCapcalera expedient);
	public void deleteExpedient(Long expedientId);
	public void putExpedient(Long expedientId, ExpedientCapcalera expedient);
	public void patchExpedient(Long expedientId, ExpedientCapcalera expedient);
	
	public List<Dada> getDades(Long expedientId);
	public Dada getDadaByCodi(Long expedientId, String codi);
	public List<Dada> getDadesByProces(Long expedientId, Long procesId);
	public Dada getDadaByProcesAndCodi(Long expedientId, Long procesId, String codi);
	public void createDades(Long expedientId, Long procesId, List<Dada> dada);
	public void putDadaByExpedientIdAndCodi(Long expedientId, String codi, Dada dada);
	public void deleteDadaByExpedientIdAndCodi(Long expedientId, String codi);
	public void postDadesByExpedientIdProcesId(Long expedientId, Long procesId, List<Dada> dada);
	public void putDadaByExpedientIdProcesIdAndCodi(Long expedientId, Long procesId, String codi, Dada dada);
	public void deleteDadaByExpedientIdAndProcesIdAndCodi(Long expedientId, Long procesId, String codi);
}
