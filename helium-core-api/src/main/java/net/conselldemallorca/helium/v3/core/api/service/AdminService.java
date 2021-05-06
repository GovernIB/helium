/**
 * 
 */
package net.conselldemallorca.helium.v3.core.api.service;

import java.util.Date;
import java.util.List;
import java.util.Set;

import net.conselldemallorca.helium.v3.core.api.dto.DominiDto;
import net.conselldemallorca.helium.v3.core.api.dto.IntegracioAccioDto;
import net.conselldemallorca.helium.v3.core.api.dto.IntegracioAccioEstatEnumDto;
import net.conselldemallorca.helium.v3.core.api.dto.IntegracioAccioTipusEnumDto;
import net.conselldemallorca.helium.v3.core.api.dto.IntegracioDto;
import net.conselldemallorca.helium.v3.core.api.dto.IntegracioParametreDto;
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
	 * Retorna el llistat de les integracions disponibles del entorn acual.
	 * 
	 * @return la llista d'integracions
	 */
	public List<IntegracioDto> monitorIntegracioFindAllEntronActual();

	/**
	 * Retorna les darreres accions de la integració especificada.
	 * 
	 * @param integracioCodi el codi de la integració a consultar
	 * @return
	 */
	public List<IntegracioAccioDto> monitorIntegracioFindAccionsByIntegracio(
			String integracioCodi);

	/**
	 * Retorna les darreres accions de la integració especificada del entorn actual.
	 * 
	 * @param integracioCodi el codi de la integració a consultar
	 * @return
	 */
	public List<IntegracioAccioDto> monitorIntegracioFindAccionsByIntegracioEntornActual(
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

	/** Mètode per afegir una acció des de la capa de la vista a través del servei
	 * 
	 * @param integracioCodi
	 * @param descripcio
	 * @param tipus
	 * @param tempsResposta
	 * @param parametres
	 */
	public void monitorAddAccio(
			String integracioCodi,
			String descripcio,
			IntegracioAccioTipusEnumDto tipus,
			IntegracioAccioEstatEnumDto estat,
			long tempsResposta,
			String errorDescripcio,
			Throwable throwable,
			List<IntegracioParametreDto> parametres);
	
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

	//TODO: probablement no s'utilitza, esborrar per la 4.0
	public abstract List<ReassignacioDto> llistaReassignacions();

	public abstract void createReassignacio(String usuariOrigen, String usuariDesti, Date dataInici, Date dataFi, Date dataCancelacio, Long tipusExpedientId);

	public abstract void updateReassignacio(Long id, String usuariOrigen, String usuariDesti, Date dataInici, Date dataFi, Date dataCancelacio, Long tipusExpedientId);

	public abstract void deleteReassignacio(Long id);

	public abstract ReassignacioDto findReassignacioById(Long id);
	
	public abstract List<TascaCompleteDto> getTasquesCompletarAdminEntorn();

	/** Mètode per establir des de l'interceptor l'idioma preferit de l'usuari.
	 * 
	 * @param usuari
	 * @param codiIdioma
	 */
	public void setIdiomaPref(String usuari, String idioma);

}
