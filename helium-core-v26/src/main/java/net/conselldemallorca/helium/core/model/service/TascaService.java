/**
 * 
 */
package net.conselldemallorca.helium.core.model.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jbpm.graph.exe.ProcessInstanceExpedient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.codahale.metrics.Counter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import com.codahale.metrics.annotation.Timed;

import net.conselldemallorca.helium.core.common.JbpmVars;
import net.conselldemallorca.helium.core.common.ThreadLocalInfo;
import net.conselldemallorca.helium.core.extern.domini.FilaResultat;
import net.conselldemallorca.helium.core.helper.ExpedientHelper;
import net.conselldemallorca.helium.core.helperv26.MesuresTemporalsHelper;
import net.conselldemallorca.helium.core.model.dao.AlertaDao;
import net.conselldemallorca.helium.core.model.dao.CampDao;
import net.conselldemallorca.helium.core.model.dao.CampTascaDao;
import net.conselldemallorca.helium.core.model.dao.ConsultaCampDao;
import net.conselldemallorca.helium.core.model.dao.DefinicioProcesDao;
import net.conselldemallorca.helium.core.model.dao.EntornDao;
import net.conselldemallorca.helium.core.model.dao.ExpedientDao;
import net.conselldemallorca.helium.core.model.dao.ExpedientTipusDao;
import net.conselldemallorca.helium.core.model.dao.FormulariExternDao;
import net.conselldemallorca.helium.core.model.dao.LuceneDao;
import net.conselldemallorca.helium.core.model.dao.PluginPersonaDao;
import net.conselldemallorca.helium.core.model.dao.RegistreDao;
import net.conselldemallorca.helium.core.model.dao.TascaDao;
import net.conselldemallorca.helium.core.model.dao.TerminiIniciatDao;
import net.conselldemallorca.helium.core.model.dto.PaginaLlistatDto;
import net.conselldemallorca.helium.core.model.dto.PersonaDto;
import net.conselldemallorca.helium.core.model.dto.TascaDto;
import net.conselldemallorca.helium.core.model.dto.TascaLlistatDto;
import net.conselldemallorca.helium.core.model.exception.IllegalStateException;
import net.conselldemallorca.helium.core.model.exception.NotFoundException;
import net.conselldemallorca.helium.core.model.hibernate.Alerta;
import net.conselldemallorca.helium.core.model.hibernate.Camp;
import net.conselldemallorca.helium.core.model.hibernate.Camp.TipusCamp;
import net.conselldemallorca.helium.core.model.hibernate.CampTasca;
import net.conselldemallorca.helium.core.model.hibernate.DefinicioProces;
import net.conselldemallorca.helium.core.model.hibernate.DocumentTasca;
import net.conselldemallorca.helium.core.model.hibernate.Entorn;
import net.conselldemallorca.helium.core.model.hibernate.Expedient;
import net.conselldemallorca.helium.core.model.hibernate.ExpedientLog;
import net.conselldemallorca.helium.core.model.hibernate.ExpedientLog.ExpedientLogAccioTipus;
import net.conselldemallorca.helium.core.model.hibernate.ExpedientTipus;
import net.conselldemallorca.helium.core.model.hibernate.FirmaTasca;
import net.conselldemallorca.helium.core.model.hibernate.FormulariExtern;
import net.conselldemallorca.helium.core.model.hibernate.Tasca;
import net.conselldemallorca.helium.core.model.hibernate.TerminiIniciat;
import net.conselldemallorca.helium.core.security.AclServiceDao;
import net.conselldemallorca.helium.jbpm3.integracio.DelegationInfo;
import net.conselldemallorca.helium.jbpm3.integracio.DominiCodiDescripcio;
import net.conselldemallorca.helium.jbpm3.integracio.JbpmHelper;
import net.conselldemallorca.helium.jbpm3.integracio.JbpmHelper.MostrarTasquesDto;
import net.conselldemallorca.helium.jbpm3.integracio.JbpmProcessInstance;
import net.conselldemallorca.helium.jbpm3.integracio.JbpmTask;
import net.conselldemallorca.helium.jbpm3.integracio.ResultatConsultaPaginadaJbpm;
import net.conselldemallorca.helium.v3.core.api.dto.PaginacioParamsDto;
import net.conselldemallorca.helium.v3.core.api.dto.PaginacioParamsDto.OrdreDireccioDto;


/**
 * Servei per gestionar les tasques assignades a una persona
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Service
public class TascaService {

	public static final String VAR_PREFIX = "H3l1um#";

	public static final String VAR_TASCA_VALIDADA = "H3l1um#tasca.validada";
	public static final String VAR_TASCA_DELEGACIO = "H3l1um#tasca.delegacio";

	public static final String DEFAULT_SECRET_KEY = "H3l1umKy";
	public static final String DEFAULT_ENCRYPTION_SCHEME = "DES/ECB/PKCS5Padding";
	public static final String DEFAULT_KEY_ALGORITHM = "DES";

	private EntornDao entornDao;
	private ExpedientDao expedientDao;
	private ExpedientTipusDao expedientTipusDao;
	private TascaDao tascaDao;
	private DefinicioProcesDao definicioProcesDao;
	private JbpmHelper jbpmDao;
	private AclServiceDao aclServiceDao;
	private DtoConverter dtoConverter;
	private PluginPersonaDao pluginPersonaDao;
	private LuceneDao luceneDao;
	private RegistreDao registreDao;
	private FormulariExternDao formulariExternDao;
	private TerminiIniciatDao terminiIniciatDao;
	private AlertaDao alertaDao;
	private CampDao campDao;
	private CampTascaDao campTascaDao;
	private ConsultaCampDao consultaCampDao;
	private MessageSource messageSource;

	private ServiceUtils serviceUtils;
	private ExpedientHelper expedientHelper;
	private ExpedientLogHelper expedientLogHelper;

	private Map<String, Map<String, Object>> dadesFormulariExternInicial;

	@Autowired
	private MetricRegistry metricRegistry;

	@Resource
	private MesuresTemporalsHelper mesuresTemporalsHelper;

	public TascaDto getById(
			Long entornId,
			String taskId,
			String usuari,
			Map<String, Object> valorsCommand,
			boolean ambVariables,
			boolean ambTexts) throws NotFoundException, IllegalStateException {
		JbpmTask task = comprovarSeguretatTasca(entornId, taskId, usuari, true);
		TascaDto resposta = toTascaDto(task, valorsCommand, ambVariables, ambTexts);
		return resposta;
	}
	public TascaDto getByIdSenseComprovacio(String taskId) {
		return getByIdSenseComprovacio(taskId, null, null);
	}
	public TascaDto getByIdSenseComprovacio(String taskId, Map<String, Object> valorsCommand) {
		return getByIdSenseComprovacio(taskId, null, valorsCommand);
	}
	public TascaDto getByIdSenseComprovacio(String taskId, String usuari) {
		return getByIdSenseComprovacio(taskId, usuari, null);
	}
	public TascaDto getByIdSenseComprovacio(String taskId, String usuari, Map<String, Object> valorsCommand) {
		JbpmTask task = jbpmDao.getTaskById(taskId);
		return toTascaDto(task, valorsCommand, true, true);
	}
	public TascaDto getByIdSenseComprovacioIDades(String taskId) {
		JbpmTask task = jbpmDao.getTaskById(taskId);
		return toTascaDto(task, null, false, false);
	}

	public List<TascaLlistatDto> findTasquesPersonalsIndex(Long entornId) {
		return findTasquesPersonalsTramitacio(entornId, null, null, false);
	}
	public int findCountTasquesPersonalsIndex(Long entornId) {
		return countTasquesPersonalsEntorn(entornId, null);
	}
	public List<TascaLlistatDto> findTasquesPersonalsTramitacio(
			Long entornId,
			String usuari,
			String expedientNumero,
			boolean perTramitacio) {
		mesuresTemporalsHelper.mesuraIniciar("Obtenir tasques personals", "consulta");
		
		String usuariBo = usuari;
		if (usuariBo == null) {
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			usuariBo = auth.getName();
		}
		PaginaLlistatDto resposta = findTasquesFiltre(
				null, //uos per tipus
				entornId,
				usuariBo,
				null,
				null,
				null,
				expedientNumero,
				null,
				null,
				null,
				null,
				null,
				null,
				0,
				-1,
				null,
				true,
				true, 	// incloureTasquesPersona
				false, 	// incloureTasquesGrup
				true, 	// nomesPendents
				false);	// ambPersones
		@SuppressWarnings("unchecked")
		List<TascaLlistatDto> tasques = resposta.getLlistat();
		mesuresTemporalsHelper.mesuraCalcular("Obtenir tasques personals", "consulta");
		return tasques;
		/*String usuariBo = usuari;
		if (usuariBo == null) {
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			usuariBo = auth.getName();
		}
		List<JbpmTask> tasques = null;
		if (codiExpedient == null) {
			tasques = jbpmDao.findPersonalTasks(usuariBo);
		} else {
			List<Long> ids = expedientDao.findPIExpedientsAmbEntornTipusINum(entornId, codiExpedient);
			LlistatIds llistatIds = jbpmDao.findListIdsPersonalTasks(usuari,ids);
			tasques = jbpmDao.findPersonalTasks(llistatIds.getIds(), usuari);
		}
		List<TascaLlistatDto> list = tasquesFiltradesPerEntorn(entornId, tasques, perTramitacio);
		mesuresTemporalsHelper.mesuraCalcular("Obtenir tasques personals", "consulta");
		return list;*/
	}

	@Timed
	public int countTasquesPersonalsEntorn(
			Long entornId,
			String usuari) {
		mesuresTemporalsHelper.mesuraIniciar(
				"Recompte tasques personals",
				"consulta");
		String usuariBo = usuari;
		if (usuariBo == null) {
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			usuariBo = auth.getName();
		}
		PaginacioParamsDto paginacioParams = new PaginacioParamsDto();
		paginacioParams.setPaginaNum(0);
		paginacioParams.setPaginaTamany(-1);
		ResultatConsultaPaginadaJbpm<JbpmTask> tasks = jbpmDao.tascaFindByFiltrePaginat(
				//null, //unitatsOrganitzativesAmbPermisos,
				entornId,
				usuariBo,
				null,
				null,
				null,
				null,
				null,
				null,
				null,
				null,
				null,
				null,
				null,
				true, // tasquesPersona
				false, // tasquesGrup
				true, // nomesPendents
				paginacioParams,
				true);
		/*List<Long> idsExpedients = getExpedientIdsPerConsultaTasques(
				entornId, 
				usuariBo, null, null, null, null, false);
		LlistatIds lista = jbpmDao.findListIdsPersonalTasks(
				usuariBo,
				idsExpedients);
		count = lista.getCount();*/
		mesuresTemporalsHelper.mesuraCalcular(
				"Recompte tasques personals",
				"consulta");
		return tasks.getCount();
	}
	@Timed
	public int countTasquesPersonalsFiltre(
			Long entornId,
			String usuari,
			String titol,
			String expedientTitol,
			String expedientNumero,
			Long expedientTipusId,
			Date dataCreacioInici,
			Date dataCreacioFi,
			Integer prioritat,
			Date dataLimitInici,
			Date dataLimitFi,
			int firstRow,
			int maxResults,
			String sort,
			boolean asc) {
		mesuresTemporalsHelper.mesuraIniciar("CONSULTA TASQUES COUNT PERSONA", "consulta");
		String usuariBo = usuari;
		if (usuariBo == null) {
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			usuariBo = auth.getName();
		}
		PaginacioParamsDto paginacioParams = new PaginacioParamsDto();
		paginacioParams.setPaginaNum(0);
		paginacioParams.setPaginaTamany(-1);
		ResultatConsultaPaginadaJbpm<JbpmTask> tasks = jbpmDao.tascaFindByFiltrePaginat(
				//null, //unitatsOrganitzativesAmbPermisos,
				entornId,
				usuariBo,
				null,
				titol,
				null,
				expedientTitol,
				expedientNumero,
				expedientTipusId,
				dataCreacioInici,
				dataCreacioFi,
				prioritat,
				dataLimitInici,
				dataLimitFi,
				true, // tasquesPersona
				false, // tasquesGrup
				true, // nomesPendents
				paginacioParams,
				true);
		/*List<Long> idsExpedients = getExpedientIdsPerConsultaTasques(
				entornId, 
				usuariBo,
				expedientTitol, 
				expedientNumero,
				expedientTipusId,
				sort,
				asc);
		LlistatIds ids = jbpmDao.findListPersonalTasks(
				usuariBo, 
				titol, 
				idsExpedients, 
				dataCreacioInici, 
				dataCreacioFi, 
				prioritat, 
				dataLimitInici, 
				dataLimitFi, firstRow, maxResults, sort, asc);*/
		mesuresTemporalsHelper.mesuraCalcular("CONSULTA TASQUES COUNT PERSONA", "consulta");
		return tasks.getCount();
	}
	@Timed
	public PaginaLlistatDto findTasquesPersonalsFiltre(
			Long entornId,
			String usuari,
			String titol,
			String expedientTitol,
			String expedientNumero,
			Long expedientTipusId,
			Date dataCreacioInici,
			Date dataCreacioFi,
			Integer prioritat,
			Date dataLimitInici,
			Date dataLimitFi,
			int firstResult,
			int maxResults,
			String sort,
			boolean asc) {
		mesuresTemporalsHelper.mesuraIniciar("CONSULTA TASQUES PERSONA", "consulta");
		
		String usuariBo = usuari;
		if (usuariBo == null) {
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			usuariBo = auth.getName();
		}
		
		PaginaLlistatDto resposta = findTasquesFiltre(
				null, //unitatsOrganitzativesAmbPermisos,
				entornId,
				usuariBo,
				null,
				titol,
				expedientTitol,
				expedientNumero,
				expedientTipusId,
				dataCreacioInici,
				dataCreacioFi,
				prioritat,
				dataLimitInici,
				dataLimitFi,
				firstResult,
				maxResults,
				sort,
				asc,
				true, 	// incloureTasquesPersona
				false, 	// incloureTasquesGrup
				true, 	// nomesPendents
				false);	// ambPersones
			/*
			final Timer timerExpedientIds = metricRegistry.timer(
					MetricRegistry.name(
							TascaService.class,
							"findTasquesPersonalsFiltre",
							entornId.toString(),
							"1_expedientIds"));
			final Timer timerTascaIds = metricRegistry.timer(
					MetricRegistry.name(
							TascaService.class,
							"findTasquesPersonalsFiltre",
							entornId.toString(),
							"2_tascaIds"));
			final Timer timerTasques = metricRegistry.timer(
					MetricRegistry.name(
							TascaService.class,
							"findTasquesPersonalsFiltre",
							entornId.toString(),
							"3_tasques"));
			final Timer timerResposta = metricRegistry.timer(
					MetricRegistry.name(
							TascaService.class,
							"findTasquesPersonalsFiltre",
							entornId.toString(),
							"4_resposta"));
			final Timer.Context contextExpedientIds = timerExpedientIds.time();
			List<Long> expedientIds;
			try {
				// Consulta els expedients de l'entorn que compleixen el filtre
				expedientIds = expedientDao.findListExpedients(
						entornId,
						null,
						usuariBo,
						expedient, 
						numeroExpedient,
						tipusExpedient,
						sort,
						asc);
			} finally {
				contextExpedientIds.stop();
			}
			final Timer.Context contextTascaIds = timerTascaIds.time();
			LlistatIds tascaIds;
			try {
				// Consulta els expedients de l'entorn que compleixen el filtre
				tascaIds = jbpmDao.findListPersonalTasks(
						usuariBo,
						tasca,
						expedientIds,
						dataCreacioInici,
						dataCreacioFi,
						prioritat,
						dataLimitInici,
						dataLimitFi, firstRow, maxResults, sort, asc);
			} finally {
				contextTascaIds.stop();
			}
			final Timer.Context contextTasques = timerTasques.time();
			List<JbpmTask> tasques;
			try {
				tasques = jbpmDao.findPersonalTasks(
						tascaIds.getIds(),
						usuariBo);
			} finally {
				contextTasques.stop();
			}
			final Timer.Context contextResposta = timerResposta.time();
			try {
				resposta = tasquesLlistatFiltradesValors(
						entornId,
						tasques, 
						tasca,
						expedient,
						tipusExpedient,
						dataCreacioInici,
						dataCreacioFi,
						prioritat,
						dataLimitInici,
						dataLimitFi,
						0,
						maxResults,
						sort,
						asc);
				resposta.setCount(tascaIds.getCount());
			} finally {
				contextResposta.stop();
			}*/
		mesuresTemporalsHelper.mesuraCalcular("CONSULTA TASQUES PERSONA", "consulta");
		return resposta;
	}

	@Timed
	public int countTasquesGrupEntorn(
			Long entornId,
			String usuari) {
		mesuresTemporalsHelper.mesuraIniciar("Recompte tasques grup", "consulta");
		//int count = 0;
		String usuariBo = usuari;
		if (usuariBo == null) {
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			usuariBo = auth.getName();
		}
		PaginacioParamsDto paginacioParams = new PaginacioParamsDto();
		paginacioParams.setPaginaNum(0);
		paginacioParams.setPaginaTamany(-1);
		ResultatConsultaPaginadaJbpm<JbpmTask> tasks = jbpmDao.tascaFindByFiltrePaginat(
				//null, //List<String> unitatsOrganitzativesAmbPermisos,
				entornId,
				usuariBo,
				null,
				null,
				null,
				null,
				null,
				null,
				null,
				null,
				null,
				null,
				null,
				false, // tasquesPersona
				true, // tasquesGrup
				true, // nomesPendents
				paginacioParams,
				true);
		/*List<Long> idsExpedients = getExpedientIdsPerConsultaTasques(
				entornId, 
				usuariBo, null, null, null, null, false);*/
		/*List<Long> idsExpedients = expedientDao.findListExpedients(
				entornId, 
				usuariBo, null, null, null, null, false);*/
		/*LlistatIds lista = jbpmDao.findListIdsGroupTasks(usuariBo, idsExpedients);
		count = lista.getCount();*/
		mesuresTemporalsHelper.mesuraCalcular("Recompte tasques grup", "consulta");
		return tasks.getCount();
	}
	@Timed
	public int countTasquesGrupFiltre(
			Long entornId,
			String usuari,
			String titol,
			String expedientTitol,
			String expedientNumero,
			Long expedientTipusId,
			Date dataCreacioInici,
			Date dataCreacioFi,
			Integer prioritat,
			Date dataLimitInici,
			Date dataLimitFi,
			int firstRow,
			int maxResults,
			String sort,
			boolean asc) {
		mesuresTemporalsHelper.mesuraIniciar("CONSULTA TASQUES GRUP COUNT", "consulta");
		String usuariBo = usuari;
		if (usuariBo == null) {
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			usuariBo = auth.getName();
		}
		PaginacioParamsDto paginacioParams = new PaginacioParamsDto();
		paginacioParams.setPaginaNum(0);
		paginacioParams.setPaginaTamany(-1);
		ResultatConsultaPaginadaJbpm<JbpmTask> tasks = jbpmDao.tascaFindByFiltrePaginat(
				//null, //List<String> unitatsOrganitzativesAmbPermisos,
				entornId,
				usuariBo,
				null,
				titol,
				null,
				expedientTitol,
				expedientNumero,
				expedientTipusId,
				dataCreacioInici,
				dataCreacioFi,
				prioritat,
				dataLimitInici,
				dataLimitFi,
				false, // tasquesPersona
				true, // tasquesGrup
				true, // nomesPendents
				paginacioParams,
				true);
		/*List<Long> idsExpedients = getExpedientIdsPerConsultaTasques(
				entornId, 
				usuariBo,
				expedient, 
				numeroExpedient,
				tipusExpedient,
				sort,
				asc);*/
		/*List<Long> idsExpedients = expedientDao.findListExpedients(
				entornId, 
				usuariBo,
				expedient, 
				numeroExpedient,
				tipusExpedient,
				sort,
				asc);*/
		/*int count = jbpmDao.findListGroupTasks(
				usuariBo, 
				tasca, 
				idsExpedients, 
				dataCreacioInici, 
				dataCreacioFi, 
				prioritat, 
				dataLimitInici, 
				dataLimitFi, firstRow, maxResults, sort, asc).getCount();*/
		mesuresTemporalsHelper.mesuraCalcular("CONSULTA TASQUES GRUP COUNT", "consulta");
		return tasks.getCount();
	}

	@Timed
	public PaginaLlistatDto findTasquesGrupFiltre(
			Long entornId,
			String usuari,
			String titol,
			String expedientTitol,
			String expedientNumero,
			Long expedientTipusId,
			Date dataCreacioInici,
			Date dataCreacioFi,
			Integer prioritat,
			Date dataLimitInici,
			Date dataLimitFi,
			int firstResult,
			int maxResults,
			String sort,
			boolean asc) {
		mesuresTemporalsHelper.mesuraIniciar("CONSULTA TASQUES GRUP", "consulta");
		
		String usuariBo = usuari;
		if (usuariBo == null) {
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			usuariBo = auth.getName();
		}
		
		PaginaLlistatDto resposta = findTasquesFiltre(
				null, //List<String> unitatsOrganitzativesAmbPermisos,
				entornId,
				usuariBo,
				null,
				titol,
				expedientTitol,
				expedientNumero,
				expedientTipusId,
				dataCreacioInici,
				dataCreacioFi,
				prioritat,
				dataLimitInici,
				dataLimitFi,
				firstResult,
				maxResults,
				sort,
				asc,
				false, 	// incloureTasquesPersona
				true, 	// incloureTasquesGrup
				true, 	// nomesPendents
				false);	// ambPersones
		/*PaginaLlistatDto resposta;
		final Timer timerTotal = metricRegistry.timer(
				MetricRegistry.name(
						TascaService.class,
						"findTasquesGrupFiltre",
						entornId.toString()));
		final Timer.Context contextTotal = timerTotal.time();
		try {
			mesuresTemporalsHelper.mesuraIniciar("CONSULTA TASQUES GRUP", "consulta");
			String usuariBo = usuari;
			if (usuariBo == null) {
				Authentication auth = SecurityContextHolder.getContext().getAuthentication();
				usuariBo = auth.getName();
			}
			final Timer timerExpedientIds = metricRegistry.timer(
					MetricRegistry.name(
							TascaService.class,
							"findTasquesGrupFiltre",
							entornId.toString(),
							"1_expedientIds"));
			final Timer timerTascaIds = metricRegistry.timer(
					MetricRegistry.name(
							TascaService.class,
							"findTasquesGrupFiltre",
							entornId.toString(),
							"2_tascaIds"));
			final Timer timerTasques = metricRegistry.timer(
					MetricRegistry.name(
							TascaService.class,
							"findTasquesGrupFiltre",
							entornId.toString(),
							"3_tasques"));
			final Timer timerResposta = metricRegistry.timer(
					MetricRegistry.name(
							TascaService.class,
							"findTasquesGrupFiltre",
							entornId.toString(),
							"4_resposta"));
			final Timer.Context contextExpedientIds = timerExpedientIds.time();
			List<Long> expedientIds;
			try {
				expedientIds = getExpedientIdsPerConsultaTasques(
						entornId,
						usuariBo,
						expedient, 
						numeroExpedient,
						tipusExpedient,
						sort,
						asc);
			} finally {
				contextExpedientIds.stop();
			}
			final Timer.Context contextTascaIds = timerTascaIds.time();
			LlistatIds tascaIds;
			try {
				tascaIds = jbpmDao.findListGroupTasks(
						usuariBo, 
						tasca, 
						expedientIds, 
						dataCreacioInici, 
						dataCreacioFi, 
						prioritat, 
						dataLimitInici, 
						dataLimitFi, firstRow, maxResults, sort, asc);
			} finally {
				contextTascaIds.stop();
			}
			final Timer.Context contextTasques = timerTasques.time();
			List<JbpmTask> tasques;
			try {
				tasques = jbpmDao.findGroupTasks(
						tascaIds.getIds(),
						usuariBo);
			} finally {
				contextTasques.stop();
			}
			final Timer.Context contextResposta = timerResposta.time();
			try {
				resposta = tasquesLlistatFiltradesValors(
						entornId,
						tasques, 
						tasca,
						expedient,
						tipusExpedient,
						dataCreacioInici,
						dataCreacioFi,
						prioritat,
						dataLimitInici,
						dataLimitFi,
						0,
						maxResults,
						sort,
						asc);	
				resposta.setCount(tascaIds.getCount());
			} finally {
				contextResposta.stop();
			}
			mesuresTemporalsHelper.mesuraCalcular("CONSULTA TASQUES GRUP", "consulta");*/
		mesuresTemporalsHelper.mesuraCalcular("CONSULTA TASQUES GRUP", "consulta");
		return resposta;
	}

	@Timed
	public PaginaLlistatDto findTasquesConsultaFiltre(
			Long entornId,
			Long expedientTipusId,
			String responsable,
			String taskName,
			String titol,
			Date dataCreacioInici,
			Date dataCreacioFi,
			MostrarTasquesDto mostrarTasques, 
			int firstResult,
			int maxResults,
			String sort,
			boolean asc) {
		mesuresTemporalsHelper.mesuraIniciar("CONSULTA TASQUES LLISTAT", "consulta");
		boolean incloureTasquesPersona = (MostrarTasquesDto.MOSTRAR_TASQUES_NOMES_PERSONALS.equals(mostrarTasques) || MostrarTasquesDto.MOSTRAR_TASQUES_TOTS.equals(mostrarTasques));
		boolean incloureTasquesGrup = (MostrarTasquesDto.MOSTRAR_TASQUES_NOMES_GROUPS.equals(mostrarTasques) || MostrarTasquesDto.MOSTRAR_TASQUES_TOTS.equals(mostrarTasques));
		PaginaLlistatDto resposta = findTasquesFiltre(
				null, //List<String> unitatsOrganitzativesAmbPermisos,
				entornId,
				(responsable != null && !responsable.isEmpty()) ? responsable : null,
				(taskName != null && !taskName.isEmpty()) ? taskName : null,
				(titol != null && !titol.isEmpty()) ? titol : null,
				null,
				null,
				expedientTipusId,
				dataCreacioInici,
				dataCreacioFi,
				null,
				null,
				null,
				firstResult,
				maxResults,
				sort,
				asc,
				incloureTasquesPersona,
				incloureTasquesGrup,
				true, 	// nomesPendents
				true);	// ambPersones
		/*List<Long> idsExpedients = getExpedientIdsPerConsultaTasques(
				entornId,
				responsable,
				titol, 
				null,
				expedientTipusId,
				sort,
				asc);
		LlistatIds ids = jbpmDao.findListTasks(
				responsable, 
				jbpmName, 
				null,
				idsExpedients, 
				dataCreacioInici, 
				dataCreacioFi, 
				null, 
				null, 
				null, 
				firstRow, 
				maxResults, 
				sort, 
				asc,
				mostrarTasques);
		List<JbpmTask> tasques = jbpmDao.findGroupTasks(ids.getIds(), responsable);
		PaginaLlistatDto resposta = tasquesLlistatFiltradesValors(
				entornId,
				tasques, 
				null,
				null,
				tipusExpedient,
				dataCreacioInici,
				dataCreacioFi,
				null,
				null,
				null,
				0,
				maxResults,
				sort,
				asc,
				true);	
		resposta.setCount(ids.getCount());*/
		mesuresTemporalsHelper.mesuraCalcular("CONSULTA TASQUES LLISTAT", "consulta");
		return resposta;
	}
	
	public List<TascaLlistatDto> findTasquesPerTramitacioMassiva(
			Long entornId,
			String usuari,
			String taskId) throws NotFoundException, IllegalStateException {
		String usuariBo = usuari;
		if (usuariBo == null) {
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			usuariBo = auth.getName();
		}
		List<JbpmTask> tasques = jbpmDao.findPersonalTasks(usuariBo);
		JbpmTask task = comprovarSeguretatTasca(entornId, taskId, usuari, true);
		TascaDto tasca = toTascaDto(task, null, true, true);
		String codi = task.getTaskName();
		String jbpmKey = tasca.getDefinicioProces().getJbpmKey();
		List<TascaLlistatDto> resposta = tasquesPerTramitacioMasiva(
				entornId,
				tasques, 
				codi,
				jbpmKey);
		return resposta;
	}

	public List<TascaLlistatDto> findTasquesGrupIndex(Long entornId) {
		return findTasquesGrupTramitacio(entornId, null, null, false);
	}
	
	public int findCountTasquesGrupIndex(Long entornId) {
		return countTasquesGrupEntorn(entornId, null);
	}
	
	public boolean isTasquesGrupTramitacio(Long entornId, String tascaId, String usuari) {
		boolean res = false;
		mesuresTemporalsHelper.mesuraIniciar("is tasques grup", "consulta");
		String usuariBo = usuari;
		if (usuariBo == null) {
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			usuariBo = auth.getName();
		}
		try {
			PaginacioParamsDto paginacioParams = new PaginacioParamsDto();
			paginacioParams.setPaginaNum(0);
			paginacioParams.setPaginaTamany(-1);
			ResultatConsultaPaginadaJbpm<JbpmTask> tasks = jbpmDao.tascaFindByFiltrePaginat(
					//null, //List<String> unitatsOrganitzativesAmbPermisos,
					entornId,
					usuariBo,
					null,
					null,
					null,
					null,
					null,
					null,
					null,
					null,
					null,
					null,
					null,
					false, // tasquesPersona
					true, // tasquesGrup
					true, // nomesPendents
					paginacioParams);
			for (JbpmTask task: tasks.getLlista()) {
				if (task.getId().equals(tascaId)) {
					res = true;
					break;
				}
			}
		} catch (NumberFormatException ignored) {}
		/*if (usuari == null) {
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			usuari = auth.getName();
		}
		List<Long> idsExpedients = getExpedientIdsPerConsultaTasques(
				entornId,
				usuari, null, null, null, null, false);
		LlistatIds llistatIds = jbpmDao.findListIdsGroupTasks(usuari, idsExpedients);
		if (llistatIds.getIds().contains(Long.valueOf(tascaId))) {
			res = true;
		} else {
			List<Long> lista = jbpmDao.findIdsRootProcessInstanceGroupTasks(llistatIds.getIds(), usuari);
			for (Long pid : lista) {
				Expedient expedient = expedientDao.findAmbProcessInstanceId(String.valueOf(pid));
				if (expedient != null && idsExpedients.contains(expedient.getId())) {
					res = true;
					break;
				}
			}
		}*/
		mesuresTemporalsHelper.mesuraCalcular("is tasques grup", "consulta");
		return res;
	}
	
	public List<TascaLlistatDto> findTasquesGrupTramitacio(
			Long entornId,
			String usuari,
			String expedientNumero,
			boolean perTramitacio) {
		mesuresTemporalsHelper.mesuraIniciar("Obtenir tasques grup", "consulta");
		
		String usuariBo = usuari;
		if (usuariBo == null) {
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			usuariBo = auth.getName();
		}
		
		PaginaLlistatDto resposta = findTasquesFiltre(
				null,// unitatsOrganitzativesAmbPermisos,
				entornId,
				usuariBo,
				null,
				null,
				null,
				expedientNumero,
				null,
				null,
				null,
				null,
				null,
				null,
				0,
				-1,
				null,
				true,
				false, 	// incloureTasquesPersona
				true, 	// incloureTasquesGrup
				true, 	// nomesPendents
				false);	// ambPersones
		@SuppressWarnings("unchecked")
		List<TascaLlistatDto> tasques = resposta.getLlistat();
		mesuresTemporalsHelper.mesuraCalcular("Obtenir tasques grup", "consulta");
		return tasques;
		/*String usuariBo = usuari;
		if (usuariBo == null) {
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			usuariBo = auth.getName();
		}
		List<JbpmTask> tasques = new ArrayList<JbpmTask>();
		if (codiExpedient == null) {
			tasques = jbpmDao.findGroupTasks(usuariBo);
		} else {
			List<Long> ids = expedientDao.findPIExpedientsAmbEntornTipusINum(entornId, codiExpedient);
			LlistatIds llistatIds = jbpmDao.findListIdsGroupTasks(usuari,ids);
			tasques = jbpmDao.findGroupTasks(llistatIds.getIds(), usuari);
		}
		List<TascaLlistatDto> list = tasquesFiltradesPerEntorn(entornId, tasques, perTramitacio);
		mesuresTemporalsHelper.mesuraCalcular("Obtenir tasques grup", "consulta");
		return list;*/
	}

	public List<TascaLlistatDto> findTasquesAmbId(Long entornId, List<Long> ids) {
		List<JbpmTask> tasques = jbpmDao.findTasks(ids);
		List<TascaLlistatDto> resposta = new ArrayList<TascaLlistatDto>();
		for (JbpmTask task: tasques) {
			DadesCacheTasca dadesCacheTasca = getDadesCacheTasca(task);
			Long currentEntornId = dadesCacheTasca.getEntornId();
			if (currentEntornId != null && entornId.equals(currentEntornId)) {
				TascaLlistatDto dto = toTascaLlistatDto(task, false);
				resposta.add(dto);
			}
		}
		return resposta;
	}
	
	public TascaDto agafar(Long entornId, String taskId) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		return agafar(entornId, auth.getName(), taskId);
	}
	public TascaDto agafar(Long entornId, String usuari, String taskId)  throws NotFoundException, IllegalStateException{
		JbpmTask task = comprovarSeguretatTasca(entornId, taskId, null, false);
		String previousActors = expedientLogHelper.getActorsPerReassignacioTasca(taskId);
		ExpedientLog expedientLog = expedientLogHelper.afegirLogExpedientPerTasca(
				taskId,
				ExpedientLogAccioTipus.TASCA_REASSIGNAR,
				previousActors);
		jbpmDao.takeTaskInstance(taskId, usuari);
		getServiceUtils().expedientIndexLuceneUpdate(task.getProcessInstanceId());
		String currentActors = expedientLogHelper.getActorsPerReassignacioTasca(taskId);
		expedientLog.setAccioParams(previousActors + "::" + currentActors);
		TascaDto tasca = toTascaDto(task, null, true, true);
		registreDao.crearRegistreIniciarTasca(
				tasca.getExpedient().getId(),
				taskId,
				SecurityContextHolder.getContext().getAuthentication().getName(),
				"Agafar tasca \"" + tasca.getNom() + "\"");
		return tasca;
	}

	public TascaDto alliberar(
			Long entornId,
			String taskId,
			boolean comprovarResponsable) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		return alliberar(entornId, auth.getName(), taskId, comprovarResponsable);
	}
	public TascaDto alliberar(
			Long entornId,
			String usuari,
			String taskId,
			boolean comprovarResponsable)  throws NotFoundException, IllegalStateException{
		JbpmTask task = comprovarSeguretatTasca(entornId, taskId, null, false);
		if (comprovarResponsable) {
			if (!task.getAssignee().equals(usuari)) {
				throw new NotFoundException(
						getServiceUtils().getMessage("error.tascaService.noAssignada"));
			}
		}
		String previousActors = expedientLogHelper.getActorsPerReassignacioTasca(taskId);
		ExpedientLog expedientLog = expedientLogHelper.afegirLogExpedientPerTasca(
				taskId,
				ExpedientLogAccioTipus.TASCA_REASSIGNAR,
				previousActors);
		jbpmDao.releaseTaskInstance(taskId);
		getServiceUtils().expedientIndexLuceneUpdate(task.getProcessInstanceId());
		String currentActors = expedientLogHelper.getActorsPerReassignacioTasca(taskId);
		expedientLog.setAccioParams(previousActors + "::" + currentActors);
		TascaDto tasca = toTascaDto(task, null, true, true);
		registreDao.crearRegistreIniciarTasca(
				tasca.getExpedient().getId(),
				taskId,
				SecurityContextHolder.getContext().getAuthentication().getName(),
				"Amollar tasca \"" + tasca.getNom() + "\"");
		return tasca;
	}

	public TascaDto guardarVariable(
			Long entornId,
			String taskId,
			String variable,
			Object valor) {
		return guardarVariable(entornId, taskId, variable, valor, null);
	}
	public TascaDto guardarVariable(
			Long entornId,
			String taskId,
			String variable,
			Object valor,
			String usuari) {
		Map<String, Object> variables = new HashMap<String, Object>();
		variables.put(variable, valor);
		return guardarVariables(entornId, taskId, variables, usuari);
	}
	public TascaDto guardarVariables(
			Long entornId,
			String taskId,
			Map<String, Object> variables,
			String usuari)  throws NotFoundException, IllegalStateException{
		logger.debug("Guardant les dades del formulari de la tasca (" +
				"taskId=" + taskId + ", " +
				"variables= "+variables+")");
		JbpmTask task = comprovarSeguretatTasca(entornId, taskId, usuari, true);
		Expedient expedient = null;
		if (MesuresTemporalsHelper.isActiu()) { 
			JbpmProcessInstance pi = jbpmDao.getRootProcessInstance(task.getProcessInstanceId());
			expedient = expedientDao.findAmbProcessInstanceId(pi.getId());
			mesuresTemporalsHelper.mesuraIniciar("Guardar tasca", "tasques", expedient.getTipus().getNom(), task.getTaskName());
			mesuresTemporalsHelper.mesuraIniciar("Guardar tasca", "tasques", expedient.getTipus().getNom(), task.getTaskName(), "LOG");
		}
		expedientLogHelper.afegirLogExpedientPerTasca(
				taskId,
				ExpedientLogAccioTipus.TASCA_FORM_GUARDAR,
				null,
				usuari);
		if (MesuresTemporalsHelper.isActiu()) {
			mesuresTemporalsHelper.mesuraCalcular("Guardar tasca", "tasques", expedient.getTipus().getNom(), task.getTaskName(), "LOG");
			mesuresTemporalsHelper.mesuraIniciar("Guardar tasca", "tasques", expedient.getTipus().getNom(), task.getTaskName(), "Optimitzar DOMINI");
		}
		boolean iniciada = task.getStartTime() == null;
		optimitzarConsultesDomini(task, variables);
		if (MesuresTemporalsHelper.isActiu()) {
			mesuresTemporalsHelper.mesuraCalcular("Guardar tasca", "tasques", expedient.getTipus().getNom(), task.getTaskName(), "Optimitzar DOMINI");
			mesuresTemporalsHelper.mesuraIniciar("Guardar tasca", "tasques", expedient.getTipus().getNom(), task.getTaskName(), "Start TaskInstance");
		}
		jbpmDao.startTaskInstance(taskId);
		if (MesuresTemporalsHelper.isActiu()) {
			mesuresTemporalsHelper.mesuraCalcular("Guardar tasca", "tasques", expedient.getTipus().getNom(), task.getTaskName(), "Start TaskInstance");
			mesuresTemporalsHelper.mesuraIniciar("Guardar tasca", "tasques", expedient.getTipus().getNom(), task.getTaskName(), "taskInstance Variables");
		}
		logger.debug("Guardant les dades v26 (filtreVariables= "+variables+")");
		jbpmDao.setTaskInstanceVariables(taskId, variables, false);
		if (MesuresTemporalsHelper.isActiu()) {
			mesuresTemporalsHelper.mesuraCalcular("Guardar tasca", "tasques", expedient.getTipus().getNom(), task.getTaskName(), "taskInstance Variables");
		}
		TascaDto tasca = toTascaDto(task, null, true, true);
		if (iniciada) {
			if (usuari == null)
				usuari = SecurityContextHolder.getContext().getAuthentication().getName();
			if (MesuresTemporalsHelper.isActiu())
				mesuresTemporalsHelper.mesuraIniciar("Guardar tasca", "tasques", expedient.getTipus().getNom(), task.getTaskName(), "REG");
			registreDao.crearRegistreModificarTasca(
					tasca.getExpedient().getId(),
					taskId,
					usuari,
					"Iniciar tasca \"" + tasca.getNom() + "\"");
			if (MesuresTemporalsHelper.isActiu())
				mesuresTemporalsHelper.mesuraCalcular("Guardar tasca", "tasques", expedient.getTipus().getNom(), task.getTaskName(), "REG");
		}
		if (MesuresTemporalsHelper.isActiu())
			mesuresTemporalsHelper.mesuraCalcular("Guardar tasca", "tasques", expedient.getTipus().getNom(), task.getTaskName());
		return tasca;
	}

	public void guardarRegistre(
			Long entornId,
			String taskId,
			String campCodi,
			Object[] valors) {
		guardarRegistre(
				entornId,
				taskId,
				campCodi,
				valors,
				-1,
				null);
	}
	public void guardarRegistre(
			Long entornId,
			String taskId,
			String campCodi,
			Object[] valors,
			int index) {
		guardarRegistre(
				entornId,
				taskId,
				campCodi,
				valors,
				index,
				null);
	}
	public void guardarRegistre(
			Long entornId,
			String taskId,
			String campCodi,
			Object[] valors,
			String usuari) {
		guardarRegistre(
				entornId,
				taskId,
				campCodi,
				valors,
				-1,
				usuari);
	}
	public void guardarRegistre(
			Long entornId,
			String taskId,
			String campCodi,
			Object[] valors,
			int index,
			String usuari) {
		JbpmTask task = jbpmDao.getTaskById(taskId);
		Expedient expedient = null;
		if (MesuresTemporalsHelper.isActiu()) {
			JbpmProcessInstance pi = jbpmDao.getRootProcessInstance(task.getProcessInstanceId());
			expedient = expedientDao.findAmbProcessInstanceId(pi.getId());
			mesuresTemporalsHelper.mesuraIniciar("Guardar registre", "tasques", expedient.getTipus().getNom(), task.getTaskName());
		}
		DefinicioProces definicioProces = definicioProcesDao.findAmbJbpmId(task.getProcessDefinitionId());
		if (MesuresTemporalsHelper.isActiu())
			mesuresTemporalsHelper.mesuraIniciar("Guardar registre", "tasques", expedient.getTipus().getNom(), task.getTaskName(), "Camp");
		Camp camp = campDao.findAmbDefinicioProcesICodi(definicioProces.getId(), campCodi);
		if (MesuresTemporalsHelper.isActiu()) {
			mesuresTemporalsHelper.mesuraCalcular("Guardar registre", "tasques", expedient.getTipus().getNom(), task.getTaskName(), "Camp");
			mesuresTemporalsHelper.mesuraIniciar("Guardar registre", "tasques", expedient.getTipus().getNom(), task.getTaskName(), "Guardar variable");
		}
		if (camp.isMultiple()) {
			Object valor = jbpmDao.getTaskInstanceVariable(taskId, campCodi);
			if (valor == null) {
				guardarVariable(
						entornId,
						taskId,
						campCodi,
						new Object[]{valors},
						usuari);
			} else {
				Object[] valorMultiple = (Object[])valor;
				if (index != -1) {
					valorMultiple[index] = valors;
					guardarVariable(
							entornId,
							taskId,
							campCodi,
							valor,
							usuari);
				} else {
					Object[] valorNou = new Object[valorMultiple.length + 1];
					for (int i = 0; i < valorMultiple.length; i++)
						valorNou[i] = valorMultiple[i];
					valorNou[valorMultiple.length] = valors;
					guardarVariable(
							entornId,
							taskId,
							campCodi,
							valorNou,
							usuari);
				}
			}
		} else {
			guardarVariable(
					entornId,
					taskId,
					campCodi,
					valors,
					usuari);
		}
		if (MesuresTemporalsHelper.isActiu()) {
			mesuresTemporalsHelper.mesuraCalcular("Guardar registre", "tasques", expedient.getTipus().getNom(), task.getTaskName(), "Guardar variable");
			mesuresTemporalsHelper.mesuraCalcular("Guardar registre", "tasques", expedient.getTipus().getNom(), task.getTaskName());
		}
	}
	public void esborrarRegistre(
			Long entornId,
			String taskId,
			String campCodi,
			int index) {
		esborrarRegistre(entornId, taskId, campCodi, index, null);
	}
	public void esborrarRegistre(
			Long entornId,
			String taskId,
			String campCodi,
			int index,
			String usuari) {
		JbpmTask task = jbpmDao.getTaskById(taskId);
		Expedient expedient = null;
		if (MesuresTemporalsHelper.isActiu()) {
			JbpmProcessInstance pi = jbpmDao.getRootProcessInstance(task.getProcessInstanceId());
			expedient = expedientDao.findAmbProcessInstanceId(pi.getId());
			mesuresTemporalsHelper.mesuraIniciar("Esborrar registre", "tasques", expedient.getTipus().getNom(), task.getTaskName());
		}
		DefinicioProces definicioProces = definicioProcesDao.findAmbJbpmId(task.getProcessDefinitionId());
		if (MesuresTemporalsHelper.isActiu())
			mesuresTemporalsHelper.mesuraIniciar("Esborrar registre", "tasques", expedient.getTipus().getNom(), task.getTaskName(), "Camp");
		Camp camp = campDao.findAmbDefinicioProcesICodi(definicioProces.getId(), campCodi);
		if (MesuresTemporalsHelper.isActiu()) {
			mesuresTemporalsHelper.mesuraCalcular("Esborrar registre", "tasques", expedient.getTipus().getNom(), task.getTaskName(), "Camp");
			mesuresTemporalsHelper.mesuraIniciar("Esborrar registre", "tasques", expedient.getTipus().getNom(), task.getTaskName(), "Guardar Variable");
		}
		if (camp.isMultiple()) {
			Object valor = jbpmDao.getTaskInstanceVariable(taskId, campCodi);
			if (valor != null) {
				Object[] valorMultiple = (Object[])valor;
				if (valorMultiple.length > 0) {
					Object[] valorNou = new Object[valorMultiple.length - 1];
					for (int i = 0; i < valorNou.length; i++)
						valorNou[i] = (i < index) ? valorMultiple[i] : valorMultiple[i + 1];
					guardarVariable(
							entornId,
							taskId,
							campCodi,
							valorNou,
							usuari);
				}
			}
		} else {
			guardarVariable(
					entornId,
					taskId,
					campCodi,
					null,
					usuari);
		}
		if (MesuresTemporalsHelper.isActiu()) {
			mesuresTemporalsHelper.mesuraCalcular("Esborrar registre", "tasques", expedient.getTipus().getNom(), task.getTaskName(), "Guardar Variable");
			mesuresTemporalsHelper.mesuraCalcular("Esborrar registre", "tasques", expedient.getTipus().getNom(), task.getTaskName());
		}
	}

	public TascaDto validar(
			Long entornId,
			String taskId,
			Map<String, Object> variables,
			boolean comprovarAssignacio) {
		return validar(entornId, taskId, variables, comprovarAssignacio, null);
	}
	public TascaDto validar(
			Long entornId,
			String taskId,
			Map<String, Object> variables,
			boolean comprovarAssignacio,
			String usuari)  throws NotFoundException, IllegalStateException{
		JbpmTask task = comprovarSeguretatTasca(entornId, taskId, usuari, comprovarAssignacio);
		Expedient expedient = null;
		if (MesuresTemporalsHelper.isActiu()) {
			JbpmProcessInstance pi = jbpmDao.getRootProcessInstance(task.getProcessInstanceId());
			expedient = expedientDao.findAmbProcessInstanceId(pi.getId());
			mesuresTemporalsHelper.mesuraIniciar("Validar tasca", "tasques", expedient.getTipus().getNom(), task.getTaskName());
			mesuresTemporalsHelper.mesuraIniciar("Validar tasca", "tasques", expedient.getTipus().getNom(), task.getTaskName(), "LOG");
		}
		expedientLogHelper.afegirLogExpedientPerTasca(
				taskId,
				ExpedientLogAccioTipus.TASCA_FORM_VALIDAR,
				null,
				usuari);
		if (MesuresTemporalsHelper.isActiu()) {
			mesuresTemporalsHelper.mesuraCalcular("Validar tasca", "tasques", expedient.getTipus().getNom(), task.getTaskName(), "LOG");
			mesuresTemporalsHelper.mesuraCalcular("Validar tasca", "tasques", expedient.getTipus().getNom(), task.getTaskName(), "Optimitzar DOMINI");
		}
		optimitzarConsultesDomini(task, variables);
		if (MesuresTemporalsHelper.isActiu()) {
			mesuresTemporalsHelper.mesuraCalcular("Validar tasca", "tasques", expedient.getTipus().getNom(), task.getTaskName(), "Optimitzar DOMINI");
			mesuresTemporalsHelper.mesuraIniciar("Validar tasca", "tasques", expedient.getTipus().getNom(), task.getTaskName(), "Start TaskInstance");
		}
		jbpmDao.startTaskInstance(taskId);
		if (MesuresTemporalsHelper.isActiu()) {
			mesuresTemporalsHelper.mesuraCalcular("Validar tasca", "tasques", expedient.getTipus().getNom(), task.getTaskName(), "Start TaskInstance");
			mesuresTemporalsHelper.mesuraIniciar("Validar tasca", "tasques", expedient.getTipus().getNom(), task.getTaskName(), "taskInstance Variables");
		}
		jbpmDao.setTaskInstanceVariables(taskId, variables, false);
		if (MesuresTemporalsHelper.isActiu()) {
			mesuresTemporalsHelper.mesuraCalcular("Validar tasca", "tasques", expedient.getTipus().getNom(), task.getTaskName(), "taskInstance Variables");
			mesuresTemporalsHelper.mesuraIniciar("Validar tasca", "tasques", expedient.getTipus().getNom(), task.getTaskName(), "Validar");
		}
		validarTasca(taskId);
		if (MesuresTemporalsHelper.isActiu()) 
			mesuresTemporalsHelper.mesuraCalcular("Validar tasca", "tasques", expedient.getTipus().getNom(), task.getTaskName(), "Validar");
		TascaDto tasca = toTascaDto(task, null, true, true);
		if (usuari == null)
			usuari = SecurityContextHolder.getContext().getAuthentication().getName();
		if (MesuresTemporalsHelper.isActiu())
			mesuresTemporalsHelper.mesuraIniciar("Validar tasca", "tasques", expedient.getTipus().getNom(), task.getTaskName(), "REG");
		registreDao.crearRegistreModificarTasca(
				tasca.getExpedient().getId(),
				taskId,
				usuari,
				"Validar \"" + tasca.getNom() + "\"");
		if (MesuresTemporalsHelper.isActiu()) {
			mesuresTemporalsHelper.mesuraCalcular("Validar tasca", "tasques", expedient.getTipus().getNom(), task.getTaskName(), "REG");
			mesuresTemporalsHelper.mesuraCalcular("Validar tasca", "tasques", expedient.getTipus().getNom(), task.getTaskName());
		}
		return tasca;
	}
	public TascaDto restaurar(
			Long entornId,
			String taskId) {
		return restaurar(entornId, taskId, null);
	}
	public TascaDto restaurar(
			Long entornId,
			String taskId,
			String user)  throws NotFoundException, IllegalStateException{
		JbpmTask task = comprovarSeguretatTasca(entornId, taskId, user, true);
		Expedient expedient = null;
		if (MesuresTemporalsHelper.isActiu()) {
			JbpmProcessInstance pi = jbpmDao.getRootProcessInstance(task.getProcessInstanceId());
			expedient = expedientDao.findAmbProcessInstanceId(pi.getId());
			mesuresTemporalsHelper.mesuraIniciar("Restaurar tasca", "tasques", expedient.getTipus().getNom(), task.getTaskName());
			mesuresTemporalsHelper.mesuraIniciar("Restaurar tasca", "tasques", expedient.getTipus().getNom(), task.getTaskName(), "LOG");
		}
		expedientLogHelper.afegirLogExpedientPerTasca(
				taskId,
				ExpedientLogAccioTipus.TASCA_FORM_RESTAURAR,
				null,
				user);
		if (MesuresTemporalsHelper.isActiu()) {
			mesuresTemporalsHelper.mesuraCalcular("Restaurar tasca", "tasques", expedient.getTipus().getNom(), task.getTaskName(), "LOG");
			mesuresTemporalsHelper.mesuraIniciar("Restaurar tasca", "tasques", expedient.getTipus().getNom(), task.getTaskName(), "Validada");
		}
		if (!isTascaValidada(task))
			throw new IllegalStateException(
					getServiceUtils().getMessage("error.tascaService.noValidada"));
		if (MesuresTemporalsHelper.isActiu()) {
			mesuresTemporalsHelper.mesuraCalcular("Restaurar tasca", "tasques", expedient.getTipus().getNom(), task.getTaskName(), "Validada");
			mesuresTemporalsHelper.mesuraIniciar("Restaurar tasca", "tasques", expedient.getTipus().getNom(), task.getTaskName(), "Restaurar");
		}
		restaurarTasca(taskId);
		if (MesuresTemporalsHelper.isActiu())
			mesuresTemporalsHelper.mesuraCalcular("Restaurar tasca", "tasques", expedient.getTipus().getNom(), task.getTaskName(), "Restaurar");
		TascaDto tasca = toTascaDto(task, null, true, true);
		if (user == null) 
			user = SecurityContextHolder.getContext().getAuthentication().getName();
		if (MesuresTemporalsHelper.isActiu())
			mesuresTemporalsHelper.mesuraIniciar("Restaurar tasca", "tasques", expedient.getTipus().getNom(), task.getTaskName(), "REG");
		registreDao.crearRegistreModificarTasca(
				tasca.getExpedient().getId(),
				taskId,
				user,
				"Restaurar \"" + tasca.getNom() + "\"");
		if (MesuresTemporalsHelper.isActiu()) {
			mesuresTemporalsHelper.mesuraCalcular("Restaurar tasca", "tasques", expedient.getTipus().getNom(), task.getTaskName(), "REG");
			mesuresTemporalsHelper.mesuraCalcular("Restaurar tasca", "tasques", expedient.getTipus().getNom(), task.getTaskName());
		}
		return tasca;
	}
	public void completar(
			Long entornId,
			String taskId,
			boolean comprovarAssignacio,
			String usuari) {
		completar(entornId, taskId, comprovarAssignacio, usuari, null);
	}
	public void completar(
			Long entornId,
			String taskId,
			boolean comprovarAssignacio,
			String usuari,
			String outcome) throws NotFoundException, IllegalStateException {
		JbpmTask task = comprovarSeguretatTasca(
				entornId,
				taskId,
				usuari,
				comprovarAssignacio);
		if (!isTascaValidada(task))
			throw new IllegalStateException(
					getServiceUtils().getMessage("error.tascaService.formNoValidat"));
		if (!isDocumentsComplet(task))
			throw new IllegalStateException(
					getServiceUtils().getMessage("error.tascaService.faltenAdjuntar"));
		if (!isSignaturesComplet(task))
			throw new IllegalStateException(
					getServiceUtils().getMessage("error.tascaService.faltenSignar"));
		ProcessInstanceExpedient piexp = jbpmDao.expedientFindByProcessInstanceId(
				task.getProcessInstanceId());
		Expedient expedient = expedientDao.getById(piexp.getId(), false);
		//JbpmProcessInstance pi = jbpmDao.getRootProcessInstance(task.getProcessInstanceId());
		//Expedient expedient = expedientDao.findAmbProcessInstanceId(pi.getId());
		//DadesCacheTasca dct = getDadesCacheTasca(task);
		mesuresTemporalsHelper.tascaCompletarIniciar(expedient, taskId, task.getTaskName());
		final Timer timerTotal = metricRegistry.timer(
				MetricRegistry.name(
						TascaService.class,
						"completar"));
		final Timer.Context contextTotal = timerTotal.time();
		Counter countTotal = metricRegistry.counter(
				MetricRegistry.name(
						TascaService.class,
						"completar.count"));
		countTotal.inc();
		final Timer timerEntorn = metricRegistry.timer(
				MetricRegistry.name(
						TascaService.class,
						"completar",
						expedient.getEntorn().getCodi()));
		final Timer.Context contextEntorn = timerEntorn.time();
		Counter countEntorn = metricRegistry.counter(
				MetricRegistry.name(
						TascaService.class,
						"completar.count",
						expedient.getEntorn().getCodi()));
		countEntorn.inc();
		final Timer timerTipexp = metricRegistry.timer(
				MetricRegistry.name(
						TascaService.class,
						"completar",
						expedient.getEntorn().getCodi(),
						expedient.getTipus().getCodi()));
		final Timer.Context contextTipexp = timerTipexp.time();
		Counter countTipexp = metricRegistry.counter(
				MetricRegistry.name(
						TascaService.class,
						"completar.count",
						expedient.getEntorn().getCodi(),
						expedient.getTipus().getCodi()));
		countTipexp.inc();
		try {
			ThreadLocalInfo.clearProcessInstanceFinalitzatIds();
			//mesuresTemporalsHelper.mesuraIniciar("Completar tasca", "tasques", expedient.getTipus().getNom(), task.getTaskName());
			//mesuresTemporalsHelper.mesuraIniciar("Completar tasca", "tasques", expedient.getTipus().getNom(), task.getTaskName(), "LOG");
			expedientLogHelper.afegirLogExpedientPerTasca(
					taskId,
					ExpedientLogAccioTipus.TASCA_COMPLETAR,
					outcome,
					usuari);
			//mesuresTemporalsHelper.mesuraCalcular("Completar tasca", "tasques", expedient.getTipus().getNom(), task.getTaskName(), "LOG");
			//mesuresTemporalsHelper.mesuraIniciar("Completar tasca", "tasques", expedient.getTipus().getNom(), task.getTaskName(), "Start TaskInstance");
			jbpmDao.startTaskInstance(taskId);
			//mesuresTemporalsHelper.mesuraCalcular("Completar tasca", "tasques", expedient.getTipus().getNom(), task.getTaskName(), "Start TaskInstance");
			//mesuresTemporalsHelper.mesuraIniciar("Completar tasca", "tasques", expedient.getTipus().getNom(), task.getTaskName(), "End TaskInstance");
			jbpmDao.endTaskInstance(task.getId(), outcome);
			//mesuresTemporalsHelper.mesuraCalcular("Completar tasca", "tasques", expedient.getTipus().getNom(), task.getTaskName(), "End TaskInstance");
			//mesuresTemporalsHelper.mesuraIniciar("Completar tasca", "tasques", expedient.getTipus().getNom(), task.getTaskName(), "Delegation info");
			// Accions per a una tasca delegada
			DelegationInfo delegationInfo = getDelegationInfo(task);
			//mesuresTemporalsHelper.mesuraCalcular("Completar tasca", "tasques", expedient.getTipus().getNom(), task.getTaskName(), "Delegation Info");
			if (delegationInfo != null) {
				//mesuresTemporalsHelper.mesuraIniciar("Completar tasca", "tasques", expedient.getTipus().getNom(), task.getTaskName(), "Delegation");
				if (!taskId.equals(delegationInfo.getSourceTaskId())) {
					// Copia les variables de la tasca delegada a la original
					jbpmDao.setTaskInstanceVariables(
							delegationInfo.getSourceTaskId(),
							getVariablesDelegacio(task),
							false);
					JbpmTask taskOriginal = jbpmDao.getTaskById(delegationInfo.getSourceTaskId());
					if (!delegationInfo.isSupervised()) {
						// Si no es supervisada també finalitza la tasca original
						completar(entornId, taskOriginal.getId(), false, null, outcome);
					}
					deleteDelegationInfo(taskOriginal);
				}
				//mesuresTemporalsHelper.mesuraCalcular("Completar tasca", "tasques", expedient.getTipus().getNom(), task.getTaskName(), "Delegation");
			}
			//mesuresTemporalsHelper.mesuraIniciar("Completar tasca", "tasques", expedient.getTipus().getNom(), task.getTaskName(), "Actualitzar alertes");
			actualitzarTerminisIAlertes(taskId, expedient);
			//mesuresTemporalsHelper.mesuraCalcular("Completar tasca", "tasques", expedient.getTipus().getNom(), task.getTaskName(), "Actualitzar alertes");
			//mesuresTemporalsHelper.mesuraIniciar("Completar tasca", "tasques", expedient.getTipus().getNom(), task.getTaskName(), "Actualitzar data fi");
			expedientHelper.verificarFinalitzacioExpedient(expedient);
			//mesuresTemporalsHelper.mesuraCalcular("Completar tasca", "tasques", expedient.getTipus().getNom(), task.getTaskName(), "Actualitzar data fi");
			//mesuresTemporalsHelper.mesuraIniciar("Completar tasca", "tasques", expedient.getTipus().getNom(), task.getTaskName(), "Update lucene");
			getServiceUtils().expedientIndexLuceneUpdate(
					task.getProcessInstanceId(),
					true,
					expedient);
			//mesuresTemporalsHelper.mesuraCalcular("Completar tasca", "tasques", expedient.getTipus().getNom(), task.getTaskName(), "Update lucene");
			TascaDto tasca = toTascaDto(task, null, true, true);
			if (usuari == null)
				usuari = SecurityContextHolder.getContext().getAuthentication().getName();
			//mesuresTemporalsHelper.mesuraIniciar("Completar tasca", "tasques", expedient.getTipus().getNom(), task.getTaskName(), "REG");
			registreDao.crearRegistreFinalitzarTasca(
					tasca.getExpedient().getId(),
					taskId,
					usuari,
					"Finalitzar \"" + tasca.getNom() + "\"");
			//mesuresTemporalsHelper.mesuraCalcular("Completar tasca", "tasques", expedient.getTipus().getNom(), task.getTaskName(), "REG");
			//mesuresTemporalsHelper.mesuraCalcular("Completar tasca", "tasques", expedient.getTipus().getNom(), task.getTaskName());
		} finally {
			mesuresTemporalsHelper.tascaCompletarFinalitzar(taskId);
			contextTotal.stop();
			contextEntorn.stop();
			contextTipexp.stop();
		}
	}

	public Object getVariable(
			Long entornId,
			String taskId,
			String codiVariable) throws NotFoundException, IllegalStateException {
		JbpmTask task = comprovarSeguretatTasca(entornId, taskId, null, true);
		return getServiceUtils().getVariableJbpmTascaValor(task.getId(), codiVariable);
	}
	public void createVariable(
			Long entornId,
			String taskId,
			String codiVariable,
			Object valor) throws NotFoundException, IllegalStateException {
		JbpmTask task = comprovarSeguretatTasca(entornId, taskId, null, true);
		Map<String, Object> variables = new HashMap<String, Object>();
		variables.put(codiVariable, valor);
		optimitzarConsultesDomini(task, variables);
		jbpmDao.setTaskInstanceVariables(task.getId(), variables, false);
		TascaDto tasca = toTascaDto(task, null, true, true);
		registreDao.crearRegistreCrearVariableTasca(
				tasca.getExpedient().getId(),
				taskId,
				SecurityContextHolder.getContext().getAuthentication().getName(),
				codiVariable,
				valor);
	}
	public void updateVariable(
			Long entornId,
			String taskId,
			String codiVariable,
			Object valor) {
		updateVariable(
				entornId,
				taskId,
				codiVariable,
				valor,
				null);
	}
	public void updateVariable(
			Long entornId,
			String taskId,
			String codiVariable,
			Object valor,
			String user) throws NotFoundException, IllegalStateException {
		JbpmTask task = comprovarSeguretatTasca(entornId, taskId, null, true);
		Object valorVell = getServiceUtils().getVariableJbpmTascaValor(task.getId(), codiVariable);
		Map<String, Object> variables = new HashMap<String, Object>();
		variables.put(codiVariable, valor);
		jbpmDao.setTaskInstanceVariables(task.getId(), variables, false);
		TascaDto tasca = toTascaDto(task, null, true, true);
		if (user == null) {
			user = SecurityContextHolder.getContext().getAuthentication().getName();
		}
		registreDao.crearRegistreModificarVariableTasca(
				tasca.getExpedient().getId(),
				taskId,
				user,
				codiVariable,
				valorVell,
				valor);
	}
	public void esborrarVariable(
			Long entornId,
			String taskId,
			String codiVariable) throws NotFoundException, IllegalStateException {
		JbpmTask task = comprovarSeguretatTasca(entornId, taskId, null, true);
		jbpmDao.deleteTaskInstanceVariable(task.getId(), codiVariable);
		TascaDto tasca = toTascaDto(task, null, true, true);
		registreDao.crearRegistreEsborrarVariableTasca(
				tasca.getExpedient().getId(),
				taskId,
				SecurityContextHolder.getContext().getAuthentication().getName(),
				codiVariable);
	}

	public void delegacioCrear(
			Long entornId,
			String taskId,
			String actorId,
			String comentari,
			boolean supervisada)  throws NotFoundException, IllegalStateException{
		JbpmTask original = comprovarSeguretatTasca(entornId, taskId, null, true);
		JbpmTask delegada = jbpmDao.cloneTaskInstance(
				taskId,
				actorId,
				getVariablesDelegacio(original));
		createDelegationInfo(
				original,
				original,
				delegada,
				comentari,
				supervisada);
		createDelegationInfo(
				delegada,
				original,
				delegada,
				comentari,
				supervisada);
	}
	public void delegacioCancelar(
			Long entornId,
			String taskId)  throws NotFoundException, IllegalStateException{
		JbpmTask task = comprovarSeguretatTasca(entornId, taskId, null, true);
		DelegationInfo delegationInfo = getDelegationInfo(task);
		if (delegationInfo == null || !taskId.equals(delegationInfo.getSourceTaskId())) {
			throw new IllegalStateException(
					getServiceUtils().getMessage("error.tascaService.cancelarDelegacio"));
		}
		// Cancelar la tasca delegada
		jbpmDao.cancelTaskInstance(delegationInfo.getTargetTaskId());
		// Esborram la delegació
		deleteDelegationInfo(task);
	}

	public FormulariExtern iniciarFormulariExtern(
			Long entornId,
			String taskId) throws NotFoundException, IllegalStateException {
		JbpmTask task = comprovarSeguretatTasca(entornId, taskId, null, true);
		Tasca tasca = tascaDao.findAmbActivityNameIProcessDefinitionId(
				task.getTaskName(),
				task.getProcessDefinitionId());
		if (tasca.getFormExtern() == null)
			throw new IllegalStateException(
					getServiceUtils().getMessage("error.tascaService.noFormExtern"));
		Map<String, Object> vars = getServiceUtils().getVariablesJbpmTascaValor(task.getId());
		JbpmProcessInstance rootProcessInstance = jbpmDao.getRootProcessInstance(task.getProcessInstanceId());
		Expedient expedient = expedientDao.findAmbProcessInstanceId(rootProcessInstance.getId());
		return formulariExternDao.iniciarFormulariExtern(
				expedient.getTipus(),
				taskId,
				tasca.getFormExtern(),
				vars);
	}

	public FormulariExtern iniciarFormulariExtern(
			String taskId,
			Long expedientTipusId,
			Long definicioProcesId) {
		ExpedientTipus expedientTipus = expedientTipusDao.getById(expedientTipusId, false);
		DefinicioProces definicioProces = null;
		if (definicioProcesId != null) {
			definicioProces = definicioProcesDao.getById(definicioProcesId, false);
		} else {
			definicioProces = definicioProcesDao.findDarreraVersioAmbEntornIJbpmKey(
					expedientTipus.getEntorn().getId(),
					expedientTipus.getJbpmProcessDefinitionKey());
		}
		if (definicioProcesId == null && definicioProces == null) {
			logger.error("No s'ha trobat la definició de procés (entorn=" + expedientTipus.getEntorn().getCodi() + ", jbpmKey=" + expedientTipus.getJbpmProcessDefinitionKey() + ")");
		}
		String startTaskName = jbpmDao.getStartTaskName(definicioProces.getJbpmId());
		Tasca tasca = tascaDao.findAmbActivityNameIProcessDefinitionId(
				startTaskName,
				definicioProces.getJbpmId());
		return formulariExternDao.iniciarFormulariExtern(
				expedientTipus,
				taskId,
				tasca.getFormExtern(),
				null);
	}

	public void guardarFormulariExtern(
			String formulariId,
			Map<String, Object> variables) {
		FormulariExtern formExtern = formulariExternDao.findAmbFormulariId(formulariId);
		if (formExtern != null) {
			if (formulariId.startsWith("TIE_")) {
				if (dadesFormulariExternInicial == null)
					dadesFormulariExternInicial = new HashMap<String, Map<String, Object>>();
				dadesFormulariExternInicial.put(formulariId, variables);
			} else {
				Map<String, Object> valors = new HashMap<String, Object>();
				JbpmTask task = jbpmDao.getTaskById(formExtern.getTaskId());
				Tasca tasca = tascaDao.findAmbActivityNameIProcessDefinitionId(
						task.getTaskName(),
						task.getProcessDefinitionId());
				for (CampTasca camp: tasca.getCamps()) {
					if (!camp.isReadOnly()) {
						String codi = camp.getCamp().getCodi();
						if (variables.keySet().contains(codi))
							valors.put(codi, variables.get(codi));
					}
				}
				validar(
						getDadesCacheTasca(task).getEntornId(),
						formExtern.getTaskId(),
						valors,
						false);
			}
			formExtern.setDataRecepcioDades(new Date());
			logger.info("Les dades del formulari amb id " + formulariId + " han estat guardades");
		} else {
			logger.warn("No s'ha trobat cap tasca amb l'id de formulari " + formulariId);
		}
	}
	public Map<String, Object> obtenirValorsFormulariExternInicial(String formulariId) {
		if (dadesFormulariExternInicial == null)
			return null;
		return dadesFormulariExternInicial.remove(formulariId);
	}

	public List<FilaResultat> getValorsCampSelect(
			String taskId,
			String campCodi,
			String textInicial) {
		JbpmTask task = jbpmDao.getTaskById(taskId);
		DefinicioProces definicioProces = definicioProcesDao.findAmbJbpmId(task.getProcessDefinitionId());
		return dtoConverter.getResultatConsultaDomini(
				definicioProces,
				taskId,
				null,
				campCodi,
				textInicial,
				null);
	}

	public void executarAccio(
			Long entornId,
			String taskId,
			String accio) {
		executarAccio(entornId, taskId, accio, null);
	}
	public void executarAccio(
			Long entornId,
			String taskId,
			String accio,
			String user)  throws NotFoundException, IllegalStateException {
		JbpmTask task = comprovarSeguretatTasca(entornId, taskId, user, true);
		Expedient expedient = null;
		if (MesuresTemporalsHelper.isActiu()) {
			JbpmProcessInstance pi = jbpmDao.getRootProcessInstance(task.getProcessInstanceId());
			expedient = expedientDao.findAmbProcessInstanceId(pi.getId());
			mesuresTemporalsHelper.mesuraIniciar("Executar ACCIO" + accio, "tasques", expedient.getTipus().getNom(), task.getTaskName());
			mesuresTemporalsHelper.mesuraIniciar("Executar ACCIO" + accio, "tasques", expedient.getTipus().getNom(), task.getTaskName(), "LOG");
		}
		expedientLogHelper.afegirLogExpedientPerTasca(
				taskId,
				ExpedientLogAccioTipus.TASCA_ACCIO_EXECUTAR,
				accio,
				user);
		if (MesuresTemporalsHelper.isActiu())
			mesuresTemporalsHelper.mesuraCalcular("Executar ACCIO" + accio, "tasques", expedient.getTipus().getNom(), task.getTaskName(), "LOG");
		jbpmDao.executeActionInstanciaTasca(taskId, accio, null);
		getServiceUtils().expedientIndexLuceneUpdate(task.getProcessInstanceId());
		if (MesuresTemporalsHelper.isActiu())
			mesuresTemporalsHelper.mesuraCalcular("Executar ACCIO" + accio, "tasques", expedient.getTipus().getNom(), task.getTaskName());
	}

	public void comprovarTascaAssignadaIValidada(
			Long entornId,
			String taskInstanceId,
			String usuari) throws NotFoundException, IllegalStateException {
		JbpmTask task = comprovarSeguretatTasca(entornId, taskInstanceId, usuari, true);
		if (!isTascaValidada(task))
			throw new IllegalStateException(
					getServiceUtils().getMessage("error.tascaService.noValidada"));
	}

	@Autowired
	public void setEntornDao(EntornDao entornDao) {
		this.entornDao = entornDao;
	}
	@Autowired
	public void setExpedientDao(ExpedientDao expedientDao) {
		this.expedientDao = expedientDao;
	}
	@Autowired
	public void setExpedientTipusDao(ExpedientTipusDao expedientTipusDao) {
		this.expedientTipusDao = expedientTipusDao;
	}
	@Autowired
	public void setTascaDao(TascaDao tascaDao) {
		this.tascaDao = tascaDao;
	}
	@Autowired
	public void setDefinicioProcesDao(DefinicioProcesDao definicioProcesDao) {
		this.definicioProcesDao = definicioProcesDao;
	}
	@Autowired
	public void setJbpmHelper(JbpmHelper jbpmDao) {
		this.jbpmDao = jbpmDao;
	}
	@Autowired
	public void setAclServiceDao(AclServiceDao aclServiceDao) {
		this.aclServiceDao = aclServiceDao;
	}
	@Autowired
	public void setPluginPersonaDao(PluginPersonaDao pluginPersonaDao) {
		this.pluginPersonaDao = pluginPersonaDao;
	}
	@Autowired
	public void setDtoConverter(DtoConverter dtoConverter) {
		this.dtoConverter = dtoConverter;
	}
	@Autowired
	public void setLuceneDao(LuceneDao luceneDao) {
		this.luceneDao = luceneDao;
	}
	@Autowired
	public void setRegistreDao(RegistreDao registreDao) {
		this.registreDao = registreDao;
	}
	@Autowired
	public void setFormulariExternDao(FormulariExternDao formulariExternDao) {
		this.formulariExternDao = formulariExternDao;
	}
	@Autowired
	public void setTerminiIniciatDao(
			TerminiIniciatDao terminiIniciatDao) {
		this.terminiIniciatDao = terminiIniciatDao;
	}
	@Autowired
	public void setAlertaDao(
			AlertaDao alertaDao) {
		this.alertaDao = alertaDao;
	}
	@Autowired
	public void setCampDao(
			CampDao campDao) {
		this.campDao = campDao;
	}
	@Autowired
	public void setCampTascaDao(
			CampTascaDao campTascaDao) {
		this.campTascaDao = campTascaDao;
	}
	@Autowired
	public void setConsultaCampDao(
			ConsultaCampDao consultaCampDao) {
		this.consultaCampDao = consultaCampDao;
	}
	@Autowired
	public void setMessageSource(MessageSource messageSource) {
		this.messageSource = messageSource;
	}
	@Autowired
	public void setExpedientLogHelper(ExpedientLogHelper expedientLogHelper) {
		this.expedientLogHelper = expedientLogHelper;
	}
	@Autowired
	public void setExpedientHelper(ExpedientHelper expedientHelper) {
		this.expedientHelper = expedientHelper;
	}



	private JbpmTask comprovarSeguretatTasca(Long entornId, String taskId, String usuari, boolean comprovarAssignacio) throws NotFoundException, IllegalStateException {
		JbpmTask task = jbpmDao.getTaskById(taskId);
		if (task == null) {
			throw new NotFoundException(
					getServiceUtils().getMessage("error.tascaService.noTrobada"));
		}
		Long tascaEntornId = getDadesCacheTasca(task).getEntornId();
		if (!tascaEntornId.equals(entornId)) {
			throw new NotFoundException(
					getServiceUtils().getMessage("error.tascaService.noTrobada"));
		}
		if (comprovarAssignacio) {
			if (usuari == null) {
				Authentication auth = SecurityContextHolder.getContext().getAuthentication();
				if (!auth.getName().equals(task.getAssignee()))
					throw new NotFoundException(
							getServiceUtils().getMessage("error.tascaService.noAssignada"));
			} else {
				if (!usuari.equals(task.getAssignee()))
					throw new NotFoundException(
							getServiceUtils().getMessage("error.tascaService.noAssignada"));
			}
		}
		if (task.isSuspended() || !task.isOpen()) {
			throw new IllegalStateException(
					getServiceUtils().getMessage("error.tascaService.noDisponible"));
		}
		return task;
	}
	private TascaDto toTascaDto(
			JbpmTask task,
			Map<String, Object> varsCommand,
			boolean ambVariables,
			boolean ambTexts) {
		return dtoConverter.toTascaDto(
				task,
				varsCommand,
				ambVariables,
				ambTexts,
				isTascaValidada(task),
				isDocumentsComplet(task),
				isSignaturesComplet(task));
	}

	/*private List<Long> getExpedientIdsPerConsultaTasques(
			Long entornId,
			String usuariCodi,
			String expedient,
			String numeroExpedient,
			Long tipusExpedient,
			String sort,
			boolean asc) {
		return expedientDao.findListExpedients(
				entornId,
				usuariCodi,
				expedient,
				numeroExpedient,
				tipusExpedient,
				sort,
				asc);
	}*/

	private PaginaLlistatDto findTasquesFiltre(
			Map<Long, List<Long>> unitatsPerTipusComuns,
			Long entornId,
			String usuari,
			String taskName,
			String titol,
			String expedientTitol,
			String expedientNumero,
			Long expedientTipusId,
			Date dataCreacioInici,
			Date dataCreacioFi,
			Integer prioritat,
			Date dataLimitInici,
			Date dataLimitFi,
			int firstResult,
			int maxResults,
			String sort,
			boolean asc,
			boolean mostrarAssignadesUsuari,
			boolean mostrarAssignadesGrup,
			boolean nomesPendents,
			boolean ambPersones) {
		PaginaLlistatDto resposta;
		Entorn entorn = entornDao.getById(entornId,  false);
		final Timer timerTotal = metricRegistry.timer(
				MetricRegistry.name(
						TascaService.class,
						"findTasquesFiltre",
						entorn.getCodi()));
		final Timer.Context contextTotal = timerTotal.time();
		try {
//			String usuariBo = usuari;
//			if (usuariBo == null) {
//				Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//				usuariBo = auth.getName();
//			}
			final Timer timerTasques = metricRegistry.timer(
					MetricRegistry.name(
							TascaService.class,
							"findTasquesFiltre",
							entorn.getCodi(),
							"1_tasks"));
			final Timer.Context contextTasques = timerTasques.time();
			PaginacioParamsDto paginacioParams = new PaginacioParamsDto();
			paginacioParams.setPaginaNum(firstResult / maxResults);
			paginacioParams.setPaginaTamany(maxResults);
			paginacioParams.afegirOrdre(
					sort,
					asc ? OrdreDireccioDto.ASCENDENT : OrdreDireccioDto.DESCENDENT);
			ResultatConsultaPaginadaJbpm<JbpmTask> tasks;
			try {
				// Consulta els expedients de l'entorn que compleixen el filtre
				tasks = jbpmDao.tascaFindByFiltrePaginat(
						//unitatsPerTipusComuns,
						entornId,
						usuari,
						taskName,
						titol,
						null,
						expedientTitol,
						expedientNumero,
						expedientTipusId,
						dataCreacioInici,
						dataCreacioFi,
						prioritat,
						dataLimitInici,
						dataLimitFi,
						mostrarAssignadesUsuari,
						mostrarAssignadesGrup,
						nomesPendents,
						paginacioParams);
			} finally {
				contextTasques.stop();
			}
			final Timer timerResposta = metricRegistry.timer(
					MetricRegistry.name(
							TascaService.class,
							"findTasquesFiltre",
							entorn.getCodi(),
							"2_resposta"));
			final Timer.Context contextResposta = timerResposta.time();
			List<TascaLlistatDto> tasques = new ArrayList<TascaLlistatDto>();
			try {
				for (JbpmTask task: tasks.getLlista()) {
					tasques.add(
							toTascaLlistatDto(
									task,
									ambPersones));
				}
				resposta = new PaginaLlistatDto(
						tasks.getCount(),
						tasques);
			} finally {
				contextResposta.stop();
			}
		} finally {
			contextTotal.stop();
		}
		return resposta;
	}
	/*private List<TascaLlistatDto> tasquesFiltradesPerEntorn(
			Long entornId,
			List<JbpmTask> tasques,
			boolean complet) {
		// Filtra les tasques per mostrar només les del entorn seleccionat
		List<TascaLlistatDto> filtrades = new ArrayList<TascaLlistatDto>();
		for (JbpmTask task: tasques) {
			DadesCacheTasca dadesCacheTasca = getDadesCacheTasca(task);
			Long currentEntornId = dadesCacheTasca.getEntornId();
			if (currentEntornId != null && entornId.equals(currentEntornId)) {
				TascaLlistatDto dto = toTascaLlistatDto(task, null);
				if (complet) {
					dto.setExpedientNumero(dadesCacheTasca.getNumeroIdentificador());
					Tasca tasca = tascaDao.findAmbActivityNameIProcessDefinitionId(
							task.getName(),
							task.getProcessDefinitionId());
					dto.setMissatgeInfo(tasca.getMissatgeInfo());
					dto.setMissatgeWarn(tasca.getMissatgeWarn());
					dto.setResultats(
							jbpmDao.findTaskInstanceOutcomes(task.getId()));
				}
				filtrades.add(dto);
			}
		}
		return filtrades;
	}*/
	/*private PaginaLlistatDto tasquesLlistatFiltradesValors(
			Long entornId,
			List<JbpmTask> tasques,
			String tasca,
			String expedient,
			Long tipusExpedient,
			Date dataCreacioInici,
			Date dataCreacioFi,
			Integer prioritat,
			Date dataLimitInici,
			Date dataLimitFi,
			int firstRow,
			int maxResults,
			String sort,
			boolean asc) {
		return tasquesLlistatFiltradesValors(
				entornId,
				tasques,
				tasca,
				expedient,
				tipusExpedient,
				dataCreacioInici,
				dataCreacioFi,
				prioritat,
				dataLimitInici,
				dataLimitFi,
				firstRow,
				maxResults,
				sort,
				asc, 
				false);
	}*/
	/*private PaginaLlistatDto tasquesLlistatFiltradesValors(
			Long entornId,
			List<JbpmTask> tasques,
			String tasca,
			String expedient,
			Long tipusExpedient,
			Date dataCreacioInici,
			Date dataCreacioFi,
			Integer prioritat,
			Date dataLimitInici,
			Date dataLimitFi,
			int firstRow,
			int maxResults,
			String sort,
			boolean asc,
			boolean ambPersones) {
		//MesurarTemps.diferenciaReiniciar("LT_TASQUES_FLT");
		// Filtra primer els camps dels tasks
		Iterator<JbpmTask> it = tasques.iterator();
		while (it.hasNext()) {
			JbpmTask task = it.next();
			if (dataCreacioInici != null && task.getCreateTime().before(dataCreacioInici))
				it.remove();
			if (dataCreacioFi != null && task.getCreateTime().after(dataCreacioFi))
				it.remove();
			if (dataLimitInici != null && task.getDueDate().before(dataLimitInici))
				it.remove();
			if (dataLimitFi != null && task.getDueDate().after(dataLimitFi))
				it.remove();
			if (prioritat != null && prioritat.intValue() != task.getPriority())
				it.remove();
		}
		//MesurarTemps.diferenciaImprimirStdoutIReiniciar("LT_TASQUES_FLT", "1");
		// Després filtra els altres camps
		List<DadesTascaOrdenacio> filtrades = new ArrayList<DadesTascaOrdenacio>();
		//MesurarTemps.mitjaReiniciar("LT_TASQUES_DIF1");
		//MesurarTemps.mitjaReiniciar("LT_TASQUES_DIF2");
		for (JbpmTask task: tasques) {
			//MesurarTemps.diferenciaReiniciar("LT_TASQUES_DIF");
			DadesCacheTasca dadesCacheTasca = getDadesCacheTasca(task);
			//MesurarTemps.mitjaCalcular("LT_TASQUES_DIF1", "LT_TASQUES_DIF");
			Long currentEntornId = dadesCacheTasca.getEntornId();
			if ((currentEntornId != null) && (entornId.equals(currentEntornId))) {
				Boolean incloure = true;
				if (tasca != null && tasca.length() > 0) {
					String titolTasca = normalitzaText(dadesCacheTasca.getTitol());
					String titolFiltre = normalitzaText(tasca);
					incloure = incloure && titolTasca.contains(titolFiltre);
				}
				if (expedient != null && expedient.length() > 0) {
					String expedientTasca = normalitzaText(dadesCacheTasca.getIdentificador());
					String expedientFiltre = normalitzaText(expedient);
					incloure = incloure && expedientTasca.contains(expedientFiltre);
				}
				if (tipusExpedient != null)
					incloure = incloure && tipusExpedient.longValue() == dadesCacheTasca.getExpedientTipusId();
				if (incloure) {
					filtrades.add(
							new DadesTascaOrdenacio(
									task.getId(),
									dadesCacheTasca.getTitol(),
									dadesCacheTasca.getNumeroIdentificador(),
									dadesCacheTasca.getExpedientTipusNom(),
									task.getCreateTime(),
									task.getPriority(),
									task.getDueDate()));
				}
			}
			//MesurarTemps.mitjaCalcular("LT_TASQUES_DIF2", "LT_TASQUES_DIF");
		}
		//MesurarTemps.mitjaImprimirStdout("LT_TASQUES_DIF1", "A");
		//MesurarTemps.mitjaImprimirStdout("LT_TASQUES_DIF2", "A");
		//MesurarTemps.diferenciaImprimirStdoutIReiniciar("LT_TASQUES_FLT", "2");
		final String finalSort = sort;
		final boolean finalAsc = asc;
		Comparator<DadesTascaOrdenacio> comparador = new Comparator<DadesTascaOrdenacio>() {
			public int compare(DadesTascaOrdenacio t1, DadesTascaOrdenacio t2) {
				int result = 0;
				NullComparator nullComparator = new NullComparator();
				if ("titol".equals(finalSort)) {
					if (finalAsc)
						result = nullComparator.compare(t1.getTitol(), t2.getTitol());
					else
						result = nullComparator.compare(t2.getTitol(), t1.getTitol());
				} else if ("expedientTitol".equals(finalSort)) {
					if (finalAsc)
						result = nullComparator.compare(t1.getExpedientTitol(), t2.getExpedientTitol());
					else 
						result = nullComparator.compare(t2.getExpedientTitol(), t1.getExpedientTitol());
				} else if ("expedientTipusNom".equals(finalSort)) {
					if (finalAsc)
						result = nullComparator.compare(t1.getExpedientTipusNom(), t2.getExpedientTipusNom());
					else
						result = nullComparator.compare(t2.getExpedientTipusNom(), t1.getExpedientTipusNom());
				} else if ("dataCreacio".equals(finalSort)) {
					if (finalAsc)
						result = nullComparator.compare(t1.getDataCreacio(), t2.getDataCreacio());
					else
						result = nullComparator.compare(t2.getDataCreacio(), t1.getDataCreacio());
				} else if ("prioritat".equals(finalSort)) {
					if (finalAsc)
						result = t1.getPrioritat() - t2.getPrioritat();
					else
						result = t2.getPrioritat() - t1.getPrioritat();
				} else if ("dataLimit".equals(finalSort)) {
					if (finalAsc)
						result = nullComparator.compare(t1.getDataLimit(), t2.getDataLimit());
					else
						result = nullComparator.compare(t2.getDataLimit(), t1.getDataLimit());
				}
				return result;
			}
		};
		Collections.sort(filtrades, comparador);
		List<DadesTascaOrdenacio> respostaDades;
		if (filtrades.size() <= firstRow + maxResults)
			respostaDades = filtrades.subList(firstRow, filtrades.size());
		else
			respostaDades = filtrades.subList(firstRow, firstRow + maxResults);
		List<TascaLlistatDto> respostaLlistat = new ArrayList<TascaLlistatDto>();
		//MesurarTemps.diferenciaImprimirStdoutIReiniciar("LT_TASQUES_FLT", "3");
		for (DadesTascaOrdenacio dades: respostaDades) {
			for (JbpmTask task: tasques) {
				if (task.getId().equals(dades.getTaskId())) {
					respostaLlistat.add(
							toTascaLlistatDto(
									task,
									getDadesCacheTasca(task),
									ambPersones));
					break;
				}
			}
		}
		//MesurarTemps.diferenciaImprimirStdoutIReiniciar("LT_TASQUES_FLT", "4");
		return new PaginaLlistatDto(
				filtrades.size(),
				respostaLlistat);
	}*/
	private List<TascaLlistatDto> tasquesPerTramitacioMasiva(
			Long entornId,
			List<JbpmTask> tasques,
			String codi,
			String jbpmKey) {
		// Filtra les tasques per mostrar només les del entorn seleccionat
		List<TascaLlistatDto> resposta = new ArrayList<TascaLlistatDto>();
		for (JbpmTask task: tasques) {
			DadesCacheTasca dadesCacheTasca = getDadesCacheTasca(task);
			Long currentEntornId = dadesCacheTasca.getEntornId();
			if (currentEntornId != null && entornId.equals(currentEntornId)) {
				TascaLlistatDto dto = toTascaLlistatDto(
						task,
						false);
				if (codi.equals(task.getTaskName()) && jbpmKey.equals(dadesCacheTasca.getDefinicioProcesJbpmKey())) {
					resposta.add(dto);
				}
			}
		}
		return resposta;
	}

	/*private String normalitzaText(String text) {
		return text
			.toUpperCase().trim()
			.replaceAll("Á", "A")
			.replaceAll("À", "A")
			.replaceAll("É", "E")
			.replaceAll("È", "E")
			.replaceAll("Í", "I")
			.replaceAll("Ï", "I")
			.replaceAll("Ó", "O")
			.replaceAll("Ò", "O")
			.replaceAll("Ú", "U")
			.replaceAll("Ü", "U");
	}*/

	private void validarTasca(String taskId) {
		jbpmDao.setTaskInstanceVariable(
				taskId,
				VAR_TASCA_VALIDADA,
				new Date());
	}
	private void restaurarTasca(String taskId) {
		jbpmDao.deleteTaskInstanceVariable(taskId, VAR_TASCA_VALIDADA);
	}
	private boolean isTascaValidada(JbpmTask task) {
		Tasca tasca = tascaDao.findAmbActivityNameIProcessDefinitionId(
				task.getTaskName(),
				task.getProcessDefinitionId());
		boolean hiHaCampsModificables = false;
		for (CampTasca camp: tasca.getCamps()) {
			if (!camp.isReadOnly()) {
				hiHaCampsModificables = true;
				break;
			}
		}
		if (!hiHaCampsModificables)
			return true;
		Object valor = jbpmDao.getTaskInstanceVariable(task.getId(), VAR_TASCA_VALIDADA);
		if (valor == null || !(valor instanceof Date))
			return false;
		return true;
	}
	private boolean isDocumentsComplet(JbpmTask task) {
		boolean ok = true;
		Tasca tasca = tascaDao.findAmbActivityNameIProcessDefinitionId(
				task.getTaskName(),
				task.getProcessDefinitionId());
		for (DocumentTasca docTasca: tasca.getDocuments()) {
			if (docTasca.isRequired()) {
				String codiJbpm = JbpmVars.PREFIX_DOCUMENT + docTasca.getDocument().getCodi();
				Object valor = jbpmDao.getTaskInstanceVariable(
						task.getId(),
						codiJbpm);
				if (valor == null) {
					ok = false;
					break;
				}
			}
		}
		return ok;
	}
	private boolean isSignaturesComplet(JbpmTask task) {
		boolean ok = true;
		Tasca tasca = tascaDao.findAmbActivityNameIProcessDefinitionId(
				task.getTaskName(),
				task.getProcessDefinitionId());
		for (FirmaTasca firmaTasca: tasca.getFirmes()) {
			if (firmaTasca.isRequired()) {
				String codiJbpm = JbpmVars.PREFIX_SIGNATURA + firmaTasca.getDocument().getCodi();
				Object valor = jbpmDao.getTaskInstanceVariable(task.getId(), codiJbpm);
				if (valor == null)
					ok = false;
			}
		}
		return ok;
	}

	private void createDelegationInfo(
			JbpmTask task,
			JbpmTask original,
			JbpmTask delegada,
			String comentari,
			boolean supervisada) {
		DelegationInfo info = new DelegationInfo();
		info.setSourceTaskId(original.getId());
		info.setTargetTaskId(delegada.getId());
		info.setStart(new Date());
		info.setComment(comentari);
		info.setSupervised(supervisada);
		jbpmDao.setTaskInstanceVariable(
				task.getId(), 
				VAR_TASCA_DELEGACIO,
				info);
	}
	private DelegationInfo getDelegationInfo(JbpmTask task) {
		return (DelegationInfo)jbpmDao.getTaskInstanceVariable(
				task.getId(),
				VAR_TASCA_DELEGACIO);
	}
	private void deleteDelegationInfo(JbpmTask task) {
		jbpmDao.deleteTaskInstanceVariable(
				task.getId(),
				VAR_TASCA_DELEGACIO);
	}
	private Map<String, Object> getVariablesDelegacio(JbpmTask task) {
		return jbpmDao.getTaskInstanceVariables(task.getId());
	}

	private TascaLlistatDto toTascaLlistatDto(
			JbpmTask task,
			boolean ambPersones) {
		TascaLlistatDto dto = new TascaLlistatDto();
		dto.setId(task.getId());
		dto.setCodi(task.getTaskName());
		dto.setTitol(task.getTaskName());
		dto.setDataCreacio(task.getCreateTime());
		dto.setDataInici(task.getStartTime());
		dto.setDataFi(task.getEndTime());
		dto.setDataLimit(task.getDueDate());
		dto.setPrioritat(task.getPriority());
		dto.setResponsable(task.getAssignee());
		dto.setResponsables(task.getPooledActors());
		if (ambPersones)
			dto.setPersonesMap(getPersonesMap(task.getAssignee(), task.getPooledActors()));
		dto.setOberta(task.isOpen());
		dto.setCompletada(task.isCompleted());
		dto.setCancelada(task.isCancelled());
		dto.setSuspesa(task.isSuspended());
		dto.setProcessInstanceId(task.getProcessInstanceId());
		dto.setAgafada(task.isAgafada());
		Map<String, Object> valorsTasca = jbpmDao.getTaskInstanceVariables(task.getId());
		DelegationInfo delegationInfo = (DelegationInfo)valorsTasca.get(
				TascaService.VAR_TASCA_DELEGACIO);
		if (delegationInfo != null) {
			boolean original = task.getId().equals(delegationInfo.getSourceTaskId());
			dto.setDelegada(true);
			dto.setDelegacioOriginal(original);
			dto.setDelegacioData(delegationInfo.getStart());
			dto.setDelegacioSupervisada(delegationInfo.isSupervised());
			dto.setDelegacioComentari(delegationInfo.getComment());
			if (original) {
				JbpmTask tascaDelegacio = jbpmDao.getTaskById(delegationInfo.getTargetTaskId());
				dto.setDelegacioPersona(pluginPersonaDao.findAmbCodiPlugin(tascaDelegacio.getAssignee()));
			} else {
				JbpmTask tascaDelegacio = jbpmDao.getTaskById(delegationInfo.getSourceTaskId());
				dto.setDelegacioPersona(pluginPersonaDao.findAmbCodiPlugin(tascaDelegacio.getAssignee()));
			}
		}
		DadesCacheTasca dadesCacheTasca = getDadesCacheTasca(task);
		dto.setTitol(dadesCacheTasca.getTitol());
		dto.setTramitacioMassiva(dadesCacheTasca.isTramitacioMassiva());
		ProcessInstanceExpedient expedient = jbpmDao.expedientFindByProcessInstanceId(
				task.getProcessInstanceId());
		dto.setExpedientTitol(expedient.getIdentificador());
		dto.setExpedientTitolOrdenacio(expedient.getIdPerOrdenacio());
		dto.setExpedientTipusId(expedient.getTipus().getId());
		dto.setExpedientTipusNom(expedient.getTipus().getNom());
		dto.setExpedientProcessInstanceId(expedient.getProcessInstanceId());
		dto.setExpedientNumero(expedient.getNumero());
		return dto;
	}
	
	public Map<String, PersonaDto> getPersonesMap(String assignee, Set<String> pooledActors) {
		Map<String, PersonaDto> resposta = new HashMap<String, PersonaDto>();
		if (assignee != null)
			resposta.put(assignee, pluginPersonaDao.findAmbCodiPlugin(assignee));
		if (pooledActors != null) {
			for (String actorId: pooledActors)
				resposta.put(actorId, pluginPersonaDao.findAmbCodiPlugin(actorId));
		}
		return resposta;
	}

	private void actualitzarTerminisIAlertes(
			String taskId,
			Expedient expedient) {
		List<TerminiIniciat> terminisIniciats = terminiIniciatDao.findAmbTaskInstanceId(
				new Long(taskId));
		for (TerminiIniciat terminiIniciat: terminisIniciats) {
			terminiIniciat.setDataCompletat(new Date());
			esborrarAlertesAntigues(terminiIniciat);
			if (terminiIniciat.getTermini().isAlertaCompletat()) {
				JbpmTask task = jbpmDao.getTaskById(taskId);
				if (task.getAssignee() != null) {
					crearAlertaCompletat(terminiIniciat, task.getAssignee(), expedient);
				} else {
					for (String actor: task.getPooledActors())
						crearAlertaCompletat(terminiIniciat, actor, expedient);
				}
				terminiIniciat.setAlertaCompletat(true);
			}
		}
	}
	private void crearAlertaCompletat(
			TerminiIniciat terminiIniciat,
			String destinatari,
			Expedient expedient) {
		Alerta alerta = new Alerta(
				new Date(),
				destinatari,
				Alerta.AlertaPrioritat.NORMAL,
				terminiIniciat.getTermini().getDefinicioProces() != null ?
						terminiIniciat.getTermini().getDefinicioProces().getEntorn()
						: terminiIniciat.getTermini().getExpedientTipus().getEntorn());
		alerta.setExpedient(expedient);
		alerta.setTerminiIniciat(terminiIniciat);
		alertaDao.saveOrUpdate(alerta);
	}
	private void esborrarAlertesAntigues(TerminiIniciat terminiIniciat) {
		List<Alerta> antigues = alertaDao.findActivesAmbTerminiIniciatId(terminiIniciat.getId());
		for (Alerta antiga: antigues)
			antiga.setDataEliminacio(new Date());
	}

	/*private void verificarFinalitzacioExpedient(
			Expedient expedient) {
		JbpmProcessInstance pi = jbpmDao.getProcessInstance(expedient.getProcessInstanceId());
		if (pi.getEnd() != null) {
			// Actualitzar data de fi de l'expedient
			expedient.setDataFi(pi.getEnd());
			// Finalitzar terminis actius
			for (TerminiIniciat terminiIniciat: terminiIniciatDao.findAmbProcessInstanceId(pi.getId())) {
				if (terminiIniciat.getDataInici() != null) {
					terminiIniciat.setDataCancelacio(new Date());
					long[] timerIds = terminiIniciat.getTimerIdsArray();
					for (int i = 0; i < timerIds.length; i++)
						jbpmDao.suspendTimer(
								timerIds[i],
								new Date(Long.MAX_VALUE));
				}
			}
		}
	}*/

	private ServiceUtils getServiceUtils() {
		if (serviceUtils == null) {
			serviceUtils = new ServiceUtils(
					expedientDao,
					definicioProcesDao,
					campDao,
					consultaCampDao,
					luceneDao,
					dtoConverter,
					jbpmDao,
					aclServiceDao,
					messageSource,
					metricRegistry);
		}
		return serviceUtils;
	}

	private void optimitzarConsultesDomini(
			JbpmTask task,
			Map<String, Object> variables) {
		Tasca tasca = tascaDao.findAmbActivityNameIProcessDefinitionId(
				task.getTaskName(),
				task.getProcessDefinitionId());
		List<CampTasca> campsTasca = campTascaDao.findAmbTascaOrdenats(tasca.getId());
		for (CampTasca campTasca: campsTasca) {
			if (campTasca.getCamp().isDominiCacheText()) {
				Object campValor = variables.get(campTasca.getCamp().getCodi());
				if (campValor != null) {
					if (	campTasca.getCamp().getTipus().equals(TipusCamp.SELECCIO) ||
							campTasca.getCamp().getTipus().equals(TipusCamp.SUGGEST)) {
						String text = dtoConverter.getCampText(
								task.getId(),
								null,
								campTasca.getCamp(),
								campValor);
						variables.put(
								campTasca.getCamp().getCodi(),
								new DominiCodiDescripcio(
										(String)campValor,
										text));
					}
				}
			}
		}
	}

	private DadesCacheTasca getDadesCacheTasca(JbpmTask task) {
		DadesCacheTasca dadesCache = null;
		if (!task.isCacheActiu()) {
			ProcessInstanceExpedient expedient = jbpmDao.expedientFindByProcessInstanceId(task.getProcessInstanceId());
			Tasca tasca = tascaDao.findAmbActivityNameIProcessDefinitionId(
					task.getTaskName(),
					task.getProcessDefinitionId());
			String titol = tasca.getNom();
			if (tasca.getNomScript() != null && tasca.getNomScript().length() > 0)
				titol = dtoConverter.getTitolPerTasca(task, tasca);
			task.setFieldFromDescription(
					"entornId",
					new Long(expedient.getEntorn().getId()).toString());
			task.setFieldFromDescription(
					"titol",
					titol);
			task.setFieldFromDescription(
					"tramitacioMassiva",
					new Boolean(tasca.isTramitacioMassiva()).toString());
			task.setFieldFromDescription(
					"definicioProcesJbpmKey",
					tasca.getDefinicioProces().getJbpmKey());
			task.setCacheActiu();
			jbpmDao.describeTaskInstance(
					task.getId(),
					titol,
					task.getDescriptionWithFields());
		}
		dadesCache = new DadesCacheTasca(
				new Long(task.getFieldFromDescription("entornId")),
				task.getFieldFromDescription("titol"),
				new Boolean(task.getFieldFromDescription("tramitacioMassiva")).booleanValue(),
				task.getFieldFromDescription("definicioProcesJbpmKey"));
		return dadesCache;
	}
	private class DadesCacheTasca {
		private Long entornId;
		private String titol;
		private boolean tramitacioMassiva;
		private String definicioProcesJbpmKey;
		public DadesCacheTasca(
				Long entornId,
				String titol,
				boolean tramitacioMassiva,
				String definicioProcesJbpmKey) {
			this.entornId = entornId;
			this.titol = titol;
			this.tramitacioMassiva = tramitacioMassiva;
			this.definicioProcesJbpmKey = definicioProcesJbpmKey;
		}
		public Long getEntornId() {
			return entornId;
		}
		public String getTitol() {
			return titol;
		}
		public boolean isTramitacioMassiva() {
			return tramitacioMassiva;
		}
		public String getDefinicioProcesJbpmKey() {
			return definicioProcesJbpmKey;
		}
	}
	/*private class DadesTascaOrdenacio {
		private String taskId;
		private String titol;
		private String expedientTitol;
		private String expedientTipusNom;
		private Date dataCreacio;
		private int prioritat;
		private Date dataLimit;
		public DadesTascaOrdenacio(
				String taskId,
				String titol,
				String expedientTitol,
				String expedientTipusNom,
				Date dataCreacio,
				int prioritat,
				Date dataLimit) {
			this.taskId = taskId;
			this.titol = titol;
			this.expedientTitol = expedientTitol;
			this.expedientTipusNom = expedientTipusNom;
			this.dataCreacio = dataCreacio;
			this.prioritat = prioritat;
			this.dataLimit = dataLimit;
		}
		public String getTaskId() {
			return taskId;
		}
		public String getTitol() {
			return titol;
		}
		public String getExpedientTitol() {
			return expedientTitol;
		}
		public String getExpedientTipusNom() {
			return expedientTipusNom;
		}
		public Date getDataCreacio() {
			return dataCreacio;
		}
		public int getPrioritat() {
			return prioritat;
		}
		public Date getDataLimit() {
			return dataLimit;
		}
	}*/

	private static final Log logger = LogFactory.getLog(TascaService.class);

}
