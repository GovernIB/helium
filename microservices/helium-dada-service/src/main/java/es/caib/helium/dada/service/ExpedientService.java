package es.caib.helium.dada.service;

import java.util.List;

import es.caib.helium.dada.exception.DadaException;
import es.caib.helium.dada.model.Consulta;
import es.caib.helium.dada.model.Dada;
import es.caib.helium.dada.model.Expedient;
import es.caib.helium.dada.model.PagedList;

public interface ExpedientService {

	PagedList<Expedient> consultaResultats(Consulta consulta) throws DadaException;

	List<Expedient> consultaResultatsLlistat(Consulta consulta) throws DadaException;

	public Expedient findByExpedientId(Long expedientId) throws DadaException;

	public boolean createExpedient(Expedient expedient) throws DadaException;
	
	public boolean createExpedients(List<Expedient> expedients) throws DadaException;

	public boolean deleteExpedient(Long expedientId) throws DadaException;

	public boolean deleteExpedients(List<Long> expedientsId) throws DadaException;

	public boolean putExpedient(Long expedientId, Expedient expedient) throws DadaException;

	public boolean putExpedients(List<Expedient> expedients) throws DadaException;

	public boolean patchExpedient(Long expedientId, Expedient expedient) throws DadaException;

	public boolean patchExpedients(List<Expedient> expedients) throws DadaException;

	public List<Dada> getDades(Long expedientId) throws DadaException;

	public Dada getDadaByCodi(Long expedientId, String codi) throws DadaException;

	public List<Dada> getDadesByProces(Long expedientId, Long procesId) throws DadaException;

	public Dada getDadaByExpedientIdProcesAndCodi(Long expedientId, Long procesId, String codi) throws DadaException;

	public Dada getDadaByProcesAndCodi(Long procesId, String codi) throws DadaException;
	
	public Long getDadaExpedientIdByProcesId(Long procesId) throws DadaException;

	public boolean createDades(Long expedientId, Long procesId, List<Dada> dada) throws DadaException;

	public boolean putDadaByExpedientIdAndCodi(Long expedientId, String codi, Dada dada) throws DadaException;

	public boolean deleteDadaByExpedientIdAndCodi(Long expedientId, String codi) throws DadaException;

	public boolean postDadesByExpedientIdProcesId(Long expedientId, Long procesId, List<Dada> dada) throws DadaException;

	public boolean putDadaByExpedientIdProcesIdAndCodi(Long expedientId, Long procesId, String codi, Dada dada) throws DadaException;

	public boolean deleteDadaByExpedientIdAndProcesIdAndCodi(Long expedientId, Long procesId, String codi) throws DadaException;
}
