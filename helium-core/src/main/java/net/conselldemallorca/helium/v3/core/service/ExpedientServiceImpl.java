/**
 * 
 */
package net.conselldemallorca.helium.v3.core.service;

import java.text.SimpleDateFormat;
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
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.UUID;

import javax.annotation.Resource;

import net.conselldemallorca.helium.core.model.dao.PluginCustodiaDao;
import net.conselldemallorca.helium.core.model.dao.PluginGestioDocumentalDao;
import net.conselldemallorca.helium.core.model.dao.RegistreDao;
import net.conselldemallorca.helium.core.model.dto.ExpedientIniciantDto;
import net.conselldemallorca.helium.core.model.exception.ExpedientRepetitException;
import net.conselldemallorca.helium.core.model.hibernate.Accio;
import net.conselldemallorca.helium.core.model.hibernate.Alerta;
import net.conselldemallorca.helium.core.model.hibernate.Camp;
import net.conselldemallorca.helium.core.model.hibernate.Camp.TipusCamp;
import net.conselldemallorca.helium.core.model.hibernate.Consulta;
import net.conselldemallorca.helium.core.model.hibernate.ConsultaCamp.TipusConsultaCamp;
import net.conselldemallorca.helium.core.model.hibernate.DefinicioProces;
import net.conselldemallorca.helium.core.model.hibernate.Document;
import net.conselldemallorca.helium.core.model.hibernate.DocumentStore;
import net.conselldemallorca.helium.core.model.hibernate.DocumentStore.DocumentFont;
import net.conselldemallorca.helium.core.model.hibernate.Entorn;
import net.conselldemallorca.helium.core.model.hibernate.Estat;
import net.conselldemallorca.helium.core.model.hibernate.ExecucioMassivaExpedient;
import net.conselldemallorca.helium.core.model.hibernate.Expedient;
import net.conselldemallorca.helium.core.model.hibernate.Expedient.IniciadorTipus;
import net.conselldemallorca.helium.core.model.hibernate.ExpedientLog;
import net.conselldemallorca.helium.core.model.hibernate.ExpedientLog.ExpedientLogAccioTipus;
import net.conselldemallorca.helium.core.model.hibernate.ExpedientLog.ExpedientLogEstat;
import net.conselldemallorca.helium.core.model.hibernate.ExpedientLog.LogInfo;
import net.conselldemallorca.helium.core.model.hibernate.ExpedientTipus;
import net.conselldemallorca.helium.core.model.hibernate.Portasignatures;
import net.conselldemallorca.helium.core.model.hibernate.Portasignatures.TipusEstat;
import net.conselldemallorca.helium.core.model.hibernate.Portasignatures.Transicio;
import net.conselldemallorca.helium.core.model.hibernate.Registre;
import net.conselldemallorca.helium.core.model.hibernate.Termini;
import net.conselldemallorca.helium.core.model.hibernate.TerminiIniciat;
import net.conselldemallorca.helium.core.model.service.DocumentHelper;
import net.conselldemallorca.helium.core.model.service.LuceneHelper;
import net.conselldemallorca.helium.core.model.service.MesuresTemporalsHelper;
import net.conselldemallorca.helium.core.security.ExtendedPermission;
import net.conselldemallorca.helium.jbpm3.integracio.ExecucioHandlerException;
import net.conselldemallorca.helium.jbpm3.integracio.JbpmHelper;
import net.conselldemallorca.helium.jbpm3.integracio.JbpmProcessDefinition;
import net.conselldemallorca.helium.jbpm3.integracio.JbpmProcessInstance;
import net.conselldemallorca.helium.jbpm3.integracio.JbpmTask;
import net.conselldemallorca.helium.jbpm3.integracio.JbpmToken;
import net.conselldemallorca.helium.jbpm3.integracio.ResultatConsultaPaginadaJbpm;
import net.conselldemallorca.helium.v3.core.api.dto.AccioDto;
import net.conselldemallorca.helium.v3.core.api.dto.AlertaDto;
import net.conselldemallorca.helium.v3.core.api.dto.ArxiuDto;
import net.conselldemallorca.helium.v3.core.api.dto.CampAgrupacioDto;
import net.conselldemallorca.helium.v3.core.api.dto.CampDto;
import net.conselldemallorca.helium.v3.core.api.dto.DadaIndexadaDto;
import net.conselldemallorca.helium.v3.core.api.dto.DadesDocumentDto;
import net.conselldemallorca.helium.v3.core.api.dto.DefinicioProcesDto;
import net.conselldemallorca.helium.v3.core.api.dto.DocumentDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientCamps;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientConsultaDissenyDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientDadaDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientDocumentDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientDto.EstatTipusDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientDto.IniciadorTipusDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientErrorDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientErrorDto.ErrorTipusDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientLogDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientTascaDto;
import net.conselldemallorca.helium.v3.core.api.dto.InstanciaProcesDto;
import net.conselldemallorca.helium.v3.core.api.dto.MostrarAnulatsDto;
import net.conselldemallorca.helium.v3.core.api.dto.PaginaDto;
import net.conselldemallorca.helium.v3.core.api.dto.PaginacioParamsDto;
import net.conselldemallorca.helium.v3.core.api.dto.PaginacioParamsDto.OrdreDireccioDto;
import net.conselldemallorca.helium.v3.core.api.dto.PaginacioParamsDto.OrdreDto;
import net.conselldemallorca.helium.v3.core.api.dto.PermisTipusEnumDto;
import net.conselldemallorca.helium.v3.core.api.dto.PersonaDto;
import net.conselldemallorca.helium.v3.core.api.dto.PortasignaturesDto;
import net.conselldemallorca.helium.v3.core.api.dto.RespostaValidacioSignaturaDto;
import net.conselldemallorca.helium.v3.core.api.dto.TascaDadaDto;
import net.conselldemallorca.helium.v3.core.api.exception.DocumentConvertirException;
import net.conselldemallorca.helium.v3.core.api.exception.DocumentGenerarException;
import net.conselldemallorca.helium.v3.core.api.exception.EntornNotFoundException;
import net.conselldemallorca.helium.v3.core.api.exception.EstatNotFoundException;
import net.conselldemallorca.helium.v3.core.api.exception.ExpedientNotFoundException;
import net.conselldemallorca.helium.v3.core.api.exception.ExpedientTipusNotFoundException;
import net.conselldemallorca.helium.v3.core.api.exception.NotAllowedException;
import net.conselldemallorca.helium.v3.core.api.exception.NotFoundException;
import net.conselldemallorca.helium.v3.core.api.service.ExpedientService;
import net.conselldemallorca.helium.v3.core.api.service.TascaService;
import net.conselldemallorca.helium.v3.core.helper.ConsultaHelper;
import net.conselldemallorca.helium.v3.core.helper.ConversioTipusHelper;
import net.conselldemallorca.helium.v3.core.helper.DocumentHelperV3;
import net.conselldemallorca.helium.v3.core.helper.EntornHelper;
import net.conselldemallorca.helium.v3.core.helper.ExpedientHelper;
import net.conselldemallorca.helium.v3.core.helper.ExpedientLoggerHelper;
import net.conselldemallorca.helium.v3.core.helper.ExpedientTipusHelper;
import net.conselldemallorca.helium.v3.core.helper.IndexHelper;
import net.conselldemallorca.helium.v3.core.helper.MessageHelper;
import net.conselldemallorca.helium.v3.core.helper.PaginacioHelper;
import net.conselldemallorca.helium.v3.core.helper.PermisosHelper;
import net.conselldemallorca.helium.v3.core.helper.PermisosHelper.ObjectIdentifierExtractor;
import net.conselldemallorca.helium.v3.core.helper.PluginHelper;
import net.conselldemallorca.helium.v3.core.helper.TascaHelper;
import net.conselldemallorca.helium.v3.core.helper.VariableHelper;
import net.conselldemallorca.helium.v3.core.repository.AccioRepository;
import net.conselldemallorca.helium.v3.core.repository.AlertaRepository;
import net.conselldemallorca.helium.v3.core.repository.CampRepository;
import net.conselldemallorca.helium.v3.core.repository.ConsultaRepository;
import net.conselldemallorca.helium.v3.core.repository.DefinicioProcesRepository;
import net.conselldemallorca.helium.v3.core.repository.DocumentRepository;
import net.conselldemallorca.helium.v3.core.repository.DocumentStoreRepository;
import net.conselldemallorca.helium.v3.core.repository.EnumeracioRepository;
import net.conselldemallorca.helium.v3.core.repository.EstatRepository;
import net.conselldemallorca.helium.v3.core.repository.ExecucioMassivaExpedientRepository;
import net.conselldemallorca.helium.v3.core.repository.ExpedientHeliumRepository;
import net.conselldemallorca.helium.v3.core.repository.ExpedientLoggerRepository;
import net.conselldemallorca.helium.v3.core.repository.ExpedientRepository;
import net.conselldemallorca.helium.v3.core.repository.ExpedientTipusRepository;
import net.conselldemallorca.helium.v3.core.repository.PortasignaturesRepository;
import net.conselldemallorca.helium.v3.core.repository.RegistreRepository;
import net.conselldemallorca.helium.v3.core.repository.TerminiIniciatRepository;
import net.conselldemallorca.helium.v3.core.repository.TerminiRepository;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.security.acls.model.Permission;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.codahale.metrics.Counter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;


/**
 * Servei per a gestionar expedients.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Service("expedientServiceV3")
public class ExpedientServiceImpl implements ExpedientService {

	@Resource
	private DocumentRepository documentRepository;
	@Resource
	private ExpedientRepository expedientRepository;
	@Resource
	private ExpedientHeliumRepository expedientHeliumRepository;
	@Resource
	private RegistreDao registreDao;
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
	private ConsultaHelper consultaHelper;
	@Resource
	private DefinicioProcesRepository definicioProcesRepository;
	@Resource
	private DocumentStoreRepository documentStoreRepository;
	@Resource
	private AccioRepository accioRepository;
	@Resource
	private ExpedientHelper expedientHelper;
	@Resource
	private MessageHelper messageHelper;
	@Resource
	private EntornHelper entornHelper;
	@Resource
	private ExpedientTipusHelper expedientTipusHelper;
	@Resource
	private JbpmHelper jbpmHelper;
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
	@Resource
	private IndexHelper indexHelper;
	@Resource
	private LuceneHelper luceneHelper;
	@Resource(name="permisosHelperV3")
	private PermisosHelper permisosHelper;
	@Resource
	private PaginacioHelper paginacioHelper;
	@Resource
	private MesuresTemporalsHelper mesuresTemporalsHelper;
	@Resource
	private ExpedientLoggerHelper expedientLoggerHelper;
	@Resource
	private PluginCustodiaDao pluginCustodiaDao;
	@Resource
	private PluginGestioDocumentalDao pluginGestioDocumentalDao;
	@Resource
	private ExecucioMassivaExpedientRepository execucioMassivaExpedientRepository;
	@Resource
	private PortasignaturesRepository portasignaturesRepository;
	private String textBloqueigIniciExpedient;

	@Autowired
	private MetricRegistry metricRegistry;



	@Override
	@Transactional
	public synchronized ExpedientDto create(
			Long entornId,
			String usuari,
			Long expedientTipusId,
			Long definicioProcesId,
			Integer any,
			String numero,
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
			Map<String, DadesDocumentDto> documents,
			List<DadesDocumentDto> adjunts) {
		logger.debug("Creant nou expedient (" +
				"entornId=" + entornId + ", " +
				"usuari=" + usuari + ", " +
				"expedientTipusId=" + expedientTipusId + ", " +
				"definicioProcesId=" + definicioProcesId + ", " +
				"any=" + any + ", " +
				"numero=" + numero + ", " +
				"titol=" + titol + ")");
		Entorn entorn = entornHelper.getEntornComprovantPermisos(
				entornId,
				true,
				false,
				false);
		if (usuari != null)
			comprovarUsuari(usuari);
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String usuariBo = (usuari != null) ? usuari : auth.getName();
		ExpedientTipus expedientTipus = expedientTipusRepository.findById(expedientTipusId);
		
		mesuresTemporalsHelper.mesuraIniciar("Iniciar", "expedient", expedientTipus.getNom());
		mesuresTemporalsHelper.mesuraIniciar("Iniciar", "expedient", expedientTipus.getNom(), null, "Nou expedient");
		
		// Obté la llista de tipus d'expedient permesos
		List<ExpedientTipus> tipusPermesos = expedientTipusRepository.findByEntorn(entorn);
		permisosHelper.filterGrantedAny(
				tipusPermesos,
				new ObjectIdentifierExtractor<ExpedientTipus>() {
					public Long getObjectIdentifier(ExpedientTipus expedientTipus) {
						return expedientTipus.getId();
					}
				},
				ExpedientTipus.class,
				new Permission[] {
					ExtendedPermission.CREATE,
					ExtendedPermission.ADMINISTRATION},
				auth);
		
		textBloqueigIniciExpedient = auth.getName() + " (" +
				"entornCodi=" + entornId + ", " +
				"expedientTipusCodi=" + expedientTipus.getCodi() + ", " +
				"data=" + new Date() + ")";
		try {
			String iniciadorCodiCalculat = (iniciadorTipus.equals(IniciadorTipusDto.INTERN)) ? usuariBo : iniciadorCodi;
			Expedient expedient = new Expedient();
			expedient.setTipus(expedientTipus);
			expedient.setIniciadorTipus(conversioTipusHelper.convertir(iniciadorTipus, IniciadorTipus.class));
			expedient.setIniciadorCodi(iniciadorCodiCalculat);
			expedient.setEntorn(entorn);
			expedient.setProcessInstanceId(UUID.randomUUID().toString());
			String responsableCodiCalculat = (responsableCodi != null) ? responsableCodi : expedientTipus.getResponsableDefecteCodi();
			if (responsableCodiCalculat == null)
				responsableCodiCalculat = iniciadorCodiCalculat;
			expedient.setResponsableCodi(responsableCodiCalculat);
			expedient.setRegistreNumero(registreNumero);
			expedient.setRegistreData(registreData);
			expedient.setUnitatAdministrativa(unitatAdministrativa);
			expedient.setIdioma(idioma);
			expedient.setAutenticat(autenticat);
			expedient.setTramitadorNif(tramitadorNif);
			expedient.setTramitadorNom(tramitadorNom);
			expedient.setInteressatNif(interessatNif);
			expedient.setInteressatNom(interessatNom);
			expedient.setRepresentantNif(representantNif);
			expedient.setRepresentantNom(representantNom);
			expedient.setAvisosHabilitats(avisosHabilitats);
			expedient.setAvisosEmail(avisosEmail);
			expedient.setAvisosMobil(avisosMobil);
			expedient.setNotificacioTelematicaHabilitada(notificacioTelematicaHabilitada);
			
			mesuresTemporalsHelper.mesuraCalcular("Iniciar", "expedient", expedientTipus.getNom(), null, "Omplir dades");
			mesuresTemporalsHelper.mesuraIniciar("Iniciar", "expedient", expedientTipus.getNom(), null, "Assignar numeros");

			expedient.setNumeroDefault(
					getNumeroExpedientDefaultActual(
							entorn,
							expedientTipus,
							any));
//			MesurarTemps.diferenciaImprimirStdoutIReiniciar(mesuraTempsIncrementalPrefix, "2");
			if (expedientTipus.getTeNumero()) {
				if (numero != null && numero.length() > 0 && expedientTipus.getDemanaNumero()) {
					expedient.setNumero(numero);
				} else {
					expedient.setNumero(
							getNumeroExpedientActual(
									entornId,
									expedientTipusId,
									any));
				}
			}
	
			mesuresTemporalsHelper.mesuraCalcular("Iniciar", "expedient", expedientTipus.getNom(), null, "Assignar numeros");
			mesuresTemporalsHelper.mesuraIniciar("Iniciar", "expedient", expedientTipus.getNom(), null, "Verificar numero repetit");

			// Verifica si l'expedient té el número repetit
			if (expedient.getNumero() != null && (expedientRepository.findByEntornIdAndTipusIdAndNumero(
					entorn.getId(),
					expedientTipus.getId(),
					expedient.getNumero()) != null)) {
				throw new ExpedientRepetitException(
						messageHelper.getMessage(
								"error.expedientService.jaExisteix",
								new Object[]{expedient.getNumero()}) );
			}
			
			mesuresTemporalsHelper.mesuraCalcular("Iniciar", "expedient", expedientTipus.getNom(), null, "Verificar numero repetit");
			mesuresTemporalsHelper.mesuraIniciar("Iniciar", "expedient", expedientTipus.getNom(), null, "Actualitzar any i sequencia");
			
			// Actualitza l'any actual de l'expedient
			int anyActual = Calendar.getInstance().get(Calendar.YEAR);
			if (any == null || any.intValue() == anyActual) {
				if (expedientTipus.getAnyActual() == 0) {
					expedientTipus.setAnyActual(anyActual);
				} else if (expedientTipus.getAnyActual() < anyActual) {
					expedientTipus.setAnyActual(anyActual);
				}
			}
			// Actualitza la seqüència del número d'expedient
			if (expedientTipus.getTeNumero() && expedientTipus.getExpressioNumero() != null && !"".equals(expedientTipus.getExpressioNumero())) {
				if (expedient.getNumero().equals(
						getNumeroExpedientActual(
								entornId,
								expedientTipusId,
								any)))
					expedientTipus.updateSequencia(any, 1);
			}
			// Actualitza la seqüència del número d'expedient per defecte
			if (expedient.getNumeroDefault().equals(
					getNumeroExpedientDefaultActual(
							entorn,
							expedientTipus,
							any)))
				expedientTipus.updateSequenciaDefault(any, 1);
			// Configura el títol de l'expedient
			if (expedientTipus.getTeTitol()) {
				if (titol != null && titol.length() > 0)
					expedient.setTitol(titol);
				else
					expedient.setTitol("[Sense títol]");
			}

			mesuresTemporalsHelper.mesuraCalcular("Iniciar", "expedient", expedientTipus.getNom(), null, "Actualitzar any i sequencia");
			mesuresTemporalsHelper.mesuraIniciar("Iniciar", "expedient", expedientTipus.getNom(), null, "Iniciar instancia de proces");
			
			// Inicia l'instància de procés jBPM
			ExpedientIniciantDto.setExpedient(expedient);
			DefinicioProces definicioProces = null;
			if (definicioProcesId != null) {
				definicioProces = definicioProcesRepository.findById(definicioProcesId);
			} else {
				definicioProces = definicioProcesRepository.findDarreraVersioAmbEntornIJbpmKey(
						entornId,
						expedientTipus.getJbpmProcessDefinitionKey());
			}
			//MesurarTemps.diferenciaImprimirStdoutIReiniciar(mesuraTempsIncrementalPrefix, "7");
			JbpmProcessInstance processInstance = jbpmHelper.startProcessInstanceById(
					usuariBo,
					definicioProces.getJbpmId(),
					variables);
			expedient.setProcessInstanceId(processInstance.getId());
			
			mesuresTemporalsHelper.mesuraCalcular("Iniciar", "expedient", expedientTipus.getNom(), null, "Iniciar instancia de proces");
			mesuresTemporalsHelper.mesuraIniciar("Iniciar", "expedient", expedientTipus.getNom(), null, "Desar el nou expedient");
			
			// Emmagatzema el nou expedient
			expedientRepository.save(expedient);

			mesuresTemporalsHelper.mesuraCalcular("Iniciar", "expedient", expedientTipus.getNom(), null, "Desar el nou expedient");
			mesuresTemporalsHelper.mesuraIniciar("Iniciar", "expedient", expedientTipus.getNom(), null, "Afegir documents");
			
			// Afegim els documents
			if (documents != null){
				for (Map.Entry<String, DadesDocumentDto> doc: documents.entrySet()) {
					if (doc.getValue() != null) {
						documentHelper.actualitzarDocument(
								null,
								expedient.getProcessInstanceId(),
								doc.getValue().getCodi(), 
								null,
								doc.getValue().getData(), 
								doc.getValue().getArxiuNom(), 
								doc.getValue().getArxiuContingut(),
								false);
					}
				}
			}
			// Afegim els adjunts
			if (adjunts != null) {
				for (DadesDocumentDto adjunt: adjunts) {
					String documentCodi = new Long(new Date().getTime()).toString();
					documentHelper.actualitzarDocument(
							null,
							expedient.getProcessInstanceId(),
							documentCodi,
							adjunt.getTitol(),
							adjunt.getData(), 
							adjunt.getArxiuNom(), 
							adjunt.getArxiuContingut(),
							true);
				}
			}

			mesuresTemporalsHelper.mesuraCalcular("Iniciar", "expedient", expedientTipus.getNom(), null, "Afegir documents");
			mesuresTemporalsHelper.mesuraIniciar("Iniciar", "expedient", expedientTipus.getNom(), null, "Afegir log");
			
			// Verificar la ultima vegada que l'expedient va modificar el seu estat
			ExpedientLog log = expedientLoggerHelper.afegirLogExpedientPerProces(
					processInstance.getId(),
					ExpedientLogAccioTipus.EXPEDIENT_INICIAR,
					null);
			log.setEstat(ExpedientLogEstat.IGNORAR);
			
			mesuresTemporalsHelper.mesuraCalcular("Iniciar", "expedient", expedientTipus.getNom(), null, "Afegir log");
			mesuresTemporalsHelper.mesuraIniciar("Iniciar", "expedient", expedientTipus.getNom(), null, "Iniciar flux");
			
			// Actualitza les variables del procés
			jbpmHelper.signalProcessInstance(expedient.getProcessInstanceId(), transitionName);

			mesuresTemporalsHelper.mesuraCalcular("Iniciar", "expedient", expedientTipus.getNom(), null, "Iniciar flux");
			mesuresTemporalsHelper.mesuraIniciar("Iniciar", "expedient", expedientTipus.getNom(), null, "Indexar expedient");
			
			// Indexam l'expedient
			logger.debug("Indexant nou expedient (id=" + expedient.getProcessInstanceId() + ")");
			indexHelper.expedientIndexLuceneCreate(expedient.getProcessInstanceId());
			
			mesuresTemporalsHelper.mesuraCalcular("Iniciar", "expedient", expedientTipus.getNom(), null, "Indexar expedient");
			mesuresTemporalsHelper.mesuraIniciar("Iniciar", "expedient", expedientTipus.getNom(), null, "Crear registre i convertir expedient");
			
			// Registra l'inici de l'expedient
			crearRegistreExpedient(
					expedient.getId(),
					usuariBo,
					Registre.Accio.INICIAR);
			// Retorna la informació de l'expedient que s'ha iniciat
			ExpedientDto dto = conversioTipusHelper.convertir(
					expedient,
					ExpedientDto.class);

			mesuresTemporalsHelper.mesuraCalcular("Iniciar", "expedient", expedientTipus.getNom(), null, "Crear registre i convertir expedient");
			mesuresTemporalsHelper.mesuraCalcular("Iniciar", "expedient", expedientTipus.getNom());
			
			logger.debug("textBloqueigIniciExpedient: " + textBloqueigIniciExpedient);
			return dto;
		} finally {
			textBloqueigIniciExpedient = null;
		}
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

	private Registre crearRegistreTasca(
			Long expedientId,
			String tascaId,
			String responsableCodi,
			Registre.Accio accio) {
		Registre registre = new Registre(
				new Date(),
				expedientId,
				responsableCodi,
				accio,
				Registre.Entitat.TASCA,
				tascaId);
		return registreRepository.save(registre);
	}
	private Registre crearRegistreInstanciaProces(
			Long expedientId,
			String processInstanceId,
			String responsableCodi,
			Registre.Accio accio) {
		Registre registre = new Registre(
				new Date(),
				expedientId,
				responsableCodi,
				accio,
				Registre.Entitat.INSTANCIA_PROCES,
				processInstanceId);
		return registreRepository.save(registre);
	}

	@Override
	@Transactional
	public ExpedientDadaDto getDadaPerInstanciaProces(String processInstanceId, String variableCodi, boolean incloureVariablesBuides) {
		return variableHelper.getDadaPerInstanciaProces(processInstanceId, variableCodi, incloureVariablesBuides);
	}

	@Override
	@Transactional(readOnly = true)
	public TascaDadaDto getTascaDadaDtoFromExpedientDadaDto(ExpedientDadaDto dadaPerInstanciaProces) {
		return variableHelper.getTascaDadaDtoFromExpedientDadaDto(dadaPerInstanciaProces);
	}

	@Override
	@Transactional
	public List<ExpedientDadaDto> findDadesPerInstanciaProces(String procesId) {
		return variableHelper.findDadesPerInstanciaProces(procesId);
	}

	@Override
	@Transactional
	public void update(
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
		if (!execucioDinsHandler) {
			ExpedientLog elog = expedientLoggerHelper.afegirLogExpedientPerExpedient(
				id,
				ExpedientLogAccioTipus.EXPEDIENT_MODIFICAR,
				null);
			elog.setEstat(ExpedientLogEstat.IGNORAR);
		}
		// Numero
		if (expedient.getTipus().getTeNumero()) {
			if (!StringUtils.equals(expedient.getNumero(), numero)) {
				expedientLoggerHelper.afegirProcessLogInfoExpedient(
						expedient.getProcessInstanceId(), 
						LogInfo.NUMERO + "#@#" + expedient.getNumero());
				expedient.setNumero(numero);
			}
		}
		// Titol
		if (expedient.getTipus().getTeTitol()) {
			if (!StringUtils.equals(expedient.getTitol(), titol)) {
				expedientLoggerHelper.afegirProcessLogInfoExpedient(
						expedient.getProcessInstanceId(), 
						LogInfo.TITOL + "#@#" + expedient.getTitol());
				expedient.setTitol(titol);
			}
		}
		// Responsable
		if (!StringUtils.equals(expedient.getResponsableCodi(), responsableCodi)) {
			expedientLoggerHelper.afegirProcessLogInfoExpedient(
					expedient.getProcessInstanceId(), 
					LogInfo.RESPONSABLE + "#@#" + expedient.getResponsableCodi());
			expedient.setResponsableCodi(responsableCodi);
		}
		// Data d'inici
		if (expedient.getDataInici() != dataInici) {
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
			String inici = sdf.format(dataInici);
			expedientLoggerHelper.afegirProcessLogInfoExpedient(
					expedient.getProcessInstanceId(), 
					LogInfo.INICI + "#@#" + inici);
			expedient.setDataInici(dataInici);
		}
		// Comentari
		if (!StringUtils.equals(expedient.getComentari(), comentari)) {
			expedientLoggerHelper.afegirProcessLogInfoExpedient(
					expedient.getProcessInstanceId(), 
					LogInfo.COMENTARI + "#@#" + expedient.getComentari());
			expedient.setComentari(comentari);
		}
		// Estat
		if (estatId != null) {
			if (expedient.getEstat() == null || !expedient.getEstat().getId().equals(estatId)) {
				expedientLoggerHelper.afegirProcessLogInfoExpedient(
						expedient.getProcessInstanceId(), 
						LogInfo.ESTAT + "#@#" + "---");
				Estat estat = estatRepository.findByExpedientTipusAndId(
						expedient.getTipus(),
						estatId);
				if (estat == null)
					throw new EstatNotFoundException();
				expedient.setEstat(estat);
			}
		} else if (expedient.getEstat() != null) {
			expedientLoggerHelper.afegirProcessLogInfoExpedient(
					expedient.getProcessInstanceId(), 
					LogInfo.ESTAT + "#@#" + expedient.getEstat().getId());
			expedient.setEstat(null);
		}
		// Geoposició
		if (expedient.getGeoPosX() != geoPosX) {
			expedientLoggerHelper.afegirProcessLogInfoExpedient(
					expedient.getProcessInstanceId(), 
					LogInfo.GEOPOSICIOX + "#@#" + expedient.getGeoPosX());
			expedient.setGeoPosX(geoPosX);
		}
		if (expedient.getGeoPosY() != geoPosY) {
			expedientLoggerHelper.afegirProcessLogInfoExpedient(
					expedient.getProcessInstanceId(), 
					LogInfo.GEOPOSICIOY + "#@#" + expedient.getGeoPosY());
			expedient.setGeoPosY(geoPosY);
		}
		// Georeferencia
		if (!StringUtils.equals(expedient.getGeoReferencia(), geoReferencia)) {
			expedientLoggerHelper.afegirProcessLogInfoExpedient(
					expedient.getProcessInstanceId(), 
					LogInfo.GEOREFERENCIA + "#@#" + expedient.getGeoReferencia());
			expedient.setGeoReferencia(geoReferencia);
		}
		// Grup
		if (!StringUtils.equals(expedient.getGrupCodi(), grupCodi)) {
			expedientLoggerHelper.afegirProcessLogInfoExpedient(
					expedient.getProcessInstanceId(), 
					LogInfo.GRUP + "#@#" + expedient.getGrupCodi());
			expedient.setGrupCodi(grupCodi);
		}
		luceneHelper.updateExpedientCapsalera(
				expedient,
				expedientHelper.isFinalitzat(expedient));
	}

	@Transactional(readOnly = true)
	@Override
	public boolean luceneReindexarExpedient(Long expedientId) {
		try {
			Expedient expedient = expedientHelper.getExpedientComprovantPermisos(
					expedientId,
					false,
					true,
					false,
					false);
			indexHelper.expedientIndexLuceneRecrear(expedient);
			return true;
		} catch (Exception ex) {
			return false;
		}
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<RespostaValidacioSignaturaDto> verificarSignatura(Long documentStoreId) {
		DocumentStore documentStore = documentStoreRepository.findById(documentStoreId);
		return documentHelper.getRespostasValidacioSignatura(documentStore);
	}

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
		List<JbpmProcessInstance> processInstancesTree = jbpmHelper.getProcessInstanceTree(expedient.getProcessInstanceId());
		for (JbpmProcessInstance pi: processInstancesTree){
			for (TerminiIniciat ti: terminiIniciatRepository.findByProcessInstanceId(pi.getId()))
				terminiIniciatRepository.delete(ti);
			
			jbpmHelper.deleteProcessInstance(pi.getId());
			
			for (DocumentStore documentStore: documentStoreRepository.findByProcessInstanceId(pi.getId())) {
				if (documentStore.isSignat()) {
					try {
						pluginCustodiaDao.esborrarSignatures(documentStore.getReferenciaCustodia());
					} catch (Exception ignored) {}
				}
				if (documentStore.getFont().equals(DocumentFont.ALFRESCO))
					pluginGestioDocumentalDao.deleteDocument(documentStore.getReferenciaFont());
				documentStoreRepository.delete(documentStore.getId());
			}
		}
		
		
		for (Portasignatures psigna: expedient.getPortasignatures()) {
			psigna.setEstat(TipusEstat.ESBORRAT);
		}
		for (ExecucioMassivaExpedient eme: execucioMassivaExpedientRepository.getExecucioMassivaByExpedient(id)) {
			execucioMassivaExpedientRepository.delete(eme);
		}
		expedientRepository.delete(expedient);
		luceneHelper.deleteExpedient(expedient);

		crearRegistreExpedient(
				expedient.getId(),
				SecurityContextHolder.getContext().getAuthentication().getName(),
				Registre.Accio.ESBORRAR);
	}

	@Override
	@Transactional
	public void crearModificarDocument(Long expedientId, String processInstanceId, Long documentStoreId, String nom, String nomArxiu, Long docId, byte[] arxiu, Date data) throws Exception {
		Expedient expedient = expedientHelper.getExpedientComprovantPermisos(
				expedientId,
				false,
				false,
				false,
				false,
				false,
				false,
				true);
		boolean creat = false;
		String arxiuNomAntic = null;
		boolean adjunt = false;
		DocumentStore documentStore = null;
		if (documentStoreId == null) {
			creat = true;
			adjunt = true;
		} else {
			documentStore = documentStoreRepository.findById(documentStoreId);
			arxiuNomAntic = documentStore.getArxiuNom();
			adjunt = documentStore.isAdjunt();
		}
		DocumentDto document = null;
		String codi = null;
		
		if (docId != null) {
			document = findDocumentsPerId(docId);
			if (document == null) {
				document = documentHelper.getDocumentSenseContingut(documentStoreId);
			}		
			if (document != null) {
				adjunt = document.isAdjunt();
				codi = document.getCodi();
			}
		}
		
		if (codi == null && (document == null || document.isAdjunt())) { 
			codi = new Long(new Date().getTime()).toString();
		}
		
		if (arxiu == null && document != null) {
			arxiu = document.getArxiuContingut();
			nomArxiu = document.getArxiuNom();
		} else if (arxiu == null && documentStore != null) {
			arxiu = documentStore.getArxiuContingut();
			nomArxiu = documentStore.getArxiuNom();
		}
		
		if (document != null && document.isAdjunt()) {
			expedientLoggerHelper.afegirLogExpedientPerProces(
					processInstanceId,
					ExpedientLogAccioTipus.PROCES_DOCUMENT_ADJUNTAR,
					codi);			
		} else if (creat) {
				expedientLoggerHelper.afegirLogExpedientPerProces(
						processInstanceId,
						ExpedientLogAccioTipus.PROCES_DOCUMENT_AFEGIR,
						codi);
		} else {
			expedientLoggerHelper.afegirLogExpedientPerProces(
					processInstanceId,
					ExpedientLogAccioTipus.PROCES_DOCUMENT_MODIFICAR,
					codi);
		}
		if (documentStoreId != null && adjunt) {
			documentStoreId = documentHelper.actualitzarAdjunt(
					documentStoreId,
					processInstanceId,
					codi,
					nom,
					data,
					nomArxiu,
					arxiu,
					adjunt);
		} else {
			documentStoreId = documentHelper.actualitzarDocument(
					null,
					processInstanceId,
					codi,
					nom,
					data,
					nomArxiu,
					arxiu,
					adjunt);
		}
		
		String user = SecurityContextHolder.getContext().getAuthentication().getName();
		
		// Registra l'acció
		if (creat) {
			registreDao.crearRegistreCrearDocumentInstanciaProces(
					expedient.getId(),
					processInstanceId,
					user,
					codi,
					nomArxiu);
		} else {
			registreDao.crearRegistreModificarDocumentInstanciaProces(
					expedient.getId(),
					processInstanceId,
					user,
					codi,
					arxiuNomAntic,
					nomArxiu);
		}
	}

	@Override
	@Transactional(readOnly = true)
	public ExpedientDto findAmbId(Long id) throws ExpedientNotFoundException {
		logger.debug("Consultant l'expedient (id=" + id + ")");
		Expedient expedient = expedientHelper.getExpedientComprovantPermisos(
				id,
				true,
				false,
				false,
				false);
		ExpedientDto expedientDto = conversioTipusHelper.convertir(
				expedient,
				ExpedientDto.class);
		expedientHelper.omplirPermisosExpedient(expedientDto);
		expedientHelper.trobarAlertesExpedient(expedientDto);
		return expedientDto;
	}

	@Override
	@Transactional(readOnly = true)
	public List<ExpedientDto> findAmbIds(Set<Long> ids) {
		List<ExpedientDto> listExpedient = new ArrayList<ExpedientDto>();
		logger.debug("Consultant l'expedient (ids=" + ids + ")");
		Set<Long> ids1 = null;
		Set<Long> ids2 = null;
		Set<Long> ids3 = null;
		Set<Long> ids4 = null;
		Set<Long> ids5 = null;
		int index = 0;
		for (Long id: ids) {
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
		for (Expedient expedient : expedientRepository.findAmbIds(ids1, ids2, ids3, ids4, ids5)) {
			listExpedient.add(conversioTipusHelper.convertir(
					expedient,
					ExpedientDto.class));
		}
		return listExpedient;
	}

	@Override
	@Transactional(readOnly = true)
	public PaginaDto<ExpedientDto> findAmbFiltrePaginat(
			Long entornId,
			Long expedientTipusId,
			String titol,
			String numero,
			Date dataInici1,
			Date dataInici2,
			Date dataFi1,
			Date dataFi2,
			EstatTipusDto estatTipus,
			Long estatId,
			Double geoPosX,
			Double geoPosY,
			String geoReferencia,
			boolean nomesTasquesPersonals,
			boolean nomesTasquesGrup,
			boolean nomesAlertes,
			boolean nomesErrors,
			MostrarAnulatsDto mostrarAnulats,
			PaginacioParamsDto paginacioParams) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		logger.debug("Consulta general d'expedients paginada (" +
				"entornId=" + entornId + ", " +
				"expedientTipusId=" + expedientTipusId + ", " +
				"titol=" + titol + ", " +
				"numero=" + numero + ", " +
				"dataInici1=" + dataInici1 + ", " +
				"dataInici2=" + dataInici2 + ", " +
				"dataFi1=" + dataFi1 + ", " +
				"dataFi2=" + dataFi2 + ", " +
				"estatTipus=" + estatTipus + ", " +
				"estatId=" + estatId + ", " +
				"geoPosX=" + geoPosX + ", " +
				"geoPosY=" + geoPosY + ", " +
				"geoReferencia=" + geoReferencia + ", " +
				"nomesTasquesPersonals=" + nomesTasquesPersonals + ", " +
				"nomesTasquesGrup=" + nomesTasquesGrup + ", " +
				"nomesAlertes=" + nomesAlertes + ", " +
				"nomesErrors=" + nomesErrors + ", " +
				"mostrarAnulats=" + mostrarAnulats + 
				"nomesTasquesPersonals=" + nomesTasquesPersonals + ", " +
				"nomesTasquesGrup=" + nomesTasquesGrup + ")");
		// Comprova l'accés a l'entorn
		Entorn entorn = entornHelper.getEntornComprovantPermisos(
				entornId,
				true,
				false,
				false);
		final Timer timerTotal = metricRegistry.timer(
				MetricRegistry.name(
						ExpedientService.class,
						"llistat"));
		final Timer.Context contextTotal = timerTotal.time();
		Counter countTotal = metricRegistry.counter(
				MetricRegistry.name(
						ExpedientService.class,
						"llistat.count"));
		countTotal.inc();
		final Timer timerEntorn = metricRegistry.timer(
				MetricRegistry.name(
						ExpedientService.class,
						"llistat",
						entorn.getCodi()));
		final Timer.Context contextEntorn = timerEntorn.time();
		Counter countEntorn = metricRegistry.counter(
				MetricRegistry.name(
						ExpedientService.class,
						"llistat.count",
						entorn.getCodi()));
		countEntorn.inc();
		try {
			// Comprova l'accés al tipus d'expedient
			ExpedientTipus expedientTipus = null;
			if (expedientTipusId != null) {
				expedientTipus = expedientTipusHelper.getExpedientTipusComprovantPermisos(
						expedientTipusId,
						true,
						false,
						false);
			}
			// Comprova l'accés a l'estat
			Estat estat = null;
			if (estatId != null) {
				estat = estatRepository.findByExpedientTipusAndId(expedientTipus, estatId);
				if (estat == null) {
					logger.debug("No s'ha trobat l'estat (expedientTipusId=" + expedientTipusId + ", estatId=" + estatId + ")");
					throw new EstatNotFoundException();
				}
			}
			// Calcula la data fi pel filtre
			if (dataInici2 != null) {
				Calendar cal = Calendar.getInstance();
				cal.setTime(dataInici2);
				cal.set(Calendar.HOUR_OF_DAY, 23);
				cal.set(Calendar.MINUTE, 59);
				cal.set(Calendar.SECOND, 59);
				cal.set(Calendar.MILLISECOND, 999);
				dataInici2.setTime(cal.getTime().getTime());
			}
			// Obté la llista de tipus d'expedient permesos
			List<Long> tipusPermesosIds = expedientTipusHelper.findIdsAmbPermisRead(entorn);
			// Executa la consulta amb paginació
			ResultatConsultaPaginadaJbpm<Long> expedientsIds = jbpmHelper.expedientFindByFiltre(
					entornId,
					auth.getName(),
					tipusPermesosIds,
					titol,
					numero,
					expedientTipusId,
					dataInici1,
					dataInici2,
					estatId,
					geoPosX,
					geoPosY,
					geoReferencia,
					EstatTipusDto.INICIAT.equals(estatTipus),
					EstatTipusDto.FINALITZAT.equals(estatTipus),
					MostrarAnulatsDto.SI.equals(mostrarAnulats),
					MostrarAnulatsDto.NOMES_ANULATS.equals(mostrarAnulats),
					nomesAlertes,
					nomesErrors,
					nomesTasquesPersonals,
					nomesTasquesGrup,
					true, // nomesTasquesMeves,
					paginacioParams,
					false);
			// Retorna la pàgina amb la resposta
			List<ExpedientDto> expedients = new ArrayList<ExpedientDto>(); 
			if (expedientsIds.getCount() > 0) {
				expedients = conversioTipusHelper.convertirList(
					expedientRepository.findByIdIn(expedientsIds.getLlista()),
					ExpedientDto.class);
			}
			// Després de la consulta els expedients es retornen en ordre invers
			Collections.reverse(expedients);
			if (expedients.size() > 0) {
				expedientHelper.omplirPermisosExpedients(expedients);
				expedientHelper.trobarAlertesExpedients(expedients);
			}
			PaginaDto<ExpedientDto> pagina = paginacioHelper.toPaginaDto(
					expedients,
					expedientsIds.getCount(),
					paginacioParams);
			return pagina;
		} finally {
			contextTotal.stop();
			contextEntorn.stop();
		}
	}

	@Transactional(readOnly = true)
	@Override
	public List<Long> findIdsAmbFiltre(
			Long entornId,
			Long expedientTipusId,
			String titol,
			String numero,
			Date dataInici1,
			Date dataInici2,
			Date dataFi1,
			Date dataFi2,
			EstatTipusDto estatTipus,
			Long estatId,
			Double geoPosX,
			Double geoPosY,
			String geoReferencia,
			boolean nomesTasquesPersonals,
			boolean nomesTasquesGrup,
			boolean nomesAlertes,
			boolean nomesErrors,
			MostrarAnulatsDto mostrarAnulats) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		logger.debug("Consulta general d'expedients només ids (" +
				"entornId=" + entornId + ", " +
				"expedientTipusId=" + expedientTipusId + ", " +
				"titol=" + titol + ", " +
				"numero=" + numero + ", " +
				"dataInici1=" + dataInici1 + ", " +
				"dataInici2=" + dataInici2 + ", " +
				"dataFi1=" + dataFi1 + ", " +
				"dataFi2=" + dataFi2 + ", " +
				"estatTipus=" + estatTipus + ", " +
				"estatId=" + estatId + ", " +
				"geoPosX=" + geoPosX + ", " +
				"geoPosY=" + geoPosY + ", " +
				"geoReferencia=" + geoReferencia + ", " +
				"nomesAlertes=" + nomesAlertes + ", " +
				"nomesErrors=" + nomesErrors + ", " +
				"mostrarAnulats=" + mostrarAnulats + 
				"nomesTasquesPersonals=" + nomesTasquesPersonals + ", " +
				"nomesTasquesGrup=" + nomesTasquesGrup + ")");
		// Comprova l'accés a l'entorn
		Entorn entorn = entornHelper.getEntornComprovantPermisos(
				entornId,
				true,
				false,
				false);
		final Timer timerTotal = metricRegistry.timer(
				MetricRegistry.name(
						ExpedientService.class,
						"llistatIds"));
		final Timer.Context contextTotal = timerTotal.time();
		Counter countTotal = metricRegistry.counter(
				MetricRegistry.name(
						ExpedientService.class,
						"llistatIds.count"));
		countTotal.inc();
		final Timer timerEntorn = metricRegistry.timer(
				MetricRegistry.name(
						ExpedientService.class,
						"llistatIds",
						entorn.getCodi()));
		final Timer.Context contextEntorn = timerEntorn.time();
		Counter countEntorn = metricRegistry.counter(
				MetricRegistry.name(
						ExpedientService.class,
						"llistatIds.count",
						entorn.getCodi()));
		countEntorn.inc();
		try {
			// Comprova l'accés al tipus d'expedient
			ExpedientTipus expedientTipus = null;
			if (expedientTipusId != null) {
				expedientTipus = expedientTipusHelper.getExpedientTipusComprovantPermisos(
						expedientTipusId,
						true,
						false,
						false);
			}
			// Comprova l'accés a l'estat
			Estat estat = null;
			if (estatId != null) {
				estat = estatRepository.findByExpedientTipusAndId(expedientTipus, estatId);
				if (estat == null) {
					logger.debug("No s'ha trobat l'estat (expedientTipusId=" + expedientTipusId + ", estatId=" + estatId + ")");
					throw new EstatNotFoundException();
				}
			}
			// Calcula la data fi pel filtre
			if (dataInici2 != null) {
				Calendar cal = Calendar.getInstance();
				cal.setTime(dataInici2);
				cal.set(Calendar.HOUR_OF_DAY, 23);
				cal.set(Calendar.MINUTE, 59);
				cal.set(Calendar.SECOND, 59);
				cal.set(Calendar.MILLISECOND, 999);
				dataInici2.setTime(cal.getTime().getTime());
			}
			// Obté la llista de tipus d'expedient permesos
			List<Long> tipusPermesosIds = expedientTipusHelper.findIdsAmbPermisRead(entorn);
			// Executa la consulta amb paginació
			ResultatConsultaPaginadaJbpm<Long> expedientsIds = jbpmHelper.expedientFindByFiltre(
					entornId,
					auth.getName(),
					tipusPermesosIds,
					titol,
					numero,
					expedientTipusId,
					dataInici1,
					dataInici2,
					estatId,
					geoPosX,
					geoPosY,
					geoReferencia,
					EstatTipusDto.INICIAT.equals(estatTipus),
					EstatTipusDto.FINALITZAT.equals(estatTipus),
					MostrarAnulatsDto.SI.equals(mostrarAnulats),
					MostrarAnulatsDto.NOMES_ANULATS.equals(mostrarAnulats),
					nomesAlertes,
					nomesErrors,
					nomesTasquesPersonals,
					nomesTasquesGrup,
					true, // nomesTasquesMeves, // TODO Si no te permis SUPERVISION nomesTasquesMeves = false
					new PaginacioParamsDto(),
					false);
			return expedientsIds.getLlista();
		} finally {
			contextTotal.stop();
			contextEntorn.stop();
		}
	}

	@Override
	@Transactional(readOnly = true)
	public boolean isDiferentsTipusExpedients(Set<Long> ids) {
		Set<Long> ids1 = null;
		Set<Long> ids2 = null;
		Set<Long> ids3 = null;
		Set<Long> ids4 = null;
		Set<Long> ids5 = null;
		int index = 0;
		for (Long id: ids) {
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
		
		List<Long> idsTipusExpedients = expedientRepository.getIdsDiferentsTipusExpedients(
				ids1,
				ids2,
				ids3,
				ids4,
				ids5);
		return idsTipusExpedients.size() > 1;
	}

	@Override
	@Transactional(readOnly = true)
	public ArxiuDto arxiuDocumentPerMostrar(String token) {
		Long documentStoreId = documentHelper.getDocumentStoreIdPerToken(token);
		DocumentStore document = documentStoreRepository.findById(documentStoreId);
		if (document == null)
			return null;
		DocumentDto dto = null;
		if (document.isSignat() || document.isRegistrat()) {
			dto = documentHelper.getDocumentVista(documentStoreId, false, false);
			if (dto == null)
				return null;
			return new ArxiuDto(dto.getVistaNom(), dto.getVistaContingut());
		} else {
			dto = documentHelper.getDocumentOriginal(documentStoreId, true);
			if (dto == null)
				return null;
			return new ArxiuDto(dto.getArxiuNom(), dto.getArxiuContingut());
		}
	}

	@Override
	@Transactional(readOnly = true)
	public ArxiuDto arxiuDocumentPerSignar(String token) {
		Long documentStoreId = documentHelper.getDocumentStoreIdPerToken(token);
		DocumentDto dto = documentHelper.getDocumentVista(documentStoreId, true, true);
		if (dto == null)
			return null;
		return new ArxiuDto(dto.getVistaNom(), dto.getVistaContingut());
	}

	@Override
	@Transactional(readOnly = true)
	public ArxiuDto getImatgeDefinicioProces(
			Long id,
			String processInstanceId) {
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
					expedient.getId(),
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
				jbpmHelper.getResourceBytes(
						definicioProces.getJbpmId(),
						resourceName));
		return imatge;
	}

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
		List<ExpedientTascaDto> tasques = tascaHelper.findTasquesPerExpedient(
				expedient,
				false,
				false);
		Set<String> codisPersona = new HashSet<String>();
		List<PersonaDto> resposta = new ArrayList<PersonaDto>();
		for (ExpedientTascaDto tasca: tasques) {
			if (tasca.getAssignee() != null && !codisPersona.contains(tasca.getAssignee())) {
				resposta.add(tasca.getResponsable());
				codisPersona.add(tasca.getAssignee());
			}
		}
		return resposta;
	}

	@Override
	@Transactional(readOnly = true)
	public List<AccioDto> findAccionsVisiblesAmbProcessInstanceId(String processInstanceId, Long expedientId) {
		logger.debug("Consulta d'accions visibles de l'expedient amb processInstanceId(" +
				"processInstanceId=" + processInstanceId + ")");
		DefinicioProces definicioProces = expedientHelper.findDefinicioProcesByProcessInstanceId(processInstanceId);
		List<Accio> accions = accioRepository.findAmbDefinicioProcesAndOcultaFalse(definicioProces);
		// Filtra les accions restringides per rol que no estan permeses per a l'usuari actual
		
		Iterator<Accio> it = accions.iterator();
		while (it.hasNext()) {
			Accio accio = it.next();
			if (!permetreExecutarAccioExpedient(accio, expedientId)) it.remove();
		}
		
		return conversioTipusHelper.convertirList(accions, AccioDto.class);
	}
	
	private boolean permetreExecutarAccioExpedient(Accio accio, Long expedientId) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		boolean permesa = true;
		
		if (!accio.isPublica()) {
			try {
				expedientHelper.getExpedientComprovantPermisos(
					expedientId,
					false,
					true,
					false,
					false);
			} catch (NotAllowedException ex) {
				permesa =  false;
			}
		}
		
		if (permesa && accio.getRols() != null) {
			permesa = false;
			for (String rol: accio.getRols().split(",")) {
				if (isUserInRole(auth, rol)) {
					permesa = true;
					break;
				}
			}
		}
		
		return permesa;
	}
	
	@Transactional
	@Override
	public void accioExecutar(
			Long expedientId,
			String processInstanceId,
			Long accioId) {
		Accio accio = accioRepository.findOne(accioId);
		
		if (this.permetreExecutarAccioExpedient(accio, expedientId)) {
			Expedient expedient = expedientRepository.findOne(expedientId);
			
			mesuresTemporalsHelper.mesuraIniciar("Executar ACCIO" + accio.getNom(), "expedient", expedient.getTipus().getNom());
			expedientLoggerHelper.afegirLogExpedientPerProces(
					processInstanceId,
					ExpedientLogAccioTipus.EXPEDIENT_ACCIO,
					accio.getJbpmAction());
			try {
				jbpmHelper.executeActionInstanciaProces(
						processInstanceId,
						accio.getJbpmAction());
			} catch (Exception ex) {
				if (ex instanceof ExecucioHandlerException) {
					logger.error(
							"Error al executa l'acció '" + accio.getCodi() + "': " + ex.toString(),
							ex.getCause());
				} else {
					logger.error(
							"Error al executa l'acció '" + accio.getCodi() + "'",
							ex);
				}
				throw new net.conselldemallorca.helium.v3.core.api.exception.JbpmException(
						expedient.getId(),
						expedient.getIdentificador(),
						expedient.getTipus().getId(),
						processInstanceId,
						(ex instanceof ExecucioHandlerException) ? ex.getCause() : ex);
			}
			verificarFinalitzacioExpedient(processInstanceId, expedient);
			indexHelper.expedientIndexLuceneUpdate(processInstanceId);
			mesuresTemporalsHelper.mesuraCalcular("Executar ACCIO" + accio.getNom(), "expedient", expedient.getTipus().getNom());
		} else {
			throw new NotAllowedException(expedientId, Expedient.class, PermisTipusEnumDto.WRITE);
		}
	}

	private void verificarFinalitzacioExpedient(String processInstanceId, Expedient expedient) {
		JbpmProcessInstance pi = jbpmHelper.getRootProcessInstance(processInstanceId);
		if (pi.getEnd() != null) {
			// Actualitzar data de fi de l'expedient
			expedient.setDataFi(pi.getEnd());
			// Finalitzar terminis actius
			for (TerminiIniciat terminiIniciat: terminiIniciatRepository.findByProcessInstanceId(pi.getId())) {
				if (terminiIniciat.getDataInici() != null) {
					terminiIniciat.setDataCancelacio(new Date());
					long[] timerIds = terminiIniciat.getTimerIdsArray();
					for (int i = 0; i < timerIds.length; i++)
						jbpmHelper.suspendTimer(
								timerIds[i],
								new Date(Long.MAX_VALUE));
				}
			}
		}
	}
	
	@Transactional(readOnly=true)
	@Override
	public AccioDto findAccioAmbId(Long idAccio) {
		return conversioTipusHelper.convertir(accioRepository.findOne(idAccio), AccioDto.class);
	}

	@Override
	@Transactional(readOnly = true)
	public List<AccioDto> findAccionsVisibles(Long id) {
		logger.debug("Consulta d'accions visibles de l'expedient (" +
				"id=" + id + ")");
		Expedient expedient = expedientHelper.getExpedientComprovantPermisos(
				id,
				true,
				false,
				false,
				false);
		DefinicioProces definicioProces = expedientHelper.findDefinicioProcesByProcessInstanceId(
				expedient.getProcessInstanceId());
		List<Accio> accions = accioRepository.findAmbDefinicioProcesAndOcultaFalse(definicioProces);
		// Filtra les accions restringides per rol que
		// no estan permeses per a l'usuari actual
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		Iterator<Accio> it = accions.iterator();
		while (it.hasNext()) {
			Accio accio = it.next();
			if (accio.getRols() != null) {
				boolean permesa = false;
				for (String rol: accio.getRols().split(",")) {
					if (isUserInRole(auth, rol)) {
						permesa = true;
						break;
					}
				}
				if (!permesa) it.remove();
			}
		}
		return conversioTipusHelper.convertirList(
				accions,
				AccioDto.class);
	}

	@Override
	// No pot ser readOnly per mor de la cache de les tasques
	@Transactional
	public List<ExpedientTascaDto> findTasquesPerInstanciaProces(Long expedientId, String processInstanceId, boolean mostrarDeOtrosUsuarios) {
		logger.debug("Consulta de tasques de l'expedient (" +
				"expedientId=" + expedientId + ")");
		// TODO tasques pendents per expedients refer
		Expedient expedient = expedientHelper.getExpedientComprovantPermisos(
				expedientId,
				true,
				false,
				false,
				false);
		List<ExpedientTascaDto> tasques = tascaHelper.findTasquesPerExpedientPerInstanciaProces(expedient, processInstanceId, false, mostrarDeOtrosUsuarios);
		tasques.addAll(tascaHelper.findTasquesPerExpedientPerInstanciaProces(expedient, processInstanceId, true, mostrarDeOtrosUsuarios));
		return tasques;
	}

	@Override
	// No pot ser readOnly per mor de la cache de les tasques
	@Transactional
	public List<ExpedientTascaDto> findTasquesPendents(
			Long expedientId,
			boolean nomesTasquesPersonals,
			boolean nomesTasquesGrup) {
		// TODO tasques pendents per expedients refer
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
		boolean tasquesAltresUsuaris = permisosHelper.isGrantedAny(
					expedient.getTipus().getId(),
					ExpedientTipus.class,
					new Permission[] {
						ExtendedPermission.REASSIGNMENT,
						ExtendedPermission.ADMINISTRATION},
					auth);
		List<ExpedientTascaDto> resposta = new ArrayList<ExpedientTascaDto>();
		for (JbpmProcessInstance jpi: jbpmHelper.getProcessInstanceTree(expedient.getProcessInstanceId())) {
			resposta.addAll(
					tascaHelper.findTasquesPerExpedientPerInstanciaProces(
							jpi.getId(),
							expedient,
							tasquesAltresUsuaris,
							nomesTasquesPersonals,
							nomesTasquesGrup));
		}
		return resposta;
	}

	@Override
	@Transactional(readOnly = true)
	public List<ExpedientDadaDto> findDadesPerInstanciaProces(
			Long id,
			String processInstanceId) {
		logger.debug("Consulta de dades de l'expedient (" +
				"id=" + id + ", " +
				"processInstanceId=" + processInstanceId + ")");
		Expedient expedient = expedientHelper.getExpedientComprovantPermisos(
				id,
				true,
				false,
				false,
				false);
		if (processInstanceId == null) {
			return variableHelper.findDadesPerInstanciaProces(
					expedient.getProcessInstanceId(), 
					true);
		} else {
			expedientHelper.comprovarInstanciaProces(
					expedient.getId(),
					processInstanceId);
			return variableHelper.findDadesPerInstanciaProces(
					processInstanceId,
					true);
		}
	}

	@Override
	@Transactional(readOnly = true)
	public List<CampAgrupacioDto> findAgrupacionsDadesPerInstanciaProces(
			Long id,
			String processInstanceId) {
		logger.debug("Consulta de les agrupacions de dades de l'expedient (" +
				"id=" + id + ", " +
				"processInstanceId=" + processInstanceId + ")");
		Expedient expedient = expedientHelper.getExpedientComprovantPermisos(
				id,
				true,
				false,
				false,
				false);
		DefinicioProces definicioProces;
		if (processInstanceId == null) {
			definicioProces = expedientHelper.findDefinicioProcesByProcessInstanceId(
					expedient.getProcessInstanceId());
		} else {
			expedientHelper.comprovarInstanciaProces(
					expedient.getId(),
					processInstanceId);
			definicioProces = expedientHelper.findDefinicioProcesByProcessInstanceId(
					processInstanceId);
		}
		return conversioTipusHelper.convertirList(
				definicioProces.getAgrupacions(),
				CampAgrupacioDto.class);
	}

	/*@Override
	@Transactional(readOnly = true)
	public ExpedientDocumentDto findDocumentPerInstanciaProcesDocumentStoreId(
			Long expedientId,
			Long documentStoreId,
			String docCodi) {
		logger.debug("Consulta el document de l'expedient (" +
				"expedientId=" + expedientId + ", " +
				"docCodi=" + docCodi + ", " +
				"documentStoreId=" + documentStoreId + ")");
		Expedient expedient = expedientHelper.getExpedientComprovantPermisos(
				expedientId,
				true,
				false,
				false,
				false);
		return documentHelper.findDocumentPerExpedientDocumentStoreId(
				expedient,
				documentStoreId,
				docCodi);
	}*/

	@Override
	@Transactional
	public void deleteSignatura(
			Long expedientId,
			Long documentStoreId) throws Exception {
		Expedient expedient = expedientHelper.getExpedientComprovantPermisos(
				expedientId,
				false,
				true,
				false,
				false);
		DocumentStore documentStore = documentStoreRepository.findById(documentStoreId);
		if (documentStore != null && documentStore.isSignat()) {
			try {
				pluginCustodiaDao.esborrarSignatures(documentStore.getReferenciaCustodia());
			} catch (Exception ignored) {}
			String jbpmVariable = documentStore.getJbpmVariable();
			documentStore.setReferenciaCustodia(null);
			documentStore.setSignat(false);
			registreDao.crearRegistreEsborrarSignatura(
					expedient.getId(),
					expedient.getProcessInstanceId(),
					SecurityContextHolder.getContext().getAuthentication().getName(),
					getVarNameFromDocumentStore(documentStore));
			List<JbpmTask> tasks = jbpmHelper.findTaskInstancesForProcessInstance(expedient.getProcessInstanceId());
			for (JbpmTask task: tasks) {
				jbpmHelper.deleteTaskInstanceVariable(
						task.getId(),
						jbpmVariable);
			}
		}
	}

	private String getVarNameFromDocumentStore(DocumentStore documentStore) {
		String jbpmVariable = documentStore.getJbpmVariable();
		if (documentStore.isAdjunt())
			return jbpmVariable.substring(DocumentHelper.PREFIX_ADJUNT.length());
		else
			return jbpmVariable.substring(DocumentHelper.PREFIX_VAR_DOCUMENT.length());
	}

	@Override
	@Transactional(readOnly = true)
	public List<ExpedientDocumentDto> findDocumentsPerInstanciaProces(
			Long expedientId,
			String processInstanceId) {
		logger.debug("Consulta els documents de l'instància de procés (" +
				"expedientId=" + expedientId + ", " +
				"processInstanceId=" + processInstanceId + ")");
		Expedient expedient = expedientHelper.getExpedientComprovantPermisos(
				expedientId,
				true,
				false,
				false,
				false);
		if (processInstanceId == null) {
			return documentHelper.findDocumentsPerInstanciaProces(
					expedient.getProcessInstanceId());
		} else {
			expedientHelper.comprovarInstanciaProces(
					expedient.getId(),
					processInstanceId);
			return documentHelper.findDocumentsPerInstanciaProces(
					processInstanceId);
		}
	}

	@Override
	@Transactional(readOnly = true)
	public ExpedientDocumentDto findDocumentPerInstanciaProces(
			Long expedientId,
			String processInstanceId,
			Long documentStoreId,
			String documentCodi) {
		logger.debug("Consulta un document de l'instància de procés (" +
				"expedientId=" + expedientId + ", " +
				"processInstanceId=" + processInstanceId + ", " +
				"documentStoreId=" + documentStoreId + ", " +
				"documentCodi=" + documentCodi + ")");
		Expedient expedient = expedientHelper.getExpedientComprovantPermisos(
				expedientId,
				true,
				false,
				false,
				false);
		if (processInstanceId == null) {
			return documentHelper.findDocumentPerInstanciaProces(
					expedient.getProcessInstanceId(),
					documentStoreId,
					documentCodi);
		} else {
			return documentHelper.findDocumentPerInstanciaProces(
					processInstanceId,
					documentStoreId,
					documentCodi);
		}
	}
	
	@Override
	@Transactional(readOnly = true)
	public ExpedientDocumentDto findDocumentPerDocumentStoreId(
			Long expedientId,
			String processInstanceId,
			Long documentStoreId) {
		logger.debug("Consulta un document de l'instància de procés (" +
				"expedientId=" + expedientId + ", " +
				"processInstanceId=" + processInstanceId + ", " +
				"documentStoreId=" + documentStoreId + ")");
		Expedient expedient = expedientHelper.getExpedientComprovantPermisos(
				expedientId,
				true,
				false,
				false,
				false);
		if (processInstanceId == null) {
			return documentHelper.findDocumentPerDocumentStoreId(
					expedient.getProcessInstanceId(),
					documentStoreId);
		} else {
			return documentHelper.findDocumentPerDocumentStoreId(
					processInstanceId,
					documentStoreId);
		}
	}

	@Override
	@Transactional
	public void esborrarDocument(
			Long expedientId,
			String processInstanceId,
			Long documentStoreId) {
		logger.debug("Consulta un document de l'instància de procés (" +
				"expedientId=" + expedientId + ", " +
				"processInstanceId=" + processInstanceId + ", " +
				"documentStoreId=" + documentStoreId + ")");
		Expedient expedient = expedientHelper.getExpedientComprovantPermisos(
				expedientId,
				false,
				true,
				false,
				false);
		if (processInstanceId == null) {
			documentHelper.esborrarDocument(
					null,
					expedient.getProcessInstanceId(),
					documentStoreId);
		} else {
			documentHelper.esborrarDocument(
					null,
					processInstanceId,
					documentStoreId);
		}
		
	}

	@Override
	@Transactional(readOnly = true)
	public List<DocumentDto> findListDocumentsPerDefinicioProces(
			Long definicioProcesId,
			String processInstanceId,
			String expedientTipusNom) {
		List<Document> documents = documentRepository.findAmbDefinicioProces(definicioProcesId);
		return conversioTipusHelper.convertirList(
				documents,
				DocumentDto.class);
	}

	@Override
	@Transactional(readOnly = true)
	public DocumentDto findDocumentsPerId(Long id) {
		Document document = documentRepository.findOne(id);
		return conversioTipusHelper.convertir(
				document,
				DocumentDto.class);
	}

	@Override
	@Transactional(readOnly = true)
	public ArxiuDto getArxiuPerDocument(
			Long id,
			Long documentStoreId) {
		logger.debug("btenint contingut de l'arxiu per l'expedient (" +
				"id=" + id + ", " +
				"documentStoreId=" + documentStoreId + ")");
		Expedient expedient = expedientHelper.getExpedientComprovantPermisos(
				id,
				true,
				false,
				false,
				false);
		DocumentStore documentStore = documentStoreRepository.findOne(documentStoreId);
		if (documentStore == null) {
			throw new NotFoundException(
					documentStoreId,
					DocumentStore.class);
		}
		expedientHelper.comprovarInstanciaProces(
				expedient.getId(),
				documentStore.getProcessInstanceId());
		return documentHelper.getArxiuPerDocumentStoreId(
				documentStoreId,
				false,
				false);
	}

	@Override
	@Transactional
	public void aturar(
			Long id,
			String motiu) {
		logger.debug("Aturant l'expedient (" +
				"id=" + id + ", " +
				"motiu=" + motiu + ")");
		Expedient expedient = expedientHelper.getExpedientComprovantPermisos(
				id,
				false,
				true,
				false,
				false);
		ExpedientLog expedientLog = expedientLoggerHelper.afegirLogExpedientPerExpedient(
				expedient.getId(),
				ExpedientLogAccioTipus.EXPEDIENT_ATURAR,
				motiu);
		expedientLog.setEstat(ExpedientLogEstat.IGNORAR);
		logger.debug("Suspenent les instàncies de procés associades a l'expedient (id=" + id + ")");
		List<JbpmProcessInstance> processInstancesTree = jbpmHelper.getProcessInstanceTree(
				expedient.getProcessInstanceId());
		String[] ids = new String[processInstancesTree.size()];
		int i = 0;
		for (JbpmProcessInstance pi: processInstancesTree)
			ids[i++] = pi.getId();
		jbpmHelper.suspendProcessInstances(ids);
		expedient.setInfoAturat(motiu);
	}

	@Override
	@Transactional
	public void activa(Long id) {
		logger.debug("Activant l'expedient (id=" + id + ")");
		Expedient expedient = expedientHelper.getExpedientComprovantPermisos(
				id,
				false,
				true,
				false,
				false);
		ExpedientLog expedientLog = expedientLoggerHelper.afegirLogExpedientPerExpedient(
				expedient.getId(),
				ExpedientLogAccioTipus.EXPEDIENT_REPRENDRE,
				null);
		expedientLog.setEstat(ExpedientLogEstat.IGNORAR);
		logger.debug("Reprenent les instàncies de procés associades a l'expedient (id=" + id + ")");
		List<JbpmProcessInstance> processInstancesTree = jbpmHelper.getProcessInstanceTree(
				expedient.getProcessInstanceId());
		String[] ids = new String[processInstancesTree.size()];
		int i = 0;
		for (JbpmProcessInstance pi: processInstancesTree)
			ids[i++] = pi.getId();
		jbpmHelper.resumeProcessInstances(ids);
		expedient.setAnulat(false);
	}
	
	@Override
	@Transactional
	public void reprendre(Long id) throws Exception {
		logger.debug("Reprenent l'expedient (id=" + id + ")");
		Expedient expedient = expedientHelper.getExpedientComprovantPermisos(
				id,
				false,
				true,
				false,
				false);
		ExpedientLog expedientLog = expedientLoggerHelper.afegirLogExpedientPerExpedient(
				expedient.getId(),
				ExpedientLogAccioTipus.EXPEDIENT_REPRENDRE,
				null);
		expedientLog.setEstat(ExpedientLogEstat.IGNORAR);
		logger.debug("Reprenent les instàncies de procés associades a l'expedient (id=" + id + ")");
		List<JbpmProcessInstance> processInstancesTree = jbpmHelper.getProcessInstanceTree(
				expedient.getProcessInstanceId());
		String[] ids = new String[processInstancesTree.size()];
		int i = 0;
		for (JbpmProcessInstance pi: processInstancesTree)
			ids[i++] = pi.getId();
		jbpmHelper.resumeProcessInstances(ids);
		expedient.setInfoAturat(null);
		registreDao.crearRegistreReprendreExpedient(
				expedient.getId(),
				(expedient.getResponsableCodi() != null) ? expedient.getResponsableCodi() : SecurityContextHolder.getContext().getAuthentication().getName());
	}
	
	@Override
	@Transactional
	public void desfinalitzar(Long id) throws Exception {
		logger.debug("Reprenent l'expedient (id=" + id + ")");
		Expedient expedient = expedientHelper.getExpedientComprovantPermisos(
				id,
				false,
				true,
				false,
				false);
		ExpedientLog expedientLog = expedientLoggerHelper.afegirLogExpedientPerExpedient(
				expedient.getId(),
				ExpedientLogAccioTipus.EXPEDIENT_REPRENDRE,
				null);
		expedientLog.setEstat(ExpedientLogEstat.IGNORAR);
		logger.debug("Desfer finalització de l'expedient (id=" + id + ")");
		
		jbpmHelper.reprendreExpedient(expedient.getProcessInstanceId());
		expedient.setDataFi(null);
		
		registreDao.crearRegistreReprendreExpedient(
				expedient.getId(),
				(expedient.getResponsableCodi() != null) ? expedient.getResponsableCodi() : SecurityContextHolder.getContext().getAuthentication().getName());
	}

	@Override
	@Transactional
	public void cancel(
			Long id,
			String motiu) {
		logger.debug("Anulant l'expedient (" +
				"id=" + id + ", " +
				"motiu=" + motiu + ")");
		Expedient expedient = expedientHelper.getExpedientComprovantPermisos(
				id,
				false,
				true,
				false,
				false);
		mesuresTemporalsHelper.mesuraIniciar(
				"Anular",
				"expedient",
				expedient.getTipus().getNom());
		List<JbpmProcessInstance> processInstancesTree = jbpmHelper.getProcessInstanceTree(expedient.getProcessInstanceId());
		String[] ids = new String[processInstancesTree.size()];
		int i = 0;
		for (JbpmProcessInstance pi: processInstancesTree)
			ids[i++] = pi.getId();
		jbpmHelper.suspendProcessInstances(ids);
		expedient.setAnulat(true);
		expedient.setComentariAnulat(motiu);
		luceneHelper.deleteExpedient(expedient);
		crearRegistreExpedient(
				expedient.getId(),
				SecurityContextHolder.getContext().getAuthentication().getName(),
				Registre.Accio.ANULAR);
		mesuresTemporalsHelper.mesuraCalcular(
				"Anular",
				"expedient",
				expedient.getTipus().getNom());
	}

	@Override
	@Transactional
	public void createRelacioExpedient(
			Long origenId,
			Long destiId) {
		logger.debug("Creant relació d'expedients (" +
				"origenId=" + origenId + ", " +
				"destiId=" + destiId + ")");
		ExpedientLog expedientLog = expedientLoggerHelper.afegirLogExpedientPerExpedient(
				origenId,
				ExpedientLogAccioTipus.EXPEDIENT_RELACIO_AFEGIR,
				destiId.toString());
		expedientLog.setEstat(ExpedientLogEstat.IGNORAR);
		Expedient origen = expedientHelper.getExpedientComprovantPermisos(
				origenId,
				false,
				true,
				false,
				false);
		Expedient desti = expedientHelper.getExpedientComprovantPermisos(
				destiId,
				true,
				false,
				false,
				false);
		boolean existeix = false;
		for (Expedient relacionat: origen.getRelacionsOrigen()) {
			if (relacionat.getId().longValue() == destiId.longValue()) {
				existeix = true;
				break;
			}
		}
		if (!existeix)
			origen.addRelacioOrigen(desti);
		existeix = false;
		for (Expedient relacionat: desti.getRelacionsOrigen()) {
			if (relacionat.getId().longValue() == origenId.longValue()) {
				existeix = true;
				break;
			}
		}
		if (!existeix)
			desti.addRelacioOrigen(origen);
	}

	@Transactional
	@Override
	public void deleteRelacioExpedient(
			Long origenId,
			Long destiId) {
		logger.debug("Esborrant relació d'expedients (" +
				"origenId=" + origenId + ", " +
				"destiId=" + destiId + ")");
		ExpedientLog expedientLog = expedientLoggerHelper.afegirLogExpedientPerExpedient(
				origenId,
				ExpedientLogAccioTipus.EXPEDIENT_RELACIO_ESBORRAR,
				destiId.toString());
		expedientLog.setEstat(ExpedientLogEstat.IGNORAR);
		Expedient origen = expedientHelper.getExpedientComprovantPermisos(
				origenId,
				false,
				true,
				false,
				false);
		Expedient desti = expedientHelper.getExpedientComprovantPermisos(
				destiId,
				false,
				false,
				false,
				false);
		origen.removeRelacioOrigen(desti);
		desti.removeRelacioOrigen(origen);		
	}

	@Override
	@Transactional(readOnly = true)
	public List<ExpedientDto> findRelacionats(Long id) {
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
			list.add(findAmbId(relacionat.getId()));
		}
		return list;
	}
	
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
		// Convertir a AlertaDto
		return conversioTipusHelper.convertirList(alertes, AlertaDto.class);
	}
	
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
		List<Portasignatures> portasignatures = portasignaturesRepository.findByExpedientAndEstat(expedient, TipusEstat.ERROR);
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
	public String canviVersioDefinicioProces(
			Long id,
			int versio) {
		logger.debug("Canviant versió de la definició de procés (" +
				"id=" + id + ", " +
				"versio=" + versio + ")");
		Expedient expedient = expedientHelper.getExpedientComprovantPermisos(
				id,
				false,
				true,
				false,
				false);
		DefinicioProces defprocNova = null;
		try {
			jbpmHelper.changeProcessInstanceVersion(expedient.getProcessInstanceId(), versio);
			// Apunta els terminis iniciats cap als terminis
			// de la nova definició de procés
			DefinicioProces defprocAntiga = expedientHelper.findDefinicioProcesByProcessInstanceId(expedient.getProcessInstanceId());
			defprocNova = expedientHelper.findDefinicioProcesByProcessInstanceId(
					expedient.getProcessInstanceId());
			List<TerminiIniciat> terminisIniciats = terminiIniciatRepository.findByProcessInstanceId(expedient.getProcessInstanceId());
			for (TerminiIniciat terminiIniciat: terminisIniciats) {
				Termini termini = terminiIniciat.getTermini();
				if (termini.getDefinicioProces().getId().equals(defprocAntiga.getId())) {
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
		} catch (Exception ex) {
			logger.error("Canviant versió de la definició de procés (" +
					"id=" + id + ", " +
					"versio=" + versio + ")", ex);
		}
		return defprocNova.getEtiqueta();
	}

	@Override
	@Transactional
	public void evaluateScript(
			Long expedientId,
			String script,
			String processInstanceId) {
		logger.debug("Consulta d'expedients relacionats amb l'expedient (" +
				"id=" + expedientId + ")");
		Expedient expedient = expedientHelper.getExpedientComprovantPermisos(
				expedientId,
				false,
				false,
				false,
				true);
		
		expedientHelper.comprovarInstanciaProces(expedient.getId(), processInstanceId);
		
		JbpmProcessInstance pi = jbpmHelper.getProcessInstance(processInstanceId);
		if (MesuresTemporalsHelper.isActiu()) {
			mesuresTemporalsHelper.mesuraIniciar("Executar SCRIPT", "expedient", expedient.getTipus().getNom());
		}
		jbpmHelper.evaluateScript(processInstanceId, script, new HashSet<String>());
		verificarFinalitzacioExpedient(expedient, pi);
		indexHelper.expedientIndexLuceneUpdate(processInstanceId);
		expedientLoggerHelper.afegirLogExpedientPerProces(
				processInstanceId,
				ExpedientLogAccioTipus.PROCES_SCRIPT_EXECUTAR,
				script);
		if (MesuresTemporalsHelper.isActiu())
			mesuresTemporalsHelper.mesuraCalcular("Executar SCRIPT", "expedient", expedient.getTipus().getNom());
	}

	@Override
	@Transactional(readOnly = true)
	public List<ExpedientDto> findSuggestAmbEntornLikeIdentificador(Long entornId, String text) {
		List<ExpedientDto> resposta = new ArrayList<ExpedientDto>();
		List<Expedient> expedients = expedientRepository.findAmbEntornLikeIdentificador(entornId, text);
		for (Expedient expedient : expedients) {
			resposta.add(conversioTipusHelper.convertir(expedient,ExpedientDto.class));
		}
		return resposta;
	}

	@Transactional(readOnly = true)
	@Override
	public List<InstanciaProcesDto> getArbreInstanciesProces(
				Long processInstanceId) {
		List<InstanciaProcesDto> resposta = new ArrayList<InstanciaProcesDto>();
		JbpmProcessInstance rootProcessInstance = jbpmHelper.getRootProcessInstance(String.valueOf(processInstanceId));
		List<JbpmProcessInstance> piTree = jbpmHelper.getProcessInstanceTree(rootProcessInstance.getId());
		for (JbpmProcessInstance jpi: piTree) {
			resposta.add(getInstanciaProcesById(jpi.getId()));
		}
		return resposta;
	}
	
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
			 final PaginacioParamsDto paginacioParams) throws EntornNotFoundException, ExpedientTipusNotFoundException, EstatNotFoundException {
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
			public boolean isLastPage() {
				return false;
			}
			
			@Override
			public boolean isFirstPage() {
				return paginacioParams.getPaginaNum() == 0;
			}
			
			@Override
			public boolean hasPreviousPage() {
				return paginacioParams.getPaginaNum() > 0;
			}
			
			@Override
			public boolean hasNextPage() {
				return false;
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
				return new Sort(orders);
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
		};

		PaginaDto<ExpedientConsultaDissenyDto> resposta = paginacioHelper.toPaginaDto(
				paginaResultats,
				ExpedientConsultaDissenyDto.class);
		
		mesuresTemporalsHelper.mesuraCalcular("CONSULTA INFORME EXPEDIENTS v3", "consulta", null, null, "2");
		mesuresTemporalsHelper.mesuraCalcular("CONSULTA INFORME EXPEDIENTS v3", "consulta");
		return resposta;
	}

	@Override
	public boolean isExtensioDocumentPermesa(String nomArxiu) {
		return (new Document()).isExtensioPermesa(getExtension(nomArxiu));
	}

	private String getExtension(String nomArxiu) {
		int index = nomArxiu.lastIndexOf('.');
		if (index == -1) {
			return "";
		} else {
			return nomArxiu.substring(index + 1);
		}
	}

	@Override
	@Transactional
	public DocumentDto generarDocumentAmbPlantillaTasca(
			String taskInstanceId,
			String documentCodi) throws NotFoundException, DocumentGenerarException, DocumentConvertirException {
		JbpmTask task = tascaHelper.getTascaComprovacionsTramitacio(
				taskInstanceId,
				true,
				true);
		DocumentDto document = documentHelper.generarDocumentAmbPlantilla(
				taskInstanceId,
				task.getProcessInstanceId(),
				documentCodi);
		if (document.isAdjuntarAuto()) {
			Long documentStoreId = documentHelper.actualitzarDocument(
					taskInstanceId,
					task.getProcessInstanceId(),
					document.getCodi(),
					null,
					document.getDataDocument(),
					document.getArxiuNom(),
					document.getArxiuContingut(),
					false);
			document.setId(documentStoreId);
		}
		return document;
	}

	@Override
	@Transactional(readOnly = true)
	public DocumentDto generarDocumentAmbPlantillaProces(
			Long expedientId,
			String processInstanceId,
			String documentCodi) throws NotFoundException, DocumentGenerarException, DocumentConvertirException {
		Expedient expedient = expedientHelper.getExpedientComprovantPermisos(
				expedientId,
				true,
				false,
				false,
				false);
		if (processInstanceId == null) {
			return documentHelper.generarDocumentAmbPlantilla(
					null,
					expedient.getProcessInstanceId(),
					documentCodi);
		} else {
			expedientHelper.comprovarInstanciaProces(
					expedient.getId(),
					processInstanceId);
			return documentHelper.generarDocumentAmbPlantilla(
					null,
					processInstanceId,
					documentCodi);
		}
	}

	@Override
	@Transactional(readOnly=true)
	public InstanciaProcesDto getInstanciaProcesById(String processInstanceId) {
		InstanciaProcesDto dto = new InstanciaProcesDto();
		dto.setId(processInstanceId);
		JbpmProcessInstance pi = jbpmHelper.getProcessInstance(processInstanceId);
		if (pi.getProcessInstance() == null)
			return null;
		dto.setInstanciaProcesPareId(pi.getParentProcessInstanceId());
		if (pi.getDescription() != null && pi.getDescription().length() > 0)
			dto.setTitol(pi.getDescription());
		dto.setDefinicioProces(conversioTipusHelper.convertir(definicioProcesRepository.findByJbpmId(pi.getProcessDefinitionId()), DefinicioProcesDto.class));
		return dto;
	}
	
	@Override
	@Transactional(readOnly=true)
	public List<CampDto> getCampsInstanciaProcesById(String processInstanceId) {
		DefinicioProces definicioProces = expedientHelper.findDefinicioProcesByProcessInstanceId(
				processInstanceId);
		List<Camp> camps = new ArrayList<Camp>(definicioProces.getCamps());
		return conversioTipusHelper.convertirList(camps, CampDto.class);
	}

	@Override
	@Transactional(readOnly=true)
	public SortedSet<Entry<InstanciaProcesDto, List<ExpedientLogDto>>> getLogsOrdenatsPerData(
			ExpedientDto expedient,
			boolean detall) {
		mesuresTemporalsHelper.mesuraIniciar("Expedient REGISTRE", "expedient", expedient.getTipus().getNom(), null, "findAmbExpedientIdOrdenatsPerData");
		Map<InstanciaProcesDto, List<ExpedientLogDto>> resposta = new HashMap<InstanciaProcesDto, List<ExpedientLogDto>>();
		List<InstanciaProcesDto> arbre = getArbreInstanciesProces(Long.parseLong(expedient.getProcessInstanceId()));
		List<ExpedientLog> logs = expedientLogRepository.findAmbExpedientIdOrdenatsPerData(expedient.getId());
		List<String> taskIds = new ArrayList<String>();
		String parentProcessInstanceId = null;
		Map<String, String> processos = new HashMap<String, String>();
		for (InstanciaProcesDto ip : arbre) {
			resposta.put(ip, new ArrayList<ExpedientLogDto>());
			for (ExpedientLog log: logs) {
				if (log.getProcessInstanceId().toString().equals(ip.getId())) {
					// Inclourem el log si:
					//    - Estam mostrant el log detallat
					//    - El log no se correspon a una tasca
					//    - Si el log pertany a una tasca i encara
					//      no s'ha afegit cap log d'aquesta tasca 
					if (detall || !log.isTargetTasca() || !taskIds.contains(log.getTargetId())) {
						taskIds.add(log.getTargetId());				
						resposta.get(ip).addAll(
								getLogs(
										processos,
										log,
										parentProcessInstanceId,
										expedient,
										ip.getId(),
										detall));
					}
				}
			}
		}
		SortedSet<Map.Entry<InstanciaProcesDto, List<ExpedientLogDto>>> sortedEntries = new TreeSet<Map.Entry<InstanciaProcesDto, List<ExpedientLogDto>>>(new Comparator<Map.Entry<InstanciaProcesDto, List<ExpedientLogDto>>>() {
			@Override
			public int compare(Map.Entry<InstanciaProcesDto, List<ExpedientLogDto>> e1, Map.Entry<InstanciaProcesDto, List<ExpedientLogDto>> e2) {
				if (e1.getKey() == null || e2.getKey() == null)
					return 0;
				int res = e1.getKey().getId().compareTo(e2.getKey().getId());
				if (e1.getKey().getId().equals(e2.getKey().getId())) {
					return res;
				} else {
					return res != 0 ? res : 1;
				}
			}
		});
		sortedEntries.addAll(resposta.entrySet());
		mesuresTemporalsHelper.mesuraCalcular("Expedient REGISTRE", "expedient", expedient.getTipus().getNom(), null, "obtenir tokens tasca");
		return sortedEntries;
	}	

	public List<ExpedientLogDto> getLogs(Map<String, String> processos, ExpedientLog log, String parentProcessInstanceId, ExpedientDto expedient, String piId, boolean detall) {
		// Obtenim el token de cada registre
		JbpmToken token = null;
		if (log.getJbpmLogId() != null) {
			token = expedientLoggerHelper.getTokenByJbpmLogId(log.getJbpmLogId());
		}
		String tokenName = null;
		String processInstanceId = null;
		List<ExpedientLogDto> resposta = new ArrayList<ExpedientLogDto>();
		if (token != null && token.getToken() != null) {
			tokenName = token.getToken().getFullName();
			processInstanceId = token.getProcessInstanceId();
			
			// Entram per primera vegada
			if (parentProcessInstanceId == null) {
				parentProcessInstanceId = processInstanceId;
				processos.put(processInstanceId, "");
			} else {
				// Canviam de procés
				if (!parentProcessInstanceId.equals(token.getProcessInstanceId())){
					// Entram en un nou subproces
					if (!processos.containsKey(processInstanceId)) {
						processos.put(processInstanceId, token.getToken().getProcessInstance().getSuperProcessToken().getFullName());
						
						if (parentProcessInstanceId.equals(piId)){
							// Añadimos una nueva línea para indicar la llamada al subproceso
							ExpedientLogDto dto = new ExpedientLogDto();
							dto.setId(log.getId());
							dto.setData(log.getData());
							dto.setUsuari(log.getUsuari());
							dto.setEstat(ExpedientLogEstat.IGNORAR.name());
							dto.setAccioTipus(ExpedientLogAccioTipus.PROCES_LLAMAR_SUBPROCES.name());
							String titol = null;
							if (token.getToken().getProcessInstance().getKey() == null)
								titol = token.getToken().getProcessInstance().getProcessDefinition().getName() + " " + log.getProcessInstanceId();
							else 
								titol = token.getToken().getProcessInstance().getKey();
							dto.setAccioParams(titol);
							dto.setTargetId(log.getTargetId());
							dto.setTargetTasca(false);
							dto.setTargetProces(false);
							dto.setTargetExpedient(true);
							if (detall || (!("RETROCEDIT".equals(dto.getEstat()) || "RETROCEDIT_TASQUES".equals(dto.getEstat()) || "EXPEDIENT_MODIFICAR".equals(dto.getAccioTipus()))))
								resposta.add(dto);
						}
					}
				}
				tokenName = processos.get(processInstanceId) + tokenName;
			}
		}
		
		if (piId == null || log.getProcessInstanceId().equals(Long.parseLong(piId))) {
			ExpedientLogDto dto = new ExpedientLogDto();
			dto.setId(log.getId());
			dto.setData(log.getData());
			dto.setUsuari(log.getUsuari());
			dto.setEstat(token == null ? ExpedientLogEstat.IGNORAR.name() : log.getEstat().name());
			dto.setAccioTipus(log.getAccioTipus().name());
			dto.setAccioParams(log.getAccioParams());
			dto.setTargetId(log.getTargetId());
			dto.setTokenName(tokenName);
			dto.setTargetTasca(log.isTargetTasca());
			dto.setTargetProces(log.isTargetProces());
			dto.setTargetExpedient(log.isTargetExpedient());
			if (detall || (!("RETROCEDIT".equals(dto.getEstat()) || "RETROCEDIT_TASQUES".equals(dto.getEstat()) || "EXPEDIENT_MODIFICAR".equals(dto.getAccioTipus()))))
				resposta.add(dto);
		}
		return resposta;
	}

	@Override
	@Transactional(readOnly=true)
	public Map<String, ExpedientTascaDto> getTasquesPerLogExpedient(Long expedientId) {
		logger.debug("Consultant tasques l'expedient (id=" + expedientId + ")");
		Expedient expedient = expedientHelper.getExpedientComprovantPermisos(
				expedientId,
				true,
				false,
				false,
				false);
		List<ExpedientLog> logs = expedientLogRepository.findAmbExpedientIdOrdenatsPerData(expedientId);
		Map<String, ExpedientTascaDto> tasquesPerLogs = new HashMap<String, ExpedientTascaDto>();
		for (ExpedientLog log: logs) {
			if (log.isTargetTasca()) {
				JbpmTask task = jbpmHelper.getTaskById(log.getTargetId());
				if (task != null) {
					tasquesPerLogs.put(
							log.getTargetId(),
							tascaHelper.getExpedientTascaDto(
									task,
									expedient,
									true,
									false));
				}
			}
		}
		return tasquesPerLogs;
	}

	@Override
	@Transactional(readOnly=true)
	public List<TascaDadaDto> findConsultaFiltre(Long consultaId) {
		Consulta consulta = consultaRepository.findById(consultaId);		
		
		List<TascaDadaDto> listTascaDada = consultaHelper.findCampsPerCampsConsulta(
				consulta,
				TipusConsultaCamp.FILTRE);
		
		// Quitamos las variables predefinidas de los filtros
		Iterator<TascaDadaDto> itListTascaDada = listTascaDada.iterator();
		while(itListTascaDada.hasNext()) {
			if (consulta.getMapValorsPredefinits().containsKey(itListTascaDada.next().getVarCodi())) {
				itListTascaDada.remove();
			}
		}
		
		return listTascaDada;
	}

	@Override
	@Transactional(readOnly=true)
	public List<TascaDadaDto> findConsultaInforme(Long consultaId) {
		Consulta consulta = consultaRepository.findById(consultaId);
		return consultaHelper.findCampsPerCampsConsulta(
				consulta,
				TipusConsultaCamp.INFORME);
	}

	@Override
	@Transactional(readOnly=true)
	public List<TascaDadaDto> findConsultaInformeParams(Long consultaId) {
		Consulta consulta = consultaRepository.findById(consultaId);
		return consultaHelper.findCampsPerCampsConsulta(
				consulta,
				TipusConsultaCamp.PARAM);
	}
	
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
		Consulta consulta = consultaRepository.findById(consultaId);		
		
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
				String clau = or.getCamp().replace(ExpedientCamps.EXPEDIENT_PREFIX_JSP, ExpedientCamps.EXPEDIENT_PREFIX);
				if (or.getCamp().contains("dadesExpedient")) {
					sort = clau.replace("/", ".").replace("dadesExpedient.", "").replace(".valorMostrar", "");
				} else {
					sort = clau.replace(".", ExpedientCamps.EXPEDIENT_PREFIX_SEPARATOR);
				}
				break;
			}
			firstRow = paginacioParams.getPaginaNum()*paginacioParams.getPaginaTamany();
			maxResults = paginacioParams.getPaginaTamany();
		}
		List<Long> llistaExpedientIds = new ArrayList<Long>();
		if (ids == null || ids.isEmpty()) {
			llistaExpedientIds = luceneHelper.findIdsAmbDadesExpedientPaginatV3(
					consulta.getEntorn(),
					consulta.getExpedientTipus(),
					campsFiltre,
					campsInforme,
					valors,
					sort,
					asc,
					0,
					-1);
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
			dadesExpedients = luceneHelper.findAmbDadesExpedientPaginatV3(
					consulta.getEntorn().getCodi(),
					llistaExpedientIds,
					campsInforme,
					sort,
					asc,
					firstRow,
					maxResults);
		
		List<ExpedientConsultaDissenyDto> resposta = new ArrayList<ExpedientConsultaDissenyDto>();
		for (Map<String, DadaIndexadaDto> dadesExpedient: dadesExpedients) {
			DadaIndexadaDto dadaExpedientId = dadesExpedient.get(LuceneHelper.CLAU_EXPEDIENT_ID);
			ExpedientConsultaDissenyDto fila = new ExpedientConsultaDissenyDto();
			Expedient expedient = expedientRepository.findOne(Long.parseLong(dadaExpedientId.getValorIndex()));
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
			dadesExpedient.remove(LuceneHelper.CLAU_EXPEDIENT_ID);
		}

		if (paginacioParams == null) {
			Iterator<Map<String, DadaIndexadaDto>> it = dadesExpedients.iterator();
			while (it.hasNext()) {
				Map<String, DadaIndexadaDto> dadesExpedient = it.next();
				DadaIndexadaDto dadaExpedientId = dadesExpedient.get(LuceneHelper.CLAU_EXPEDIENT_ID);
				if (dadaExpedientId != null && !llistaExpedientIds.contains(Long.parseLong(dadaExpedientId.getValorIndex()))) {
					it.remove();
				}
			}
		}
		return resposta;
	}

	@Override
	@Transactional(readOnly=true)
	public List<Long> findIdsPerConsultaInforme(
			Long consultaId,
			Map<String, Object> valors,
			boolean nomesMeves,
			boolean nomesAlertes,
			boolean mostrarAnulats,
			boolean nomesTasquesPersonals,
			boolean nomesTasquesGrup) {
		Consulta consulta = consultaRepository.findById(consultaId);
		
		List<Camp> campsFiltre = consultaHelper.toListCamp(consultaHelper.findCampsPerCampsConsulta(
				consulta,
				TipusConsultaCamp.FILTRE));
		
		afegirValorsPredefinits(consulta, valors, campsFiltre);
		
		List<Long> llistaExpedientIds = luceneHelper.findNomesIds(
				consulta.getEntorn(),
				consulta.getExpedientTipus(),
				campsFiltre,
				valors);
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

		List<String> ids = jbpmHelper.findRootProcessInstancesWithTasksCommand(
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
	@Transactional(readOnly=true)
	public List<ExpedientLogDto> findLogsTascaOrdenatsPerData(Long logId) {
		List<ExpedientLog> logs = expedientLogRepository.findLogsTascaByIdOrdenatsPerData(String.valueOf(logId));
		return conversioTipusHelper.convertirList(logs, ExpedientLogDto.class);
	}

	@Override
	@Transactional(readOnly=true)
	public ExpedientLogDto findLogById(Long logId) {
		return conversioTipusHelper.convertir( expedientLogRepository.findById(logId), ExpedientLogDto.class);
	}

	@Override
	@Transactional
	public void retrocedirFinsLog(Long expedientLogId, boolean retrocedirPerTasques) {
		ExpedientLog log = expedientLogRepository.findById(expedientLogId);
		mesuresTemporalsHelper.mesuraIniciar("Retrocedir" + (retrocedirPerTasques ? " per tasques" : ""), "expedient", log.getExpedient().getTipus().getNom());
		ExpedientLog logRetroces = expedientLoggerHelper.afegirLogExpedientPerExpedient(
				log.getExpedient().getId(),
				retrocedirPerTasques ? ExpedientLogAccioTipus.EXPEDIENT_RETROCEDIR_TASQUES : ExpedientLogAccioTipus.EXPEDIENT_RETROCEDIR,
				expedientLogId.toString());
		expedientLoggerHelper.retrocedirFinsLog(log, retrocedirPerTasques, logRetroces.getId());
		logRetroces.setEstat(ExpedientLogEstat.IGNORAR);
		indexHelper.expedientIndexLuceneUpdate(
				log.getExpedient().getProcessInstanceId());
		mesuresTemporalsHelper.mesuraCalcular("Retrocedir" + (retrocedirPerTasques ? " per tasques" : ""), "expedient", log.getExpedient().getTipus().getNom());
	}

	@Override
	@Transactional(readOnly=true)
	public List<ExpedientLogDto> findLogsRetroceditsOrdenatsPerData(Long logId) {
		List<ExpedientLog> logs = expedientLoggerHelper.findLogsRetrocedits(logId);
		return conversioTipusHelper.convertirList(logs, ExpedientLogDto.class);
	}
	
	@Override
	@Transactional(readOnly=true)
	public boolean existsExpedientAmbEntornTipusITitol(Long entornId, Long expedientTipusId, String titol) {
		return !expedientRepository.findByEntornIdAndTipusIdAndTitol(entornId, expedientTipusId, titol == null, titol).isEmpty();
	}	
	
	@Override
	@Transactional
	public void suspendreTasca(
			Long expedientId,
			Long taskId) {
		JbpmTask task = jbpmHelper.getTaskById(String.valueOf(taskId));
		expedientLoggerHelper.afegirLogExpedientPerProces(
				task.getProcessInstanceId(),
				ExpedientLogAccioTipus.TASCA_SUSPENDRE,
				null);
		jbpmHelper.suspendTaskInstance(String.valueOf(taskId));
		crearRegistreTasca(
				expedientId,
				String.valueOf(taskId),
				SecurityContextHolder.getContext().getAuthentication().getName(),
				Registre.Accio.ATURAR);
	}

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
		Entorn entorn = entornHelper.getEntornComprovantPermisos(
				entornId,
				true,
				false,
				false);
		ExpedientTipus expedientTipus = expedientTipusRepository.findOne(expedientTipusId);
		return this.getNumeroExpedientActual(
				entorn,
				expedientTipus,
				any);
	}

	@Override
	@Transactional(readOnly=true)
	public ExpedientTascaDto getStartTask(
			Long entornId,
			Long expedientTipusId,
			Long definicioProcesId,
			Map<String, Object> valors) {
		ExpedientTipus expedientTipus = expedientTipusRepository.findById(expedientTipusId);
		DefinicioProces definicioProces = null;
		if (definicioProcesId != null) {
			definicioProces = definicioProcesRepository.findById(definicioProcesId);
		}

		if (definicioProces == null){
			definicioProces = definicioProcesRepository.findDarreraVersioAmbEntornIJbpmKey(
					entornId,
					expedientTipus.getJbpmProcessDefinitionKey());
		}
		if (definicioProcesId == null && definicioProces == null) {
			logger.error("No s'ha trobat la definició de procés (entorn=" + entornId + ", jbpmKey=" + expedientTipus.getJbpmProcessDefinitionKey() + ")");
		}
		String startTaskName = jbpmHelper.getStartTaskName(definicioProces.getJbpmId());
		if (startTaskName != null) {
			return tascaHelper.toTascaInicialDto(startTaskName, definicioProces.getJbpmId(), valors);
		}
		return null;
	}
	
	@Override
	@Transactional
	public void reprendreTasca(
			Long expedientId,
			Long taskId) {
		logger.debug("Reprende tasca l'expedient (id=" + expedientId + ")");
		expedientHelper.getExpedientComprovantPermisos(
				expedientId,
				false,
				true,
				false,
				false);
		JbpmTask task = jbpmHelper.getTaskById(String.valueOf(taskId));
		expedientLoggerHelper.afegirLogExpedientPerProces(
				task.getProcessInstanceId(),
				ExpedientLogAccioTipus.TASCA_CONTINUAR,
				null);
		jbpmHelper.resumeTaskInstance(String.valueOf(taskId));
		crearRegistreTasca(
				expedientId,
				String.valueOf(taskId),
				SecurityContextHolder.getContext().getAuthentication().getName(),
				Registre.Accio.REPRENDRE);
	}
	
	@Override
	@Transactional
	public void cancelarTasca(
			Long expedientId,
			Long taskId) {
		logger.debug("Cancelar tasca l'expedient (id=" + expedientId + ")");
		expedientHelper.getExpedientComprovantPermisos(
				expedientId,
				false,
				true,
				false,
				false);
		JbpmTask task = jbpmHelper.getTaskById(String.valueOf(taskId));
		expedientLoggerHelper.afegirLogExpedientPerProces(
				task.getProcessInstanceId(),
				ExpedientLogAccioTipus.TASCA_CANCELAR,
				null);
		jbpmHelper.cancelTaskInstance(String.valueOf(taskId));
		crearRegistreTasca(
				expedientId,
				String.valueOf(taskId),
				SecurityContextHolder.getContext().getAuthentication().getName(),
				Registre.Accio.CANCELAR);
	}

	@Override
	@Transactional
	public void reassignarTasca(String taskId, String expression) {
		String previousActors = expedientLoggerHelper.getActorsPerReassignacioTasca(taskId);
		ExpedientLog expedientLog = expedientLoggerHelper.afegirLogExpedientPerTasca(
				taskId,
				ExpedientLogAccioTipus.TASCA_REASSIGNAR,
				null);
		logger.debug("Reassignar tasca l'expedient (id=" + expedientLog.getExpedient().getId() + ")");
		expedientHelper.getExpedientComprovantPermisos(
				expedientLog.getExpedient().getId(),
				false,
				false,
				false,
				false,
				true,
				false,
				false);
		jbpmHelper.reassignTaskInstance(taskId, expression, expedientLog.getExpedient().getEntorn().getId());
		String currentActors = expedientLoggerHelper.getActorsPerReassignacioTasca(taskId);
		expedientLog.setAccioParams(previousActors + "::" + currentActors);
		String usuari = SecurityContextHolder.getContext().getAuthentication().getName();
		crearRegistreRedirigirTasca(
				expedientLog.getExpedient().getId(),
				taskId,
				usuari,
				expression);
	}

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
		Consulta consulta = consultaRepository.findById(consultaId);
		if (consulta == null) {
			throw new NotFoundException(
					consultaId,
					Consulta.class);
		}
		// Comprova l'accés a l'entorn
		Entorn entorn = entornHelper.getEntornComprovantPermisos(
				consulta.getEntorn().getId(),
				true,
				false,
				false);
		// Comprova l'accés al tipus d'expedient
		ExpedientTipus expedientTipus = expedientTipusHelper.getExpedientTipusComprovantPermisos(
					consulta.getExpedientTipus().getId(),
					true,
					false,
					false);
		final Timer timerTotal = metricRegistry.timer(
				MetricRegistry.name(
						ExpedientService.class,
						"llistatConsulta"));
		final Timer.Context contextTotal = timerTotal.time();
		Counter countTotal = metricRegistry.counter(
				MetricRegistry.name(
						ExpedientService.class,
						"llistatConsulta.count"));
		countTotal.inc();
		final Timer timerEntorn = metricRegistry.timer(
				MetricRegistry.name(
						ExpedientService.class,
						"llistatConsulta",
						entorn.getCodi()));
		final Timer.Context contextEntorn = timerEntorn.time();
		Counter countEntorn = metricRegistry.counter(
				MetricRegistry.name(
						ExpedientService.class,
						"llistatConsulta.count",
						entorn.getCodi()));
		countEntorn.inc();
		final Timer timerTipexp = metricRegistry.timer(
				MetricRegistry.name(
						TascaService.class,
						"llistatConsulta",
						entorn.getCodi(),
						expedientTipus.getCodi()));
		final Timer.Context contextTipexp = timerTipexp.time();
		Counter countTipexp = metricRegistry.counter(
				MetricRegistry.name(
						TascaService.class,
						"llistatConsulta.count",
						entorn.getCodi(),
						expedientTipus.getCodi()));
		countTipexp.inc();
		try {
			// Obte la llista d'expedients permesos
			List<Long> expedientIdsPermesos;
			if (expedientIdsSeleccio != null && !expedientIdsSeleccio.isEmpty()) {
				expedientIdsPermesos = new ArrayList<Long>(expedientIdsSeleccio);
			} else {
				List<Long> tipusPermesosIds = expedientTipusHelper.findIdsAmbPermisRead(entorn);
				Authentication auth = SecurityContextHolder.getContext().getAuthentication();
				ResultatConsultaPaginadaJbpm<Long> expedientsIds = jbpmHelper.expedientFindByFiltre(
						entorn.getId(),
						auth.getName(),
						tipusPermesosIds,
						null,
						null,
						expedientTipus.getId(),
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
						new PaginacioParamsDto(),
						false);
				expedientIdsPermesos = expedientsIds.getLlista();
			}
			// Obte la llista d'expedients de lucene passant els expedients permesos
			// com a paràmetres
			List<Camp> filtreCamps = consultaHelper.toListCamp(
					consultaHelper.findCampsPerCampsConsulta(
					consulta,
					TipusConsultaCamp.FILTRE));
			afegirValorsPredefinits(consulta, filtreValors, filtreCamps);
			List<Camp> informeCamps = consultaHelper.toListCamp(
					consultaHelper.findCampsPerCampsConsulta(
							consulta,
							TipusConsultaCamp.INFORME));
			Object[] respostaLucene = luceneHelper.findPaginatAmbDadesV3(
					entorn,
					expedientTipus,
					expedientIdsPermesos,
					filtreCamps,
					filtreValors,
					informeCamps,
					paginacioParams);
			@SuppressWarnings("unchecked")
			List<Map<String, DadaIndexadaDto>> dadesExpedients = (List<Map<String, DadaIndexadaDto>>)respostaLucene[0];
			Long count = (Long)respostaLucene[1];
			List<ExpedientConsultaDissenyDto> resposta = new ArrayList<ExpedientConsultaDissenyDto>();
			for (Map<String, DadaIndexadaDto> dadesExpedient: dadesExpedients) {
				DadaIndexadaDto dadaExpedientId = dadesExpedient.get(LuceneHelper.CLAU_EXPEDIENT_ID);
				ExpedientConsultaDissenyDto fila = new ExpedientConsultaDissenyDto();
				Expedient expedient = expedientRepository.findOne(Long.parseLong(dadaExpedientId.getValorIndex()));
				if (expedient != null) {
					ExpedientDto expedientDto = expedientHelper.toExpedientDto(expedient);
					expedientHelper.omplirPermisosExpedient(expedientDto);
					fila.setExpedient(expedientDto);
					consultaHelper.revisarDadesExpedientAmbValorsEnumeracionsODominis(
							dadesExpedient,
							informeCamps,
							expedient);
					fila.setDadesExpedient(dadesExpedient);
					resposta.add(fila);
				}
				dadesExpedient.remove(LuceneHelper.CLAU_EXPEDIENT_ID);
			}
			return paginacioHelper.toPaginaDto(
					resposta,
					count.intValue(),
					paginacioParams);
		} finally {
			contextTotal.stop();
			contextEntorn.stop();
			contextTipexp.stop();
		}
	}

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
		Consulta consulta = consultaRepository.findById(consultaId);
		if (consulta == null) {
			throw new NotFoundException(
					consultaId,
					Consulta.class);
		}
		// Comprova l'accés a l'entorn
		Entorn entorn = entornHelper.getEntornComprovantPermisos(
				consulta.getEntorn().getId(),
				true,
				false,
				false);
		// Comprova l'accés al tipus d'expedient
		ExpedientTipus expedientTipus = expedientTipusHelper.getExpedientTipusComprovantPermisos(
					consulta.getExpedientTipus().getId(),
					true,
					false,
					false);
		final Timer timerTotal = metricRegistry.timer(
				MetricRegistry.name(
						ExpedientService.class,
						"llistatConsultaIds"));
		final Timer.Context contextTotal = timerTotal.time();
		Counter countTotal = metricRegistry.counter(
				MetricRegistry.name(
						ExpedientService.class,
						"llistatConsultaIds.count"));
		countTotal.inc();
		final Timer timerEntorn = metricRegistry.timer(
				MetricRegistry.name(
						ExpedientService.class,
						"llistatConsultaIds",
						entorn.getCodi()));
		final Timer.Context contextEntorn = timerEntorn.time();
		Counter countEntorn = metricRegistry.counter(
				MetricRegistry.name(
						ExpedientService.class,
						"llistatConsultaIds.count",
						entorn.getCodi()));
		countEntorn.inc();
		final Timer timerTipexp = metricRegistry.timer(
				MetricRegistry.name(
						TascaService.class,
						"llistatConsultaIds",
						entorn.getCodi(),
						expedientTipus.getCodi()));
		final Timer.Context contextTipexp = timerTipexp.time();
		Counter countTipexp = metricRegistry.counter(
				MetricRegistry.name(
						TascaService.class,
						"llistatConsultaIds.count",
						entorn.getCodi(),
						expedientTipus.getCodi()));
		countTipexp.inc();
		try {
			// Obte la llista d'expedients permesos segons els filtres
			List<Long> tipusPermesosIds = expedientTipusHelper.findIdsAmbPermisRead(entorn);
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			ResultatConsultaPaginadaJbpm<Long> expedientsIds = jbpmHelper.expedientFindByFiltre(
					entorn.getId(),
					auth.getName(),
					tipusPermesosIds,
					null,
					null,
					expedientTipus.getId(),
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
					new PaginacioParamsDto(),
					false);
			// Obte la llista d'ids de lucene passant els expedients permesos
			// com a paràmetres
			List<Camp> filtreCamps = consultaHelper.toListCamp(
					consultaHelper.findCampsPerCampsConsulta(
					consulta,
					TipusConsultaCamp.FILTRE));
			afegirValorsPredefinits(consulta, filtreValors, filtreCamps);
			Object[] respostaLucene = luceneHelper.findPaginatNomesIdsV3(
					entorn,
					expedientTipus,
					expedientsIds.getLlista(),
					filtreCamps,
					filtreValors,
					paginacioParams);
			@SuppressWarnings("unchecked")
			List<Long> ids = (List<Long>)respostaLucene[0];
			Long count = (Long)respostaLucene[1];
			return paginacioHelper.toPaginaDto(
					ids,
					count.intValue(),
					paginacioParams);
		} finally {
			contextTotal.stop();
			contextEntorn.stop();
			contextTipexp.stop();
		}
	}

	@Override
	@Transactional
	public void buidarLogExpedient(String processInstanceId) {
		logger.debug("Buidant logs de l'expedient amb processInstance(id=" + processInstanceId + ")");
		jbpmHelper.deleteProcessInstanceTreeLogs(processInstanceId);
	}

	@Override
	@Transactional
	public void createVariable(Long expedientId, String processInstanceId, String varName, Object value) {
		@SuppressWarnings("unused")
		Expedient expedient = expedientHelper.getExpedientComprovantPermisos(
				expedientId,
				false,
				true,
				false,
				false);
		expedientLoggerHelper.afegirLogExpedientPerProces(
				processInstanceId,
				ExpedientLogAccioTipus.PROCES_VARIABLE_CREAR,
				varName);
		
		optimitzarValorPerConsultesDominiGuardar(processInstanceId, varName, value);
		
		indexHelper.expedientIndexLuceneUpdate(processInstanceId);
		Registre registre = crearRegistreInstanciaProces(
				expedientId,
				processInstanceId,
				SecurityContextHolder.getContext().getAuthentication().getName(),
				Registre.Accio.MODIFICAR);
		registre.setMissatge("Crear variable '" + varName + "'");
		if (value != null)
			registre.setValorNou(value.toString());
	}
	
	@Override
	@Transactional
	public void updateVariable(Long expedientId, String processInstanceId, String varName, Object varValue) {
		@SuppressWarnings("unused")
		Expedient expedient = expedientHelper.getExpedientComprovantPermisos(
				expedientId,
				false,
				true,
				false,
				false);
		expedientLoggerHelper.afegirLogExpedientPerProces(
				processInstanceId,
				ExpedientLogAccioTipus.PROCES_VARIABLE_MODIFICAR,
				varName);
		Object valorVell = variableHelper.getVariableJbpmProcesValor(processInstanceId, varName);
		optimitzarValorPerConsultesDominiGuardar(processInstanceId, varName, varValue);
		
		indexHelper.expedientIndexLuceneUpdate(processInstanceId);
		Registre registre = crearRegistreInstanciaProces(
				expedientId,
				processInstanceId,
				SecurityContextHolder.getContext().getAuthentication().getName(),
				Registre.Accio.MODIFICAR);
		registre.setMissatge("Modificar variable '" + varName + "'");
		if (valorVell != null)
			registre.setValorVell(valorVell.toString());
		if (varValue != null)
			registre.setValorNou(varValue.toString());
	}
	
	private void optimitzarValorPerConsultesDominiGuardar(
			String processInstanceId,
			String varName,
			Object varValue) {
		JbpmProcessDefinition jpd = jbpmHelper.findProcessDefinitionWithProcessInstanceId(processInstanceId);
		DefinicioProces definicioProces = definicioProcesRepository.findByJbpmId(jpd.getId());
		Camp camp = campRepository.findByDefinicioProcesAndCodi(
				definicioProces,
				varName);
		if (camp != null && camp.isDominiCacheText()) {
			if (varValue != null) {
				if (camp.getTipus().equals(TipusCamp.SELECCIO) ||
					camp.getTipus().equals(TipusCamp.SUGGEST)) {
					
					String text = variableHelper.getTextPerCamp(
							camp, 
							varValue, 
							null, 
							processInstanceId);
					
					jbpmHelper.setProcessInstanceVariable(processInstanceId, VariableHelper.PREFIX_VAR_DESCRIPCIO + varName, text);
				}
			}
		}
		jbpmHelper.setProcessInstanceVariable(processInstanceId, varName, varValue);
	}
	
	@Override
	@Transactional
	public void deleteVariable(Long expedientId, String processInstanceId, String varName) {
		@SuppressWarnings("unused")
		Expedient expedient = expedientHelper.getExpedientComprovantPermisos(
				expedientId,
				false,
				true,
				false,
				false);
		expedientLoggerHelper.afegirLogExpedientPerProces(
				processInstanceId,
				ExpedientLogAccioTipus.PROCES_VARIABLE_ESBORRAR,
				varName);
		jbpmHelper.deleteProcessInstanceVariable(processInstanceId, varName);
		indexHelper.expedientIndexLuceneUpdate(processInstanceId);
		Registre registre = crearRegistreInstanciaProces(
				expedientId,
				processInstanceId,
				SecurityContextHolder.getContext().getAuthentication().getName(),
				Registre.Accio.MODIFICAR);
		registre.setMissatge("Esborrar variable '" + varName + "'");
	}
	
	private void verificarFinalitzacioExpedient(Expedient expedient, JbpmProcessInstance pi) {
		if (pi.getEnd() != null) {
			// Actualitzar data de fi de l'expedient
			expedient.setDataFi(pi.getEnd());
			// Finalitzar terminis actius
			for (TerminiIniciat terminiIniciat: terminiIniciatRepository.findByProcessInstanceId(pi.getId())) {
				if (terminiIniciat.getDataInici() != null) {
					terminiIniciat.setDataCancelacio(new Date());
					long[] timerIds = terminiIniciat.getTimerIdsArray();
					for (int i = 0; i < timerIds.length; i++)
						jbpmHelper.suspendTimer(
								timerIds[i],
								new Date(Long.MAX_VALUE));
				}
			}
		}
	}

	private void afegirValorsPredefinits(
			Consulta consulta,
			Map<String, Object> valors,
			List<Camp> campsFiltre) {
		for (Camp camp: campsFiltre) {
			if (consulta.getMapValorsPredefinits().containsKey(camp.getCodi())) {
				valors.put(
						camp.getDefinicioProces()!= null ? camp.getDefinicioProces().getJbpmKey() + "." + camp.getCodi() : camp.getCodi(),
						Camp.getComObject(
								camp.getTipus(),
								consulta.getMapValorsPredefinits().get(camp.getCodi())));
			}
		}
	}

	private Registre crearRegistreRedirigirTasca(
			Long expedientId,
			String tascaId,
			String responsableCodi,
			String expression) {
		Registre registre = new Registre(
				new Date(),
				expedientId,
				responsableCodi,
				Registre.Accio.MODIFICAR,
				Registre.Entitat.TASCA,
				tascaId);
		registre.setMissatge("Redirecció de tasca amb expressió \"" + expression + "\"");
		return registreRepository.save(registre);
	}
	
	public boolean isUserInRole(
			Authentication auth,
			String role) {
		for (GrantedAuthority ga: auth.getAuthorities()) {
			if (role.equals(ga.getAuthority()))
				return true;
		}
		return false;
	}

	private String getNumeroExpedientActual(
			Entorn entorn,
			ExpedientTipus expedientTipus,
			Integer any) {
		long increment = 0;
		String numero = null;
		//ExpedientTipusDto expedientTipusDto = conversioTipusHelper.convertir(expedientTipus, ExpedientTipusDto.class);
		Expedient expedient = null;
		if (any == null) 
			any = Calendar.getInstance().get(Calendar.YEAR);
		do {
			numero = expedientHelper.getNumeroExpedientActual(
					expedientTipus,
					any.intValue(),
					increment);
			expedient = expedientRepository.findByEntornIdAndTipusIdAndNumero(
					entorn.getId(),
					expedientTipus.getId(),
					numero);
			increment++;
		} while (expedient != null);
		/*if (increment > 1) {
			expedientTipusDto.updateSequencia(any, increment - 1);
		}*/
		return numero;
	}
	
	private String getNumeroExpedientDefaultActual(
			Entorn entorn,
			ExpedientTipus expedientTipus,
			Integer any) {
		long increment = 0;
		String numero = null;
		Expedient expedient = null;
		if (any == null) 
			any = Calendar.getInstance().get(Calendar.YEAR);
		do {
			numero = expedientHelper.getNumeroExpedientDefaultActual(
					expedientTipus,
					any.intValue(),
					increment);
			expedient = expedientRepository.findByEntornIdAndTipusIdAndNumero(
					entorn.getId(),
					expedientTipus.getId(),
					numero);
			increment++;
		} while (expedient != null);
		if (increment > 1) {
			expedientTipus.updateSequenciaDefault(any, increment - 1);
		}
		return numero;
	}

	@Override
	@Transactional(readOnly = true)
	public boolean existsExpedientAmbEntornTipusINumero(Long entornId, Long expedientTipusId, String numero) {
		return expedientRepository.findByEntornIdAndTipusIdAndNumero(
				entornId,
				expedientTipusId,
				numero) != null;
	}
	
	@Override
	@Transactional(readOnly = true)
	public Long findDocumentStorePerInstanciaProcesAndDocumentCodi(String processInstanceId, String documentCodi) {
		return documentHelper.findDocumentStorePerInstanciaProcesAndDocumentCodi(processInstanceId, documentCodi);
	}
	
	private PersonaDto comprovarUsuari(String usuari) {
		try {
			PersonaDto persona = pluginHelper.findPersonaAmbCodi(usuari);
			if (persona == null) {
				throw new NotFoundException(
						usuari,
						PersonaDto.class);
			}
			return persona;
		} catch (Exception ex) {
			logger.error("No s'ha pogut comprovar l'usuari (codi=" + usuari + ")");
			throw new NotFoundException(
					usuari,
					PersonaDto.class);
		}
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<PortasignaturesDto> findDocumentsPendentsPortasignatures(String processInstanceId) {
		List<PortasignaturesDto> resposta = new ArrayList<PortasignaturesDto>();
		List<Portasignatures> pendents = pluginHelper.findPendentsPortasignaturesPerProcessInstanceId(processInstanceId);
		for (Portasignatures pendent: pendents) {
			PortasignaturesDto dto = new PortasignaturesDto();
			dto.setId(pendent.getId());
			dto.setDocumentId(pendent.getDocumentId());
			dto.setTokenId(pendent.getTokenId());
			dto.setDataEnviat(pendent.getDataEnviat());
			if (TipusEstat.ERROR.equals(pendent.getEstat())) {
				if (Transicio.SIGNAT.equals(pendent.getTransition()))
					dto.setEstat(TipusEstat.SIGNAT.toString());
				else
					dto.setEstat(TipusEstat.REBUTJAT.toString());
				dto.setError(true);
			} else {
				dto.setEstat(pendent.getEstat().toString());
				dto.setError(false);
			}
			if (pendent.getTransition() != null)
				dto.setTransicio(pendent.getTransition().toString());
			dto.setDocumentStoreId(pendent.getDocumentStoreId());
			dto.setMotiuRebuig(pendent.getMotiuRebuig());
			dto.setTransicioOK(pendent.getTransicioOK());
			dto.setTransicioKO(pendent.getTransicioKO());
			dto.setDataProcessamentPrimer(pendent.getDataProcessamentPrimer());
			dto.setDataProcessamentDarrer(pendent.getDataProcessamentDarrer());
			dto.setDataSignatRebutjat(pendent.getDataSignatRebutjat());
			dto.setDataCustodiaIntent(pendent.getDataCustodiaIntent());
			dto.setDataCustodiaOk(pendent.getDataCustodiaOk());
			dto.setDataSignalIntent(pendent.getDataSignalIntent());
			dto.setDataSignalOk(pendent.getDataSignalOk());
			dto.setErrorProcessant(pendent.getErrorCallbackProcessant());
			dto.setProcessInstanceId(pendent.getProcessInstanceId());
			resposta.add(dto);
		}
		return resposta;
	}

	private static final Logger logger = LoggerFactory.getLogger(ExpedientServiceImpl.class);

}
