/**
 * 
 */
package net.conselldemallorca.helium.core.model.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.conselldemallorca.helium.core.extern.domini.FilaResultat;
import net.conselldemallorca.helium.core.model.dao.AlertaDao;
import net.conselldemallorca.helium.core.model.dao.CampDao;
import net.conselldemallorca.helium.core.model.dao.CampTascaDao;
import net.conselldemallorca.helium.core.model.dao.ConsultaCampDao;
import net.conselldemallorca.helium.core.model.dao.DefinicioProcesDao;
import net.conselldemallorca.helium.core.model.dao.ExpedientDao;
import net.conselldemallorca.helium.core.model.dao.ExpedientTipusDao;
import net.conselldemallorca.helium.core.model.dao.FormulariExternDao;
import net.conselldemallorca.helium.core.model.dao.LuceneDao;
import net.conselldemallorca.helium.core.model.dao.PluginPersonaDao;
import net.conselldemallorca.helium.core.model.dao.RegistreDao;
import net.conselldemallorca.helium.core.model.dao.TascaDao;
import net.conselldemallorca.helium.core.model.dao.TerminiIniciatDao;
import net.conselldemallorca.helium.core.model.dto.PaginaLlistatDto;
import net.conselldemallorca.helium.core.model.dto.TascaDto;
import net.conselldemallorca.helium.core.model.dto.TascaLlistatDto;
import net.conselldemallorca.helium.core.model.exception.DominiException;
import net.conselldemallorca.helium.core.model.exception.IllegalStateException;
import net.conselldemallorca.helium.core.model.exception.NotFoundException;
import net.conselldemallorca.helium.core.model.hibernate.Alerta;
import net.conselldemallorca.helium.core.model.hibernate.Camp.TipusCamp;
import net.conselldemallorca.helium.core.model.hibernate.Camp;
import net.conselldemallorca.helium.core.model.hibernate.CampTasca;
import net.conselldemallorca.helium.core.model.hibernate.DefinicioProces;
import net.conselldemallorca.helium.core.model.hibernate.DocumentTasca;
import net.conselldemallorca.helium.core.model.hibernate.Expedient;
import net.conselldemallorca.helium.core.model.hibernate.ExpedientLog;
import net.conselldemallorca.helium.core.model.hibernate.ExpedientLog.ExpedientLogAccioTipus;
import net.conselldemallorca.helium.core.model.hibernate.ExpedientTipus;
import net.conselldemallorca.helium.core.model.hibernate.FirmaTasca;
import net.conselldemallorca.helium.core.model.hibernate.FormulariExtern;
import net.conselldemallorca.helium.core.model.hibernate.Tasca;
import net.conselldemallorca.helium.core.model.hibernate.TerminiIniciat;
import net.conselldemallorca.helium.core.security.acl.AclServiceDao;
import net.conselldemallorca.helium.jbpm3.integracio.DelegationInfo;
import net.conselldemallorca.helium.jbpm3.integracio.DominiCodiDescripcio;
import net.conselldemallorca.helium.jbpm3.integracio.JbpmDao;
import net.conselldemallorca.helium.jbpm3.integracio.JbpmProcessInstance;
import net.conselldemallorca.helium.jbpm3.integracio.JbpmTask;

import org.apache.commons.collections.comparators.NullComparator;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.Authentication;
import org.springframework.security.context.SecurityContextHolder;
import org.springframework.stereotype.Service;


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

	private ExpedientDao expedientDao;
	private ExpedientTipusDao expedientTipusDao;
	private TascaDao tascaDao;
	private DefinicioProcesDao definicioProcesDao;
	private JbpmDao jbpmDao;
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
	private ExpedientLogHelper expedientLogHelper;

	private Map<String, Map<String, Object>> dadesFormulariExternInicial;



	public TascaDto getById(
			Long entornId,
			String taskId,
			String usuari,
			Map<String, Object> valorsCommand,
			boolean ambVariables,
			boolean ambTexts) {
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

	public List<TascaLlistatDto> findTasquesPersonalsIndex(Long entornId) {
		return findTasquesPersonalsTramitacio(entornId, null, false);
	}
	public List<TascaLlistatDto> findTasquesPersonalsTramitacio(
			Long entornId,
			String usuari,
			boolean perTramitacio) {
		String usuariBo = usuari;
		if (usuariBo == null) {
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			usuariBo = auth.getName();
		}
		List<JbpmTask> tasques = jbpmDao.findPersonalTasks(usuariBo);
		return tasquesFiltradesPerEntorn(entornId, tasques, perTramitacio);
	}

	public int countTasquesPersonalsEntorn(
			Long entornId,
			String usuari) {
		//MesurarTemps.diferenciaReiniciar("LT_PERSONA_CNT");
		String usuariBo = usuari;
		if (usuariBo == null) {
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			usuariBo = auth.getName();
		}
		int count = 0;
		//MesurarTemps.diferenciaImprimirStdoutIReiniciar("LT_PERSONA_CNT", "1");
		List<JbpmTask> tasques = jbpmDao.findPersonalTasks(usuariBo);
		if (tasques != null) {
			for (JbpmTask task: tasques) {
				Long currentEntornId = getDadesCacheTasca(task).getEntornId();
				if (currentEntornId != null && entornId.equals(currentEntornId))
					count++;
			}
		}
		//MesurarTemps.diferenciaImprimirStdoutIReiniciar("LT_PERSONA_CNT", "2");
		return count;
	}
	public PaginaLlistatDto findTasquesPersonalsFiltre(
			Long entornId,
			String usuari,
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
		//MesurarTemps.diferenciaReiniciar("LT_PERSONA_FLT");
		String usuariBo = usuari;
		if (usuariBo == null) {
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			usuariBo = auth.getName();
		}
		List<JbpmTask> tasques = jbpmDao.findPersonalTasks(usuariBo);
		//MesurarTemps.diferenciaImprimirStdoutIReiniciar("LT_PERSONA_FLT", "1");
		PaginaLlistatDto resposta = tasquesLlistatFiltradesValors(
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
				asc);
		//MesurarTemps.diferenciaImprimirStdoutIReiniciar("LT_PERSONA_FLT", "2");
		return resposta;
	}

	public int countTasquesGrupEntorn(
			Long entornId,
			String usuari) {
		//MesurarTemps.diferenciaReiniciar("LT_GRUP_CNT");
		String usuariBo = usuari;
		if (usuariBo == null) {
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			usuariBo = auth.getName();
		}
		int count = 0;
		//MesurarTemps.diferenciaImprimirStdoutIReiniciar("LT_GRUP_CNT", "1");
		List<JbpmTask> tasques = jbpmDao.findGroupTasks(usuariBo);
		if (tasques != null) {
			for (JbpmTask task: tasques) {
				Long currentEntornId = getDadesCacheTasca(task).getEntornId();
				if (currentEntornId != null && entornId.equals(currentEntornId))
					count++;
			}
		}
		//MesurarTemps.diferenciaImprimirStdoutIReiniciar("LT_GRUP_CNT", "2");
		return count;
	}
	public PaginaLlistatDto findTasquesGrupFiltre(
			Long entornId,
			String usuari,
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
		String usuariBo = usuari;
		if (usuariBo == null) {
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			usuariBo = auth.getName();
		}
		List<JbpmTask> tasques = jbpmDao.findGroupTasks(usuariBo);
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
				asc);
	}

	public List<TascaLlistatDto> findTasquesPerTramitacioMassiva(
			Long entornId,
			String usuari,
			String taskId) {
		String usuariBo = usuari;
		if (usuariBo == null) {
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			usuariBo = auth.getName();
		}
		List<JbpmTask> tasques = jbpmDao.findPersonalTasks(usuariBo);
		JbpmTask task = comprovarSeguretatTasca(entornId, taskId, usuari, true);
		TascaDto tasca = toTascaDto(task, null, true, true);
		String codi = task.getName();
		String jbpmKey = tasca.getDefinicioProces().getJbpmKey();
		List<TascaLlistatDto> resposta = tasquesPerTramitacioMasiva(
				entornId,
				tasques, 
				codi,
				jbpmKey);
		return resposta;
	}

	public List<TascaLlistatDto> findTasquesGrupIndex(Long entornId) {
		return findTasquesGrupTramitacio(entornId, null, false);
	}
	public List<TascaLlistatDto> findTasquesGrupTramitacio(
			Long entornId,
			String usuari,
			boolean perTramitacio) {
		String usuariBo = usuari;
		if (usuariBo == null) {
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			usuariBo = auth.getName();
		}
		List<JbpmTask> tasques = jbpmDao.findGroupTasks(usuariBo);
		return tasquesFiltradesPerEntorn(entornId, tasques, perTramitacio);
	}

	public TascaDto agafar(Long entornId, String taskId) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		return agafar(entornId, auth.getName(), taskId);
	}
	public TascaDto agafar(Long entornId, String usuari, String taskId) {
		JbpmTask task = comprovarSeguretatTasca(entornId, taskId, null, false);
		String previousActors = expedientLogHelper.getActorsPerReassignacioTasca(taskId);
		ExpedientLog expedientLog = expedientLogHelper.afegirLogExpedientPerTasca(
				taskId,
				ExpedientLogAccioTipus.TASCA_REASSIGNAR,
				previousActors);
		jbpmDao.takeTaskInstance(taskId, usuari);
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
	public TascaDto guardarVariable(
			Long entornId,
			String taskId,
			String variable,
			Object valor) {
		Map<String, Object> variables = new HashMap<String, Object>();
		variables.put(variable, valor);
		return guardarVariables(entornId, taskId, variables, null);
	}
	public TascaDto guardarVariables(
			Long entornId,
			String taskId,
			Map<String, Object> variables,
			String usuari) {
		JbpmTask task = comprovarSeguretatTasca(entornId, taskId, usuari, true);
		expedientLogHelper.afegirLogExpedientPerTasca(
				taskId,
				ExpedientLogAccioTipus.TASCA_FORM_GUARDAR,
				null);
		boolean iniciada = task.getStartTime() == null;
		optimitzarConsultesDomini(task, variables);
		jbpmDao.startTaskInstance(taskId);
		jbpmDao.setTaskInstanceVariables(taskId, variables, false);
		TascaDto tasca = toTascaDto(task, null, true, true);
		if (iniciada) {
			registreDao.crearRegistreModificarTasca(
					tasca.getExpedient().getId(),
					taskId,
					SecurityContextHolder.getContext().getAuthentication().getName(),
					"Iniciar tasca \"" + tasca.getNom() + "\"");
		}
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
				-1);
	}
	public void guardarRegistre(
			Long entornId,
			String taskId,
			String campCodi,
			Object[] valors,
			int index) {
		JbpmTask task = jbpmDao.getTaskById(taskId);
		DefinicioProces definicioProces = definicioProcesDao.findAmbJbpmId(task.getProcessDefinitionId());
		Camp camp = campDao.findAmbDefinicioProcesICodi(definicioProces.getId(), campCodi);
		if (camp.isMultiple()) {
			Object valor = jbpmDao.getTaskInstanceVariable(taskId, campCodi);
			if (valor == null) {
				guardarVariable(
						entornId,
						taskId,
						campCodi,
						new Object[]{valors});
			} else {
				Object[] valorMultiple = (Object[])valor;
				if (index != -1) {
					valorMultiple[index] = valors;
					guardarVariable(
							entornId,
							taskId,
							campCodi,
							valor);
				} else {
					Object[] valorNou = new Object[valorMultiple.length + 1];
					for (int i = 0; i < valorMultiple.length; i++)
						valorNou[i] = valorMultiple[i];
					valorNou[valorMultiple.length] = valors;
					guardarVariable(
							entornId,
							taskId,
							campCodi,
							valorNou);
				}
			}
		} else {
			guardarVariable(
					entornId,
					taskId,
					campCodi,
					valors);
		}
	}
	public void esborrarRegistre(
			Long entornId,
			String taskId,
			String campCodi,
			int index) {
		JbpmTask task = jbpmDao.getTaskById(taskId);
		DefinicioProces definicioProces = definicioProcesDao.findAmbJbpmId(task.getProcessDefinitionId());
		Camp camp = campDao.findAmbDefinicioProcesICodi(definicioProces.getId(), campCodi);
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
							valorNou);
				}
			}
		} else {
			guardarVariable(
					entornId,
					taskId,
					campCodi,
					null);
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
			String usuari) {
		JbpmTask task = comprovarSeguretatTasca(entornId, taskId, usuari, comprovarAssignacio);
		expedientLogHelper.afegirLogExpedientPerTasca(
				taskId,
				ExpedientLogAccioTipus.TASCA_FORM_VALIDAR,
				null);
		optimitzarConsultesDomini(task, variables);
		jbpmDao.startTaskInstance(taskId);
		jbpmDao.setTaskInstanceVariables(taskId, variables, false);
		validarTasca(taskId);
		TascaDto tasca = toTascaDto(task, null, true, true);
		registreDao.crearRegistreModificarTasca(
				tasca.getExpedient().getId(),
				taskId,
				SecurityContextHolder.getContext().getAuthentication().getName(),
				"Validar \"" + tasca.getNom() + "\"");
		return tasca;
	}
	public TascaDto restaurar(
			Long entornId,
			String taskId) {
		JbpmTask task = comprovarSeguretatTasca(entornId, taskId, null, true);
		expedientLogHelper.afegirLogExpedientPerTasca(
				taskId,
				ExpedientLogAccioTipus.TASCA_FORM_RESTAURAR,
				null);
		if (!isTascaValidada(task))
			throw new IllegalStateException(
					getServiceUtils().getMessage("error.tascaService.noValidada"));
		//deleteDocumentsTasca(taskId);
		restaurarTasca(taskId);
		TascaDto tasca = toTascaDto(task, null, true, true);
		registreDao.crearRegistreModificarTasca(
				tasca.getExpedient().getId(),
				taskId,
				SecurityContextHolder.getContext().getAuthentication().getName(),
				"Restaurar \"" + tasca.getNom() + "\"");
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
			String outcome) {
		JbpmTask task = comprovarSeguretatTasca(entornId, taskId, usuari, comprovarAssignacio);
		if (!isTascaValidada(task))
			throw new IllegalStateException(
					getServiceUtils().getMessage("error.tascaService.formNoValidat"));
		if (!isDocumentsComplet(task))
			throw new IllegalStateException(
					getServiceUtils().getMessage("error.tascaService.faltenAdjuntar"));
		if (!isSignaturesComplet(task))
			throw new IllegalStateException(
					getServiceUtils().getMessage("error.tascaService.faltenSignar"));
		expedientLogHelper.afegirLogExpedientPerTasca(
				taskId,
				ExpedientLogAccioTipus.TASCA_COMPLETAR,
				outcome);
		jbpmDao.startTaskInstance(taskId);
		jbpmDao.endTaskInstance(task.getId(), outcome);
		// Accions per a una tasca delegada
		DelegationInfo delegationInfo = getDelegationInfo(task);
		if (delegationInfo != null) {
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
		}
		JbpmProcessInstance pi = jbpmDao.getRootProcessInstance(task.getProcessInstanceId());
		Expedient expedient = expedientDao.findAmbProcessInstanceId(pi.getId());
		actualitzarTerminisIAlertes(taskId, expedient);
		actualitzarDataFiExpedient(expedient, pi);
		getServiceUtils().expedientIndexLuceneUpdate(task.getProcessInstanceId());
		TascaDto tasca = toTascaDto(task, null, true, true);
		registreDao.crearRegistreFinalitzarTasca(
				tasca.getExpedient().getId(),
				taskId,
				SecurityContextHolder.getContext().getAuthentication().getName(),
				"Finalitzar \"" + tasca.getNom() + "\"");
	}

	public Object getVariable(
			Long entornId,
			String taskId,
			String codiVariable) {
		JbpmTask task = comprovarSeguretatTasca(entornId, taskId, null, true);
		return getServiceUtils().getVariableJbpmTascaValor(task.getId(), codiVariable);
	}
	public void createVariable(
			Long entornId,
			String taskId,
			String codiVariable,
			Object valor) {
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
			String user) {
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
			String codiVariable) {
		JbpmTask task = comprovarSeguretatTasca(entornId, taskId, null, true);
		jbpmDao.deleteTaskInstanceVariable(task.getId(), codiVariable);
		TascaDto tasca = toTascaDto(task, null, true, true);
		registreDao.crearRegistreEsborrarVariableTasca(
				tasca.getExpedient().getId(),
				taskId,
				SecurityContextHolder.getContext().getAuthentication().getName(),
				codiVariable);
	}

	/*public DocumentDto generarDocumentPlantilla(
			Long entornId,
			Long documentId,
			String taskId,
			Date dataDocument) {
		Document document = documentDao.getById(documentId, false);
		DocumentDto resposta = new DocumentDto();
		resposta.setDataCreacio(new Date());
		resposta.setDataDocument(new Date());
		resposta.setArxiuNom(document.getNom() + ".odt");
		resposta.setAdjuntarAuto(document.isAdjuntarAuto());
		JbpmTask task = comprovarSeguretatTasca(entornId, taskId, null, true);
		if (document.isPlantilla()) {
			JbpmProcessInstance rootProcessInstance = jbpmDao.getRootProcessInstance(task.getProcessInstanceId());
			ExpedientDto expedient = dtoConverter.toExpedientDto(
					expedientDao.findAmbProcessInstanceId(rootProcessInstance.getId()),
					false);
			TascaDto tasca = toTascaDto(task, null, true, true);
			InstanciaProcesDto instanciaProces = dtoConverter.toInstanciaProcesDto(
					task.getProcessInstanceId(),
					true);
			Map<String, Object> model = new HashMap<String, Object>();
			model.putAll(instanciaProces.getVarsComText());
			model.putAll(tasca.getVarsComText());
			try {
				byte[] resultat = plantillaDocumentDao.generarDocumentAmbPlantilla(
						entornId,
						document,
						task.getAssignee(),
						expedient,
						task.getProcessInstanceId(),
						tasca,
						dataDocument,
						model);
				if (isActiuConversioVista()) {
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					getOpenOfficeUtils().convertir(
							resposta.getArxiuNom(),
							resultat,
							getExtensioVista(document),
							baos);
					resposta.setArxiuNom(
							nomArxiuAmbExtensio(
									resposta.getArxiuNom(),
									getExtensioVista(document)));
					resposta.setArxiuContingut(baos.toByteArray());
				} else {
					resposta.setArxiuContingut(resultat);
				}
				if (document.isAdjuntarAuto()) {
					documentHelper.actualitzarDocument(
							taskId,
							null,
							document.getCodi(),
							null,
							dataDocument,
							resposta.getArxiuNom(),
							resposta.getArxiuContingut(),
							false);
				}
			} catch (Exception ex) {
				throw new TemplateException(
						getServiceUtils().getMessage("error.tascaService.generarDocument"), ex);
			}
		} else {
			resposta.setArxiuContingut(document.getArxiuContingut());
		}
		return resposta;
	}*/

	public void delegacioCrear(
			Long entornId,
			String taskId,
			String actorId,
			String comentari,
			boolean supervisada) {
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
			String taskId) {
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
			String taskId) {
		JbpmTask task = comprovarSeguretatTasca(entornId, taskId, null, true);
		Tasca tasca = tascaDao.findAmbActivityNameIProcessDefinitionId(
				task.getName(),
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
		Tasca tasca = tascaDao.findAmbActivityNameIDefinicioProces(
				startTaskName,
				definicioProces.getId());
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
						task.getName(),
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
			String textInicial) throws DominiException {
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
			String accio) throws DominiException {
		expedientLogHelper.afegirLogExpedientPerTasca(
				taskId,
				ExpedientLogAccioTipus.TASCA_ACCIO_EXECUTAR,
				accio);
		JbpmTask task = comprovarSeguretatTasca(entornId, taskId, null, true);
		jbpmDao.executeActionInstanciaTasca(taskId, accio);
		getServiceUtils().expedientIndexLuceneUpdate(task.getProcessInstanceId());
	}

	public void comprovarTascaAssignadaIValidada(
			Long entornId,
			String taskInstanceId,
			String usuari) {
		JbpmTask task = comprovarSeguretatTasca(entornId, taskInstanceId, usuari, true);
		if (!isTascaValidada(task))
			throw new IllegalStateException(
					getServiceUtils().getMessage("error.tascaService.noValidada"));
	}

	/*public Integer getTotalTasquesPersona(Long entornId) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String usuariBo = auth.getName();
		List<JbpmTask> tasques = jbpmDao.findPersonalTasks(usuariBo);
		Integer total = 0;
		for (JbpmTask task: tasques) {
			Long currentEntornId = getDadesCacheTasca(task).getEntornId();
			if (currentEntornId != null && entornId.equals(currentEntornId))
				total++;
		}
		return total;
	}
	public Integer getTotalTasquesGrup(Long entornId) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String usuariBo = auth.getName();
		List<JbpmTask> tasques = jbpmDao.findGroupTasks(usuariBo);
		Integer total = 0;
		for (JbpmTask task: tasques) {
			Long currentEntornId = getDadesCacheTasca(task).getEntornId();;
			if (currentEntornId != null && entornId.equals(currentEntornId))
				total++;
		}
		return total;
	}*/

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
	public void setJbpmDao(JbpmDao jbpmDao) {
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



	private JbpmTask comprovarSeguretatTasca(Long entornId, String taskId, String usuari, boolean comprovarAssignacio) {
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
		if (task.isSuspended()) {
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
	private List<TascaLlistatDto> tasquesFiltradesPerEntorn(
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
	}
	private PaginaLlistatDto tasquesLlistatFiltradesValors(
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
									getDadesCacheTasca(task)));
					break;
				}
			}
		}
		//MesurarTemps.diferenciaImprimirStdoutIReiniciar("LT_TASQUES_FLT", "4");
		return new PaginaLlistatDto(
				filtrades.size(),
				respostaLlistat);
	}
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
				TascaLlistatDto dto = toTascaLlistatDto(task, dadesCacheTasca);
				if (codi.equals(task.getName()) && jbpmKey.equals(dadesCacheTasca.getDefinicioProcesJbpmKey())) {
					resposta.add(dto);
				}
			}
		}
		return resposta;
	}

	private String normalitzaText(String text) {
		return text
			.toUpperCase()
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
	}

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
				task.getName(),
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
				task.getName(),
				task.getProcessDefinitionId());
		for (DocumentTasca docTasca: tasca.getDocuments()) {
			if (docTasca.isRequired()) {
				String codiJbpm = DocumentHelper.PREFIX_VAR_DOCUMENT + docTasca.getDocument().getCodi();
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
				task.getName(),
				task.getProcessDefinitionId());
		for (FirmaTasca firmaTasca: tasca.getFirmes()) {
			if (firmaTasca.isRequired()) {
				String codiJbpm = DocumentHelper.PREFIX_SIGNATURA + firmaTasca.getDocument().getCodi();
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
			DadesCacheTasca dadesCacheTasca) {
		TascaLlistatDto dto = new TascaLlistatDto();
		dto.setId(task.getId());
		dto.setCodi(task.getName());
		dto.setTitol(task.getName());
		dto.setDataCreacio(task.getCreateTime());
		dto.setDataInici(task.getStartTime());
		dto.setDataFi(task.getEndTime());
		dto.setDataLimit(task.getDueDate());
		dto.setPrioritat(task.getPriority());
		dto.setResponsable(task.getAssignee());
		dto.setResponsables(task.getPooledActors());
		dto.setOberta(task.isOpen());
		dto.setCompletada(task.isCompleted());
		dto.setCancelada(task.isCancelled());
		dto.setSuspesa(task.isSuspended());
		dto.setProcessInstanceId(task.getProcessInstanceId());
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
		if (dadesCacheTasca != null) {
			dto.setTitol(dadesCacheTasca.getTitol());
			dto.setExpedientTitol(dadesCacheTasca.getIdentificador());
			dto.setExpedientTitolOrdenacio(dadesCacheTasca.getIdentificadorOrdenacio());
			dto.setExpedientTipusId(dadesCacheTasca.getExpedientTipusId());
			dto.setExpedientTipusNom(dadesCacheTasca.getExpedientTipusNom());
			dto.setExpedientProcessInstanceId(dadesCacheTasca.getProcessInstanceId());
			dto.setTramitacioMassiva(dadesCacheTasca.isTramitacioMassiva());
		}
		return dto;
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
				terminiIniciat.getTermini().getDefinicioProces().getEntorn());
		alerta.setExpedient(expedient);
		alerta.setTerminiIniciat(terminiIniciat);
		alertaDao.saveOrUpdate(alerta);
	}
	private void esborrarAlertesAntigues(TerminiIniciat terminiIniciat) {
		List<Alerta> antigues = alertaDao.findActivesAmbTerminiIniciatId(terminiIniciat.getId());
		for (Alerta antiga: antigues)
			antiga.setDataEliminacio(new Date());
	}

	private void actualitzarDataFiExpedient(
			Expedient expedient,
			JbpmProcessInstance pi) {
		if (pi.getEnd() != null)
			expedient.setDataFi(pi.getEnd());
	}

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
					messageSource);
		}
		return serviceUtils;
	}

	private void optimitzarConsultesDomini(
			JbpmTask task,
			Map<String, Object> variables) {
		Tasca tasca = tascaDao.findAmbActivityNameIProcessDefinitionId(
				task.getName(),
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
			String rootProcessInstanceId = jbpmDao.getRootProcessInstance(task.getProcessInstanceId()).getId();
			Expedient expedientPerTasca = expedientDao.findAmbProcessInstanceId(rootProcessInstanceId);
			Tasca tasca = tascaDao.findAmbActivityNameIProcessDefinitionId(
					task.getName(),
					task.getProcessDefinitionId());
			String titol = tasca.getNom();
			if (tasca.getNomScript() != null && tasca.getNomScript().length() > 0)
				titol = dtoConverter.getTitolPerTasca(task, tasca);
			task.setFieldFromDescription(
					"entornId",
					expedientPerTasca.getEntorn().getId().toString());
			task.setFieldFromDescription(
					"titol",
					titol);
			task.setFieldFromDescription(
					"identificador",
					expedientPerTasca.getIdentificador());
			task.setFieldFromDescription(
					"identificadorOrdenacio",
					expedientPerTasca.getIdentificadorOrdenacio());
			task.setFieldFromDescription(
					"numeroIdentificador",
					expedientPerTasca.getNumeroIdentificador());
			task.setFieldFromDescription(
					"expedientTipusId",
					expedientPerTasca.getTipus().getId().toString());
			task.setFieldFromDescription(
					"expedientTipusNom",
					expedientPerTasca.getTipus().getNom());
			task.setFieldFromDescription(
					"processInstanceId",
					expedientPerTasca.getProcessInstanceId());
			task.setFieldFromDescription(
					"tramitacioMassiva",
					new Boolean(tasca.isTramitacioMassiva()).toString());
			task.setFieldFromDescription(
					"definicioProcesJbpmKey",
					tasca.getDefinicioProces().getJbpmKey());
			task.setCacheActiu();
			jbpmDao.describeTaskInstance(
					task.getId(),
					task.getDescriptionWithFields());
		}
		dadesCache = new DadesCacheTasca(
				new Long(task.getFieldFromDescription("entornId")),
				task.getFieldFromDescription("titol"),
				task.getFieldFromDescription("identificador"),
				task.getFieldFromDescription("identificadorOrdenacio"),
				task.getFieldFromDescription("numeroIdentificador"),
				new Long(task.getFieldFromDescription("expedientTipusId")),
				task.getFieldFromDescription("expedientTipusNom"),
				task.getFieldFromDescription("processInstanceId"),
				new Boolean(task.getFieldFromDescription("tramitacioMassiva")).booleanValue(),
				task.getFieldFromDescription("definicioProcesJbpmKey"));
		return dadesCache;
	}
	private class DadesCacheTasca {
		private Long entornId;
		private String titol;
		private String identificador;
		private String identificadorOrdenacio;
		private String numeroIdentificador;
		private Long expedientTipusId;
		private String expedientTipusNom;
		private String processInstanceId;
		private boolean tramitacioMassiva;
		private String definicioProcesJbpmKey;
		public DadesCacheTasca(
				Long entornId,
				String titol,
				String identificador,
				String identificadorOrdenacio,
				String numeroIdentificador,
				Long expedientTipusId,
				String expedientTipusNom,
				String processInstanceId,
				boolean tramitacioMassiva,
				String definicioProcesJbpmKey) {
			this.entornId = entornId;
			this.titol = titol;
			this.identificador = identificador;
			this.identificadorOrdenacio = identificadorOrdenacio;
			this.numeroIdentificador = numeroIdentificador;
			this.expedientTipusId = expedientTipusId;
			this.expedientTipusNom = expedientTipusNom;
			this.processInstanceId = processInstanceId;
			this.tramitacioMassiva = tramitacioMassiva;
			this.definicioProcesJbpmKey = definicioProcesJbpmKey;
		}
		public Long getEntornId() {
			return entornId;
		}
		public String getTitol() {
			return titol;
		}
		public String getIdentificador() {
			return identificador;
		}
		public String getIdentificadorOrdenacio() {
			return identificadorOrdenacio;
		}
		public String getNumeroIdentificador() {
			return numeroIdentificador;
		}
		public Long getExpedientTipusId() {
			return expedientTipusId;
		}
		public String getExpedientTipusNom() {
			return expedientTipusNom;
		}
		public String getProcessInstanceId() {
			return processInstanceId;
		}
		public boolean isTramitacioMassiva() {
			return tramitacioMassiva;
		}
		public String getDefinicioProcesJbpmKey() {
			return definicioProcesJbpmKey;
		}
	}
	private class DadesTascaOrdenacio {
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
	}

	private static final Log logger = LogFactory.getLog(TascaService.class);

}
