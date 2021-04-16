package es.caib.helium.dada.service;

import java.util.List;
import java.util.Map;

import es.caib.helium.dada.domain.Dada;
import es.caib.helium.dada.domain.Expedient;
import es.caib.helium.dada.model.Filtre;
import es.caib.helium.dada.model.Ordre;
import es.caib.helium.dada.model.PagedList;

public interface ExpedientService {

	PagedList<Expedient> consultaResultats(Map<String, Filtre> filtreValors, Integer entornId, Integer expedientTipusId,
			List<Ordre> ordre, Integer page, Integer size);

	List<Expedient> consultaResultats(Map<String, Filtre> filtreValors, Integer entornId, Integer expedientTipusId,
			List<Ordre> ordre);

	public Expedient findByExpedientId(Long expedientId);

	public void createExpedient(Expedient expedient);
	
	public void createExpedients(List<Expedient> expedients);

	public boolean deleteExpedient(Long expedientId);

	public void deleteExpedients(List<Long> expedientsId);

	public boolean putExpedient(Long expedientId, Expedient expedient);

	public void putExpedients(List<Expedient> expedients);

	public boolean patchExpedient(Long expedientId, Expedient expedient);

	public void patchExpedients(List<Expedient> expedients);

	public List<Dada> getDades(Long expedientId);

	public Dada getDadaByCodi(Long expedientId, String codi);

	public List<Dada> getDadesByProces(Long expedientId, Long procesId);

	public Dada getDadaByProcesAndCodi(Long expedientId, Long procesId, String codi);

	public void createDades(Long expedientId, Long procesId, List<Dada> dada);

	public boolean putDadaByExpedientIdAndCodi(Long expedientId, String codi, Dada dada);

	public boolean deleteDadaByExpedientIdAndCodi(Long expedientId, String codi);

	public void postDadesByExpedientIdProcesId(Long expedientId, Long procesId, List<Dada> dada);

	public boolean putDadaByExpedientIdProcesIdAndCodi(Long expedientId, Long procesId, String codi, Dada dada);

	public boolean deleteDadaByExpedientIdAndProcesIdAndCodi(Long expedientId, Long procesId, String codi);
}
