/**
 * 
 */
package es.caib.helium.logic.helper;

import es.caib.helium.logic.intf.dto.IntervalEventDto;
import es.caib.helium.logic.intf.dto.MesuraTemporalDto;
import org.apache.commons.lang.StringEscapeUtils;
import org.hibernate.SessionFactory;
import org.hibernate.stat.QueryStatistics;
import org.hibernate.stat.Statistics;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Helper per a enviament de correus
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class HibernateHelper {

	@Resource
	private SessionFactory sessionFactory;



	public List<MesuraTemporalDto> getHibernateStatistics(
			String familia,
			boolean exportar) {
		Map<String,MesuraTemporalDto> resposta = new HashMap<String, MesuraTemporalDto>();
		Map<String,MesuraTemporalDto> respostaDetallada = new HashMap<String, MesuraTemporalDto>();
		
		String clauJbpm = exportar?"Consultas Jbpm":"Consultas totales de JBPM";
		String clauHelium = exportar?"Consultas Helium":"Consultas totales de Helium";
		Statistics stats = sessionFactory.getStatistics();
		for (String sQuery: stats.getQueries()) {
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

		// TODO: Mètriques
		List<MesuraTemporalDto> ret = new ArrayList<MesuraTemporalDto>();
//		Long temps = MesuresTemporalsHelper.getTemps();
		for (MesuraTemporalDto mesura: resposta.values()) {
//			mesura.setPeriode((mesura.getNumMesures() * 60000.0) / temps);
			ret.add(mesura);
		}
		return ret;
	}

	public boolean isStatisticActive() {
		return sessionFactory.getStatistics().isStatisticsEnabled();
	}

}
