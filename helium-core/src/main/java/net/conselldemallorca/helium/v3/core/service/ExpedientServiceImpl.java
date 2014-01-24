/**
 * 
 */
package net.conselldemallorca.helium.v3.core.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.annotation.Resource;

import net.conselldemallorca.helium.core.extern.domini.FilaResultat;
import net.conselldemallorca.helium.core.model.dao.ExpedientDao;
import net.conselldemallorca.helium.core.model.dao.ExpedientTipusDao;
import net.conselldemallorca.helium.core.model.dao.PluginCustodiaDao;
import net.conselldemallorca.helium.core.model.dao.PluginGestioDocumentalDao;
import net.conselldemallorca.helium.core.model.dao.RegistreDao;
import net.conselldemallorca.helium.core.model.exception.ExpedientRepetitException;
import net.conselldemallorca.helium.core.model.exception.IllegalArgumentsException;
import net.conselldemallorca.helium.core.model.exception.NotFoundException;
import net.conselldemallorca.helium.core.model.hibernate.Alerta;
import net.conselldemallorca.helium.core.model.hibernate.Camp;
import net.conselldemallorca.helium.core.model.hibernate.Consulta;
import net.conselldemallorca.helium.core.model.hibernate.ConsultaCamp.TipusConsultaCamp;
import net.conselldemallorca.helium.core.model.hibernate.DefinicioProces;
import net.conselldemallorca.helium.core.model.hibernate.DocumentStore;
import net.conselldemallorca.helium.core.model.hibernate.DocumentStore.DocumentFont;
import net.conselldemallorca.helium.core.model.hibernate.Domini;
import net.conselldemallorca.helium.core.model.hibernate.Entorn;
import net.conselldemallorca.helium.core.model.hibernate.Enumeracio;
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
import net.conselldemallorca.helium.core.model.hibernate.TerminiIniciat;
import net.conselldemallorca.helium.core.model.service.ConversioTipusHelper;
import net.conselldemallorca.helium.core.model.service.LuceneHelper;
import net.conselldemallorca.helium.core.model.service.MesuresTemporalsHelper;
import net.conselldemallorca.helium.core.model.service.PermisosHelper;
import net.conselldemallorca.helium.core.model.service.PermisosHelper.ObjectIdentifierExtractor;
import net.conselldemallorca.helium.core.model.service.TascaService;
import net.conselldemallorca.helium.core.security.ExtendedPermission;
import net.conselldemallorca.helium.core.util.MesurarTemps;
import net.conselldemallorca.helium.jbpm3.integracio.JbpmHelper;
import net.conselldemallorca.helium.jbpm3.integracio.JbpmProcessInstance;
import net.conselldemallorca.helium.jbpm3.integracio.JbpmTask;
import net.conselldemallorca.helium.jbpm3.integracio.JbpmToken;
import net.conselldemallorca.helium.v3.core.api.dto.ArxiuDto;
import net.conselldemallorca.helium.v3.core.api.dto.CampAgrupacioDto;
import net.conselldemallorca.helium.v3.core.api.dto.CampDto;
import net.conselldemallorca.helium.v3.core.api.dto.DadaIndexadaDto;
import net.conselldemallorca.helium.v3.core.api.dto.DadesDocumentDto;
import net.conselldemallorca.helium.v3.core.api.dto.DominiDto;
import net.conselldemallorca.helium.v3.core.api.dto.DominiRespostaFilaDto;
import net.conselldemallorca.helium.v3.core.api.dto.EnumeracioValorDto;
import net.conselldemallorca.helium.v3.core.api.dto.EstatDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientConsultaDissenyDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientDadaDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientDocumentDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientDto.EstatTipusDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientDto.IniciadorTipusDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientIniciantDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientLogDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientTascaDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientTipusDto;
import net.conselldemallorca.helium.v3.core.api.dto.InstanciaProcesDto;
import net.conselldemallorca.helium.v3.core.api.dto.PaginaDto;
import net.conselldemallorca.helium.v3.core.api.dto.PaginacioParamsDto;
import net.conselldemallorca.helium.v3.core.api.dto.PersonaDto;
import net.conselldemallorca.helium.v3.core.api.dto.RegistreDto;
import net.conselldemallorca.helium.v3.core.api.exception.DocumentDescarregarException;
import net.conselldemallorca.helium.v3.core.api.exception.DominiNotFoundException;
import net.conselldemallorca.helium.v3.core.api.exception.EntornNotFoundException;
import net.conselldemallorca.helium.v3.core.api.exception.EnumeracioNotFoundException;
import net.conselldemallorca.helium.v3.core.api.exception.EstatNotFoundException;
import net.conselldemallorca.helium.v3.core.api.exception.ExpedientNotFoundException;
import net.conselldemallorca.helium.v3.core.api.exception.ExpedientTipusNotFoundException;
import net.conselldemallorca.helium.v3.core.api.exception.SistemaExternException;
import net.conselldemallorca.helium.v3.core.api.exception.TaskInstanceNotFoundException;
import net.conselldemallorca.helium.v3.core.api.service.ExpedientService;
import net.conselldemallorca.helium.v3.core.api.service.PermissionService;
import net.conselldemallorca.helium.v3.core.helper.ConsultaHelper;
import net.conselldemallorca.helium.v3.core.helper.DocumentHelperV3;
import net.conselldemallorca.helium.v3.core.helper.DominiHelper;
import net.conselldemallorca.helium.v3.core.helper.DtoConverter;
import net.conselldemallorca.helium.v3.core.helper.ExpedientHelper;
import net.conselldemallorca.helium.v3.core.helper.ExpedientLoggerHelper;
import net.conselldemallorca.helium.v3.core.helper.PaginacioHelper;
import net.conselldemallorca.helium.v3.core.helper.PersonaHelper;
import net.conselldemallorca.helium.v3.core.helper.ServiceUtils;
import net.conselldemallorca.helium.v3.core.helper.TascaHelper;
import net.conselldemallorca.helium.v3.core.helper.VariableHelper;
import net.conselldemallorca.helium.v3.core.repository.AlertaRepository;
import net.conselldemallorca.helium.v3.core.repository.CampRepository;
import net.conselldemallorca.helium.v3.core.repository.ConsultaCampRepository;
import net.conselldemallorca.helium.v3.core.repository.ConsultaRepository;
import net.conselldemallorca.helium.v3.core.repository.DefinicioProcesRepository;
import net.conselldemallorca.helium.v3.core.repository.DocumentStoreRepository;
import net.conselldemallorca.helium.v3.core.repository.DominiRepository;
import net.conselldemallorca.helium.v3.core.repository.EntornRepository;
import net.conselldemallorca.helium.v3.core.repository.EnumeracioRepository;
import net.conselldemallorca.helium.v3.core.repository.EstatRepository;
import net.conselldemallorca.helium.v3.core.repository.ExecucioMassivaExpedientRepository;
import net.conselldemallorca.helium.v3.core.repository.ExpedientLoggerRepository;
import net.conselldemallorca.helium.v3.core.repository.ExpedientRepository;
import net.conselldemallorca.helium.v3.core.repository.ExpedientTipusRepository;
import net.conselldemallorca.helium.v3.core.repository.RegistreRepository;
import net.conselldemallorca.helium.v3.core.repository.TerminiIniciatRepository;

import org.apache.commons.lang.StringUtils;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.security.acls.model.Permission;
import org.springframework.security.core.Authentication;
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
	private EntornRepository entornRepository;
	@Resource
	private ExpedientRepository expedientRepository;
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
	private RegistreDao registreDao;
	@Resource
	private RegistreRepository registreRepository;
	@Resource
	private DominiRepository dominiRepository;
	@Resource
	private EnumeracioRepository enumeracioRepository;
	@Resource
	private TerminiIniciatRepository terminiIniciatRepository;
	@Resource
	private DefinicioProcesRepository definicioProcesRepository;
	@Resource
	private DocumentStoreRepository documentStoreRepository;
	@Resource
	private JbpmHelper jbpmHelper;
	@Resource
	private PersonaHelper personaHelper;
	@Resource
	private VariableHelper variableHelper;
	@Resource(name="documentHelperV3")
	private DocumentHelperV3 documentHelper;
	@Resource
	private ExpedientHelper expedientHelper;
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
	@Resource
	private DominiHelper dominiHelper;
	@Resource
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
	private MessageSource messageSource;
	@Resource
	private PluginCustodiaDao pluginCustodiaDao;
	@Resource
	private ExpedientTipusDao expedientTipusDao;
	@Resource(name = "pluginServiceV3")
	private PluginServiceImpl pluginService;
	@Resource
	private PluginGestioDocumentalDao pluginGestioDocumentalDao;
	@Resource
	private ExpedientDao expedientDao;
	@Resource
	private ExecucioMassivaExpedientRepository execucioMassivaExpedientRepository;
	
	private String textBloqueigIniciExpedient;

	@Transactional
	private void comprovarUsuari(String usuari) {
		PersonaDto persona = pluginService.findPersonaAmbCodi(usuari);
		if (persona == null)
			throw new IllegalArgumentsException(
					getServiceUtils().getMessage("error.expedientService.trobarPersona",
							new Object[]{usuari}));
	}

	@Transactional
	public void modificar(
			Long id,
			String numero,
			String titol,
			String responsableCodi,
			Date dataInici,
			String comentari,
			String estatCodi,
			Double geoPosX,
			Double geoPosY,
			String geoReferencia,
			String grupCodi,
			boolean execucioDinsHandler) throws ExpedientNotFoundException, EstatNotFoundException {
		logger.debug(
				"Modificar informació de l'expedient (id=" + id +
				", numero=" + numero +
				", titol=" + titol +
				", responsableCodi=" + responsableCodi +
				", dataInici=" + dataInici +
				", comentari=" + comentari +
				", estatCodi=" + estatCodi +
				", geoPosX=" + geoPosX +
				", geoPosY=" + geoPosY +
				", geoReferencia=" + geoReferencia +
				", grupCodi=" + grupCodi + ")");
		Expedient expedient = expedientRepository.findOne(id);
		if (expedient == null)
			throw new ExpedientNotFoundException();
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
		if (estatCodi != null) {
			if (expedient.getEstat() == null || expedient.getEstat().getCodi() != estatCodi) {
				expedientLoggerHelper.afegirProcessLogInfoExpedient(
						expedient.getProcessInstanceId(), 
						LogInfo.ESTAT + "#@#" + "---");
				Estat estat = estatRepository.findByExpedientTipusAndCodi(
						expedient.getTipus(),
						estatCodi);
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

	@Transactional
	public void aturar(
			Long expedientId,
			String motiu) throws ExpedientNotFoundException {
		logger.debug("Aturada l'expedient (id=" + expedientId + ", motiu=" + motiu + ")");
		Expedient expedient = expedientRepository.findOne(expedientId);
		if (expedient == null)
			throw new ExpedientNotFoundException();
		logger.debug("Afegint log EXPEDIENT_ATURAR a l'expedient (id=" + expedientId + ")");
		ExpedientLog expedientLog = expedientLoggerHelper.afegirLogExpedientPerExpedient(
				expedient.getId(),
				ExpedientLogAccioTipus.EXPEDIENT_ATURAR,
				motiu);
		expedientLog.setEstat(ExpedientLogEstat.IGNORAR);
		logger.debug("Suspenent les instàncies de procés associades a l'expedient (id=" + expedientId + ")");
		List<JbpmProcessInstance> processInstancesTree = jbpmHelper.getProcessInstanceTree(
				expedient.getProcessInstanceId());
		String[] ids = new String[processInstancesTree.size()];
		int i = 0;
		for (JbpmProcessInstance pi: processInstancesTree)
			ids[i++] = pi.getId();
		jbpmHelper.suspendProcessInstances(ids);
		expedient.setInfoAturat(motiu);
	}

	@Transactional
	public void reprendre(Long expedientId) throws ExpedientNotFoundException {
		logger.debug("Represa l'expedient (id=" + expedientId + ")");
		Expedient expedient = expedientRepository.findOne(expedientId);
		if (expedient == null)
			throw new ExpedientNotFoundException();
		logger.debug("Afegint log EXPEDIENT_REPRENDRE a l'expedient (id=" + expedientId + ")");
		ExpedientLog expedientLog = expedientLoggerHelper.afegirLogExpedientPerExpedient(
				expedient.getId(),
				ExpedientLogAccioTipus.EXPEDIENT_REPRENDRE,
				null);
		expedientLog.setEstat(ExpedientLogEstat.IGNORAR);
		logger.debug("Reprenent les instàncies de procés associades a l'expedient (id=" + expedientId + ")");
		List<JbpmProcessInstance> processInstancesTree = jbpmHelper.getProcessInstanceTree(
				expedient.getProcessInstanceId());
		String[] ids = new String[processInstancesTree.size()];
		int i = 0;
		for (JbpmProcessInstance pi: processInstancesTree)
			ids[i++] = pi.getId();
		jbpmHelper.resumeProcessInstances(ids);
	}

	@Transactional(readOnly = true)
	public ExpedientDto findById(Long expedientId) throws ExpedientNotFoundException {
		logger.debug("Consultant l'expedient (id=" + expedientId + ")");
		Expedient expedient = expedientHelper.geExpedientComprovantPermisosAny(
				expedientId,
				new Permission[] {
						ExtendedPermission.READ,
						ExtendedPermission.ADMINISTRATION});
		ExpedientDto expedientDto = conversioTipusHelper.convertir(
				expedient,
				ExpedientDto.class);
		
		for (Expedient relacionat: expedient.getRelacionsOrigen()) {
			ExpedientDto relacionatDto = new ExpedientDto();
			relacionatDto.setId(relacionat.getId());
			relacionatDto.setTitol(relacionat.getTitol());
			relacionatDto.setNumero(relacionat.getNumero());
			relacionatDto.setDataInici(relacionat.getDataInici());
			relacionatDto.setTipus(new ModelMapper().map(relacionat.getTipus(), ExpedientTipusDto.class));
			if (relacionat.getEstat() != null) {
				relacionatDto.setEstat(new ModelMapper().map(relacionat.getEstat(), EstatDto.class));
			}
			relacionatDto.setProcessInstanceId(relacionat.getProcessInstanceId());
			expedientDto.addExpedientRelacionat(relacionatDto);
		}

		return expedientDto;
	}

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
		Expedient expedient = expedientHelper.findAmbProcessInstanceId(processInstanceId);
		return conversioTipusHelper.convertir(
				expedient,
				ExpedientDto.class);
	}

	@Transactional(readOnly = true)
	public PaginaDto<ExpedientDto> findPerConsultaGeneralPaginat(
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
			PaginacioParamsDto paginacioParams) throws EntornNotFoundException, ExpedientTipusNotFoundException, EstatNotFoundException {
		mesuresTemporalsHelper.mesuraIniciar("CONSULTA GENERAL EXPEDIENTS v3", "consulta");
		mesuresTemporalsHelper.mesuraIniciar("CONSULTA GENERAL EXPEDIENTS v3", "consulta", null, null, "0");
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		logger.debug("Consulta general d'expedients paginada (entornId=" + entornId + "expedientTipusId=" + expedientTipusId + ")");
		// Comprova l'accés a l'entorn
		Entorn entorn = entornRepository.findOne(entornId);
		if (entorn == null) {
			logger.debug("No s'ha trobat l'entorn (entornId=" + entornId + ")");
			throw new EntornNotFoundException();
		} else {
			boolean ambPermis = permisosHelper.isGrantedAny(
					entorn.getId(),
					Entorn.class,
					new Permission[] {
						ExtendedPermission.READ,
						ExtendedPermission.ADMINISTRATION});
			if (!ambPermis) {
				logger.debug("No es tenen permisos per accedir a l'entorn (entornId=" + entornId + ")");
				throw new EntornNotFoundException();
			}
		}
		// Comprova l'accés al tipus d'expedient
		ExpedientTipus expedientTipus = null;
		if (expedientTipusId != null) {
			expedientTipus = expedientTipusRepository.findOne(expedientTipusId);
			if (expedientTipus == null) {
				logger.debug("No s'ha trobat l'expedientTipus (expedientTipusId=" + expedientTipusId + ")");
				throw new ExpedientTipusNotFoundException();
			} else {
				boolean ambPermis = permisosHelper.isGrantedAny(
						expedientTipus.getId(),
						ExpedientTipus.class,
						new Permission[] {
							ExtendedPermission.READ,
							ExtendedPermission.ADMINISTRATION});
				if (!ambPermis) {
					logger.debug("No es tenen permisos per accedir a l'expedientTipus (expedientTipusId=" + expedientTipusId + ")");
					throw new ExpedientTipusNotFoundException();
				}
			}
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
					ExtendedPermission.ADMINISTRATION});
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
			List<Long> idsExpedients = expedientRepository.findByIdFiltreGeneralPaginat(
					entornId,
					tipusPermesos,
					(expedientTipusId == null),
					expedientTipusId,
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
					(!EstatTipusDto.CUSTOM.equals(estatTipus) || estatId == null),
					estatId,
					(geoPosX == null),
					geoPosX,
					(geoPosY == null),
					geoPosY,
					(geoReferencia == null),
					geoReferencia,
					mostrarAnulats);	
			List<Long> ids = jbpmHelper.findRootProcessInstancesForExpedientsWithActiveTasksCommand(auth.getName(), idsExpedients);
			Set<String> idsDiferents = new HashSet<String>();
			for (Long id: ids) 
				idsDiferents.add(id.toString());
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
				entornId,
				tipusPermesos,
				(expedientTipusId == null),
				expedientTipusId,
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
				(!EstatTipusDto.CUSTOM.equals(estatTipus) || estatId == null),
				estatId,
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
				paginacioHelper.toSpringDataPageable(paginacioParams));			
		
		mesuresTemporalsHelper.mesuraCalcular("CONSULTA GENERAL EXPEDIENTS v3", "consulta", null, null, "2");
		mesuresTemporalsHelper.mesuraIniciar("CONSULTA GENERAL EXPEDIENTS v3", "consulta", null, null, "3");
		PaginaDto<ExpedientDto> resposta = paginacioHelper.toPaginaDto(
				paginaResultats,
				ExpedientDto.class);
		mesuresTemporalsHelper.mesuraCalcular("CONSULTA GENERAL EXPEDIENTS v3", "consulta", null, null, "3");
		mesuresTemporalsHelper.mesuraCalcular("CONSULTA GENERAL EXPEDIENTS v3", "consulta");
		return resposta;
	}

	@Transactional
	public void createRelacioExpedient(
			Long expedientOrigenId,
			Long expedientDestiId) {
		logger.debug("Nova relació d'expedients (expedientOrigenId=" + expedientOrigenId + "expedientDestiId=" + expedientDestiId + ")");
		ExpedientLog expedientLog = expedientLoggerHelper.afegirLogExpedientPerExpedient(
				expedientOrigenId,
				ExpedientLogAccioTipus.EXPEDIENT_RELACIO_AFEGIR,
				expedientDestiId.toString());
		expedientLog.setEstat(ExpedientLogEstat.IGNORAR);
		Expedient origen = expedientRepository.findOne(expedientOrigenId);
		Expedient desti = expedientRepository.findOne(expedientDestiId);
		boolean existeix = false;
		for (Expedient relacionat: origen.getRelacionsOrigen()) {
			if (relacionat.getId().longValue() == expedientDestiId.longValue()) {
				existeix = true;
				break;
			}
		}
		if (!existeix)
			origen.addRelacioOrigen(desti);
		existeix = false;
		for (Expedient relacionat: desti.getRelacionsOrigen()) {
			if (relacionat.getId().longValue() == expedientOrigenId.longValue()) {
				existeix = true;
				break;
			}
		}
		if (!existeix)
			desti.addRelacioOrigen(origen);
	}

	private ServiceUtils getServiceUtils() {
		if (serviceUtils == null) {
			serviceUtils = new ServiceUtils();
		}
		return serviceUtils;
	}

	@Transactional
	public void processInstanceTokenRedirect(
			long tokenId,
			String nodeName,
			boolean cancelarTasques) {
		logger.debug("Redirigir token (tokenId=" + tokenId + ", nodeName=" + nodeName + ", cancelarTasques=" + cancelarTasques + ")");
		jbpmHelper.tokenRedirect(
				tokenId,
				nodeName,
				cancelarTasques,
				true,
				false);
	}

	@Transactional
	public void alertaCrear(
			Long entornId,
			Long expedientId,
			Date data,
			String usuariCodi,
			String text) throws EntornNotFoundException, ExpedientNotFoundException {
		logger.debug("Crear alerta per expedient (entornId=" + entornId + ", expedientId=" + expedientId + ", usuariCodi=" + usuariCodi + ")");
		Entorn entorn = entornRepository.findOne(entornId);
		if (entorn == null)
			throw new EntornNotFoundException();
		Expedient expedient = expedientRepository.findOne(expedientId);
		if (expedient == null)
			throw new ExpedientNotFoundException();
		Alerta alerta = new Alerta(
				data,
				usuariCodi,
				text,
				entorn);
		alerta.setExpedient(expedient);
		alertaRepository.save(alerta);
	}
	@Transactional
	public void alertaEsborrarAmbTaskInstanceId(
			long taskInstanceId) throws TaskInstanceNotFoundException {
		Date ara = new Date();
		List<TerminiIniciat> terminis = terminiIniciatRepository.findByTaskInstanceId(
				new Long(taskInstanceId).toString());
		for (TerminiIniciat termini: terminis) {
			for (Alerta alerta: termini.getAlertes())
				alerta.setDataEliminacio(ara);
		}		
	}

	@Transactional(readOnly = true)
	public List<ExpedientDadaDto> findDadesPerExpedient(Long expedientId) {
		logger.debug("Consulta de dades de l'expedient (id=" + expedientId + ")");
		Expedient expedient = expedientHelper.geExpedientComprovantPermisosAny(
				expedientId,
				new Permission[] {
						ExtendedPermission.READ,
						ExtendedPermission.ADMINISTRATION});
		return variableHelper.findDadesPerInstanciaProces(
				expedient.getProcessInstanceId());
	}

	@Transactional(readOnly = true)
	public ExpedientDadaDto getDadaPerProcessInstance(
			String processInstanceId,
			String variableCodi) {
		logger.debug("Obtenir dada per la variable (processInstanceId=" + processInstanceId + ", variableCodi=" + variableCodi + ")");
		ExpedientDadaDto dto = variableHelper.getDadaPerInstanciaProces(
				processInstanceId,
				variableCodi);
		return dto;
	}

	@Transactional(readOnly = true)
	public List<ExpedientDocumentDto> findDocumentsPerExpedient(Long expedientId) {
		logger.debug("Consulta de documents de l'expedient (id=" + expedientId + ")");
		Expedient expedient = expedientHelper.geExpedientComprovantPermisosAny(
				expedientId,
				new Permission[] {
						ExtendedPermission.READ,
						ExtendedPermission.ADMINISTRATION});
		return documentHelper.findDocumentsPerInstanciaProces(
				expedient.getProcessInstanceId());
	}

	@Transactional(readOnly = true)
	public ArxiuDto getArxiuExpedient(
			Long expedientId,
			Long documentStoreId) {
		logger.debug("Obtenint contingut de l'arxiu per l'expedient (expedientId=" + expedientId + ", documentStoreId=" + documentStoreId + ")");
		Expedient expedient = expedientHelper.geExpedientComprovantPermisosAny(
				expedientId,
				new Permission[] {
						ExtendedPermission.READ,
						ExtendedPermission.ADMINISTRATION});
		DocumentStore documentStore = documentStoreRepository.findOne(documentStoreId);
		if (documentStore == null)
			throw new DocumentDescarregarException("No s'ha trobat el documentStore (id=" + documentStoreId + ", expedientId=" + expedientId + ")");
		List<JbpmProcessInstance> processInstances = jbpmHelper.getProcessInstanceTree(expedient.getProcessInstanceId());
		boolean trobat = false;
		for (JbpmProcessInstance processInstance: processInstances) {
			String processInstanceId = new Long(processInstance.getId()).toString();
			if (processInstanceId.equals(documentStore.getProcessInstanceId())) {
				trobat = true;
				break;
			}
		}
		if (!trobat)
			throw new DocumentDescarregarException("El documentStore no pertany a l'expedient (id=" + documentStoreId + ", expedientId=" + expedientId + ", documentStore.processInstanceId=" + documentStore.getProcessInstanceId() + ")");
		return documentHelper.getArxiuPerDocumentStoreId(
				documentStoreId,
				false,
				false,
				false);
	}

	// No pot ser readOnly per mor de la cache de les tasques
	@Transactional
	public List<ExpedientTascaDto> findTasquesPerExpedient(Long expedientId) {
		logger.debug("Consulta de tasques de l'expedient (id=" + expedientId + ")");
		Expedient expedient = expedientHelper.geExpedientComprovantPermisosAny(
				expedientId,
				new Permission[] {
						ExtendedPermission.READ,
						ExtendedPermission.ADMINISTRATION});
		return tascaHelper.findTasquesPerExpedient(
				expedient);
	}
	// No pot ser readOnly per mor de la cache de les tasques
	@Transactional
	public ExpedientTascaDto getTascaPerExpedient(
			Long expedientId,
			String tascaId) {
		logger.debug("Obtenció de la tasca de l'expedient (expedientId=" + expedientId + ", tascaId=" + tascaId + ")");
		Expedient expedient = expedientHelper.geExpedientComprovantPermisosAny(
				expedientId,
				new Permission[] {
						ExtendedPermission.READ,
						ExtendedPermission.ADMINISTRATION});
		ExpedientTascaDto resposta = tascaHelper.getTascaPerExpedient(
				expedient,
				tascaId,
				true,
				true);
		return resposta;
	}

	@Transactional(readOnly = true)
	public List<PersonaDto> findParticipantsPerExpedient(Long expedientId) {
		logger.debug("Consulta de tasques de l'expedient (id=" + expedientId + ")");
		Expedient expedient = expedientHelper.geExpedientComprovantPermisosAny(
				expedientId,
				new Permission[] {
						ExtendedPermission.READ,
						ExtendedPermission.ADMINISTRATION});
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

	@Transactional(readOnly = true)
	public List<CampAgrupacioDto> findAgrupacionsDadesExpedient(Long expedientId) {
		logger.debug("Consulta de les agrupacions de dades de l'expedient (id=" + expedientId + ")");
		Expedient expedient = expedientHelper.geExpedientComprovantPermisosAny(
				expedientId,
				new Permission[] {
						ExtendedPermission.READ,
						ExtendedPermission.ADMINISTRATION});
		DefinicioProces definicioProces = expedientHelper.findDefinicioProcesByProcessInstanceId(expedient.getProcessInstanceId());
		return conversioTipusHelper.convertirList(
				definicioProces.getAgrupacions(),
				CampAgrupacioDto.class);
	}

	@Transactional(readOnly = true)
	@Override
	public List<DominiRespostaFilaDto> dominiConsultar(
			String processInstanceId,
			String dominiCodi,
			String dominiId,
			Map<String, Object> parametres) throws DominiNotFoundException, SistemaExternException {
		Expedient expedient = expedientHelper.findExpedientByProcessInstanceId(processInstanceId);
		Domini domini = dominiRepository.findByExpedientTipusAndCodi(
				expedient.getTipus(),
				dominiCodi);
		if (domini == null)
			domini = dominiRepository.findByEntornAndCodi(
					expedient.getEntorn(),
					dominiCodi);
		try {
			DominiDto dominiDto = new ModelMapper().map(domini, DominiDto.class);
			List<FilaResultat> resposta = dominiHelper.consultar(
					dominiDto,
					dominiId,
					parametres);
			return conversioTipusHelper.convertirList(
					resposta,
					DominiRespostaFilaDto.class);
		} catch (Exception ex) {
			throw new SistemaExternException(ex);
		}
	}
	@Transactional(readOnly = true)
	public List<EnumeracioValorDto> enumeracioConsultar(
			String processInstanceId,
			String enumeracioCodi) throws EnumeracioNotFoundException {
		Expedient expedient = expedientHelper.findExpedientByProcessInstanceId(processInstanceId);
		Enumeracio enumeracio = enumeracioRepository.findByExpedientTipusAndCodi(
				expedient.getTipus(),
				enumeracioCodi);
		if (enumeracio == null)
			enumeracio = enumeracioRepository.findByEntornAndCodi(
					expedient.getEntorn(),
					enumeracioCodi);
		return conversioTipusHelper.convertirList(
				enumeracio.getEnumeracioValors(),
				EnumeracioValorDto.class);
	}

	@Transactional
	public ExpedientDto getExpedientIniciant() {
		return conversioTipusHelper.convertir(
				ExpedientIniciantDto.getExpedient(),
				ExpedientDto.class);
	}

	private static final Logger logger = LoggerFactory.getLogger(ExpedientServiceImpl.class);



	@Transactional
	public boolean updateExpedientError(String processInstanceId,
			String errorDesc, String errorFull) {
		// TODO Auto-generated method stub
		return false;
	}

	@Transactional
	public void editar(
			Long entornId,
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
			String grupCodi) {
		editar(entornId,
				id,
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
				false);
	}
	
	@Transactional
	public String getNumeroExpedientDefaultActual(
			Long entornId,
			ExpedientTipus expedientTipus,
			Integer any) {
		long increment = 0;
		String numero = null;
		Expedient expedient = null;
		if (any == null) 
			any = Calendar.getInstance().get(Calendar.YEAR);
		do {
			numero = expedientTipusDao.getNumeroExpedientDefaultActual(
					expedientTipus.getId(),
					any.intValue(),
					increment);
			expedient = expedientDao.findAmbEntornTipusINumeroDefault(
					entornId,
					expedientTipus.getId(),
					numero);
			increment++;
		} while (expedient != null);
		if (increment > 1)
			expedientTipus.updateSequenciaDefault(any, increment - 1);
		return numero;
	}
	
	@Transactional
	public void editar(
			Long entornId,
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
			boolean executatEnHandler) {
		Expedient expedient = expedientHelper.findAmbEntornIId(entornId, id);
		
		if (!executatEnHandler) {
			ExpedientLog elog = expedientLoggerHelper.afegirLogExpedientPerExpedient(
				id,
				ExpedientLogAccioTipus.EXPEDIENT_MODIFICAR,
				null);
			elog.setEstat(ExpedientLogEstat.IGNORAR);
		}

		String informacioVella = getInformacioExpedient(expedient);
		
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
		if (!"".equals(responsableCodi) && !StringUtils.equals(expedient.getResponsableCodi(), responsableCodi)) {
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
			if (expedient.getEstat() == null) {
				expedientLoggerHelper.afegirProcessLogInfoExpedient(
						expedient.getProcessInstanceId(), 
						LogInfo.ESTAT + "#@#" + "---");
				expedient.setEstat(estatRepository.findById(estatId));
			} else if (expedient.getEstat().getId() != estatId){
				expedientLoggerHelper.afegirProcessLogInfoExpedient(
						expedient.getProcessInstanceId(), 
						LogInfo.ESTAT + "#@#" + expedient.getEstat().getId());
				expedient.setEstat(estatRepository.findById(estatId));
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
		String informacioNova = getInformacioExpedient(expedient);
		registreDao.crearRegistreModificarExpedient(
				expedient.getId(),
				getUsuariPerRegistre(),
				informacioVella,
				informacioNova);
	}

	@Transactional
	private String getUsuariPerRegistre() {
		if (SecurityContextHolder.getContext() != null && SecurityContextHolder.getContext().getAuthentication() != null)
			return SecurityContextHolder.getContext().getAuthentication().getName();
		else
			return "Procés automàtic";
	}

	@Transactional
	private String getInformacioExpedient(Expedient expedient) {
		StringBuilder sb = new StringBuilder();
		sb.append("[");
		if (expedient.getTitol() != null)
			sb.append("titol: \"" + expedient.getTitol() + "\", ");
		if (expedient.getNumero() != null)
			sb.append("numero: \"" + expedient.getNumero() + "\", ");
		if (expedient.getEstat() != null)
			sb.append("estat: \"" + expedient.getEstat().getNom() + "\", ");
		sb.append("dataInici: \"" + expedient.getDataInici() + "\", ");
		if (expedient.getDataFi() != null)
			sb.append("dataFi: \"" + expedient.getDataFi() + "\", ");
		if (expedient.getComentari() != null && expedient.getComentari().length() > 0)
			sb.append("comentari: \"" + expedient.getComentari() + "\", ");
		if (expedient.getResponsableCodi() != null)
			sb.append("responsableCodi: \"" + expedient.getResponsableCodi() + "\", ");
		sb.append("iniciadorCodi: \"" + expedient.getIniciadorCodi() + "\"");
		sb.append("]");
		return sb.toString();
	}

	@Transactional
	public List<InstanciaProcesDto> getArbreInstanciesProces(
				Long processInstanceId) {
		List<InstanciaProcesDto> resposta = new ArrayList<InstanciaProcesDto>();
		JbpmProcessInstance rootProcessInstance = jbpmHelper.getRootProcessInstance(String.valueOf(processInstanceId));
		List<JbpmProcessInstance> piTree = jbpmHelper.getProcessInstanceTree(rootProcessInstance.getId());
		for (JbpmProcessInstance jpi: piTree) {
			resposta.add(dtoConverter.toInstanciaProcesDto(jpi.getId(), false, false, false));
		}
		return resposta;
	}

	@Transactional
	public InstanciaProcesDto getInstanciaProcesById(String processInstanceId, boolean ambImatgeProces, boolean ambVariables, boolean ambDocuments) {
		return dtoConverter.toInstanciaProcesDto(processInstanceId, ambImatgeProces, ambVariables, ambDocuments);
	}

	@Transactional
	public InstanciaProcesDto getInstanciaProcesByIdReg(String processInstanceId, boolean ambImatgeProces, boolean ambVariables, boolean ambDocuments, String varRegistre, Object[] valorsRegistre) {
		return dtoConverter.toInstanciaProcesDto(processInstanceId, ambImatgeProces, ambVariables, ambDocuments, varRegistre, valorsRegistre);
	}

	@Transactional
	public List<RegistreDto> getRegistrePerExpedient(Long expedientId) {
		List<Registre> registre = registreRepository.findByExpedientId(expedientId);
		return conversioTipusHelper.convertirList(registre, RegistreDto.class);
	}

	@Transactional
	public List<ExpedientLogDto> getLogsOrdenatsPerData(ExpedientDto expedient) {
		mesuresTemporalsHelper.mesuraIniciar("Expedient REGISTRE", "expedient", expedient.getTipus().getNom(), null, "findAmbExpedientIdOrdenatsPerData");
		List<ExpedientLogDto> resposta = new ArrayList<ExpedientLogDto>();
		List<ExpedientLog> logs = expedientLogRepository.findAmbExpedientIdOrdenatsPerData(expedient.getId());
		String parentProcessInstanceId = null;
		Map<String, String> processos = new HashMap<String, String>();
		mesuresTemporalsHelper.mesuraCalcular("Expedient REGISTRE", "expedient", expedient.getTipus().getNom(), null, "findAmbExpedientIdOrdenatsPerData");
		mesuresTemporalsHelper.mesuraIniciar("Expedient REGISTRE", "expedient", expedient.getTipus().getNom(), null, "obtenir tokens");
		for (ExpedientLog log: logs) {
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
						}
					}
					tokenName = processos.get(processInstanceId) + tokenName;
				}
			}
				
			ExpedientLogDto dto = new ExpedientLogDto();
			dto.setId(log.getId());
			dto.setData(log.getData());
			dto.setUsuari(log.getUsuari());
			dto.setEstat(log.getEstat().name());
			dto.setAccioTipus(log.getAccioTipus().name());
			dto.setAccioParams(log.getAccioParams());
			dto.setTargetId(log.getTargetId());
			dto.setTokenName(tokenName);
			dto.setTargetTasca(log.isTargetTasca());
			dto.setTargetProces(log.isTargetProces());
			dto.setTargetExpedient(log.isTargetExpedient());
			resposta.add(dto);
		}
		mesuresTemporalsHelper.mesuraCalcular("Expedient REGISTRE", "expedient", expedient.getTipus().getNom(), null, "obtenir tokens");
		return resposta;
	}
	
	@Transactional
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
			if (	//log.getAccioTipus() == ExpedientLogAccioTipus.TASCA_REASSIGNAR ||
					!log.isTargetTasca() ||
					!taskIds.contains(log.getTargetId())) {
				taskIds.add(log.getTargetId());
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
							}
						}
						tokenName = processos.get(processInstanceId) + tokenName;
					}
				}
				
				ExpedientLogDto dto = new ExpedientLogDto();
				dto.setId(log.getId());
				dto.setData(log.getData());
				dto.setUsuari(log.getUsuari());
				dto.setEstat(log.getEstat().name());
				dto.setAccioTipus(log.getAccioTipus().name());
				dto.setAccioParams(log.getAccioParams());
				dto.setTargetId(log.getTargetId());
				dto.setTokenName(tokenName);
				dto.setTargetTasca(log.isTargetTasca());
				dto.setTargetProces(log.isTargetProces());
				dto.setTargetExpedient(log.isTargetExpedient());
				resposta.add(dto);
			}
		}
		mesuresTemporalsHelper.mesuraCalcular("Expedient REGISTRE", "expedient", expedient.getTipus().getNom(), null, "obtenir tokens tasca");
		return resposta;
	}
	
	@Transactional
	public Map<String, ExpedientTascaDto> getTasquesPerLogExpedient(Long expedientId) {
		List<ExpedientLog> logs = expedientLogRepository.findAmbExpedientIdOrdenatsPerData(expedientId);
		Map<String, ExpedientTascaDto> tasquesPerLogs = new HashMap<String, ExpedientTascaDto>();
		for (ExpedientLog log: logs) {
			if (log.isTargetTasca()) {
				JbpmTask task = jbpmHelper.getTaskById(log.getTargetId());
				if (task != null)
					tasquesPerLogs.put(
							log.getTargetId(),
							dtoConverter.toExpedientTascaDto(
									task,
									null,
									false,
									false,
									true,
									true,
									true));
			}
		}
		return tasquesPerLogs;
	}
	
	@Transactional
	public List<ExpedientConsultaDissenyDto> findAmbEntornConsultaDisseny(
			Long entornId,
			Long consultaId,
			Map<String, Object> valors,
			String sort,
			boolean asc) {
		return findAmbEntornConsultaDisseny(
				entornId,
				consultaId,
				valors,
				sort,
				asc,
				0,
				-1);
	}

	@Transactional
	private void afegirValorsPredefinits(
			Consulta consulta,
			Map<String, Object> valors,
			List<Camp> camps) {
		if (consulta.getValorsPredefinits() != null && consulta.getValorsPredefinits().length() > 0) {
			String[] parelles = consulta.getValorsPredefinits().split(",");
			for (String parelle : parelles) {
				String[] parella = (parelle.contains(":")) ? parelle.split(":") : parelle.split("=");
				if (parella.length == 2) {
					String campCodi = parella[0];
					String valor = parella[1];
					for (Camp camp: camps) {
						if (camp.getCodi().equals(campCodi)) {
							valors.put(
									camp.getDefinicioProces().getJbpmKey() + "." + campCodi,
									Camp.getComObject(
											camp.getTipus(),
											valor));
							break;
						}
					}
				}
			}
		}
	}

	@Transactional
	public List<ExpedientConsultaDissenyDto> findAmbEntornConsultaDisseny(
			Long entornId,
			Long consultaId,
			Map<String, Object> valors,
			String sort,
			boolean asc,
			int firstRow,
			int maxResults) {
		List<ExpedientConsultaDissenyDto> resposta = new ArrayList<ExpedientConsultaDissenyDto>();
		Consulta consulta = consultaHelper.findById(consultaId);		
		
		List<Camp> campsFiltre = getServiceUtils().findCampsPerCampsConsulta(
				consulta,
				TipusConsultaCamp.FILTRE);
		List<Camp> campsInforme = getServiceUtils().findCampsPerCampsConsulta(
				consulta,
				TipusConsultaCamp.INFORME);
		afegirValorsPredefinits(consulta, valors, campsFiltre);
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
		
		List<CampDto> campsInformeDto = new ArrayList<CampDto>();
		for(Camp campTmp : campsInforme) {
			campsInformeDto.add(new ModelMapper().map(campTmp, CampDto.class));
		}
		for (Map<String, DadaIndexadaDto> dadesExpedient: dadesExpedients) {
			DadaIndexadaDto dadaExpedientId = dadesExpedient.get(LuceneHelper.CLAU_EXPEDIENT_ID);
			ExpedientConsultaDissenyDto fila = new ExpedientConsultaDissenyDto();
			Expedient expedient = expedientRepository.findById(Long.parseLong(dadaExpedientId.getValorIndex()));
			if (expedient != null) {
				fila.setExpedient(
						dtoConverter.toExpedientDto(
								expedient,
								false));
				dtoConverter.revisarDadesExpedientAmbValorsEnumeracionsODominis(
						dadesExpedient,
						campsInformeDto);
				fila.setDadesExpedient(dadesExpedient);
				resposta.add(fila);
			}
			dadesExpedient.remove(LuceneHelper.CLAU_EXPEDIENT_ID);
		}
		return resposta;
	}

	@Transactional
	public List<ExpedientLogDto> findLogsTascaOrdenatsPerData(Long targetId) {
		List<ExpedientLog> logs = expedientLogRepository.findLogsTascaByIdOrdenatsPerData(String.valueOf(targetId));
		List<ExpedientLogDto> resposta = new ArrayList<ExpedientLogDto>();
		for (ExpedientLog log: logs) {
			ExpedientLogDto dto = new ExpedientLogDto();
			dto.setId(log.getId());
			dto.setData(log.getData());
			dto.setUsuari(log.getUsuari());
			dto.setEstat(log.getEstat().name());
			dto.setAccioTipus(log.getAccioTipus().name());
			dto.setAccioParams(log.getAccioParams());
			dto.setTargetId(log.getTargetId());
			dto.setTargetTasca(log.isTargetTasca());
			dto.setTargetProces(log.isTargetProces());
			dto.setTargetExpedient(log.isTargetExpedient());
			resposta.add(dto);
		}
		return resposta;
	}

	@Transactional
	@Override
	public void retrocedirFinsLog(Long expedientLogId, boolean retrocedirPerTasques) {
		ExpedientLog log = expedientLogRepository.findById(expedientLogId);
		mesuresTemporalsHelper.mesuraIniciar("Retrocedir" + (retrocedirPerTasques ? " per tasques" : ""), "expedient", log.getExpedient().getTipus().getNom());
		ExpedientLog logRetroces = expedientLoggerHelper.afegirLogExpedientPerExpedient(
				log.getExpedient().getId(),
				retrocedirPerTasques ? ExpedientLogAccioTipus.EXPEDIENT_RETROCEDIR_TASQUES : ExpedientLogAccioTipus.EXPEDIENT_RETROCEDIR,
				expedientLogId.toString());
		expedientLoggerHelper.retrocedirFinsLog(log, retrocedirPerTasques, logRetroces.getId());
		logRetroces.setEstat(ExpedientLogEstat.IGNORAR);
		getServiceUtils().expedientIndexLuceneUpdate(
				log.getExpedient().getProcessInstanceId());
		mesuresTemporalsHelper.mesuraCalcular("Retrocedir" + (retrocedirPerTasques ? " per tasques" : ""), "expedient", log.getExpedient().getTipus().getNom());
	}

	@Transactional
	@Override
	public List<ExpedientLogDto> findLogsRetroceditsOrdenatsPerData(Long logId) {
		List<ExpedientLog> logs = expedientLoggerHelper.findLogsRetrocedits(logId);
		List<ExpedientLogDto> resposta = new ArrayList<ExpedientLogDto>();
		for (ExpedientLog log: logs) {
			ExpedientLogDto dto = new ExpedientLogDto();
			dto.setId(log.getId());
			dto.setData(log.getData());
			dto.setUsuari(log.getUsuari());
			dto.setEstat(log.getEstat().name());
			dto.setAccioTipus(log.getAccioTipus().name());
			dto.setAccioParams(log.getAccioParams());
			dto.setTargetId(log.getTargetId());
			dto.setTargetTasca(log.isTargetTasca());
			dto.setTargetProces(log.isTargetProces());
			dto.setTargetExpedient(log.isTargetExpedient());
			resposta.add(dto);
		}
		return resposta;
	}
	
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

	@Transactional
	@Override
	public void delete(Long entornId, Long expedientId) {
		Expedient expedient = expedientRepository.findByEntornIdAndId(entornId, expedientId);
		if (expedient != null) {
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
			for (ExecucioMassivaExpedient eme: execucioMassivaExpedientRepository.getExecucioMassivaByExpedient(expedientId)) {
				execucioMassivaExpedientRepository.delete(eme);
			}
			expedientRepository.delete(expedient);
			luceneHelper.deleteExpedientAsync(expedient);
			registreDao.crearRegistreEsborrarExpedient(
					expedient.getId(),
					SecurityContextHolder.getContext().getAuthentication().getName());
		} else {
			throw new NotFoundException(getServiceUtils().getMessage("error.expedientService.noExisteix"));
		}
	}

	@Transactional
	@Override
	public List<ExpedientDto> findAmbEntornLikeIdentificador(Long entornId, String text) {
		List<ExpedientDto> resposta = new ArrayList<ExpedientDto>();
		List<Expedient> expedients = expedientRepository.findAmbEntornLikeIdentificador(entornId, text);
		for (Expedient expedient : expedients) {
			resposta.add(new ModelMapper().map(expedient, ExpedientDto.class));
		}
		return resposta;
	}

	@Transactional
	@Override
	public void deleteRelacioExpedient(Long expedientIdOrigen, Long expedientIdDesti) {
		ExpedientLog expedientLog = expedientLoggerHelper.afegirLogExpedientPerExpedient(
				expedientIdOrigen,
				ExpedientLogAccioTipus.EXPEDIENT_RELACIO_ESBORRAR,
				expedientIdDesti.toString());
		expedientLog.setEstat(ExpedientLogEstat.IGNORAR);
		Expedient origen = expedientRepository.findById(expedientIdOrigen);
		Expedient desti = expedientRepository.findById(expedientIdDesti);
		origen.removeRelacioOrigen(desti);
		desti.removeRelacioOrigen(origen);		
	}

	@Transactional
	@Override
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
	
	@Transactional
	@Override
	public synchronized ExpedientDto iniciar(
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
		if (usuari != null)
			comprovarUsuari(usuari);
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String usuariBo = (usuari != null) ? usuari : auth.getName();
		ExpedientTipus expedientTipus = expedientTipusRepository.findById(expedientTipusId);
		ExpedientTipusDto expedientTipusDto = new ModelMapper().map(expedientTipus, ExpedientTipusDto.class);
		String mesuraTempsTotalPrefix = "INIEXP_TOT_" + expedientTipus.getCodi();
		String mesuraTempsIncrementalPrefix = "INIEXP_INC_" + expedientTipus.getCodi();
		MesurarTemps.diferenciaReiniciar(mesuraTempsTotalPrefix);
		MesurarTemps.diferenciaReiniciar(mesuraTempsIncrementalPrefix);
		Entorn entorn = entornRepository.findOne(entornId);
		textBloqueigIniciExpedient = auth.getName() + " (" +
				"entornCodi=" + entorn.getCodi() + ", " +
				"expedientTipusCodi=" + expedientTipus.getCodi() + ", " +
				"data=" + new Date() + ")";
		try {
			String iniciadorCodiCalculat = (iniciadorTipus.equals(IniciadorTipusDto.INTERN)) ? usuariBo : iniciadorCodi;
			Expedient expedient = new Expedient();
			MesurarTemps.diferenciaImprimirStdoutIReiniciar(mesuraTempsIncrementalPrefix, "0");
			expedient.setTipus(expedientTipus);
			expedient.setIniciadorTipus(new ModelMapper().map(iniciadorTipus, IniciadorTipus.class));
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
			MesurarTemps.diferenciaImprimirStdoutIReiniciar(mesuraTempsIncrementalPrefix, "1");
			expedient.setNumeroDefault(
					getNumeroExpedientDefaultActual(
							entornId,
							expedientTipus,
							any));
			MesurarTemps.diferenciaImprimirStdoutIReiniciar(mesuraTempsIncrementalPrefix, "2");
			if (expedientTipus.getTeNumero()) {
				if (numero != null && numero.length() > 0 && expedientTipus.getDemanaNumero()) {
					expedient.setNumero(numero);
				} else {
					expedient.setNumero(
							getNumeroExpedientActual(
									entornId,
									expedientTipusDto,
									any));
				}
			}
	
			// Verifica si l'expedient té el número repetit
			MesurarTemps.diferenciaImprimirStdoutIReiniciar(mesuraTempsIncrementalPrefix, "3");
			if (expedientDao.findAmbEntornTipusINumero(
					entornId,
					expedientTipusId,
					expedient.getNumero()) != null) {
				throw new ExpedientRepetitException(
						getServiceUtils().getMessage(
								"error.expedientService.jaExisteix",
								new Object[]{expedient.getNumero()}) );
			}
			// Actualitza l'any actual de l'expedient
			MesurarTemps.diferenciaImprimirStdoutIReiniciar(mesuraTempsIncrementalPrefix, "4");
			int anyActual = Calendar.getInstance().get(Calendar.YEAR);
			if (any == null || any.intValue() == anyActual) {
				if (expedientTipus.getAnyActual() == 0) {
					expedientTipus.setAnyActual(anyActual);
				} else if (expedientTipus.getAnyActual() < anyActual) {
					expedientTipus.setAnyActual(anyActual);
				}
			}
			// Actualitza la seqüència del número d'expedient
			MesurarTemps.diferenciaImprimirStdoutIReiniciar(mesuraTempsIncrementalPrefix, "5");
			if (expedientTipus.getTeNumero() && expedientTipus.getExpressioNumero() != null && !"".equals(expedientTipus.getExpressioNumero())) {
				if (expedient.getNumero().equals(
						getNumeroExpedientActual(
								entornId,
								expedientTipusDto,
								any)))
					expedientTipus.updateSequencia(any, 1);
			}
			// Actualitza la seqüència del número d'expedient per defecte
			if (expedient.getNumeroDefault().equals(getNumeroExpedientDefaultActual(entornId, expedientTipus, any)))
				expedientTipus.updateSequenciaDefault(any, 1);
			// Configura el títol de l'expedient
			if (expedientTipus.getTeTitol()) {
				if (titol != null && titol.length() > 0)
					expedient.setTitol(titol);
				else
					// TODO: multiidioma
					expedient.setTitol("[Sense títol]");
			}
			// Inicia l'instància de procés jBPM
			MesurarTemps.diferenciaImprimirStdoutIReiniciar(mesuraTempsIncrementalPrefix, "6");
			ExpedientIniciantDto.setExpedient(dtoConverter.toExpedientDto(expedient, true));
			DefinicioProces definicioProces = null;
			if (definicioProcesId != null) {
				definicioProces = definicioProcesRepository.findById(definicioProcesId);
			} else {
				definicioProces = definicioProcesRepository.findDarreraVersioAmbEntornIJbpmKey(
						entornId,
						expedientTipus.getJbpmProcessDefinitionKey());
			}
			MesurarTemps.diferenciaImprimirStdoutIReiniciar(mesuraTempsIncrementalPrefix, "7");
			JbpmProcessInstance processInstance = jbpmHelper.startProcessInstanceById(
					usuariBo,
					definicioProces.getJbpmId(),
					variables);
			expedient.setProcessInstanceId(processInstance.getId());
			// Emmagatzema el nou expedient
			MesurarTemps.diferenciaImprimirStdoutIReiniciar(mesuraTempsIncrementalPrefix, "8");
			expedientRepository.save(expedient);
			// Afegim els documents
			MesurarTemps.diferenciaImprimirStdoutIReiniciar(mesuraTempsIncrementalPrefix, "9");
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
			MesurarTemps.diferenciaImprimirStdoutIReiniciar(mesuraTempsIncrementalPrefix, "10");
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
			// Verificar la ultima vegada que l'expedient va modificar el seu estat
			MesurarTemps.diferenciaImprimirStdoutIReiniciar(mesuraTempsIncrementalPrefix, "11");
			ExpedientLog log = expedientLoggerHelper.afegirLogExpedientPerProces(
					processInstance.getId(),
					ExpedientLogAccioTipus.EXPEDIENT_INICIAR,
					null);
			log.setEstat(ExpedientLogEstat.IGNORAR);
			// Actualitza les variables del procés
			MesurarTemps.diferenciaImprimirStdoutIReiniciar(mesuraTempsIncrementalPrefix, "12");
			jbpmHelper.signalProcessInstance(expedient.getProcessInstanceId(), transitionName);
			// Indexam l'expedient
			MesurarTemps.diferenciaImprimirStdoutIReiniciar(mesuraTempsIncrementalPrefix, "13");
			getServiceUtils().expedientIndexLuceneCreate(expedient.getProcessInstanceId());
			// Registra l'inici de l'expedient
			MesurarTemps.diferenciaImprimirStdoutIReiniciar(mesuraTempsIncrementalPrefix, "14");
			registreDao.crearRegistreIniciarExpedient(
					expedient.getId(),
					usuariBo);
			// Retorna la informació de l'expedient que s'ha iniciat
			ExpedientDto dto = dtoConverter.toExpedientDto(expedient, true);
			MesurarTemps.diferenciaImprimirStdoutIReiniciar(mesuraTempsIncrementalPrefix, "15");
			MesurarTemps.diferenciaImprimirStdout(mesuraTempsTotalPrefix);
			MesurarTemps.mitjaCalcular(mesuraTempsTotalPrefix, mesuraTempsTotalPrefix);
			MesurarTemps.mitjaImprimirStdout(mesuraTempsTotalPrefix);
			logger.debug("textBloqueigIniciExpedient: " + textBloqueigIniciExpedient);
			return dto;
		} finally {
			textBloqueigIniciExpedient = null;
		}
	}

	@Transactional
	@Override
	public String getNumeroExpedientActual(
			Long entornId,
			ExpedientTipusDto expedientTipus,
			Integer any) {
		long increment = 0;
		String numero = null;
		Expedient expedient = null;
		if (any == null) 
			any = Calendar.getInstance().get(Calendar.YEAR);
		do {
			numero = expedientTipusDao.getNumeroExpedientActual(
					expedientTipus.getId(),
					any.intValue(),
					increment);
			expedient = expedientDao.findAmbEntornTipusINumero(
					entornId,
					expedientTipus.getId(),
					numero);
			increment++;
		} while (expedient != null);
		if (increment > 1)
			expedientTipus.updateSequencia(any, increment - 1);
		return numero;
	}

	@Transactional
	@Override
	public ExpedientDto findExpedientAmbEntornTipusITitol(Long entornId, Long expedientTipusId, String titol) {
		Expedient expedient = expedientDao.findAmbEntornTipusITitol(entornId, expedientTipusId, titol);
		if (expedient == null)
			return null;
		return dtoConverter.toExpedientDto(expedient, false);
	}

	@Transactional
	@Override
	public void anular(Long entornId, Long id, String motiu) { 
		Expedient expedient = expedientDao.findAmbEntornIId(entornId, id);
		if (expedient != null) {
			mesuresTemporalsHelper.mesuraIniciar("Anular", "expedient", expedient.getTipus().getNom());
			List<JbpmProcessInstance> processInstancesTree = jbpmHelper.getProcessInstanceTree(expedient.getProcessInstanceId());
			String[] ids = new String[processInstancesTree.size()];
			int i = 0;
			for (JbpmProcessInstance pi: processInstancesTree)
				ids[i++] = pi.getId();
			jbpmHelper.suspendProcessInstances(ids);
			expedient.setAnulat(true);
			expedient.setComentariAnulat(motiu);
			luceneHelper.deleteExpedientAsync(expedient);
			registreDao.crearRegistreAnularExpedient(
					expedient.getId(),
					SecurityContextHolder.getContext().getAuthentication().getName());
			mesuresTemporalsHelper.mesuraCalcular("Anular", "expedient", expedient.getTipus().getNom());
		} else {
			throw new NotFoundException(getServiceUtils().getMessage("error.expedientService.noExisteix"));
		}
	}
	
	@Transactional
	@Override
	public void suspendreTasca(
			Long entornId,
			String taskId) {
		JbpmTask task = jbpmHelper.getTaskById(taskId);
		expedientLoggerHelper.afegirLogExpedientPerProces(
				task.getProcessInstanceId(),
				ExpedientLogAccioTipus.TASCA_SUSPENDRE,
				null);
		jbpmHelper.suspendTaskInstance(taskId);
		registreDao.crearRegistreSuspendreTasca(
				getExpedientPerTaskInstanceId(taskId).getId(),
				taskId,
				SecurityContextHolder.getContext().getAuthentication().getName());
	}
	
	@Transactional
	@Override
	public void reprendreTasca(
			Long entornId,
			String taskId) {
		JbpmTask task = jbpmHelper.getTaskById(taskId);
		expedientLoggerHelper.afegirLogExpedientPerProces(
				task.getProcessInstanceId(),
				ExpedientLogAccioTipus.TASCA_CONTINUAR,
				null);
		jbpmHelper.resumeTaskInstance(taskId);
		registreDao.crearRegistreReprendreTasca(
				getExpedientPerTaskInstanceId(taskId).getId(),
				taskId,
				SecurityContextHolder.getContext().getAuthentication().getName());
	}
	
	@Transactional
	@Override
	public void cancelarTasca(
			Long entornId,
			String taskId) {
		JbpmTask task = jbpmHelper.getTaskById(taskId);
		expedientLoggerHelper.afegirLogExpedientPerProces(
				task.getProcessInstanceId(),
				ExpedientLogAccioTipus.TASCA_CANCELAR,
				null);
		jbpmHelper.cancelTaskInstance(taskId);
		registreDao.crearRegistreCancelarTasca(
				getExpedientPerTaskInstanceId(taskId).getId(),
				taskId,
				SecurityContextHolder.getContext().getAuthentication().getName());
	}
	
	@Transactional
	private Expedient getExpedientPerTaskInstanceId(String taskId) {
		JbpmTask task = jbpmHelper.getTaskById(taskId);
		JbpmProcessInstance pi = jbpmHelper.getRootProcessInstance(
				task.getProcessInstanceId());
		return expedientDao.findAmbProcessInstanceId(pi.getId());
	}

	@Transactional
	@Override
	public void reassignarTasca(Long entornId, String taskId, String expression) {
		reassignarTasca(entornId, taskId, expression, null);
	}
	
	@Transactional
	private void reassignarTasca(
			Long entornId,
			String taskId,
			String expression,
			String usuari) {
		String previousActors = expedientLoggerHelper.getActorsPerReassignacioTasca(taskId);
		ExpedientLog expedientLog = expedientLoggerHelper.afegirLogExpedientPerTasca(
				taskId,
				ExpedientLogAccioTipus.TASCA_REASSIGNAR,
				null);
		jbpmHelper.reassignTaskInstance(taskId, expression, entornId);
		String currentActors = expedientLoggerHelper.getActorsPerReassignacioTasca(taskId);
		expedientLog.setAccioParams(previousActors + "::" + currentActors);
		if (previousActors.startsWith("[")) {
			JbpmTask task = jbpmHelper.getTaskById(taskId);
			if (task != null) task.setFieldFromDescription(TascaService.TASKDESC_CAMP_AGAFADA, "true");
		}
		if (usuari == null) {
			usuari = SecurityContextHolder.getContext().getAuthentication().getName();
		}
		registreDao.crearRegistreRedirigirTasca(
				getExpedientPerTaskInstanceId(taskId).getId(),
				taskId,
				usuari,
				expression);
	}

	@Transactional
	@Override
	public List<Object> findLogIdTasquesById(List<ExpedientTascaDto> tasques) {
		List<String> tasquesIds = new ArrayList<String>();
		for (ExpedientTascaDto tasca : tasques) {
			tasquesIds.add(tasca.getId());
		}
		return expedientLoggerHelper.findLogIdTasquesById(tasquesIds);
	}
}
