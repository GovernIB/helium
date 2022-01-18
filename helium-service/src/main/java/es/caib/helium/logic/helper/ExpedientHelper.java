/**
 * 
 */
package es.caib.helium.logic.helper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.security.acls.model.Permission;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import es.caib.helium.client.engine.model.WProcessInstance;
import es.caib.helium.client.engine.model.WToken;
import es.caib.helium.client.expedient.expedient.ExpedientClientService;
import es.caib.helium.client.expedient.expedient.enums.ExpedientEstatTipusEnum;
import es.caib.helium.client.expedient.proces.ProcesClientService;
import es.caib.helium.client.expedient.proces.model.ProcesDto;
import es.caib.helium.client.expedient.tasca.TascaClientService;
import es.caib.helium.client.expedient.tasca.model.ConsultaTascaDades;
import es.caib.helium.client.expedient.tasca.model.TascaDto;
import es.caib.helium.logic.helper.PermisosHelper.ObjectIdentifierExtractor;
import es.caib.helium.logic.intf.WorkflowEngineApi;
import es.caib.helium.logic.intf.WorkflowRetroaccioApi;
import es.caib.helium.logic.intf.WorkflowRetroaccioApi.ExpedientRetroaccioEstat;
import es.caib.helium.logic.intf.WorkflowRetroaccioApi.ExpedientRetroaccioTipus;
import es.caib.helium.logic.intf.WorkflowRetroaccioApi.RetroaccioInfo;
import es.caib.helium.logic.intf.dto.ArxiuDto;
import es.caib.helium.logic.intf.dto.DadesDocumentDto;
import es.caib.helium.logic.intf.dto.DefinicioProcesDto;
import es.caib.helium.logic.intf.dto.EntornDto;
import es.caib.helium.logic.intf.dto.EstatDto;
import es.caib.helium.logic.intf.dto.ExpedientDto;
import es.caib.helium.logic.intf.dto.ExpedientDto.IniciadorTipusDto;
import es.caib.helium.logic.intf.dto.ExpedientTipusDto;
import es.caib.helium.logic.intf.dto.InstanciaProcesDto;
import es.caib.helium.logic.intf.dto.MotorTipusEnum;
import es.caib.helium.logic.intf.dto.PersonaDto;
import es.caib.helium.logic.intf.dto.expedient.ExpedientInfoDto;
import es.caib.helium.logic.intf.exception.NoTrobatException;
import es.caib.helium.logic.intf.exception.PermisDenegatException;
import es.caib.helium.logic.intf.exception.ValidacioException;
import es.caib.helium.logic.intf.util.Constants;
import es.caib.helium.logic.intf.util.GlobalProperties;
import es.caib.helium.logic.security.ExtendedPermission;
import es.caib.helium.logic.util.PdfUtils;
import es.caib.helium.persist.entity.Alerta;
import es.caib.helium.persist.entity.Camp;
import es.caib.helium.persist.entity.Camp.TipusCamp;
import es.caib.helium.persist.entity.DefinicioProces;
import es.caib.helium.persist.entity.Document;
import es.caib.helium.persist.entity.DocumentStore;
import es.caib.helium.persist.entity.Entorn;
import es.caib.helium.persist.entity.Estat;
import es.caib.helium.persist.entity.Expedient;
import es.caib.helium.persist.entity.Expedient.IniciadorTipus;
import es.caib.helium.persist.entity.ExpedientTipus;
import es.caib.helium.persist.entity.Registre;
import es.caib.helium.persist.entity.SequenciaAny;
import es.caib.helium.persist.entity.SequenciaDefaultAny;
import es.caib.helium.persist.entity.TerminiIniciat;
import es.caib.helium.persist.repository.AlertaRepository;
import es.caib.helium.persist.repository.DefinicioProcesRepository;
import es.caib.helium.persist.repository.DocumentStoreRepository;
import es.caib.helium.persist.repository.EstatRepository;
import es.caib.helium.persist.repository.ExpedientRepository;
import es.caib.helium.persist.repository.ExpedientTipusRepository;
import es.caib.helium.persist.repository.ReassignacioRepository;
import es.caib.helium.persist.repository.RegistreRepository;
import es.caib.helium.persist.repository.TerminiIniciatRepository;
import es.caib.helium.persist.util.ThreadLocalInfo;
import es.caib.plugins.arxiu.api.ContingutArxiu;
import es.caib.plugins.arxiu.api.ExpedientEstat;

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
	private ReassignacioRepository reassignacioRepository;

	@Resource
	private ExpedientHelper expedientHelper;
	@Resource
	private EntornHelper entornHelper;
	@Resource
	private PermisosHelper permisosHelper;
	@Resource
	private DocumentHelper documentHelper;
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
	private MessageServiceHelper messageServiceHelper;
	@Resource
	private ConversioTipusServiceHelper conversioTipusServiceHelper;
//	@Resource
//	private MesuresTemporalsHelper mesuresTemporalsHelper;
	@Resource
	private DefinicioProcesHelper definicioProcesHelper;
	@Resource
	private GlobalProperties globalProperties;
	@Resource
	private ExpedientClientService expedientClientService;
	@Resource
	private ProcesClientService procesClientService;
	@Resource
	private TascaClientService tascaClientService;



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
		dto.setIniciadorTipus(conversioTipusServiceHelper.convertir(expedient.getIniciadorTipus(), IniciadorTipusDto.class));
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
		dto.setTipus(conversioTipusServiceHelper.convertir(expedient.getTipus(), ExpedientTipusDto.class));
		dto.setEntorn(conversioTipusServiceHelper.convertir(expedient.getEntorn(), EntornDto.class));
		if (expedient.getEstat() != null)
			dto.setEstat(conversioTipusServiceHelper.convertir(expedient.getEstat(), EstatDto.class));
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

	public ExpedientInfoDto toExpedientInfo(Expedient expedient) {
		if (expedient == null) {
			return null;
		}

		ExpedientInfoDto resposta = new ExpedientInfoDto();
		resposta.setId(expedient.getId());
		resposta.setTitol(expedient.getTitol());
		resposta.setNumero(expedient.getNumero());
		resposta.setNumeroDefault(expedient.getNumeroDefault());
		resposta.setDataInici(expedient.getDataInici());
		resposta.setDataFi(expedient.getDataFi());
		resposta.setComentari(expedient.getComentari());
		resposta.setComentariAnulat(expedient.getComentariAnulat());
		resposta.setInfoAturat(expedient.getInfoAturat());
		if (expedient.getIniciadorTipus().equals(IniciadorTipusDto.INTERN))
			resposta.setIniciadorTipus(ExpedientInfoDto.IniciadorTipus.INTERN);
		else if (expedient.getIniciadorTipus().equals(IniciadorTipusDto.SISTRA))
			resposta.setIniciadorTipus(ExpedientInfoDto.IniciadorTipus.SISTRA);
		resposta.setIniciadorCodi(expedient.getIniciadorCodi());
		resposta.setResponsableCodi(expedient.getResponsableCodi());
		resposta.setGeoPosX(expedient.getGeoPosX());
		resposta.setGeoPosY(expedient.getGeoPosY());
		resposta.setGeoReferencia(expedient.getGeoReferencia());
		resposta.setRegistreNumero(expedient.getRegistreNumero());
		resposta.setRegistreData(expedient.getRegistreData());
		resposta.setUnitatAdministrativa(expedient.getUnitatAdministrativa());
		resposta.setIdioma(expedient.getIdioma());
		resposta.setAutenticat(expedient.isAutenticat());
		resposta.setTramitadorNif(expedient.getTramitadorNif());
		resposta.setTramitadorNom(expedient.getTramitadorNom());
		resposta.setInteressatNif(expedient.getInteressatNif());
		resposta.setInteressatNom(expedient.getInteressatNom());
		resposta.setRepresentantNif(expedient.getRepresentantNif());
		resposta.setRepresentantNom(expedient.getRepresentantNom());
		resposta.setAvisosHabilitats(expedient.isAvisosHabilitats());
		resposta.setAvisosEmail(expedient.getAvisosEmail());
		resposta.setAvisosMobil(expedient.getAvisosMobil());
		resposta.setNotificacioTelematicaHabilitada(expedient.isNotificacioTelematicaHabilitada());
		resposta.setTramitExpedientIdentificador(expedient.getTramitExpedientIdentificador());
		resposta.setTramitExpedientClau(expedient.getTramitExpedientClau());
		resposta.setExpedientTipusCodi(expedient.getTipus().getCodi());
		resposta.setExpedientTipusNom(expedient.getTipus().getNom());
		resposta.setEntornCodi(expedient.getEntorn().getCodi());
		resposta.setEntornNom(expedient.getEntorn().getNom());
		if (expedient.getEstat() != null) {
			resposta.setEstatCodi(expedient.getEstat().getCodi());
			resposta.setEstatNom(expedient.getEstat().getNom());
		}
		resposta.setProcessInstanceId(new Long(expedient.getProcessInstanceId()).longValue());
		resposta.setAmbRetroaccio(expedient.isAmbRetroaccio());
		return resposta;
	}

	public Expedient getExpedientComprovantPermisos(
			Long id,
			Permission[] permisos) {
		Optional<Expedient> expedientOptional = expedientRepository.findById(id);
		if (expedientOptional.isEmpty()) {
			throw new NoTrobatException(
					Expedient.class,
					id);
		}
		Expedient expedient = expedientOptional.get();

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
		Optional<Expedient> expedientOptional = expedientRepository.findById(id);
		if (expedientOptional.isEmpty()) {
			throw new NoTrobatException(
					Expedient.class,
					id);
		}
		Expedient expedient = expedientOptional.get();
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
				// TODO: Revisar:
				workflowRetroaccioApi.afegirInformacioRetroaccioPerExpedient(
						ambRetroaccio,
						expedient.getProcessInstanceId(),
						RetroaccioInfo.ESTAT + "#@#" + "---"); // No seria: (expedient.getEstat() == null ? "---" : expedient.getEstat().getId())); ?
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

		
		expedientClientService.modificarExpedient(
				expedient.getId(),
				expedient.getTipus().getTeNumero(),
				expedient.getNumero(),
				expedient.getTipus().getDemanaTitol(),
				expedient.getTitol(),
				expedient.getDataInici(),
				expedient.getDataFi(),
				expedient.getEstat() != null ? expedient.getEstat().getId() : null);
		
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
		// TODO: Mètriques
//		mesuresTemporalsHelper.mesuraIniciar(
//				"Aturar",
//				"expedient",
//				expedient.getTipus().getNom());
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
		expedientClientService.aturar(expedient.getId(), motiu);
		expedientRegistreHelper.crearRegistreAturarExpedient(
				expedient.getId(),
				(usuari != null) ? usuari : SecurityContextHolder.getContext().getAuthentication().getName(),
				motiu);
//		mesuresTemporalsHelper.mesuraCalcular(
//				"Aturar",
//				"expedient",
//				expedient.getTipus().getNom());
	}

	public void reprendre(
			Expedient expedient,
			String usuari) {
		// TODO: Mètriques
//		mesuresTemporalsHelper.mesuraIniciar(
//				"Reprendre",
//				"expedient",
//				expedient.getTipus().getNom());

		// 1 crida motor retroacció
		workflowRetroaccioApi.afegirInformacioRetroaccioPerExpedient(
				expedient.getId(),
				ExpedientRetroaccioTipus.EXPEDIENT_REPRENDRE,
				null,
				ExpedientRetroaccioEstat.IGNORAR);
		// 2 crida al client ms processos
		List<WProcessInstance> processInstancesTree = workflowEngineApi.getProcessInstanceTree(
				expedient.getProcessInstanceId());
		String[] ids = new String[processInstancesTree.size()];
		int i = 0;
		for (WProcessInstance pi: processInstancesTree)
			ids[i++] = pi.getId();
		// 3 crida motor resume procés
		workflowEngineApi.resumeProcessInstances(ids);
		expedient.setInfoAturat(null);
		// 4 crida ms expedients per modifica informació
		expedientClientService.reprendre(expedient.getId());
		expedientRegistreHelper.crearRegistreReprendreExpedient(
				expedient.getId(),
				(usuari != null) ? usuari : SecurityContextHolder.getContext().getAuthentication().getName());
	}

	@Transactional
	public void finalitzar(long expedientId) {

		Expedient expedient = expedientHelper.getExpedientComprovantPermisos(
				expedientId,
				new Permission[] {
						ExtendedPermission.WRITE,
						ExtendedPermission.ADMINISTRATION});

		// TODO: Mètriques
//		mesuresTemporalsHelper.mesuraIniciar(
//				"Finalitzar",
//				"expedient",
//				expedient.getTipus().getNom());

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
		
		expedientClientService.finalitzar(expedientId, dataFinalitzacio);
		
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
	public void tancarExpedientArxiu(Expedient expedient) {
		var continguts = pluginHelper.arxiuExpedientInfo(expedient.getArxiuUuid()).getContinguts();
		if(continguts == null || continguts.isEmpty()) {
			// S'eborra l'expedient del arxiu si no te cap document.
			pluginHelper.arxiuExpedientEsborrar(expedient.getArxiuUuid());
			expedient.setArxiuUuid(null);
		}else {
			//firmem els documents que no estan firmats
			expedientHelper.firmarDocumentsPerArxiuFiExpedient(expedient);

			// Tanca l'expedient a l'arxiu.
			var metadades = pluginHelper.arxiuExpedientInfo(expedient.getArxiuUuid()).getMetadades();
			if(metadades.getEstat() != ExpedientEstat.TANCAT) {
				pluginHelper.arxiuExpedientTancar(expedient.getArxiuUuid());
			}
		}
	}

	@Transactional
	public void desfinalitzar(
			Expedient expedient,
			String usuari) {
		// TODO: Mètriques
//		mesuresTemporalsHelper.mesuraIniciar(
//				"Desfinalitzar",
//				"expedient",
//				expedient.getTipus().getNom());

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

		expedientClientService.desfinalitzar(
				expedient.getId(), 
				expedient.getEstat() != null ? expedient.getEstat().getId() : null);		
//		mesuresTemporalsHelper.mesuraCalcular(
//				"Desfinalitzar",
//				"expedient",
//				expedient.getTipus().getNom());

        if (expedient.isArxiuActiu()) {
    		reobrirExpedient(expedient);
        }
	}

	public void reobrirExpedient(Expedient expedient) {
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
		var expedientArxiu = pluginHelper.arxiuExpedientInfo(expedientCreat.getIdentificador());
		expedient.setNtiIdentificador(expedientArxiu.getMetadades().getIdentificador());

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
			throw new ValidacioException(messageServiceHelper.getMessage("document.controller.firma.servidor.validacio.conversio.documents", new Object[]{ llistaDocuments.toString(), PdfUtils.getExtensionsConvertiblesPdf()}));
		}

		// Firma en el servidor els documents pendents de firma
		for (DocumentStore documentStore: documentsPerSignar) {
				documentHelper.firmaServidor(
						documentStore.getProcessInstanceId(),
						documentStore.getId(),
						messageServiceHelper.getMessage("document.controller.firma.servidor.default.message"));
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
		Long expId = procesClientService.getProcesExpedientId(token.getProcessInstanceId());
		workflowEngineApi.tokenRedirect(
				tokenId,
				nodeName,
				cancelTasks,
				true,
				false);
		expedientRegistreHelper.crearRegistreRedirigirToken(
				expId,
				token.getProcessInstanceId(),
				SecurityContextHolder.getContext().getAuthentication().getName(),
				token.getFullName(),
				nodeNameVell,
				nodeName);
	}

	public void comprovarInstanciaProces(
			Expedient expedient,
			String processInstanceId) {
		Long expId = procesClientService.getProcesExpedientId(processInstanceId);
		if (expId != expedient.getId().longValue()) {
			throw new NoTrobatException(
					WProcessInstance.class,
					Long.valueOf(processInstanceId));
		}
	}

	public Expedient findAmbEntornIId(Long entornId, Long id) {
		return expedientRepository.findByEntornIdAndId(entornId, id);
	}

	public Expedient findExpedientByProcessInstanceId(String processInstanceId) {
		Expedient expedient = null;
		// Busca l'expedient per procés principial
		expedient = expedientRepository.findByProcessInstanceId(processInstanceId);
		// Mira si és l'expedient iniciant del thread
		if (expedient == null 
				&&ThreadLocalInfo.getExpedient() != null 
				&& ThreadLocalInfo.getExpedient().getProcessInstanceId().equals(processInstanceId)) {
			expedient = ThreadLocalInfo.getExpedient();
		} 
		// Mira si està en el Map d'expedients que s'estan iniciant
		if (expedient == null) {
			expedient = this.expedientsIniciGet(processInstanceId);
		}
		// Com a darrera opció consulta informació del procés del MS d'expedients i tasques per obtenir l'id
		if (expedient == null) {
			ProcesDto proces = procesClientService.getProcesV1(processInstanceId);
			if (proces != null && proces.getExpedientId() != null) {
				Optional<Expedient> expedientOptional = expedientRepository.findById(proces.getExpedientId());
				expedient = expedientOptional.orElse(null);
			}
		}
		if (expedient == null) {
			throw new NoTrobatException(Expedient.class, "PID:" + processInstanceId);
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
				getCampExpedient(Constants.EXPEDIENT_CAMP_ID));
		resposta.add(
				getCampExpedient(Constants.EXPEDIENT_CAMP_NUMERO));
		resposta.add(
				getCampExpedient(Constants.EXPEDIENT_CAMP_TITOL));
		resposta.add(
				getCampExpedient(Constants.EXPEDIENT_CAMP_COMENTARI));
		resposta.add(
				getCampExpedient(Constants.EXPEDIENT_CAMP_INICIADOR));
		resposta.add(
				getCampExpedient(Constants.EXPEDIENT_CAMP_RESPONSABLE));
		resposta.add(
				getCampExpedient(Constants.EXPEDIENT_CAMP_DATA_INICI));
		resposta.add(
				getCampExpedient(Constants.EXPEDIENT_CAMP_ESTAT));
		resposta.add(
				getCampExpedient(Constants.EXPEDIENT_CAMP_ERRORS_REINDEXACIO));
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

		List<ProcesDto> processos = procesClientService.getLlistatProcessos(processInstanceId);
//		String procesArrelId = procesClientService.getProcesV1(processInstanceId).getProcesArrelId();
//		List<ProcesDto> processos = procesClientService.findProcessAmbFiltrePaginatV1(ConsultaProcesDades.builder()
//				.procesArrelId(procesArrelId)
//				.build()).getContent();

		List<InstanciaProcesDto> resposta = new ArrayList<InstanciaProcesDto>();
		for (ProcesDto procesMs : processos) {
			resposta.add(this.procesMsToInstanciaProcesDto(procesMs));
		}
		return resposta;

	}
	
	public InstanciaProcesDto getInstanciaProcesById(String processInstanceId) {
		
		ProcesDto procesMs = procesClientService.getProcesV1(processInstanceId);
		if (procesMs == null)
			return null;
		
		InstanciaProcesDto dto = this.procesMsToInstanciaProcesDto(procesMs);
		
		return dto;
	}



	private InstanciaProcesDto procesMsToInstanciaProcesDto(ProcesDto procesMs) {
		InstanciaProcesDto dto = new InstanciaProcesDto();
		dto.setId(procesMs.getProcesId());
		dto.setInstanciaProcesPareId(procesMs.getProcesPareId());
		if (procesMs.getDescripcio() != null && procesMs.getDescripcio().length() > 0) {
			dto.setTitol(procesMs.getDescripcio());
		}
		DefinicioProces definicioProces = definicioProcesRepository.findByJbpmId(procesMs.getProcessDefinitionId());
		dto.setDefinicioProces(conversioTipusServiceHelper.convertir(definicioProces, DefinicioProcesDto.class));
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
						//   l'any, i d'aquesta manera assignar com a número inicial el major utilitzat + 1
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
				// TODO: Mirar si fem alguna cosa més a part de substituir valors
				String entorn_cod = expedientTipus.getEntorn().getCodi();
				String tipexp_cod = expedientTipus.getCodi();

				return expressio
						.replace("${entorn_cod}", entorn_cod)
						.replace("${tipexp_cod}", tipexp_cod)
						.replace("${any}", String.valueOf(any))
						.replace("${seq}", String.valueOf(seq));
//				final Map<String, Object> context = new HashMap<String, Object>();
//				context.put("entorn_cod", expedientTipus.getEntorn().getCodi());
//				context.put("tipexp_cod", expedientTipus.getCodi());
//				context.put("any", any);
//				context.put("seq", seq);
//				ExpressionEvaluator evaluator = new ExpressionEvaluatorImpl();
//				String resultat = (String)evaluator.evaluate(
//						expressio,
//						String.class,
//						new VariableResolver() {
//							public Object resolveVariable(String name)
//									throws ELException {
//								return context.get(name);
//							}
//						},
//						null);
//				return resultat;
			} catch (Exception ex) {
				return "#invalid expression#";
			}
		} else {
			return new Long(seq).toString();
		}
	}

	private Camp getCampExpedient(String campCodi) {
		if (Constants.EXPEDIENT_CAMP_ID.equals(campCodi)) {
			Camp campExpedient = new Camp();
			campExpedient.setCodi(campCodi);
			campExpedient.setTipus(TipusCamp.INTEGER);
			campExpedient.setEtiqueta(messageServiceHelper.getMessage("etiqueta.exp.id"));
			return campExpedient;
		}
		if (Constants.EXPEDIENT_CAMP_NUMERO.equals(campCodi)) {
			Camp campExpedient = new Camp();
			campExpedient.setCodi(campCodi);
			campExpedient.setTipus(TipusCamp.STRING);
			campExpedient.setEtiqueta(messageServiceHelper.getMessage("etiqueta.exp.numero"));
			return campExpedient;
		}
		if (Constants.EXPEDIENT_CAMP_TITOL.equals(campCodi)) {
			Camp campExpedient = new Camp();
			campExpedient.setCodi(campCodi);
			campExpedient.setTipus(TipusCamp.STRING);
			campExpedient.setEtiqueta(messageServiceHelper.getMessage("etiqueta.exp.titol"));
			return campExpedient;
		}
		if (Constants.EXPEDIENT_CAMP_COMENTARI.equals(campCodi)) {
			Camp campExpedient = new Camp();
			campExpedient.setCodi(campCodi);
			campExpedient.setTipus(TipusCamp.STRING);
			campExpedient.setEtiqueta(messageServiceHelper.getMessage("etiqueta.exp.comentari"));
			return campExpedient;
		}
		if (Constants.EXPEDIENT_CAMP_INICIADOR.equals(campCodi)) {
			Camp campExpedient = new Camp();
			campExpedient.setCodi(campCodi);
			campExpedient.setTipus(TipusCamp.SUGGEST);
			campExpedient.setEtiqueta(messageServiceHelper.getMessage("etiqueta.exp.iniciador"));
			return campExpedient;
		}
		if (Constants.EXPEDIENT_CAMP_RESPONSABLE.equals(campCodi)) {
			Camp campExpedient = new Camp();
			campExpedient.setCodi(campCodi);
			campExpedient.setTipus(TipusCamp.SUGGEST);
			campExpedient.setEtiqueta(messageServiceHelper.getMessage("etiqueta.exp.responsable"));
			return campExpedient;
		}
		if (Constants.EXPEDIENT_CAMP_DATA_INICI.equals(campCodi)) {
			Camp campExpedient = new Camp();
			campExpedient.setCodi(campCodi);
			campExpedient.setTipus(TipusCamp.DATE);
			campExpedient.setEtiqueta(messageServiceHelper.getMessage("etiqueta.exp.data_ini"));
			return campExpedient;
		}
		if (Constants.EXPEDIENT_CAMP_ESTAT.equals(campCodi)) {
			Camp campExpedient = new Camp();
			campExpedient.setCodi(campCodi);
			campExpedient.setTipus(TipusCamp.SELECCIO);
			campExpedient.setEtiqueta(messageServiceHelper.getMessage("etiqueta.exp.estat"));
			return campExpedient;
		}
		if (Constants.EXPEDIENT_CAMP_ERRORS_REINDEXACIO.equals(campCodi)) {
			Camp campExpedient = new Camp();
			campExpedient.setCodi(campCodi);
			campExpedient.setTipus(TipusCamp.STRING);
			campExpedient.setEtiqueta(messageServiceHelper.getMessage("etiqueta.exp.errorsReindexacio"));
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
		return globalProperties.getProperty("es.caib.helium.numexp.expression");
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
					String[] timerIds = terminiIniciat.getTimerIdsArray();
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
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String usuariBo = null;
		if (usuari != null) {
			comprovarUsuari(usuari);
			usuariBo = usuari;
		} else {
			usuari = auth.getName();
			if (auth.isAuthenticated() && !(auth instanceof AnonymousAuthenticationToken))
				usuariBo = usuari;
		}
		// Consulta de l'expedient tipus amb bloqueig del registre #1423
		ExpedientTipus expedientTipus = expedientTipusRepository.findByIdAmbBloqueig(expedientTipusId);

		if (expedientTipus == null) {
			throw new NoTrobatException(ExpedientTipus.class, expedientTipusId);
		}
		// TODO: Mètriques
//		mesuresTemporalsHelper.mesuraIniciar("Iniciar", "expedient", expedientTipus.getNom());
//		mesuresTemporalsHelper.mesuraIniciar("Iniciar", "expedient", expedientTipus.getNom(), null, "Nou expedient");
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
		expedient.setIniciadorTipus(conversioTipusServiceHelper.convertir(iniciadorTipus, IniciadorTipus.class));
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
//		mesuresTemporalsHelper.mesuraCalcular("Iniciar", "expedient", expedientTipus.getNom(), null, "Omplir dades");
//		mesuresTemporalsHelper.mesuraIniciar("Iniciar", "expedient", expedientTipus.getNom(), null, "Assignar numeros");
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
//		mesuresTemporalsHelper.mesuraCalcular("Iniciar", "expedient", expedientTipus.getNom(), null, "Assignar numeros");
//		mesuresTemporalsHelper.mesuraIniciar("Iniciar", "expedient", expedientTipus.getNom(), null, "Verificar numero repetit");
		// Verifica si l'expedient té el número repetit
		if (expedient.getNumero() != null && (expedientRepository.findByEntornIdAndTipusIdAndNumero(
				entorn.getId(),
				expedientTipus.getId(),
				expedient.getNumero()) != null)) {
			throw new ValidacioException(
					messageServiceHelper.getMessage(
							"error.expedientService.jaExisteix",
							new Object[]{expedient.getNumero()}) );
		}
//		mesuresTemporalsHelper.mesuraCalcular("Iniciar", "expedient", expedientTipus.getNom(), null, "Verificar numero repetit");
//		mesuresTemporalsHelper.mesuraIniciar("Iniciar", "expedient", expedientTipus.getNom(), null, "Actualitzar any i sequencia");
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
						messageServiceHelper.getMessage(
								"error.expedient.titolrepetit",
								new Object[]{expedient.getNumero()}) );
		}

//		mesuresTemporalsHelper.mesuraCalcular("Iniciar", "expedient", expedientTipus.getNom(), null, "Actualitzar any i sequencia");

			// Emmagatzema el nou expedient
	//		mesuresTemporalsHelper.mesuraIniciar("Iniciar", "expedient", expedientTipus.getNom(), null, "Desar el nou expedient");
		expedient = expedientRepository.saveAndFlush(expedient);
		//		mesuresTemporalsHelper.mesuraCalcular("Iniciar", "expedient", expedientTipus.getNom(), null, "Desar el nou expedient");
		// Inicia l'instància de procés jBPM

//		mesuresTemporalsHelper.mesuraIniciar("Iniciar", "expedient", expedientTipus.getNom(), null, "Iniciar instancia de proces");

		ThreadLocalInfo.setExpedient(expedient);
		DefinicioProces definicioProces = null;
		if (definicioProcesId != null) {
			definicioProces = definicioProcesRepository.findById(definicioProcesId).get();
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

		//		mesuresTemporalsHelper.mesuraCalcular("Iniciar", "expedient", expedientTipus.getNom(), null, "Iniciar instancia de proces");

		// Afegeix l'expedient iniciant al map static
		this.expedientsIniciAdd(expedient);

		if (MotorTipusEnum.CAMUNDA.equals(definicioProces.getMotorTipus()))
			reassignarTasques(expedient);

		Long infoRetroaccioId = null;
		String arxiuUuid = null;
		es.caib.helium.client.expedient.expedient.model.ExpedientDto expedientMs = null;
		try {

			// Verificar la ultima vegada que l'expedient va modificar el seu estat
	//		mesuresTemporalsHelper.mesuraIniciar("Iniciar", "expedient", expedientTipus.getNom(), null, "Afegir log");
			infoRetroaccioId = workflowRetroaccioApi.afegirInformacioRetroaccioPerProces(
					processInstance.getId(),
					ExpedientRetroaccioTipus.EXPEDIENT_INICIAR,
					null,
					ExpedientRetroaccioEstat.IGNORAR);
	//		mesuresTemporalsHelper.mesuraCalcular("Iniciar", "expedient", expedientTipus.getNom(), null, "Afegir log");

	//		mesuresTemporalsHelper.mesuraIniciar("Iniciar", "expedient", expedientTipus.getNom(), null, "Crear registre i convertir expedient");
			// Registra l'inici de l'expedient
			crearRegistreExpedient(
					expedient.getId(),
					usuari,
					Registre.Accio.INICIAR);
	//		mesuresTemporalsHelper.mesuraCalcular("Iniciar", "expedient", expedientTipus.getNom(), null, "Crear registre i convertir expedient");

			// Crear expedient a l'Arxiu
	//		mesuresTemporalsHelper.mesuraIniciar("Iniciar", "expedient", expedientTipus.getNom(), null, "Metadades NTI i creació a dins l'arxiu");
			if (expedientTipus.isNtiActiu()) {
				expedient.setNtiIdentificador(
						generarNtiIdentificador(expedient));
			}
			if (expedientTipus.isArxiuActiu()) {
				// Crea l'expedient a l'arxiu i actualitza l'identificador.
				ContingutArxiu expedientCreat = pluginHelper.arxiuExpedientCrear(expedient);
				arxiuUuid = expedientCreat.getIdentificador();
				expedient.setArxiuUuid(
						expedientCreat.getIdentificador());
				// Consulta l'identificador NTI generat per l'arxiu i el modifica
				// a dins l'expedient creat.
				var expedientArxiu = pluginHelper.arxiuExpedientInfo(
						expedientCreat.getIdentificador());
				expedient.setNtiIdentificador(
						expedientArxiu.getMetadades().getIdentificador());
				expedient.setArxiuActiu(true);
			}
	//		mesuresTemporalsHelper.mesuraCalcular("Iniciar", "expedient", expedientTipus.getNom(), null, "Metadades NTI i creació a dins l'arxiu");

			// Afegim els documents
	//			mesuresTemporalsHelper.mesuraIniciar("Iniciar", "expedient", expedientTipus.getNom(), null, "Afegir documents");
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
			// TODO: eliminar indexació
			// Indexam l'expedient
//			logger.debug("Indexant nou expedient (id=" + expedient.getProcessInstanceId() + ")");
//			indexHelper.expedientIndexLuceneCreate(expedient.getProcessInstanceId());

			// Crea l'expedient al MS
			expedientMs = es.caib.helium.client.expedient.expedient.model.ExpedientDto.builder()
						.id(expedient.getId())
						.entornId(entornId)
						.expedientTipusId(expedientTipusId)
						.processInstanceId(expedient.getProcessInstanceId())
						.numero(expedient.getNumero() != null ? expedient.getNumero() : expedient.getNumeroDefault())
						.numeroDefault(expedient.getNumeroDefault())
						.titol(expedient.getTitol())
						.dataInici(expedient.getDataInici())
						.dataFi(expedient.getDataFi())
						.aturat(expedient.isAturat())
						.infoAturat(expedient.getInfoAturat())
						.anulat(expedient.isAnulat())
						.comentariAnulat(expedient.getComentariAnulat())
						.alertesTotals(Long.valueOf(expedient.getAlertes().size()))
						.alertesPendents(expedient.getAlertes().stream().filter(a -> true).count())
						.ambErrors(expedient.isErrorsIntegracions() || expedient.getErrorDesc() != null)
						.estatTipus(ExpedientEstatTipusEnum.INICIAT)
						.build();
			
			// Crea la informació del procés i l'expedient al MS d'expedients i tasques
			expedientClientService.createExpedientV1(expedientMs);
			
			// Inicia el procés
			workflowEngineApi.signalProcessInstance(expedient.getProcessInstanceId(), transitionName);

			// Comprova si després de l'inici ja està en un node fi
			this.verificarFinalitzacioExpedient(expedient);
		
		} catch(Exception e) {
			// Rollback de la creació del procés al Motor de WF
			workflowEngineApi.deleteProcessInstance(expedient.getProcessInstanceId());

			// Rollback de la informació de retroacció
			if (infoRetroaccioId != null) {
				workflowRetroaccioApi.eliminaInformacioRetroaccio(infoRetroaccioId);
			}

			// Rollback de la creació de l'expedient a l'arxiu
			if (arxiuUuid != null)
				try {
					logger.info("Rollback de la creació de l'expedient a l'Arxiu " + expedient.getIdentificador() + " amb uuid " + arxiuUuid);
					// Esborra l'expedient de l'arxiu
					pluginHelper.arxiuExpedientEsborrar(arxiuUuid);
				} catch(Exception re) {
					logger.error("Error esborrant l'expedient " + expedient.getIdentificador() + " amb uuid " + arxiuUuid + " :" + re.getMessage());
				}
			// Rollback de la creació de l'expedient al MS
			if (expedientMs != null) {
				try {
					logger.info("Rollback de la creació de l'expedient al MS " + expedient.getIdentificador() + " amb id " + expedientMs.getId());
					// Esborra l'expedient de l'arxiu
					expedientClientService.deleteExpedientV1(expedientMs.getId());
				} catch(Exception re) {
					logger.error("Error esborrant l'expedient " + expedient.getIdentificador() + " amb id " + expedientMs.getId() + " :" + re.getMessage());
				}
			}
			throw e;
		} finally {
			this.expedientsIniciDelete(expedient);
		}

//		mesuresTemporalsHelper.mesuraCalcular("Iniciar", "expedient", expedientTipus.getNom());
		return expedient;
	}

	private void reassignarTasques(Expedient expedient) {
		try {
			var tasques = tascaClientService.findTasquesAmbFiltrePaginatV1(ConsultaTascaDades.builder()
					.nomesPendents(true)
					.expedientId(expedient.getId())
					//.filtre("proces.procesId==" + expedient.getProcessInstanceId())
					.build());
			if (tasques != null && tasques.getContent() != null && tasques.hasContent()) {
				tasques.getContent().forEach(t -> reassignarTascaIfNeeded(expedient, t));
			}
		} catch (Exception ex) {
			logger.error("No s'han pogut obtenir les tasques le l'expedient iniciant: " + expedient.getId(), ex);
		}
	}

	private void reassignarTascaIfNeeded(Expedient expedient, TascaDto tasca) {
		try {
			if (tasca.getUsuariAssignat() != null) {
				Date ara = new Date();
				var reassignacio = reassignacioRepository.findByUsuariAndTipusExpedientId(
						tasca.getUsuariAssignat(),
						expedient.getTipus().getId(),
						ara,
						ara);
				// Si no es troba cerca una redirecció global
				if (reassignacio == null) {
					reassignacio = reassignacioRepository.findByUsuari(
							tasca.getUsuariAssignat(),
							ara,
							ara);
				}
				if (reassignacio != null) {

					var informacioRetroaccioId = workflowRetroaccioApi.afegirInformacioRetroaccioPerTasca(
							tasca.getTascaId(),
							WorkflowRetroaccioApi.ExpedientRetroaccioTipus.TASCA_REASSIGNAR,
							null);
					var expressio = "user(" + reassignacio.getUsuariDesti() + ")";
					workflowEngineApi.reassignTaskInstance(
							tasca.getTascaId(),
							expressio,
							expedient.getEntorn().getId());
					workflowRetroaccioApi.actualitzaParametresAccioInformacioRetroaccio(
							informacioRetroaccioId,
							reassignacio.getUsuariOrigen() + "::" + reassignacio.getUsuariDesti());
					String usuari = SecurityContextHolder.getContext().getAuthentication().getName();
					Registre registre = new Registre(
							new Date(),
							expedient.getId(),
							usuari,
							Registre.Accio.MODIFICAR,
							Registre.Entitat.TASCA,
							tasca.getTascaId());
					registre.setMissatge("Reassignació de la tasca amb l'expressió \"" + expressio + "\"");
					registreRepository.save(registre);
				}
			}
		} catch (Exception ex) {
			logger.error("No s'ha pogut reassignar la tasca: " + tasca.getTascaId(), ex);
		}
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
		ExpedientTipus expedientTipus = expedientTipusRepository.findById(expedientTipusId).get();
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

	public Registre crearRegistreExpedient(
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

	/** Mètode per consultar l'expedient per ID o llençar excepció si no existeix.
	 *
	 * @param expedientId
	 * @return L'expedient trobat.
	 * @throws NoTrobatException Si no troba l'expedient per identificador.
	 */
	public Expedient findById(Long expedientId) {
		Optional<Expedient> optionalExpedient = expedientRepository.findById(expedientId);
		if (optionalExpedient.isEmpty())
			throw new NoTrobatException(Expedient.class, expedientId);
		Expedient expedient = optionalExpedient.get();
		return expedient;
	}


	// Mètodes per operar amb el la llista d'expedients que estan iniciant-se
	
	/** Llistat amb els expedients que s'estan iniciant. Es diferencia del theadInfo perque 
	 * podria ser que el motor consulti l'expedient des del micro serei del motor.
	 */
	private static List<Expedient> expedientsInici = new ArrayList<Expedient>();
	
	private void expedientsIniciDelete(Expedient expedient) {
		synchronized (expedientsInici) {
			expedientsInici.remove(expedient);
		}
	}
	
	private Expedient expedientsIniciGet(String processInstanceId) {
		Expedient expedient = null;
		synchronized (expedientsInici) {
			for (Expedient e : expedientsInici) {
				if (e.getProcessInstanceId() != null && e.getProcessInstanceId().equals(processInstanceId)) {
					expedient = e;
					break;
				}
			}
		}
		return expedient;
	}

	private void expedientsIniciAdd(Expedient expedient) {
		synchronized (expedientsInici) {
			expedientsInici.add(expedient);
		}
	}

	private static final Logger logger = LoggerFactory.getLogger(ExpedientHelper.class);
}