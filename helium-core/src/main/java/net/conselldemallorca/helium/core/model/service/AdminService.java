/**
 * 
 */
package net.conselldemallorca.helium.core.model.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import net.conselldemallorca.helium.core.model.dao.RegistreDao;
import net.conselldemallorca.helium.core.model.dto.IntervalEventDto;
import net.conselldemallorca.helium.core.model.dto.MesuraTemporalDto;
import net.conselldemallorca.helium.core.model.service.MesuresTemporalsHelper.IntervalEvent;

import org.apache.commons.lang.StringEscapeUtils;
import org.hibernate.stat.QueryStatistics;
import org.hibernate.stat.Statistics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Servei per gestionar la configuració de l'aplicació.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Service
public class AdminService {

	@Resource
	private MesuresTemporalsHelper mesuresTemporalsHelper;

	@Resource
	private RegistreDao registreDao;
	
	public boolean isStatisticActive() {
		return registreDao.getSessionFactory().getStatistics().isStatisticsEnabled();
	}
	public List<MesuraTemporalDto> getHibernateStatistics(String familia, boolean exportar) {
		Map<String,MesuraTemporalDto> resposta = new HashMap<String, MesuraTemporalDto>();
		Map<String,MesuraTemporalDto> respostaDetallada = new HashMap<String, MesuraTemporalDto>();
		
		String clauJbpm = exportar?"Consultas Jbpm":"Consultas totales de JBPM";
		String clauHelium = exportar?"Consultas Helium":"Consultas totales de Helium";
		Statistics stats = registreDao.getSessionFactory().getStatistics();
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
			
			if (!pasar) {
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
				
				if (exportar || qStats.getExecutionMaxTime() > 100) {
					dtoDetalle = new MesuraTemporalDto();
					dtoDetalle.setClau(sQuery);
					dtoDetalle.setMinima(qStats.getExecutionMinTime());
					dtoDetalle.setMaxima(qStats.getExecutionMaxTime());
					dtoDetalle.setNumMesures((int) qStats.getExecutionCount());
					dtoDetalle.setMitja(qStats.getExecutionAvgTime());
					
					LinkedList<IntervalEventDto> intervalEventsDetalle = new LinkedList<IntervalEventDto>();
					dtoDetalle.setEvents(intervalEventsDetalle);
					
					respostaDetallada.put(StringEscapeUtils.escapeJavaScript(sQuery),dtoDetalle);
				}
				
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
			if (resposta.containsKey(clauJbpm)) {
				resposta.remove(clauJbpm);
			}
			if (resposta.containsKey(clauHelium)) {
				resposta.remove(clauHelium);
			}
			resposta.putAll(respostaDetallada);
		}

		List<MesuraTemporalDto> ret = new ArrayList<MesuraTemporalDto>();
		
		Iterator it = resposta.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry e = (Map.Entry)it.next();
			ret.add((MesuraTemporalDto) e.getValue());
		}
		return ret;
	}

	public List<MesuraTemporalDto> findMesuresTemporals(String familia) {
		logger.debug("Consultant el llistat de les mesures temporals");
		List<MesuraTemporalDto> resposta = new ArrayList<MesuraTemporalDto>();
		for (String clau: mesuresTemporalsHelper.getClausMesures(familia)) {
			MesuraTemporalDto dto = new MesuraTemporalDto();
			dto.setClau(clau);
			dto.setDarrera(mesuresTemporalsHelper.getDarreraMesura(clau));
			dto.setMitja(mesuresTemporalsHelper.getMitja(clau));
			dto.setMinima(mesuresTemporalsHelper.getMinim(clau));
			dto.setMaxima(mesuresTemporalsHelper.getMaxim(clau));
			dto.setNumMesures(mesuresTemporalsHelper.getNumMesures(clau));
			LinkedList<IntervalEventDto> intervalEvents = new LinkedList<IntervalEventDto>();
			long iniMilis = 0;
			long fiMilis = 0;
			for (IntervalEvent event : mesuresTemporalsHelper.getIntervalEvents(clau)) {
				intervalEvents.add(new IntervalEventDto(event.getDate(), event.getDuracio()));
				long milis =  event.getDate().getTime();
				if (iniMilis == 0 || iniMilis > milis) iniMilis = milis;
				if (fiMilis < milis) fiMilis = milis;
			}
			dto.setEvents(intervalEvents);
			if (dto.getNumMesures() > 1) {
				dto.setPeriode((fiMilis - iniMilis) / dto.getNumMesures());
			}
			resposta.add(dto);
		}
		return resposta;
	}
	
	public Set<String> findFamiliesMesuresTemporals() {
		return mesuresTemporalsHelper.getIntervalsFamilia();
	}

	public MesuresTemporalsHelper getMesuresTemporalsHelper() {
		return mesuresTemporalsHelper;
	}
	
	@Autowired
	public void setRegistreDao(RegistreDao registreDao) {
		this.registreDao = registreDao;
	}
	
	private static final Logger logger = LoggerFactory.getLogger(AdminService.class);

}
