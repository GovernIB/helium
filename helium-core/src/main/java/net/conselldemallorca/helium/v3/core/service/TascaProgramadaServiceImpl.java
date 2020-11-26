package net.conselldemallorca.helium.v3.core.service;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

import net.conselldemallorca.helium.core.helper.ExpedientHelper;
import net.conselldemallorca.helium.core.helper.IndexHelper;
import net.conselldemallorca.helium.core.helper.NotificacioHelper;
import net.conselldemallorca.helium.core.helper.TascaProgramadaHelper;
import net.conselldemallorca.helium.core.model.hibernate.ExecucioMassiva.ExecucioMassivaTipus;
import net.conselldemallorca.helium.core.model.hibernate.ExecucioMassivaExpedient;
import net.conselldemallorca.helium.core.model.hibernate.Expedient;
import net.conselldemallorca.helium.core.model.hibernate.ExpedientReindexacio;
import net.conselldemallorca.helium.core.model.hibernate.Notificacio;
import net.conselldemallorca.helium.core.util.GlobalProperties;
import net.conselldemallorca.helium.v3.core.api.dto.DocumentEnviamentEstatEnumDto;
import net.conselldemallorca.helium.v3.core.api.dto.DocumentNotificacioTipusEnumDto;
import net.conselldemallorca.helium.v3.core.api.exception.NoTrobatException;
import net.conselldemallorca.helium.v3.core.api.service.ExecucioMassivaService;
import net.conselldemallorca.helium.v3.core.api.service.TascaProgramadaService;
import net.conselldemallorca.helium.v3.core.repository.ExecucioMassivaExpedientRepository;
import net.conselldemallorca.helium.v3.core.repository.ExpedientReindexacioRepository;
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
	private ExpedientReindexacioRepository expedientReindexacioRepository;
	@Resource
	private NotificacioRepository notificacioRepository;
	@Autowired
	private ExecucioMassivaService execucioMassivaService;
	@Resource
	private IndexHelper indexHelper;
	@Resource
	private ExpedientHelper expedientHelper;
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
	
	/** Variable privada per activar o descativar les reindexacions asíncrones. */
	private boolean reindexarAsíncronament = true;
	
	/** Comprovació cada 10 segons si hi ha expedients pendents de reindexació asíncrona segons la taula
	 * hel_expedient_reindexacio. Cada cop que s'executa va consultant si en queden de pendents fins la 
	 * propera execució.
	 */
	@Override
	@Scheduled(fixedDelay=10000)
	public void comprovarReindexacioAsincrona() {
		
		Counter countMetodeAsincronTotal = metricRegistry.counter(MetricRegistry.name(TascaProgramadaService.class, "reindexacio.asincrona.metode.count"));
		countMetodeAsincronTotal.inc();		
		
		// comprova que la propietat de reindexació estigui a true
		if (!this.isReindexarAsincronament()) {
			logger.warn("Actualment la tasca de reindexació asíncrona està aturada. Activi-la des del menú 'Adminsitrador > Reindexacions asíncrones'");
			return;
		}
		
		// Consulta les reindexacions pendents
		List<ExpedientReindexacio> reindexacions = expedientReindexacioRepository.findAll();
		// Consulta els expedients amb data de reindexació per comprovar si estan a la cua
		List<Long> expedientIdsAmbReindexarData = expedientRepository.findIdsPendentsReindexacio();

		boolean fi = reindexacions.isEmpty() && expedientIdsAmbReindexarData.isEmpty();
		// Llistat d'expedients reindexats per no reindexar dues vegades
		Set<Long> expedientsIdsReindexats;
		Date darreraData = null;
		while(!fi) {
			
			// Busca la darrera data per buscar si es programen noves reindexacions per un expedient a partir de la darrera data.
			// També revisa si hi ha expedients amb data de reindexació que no estan a la cua
			for (ExpedientReindexacio reindexacio : reindexacions) {
				if (darreraData == null || darreraData.before(reindexacio.getDataReindexacio()))
					darreraData = reindexacio.getDataReindexacio();
				// Esborra l'id de la llista d'expedients amb data de reindexació
				expedientIdsAmbReindexarData.remove(reindexacio.getExpedientId());
			}
			if (!expedientIdsAmbReindexarData.isEmpty()) {
				// Afegeix els expedients a la cua de reindexació
				Expedient expedient;
				ExpedientReindexacio novaReindexacio;
				for (Long expedientId : expedientIdsAmbReindexarData) {
					expedient = expedientRepository.findOne(expedientId);
					novaReindexacio = new ExpedientReindexacio();
					novaReindexacio.setExpedientId(expedientId);
					novaReindexacio.setDataReindexacio(expedient.getReindexarData());
					expedientReindexacioRepository.save(novaReindexacio);
				}
			} else {
				// Continua amb les reindexacions
				
				// Llistat d'expedients reindexats per no reindexa el mateix expedient dues vegades en la mateixa iteració
				expedientsIdsReindexats = new HashSet<Long>(); 
				// Itera per les diferent reindexacions
				for(ExpedientReindexacio reindexacio : reindexacions) {
					if (!expedientsIdsReindexats.contains(reindexacio.getExpedientId())) {
						expedientsIdsReindexats.add(reindexacio.getExpedientId());
						tascaProgramadaHelper.reindexarExpedient(reindexacio.getExpedientId());
						
						// Comprova si mentres s'ha reindexat s'ha programat una nova reindexació per fixar la nova data a l'expedient
						Date dataReindexacio = expedientReindexacioRepository.findSeguentReindexacioData(reindexacio.getExpedientId(), darreraData);
						if (dataReindexacio != null) {
							try {
								tascaProgramadaHelper.actualitzarExpedientReindexacioData(reindexacio.getExpedientId(), dataReindexacio);
							}catch(Exception e) {
								logger.error("Error actualtizant les dades de reindexació per l'expedient " + reindexacio.getExpedientId() + ": " + e.getMessage(), e);
							}

						}
					}
					expedientReindexacioRepository.delete(reindexacio);
					// Comprova si s'han aturat les reindexacions
					if (!this.isReindexarAsincronament())
						break;
				}	
			}

			// torna a consultar les reindexacions i els expedients amb data de reindexació
			reindexacions = expedientReindexacioRepository.findAll();
			expedientIdsAmbReindexarData = expedientRepository.findIdsPendentsReindexacio();
			
			fi = reindexacions.isEmpty() || !this.isReindexarAsincronament();	
		}
	}
	
	/** Mètode per iniciar o aturar les reindexacions asíncrones. Si es marca a false la tasca
	 * en segon pla no reindexarà i si es torna a posar a true es marcarà.
	 */
	@Override
	public void setReindexarAsincronament(boolean reindexar) {
		logger.info((reindexar ? "Iniciant" :"Aturant") + " la tasca de reindexació en 2n pla. Actualment està " + (this.isReindexarAsincronament() ? "iniciada" : "aturada"));
		this.reindexarAsíncronament = reindexar;
	}

	/** Mètode per consultar l'estat acutal de la propietat que marca si reindexar asíncronament.
	 * 
	 */
	@Override
	public boolean isReindexarAsincronament() {
		return this.reindexarAsíncronament;
	}

	
	/** Mètode  per acutalitzar les dades de reindexació d'un expedient dins d'una transacció. Serveix per
	 * informar la data de reindexació asíncrona en el cas que s'hagi reindexat però que s'hagi tornat a programar
	 * una reindexació posterior.
	 * 
	 * @param expedientId
	 * @param dataReindexacio
	 */
	@Transactional
	public void actualitzarExpedientReindexacioData(Long expedientId, Date dataReindexacio) {
		try {
			Expedient expedient = expedientRepository.findOne(expedientId);
			expedientRepository.setReindexarErrorData(expedientId, expedient.isReindexarError(), dataReindexacio);			
		}catch(Exception e) {
			logger.error("Error actualtizant les dades de reindexació per l'expedient " + expedientId + ": " + e.getMessage(), e);
		}
	}
	
	@Override
	@Transactional
	public void reindexarExpedient (Long expedientId) {
		
		Expedient expedient = expedientRepository.findOne(expedientId);
		if (expedient != null) {
			
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
				expedientRepository.setReindexarErrorData(expedientId, true, null);
			} finally {			
				contextTotal.stop();
				contextEntorn.stop();
				contextTipexp.stop();
			}			
		} else {
			logger.warn("No s'ha trobat l'expedient amb id " + expedientId + " per reindexar.");
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
