/**
 * 
 */
package net.conselldemallorca.helium.v3.core.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.annotation.Resource;

import org.jbpm.graph.exe.ProcessInstanceExpedient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

import net.conselldemallorca.helium.core.common.ExpedientCamps;
import net.conselldemallorca.helium.core.common.ExpedientIniciantDto;
import net.conselldemallorca.helium.core.common.JbpmVars;
import net.conselldemallorca.helium.core.helper.ConsultaHelper;
import net.conselldemallorca.helium.core.helper.ConversioTipusHelper;
import net.conselldemallorca.helium.core.helper.DocumentHelperV3;
import net.conselldemallorca.helium.core.helper.EntornHelper;
import net.conselldemallorca.helium.core.helper.ExpedientHelper;
import net.conselldemallorca.helium.core.helper.ExpedientLoggerHelper;
import net.conselldemallorca.helium.core.helper.ExpedientRegistreHelper;
import net.conselldemallorca.helium.core.helper.ExpedientTipusHelper;
import net.conselldemallorca.helium.core.helper.IndexHelper;
import net.conselldemallorca.helium.core.helper.MessageHelper;
import net.conselldemallorca.helium.core.helper.PaginacioHelper;
import net.conselldemallorca.helium.core.helper.PermisosHelper;
import net.conselldemallorca.helium.core.helper.PermisosHelper.ObjectIdentifierExtractor;
import net.conselldemallorca.helium.core.helper.PluginHelper;
import net.conselldemallorca.helium.core.helper.TascaHelper;
import net.conselldemallorca.helium.core.helper.VariableHelper;
import net.conselldemallorca.helium.core.helperv26.LuceneHelper;
import net.conselldemallorca.helium.core.helperv26.MesuresTemporalsHelper;
import net.conselldemallorca.helium.core.model.hibernate.Accio;
import net.conselldemallorca.helium.core.model.hibernate.Alerta;
import net.conselldemallorca.helium.core.model.hibernate.Camp;
import net.conselldemallorca.helium.core.model.hibernate.Consulta;
import net.conselldemallorca.helium.core.model.hibernate.ConsultaCamp.TipusConsultaCamp;
import net.conselldemallorca.helium.core.model.hibernate.DefinicioProces;
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
import net.conselldemallorca.helium.core.model.hibernate.ExpedientTipus;
import net.conselldemallorca.helium.core.model.hibernate.Portasignatures;
import net.conselldemallorca.helium.core.model.hibernate.Portasignatures.TipusEstat;
import net.conselldemallorca.helium.core.model.hibernate.Registre;
import net.conselldemallorca.helium.core.model.hibernate.Termini;
import net.conselldemallorca.helium.core.model.hibernate.TerminiIniciat;
import net.conselldemallorca.helium.core.security.ExtendedPermission;
import net.conselldemallorca.helium.jbpm3.handlers.exception.ValidationException;
import net.conselldemallorca.helium.jbpm3.integracio.ExecucioHandlerException;
import net.conselldemallorca.helium.jbpm3.integracio.JbpmHelper;
import net.conselldemallorca.helium.jbpm3.integracio.JbpmProcessInstance;
import net.conselldemallorca.helium.jbpm3.integracio.JbpmTask;
import net.conselldemallorca.helium.jbpm3.integracio.ResultatConsultaPaginadaJbpm;
import net.conselldemallorca.helium.v3.core.api.dto.AccioDto;
import net.conselldemallorca.helium.v3.core.api.dto.AlertaDto;
import net.conselldemallorca.helium.v3.core.api.dto.ArxiuDto;
import net.conselldemallorca.helium.v3.core.api.dto.CampDto;
import net.conselldemallorca.helium.v3.core.api.dto.DadaIndexadaDto;
import net.conselldemallorca.helium.v3.core.api.dto.DadesDocumentDto;
import net.conselldemallorca.helium.v3.core.api.dto.DefinicioProcesDto;
import net.conselldemallorca.helium.v3.core.api.dto.DefinicioProcesExpedientDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientConsultaDissenyDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientDto.EstatTipusDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientDto.IniciadorTipusDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientErrorDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientErrorDto.ErrorTipusDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientTascaDto;
import net.conselldemallorca.helium.v3.core.api.dto.InstanciaProcesDto;
import net.conselldemallorca.helium.v3.core.api.dto.MostrarAnulatsDto;
import net.conselldemallorca.helium.v3.core.api.dto.PaginaDto;
import net.conselldemallorca.helium.v3.core.api.dto.PaginacioParamsDto;
import net.conselldemallorca.helium.v3.core.api.dto.PaginacioParamsDto.OrdreDireccioDto;
import net.conselldemallorca.helium.v3.core.api.dto.PaginacioParamsDto.OrdreDto;
import net.conselldemallorca.helium.v3.core.api.dto.PersonaDto;
import net.conselldemallorca.helium.v3.core.api.dto.RespostaValidacioSignaturaDto;
import net.conselldemallorca.helium.v3.core.api.dto.TascaDadaDto;
import net.conselldemallorca.helium.v3.core.api.exception.NoTrobatException;
import net.conselldemallorca.helium.v3.core.api.exception.PermisDenegatException;
import net.conselldemallorca.helium.v3.core.api.exception.TramitacioException;
import net.conselldemallorca.helium.v3.core.api.exception.TramitacioHandlerException;
import net.conselldemallorca.helium.v3.core.api.exception.TramitacioValidacioException;
import net.conselldemallorca.helium.v3.core.api.exception.ValidacioException;
import net.conselldemallorca.helium.v3.core.api.service.ExpedientService;
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


/**
 * Implementació dels mètodes del servei ExpedientService.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Service("expedientServiceV3")
public class ExpedientServiceImpl implements ExpedientService {

	private String textBloqueigIniciExpedient;

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
	private LuceneHelper luceneHelper;
//	@Resource
//	private MongoDBHelper mongoDBHelper;
	@Resource(name="permisosHelperV3")
	private PermisosHelper permisosHelper;
	@Resource
	private PaginacioHelper paginacioHelper;
	@Resource
	private MesuresTemporalsHelper mesuresTemporalsHelper;
	@Resource
	private ExpedientLoggerHelper expedientLoggerHelper;
	@Resource
	private IndexHelper indexHelper;
//	@Resource
//	private MetricRegistry metricRegistry;



	/**
	 * {@inheritDoc}
	 */
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
				true);
		if (usuari != null)
			comprovarUsuari(usuari);
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		
		String usuariBo = (usuari != null) ? usuari : auth.getName();
		ExpedientTipus expedientTipus = expedientTipusRepository.findById(expedientTipusId);
		if (expedientTipus == null)
			throw new NoTrobatException(ExpedientTipus.class, expedientTipusId);
		
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
		
		Expedient expedient = new Expedient();
		try {
			
			String iniciadorCodiCalculat = (iniciadorTipus.equals(IniciadorTipusDto.INTERN)) ? usuariBo : iniciadorCodi;
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
			expedient.setAmbRetroaccio(expedientTipus.isAmbRetroaccio());
			
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
				throw new ValidacioException(
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
			expedientRepository.saveAndFlush(expedient);

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

			// Verificar la ultima vegada que l'expedient va modificar el seu estat
			mesuresTemporalsHelper.mesuraIniciar("Iniciar", "expedient", expedientTipus.getNom(), null, "Afegir log");
		
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

			ExpedientIniciantDto.setExpedient(null);
			logger.debug("textBloqueigIniciExpedient: " + textBloqueigIniciExpedient);
			return dto;
		} catch (ExecucioHandlerException ex) {
			throw new TramitacioHandlerException(
					(expedient != null) ? expedient.getEntorn().getId() : null, 
					(expedient != null) ? expedient.getEntorn().getCodi() : null,
					(expedient != null) ? expedient.getEntorn().getNom() : null, 
					(expedient != null) ? expedient.getId() : null, 
					(expedient != null) ? expedient.getTitol() : null,
					(expedient != null) ? expedient.getNumero() : null,
					(expedient != null) ? expedient.getTipus().getId() : null, 
					(expedient != null) ? expedient.getTipus().getCodi() : null,
					(expedient != null) ? expedient.getTipus().getNom() : null,
					ex.getProcessInstanceId(),
					ex.getTaskInstanceId(),
					ex.getTokenId(),
					ex.getClassName(),
					ex.getMethodName(),
					ex.getFileName(),
					ex.getLineNumber(),
					"", 
					ex.getCause());
		} catch (ValidationException ex) {
			throw new TramitacioValidacioException("Error de validació en Handler", ex);
		} finally {
			textBloqueigIniciExpedient = null;
		}
	}

	/**
	 * {@inheritDoc}
	 */
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
		
		expedientHelper.update(
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
	public boolean luceneReindexarExpedient(Long expedientId) {
		Expedient expedient = expedientHelper.getExpedientComprovantPermisos(
				expedientId,
				false,
				true,
				false,
				false);
		
		indexHelper.expedientIndexLuceneRecrear(expedient);
		return true;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly = true)
	public List<RespostaValidacioSignaturaDto> verificarSignatura(Long documentStoreId) {
		DocumentStore documentStore = documentStoreRepository.findById(documentStoreId);
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
		
		List<JbpmProcessInstance> processInstancesTree = jbpmHelper.getProcessInstanceTree(expedient.getProcessInstanceId());
		for (JbpmProcessInstance pi: processInstancesTree){
			for (TerminiIniciat ti: terminiIniciatRepository.findByProcessInstanceId(pi.getId()))
				terminiIniciatRepository.delete(ti);
			
			jbpmHelper.deleteProcessInstance(pi.getId());
			
			for (DocumentStore documentStore: documentStoreRepository.findByProcessInstanceId(pi.getId())) {
				if (documentStore.isSignat()) {
					try {
						pluginHelper.custodiaEsborrarSignatures(documentStore.getReferenciaCustodia(), expedient);
					} catch (Exception ignored) {}
				}
				if (documentStore.getFont().equals(DocumentFont.ALFRESCO))
					pluginHelper.gestioDocumentalDeleteDocument(
							documentStore.getReferenciaFont(), expedient);
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

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly = true)
	public Long findIdAmbProcessInstanceId(String processInstanceId) {
		return expedientRepository.findIdByProcessInstanceId(processInstanceId);
	}
	
	@Override
	@Transactional(readOnly = true)
	public ExpedientDto findAmbId(Long id) {
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

	/**
	 * {@inheritDoc}
	 */
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

	/**
	 * {@inheritDoc}
	 */
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
				true);
		// Comprova l'accés al tipus d'expedient
		ExpedientTipus expedientTipus = null;
		if (expedientTipusId != null) {
			expedientTipus = expedientTipusHelper.getExpedientTipusComprovantPermisos(
					expedientTipusId,
					true);
		}
		// Comprova l'accés a l'estat
		Estat estat = null;
		if (estatId != null) {
			estat = estatRepository.findByExpedientTipusAndId(expedientTipus, estatId);
			if (estat == null) {
				logger.debug("No s'ha trobat l'estat (expedientTipusId=" + expedientTipusId + ", estatId=" + estatId + ")");
				throw new NoTrobatException(Estat.class,estatId);
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
		return paginacioHelper.toPaginaDto(
				expedients,
				expedientsIds.getCount(),
				paginacioParams);
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
				true);
		// Comprova l'accés al tipus d'expedient
		ExpedientTipus expedientTipus = null;
		if (expedientTipusId != null) {
			expedientTipus = expedientTipusHelper.getExpedientTipusComprovantPermisos(
					expedientTipusId,
					true);
		}
		// Comprova l'accés a l'estat
		Estat estat = null;
		if (estatId != null) {
			estat = estatRepository.findByExpedientTipusAndId(expedientTipus, estatId);
			if (estat == null) {
				logger.debug("No s'ha trobat l'estat (expedientTipusId=" + expedientTipusId + ", estatId=" + estatId + ")");
				throw new NoTrobatException(Estat.class, estatId);
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
	}

	/**
	 * {@inheritDoc}
	 */
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

	/**
	 * {@inheritDoc}
	 */
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
				jbpmHelper.getResourceBytes(
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
		
		DocumentStore documentStore = documentStoreRepository.findById(documentStoreId);
		if (documentStore != null && documentStore.isSignat()) {
				pluginHelper.custodiaEsborrarSignatures(
						documentStore.getReferenciaCustodia(),
						expedient);
			String jbpmVariable = documentStore.getJbpmVariable();
			documentStore.setReferenciaCustodia(null);
			documentStore.setSignat(false);
			expedientRegistreHelper.crearRegistreEsborrarSignatura(
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
		List<JbpmProcessInstance> processInstancesTree = jbpmHelper.getProcessInstanceTree(
				expedient.getProcessInstanceId());
		String[] ids = new String[processInstancesTree.size()];
		int i = 0;
		for (JbpmProcessInstance pi: processInstancesTree)
			ids[i++] = pi.getId();
		jbpmHelper.resumeProcessInstances(ids);
		expedient.setAnulat(false);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	public void desfinalitzar(Long id) {
		logger.debug("Reprenent l'expedient (id=" + id + ")");
		Expedient expedient = expedientHelper.getExpedientComprovantPermisos(
				id,
				new Permission[] {
						ExtendedPermission.UNDO_END,
						ExtendedPermission.ADMINISTRATION});
		ExpedientLog expedientLog = expedientLoggerHelper.afegirLogExpedientPerExpedient(
				expedient.getId(),
				ExpedientLogAccioTipus.EXPEDIENT_DESFINALITZAR,
				null);
		expedientLog.setEstat(ExpedientLogEstat.IGNORAR);
		logger.debug("Desfer finalització de l'expedient (id=" + id + ")");
		jbpmHelper.desfinalitzarExpedient(expedient.getProcessInstanceId());
		expedient.setDataFi(null);
		expedientRegistreHelper.crearRegistreReprendreExpedient(
				expedient.getId(),
				(expedient.getResponsableCodi() != null) ? expedient.getResponsableCodi() : SecurityContextHolder.getContext().getAuthentication().getName());
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
			list.add(findAmbId(relacionat.getId()));
		}
		return list;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	public void procesScriptExec(
			Long expedientId,
			String processInstanceId,
			String script) {
		logger.debug("Executa script sobre l'expedient (" +
				"expedientId=" + expedientId + ", " +
				"processInstanceId=" + processInstanceId + ", " +
				"script=" + script + ")");
		Expedient expedient = expedientHelper.getExpedientComprovantPermisos(
				expedientId,
				new Permission[] {
						ExtendedPermission.SCRIPT_EXE,
						ExtendedPermission.ADMINISTRATION});
		expedientHelper.comprovarInstanciaProces(expedient, processInstanceId);
		if (MesuresTemporalsHelper.isActiu()) {
			mesuresTemporalsHelper.mesuraIniciar("Executar SCRIPT", "expedient", expedient.getTipus().getNom());
		}
		jbpmHelper.evaluateScript(processInstanceId, script, new HashSet<String>());
		verificarFinalitzacioExpedient(expedient, processInstanceId);
		indexHelper.expedientIndexLuceneUpdate(processInstanceId);
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
		ProcessInstanceExpedient piexp = jbpmHelper.expedientFindByProcessInstanceId(processInstanceId);
		expedientHelper.getExpedientComprovantPermisos(
				piexp.getId(),
				new Permission[] {
						ExtendedPermission.DEFPROC_UPDATE,
						ExtendedPermission.ADMINISTRATION});
		DefinicioProces defprocAntiga = expedientHelper.findDefinicioProcesByProcessInstanceId(processInstanceId);
		if (defprocAntiga == null)
			throw new NoTrobatException(DefinicioProces.class, processInstanceId);
		jbpmHelper.changeProcessInstanceVersion(processInstanceId, versio);
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
			jbpmHelper.deleteProcessInstanceTreeLogs(expedient.getProcessInstanceId());
		}
		if (definicioProcesId != null) {
			DefinicioProces defprocAntiga = expedientHelper.findDefinicioProcesByProcessInstanceId(expedient.getProcessInstanceId());
			DefinicioProces defprocNova = definicioProcesRepository.findById(definicioProcesId);
			if (!defprocAntiga.equals(defprocNova)) {
				jbpmHelper.changeProcessInstanceVersion(expedient.getProcessInstanceId(), defprocNova.getVersio());
				updateTerminis(expedient.getProcessInstanceId(), defprocAntiga, defprocNova);
			}
		}
		// Subprocessos
		if (subProcesIds != null && subProcesIds.length > 0) {
			// Arriben amb el mateix ordre??
			List<JbpmProcessInstance> instanciesProces = jbpmHelper.getProcessInstanceTree(expedient.getProcessInstanceId());
			for (JbpmProcessInstance instanciaProces: instanciesProces) {
				DefinicioProces defprocAntiga = expedientHelper.findDefinicioProcesByProcessInstanceId(instanciaProces.getId());
				int versio = findVersioDefProcesActualitzar(subDefinicioProces, subProcesIds, instanciaProces.getProcessInstance().getProcessDefinition().getName());
				if (versio != -1 && versio != defprocAntiga.getVersio()) {
					jbpmHelper.changeProcessInstanceVersion(instanciaProces.getId(), versio);
					DefinicioProces defprocNova =  expedientHelper.findDefinicioProcesByProcessInstanceId(instanciaProces.getId());
					updateTerminis(instanciaProces.getId(), defprocAntiga, defprocNova);
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
			accions = accioRepository.findAmbExpedientTipusAndOcultaFalse(expedient.getTipus());
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
				accioRepository.findOne(accioId),
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
		Expedient expedient = expedientHelper.getExpedientComprovantPermisos(
				expedientId,
				new Permission[] {
						ExtendedPermission.TERM_MANAGE,
						ExtendedPermission.ADMINISTRATION});
		expedientHelper.comprovarInstanciaProces(
				expedient,
				processInstanceId);
		Accio accio = accioRepository.findOne(accioId);
		if (accio == null)
			throw new NoTrobatException(Accio.class, accioId);
		if (permetreExecutarAccioExpedient(accio, expedient)) {
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
						"Error al executa l'acció '" + accio.getCodi() + "'", 
						ex);
			}
			verificarFinalitzacioExpedient(expedient, processInstanceId);
			indexHelper.expedientIndexLuceneUpdate(processInstanceId);
			mesuresTemporalsHelper.mesuraCalcular("Executar ACCIO" + accio.getNom(), "expedient", expedient.getTipus().getNom());
		} else {
			throw new PermisDenegatException(
					expedientId, 
					Expedient.class, 
					new Permission[]{ExtendedPermission.WRITE},
					null);
		}
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
		// Convertir a AlertaDto
		return conversioTipusHelper.convertirList(alertes, AlertaDto.class);
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
		
		if (expedient.isReindexarError()) {
			errors_bas.add(new ExpedientErrorDto(ErrorTipusDto.BASIC, 
					messageHelper.getMessage("expedient.consulta.reindexacio.error"),
					messageHelper.getMessage("expedient.consulta.reindexacio.error.full")));
		}
		
		return new Object[]{errors_bas,errors_int};
	}

	/**
	 * {@inheritDoc}
	 */
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

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly = true)
	public List<InstanciaProcesDto> getArbreInstanciesProces(
				Long processInstanceId) {
		return expedientHelper.getArbreInstanciesProces(String.valueOf(processInstanceId));
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

	/**
	 * {@inheritDoc}
	 */
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

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly=true)
	public List<CampDto> getCampsInstanciaProcesById(
			Long expedientTipusId,
			String processInstanceId) {
		ExpedientTipus expedientTipus = expedientTipusRepository.findOne(expedientTipusId);
		DefinicioProces definicioProces = expedientHelper.findDefinicioProcesByProcessInstanceId(
				processInstanceId);

		List<Camp> camps;
		if (expedientTipus.isAmbInfoPropia()) {
			camps = campRepository.findByExpedientTipusOrderByCodiAsc(expedientTipus);
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

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly=true)
	public List<TascaDadaDto> findConsultaInforme(Long consultaId) {
		Consulta consulta = consultaRepository.findById(consultaId);
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
		Consulta consulta = consultaRepository.findById(consultaId);
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
				String clau = or.getCamp().replace(
						ExpedientCamps.EXPEDIENT_PREFIX_JSP,
						ExpedientCamps.EXPEDIENT_PREFIX);
				if (or.getCamp().contains("dadesExpedient")) {
					sort = clau.replace("/", ".").replace("dadesExpedient.", "").replace(".valorMostrar", "");
				} else {
					sort = clau.replace(
							".",
							ExpedientCamps.EXPEDIENT_PREFIX_SEPARATOR);
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

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly=true)
	public boolean existsExpedientAmbEntornTipusITitol(Long entornId, Long expedientTipusId, String titol) {
		return !expedientRepository.findByEntornIdAndTipusIdAndTitol(entornId, expedientTipusId, titol == null, titol).isEmpty();
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
		Entorn entorn = entornHelper.getEntornComprovantPermisos(
				entornId,
				true);
		ExpedientTipus expedientTipus = expedientTipusRepository.findOne(expedientTipusId);
		return this.getNumeroExpedientActual(
				entorn,
				expedientTipus,
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
//		
//		// Mètriques - Timers
//		Timer.Context contextConsultaLuceneTotal = null;
//		Timer.Context contextConsultaMongoTotal = null;
//		
//		final Timer timerConsultaLuceneTotal = metricRegistry.timer(MetricRegistry.name(LuceneHelper.class, "consulta.lucene"));
//		final Timer timerConsultaMongoTotal = metricRegistry.timer(MetricRegistry.name(LuceneHelper.class, "consulta.mongoDB"));
//		
//		Counter countTotal = metricRegistry.counter(MetricRegistry.name(LuceneHelper.class, "consulta.count"));
//		countTotal.inc();
//		
		// Comprova l'accés a la consulta
		Consulta consulta = consultaRepository.findById(consultaId);
		if (consulta == null) {
			throw new NoTrobatException(Consulta.class,consultaId);
		}
		// Comprova l'accés a l'entorn
		Entorn entorn = entornHelper.getEntornComprovantPermisos(
				consulta.getEntorn().getId(),
				true);
		// Comprova l'accés al tipus d'expedient
		ExpedientTipus expedientTipus = expedientTipusHelper.getExpedientTipusComprovantPermisos(
					consulta.getExpedientTipus().getId(),
					true);
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

		Object[] respostaLucene = null;
		
//		boolean ctxLuceneStoped = false;
//		try {
//			contextConsultaLuceneTotal = timerConsultaLuceneTotal.time();
			
			respostaLucene = luceneHelper.findPaginatAmbDadesV3(
					entorn,
					expedientTipus,
					expedientIdsPermesos,
					filtreCamps,
					filtreValors,
					informeCamps,
					paginacioParams);
			
//			contextConsultaLuceneTotal.stop();
//			ctxLuceneStoped = true;
//			contextConsultaMongoTotal = timerConsultaMongoTotal.time();
//			
//			mongoDBHelper.findPaginatAmbDadesV3(
//					expedientIdsPermesos, 
//					filtreCamps, 
//					filtreValors, 
//					informeCamps, 
//					paginacioParams);
//		} finally {
//			if (!ctxLuceneStoped) {
//				contextConsultaLuceneTotal.stop();
//			}
//			contextConsultaMongoTotal.stop();
//		}
		
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
			throw new NoTrobatException(Consulta.class,consultaId);
		}
		// Comprova l'accés a l'entorn
		Entorn entorn = entornHelper.getEntornComprovantPermisos(
				consulta.getEntorn().getId(),
				true);
		// Comprova l'accés al tipus d'expedient
		ExpedientTipus expedientTipus = expedientTipusHelper.getExpedientTipusComprovantPermisos(
					consulta.getExpedientTipus().getId(),
					true);
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



	private String getNumeroExpedientActual(
			Entorn entorn,
			ExpedientTipus expedientTipus,
			Integer any) {
		long increment = 0;
		String numero = null;
		
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

	private PersonaDto comprovarUsuari(String usuari) {
		try {
			PersonaDto persona = pluginHelper.personaFindAmbCodi(usuari);
			return persona;
		} catch (Exception ex) {
			logger.error("No s'ha pogut comprovar l'usuari (codi=" + usuari + ")");
			throw new NoTrobatException(PersonaDto.class,usuari);
		}
	}

	private boolean permetreExecutarAccioExpedient(
			Accio accio,
			Expedient expedient) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		boolean permesa = true;
		if (!accio.isPublica()) {
			permesa = permisosHelper.isGrantedAny(
					expedient.getTipus().getId(),
					ExpedientTipus.class,
					new Permission[] {
							ExtendedPermission.WRITE,
							ExtendedPermission.ADMINISTRATION},
					auth);
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

	private void verificarFinalitzacioExpedient(
			Expedient expedient,
			String processInstanceId) {
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

	private boolean isUserInRole(
			Authentication auth,
			String role) {
		for (GrantedAuthority ga: auth.getAuthorities()) {
			if (role.equals(ga.getAuthority()))
				return true;
		}
		return false;
	}

	private String getVarNameFromDocumentStore(DocumentStore documentStore) {
		String jbpmVariable = documentStore.getJbpmVariable();
		if (documentStore.isAdjunt())
			return jbpmVariable.substring(
					JbpmVars.PREFIX_ADJUNT.length());
		else
			return jbpmVariable.substring(
					JbpmVars.PREFIX_DOCUMENT.length());
	}

	private int findVersioDefProcesActualitzar(List<DefinicioProcesExpedientDto> definicionsProces, Long[] definicionsProcesId, String key) {
		int versio = -1;
		int i = 0;
		while (i < definicionsProces.size() && !definicionsProces.get(i).getJbpmKey().equals(key)) 
			i++;
		if (i < definicionsProces.size() && definicionsProcesId[i] != null) {
			DefinicioProces definicioProces = definicioProcesRepository.findById(definicionsProcesId[i]);
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

	private static final Logger logger = LoggerFactory.getLogger(ExpedientServiceImpl.class);

}
