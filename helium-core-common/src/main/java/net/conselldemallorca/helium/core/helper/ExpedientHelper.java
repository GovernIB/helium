/**
 * 
 */
package net.conselldemallorca.helium.core.helper;

import es.caib.plugins.arxiu.api.ContingutArxiu;
import es.caib.plugins.arxiu.api.ExpedientEstat;
import es.caib.plugins.arxiu.api.ExpedientMetadades;
import net.conselldemallorca.helium.core.api.WProcessInstance;
import net.conselldemallorca.helium.core.api.WToken;
import net.conselldemallorca.helium.core.api.WorkflowEngineApi;
import net.conselldemallorca.helium.core.api.WorkflowRetroaccioApi;
import net.conselldemallorca.helium.core.api.WorkflowRetroaccioApi.ExpedientRetroaccioEstat;
import net.conselldemallorca.helium.core.api.WorkflowRetroaccioApi.ExpedientRetroaccioTipus;
import net.conselldemallorca.helium.core.api.WorkflowRetroaccioApi.RetroaccioInfo;
import net.conselldemallorca.helium.core.common.ThreadLocalInfo;
import net.conselldemallorca.helium.core.helper.PermisosHelper.ObjectIdentifierExtractor;
import net.conselldemallorca.helium.core.helperv26.MesuresTemporalsHelper;
import net.conselldemallorca.helium.core.model.hibernate.*;
import net.conselldemallorca.helium.core.model.hibernate.Camp.TipusCamp;
import net.conselldemallorca.helium.core.model.hibernate.Expedient.IniciadorTipus;
import net.conselldemallorca.helium.core.security.ExtendedPermission;
import net.conselldemallorca.helium.core.util.ExpedientCamps;
import net.conselldemallorca.helium.core.util.GlobalProperties;
import net.conselldemallorca.helium.core.util.PdfUtils;
import net.conselldemallorca.helium.jbpm3.integracio.JbpmProcessInstance;
import net.conselldemallorca.helium.v3.core.api.dto.ArxiuDto;
import net.conselldemallorca.helium.v3.core.api.dto.DadesDocumentDto;
import net.conselldemallorca.helium.v3.core.api.dto.DefinicioProcesDto;
import net.conselldemallorca.helium.v3.core.api.dto.EntornDto;
import net.conselldemallorca.helium.v3.core.api.dto.EstatDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientDto.IniciadorTipusDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientTipusDto;
import net.conselldemallorca.helium.v3.core.api.dto.InstanciaProcesDto;
import net.conselldemallorca.helium.v3.core.api.dto.PersonaDto;
import net.conselldemallorca.helium.v3.core.api.exception.NoTrobatException;
import net.conselldemallorca.helium.v3.core.api.exception.PermisDenegatException;
import net.conselldemallorca.helium.v3.core.api.exception.ValidacioException;
import net.conselldemallorca.helium.v3.core.repository.AlertaRepository;
import net.conselldemallorca.helium.v3.core.repository.DefinicioProcesRepository;
import net.conselldemallorca.helium.v3.core.repository.DocumentStoreRepository;
import net.conselldemallorca.helium.v3.core.repository.EstatRepository;
import net.conselldemallorca.helium.v3.core.repository.ExpedientRepository;
import net.conselldemallorca.helium.v3.core.repository.ExpedientTipusRepository;
import net.conselldemallorca.helium.v3.core.repository.RegistreRepository;
import net.conselldemallorca.helium.v3.core.repository.TerminiIniciatRepository;
import org.apache.commons.lang.StringUtils;
import org.jbpm.jpdl.el.ELException;
import org.jbpm.jpdl.el.ExpressionEvaluator;
import org.jbpm.jpdl.el.VariableResolver;
import org.jbpm.jpdl.el.impl.ExpressionEvaluatorImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.security.acls.model.Permission;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
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

/**
 * Helper per a gestionar els expedients.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class ExpedientHelper {

	@Resource
	private ExpedientTipusRepository expedientTipusRepository;
	@Resource
	private DefinicioProcesRepository definicioProcesRepository;
	@Resource
	private ExpedientRepository expedientRepository;
	@Resource
	private EstatRepository estatRepository;
	@Resource
	private AlertaRepository alertaRepository;
	@Resource
	private TerminiIniciatRepository terminiIniciatRepository;
	@Resource
	private RegistreRepository registreRepository;
	@Resource
	private DocumentStoreRepository documentStoreRepository;

	@Resource
	private ExpedientHelper expedientHelper;
	@Resource
	private EntornHelper entornHelper;
	@Resource(name = "permisosHelperV3")
	private PermisosHelper permisosHelper;
	@Resource(name="documentHelperV3")
	private DocumentHelperV3 documentHelper;
	@Resource
	private WorkflowEngineApi workflowEngineApi;
	@Resource
	private WorkflowRetroaccioApi workflowRetroaccioApi;
	@Resource
	private ExpedientRegistreHelper expedientRegistreHelper;
	@Resource
	private PluginHelper pluginHelper;
	@Resource
	private IndexHelper indexHelper;
	@Resource
	private MessageHelper messageHelper;
	@Resource
	private ConversioTipusHelper conversioTipusHelper;
	@Resource
	private MesuresTemporalsHelper mesuresTemporalsHelper;
	@Resource
	private DefinicioProcesHelper definicioProcesHelper;

	
	public static String VERSIO_NTI = "http://administracionelectronica.gob.es/ENI/XSD/v1.0/expediente-e";
	
	
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
				new Permission[] {
						BasePermission.ADMINISTRATION,
						BasePermission.READ},
				auth)) {
			throw new PermisDenegatException(
					entorn.getId(),
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

	public boolean isGrantedAny(
			Expedient expedient,
			Permission[] permisos) {
		ExpedientTipus expedientTipus = expedient.getTipus();
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		return permisosHelper.isGrantedAny(
				expedientTipus.getId(),
				ExpedientTipus.class,
				permisos,
				auth);
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
		boolean atributsArxiuCanviats = false;
		if (!execucioDinsHandler) {
			workflowRetroaccioApi.afegirInformacioRetroaccioPerExpedient(
					expedient.getId(),
					ExpedientRetroaccioTipus.EXPEDIENT_MODIFICAR,
					null,
					ExpedientRetroaccioEstat.IGNORAR);
		}
		// Numero
		if (expedient.getTipus().getTeNumero()) {
			if (!StringUtils.equals(expedient.getNumero(), numero)) {
				workflowRetroaccioApi.afegirInformacioRetroaccioPerExpedient(
						ambRetroaccio,
						expedient.getProcessInstanceId(),
						RetroaccioInfo.NUMERO + "#@#" + expedient.getNumero());
				expedient.setNumero(numero);
				atributsArxiuCanviats = true;
			}
		}
		// Titol
		if (expedient.getTipus().getTeTitol()) {
			if (!StringUtils.equals(expedient.getTitol(), titol)) {
				workflowRetroaccioApi.afegirInformacioRetroaccioPerExpedient(
						ambRetroaccio,
						expedient.getProcessInstanceId(),
						RetroaccioInfo.TITOL + "#@#" + expedient.getTitol());
				expedient.setTitol(titol);
				atributsArxiuCanviats = true;
			}
		}
		// Responsable
		if (!StringUtils.equals(expedient.getResponsableCodi(), responsableCodi)) {
			workflowRetroaccioApi.afegirInformacioRetroaccioPerExpedient(
					ambRetroaccio,
					expedient.getProcessInstanceId(),
					RetroaccioInfo.RESPONSABLE + "#@#" + expedient.getResponsableCodi());
			expedient.setResponsableCodi(responsableCodi);
		}
		// Data d'inici
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		if ( !sdf.format(expedient.getDataInici()).equals(sdf.format(dataInici)) ) {
			String inici = sdf.format(dataInici);
			workflowRetroaccioApi.afegirInformacioRetroaccioPerExpedient(
					ambRetroaccio,
					expedient.getProcessInstanceId(),
					RetroaccioInfo.INICI + "#@#" + inici);
			expedient.setDataInici(dataInici);
			atributsArxiuCanviats = true;
		}
		// Comentari
		if (!StringUtils.equals(expedient.getComentari(), comentari)) {
			workflowRetroaccioApi.afegirInformacioRetroaccioPerExpedient(
					ambRetroaccio,
					expedient.getProcessInstanceId(),
					RetroaccioInfo.COMENTARI + "#@#" + expedient.getComentari());
			expedient.setComentari(comentari);
		}
		// Estat
		if (estatId != null) {
			if (expedient.getEstat() == null || !expedient.getEstat().getId().equals(estatId)) {
				workflowRetroaccioApi.afegirInformacioRetroaccioPerExpedient(
						ambRetroaccio,
						expedient.getProcessInstanceId(),
						RetroaccioInfo.ESTAT + "#@#" + "---");
				Estat estat = estatRepository.findByExpedientTipusAndIdAmbHerencia(
						expedient.getTipus().getId(), 
						estatId);
				if (estat == null)
					throw new NoTrobatException(Estat.class, estatId);
				expedient.setEstat(estat);
			}
		} else if (expedient.getEstat() != null) {
			workflowRetroaccioApi.afegirInformacioRetroaccioPerExpedient(
					ambRetroaccio,
					expedient.getProcessInstanceId(),
					RetroaccioInfo.ESTAT + "#@#" + expedient.getEstat().getId());
			expedient.setEstat(null);
		}
		// Geoposició
		if (expedient.getGeoPosX() != geoPosX) {
			workflowRetroaccioApi.afegirInformacioRetroaccioPerExpedient(
					ambRetroaccio,
					expedient.getProcessInstanceId(),
					RetroaccioInfo.GEOPOSICIOX + "#@#" + expedient.getGeoPosX());
			expedient.setGeoPosX(geoPosX);
		}
		if (expedient.getGeoPosY() != geoPosY) {
			workflowRetroaccioApi.afegirInformacioRetroaccioPerExpedient(
					ambRetroaccio,
					expedient.getProcessInstanceId(),
					RetroaccioInfo.GEOPOSICIOY + "#@#" + expedient.getGeoPosY());
			expedient.setGeoPosY(geoPosY);
		}
		// Georeferencia
		if (!StringUtils.equals(expedient.getGeoReferencia(), geoReferencia)) {
			workflowRetroaccioApi.afegirInformacioRetroaccioPerExpedient(
					ambRetroaccio,
					expedient.getProcessInstanceId(),
					RetroaccioInfo.GEOREFERENCIA + "#@#" + expedient.getGeoReferencia());
			expedient.setGeoReferencia(geoReferencia);
		}
		// Grup
		if (!StringUtils.equals(expedient.getGrupCodi(), grupCodi)) {
			workflowRetroaccioApi.afegirInformacioRetroaccioPerExpedient(
					ambRetroaccio,
					expedient.getProcessInstanceId(),
					RetroaccioInfo.GRUP + "#@#" + expedient.getGrupCodi());
			expedient.setGrupCodi(grupCodi);
		}
		// Reindexació a lucene. Pot ser síncrona o asíncrona depenent del tipus d'expedient
		indexHelper.expedientIndexLuceneUpdate(
				expedient.getProcessInstanceId(), 
				false);
		
		//Actualitzem el nom de l'expedient a l'arxiu
		if (expedient.getTipus().isArxiuActiu() &&
			expedient.getArxiuUuid() != null &&
			atributsArxiuCanviats) {
			// Modifiquem l'expedient a l'arxiu.
			pluginHelper.arxiuExpedientModificar(expedient);
		}
		
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
		workflowRetroaccioApi.afegirInformacioRetroaccioPerExpedient(
				expedient.getId(),
				ExpedientRetroaccioTipus.EXPEDIENT_ATURAR,
				motiu,
				ExpedientRetroaccioEstat.IGNORAR);
		List<WProcessInstance> processInstancesTree = workflowEngineApi.getProcessInstanceTree(
				expedient.getProcessInstanceId());
		String[] ids = new String[processInstancesTree.size()];
		int i = 0;
		for (WProcessInstance pi: processInstancesTree)
			ids[i++] = pi.getId();
		workflowEngineApi.suspendProcessInstances(ids);
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
		workflowRetroaccioApi.afegirInformacioRetroaccioPerExpedient(
				expedient.getId(),
				ExpedientRetroaccioTipus.EXPEDIENT_REPRENDRE,
				null,
				ExpedientRetroaccioEstat.IGNORAR);
		List<WProcessInstance> processInstancesTree = workflowEngineApi.getProcessInstanceTree(
				expedient.getProcessInstanceId());
		String[] ids = new String[processInstancesTree.size()];
		int i = 0;
		for (WProcessInstance pi: processInstancesTree)
			ids[i++] = pi.getId();
		workflowEngineApi.resumeProcessInstances(ids);
		expedient.setInfoAturat(null);
		expedientRegistreHelper.crearRegistreReprendreExpedient(
				expedient.getId(),
				(usuari != null) ? usuari : SecurityContextHolder.getContext().getAuthentication().getName());
		mesuresTemporalsHelper.mesuraCalcular(
				"Reprendre",
				"expedient",
				expedient.getTipus().getNom());
	}

	@Transactional
	public void finalitzar(long expedientId) {
		
		Expedient expedient = expedientHelper.getExpedientComprovantPermisos(
				expedientId,
				new Permission[] {
						ExtendedPermission.WRITE,
						ExtendedPermission.ADMINISTRATION});

		mesuresTemporalsHelper.mesuraIniciar(
				"Finalitzar",
				"expedient",
				expedient.getTipus().getNom());

		workflowRetroaccioApi.afegirInformacioRetroaccioPerExpedient(
				expedient.getId(),
				ExpedientRetroaccioTipus.EXPEDIENT_FINALITZAR,
				null);

		List<WProcessInstance> processInstancesTree = workflowEngineApi.getProcessInstanceTree(expedient.getProcessInstanceId());
		String[] ids = new String[processInstancesTree.size()];
		int i = 0;
		for (WProcessInstance pi: processInstancesTree)
			ids[i++] = pi.getId();
		
		Date dataFinalitzacio = new Date();
		workflowEngineApi.finalitzarExpedient(ids, dataFinalitzacio);
		expedient.setDataFi(dataFinalitzacio);

		//tancam l'expedient de l'arxiu si escau
		if (expedient.isArxiuActiu() && expedient.getArxiuUuid() != null) {
			this.tancarExpedientArxiu(expedient);
		}		
		crearRegistreExpedient(
				expedient.getId(),
				SecurityContextHolder.getContext().getAuthentication().getName(),
				Registre.Accio.FINALITZAR);
	}

	/** Mètode comú per tancar l'expedient a l'Arxiu en el cas de finalitzar manualment un expedient o des de la verificació
	 * i finalització d'una tasca. Si no té contingut l'esborra de l'arxiu i si en té firma els documents sense firma i el tanca.
	 * 
	 * @param expedient
	 * 			Expedient amb la propietat isArxiuActiu a true.
	 */
	private void tancarExpedientArxiu(Expedient expedient) {
		List<ContingutArxiu> continguts = pluginHelper.arxiuExpedientInfo(expedient.getArxiuUuid()).getContinguts();
		if(continguts == null || continguts.isEmpty()) {
			// S'eborra l'expedient del arxiu si no te cap document.
			pluginHelper.arxiuExpedientEsborrar(expedient.getArxiuUuid());
			expedient.setArxiuUuid(null);
		}else {
			//firmem els documents que no estan firmats
			expedientHelper.firmarDocumentsPerArxiuFiExpedient(expedient);	
			
			// Tanca l'expedient a l'arxiu.
			ExpedientMetadades metadades = pluginHelper.arxiuExpedientInfo(expedient.getArxiuUuid()).getMetadades();
			if(metadades.getEstat() != ExpedientEstat.TANCAT) {
				pluginHelper.arxiuExpedientTancar(expedient.getArxiuUuid());
			}
		}
	}

	@Transactional
	public void desfinalitzar(
			Expedient expedient,
			String usuari) {
		mesuresTemporalsHelper.mesuraIniciar(
				"Desfinalitzar",
				"expedient",
				expedient.getTipus().getNom());

		workflowRetroaccioApi.afegirInformacioRetroaccioPerExpedient(
				expedient.getId(),
				ExpedientRetroaccioTipus.EXPEDIENT_DESFINALITZAR,
				null,
				ExpedientRetroaccioEstat.IGNORAR);
		logger.debug("Desfer finalització de l'expedient (id=" + expedient.getId() + ")");
		workflowEngineApi.desfinalitzarExpedient(expedient.getProcessInstanceId());
		expedient.setDataFi(null);
		expedientRegistreHelper.crearRegistreReprendreExpedient(
				expedient.getId(),
				(expedient.getResponsableCodi() != null) ? expedient.getResponsableCodi() : SecurityContextHolder.getContext().getAuthentication().getName());

		mesuresTemporalsHelper.mesuraCalcular(
				"Desfinalitzar",
				"expedient",
				expedient.getTipus().getNom());
		
        if (expedient.isArxiuActiu()) {
    		//reobrim l'expedient de l'arxiu digital si escau
        	if (expedient.getArxiuUuid() != null
        			&& pluginHelper.arxiuExisteixExpedient(expedient.getArxiuUuid())) {
	            // Obre de nou l'expedient tancat a l'arxiu.
	            pluginHelper.arxiuExpedientReobrir(expedient.getArxiuUuid());
	        } else {
	            // Migra l'expedient a l'arxiu
	            expedientHelper.migrarArxiu(expedient);
	        }
        }
	}
	
	@Transactional
	public void migrarArxiu(Expedient expedient) {
		
		if (!expedient.getTipus().isNtiActiu())
			throw new ValidacioException("Aquest expedient no es pot migrar perquè el tipus d'expedient no té activades les metadades NTI");

		// Fa validacions prèvies
		if (!expedient.getTipus().isArxiuActiu())
			throw new ValidacioException("Aquest expedient no es pot migrar perquè el tipus d'expedient no té activada la intagració amb l'arxiu");
		
		if (expedient.getArxiuUuid() != null && !expedient.getArxiuUuid().isEmpty())
			throw new ValidacioException("Aquest expedient ja està vinculat a l'arxiu amb la uuid: " + expedient.getArxiuUuid());
		
		// Valida que els documents siguin convertibles
		List<String> noConvertibles = new ArrayList<String>();
		for (DocumentStore document: documentStoreRepository.findByProcessInstanceId(expedient.getProcessInstanceId())) {
			if(!PdfUtils.isArxiuConvertiblePdf(document.getArxiuNom()))
				noConvertibles.add(document.getArxiuNom());
		}
		if (!noConvertibles.isEmpty())
			throw new ValidacioException("No es pot migrar l'expedient perquè conté els següents documents no convertibles a PDF per persistir-los a l'Arxiu: " + noConvertibles.toString());

		if (!expedient.isNtiActiu()) {
			// Informa les metadades NTI de l'expedient
			expedient.setNtiVersion(VERSIO_NTI);
			expedient.setNtiOrgano(expedient.getTipus().getNtiOrgano());
			expedient.setNtiClasificacion(expedient.getTipus().getNtiClasificacion());
			expedient.setNtiSerieDocumental(expedient.getTipus().getNtiSerieDocumental());			
		}

		// Migra a l'Arxiu
		ContingutArxiu expedientCreat = pluginHelper.arxiuExpedientCrear(expedient);
		expedient.setArxiuUuid(
				expedientCreat.getIdentificador());
		// Consulta l'identificador NTI generat per l'arxiu i el modifica
		// a dins l'expedient creat.
		es.caib.plugins.arxiu.api.Expedient expedientArxiu = pluginHelper.arxiuExpedientInfo(
				expedientCreat.getIdentificador());
		expedient.setNtiIdentificador(
				expedientArxiu.getMetadades().getIdentificador());
		
		List<DocumentStore> documents = documentStoreRepository.findByProcessInstanceId(expedient.getProcessInstanceId());
		
		Set<String> documentsExistents = new HashSet<String>();
		for (DocumentStore documentStore: documents) {
			
			Document document = documentHelper.findDocumentPerInstanciaProcesICodi(
					expedient.getProcessInstanceId(),
					documentStore.getCodiDocument());
			
			if (!expedient.isNtiActiu()) {
				// Informa les metadades NTI del document
				documentHelper.actualizarMetadadesNti(
						expedient, 
						document, 
						documentStore, 
						null, //ntiOrigen
						null, //ntiEstadoElaboracion
						null, //ntiTipoDocumental
						null); //ntiIdDocumentoOrigen
			}
			
			String documentDescripcio;
			if (documentStore.isAdjunt()) {
				documentDescripcio = documentStore.getAdjuntTitol();
			} else {
				// El document pot ser null si s'ha esborrat al tipus d'exedient
				documentDescripcio = document != null ? document.getNom() : documentStore.getCodiDocument();
			}
			
			ArxiuDto arxiu = documentHelper.getArxiuPerDocumentStoreId(
					documentStore.getId(),
					false,
					false);
			
			if (arxiu.getTipusMime() == null)
				arxiu.setTipusMime(documentHelper.getContentType(arxiu.getNom()));
			
			// Si la descripició no acaba amb l'extensió l'afegeix
			String extensio = "." + arxiu.getExtensio();
			if (!documentDescripcio.endsWith(extensio))
				documentDescripcio += extensio;
			
			// Corregeix el nom si ja hi ha un altre document amb el mateix nom i posant l'extensio
			if (documentsExistents.contains(documentDescripcio)) {
				int occurrences = 1;
				String novaDescripcio;
				do {
					// descripcio.ext := descripcio (1).ext
					novaDescripcio = documentDescripcio.substring(0, documentDescripcio.lastIndexOf(extensio)) + " (" + occurrences++ + ")" + extensio;
				} while (documentsExistents.contains(novaDescripcio));
				documentDescripcio = novaDescripcio;
			}
			documentsExistents.add(documentDescripcio);
			
			ContingutArxiu contingutArxiu = pluginHelper.arxiuDocumentCrearActualitzar(
					expedient,
					documentDescripcio,
					documentStore,
					arxiu);
			documentStore.setArxiuUuid(contingutArxiu.getIdentificador());
			es.caib.plugins.arxiu.api.Document documentArxiu = pluginHelper.arxiuDocumentInfo(
					contingutArxiu.getIdentificador(),
					null,
					false,
					true);
			documentStore.setNtiIdentificador(
					documentArxiu.getMetadades().getIdentificador());
			
			if (documentStore.isSignat()) {
				
				pluginHelper.arxiuDocumentGuardarPdfFirmat(
						expedient,
						documentStore,
						documentDescripcio,
						arxiu);
				documentArxiu = pluginHelper.arxiuDocumentInfo(
						documentStore.getArxiuUuid(),
						null,
						false,
						true);
				documentHelper.actualitzarNtiFirma(documentStore, documentArxiu);
			}
			documentStore.setArxiuContingut(null);			
		}

		// Informa convorme l'expedient és NTI i a l'Arxiu
		expedient.setArxiuActiu(true);
		expedient.setNtiActiu(true);

		if (expedient.getDataFi() != null) {
			this.tancarExpedientArxiu(expedient);
		}
	}
	
	/** Mètode per firmar els documents de l'expedient sense firma. Primer fa una validació
	 * de que es pugui firmar.
	 * 
	 * @param expedient
	 */
	@Transactional
	public void firmarDocumentsPerArxiuFiExpedient(Expedient expedient) {

		//List<DocumentStore> documents = documentStoreRepository.findByProcessInstanceId(expedient.getProcessInstanceId());
		List<DocumentStore> documents = new ArrayList<DocumentStore>();
		List<InstanciaProcesDto> arbreProcesInstance = expedientHelper.getArbreInstanciesProces(expedient.getProcessInstanceId());
		
		// Genera llista de tots els documents del expedient
		for(InstanciaProcesDto procesInstance :arbreProcesInstance) {
			documents.addAll(
					documentStoreRepository.findByProcessInstanceId(procesInstance.getId())
					);
		}
		
		
		List<DocumentStore> documentsPerSignar = new ArrayList<DocumentStore>();
		List<DocumentStore> documentsNoValids = new ArrayList<DocumentStore>();
		
		// Valida que els documents es puguin firmar
		for (DocumentStore documentStore : documents) {
			if (!documentStore.isSignat()) {
				if (!PdfUtils.isArxiuConvertiblePdf(documentStore.getArxiuNom())) {
					documentsNoValids.add(documentStore);
				} else {
					documentsPerSignar.add(documentStore);
				}
			}
		}
		if (!documentsNoValids.isEmpty()) {
			// Informa de l'error de validació
			StringBuilder llistaDocuments = new StringBuilder();
			for (DocumentStore d : documentsNoValids) {
				if(llistaDocuments.length() > 0)
					llistaDocuments.append(", ");
				llistaDocuments.append(d.getArxiuNom());
			}
			throw new ValidacioException(messageHelper.getMessage("document.controller.firma.servidor.validacio.conversio.documents", new Object[]{ llistaDocuments.toString(), PdfUtils.getExtensionsConvertiblesPdf()}));
		}
		
		// Firma en el servidor els documents pendents de firma
		for (DocumentStore documentStore: documentsPerSignar) {
				documentHelper.firmaServidor(
						documentStore.getProcessInstanceId(),
						//expedient.getProcessInstanceId(), 
						documentStore.getId(), 
						messageHelper.getMessage("document.controller.firma.servidor.default.message"),
						true);
		}
	}
	
	public void relacioCrear(
			Expedient origen,
			Expedient desti) {
		workflowRetroaccioApi.afegirInformacioRetroaccioPerExpedient(
				origen.getId(),
				ExpedientRetroaccioTipus.EXPEDIENT_RELACIO_AFEGIR,
				desti.getId().toString(),
				ExpedientRetroaccioEstat.IGNORAR);
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
		WToken token = workflowEngineApi.getTokenById(tokenId);
		String nodeNameVell = token.getNodeName();
		ExpedientDto piexp = workflowEngineApi.expedientFindByProcessInstanceId(
				token.getProcessInstanceId());
		workflowEngineApi.tokenRedirect(
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
		ExpedientDto piexp = workflowEngineApi.expedientFindByProcessInstanceId(
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
		ExpedientDto piexp = workflowEngineApi.expedientFindByProcessInstanceId(
				processInstanceId);
		if (piexp != null)
			expedient = expedientRepository.findOne(piexp.getId());
		if (expedient == null) {
			Expedient expedientIniciant = ThreadLocalInfo.getExpedient();
			if (expedientIniciant != null && expedientIniciant.getProcessInstanceId().equals(processInstanceId)) {
				expedient = expedientIniciant;
			} else {
				throw new NoTrobatException(Expedient.class, "PID:" + processInstanceId);
			}
		}
		return expedient;
	}
	
	/** Per buscar per títol
	 * 
	 * @param entornId
	 * @param expedientTipusId
	 * @param titol
	 * @return
	 */
	public List<Expedient> findByEntornIdAndTipusAndTitol(Long entornId, Long expedientTipusId, String titol) {
		List<Expedient> expedients = expedientRepository.findByEntornIdAndTipusIdAndTitol(
				entornId, 
				expedientTipusId, 
				titol == null, 
				titol != null? titol : "");
		return expedients;
	}

	
	public DefinicioProces findDefinicioProcesByProcessInstanceId(
			String processInstanceId) {
		String processDefinitionId = workflowEngineApi.getProcessInstance(processInstanceId).getProcessDefinitionId();
		DefinicioProces definicioProces = definicioProcesRepository.findByJbpmId(processDefinitionId);
		if (definicioProces == null) {
			throw new NoTrobatException(
					DefinicioProces.class,
					processDefinitionId);
		}
		return definicioProces;
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
		resposta.add(
				getCampExpedient(ExpedientCamps.EXPEDIENT_CAMP_ERRORS_REINDEXACIO));
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
	
	public void omplirPermisosExpedientTipus(ExpedientTipusDto expedientTipus) {
		List<ExpedientTipusDto> expedientsTipus = new ArrayList<ExpedientTipusDto>();
		expedientsTipus.add(expedientTipus);
		omplirPermisosExpedientsTipus(expedientsTipus);
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
	public void trobarAlertesExpedient(Expedient expedient, ExpedientDto dto) {
		/*Expedient expedient = this.getExpedientComprovantPermisos(
				dto.getId(),
				true,
				false,
				false,
				false);*/
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

	/**
	 * 
	 * @param expedient
	 */
	public void verificarFinalitzacioExpedient(
			Expedient expedient) {
		// actualitza l'expedient si el seu procés està finalitzat
		verificarFinalitzacioProcessInstance(expedient.getProcessInstanceId());
		
		// Obté la llista de totes les instancies de processos finalitzats excepte l'actual
		List<String> processInstanceFinalitzatIds = ThreadLocalInfo.getProcessInstanceFinalitzatIds();
		processInstanceFinalitzatIds.remove(expedient.getProcessInstanceId());

		// actualitza tots els expedients processos finalitzats
		for (String processInstanceId: processInstanceFinalitzatIds) {
			verificarFinalitzacioProcessInstance(processInstanceId);
		}
		ThreadLocalInfo.clearProcessInstanceFinalitzatIds();
	}

	public List<InstanciaProcesDto> getArbreInstanciesProces(
			String processInstanceId) {
		List<InstanciaProcesDto> resposta = new ArrayList<InstanciaProcesDto>();
		WProcessInstance rootProcessInstance = workflowEngineApi.getRootProcessInstance(processInstanceId);
		List<WProcessInstance> piTree = workflowEngineApi.getProcessInstanceTree(rootProcessInstance.getId());
		for (WProcessInstance jpi: piTree) {
			resposta.add(getInstanciaProcesById(jpi.getId()));
		}
		return resposta;
	}
	public InstanciaProcesDto getInstanciaProcesById(String processInstanceId) {
		InstanciaProcesDto dto = new InstanciaProcesDto();
		dto.setId(processInstanceId);
		WProcessInstance pi = workflowEngineApi.getProcessInstance(processInstanceId);
		if (pi.getProcessInstance() == null)
			return null;
		dto.setInstanciaProcesPareId(pi.getParentProcessInstanceId());
		if (pi.getDescription() != null && pi.getDescription().length() > 0)
			dto.setTitol(pi.getDescription());
		dto.setDefinicioProces(conversioTipusHelper.convertir(definicioProcesRepository.findByJbpmId(pi.getProcessDefinitionId()), DefinicioProcesDto.class));
		return dto;
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
		if (ExpedientCamps.EXPEDIENT_CAMP_ERRORS_REINDEXACIO.equals(campCodi)) {
			Camp campExpedient = new Camp();
			campExpedient.setCodi(campCodi);
			campExpedient.setTipus(TipusCamp.STRING);
			campExpedient.setEtiqueta(messageHelper.getMessage("etiqueta.exp.errorsReindexacio"));
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

	/**
	 * No verifica res, actualitza l'expedient si el procés està finalitzat
	 * 
	 * @param processInstanceId Identificador de la instancia del procés de l'expedient
	 */
	private void verificarFinalitzacioProcessInstance(
			String processInstanceId) {
		WProcessInstance processInstance = workflowEngineApi.getRootProcessInstance(processInstanceId);
		
		// Si el procés està finalitzat
		if (processInstance.getEndTime() != null) {
			// Actualitzar data de fi de l'expedient
			Expedient expedient = expedientRepository.findByProcessInstanceId(processInstanceId);
			if (expedient != null) {
				expedient.setDataFi(processInstance.getEndTime());
				
				//tancam l'expedient de l'arxiu si escau
				if (expedient.isArxiuActiu()) {
					this.tancarExpedientArxiu(expedient);
				}
			}
			// Finalitzar terminis actius
			for (TerminiIniciat terminiIniciat: terminiIniciatRepository.findByProcessInstanceId(processInstance.getId())) {
				if (terminiIniciat.getDataInici() != null) {
					terminiIniciat.setDataCancelacio(new Date());
					long[] timerIds = terminiIniciat.getTimerIdsArray();
					for (int i = 0; i < timerIds.length; i++)
						workflowEngineApi.suspendTimer(
								timerIds[i],
								new Date(Long.MAX_VALUE));
				}
			}
		}
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public Expedient iniciar(
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
			List<DadesDocumentDto> adjunts) throws Exception {
		
		Expedient expedient = new Expedient();
		Entorn entorn = entornHelper.getEntorn(entornId);
		if (usuari != null)
			comprovarUsuari(usuari);
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String usuariBo = (usuari != null) ? usuari : auth.getName();

		// Consulta de l'expedient tipus amb bloqueig del registre #1423
		ExpedientTipus expedientTipus = expedientTipusRepository.findByIdAmbBloqueig(expedientTipusId);
		
		if (expedientTipus == null) {
			throw new NoTrobatException(ExpedientTipus.class, expedientTipusId);
		}
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
		String iniciadorCodiCalculat = (iniciadorTipus.equals(IniciadorTipusDto.INTERN)) ? usuariBo : iniciadorCodi;
		expedient.setTipus(expedientTipus);
		expedient.setIniciadorTipus(conversioTipusHelper.convertir(iniciadorTipus, IniciadorTipus.class));
		expedient.setIniciadorCodi(iniciadorCodiCalculat);
		expedient.setEntorn(entorn);
		expedient.setProcessInstanceId(UUID.randomUUID().toString());
		String responsableCodiCalculat = (responsableCodi != null) ? responsableCodi : expedientTipus.getResponsableDefecteCodi();
		if (responsableCodiCalculat == null) {
			responsableCodiCalculat = iniciadorCodiCalculat;
		}
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
		expedient.setNtiActiu(expedientTipus.isNtiActiu());
		if (expedientTipus.isNtiActiu()) {
			expedient.setNtiVersion(VERSIO_NTI);
			expedient.setNtiOrgano(expedientTipus.getNtiOrgano());
			expedient.setNtiClasificacion(expedientTipus.getNtiClasificacion());
			expedient.setNtiSerieDocumental(expedientTipus.getNtiSerieDocumental());
			// L'identificador NTI no es pot generar en aquest moment perquè encara no
			// està disponible l'identificador de l'expedient.
		}
		mesuresTemporalsHelper.mesuraCalcular("Iniciar", "expedient", expedientTipus.getNom(), null, "Omplir dades");
		mesuresTemporalsHelper.mesuraIniciar("Iniciar", "expedient", expedientTipus.getNom(), null, "Assignar numeros");
		expedient.setNumeroDefault(
				getNumeroExpedientDefaultActual(
						entorn,
						expedientTipus,
						any));
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
		// Verifica si pot estar repetit per tipus d'expedient
		if (expedientTipus.getTeTitol() && expedientTipus.getDemanaTitol()) {
			List<Expedient> expedientMateixTitol = this.findByEntornIdAndTipusAndTitol(entornId, expedientTipusId, expedient.getTitol());
			if (expedientMateixTitol.size() > 0)
				throw new ValidacioException(
						messageHelper.getMessage(
								"error.expedient.titolrepetit",
								new Object[]{expedient.getNumero()}) );			
		}
		
		mesuresTemporalsHelper.mesuraCalcular("Iniciar", "expedient", expedientTipus.getNom(), null, "Actualitzar any i sequencia");
		
		// Inicia l'instància de procés jBPM
		mesuresTemporalsHelper.mesuraIniciar("Iniciar", "expedient", expedientTipus.getNom(), null, "Iniciar instancia de proces");
		
		ThreadLocalInfo.setExpedient(expedient);
		DefinicioProces definicioProces = null;
		if (definicioProcesId != null) {
			definicioProces = definicioProcesRepository.findById(definicioProcesId);
		} else {
			definicioProces = definicioProcesHelper.findDarreraVersioDefinicioProces(
					expedientTipus, 
					expedientTipus.getJbpmProcessDefinitionKey());
		}
		//MesurarTemps.diferenciaImprimirStdoutIReiniciar(mesuraTempsIncrementalPrefix, "7");
		WProcessInstance processInstance = workflowEngineApi.startProcessInstanceById(
				IniciadorTipusDto.INTERN.equals(iniciadorTipus) ?  usuariBo : null,
				definicioProces.getJbpmId(),
				variables);
		expedient.setProcessInstanceId(processInstance.getId());
		
		mesuresTemporalsHelper.mesuraCalcular("Iniciar", "expedient", expedientTipus.getNom(), null, "Iniciar instancia de proces");
		
		// Emmagatzema el nou expedient
		mesuresTemporalsHelper.mesuraIniciar("Iniciar", "expedient", expedientTipus.getNom(), null, "Desar el nou expedient");
		Expedient expedientPerRetornar = expedientRepository.saveAndFlush(expedient);
		mesuresTemporalsHelper.mesuraCalcular("Iniciar", "expedient", expedientTipus.getNom(), null, "Desar el nou expedient");

		// Verificar la ultima vegada que l'expedient va modificar el seu estat
		mesuresTemporalsHelper.mesuraIniciar("Iniciar", "expedient", expedientTipus.getNom(), null, "Afegir log");
		workflowRetroaccioApi.afegirInformacioRetroaccioPerProces(
				processInstance.getId(),
				ExpedientRetroaccioTipus.EXPEDIENT_INICIAR,
				null,
				ExpedientRetroaccioEstat.IGNORAR);
		mesuresTemporalsHelper.mesuraCalcular("Iniciar", "expedient", expedientTipus.getNom(), null, "Afegir log");

		mesuresTemporalsHelper.mesuraIniciar("Iniciar", "expedient", expedientTipus.getNom(), null, "Crear registre i convertir expedient");
		// Registra l'inici de l'expedient
		crearRegistreExpedient(
				expedient.getId(),
				usuariBo,
				Registre.Accio.INICIAR);
		mesuresTemporalsHelper.mesuraCalcular("Iniciar", "expedient", expedientTipus.getNom(), null, "Crear registre i convertir expedient");
					
		// Crear expedient a l'Arxiu
		mesuresTemporalsHelper.mesuraIniciar("Iniciar", "expedient", expedientTipus.getNom(), null, "Metadades NTI i creació a dins l'arxiu");
		if (expedientTipus.isNtiActiu()) {
			expedientPerRetornar.setNtiIdentificador(
					generarNtiIdentificador(expedientPerRetornar));
		}
		String arxiuUuid = null;
		if (expedientTipus.isArxiuActiu()) {
			// Crea l'expedient a l'arxiu i actualitza l'identificador.
			ContingutArxiu expedientCreat = pluginHelper.arxiuExpedientCrear(expedientPerRetornar);
			arxiuUuid = expedientCreat.getIdentificador();
			expedientPerRetornar.setArxiuUuid(
					expedientCreat.getIdentificador());
			// Consulta l'identificador NTI generat per l'arxiu i el modifica
			// a dins l'expedient creat.
			es.caib.plugins.arxiu.api.Expedient expedientArxiu = pluginHelper.arxiuExpedientInfo(
					expedientCreat.getIdentificador());
			expedientPerRetornar.setNtiIdentificador(
					expedientArxiu.getMetadades().getIdentificador());
			expedientPerRetornar.setArxiuActiu(true);
		}
		mesuresTemporalsHelper.mesuraCalcular("Iniciar", "expedient", expedientTipus.getNom(), null, "Metadades NTI i creació a dins l'arxiu");
		
		
		try {
			// Afegim els documents
			mesuresTemporalsHelper.mesuraIniciar("Iniciar", "expedient", expedientTipus.getNom(), null, "Afegir documents");
			if (documents != null){
				for (Map.Entry<String, DadesDocumentDto> doc: documents.entrySet()) {
					if (doc.getValue() != null) {
						documentHelper.crearDocument(
								null,
								expedient.getProcessInstanceId(),
								doc.getValue().getCodi(),
								doc.getValue().getData(),
								false,
								null,
								doc.getValue().getArxiuNom(),
								doc.getValue().getArxiuContingut(),
								null,
								null,
								null,
								null);
					}
				}
			}
			// Afegim els adjunts
			if (adjunts != null) {
				for (DadesDocumentDto adjunt: adjunts) {
					documentHelper.crearDocument(
							null,
							expedient.getProcessInstanceId(),
							null,
							adjunt.getData(),
							true,
							adjunt.getTitol(),
							adjunt.getArxiuNom(),
							adjunt.getArxiuContingut(),
							null,
							null,
							null,
							null);
				}
			}
			mesuresTemporalsHelper.mesuraCalcular("Iniciar", "expedient", expedientTipus.getNom(), null, "Afegir documents");
			
			mesuresTemporalsHelper.mesuraIniciar("Iniciar", "expedient", expedientTipus.getNom(), null, "Iniciar flux");
			// Actualitza les variables del procés
			workflowEngineApi.signalProcessInstance(expedient.getProcessInstanceId(), transitionName);
			mesuresTemporalsHelper.mesuraCalcular("Iniciar", "expedient", expedientTipus.getNom(), null, "Iniciar flux");
			mesuresTemporalsHelper.mesuraIniciar("Iniciar", "expedient", expedientTipus.getNom(), null, "Indexar expedient");
			// Comprova si després de l'inici ja està en un node fi
			this.verificarFinalitzacioExpedient(expedientPerRetornar);
			// Indexam l'expedient
			logger.debug("Indexant nou expedient (id=" + expedient.getProcessInstanceId() + ")");
			indexHelper.expedientIndexLuceneCreate(expedient.getProcessInstanceId());
			mesuresTemporalsHelper.mesuraCalcular("Iniciar", "expedient", expedientTipus.getNom(), null, "Indexar expedient");			
		} catch(Exception e) {
			// Rollback de la creació de l'expedient a l'arxiu
			if (arxiuUuid != null)
				try {
					logger.info("Rollback de la creació de l'expedient a l'Arxiu " + expedientPerRetornar.getIdentificador() + " amb uuid " + arxiuUuid);
					// Esborra l'expedient de l'arxiu
					pluginHelper.arxiuExpedientEsborrar(arxiuUuid);
				} catch(Exception re) {
					logger.error("Error esborrant l'expedient " + expedientPerRetornar.getIdentificador() + " amb uuid " + arxiuUuid + " :" + re.getMessage());
				}
			throw e;
		}
		
		mesuresTemporalsHelper.mesuraCalcular("Iniciar", "expedient", expedientTipus.getNom());
		return expedientPerRetornar;
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
			numero = this.getNumeroExpedientDefaultActual(
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

	@Transactional(readOnly=true)
	public String getNumeroExpedientActual(
			Long entornId,
			Long expedientTipusId,
			Integer any) {
		Entorn entorn = entornHelper.getEntorn(entornId);
		ExpedientTipus expedientTipus = expedientTipusRepository.findOne(expedientTipusId);
		return this.getNumeroExpedientActualAux(
				entorn,
				expedientTipus,
				any);
	}

	private String getNumeroExpedientActualAux(
			Entorn entorn,
			ExpedientTipus expedientTipus,
			Integer any) {
		long increment = 0;
		String numero = null;
		
		Expedient expedient = null;
		if (any == null) 
			any = Calendar.getInstance().get(Calendar.YEAR);
		do {
			numero = this.getNumeroExpedientActual(
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
			expedientTipus.updateSequencia(any, increment -1);
		}
		return numero;
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

	public String generarNtiIdentificador(
			Expedient expedient) {
		String id = expedient.getId().toString();
		int length = id.length();
		for(int i = 0; i < 27 - length; i++) id = "0" + id;
	    Calendar cal = Calendar.getInstance();
	    cal.setTime(expedient.getDataInici());
	    String any = String.valueOf(cal.get(Calendar.YEAR));
	    String org = expedient.getNtiOrgano();
	    return "ES_" + org + "_" + any + "_EXP_HEL" + id;
	}

	private static final Logger logger = LoggerFactory.getLogger(ExpedientHelper.class);
}
