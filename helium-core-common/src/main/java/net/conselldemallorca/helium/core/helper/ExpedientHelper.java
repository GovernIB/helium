/**
 * 
 */
package net.conselldemallorca.helium.core.helper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import net.conselldemallorca.helium.core.common.ExpedientIniciantDto;
import net.conselldemallorca.helium.core.helperv26.LuceneHelper;
import net.conselldemallorca.helium.core.helperv26.MesuresTemporalsHelper;
import net.conselldemallorca.helium.core.model.hibernate.Alerta;
import net.conselldemallorca.helium.core.model.hibernate.Camp;
import net.conselldemallorca.helium.core.model.hibernate.Camp.TipusCamp;
import net.conselldemallorca.helium.core.model.hibernate.DefinicioProces;
import net.conselldemallorca.helium.core.model.hibernate.Entorn;
import net.conselldemallorca.helium.core.model.hibernate.Estat;
import net.conselldemallorca.helium.core.model.hibernate.Expedient;
import net.conselldemallorca.helium.core.model.hibernate.Expedient.IniciadorTipus;
import net.conselldemallorca.helium.core.model.hibernate.ExpedientLog;
import net.conselldemallorca.helium.core.model.hibernate.ExpedientLog.ExpedientLogAccioTipus;
import net.conselldemallorca.helium.core.model.hibernate.ExpedientLog.ExpedientLogEstat;
import net.conselldemallorca.helium.core.model.hibernate.ExpedientLog.LogInfo;
import net.conselldemallorca.helium.core.model.hibernate.ExpedientTipus;
import net.conselldemallorca.helium.core.model.hibernate.SequenciaAny;
import net.conselldemallorca.helium.core.model.hibernate.SequenciaDefaultAny;
import net.conselldemallorca.helium.core.security.ExtendedPermission;
import net.conselldemallorca.helium.core.util.ExpedientCamps;
import net.conselldemallorca.helium.core.util.GlobalProperties;
import net.conselldemallorca.helium.jbpm3.integracio.JbpmHelper;
import net.conselldemallorca.helium.jbpm3.integracio.JbpmProcessInstance;
import net.conselldemallorca.helium.jbpm3.integracio.JbpmToken;
import net.conselldemallorca.helium.v3.core.api.dto.EntornDto;
import net.conselldemallorca.helium.v3.core.api.dto.EstatDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientDto.IniciadorTipusDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientTipusDto;
import net.conselldemallorca.helium.v3.core.api.exception.NoTrobatException;
import net.conselldemallorca.helium.v3.core.api.exception.PermisDenegatException;
import net.conselldemallorca.helium.v3.core.repository.AlertaRepository;
import net.conselldemallorca.helium.v3.core.repository.DefinicioProcesRepository;
import net.conselldemallorca.helium.v3.core.repository.EstatRepository;
import net.conselldemallorca.helium.v3.core.repository.ExpedientRepository;

import org.apache.commons.lang.StringUtils;
import org.jbpm.graph.exe.ProcessInstanceExpedient;
import org.jbpm.jpdl.el.ELException;
import org.jbpm.jpdl.el.ExpressionEvaluator;
import org.jbpm.jpdl.el.VariableResolver;
import org.jbpm.jpdl.el.impl.ExpressionEvaluatorImpl;
import org.springframework.security.acls.model.Permission;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * Helper per a gestionar els expedients.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class ExpedientHelper {

	@Resource
	private DefinicioProcesRepository definicioProcesRepository;
	@Resource
	private ExpedientRepository expedientRepository;
	@Resource
	private EstatRepository estatRepository;

	@Resource(name = "permisosHelperV3")
	private PermisosHelper permisosHelper;
	@Resource
	private JbpmHelper jbpmHelper;
	@Resource
	private ExpedientRegistreHelper expedientRegistreHelper;
	@Resource
	private ExpedientLoggerHelper expedientLoggerHelper;
	@Resource
	private PluginHelper pluginHelper;
	@Resource
	private LuceneHelper luceneHelper;
	@Resource
	private MessageHelper messageHelper;
	@Resource
	private ConversioTipusHelper conversioTipusHelper;
	@Resource
	private MesuresTemporalsHelper mesuresTemporalsHelper;
	@Resource
	private AlertaRepository alertaRepository;



	public ExpedientDto toExpedientDto(Expedient expedient) {
		ExpedientDto dto = new ExpedientDto();
		dto.setId(expedient.getId());
		dto.setProcessInstanceId(expedient.getProcessInstanceId());
		dto.setTitol(expedient.getTitol());
		dto.setNumero(expedient.getNumero());
		dto.setNumeroDefault(expedient.getNumeroDefault());
		dto.setComentari(expedient.getComentari());
		dto.setInfoAturat(expedient.getInfoAturat());
		dto.setComentariAnulat(expedient.getComentariAnulat());
		dto.setAnulat(expedient.isAnulat());
		dto.setDataInici(expedient.getDataInici());
		dto.setIniciadorCodi(expedient.getIniciadorCodi());
		dto.setIniciadorTipus(conversioTipusHelper.convertir(expedient.getIniciadorTipus(), IniciadorTipusDto.class));
		dto.setResponsableCodi(expedient.getResponsableCodi());
		dto.setGrupCodi(expedient.getGrupCodi());
		if (expedient.getIniciadorTipus().equals(IniciadorTipus.INTERN)) {
			if (expedient.getIniciadorCodi() != null)
				dto.setIniciadorPersona(
						pluginHelper.personaFindAmbCodi(expedient.getIniciadorCodi()));
			if (expedient.getResponsableCodi() != null)
				dto.setResponsablePersona(
						pluginHelper.personaFindAmbCodi(expedient.getResponsableCodi()));
		}
		if (expedient.getIniciadorTipus().equals(IniciadorTipus.SISTRA))
			dto.setBantelEntradaNum(expedient.getNumeroEntradaSistra());
		dto.setTipus(conversioTipusHelper.convertir(expedient.getTipus(), ExpedientTipusDto.class));
		dto.setEntorn(conversioTipusHelper.convertir(expedient.getEntorn(), EntornDto.class));
		if (expedient.getEstat() != null)
			dto.setEstat(conversioTipusHelper.convertir(expedient.getEstat(), EstatDto.class));
		dto.setGeoPosX(expedient.getGeoPosX());
		dto.setGeoPosY(expedient.getGeoPosY());
		dto.setGeoReferencia(expedient.getGeoReferencia());
		dto.setRegistreNumero(expedient.getRegistreNumero());
		dto.setRegistreData(expedient.getRegistreData());
		dto.setUnitatAdministrativa(expedient.getUnitatAdministrativa());
		dto.setIdioma(expedient.getIdioma());
		dto.setAutenticat(expedient.isAutenticat());
		dto.setTramitadorNif(expedient.getTramitadorNif());
		dto.setTramitadorNom(expedient.getTramitadorNom());
		dto.setInteressatNif(expedient.getInteressatNif());
		dto.setInteressatNom(expedient.getInteressatNom());
		dto.setRepresentantNif(expedient.getRepresentantNif());
		dto.setRepresentantNom(expedient.getRepresentantNom());
		dto.setAvisosHabilitats(expedient.isAvisosHabilitats());
		dto.setAvisosEmail(expedient.getAvisosEmail());
		dto.setAvisosMobil(expedient.getAvisosMobil());
		dto.setErrorDesc(expedient.getErrorDesc());
		dto.setErrorFull(expedient.getErrorFull());
		dto.setErrorsIntegracions(expedient.isErrorsIntegracions());
		dto.setNotificacioTelematicaHabilitada(expedient.isNotificacioTelematicaHabilitada());
		dto.setTramitExpedientIdentificador(expedient.getTramitExpedientIdentificador());
		dto.setTramitExpedientClau(expedient.getTramitExpedientClau());
		dto.setErrorsIntegracions(expedient.isErrorsIntegracions());		
		dto.setDataFi(expedient.getDataFi());
		dto.setAmbRetroaccio(expedient.isAmbRetroaccio());
		dto.setReindexarData(expedient.getReindexarData());
		dto.setReindexarError(expedient.isReindexarError());
		return dto;
	}

	public Expedient getExpedientComprovantPermisos(
			Long id,
			Permission[] permisos) {
		Expedient expedient = expedientRepository.findOne(id);
		if (expedient == null) {
			throw new NoTrobatException(
					Expedient.class, 
					id);
		}
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		// Si no te accés a l'entorn no te accés a l'expedient
		Entorn entorn = expedient.getEntorn();
		if (!permisosHelper.isGrantedAny(
				entorn.getId(),
				Entorn.class,
				permisos,
				auth)) {
			throw new PermisDenegatException(
					id,
					Entorn.class,
					permisos);
		}
		ExpedientTipus expedientTipus = expedient.getTipus();
		if (!permisosHelper.isGrantedAny(
				expedientTipus.getId(),
				ExpedientTipus.class,
				permisos,
				auth)) {
			throw new PermisDenegatException(
					id,
					Expedient.class,
					permisos);
		}
		return expedient;
	}
			
	public Expedient getExpedientComprovantPermisos(
			Long id,
			boolean comprovarPermisRead,
			boolean comprovarPermisWrite,
			boolean comprovarPermisDelete,
			boolean comprovarPermisAdministration) {
		return getExpedientComprovantPermisos(
				id,
				comprovarPermisRead,
				comprovarPermisWrite,
				comprovarPermisDelete,
				false,
				false,
				comprovarPermisAdministration,
				false);
	}
	public Expedient getExpedientComprovantPermisos(
			Long id,
			boolean comprovarPermisRead,
			boolean comprovarPermisWrite,
			boolean comprovarPermisDelete,
			boolean comprovarPermisSupervision,
			boolean comprovarPermisReassignment,
			boolean comprovarPermisAdministration,
			boolean comprovarPermisReassignmentOrWrite) {
		Expedient expedient = expedientRepository.findOne(id);
		if (expedient == null) {
			throw new NoTrobatException(
					Expedient.class, 
					id);
		}
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		Entorn entorn = expedient.getEntorn();
		
		Permission[] permisos = new Permission[] {
				ExtendedPermission.READ,
				ExtendedPermission.ADMINISTRATION};
		if (!permisosHelper.isGrantedAny(
				entorn.getId(),
				Entorn.class,
				permisos,
				auth)) {
			throw new PermisDenegatException(
					id,
					Entorn.class,
					permisos);
		}
		ExpedientTipus expedientTipus = expedient.getTipus();
		if (comprovarPermisRead) {
			permisos = new Permission[] {
					ExtendedPermission.READ,
					ExtendedPermission.ADMINISTRATION};
			if (!permisosHelper.isGrantedAny(
					expedientTipus.getId(),
					ExpedientTipus.class,
					permisos,
					auth)) {
				throw new PermisDenegatException(
						id,
						Expedient.class,
						permisos);
			}
		}
		if (comprovarPermisWrite) {
			permisos = new Permission[] {
					ExtendedPermission.WRITE,
					ExtendedPermission.ADMINISTRATION};
			if (!permisosHelper.isGrantedAny(
					expedientTipus.getId(),
					ExpedientTipus.class,
					permisos,
					auth)) {
				throw new PermisDenegatException(
						id,
						Expedient.class,
						permisos);
			}
		}
		if (comprovarPermisDelete) {
			permisos = new Permission[] {
					ExtendedPermission.DELETE,
					ExtendedPermission.ADMINISTRATION};
			if (!permisosHelper.isGrantedAny(
					expedientTipus.getId(),
					ExpedientTipus.class,
					permisos,
					auth)) {
				throw new PermisDenegatException(
						id,
						Expedient.class,
						permisos);
			}
		}
		if (comprovarPermisSupervision) {
			permisos = new Permission[] {
					ExtendedPermission.SUPERVISION,
					ExtendedPermission.ADMINISTRATION};
			if (!permisosHelper.isGrantedAny(
					expedientTipus.getId(),
					ExpedientTipus.class,
					permisos,
					auth)) {
				throw new PermisDenegatException(
						id,
						Expedient.class,
						permisos);
			}
		}
		if (comprovarPermisReassignment) {
			permisos = new Permission[] {
					ExtendedPermission.REASSIGNMENT,
					ExtendedPermission.ADMINISTRATION};
			if (!permisosHelper.isGrantedAny(
					expedientTipus.getId(),
					ExpedientTipus.class,
					permisos,
					auth)) {
				throw new PermisDenegatException(
						id,
						Expedient.class,
						permisos);
			}
		}
		if (comprovarPermisAdministration) {
			permisos = new Permission[] {
					ExtendedPermission.ADMINISTRATION};
			if (!permisosHelper.isGrantedAny(
					expedientTipus.getId(),
					ExpedientTipus.class,
					permisos,
					auth)) {
				throw new PermisDenegatException(
						id,
						Expedient.class,
						permisos);
			}
		}
		if (comprovarPermisReassignmentOrWrite) {
			permisos = new Permission[] {
					ExtendedPermission.WRITE,
					ExtendedPermission.REASSIGNMENT,
					ExtendedPermission.ADMINISTRATION};
			if (!permisosHelper.isGrantedAny(
					expedientTipus.getId(),
					ExpedientTipus.class,
					permisos,
					auth)) {
				throw new PermisDenegatException(
						id,
						Expedient.class,
						permisos);
			}
		}
		return expedient;
	}

	public void update(
			Expedient expedient,
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
		boolean ambRetroaccio = expedient.isAmbRetroaccio();
		if (!execucioDinsHandler) {
			ExpedientLog elog = expedientLoggerHelper.afegirLogExpedientPerExpedient(
				expedient.getId(),
				ExpedientLogAccioTipus.EXPEDIENT_MODIFICAR,
				null);
			elog.setEstat(ExpedientLogEstat.IGNORAR);
		}
		// Numero
		if (expedient.getTipus().getTeNumero()) {
			if (!StringUtils.equals(expedient.getNumero(), numero)) {
				expedientLoggerHelper.afegirProcessLogInfoExpedient(
						ambRetroaccio,
						expedient.getProcessInstanceId(), 
						LogInfo.NUMERO + "#@#" + expedient.getNumero());
				expedient.setNumero(numero);
			}
		}
		// Titol
		if (expedient.getTipus().getTeTitol()) {
			if (!StringUtils.equals(expedient.getTitol(), titol)) {
				expedientLoggerHelper.afegirProcessLogInfoExpedient(
						ambRetroaccio,
						expedient.getProcessInstanceId(), 
						LogInfo.TITOL + "#@#" + expedient.getTitol());
				expedient.setTitol(titol);
			}
		}
		// Responsable
		if (!StringUtils.equals(expedient.getResponsableCodi(), responsableCodi)) {
			expedientLoggerHelper.afegirProcessLogInfoExpedient(
					ambRetroaccio,
					expedient.getProcessInstanceId(), 
					LogInfo.RESPONSABLE + "#@#" + expedient.getResponsableCodi());
			expedient.setResponsableCodi(responsableCodi);
		}
		// Data d'inici
		if (expedient.getDataInici() != dataInici) {
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
			String inici = sdf.format(dataInici);
			expedientLoggerHelper.afegirProcessLogInfoExpedient(
					ambRetroaccio,
					expedient.getProcessInstanceId(), 
					LogInfo.INICI + "#@#" + inici);
			expedient.setDataInici(dataInici);
		}
		// Comentari
		if (!StringUtils.equals(expedient.getComentari(), comentari)) {
			expedientLoggerHelper.afegirProcessLogInfoExpedient(
					ambRetroaccio,
					expedient.getProcessInstanceId(), 
					LogInfo.COMENTARI + "#@#" + expedient.getComentari());
			expedient.setComentari(comentari);
		}
		// Estat
		if (estatId != null) {
			if (expedient.getEstat() == null || !expedient.getEstat().getId().equals(estatId)) {
				expedientLoggerHelper.afegirProcessLogInfoExpedient(
						ambRetroaccio,
						expedient.getProcessInstanceId(), 
						LogInfo.ESTAT + "#@#" + "---");
				Estat estat = estatRepository.findByExpedientTipusAndId(
						expedient.getTipus(),
						estatId);
				if (estat == null)
					throw new NoTrobatException(Estat.class, estatId);
				expedient.setEstat(estat);
			}
		} else if (expedient.getEstat() != null) {
			expedientLoggerHelper.afegirProcessLogInfoExpedient(
					ambRetroaccio,
					expedient.getProcessInstanceId(), 
					LogInfo.ESTAT + "#@#" + expedient.getEstat().getId());
			expedient.setEstat(null);
		}
		// Geoposició
		if (expedient.getGeoPosX() != geoPosX) {
			expedientLoggerHelper.afegirProcessLogInfoExpedient(
					ambRetroaccio,
					expedient.getProcessInstanceId(), 
					LogInfo.GEOPOSICIOX + "#@#" + expedient.getGeoPosX());
			expedient.setGeoPosX(geoPosX);
		}
		if (expedient.getGeoPosY() != geoPosY) {
			expedientLoggerHelper.afegirProcessLogInfoExpedient(
					ambRetroaccio,
					expedient.getProcessInstanceId(), 
					LogInfo.GEOPOSICIOY + "#@#" + expedient.getGeoPosY());
			expedient.setGeoPosY(geoPosY);
		}
		// Georeferencia
		if (!StringUtils.equals(expedient.getGeoReferencia(), geoReferencia)) {
			expedientLoggerHelper.afegirProcessLogInfoExpedient(
					ambRetroaccio,
					expedient.getProcessInstanceId(), 
					LogInfo.GEOREFERENCIA + "#@#" + expedient.getGeoReferencia());
			expedient.setGeoReferencia(geoReferencia);
		}
		// Grup
		if (!StringUtils.equals(expedient.getGrupCodi(), grupCodi)) {
			expedientLoggerHelper.afegirProcessLogInfoExpedient(
					ambRetroaccio,
					expedient.getProcessInstanceId(), 
					LogInfo.GRUP + "#@#" + expedient.getGrupCodi());
			expedient.setGrupCodi(grupCodi);
		}
		luceneHelper.updateExpedientCapsalera(
				expedient,
				isFinalitzat(expedient));
		// TODO
		/*String informacioNova = getInformacioExpedient(expedient);
		registreDao.crearRegistreModificarExpedient(
				expedient.getId(),
				getUsuariPerRegistre(),
				informacioVella,
				informacioNova);
		actualizarCacheExpedient(expedient);*/
	}

	public List<Expedient> findByFiltreGeneral(
			Entorn entorn,
			String titol,
			String numero,
			Date dataInici1,
			Date dataInici2,
			ExpedientTipus expedientTipus,
			Estat estat,
			boolean nomesIniciats,
			boolean nomesFinalitzats) {
		List<ExpedientTipus> tipusPermesos = new ArrayList<ExpedientTipus>();
		tipusPermesos.add(expedientTipus);
		return expedientRepository.findByFiltreGeneral(
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
				nomesIniciats,
				nomesFinalitzats,
				estat == null,
				estat,
				true,
				null,
				true,
				null,
				true,
				null,
				false,
				null,
				null,
				null,
				null,
				null,
				false,
				false);
	}

	public void aturar(
			Expedient expedient,
			String motiu,
			String usuari) {
		mesuresTemporalsHelper.mesuraIniciar(
				"Aturar",
				"expedient",
				expedient.getTipus().getNom());
		ExpedientLog expedientLog = expedientLoggerHelper.afegirLogExpedientPerExpedient(
				expedient.getId(),
				ExpedientLogAccioTipus.EXPEDIENT_ATURAR,
				motiu);
		expedientLog.setEstat(ExpedientLogEstat.IGNORAR);
		List<JbpmProcessInstance> processInstancesTree = jbpmHelper.getProcessInstanceTree(
				expedient.getProcessInstanceId());
		String[] ids = new String[processInstancesTree.size()];
		int i = 0;
		for (JbpmProcessInstance pi: processInstancesTree)
			ids[i++] = pi.getId();
		jbpmHelper.suspendProcessInstances(ids);
		expedient.setInfoAturat(motiu);
		expedientRegistreHelper.crearRegistreAturarExpedient(
				expedient.getId(),
				(usuari != null) ? usuari : SecurityContextHolder.getContext().getAuthentication().getName(),
				motiu);
		mesuresTemporalsHelper.mesuraCalcular(
				"Aturar", 
				"expedient",
				expedient.getTipus().getNom());
	}
	public void reprendre(
			Expedient expedient,
			String usuari) {
		mesuresTemporalsHelper.mesuraIniciar(
				"Reprendre",
				"expedient",
				expedient.getTipus().getNom());
		ExpedientLog expedientLog = expedientLoggerHelper.afegirLogExpedientPerExpedient(
				expedient.getId(),
				ExpedientLogAccioTipus.EXPEDIENT_REPRENDRE,
				null);
		expedientLog.setEstat(ExpedientLogEstat.IGNORAR);
		List<JbpmProcessInstance> processInstancesTree = jbpmHelper.getProcessInstanceTree(
				expedient.getProcessInstanceId());
		String[] ids = new String[processInstancesTree.size()];
		int i = 0;
		for (JbpmProcessInstance pi: processInstancesTree)
			ids[i++] = pi.getId();
		jbpmHelper.resumeProcessInstances(ids);
		expedient.setInfoAturat(null);
		expedientRegistreHelper.crearRegistreReprendreExpedient(
				expedient.getId(),
				(usuari != null) ? usuari : SecurityContextHolder.getContext().getAuthentication().getName());
		mesuresTemporalsHelper.mesuraCalcular(
				"Reprendre",
				"expedient",
				expedient.getTipus().getNom());
	}

	public void relacioCrear(
			Expedient origen,
			Expedient desti) {
		ExpedientLog expedientLog = expedientLoggerHelper.afegirLogExpedientPerExpedient(
				origen.getId(),
				ExpedientLogAccioTipus.EXPEDIENT_RELACIO_AFEGIR,
				desti.getId().toString());
		expedientLog.setEstat(ExpedientLogEstat.IGNORAR);
		boolean existeix = false;
		for (Expedient relacionat: origen.getRelacionsOrigen()) {
			if (relacionat.getId().longValue() == desti.getId().longValue()) {
				existeix = true;
				break;
			}
		}
		if (!existeix)
			origen.addRelacioOrigen(desti);
		existeix = false;
		for (Expedient relacionat: desti.getRelacionsOrigen()) {
			if (relacionat.getId().longValue() == origen.getId().longValue()) {
				existeix = true;
				break;
			}
		}
		if (!existeix)
			desti.addRelacioOrigen(origen);
	}

	public void tokenRetrocedir(
			String tokenId,
			String nodeName,
			boolean cancelTasks) {
		JbpmToken token = jbpmHelper.getTokenById(tokenId);
		String nodeNameVell = token.getNodeName();
		ProcessInstanceExpedient piexp = jbpmHelper.expedientFindByProcessInstanceId(
				token.getProcessInstanceId());
		jbpmHelper.tokenRedirect(
				new Long(tokenId).longValue(),
				nodeName,
				cancelTasks,
				true,
				false);
		expedientRegistreHelper.crearRegistreRedirigirToken(
				piexp.getId(),
				token.getProcessInstanceId(),
				SecurityContextHolder.getContext().getAuthentication().getName(),
				token.getFullName(),
				nodeNameVell,
				nodeName);
	}

	public void comprovarInstanciaProces(
			Expedient expedient,
			String processInstanceId) {
		ProcessInstanceExpedient piexp = jbpmHelper.expedientFindByProcessInstanceId(
				processInstanceId);
		if (piexp.getId() != expedient.getId().longValue()) {
			throw new NoTrobatException(
					JbpmProcessInstance.class,
					new Long(processInstanceId));
		}
	}

	public Expedient findAmbEntornIId(Long entornId, Long id) {
		return expedientRepository.findByEntornIdAndId(entornId, id);
	}
	
	public Expedient findExpedientByProcessInstanceId(String processInstanceId) {
		Expedient expedient = null;
		ProcessInstanceExpedient piexp = jbpmHelper.expedientFindByProcessInstanceId(
				processInstanceId);
		if (piexp != null)
			expedient = expedientRepository.findOne(piexp.getId());
		if (expedient == null) {
			Expedient expedientIniciant = ExpedientIniciantDto.getExpedient();
			if (expedientIniciant != null && expedientIniciant.getProcessInstanceId().equals(processInstanceId)) {
				expedient = expedientIniciant;
			} else {
				throw new NoTrobatException(Expedient.class, "PID:" + processInstanceId);
			}
		}
		return expedient;
	}
	public DefinicioProces findDefinicioProcesByProcessInstanceId(String processInstanceId) {
		String processDefinitionId = jbpmHelper.getProcessInstance(processInstanceId).getProcessDefinitionId();
		return definicioProcesRepository.findByJbpmId(processDefinitionId);
	}

	public boolean isFinalitzat(Expedient expedient) {
		return expedient.getDataFi() != null;
	}

	public List<Camp> findAllCampsExpedientConsulta() {
		List<Camp> resposta = new ArrayList<Camp>();
		resposta.add(
				getCampExpedient(ExpedientCamps.EXPEDIENT_CAMP_ID));
		resposta.add(
				getCampExpedient(ExpedientCamps.EXPEDIENT_CAMP_NUMERO));
		resposta.add(
				getCampExpedient(ExpedientCamps.EXPEDIENT_CAMP_TITOL));
		resposta.add(
				getCampExpedient(ExpedientCamps.EXPEDIENT_CAMP_COMENTARI));
		resposta.add(
				getCampExpedient(ExpedientCamps.EXPEDIENT_CAMP_INICIADOR));
		resposta.add(
				getCampExpedient(ExpedientCamps.EXPEDIENT_CAMP_RESPONSABLE));
		resposta.add(
				getCampExpedient(ExpedientCamps.EXPEDIENT_CAMP_DATA_INICI));
		resposta.add(
				getCampExpedient(ExpedientCamps.EXPEDIENT_CAMP_ESTAT));
		return resposta;
	}

	public void omplirPermisosExpedientsTipus(List<ExpedientTipusDto> expedientsTipus) {
		List<Long> expedientTipusIds = new ArrayList<Long>();
		for (ExpedientTipusDto expedientTipus: expedientsTipus) {
			expedientTipusIds.add(expedientTipus.getId());
		}
		permisosHelper.omplirControlPermisosSegonsUsuariActual(
				expedientTipusIds,
				expedientsTipus,
				ExpedientTipus.class);
	}
	public void omplirPermisosExpedients(List<ExpedientDto> expedients) {
		List<Long> expedientTipusIds = new ArrayList<Long>();
		for (ExpedientDto expedient: expedients) {
			expedientTipusIds.add(expedient.getTipus().getId());
		}
		permisosHelper.omplirControlPermisosSegonsUsuariActual(
				expedientTipusIds,
				expedients,
				ExpedientTipus.class);
	}
	public void omplirPermisosExpedient(ExpedientDto expedient) {
		List<ExpedientDto> expedients = new ArrayList<ExpedientDto>();
		expedients.add(expedient);
		omplirPermisosExpedients(expedients);
	}
	public void trobarAlertesExpedients(List<ExpedientDto> expedients) {
		List<Long> expedientIds = new ArrayList<Long>();
		for (ExpedientDto expedient: expedients) {
			expedientIds.add(expedient.getId());
		}
		omplirNumAlertes(
				expedientIds,
				expedients);
	}
	public void trobarAlertesExpedient(ExpedientDto dto) {
		Expedient expedient = this.getExpedientComprovantPermisos(
				dto.getId(),
				true,
				false,
				false,
				false);
		List<Alerta> alertes = alertaRepository.findByExpedientAndDataEliminacioNull(expedient);
		
		long pendents = 0L;
		if (!alertes.isEmpty()) {
			dto.setAlertesTotals(new Long(alertes.size()));
			dto.setAlertesPendents(0L);
			for (Alerta alerta: alertes) {
				if (alerta.getDataLectura() == null)
					pendents ++;
			}
			dto.setAlertesPendents(pendents);
		}
		
	}

	public String getNumeroExpedientActual(
			ExpedientTipus expedientTipus,
			int any,
			long increment) {
		long seq = expedientTipus.getSequencia();
		return getNumeroExpedientExpressio(
				expedientTipus,
				expedientTipus.getExpressioNumero(),
				seq,
				increment,
				any,
				expedientTipus.isReiniciarCadaAny(),
				false);
	}
	
	public String getNumeroExpedientDefaultActual(
			ExpedientTipus expedientTipus,
			int any,
			long increment) {
		long seq = expedientTipus.getSequenciaDefault();
		return getNumeroExpedientExpressio(
				expedientTipus,
				getNumexpDefaultExpression(),
				seq,
				increment,
				any,
				expedientTipus.isReiniciarCadaAny(),
				true);
	}



	private String getNumeroExpedientExpressio(
			ExpedientTipus expedientTipus,
			String expressio,
			long seq,
			long increment,
			int any,
			boolean reiniciarCadaAny,
			boolean numeroDefault) {
		if (reiniciarCadaAny) {
			if (any != 0) {
				if (numeroDefault) {
					if (expedientTipus.getSequenciaDefaultAny().containsKey(any)) {
						seq = expedientTipus.getSequenciaDefaultAny().get(any).getSequenciaDefault() + increment;
					} else {
						// TODO: podriem comprovar, segons el format del número per defecte si hi ha expedients ja creats de 
						// l'any, i d'aquesta manera assignar com a número inicial el major utilitzat + 1
						SequenciaDefaultAny sda = new SequenciaDefaultAny(
								expedientTipus, any, 1L);
						expedientTipus.getSequenciaDefaultAny().put(any, sda);
						seq = 1;
					}
				} else {
					if (expedientTipus.getSequenciaAny().containsKey(any)) {
						seq = expedientTipus.getSequenciaAny().get(any).getSequencia() + increment;
					} else {
						SequenciaAny sa = new SequenciaAny(expedientTipus, any, 1L);
						expedientTipus.getSequenciaAny().put(any, sa);
						seq = 1;
					}
				}
			}
		} else {
			seq = seq + increment;
		}
		if (expressio != null) {
			try {
				final Map<String, Object> context = new HashMap<String, Object>();
				context.put("entorn_cod", expedientTipus.getEntorn().getCodi());
				context.put("tipexp_cod", expedientTipus.getCodi());
				context.put("any", any);
				context.put("seq", seq);
				ExpressionEvaluator evaluator = new ExpressionEvaluatorImpl();
				String resultat = (String)evaluator.evaluate(
						expressio,
						String.class,
						new VariableResolver() {
							public Object resolveVariable(String name)
									throws ELException {
								return context.get(name);
							}
						},
						null);
				return resultat;
			} catch (Exception ex) {
				return "#invalid expression#";
			}
		} else {
			return new Long(seq).toString();
		}
	}

	private Camp getCampExpedient(String campCodi) {
		if (ExpedientCamps.EXPEDIENT_CAMP_ID.equals(campCodi)) {
			Camp campExpedient = new Camp();
			campExpedient.setCodi(campCodi);
			campExpedient.setTipus(TipusCamp.INTEGER);
			campExpedient.setEtiqueta(messageHelper.getMessage("etiqueta.exp.id"));
			return campExpedient;
		}
		if (ExpedientCamps.EXPEDIENT_CAMP_NUMERO.equals(campCodi)) {
			Camp campExpedient = new Camp();
			campExpedient.setCodi(campCodi);
			campExpedient.setTipus(TipusCamp.STRING);
			campExpedient.setEtiqueta(messageHelper.getMessage("etiqueta.exp.numero"));
			return campExpedient;
		}
		if (ExpedientCamps.EXPEDIENT_CAMP_TITOL.equals(campCodi)) {
			Camp campExpedient = new Camp();
			campExpedient.setCodi(campCodi);
			campExpedient.setTipus(TipusCamp.STRING);
			campExpedient.setEtiqueta(messageHelper.getMessage("etiqueta.exp.titol"));
			return campExpedient;
		}
		if (ExpedientCamps.EXPEDIENT_CAMP_COMENTARI.equals(campCodi)) {
			Camp campExpedient = new Camp();
			campExpedient.setCodi(campCodi);
			campExpedient.setTipus(TipusCamp.STRING);
			campExpedient.setEtiqueta(messageHelper.getMessage("etiqueta.exp.comentari"));
			return campExpedient;
		}
		if (ExpedientCamps.EXPEDIENT_CAMP_INICIADOR.equals(campCodi)) {
			Camp campExpedient = new Camp();
			campExpedient.setCodi(campCodi);
			campExpedient.setTipus(TipusCamp.SUGGEST);
			campExpedient.setEtiqueta(messageHelper.getMessage("etiqueta.exp.iniciador"));
			return campExpedient;
		}
		if (ExpedientCamps.EXPEDIENT_CAMP_RESPONSABLE.equals(campCodi)) {
			Camp campExpedient = new Camp();
			campExpedient.setCodi(campCodi);
			campExpedient.setTipus(TipusCamp.SUGGEST);
			campExpedient.setEtiqueta(messageHelper.getMessage("etiqueta.exp.responsable"));
			return campExpedient;
		}
		if (ExpedientCamps.EXPEDIENT_CAMP_DATA_INICI.equals(campCodi)) {
			Camp campExpedient = new Camp();
			campExpedient.setCodi(campCodi);
			campExpedient.setTipus(TipusCamp.DATE);
			campExpedient.setEtiqueta(messageHelper.getMessage("etiqueta.exp.data_ini"));
			return campExpedient;
		}
		if (ExpedientCamps.EXPEDIENT_CAMP_ESTAT.equals(campCodi)) {
			Camp campExpedient = new Camp();
			campExpedient.setCodi(campCodi);
			campExpedient.setTipus(TipusCamp.SELECCIO);
			campExpedient.setEtiqueta(messageHelper.getMessage("etiqueta.exp.estat"));
			return campExpedient;
		}
		return null;
	}

	private void omplirNumAlertes(List<Long> ids, List<ExpedientDto> dtos) {
		List<Object[]> comptadorsAlertes = alertaRepository.findByExpedientIds(ids);
		
		for (Object[] comptadorsAlerta: comptadorsAlertes) {
			long id = (Long)comptadorsAlerta[0];
			for (ExpedientDto dto: dtos) {
				if (id == dto.getId()) {
					dto.setAlertesTotals((Long)comptadorsAlerta[1]);
					dto.setAlertesPendents((Long)comptadorsAlerta[2]);
				}
			}
		}
	}
	private String getNumexpDefaultExpression() {
		return GlobalProperties.getInstance().getProperty("app.numexp.expression");
	}

}
