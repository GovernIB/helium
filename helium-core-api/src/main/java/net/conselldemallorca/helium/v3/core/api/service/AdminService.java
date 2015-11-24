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
	 * Obté les mesures temporals per a evaluar el rendiment
	 * de l'aplicació al efectuar determinades tasques.
	 * 
	 * @return El llistat de mesures.
	 */
	public List<MesuraTemporalDto> findMesuresTemporals(String familia, boolean ambDetall);
	public List<MesuraTemporalDto> findMesuresTemporalsTipusExpedient();
	public List<MesuraTemporalDto> findMesuresTemporalsTasca();

	public Set<String> findFamiliesMesuresTemporals();
	public Object getMesuresTemporalsHelper();
	public void mesuraIniciar(String nom, String familia, String tipusExpedient, String tasca, String detall);
	public void mesuraIniciar(String nom, String familia, String tipusExpedient);
	public void mesuraIniciar(String clau, String familia);
	public void mesuraCalcular(String nom, String familia, String tipusExpedient, String tasca, String detall);
	public void mesuraCalcular(String nom, String familia, String tipusExpedient);
	public void mesuraCalcular(String clau, String familia);
	public boolean isStatisticActive();
	public List<MesuraTemporalDto> getHibernateStatistics(String familia, boolean exportar);
	public List<TascaCompleteDto> getTasquesCompletar();
	public void updatePerfil(UsuariPreferenciesDto preferencies);
	public void updatePersona(PersonaDto persona);

	public abstract List<ReassignacioDto> llistaReassignacions();

	public abstract void createReassignacio(String usuariOrigen, String usuariDesti, Date dataInici, Date dataFi, Date dataCancelacio, Long tipusExpedientId);

	public abstract void updateReassignacio(Long id, String usuariOrigen, String usuariDesti, Date dataInici, Date dataFi, Date dataCancelacio, Long tipusExpedientId);

	public abstract void deleteReassignacio(Long id);

	public abstract ReassignacioDto findReassignacioById(Long id);

	public PersonaDto findPersonaAmbCodi(String codi);

	public List<PersonaDto> findPersonaLikeNomSencer(String text);
}
