/**
 * 
 */
package net.conselldemallorca.helium.v3.core.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import net.conselldemallorca.helium.core.model.hibernate.Persona;
import net.conselldemallorca.helium.core.model.hibernate.UsuariPreferencies;
import net.conselldemallorca.helium.core.model.service.MesuresTemporalsHelper;
import net.conselldemallorca.helium.v3.core.api.dto.EntornDto;
import net.conselldemallorca.helium.v3.core.api.dto.IntervalEventDto;
import net.conselldemallorca.helium.v3.core.api.dto.MesuraTemporalDto;
import net.conselldemallorca.helium.v3.core.api.dto.PersonaDto;
import net.conselldemallorca.helium.v3.core.api.dto.TascaCompleteDto;
import net.conselldemallorca.helium.v3.core.api.dto.UsuariPreferenciesDto;
import net.conselldemallorca.helium.v3.core.api.dto.PersonaDto.Sexe;
import net.conselldemallorca.helium.v3.core.api.service.AdminService;
import net.conselldemallorca.helium.v3.core.helper.ConversioTipusHelper;
import net.conselldemallorca.helium.v3.core.helper.UsuariActualCacheHelper;
import net.conselldemallorca.helium.v3.core.repository.PersonaRepository;
import net.conselldemallorca.helium.v3.core.repository.UsuariPreferenciesRepository;

import org.apache.commons.lang.StringEscapeUtils;
import org.hibernate.SessionFactory;
import org.hibernate.stat.QueryStatistics;
import org.hibernate.stat.Statistics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Servei per gestionar la configuració de l'aplicació.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Service
public class AdminServiceImpl implements AdminService {

	@Resource
	private UsuariPreferenciesRepository usuariPreferenciesRepository;
	@Resource
	private ConversioTipusHelper conversioTipusHelper;
	@Resource
	private UsuariActualCacheHelper usuariActualCacheHelper;
	@Resource
	private MesuresTemporalsHelper mesuresTemporalsHelper;
	@Resource
	private PersonaRepository personaRepository;
	@Resource
	private SessionFactory sessionFactory;

	@Override
	public List<EntornDto> findEntornAmbPermisReadUsuariActual() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth == null)
			return new ArrayList<EntornDto>();
		String usuariActual = auth.getName();
		logger.debug("Cercant entorns permesos per a l'usuari actual (codi=" + usuariActual + ")");
		return usuariActualCacheHelper.findEntornsPermesosUsuariActual(auth.getName());
	}

	@Override
	public UsuariPreferenciesDto getPreferenciesUsuariActual() {
		logger.debug("Obtenint preferències per a l'usuari actual");
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth == null)
			return null;
		String usuariActual = auth.getName();
		logger.debug("Obtenint preferències per a l'usuari actual (codi=" + usuariActual + ")");
		return conversioTipusHelper.convertir(
				usuariPreferenciesRepository.findByCodi(usuariActual),
				UsuariPreferenciesDto.class);
	}

	@Override
	public List<MesuraTemporalDto> findMesuresTemporals(String familia, boolean ambDetall) {
		logger.debug("Consultant el llistat de les mesures temporals");
		return mesuresTemporalsHelper.getEstadistiques(familia, ambDetall);
	}
	
	@Override
	public List<MesuraTemporalDto> findMesuresTemporalsTipusExpedient() {
		return mesuresTemporalsHelper.getEstadistiquesTipusExpedient();
	}
	
	@Override
	public List<MesuraTemporalDto> findMesuresTemporalsTasca() {
		return mesuresTemporalsHelper.getEstadistiquesTasca();
	}
	
	@Override
	public Set<String> findFamiliesMesuresTemporals() {
		return mesuresTemporalsHelper.getIntervalsFamilia();
	}

	@Override
	public MesuresTemporalsHelper getMesuresTemporalsHelper() {
		return mesuresTemporalsHelper;
	}
	
	@Override
	public void mesuraIniciar(String clau, String familia) {
		mesuresTemporalsHelper.mesuraIniciar(clau, familia);
	}
	
	@Override
	public void mesuraIniciar(String clau, String familia, String tipusExpedient) {
		mesuresTemporalsHelper.mesuraIniciar(clau, familia, tipusExpedient);
	}
	
	@Override
	public void mesuraIniciar(String clau, String familia, String tipusExpedient, String tasca, String detall) {
		mesuresTemporalsHelper.mesuraIniciar(clau, familia, tipusExpedient, tasca, detall);
	}
	
	@Override
	public void mesuraCalcular(String clau, String familia) {
		mesuresTemporalsHelper.mesuraCalcular(clau, familia);
	}
	
	@Override
	public void mesuraCalcular(String clau, String familia, String tipusExpedient) {
		mesuresTemporalsHelper.mesuraCalcular(clau, familia, tipusExpedient);
	}
	
	@Override
	public void mesuraCalcular(String clau, String familia, String tipusExpedient, String tasca, String detall) {
		mesuresTemporalsHelper.mesuraCalcular(clau, familia, tipusExpedient, tasca, detall);
	}
	
	@Override
	public boolean isStatisticActive() {
		return sessionFactory.getStatistics().isStatisticsEnabled();
	}
	
	@Override
	public List<MesuraTemporalDto> getHibernateStatistics(String familia, boolean exportar) {
		Map<String,MesuraTemporalDto> resposta = new HashMap<String, MesuraTemporalDto>();
		Map<String,MesuraTemporalDto> respostaDetallada = new HashMap<String, MesuraTemporalDto>();
		
		String clauJbpm = exportar?"Consultas Jbpm":"Consultas totales de JBPM";
		String clauHelium = exportar?"Consultas Helium":"Consultas totales de Helium";
		Statistics stats = sessionFactory.getStatistics();
		for (String sQuery : stats.getQueries()) {
			QueryStatistics qStats = stats.getQueryStatistics(sQuery);
			String clau = clauHelium;
			boolean pasar = true;
			if (!sQuery.contains("org.jbpm.") && "sql_helium".equals(familia)) {
				clau = sQuery;
				pasar = false;
			} else if (sQuery.contains("org.jbpm.") && "sql_jbpm".equals(familia)) {
				clau = sQuery;
				pasar = false;
			} else if ("".equals(familia)) {
				if (sQuery.contains("org.jbpm.")) {
					clau = clauJbpm;
				}
				pasar = false;
			} 
			
			if (!pasar && (exportar || "".equals(familia) || qStats.getExecutionMaxTime() > 100)) {
				MesuraTemporalDto dto;
				MesuraTemporalDto dtoDetalle;
				if (resposta.containsKey(clau)) {
					dto = resposta.get(clau);
					if (dto.getMinima()>qStats.getExecutionMinTime()) {
						dto.setMinima(qStats.getExecutionMinTime());
					}
					if (dto.getMaxima()<qStats.getExecutionMinTime()) {
						dto.setMaxima(qStats.getExecutionMaxTime());
					}
					
					long  mediaAnterior = (long) (dto.getMitja() * ((float) dto.getNumMesures()/(float) (dto.getNumMesures() + qStats.getExecutionCount())));
					long  mediaActual = (long) (qStats.getExecutionAvgTime() * ((float) qStats.getExecutionCount()/(float) (dto.getNumMesures() + qStats.getExecutionCount())));
					dto.setNumMesures((int) (dto.getNumMesures() + qStats.getExecutionCount()));				
					dto.setMitja(mediaAnterior+mediaActual);
				} else {
					dto = new MesuraTemporalDto();
					dto.setClau(clau);
					dto.setMinima(qStats.getExecutionMinTime());
					dto.setMaxima(qStats.getExecutionMaxTime());
					dto.setNumMesures((int) qStats.getExecutionCount());
					dto.setMitja(qStats.getExecutionAvgTime());
				}
				
				LinkedList<IntervalEventDto> intervalEvents = new LinkedList<IntervalEventDto>();
				dto.setEvents(intervalEvents);
				dtoDetalle = new MesuraTemporalDto();
				dtoDetalle.setClau(sQuery);
				dtoDetalle.setMinima(qStats.getExecutionMinTime());
				dtoDetalle.setMaxima(qStats.getExecutionMaxTime());
				dtoDetalle.setNumMesures((int) qStats.getExecutionCount());
				dtoDetalle.setMitja(qStats.getExecutionAvgTime());
				
				LinkedList<IntervalEventDto> intervalEventsDetalle = new LinkedList<IntervalEventDto>();
				dtoDetalle.setEvents(intervalEventsDetalle);
				
				respostaDetallada.put(StringEscapeUtils.escapeJavaScript(sQuery),dtoDetalle);
								
				if ("sql_helium".equals(familia)) {
					resposta.put(StringEscapeUtils.escapeJavaScript(sQuery),dto);
				} else if ("sql_jbpm".equals(familia)) {
					resposta.put(StringEscapeUtils.escapeJavaScript(sQuery),dto);
				} else if ("".equals(familia)) {
					resposta.put(clau,dto);
				}
			}
		}
		
		if (!"".equals(familia)) {
			if ("sql_helium".equals(familia) && resposta.containsKey(clauJbpm)) {
				resposta.remove(clauJbpm);
			}
			if ("sql_jbpm".equals(familia) && resposta.containsKey(clauHelium)) {
				resposta.remove(clauHelium);
			}
			resposta.putAll(respostaDetallada);
		}

		List<MesuraTemporalDto> ret = new ArrayList<MesuraTemporalDto>();
		Long temps = MesuresTemporalsHelper.getTemps();
		for (MesuraTemporalDto mesura: resposta.values()) {
			mesura.setPeriode((mesura.getNumMesures() * 60000.0) / temps);
			ret.add(mesura);
		}
		return ret;
	}
	
	@Override
	public List<TascaCompleteDto> getTasquesCompletar() {
		return mesuresTemporalsHelper.getTasquesCompletar();
	}
	
	@Transactional
	@Override
	public void updatePerfil(UsuariPreferenciesDto preferencies) {
		UsuariPreferencies usuari = usuariPreferenciesRepository.findByCodi(preferencies.getCodi());
		usuari.setCabeceraReducida(preferencies.isCabeceraReducida());
		usuari.setConsultaId(preferencies.getConsultaId());
		usuari.setDefaultEntornCodi(preferencies.getDefaultEntornCodi());
		usuari.setFiltroTareasActivas(preferencies.isFiltroTareasActivas());
		usuari.setListado(preferencies.getListado());
		usuari.setNumElementosPagina(preferencies.getNumElementosPagina());
		usuariPreferenciesRepository.save(usuari);
	}

	@Transactional
	@Override
	public void updatePersona(PersonaDto personaDto) {
		Persona persona = personaRepository.findByCodi(personaDto.getCodi());
		persona.setDni(personaDto.getDni());
		persona.setEmail(personaDto.getEmail());
		persona.setLlinatge1(personaDto.getLlinatge1());
		persona.setLlinatge2(personaDto.getLlinatge2());
		persona.setNom(personaDto.getNom());
		persona.setSexe(personaDto.getSexe().equals(Sexe.SEXE_HOME) ? Persona.Sexe.SEXE_HOME : Persona.Sexe.SEXE_DONA);
		personaRepository.save(persona);
	}
	
	private static final Logger logger = LoggerFactory.getLogger(AdminServiceImpl.class);	
}
