/**
 * 
 */
package net.conselldemallorca.helium.v3.core.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import org.apache.commons.lang.StringEscapeUtils;
import org.hibernate.SessionFactory;
import org.hibernate.stat.QueryStatistics;
import org.hibernate.stat.Statistics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.json.MetricsModule;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import net.conselldemallorca.helium.core.model.hibernate.Persona;
import net.conselldemallorca.helium.core.model.hibernate.Reassignacio;
import net.conselldemallorca.helium.core.model.hibernate.UsuariPreferencies;
import net.conselldemallorca.helium.core.model.service.MesuresTemporalsHelper;
import net.conselldemallorca.helium.core.util.GlobalProperties;
import net.conselldemallorca.helium.v3.core.api.dto.ArxiuDto;
import net.conselldemallorca.helium.v3.core.api.dto.IntervalEventDto;
import net.conselldemallorca.helium.v3.core.api.dto.MesuraTemporalDto;
import net.conselldemallorca.helium.v3.core.api.dto.PersonaDto;
import net.conselldemallorca.helium.v3.core.api.dto.PersonaDto.Sexe;
import net.conselldemallorca.helium.v3.core.api.dto.ReassignacioDto;
import net.conselldemallorca.helium.v3.core.api.dto.TascaCompleteDto;
import net.conselldemallorca.helium.v3.core.api.dto.UsuariPreferenciesDto;
import net.conselldemallorca.helium.v3.core.api.service.AdminService;
import net.conselldemallorca.helium.v3.core.helper.ConversioTipusHelper;
import net.conselldemallorca.helium.v3.core.helper.MailHelper;
import net.conselldemallorca.helium.v3.core.helper.UsuariActualHelper;
import net.conselldemallorca.helium.v3.core.repository.PersonaRepository;
import net.conselldemallorca.helium.v3.core.repository.ReassignacioRepository;
import net.conselldemallorca.helium.v3.core.repository.UsuariPreferenciesRepository;

/**
 * Servei per gestionar la configuració de l'aplicació.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Service
public class AdminServiceImpl implements AdminService {

	@Resource
	private ReassignacioRepository reassignacioRepository;
	@Resource
	private UsuariPreferenciesRepository usuariPreferenciesRepository;
	@Resource
	private PersonaRepository personaRepository;

	@Resource
	private ConversioTipusHelper conversioTipusHelper;
	@Resource
	private UsuariActualHelper usuariActualCacheHelper;
	@Resource
	private MesuresTemporalsHelper mesuresTemporalsHelper;
	@Resource
	private MailHelper mailHelper;

	@Resource
	private SessionFactory sessionFactory;
	@Resource
	private MetricRegistry metricRegistry;



	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getMetrics() {
		logger.debug("Consultant les mètriques de l'aplicació");
		try {
			return getApplictionMetrics();
		} catch (Exception ex) {
			logger.error("Error al generar les mètriques de l'aplicació", ex);
			return "ERR";
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Scheduled(cron="0 30 23 * * *")
	public void metricsEmailResponsables() {
		logger.debug("Enviant email amb les mètriques de l'aplicació");
		String destinataris = getCorreuMetriquesDestinataris();
		if (destinataris != null && !destinataris.isEmpty()) {
			try {
				List<String> recipients = new ArrayList<String>();
				for (String recipient: destinataris.split(",")) {
					recipients.add(recipient.trim());
				}
				String fromAddress = getCorreuRemitent();
				List<ArxiuDto> attachments = new ArrayList<ArxiuDto>();
				attachments.add(
						new ArxiuDto(
								"metrics.json",
								getApplictionMetrics().getBytes()));
				mailHelper.send(
						fromAddress,
						recipients,
						null,
						null,
						"Mètriques Helium " + new SimpleDateFormat("dd/MM/yyyy").format(new Date()),
						"Mètriques generades per a monitoritzar l'ús de l'aplicació.",
						attachments);
			} catch (Exception ex) {
				logger.error("Error al enviar per correu les mètriques de l'aplicació", ex);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<MesuraTemporalDto> mesuraTemporalFindByFamilia(
			String familia,
			boolean ambDetall) {
		logger.debug("Consultant el llistat de mesures temporals per família (" +
				"familia=" + familia + ", " +
				"ambDetall=" + ambDetall + ")");
		return mesuresTemporalsHelper.getEstadistiques(familia, ambDetall);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<MesuraTemporalDto> mesuraTemporalFindByTipusExpedient() {
		logger.debug("Consultant el llistat de mesures temporals dels tipus d'expedient");
		return mesuresTemporalsHelper.getEstadistiquesTipusExpedient();
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<MesuraTemporalDto> mesuraTemporalFindByTasca() {
		logger.debug("Consultant el llistat de mesures temporals de les tasques");
		return mesuresTemporalsHelper.getEstadistiquesTasca();
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Set<String> mesuraTemporalFindFamiliesAll() {
		logger.debug("Consultant el llistat de famílies de mesures temporals");
		return mesuresTemporalsHelper.getIntervalsFamilia();
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void mesuraTemporalIniciar(
			String clau,
			String familia) {
		logger.debug("Consultant el llistat de famílies de mesures temporals");
		mesuresTemporalsHelper.mesuraIniciar(
				clau,
				familia);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void mesuraTemporalIniciar(
			String clau,
			String familia,
			String tipusExpedient) {
		mesuresTemporalsHelper.mesuraIniciar(
				clau,
				familia,
				tipusExpedient);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void mesuraTemporalIniciar(
			String clau,
			String familia,
			String tipusExpedient,
			String tasca,
			String detall) {
		mesuresTemporalsHelper.mesuraIniciar(
				clau,
				familia,
				tipusExpedient,
				tasca,
				detall);
	}
	
	@Override
	public void mesuraTemporalCalcular(String clau, String familia) {
		mesuresTemporalsHelper.mesuraCalcular(clau, familia);
	}
	
	@Override
	public void mesuraTemporalCalcular(String clau, String familia, String tipusExpedient) {
		mesuresTemporalsHelper.mesuraCalcular(clau, familia, tipusExpedient);
	}
	@Override
	public void mesuraTemporalCalcular(String clau, String familia, String tipusExpedient, String tasca, String detall) {
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
		if (usuari == null)
			usuari = new UsuariPreferencies();
		
		usuari.setCodi(preferencies.getCodi());
		usuari.setCabeceraReducida(preferencies.isCabeceraReducida());
		usuari.setConsultaId(preferencies.getConsultaId());
		usuari.setExpedientTipusDefecteId(preferencies.getExpedientTipusDefecteId());
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
	
	@Transactional(readOnly = true)
	@Override
	public List<ReassignacioDto> llistaReassignacions() {
		return conversioTipusHelper.convertirList(reassignacioRepository.findLlistaActius(Calendar.getInstance().getTime()), ReassignacioDto.class);
	}
	
	@Transactional
	@Override
	public void createReassignacio(
			String usuariOrigen,
			String usuariDesti,
			Date dataInici,
			Date dataFi,
			Date dataCancelacio,
			Long tipusExpedientId) {
		Reassignacio reassignacio = new Reassignacio();
		reassignacio.setUsuariOrigen(usuariOrigen);
		reassignacio.setUsuariDesti(usuariDesti);
		reassignacio.setDataInici(dataInici);
		reassignacio.setDataFi(dataFi);
		reassignacio.setDataCancelacio(dataCancelacio);
		reassignacio.setTipusExpedientId(tipusExpedientId);
		reassignacioRepository.save(reassignacio);
	}

	@Transactional
	@Override
	public void updateReassignacio(
			Long id,
			String usuariOrigen,
			String usuariDesti,
			Date dataInici,
			Date dataFi,
			Date dataCancelacio,
			Long tipusExpedientId) {
		Reassignacio reassignacio = reassignacioRepository.findOne(id);
		reassignacio.setUsuariOrigen(usuariOrigen);
		reassignacio.setUsuariDesti(usuariDesti);
		reassignacio.setDataInici(dataInici);
		reassignacio.setDataFi(dataFi);
		reassignacio.setDataCancelacio(dataCancelacio);
		reassignacio.setTipusExpedientId(tipusExpedientId);
		reassignacioRepository.save(reassignacio);
	}

	@Transactional
	@Override
	public void deleteReassignacio(Long id) {
		Reassignacio reassignacio = reassignacioRepository.findOne(id);
		if (reassignacio != null) {
			reassignacio.setDataCancelacio(Calendar.getInstance().getTime());
			reassignacioRepository.save(reassignacio);
		}
	}

	@Transactional(readOnly = true)
	@Override
	public ReassignacioDto findReassignacioById(Long id) {
		return conversioTipusHelper.convertir(reassignacioRepository.findOne(id), ReassignacioDto.class);
	}



	private String getApplictionMetrics() throws JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();
		mapper.registerModule(
				new MetricsModule(
						TimeUnit.SECONDS,
						TimeUnit.MILLISECONDS,
						false));
		return mapper.writeValueAsString(metricRegistry);
	}

	private String getCorreuRemitent() {
		return GlobalProperties.getInstance().getProperty("app.correu.remitent");
	}
	private String getCorreuMetriquesDestinataris() {
		return GlobalProperties.getInstance().getProperty("app.correu.metrics.recipients");
	}

	private static final Logger logger = LoggerFactory.getLogger(AdminServiceImpl.class);

}
