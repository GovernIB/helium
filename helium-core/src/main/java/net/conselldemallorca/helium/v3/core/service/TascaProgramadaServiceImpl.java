package net.conselldemallorca.helium.v3.core.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.codahale.metrics.Counter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;

import net.conselldemallorca.helium.core.helper.IndexHelper;
import net.conselldemallorca.helium.core.helper.NotificacioHelper;
import net.conselldemallorca.helium.core.helper.TascaProgramadaHelper;
import net.conselldemallorca.helium.core.model.hibernate.ExecucioMassiva.ExecucioMassivaTipus;
import net.conselldemallorca.helium.core.model.hibernate.ExecucioMassivaExpedient;
import net.conselldemallorca.helium.core.model.hibernate.Expedient;
import net.conselldemallorca.helium.core.model.hibernate.Notificacio;
import net.conselldemallorca.helium.core.util.GlobalProperties;
import net.conselldemallorca.helium.v3.core.api.dto.DocumentEnviamentEstatEnumDto;
import net.conselldemallorca.helium.v3.core.api.dto.DocumentNotificacioTipusEnumDto;
import net.conselldemallorca.helium.v3.core.api.exception.NoTrobatException;
import net.conselldemallorca.helium.v3.core.api.service.ExecucioMassivaService;
import net.conselldemallorca.helium.v3.core.api.service.TascaProgramadaService;
import net.conselldemallorca.helium.v3.core.repository.ExecucioMassivaExpedientRepository;
import net.conselldemallorca.helium.v3.core.repository.ExpedientRepository;
import net.conselldemallorca.helium.v3.core.repository.NotificacioRepository;

/**
 * Servei per gestionar els terminis dels expedients
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Service("tascaProgramadaServiceV3")
public class TascaProgramadaServiceImpl implements TascaProgramadaService {
	
	@Resource
	private ExecucioMassivaExpedientRepository execucioMassivaExpedientRepository;
	@Resource
	private ExpedientRepository expedientRepository;
	@Resource
	private NotificacioRepository notificacioRepository;
	@Autowired
	private ExecucioMassivaService execucioMassivaService;
	@Resource
	private IndexHelper indexHelper;
	@Resource
	private NotificacioHelper notificacioHelper;
	@Resource
	private TascaProgramadaHelper tascaProgramadaHelper;
	@Resource
  	private MetricRegistry metricRegistry;
	
	private static Map<Long, String> errorsMassiva = new HashMap<Long, String>();
	
	@Override
	@Scheduled(fixedDelayString = "${app.massiu.periode.noves}")
	public void comprovarExecucionsMassives() {
		boolean active = true;
		Long ultimaExecucioMassiva = null;
		
		int timeBetweenExecutions = 500;
		try {
			timeBetweenExecutions = Integer.parseInt(
					GlobalProperties.getInstance().getProperty("app.massiu.periode.execucions")); 
		} catch (Exception ex) {}
		
		while (active) {
			try {
				Long ome_id = execucioMassivaService.getExecucionsMassivesActiva(ultimaExecucioMassiva);
				if (ome_id != null) {
					try {
						execucioMassivaService.executarExecucioMassiva(ome_id);
					}
					catch (Exception e) {
						// recuperem l'error de la aplicació
						String errMsg = getError(ome_id);
						if (errMsg == null || "".equals(errMsg))
							errMsg = e.getMessage();
						execucioMassivaService.generaInformeError(ome_id, errMsg);
					}
					ExecucioMassivaExpedient ome = execucioMassivaExpedientRepository.findOne(ome_id);
					if (ome == null)
						throw new NoTrobatException(ExecucioMassivaExpedient.class, ome_id);
					ultimaExecucioMassiva = ome.getExecucioMassiva().getId();
					execucioMassivaService.actualitzaUltimaOperacio(ome.getId());
				} else {
					active = false;
				}
				Thread.sleep(timeBetweenExecutions);
			} catch (Exception e) {
				logger.error("La execució de execucions massives ha estat interromput");
				active = false;
			}
		}
	}
	
	/*** REINDEXACIO ASÍNCRONA ***/
	@Override
	@Scheduled(fixedDelay=15000)
	public void comprovarReindexacioAsincrona() {
		Counter countMetodeAsincronTotal = metricRegistry.counter(MetricRegistry.name(TascaProgramadaService.class, "reindexacio.asincrona.metode.count"));
		countMetodeAsincronTotal.inc();
		
		List<Long> expedientIds = expedientRepository.findAmbDataReindexacio();
		
		for (Long expedientId: expedientIds) {
			tascaProgramadaHelper.reindexarExpedient(expedientId);
		}
	}
	
	@Override
	@Transactional
	public void reindexarExpedient (Long expedientId) {
		
		Expedient expedient = expedientRepository.findOne(expedientId);
		if (expedient == null)
			throw new NoTrobatException(Expedient.class, expedientId);
		
		Timer.Context contextTotal = null;
		Timer.Context contextEntorn = null;
		Timer.Context contextTipexp = null;

		try {
			
			final Timer timerTotal = metricRegistry.timer(MetricRegistry.name(TascaProgramadaService.class, "reindexacio.asincrona.expedient"));
			final Timer timerEntorn = metricRegistry.timer(MetricRegistry.name(TascaProgramadaService.class, "reindexacio.asincrona.expedient", expedient.getEntorn().getCodi()));
			final Timer timerTipexp = metricRegistry.timer(MetricRegistry.name(TascaProgramadaService.class, "reindexacio.asincrona.expedient", expedient.getEntorn().getCodi(), expedient.getTipus().getCodi()));
			
			Counter countTotal = metricRegistry.counter(MetricRegistry.name(TascaProgramadaService.class, "reindexacio.asincrona.expedient.count"));
			Counter countEntorn = metricRegistry.counter(MetricRegistry.name(TascaProgramadaService.class, "reindexacio.asincrona.expedient.count", expedient.getEntorn().getCodi()));
			Counter countTipexp = metricRegistry.counter(MetricRegistry.name(TascaProgramadaService.class, "reindexacio.asincrona.expedient.count", expedient.getEntorn().getCodi(), expedient.getTipus().getCodi()));
		
			countTotal.inc();
			countEntorn.inc();
			countTipexp.inc();
			
			contextTotal = timerTotal.time();
			contextEntorn = timerEntorn.time();
			contextTipexp = timerTipexp.time();
			
			indexHelper.expedientIndexLuceneUpdate(
					expedient.getProcessInstanceId(),
					false,
					null);
			
		} catch (Exception ex) {
			logger.error(
					"Error reindexant l'expedient " + expedient.getIdentificador(),
					ex);
		} finally {			
			contextTotal.stop();
			contextEntorn.stop();
			contextTipexp.stop();
		}
	}

	/**************************/
	
	/*** ACTUALITZAR ESTAT NOTIFICACIONS ***/
	@Override
//	#1164 Comentam aquesta tasca programada, ja que ara les notificacions es faran amb Notib, 
//	i no és necessari fer una consulta activa, ja que notib ens avisarà en cas de canvi.	
//	@Scheduled(fixedDelayString = "${app.notificacions.comprovar.estat}")
	public void comprovarEstatNotificacions() {
		List<Notificacio> notificacionsPendentsRevisar = notificacioRepository.findByEstatAndTipusOrderByDataEnviamentAsc(DocumentEnviamentEstatEnumDto.ENVIAT, DocumentNotificacioTipusEnumDto.ELECTRONICA);
		for (Notificacio notificacio: notificacionsPendentsRevisar) {
			tascaProgramadaHelper.actualitzarEstatNotificacions(notificacio.getId());
		}
	}

	
	@Override
	@Transactional
	public void actualitzarEstatNotificacions(Long notificacioId) {
		Notificacio notificacio = notificacioRepository.findOne(notificacioId);
		if (notificacio == null)
			throw new NoTrobatException(Notificacio.class, notificacioId);
		
		notificacioHelper.obtenirJustificantNotificacio(notificacio);
	}
	/**************************/
	
	
	
	public static void saveError(Long operacioMassivaId, Throwable error, ExecucioMassivaTipus tipus) {
		if (tipus != ExecucioMassivaTipus.ELIMINAR_VERSIO_DEFPROC) {
			StringBuilder sb = new StringBuilder();
			if (error != null) {
				sb.append(error.getLocalizedMessage());
				for (StackTraceElement element : error.getStackTrace()) {
			        sb.append("\nat ");
			        sb.append(element.toString());
			    }
			}
			errorsMassiva.put(operacioMassivaId, sb.toString());
		} else {
			errorsMassiva.put(operacioMassivaId, error.getMessage());
		}
	}
	
	private static String getError(Long operacioMassivaId) {
		String error = errorsMassiva.get(operacioMassivaId);
		errorsMassiva.remove(operacioMassivaId);
		return error;
	}
	
	private static final Log logger = LogFactory.getLog(TascaProgramadaService.class);
}
