/**
 * 
 */
package net.conselldemallorca.helium.v3.core.api.service;

import java.util.Date;
import java.util.List;
import java.util.Set;

import net.conselldemallorca.helium.v3.core.api.dto.MesuraTemporalDto;
import net.conselldemallorca.helium.v3.core.api.dto.PersonaDto;
import net.conselldemallorca.helium.v3.core.api.dto.ReassignacioDto;
import net.conselldemallorca.helium.v3.core.api.dto.TascaCompleteDto;
import net.conselldemallorca.helium.v3.core.api.dto.UsuariPreferenciesDto;

/**
 * Servei per a enllaçar les llibreries jBPM 3 amb la funcionalitat
 * de Helium.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface AdminService {

	/**
	 * Retorna un string en format JSON amb les mètriques de l'aplicació.
	 * 
	 * @return l'string amb les mètriques
	 */
	public String getMetrics();

	/**
	 * Consulta les mesures temporals per família.
	 * 
	 * @param familia la familia per a la consulta (pot ser null)
	 * @param ambDetall indica si el resultat es retorna amb més detalls
	 * @return el llistat de mesures temporals
	 */
	public List<MesuraTemporalDto> mesuraTemporalFindByFamilia(
			String familia,
			boolean ambDetall);

	/**
	 * Retorna la llista de mesures temporals dels tipus d'expedient.
	 * @return el llistat de mesures temporals
	 */
	public List<MesuraTemporalDto> mesuraTemporalFindByTipusExpedient();

	/**
	 * Retorna la llista de mesures temporals dels tipus d'expedient.
	 * 
	 * @return el llistat de mesures temporals
	 */
	public List<MesuraTemporalDto> mesuraTemporalFindByTasca();

	/**
	 * Retorna el llistat de famílies de les mesures temporals.
	 * 
	 * @return el llistat de famílies
	 */
	public Set<String> mesuraTemporalFindFamiliesAll();


	public void mesuraTemporalIniciar(
			String clau,
			String familia);
	public void mesuraTemporalIniciar(
			String clau,
			String familia,
			String tipusExpedient);
	public void mesuraTemporalIniciar(
			String clau,
			String familia,
			String tipusExpedient,
			String tasca,
			String detall);
	public void mesuraTemporalCalcular(
			String clau,
			String familia,
			String tipusExpedient,
			String tasca,
			String detall);
	public void mesuraTemporalCalcular(
			String clau,
			String familia,
			String tipusExpedient);
	public void mesuraTemporalCalcular(
			String clau,
			String familia);
	public boolean isStatisticActive();

	public List<MesuraTemporalDto> getHibernateStatistics(
			String familia,
			boolean exportar);
	public List<TascaCompleteDto> getTasquesCompletar();
	public void updatePerfil(UsuariPreferenciesDto preferencies);
	public void updatePersona(PersonaDto persona);

	public abstract List<ReassignacioDto> llistaReassignacions();

	public abstract void createReassignacio(String usuariOrigen, String usuariDesti, Date dataInici, Date dataFi, Date dataCancelacio, Long tipusExpedientId);

	public abstract void updateReassignacio(Long id, String usuariOrigen, String usuariDesti, Date dataInici, Date dataFi, Date dataCancelacio, Long tipusExpedientId);

	public abstract void deleteReassignacio(Long id);

	public abstract ReassignacioDto findReassignacioById(Long id);

}
