package net.conselldemallorca.helium.v3.core.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.codahale.metrics.Counter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;

import es.caib.distribucio.backoffice.utils.arxiu.ArxiuPluginListener;
import es.caib.distribucio.backoffice.utils.arxiu.BackofficeArxiuUtils;
import es.caib.distribucio.backoffice.utils.arxiu.BackofficeArxiuUtilsImpl;
import es.caib.distribucio.rest.client.integracio.domini.AnotacioRegistreEntrada;
import es.caib.distribucio.rest.client.integracio.domini.AnotacioRegistreId;
import net.conselldemallorca.helium.core.helper.ConsultaPinbalHelper;
import net.conselldemallorca.helium.core.helper.DistribucioHelper;
import net.conselldemallorca.helium.core.helper.EmailHelper;
import net.conselldemallorca.helium.core.helper.ExceptionHelper;
import net.conselldemallorca.helium.core.helper.ExpedientHelper;
import net.conselldemallorca.helium.core.helper.IndexHelper;
import net.conselldemallorca.helium.core.helper.MonitorIntegracioHelper;
import net.conselldemallorca.helium.core.helper.NotificacioHelper;
import net.conselldemallorca.helium.core.helper.PluginHelper;
import net.conselldemallorca.helium.core.model.hibernate.Anotacio;
import net.conselldemallorca.helium.core.model.hibernate.AnotacioEmail;
import net.conselldemallorca.helium.core.model.hibernate.ExecucioMassiva.ExecucioMassivaTipus;
import net.conselldemallorca.helium.core.model.hibernate.ExecucioMassivaExpedient;
import net.conselldemallorca.helium.core.model.hibernate.Expedient;
import net.conselldemallorca.helium.core.model.hibernate.ExpedientReindexacio;
import net.conselldemallorca.helium.core.model.hibernate.Notificacio;
import net.conselldemallorca.helium.core.model.hibernate.PeticioPinbal;
import net.conselldemallorca.helium.core.util.GlobalProperties;
import net.conselldemallorca.helium.v3.core.api.dto.DocumentEnviamentEstatEnumDto;
import net.conselldemallorca.helium.v3.core.api.dto.DocumentNotificacioTipusEnumDto;
import net.conselldemallorca.helium.v3.core.api.dto.IntegracioAccioTipusEnumDto;
import net.conselldemallorca.helium.v3.core.api.dto.IntegracioParametreDto;
import net.conselldemallorca.helium.v3.core.api.exception.ExecucioMassivaException;
import net.conselldemallorca.helium.v3.core.api.dto.ParametreDto;
import net.conselldemallorca.helium.v3.core.api.dto.UnitatOrganitzativaDto;
import net.conselldemallorca.helium.v3.core.api.dto.procediment.ProgresActualitzacioDto;
import net.conselldemallorca.helium.v3.core.api.exception.NoTrobatException;
import net.conselldemallorca.helium.v3.core.api.exportacio.TascaExportacio;
import net.conselldemallorca.helium.v3.core.api.service.ExecucioMassivaService;
import net.conselldemallorca.helium.v3.core.api.service.ParametreService;
import net.conselldemallorca.helium.v3.core.api.service.ProcedimentService;
import net.conselldemallorca.helium.v3.core.api.service.TascaProgramadaService;
import net.conselldemallorca.helium.v3.core.api.service.UnitatOrganitzativaService;
import net.conselldemallorca.helium.v3.core.repository.AnotacioEmailRepository;
import net.conselldemallorca.helium.v3.core.repository.ExecucioMassivaExpedientRepository;
import net.conselldemallorca.helium.v3.core.repository.ExpedientReindexacioRepository;
import net.conselldemallorca.helium.v3.core.repository.ExpedientRepository;
import net.conselldemallorca.helium.v3.core.repository.NotificacioRepository;
import net.conselldemallorca.helium.v3.core.repository.PeticioPinbalRepository;

/**
 * Servei per gestionar els terminis dels expedients
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Service("tascaProgramadaServiceV3")
public class TascaProgramadaServiceImpl implements TascaProgramadaService, ArxiuPluginListener {
	
	/** Referència al mateix service per fer crides transaccionals. */
	private TascaProgramadaService self;
	@Autowired
	private ApplicationContext applicationContext;
	@PostConstruct
	public void postContruct() {
		self = applicationContext.getBean(TascaProgramadaService.class);
	}

	
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
	@Autowired
	private ProcedimentService procedimentService;
	@Resource
	private ParametreService parametreService;
	@Resource
	private UnitatOrganitzativaService unitatOrganitzativaService;

	@Resource
	private AnotacioEmailRepository anotacioEmailRepository;
	@Resource
	private PeticioPinbalRepository peticioPinbalRepository;
	@Resource
	private ConsultaPinbalHelper consultaPinbalHelper;
	@Resource
	private IndexHelper indexHelper;
	@Resource
	private ExpedientHelper expedientHelper;
	@Resource
	private NotificacioHelper notificacioHelper;
	@Resource
	private DistribucioHelper distribucioHelper;
	@Resource
  	private MetricRegistry metricRegistry;
	@Resource
	private ExceptionHelper exceptionHelper;
	@Resource
	private PluginHelper pluginHelper;
	@Autowired
	private MonitorIntegracioHelper monitorIntegracioHelper;
	@Resource
	private EmailHelper emailHelper;
	private static Map<Long, String> errorsMassiva = new HashMap<Long, String>();
	
	@Override
//	@Scheduled(fixedDelayString = "${app.massiu.periode.noves}")
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
//	@Scheduled(fixedDelay=10000)
	public void comprovarReindexacioAsincrona() {
		
		Counter countMetodeAsincronTotal = metricRegistry.counter(MetricRegistry.name(TascaProgramadaService.class, "reindexacio.asincrona.metode.count"));
		countMetodeAsincronTotal.inc();		
		
		// comprova que la propietat de reindexació estigui a true
		if (!this.isReindexarAsincronament()) {
			logger.warn("Actualment la tasca de reindexació asíncrona està aturada. Activi-la des del menú 'Adminsitrador > Reindexacions asíncrones'");
			return;
		}
		
		// Consulta les reindexacions pendents
		Sort sort = new Sort(Direction.ASC, "id");
		List<ExpedientReindexacio> reindexacions = expedientReindexacioRepository.findAll(sort);
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
						self.reindexarExpedient(reindexacio.getExpedientId());
						
						// Comprova si mentres s'ha reindexat s'ha programat una nova reindexació per fixar la nova data a l'expedient
						Date dataReindexacio = expedientReindexacioRepository.findSeguentReindexacioData(reindexacio.getExpedientId(), darreraData);
						if (dataReindexacio != null) {
							try {
								self.actualitzarExpedientReindexacioData(reindexacio.getExpedientId(), dataReindexacio);
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
			reindexacions = expedientReindexacioRepository.findAll(sort);
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
			self.actualitzarEstatNotificacions(notificacio.getId());
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
	
	
	/** Classe runable per guardar un annex a l'Arxiu en un thread independent.
	 * 
	 */
	public class GuardarAnotacioPendentThread implements Runnable {

	    private Long anotacioId;
	    private AnotacioRegistreId idWs;
	    private int maxReintents;
	    private int consultaIntents;
	    private String consultaError ;
	    private Date consultaData;
		
		/** Constructor amb els objectes de consulta i el zip per actualitzar. 
		 * @param idWs 
		 * @param maxReintents 
		 * @param consultaError */
		public GuardarAnotacioPendentThread(
				Long anotacioId,
				AnotacioRegistreId idWs,
				int maxReintents,
				int consultaIntents,
				String consultaError,
				Date consultaData) {
			this.anotacioId = anotacioId;
			this.idWs = idWs;
			this.maxReintents = maxReintents;
			this.consultaIntents = consultaIntents;
			this.consultaError = consultaError;
			this.consultaData = consultaData;
		}

		@Override
		public void run() {
			// Posa una autenticació per defecte per l'usuari del registre
			List<GrantedAuthority> rols = new ArrayList<GrantedAuthority>();
			rols.add(new SimpleGrantedAuthority("tothom"));
			Authentication authentication = new AnonymousAuthenticationToken(
					"DISTRIBUCIO", 
					"DISTRIBUCIO",
					rols);
			SecurityContextHolder.getContext().setAuthentication(authentication);	
			// Consulta la anotació a Distribucio
			AnotacioRegistreEntrada anotacioRegistreEntrada = null;
			try {
				anotacioRegistreEntrada = distribucioHelper.consulta(idWs);
			} catch(Exception e) {
				consultaError  = "Error consultant l'anotació " + idWs.getIndetificador() + " i clau " + idWs.getClauAcces() + ". Intent " + consultaIntents + " de " + maxReintents + ": " + e.getMessage();
				logger.error(consultaError, e);
							
				if (consultaIntents >= maxReintents) {
					// Comunica l'error a Distribucio
					try {
						distribucioHelper.canviEstat(
									idWs, 
									es.caib.distribucio.rest.client.integracio.domini.Estat.ERROR,
									"Error consultant l'anotació amb id " + idWs.getIndetificador() + " després de " + consultaIntents + " intents: " + e.getMessage());
					} catch(Exception ed) {
						logger.error("Error comunicant l'error de consulta a Distribucio de la petició amb id : " + idWs.getIndetificador() + ": " + ed.getMessage(), ed);
					}
				}
			}			
			distribucioHelper.updateConsulta(anotacioId, consultaIntents, consultaError, consultaData);
			// Actualitza la informació de l'anotació amb les dades consultades i la posa en estat pendent.
			logger.debug("Anotació " + idWs.getIndetificador() + " consultada correctament. Actualitzant la informació i estat a PENDENT.");
				
			if (anotacioRegistreEntrada != null ) {
				try {
					distribucioHelper.updateAnotacio(anotacioId, anotacioRegistreEntrada);
					// Processa i comunica l'estat de processada 
					logger.debug("Processant l'anotació " + idWs.getIndetificador() + ".");
					BackofficeArxiuUtils backofficeUtils = new BackofficeArxiuUtilsImpl(pluginHelper.getArxiuPlugin());
					distribucioHelper.processarAnotacio(idWs, anotacioRegistreEntrada, anotacioId, backofficeUtils);
				} catch (Throwable e) {
					String message = exceptionHelper.getRouteCauses(e);
					String errorProcessament = "Error processant l'anotació " + idWs.getIndetificador() + ":" + message;
					logger.error(errorProcessament, e);
					distribucioHelper.updateErrorProcessament(anotacioId, errorProcessament);
	
					// Es comunica l'estat a Distribucio
					try {
						distribucioHelper.canviEstat(
								idWs, 
								es.caib.distribucio.rest.client.integracio.domini.Estat.ERROR,
								"Error processant l'anotació amb id " + idWs.getIndetificador() + ": " + e.getMessage());
					} catch(Exception ed) {
						logger.error("Error comunicant l'error de processament a Distribucio de la petició amb id : " + idWs.getIndetificador() + ": " + ed.getMessage(), ed);
					}
				}
			}					
		}	
	}
	
	
	
	/** Tasca programada per comprovar les anotacions pendents de consultar periòdicament 
	 * a DISTRIBUCIO. Entre comrpovació i comprovació hi ha un període de 10 segons. 
	 * Les anotacions es consultaran fins a un màxim de n reintents definits per la 
	 * propietat <i>app.anotacions.pendents.comprovar.intents</i> amb un valor per defecte
	 * de 5 reintents.
	 */
	@Override
	//@Scheduled(fixedDelayString = "10000")
	public void comprovarAnotacionsPendents() {
		int maxReintents = this.getConsultaAnotacioMaxReintents();
		int maxAnotacions = 100;
		AnotacioRegistreId idWs;
		int maxThreadsParallel = this.getMaxThreadsParallel();		
		List<Anotacio> anotacionsPendentsConsultar;
		anotacionsPendentsConsultar = distribucioHelper.findPendentConsultar(maxReintents, maxAnotacions);
		if (anotacionsPendentsConsultar != null && !anotacionsPendentsConsultar.isEmpty()) {
			String consultaError = null;
			Date consultaData = new Date();
			long startTime = new Date().getTime();
			ExecutorService executor = Executors.newFixedThreadPool(maxThreadsParallel);
			for (Anotacio anotacioPendent : anotacionsPendentsConsultar) {
				idWs = new AnotacioRegistreId();
				idWs.setIndetificador(anotacioPendent.getIdentificador());
				idWs.setClauAcces(anotacioPendent.getDistribucioClauAcces());
				int consultaIntents = anotacioPendent.getConsultaIntents() + 1;
				Runnable thread =
						new GuardarAnotacioPendentThread(
								anotacioPendent.getId(),
								idWs,
								maxReintents,
								consultaIntents,
								consultaError,
								consultaData);
				executor.execute(thread);
			}
			
	        executor.shutdown();
	        while (!executor.isTerminated()) {
	        	try {
	        		executor.awaitTermination(100, TimeUnit.MILLISECONDS);
	        	} catch (InterruptedException e) {}
	        }
	        long stopTime = new Date().getTime();
			logger.trace("Finished processing annotacions with " + maxThreadsParallel + " threads. " + anotacionsPendentsConsultar.size() + " annotacions processed in " + (stopTime - startTime) + "ms");	
		}				
	}
	

	
	private int getConsultaAnotacioMaxReintents() {
		int maxReintents = 5;
		try {
			String strVal = GlobalProperties.getInstance().getProperty("app.anotacions.pendents.comprovar.intents", "5");
			if (strVal != null && !"".equals(strVal.trim())) {
				maxReintents = Integer.parseInt(strVal);
			}
		} catch (Exception ex) {
			logger.warn("Error llegint la propietat 'app.anotacions.pendents.comprovar.intents':" + ex.getMessage() );
		}
		return maxReintents;
	}
	
	private int getMaxThreadsParallel() {
		int maxThreads = 5;
		try {
			String strVal = GlobalProperties.getInstance().getProperty("app.anotacions.consulta.num.threads", "5");
			if (strVal != null && !"".equals(strVal.trim())) {
				maxThreads = Integer.parseInt(strVal);
			}
		} catch (Exception ex) {
			logger.warn("Error llegint la propietat 'app.anotacions.consulta.num.threads':" + ex.getMessage() );
		}
		return maxThreads;
	}

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
	
	/** Mètode per implementar la interfície {@link ArxiuPluginListener} de Distribució per rebre events de quan es crida l'Arxiu i afegir
	 * els logs al monitor d'integracions. 
	 * @param metode
	 * @param parametres
	 * @param correcte
	 * @param error
	 * @param e
	 * @param timeMs
	 */
	@Override
	public void event(String metode, Map<String, String> parametres, boolean correcte, String error, Exception e, long timeMs) {
		
		IntegracioParametreDto[] parametresMonitor = new IntegracioParametreDto[parametres.size()];
		int i = 0;
		for (String nom : parametres.keySet())
			parametresMonitor[i++] = new IntegracioParametreDto(nom, parametres.get(nom));
		
		if (correcte) {
			monitorIntegracioHelper.addAccioOk(
					MonitorIntegracioHelper.INTCODI_ARXIU, 
					"Invocació al mètode del plugin d'Arxiu " + metode, 
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					timeMs, 
					parametresMonitor);
		} else {
			monitorIntegracioHelper.addAccioError(
					MonitorIntegracioHelper.INTCODI_ARXIU, 
					"Error invocant al mètode del plugin d'Arxiu " + metode, 
					IntegracioAccioTipusEnumDto.ENVIAMENT, 
					timeMs,
					error, 
					e, 
					parametresMonitor);
		}
	}

	/** Mètode periòdic per sincronitzar les taules internes d'unitats organitzatives i procediments
	 * segons la propietat app.unitats.procediments.sync.
	 */
	@Override
//	@Scheduled(cron="${app.unitats.procediments.sync}")
	@Async
	public void actualitzarUnitatsIProcediments() {
		logger.info("Inici de la tasca periòdica de sincronització d'unitats i procediments.");
		// Actualitza unitats organitzatives
		try {
			ParametreDto parametreArrel = parametreService.findByCodi(ParametreService.APP_CONFIGURACIO_CODI_ARREL_UO);
			logger.info("Sincronització d'unitats organitzatives amb codi arrel " + (parametreArrel != null ? parametreArrel.getValor() : null) + "...");
			UnitatOrganitzativaDto unitatDto = unitatOrganitzativaService.findByCodi(parametreArrel.getValor());
			unitatOrganitzativaService.synchronize(unitatDto.getId());	
		} catch(Throwable th) {
			logger.error("Error no controlat sincronitzant unitats organitzatives: " + th.getMessage(), th);
		}
		// Actualtiza procediments
		try {
			logger.info("Actualització de procediments...");
			procedimentService.actualitzaProcediments();
			ProgresActualitzacioDto data = procedimentService.getProgresActualitzacio();
			if (data != null) {
					if (data.getAvisos() != null && !data.getAvisos().isEmpty()) {
						for (String avis : data.getAvisos()) {
							logger.warn("Avís en l'actualització de procediments: " + avis);
						}
					}
					logger.info("Resum de l'actualtizació de procediments: \n" + 
							"\t- Total: " + data.getNumOperacions() + "\n" + 
							"\t- Nous: " + data.getNNous() + "\n" + 
							"\t- Extingits: " + data.getNExtingits() + "\n" + 
							"\t- Canvis: " + data.getNCanvis() + "\n" + 
							"\t- Avisos: " + data.getNAvisos() + "\n" + 
							"\t- Errors: " + data.getNErrors()  + "\n");
				if (data.isError())  {
					logger.error("Error detectat en l'actualització de procediments: " + data.getErrorMsg());
				}
			}
		} catch(Throwable th) {
			logger.error("Error no controlat sincronitzant procediments: " + th.getMessage(), th);
		}
		logger.info("Fi de la tasca periòdica de sincronització d'unitats i procediments.");
	}
	

	@Override
//	@Scheduled(fixedDelayString = "600000")
	@Async
	@Transactional
	public void updatePeticionsAsincronesPinbal() throws ExecucioMassivaException {
		List<PeticioPinbal> peticionsAsincronesPendents = peticioPinbalRepository.findAsincronesPendents();
		if (peticionsAsincronesPendents!=null) {
			for (PeticioPinbal pi: peticionsAsincronesPendents) {
				consultaPinbalHelper.tractamentPeticioAsincronaPendentPinbal(pi.getId());
			}
		}
	}	
	
	
	/** Tasca programada per comprovar si hi ha enviment de correus no agrupats no pendents
	 */
	@Override
	@Transactional
//	@Scheduled(fixedDelayString = "60000")
	public void comprovarEmailAnotacionsNoAgrupats() {
		// Consultar entrades de la taula HEL_ANOTACIO_EMAIL amb agrupat = 0
		List<AnotacioEmail> anotacioEmailListNoAgrupats=anotacioEmailRepository.findByEnviamentAgrupatOrderByDestinatariCodi(false);
		boolean fi = anotacioEmailListNoAgrupats!=null && anotacioEmailListNoAgrupats.isEmpty();	
		while(!fi) {
			try {
				for(AnotacioEmail anotacioEmail: anotacioEmailListNoAgrupats) {
					//enviar email correu (no agrupat) de creació/incorporació/arribada d'anotació
					emailHelper.sendAnotacioEmailNoAgrupat(anotacioEmail, anotacioEmailListNoAgrupats);
					// Esborrar les que s'hagin pogut enviar
					anotacioEmailListNoAgrupats.remove(anotacioEmail);
					anotacioEmailRepository.delete(anotacioEmail);
				}
			}catch(Exception e) {
				// Si l'error és que l'email no existeix o no és correcte, igualment eliminar-lo.
				// Posarem un número de reintents i passat aquest límit s'eliminarà.
				logger.error("Error enviant l'email d'anotació: " + e.getMessage(), e);
			}
			fi = anotacioEmailListNoAgrupats!=null && anotacioEmailListNoAgrupats.isEmpty();
		}	
		// Eliminar correus més antics de 3 dies pedents d'enviar
		for (AnotacioEmail anotacioEmail : anotacioEmailListNoAgrupats) {
			// remove pending email if it is older that one week
			Date formattedToday = new Date();
			Date formattedExpired = anotacioEmail.getDataCreacio();
			int diffInDays = (int)( (formattedToday.getTime() - formattedExpired.getTime()) / (1000 * 60 * 60 * 24) );
			if (diffInDays > 2) {
				anotacioEmailRepository.delete(anotacioEmail);
			}
		}
	}
	
	/** Mètode periòdic per enviar correus agrupats de noves anotacions
	 * segons la propietat app.anotacions.emails.agrupats.cron , Per defecte a les 20h
	 */
	@Override
//	@Scheduled(cron="${app.anotacions.emails.agrupats.cron}")
	@Async
	public void comprovarEmailAnotacionsAgrupats() {
		logger.info("Inici de la tasca periòdica d'enviament de correus agrupats de noves anotacions de distribució.");
	
		List<AnotacioEmail> anotacioEmailAgrupatList = anotacioEmailRepository.findByEnviamentAgrupatOrderByDestinatariCodi(true);
		if(anotacioEmailAgrupatList!=null && !anotacioEmailAgrupatList.isEmpty()) {
			// Agrupa per destinataris
			Map<String, List<AnotacioEmail>> anotacioEmailAgrupatMap = new HashMap<String, List<AnotacioEmail>>();
			for (AnotacioEmail anotacioEmail : anotacioEmailAgrupatList) {
				if (anotacioEmailAgrupatMap.containsKey(anotacioEmail.getDestinatariEmail())) {
					anotacioEmailAgrupatMap.get(anotacioEmail.getDestinatariEmail()).add(anotacioEmail);
				} else {
					List<AnotacioEmail> lContingutEmails = new ArrayList<AnotacioEmail>();
					lContingutEmails.add(anotacioEmail);
					anotacioEmailAgrupatMap.put(anotacioEmail.getDestinatariEmail(), lContingutEmails);
				}
			}	
			// Envia i esborra per agrupació
			for (String email: anotacioEmailAgrupatMap.keySet()) {	
				anotacioEmailAgrupatList = anotacioEmailAgrupatMap.get(email);
				try {
					emailHelper.sendAnotacioEmailsPendentsAgrupats(
							email, 
							anotacioEmailAgrupatList);	
					logger.info("Enviat el correu de " + anotacioEmailAgrupatList.size() + " anotacions agrupades al destinatari " + email);
							
				} catch (Exception e) {
					logger.error("Error enviant el correu de " + anotacioEmailAgrupatList.size() + " anotacions agrupades al destinatari " + email + ": " + e.getMessage());		
					for (AnotacioEmail anotacioEmail : anotacioEmailAgrupatList) {
							// remove pending email if it is older than 3 days
							Date formattedToday = new Date();
							Date formattedExpired = anotacioEmail.getDataCreacio();
							int diffInDays = (int)( (formattedToday.getTime() - formattedExpired.getTime()) / (1000 * 60 * 60 * 24) );
							if (diffInDays > 2) {
								anotacioEmailRepository.delete(anotacioEmail);
							}
					}
				}
			}
		}
	}
			
	private static final Log logger = LogFactory.getLog(TascaProgramadaService.class);
}