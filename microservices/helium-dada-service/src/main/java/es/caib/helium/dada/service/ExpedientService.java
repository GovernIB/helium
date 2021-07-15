package es.caib.helium.dada.service;

import es.caib.helium.dada.exception.DadaException;
import es.caib.helium.dada.model.Consulta;
import es.caib.helium.dada.model.Dada;
import es.caib.helium.dada.model.Expedient;
import es.caib.helium.dada.model.PagedList;

import java.util.List;

interface ExpedientService {

	PagedList<Expedient> consultaResultats(Consulta consulta) throws DadaException;

	List<Expedient> consultaResultatsLlistat(Consulta consulta) throws DadaException;

	Expedient findByExpedientId(Long expedientId) throws DadaException;

	boolean createExpedient(Expedient expedient) throws DadaException;
	
	boolean createExpedients(List<Expedient> expedients) throws DadaException;

	boolean deleteExpedient(Long expedientId) throws DadaException;

	boolean deleteExpedients(List<Long> expedientsId) throws DadaException;

	boolean putExpedient(Long expedientId, Expedient expedient) throws DadaException;

	boolean putExpedients(List<Expedient> expedients) throws DadaException;

	boolean patchExpedient(Long expedientId, Expedient expedient) throws DadaException;

	boolean patchExpedients(List<Expedient> expedients) throws DadaException;

	List<Dada> getDades(Long expedientId) throws DadaException;

	Dada getDadaByCodi(Long expedientId, String codi) throws DadaException;

	List<Dada> getDadesByProces(Long expedientId, Long procesId) throws DadaException;

	Dada getDadaByExpedientIdProcesAndCodi(Long expedientId, Long procesId, String codi) throws DadaException;

	Dada getDadaByProcesAndCodi(Long procesId, String codi) throws DadaException;
	
	Long getDadaExpedientIdByProcesId(Long procesId) throws DadaException;

	boolean createDades(Long expedientId, Long procesId, List<Dada> dada) throws DadaException;

	boolean putDadaByExpedientIdAndCodi(Long expedientId, String codi, Dada dada) throws DadaException;

	boolean deleteDadaByExpedientIdAndCodi(Long expedientId, String codi) throws DadaException;

	boolean postDadesByExpedientIdProcesId(Long expedientId, Long procesId, List<Dada> dada) throws DadaException;

	boolean putDadaByExpedientIdProcesIdAndCodi(Long expedientId, Long procesId, String codi, Dada dada) throws DadaException;

	boolean deleteDadaByExpedientIdAndProcesIdAndCodi(Long expedientId, Long procesId, String codi) throws DadaException;

	List<Dada> getDadesByProcesId(Long procesId) throws DadaException;
}
