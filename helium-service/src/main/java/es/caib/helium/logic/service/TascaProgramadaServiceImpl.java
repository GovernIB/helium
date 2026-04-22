package es.caib.helium.logic.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.codahale.metrics.MetricRegistry;

import es.caib.distribucio.backoffice.utils.arxiu.ArxiuPluginListener;
import es.caib.distribucio.backoffice.utils.arxiu.BackofficeArxiuUtils;
import es.caib.distribucio.backoffice.utils.arxiu.BackofficeArxiuUtilsImpl;
import es.caib.distribucio.rest.client.integracio.domini.AnotacioRegistreEntrada;
import es.caib.distribucio.rest.client.integracio.domini.AnotacioRegistreId;
import es.caib.helium.commons.dto.AnotacioEstatEnumDto;
import es.caib.helium.commons.dto.DocumentEnviamentEstatEnumDto;
import es.caib.helium.commons.dto.DocumentNotificacioTipusEnumDto;
import es.caib.helium.commons.dto.EmailTipusEnumDto;
import es.caib.helium.commons.dto.IntegracioAccioTipusEnumDto;
import es.caib.helium.commons.dto.IntegracioParametreDto;
import es.caib.helium.commons.dto.ParametreDto;
import es.caib.helium.commons.dto.UnitatOrganitzativaDto;
import es.caib.helium.commons.dto.procediment.ProgresActualitzacioDto;
import es.caib.helium.commons.exception.ExecucioMassivaException;
import es.caib.helium.commons.exception.NoTrobatException;
import es.caib.helium.commons.utils.GlobalProperties;
import es.caib.helium.logic.intf.service.ExecucioMassivaService;
import es.caib.helium.logic.intf.service.ExpedientDocumentService;
import es.caib.helium.logic.intf.service.ExpedientService;
import es.caib.helium.logic.intf.service.ParametreService;
import es.caib.helium.logic.intf.service.ProcedimentService;
import es.caib.helium.logic.intf.service.TascaProgramadaService;
import es.caib.helium.logic.intf.service.UnitatOrganitzativaService;
import es.caib.helium.persistence.entity.Anotacio;
import es.caib.helium.persistence.entity.AnotacioEmail;
import es.caib.helium.persistence.entity.ExecucioMassiva.ExecucioMassivaTipus;
import es.caib.helium.persistence.entity.ExecucioMassivaExpedient;
import es.caib.helium.persistence.entity.Notificacio;
import es.caib.helium.persistence.entity.PeticioPinbal;
import es.caib.helium.persistence.repository.AnotacioEmailRepository;
import es.caib.helium.persistence.repository.DocumentStoreRepository;
import es.caib.helium.persistence.repository.ExecucioMassivaExpedientRepository;
import es.caib.helium.persistence.repository.ExpedientReindexacioRepository;
import es.caib.helium.persistence.repository.ExpedientRepository;
import es.caib.helium.persistence.repository.NotificacioRepository;
import es.caib.helium.persistence.repository.PeticioPinbalRepository;
import es.caib.helium.logic.helper.ConsultaPinbalHelper;
import es.caib.helium.logic.helper.DistribucioHelper;
import es.caib.helium.logic.helper.DocumentHelperV3;
import es.caib.helium.logic.helper.EmailHelper;
import es.caib.helium.logic.helper.ExceptionHelper;
import es.caib.helium.logic.helper.ExpedientHelper;
import es.caib.helium.logic.helper.IndexHelper;
import es.caib.helium.logic.helper.MonitorIntegracioHelper;
import es.caib.helium.logic.helper.NotificacioHelper;
import es.caib.helium.logic.helper.PluginHelper;

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
	private DocumentStoreRepository documentStoreRepository;
	@Autowired
	private ExpedientService expedientService;
	@Autowired
	private ExpedientDocumentService expedientDocumentService;
	@Resource(name = "documentHelperV3")
	private DocumentHelperV3 documentHelper;


	@Resource
	private AnotacioEmailRepository anotacioEmailRepository;
	@Resource
	private PeticioPinbalRepository peticioPinbalRepository;
	@Resource
	private ConsultaPinbalHelper consultaPinbalHelper;
	@Autowired
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
					ExecucioMassivaExpedient ome = execucioMassivaExpedientRepository.findById(ome_id).orElse(null);
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
		Notificacio notificacio = notificacioRepository.findById(notificacioId).orElse(null);
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
					// Es guarda l'anotació i es determina si queda com a pendent o pendent de processament automàtic
					Anotacio anotacio = distribucioHelper.updateAnotacio(anotacioId, anotacioRegistreEntrada);
					// Es comunica l'estat a Distribucio
					try {
						if(anotacio.getEstat() == AnotacioEstatEnumDto.REBUTJADA) {
						// if(anotacio.getExpedientTipus() == null) {
							distribucioHelper.rebutjar(
									anotacio,
									"No hi ha cap tipus d'expedient per processar anotacions amb codi de procediment " + anotacio.getProcedimentCodi() +
									" i assumpte " + (anotacio.getAssumpteCodiCodi()!=null ? anotacio.getAssumpteCodiCodi() : "(sense assumpte)") +", es rebutja automàticament amb data "
									+ new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date()) +
									". Petició rebutjada a Helium.");
						} else {
							distribucioHelper.canviEstat(
									idWs,
									es.caib.distribucio.rest.client.integracio.domini.Estat.PENDENT,
									"Anotació " + idWs.getIndetificador() + " rebuda correctament." );
							//Si l'estat és Pendent manual, encuem l'email
							if(AnotacioEstatEnumDto.PENDENT.equals(anotacio.getEstat()) &&
									anotacio.getExpedientTipus()!=null &&
									!anotacio.getExpedientTipus().isDistribucioProcesAuto() &&
									anotacio.getExpedientTipus().isEnviarCorreuAnotacions()) {
								emailHelper.createEmailsAnotacioToSend(
										anotacio,
										anotacio.getExpedient(),
										EmailTipusEnumDto.REBUDA_PENDENT);
							}
					}
					} catch(Exception ed) {
						logger.error("Error comunicant l'estat d'anotació rebuda a Distribucio de la petició amb id : " + idWs.getIndetificador() + ": " + ed.getMessage(), ed);
					}
					logger.debug("Anotació " + idWs.getIndetificador() + " consultada correctament amb estat " + anotacio.getEstat());
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

	/** Tasca programada per comprovar les anotacions que estan en estat de pendents de processament
	 * automàtic. Entre comrpovació i comprovació hi ha un període de 10 segons.
	 */
	@Override
	public void processarAnotacionsAutomatiques() {
		int maxAnotacions = 100;
		AnotacioRegistreId idWs;
		List<Anotacio> anotacionsPendentsProcessar = distribucioHelper.findPendentProcessarAuto(maxAnotacions);
		if (anotacionsPendentsProcessar != null && !anotacionsPendentsProcessar.isEmpty()) {
			// Posa una autenticació per defecte per l'usuari del registre
			List<GrantedAuthority> rols = new ArrayList<GrantedAuthority>();
			rols.add(new SimpleGrantedAuthority("tothom"));
			Authentication authentication = new AnonymousAuthenticationToken(
					"DISTRIBUCIO",
					"DISTRIBUCIO",
					rols);
			SecurityContextHolder.getContext().setAuthentication(authentication);
	        long startTime = new Date().getTime();
			for (Anotacio anotacioPendent : anotacionsPendentsProcessar) {
				idWs = new AnotacioRegistreId();
				idWs.setIndetificador(anotacioPendent.getIdentificador());
				idWs.setClauAcces(anotacioPendent.getDistribucioClauAcces());
				// Processa i comunica l'estat de processada
				logger.debug("Processant l'anotació " + idWs.getIndetificador() + ".");
				BackofficeArxiuUtils backofficeUtils = new BackofficeArxiuUtilsImpl(pluginHelper.getArxiuPlugin());
				try {
					distribucioHelper.processarAnotacio(idWs, anotacioPendent.getId(), backofficeUtils);
				} catch(Exception e) {
					logger.error("Error processant automàticament l'anotació " + anotacioPendent.getIdentificador() + ": " + e.getMessage());
				}
			}
	        long stopTime = new Date().getTime();
			logger.trace("Finished processing annotacions. " + anotacionsPendentsProcessar.size() + " annotacions processed in " + (stopTime - startTime) + "ms");
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
			String errorText = ExceptionHelper.getErrorText(error);
			errorsMassiva.put(operacioMassivaId, errorText);
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

	/** Mètode periòdic per sincronitzar les taules internes d'unitats organitzatives, procediments i serveis
	 * segons la propietat app.unitats.procediments.sync.
	 */
	@Override
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

		// Actualtiza serveis
		try {
			logger.info("Actualització de serveis...");
			procedimentService.actualitzaServeis();
			ProgresActualitzacioDto data = procedimentService.getProgresServisActualitzacio();
			if (data != null) {
					if (data.getAvisos() != null && !data.getAvisos().isEmpty()) {
						for (String avis : data.getAvisos()) {
							logger.warn("Avís en l'actualització de serveis: " + avis);
						}
					}
					logger.info("Resum de l'actualtizació de serveis: \n" +
							"\t- Total: " + data.getNumOperacions() + "\n" +
							"\t- Nous: " + data.getNNous() + "\n" +
							"\t- Extingits: " + data.getNExtingits() + "\n" +
							"\t- Canvis: " + data.getNCanvis() + "\n" +
							"\t- Avisos: " + data.getNAvisos() + "\n" +
							"\t- Errors: " + data.getNErrors()  + "\n");
				if (data.isError())  {
					logger.error("Error detectat en l'actualització de serveis: " + data.getErrorMsg());
				}
			}
		} catch(Throwable th) {
			logger.error("Error no controlat sincronitzant serveis: " + th.getMessage(), th);
		}

		logger.info("Fi de la tasca periòdica de sincronització d'unitats, procediments i serveis.");
	}


	@Override
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
	public void comprovarEmailAnotacionsNoAgrupats() {
		// Consultar entrades de la taula HEL_ANOTACIO_EMAIL amb agrupat = 0
		List<AnotacioEmail> anotacioEmailListNoAgrupats=anotacioEmailRepository.findByEnviamentAgrupatOrderByDestinatariCodi(false);
		boolean fi = anotacioEmailListNoAgrupats!=null && anotacioEmailListNoAgrupats.isEmpty();
		while(!fi) {
			List<AnotacioEmail> emailsPerEsborrar=new ArrayList<AnotacioEmail>();
			try {
				for(AnotacioEmail anotacioEmail: anotacioEmailListNoAgrupats) {
					//enviar email correu (no agrupat) de creació/incorporació/arribada d'anotació
					emailHelper.sendAnotacioEmailNoAgrupat(anotacioEmail, anotacioEmailListNoAgrupats);
					// Esborrar les que s'hagin pogut enviar
					emailsPerEsborrar.add(anotacioEmail);
					anotacioEmailRepository.delete(anotacioEmail);
				}
				anotacioEmailListNoAgrupats.removeAll(emailsPerEsborrar);
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

	/**
	 * Mètode per cercar i intentar mirgrar a arxiu tots els expedients pendents amb reintents
	 */
	@Override
	@Transactional
	public void migrarExpedientsDocumentsArxiu() {
		try {
			String value = GlobalProperties.getInstance().getProperty("app.arxiu.migracio.reintents");
			Long maxReintents = (value == null || value.isEmpty())? 0L : Long.parseLong(value);
			if (maxReintents == 0L) {
				return;
			}
			// Provam de migrar els expedients que pertanyen a un tipus amb integració amb arxiu activat
			// i no estiguin sicronitzats amb Arxiu
			List<Long> expedientsPendents = expedientRepository.findPendentsArxiu(maxReintents);
			for(Long expedientId : expedientsPendents) {
				try {
					expedientService.trySincronitzarArxiu(expedientId);
				} catch (Exception ex) {
					logger.debug("Error inesperat migrant expedient amb id:" + expedientId + " a l'arxiu: " + ex.getMessage());
				}
			}

			// Cercam els documents pendents de sincronitzar amb arxiu
			Iterator documentsPendents = documentStoreRepository.findDocumentsPendentsArxiu(maxReintents).iterator();
			while(documentsPendents.hasNext()) {
				Object[] obj = (Object[]) documentsPendents.next();
				Long expedientId = Long.valueOf(obj[0].toString());
				Long documentStoreId = Long.valueOf(obj[1].toString());
				try {
					expedientDocumentService.trySincronitzarArxiu(expedientId, documentStoreId);
				} catch (Exception ex) {
					logger.debug("Error inesperat migrant document amb id: " + documentStoreId + " del expedient :" + expedientId + " a l'arxiu: " + ex.getMessage());
				}
			}
		} catch(Exception e) {
			logger.error("Error migrant expedients a Arxiu: " + e.getMessage());
		}
	}

	@Override
	public void restartSchedulledTasks(String taskCodi) {
//		tascaProgramadaConfig.restartSchedulledTasks(taskCodi);
	}

	private static final Log logger = LogFactory.getLog(TascaProgramadaService.class);
}
