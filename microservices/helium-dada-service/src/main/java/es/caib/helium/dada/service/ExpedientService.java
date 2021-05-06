package es.caib.helium.dada.service;

import java.util.List;

import es.caib.helium.dada.domain.Dada;
import es.caib.helium.dada.domain.Expedient;
import es.caib.helium.dada.model.Consulta;
import es.caib.helium.dada.model.PagedList;

public interface ExpedientService {

	PagedList<Expedient> consultaResultats(Consulta consulta);

	List<Expedient> consultaResultatsLlistat(Consulta consulta);

	public Expedient findByExpedientId(Long expedientId);

	public boolean createExpedient(Expedient expedient);
	
	public boolean createExpedients(List<Expedient> expedients);

	public boolean deleteExpedient(Long expedientId);

	public boolean deleteExpedients(List<Long> expedientsId);

	public boolean putExpedient(Long expedientId, Expedient expedient);

	public boolean putExpedients(List<Expedient> expedients);

	public boolean patchExpedient(Long expedientId, Expedient expedient);

	public boolean patchExpedients(List<Expedient> expedients);

	public List<Dada> getDades(Long expedientId);

	public Dada getDadaByCodi(Long expedientId, String codi);

	public List<Dada> getDadesByProces(Long expedientId, Long procesId);

	public Dada getDadaByProcesAndCodi(Long expedientId, Long procesId, String codi);

	public boolean createDades(Long expedientId, Long procesId, List<Dada> dada);

	public boolean putDadaByExpedientIdAndCodi(Long expedientId, String codi, Dada dada);

	public boolean deleteDadaByExpedientIdAndCodi(Long expedientId, String codi);

	public boolean postDadesByExpedientIdProcesId(Long expedientId, Long procesId, List<Dada> dada);

	public boolean putDadaByExpedientIdProcesIdAndCodi(Long expedientId, Long procesId, String codi, Dada dada);

	public boolean deleteDadaByExpedientIdAndProcesIdAndCodi(Long expedientId, Long procesId, String codi);
}
