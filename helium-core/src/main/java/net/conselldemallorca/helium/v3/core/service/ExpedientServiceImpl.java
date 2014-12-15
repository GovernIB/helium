/**
 * 
 */
package net.conselldemallorca.helium.v3.core.service;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.annotation.Resource;

import net.conselldemallorca.helium.core.model.dao.PluginCustodiaDao;
import net.conselldemallorca.helium.core.model.dao.PluginGestioDocumentalDao;
import net.conselldemallorca.helium.core.model.dao.RegistreDao;
import net.conselldemallorca.helium.core.model.exception.ExpedientRepetitException;
import net.conselldemallorca.helium.core.model.hibernate.Accio;
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
import net.conselldemallorca.helium.core.model.hibernate.Registre;
import net.conselldemallorca.helium.core.model.hibernate.Termini;
import net.conselldemallorca.helium.core.model.hibernate.TerminiIniciat;
import net.conselldemallorca.helium.core.model.service.DocumentHelper;
import net.conselldemallorca.helium.core.model.service.LuceneHelper;
import net.conselldemallorca.helium.core.model.service.MesuresTemporalsHelper;
import net.conselldemallorca.helium.core.security.ExtendedPermission;
import net.conselldemallorca.helium.core.util.GlobalProperties;
import net.conselldemallorca.helium.core.util.OpenOfficeUtils;
import net.conselldemallorca.helium.jbpm3.integracio.JbpmHelper;
import net.conselldemallorca.helium.jbpm3.integracio.JbpmProcessInstance;
import net.conselldemallorca.helium.jbpm3.integracio.JbpmTask;
import net.conselldemallorca.helium.jbpm3.integracio.JbpmToken;
import net.conselldemallorca.helium.v3.core.api.dto.AccioDto;
import net.conselldemallorca.helium.v3.core.api.dto.ArxiuDto;
import net.conselldemallorca.helium.v3.core.api.dto.CampAgrupacioDto;
import net.conselldemallorca.helium.v3.core.api.dto.CampDto;
import net.conselldemallorca.helium.v3.core.api.dto.DadaIndexadaDto;
import net.conselldemallorca.helium.v3.core.api.dto.DadesDocumentDto;
import net.conselldemallorca.helium.v3.core.api.dto.DocumentDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientCamps;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientConsultaDissenyDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientDadaDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientDocumentDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientDto.EstatTipusDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientDto.IniciadorTipusDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientIniciantDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientLogDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientTascaDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientTerminiDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientTipusDto;
import net.conselldemallorca.helium.v3.core.api.dto.InstanciaProcesDto;
import net.conselldemallorca.helium.v3.core.api.dto.PaginaDto;
import net.conselldemallorca.helium.v3.core.api.dto.PaginacioParamsDto;
import net.conselldemallorca.helium.v3.core.api.dto.PaginacioParamsDto.OrdreDireccioDto;
import net.conselldemallorca.helium.v3.core.api.dto.PaginacioParamsDto.OrdreDto;
import net.conselldemallorca.helium.v3.core.api.dto.PersonaDto;
import net.conselldemallorca.helium.v3.core.api.dto.RegistreDto;
import net.conselldemallorca.helium.v3.core.api.dto.RespostaValidacioSignaturaDto;
import net.conselldemallorca.helium.v3.core.api.dto.TascaDadaDto;
import net.conselldemallorca.helium.v3.core.api.exception.EntornNotFoundException;
import net.conselldemallorca.helium.v3.core.api.exception.EstatNotFoundException;
import net.conselldemallorca.helium.v3.core.api.exception.ExpedientNotFoundException;
import net.conselldemallorca.helium.v3.core.api.exception.ExpedientTipusNotFoundException;
import net.conselldemallorca.helium.v3.core.api.exception.NotFoundException;
import net.conselldemallorca.helium.v3.core.api.service.ExpedientService;
import net.conselldemallorca.helium.v3.core.api.service.PermissionService;
import net.conselldemallorca.helium.v3.core.helper.ConsultaHelper;
import net.conselldemallorca.helium.v3.core.helper.ConversioTipusHelper;
import net.conselldemallorca.helium.v3.core.helper.DocumentHelperV3;
import net.conselldemallorca.helium.v3.core.helper.DtoConverter;
import net.conselldemallorca.helium.v3.core.helper.EntornHelper;
import net.conselldemallorca.helium.v3.core.helper.ExpedientHelper;
import net.conselldemallorca.helium.v3.core.helper.ExpedientLoggerHelper;
import net.conselldemallorca.helium.v3.core.helper.ExpedientTipusHelper;
import net.conselldemallorca.helium.v3.core.helper.PaginacioHelper;
import net.conselldemallorca.helium.v3.core.helper.PermisosHelper;
import net.conselldemallorca.helium.v3.core.helper.PermisosHelper.ObjectIdentifierExtractor;
import net.conselldemallorca.helium.v3.core.helper.PersonaHelper;
import net.conselldemallorca.helium.v3.core.helper.PlantillaHelper;
import net.conselldemallorca.helium.v3.core.helper.PluginHelper;
import net.conselldemallorca.helium.v3.core.helper.ServiceUtils;
import net.conselldemallorca.helium.v3.core.helper.TascaHelper;
import net.conselldemallorca.helium.v3.core.helper.VariableHelper;
import net.conselldemallorca.helium.v3.core.repository.AccioRepository;
import net.conselldemallorca.helium.v3.core.repository.AlertaRepository;
import net.conselldemallorca.helium.v3.core.repository.CampRepository;
import net.conselldemallorca.helium.v3.core.repository.ConsultaCampRepository;
import net.conselldemallorca.helium.v3.core.repository.ConsultaRepository;
import net.conselldemallorca.helium.v3.core.repository.DefinicioProcesRepository;
import net.conselldemallorca.helium.v3.core.repository.DocumentRepository;
import net.conselldemallorca.helium.v3.core.repository.DocumentStoreRepository;
import net.conselldemallorca.helium.v3.core.repository.EnumeracioRepository;
import net.conselldemallorca.helium.v3.core.repository.EstatRepository;
import net.conselldemallorca.helium.v3.core.repository.ExecucioMassivaExpedientRepository;
import net.conselldemallorca.helium.v3.core.repository.ExpedientLoggerRepository;
import net.conselldemallorca.helium.v3.core.repository.ExpedientRepository;
import net.conselldemallorca.helium.v3.core.repository.ExpedientTipusRepository;
import net.conselldemallorca.helium.v3.core.repository.RegistreRepository;
import net.conselldemallorca.helium.v3.core.repository.TerminiIniciatRepository;
import net.conselldemallorca.helium.v3.core.repository.TerminiRepository;

import org.apache.commons.lang.StringUtils;
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
	private PlantillaHelper plantillaHelper;
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
	private ExpedientHelper expedientHelper;
	@Resource
	private EntornHelper entornHelper;
	@Resource
	private ExpedientTipusHelper expedientTipusHelper;
	@Resource
	private JbpmHelper jbpmHelper;
	@Resource
	private PersonaHelper personaHelper;
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
	private ConsultaHelper consultaHelper;
	@Resource(name="dtoConverterV3")
	private DtoConverter dtoConverter;
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
	@Resource(name="serviceUtilsV3")
	private ServiceUtils serviceUtils;
	@Resource
	private ConsultaCampRepository consultaCampRepository;
	@Resource(name="permissionServiceV3")
	private PermissionService permissionService;
	@Resource
	private PluginCustodiaDao pluginCustodiaDao;
	@Resource(name = "pluginServiceV3")
	private PluginServiceImpl pluginService;
	@Resource
	private PluginGestioDocumentalDao pluginGestioDocumentalDao;
	@Resource
	private ExecucioMassivaExpedientRepository execucioMassivaExpedientRepository;
	private String textBloqueigIniciExpedient;
	@Resource
	private OpenOfficeUtils openOfficeUtils;

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
			if (expedientRepository.findByEntornAndTipusAndNumero(
					entorn,
					expedientTipus,
					expedient.getNumero()) != null) {
				throw new ExpedientRepetitException(
						serviceUtils.getMessage(
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
			ExpedientIniciantDto.setExpedient(conversioTipusHelper.convertir(expedient, ExpedientDto.class));
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
			serviceUtils.expedientIndexLuceneCreate(expedient.getProcessInstanceId());
			
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
				true);
		List<JbpmProcessInstance> processInstancesTree = jbpmHelper.getProcessInstanceTree(expedient.getProcessInstanceId());
		for (JbpmProcessInstance pi: processInstancesTree)
			for (TerminiIniciat ti: terminiIniciatRepository.findByProcessInstanceId(pi.getId()))
				terminiIniciatRepository.delete(ti);
		jbpmHelper.deleteProcessInstance(expedient.getProcessInstanceId());
		for (DocumentStore documentStore: documentStoreRepository.findByProcessInstanceId(expedient.getProcessInstanceId())) {
			if (documentStore.isSignat()) {
				try {
					pluginCustodiaDao.esborrarSignatures(documentStore.getReferenciaCustodia());
				} catch (Exception ignored) {}
			}
			if (documentStore.getFont().equals(DocumentFont.ALFRESCO))
				pluginGestioDocumentalDao.deleteDocument(documentStore.getReferenciaFont());
			documentStoreRepository.delete(documentStore.getId());
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
	public void modificarContingutDocument(Long docId, byte[] arxiu) throws Exception {
		Document document = documentRepository.findOne(docId);
		document.setArxiuContingut(arxiu);
	}

	@Override
	@Transactional(readOnly = true)
	public ExpedientDto findAmbId(Long id) throws ExpedientNotFoundException {
		logger.debug("Consultant l'expedient (id=" + id + ")");
		Expedient expedient = expedientHelper.getExpedientComprovantPermisos(
				id,
				true,
				false,
				false);
		ExpedientDto expedientDto = conversioTipusHelper.convertir(
				expedient,
				ExpedientDto.class);
		expedientHelper.omplirPermisosExpedient(expedientDto);
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
	public PaginaDto<ExpedientDto> findAmbFiltrePaginat (
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
			boolean nomesAmbTasquesActives,
			boolean nomesAlertes,
			boolean mostrarAnulats,
			PaginacioParamsDto paginacioParams) throws Exception {
		mesuresTemporalsHelper.mesuraIniciar("CONSULTA GENERAL EXPEDIENTS v3", "consulta");
		mesuresTemporalsHelper.mesuraIniciar("CONSULTA GENERAL EXPEDIENTS v3", "consulta", null, null, "0");
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
				"nomesAmbTasquesActives=" + nomesAmbTasquesActives + ", " +
				"nomesAlertes=" + nomesAlertes + ", " +
				"mostrarAnulats=" + mostrarAnulats + ")");
		// Comprova l'accés a l'entorn
		Entorn entorn = entornHelper.getEntornComprovantPermisos(
				entornId,
				true,
				false,
				false);
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
					ExtendedPermission.READ,
					ExtendedPermission.ADMINISTRATION},
				auth);
		mesuresTemporalsHelper.mesuraCalcular("CONSULTA GENERAL EXPEDIENTS v3", "consulta", null, null, "0");
		mesuresTemporalsHelper.mesuraIniciar("CONSULTA GENERAL EXPEDIENTS v3", "consulta", null, null, "1");
		// Obté la llista d'ids d'expedient de l'entorn actual que
		// tenen alguna tasca activa per a l'usuari actual.
		// Per evitar la limitació d'Oracle que impedeix més de 1000
		// elements com a paràmetres de l'operador IN cream varis
		// conjunts d'ids.
		Set<String> rootProcessInstanceIdsAmbTasquesActives1 = null;
		Set<String> rootProcessInstanceIdsAmbTasquesActives2 = null;
		Set<String> rootProcessInstanceIdsAmbTasquesActives3 = null;
		Set<String> rootProcessInstanceIdsAmbTasquesActives4 = null;
		Set<String> rootProcessInstanceIdsAmbTasquesActives5 = null;
		mesuresTemporalsHelper.mesuraCalcular("CONSULTA GENERAL EXPEDIENTS v3", "consulta", null, null, "1");
		mesuresTemporalsHelper.mesuraIniciar("CONSULTA GENERAL EXPEDIENTS v3", "consulta", null, null, "2");
		Page<Expedient> paginaResultats = null;
		if (nomesAmbTasquesActives) {
			// Fa la consulta
			List<String> idsInstanciesProces = expedientRepository.findProcessInstanceIdsByFiltreGeneral(
					entorn,
					tipusPermesos,
					(expedientTipus == null),
					expedientTipus,
					(titol == null),
					titol,
					(numero == null),
					numero,
					(dataInici1 == null),
					dataInici1,
					(dataInici2 == null),
					dataInici2,
					EstatTipusDto.INICIAT.equals(estatTipus),
					EstatTipusDto.FINALITZAT.equals(estatTipus),
					(!EstatTipusDto.CUSTOM.equals(estatTipus) || estat == null),
					estat,
					(geoPosX == null),
					geoPosX,
					(geoPosY == null),
					geoPosY,
					(geoReferencia == null),
					geoReferencia,
					mostrarAnulats,
					nomesAlertes);	
			List<String> ids = jbpmHelper.findRootProcessInstancesWithActiveTasksCommand(
					auth.getName(),
					idsInstanciesProces);
			Set<String> idsDiferents = new HashSet<String>();
			for (String id: ids) 
				idsDiferents.add(id);
			int index = 0;
			for (String id: idsDiferents) {
				if (index == 0)
					rootProcessInstanceIdsAmbTasquesActives1 = new HashSet<String>();
				if (index == 1000)
					rootProcessInstanceIdsAmbTasquesActives2 = new HashSet<String>();
				if (index == 2000)
					rootProcessInstanceIdsAmbTasquesActives3 = new HashSet<String>();
				if (index == 3000)
					rootProcessInstanceIdsAmbTasquesActives4 = new HashSet<String>();
				if (index == 4000)
					rootProcessInstanceIdsAmbTasquesActives5 = new HashSet<String>();
				if (index < 1000)
					rootProcessInstanceIdsAmbTasquesActives1.add(id);
				else if (index < 2000)
					rootProcessInstanceIdsAmbTasquesActives2.add(id);
				else if (index < 3000)
					rootProcessInstanceIdsAmbTasquesActives3.add(id);
				else if (index < 4000)
					rootProcessInstanceIdsAmbTasquesActives4.add(id);
				else
					rootProcessInstanceIdsAmbTasquesActives5.add(id);
				index++;
			}
		}
		// Fa la consulta
		paginaResultats = expedientRepository.findByFiltreGeneralPaginat(
				entorn,
				tipusPermesos,
				(expedientTipus == null),
				expedientTipus,
				(titol == null),
				titol,
				(numero == null),
				numero,
				(dataInici1 == null),
				dataInici1,
				(dataInici2 == null),
				dataInici2,
				EstatTipusDto.INICIAT.equals(estatTipus),
				EstatTipusDto.FINALITZAT.equals(estatTipus),
				(!EstatTipusDto.CUSTOM.equals(estatTipus) || estat == null),
				estat,
				(geoPosX == null),
				geoPosX,
				(geoPosY == null),
				geoPosY,
				(geoReferencia == null),
				geoReferencia,
				nomesAmbTasquesActives,
				rootProcessInstanceIdsAmbTasquesActives1,
				rootProcessInstanceIdsAmbTasquesActives2,
				rootProcessInstanceIdsAmbTasquesActives3,
				rootProcessInstanceIdsAmbTasquesActives4,
				rootProcessInstanceIdsAmbTasquesActives5,
				mostrarAnulats,
				nomesAlertes,
				paginacioHelper.toSpringDataPageable(paginacioParams));			
		mesuresTemporalsHelper.mesuraCalcular("CONSULTA GENERAL EXPEDIENTS v3", "consulta", null, null, "2");
		mesuresTemporalsHelper.mesuraIniciar("CONSULTA GENERAL EXPEDIENTS v3", "consulta", null, null, "3");
		PaginaDto<ExpedientDto> resposta = paginacioHelper.toPaginaDto(
				paginaResultats,
				ExpedientDto.class);
		expedientHelper.omplirPermisosExpedients(resposta.getContingut());
		mesuresTemporalsHelper.mesuraCalcular("CONSULTA GENERAL EXPEDIENTS v3", "consulta", null, null, "3");
		mesuresTemporalsHelper.mesuraCalcular("CONSULTA GENERAL EXPEDIENTS v3", "consulta");
		return resposta;
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
			boolean nomesAmbTasquesActives,
			boolean nomesAlertes,
			boolean mostrarAnulats) {
		mesuresTemporalsHelper.mesuraIniciar("CONSULTA GENERAL EXPEDIENTS v3", "consulta");
		mesuresTemporalsHelper.mesuraIniciar("CONSULTA GENERAL EXPEDIENTS v3", "consulta", null, null, "0");
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
				"nomesAmbTasquesActives=" + nomesAmbTasquesActives + ", " +
				"nomesAlertes=" + nomesAlertes + ", " +
				"mostrarAnulats=" + mostrarAnulats + ")");
		// Comprova l'accés a l'entorn
		Entorn entorn = entornHelper.getEntornComprovantPermisos(
				entornId,
				true,
				false,
				false);
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
					ExtendedPermission.READ,
					ExtendedPermission.ADMINISTRATION},
				auth);
		mesuresTemporalsHelper.mesuraCalcular("CONSULTA GENERAL EXPEDIENTS v3", "consulta", null, null, "0");
		mesuresTemporalsHelper.mesuraIniciar("CONSULTA GENERAL EXPEDIENTS v3", "consulta", null, null, "1");
		// Obté la llista d'ids d'expedient de l'entorn actual que
		// tenen alguna tasca activa per a l'usuari actual.
		// Per evitar la limitació d'Oracle que impedeix més de 1000
		// elements com a paràmetres de l'operador IN cream varis
		// conjunts d'ids.
		Set<String> rootProcessInstanceIdsAmbTasquesActives1 = null;
		Set<String> rootProcessInstanceIdsAmbTasquesActives2 = null;
		Set<String> rootProcessInstanceIdsAmbTasquesActives3 = null;
		Set<String> rootProcessInstanceIdsAmbTasquesActives4 = null;
		Set<String> rootProcessInstanceIdsAmbTasquesActives5 = null;
		mesuresTemporalsHelper.mesuraCalcular("CONSULTA GENERAL EXPEDIENTS v3", "consulta", null, null, "1");
		mesuresTemporalsHelper.mesuraIniciar("CONSULTA GENERAL EXPEDIENTS v3", "consulta", null, null, "2");
		if (nomesAmbTasquesActives) {
			// Fa la consulta
			List<String> idsInstanciesProces = expedientRepository.findProcessInstanceIdsByFiltreGeneral(
					entorn,
					tipusPermesos,
					(expedientTipus == null),
					expedientTipus,
					(titol == null),
					titol,
					(numero == null),
					numero,
					(dataInici1 == null),
					dataInici1,
					(dataInici2 == null),
					dataInici2,
					EstatTipusDto.INICIAT.equals(estatTipus),
					EstatTipusDto.FINALITZAT.equals(estatTipus),
					(!EstatTipusDto.CUSTOM.equals(estatTipus) || estat == null),
					estat,
					(geoPosX == null),
					geoPosX,
					(geoPosY == null),
					geoPosY,
					(geoReferencia == null),
					geoReferencia,
					mostrarAnulats,
					nomesAlertes);	
			List<String> ids = jbpmHelper.findRootProcessInstancesWithActiveTasksCommand(
					auth.getName(),
					idsInstanciesProces);
			Set<String> idsDiferents = new HashSet<String>();
			for (String id: ids) 
				idsDiferents.add(id);
			int index = 0;
			for (String id: idsDiferents) {
				if (index == 0)
					rootProcessInstanceIdsAmbTasquesActives1 = new HashSet<String>();
				if (index == 1000)
					rootProcessInstanceIdsAmbTasquesActives2 = new HashSet<String>();
				if (index == 2000)
					rootProcessInstanceIdsAmbTasquesActives3 = new HashSet<String>();
				if (index == 3000)
					rootProcessInstanceIdsAmbTasquesActives4 = new HashSet<String>();
				if (index == 4000)
					rootProcessInstanceIdsAmbTasquesActives5 = new HashSet<String>();
				if (index < 1000)
					rootProcessInstanceIdsAmbTasquesActives1.add(id);
				else if (index < 2000)
					rootProcessInstanceIdsAmbTasquesActives2.add(id);
				else if (index < 3000)
					rootProcessInstanceIdsAmbTasquesActives3.add(id);
				else if (index < 4000)
					rootProcessInstanceIdsAmbTasquesActives4.add(id);
				else
					rootProcessInstanceIdsAmbTasquesActives5.add(id);
				index++;
			}
		}
		// Fa la consulta
		List<Long> listaIds = expedientRepository.findIdsByFiltreGeneral(
				entorn,
				tipusPermesos,
				(expedientTipus == null),
				expedientTipus,
				(titol == null),
				titol,
				(numero == null),
				numero,
				(dataInici1 == null),
				dataInici1,
				(dataInici2 == null),
				dataInici2,
				EstatTipusDto.INICIAT.equals(estatTipus),
				EstatTipusDto.FINALITZAT.equals(estatTipus),
				(!EstatTipusDto.CUSTOM.equals(estatTipus) || estat == null),
				estat,
				(geoPosX == null),
				geoPosX,
				(geoPosY == null),
				geoPosY,
				(geoReferencia == null),
				geoReferencia,
				nomesAmbTasquesActives,
				rootProcessInstanceIdsAmbTasquesActives1,
				rootProcessInstanceIdsAmbTasquesActives2,
				rootProcessInstanceIdsAmbTasquesActives3,
				rootProcessInstanceIdsAmbTasquesActives4,
				rootProcessInstanceIdsAmbTasquesActives5,
				mostrarAnulats);
		mesuresTemporalsHelper.mesuraCalcular("CONSULTA GENERAL EXPEDIENTS v3", "consulta", null, null, "1");
		mesuresTemporalsHelper.mesuraCalcular("CONSULTA GENERAL EXPEDIENTS v3", "consulta");
		return listaIds;
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

	@Override
	@Transactional(readOnly = true)
	public List<PersonaDto> findParticipants(Long id) {
		logger.debug("Consulta de participants per a l'expedient (" +
				"id=" + id + ")");
		Expedient expedient = expedientHelper.getExpedientComprovantPermisos(
				id,
				true,
				false,
				false);
		List<ExpedientTascaDto> tasques = tascaHelper.findTasquesPerExpedient(
				expedient);
		Set<String> codisPersona = new HashSet<String>();
		List<PersonaDto> resposta = new ArrayList<PersonaDto>();
		for (ExpedientTascaDto tasca: tasques) {
			if (tasca.getResponsableCodi() != null && !codisPersona.contains(tasca.getResponsableCodi())) {
				resposta.add(tasca.getResponsable());
				codisPersona.add(tasca.getResponsableCodi());
			}
		}
		return resposta;
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
	public List<ExpedientTascaDto> findTasques(Long id) {
		logger.debug("Consulta de tasques de l'expedient (" +
				"id=" + id + ")");
		Expedient expedient = expedientHelper.getExpedientComprovantPermisos(
				id,
				true,
				false,
				false);
		return tascaHelper.findTasquesPerExpedient(
				expedient);
	}

	@Override
	// No pot ser readOnly per mor de la cache de les tasques
	@Transactional
	public List<ExpedientTascaDto> findTasquesPendents(Long id) {
		logger.debug("Consulta de tasques pendents de l'expedient (" +
				"id=" + id + ")");
		Expedient expedient = expedientHelper.getExpedientComprovantPermisos(
				id,
				true,
				false,
				false);
		return tascaHelper.findTasquesPendentsPerExpedient(
				expedient);
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
				false);
		if (processInstanceId == null) {
			return variableHelper.findDadesPerInstanciaProces(
					expedient.getProcessInstanceId());
		} else {
			expedientHelper.comprovarInstanciaProces(
					expedient,
					processInstanceId);
			return variableHelper.findDadesPerInstanciaProces(
					processInstanceId);
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
				false);
		DefinicioProces definicioProces;
		if (processInstanceId == null) {
			definicioProces = expedientHelper.findDefinicioProcesByProcessInstanceId(
					expedient.getProcessInstanceId());
		} else {
			expedientHelper.comprovarInstanciaProces(
					expedient,
					processInstanceId);
			definicioProces = expedientHelper.findDefinicioProcesByProcessInstanceId(
					processInstanceId);
		}
		return conversioTipusHelper.convertirList(
				definicioProces.getAgrupacions(),
				CampAgrupacioDto.class);
	}

	@Override
	@Transactional(readOnly = true)
	public ExpedientDocumentDto findDocumentPerInstanciaProcesDocumentStoreId(Long expedientId, Long documentStoreId, String docCodi) {
		logger.debug("Consulta el document de l'expedient (" +
				"expedientId=" + expedientId + ", " +
				"docCodi=" + docCodi + ", " +
				"documentStoreId=" + documentStoreId + ")");
		Expedient expedient = expedientHelper.getExpedientComprovantPermisos(
				expedientId,
				true,
				false,
				false);
		return documentHelper.findDocumentPerExpedientDocumentStoreId(
				expedient,
				documentStoreId,
				docCodi);
	}

	@Override
	@Transactional
	public void esborrarDocument(Long expedientId, Long documentStoreId, String docCodi) throws Exception {
		ExpedientDocumentDto document = findDocumentPerInstanciaProcesDocumentStoreId(expedientId, documentStoreId, docCodi);
		List<JbpmTask> tasks = jbpmHelper.findTaskInstancesForProcessInstance(document.getProcessInstanceId());
		for (JbpmTask task: tasks) {
			documentHelper.esborrarDocument(
					String.valueOf(task.getTask().getId()),
					document.getProcessInstanceId(),
					document.getDocumentCodi());
		}
	}

	@Override
	@Transactional
	public void deleteSignatura(
			Long expedientId,
			Long documentStoreId) throws Exception {
		DocumentStore documentStore = documentStoreRepository.findById(documentStoreId);
		if (documentStore != null && documentStore.isSignat()) {
			try {
				pluginCustodiaDao.esborrarSignatures(documentStore.getReferenciaCustodia());
			} catch (Exception ignored) {}
			String jbpmVariable = documentStore.getJbpmVariable();
			documentStore.setReferenciaCustodia(null);
			documentStore.setSignat(false);
			Expedient expedient = expedientRepository.findOne(expedientId);
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
			Long id,
			String processInstanceId) {
		logger.debug("Consulta els documents de l'expedient (" +
				"id=" + id + ", " +
				"processInstanceId=" + processInstanceId + ")");
		Expedient expedient = expedientHelper.getExpedientComprovantPermisos(
				id,
				true,
				false,
				false);
		if (processInstanceId == null) {
			return documentHelper.findDocumentsPerInstanciaProces(
					expedient.getProcessInstanceId());
		} else {
			expedientHelper.comprovarInstanciaProces(
					expedient,
					processInstanceId);
			return documentHelper.findDocumentsPerInstanciaProces(
					processInstanceId);
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
				false);
		DocumentStore documentStore = documentStoreRepository.findOne(documentStoreId);
		if (documentStore == null) {
			throw new NotFoundException(
					documentStoreId,
					DocumentStore.class);
		}
		expedientHelper.comprovarInstanciaProces(
				expedient,
				documentStore.getProcessInstanceId());
		return documentHelper.getArxiuPerDocumentStoreId(
				documentStoreId,
				false,
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
	public void reprendre(Long id) {
		logger.debug("Reprenent l'expedient (id=" + id + ")");
		Expedient expedient = expedientHelper.getExpedientComprovantPermisos(
				id,
				false,
				true,
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
				false);
		Expedient desti = expedientHelper.getExpedientComprovantPermisos(
				destiId,
				true,
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
				false);
		Expedient desti = expedientHelper.getExpedientComprovantPermisos(
				destiId,
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
				false);
		List<ExpedientDto> list = new ArrayList<ExpedientDto>();
		for (Expedient relacionat: expedient.getRelacionsOrigen()) {			
			list.add(findAmbId(relacionat.getId()));
		}
		return list;
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
	@Transactional(readOnly = true)
	public List<ExpedientTerminiDto> findTerminis(
			Long id,
			String processInstanceId) {
		logger.debug("Consulta de terminis per l'expedient (" +
				"id=" + id + ", " +
				"processInstanceId=" + processInstanceId + ")");
		Expedient expedient = expedientHelper.getExpedientComprovantPermisos(
				id,
				true,
				false,
				false);
		expedientHelper.comprovarInstanciaProces(
				expedient,
				processInstanceId);
		DefinicioProces definicioProces = expedientHelper.findDefinicioProcesByProcessInstanceId(processInstanceId);
		List<Termini> terminis = terminiRepository.findByDefinicioProcesId(definicioProces.getId());
		List<TerminiIniciat> terminisIniciats = terminiIniciatRepository.findByProcessInstanceId(processInstanceId);
		List<ExpedientTerminiDto> dtos = conversioTipusHelper.convertirList(
				terminis,
				ExpedientTerminiDto.class);
		for (Termini termini: terminis) {
			for (TerminiIniciat iniciat: terminisIniciats) {
				if (iniciat.getTermini().equals(termini)) {
					for (ExpedientTerminiDto dto: dtos) {
						if (termini.getId().equals(dto.getId())) {
							dto.setIniciat(true);
							dto.setIniciatDataInici(
									iniciat.getDataInici());
							dto.setIniciatDataFi(
									iniciat.getDataFi());
							dto.setIniciatDataAturada(
									iniciat.getDataAturada());
							dto.setIniciatDataCancelacio(
									iniciat.getDataCancelacio());
							dto.setIniciatDataFiProrroga(
									iniciat.getDataFiProrroga());
							dto.setIniciatDataCompletat(
									iniciat.getDataCompletat());
							dto.setIniciatDiesAturat(
									iniciat.getDiesAturat());
							dto.setIniciatAnys(
									iniciat.getAnys());
							dto.setIniciatMesos(
									iniciat.getMesos());
							dto.setIniciatDies(
									iniciat.getDies());
							break;
						}
					}
					break;
				}
			}
		}
		return dtos;
	}

	@Override
	@Transactional
	public void evaluateScript(
			Long expedientId,
			String script) {
		logger.debug("Consulta d'expedients relacionats amb l'expedient (" +
				"id=" + expedientId + ")");
		Expedient expedient = expedientHelper.getExpedientComprovantPermisos(
				expedientId,
				true,
				true,
				true);
		JbpmProcessInstance pi = jbpmHelper.getRootProcessInstance(expedient.getProcessInstanceId());
		if (MesuresTemporalsHelper.isActiu()) {
			expedient = expedientRepository.findByProcessInstanceId(pi.getId()).get(0);
			mesuresTemporalsHelper.mesuraIniciar("Executar SCRIPT", "expedient", expedient.getTipus().getNom());
		}
		jbpmHelper.evaluateScript(expedient.getProcessInstanceId(), script, new HashSet<String>());
		verificarFinalitzacioExpedient(expedient, pi);
		serviceUtils.expedientIndexLuceneUpdate(expedient.getProcessInstanceId());
		expedientLoggerHelper.afegirLogExpedientPerProces(
				expedient.getProcessInstanceId(),
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

	/*@Override
	@Transactional(readOnly = true)
	public ExpedientDto findAmbEntornITipusINumero(
			Long entornId,
			String expedientTipusCodi,
			String numero) throws EntornNotFoundException, ExpedientTipusNotFoundException {
		logger.debug("Consulta d'expedient (entornId=" + entornId + ", expedientTipusCodi=" + expedientTipusCodi + ", numero=" + numero + ")");
		Entorn entorn = entornRepository.findOne(entornId);
		if (entorn == null)
			throw new EntornNotFoundException();
		ExpedientTipus expedientTipus = expedientTipusRepository.findByEntornAndCodi(
				entorn,
				expedientTipusCodi);
		if (expedientTipus == null)
			throw new ExpedientTipusNotFoundException();
		Expedient expedient = expedientRepository.findByTipusAndNumero(
				expedientTipus,
				numero);
		return conversioTipusHelper.convertir(
				expedient,
				ExpedientDto.class);
	}

	@Transactional(readOnly = true)
	public ExpedientDto findAmbProcessInstanceId(String processInstanceId) {
		logger.debug("Consulta de l'expedient (processInstanceId=" + processInstanceId + ")");
		Expedient expedient = expedientHelper.findExpedientByProcessInstanceId(processInstanceId);
		return conversioTipusHelper.convertir(
				expedient,
				ExpedientDto.class);
	}
*/
	@Override
	@Transactional(readOnly = true)
	public PaginaDto<ExpedientConsultaDissenyDto> findConsultaInformePaginat(final Long consultaId, Map<String, Object> valorsPerService, Boolean nomesPendents, Boolean nomesAlertes, Boolean mostrarAnulats, final PaginacioParamsDto paginacioParams) throws EntornNotFoundException, ExpedientTipusNotFoundException, EstatNotFoundException {
		mesuresTemporalsHelper.mesuraIniciar("CONSULTA INFORME EXPEDIENTS v3", "consulta");
		mesuresTemporalsHelper.mesuraIniciar("CONSULTA INFORME EXPEDIENTS v3", "consulta", null, null, "0");
		
		final List<ExpedientConsultaDissenyDto> expedientsConsultaDisseny = findConsultaDissenyPaginat(
			consultaId,
			valorsPerService,
			paginacioParams,
			nomesPendents, nomesAlertes, mostrarAnulats
		);
		
		mesuresTemporalsHelper.mesuraCalcular("CONSULTA INFORME EXPEDIENTS v3", "consulta", null, null, "0");
		mesuresTemporalsHelper.mesuraIniciar("CONSULTA INFORME EXPEDIENTS v3", "consulta", null, null, "1");
		
		final int numExpedients= findIdsPerConsultaInformePaginat(
				consultaId,
				valorsPerService, 
				nomesPendents, nomesAlertes, mostrarAnulats
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
	public DocumentDto generarDocumentPlantillaTasca(String tascaId, Long documentId, Long expedientId) throws Exception {
		ExpedientDto expedient = findAmbId(expedientId);
		DocumentDto doc = generarDocumentPlantilla(documentId, expedient);
		documentHelper.actualitzarDocument(
				tascaId,
				expedient.getProcessInstanceId(),
				doc.getCodi(),
				null,
				doc.getDataDocument(),
				doc.getArxiuNom(),
				doc.getArxiuContingut(),
				false);
		
		return doc;
	}
	
	@Override
	@Transactional(readOnly = true)
	public DocumentDto generarDocumentPlantilla(Long documentId, ExpedientDto expedient) throws Exception {
		Document document = documentRepository.findOne(documentId);
		DocumentDto resposta = new DocumentDto();
		resposta.setCodi(document.getCodi());
		resposta.setDataCreacio(new Date());
		resposta.setDataDocument(new Date());
		resposta.setArxiuNom(document.getNom() + ".odt");
		resposta.setAdjuntarAuto(document.isAdjuntarAuto());
		if (document.isPlantilla()) {
			try {				
				ArxiuDto resultat = plantillaHelper.generarDocumentPlantilla(
						expedient.getEntorn().getId(),
						documentId,
						null,
						expedient.getProcessInstanceId(),
						new Date());
				if (isActiuConversioVista()) {
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					openOfficeUtils.convertir(
							resposta.getArxiuNom(),
							resultat.getContingut(),
							getExtensioVista(document),
							baos);
					resposta.setArxiuNom(
							nomArxiuAmbExtensio(
									resposta.getArxiuNom(),
									getExtensioVista(document)));
					resposta.setArxiuContingut(baos.toByteArray());
				} else {
					resposta.setArxiuContingut(resultat.getContingut());
				}
			} catch (Exception ex) {
				throw new Exception(ex);
			}
		} else {
			resposta.setArxiuContingut(document.getArxiuContingut());
		}
		return resposta;
	}
	
	private String nomArxiuAmbExtensio(String fileName, String extensio) {
		if (extensio == null || extensio.length() == 0)
			return fileName;
		int indexPunt = fileName.lastIndexOf(".");
		if (indexPunt != -1) {
			String nom = fileName.substring(0, indexPunt);
			return nom + "." + extensio;
		} else {
			return fileName + "." + extensio;
		}
	}

	private boolean isActiuConversioVista() {
		String actiuConversio = (String)GlobalProperties.getInstance().get("app.conversio.actiu");
		if (!"true".equalsIgnoreCase(actiuConversio))
			return false;
		String actiuConversioVista = (String)GlobalProperties.getInstance().get("app.conversio.vista.actiu");
		if (actiuConversioVista == null)
			actiuConversioVista = (String)GlobalProperties.getInstance().get("app.conversio.gentasca.actiu");
		return "true".equalsIgnoreCase(actiuConversioVista);
	}
	
	private String getExtensioVista(Document document) {
		String extensioVista = null;
		if (isActiuConversioVista()) {
			if (document.getConvertirExtensio() != null && document.getConvertirExtensio().length() > 0) {
				extensioVista = document.getConvertirExtensio();
			} else {
				extensioVista = (String)GlobalProperties.getInstance().get("app.conversio.vista.extension");
				if (extensioVista == null)
					extensioVista = (String)GlobalProperties.getInstance().get("app.conversio.gentasca.extension");
			}
		}
		return extensioVista;
	}
	
	@Override
	@Transactional(readOnly=true)
	public InstanciaProcesDto getInstanciaProcesById(String processInstanceId) {
		return dtoConverter.toInstanciaProcesDto(processInstanceId);
	}
	
	@Override
	@Transactional(readOnly=true)
	public List<CampDto> getCampsInstanciaProcesById(String processInstanceId) {
		return dtoConverter.toCampInstanciaProcesDto(processInstanceId);
	}

	@Override
	@Transactional(readOnly=true)
	public CampDto getCampsInstanciaProcesByIdAmdVarcodi(String processInstanceId, String varCodi) {
		return dtoConverter.toCampInstanciaProcesDtoVarCodi(processInstanceId, varCodi);
	}

	@Override
	@Transactional(readOnly=true)
	public List<RegistreDto> getRegistrePerExpedient(Long expedientId) {
		List<Registre> registre = registreRepository.findByExpedientId(expedientId);
		return conversioTipusHelper.convertirList(registre, RegistreDto.class);
	}

	@Override
	@Transactional(readOnly=true)
	public List<ExpedientLogDto> getLogsOrdenatsPerData(ExpedientDto expedient) {
		mesuresTemporalsHelper.mesuraIniciar("Expedient REGISTRE", "expedient", expedient.getTipus().getNom(), null, "findAmbExpedientIdOrdenatsPerData");
		List<ExpedientLogDto> resposta = new ArrayList<ExpedientLogDto>();
		List<ExpedientLog> logs = expedientLogRepository.findAmbExpedientIdOrdenatsPerData(expedient.getId());
		String parentProcessInstanceId = null;
		Map<String, String> processos = new HashMap<String, String>();
		mesuresTemporalsHelper.mesuraCalcular("Expedient REGISTRE", "expedient", expedient.getTipus().getNom(), null, "findAmbExpedientIdOrdenatsPerData");
		mesuresTemporalsHelper.mesuraIniciar("Expedient REGISTRE", "expedient", expedient.getTipus().getNom(), null, "obtenir tokens");
		for (ExpedientLog log: logs) {
			resposta.addAll(logsOrdenats(log, parentProcessInstanceId, processos));
		}
		mesuresTemporalsHelper.mesuraCalcular("Expedient REGISTRE", "expedient", expedient.getTipus().getNom(), null, "obtenir tokens");
		return resposta;
	}

	@Override
	@Transactional(readOnly=true)
	public List<ExpedientLogDto> getLogsPerTascaOrdenatsPerData(ExpedientDto expedient) {
		mesuresTemporalsHelper.mesuraIniciar("Expedient REGISTRE", "expedient", expedient.getTipus().getNom(), null, "findAmbExpedientIdOrdenatsPerData");
		List<ExpedientLogDto> resposta = new ArrayList<ExpedientLogDto>();
		List<ExpedientLog> logs = expedientLogRepository.findAmbExpedientIdOrdenatsPerData(expedient.getId());
		List<String> taskIds = new ArrayList<String>();
		String parentProcessInstanceId = null;
		Map<String, String> processos = new HashMap<String, String>();
		mesuresTemporalsHelper.mesuraCalcular("Expedient REGISTRE", "expedient", expedient.getTipus().getNom(), null, "findAmbExpedientIdOrdenatsPerData");
		mesuresTemporalsHelper.mesuraIniciar("Expedient REGISTRE", "expedient", expedient.getTipus().getNom(), null, "obtenir tokens tasca");
		for (ExpedientLog log: logs) {
			if (	!log.isTargetTasca() ||
					!taskIds.contains(log.getTargetId())) {
				taskIds.add(log.getTargetId());
				// Obtenim el token de cada registre
				resposta.addAll(logsOrdenats(log, parentProcessInstanceId, processos));
			}
		}
		mesuresTemporalsHelper.mesuraCalcular("Expedient REGISTRE", "expedient", expedient.getTipus().getNom(), null, "obtenir tokens tasca");
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
				false);
		List<ExpedientLog> logs = expedientLogRepository.findAmbExpedientIdOrdenatsPerData(expedientId);
		Map<String, ExpedientTascaDto> tasquesPerLogs = new HashMap<String, ExpedientTascaDto>();
		for (ExpedientLog log: logs) {
			if (log.isTargetTasca()) {
				JbpmTask task = jbpmHelper.getTaskById(log.getTargetId());
				if (task != null) {
					tasquesPerLogs.put(log.getTargetId(),tascaHelper.toExpedientTascaCompleteDto(task,expedient));
				}
			}
		}
		return tasquesPerLogs;
	}

	@Override
	@Transactional(readOnly=true)
	public List<TascaDadaDto> findConsultaFiltre(Long consultaId) {
		Consulta consulta = consultaHelper.findById(consultaId);		
		
		List<TascaDadaDto> listTascaDada = serviceUtils.findCampsPerCampsConsulta(
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
		Consulta consulta = consultaHelper.findById(consultaId);
		return serviceUtils.findCampsPerCampsConsulta(
				consulta,
				TipusConsultaCamp.INFORME);
	}

	@Override
	@Transactional(readOnly=true)
	public List<TascaDadaDto> findConsultaInformeParams(Long consultaId) {
		Consulta consulta = consultaHelper.findById(consultaId);
		return serviceUtils.findCampsPerCampsConsulta(
				consulta,
				TipusConsultaCamp.PARAM);
	}
	
	@Override
	@Transactional(readOnly=true)
	public List<ExpedientConsultaDissenyDto> findConsultaDissenyPaginat(
			Long consultaId,
			Map<String, Object> valors,
			PaginacioParamsDto paginacioParams, Boolean nomesPendents, Boolean nomesAlertes, Boolean mostrarAnulats) {
		List<ExpedientConsultaDissenyDto> resposta = new ArrayList<ExpedientConsultaDissenyDto>();
		Consulta consulta = consultaHelper.findById(consultaId);		
		
		List<TascaDadaDto> campsFiltreDto = serviceUtils.findCampsPerCampsConsulta(
				consulta,
				TipusConsultaCamp.FILTRE);
		List<TascaDadaDto> campsInformeDto = serviceUtils.findCampsPerCampsConsulta(
				consulta,
				TipusConsultaCamp.INFORME);
		
		List<Camp> campsFiltre = new ArrayList<Camp>();
		for (TascaDadaDto tascaDadaDto : campsFiltreDto) {
			Camp camp = campRepository.findById(tascaDadaDto.getCampId());
			if (camp == null) {
				camp = new Camp(
//						consulta.getExpedientTipus().getDefinicionsProces(),
						null,
						tascaDadaDto.getVarCodi(),
						TipusCamp.STRING,
						tascaDadaDto.getVarCodi());
			}
			campsFiltre.add(camp);
		}
		
		List<Camp> campsInforme = new ArrayList<Camp>();
		for (TascaDadaDto tascaDadaDto : campsInformeDto) {
			Camp camp = campRepository.findById(tascaDadaDto.getCampId());
			if (camp == null) {
				camp = new Camp(
						null,
						tascaDadaDto.getVarCodi(),
						TipusCamp.STRING,
						tascaDadaDto.getVarCodi());
			}
			campsInforme.add(camp);
		}
		
		afegirValorsPredefinits(consulta, valors, campsFiltre);
		
		String sort = null;
		boolean asc = false;
		int firstRow;
		int maxResults;
		if (paginacioParams == null) {
			sort = ExpedientCamps.EXPEDIENT_CAMP_ID;
			asc = false;
			firstRow = 0;
			maxResults = -1;
		} else {
			for (OrdreDto or : paginacioParams.getOrdres()) {
				asc = or.getDireccio().equals(OrdreDireccioDto.ASCENDENT);
				if (or.getCamp().contains("dadesExpedient")) {
					sort = or.getCamp().replace("/", ".").replace("dadesExpedient.", "").replace(".valorMostrar", "");
				} else {
					sort = or.getCamp().replace(".", "$");
				}
				break;
			}
			firstRow = paginacioParams.getPaginaNum()*paginacioParams.getPaginaTamany();
			maxResults = paginacioParams.getPaginaTamany();
		}
		
		List<Map<String, DadaIndexadaDto>> dadesExpedients = luceneHelper.findAmbDadesExpedientV3(
				consulta.getEntorn().getCodi(),
				consulta.getExpedientTipus().getCodi(),
				campsFiltre,
				valors,
				campsInforme,
				sort,
				asc,
				firstRow,
				maxResults);
		
		for (Map<String, DadaIndexadaDto> dadesExpedient: dadesExpedients) {
			DadaIndexadaDto dadaExpedientId = dadesExpedient.get(LuceneHelper.CLAU_EXPEDIENT_ID);
			ExpedientConsultaDissenyDto fila = new ExpedientConsultaDissenyDto();
			Expedient expedient = expedientRepository.findOne(Long.parseLong(dadaExpedientId.getValorIndex()));
			if (expedient != null) {
				fila.setExpedient(dtoConverter.toExpedientDto(expedient));
				dtoConverter.revisarDadesExpedientAmbValorsEnumeracionsODominis(
						dadesExpedient,
						campsInforme,
						expedient);
				fila.setDadesExpedient(dadesExpedient);
				resposta.add(fila);
			}
			dadesExpedient.remove(LuceneHelper.CLAU_EXPEDIENT_ID);
		}
		return resposta;
	}
	
	@Override
	@Transactional(readOnly=true)
	public List<Long> findIdsPerConsultaInformePaginat(
			Long consultaId,
			Map<String, Object> valors,
			Boolean nomesPendents, Boolean nomesAlertes, Boolean mostrarAnulats) {
		Consulta consulta = consultaHelper.findById(consultaId);		
		
		List<TascaDadaDto> campsFiltreDto = serviceUtils.findCampsPerCampsConsulta(
				consulta,
				TipusConsultaCamp.FILTRE);
		
		List<Camp> campsFiltre = new ArrayList<Camp>();
		for (TascaDadaDto tascaDadaDto : campsFiltreDto) {
			Camp camp = campRepository.findById(tascaDadaDto.getCampId());
			if (camp == null) {
				camp = new Camp(
						null,
						tascaDadaDto.getVarCodi(),
						TipusCamp.STRING,
						tascaDadaDto.getVarCodi());
			}
			campsFiltre.add(camp);
		}
		
		afegirValorsPredefinits(consulta, valors, campsFiltre);
		return luceneHelper.findNomesIds(
				consulta.getEntorn().getCodi(),
				consulta.getExpedientTipus().getCodi(),
				campsFiltre,
				valors);
	}

	@Override
	@Transactional(readOnly=true)
	public List<ExpedientLogDto> findLogsTascaOrdenatsPerData(Long logId) {
		List<ExpedientLog> logs = expedientLogRepository.findLogsTascaByIdOrdenatsPerData(String.valueOf(logId));
		return conversioTipusHelper.convertirList(logs, ExpedientLogDto.class);
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
		serviceUtils.expedientIndexLuceneUpdate(
				log.getExpedient().getProcessInstanceId());
		mesuresTemporalsHelper.mesuraCalcular("Retrocedir" + (retrocedirPerTasques ? " per tasques" : ""), "expedient", log.getExpedient().getTipus().getNom());
	}

	@Override
	@Transactional(readOnly=true)
	public List<ExpedientLogDto> findLogsRetroceditsOrdenatsPerData(Long logId) {
		List<ExpedientLog> logs = expedientLoggerHelper.findLogsRetrocedits(logId);
		return conversioTipusHelper.convertirList(logs, ExpedientLogDto.class);
	}
	/*
	@Transactional
	@Override
	public void deleteConsulta(Long id) {
		Consulta vell = consultaRepository.findById(id);
		Long expedientTipusId = vell.getExpedientTipus().getId();
		Long entornId = vell.getEntorn().getId();
		if (vell != null){
			consultaRepository.delete(id);
			reordenarConsultes(entornId, expedientTipusId); 
		}
	}

	@Transactional
	private void reordenarConsultes(Long entornId, Long expedientTipusId) {		
		List<Consulta> consultes = consultaRepository.findConsultesAmbEntornIExpedientTipusOrdenat(
				entornId,
				expedientTipusId);		
		int i = 0;
		for (Consulta consulta: consultes)
			consulta.setOrdre(i++);
	}
	
	

	
*/
	@Override
	@Transactional(readOnly=true)
	public boolean existsExpedientAmbEntornTipusITitol(Long entornId, Long expedientTipusId, String titol) {
		return expedientRepository.findByEntornIdAndTipusIdAndTitol(entornId, expedientTipusId, titol) != null;
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
			return dtoConverter.toTascaInicialDto(startTaskName, definicioProces.getJbpmId(), valors);
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
				true,
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
	@Transactional(readOnly=true)
	public List<Object> findLogIdTasquesById(List<ExpedientTascaDto> tasques) {
		if (!tasques.isEmpty()) {
			List<String> tasquesIds = new ArrayList<String>();
			for (ExpedientTascaDto tasca : tasques) {
				tasquesIds.add(tasca.getId());
			}
			return expedientLoggerHelper.findLogIdTasquesById(tasquesIds);
		} else {
			return new ArrayList<Object>();
		}
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

	private List<ExpedientLogDto> logsOrdenats(ExpedientLog log, String parentProcessInstanceId, Map<String, String> processos) {
		List<ExpedientLogDto> resposta = new ArrayList<ExpedientLogDto>();
		// Obtenim el token de cada registre
		JbpmToken token = null;
		if (log.getJbpmLogId() != null) {
			token = expedientLoggerHelper.getTokenByJbpmLogId(log.getJbpmLogId());
		}
		String tokenName = null;
		String processInstanceId = null;
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
						resposta.add(dto);
					}
				}
				tokenName = processos.get(processInstanceId) + tokenName;
			}
		}
			
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
		resposta.add(dto);
		
		return resposta;
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
	
	private boolean isUserInRole(
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
		ExpedientTipusDto expedientTipusDto = conversioTipusHelper.convertir(expedientTipus, ExpedientTipusDto.class);
		Expedient expedient = null;
		if (any == null) 
			any = Calendar.getInstance().get(Calendar.YEAR);
		do {
			numero = expedientHelper.getNumeroExpedientActual(
					expedientTipus,
					any.intValue(),
					increment);
			expedient = expedientRepository.findByEntornAndTipusAndNumero(
					entorn,
					expedientTipus,
					numero);
			increment++;
		} while (expedient != null);
		if (increment > 1) {
			expedientTipusDto.updateSequencia(any, increment - 1);
		}
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
			expedient = expedientRepository.findByEntornAndTipusAndNumeroDefault(
					entorn,
					expedientTipus,
					numero);
			increment++;
		} while (expedient != null);
		if (increment > 1) {
			expedientTipus.updateSequenciaDefault(any, increment - 1);
		}
		return numero;
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

	private static final Logger logger = LoggerFactory.getLogger(ExpedientServiceImpl.class);
}
