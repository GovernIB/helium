/**
 *
 */
package es.caib.helium.logic.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.validation.ValidationException;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.security.acls.model.Permission;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.google.common.collect.Lists;

import es.caib.comanda.model.management.TascaEstat;
import es.caib.distribucio.backoffice.utils.arxiu.ArxiuPluginListener;
import es.caib.distribucio.backoffice.utils.arxiu.ArxiuResultat;
import es.caib.distribucio.backoffice.utils.arxiu.BackofficeArxiuUtils;
import es.caib.distribucio.backoffice.utils.arxiu.BackofficeArxiuUtilsImpl;
import es.caib.distribucio.rest.client.integracio.domini.AnotacioRegistreEntrada;
import es.caib.distribucio.rest.client.integracio.domini.AnotacioRegistreId;
import es.caib.helium.commons.constants.ExpedientCamps;
import es.caib.helium.commons.dto.AccioDto;
import es.caib.helium.commons.dto.AlertaDto;
import es.caib.helium.commons.dto.AnotacioAnnexEstatEnumDto;
import es.caib.helium.commons.dto.AnotacioMapeigResultatDto;
import es.caib.helium.commons.dto.ArxiuContingutDto;
import es.caib.helium.commons.dto.ArxiuContingutTipusEnumDto;
import es.caib.helium.commons.dto.ArxiuDetallDto;
import es.caib.helium.commons.dto.ArxiuDto;
import es.caib.helium.commons.dto.CampDto;
import es.caib.helium.commons.dto.DadaIndexadaDto;
import es.caib.helium.commons.dto.DadesDocumentDto;
import es.caib.helium.commons.dto.DadesNotificacioDto;
import es.caib.helium.commons.dto.DefinicioProcesDto;
import es.caib.helium.commons.dto.DefinicioProcesExpedientDto;
import es.caib.helium.commons.dto.DocumentDto;
import es.caib.helium.commons.dto.DocumentStoreBackupDto;
import es.caib.helium.commons.dto.EntornDto;
import es.caib.helium.commons.dto.EstatDto;
import es.caib.helium.commons.dto.ExpedientConsultaDissenyDto;
import es.caib.helium.commons.dto.ExpedientDocumentDto;
import es.caib.helium.commons.dto.ExpedientDto;
import es.caib.helium.commons.dto.ExpedientDto.EstatTipusDto;
import es.caib.helium.commons.dto.ExpedientDto.IniciadorTipusDto;
import es.caib.helium.commons.dto.ExpedientErrorDto;
import es.caib.helium.commons.dto.ExpedientErrorDto.ErrorTipusDto;
import es.caib.helium.commons.dto.ExpedientTascaDto;
import es.caib.helium.commons.dto.ExpedientTipusDto;
import es.caib.helium.commons.dto.ExpedientTipusTipusEnumDto;
import es.caib.helium.commons.dto.InstanciaProcesDto;
import es.caib.helium.commons.dto.IntegracioAccioTipusEnumDto;
import es.caib.helium.commons.dto.IntegracioParametreDto;
import es.caib.helium.commons.dto.MostrarAnulatsDto;
import es.caib.helium.commons.dto.NtiExpedienteEstadoEnumDto;
import es.caib.helium.commons.dto.PaginaDto;
import es.caib.helium.commons.dto.PaginacioParamsDto;
import es.caib.helium.commons.dto.PaginacioParamsDto.OrdreDireccioDto;
import es.caib.helium.commons.dto.PaginacioParamsDto.OrdreDto;
import es.caib.helium.commons.dto.PersonaDto;
import es.caib.helium.commons.dto.PortafirmesEstatEnum;
import es.caib.helium.commons.dto.RespostaValidacioSignaturaDto;
import es.caib.helium.commons.dto.TascaDadaDto;
import es.caib.helium.commons.exception.NoTrobatException;
import es.caib.helium.commons.exception.PermisDenegatException;
import es.caib.helium.commons.exception.SistemaExternException;
import es.caib.helium.commons.exception.TramitacioException;
import es.caib.helium.commons.exception.TramitacioValidacioException;
import es.caib.helium.commons.utils.EntornActual;
import es.caib.helium.commons.utils.MessageHelper;
import es.caib.helium.logic.helper.AlertaHelper;
import es.caib.helium.logic.helper.ComandaHelper;
import es.caib.helium.logic.helper.ConsultaHelper;
import es.caib.helium.logic.helper.ConversioTipusHelper;
import es.caib.helium.logic.helper.DistribucioHelper;
import es.caib.helium.logic.helper.DocumentHelperV3;
import es.caib.helium.logic.helper.EntornHelper;
import es.caib.helium.logic.helper.ExceptionHelper;
import es.caib.helium.logic.helper.ExpedientDadaHelper;
import es.caib.helium.logic.helper.ExpedientDocumentHelper;
import es.caib.helium.logic.helper.ExpedientHelper;
import es.caib.helium.logic.helper.ExpedientLoggerHelper;
import es.caib.helium.logic.helper.ExpedientRegistreHelper;
import es.caib.helium.logic.helper.ExpedientTipusHelper;
import es.caib.helium.logic.helper.HerenciaHelper;
import es.caib.helium.logic.helper.MonitorIntegracioHelper;
import es.caib.helium.logic.helper.NotificacioHelper;
import es.caib.helium.logic.helper.PaginacioHelper;
import es.caib.helium.logic.helper.PermisosHelper;
import es.caib.helium.logic.helper.PluginHelper;
import es.caib.helium.logic.helper.TascaHelper;
import es.caib.helium.logic.helper.UnitatOrganitzativaHelper;
import es.caib.helium.logic.helper.UsuariActualHelper;
import es.caib.helium.logic.helper.VariableHelper;
import es.caib.helium.logic.helpers.MesuresTemporalsHelper;
import es.caib.helium.disseny.engine.WProcessInstance;
import es.caib.helium.disseny.engine.WTaskInstance;
import es.caib.helium.logic.intf.service.AnotacioService;
import es.caib.helium.logic.intf.service.ExpedientService;
import es.caib.helium.logic.intf.service.ExpedientTipusService;
import es.caib.helium.logic.intf.service.Jbpm3HeliumService;
import es.caib.helium.logic.intf.service.ParametreService;
import es.caib.helium.logic.intf.service.WorkflowEngineApi;
import es.caib.helium.logic.security.ExtendedPermission;
import es.caib.helium.persistence.entity.Accio;
import es.caib.helium.persistence.entity.Alerta;
import es.caib.helium.persistence.entity.Alerta.AlertaPrioritat;
import es.caib.helium.persistence.entity.Anotacio;
import es.caib.helium.persistence.entity.AnotacioAnnex;
import es.caib.helium.persistence.entity.AnotacioEmail;
import es.caib.helium.persistence.entity.Camp;
import es.caib.helium.persistence.entity.Consulta;
import es.caib.helium.persistence.entity.ConsultaCamp.TipusConsultaCamp;
import es.caib.helium.persistence.entity.DefinicioProces;
import es.caib.helium.persistence.entity.Document;
import es.caib.helium.persistence.entity.DocumentNotificacio;
import es.caib.helium.persistence.entity.DocumentStore;
import es.caib.helium.persistence.entity.DocumentStore.DocumentFont;
import es.caib.helium.persistence.entity.Entorn;
import es.caib.helium.persistence.entity.Estat;
import es.caib.helium.persistence.entity.ExecucioMassivaExpedient;
import es.caib.helium.persistence.entity.Expedient;
import es.caib.helium.persistence.entity.ExpedientLog;
import es.caib.helium.persistence.entity.ExpedientLog.ExpedientLogAccioTipus;
import es.caib.helium.persistence.entity.ExpedientLog.ExpedientLogEstat;
import es.caib.helium.persistence.entity.ExpedientTipus;
import es.caib.helium.persistence.entity.ExpedientTipusUnitatOrganitzativa;
import es.caib.helium.persistence.entity.Parametre;
import es.caib.helium.persistence.entity.Portasignatures;
import es.caib.helium.persistence.entity.Registre;
import es.caib.helium.persistence.entity.Termini;
import es.caib.helium.persistence.entity.TerminiIniciat;
import es.caib.helium.persistence.entity.UnitatOrganitzativa;
import es.caib.helium.persistence.repository.AccioRepository;
import es.caib.helium.persistence.repository.AlertaRepository;
import es.caib.helium.persistence.repository.AnotacioEmailRepository;
import es.caib.helium.persistence.repository.AnotacioRepository;
import es.caib.helium.persistence.repository.CampRepository;
import es.caib.helium.persistence.repository.ConsultaRepository;
import es.caib.helium.persistence.repository.DefinicioProcesRepository;
import es.caib.helium.persistence.repository.DocumentNotificacioRepository;
import es.caib.helium.persistence.repository.DocumentRepository;
import es.caib.helium.persistence.repository.DocumentStoreRepository;
import es.caib.helium.persistence.repository.EnumeracioRepository;
import es.caib.helium.persistence.repository.EstatAccioEntradaRepository;
import es.caib.helium.persistence.repository.EstatAccioSortidaRepository;
import es.caib.helium.persistence.repository.EstatRepository;
import es.caib.helium.persistence.repository.ExecucioMassivaExpedientRepository;
import es.caib.helium.persistence.repository.ExpedientHeliumRepository;
import es.caib.helium.persistence.repository.ExpedientLoggerRepository;
import es.caib.helium.persistence.repository.ExpedientRepository;
import es.caib.helium.persistence.repository.ExpedientTipusRepository;
import es.caib.helium.persistence.repository.ExpedientTipusUnitatOrganitzativaRepository;
import es.caib.helium.persistence.repository.ParametreRepository;
import es.caib.helium.persistence.repository.PeticioPinbalRepository;
import es.caib.helium.persistence.repository.PortasignaturesRepository;
import es.caib.helium.persistence.repository.RegistreRepository;
import es.caib.helium.persistence.repository.TerminiIniciatRepository;
import es.caib.helium.persistence.repository.TerminiRepository;
import es.caib.helium.persistence.repository.UnitatOrganitzativaRepository;
import es.caib.pluginsib.arxiu.api.ContingutArxiu;
import es.caib.pluginsib.arxiu.api.ExpedientMetadades;
import es.caib.pluginsib.arxiu.caib.ArxiuConversioHelper;

/**
 * Implementació dels mètodes del servei ExpedientService.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Service("expedientServiceV3")
public class ExpedientServiceImpl implements ExpedientService, ArxiuPluginListener {

	private static List<Long> currentlyMigratingExpedients = Collections.synchronizedList(new ArrayList<Long>());

	private ExpedientService self;
	@Autowired
	private ApplicationContext applicationContext;
	@Resource
	private DocumentRepository documentRepository;
	@Resource
	private ExpedientRepository expedientRepository;
	@Resource
	private ExpedientHeliumRepository expedientHeliumRepository;
	@Resource
	private ExpedientTipusRepository expedientTipusRepository;
	@Resource
	private EstatRepository estatRepository;
	@Resource
	private ConsultaRepository consultaRepository;
	@Resource
	private ExpedientLoggerRepository expedientLogRepository;
	@Resource
	private CampRepository campRepository;
	@Resource
	private AlertaRepository alertaRepository;
	@Resource
	private RegistreRepository registreRepository;
	@Resource
	private EnumeracioRepository enumeracioRepository;
	@Resource
	private TerminiRepository terminiRepository;
	@Resource
	private TerminiIniciatRepository terminiIniciatRepository;
	@Resource
	private DefinicioProcesRepository definicioProcesRepository;
	@Resource
	private DocumentStoreRepository documentStoreRepository;
	@Resource
	private AccioRepository accioRepository;
	@Resource
	private ExecucioMassivaExpedientRepository execucioMassivaExpedientRepository;
	@Resource
	private PortasignaturesRepository portasignaturesRepository;
	@Resource
	private DocumentNotificacioRepository documentNotificacioRepository;
	@Resource
	private AnotacioRepository anotacioRepository;
	@Resource
	private EstatAccioEntradaRepository estatAccioEntradaRepository;
	@Resource
	private EstatAccioSortidaRepository estatAccioSortidaRepository;
	@Resource
	private PeticioPinbalRepository peticioPinbalRepository;

	@Resource
	private ExpedientTipusUnitatOrganitzativaRepository expedientTipusUnitatOrganitzativaRepository;
	@Resource
	private UnitatOrganitzativaRepository unitatOrganitzativaRepository;
	@Resource
	private ParametreRepository parametreRepository;
	@Resource
	private AnotacioEmailRepository anotacioEmailRepository;
	@Resource
	private ExpedientHelper expedientHelper;
	@Resource
	private ExpedientRegistreHelper expedientRegistreHelper;
	@Resource
	private MessageHelper messageHelper;
	@Resource
	private EntornHelper entornHelper;
	@Resource
	private ConsultaHelper consultaHelper;
	@Resource
	private ExpedientTipusHelper expedientTipusHelper;
	@Resource
	private WorkflowEngineApi workflowEngineApi;
	@Resource
	private VariableHelper variableHelper;
	@Resource(name="documentHelperV3")
	private DocumentHelperV3 documentHelper;
	@Resource(name="pluginHelperV3")
	private PluginHelper pluginHelper;
	@Resource
	private TascaHelper tascaHelper;
	@Resource
	private ConversioTipusHelper conversioTipusHelper;
//	@Resource
//	private LuceneHelper luceneHelper;
	@Resource(name="permisosHelperV3")
	private PermisosHelper permisosHelper;
	@Resource
	private PaginacioHelper paginacioHelper;
	@Autowired
	private MesuresTemporalsHelper mesuresTemporalsHelper;
	@Resource
	private ExpedientLoggerHelper expedientLoggerHelper;
	@Autowired
	private ExpedientDadaHelper expedientDadaHelper;
	@Autowired
	private ExpedientDocumentHelper expedientDocumentHelper;
	@Resource
	private NotificacioHelper notificacioHelper;
	@Resource
	private MonitorIntegracioHelper monitorIntegracioHelper;
	@Resource
	private HerenciaHelper herenciaHelper;
	@Resource
	private DistribucioHelper distribucioHelper;
	@Resource
	private AnotacioService anotacioService;
	@Resource
	private AlertaHelper alertaHelper;
	@Resource
	private ExceptionHelper exceptionHelper;
	@Resource
	private UsuariActualHelper usuariActualHelper;
	@Resource
	private UnitatOrganitzativaHelper unitatOrganitzativaHelper;
	@Resource
	private ExpedientTipusService expedientTipusService;
	@Resource
	private Jbpm3HeliumService jbpm3HeliumService;
	@Autowired
	private ComandaHelper comandaHelper;

	@Autowired
	private ApplicationEventPublisher eventPublisher;
	@Autowired
	private EntityManager entityManager;

	private final ConcurrentMap<Long, Object> anotaciosInPorcess = new ConcurrentHashMap<Long, Object>();

	@PostConstruct
	public void postContruct() {
		self = applicationContext.getBean(ExpedientService.class);
	}

	@Override
	public ExpedientDto create(
		Long entornId,
		String usuari,
		Long expedientTipusId,
		Long definicioProcesId,
		Integer any,
		String numero,
		String unitatOrganitzativaCodi,
		String titol,
		String registreNumero,
		Date registreData,
		Long unitatAdministrativa,
		String idioma,
		boolean autenticat,
		String tramitadorNif,
		String tramitadorNom,
		String interessatNif,
		String interessatNom,
		String representantNif,
		String representantNom,
		boolean avisosHabilitats,
		String avisosEmail,
		String avisosMobil,
		boolean notificacioTelematicaHabilitada,
		Map<String, Object> variables,
		String transitionName,
		IniciadorTipusDto iniciadorTipus,
		String iniciadorCodi,
		String responsableCodi,
		List<DadesDocumentDto> documents,
		List<DadesDocumentDto> adjunts,
		Long anotacioId,
		boolean anotacioInteressatsAssociar) throws Exception {
		
		ExpedientDto expedientDto = null;
		
		// Evita crear un expedient per la mateixa anotació
		Object lock;
		if(anotacioId != null) {
			Object objVal = new Object();
			lock = anotaciosInPorcess.putIfAbsent(anotacioId, objVal);
			if(lock == null)
				lock = objVal;
		} else {
			lock = new Object();
		}
		synchronized(lock) {
			try {
				expedientDto = createExpedient(
					entornId,
					usuari,
					expedientTipusId,
					definicioProcesId,
					any,
					numero,
					unitatOrganitzativaCodi,
					titol,
					registreNumero,
					registreData,
					unitatAdministrativa,
					idioma,
					autenticat,
					tramitadorNif,
					tramitadorNom,
					interessatNif,
					interessatNom,
					representantNif,
					representantNom,
					avisosHabilitats,
					avisosEmail,
					avisosMobil,
					notificacioTelematicaHabilitada,
					variables,
					transitionName,
					iniciadorTipus,
					iniciadorCodi,
					responsableCodi,
					documents,
					adjunts,
					anotacioId,
					anotacioInteressatsAssociar);
			} finally {
				if(anotacioId != null)
					anotaciosInPorcess.remove(anotacioId);
			}
		}
		return expedientDto;
	}

	/**
	 * {@inheritDoc}
	 */
	@Transactional
	public ExpedientDto createExpedient(
			Long entornId,
			String usuari,
			Long expedientTipusId,
			Long definicioProcesId,
			Integer any,
			String numero,
			String unitatOrganitzativaCodi,
			String titol,
			String registreNumero,
			Date registreData,
			Long unitatAdministrativa,
			String idioma,
			boolean autenticat,
			String tramitadorNif,
			String tramitadorNom,
			String interessatNif,
			String interessatNom,
			String representantNif,
			String representantNom,
			boolean avisosHabilitats,
			String avisosEmail,
			String avisosMobil,
			boolean notificacioTelematicaHabilitada,
			Map<String, Object> variables,
			String transitionName,
			IniciadorTipusDto iniciadorTipus,
			String iniciadorCodi,
			String responsableCodi,
			List<DadesDocumentDto> documents,
			List<DadesDocumentDto> adjunts,
			Long anotacioId,
			boolean anotacioInteressatsAssociar) throws Exception {
		logger.debug("Creant nou expedient (" +
				"entornId=" + entornId + ", " +
				"usuari=" + usuari + ", " +
				"expedientTipusId=" + expedientTipusId + ", " +
				"definicioProcesId=" + definicioProcesId + ", " +
				"any=" + any + ", " +
				"titol=" + titol + ", " +
				"anotacioId=" + anotacioId + ", " +
				"anotacioInteressatsAssociar=" + anotacioInteressatsAssociar + ")");

		Expedient expedient = null;
		Anotacio anotacio = null;
		AnotacioMapeigResultatDto resultatMapeig = null;
		try {
			if (anotacioId != null) {
				anotacio = anotacioRepository.findById(anotacioId).orElse(null);
				ExpedientTipus expedientTipus = expedientTipusRepository.findById(expedientTipusId).orElse(null);
				if (expedientTipus.isDistribucioSistra()) {
					// Si el tipus no integra amb l'Arxiu llavors s'obtindran els documents amb contingut
					boolean ambContingut = !expedientTipus.isArxiuActiu();
					// Extreu documents i variables segons el mapeig sistra
					resultatMapeig = distribucioHelper.getMapeig(expedientTipus, anotacio, ambContingut);

					for(DadesDocumentDto dd : resultatMapeig.getDocuments()) {
						dd.setUuid(null);
					}

					for(DadesDocumentDto dd : resultatMapeig.getAdjunts()) {
						dd.setUuid(null);
					}

					if (variables == null) {
						variables = new HashMap<String, Object>(resultatMapeig.getDades());
					} else {
						// Si ja hi ha variabls només s'afegeixen les del mapeig que no hi siguin
						for (String varCodi : resultatMapeig.getDades().keySet()) {
							if ( ! variables.containsKey(varCodi)) {
								variables.put(varCodi, resultatMapeig.getDades().get(varCodi));
							}
						}
					}
					if (documents == null)
						documents = new ArrayList<DadesDocumentDto>();
					documents.addAll(resultatMapeig.getDocuments());
					if (adjunts == null)
						adjunts = new ArrayList<DadesDocumentDto>();
					adjunts.addAll(resultatMapeig.getAdjunts());
				}
				if(expedientTipus.isProcedimentComu() && unitatOrganitzativaCodi==null) {
					unitatOrganitzativaCodi=anotacio.getDestiCodi();
				}
				registreNumero = anotacio.getIdentificador();
				registreData = anotacio.getData();
			}

			// Es crida la creació a través del helper per evitar errors de concurrència de creació de dos expedients
			// a la vegada que ja s'ha donat el cas.
			BackofficeArxiuUtils backofficeUtils = new BackofficeArxiuUtilsImpl(pluginHelper.getArxiuPlugin());
			backofficeUtils.setArxiuPluginListener(this);
			expedient = expedientHelper.iniciar(
					entornId,
					usuari,
					expedientTipusId,
					definicioProcesId,
					any,
					numero,
					unitatOrganitzativaCodi,
					titol,
					registreNumero,
					registreData,
					unitatAdministrativa,
					idioma,
					autenticat,
					tramitadorNif,
					tramitadorNom,
					interessatNif,
					interessatNom,
					representantNif,
					representantNom,
					avisosHabilitats,
					avisosEmail,
					avisosMobil,
					notificacioTelematicaHabilitada,
					variables,
					transitionName,
					iniciadorTipus,
					iniciadorCodi,
					responsableCodi,
					documents,
					adjunts,
					anotacioId,
					resultatMapeig,
					anotacioInteressatsAssociar,
					backofficeUtils);

			if (anotacioId != null) {
				if (resultatMapeig != null && resultatMapeig.isError()) {
					Alerta alerta = alertaHelper.crearAlerta(
							expedient.getEntorn(),
							expedient,
							new Date(),
							null,
							resultatMapeig.getMissatgeAlertaErrors());
					alerta.setPrioritat(AlertaPrioritat.ALTA);
				}
				// Incorporporar l'anotació a l'expedient
				anotacioService.incorporarReprocessarExpedient(
						anotacio.getId(),
						expedientTipusId,
						expedient.getId(),
						anotacioInteressatsAssociar,
						true,
						false);
			}

			// Retorna la informació de l'expedient que s'ha iniciat
			ExpedientDto dto = conversioTipusHelper.convertir(
					expedient,
					ExpedientDto.class);

			eventPublisher.publishEvent(dto);

			return dto;
		} catch (ValidationException ex) {
			throw new TramitacioValidacioException("Error de validació en Handler: " +ex.getMessage(), ex);
		}catch (Exception e) {
			throw new Exception(messageHelper.getMessage("error.proces.peticio") + ": "
					+ ExceptionUtils.getRootCauseMessage(e), e);
		}
	}


	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	public boolean update(
			Long id,
			String numero,
			String titol,
			String responsableCodi,
			Date dataInici,
			String comentari,
			Long estatId,
			Double geoPosX,
			Double geoPosY,
			String geoReferencia,
			String grupCodi,
			boolean execucioDinsHandler) {
		logger.debug(
				"Modificar informació de l'expedient (" +
				"id=" + id + ", " +
				"numero=" + numero + ", " +
				"titol=" + titol + ", " +
				"responsableCodi=" + responsableCodi + ", " +
				"dataInici=" + dataInici + ", " +
				"comentari=" + comentari + ", " +
				"estatId=" + estatId + ", " +
				"geoPosX=" + geoPosX + ", " +
				"geoPosY=" + geoPosY + ", " +
				"geoReferencia=" + geoReferencia + ", " +
				"grupCodi=" + grupCodi + ")");
		Expedient expedient = expedientHelper.getExpedientComprovantPermisos(
				id,
				false,
				true,
				false,
				false);
		if (estatId != null && estatId == -1) {
			estatId = expedient.getEstat() != null ? expedient.getEstat().getId() : null;
			List<WProcessInstance> processInstancesTree = workflowEngineApi.getProcessInstanceTree(expedient.getProcessInstanceId());
			String[] ids = new String[processInstancesTree.size()];
			int i = 0;
			for (WProcessInstance pi: processInstancesTree)
				ids[i++] = pi.getId().toString();
			Date dataFinalitzacio = new Date();
			workflowEngineApi.finalitzarExpedient(ids, dataFinalitzacio);
			expedient.setDataFi(dataFinalitzacio);
			expedientLoggerHelper.afegirLogExpedientPerExpedient(
					expedient.getId(),
					ExpedientLogAccioTipus.EXPEDIENT_FINALITZAR,
					null);
			estatId = null;
		}

		return expedientHelper.update(
				expedient,
				numero,
				titol,
				responsableCodi,
				dataInici,
				comentari,
				estatId,
				geoPosX,
				geoPosY,
				geoReferencia,
				grupCodi,
				execucioDinsHandler);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly = true)
	public List<RespostaValidacioSignaturaDto> verificarSignatura(Long documentStoreId) {
		DocumentStore documentStore = documentStoreRepository.findById(documentStoreId).orElse(null);
		if (documentStore == null)
			throw new NoTrobatException(DocumentStore.class, documentStoreId);
		return documentHelper.getRespostasValidacioSignatura(documentStore);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	public void delete(Long id) {
		logger.debug("Esborrant l'expedient (id=" + id + ")");
		Expedient expedient = expedientHelper.getExpedientComprovantPermisos(
			id,
			false,
			false,
			true,
			false);
		List<WProcessInstance> processInstancesTree = workflowEngineApi.getProcessInstanceTree(expedient.getProcessInstanceId());
		if(processInstancesTree != null) {
			if (expedient.isArxiuActiu() && !isPropagarEsbExp()) {
				// Si l'expedient està emmagatzemat a dins l'arxiu comprovam que
				// l'expedient no contengui documents firmats abans d'esborrar-lo.
				List<String> processInstanceIds = new ArrayList<String>();
				for (WProcessInstance processInstance : processInstancesTree) {
					processInstanceIds.add(processInstance.getId().toString());
				}
	//			List<DocumentStore> documentsSignats = documentStoreRepository.findByProcessInstanceIdInAndSignatTrue(
	//					processInstanceIds);
	//			if (!documentsSignats.isEmpty()) {
	//				throw new ValidacioException("Aquest expedient no es pot esborrar perquè conté documents firmats");
	//			}
			}
	//		List<PeticioPinbal> pets = peticioPinbalRepository.findByExpedientId(expedient.getId());
	//		if (pets!=null) {
	//			for (PeticioPinbal p: pets) {
	//				if (p.getDocument()!=null) {
	//					documentHelper.esborrarDocument(null, expedient.getProcessInstanceId(), p.getDocument().getId());
	//				}
	//				peticioPinbalRepository.delete(p);
	//			}
	//		}

			peticioPinbalRepository.deleteAll(peticioPinbalRepository.findByExpedientId(expedient.getId()));

			List<AnotacioEmail> anotacioEmails = anotacioEmailRepository.findByExpedientId(expedient.getId());
			if (anotacioEmails != null && !anotacioEmails.isEmpty()) {
				for (AnotacioEmail anotacioEmail : anotacioEmails) {
					anotacioEmailRepository.delete(anotacioEmail);
				}
			}

			anotacioService.esborrarAnotacionsExpedient(expedient.getId());

			// Ordena per id de menor a major per evitar errors de dependències
			Collections.sort(
				processInstancesTree,
				new Comparator<WProcessInstance>() {
					public int compare(WProcessInstance o1, WProcessInstance o2) {
						String l1 = o1.getId();
						String l2 = o2.getId();
						return l2.compareTo(l1);
					}
				});
			for (WProcessInstance pi : processInstancesTree) {
				for (TerminiIniciat ti : terminiIniciatRepository.findByProcessInstanceId(pi.getId()))
					terminiIniciatRepository.delete(ti);
				workflowEngineApi.deleteProcessInstance(pi.getId());
				for (DocumentStore documentStore : documentStoreRepository.findByProcessInstanceId(pi.getId())) {
					if (documentStore.isSignat() && documentStore.getReferenciaCustodia() != null) {
						try {
//							pluginHelper.custodiaEsborrarSignatures(documentStore.getReferenciaCustodia(), expedient);
						} catch (Exception ignored) {
						}
					}
					List<DocumentNotificacio> enviaments = documentNotificacioRepository.findByExpedientAndDocumentId(expedient, documentStore.getId());
					if (enviaments != null && enviaments.size() > 0)
						documentNotificacioRepository.deleteAll(enviaments);

					if (documentStore.getFont().equals(DocumentFont.ALFRESCO))
						pluginHelper.gestioDocumentalDeleteDocument(
							documentStore.getReferenciaFont(), expedient);
					//Si el document pertany a un zip, de moment no l'esborrem, esperarem a esborrar el zip
					if (documentStore.getZips() != null && !documentStore.getZips().isEmpty()) {
						continue;
					} else {
						documentStoreRepository.deleteById(documentStore.getId());
					}
				}
			}
		}
		for (Portasignatures psigna: expedient.getPortasignatures()) {
			psigna.setEstat(PortafirmesEstatEnum.ESBORRAT);
		}
		for (ExecucioMassivaExpedient eme: execucioMassivaExpedientRepository.getExecucioMassivaByExpedient(id)) {
			execucioMassivaExpedientRepository.delete(eme);
		}
		if (expedient.getRelacionsOrigen() != null) {
			for (Expedient desti : expedient.getRelacionsOrigen()) {
				expedient.removeRelacioOrigen(desti);
				desti.removeRelacioOrigen(expedient);
			}
		}
		expedientDadaHelper.deleteByExpedient(expedient.getId());
		expedientDocumentHelper.deleteByExpedient(expedient.getId());
		expedientRepository.delete(expedient);
		if (expedient.getArxiuUuid() != null && pluginHelper.arxiuExisteixExpedient(expedient.getArxiuUuid())) {
			try {
				pluginHelper.arxiuExpedientEsborrar(expedient.getArxiuUuid());
			} catch (SistemaExternException e) {
				logger.warn("Error esborrant l'expedient " + expedient.getNumero() + " " + expedient.getTitol() +" a l'Arxliu: " + e.getMessage());
			}
		}
		crearRegistreExpedient(
				expedient.getId(),
				SecurityContextHolder.getContext().getAuthentication().getName(),
				Registre.Accio.ESBORRAR);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly = true)
	public Long findIdAmbProcessInstanceId(String processInstanceId) {
		return expedientRepository.findIdByProcessInstanceId(processInstanceId);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly = true)
	public ExpedientDto findAmbIdAmbPermis(Long id) {
		logger.debug("Consultant l'expedient amb permis lectura (id=" + id + ")");
		Expedient expedient = expedientHelper.getExpedientComprovantPermisos(
				id,
				true,
				false,
				false,
				false);
		ExpedientDto expedientDto = conversioTipusHelper.convertir(
				expedient,
				ExpedientDto.class);
		//  A vegades es produeix un null pointer accedint al tipus d'expedient del DTO, issue #1094
		if (expedientDto.getTipus() == null) {
			// Es marca com un error per identificar quan falla i es rectifica la propietat del dto per continuar treballant
			logger.error("La propietat expedientDto.tipus és null (expedient.getTipus() = " + expedient.getTipus() + ", es fixarà el tipus del dto manualment");
			expedientDto.setTipus(conversioTipusHelper.convertir(expedient.getTipus(), ExpedientTipusDto.class));
		}
		expedientHelper.omplirPermisosExpedient(expedientDto);
		expedientHelper.trobarAlertesExpedient(expedient, expedientDto);

		return expedientDto;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly = true)
	public ExpedientDto findAmbId(Long id) {
		logger.debug("Consultant l'expedient sense comprovar permisos (id=" + id + ")");
		Expedient expedient = expedientRepository.findById(id).orElse(null);
		return conversioTipusHelper.convertir(
				expedient,
				ExpedientDto.class);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly = true)
	public ExpedientDto findExpedientAmbProcessInstanceId(String processInstanceId) {
		logger.debug("Consultant l'expedient sense comprovar permisos (processInstanceId=" + processInstanceId + ")");
		Expedient expedient = expedientRepository.findByProcessInstanceId(processInstanceId);
		return conversioTipusHelper.convertir(
				expedient,
				ExpedientDto.class);
	}


	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly = true)
	public List<ExpedientDto> findAmbIds(Set<Long> ids) {
		List<ExpedientDto> listExpedient = new ArrayList<ExpedientDto>();
		logger.debug("Consultant l'expedient (ids=" + ids + ")");
		Iterator<Long> iterator = ids.iterator();
		Set<Long> idsConsulta = new HashSet<Long>();
		int consultats = 0;
		int n, i;
		while (consultats < ids.size()) {
			idsConsulta.clear();
			// Fa la consulta cada 1000 perquè és el màxim d'ids per clàusula in() a la BBDD
			n = Math.min(1000, ids.size()-consultats);
			for (i=0; i< n; i++) {
				idsConsulta.add(iterator.next());
			}
			consultats += n;
			for (Expedient expedient : expedientRepository.findAmbIds(idsConsulta)) {
				listExpedient.add(conversioTipusHelper.convertir(
						expedient,
						ExpedientDto.class));
			}
		}
		return listExpedient;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly = true)
	public PaginaDto<ExpedientDto> findAmbFiltrePaginat(
			List<ExpedientTipusDto> expedientTipusDtoAccessibles,
			Long entornId,
			Long expedientTipusId,
			String titol,
			String numero,
			String unitatOrganitzativaCodi,
			Date dataInici1,
			Date dataInici2,
			Date dataFi1,
			Date dataFi2,
			EstatTipusDto estatTipus,
			Long estatId,
			Double geoPosX,
			Double geoPosY,
			String geoReferencia,
			String registreNumero,
			boolean nomesTasquesPersonals,
			boolean nomesTasquesGrup,
			boolean nomesAlertes,
			boolean nomesErrors,
			boolean nomesErrorsArxiu,
			MostrarAnulatsDto mostrarAnulats,
			Set<Long> idsSeleccionats,
			PaginacioParamsDto paginacioParams) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		logger.debug("Consulta general d'expedients paginada (" +
				"entornId=" + entornId + ", " +
				"expedientTipusId=" + expedientTipusId + ", " +
				"titol=" + titol + ", " +
				"numero=" + numero + ", " +
				"unitatOrganitzativaCodi=" + unitatOrganitzativaCodi+ ", " +
				"dataInici1=" + dataInici1 + ", " +
				"dataInici2=" + dataInici2 + ", " +
				"dataFi1=" + dataFi1 + ", " +
				"dataFi2=" + dataFi2 + ", " +
				"estatTipus=" + estatTipus + ", " +
				"estatId=" + estatId + ", " +
				"geoPosX=" + geoPosX + ", " +
				"geoPosY=" + geoPosY + ", " +
				"geoReferencia=" + geoReferencia + ", " +
				"registreNumero=" + registreNumero + ", "+
				"nomesTasquesPersonals=" + nomesTasquesPersonals + ", " +
				"nomesTasquesGrup=" + nomesTasquesGrup + ", " +
				"nomesAlertes=" + nomesAlertes + ", " +
				"nomesErrors=" + nomesErrors + ", " +
				"nomesErrorsArxiu=" + nomesErrorsArxiu + ", " +
				"mostrarAnulats=" + mostrarAnulats +
				"nomesTasquesPersonals=" + nomesTasquesPersonals + ", " +
				"nomesTasquesGrup=" + nomesTasquesGrup + "," +
				"idsSeleccionats=" + idsSeleccionats.size() + ")");
		// Comprova l'accés a l'entorn
		Entorn entorn = entornHelper.getEntornComprovantPermisos(
				entornId,
				true);
		// Comprova l'accés al tipus d'expedient
		ExpedientTipus expedientTipus = null;
		Map<Long,List<Long>> unitatsPerTipusComu = new HashMap<Long, List<Long>>();
		Permission[] permisosRequerits= new Permission[] {
				ExtendedPermission.READ,
				ExtendedPermission.ADMINISTRATION};
		if (expedientTipusId != null) {
			expedientTipus = expedientTipusHelper.getExpedientTipusComprovantPermisLectura(
					expedientTipusId);

			//Obté la llista de unitats organitzatives per les quals té permisos (també retornarà les uo filles)
			if(expedientTipus.isProcedimentComu()) {
				List<ExpedientTipusUnitatOrganitzativa> expTipUnitOrgList = expedientTipusUnitatOrganitzativaRepository.findByExpedientTipusId(expedientTipusId);
				unitatsPerTipusComu = expedientTipusHelper.unitatsPerTipusComuIds(entornId,expTipUnitOrgList, permisosRequerits);
			}

		} else { //si no hi ha expedientTipus al filtre, hem de buscar totes les UO per las quals es té permís i obtenir els expedinetTipus
			if (expedientTipusDtoAccessibles != null && !expedientTipusDtoAccessibles.isEmpty()) {
				List<Long> idsExpTipusAccessibles = new ArrayList<Long>();
				for(ExpedientTipusDto expTipus: expedientTipusDtoAccessibles) {
					idsExpTipusAccessibles.add(expTipus.getId());
				}
				List<ExpedientTipusUnitatOrganitzativa> expTipUnitOrgList =
						expedientTipusUnitatOrganitzativaRepository.findByExpedientTipus(idsExpTipusAccessibles);
				unitatsPerTipusComu =
						expedientTipusHelper.unitatsPerTipusComuIds(entornId,expTipUnitOrgList, permisosRequerits);
			}
		}
		// Comprova l'accés a l'estat
		Estat estat = null;
		if (estatId != null) {
			estat = estatRepository.findByExpedientTipusAndIdAmbHerencia(
					expedientTipus.getId(),
					estatId);
			if (estat == null) {
				logger.debug("No s'ha trobat l'estat (expedientTipusId=" + expedientTipusId + ", estatId=" + estatId + ")");
				throw new NoTrobatException(Estat.class,estatId);
			}
		}
		// Calcula la data fi pel filtre
		dataInici2 = this.ajustaFinalDia(dataInici2);
		// Calcula la data finalització fi pel filtre
		dataFi2 = this.ajustaFinalDia(dataFi2);

		// Obté la llista de tipus d'expedient permesos
//		List<Long> tipusPermesosIds = expedientTipusHelper.findIdsAmbPermisRead(entorn);
		List<ExpedientTipus> tipusPermesos = expedientTipusHelper.findAmbPermisRead(entorn);

		//Tenim en compte si es filtre per unitatOrganitzativa, es té en compte la llista de uo's permeses
		Long unitatOrganitzativaId = null;
		if(unitatOrganitzativaCodi!=null) {
			UnitatOrganitzativa unitatOrg = unitatOrganitzativaRepository.findByCodi(unitatOrganitzativaCodi);
			unitatOrganitzativaId = unitatOrg!=null? unitatOrg.getId() : null;
		}

		List<Long> expedientsIds;
		/*
		List<ExpedientDto> expedients = new ArrayList<ExpedientDto>();
		if(idsSeleccionats == null || idsSeleccionats.isEmpty()) {
			// Executa la consulta amb paginació
			expedientsIds = jbpmHelper.expedientFindByFiltre(
					entornId,
					auth.getName(),
					tipusPermesosIds,
					unitatsPerTipusComu,
					titol,
					numero,
					unitatOrganitzativaId,
					expedientTipusId,
					dataInici1,
					dataInici2,
					dataFi1,
					dataFi2,
					estatId,
					geoPosX,
					geoPosY,
					geoReferencia,
					//TODO3: afegir el nou camp en la capa service
					registreNumero,
					EstatTipusDto.INICIAT.equals(estatTipus),
					EstatTipusDto.FINALITZAT.equals(estatTipus),
					MostrarAnulatsDto.SI.equals(mostrarAnulats),
					MostrarAnulatsDto.NOMES_ANULATS.equals(mostrarAnulats),
					nomesAlertes,
					nomesErrors,
					nomesTasquesPersonals,
					nomesTasquesGrup,
					true, // nomesTasquesMeves, // TODO Si no te permis SUPERVISION nomesTasquesMeves = false
					usuariActualHelper.isAdministrador() || entornHelper.esAdminEntorn(entornId)? null : usuariActualHelper.getAreesGrupsUsuariActual(),
					paginacioParams,
					false,
					nomesErrorsArxiu,
					idsSeleccionats);
			// Retorna la pàgina amb la resposta
			*/
			// TODO: Mostrar nomes els expedients que compleixen el filtre
			//if (expedientsIds.size() > 0) {
//			try {
				return paginacioHelper.toPaginaDto(
					expedientRepository.findByFiltreGeneralPaginat(
						entorn,
						tipusPermesos,
						expedientTipus == null,
						expedientTipus,
						titol == null,
						titol,
						numero == null,
						numero,
						dataInici1 == null,
						dataInici1,
						dataInici2 == null,
						dataInici2,
						EstatTipusDto.INICIAT.equals(estatTipus),
						EstatTipusDto.FINALITZAT.equals(estatTipus),
						estat == null,
						estat,
						geoPosX == null,
						geoPosX,
						geoPosY == null,
						geoPosY,
						geoReferencia == null,
						geoReferencia,
						false,
						null,
						null,
						null,
						null,
						null,
						MostrarAnulatsDto.SI.equals(mostrarAnulats),
						nomesAlertes,
						paginacioHelper.toSpringDataPageable(paginacioParams)
					),
					ExpedientDto.class);
//			} catch(Exception e) {
//				e.printStackTrace();
//				throw e;
//			}
			//}
//		} else {
//
//			expedientsIds = new ArrayList<Long>(0);
//			//List<Expedient> expedientsList = new ArrayList<Expedient>();
//
//			for(List<Long> expIds : Iterables.partition(idsSeleccionats, 800)) {
//
//				paginacioParams.setPaginaTamany(idsSeleccionats.size());
//
//				List<Long> fiteredExpedientsIds = jbpmHelper.expedientFindByFiltre(
//						entornId,
//						auth.getName(),
//						tipusPermesosIds,
//						unitatsPerTipusComu,
//						titol,
//						numero,
//						unitatOrganitzativaId,
//						expedientTipusId,
//						dataInici1,
//						dataInici2,
//						dataFi1,
//						dataFi2,
//						estatId,
//						geoPosX,
//						geoPosY,
//						geoReferencia,
//						//TODO3: afegir el nou camp en la capa service
//						registreNumero,
//						EstatTipusDto.INICIAT.equals(estatTipus),
//						EstatTipusDto.FINALITZAT.equals(estatTipus),
//						MostrarAnulatsDto.SI.equals(mostrarAnulats),
//						MostrarAnulatsDto.NOMES_ANULATS.equals(mostrarAnulats),
//						nomesAlertes,
//						nomesErrors,
//						nomesTasquesPersonals,
//						nomesTasquesGrup,
//						true, // nomesTasquesMeves, // TODO Si no te permis SUPERVISION nomesTasquesMeves = false
//						usuariActualHelper.isAdministrador() || entornHelper.esAdminEntorn(entornId)? null : usuariActualHelper.getAreesGrupsUsuariActual(),
//						paginacioParams,
//						false,
//						nomesErrorsArxiu,
//						Sets.newTreeSet(expIds));
//
//				expedientsIds.addAll(fiteredExpedientsIds);
//
//				// TODO: Mostrar nomes els expedients que compleixen el filtre
//				expedients.addAll(
//						conversioTipusHelper.convertirList(
//								expedientRepository.findAll(),
////								expedientRepository.findByIdIn(fiteredExpedientsIds),
//								ExpedientDto.class));
//			}
//
//			paginacioParams.setPaginaTamany(expedients.size());
//		}

		// Després de la consulta els expedients es retornen en ordre invers
//		Collections.sort(expedients, new ExpedientDtoIdsComparator(expedientsIds));
//
//		if (expedients.size() > 0) {
//			for(List<ExpedientDto> expedientsChunk : Iterables.partition(expedients, 800)) {
//				expedientHelper.omplirPermisosExpedients(expedientsChunk);
//				expedientHelper.trobarAlertesExpedients(expedientsChunk);
//			}
//		}
//		return paginacioHelper.toPaginaDto(
//				expedients,
//				expedientsIds.size(),
//				paginacioParams);
	}

	/** Ajusta el dia per a que estigui tot inclòs. Ajusta l'hora i els minuts fins al final del dia. */
	private Date ajustaFinalDia(Date data) {
		Date ret = null;
		if (data != null) {
			Calendar cal = Calendar.getInstance();
			cal.setTime(data);
			cal.set(Calendar.HOUR_OF_DAY, 23);
			cal.set(Calendar.MINUTE, 59);
			cal.set(Calendar.SECOND, 59);
			cal.set(Calendar.MILLISECOND, 999);
			ret = cal.getTime();
		}
		return ret;
	}

	/** Classe per poder comparar la posició dels expedients segons la llista d'identificadors ordenada
	 * que es retorna de la consulta d'expedients paginada i ordenada. Segons la posició de l'identificador
	 * a la llista retornada els expedients van en una o altra posició.
	 */
	public class ExpedientDtoIdsComparator implements Comparator<ExpedientDto> {

		private List<Long> ids;

		public ExpedientDtoIdsComparator(List<Long> ids){
			this.ids = ids;
		}

		/** L'ordre dels expedients depén de la posició del seu ID després de la consulta. */
		@Override
		public int compare(ExpedientDto e1, ExpedientDto e2) {
			Integer i1 = ids.indexOf(e1.getId());
			Integer i2 = ids.indexOf(e2.getId());
			return i1.compareTo(i2);
		}

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly = true)
	public List<Long> findIdsAmbFiltre(
			Long entornId,
			Long expedientTipusId,
			String titol,
			String numero,
			String unitatOrganitzativaCodi,
			Date dataInici1,
			Date dataInici2,
			Date dataFi1,
			Date dataFi2,
			EstatTipusDto estatTipus,
			Long estatId,
			Double geoPosX,
			Double geoPosY,
			String geoReferencia,
			String registreNumero,
			boolean nomesTasquesPersonals,
			boolean nomesTasquesGrup,
			boolean nomesAlertes,
			boolean nomesErrors,
			boolean nomesErrorsArxiu,
			MostrarAnulatsDto mostrarAnulats) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		logger.debug("Consulta general d'expedients només ids (" +
				"entornId=" + entornId + ", " +
				"expedientTipusId=" + expedientTipusId + ", " +
				"titol=" + titol + ", " +
				"numero=" + numero + ", " +
				"unitatOrganitzativaCodi=" + unitatOrganitzativaCodi + ", " +
				"dataInici1=" + dataInici1 + ", " +
				"dataInici2=" + dataInici2 + ", " +
				"dataFi1=" + dataFi1 + ", " +
				"dataFi2=" + dataFi2 + ", " +
				"estatTipus=" + estatTipus + ", " +
				"estatId=" + estatId + ", " +
				"geoPosX=" + geoPosX + ", " +
				"geoPosY=" + geoPosY + ", " +
				"geoReferencia=" + geoReferencia + ", " +
				"registreNumero=" + registreNumero + ", "+
				"nomesAlertes=" + nomesAlertes + ", " +
				"nomesErrors=" + nomesErrors + ", " +
				"mostrarAnulats=" + mostrarAnulats +
				"nomesTasquesPersonals=" + nomesTasquesPersonals + ", " +
				"nomesTasquesGrup=" + nomesTasquesGrup + ")");
		// Comprova l'accés a l'entorn
		Entorn entorn = entornHelper.getEntornComprovantPermisos(
				entornId,
				true);
		// Comprova l'accés al tipus d'expedient
		ExpedientTipus expedientTipus = null;
		if (expedientTipusId != null) {
			expedientTipus = expedientTipusHelper.getExpedientTipusComprovantPermisLectura(
					expedientTipusId);
		}
		// Comprova l'accés a l'estat
		Estat estat = null;
		if (estatId != null) {
			estat = estatRepository.findByExpedientTipusAndIdAmbHerencia(
					expedientTipus.getId(),
					estatId);
			if (estat == null) {
				logger.debug("No s'ha trobat l'estat (expedientTipusId=" + expedientTipusId + ", estatId=" + estatId + ")");
				throw new NoTrobatException(Estat.class, estatId);
			}
		}
		// Calcula la data fi pel filtre
		dataInici2 = this.ajustaFinalDia(dataInici2);
		// Calcula la data finalitzacio fi pel filtre
		dataFi2 = this.ajustaFinalDia(dataFi2);

		// Obté la llista de tipus d'expedient permesos
		List<ExpedientTipus> tipusPermesos = expedientTipusHelper.findAmbPermisRead(entorn);

		Long unitatOrganitzativaId=null;
		if(unitatOrganitzativaCodi!=null) {
			UnitatOrganitzativa unitatOrg = unitatOrganitzativaRepository.findByCodi(unitatOrganitzativaCodi);
			unitatOrganitzativaId = unitatOrg!=null? unitatOrg.getId() : null;
		}
		Map<Long,List<Long>> unitatsPerTipusComu = new HashMap<Long, List<Long>>();
		List<ExpedientTipusUnitatOrganitzativa> expTipUnitOrgList = expedientTipusUnitatOrganitzativaRepository.findByExpedientTipusEntornId(entorn.getId());
		Permission[] permisosRequerits= new Permission[] {
				ExtendedPermission.READ,
				ExtendedPermission.ADMINISTRATION};
		unitatsPerTipusComu = expedientTipusHelper.unitatsPerTipusComuIds(entornId,expTipUnitOrgList, permisosRequerits);
		// Executa la consulta amb paginació

		return expedientRepository.findIdsByFiltreGeneral(
					entorn,
					tipusPermesos,
					expedientTipus == null,
					expedientTipus,
					titol == null,
					titol,
					numero == null,
					numero,
					dataInici1 == null,
					dataInici1,
					dataInici2 == null,
					dataInici2,
					EstatTipusDto.INICIAT.equals(estatTipus),
					EstatTipusDto.FINALITZAT.equals(estatTipus),
					estat == null,
					estat,
					geoPosX == null,
					geoPosX,
					geoPosY == null,
					geoPosY,
					geoReferencia == null,
					geoReferencia,
					false,
					null,
					null,
					null,
					null,
					null,
					MostrarAnulatsDto.SI.equals(mostrarAnulats),
					nomesAlertes
				);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly = true)
	public List<ExpedientDto> findPerSuggest(Long expedientTipusId, String text) {
		logger.debug("Consulta suggest d'expedients (" +
				"expedientTipusId=" + expedientTipusId + ", " +
				"text=" + text + ")");
		String textDecoded = null;
		try {
			textDecoded = new String(text.getBytes("ISO-8859-1"), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			logger.error("No s'ha pogut consultar el text " + textDecoded + ": " + e.getMessage());
		}
		List<Expedient> expedients = null;
		if (expedientTipusId != null) {
			// Comprova l'accés al tipus d'expedient
			expedientTipusHelper.getExpedientTipusComprovantPermisLectura(
					expedientTipusId);
			expedients = expedientRepository.findByTipusAndNumeroOrTitol(expedientTipusId, textDecoded);

			try {
				Long expedientId = Long.valueOf(textDecoded);
				Expedient expedient = expedientRepository.findById(expedientId).orElse(null);
				if (expedient != null && expedient.getTipus().getId() == expedientTipusId) {
					expedients.add(expedient);
				}
			} catch(Exception e) {
				// el text no es correspon a un id vàlid
			}
		}
		return conversioTipusHelper.convertirList(
				expedients,
				ExpedientDto.class);
	}



	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly = true)
	public boolean isDiferentsTipusExpedients(Set<Long> ids) {

		boolean diferents = false;
		Set<Long> idsTipusExpedients = new HashSet<Long>();
		Set<Long> idsConsulta = new HashSet<Long>();
		Iterator<Long> iterator = ids.iterator();
		int consultats = 0;
		int n, i;
		while (!diferents && consultats < ids.size()) {
			idsConsulta.clear();
			// Fa la consulta cada 1000 perquè és el màxim d'ids per clàusula in() a la BBDD
			n = Math.min(1000, ids.size()-consultats);
			for (i=0; i< n; i++) {
				idsConsulta.add(iterator.next());
			}
			consultats += n;
			idsTipusExpedients.addAll(expedientRepository.getIdsDiferentsTipusExpedients(idsConsulta));
			diferents = idsTipusExpedients.size() > 1;
		}
		return diferents;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly = true)
	public ArxiuDto getImatgeDefinicioProces(
			Long id,
			String processInstanceId) throws IOException {
		logger.debug("Consulta de la imatge de la definició de procés (" +
				"id=" + id + ", " +
				"processInstanceId=" + processInstanceId + ")");
		Expedient expedient = expedientHelper.getExpedientComprovantPermisos(
				id,
				true,
				false,
				false,
				false);
		DefinicioProces definicioProces;
		if (processInstanceId != null) {
			expedientHelper.comprovarInstanciaProces(
					expedient,
					processInstanceId);
			definicioProces = expedientHelper.findDefinicioProcesByProcessInstanceId(
					processInstanceId);
		} else {
			definicioProces = expedientHelper.findDefinicioProcesByProcessInstanceId(
					expedient.getProcessInstanceId());
		}
		String resourceName = "processimage.jpg";
		ArxiuDto imatge = new ArxiuDto();
		imatge.setNom(resourceName);
		imatge.setContingut(
				workflowEngineApi.getResourceBytes(
						definicioProces.getJbpmId(),
						resourceName));
		return imatge;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly = true)
	public List<PersonaDto> findParticipants(Long id) {
		logger.debug("Consulta de participants per a l'expedient (" +
				"id=" + id + ")");
		Expedient expedient = expedientHelper.getExpedientComprovantPermisos(
				id,
				true,
				false,
				false,
				false);

		List<PersonaDto> resposta = tascaHelper.findParticipants(expedient);

		return resposta;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	// No pot ser readOnly per mor de la cache de les tasques
	@Transactional
	public List<ExpedientTascaDto> findTasquesPendents(
			Long expedientId,
			boolean nomesTasquesPersonals,
			boolean nomesTasquesGrup) {
		logger.debug("Consulta de tasques pendents de l'expedient (" +
				"id=" + expedientId + ", " +
				"nomesTasquesPersonals=" + nomesTasquesPersonals + ", " +
				"nomesTasquesGrup=" + nomesTasquesGrup + ")");
		Expedient expedient = expedientHelper.getExpedientComprovantPermisos(
				expedientId,
				true,
				false,
				false,
				false);

		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		Permission[] permisos = new Permission[] {
				ExtendedPermission.SUPERVISION,
				ExtendedPermission.TASK_SUPERV,
				ExtendedPermission.ADMINISTRATION};
		boolean tasquesAltresUsuaris = permisosHelper.isGrantedAny(
					expedient.getTipus().getId(),
					ExpedientTipus.class,
					permisos,
					auth);
		boolean tasquesSobreUO = false;
		if(expedient.getTipus().isProcedimentComu()) {
				tasquesSobreUO = expedientTipusHelper.tePermisosSobreUnitatOrganitzativaOrParents(
					expedient.getTipus().getId(),
					expedient.getUnitatOrganitzativa().getCodi(),
					permisos);
		}

		List<ExpedientTascaDto> resposta = new ArrayList<ExpedientTascaDto>();
		for (WProcessInstance jpi: workflowEngineApi.getProcessInstanceTree(expedient.getProcessInstanceId())) {
			resposta.addAll(
					tascaHelper.findTasquesPerExpedientPerInstanciaProces(
							jpi.getId().toString(),
							expedient,
							tasquesAltresUsuaris || tasquesSobreUO,
							nomesTasquesPersonals,
							nomesTasquesGrup));
		}
		return resposta;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	public void deleteSignatura(
			Long expedientId,
			Long documentStoreId) {
		Expedient expedient = expedientHelper.getExpedientComprovantPermisos(
				expedientId,
				false,
				true,
				false,
				false);
		DocumentStore documentStore = documentStoreRepository.findById(documentStoreId).orElse(null);
		if (documentStore != null && documentStore.isSignat()) {
//				pluginHelper.custodiaEsborrarSignatures(
//						documentStore.getReferenciaCustodia(),
//						expedient);
			String codi = documentStore.getCodi();
			documentStore.setReferenciaCustodia(null);
			documentStore.setSignat(false);
			expedientRegistreHelper.crearRegistreEsborrarSignatura(
					expedient.getId(),
					expedient.getProcessInstanceId(),
					SecurityContextHolder.getContext().getAuthentication().getName(),
					documentStore.getCodi());
			List<WTaskInstance> tasks = workflowEngineApi.findTaskInstancesForProcessInstance(expedient.getProcessInstanceId());
			for (WTaskInstance task: tasks) {
				workflowEngineApi.deleteTaskInstanceVariable(
						task.getId(),
						codi);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	public void aturar(
			Long id,
			String motiu) {
		logger.debug("Aturant la tramitació de l'expedient (" +
				"id=" + id + ", " +
				"motiu=" + motiu + ")");
		Expedient expedient = expedientHelper.getExpedientComprovantPermisos(
				id,
				new Permission[] {
						ExtendedPermission.STOP,
						ExtendedPermission.ADMINISTRATION});
		expedientHelper.aturar(
				expedient,
				motiu,
				null);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	public void reprendre(Long id) {
		logger.debug("Reprenent la tramitació de l'expedient (" +
				"id=" + id + ")");
		Expedient expedient = expedientHelper.getExpedientComprovantPermisos(
				id,
				new Permission[] {
						ExtendedPermission.STOP,
						ExtendedPermission.ADMINISTRATION});
		expedientHelper.reprendre(expedient, null);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	public void anular(
			Long id,
			String motiu) {
		logger.debug("Anulant l'expedient (" +
				"id=" + id + ", " +
				"motiu=" + motiu + ")");
		Expedient expedient = expedientHelper.getExpedientComprovantPermisos(
				id,
				new Permission[] {
						ExtendedPermission.CANCEL,
						ExtendedPermission.ADMINISTRATION});
		mesuresTemporalsHelper.mesuraIniciar(
				"Anular",
				"expedient",
				expedient.getTipus().getNom());
		List<WProcessInstance> processInstancesTree = workflowEngineApi.getProcessInstanceTree(expedient.getProcessInstanceId());
		String[] ids = new String[processInstancesTree.size()];
		int i = 0;
		for (WProcessInstance pi: processInstancesTree)
			ids[i++] = pi.getId().toString();
		workflowEngineApi.suspendProcessInstances(ids);
		expedient.setAnulat(true);
		expedient.setComentariAnulat(motiu);
		expedientDadaHelper.deleteByExpedient(expedient.getId());
		crearRegistreExpedient(
				expedient.getId(),
				SecurityContextHolder.getContext().getAuthentication().getName(),
				Registre.Accio.ANULAR);
		mesuresTemporalsHelper.mesuraCalcular(
				"Anular",
				"expedient",
				expedient.getTipus().getNom());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	public void desanular(Long id) {
		logger.debug("Activant l'expedient (id=" + id + ")");
		Expedient expedient = expedientHelper.getExpedientComprovantPermisos(
				id,
				new Permission[] {
						ExtendedPermission.CANCEL,
						ExtendedPermission.ADMINISTRATION});
		ExpedientLog expedientLog = expedientLoggerHelper.afegirLogExpedientPerExpedient(
				expedient.getId(),
				ExpedientLogAccioTipus.EXPEDIENT_REPRENDRE,
				null);
		expedientLog.setEstat(ExpedientLogEstat.IGNORAR);
		logger.debug("Reprenent les instàncies de procés associades a l'expedient (id=" + id + ")");
		List<WProcessInstance> processInstancesTree = workflowEngineApi.getProcessInstanceTree(
				expedient.getProcessInstanceId());
		String[] ids = new String[processInstancesTree.size()];
		int i = 0;
		for (WProcessInstance pi: processInstancesTree)
			ids[i++] = pi.getId().toString();
		workflowEngineApi.resumeProcessInstances(ids);
		expedient.setAnulat(false);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	public void desfinalitzar(Long id) {
		logger.debug("Desfinalitzant l'expedient (id=" + id + ")");
		Expedient expedient = expedientHelper.getExpedientComprovantPermisos(
				id,
				new Permission[] {
						ExtendedPermission.UNDO_END,
						ExtendedPermission.ADMINISTRATION});
		// Desfinalitza
		expedientHelper.desfinalitzar(
				expedient,
				null);
	}

	@Override
	@Transactional(readOnly=true)
	public boolean potDesfinalitzar(
			Long id) throws NoTrobatException, PermisDenegatException {
		Expedient expedient = expedientHelper.getExpedientComprovantPermisos(
				id,
				new Permission[] {
						ExtendedPermission.UNDO_END,
						ExtendedPermission.ADMINISTRATION});
		if (expedient.isArxiuActiu()
			&& expedient.getArxiuUuid() != null) {
			es.caib.pluginsib.arxiu.api.Expedient expedientArxiu = pluginHelper.arxiuExpedientInfo(expedient.getArxiuUuid());
			return expedientArxiu.getExpedientMetadades().getEstat() == es.caib.pluginsib.arxiu.api.ExpedientEstat.OBERT;
		}
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	public void finalitzar(Long id) {
		logger.debug("Finalitzar l'expedient (id=" + id + ")");
		//Tancam expedient al arxiu, firmant els documents sense firma amb firma servidor
		expedientHelper.finalitzar(id, true);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	public void finalitzar(Long id, boolean firmaDocumentsServidor) {
		logger.debug("Finalitzar l'expedient (id=" + id + ", firmaDocumentsServidor=" + firmaDocumentsServidor + ")");
		//Tancam expedient al arxiu, firmant els documents sense firma amb firma servidor
		expedientHelper.finalitzar(id, firmaDocumentsServidor);
	}


	private void migrarArxiu(Long id, boolean esborrarExpSiError) {
		Expedient expedient = expedientHelper.getExpedientComprovantPermisos(
				id,
				new Permission[] {
						ExtendedPermission.WRITE,
						ExtendedPermission.ADMINISTRATION});
		self.syncArxiu(expedient.getId(), esborrarExpSiError);
	}

	@Transactional(propagation=Propagation.REQUIRES_NEW)
	public void syncArxiu(Long expedientId, boolean esborrarExpSiError) {
		Expedient expedient = expedientRepository.findById(expedientId).orElse(null);
		try {
			expedientHelper.migrarExpedientArxiu(expedient);
			entityManager.flush();
			entityManager.clear();
		} catch (Exception ex) {
			String errorDescripcio = "Error sincronitzant l'expedient " + expedient.getTitol() + " a l'arxiu: " + ex.getMessage();
			if (esborrarExpSiError && expedient.getArxiuUuid() != null && !expedient.getArxiuUuid().isEmpty()) {
				logger.info("Es procedeix a esborrar l'expedient '" + expedient.getTitol() + "' amb uid '" + expedient.getArxiuUuid() + "' de l'arxiu per error en la migració.");
				try{
					pluginHelper.arxiuExpedientEsborrar(expedient.getArxiuUuid());
				} catch(Exception aex) {
					logger.error("Error esborrant l'expedient '" + expedient.getTitol() + "' amb uid '" + expedient.getArxiuUuid() + "' de l'arxiu per error en la migració.", aex);
				}
			}
			throw new TramitacioException(
					expedient.getEntorn().getId(),
					expedient.getEntorn().getCodi(),
					expedient.getEntorn().getNom(),
					expedient.getId(),
					expedient.getTitol(),
					expedient.getNumero(),
					expedient.getTipus().getId(),
					expedient.getTipus().getCodi(),
					expedient.getTipus().getNom(),
					errorDescripcio,
					ex);
		}
	}

	private void migrarDocumentsArxiu(Long id, boolean esborrarExpSiError) {
		// Si el usuari no te permisos es llança una excepció
		expedientHelper.getExpedientComprovantPermisos(
				id,
				new Permission[] {
						ExtendedPermission.WRITE,
						ExtendedPermission.ADMINISTRATION});
		self.syncDocumentsArxiu(id, esborrarExpSiError);
	}

	@Transactional(propagation=Propagation.REQUIRES_NEW)
	public void syncDocumentsArxiu(Long expedientId, boolean esborrarExpSiError) {
		Expedient expedient = expedientRepository.findById(expedientId).orElse(null);
		try {
			expedientHelper.migrarDocumentsArxiu(expedient);
			//Si l'expedient esta tancat, el migrar la l'haura tancat a arxiu, i no es podran mourer els annexos
			//a més si l'expedient ja estava tancat probablement l'estat de l'anotació era de processada
			//i si Distribucio tanca l'expedient de l'anotació llavors ja no es poden "moure***" els annexos
			if (expedient.getDataFi()==null) {
				moureAnnexos(expedient);
			}
			entityManager.flush();
			entityManager.clear();
		} catch (Exception ex) {
			String errorDescripcio = "Error migrant l'expedient " + expedient.getTitol() + " a l'arxiu: " + ex.getMessage();
			if (esborrarExpSiError && expedient.getArxiuUuid() != null && !expedient.getArxiuUuid().isEmpty()) {
				logger.info("Es procedeix a esborrar l'expedient '" + expedient.getTitol() + "' amb uid '" + expedient.getArxiuUuid() + "' de l'arxiu per error en la migració.");
				try{
					pluginHelper.arxiuExpedientEsborrar(expedient.getArxiuUuid());
				} catch(Exception aex) {
					logger.error("Error esborrant l'expedient '" + expedient.getTitol() + "' amb uid '" + expedient.getArxiuUuid() + "' de l'arxiu per error en la migració.", aex);
				}
			}
			throw new TramitacioException(
					expedient.getEntorn().getId(),
					expedient.getEntorn().getCodi(),
					expedient.getEntorn().getNom(),
					expedient.getId(),
					expedient.getTitol(),
					expedient.getNumero(),
					expedient.getTipus().getId(),
					expedient.getTipus().getCodi(),
					expedient.getTipus().getNom(),
					errorDescripcio,
					ex);
		}
	}
	
	@Transactional(propagation=Propagation.REQUIRES_NEW)
	public void syncTancamentArxiu(Long expedientId, boolean esborrarExpSiError) {
		Expedient expedient = expedientRepository.findById(expedientId).orElseThrow();
		try {
			expedientHelper.tancarExpedientArxiu(expedient.getId(), true);
		} catch (Exception ex) {
			String errorDescripcio = "Error migrant l'expedient " + expedient.getTitol() + " a l'arxiu: " + ex.getMessage();
			if (esborrarExpSiError && expedient.getArxiuUuid() != null && !expedient.getArxiuUuid().isEmpty()) {
				logger.info("Es procedeix a esborrar l'expedient '" + expedient.getTitol() + "' amb uid '" + expedient.getArxiuUuid() + "' de l'arxiu per error en la migració.");
				try{
					pluginHelper.arxiuExpedientEsborrar(expedient.getArxiuUuid());
				} catch(Exception aex) {
					logger.error("Error esborrant l'expedient '" + expedient.getTitol() + "' amb uid '" + expedient.getArxiuUuid() + "' de l'arxiu per error en la migració.", aex);
				}
			}
			throw new TramitacioException(
					expedient.getEntorn().getId(), 
					expedient.getEntorn().getCodi(), 
					expedient.getEntorn().getNom(), 
					expedient.getId(), 
					expedient.getTitol(), 
					expedient.getNumero(), 
					expedient.getTipus().getId(), 
					expedient.getTipus().getCodi(), 
					expedient.getTipus().getNom(), 
					errorDescripcio, 
					ex);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	public void sincronitzarArxiu(Long id, boolean esborrarExpSiError) {
		// Comprovam si ja s'esta executant la migració per aquest expedient
		if(isCurrentlyMigrating(id))
			return;
		logger.debug("Migrar l'expedient (id=" + id + ") a l'arxiu");
		expedientLoggerHelper.afegirLogExpedientPerExpedient(
				id,
				ExpedientLogAccioTipus.EXPEDIENT_MIGRAR_ARXIU,
				null);
		List<DocumentStoreBackupDto> documentsEstatAnterior = getDocumentsExpedient(id);
		try {
			addCurrentlyMigrating(id);
			this.migrarArxiu(id, esborrarExpSiError);
			this.migrarDocumentsArxiu(id, esborrarExpSiError);
			this.finalitzaArxiuMigrat(id, esborrarExpSiError);
		} catch(TramitacioException ex) {
			if (esborrarExpSiError) {
				this.undoSincronitzacioArxiu(id);
				this.undoSincronitzacioDocumentsArxiu(documentsEstatAnterior);
			}
			throw ex;
		} finally {
			deleteCurrentlyMigrating(id);
		}
	}

	private void finalitzaArxiuMigrat(Long id, boolean esborrarExpSiError) {
		Expedient expedient = expedientHelper.getExpedientComprovantPermisos(
				id,
				new Permission[] {
						ExtendedPermission.WRITE,
						ExtendedPermission.ADMINISTRATION});
		try {
			if(expedient.getDataFi() == null)
				return;
			self.syncTancamentArxiu(expedient.getId(), esborrarExpSiError);
		} catch(Exception ex) {
			String errorDescripcio = "Error finalitzant l'expedient migrant " + expedient.getTitol() + " a l'arxiu: " + ex.getMessage();
			throw new TramitacioException(
					expedient.getEntorn().getId(), 
					expedient.getEntorn().getCodi(), 
					expedient.getEntorn().getNom(), 
					expedient.getId(), 
					expedient.getTitol(), 
					expedient.getNumero(), 
					expedient.getTipus().getId(), 
					expedient.getTipus().getCodi(), 
					expedient.getTipus().getNom(), 
					errorDescripcio, 
					ex);
		}
	}

	@Override
	@Transactional(propagation=Propagation.REQUIRES_NEW)
	public void trySincronitzarArxiu(Long id) throws NoTrobatException{
		// Comprovam si ja s'esta executant la migració per aquest expedient
		if(isCurrentlyMigrating(id))
			return;

		Expedient expedient = null;
		Long reintents = 1L;
		try {
			addCurrentlyMigrating(id);
			expedient = expedientRepository.findById(id).orElse(null);
			if (expedient.getSyncReintents() != null) {
				reintents = expedient.getSyncReintents() + 1L;
			}
			logger.debug("Intent " + reintents + " de sincronització de l'expedient (id=" + id + ") a l'Arxiu");
			self.syncArxiu(id, false);
			self.syncDocumentsArxiu(id, false);
		} catch(Exception e) {
			if (expedient != null) {
				expedient.setSyncReintentData(new Date());
				expedient.setSyncReintents(reintents != null? reintents + 1 : 1);
			}
		} finally {
			deleteCurrentlyMigrating(id);
		}
	}

	@Transactional(propagation=Propagation.REQUIRES_NEW)
	public void undoSincronitzacioArxiu(Long id) {
		Expedient expedient = expedientRepository.findById(id).orElse(null);
		expedient.setArxiuUuid(null);
		expedient.setArxiuActiu(false);
		expedientRepository.save(expedient);
	}

	@Transactional(propagation=Propagation.REQUIRES_NEW)
	private List<DocumentStoreBackupDto> getDocumentsExpedient(Long expedientId) {
		Expedient expedient = expedientHelper.getExpedientComprovantPermisos(
				expedientId,
				new Permission[] {
						ExtendedPermission.READ,
						ExtendedPermission.WRITE,
						ExtendedPermission.ADMINISTRATION});
		List<DocumentStoreBackupDto> documentsExpedient = new ArrayList<DocumentStoreBackupDto>();
		List<InstanciaProcesDto> arbreProcesInstance = expedientHelper.getArbreInstanciesProces(expedient.getProcessInstanceId());

		// Genera llista de tots els documents del expedient
		for(InstanciaProcesDto procesInstance :arbreProcesInstance) {
			documentsExpedient.addAll(
					conversioTipusHelper.convertirList(
							documentStoreRepository.findByProcessInstanceId(procesInstance.getId()), DocumentStoreBackupDto.class));
		}
		return documentsExpedient;
	}

	@Transactional(propagation=Propagation.REQUIRES_NEW)
	public void undoSincronitzacioDocumentsArxiu(List<DocumentStoreBackupDto> documentsEstatAnterior) {
		for (DocumentStoreBackupDto documentStore: documentsEstatAnterior) {
			DocumentStore entity = documentStoreRepository.findById(documentStore.getId()).orElse(null);
			entity.setArxiuUuid(documentStore.getArxiuUuid());
			entity.setReferenciaFont(documentStore.getReferenciaFont());
			entity.setArxiuNom(documentStore.getArxiuNom());
			entity.setNtiIdentificador(documentStore.getNtiIdentificador());
			entity.setSignat(documentStore.isSignat());
			entity.setReferenciaCustodia(documentStore.getReferenciaCustodia());
			entity.setNtiDefinicionGenCsv(documentStore.getNtiDefinicionGenCsv());
			entity.setNtiCsv(documentStore.getNtiCsv());
			entity.setNtiTipoFirma(documentStore.getNtiTipoFirma());
			entity.setNtiIdentificador(documentStore.getNtiIdentificador());
			entity.setArxiuContingut(documentStore.getArxiuContingut());
			entity.setDocumentValid(documentStore.isDocumentValid());
			entity.setDocumentError(documentStore.getDocumentError());
			documentStoreRepository.save(entity);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	public void relacioCreate(
			Long origenId,
			Long destiId) {
		logger.debug("Creant relació d'expedients (" +
				"origenId=" + origenId + ", " +
				"destiId=" + destiId + ")");
		Expedient origen = expedientHelper.getExpedientComprovantPermisos(
				origenId,
				new Permission[] {
						ExtendedPermission.RELATE,
						ExtendedPermission.ADMINISTRATION});
		Expedient desti = expedientHelper.getExpedientComprovantPermisos(
				destiId,
				new Permission[] {
						ExtendedPermission.RELATE,
						ExtendedPermission.ADMINISTRATION});
		ExpedientLog expedientLogOrigen = expedientLoggerHelper.afegirLogExpedientPerExpedient(
				origenId,
				ExpedientLogAccioTipus.EXPEDIENT_RELACIO_AFEGIR,
				destiId.toString());
		expedientLogOrigen.setEstat(ExpedientLogEstat.IGNORAR);
		ExpedientLog expedientLogDesti = expedientLoggerHelper.afegirLogExpedientPerExpedient(
				destiId,
				ExpedientLogAccioTipus.EXPEDIENT_RELACIO_AFEGIR,
				origenId.toString());
		expedientLogDesti.setEstat(ExpedientLogEstat.IGNORAR);
		expedientHelper.relacioCrear(origen, desti);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	public void relacioDelete(
			Long origenId,
			Long destiId) {
		logger.debug("Esborrant relació d'expedients (" +
				"origenId=" + origenId + ", " +
				"destiId=" + destiId + ")");
		Expedient origen = expedientHelper.getExpedientComprovantPermisos(
				origenId,
				new Permission[] {
						ExtendedPermission.RELATE,
						ExtendedPermission.ADMINISTRATION});
		Expedient desti = expedientHelper.getExpedientComprovantPermisos(
				destiId,
				new Permission[] {
						ExtendedPermission.RELATE,
						ExtendedPermission.ADMINISTRATION});
		ExpedientLog expedientLogOrigen = expedientLoggerHelper.afegirLogExpedientPerExpedient(
				origenId,
				ExpedientLogAccioTipus.EXPEDIENT_RELACIO_ESBORRAR,
				destiId.toString());
		expedientLogOrigen.setEstat(ExpedientLogEstat.IGNORAR);
		ExpedientLog expedientLogDesti = expedientLoggerHelper.afegirLogExpedientPerExpedient(
				destiId,
				ExpedientLogAccioTipus.EXPEDIENT_RELACIO_ESBORRAR,
				origenId.toString());
		expedientLogDesti.setEstat(ExpedientLogEstat.IGNORAR);
		origen.removeRelacioOrigen(desti);
		desti.removeRelacioOrigen(origen);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly = true)
	public List<ExpedientDto> relacioFindAmbExpedient(Long id) {
		logger.debug("Consulta d'expedients relacionats amb l'expedient (" +
				"id=" + id + ")");
		Expedient expedient = expedientHelper.getExpedientComprovantPermisos(
				id,
				true,
				false,
				false,
				false);
		List<ExpedientDto> list = new ArrayList<ExpedientDto>();
		for (Expedient relacionat: expedient.getRelacionsOrigen()) {
			list.add(findAmbIdAmbPermis(relacionat.getId()));
		}
		return list;
	}

	/**
	 * Processament d'un script en un expedient.
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	public void procesScriptExec(
			Long expedientId,
			String processInstanceId,
			String script) throws PermisDenegatException, NoTrobatException{
		logger.debug("Executa script sobre l'expedient (" +
				"expedientId=" + expedientId + ", " +
				"processInstanceId=" + processInstanceId + ", " +
				"script=" + script + ")");

		// Obtenim expedient comprovant que esté permisos per executar scripts i administrar
		Expedient expedient = expedientHelper.getExpedientComprovantPermisos(
				expedientId,
				new Permission[] {
						ExtendedPermission.SCRIPT_EXE,
						ExtendedPermission.ADMINISTRATION});

		expedientHelper.comprovarInstanciaProces(expedient, processInstanceId);

		if (MesuresTemporalsHelper.isActiu()) {
			mesuresTemporalsHelper.mesuraIniciar("Executar SCRIPT", "expedient", expedient.getTipus().getNom());
		}

		// executa l'script
		workflowEngineApi.evaluateScript(processInstanceId, script, new HashSet<String>());


		expedientHelper.verificarFinalitzacioExpedient(expedient);
		expedientDadaHelper.setExpedientDades(expedient);
		expedientLoggerHelper.afegirLogExpedientPerProces(
				processInstanceId,
				ExpedientLogAccioTipus.PROCES_SCRIPT_EXECUTAR,
				script);
		if (MesuresTemporalsHelper.isActiu())
			mesuresTemporalsHelper.mesuraCalcular("Executar SCRIPT", "expedient", expedient.getTipus().getNom());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	public void procesDefinicioProcesActualitzar(
			String processInstanceId,
			int versio) {
		logger.debug("Canviant versió de la definició de procés (" +
				"processInstanceId=" + processInstanceId + ", " +
				"versio=" + versio + ")");
		Expedient piexp = expedientHelper.findExpedientByProcessInstanceId(processInstanceId);
		expedientHelper.getExpedientComprovantPermisos(
				piexp.getId(),
				new Permission[] {
						ExtendedPermission.DEFPROC_UPDATE,
						ExtendedPermission.ADMINISTRATION});
		DefinicioProces defprocAntiga = expedientHelper.findDefinicioProcesByProcessInstanceId(processInstanceId);
		if (defprocAntiga == null)
			throw new NoTrobatException(DefinicioProces.class, processInstanceId);
		workflowEngineApi.changeProcessInstanceVersion(processInstanceId, versio);
		// Apunta els terminis iniciats cap als terminis
		// de la nova definició de procés
		DefinicioProces defprocNova = expedientHelper.findDefinicioProcesByProcessInstanceId(processInstanceId);
		updateTerminis(processInstanceId, defprocAntiga, defprocNova);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	public void procesDefinicioProcesCanviVersio(
			Long expedientId,
			Long definicioProcesId,
			Long[] subProcesIds,
			List<DefinicioProcesExpedientDto> subDefinicioProces) {
		logger.debug("Canviant versió de les definicións de procés de l'expedient (" +
				"expedientId" + expedientId + ", " +
				"definicioProcesId" + definicioProcesId + ", " +
				"subProcesIds=" + subProcesIds + ")");
		Expedient expedient = expedientHelper.getExpedientComprovantPermisos(
				expedientId,
				new Permission[] {
						ExtendedPermission.DEFPROC_UPDATE,
						ExtendedPermission.ADMINISTRATION});
		if (!expedient.isAmbRetroaccio()) {
			workflowEngineApi.deleteProcessInstanceTreeLogs(expedient.getProcessInstanceId());
		}
		if (definicioProcesId != null) {
			DefinicioProces defprocAntiga = expedientHelper.findDefinicioProcesByProcessInstanceId(expedient.getProcessInstanceId());
			DefinicioProces defprocNova = definicioProcesRepository.findById(definicioProcesId).orElse(null);
			if (!defprocAntiga.equals(defprocNova)) {
				workflowEngineApi.changeProcessInstanceVersion(expedient.getProcessInstanceId(), defprocNova.getVersio());
				updateTerminis(expedient.getProcessInstanceId(), defprocAntiga, defprocNova);
			}
		}
		// Subprocessos
		if (subProcesIds != null && subProcesIds.length > 0) {
			// Arriben amb el mateix ordre??
			List<WProcessInstance> instanciesProces = workflowEngineApi.getProcessInstanceTree(expedient.getProcessInstanceId());
			for (WProcessInstance instanciaProces: instanciesProces) {
				DefinicioProces defprocAntiga = expedientHelper.findDefinicioProcesByProcessInstanceId(instanciaProces.getId().toString());
				int versio = findVersioDefProcesActualitzar(subDefinicioProces, subProcesIds, instanciaProces.getProcessDefinitionName());
				if (versio != -1 && versio != defprocAntiga.getVersio()) {
					workflowEngineApi.changeProcessInstanceVersion(instanciaProces.getId().toString(), versio);
					DefinicioProces defprocNova =  expedientHelper.findDefinicioProcesByProcessInstanceId(instanciaProces.getId().toString());
					updateTerminis(instanciaProces.getId().toString(), defprocAntiga, defprocNova);
				}
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly = true)
	public List<AccioDto> accioFindVisiblesAmbProcessInstanceId(
			Long expedientId,
			String processInstanceId) {
		logger.debug("Consultant les accions visibles de la instància de procés(" +
				"expedientId" + expedientId + ", " +
				"processInstanceId=" + processInstanceId + ")");
		Expedient expedient = expedientHelper.getExpedientComprovantPermisos(
				expedientId,
				true,
				false,
				false,
				false);
		expedientHelper.comprovarInstanciaProces(
				expedient,
				processInstanceId);
		List<Accio> accions = null;
		if (expedient.getTipus().isAmbInfoPropia()) {
			boolean ambHerencia = HerenciaHelper.ambHerencia(expedient.getTipus());
			accions = accioRepository.findAmbExpedientTipusAndOcultaFalse(
					expedient.getTipus().getId(),
					ambHerencia);
		} else {
			DefinicioProces definicioProces = expedientHelper.findDefinicioProcesByProcessInstanceId(processInstanceId);
			accions = accioRepository.findAmbDefinicioProcesAndOcultaFalse(definicioProces);
		}
		Iterator<Accio> it = accions.iterator();
		while (it.hasNext()) {
			Accio accio = it.next();
			if (!permetreExecutarAccioExpedient(
					accio,
					expedient))
				it.remove();
		}
		return conversioTipusHelper.convertirList(
				accions,
				AccioDto.class);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly=true)
	public AccioDto accioFindAmbId(
			Long expedientId,
			String processInstanceId,
			Long accioId) {
		logger.debug("Consultant l'acció amb l'id (" +
				"expedientId" + expedientId + ", " +
				"processInstanceId" + processInstanceId + ", " +
				"accioId=" + accioId + ")");
		Expedient expedient = expedientHelper.getExpedientComprovantPermisos(
				expedientId,
				true,
				false,
				false,
				false);
		expedientHelper.comprovarInstanciaProces(
				expedient,
				processInstanceId);
		return conversioTipusHelper.convertir(
				accioRepository.findById(accioId),
				AccioDto.class);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	public void accioExecutar(
			Long expedientId,
			String processInstanceId,
			Long accioId) {
		logger.debug("Executant l'acció a dins una instància de procés (" +
				"expedientId" + expedientId + ", " +
				"processInstanceId" + processInstanceId + ", " +
				"accioId=" + accioId + ")");

		Expedient expedient = expedientHelper.findExpedientByProcessInstanceId(processInstanceId);

		Accio accio = accioRepository.findById(accioId).orElse(null);
		if (accio == null)
			throw new NoTrobatException(Accio.class, accioId);
		if (permetreExecutarAccioExpedient(accio, expedient)) {
			mesuresTemporalsHelper.mesuraIniciar("Executar ACCIO" + accio.getNom(), "expedient", expedient.getTipus().getNom());
			expedientLoggerHelper.afegirLogExpedientPerProces(
					processInstanceId,
					ExpedientLogAccioTipus.EXPEDIENT_ACCIO,
					accio.getJbpmAction());
			try {
				this.executarAccio(processInstanceId, accio, expedient);
			} catch (Exception ex) {
//				if (ex instanceof ExecucioHandlerException) {
//					logger.error(
//							"Error al executar l'acció '" + accio.getCodi() + "': " + ex.toString(),
//							ex.getCause());
//				} else {
					logger.error(
							"Error al executar l'acció '" + accio.getCodi() + "'",
							ex);
//				}
				throw new TramitacioException(
						expedient.getEntorn().getId(),
						expedient.getEntorn().getCodi(),
						expedient.getEntorn().getNom(),
						expedient.getId(),
						expedient.getTitol(),
						expedient.getNumero(),
						expedient.getTipus().getId(),
						expedient.getTipus().getCodi(),
						expedient.getTipus().getNom(),
						"Error al executa l'acció '" + accio.getCodi() + "'"+ ex.getMessage(),
						ex);
			}
			expedientHelper.verificarFinalitzacioExpedient(expedient);
			expedientDadaHelper.setExpedientDades(expedient);
			mesuresTemporalsHelper.mesuraCalcular("Executar ACCIO" + accio.getNom(), "expedient", expedient.getTipus().getNom());
		} else {
			throw new PermisDenegatException(
					expedientId,
					Expedient.class,
					new Permission[]{ExtendedPermission.WRITE},
					null);
		}
	}

	private void executarAccio(String processInstanceId, Accio accio, Expedient expedient) {
		expedientHelper.executarAccio(processInstanceId, accio, expedient);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	public void executarCampAccio(
			Long expedientId,
			String processInstanceId,
			String accioCamp) {

		logger.debug("Executant camp acció dins una instància de procés (" +
				"expedientId" + expedientId + ", " +
				"processInstanceId" + processInstanceId + ", " +
				"accioCamp=" + accioCamp + ")");

		Expedient expedient;
		if (expedientId != null) {
			expedient = expedientHelper.findAmbEntornIId(EntornActual.getEntornId(), expedientId);
		} else {
			expedient = expedientHelper.findExpedientByProcessInstanceId(processInstanceId);
		}

		mesuresTemporalsHelper.mesuraIniciar("Executar ACCIO" + accioCamp, "expedient", expedient.getTipus().getNom());
		expedientLoggerHelper.afegirLogExpedientPerProces(
				processInstanceId,
				ExpedientLogAccioTipus.EXPEDIENT_ACCIO,
				accioCamp);
		try {
			if ( ExpedientTipusTipusEnumDto.FLOW.equals(expedient.getTipus().getTipus())) {
				// Acció JBPM
				workflowEngineApi.executeActionInstanciaProces(
						processInstanceId,
						accioCamp,
						herenciaHelper.getProcessDefinitionIdHeretadaAmbExpedient(expedient));
			} else {
				// Acció definida al tipus d'expedient
				Accio accio = accioRepository.findByExpedientTipusIdAndCodi(
						expedient.getTipus().getId(),
						accioCamp);
				if (accio == null) {
					throw new NoTrobatException(Accio.class, accioCamp);
				}
				this.executarAccio(processInstanceId, accio, expedient);
			}
		} catch (Exception ex) {
//			if (ex instanceof ExecucioHandlerException) {
//				logger.error(
//						"Error al executar l'acció '" + accioCamp + "': " + ex.toString(),
//						ex.getCause());
//			} else {
				logger.error(
						"Error al executar l'acció '" + accioCamp + "'",
						ex);
//			}
			throw new TramitacioException(
					expedient.getEntorn().getId(),
					expedient.getEntorn().getCodi(),
					expedient.getEntorn().getNom(),
					expedient.getId(),
					expedient.getTitol(),
					expedient.getNumero(),
					expedient.getTipus().getId(),
					expedient.getTipus().getCodi(),
					expedient.getTipus().getNom(),
					"Error al executar l'acció '" + accioCamp + "'",
					ex);
		}
		expedientHelper.verificarFinalitzacioExpedient(expedient);
		expedientDadaHelper.setExpedientDades(expedient);
		mesuresTemporalsHelper.mesuraCalcular("Executar CAMP ACCIO" + accioCamp, "expedient", expedient.getTipus().getNom());
	}


	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly = true)
	public List<AlertaDto> findAlertes(Long id) {
		logger.debug("Consulta alertes de l'expedient (" +
				"id=" + id + ")");
		Expedient expedient = expedientHelper.getExpedientComprovantPermisos(
				id,
				true,
				false,
				false,
				false);
		List<Alerta> alertes = alertaRepository.findByExpedientAndDataEliminacioNull(expedient);
		Collections.sort(alertes, new ComparadorAlertes());
		// Convertir a AlertaDto
		return conversioTipusHelper.convertirList(alertes, AlertaDto.class);
	}

	public class ComparadorAlertes implements Comparator<Alerta>{
		public int compare(Alerta a1, Alerta a2) {
			return a2.getDataCreacio().compareTo(a1.getDataCreacio());
		}

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly = true)
	public Object[] findErrorsExpedient(Long id) {
		logger.debug("Consulta errors de l'expedient (" +
				"id=" + id + ")");
		Expedient expedient = expedientHelper.getExpedientComprovantPermisos(
				id,
				true,
				false,
				false,
				false);
		List<Portasignatures> portasignatures = portasignaturesRepository.findByExpedientAndEstat(expedient, PortafirmesEstatEnum.ERROR);
		List<ExpedientErrorDto> errors_int = new ArrayList<ExpedientErrorDto>();

		if(!portasignatures.isEmpty()){
			for(Portasignatures ps: portasignatures) {
				errors_int.add(new ExpedientErrorDto(ErrorTipusDto.INTEGRACIONS, ps.getErrorCallbackProcessant()));
			}
		}

		List<ExpedientErrorDto> errors_bas = new ArrayList<ExpedientErrorDto>();
		if (expedient.getErrorDesc() != null) {
			errors_bas.add(new ExpedientErrorDto(ErrorTipusDto.BASIC, expedient.getErrorDesc(), expedient.getErrorFull()));
		}

		return new Object[]{errors_bas,errors_int};
	}

	@Override
	@Transactional
	public void netejarErrorsExp(Long id) throws NoTrobatException {
		Expedient expedient = expedientHelper.getExpedientComprovantPermisos(
				id,
				true,
				false,
				false,
				false);
		//No fa falta netejar el error del portafib, a més no tendria sentit si no es canvia també el TipusEstat
		//Això en teoria ja s'actualitza correctament al metode de processarDocumentPendentPortasignatures del PluginService
//		List<Portasignatures> portasignatures = portasignaturesRepository.findByExpedientAndEstat(expedient, PortafirmesEstatEnum.ERROR);
//		if(!portasignatures.isEmpty()){
//			for(Portasignatures ps: portasignatures) {
//				ps.setErrorCallbackProcessant(null);
//			}
//		}
		expedient.setErrorDesc(null);
		expedient.setErrorFull(null);
		expedient.setErrorsIntegracions(false);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly = true)
	public List<ExpedientDto> findSuggestAmbEntornLikeIdentificador(Long entornId, String text) {
		String textDecoded = null;
		try {
			textDecoded = new String(text.getBytes("ISO-8859-1"), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			logger.error("No s'ha pogut consultar el text " + textDecoded + ": " + e.getMessage());
		}
		List<ExpedientDto> resposta = new ArrayList<ExpedientDto>();
		List<Expedient> expedients = expedientRepository.findAmbEntornLikeIdentificador(entornId, textDecoded);
		for (Expedient expedient: expedients) {
			resposta.add(conversioTipusHelper.convertir(expedient,ExpedientDto.class));
		}
		return resposta;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly = true)
	public List<InstanciaProcesDto> getArbreInstanciesProces(
			String processInstanceId) {
		return expedientHelper.getArbreInstanciesProces(processInstanceId);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly = true)
	public PaginaDto<ExpedientConsultaDissenyDto> findConsultaInformePaginat(
			final Long consultaId,
			Map<String, Object> valorsPerService,
			boolean nomesMeves,
			boolean nomesAlertes,
			boolean mostrarAnulats,
			boolean nomesTasquesPersonals,
			boolean nomesTasquesGrup,
			 final PaginacioParamsDto paginacioParams) {
		mesuresTemporalsHelper.mesuraIniciar("CONSULTA INFORME EXPEDIENTS v3", "consulta");
		mesuresTemporalsHelper.mesuraIniciar("CONSULTA INFORME EXPEDIENTS v3", "consulta", null, null, "0");

		final List<ExpedientConsultaDissenyDto> expedientsConsultaDisseny = findConsultaDissenyPaginat(
			consultaId,
			valorsPerService,
			paginacioParams,
			nomesMeves,
			nomesAlertes,
			mostrarAnulats,
			nomesTasquesPersonals,
			nomesTasquesGrup,
			null
		);

		mesuresTemporalsHelper.mesuraCalcular("CONSULTA INFORME EXPEDIENTS v3", "consulta", null, null, "0");
		mesuresTemporalsHelper.mesuraIniciar("CONSULTA INFORME EXPEDIENTS v3", "consulta", null, null, "1");

		final int numExpedients= findIdsPerConsultaInforme(
				consultaId,
				valorsPerService,
				nomesMeves,
				nomesAlertes,
				mostrarAnulats,
				nomesTasquesPersonals,
				nomesTasquesGrup
			).size();

		mesuresTemporalsHelper.mesuraCalcular("CONSULTA INFORME EXPEDIENTS v3", "consulta", null, null, "1");
		mesuresTemporalsHelper.mesuraIniciar("CONSULTA INFORME EXPEDIENTS v3", "consulta", null, null, "2");

		Page<ExpedientConsultaDissenyDto> paginaResultats = new Page<ExpedientConsultaDissenyDto>() {

			@Override
			public Iterator<ExpedientConsultaDissenyDto> iterator() {
				return getContent().iterator();
			}

			@Override
			public boolean hasContent() {
				return !expedientsConsultaDisseny.isEmpty();
			}

			@Override
			public int getTotalPages() {
				return 0;
			}

			@Override
			public long getTotalElements() {
				return numExpedients;
			}

			@Override
			public Sort getSort() {
				List<Order> orders = new ArrayList<Order>();
				for (OrdreDto or : paginacioParams.getOrdres()) {
					orders.add(new Order(or.getDireccio().equals(OrdreDireccioDto.ASCENDENT) ? Direction.ASC : Direction.DESC, or.getCamp()));
				}
				return Sort.by(orders);
			}

			@Override
			public int getSize() {
				return paginacioParams.getPaginaTamany();
			}

			@Override
			public int getNumberOfElements() {
				return 0;
			}

			@Override
			public int getNumber() {
				return 0;
			}

			@Override
			public List<ExpedientConsultaDissenyDto> getContent() {
				return expedientsConsultaDisseny;
			}

			@Override
			public boolean isFirst() {
				return paginacioParams.getPaginaNum() == 0;
			}

			@Override
			public boolean isLast() {
				return false;
			}

			@Override
			public boolean hasNext() {
				return false;
			}

			@Override
			public boolean hasPrevious() {
				return paginacioParams.getPaginaNum() > 0;
			}

			@Override
			public Pageable nextPageable() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Pageable previousPageable() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public <U> Page<U> map(Function<? super ExpedientConsultaDissenyDto, ? extends U> converter) {
				// TODO Auto-generated method stub
				return null;
			}
		};

		PaginaDto<ExpedientConsultaDissenyDto> resposta = paginacioHelper.toPaginaDto(
				paginaResultats,
				ExpedientConsultaDissenyDto.class);

		mesuresTemporalsHelper.mesuraCalcular("CONSULTA INFORME EXPEDIENTS v3", "consulta", null, null, "2");
		mesuresTemporalsHelper.mesuraCalcular("CONSULTA INFORME EXPEDIENTS v3", "consulta");
		return resposta;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly=true)
	public InstanciaProcesDto getInstanciaProcesById(String processInstanceId) {
		InstanciaProcesDto dto = new InstanciaProcesDto();
		dto.setId(processInstanceId);
		WProcessInstance pi = workflowEngineApi.getProcessInstance(processInstanceId);
		if (pi == null )
			return null;
		dto.setInstanciaProcesPareId(pi.getParentProcessInstanceId());
		if (pi.getDescription() != null && pi.getDescription().length() > 0)
			dto.setTitol(pi.getDescription());
		DefinicioProces definicioProces = definicioProcesRepository.findByJbpmId(pi.getProcessDefinitionId());
		dto.setDefinicioProces(conversioTipusHelper.convertir(definicioProces, DefinicioProcesDto.class));
		List<ExpedientDocumentDto> documents = documentHelper.findDocumentsPerInstanciaProces(processInstanceId);
		Map<String, DocumentDto> documentsDto = new HashMap<String, DocumentDto>();
		for(ExpedientDocumentDto doc : documents) {
			documentsDto.put(doc.getDocumentCodi(), conversioTipusHelper.convertir(doc, DocumentDto.class));
		}
		List<Document> documentsIP = documentHelper.findDocumentsExpedient(expedientHelper.findExpedientByProcessInstanceId(processInstanceId), pi.getId().toString());
		if (documentsIP!=null) {
			for (Document doc: documentsIP) {
				if (doc.isPinbalActiu()) {
					dto.setDocumentsPinbal(true);
					break;
				}
			}
		}
		dto.setVarsDocuments(documentsDto);
		return dto;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly=true)
	public List<CampDto> getCampsInstanciaProcesById(
			Long expedientTipusId,
			String processInstanceId) {
		ExpedientTipus expedientTipus = expedientTipusRepository.findById(expedientTipusId).orElse(null);
		DefinicioProces definicioProces = expedientHelper.findDefinicioProcesByProcessInstanceId(
				processInstanceId);

		List<Camp> camps;
		if (expedientTipus.isAmbInfoPropia()) {
			camps = campRepository.findByExpedientTipusAmbHerencia(expedientTipusId);
		} else {
			camps = campRepository.findByDefinicioProcesOrderByCodiAsc(definicioProces);
		}
		return conversioTipusHelper.convertirList(camps, CampDto.class);


	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly=true)
	public List<TascaDadaDto> findConsultaFiltre(Long consultaId) {
		Consulta consulta = consultaRepository.findById(consultaId).orElse(null);

		List<TascaDadaDto> listTascaDada = consultaHelper.findCampsPerCampsConsulta(
				consulta,
				TipusConsultaCamp.FILTRE);

		// Quitamos las variables predefinidas de los filtros con amplitud 0
		Iterator<TascaDadaDto> itListTascaDada = listTascaDada.iterator();
		TascaDadaDto tascaDada;
		while(itListTascaDada.hasNext()) {
			tascaDada = itListTascaDada.next();
			if (consulta.getMapValorsPredefinits().containsKey(tascaDada.getVarCodi())
					&& tascaDada.getAmpleCols() == 0) {
				itListTascaDada.remove();
			}
		}

		return listTascaDada;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly=true)
	public List<TascaDadaDto> findConsultaInforme(Long consultaId) {
		Consulta consulta = consultaRepository.findById(consultaId).orElse(null);
		return consultaHelper.findCampsPerCampsConsulta(
				consulta,
				TipusConsultaCamp.INFORME);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly=true)
	public List<TascaDadaDto> findConsultaInformeParams(Long consultaId) {
		Consulta consulta = consultaRepository.findById(consultaId).orElse(null);
		return consultaHelper.findCampsPerCampsConsulta(
				consulta,
				TipusConsultaCamp.PARAM);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly=true)
	public List<ExpedientConsultaDissenyDto> findConsultaDissenyPaginat(
			Long consultaId,
			Map<String, Object> valors,
			PaginacioParamsDto paginacioParams,
			boolean nomesMeves,
			boolean nomesAlertes,
			boolean mostrarAnulats,
			boolean nomesTasquesPersonals,
			boolean nomesTasquesGrup,
			Set<Long> ids) {
		Consulta consulta = consultaRepository.findById(consultaId).orElse(null);

		List<Camp> campsFiltre = consultaHelper.toListCamp(consultaHelper.findCampsPerCampsConsulta(
				consulta,
				TipusConsultaCamp.FILTRE));

		List<Camp> campsInforme = consultaHelper.toListCamp(consultaHelper.findCampsPerCampsConsulta(
				consulta,
				TipusConsultaCamp.INFORME));

		afegirValorsPredefinits(consulta, valors, campsFiltre);

		String sort = "expedient$identificador"; //ExpedientCamps.EXPEDIENT_CAMP_ID;
		boolean asc = false;
		int firstRow = 0;
		int maxResults = -1;

		if (paginacioParams != null) {
			for (OrdreDto or : paginacioParams.getOrdres()) {
				asc = or.getDireccio().equals(OrdreDireccioDto.ASCENDENT);
				if (or.getCamp().contains("dadesExpedient")) {
					sort = or.getCamp().replace("/", ".").replace("dadesExpedient.", "").replace(".valorMostrar", "");
				} else {
					sort = or.getCamp().replace(
							".",
							ExpedientCamps.EXPEDIENT_PREFIX_SEPARADOR);
				}
				break;
			}
			firstRow = paginacioParams.getPaginaNum()*paginacioParams.getPaginaTamany();
			maxResults = paginacioParams.getPaginaTamany();
		}
		List<Long> llistaExpedientIds = new ArrayList<Long>();
		if (ids == null || ids.isEmpty()) {
			llistaExpedientIds = expedientDadaHelper.findExpedientsIdsByFiltre(
				consulta.getEntorn().getId(),
				consulta.getExpedientTipus() != null ? consulta.getExpedientTipus().getId() : null,
				campsFiltre,
				valors,
				List.of(new PaginacioParamsDto.OrdreDto(sort, asc? OrdreDireccioDto.ASCENDENT : OrdreDireccioDto.DESCENDENT))
			);
		} else {
			llistaExpedientIds.addAll(ids);
		}
		boolean filtreTasques = nomesMeves || nomesTasquesPersonals || nomesTasquesGrup;
		if (filtreTasques) {
			filtrarExpedientsAmbTasques(
					llistaExpedientIds,
					nomesMeves,
					nomesAlertes,
					mostrarAnulats,
					nomesTasquesPersonals,
					nomesTasquesGrup);
		}
		List<Map<String, DadaIndexadaDto>> dadesExpedients = new ArrayList<Map<String,DadaIndexadaDto>>();
		if (!llistaExpedientIds.isEmpty())
			dadesExpedients = expedientDadaHelper.findDadesExpedients(
				consulta.getEntorn().getId(),
				null,
				llistaExpedientIds,
				null,
				null,
				campsInforme,
				Lists.newArrayList(
					new OrdreDto(sort, asc? OrdreDireccioDto.ASCENDENT : OrdreDireccioDto.DESCENDENT)
				),
				firstRow,
				maxResults);

		List<ExpedientConsultaDissenyDto> resposta = new ArrayList<ExpedientConsultaDissenyDto>();
		for (Map<String, DadaIndexadaDto> dadesExpedient: dadesExpedients) {
			DadaIndexadaDto dadaExpedientId = dadesExpedient.get(ExpedientDadaHelper.CLAU_EXPEDIENT_ID);
			ExpedientConsultaDissenyDto fila = new ExpedientConsultaDissenyDto();
			Expedient expedient = expedientRepository.findById(Long.parseLong(dadaExpedientId.getValorIndex())).orElse(null);
			if (expedient != null) {
				ExpedientDto expedientDto = expedientHelper.toExpedientDto(expedient);
				expedientHelper.omplirPermisosExpedient(expedientDto);
				fila.setExpedient(expedientDto);
				consultaHelper.revisarDadesExpedientAmbValorsEnumeracionsODominis(
						dadesExpedient,
						campsInforme,
						expedient);
				fila.setDadesExpedient(dadesExpedient);
				resposta.add(fila);
			}
			dadesExpedient.remove(ExpedientDadaHelper.CLAU_EXPEDIENT_ID);
		}

		if (paginacioParams == null) {
			Iterator<Map<String, DadaIndexadaDto>> it = dadesExpedients.iterator();
			while (it.hasNext()) {
				Map<String, DadaIndexadaDto> dadesExpedient = it.next();
				DadaIndexadaDto dadaExpedientId = dadesExpedient.get(ExpedientDadaHelper.CLAU_EXPEDIENT_ID);
				if (dadaExpedientId != null && !llistaExpedientIds.contains(Long.parseLong(dadaExpedientId.getValorIndex()))) {
					it.remove();
				}
			}
		}
		return resposta;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly = true)
	public List<Long> findIdsPerConsultaInforme(
			Long consultaId,
			Map<String, Object> valors,
			boolean nomesMeves,
			boolean nomesAlertes,
			boolean mostrarAnulats,
			boolean nomesTasquesPersonals,
			boolean nomesTasquesGrup) {
		Consulta consulta = consultaRepository.findById(consultaId).orElse(null);

		List<Camp> campsFiltre = consultaHelper.toListCamp(consultaHelper.findCampsPerCampsConsulta(
				consulta,
				TipusConsultaCamp.INFORME));

		afegirValorsPredefinits(consulta, valors, campsFiltre);

		List<Long> llistaExpedientIds = expedientDadaHelper.findExpedientsIdsByFiltre(
				consulta.getEntorn().getId(),
				consulta.getExpedientTipus() != null? consulta.getExpedientTipus().getId() : null,
				campsFiltre,
				valors
			);
		boolean filtreTasques = nomesMeves || nomesTasquesPersonals || nomesTasquesGrup;
		if (filtreTasques) {
			filtrarExpedientsAmbTasques(
					llistaExpedientIds,
					nomesMeves,
					nomesAlertes,
					mostrarAnulats,
					nomesTasquesPersonals,
					nomesTasquesGrup);
		}
		return llistaExpedientIds;
	}

	@Override
	@Transactional(readOnly=true)
	public List<ExpedientConsultaDissenyDto> findExpedientsExportacio(List<Long> ids, EntornDto entornActual) {
		List<ExpedientConsultaDissenyDto> resposta = new ArrayList<ExpedientConsultaDissenyDto>();
		if (ids == null || ids.isEmpty()) {
			return resposta;
		}
		List<Camp> camps = new ArrayList<Camp>();
		List<Expedient> listExpedients = expedientRepository.findByIdIn(ids);
		ExpedientTipus expedientTipus = expedientRepository.findById(listExpedients.get(0).getId()).orElse(null).getTipus();
		if(expedientTipus.isAmbInfoPropia()) {
			camps = campRepository.findByExpedientTipusAmbHerencia(expedientTipus.getId());
		} else {
			List<Long> defProcIds = definicioProcesRepository.findIdsDarreraVersioAmbEntornIdIExpedientTipusId(
					entornActual.getId(),
					expedientTipus.getId());
			if(defProcIds!=null && !defProcIds.isEmpty()) {
				for(Long idDefProc : defProcIds) {
					DefinicioProces defProces = definicioProcesRepository.findById(idDefProc).orElse(null);
					camps.addAll(campRepository.findByDefinicioProcesOrderByCodiAsc(defProces));
				}
			}
		}
		String sort = "expedient$identificador"; //ExpedientCamps.EXPEDIENT_CAMP_ID;
//		boolean asc = false;
		int firstRow = 0;
		int maxResults = -1;

		List<PaginacioParamsDto.OrdreDto> orders = Lists.newArrayList(
			new PaginacioParamsDto.OrdreDto(sort, OrdreDireccioDto.DESCENDENT));

		List<Map<String, DadaIndexadaDto>> dadesExpedients = expedientDadaHelper.findDadesExpedients(
									entornActual.getId(),
									null,
									ids,
								null,
									null,
									camps,
									orders,
									firstRow,
									maxResults);

		for (Map<String, DadaIndexadaDto> dadesExpedient: dadesExpedients) {
			DadaIndexadaDto dadaExpedientId = dadesExpedient.get(ExpedientDadaHelper.CLAU_EXPEDIENT_ID);
			ExpedientConsultaDissenyDto fila = new ExpedientConsultaDissenyDto();
			Expedient expedient = expedientRepository.findById(((BigDecimal)dadaExpedientId.getValor()).longValue()).orElse(null);
			if (expedient != null) {
				ExpedientDto expedientDto = expedientHelper.toExpedientDto(expedient);
				expedientHelper.omplirPermisosExpedient(expedientDto);
				fila.setExpedient(expedientDto);
				consultaHelper.revisarDadesExpedientAmbValorsEnumeracionsODominis(
						dadesExpedient,
						camps,
						expedient);
				fila.setDadesExpedient(dadesExpedient);
				resposta.add(fila);
			}
			dadesExpedient.remove(ExpedientDadaHelper.CLAU_EXPEDIENT_ID);
		}

		Iterator<Map<String, DadaIndexadaDto>> it = dadesExpedients.iterator();
		while (it.hasNext()) {
			Map<String, DadaIndexadaDto> dadesExpedient = it.next();
			DadaIndexadaDto dadaExpedientId = dadesExpedient.get(ExpedientDadaHelper.CLAU_EXPEDIENT_ID);
			if (dadaExpedientId != null && !ids.contains(Long.parseLong(dadaExpedientId.getValorIndex()))) {
				it.remove();
			}
		}
		return resposta;
	}

    @Override
	@Transactional(readOnly=true)
    public String getExpedientProcessInstanceId(Long expedientId) {
        return expedientRepository.getExpedientProcessInstanceId(expedientId);
    }

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly=true)
	public boolean existsExpedientAmbEntornTipusITitol(Long entornId, Long expedientTipusId, String titol) {
		List<Expedient> expedients = expedientHelper.findByEntornIdAndTipusAndTitol(entornId, expedientTipusId, titol);
		return !expedients.isEmpty();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly=true)
	public String getNumeroExpedientActual(
			Long entornId,
			Long expedientTipusId,
			Integer any) {
		logger.debug("Consulta del número d'expedient pel tipus d'expedient(" +
				"entornId" + entornId + ", " +
				"expedientTipusId" + expedientTipusId + ", " +
				"any=" + any + ")");
		return expedientHelper.getNumeroExpedientActual(
				entornId,
				expedientTipusId,
				any);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly=true)
	public ExpedientTascaDto getStartTask(
			Long entornId,
			Long expedientTipusId,
			Long definicioProcesId,
			Map<String, Object> valors) {
		ExpedientTipus expedientTipus = expedientTipusRepository.findById(expedientTipusId).orElse(null);
		DefinicioProces definicioProces = null;
		if (definicioProcesId != null) {
			definicioProces = definicioProcesRepository.findById(definicioProcesId).orElse(null);
		}

		if (definicioProces == null){
			definicioProces = definicioProcesRepository.findDarreraVersioAmbTipusExpedientIJbpmKey(
					expedientTipus.getId(),
					expedientTipus.getJbpmProcessDefinitionKey());
		}
		if (definicioProcesId == null && definicioProces == null) {
			logger.error("No s'ha trobat la definició de procés (entorn=" + entornId + ", jbpmKey=" + expedientTipus.getJbpmProcessDefinitionKey() + ")");
		}
		String startTaskName = definicioProces.getStartTaskName();
		if (startTaskName != null) {
			return tascaHelper.toTascaInicialDto(startTaskName, definicioProces.getJbpmId(), valors);
		}
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly = true)
	public PaginaDto<ExpedientConsultaDissenyDto> consultaFindPaginat(
			Long consultaId,
			Map<String, Object> filtreValors,
			Set<Long> expedientIdsSeleccio,
			boolean nomesTasquesPersonals,
			boolean nomesTasquesGrup,
			boolean nomesMeves,
			boolean nomesAlertes,
			boolean nomesErrors,
			boolean nomesErrorsArxiu,
			MostrarAnulatsDto mostrarAnulats,
			PaginacioParamsDto paginacioParams) {
		logger.debug("Consulta general d'expedients paginada (" +
				"consultaId=" + consultaId + ", " +
				"filtreValors=" + filtreValors + ", " +
				"expedientIdsSeleccio=" + expedientIdsSeleccio + ", " +
				"nomesTasquesPersonals=" + nomesTasquesPersonals + ", " +
				"nomesTasquesGrup=" + nomesTasquesGrup + ", " +
				"nomesMeves=" + nomesMeves + ", " +
				"nomesAlertes=" + nomesAlertes + ", " +
				"nomesErrors=" + nomesErrors + ", " +
				"mostrarAnulats=" + mostrarAnulats +
				"paginacioParams=" + paginacioParams + ")");
		// Comprova l'accés a la consulta
		Consulta consulta = consultaRepository.findById(consultaId).orElse(null);
		if (consulta == null) {
			throw new NoTrobatException(Consulta.class,consultaId);
		}

		List<TascaDadaDto> campsConsulta_ = consultaHelper.findCampsPerCampsConsulta(
				consulta,
				TipusConsultaCamp.INFORME);

		List<Map<String, DadaIndexadaDto>> result = expedientDadaHelper.findDadesExpedients(
			consulta.getEntorn().getId(),
			consulta.getExpedientTipus().getId(),
			null,
			consultaHelper.toListCamp(consultaHelper.findCampsPerCampsConsulta(
				consulta,
				TipusConsultaCamp.FILTRE)),
			filtreValors,
			consultaHelper.toListCamp(consultaHelper.findCampsPerCampsConsulta(
				consulta,
				TipusConsultaCamp.INFORME)),
			paginacioParams.getOrdres(),
			paginacioParams.getPaginaNum(),
			paginacioParams.getPaginaTamany()
		);

		List<ExpedientConsultaDissenyDto> resposta = new ArrayList<ExpedientConsultaDissenyDto>();
		for(Map<String, DadaIndexadaDto> row : result) {
			DadaIndexadaDto expedientIdDada = row.get(ExpedientDadaHelper.CLAU_EXPEDIENT_ID);
			Expedient expedient = expedientRepository.getReferenceById(Long.parseLong(expedientIdDada.getValorIndex()));
			ExpedientDto expedientDto = expedientHelper.toExpedientDto(expedient);
			ExpedientConsultaDissenyDto fila = new ExpedientConsultaDissenyDto();
			fila.setExpedient(expedientDto);
			fila.setDadesExpedient(row);
			resposta.add(fila);
		}

		return paginacioHelper.toPaginaDto(
				resposta,
				result.size(),
				paginacioParams);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly = true)
	public PaginaDto<Long> consultaFindNomesIdsPaginat(
			Long consultaId,
			Map<String, Object> filtreValors,
			boolean nomesTasquesPersonals,
			boolean nomesTasquesGrup,
			boolean nomesMeves,
			boolean nomesAlertes,
			boolean nomesErrors,
			boolean nomesErrorsArxiu,
			MostrarAnulatsDto mostrarAnulats,
			PaginacioParamsDto paginacioParams) {
		logger.debug("Consulta general d'expedients paginada (" +
				"consultaId=" + consultaId + ", " +
				"filtreValors=" + filtreValors + ", " +
				"nomesTasquesPersonals=" + nomesTasquesPersonals + ", " +
				"nomesTasquesGrup=" + nomesTasquesGrup + ", " +
				"nomesMeves=" + nomesMeves + ", " +
				"nomesAlertes=" + nomesAlertes + ", " +
				"nomesErrors=" + nomesErrors + ", " +
				"mostrarAnulats=" + mostrarAnulats +
				"paginacioParams=" + paginacioParams + ")");
		// Comprova l'accés a la consulta
		Consulta consulta = consultaRepository.findById(consultaId).orElse(null);
		if (consulta == null) {
			throw new NoTrobatException(Consulta.class,consultaId);
		}
		// Comprova l'accés a l'entorn
		Entorn entorn = entornHelper.getEntornComprovantPermisos(
				consulta.getEntorn().getId(),
				true);
		// Comprova l'accés al tipus d'expedient
		ExpedientTipus expedientTipus = expedientTipusHelper.getExpedientTipusComprovantPermisLectura(
					consulta.getExpedientTipus().getId());
		// Obte la llista d'expedients permesos segons els filtres
		List<Long> tipusPermesosIds = expedientTipusHelper.findIdsAmbPermisRead(entorn);
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		Map<Long,List<Long>> unitatsPerTipusComu = new HashMap<Long, List<Long>>();
		List<ExpedientTipusUnitatOrganitzativa> expTipUnitOrgList = expedientTipusUnitatOrganitzativaRepository.findByExpedientTipusEntornId(entorn.getId());
		Permission[] permisosRequerits= new Permission[] {
				ExtendedPermission.READ,
				ExtendedPermission.ADMINISTRATION};
		unitatsPerTipusComu = expedientTipusHelper.unitatsPerTipusComuIds(entorn.getId(),expTipUnitOrgList, permisosRequerits);
		List<Long> expedientsIds = workflowEngineApi.expedientFindByFiltre(
				entorn.getId(),
				auth.getName(),
				tipusPermesosIds,
				unitatsPerTipusComu,
				null,
				null,
				null,//unitatOrganitzativaCodi
				expedientTipus.getId(),
				null,
				null,
				null,
				null,
				null,
				null,
				null,
				null,
				null,
				false,
				false,
				MostrarAnulatsDto.SI.equals(mostrarAnulats),
				MostrarAnulatsDto.NOMES_ANULATS.equals(mostrarAnulats),
				nomesAlertes,
				nomesErrors,
				nomesTasquesPersonals,
				nomesTasquesGrup,
				nomesMeves,
				usuariActualHelper.isAdministrador() || entornHelper.esAdminEntorn(entorn.getId())? null : usuariActualHelper.getAreesGrupsUsuariActual(),
				new PaginacioParamsDto(),
				false,
				nomesErrorsArxiu,
				null //idsSeleccionats
				);
		// Obte la llista d'ids de lucene passant els expedients permesos
		// com a paràmetres
		List<Camp> filtreCamps = consultaHelper.toListCamp(
				consultaHelper.findCampsPerCampsConsulta(
				consulta,
				TipusConsultaCamp.FILTRE));
		afegirValorsPredefinits(consulta, filtreValors, filtreCamps);
		List<Long> ids = expedientDadaHelper.findExpedientsIdsByFiltre(
			entorn.getId(),
			expedientTipus != null? expedientTipus.getId() : null,
			filtreCamps,
			filtreValors,
			paginacioParams.getOrdres());
//		Object[] respostaLucene = luceneHelper.findPaginatNomesIdsV3(
//				entorn,
//				expedientTipus,
//				expedientsIds,
//				filtreCamps,
//				filtreValors,
//				paginacioParams);
//		@SuppressWarnings("unchecked")
//		List<Long> ids = (List<Long>)respostaLucene[0];
//		Long count = (Long)respostaLucene[1];
		return paginacioHelper.toPaginaDto(
				ids,
				ids.size(),
				paginacioParams);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly = true)
	public boolean existsExpedientAmbEntornTipusINumero(Long entornId, Long expedientTipusId, String numero) {
		return expedientRepository.findByEntornIdAndTipusIdAndNumero(
				entornId,
				expedientTipusId,
				numero) != null;
	}

	@Transactional(readOnly = true)
	@Override
	public ArxiuDetallDto getArxiuDetall(Long expedientId) {
		logger.debug("Obtenint informació de l'arxiu per l'expedient ("
				+ "expedientId=" + expedientId + ")");
		Expedient expedient = expedientHelper.getExpedientComprovantPermisos(
				expedientId,
				true,
				false,
				false,
				false);
		ArxiuDetallDto arxiuDetall = null;
		if (expedient.isArxiuActiu()
				&& expedient.getArxiuUuid() != null) {
			arxiuDetall = new ArxiuDetallDto();
			es.caib.pluginsib.arxiu.api.Expedient arxiuExpedient = pluginHelper.arxiuExpedientInfo(expedient.getArxiuUuid());
			List<ContingutArxiu> continguts = arxiuExpedient.getContinguts();
			arxiuDetall.setIdentificador(arxiuExpedient.getIdentificador());
			arxiuDetall.setNom(arxiuExpedient.getNom());
			ExpedientMetadades metadades = arxiuExpedient.getMetadades();
			if (metadades != null) {
				arxiuDetall.setEniVersio(metadades.getVersioNti());
				arxiuDetall.setEniIdentificador(metadades.getIdentificador());
				arxiuDetall.setSerieDocumental(metadades.getSerieDocumental());
				arxiuDetall.setEniDataObertura(metadades.getDataObertura());
				arxiuDetall.setEniClassificacio(metadades.getClassificacio());
				if (metadades.getEstat() != null) {
					switch (metadades.getEstat()) {
					case OBERT:
						arxiuDetall.setEniEstat(NtiExpedienteEstadoEnumDto.OBERT);
						break;
					case TANCAT:
						arxiuDetall.setEniEstat(NtiExpedienteEstadoEnumDto.TANCAT);
						break;
					case INDEX_REMISSIO:
						arxiuDetall.setEniEstat(NtiExpedienteEstadoEnumDto.INDEX_REMISSIO);
						break;
					}
				}
				arxiuDetall.setEniInteressats(metadades.getInteressats());
				arxiuDetall.setEniOrgans(metadades.getOrgans());
				arxiuDetall.setMetadadesAddicionals(metadades.getMetadadesAddicionals());
			}
			if (continguts != null) {
				List<ArxiuContingutDto> detallFills = new ArrayList<ArxiuContingutDto>();
				for (ContingutArxiu cont: continguts) {
					ArxiuContingutDto detallFill = new ArxiuContingutDto();
					detallFill.setIdentificador(
							cont.getIdentificador());
					detallFill.setNom(
							cont.getNom());
					if (cont.getTipus() != null) {
						switch (cont.getTipus()) {
						case EXPEDIENT:
							detallFill.setTipus(ArxiuContingutTipusEnumDto.EXPEDIENT);
							break;
						case DOCUMENT:
							detallFill.setTipus(ArxiuContingutTipusEnumDto.DOCUMENT);
							break;
						case CARPETA:
							detallFill.setTipus(ArxiuContingutTipusEnumDto.CARPETA);
							break;
						}
					}
					detallFills.add(detallFill);
				}
				arxiuDetall.setFills(detallFills);
			}
		}
		return arxiuDetall;
	}


	@Transactional(readOnly = true)
	@Override
	public List<ExpedientDto> findAmbIniciadorCodi(String iniciadorCodi) {
		logger.debug("Consultant expedients per iniciadorCodi (iniciadorCodi=" + iniciadorCodi + ")");
		List<Expedient> expedients = expedientRepository.findByIniciadorCodi(iniciadorCodi);
		return conversioTipusHelper.convertirList(expedients, ExpedientDto.class);
	}


	private Registre crearRegistreExpedient(
			Long expedientId,
			String responsableCodi,
			Registre.Accio accio) {
		Registre registre = new Registre(
				new Date(),
				expedientId,
				responsableCodi,
				accio,
				Registre.Entitat.EXPEDIENT,
				String.valueOf(expedientId));
		return registreRepository.save(registre);
	}

	private boolean permetreExecutarAccioExpedient(
			Accio accio,
			Expedient expedient) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		boolean permesa = false;

		if (accio.getRols() == null || accio.getRols().isEmpty()) {
			permesa = true;
		} else {
			for (String rol: accio.getRols().split(",")) {
				if (isUserInRole(auth, rol)) {
					permesa = true;
					break;
				}
			}
		}

		if (permesa && !accio.isPublica()) {
			permesa = permisosHelper.isGrantedAny(
					expedient.getTipus().getId(),
					ExpedientTipus.class,
					new Permission[] {
							ExtendedPermission.WRITE,
							ExtendedPermission.ADMINISTRATION},
					auth);
		}
		return permesa;
	}

//	private boolean permetreExecutarAccioExpedient(
//			Accio accio,
//			Expedient expedient) {
//		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//		boolean permesa = true;
//		if (!accio.isPublica()) {
//			permesa = permisosHelper.isGrantedAny(
//					expedient.getTipus().getId(),
//					ExpedientTipus.class,
//					new Permission[] {
//							ExtendedPermission.WRITE,
//							ExtendedPermission.ADMINISTRATION},
//					auth);
//		}
//		if (permesa && accio.getRols() != null && !accio.getRols().isEmpty()) {
//			permesa = false;
//			for (String rol: accio.getRols().split(",")) {
//				if (isUserInRole(auth, rol)) {
//					permesa = true;
//					break;
//				}
//			}
//		}
//		return permesa;
//	}

	/** Afegeix els valors predefinits si aquest no existeixen. */
	private void afegirValorsPredefinits(
			Consulta consulta,
			Map<String, Object> valors,
			List<Camp> campsFiltre) {
		for (Camp camp: campsFiltre) {
			String campFiltreCodi = camp.getDefinicioProces()!= null ? camp.getDefinicioProces().getJbpmKey() + "." + camp.getCodi() : camp.getCodi();
			if (consulta.getMapValorsPredefinits().containsKey(camp.getCodi())
					&& !valors.containsKey(campFiltreCodi)) {
				valors.put(
						campFiltreCodi,
						Camp.getComObject(
								camp.getTipus(),
								consulta.getMapValorsPredefinits().get(camp.getCodi())));
			}
		}
	}

	private boolean isUserInRole(
			Authentication auth,
			String role) {
		for (GrantedAuthority ga: auth.getAuthorities()) {
			if (role.equals(ga.getAuthority()))
				return true;
		}
		return false;
	}

	private int findVersioDefProcesActualitzar(List<DefinicioProcesExpedientDto> definicionsProces, Long[] definicionsProcesId, String key) {
		int versio = -1;
		int i = 0;
		while (i < definicionsProces.size() && !definicionsProces.get(i).getJbpmKey().equals(key))
			i++;
		if (i < definicionsProces.size() && definicionsProcesId[i] != null) {
			DefinicioProces definicioProces = definicioProcesRepository.findById(definicionsProcesId[i]).orElse(null);
			if (definicioProces != null)
				versio = definicioProces.getVersio();
		}
		return versio;
	}

	// Apunta els terminis iniciats cap als terminis de la nova definició de procés
	private void updateTerminis(String procesInstanceId, DefinicioProces defprocAntiga, DefinicioProces defprocNova) {
		List<TerminiIniciat> terminisIniciats = terminiIniciatRepository.findByProcessInstanceId(procesInstanceId);
		for (TerminiIniciat terminiIniciat: terminisIniciats) {
			Termini termini = terminiIniciat.getTermini();
			if (termini.getDefinicioProces() != null && termini.getDefinicioProces().getId().equals(defprocAntiga.getId())) {
				for (Termini terminiNou: defprocNova.getTerminis()) {
					if (terminiNou.getCodi().equals(termini.getCodi())) {
						termini.removeIniciat(terminiIniciat);
						terminiNou.addIniciat(terminiIniciat);
						terminiIniciat.setTermini(terminiNou);
						break;
					}
				}
			}
		}
	}

	private void filtrarExpedientsAmbTasques(
			List<Long> llistaExpedientIds,
			boolean nomesMeves,
			boolean nomesAlertes,
			boolean mostrarAnulats,
			boolean nomesTasquesPersonals,
			boolean nomesTasquesGrup) {
		Set<Long> ids1 = null;
		Set<Long> ids2 = null;
		Set<Long> ids3 = null;
		Set<Long> ids4 = null;
		Set<Long> ids5 = null;
		int index = 0;
		for (Long id: llistaExpedientIds) {
			if (index == 0)
				ids1 = new HashSet<Long>();
			if (index == 1000)
				ids2 = new HashSet<Long>();
			if (index == 2000)
				ids3 = new HashSet<Long>();
			if (index == 3000)
				ids4 = new HashSet<Long>();
			if (index == 4000)
				ids5 = new HashSet<Long>();
			if (index < 1000)
				ids1.add(id);
			else if (index < 2000)
				ids2.add(id);
			else if (index < 3000)
				ids3.add(id);
			else if (index < 4000)
				ids4.add(id);
			else
				ids5.add(id);
			index++;
		}
		List<Object[]> idsInstanciesProces = expedientRepository.findAmbIdsByFiltreConsultesTipus(
				ids1,
				ids2,
				ids3,
				ids4,
				ids5,
				mostrarAnulats,
				nomesAlertes);

		List<String> idsPI = new ArrayList<String>();
		List<Long> idsExp = new ArrayList<Long>();
		for (Object[] id : idsInstanciesProces) {
			idsExp.add((Long) id[0]);
			idsPI.add((String) id[1]);
		}
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		List<String> ids = workflowEngineApi.findRootProcessInstancesWithTasksCommand(
						auth.getName(),
						idsPI,
						nomesMeves,
						nomesTasquesPersonals,
						nomesTasquesGrup);
		Iterator<Long> itExps = llistaExpedientIds.iterator();
		ArrayList<Long> removeList = new ArrayList<Long>();
		while (itExps.hasNext()) {
			Long elem = itExps.next();
			int pos = idsExp.indexOf(elem);
			if (pos == -1 || !ids.contains(idsPI.get(pos))) {
				removeList.add(elem);
			}
		}
		llistaExpedientIds.removeAll(removeList);
	}

	@Override
	@Transactional(readOnly = true)
	public List<DadesNotificacioDto> findNotificacionsNotibPerExpedientId(Long expedientId) throws NoTrobatException {
		List<DadesNotificacioDto> notificaionsDto = new ArrayList<DadesNotificacioDto>();
		Expedient expedient = expedientRepository.findById(expedientId).orElse(null);
		List<DocumentNotificacio> notificacions = notificacioHelper.findNotificacionsNotibPerExpedientId(expedientId);
		for (DocumentNotificacio notificacio: notificacions) {
			DadesNotificacioDto notificaicoDto = notificacioHelper.toDadesNotificacioDto(notificacio, expedient.isArxiuActiu());
			notificaionsDto.add(notificaicoDto);
		}
		return notificaionsDto;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<String> findProcesInstanceIdsAmbEntornTipusAndProcessDefinitionName(
			Long entornId,
			Long expedientTipusId,
			String jbpmKey) {
		logger.debug("Consultant instancies de procés amb entorn, tipus i process definition name(" +
				"entornId = " + entornId +
			", expedientTipusId = " + expedientTipusId +
			", jbpmKey = " + jbpmKey + ")");
		List<String> processInstancesIds = new ArrayList<String>();
		for (WProcessInstance processInstance :
			workflowEngineApi.findProcessInstancesWithProcessDefinitionNameEntornAndTipus(
					jbpmKey,
					entornId,
					expedientTipusId))
			processInstancesIds.add(processInstance.getId().toString());
		return processInstancesIds;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public long countAmbDefinicioProcesId(
			Long definicioProcesId) {
		logger.debug("Compta el número de instàncies de processos amb process definition id(" +
			"definicioProcesId = " + definicioProcesId + ")");
		long count = 0;
		DefinicioProces definicioProces = definicioProcesRepository.findById(definicioProcesId).orElse(null);
		if (definicioProces != null) {
			count = workflowEngineApi.countProcessInstancesWithProcessDefinitionId(definicioProces.getJbpmId());
		}
		return count;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly = true)
	public byte[] getZipDocumentacio(Long expedientId) {
		return getZipDocumentacio(expedientId, null);
	}

    @Override
	@Transactional(readOnly = true)
    public byte[] getZipDocumentacio(Long expedientId, Set<Long> seleccio) {
		Expedient expedient = expedientRepository.findById(expedientId).orElse(null);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ZipOutputStream out = new ZipOutputStream(baos);
		ZipEntry ze;
		ArxiuDto arxiu;

		List<String> avisos = new ArrayList<String>();
		List<String> errors = new ArrayList<String>();

		try {
			// Consulta l'arbre de processos
			String procesPrincipalId = String.valueOf(expedient.getProcessInstanceId());
			List<InstanciaProcesDto> arbreProcessos = expedientHelper.getArbreInstanciesProces(procesPrincipalId);
			// Llistat de noms dins del zip per no repetir-los.
			Set<String> nomsArxius = new HashSet<String>();
			for (InstanciaProcesDto instanciaProces: arbreProcessos) {
				// Per cada instancia de proces consulta els documents.
				List<ExpedientDocumentDto> documentsInstancia =	documentHelper.findDocumentsPerInstanciaProces(instanciaProces.getId());
				// Per cada document de la instància
				for (ExpedientDocumentDto document : documentsInstancia) {
					if (seleccio == null || seleccio.contains(document.getId())) {
						// Consulta l'arxiu del document
						DocumentStore documentStore = documentStoreRepository.findById(document.getId()).orElse(null);
						if (documentStore == null) {
							errors.add("No s'ha pogut obtenir i adjuntar " + this.getZipDocumentErrorDesc(document, instanciaProces, procesPrincipalId) + ".");
							continue;
							//throw new NoTrobatException(DocumentStore.class, document.getId());
						}

						try {
							// Consulta el contingut.
							arxiu = documentHelper.getArxiuPerDocumentStoreId(
									document.getId(),
									false,
									false,
									null);
						} catch (SistemaExternException ex) {
							arxiu = null;
						}

						// En cas d'error al consultar l'arxiu, s'intenta cercar la versió original
						if(arxiu == null) {
							try {
								arxiu = jbpm3HeliumService.getArxiuVersioOriginal(expedientId, document.getId());
								avisos.add("No s'ha pogut obtenir la versió imprimible de " + this.getZipDocumentErrorDesc(document, instanciaProces, procesPrincipalId) + ", s'ha obtingut la versió original.");
							} catch (Exception ex) {
								errors.add("No s'ha pogut obtenir i adjuntar " + this.getZipDocumentErrorDesc(document, instanciaProces, procesPrincipalId) + ".");
								continue;
							}
						}

						// Crea l'entrada en el zip
						String recursNom = documentHelper.getZipRecursNom(
								instanciaProces,
								document,
								nomsArxius);
						if (recursNom!=null) {
							ze = new ZipEntry(recursNom);
							out.putNextEntry(ze);
							out.write(arxiu.getContingut());
							out.closeEntry();
						}
					}
				}
			}

			// Generam i afegim el fitxer d'avisos al zip
			StringBuilder avisosContent = new StringBuilder();
			if(!(avisos.isEmpty() && errors.isEmpty())) {
				avisosContent.append("S'han produït " + avisos.size() + " avisos i " + errors.size() + " errors en la descàrrega de documents.\n\n");
				if(!avisos.isEmpty()) {
					avisosContent.append("\nAvisos:\n");
					for(String avis : avisos) {
						avisosContent.append("- " + avis + "\n");
					}
				}

				if(!errors.isEmpty()) {
					avisosContent.append("\nErrors:\n");
					for(String error : errors) {
						avisosContent.append("- " + error + "\n");
					}
				}

				ze = new ZipEntry(errors.isEmpty()? "avisos.txt" : "errors.txt");
				out.putNextEntry(ze);
				out.write(avisosContent.toString().getBytes());
				out.closeEntry();
			}
			out.close();
		} catch (Exception e) {
			String errMsg = "Error construint el zip dels documents per l'expedient " + expedient.getIdentificador() + ": " + e.getMessage();
			logger.error(errMsg, e);
			throw new RuntimeException(errMsg, e);
		}
		return baos.toByteArray();
	}

    /** Mètode privat per completar la informació de l'error d'obtenció d'un document o adjunt segons si és del procés principal o no. */
	private String getZipDocumentErrorDesc(ExpedientDocumentDto document, InstanciaProcesDto instanciaProces,
			String procesPrincipalId) {

		StringBuilder documentDesc = new StringBuilder();
		// Documento o adjunt
		if (document.isAdjunt()) {
			documentDesc.append("l'adjunt \"").append(document.getAdjuntTitol()).append("\"");
		} else {
			documentDesc.append("el document \"").append(document.getDocumentNom()).append("\"");
		}
		// Subprocés
		if (!procesPrincipalId.equals(instanciaProces.getId())) {
			documentDesc.append(" del subprocés ").append(instanciaProces.getId() + " - " + instanciaProces.getTitol());
		}
		return documentDesc.toString();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly = true)
	public byte[] getZipPerNotificar(Long expedientId, List<ExpedientDocumentDto> documentsPerAfegir) {

		Expedient expedient = expedientRepository.findById(expedientId).orElse(null);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ZipOutputStream out = new ZipOutputStream(baos);
		ZipEntry ze;
		ArxiuDto arxiu;
		try {
			// Consulta l'arbre de processos
			List<InstanciaProcesDto> arbreProcessos =
					expedientHelper.getArbreInstanciesProces(String.valueOf(expedient.getProcessInstanceId()));
			// Llistat de noms dins del zip per no repetir-los.
			Set<String> nomsArxius = new HashSet<String>();
			for (InstanciaProcesDto instanciaProces: arbreProcessos) {
				// Per cada instancia de proces consulta els documents.
//				List<ExpedientDocumentDto> documentsInstancia =
//						documentHelper.findDocumentsPerInstanciaProces(
//							instanciaProces.getId());
				// Per cada document de la instància
				for (ExpedientDocumentDto document : documentsPerAfegir) {
					// Consulta l'arxiu del document
					DocumentStore documentStore = documentStoreRepository.findById(document.getId()).orElse(null);
					if (documentStore == null) {
						throw new NoTrobatException(
								DocumentStore.class,
								document.getId());
					}
					// Consulta el contingut.
					arxiu = documentHelper.getArxiuPerDocumentStoreId(
							document.getId(),
							false,
							false,
							null);
					// Crea l'entrada en el zip
					String recursNom = documentHelper.getZipRecursNom(
							instanciaProces,
							document,
							nomsArxius);
					if (recursNom!=null) {
						ze = new ZipEntry(recursNom);
						out.putNextEntry(ze);
						out.write(arxiu.getContingut());
						out.closeEntry();
					}
				}
			}
			out.close();
		} catch (Exception e) {
			String errMsg = "Error construint el zip dels documents per notificar " + expedient.getIdentificador() + ": " + e.getMessage();
			logger.error(errMsg, e);
			throw new RuntimeException(errMsg, e);
		}
		return baos.toByteArray();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	public void arreglarMetadadesNti(Long expedientId) {
		Expedient expedient = expedientRepository.findById(expedientId).orElse(null);
		if ( (expedient.isArxiuActiu() && expedient.isNtiActiu())
				&& (expedient.getNtiOrgano() == null
					|| expedient.getNtiClasificacion() == null
					|| expedient.getNtiSerieDocumental() == null)) {
			// Consulta la informació de l'expedient i actualitza l'expedient
			expedient.setNtiVersion(ExpedientHelper.VERSIO_NTI);
			es.caib.pluginsib.arxiu.api.Expedient expedientArxiu = pluginHelper.arxiuExpedientInfo(expedient.getArxiuUuid());
			expedient.setNtiIdentificador(
					expedientArxiu.getMetadades().getIdentificador());
			expedient.setNtiVersion(expedientArxiu.getMetadades().getVersioNti());
			expedient.setNtiClasificacion(expedientArxiu.getMetadades().getClassificacio());
			expedient.setNtiSerieDocumental(expedientArxiu.getMetadades().getSerieDocumental());
			if (expedientArxiu.getMetadades().getOrgans() != null
					&& expedientArxiu.getMetadades().getOrgans().size() > 0) {
				expedient.setNtiOrgano(expedient.getUnitatOrganitzativa()!=null ? expedient.getUnitatOrganitzativa().getCodi()
						: expedientArxiu.getMetadades().getOrgans().get(0));
			}
			// Per cada document actualitza la informació NTI del document
			// Consulta l'arbre de processos
			List<InstanciaProcesDto> arbreProcessos =
					expedientHelper.getArbreInstanciesProces(String.valueOf(expedient.getProcessInstanceId()));
			for (InstanciaProcesDto instanciaProces: arbreProcessos) {
				// Per cada instancia de proces consulta els documents.
				List<ExpedientDocumentDto> documentsInstancia =
						documentHelper.findDocumentsPerInstanciaProces(
							instanciaProces.getId());
				// Per cada document de la instància
				for (ExpedientDocumentDto documentDto : documentsInstancia) {
					DocumentStore documentStore = documentStoreRepository.findById(documentDto.getId()).orElse(null);
					Document document;
					if (!documentStore.isAdjunt() && documentStore.getCodiDocument() != null)
						document = documentHelper.findDocumentPerInstanciaProcesICodi(documentStore.getProcessInstanceId(), documentStore.getCodiDocument());
					else
						document = null;
					documentHelper.actualizarMetadadesNti(
							expedient,
							document,
							documentStore,
							null,
							null,
							null,
							null);
					// Recupera l'identificador de l'arxiu
					if (documentStore.getArxiuUuid() != null) {
						es.caib.pluginsib.arxiu.api.Document documentArxiuInfo = pluginHelper.arxiuDocumentInfo(
								documentStore.getArxiuUuid(),
								null,
								false,
								documentStore.isSignat());
						documentStore.setNtiIdentificador(documentArxiuInfo.getDocumentMetadades().getIdentificador());
					}
				}
			}
		}
	}

	/** Mètode per consultar la propietat de propagació d'esborrat d'expedients si s'esborra el tipus d'expedient.*/
	private boolean isPropagarEsbExp() {
		Parametre parametrePropagarEsbExp = parametreRepository.findByCodi(ParametreService.APP_CONFIGURACIO_PROPAGAR_ESBORRAR_EXPEDIENTS);
		if(parametrePropagarEsbExp==null)
			throw new NoTrobatException(Parametre.class, ParametreService.APP_CONFIGURACIO_PROPAGAR_ESBORRAR_EXPEDIENTS);
		return "true".equalsIgnoreCase(parametrePropagarEsbExp.getValor());
	}

	/** Funció que mira si l'expedient està integrat amb l'arxiu i utlitzarà
	//la llibreria d'utilitats de backoffice de Distribució per moure tots els annexos **/
	private void moureAnnexos(Expedient expedient) {
		ArxiuResultat resultat = null;
		List<Anotacio> anotacions = anotacioRepository.findByExpedientId(expedient.getId());
		if (expedient.isArxiuActiu()) {
			//Recorro les anotacions associades a l'expedient
			if(anotacions!=null) {
				for(Anotacio anotacio: anotacions) {

					// Utilitza la llibreria d'utilitats de Distribució per incorporar la informació de l'anotació directament a l'expedient dins l'Arxiu
					es.caib.pluginsib.arxiu.api.Expedient expedientArxiu = pluginHelper.arxiuExpedientInfo(expedient.getArxiuUuid());
					BackofficeArxiuUtils backofficeUtils = new BackofficeArxiuUtilsImpl(pluginHelper.getArxiuPlugin());
					// Posarà els annexos en la carpeta de l'anotació
					backofficeUtils.setCarpeta(ArxiuConversioHelper.revisarContingutNom(anotacio.getIdentificador().replace("/", "_")));
					// S'enregistraran els events al monitor d'integració
					backofficeUtils.setArxiuPluginListener(this);
					// Consulta la informació de l'anotació
					AnotacioRegistreEntrada anotacioRegistreEntrada = null;
					try {
						es.caib.distribucio.rest.client.integracio.domini.AnotacioRegistreId idWs = new AnotacioRegistreId();
						idWs.setClauAcces(anotacio.getDistribucioClauAcces());
						idWs.setIdentificador(anotacio.getDistribucioId());
						anotacioRegistreEntrada = distribucioHelper.consulta(idWs);

					} catch(Exception e) {
						// Error no controlat consultant la informació de l'expedient, es posa una alerta
						String errMsg = "Error consultant la informació de l'anotació " +
								anotacio.getIdentificador() + " a l'hora d'incorporar la anotació a l'expedient, és necessari reintentar el processament dels annexos.";
						logger.error(errMsg, e);
						Alerta alerta = alertaHelper.crearAlerta(
								expedient.getEntorn(),
								expedient,
								new Date(),
								null,
								errMsg);
						alerta.setPrioritat(AlertaPrioritat.ALTA);
						resultat = new ArxiuResultat();
						for (AnotacioAnnex annex : anotacio.getAnnexos()) {
							annex.setEstat(AnotacioAnnexEstatEnumDto.PENDENT);
							annex.setError(errMsg);
						}
					}
					// Processa la informació amb la llibreria d'utilitats per moure els annexos
					if (anotacioRegistreEntrada != null) {
						resultat = backofficeUtils.crearExpedientAmbAnotacioRegistre(expedientArxiu, anotacioRegistreEntrada);
						if (resultat.getErrorCodi() != 0) {
							// Error en el processament
							String errMsg = "S'han produit errors processant l'anotació de Distribucio \"" + anotacio.getIdentificador() + "\" amb la llibreria de distribucio-backoffice-utils: " + resultat.getErrorCodi() + " " + resultat.getErrorMessage();
							logger.error(errMsg, resultat.getException());
							Alerta alerta = alertaHelper.crearAlerta(
									expedient.getEntorn(),
									expedient,
									new Date(),
									null,
									errMsg + ". Es necessari reintentar el processament.");
							alerta.setPrioritat(AlertaPrioritat.ALTA);
						}
					}
				}
			}
		} else {
			resultat = new ArxiuResultat();
		}

		// Si no s'integra amb Sistra2
		if(anotacions!=null) {
			for(Anotacio anotacio: anotacions) {
				for ( AnotacioAnnex annex : anotacio.getAnnexos() ) {
					// Incorpora cada annex de forma separada per evitar excepcions i continuar amb els altres
					// Si no s'integra amb Sistra crea un document per annex incorportat correctament
					distribucioHelper.incorporarAnnex(
							expedient.getTipus().isDistribucioSistra(),
							expedient,
							anotacio,
							annex,
							resultat);
				}
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	public EstatDto estatCanviar(Long expedientId, Long estatId, boolean retrocedir) {

		logger.debug("Canviant l'estat a l'expedient (" +
				"estatId=" + estatId + ")");

		Expedient expedient = expedientHelper.getExpedientComprovantPermisos(
				expedientId,
				true,
				false,
				false,
				false);

		Estat estat = expedientHelper.estatCanviar(expedient, estatId, retrocedir);

		expedientHelper.verificarFinalitzacioExpedient(expedient);
		expedientDadaHelper.setExpedientDades(expedient);

		return conversioTipusHelper.convertir(estat, EstatDto.class);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	public void firmarDocumentServidorPerArxiuFiExpedient(Long documentStoreId) {
		expedientHelper.firmarDocumentServidorPerArxiuFiExpedient(documentStoreId);
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

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly = true)
	public Long countByTipus(Long expedientTipusId) {
		return expedientHelper.countByEntornIdAndTipus(expedientTipusId);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly = true)
	public List<Long> findIdsPerTipus(Long expedientTipusId) {
		return expedientHelper.findIdsPerTipus(expedientTipusId);
	}

	@Transactional
	private void updateTasquesExpedient(String processInstanceId, String expedientNumero, String tipusExpedientNom) {
		List<WTaskInstance> tasks = workflowEngineApi.findTaskInstancesByProcessInstanceId(processInstanceId);
		for(WTaskInstance task : tasks) {
			try {
				tascaHelper.refreshExpedientTasca(task.getId());
				comandaHelper.upsertTasca(
					task.getId(),
					task.getName(),
					expedientNumero,
					tipusExpedientNom,
					task,
					TascaEstat.PENDENT
				);
			} catch (Exception e) {
				logger.error("Error processant tasques d'expedient iniciat", e);
			}
		}
	}

	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	public void handleExpedientSaveListener(ExpedientDto expedient) {
		try {
			if(expedient.getTipus().getTipus() == ExpedientTipusTipusEnumDto.FLOW
				&& expedient.getProcessInstanceId() != null) {
				updateTasquesExpedient(expedient.getProcessInstanceId(), expedient.getNumeroDefault(), expedient.getTipus().getNom());
			}
		} catch (Exception e) {
			logger.error("Error capturant event AFTER_COMMIT d'expedient", e);
		}
	}

	private static void addCurrentlyMigrating(Long expedientId) {
		synchronized(currentlyMigratingExpedients) {
			if(!currentlyMigratingExpedients.contains(expedientId))
				currentlyMigratingExpedients.add(expedientId);
		}
	}

	private static void deleteCurrentlyMigrating(Long expedientId) {
		synchronized(currentlyMigratingExpedients) {
			currentlyMigratingExpedients.remove(expedientId);
		}
	}

	private static boolean isCurrentlyMigrating(Long expedientId) {
		synchronized(currentlyMigratingExpedients) {
			return currentlyMigratingExpedients.contains(expedientId);
		}
	}


	private static final Logger logger = LoggerFactory.getLogger(ExpedientServiceImpl.class);
}
