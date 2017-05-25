/**
 * 
 */
package net.conselldemallorca.helium.v3.core.api.service;

import java.util.Date;
import java.util.List;
import java.util.Set;

import net.conselldemallorca.helium.v3.core.api.dto.DominiDto;
import net.conselldemallorca.helium.v3.core.api.dto.IntegracioAccioDto;
import net.conselldemallorca.helium.v3.core.api.dto.IntegracioDto;
import net.conselldemallorca.helium.v3.core.api.dto.MesuraTemporalDto;
import net.conselldemallorca.helium.v3.core.api.dto.PersonaDto;
import net.conselldemallorca.helium.v3.core.api.dto.ReassignacioDto;
import net.conselldemallorca.helium.v3.core.api.dto.TascaCompleteDto;
import net.conselldemallorca.helium.v3.core.api.dto.UsuariPreferenciesDto;
import net.conselldemallorca.helium.v3.core.api.exception.NoTrobatException;

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
	 * Envia les mètriques per correu-e als responsables de l'aplicació.
	 */
	public void metricsEmailResponsables();

	/**
	 * Retorna el llistat de les integracions disponibles.
	 * 
	 * @return la llista d'integracions
	 */
	public List<IntegracioDto> monitorIntegracioFindAll();

	/**
	 * Retorna les darreres accions de la integració especificada.
	 * 
	 * @param integracioCodi el codi de la integració a consultar
	 * @return
	 */
	public List<IntegracioAccioDto> monitorIntegracioFindAccionsByIntegracio(
			String integracioCodi);

	/**
	 * Retorna la llista de dominis d'un entorn
	 * 
	 * @param entornId l'id del entorn que es vol consultar, si es null
	 * retorna tots els dominis
	 * @return la llista de dominis
	 */
	public List<DominiDto> monitorDominiFindByEntorn(
			Long entornId) throws NoTrobatException;

	/**
	 * Retorna la llista de les darreres accions d'un domini
	 * 
	 * @param dominiId l'id del domini que es vol consultar
	 * @return la llista d'accions
	 */
	public List<IntegracioAccioDto> monitorDominiFindAccionsByDomini(
			Long dominiId) throws NoTrobatException;

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
	public boolean mesuraTemporalIsActive();

	public boolean isStatisticActive();

	public List<MesuraTemporalDto> getHibernateStatistics(
			String familia,
			boolean exportar);
	public List<TascaCompleteDto> getTasquesCompletar();
	public void updatePerfil(UsuariPreferenciesDto preferencies);
	public void updatePersona(PersonaDto persona);

	public abstract void createReassignacio(String usuariOrigen, String usuariDesti, Date dataInici, Date dataFi, Date dataCancelacio, Long tipusExpedientId);

	public abstract void updateReassignacio(Long id, String usuariOrigen, String usuariDesti, Date dataInici, Date dataFi, Date dataCancelacio, Long tipusExpedientId);

	public abstract void deleteReassignacio(Long id);

	public abstract ReassignacioDto findReassignacioById(Long id);

}
