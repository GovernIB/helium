/**
 * 
 */
package net.conselldemallorca.helium.core.helper;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jbpm.bytes.ByteArray;
import org.jbpm.context.exe.ContextInstance;
import org.jbpm.context.exe.VariableInstance;
import org.jbpm.context.log.VariableCreateLog;
import org.jbpm.context.log.VariableDeleteLog;
import org.jbpm.context.log.VariableLog;
import org.jbpm.context.log.VariableUpdateLog;
import org.jbpm.graph.def.Node;
import org.jbpm.graph.def.Node.NodeType;
import org.jbpm.graph.exe.ProcessInstance;
import org.jbpm.graph.exe.Token;
import org.jbpm.graph.log.ActionLog;
import org.jbpm.graph.log.NodeLog;
import org.jbpm.graph.log.ProcessInstanceCreateLog;
import org.jbpm.graph.log.ProcessInstanceEndLog;
import org.jbpm.graph.log.TokenCreateLog;
import org.jbpm.graph.log.TokenEndLog;
import org.jbpm.graph.log.TransitionLog;
import org.jbpm.logging.log.MessageLog;
import org.jbpm.logging.log.ProcessLog;
import org.jbpm.taskmgmt.exe.TaskInstance;
import org.jbpm.taskmgmt.log.TaskAssignLog;
import org.jbpm.taskmgmt.log.TaskCreateLog;
import org.jbpm.taskmgmt.log.TaskEndLog;
import org.jbpm.taskmgmt.log.TaskLog;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import net.conselldemallorca.helium.core.api.WProcessInstance;
import net.conselldemallorca.helium.core.api.WTaskInstance;
import net.conselldemallorca.helium.core.api.WToken;
import net.conselldemallorca.helium.core.api.WorkflowRetroaccioApi;
import net.conselldemallorca.helium.core.common.JbpmVars;
import net.conselldemallorca.helium.core.helperv26.MesuresTemporalsHelper;
import net.conselldemallorca.helium.core.model.hibernate.Camp;
import net.conselldemallorca.helium.core.model.hibernate.Camp.TipusCamp;
import net.conselldemallorca.helium.core.model.hibernate.CampTasca;
import net.conselldemallorca.helium.core.model.hibernate.DefinicioProces;
import net.conselldemallorca.helium.core.model.hibernate.DocumentTasca;
import net.conselldemallorca.helium.core.model.hibernate.Estat;
import net.conselldemallorca.helium.core.model.hibernate.Expedient;
import net.conselldemallorca.helium.core.model.hibernate.ExpedientLog;
import net.conselldemallorca.helium.core.model.hibernate.ExpedientTipus;
import net.conselldemallorca.helium.core.model.hibernate.Tasca;
import net.conselldemallorca.helium.jbpm3.handlers.BasicActionHandler;
import net.conselldemallorca.helium.jbpm3.integracio.JbpmHelper;
import net.conselldemallorca.helium.jbpm3.integracio.JbpmTask;
import net.conselldemallorca.helium.jbpm3.integracio.JbpmToken;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientTascaDto;
import net.conselldemallorca.helium.v3.core.api.dto.InformacioRetroaccioDto;
import net.conselldemallorca.helium.v3.core.api.dto.InstanciaProcesDto;
import net.conselldemallorca.helium.v3.core.api.dto.LogObjectDto;
import net.conselldemallorca.helium.v3.core.api.exception.NoTrobatException;
import net.conselldemallorca.helium.v3.core.api.exception.PermisDenegatException;
import net.conselldemallorca.helium.v3.core.repository.CampRepository;
import net.conselldemallorca.helium.v3.core.repository.CampTascaRepository;
import net.conselldemallorca.helium.v3.core.repository.DefinicioProcesRepository;
import net.conselldemallorca.helium.v3.core.repository.EstatRepository;
import net.conselldemallorca.helium.v3.core.repository.ExpedientLoggerRepository;
import net.conselldemallorca.helium.v3.core.repository.ExpedientRepository;
import net.conselldemallorca.helium.v3.core.repository.TascaRepository;

/**
 * Helper per a gestionar els logs dels expedients
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class RetroaccioHelper implements WorkflowRetroaccioApi {

	public static final String MESSAGE_LOG_PREFIX = "[H3l1um]";
	private static final String MESSAGE_LOGINFO_PREFIX = "[H3l1nf0]";

	@Resource
	private ExpedientRepository expedientRepository;
	@Resource
	private ExpedientLoggerRepository expedientLoggerRepository;
	@Resource
	private TascaRepository tascaRepository;
	@Resource
	private DefinicioProcesRepository definicioProcesRepository;
	@Resource
	private EstatRepository estatRepository;
	@Resource
	private CampRepository campRepository;
	@Resource
	private CampTascaRepository campTascaRepository;

	@Resource
	private ExpedientHelper expedientHelper;
	@Resource
	private TascaHelper tascaHelper;
	@Resource
	private ConversioTipusHelper conversioTipusHelper;
	@Resource
	private IndexHelper indexHelper;
	@Resource
	private JbpmHelper jbpmHelper;
	@Resource
	private MesuresTemporalsHelper mesuresTemporalsHelper;
	@Resource(name="documentHelperV3")
	private DocumentHelperV3 documentHelper;
	@Resource
	private ExpedientRegistreHelper expedientRegistreHelper;

	
	public SortedSet<Entry<InstanciaProcesDto, List<InformacioRetroaccioDto>>> findInformacioRetroaccioExpedientOrdenatPerData(
			Long expedientId,
			String instanciaProcesId,
			boolean detall) {

		Map<InstanciaProcesDto, List<InformacioRetroaccioDto>> resposta = new HashMap<InstanciaProcesDto, List<InformacioRetroaccioDto>>();
		List<InstanciaProcesDto> arbre = expedientHelper.getArbreInstanciesProces(instanciaProcesId);
		List<ExpedientLog> logs = expedientLoggerRepository.findAmbExpedientIdOrdenatsPerData(expedientId);
		List<String> taskIds = new ArrayList<String>();
		String parentProcessInstanceId = null;
		Map<String, String> processos = new HashMap<String, String>();
		for (InstanciaProcesDto ip: arbre) {
			resposta.put(ip, new ArrayList<InformacioRetroaccioDto>());
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
										ip.getId(),
										detall));
					}
				}
			}
		}
		SortedSet<Map.Entry<InstanciaProcesDto, List<InformacioRetroaccioDto>>> sortedEntries = new TreeSet<Map.Entry<InstanciaProcesDto, List<InformacioRetroaccioDto>>>(new Comparator<Map.Entry<InstanciaProcesDto, List<InformacioRetroaccioDto>>>() {
			@Override
			public int compare(Map.Entry<InstanciaProcesDto, List<InformacioRetroaccioDto>> e1, Map.Entry<InstanciaProcesDto, List<InformacioRetroaccioDto>> e2) {
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
		return sortedEntries;
	}
	
	public Map<String, ExpedientTascaDto> findTasquesExpedientPerRetroaccio(Long expedientId) {
		List<ExpedientLog> logs = expedientLoggerRepository.findAmbExpedientIdOrdenatsPerData(expedientId);
		Map<String, ExpedientTascaDto> tasquesPerRetroaccio = new HashMap<String, ExpedientTascaDto>();
		for (ExpedientLog log: logs) {
			if (log.isTargetTasca()) {
				WTaskInstance task = jbpmHelper.getTaskById(log.getTargetId());
				if (task != null) {
					tasquesPerRetroaccio.put(
							log.getTargetId(),
							tascaHelper.toExpedientTascaDto(
									task,
									expedientRepository.findOne(expedientId),
									true,
									false));
				}
			}
		}
		return tasquesPerRetroaccio;
	}
	
	public void executaRetroaccio(
//			Long expedientId,
			Long informacioRetroaccioId,
			boolean retrocedirPerTasques) {
		ExpedientLog log = expedientLoggerRepository.findById(informacioRetroaccioId);
		if (log.getExpedient().isAmbRetroaccio()) {
			Long logRetrocesId = afegirInformacioRetroaccioPerExpedient(
					log.getExpedient().getId(),
					retrocedirPerTasques ? ExpedientRetroaccioTipus.EXPEDIENT_RETROCEDIR_TASQUES : ExpedientRetroaccioTipus.EXPEDIENT_RETROCEDIR,
					informacioRetroaccioId.toString());
			retrocedirFinsLog(log, retrocedirPerTasques, logRetrocesId);
			actualitzaEstatInformacioRetroaccio(
					logRetrocesId,
					ExpedientRetroaccioEstat.IGNORAR);
			indexHelper.expedientIndexLuceneUpdate(log.getExpedient().getProcessInstanceId());
		}
	}
	
	public void eliminaInformacioRetroaccio(String processInstanceId) {
		jbpmHelper.deleteProcessInstanceTreeLogs(processInstanceId);
	}
	
	public List<InformacioRetroaccioDto> findInformacioRetroaccioTascaOrdenatPerData(Long informacioRetroaccioId) {
		List<ExpedientLog> logs = expedientLoggerRepository.findLogsTascaByIdOrdenatsPerData(String.valueOf(informacioRetroaccioId));
		return conversioTipusHelper.convertirList(logs, InformacioRetroaccioDto.class);
	}
	
	public List<InformacioRetroaccioDto> findInformacioRetroaccioAccioRetrocesOrdenatsPerData(
			Long informacioRetroaccioId) throws NoTrobatException, PermisDenegatException {
		List<ExpedientLog> logs = findLogsRetrocedits(informacioRetroaccioId);
		return conversioTipusHelper.convertirList(logs, InformacioRetroaccioDto.class);
	}
	
	public InformacioRetroaccioDto findInformacioRetroaccioById(Long informacioRetroaccioId) {
		return conversioTipusHelper.convertir(
				expedientLoggerRepository.findById(informacioRetroaccioId),
				InformacioRetroaccioDto.class);
	}
	
//	public long afegirProcessLogInfoExpedient(
	public Long afegirInformacioRetroaccioPerExpedient(
			boolean ambRetroaccio,
			String processInstanceId,
			String message) {
		if (ambRetroaccio) {
			long jbpmLogId = jbpmHelper.addProcessInstanceMessageLog(
					processInstanceId,
					MESSAGE_LOGINFO_PREFIX + "::" + message);
			return jbpmLogId;
		} else {
			return -1L;
		}
	}
	
	public Long afegirInformacioRetroaccioPerExpedient(
			Long expedientId,
			ExpedientRetroaccioTipus tipus,
			String accioParams) {
		return afegirInformacioRetroaccioPerExpedient(
				expedientId,
				tipus,
				accioParams,
				null);
	}
//	public ExpedientLog afegirLogExpedientPerExpedient(
	public Long afegirInformacioRetroaccioPerExpedient(
			Long expedientId,
			ExpedientRetroaccioTipus tipus,
			String accioParams,
			ExpedientRetroaccioEstat estat) {
		Expedient expedient = expedientRepository.findOne(expedientId);
		String processInstanceId = expedient.getProcessInstanceId();
		String usuari = "Timer";
		try {
			usuari = SecurityContextHolder.getContext().getAuthentication().getName();
		} catch (Exception e) {}
		ExpedientLog expedientLog = new ExpedientLog(
				expedient,
				usuari,
				processInstanceId,
				tipus);
		expedientLog.setProcessInstanceId(new Long(processInstanceId));
		if (accioParams != null)
			expedientLog.setAccioParams(accioParams);
		if (estat != null)
			expedientLog.setEstat(estat);
		expedientLoggerRepository.save(expedientLog);
		return expedientLog.getId();
	}
	
//	public ExpedientLog afegirLogExpedientPerTasca(
	public Long afegirInformacioRetroaccioPerTasca(
			String taskInstanceId,
			ExpedientRetroaccioTipus tipus,
			String accioParams) {
		return afegirInformacioRetroaccioPerTasca(taskInstanceId, null, tipus, accioParams, null);
	}
	
//	public ExpedientLog afegirLogExpedientPerTasca(
	public Long afegirInformacioRetroaccioPerTasca(
			String taskInstanceId,
			ExpedientRetroaccioTipus tipus,
			String accioParams,
			String user) {
		return afegirInformacioRetroaccioPerTasca(taskInstanceId, null, tipus, accioParams, user);
	}

//	public ExpedientLog afegirLogExpedientPerTasca(
	public Long afegirInformacioRetroaccioPerTasca(
			String taskInstanceId,
			Long expedientId,
			ExpedientRetroaccioTipus tipus,
			String accioParams,
			String user) {
		
		WTaskInstance task = jbpmHelper.getTaskById(taskInstanceId);
		Expedient expedient = null;
		if (expedientId == null) {
			expedient = expedientHelper.findExpedientByProcessInstanceId(task.getProcessInstanceId());
		} else {
			expedient = expedientRepository.findOne(expedientId);
		}
		
		Long jbpmLogId = null;
		if (expedient.isAmbRetroaccio()) {
			jbpmLogId = jbpmHelper.addTaskInstanceMessageLog(
					taskInstanceId,
					getMessageLogPerTipus(tipus));
		}
		String usuari = "Timer";
		if (user != null) {
			usuari = user;
		} else {
			try {usuari = SecurityContextHolder.getContext().getAuthentication().getName();}
			catch (Exception e){}
		}
		ExpedientLog expedientLog = new ExpedientLog(
				expedient,
				usuari,
				taskInstanceId,
				tipus);
		expedientLog.setProcessInstanceId(new Long(task.getProcessInstanceId()));
		expedientLog.setJbpmLogId(jbpmLogId);
		if (accioParams != null)
			expedientLog.setAccioParams(accioParams);
		expedientLoggerRepository.save(expedientLog);
		return expedientLog.getId();
	}
	
	public Long afegirInformacioRetroaccioPerProces(
			String processInstanceId,
			ExpedientRetroaccioTipus tipus,
			String accioParams) {
		return afegirInformacioRetroaccioPerProces(
				processInstanceId,
				tipus,
				accioParams,
				null);
	}
//	public Long afegirLogExpedientPerProces(
	public Long afegirInformacioRetroaccioPerProces(
			String processInstanceId,
			ExpedientRetroaccioTipus tipus,
			String accioParams,
			ExpedientRetroaccioEstat estat) {
		
		Expedient expedient = expedientHelper.findExpedientByProcessInstanceId(processInstanceId);
		
		Long jbpmLogId = null;
		if (expedient.isAmbRetroaccio()) {
			jbpmLogId = jbpmHelper.addProcessInstanceMessageLog(
					processInstanceId,
					getMessageLogPerTipus(tipus));
		}
		String usuari = "Timer";
		try {
			usuari = SecurityContextHolder.getContext().getAuthentication().getName();
		}catch (Exception e){}
		ExpedientLog expedientLog = new ExpedientLog(
				expedient,
				usuari,
				processInstanceId,
				tipus);
		expedientLog.setProcessInstanceId(new Long(processInstanceId));
		expedientLog.setJbpmLogId(jbpmLogId);
		if (accioParams != null)
			expedientLog.setAccioParams(accioParams);
		if (estat != null)
			expedientLog.setEstat(estat);
		expedientLoggerRepository.save(expedientLog);
		return expedientLog.getId();
	}
	
	public void actualitzaEstatInformacioRetroaccio(
			Long informacioRetroaccioId,
			ExpedientRetroaccioEstat estat) {
		ExpedientLog expedientLog = expedientLoggerRepository.findOne(informacioRetroaccioId);
		expedientLog.setEstat(estat);
		expedientLoggerRepository.save(expedientLog);
	}
	
	public void actualitzaParametresAccioInformacioRetroaccio(
			Long informacioRetroaccioId,
			String parametresAccio) {
		ExpedientLog expedientLog = expedientLoggerRepository.findOne(informacioRetroaccioId);
		expedientLog.setAccioParams(parametresAccio);
		expedientLoggerRepository.save(expedientLog);
	}
	
//	public List<Object> findLogIdTasquesById(List<String> tasquesIds) {
//		return expedientLoggerRepository.findLogIdTasquesById(tasquesIds);
//	}
	
	private List<ExpedientLog> findLogsRetrocedits(Long expedientLogId) {
		List<ExpedientLog> resposta = new ArrayList<ExpedientLog>();
		ExpedientLog expedientLog = expedientLoggerRepository.findById(expedientLogId);
		if (ExpedientRetroaccioTipus.EXPEDIENT_RETROCEDIR.equals(expedientLog.getAccioTipus()) ||
			ExpedientRetroaccioTipus.EXPEDIENT_RETROCEDIR_TASQUES.equals(expedientLog.getAccioTipus())) {
			List<ExpedientLog> logs = expedientLoggerRepository.findAmbExpedientRetroceditIdOrdenatsPerData(
					expedientLog.getId());
			
			if (logs != null && logs.size() > 0) {
				resposta = logs;
			} else {
				// Aquí no ha d'entrar al retrocedir per tasques, degut a que sempre tindrà la
				// referència al log iniciador del retrocés
				List<ExpedientLog> expedientLogs = expedientLoggerRepository.findAmbExpedientIdOrdenatsPerData(
						expedientLog.getExpedient().getId());
				Long idInicialExclos = null;
				Long idFinalExclos = null;
				for (ExpedientLog elog: expedientLogs) {
					if (elog.getId().equals(expedientLogId))
						break;
					if (ExpedientRetroaccioTipus.EXPEDIENT_RETROCEDIR.equals(elog.getAccioTipus())) {
						idInicialExclos = new Long(elog.getAccioParams());
						idFinalExclos = elog.getId();
					}
				}
				Long idInicial = new Long(expedientLog.getAccioParams());
				for (ExpedientLog elog: expedientLogs) {
					if (elog.getId().equals(expedientLogId))
						break;
					if (elog.getId().longValue() >= idInicial.longValue() && ExpedientRetroaccioEstat.RETROCEDIT.equals(elog.getEstat())) {
						if (idInicialExclos == null || elog.getId().longValue() < idInicialExclos)
							resposta.add(elog);
						else if (idFinalExclos == null || elog.getId().longValue() > idFinalExclos)
							resposta.add(elog);
					}
				}
			}
		}
		return resposta;
	}
	
	private void retrocedirFinsLog(ExpedientLog expedientLog, boolean retrocedirPerTasques, Long iniciadorId) {
		boolean debugRetroces = true;
		
//		ExpedientLog expedientLog = expedientLoggerRepository.getById(expedientLogId, false);
		WTaskInstance jtask = jbpmHelper.getTaskById(expedientLog.getTargetId());
		
		// Variables per a desar la informació per a executar el node enter al final de tot
//		long nodeEnterObjectId = 0;
		Long nodeEnterTokenId = null;
				
		mesuresTemporalsHelper.mesuraIniciar("Retrocedir" + (retrocedirPerTasques ? " per tasques" : ""), "expedient", expedientLog.getExpedient().getTipus().getNom(), null, "findAmbExpedientIdOrdenatsPerData");
		List<ExpedientLog> expedientLogs = expedientLoggerRepository.findAmbExpedientIdOrdenatsPerData(
				expedientLog.getExpedient().getId());
		mesuresTemporalsHelper.mesuraCalcular("Retrocedir" + (retrocedirPerTasques ? " per tasques" : ""), "expedient", expedientLog.getExpedient().getTipus().getNom(), null, "findAmbExpedientIdOrdenatsPerData");
		mesuresTemporalsHelper.mesuraIniciar("Retrocedir" + (retrocedirPerTasques ? " per tasques" : ""), "expedient", expedientLog.getExpedient().getTipus().getNom(), null, "filtraExpedientsLogPerRetrocedir");
		expedientLogs = filtraExpedientsLogPerRetrocedir(expedientLogs, expedientLog.getId(), retrocedirPerTasques);
		mesuresTemporalsHelper.mesuraCalcular("Retrocedir" + (retrocedirPerTasques ? " per tasques" : ""), "expedient", expedientLog.getExpedient().getTipus().getNom(), null, "filtraExpedientsLogPerRetrocedir");

		// Retrocedeix els canvis al jBPM relacionats amb els logs
		mesuresTemporalsHelper.mesuraIniciar("Retrocedir" + (retrocedirPerTasques ? " per tasques" : ""), "expedient", expedientLog.getExpedient().getTipus().getNom(), null, "getLogsJbpmPerRetrocedir");
		List<ProcessLog> logsJbpm = getLogsJbpmPerRetrocedir(expedientLogs); //, expedientLogId);
		mesuresTemporalsHelper.mesuraCalcular("Retrocedir" + (retrocedirPerTasques ? " per tasques" : ""), "expedient", expedientLog.getExpedient().getTipus().getNom(), null, "getLogsJbpmPerRetrocedir");
		
		// Primer i últim log (rang a retrocedir)
		long beginLogId = logsJbpm.get(0).getId();
		long endLogId = logsJbpm.get(logsJbpm.size() - 1).getId();
		
		// Info log actual
		Token currentToken = null;
//		LogObjectDto currentLog = null;
		Node nodeDesti = null;
		boolean tascaActual = false;
		
		if (debugRetroces)
			printLogs(logsJbpm);
		jbpmHelper.addProcessInstanceMessageLog(
				expedientLog.getExpedient().getProcessInstanceId(),
				getMessageLogPerTipus(retrocedirPerTasques ? ExpedientRetroaccioTipus.EXPEDIENT_RETROCEDIR_TASQUES : ExpedientRetroaccioTipus.EXPEDIENT_RETROCEDIR));
		if (logsJbpm != null && !logsJbpm.isEmpty()) {
			mesuresTemporalsHelper.mesuraIniciar("Retrocedir" + (retrocedirPerTasques ? " per tasques" : ""), "expedient", expedientLog.getExpedient().getTipus().getNom(), null, "getAccionsJbpmPerRetrocedir");
			// Recull totes les accions executades a jBPM relacionats amb els logs
			Collection<LogObjectDto> LogObjectDtos = getAccionsJbpmPerRetrocedir(
					expedientLogs,
					logsJbpm);
			if (debugRetroces) {
				for (LogObjectDto logo: LogObjectDtos) {
					String logInfo = ">>> [RETPRN] ";
					switch (logo.getTipus()) {
					case LogObjectDto.LOG_OBJECT_PROCES:
						logInfo = logInfo + "PROCES ";
						break;
					case LogObjectDto.LOG_OBJECT_TOKEN:
						logInfo = logInfo + "TOKEN ";
						break;
					case LogObjectDto.LOG_OBJECT_TASK:
						logInfo = logInfo + "TASCA ";
						break;
					case LogObjectDto.LOG_OBJECT_VARTASCA:
						logInfo = logInfo + "VARTASCA ";
						break;
					case LogObjectDto.LOG_OBJECT_VARPROCES:
						logInfo = logInfo + "VARPROCES ";
						break;
					case LogObjectDto.LOG_OBJECT_ACTION:
						logInfo = logInfo + "ACTION ";
						break;
					case LogObjectDto.LOG_OBJECT_INFO:
						logInfo = logInfo + "INFO ";
						break;
					default:
						logInfo = logInfo + "??? ";
					}
					logInfo = logInfo + "(" + logo.getName() + ") ";
					for (String accio: logo.getAccions())
						logInfo = logInfo + accio;
					logger.info(logInfo);
				}
			}
			mesuresTemporalsHelper.mesuraCalcular("Retrocedir" + (retrocedirPerTasques ? " per tasques" : ""), "expedient", expedientLog.getExpedient().getTipus().getNom(), null, "getAccionsJbpmPerRetrocedir");
			mesuresTemporalsHelper.mesuraIniciar("Retrocedir" + (retrocedirPerTasques ? " per tasques" : ""), "expedient", expedientLog.getExpedient().getTipus().getNom(), null, "obtenir dades per retroces");
			
			// Emmagatzema els paràmetres per a retrocedir cada acció
			Map<Long, String> paramsAccio = new HashMap<Long, String>();
			for (LogObjectDto logo: LogObjectDtos) {
				if (logo.getTipus() == LogObjectDto.LOG_OBJECT_ACTION) {
					String varName = BasicActionHandler.PARAMS_RETROCEDIR_VARIABLE_PREFIX + new Long(logo.getObjectId());
					String params = (String)jbpmHelper.getProcessInstanceVariable(
							new Long(logo.getProcessInstanceId()).toString(),
							varName);
					paramsAccio.put(new Long(logo.getObjectId()), params);
				}
			}
			
			// comprovam si estem retrocedint únicament la tasca actual
			if (jtask != null) {
				WProcessInstance pi = jbpmHelper.getProcessInstance(String.valueOf(expedientLog.getProcessInstanceId()));
				currentToken = jbpmHelper.getProcessLogById(expedientLog.getJbpmLogId()).getToken();
				Collection<TaskInstance> tis = ((ProcessInstance)pi.getProcessInstance()).getTaskMgmtInstance().getUnfinishedTasks(currentToken);
				for (TaskInstance ti: tis) {
					if (ti.getId() == ((JbpmTask)jtask).getTaskInstance().getId()){
						nodeDesti = ti.getTask().getTaskNode();
						tascaActual = true;
						if (debugRetroces)
							logger.info(">>> [LOGTASK] Retroces de la tasca actual (" + nodeDesti + ")!");
						break;
					}
				}
			}
			
			mesuresTemporalsHelper.mesuraCalcular("Retrocedir" + (retrocedirPerTasques ? " per tasques" : ""), "expedient", expedientLog.getExpedient().getTipus().getNom(), null, "obtenir dades per retroces");
			mesuresTemporalsHelper.mesuraIniciar("Retrocedir" + (retrocedirPerTasques ? " per tasques" : ""), "expedient", expedientLog.getExpedient().getTipus().getNom(), null, "realitzar accions de retroces");
			// Executa les accions necessàries per a retrocedir l'expedient
			for (LogObjectDto logo: LogObjectDtos) {
				boolean created = logo.getAccions().contains(LogObjectDto.LOG_ACTION_CREATE);
				// boolean update = logo.getAccions().contains(LogObjectDto.LOG_ACTION_UPDATE);
				boolean deleted = logo.getAccions().contains(LogObjectDto.LOG_ACTION_DELETE);
				boolean started = logo.getAccions().contains(LogObjectDto.LOG_ACTION_START);
				boolean ended = logo.getAccions().contains(LogObjectDto.LOG_ACTION_END);
				boolean assigned = logo.getAccions().contains(LogObjectDto.LOG_ACTION_ASSIGN);
				switch (logo.getTipus()) {
				case LogObjectDto.LOG_OBJECT_PROCES:
					if (started && !ended) {
						if (debugRetroces)
							logger.info(">>> [RETLOG] Cancel·lar/finalitzar procés (" + logo.getName() + ")");
						jbpmHelper.cancelProcessInstance(logo.getObjectId());
					} else if (!started && ended) {
						if (debugRetroces)
							logger.info(">>> [RETLOG] Desfer finalitzar procés (" + logo.getName() + ")");
						jbpmHelper.revertProcessInstanceEnd(logo.getObjectId());
						WProcessInstance jpi = jbpmHelper.getProcessInstance(String.valueOf(logo.getProcessInstanceId()));
						if (debugRetroces)
							logger.info(">>> [RETLOG] Desfer finalitzar token (" + ((ProcessInstance)jpi.getProcessInstance()).getRootToken().getFullName() + ")");
						jbpmHelper.revertTokenEnd(((ProcessInstance)jpi.getProcessInstance()).getRootToken().getId());
					}
					break;
				case LogObjectDto.LOG_OBJECT_TOKEN:
					if (debugRetroces) {
						WToken jtok = jbpmHelper.getTokenById(String.valueOf(logo.getObjectId()));
						logger.info(">>> [LOGTOKEN] Inici Retroces token (" + logo.getName() + ") - End: " + jtok.getEnd());
					}
					if (started && !ended) {
						if (debugRetroces)
							logger.info(">>> [RETLOG] Cancel·lar token (" + logo.getName() + ")");
						jbpmHelper.cancelToken(logo.getObjectId());
						
						if (debugRetroces) {
							WToken jtok = jbpmHelper.getTokenById(String.valueOf(logo.getObjectId()));
							logger.info(">>> [LOGTOKEN] Retroces token cancelat (" + logo.getName() + ") - End: " + jtok.getEnd());
						}
					} else if (!started && ended) {
						if (debugRetroces)
							logger.info(">>> [RETLOG] Desfer finalitzar token (" + logo.getName() + ")");
						jbpmHelper.revertTokenEnd(logo.getObjectId());
						
						if (debugRetroces) {
							WToken jtok = jbpmHelper.getTokenById(String.valueOf(logo.getObjectId()));
							logger.info(">>> [LOGTOKEN] Retroces revert token end (" + logo.getName() + ") - End: " + jtok.getEnd());
						}
					}
					if (!started) {
						// Només ha d'executar el node si no és una instància de procés
						// o un join o un fork
						String desti = (String)logo.getValorInicial();
						if (jbpmHelper.isJoinNode(logo.getProcessInstanceId(), (String)logo.getValorInicial())) {
							Node joinNode = jbpmHelper.getNodeByName(logo.getProcessInstanceId(), desti);
							Node forkNode = getForkNode(logo.getProcessInstanceId(), joinNode);
							if (forkNode != null)
								desti = forkNode.getName();
						} 
						
						if (debugRetroces) {
							WToken jtok = jbpmHelper.getTokenById(String.valueOf(logo.getObjectId()));
							logger.info(">>> [LOGTOKEN] Retroces abans token redirect (" + logo.getName() + ") - End: " + jtok.getEnd());
						}
						
//						boolean enterNode = (nodeRetrocedir == logo.getLogId());
//						JbpmTask jtask = jbpmHelper.getTaskById(expedientLog.getTargetId());
						boolean enterNode = false;
						Node ndesti = jbpmHelper.getNodeByName(logo.getProcessInstanceId(), desti);
						if (ndesti != null)
							enterNode = retrocedirPerTasques && (ndesti.getId() == ((JbpmTask)jtask).getTaskInstance().getTask().getTaskNode().getId()); // és la tasca a la que volem retrocedir!!
						boolean executeNode = (!jbpmHelper.isProcessStateNodeJoinOrFork( //(!jbpmHelper.isProcessStateNode(
								logo.getProcessInstanceId(),
								(String)logo.getValorInicial()));
						if (enterNode) {
//							nodeEnterObjectId = logo.getObjectId();
							nodeEnterTokenId = logo.getTokenId();
						}
						if (debugRetroces)
							logger.info(">>> [RETLOG] Retornar token (name=" + logo.getName() + ") al node (name=" + desti + ", enter = " + enterNode + ", execute=" + executeNode + ")");
						jbpmHelper.tokenRedirect(
								logo.getObjectId(),
								desti,
								true,
								enterNode,
								executeNode);
						
						if (debugRetroces) {
							WToken jtok = jbpmHelper.getTokenById(String.valueOf(logo.getObjectId()));
							logger.info(">>> [LOGTOKEN] Retroces després token redirect (" + logo.getName() + ") - End: " + jtok.getEnd());
						}
					}
					break;
				case LogObjectDto.LOG_OBJECT_TASK:
					boolean tascaStarted = jbpmHelper.hasStartBetweenLogs(beginLogId, endLogId, logo.getObjectId());
					if (!tascaStarted && assigned) {
						WTaskInstance task = jbpmHelper.findEquivalentTaskInstance(logo.getTokenId(), logo.getObjectId());
						String valor = (String)logo.getValorInicial();
						if (valor != null && !"".equals(valor)) {
							if (debugRetroces)
								logger.info(">>> [RETLOG] Reassignar tasca (" + task.getId() + ") a " + valor);
							if (valor.startsWith("[") && valor.endsWith("]")) {
								String[] actors = valor.substring(1, valor.length()-1).split(",");
								jbpmHelper.setTaskInstancePooledActors(
										task.getId(),
										actors);
							} else {
								jbpmHelper.setTaskInstanceActorId(
										task.getId(),
										valor);
							}
						}
					} else if (!tascaStarted && ended) {
						WTaskInstance task = jbpmHelper.findEquivalentTaskInstance(logo.getTokenId(), logo.getObjectId());
						if (debugRetroces)
							logger.info(">>> [RETLOG] Copiar variables de la tasca (id=" + logo.getObjectId() + ") a la tasca (id=" + task.getId() + ")");
						Map<String, Object> vars = jbpmHelper.getTaskInstanceVariables(new Long(logo.getObjectId()).toString());
						jbpmHelper.setTaskInstanceVariables(task.getId(), vars, true);
					}
					break;
				case LogObjectDto.LOG_OBJECT_VARPROCES:
					if (logo.getName() != null) {
						String pid = new Long(logo.getProcessInstanceId()).toString();
						if (created && !deleted) {
							if (debugRetroces)
								logger.info(">>> [RETLOG] Esborrar variable " + logo.getName() + " del proces (" + pid + ")");
							jbpmHelper.deleteProcessInstanceVariable(
									pid,
									logo.getName());
							if (logo.getName().startsWith(JbpmVars.PREFIX_DOCUMENT)) {
								documentHelper.esborrarDocument(
										null,
										pid,
										DocumentHelperV3.getDocumentCodiPerVariableJbpm(logo.getName()));
							}
						} else if (!created && deleted) {
							if (debugRetroces)
								logger.info(">>> [RETLOG] Crear variable " + logo.getName() + " del proces (" + pid + ") amb el valor (" + logo.getValorInicial() + ")");
							if (logo.getName().startsWith(JbpmVars.PREFIX_DOCUMENT)) {
								// Si existissin versions de documents no s'hauria de fer res
								Long documentStoreId = documentHelper.actualitzarDocument(
										null,
										pid,
										DocumentHelperV3.getDocumentCodiPerVariableJbpm(logo.getName()),
										"Document buit",
										new Date(),
										"document.pdf",
										getContingutRecurs("document_buit.pdf"),
										false);
								jbpmHelper.setProcessInstanceVariable(
										pid,
										logo.getName(),
										documentStoreId);
							} else {
								jbpmHelper.setProcessInstanceVariable(
										pid,
										logo.getName(),
										logo.getValorInicial());
							}
						} else if (!created && !deleted) {
							if (debugRetroces)
								logger.info(">>> [RETLOG] Actualitzar variable " + logo.getName() + " del proces (" + pid + ") amb el valor (" + logo.getValorInicial() + ")");
							if (logo.getName().startsWith(JbpmVars.PREFIX_DOCUMENT)) {
								// Si existissin versions de documents no s'hauria de fer res
								Long documentStoreId = documentHelper.actualitzarDocument(
										null,
										pid,
										DocumentHelperV3.getDocumentCodiPerVariableJbpm(logo.getName()),
										"Document buit",
										new Date(),
										"document.pdf",
										getContingutRecurs("document_buit.pdf"),
										false);
								jbpmHelper.setProcessInstanceVariable(
										pid,
										logo.getName(),
										documentStoreId);
							} else {
								jbpmHelper.setProcessInstanceVariable(
										pid,
										logo.getName(),
										logo.getValorInicial());
							}
						}
					}
					break;
				case LogObjectDto.LOG_OBJECT_VARTASCA:
					if (logo.getName() != null) {
						// Només processa el log si la tasca a la qual correspon la modificació
						// no s'ha iniciat a dins els logs que volem retrocedir
						boolean hiHaLogTasca = false;
						boolean logTascaStarted = false;
						for (LogObjectDto lo: LogObjectDtos) {
							if (lo.getTipus() == LogObjectDto.LOG_OBJECT_TASK && lo.getObjectId() == logo.getTaskInstanceId()) {
								hiHaLogTasca = true;
								logTascaStarted = jbpmHelper.hasStartBetweenLogs(beginLogId, endLogId, logo.getTaskInstanceId());
								break;
							}
						}
						if (!hiHaLogTasca || (hiHaLogTasca && !logTascaStarted)) {
							WTaskInstance task = jbpmHelper.findEquivalentTaskInstance(logo.getTokenId(), logo.getTaskInstanceId());
								//String tid = new Long(logo.getTaskInstanceId()).toString();
							if (created && !deleted) {
								if (debugRetroces)
									logger.info(">>> [RETLOG] Esborrar variable " + logo.getName() + " de la tasca (" + task.getId() + ")");
								jbpmHelper.deleteTaskInstanceVariable(
										task.getId(),
										logo.getName());
								// Si la variable ha estat creada mitjançant el DefaultControllerHandler fa un setVariableLocally
								Tasca tasca = tascaRepository.findByJbpmNameAndDefinicioProcesJbpmId(
										task.getTaskName(),
										task.getProcessDefinitionId());
								CampTasca campTasca = campTascaRepository.findAmbTascaCodi(
										tasca.getId(),
										logo.getName());
								if (campTasca != null) {
									jbpmHelper.setTaskInstanceVariable(
											task.getId(),
											logo.getName(),
											null);
								}
								// Si la variable correspon a un document vol dir que també l'hem d'esborrar
								if (logo.getName().startsWith(JbpmVars.PREFIX_DOCUMENT)) {
									documentHelper.esborrarDocument(
											task.getId(),
											null,
											DocumentHelperV3.getDocumentCodiPerVariableJbpm(logo.getName()));
								}
							} else if (!created && deleted) {
								if (debugRetroces)
									logger.info(">>> [RETLOG] Crear variable " + logo.getName() + " de la tasca (" + task.getId() + ") amb el valor (" + logo.getValorInicial() + ")");
								if (logo.getName().startsWith(JbpmVars.PREFIX_DOCUMENT)) {
									// Si existissin versions de documents no s'hauria de fer res
									Long documentStoreId = documentHelper.actualitzarDocument(
											task.getId(),
											null,
											DocumentHelperV3.getDocumentCodiPerVariableJbpm(logo.getName()),
											"Document buit",
											new Date(),
											"document.pdf",
											getContingutRecurs("document_buit.pdf"),
											false);
									jbpmHelper.setTaskInstanceVariable(
											task.getId(),
											logo.getName(),
											documentStoreId);
								} else {
									jbpmHelper.setTaskInstanceVariable(
											task.getId(),
											logo.getName(),
											logo.getValorInicial());
								}
							} else if (!created && !deleted) {
								if (debugRetroces)
									logger.info(">>> [RETLOG] Actualitzar variable " + logo.getName() + " de la tasca (" + task.getId() + ") amb el valor (" + logo.getValorInicial() + ")");
								if (logo.getName().startsWith(JbpmVars.PREFIX_DOCUMENT)) {
									// Si existissin versions de documents no s'hauria de fer res
									Long documentStoreId = documentHelper.actualitzarDocument(
											task.getId(),
											new Long(logo.getProcessInstanceId()).toString(),
											DocumentHelperV3.getDocumentCodiPerVariableJbpm(logo.getName()),
											"Document buit",
											new Date(),
											"document.pdf",
											getContingutRecurs("document_buit.pdf"),
											false);
									jbpmHelper.setTaskInstanceVariable(
											task.getId(),
											logo.getName(),
											documentStoreId);
								} else {
									jbpmHelper.setTaskInstanceVariable(
											task.getId(),
											logo.getName(),
											logo.getValorInicial());
								}
							}
						}
					}
					break;
				case LogObjectDto.LOG_OBJECT_ACTION:
					if (debugRetroces)
						logger.info(">>> [RETLOG] Executar accio inversa " + logo.getObjectId());
					String pid = new Long(logo.getProcessInstanceId()).toString();
					List<String> params = null;
					String paramsStr = paramsAccio.get(new Long(logo.getObjectId()));
					if (paramsStr != null) {
						params = new ArrayList<String>();
						String[] parts = paramsStr.split(BasicActionHandler.PARAMS_RETROCEDIR_SEPARADOR);
						for (String part: parts) {
							if (part.length() > 0)
								params.add(part);
						}
					}
					jbpmHelper.retrocedirAccio(
							pid,
							logo.getName(),
							params);
					break;
				case LogObjectDto.LOG_OBJECT_INFO:
					Expedient expedient = expedientHelper.findExpedientByProcessInstanceId(String.valueOf(logo.getProcessInstanceId()));
					
					RetroaccioInfo li = RetroaccioInfo.valueOf(logo.getName());
					switch (li) {
						case NUMERO:
							expedient.setNumero((String)logo.getValorInicial());
							break;
						case TITOL:
							expedient.setTitol((String)logo.getValorInicial());
							break;
						case RESPONSABLE:
							expedient.setResponsableCodi((String)logo.getValorInicial());
							break;
						case COMENTARI:
							expedient.setComentari((String)logo.getValorInicial());
							break;
						case GEOREFERENCIA:
							expedient.setGeoReferencia((String)logo.getValorInicial());
							break;
						case GRUP:
							expedient.setGrupCodi((String)logo.getValorInicial());
							break;
						case INICI:
							expedient.setDataInici((Date)logo.getValorInicial());
							break;
						case ESTAT:
							expedient.setEstat((Estat)logo.getValorInicial());
							break;
						case GEOPOSICIOX:
							expedient.setGeoPosX((Double)logo.getValorInicial());
							break;
						case GEOPOSICIOY:
							expedient.setGeoPosY((Double)logo.getValorInicial());
							break;
						default:
							break;
					}
					break;
				}
			}
			mesuresTemporalsHelper.mesuraCalcular("Retrocedir" + (retrocedirPerTasques ? " per tasques" : ""), "expedient", expedientLog.getExpedient().getTipus().getNom(), null, "realitzar accions de retroces");
		}
		
		// Si retrocedim la tasca actual...
		if (tascaActual) {
//			Node ndesti = jbpmHelper.getNodeByName(expedientLog.getProcessInstanceId(), desti);
			boolean enterNode = retrocedirPerTasques; //&& (nodeDesti.getId() == jtask.getTask().getTask().getTaskNode().getId()); // és la tasca a la que volem retrocedir!!
			boolean executeNode = (!jbpmHelper.isProcessStateNodeJoinOrFork( //(!jbpmHelper.isProcessStateNode(
					expedientLog.getProcessInstanceId(),
					nodeDesti.getName()));
			if (enterNode) {
				nodeEnterTokenId = currentToken.getId();
			}
			if (debugRetroces)
				logger.info(">>> [RETLOG] Retornar token (name=" + currentToken.getName() + ") al node (name=" + nodeDesti.getName() + ", enter = " + enterNode + ", execute=" + executeNode + ")");
			jbpmHelper.tokenRedirect(
					currentToken.getId(),
					nodeDesti.getName(),
					true,
					enterNode,
					executeNode);
			
			if (debugRetroces) {
				logger.info(">>> [LOGTOKEN] Retroces després token redirect (" + currentToken.getName() + ") - End: " + currentToken.getEnd());
			}
		}
		if (retrocedirPerTasques && nodeEnterTokenId != null) {
			WTaskInstance task = jbpmHelper.findEquivalentTaskInstance(nodeEnterTokenId, Long.valueOf(expedientLog.getTargetId()));
			TaskInstance ti = ((JbpmTask)task).getTaskInstance();
			ContextInstance ci = ti.getProcessInstance().getContextInstance();
			for (CampTasca camp: getCampsPerTaskInstance(ti)) {
				if (camp.isReadFrom()) {
					if (debugRetroces)
						logger.info(">>> [RETVAR] Carregar variable del procés " + camp.getCamp().getCodi() + " a la tasca " + task.getTaskName() + " (" + task.getId() + ")");
					String codi = camp.getCamp().getCodi();
					ti.setVariableLocally(
							codi,
							ci.getVariable(codi));
				}
			}
			for (DocumentTasca document: getDocumentsPerTaskInstance(ti)) {
				String codi = JbpmVars.PREFIX_DOCUMENT + document.getDocument().getCodi();
				if (!document.isReadOnly()) {
					Object valor = ci.getVariable(JbpmVars.PREFIX_DOCUMENT + document.getDocument().getCodi());
					if (valor != null)
						if (debugRetroces)
							logger.info(">>> [RETDOC] Carregar document del procés " + codi + " a la tasca " + task.getTaskName() + " (" + task.getId() + ")");
						ti.setVariableLocally(
								codi,
								ci.getVariable(codi));
				}
			}
		}
		
		for (ExpedientLog elog: expedientLogs) {
			// Marca les accions com a retrocedides
			if (ExpedientRetroaccioEstat.NORMAL.equals(elog.getEstat()))
				if (retrocedirPerTasques) {
					elog.setEstat(ExpedientRetroaccioEstat.RETROCEDIT_TASQUES);
				} else {
					elog.setEstat(ExpedientRetroaccioEstat.RETROCEDIT);
				}
			if (elog.getId() != iniciadorId) elog.setIniciadorRetroces(iniciadorId);
			// Retrocediex la informació de l'expedient
		}
	}
	
	private JbpmToken getTokenByJbpmLogId(Long jbpmLogId){
		ProcessLog pl = jbpmHelper.getProcessLogById(jbpmLogId);
		if (pl == null)
			return null;
		return new JbpmToken(pl.getToken());
	}
	
	private void printLogs(List<ProcessLog> logs) {
		for (ProcessLog log: logs) {
			if (log.getParent() == null) {
				printLogMessage(
						logs,
						log,
						0);
			}
		}
	}
	
	private Collection<LogObjectDto> getAccionsJbpmPerRetrocedir(
			List<ExpedientLog> expedientLogs,
			List<ProcessLog> logsSorted) {
		Map<Long, LogObjectDto> logObjectDtos = new HashMap<Long, LogObjectDto>();
		long currentMessageLogId = -1;
		for (ProcessLog plog: logsSorted) {
			if (plog instanceof MessageLog) {
				MessageLog mlog = (MessageLog)plog;
				if (mlog.getMessage().startsWith(MESSAGE_LOGINFO_PREFIX)) {
					
					Long objId = new Long(plog.getToken().getProcessInstance().getId());
					LogObjectDto lobj = logObjectDtos.get(objId);
					
					if (lobj == null) {
						String sTipus = mlog.getMessage().substring(mlog.getMessage().indexOf("::") + 2, mlog.getMessage().indexOf("#@#"));
						String info = mlog.getMessage().substring(mlog.getMessage().indexOf("#@#") + 3);
						
						RetroaccioInfo li = RetroaccioInfo.valueOf(sTipus);
						
						lobj = new LogObjectDto(
								objId.longValue(),
								plog.getId(),
								//objId.toString(),
								li.name(),
								LogObjectDto.LOG_OBJECT_INFO,
								plog.getToken().getProcessInstance().getId(),
								plog.getToken().getId());
						
						try{
							switch (li) {
								case NUMERO:
								case TITOL:
								case RESPONSABLE:
								case COMENTARI:
								case GEOREFERENCIA:
								case GRUP:
									lobj.setValorInicial(info);
									break;
								case INICI:
									SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
									lobj.setValorInicial(sdf.parse(info));
									break;
								case ESTAT:
									if (info.equals("---")) {
										lobj.setValorInicial(null);
									} else {
										Long estatId = Long.parseLong(info);
										lobj.setValorInicial(estatRepository.findById(estatId));
									}
									break;
								case GEOPOSICIOX:
								case GEOPOSICIOY:
									lobj.setValorInicial(Double.parseDouble(info));
									break;
								default:
									break;
							}
							logObjectDtos.put(objId, lobj);
							
						} catch (Exception e) { 
							logger.error("ERROR: Error al obtenir el tipus de informació de l'expedient dels logs", e);
						}
					}
				} else {
					currentMessageLogId = plog.getId();
				}
			} else if (plog instanceof TaskLog) {
				TaskInstance taskInstance = ((TaskLog)plog).getTaskInstance();
				Long objId = new Long(taskInstance.getId());
				LogObjectDto lobj = logObjectDtos.get(objId);
				if (lobj == null) {
					lobj = new LogObjectDto(
							objId.longValue(),
							plog.getId(),
							taskInstance.getName(),
							LogObjectDto.LOG_OBJECT_TASK,
							plog.getToken().getProcessInstance().getId(),
							plog.getToken().getId());
					logObjectDtos.put(objId, lobj);
				}
				if (plog instanceof TaskCreateLog)
					lobj.addAccio(LogObjectDto.LOG_ACTION_START);
				if (plog instanceof TaskEndLog)
					lobj.addAccio(LogObjectDto.LOG_ACTION_END);
				if (plog instanceof TaskAssignLog) {
					lobj.addAccio(LogObjectDto.LOG_ACTION_ASSIGN);
					lobj.setValorInicial(((TaskAssignLog)plog).getTaskOldActorId());
					ExpedientLog expedientLog = getExpedientLogPerJbpmLogId(expedientLogs, currentMessageLogId);
					if (expedientLog != null && expedientLog.getAccioTipus().equals(ExpedientRetroaccioTipus.TASCA_REASSIGNAR)) {
						String params = expedientLog.getAccioParams();
						if (params.indexOf("::") != -1)
							lobj.setValorInicial(params.substring(0, params.indexOf("::")));
					}
				}
			} else if (plog instanceof VariableLog) {
				VariableInstance variableInstance = ((VariableLog)plog).getVariableInstance();
				boolean ignored = false;
				// Hi ha logs de variables que ténen el nom null i el valor null 
				// No sé molt bé el motiu
				// El següent if els descarta
				if(variableInstance.getProcessInstance()!=null){
					String codi = variableInstance.getName();
					DefinicioProces pDef = definicioProcesRepository.findByJbpmId(String.valueOf(variableInstance.getProcessInstance().getProcessDefinition().getId()));
					Camp camp = null;
					Expedient expedient = expedientRepository.findOne(
							variableInstance.getProcessInstance().getExpedient().getId());
					ExpedientTipus expedientTipus = expedient != null ? expedient.getTipus() : null;
					if (expedientTipus != null && expedientTipus.isAmbInfoPropia()) {
						camp = campRepository.findByExpedientTipusAndCodi(
								expedientTipus, 
								codi);
					} else {
						camp = campRepository.findByDefinicioProcesAndCodi(
								pDef,
								codi);		
					}
					if(camp != null){
						ignored = camp.isIgnored();
					}
					if (!ignored) {
						if (variableInstance.getName() != null || variableInstance.getValue() != null) {
							Long variableInstanceId = jbpmHelper.getVariableIdFromVariableLog(plog.getId());
							Long taskInstanceId = jbpmHelper.getTaskIdFromVariableLog(plog.getId());
							Long objId = new Long(variableInstanceId);
							LogObjectDto lobj = logObjectDtos.get(objId);
							if (lobj == null) {
								lobj = new LogObjectDto(
										objId.longValue(),
										plog.getId(),
										variableInstance.getName(),
										(taskInstanceId != null) ? LogObjectDto.LOG_OBJECT_VARTASCA : LogObjectDto.LOG_OBJECT_VARPROCES,
										plog.getToken().getProcessInstance().getId(),
										plog.getToken().getId());
								if (taskInstanceId != null) {
									lobj.setTaskInstanceId(taskInstanceId.longValue());
								}
								logObjectDtos.put(objId, lobj);
							}
							if (plog instanceof VariableCreateLog)
								lobj.addAccio(LogObjectDto.LOG_ACTION_CREATE);
							if (plog instanceof VariableUpdateLog) {
								VariableUpdateLog vulog = (VariableUpdateLog)plog;
								lobj.addAccio(LogObjectDto.LOG_ACTION_UPDATE);
								Object oldValue = vulog.getOldValue();
								if (oldValue instanceof ByteArray) {
									try {
										oldValue = new ObjectInputStream(new ByteArrayInputStream(((ByteArray)vulog.getNewValue()).getBytes())).readObject();
									} catch (Exception e) {
										logger.error("Error obtenint el valor del ByteArray de la variable " + vulog.getVariableInstance().getName(), e);
									}
								} else if (oldValue instanceof String && camp != null && camp.getTipus() == TipusCamp.BOOLEAN) {
									oldValue =  oldValue.equals("T") ? new Boolean(true) : new Boolean(false);
								}
								lobj.setValorInicial(oldValue);
							}
							if (plog instanceof VariableDeleteLog)
								lobj.addAccio(LogObjectDto.LOG_ACTION_DELETE);
						}
					}
				}
			} else if (plog instanceof TokenCreateLog || plog instanceof TokenEndLog || plog instanceof TransitionLog) {
				Token token = plog.getToken();
				if (plog instanceof TokenCreateLog)
					token = ((TokenCreateLog)plog).getChild();
				if (plog instanceof TokenEndLog)
					token = ((TokenEndLog)plog).getChild();
				Long objId = new Long(token.getId());
				LogObjectDto lobj = logObjectDtos.get(objId);
				if (lobj == null) {
					String tokenName = token.getName();
					if (tokenName == null && token.isRoot())
						tokenName = "[ROOT]";
					lobj = new LogObjectDto(
							objId.longValue(),
							plog.getId(),
							tokenName,
							LogObjectDto.LOG_OBJECT_TOKEN,
							plog.getToken().getProcessInstance().getId(),
							plog.getToken().getId());
					logObjectDtos.put(objId, lobj);
				}
				if (plog instanceof TokenCreateLog)
					lobj.addAccio(LogObjectDto.LOG_ACTION_START);
				if (plog instanceof TokenEndLog)
					lobj.addAccio(LogObjectDto.LOG_ACTION_END);
				if (plog instanceof TransitionLog) {
					TransitionLog trlog = (TransitionLog)plog;
					lobj.addAccio(LogObjectDto.LOG_ACTION_UPDATE);
					lobj.setValorInicial(trlog.getSourceNode().getName());
				}
			} else if (plog instanceof ProcessInstanceCreateLog || plog instanceof ProcessInstanceEndLog) {
				Long objId = new Long(plog.getToken().getProcessInstance().getId());
				LogObjectDto lobj = logObjectDtos.get(objId);
				if (lobj == null) {
					lobj = new LogObjectDto(
							objId.longValue(),
							plog.getId(),
							objId.toString(),
							LogObjectDto.LOG_OBJECT_PROCES,
							plog.getToken().getProcessInstance().getId(),
							plog.getToken().getId());
					logObjectDtos.put(objId, lobj);
				}
				if (plog instanceof ProcessInstanceCreateLog)
					lobj.addAccio(LogObjectDto.LOG_ACTION_START);
				if (plog instanceof ProcessInstanceEndLog)
					lobj.addAccio(LogObjectDto.LOG_ACTION_END);
			} else if (plog instanceof ActionLog) {
				Long objId = ((ActionLog)plog).getAction().getId();
				LogObjectDto lobj = logObjectDtos.get(objId);
				if (lobj == null) {
					lobj = new LogObjectDto(
							objId.longValue(),
							plog.getId(),
							((ActionLog)plog).getAction().getName(),
							LogObjectDto.LOG_OBJECT_ACTION,
							plog.getToken().getProcessInstance().getId(),
							plog.getToken().getId());
					logObjectDtos.put(objId, lobj);
				}
				lobj.addAccio(LogObjectDto.LOG_ACTION_EXEC);
			}
		//}
		}
		List<LogObjectDto> logsOrdenats = new ArrayList<LogObjectDto>(logObjectDtos.values());
		Collections.sort(
				logsOrdenats,
				new Comparator<LogObjectDto>() {
					public int compare(LogObjectDto o1, LogObjectDto o2) {
						Long l1 = new Long(o1.getLogId());
						Long l2 = new Long(o2.getLogId());
						return l1.compareTo(l2);
					}
				});
		Collections.reverse(logsOrdenats);
		return logsOrdenats;
	}
	
	private List<ExpedientLog> filtraExpedientsLogPerRetrocedir(
			List<ExpedientLog> expedientLogs, 
			Long expedientLogId,
			boolean retrocedirPerTasques) {
		Token tokenRetroces = null;
		boolean incloure = false;
		boolean found = false;
		
		List<ExpedientLog> expedientLogsRetrocedir = new ArrayList<ExpedientLog>();
		for (ExpedientLog elog: expedientLogs) {
			// Obtenim el log seleccionat
			if (elog.getId().equals(expedientLogId)) {
				found = true;
				incloure = true;
				if (/*retrocedirPerTasques  && */elog.isTargetTasca()) {
					JbpmToken jbpmTokenRetroces = getTokenByJbpmLogId(elog.getJbpmLogId());
					if (jbpmTokenRetroces != null) tokenRetroces = jbpmTokenRetroces.getToken();
				}
			}
			// Obtenim els logs a retrocedir
			if (found) {
				// Ara
//				if (retrocedirPerTasques) {
					 if (elog.isTargetTasca() && tokenRetroces != null) {
						 // Si la tasca seleccionada es del token arrel, llavors 
						 // totes les tasques posteriors s'han de incloure
						 if (tokenRetroces.isRoot() && tokenRetroces.getProcessInstance().getSuperProcessToken() == null) { //processos.get(tokenRetroces.getProcessInstance().getId()) == null) { 
							 incloure = true;
						 } else {
							Token tokenActual = null;
							JbpmToken jbpmTokenActual = getTokenByJbpmLogId(elog.getJbpmLogId());
							if (jbpmTokenActual != null) {
								tokenActual = jbpmTokenActual.getToken();
							
								if ((tokenActual.isRoot() && tokenActual.getProcessInstance().getSuperProcessToken() == null) 
										|| tokenActual.equals(tokenRetroces)) {
									// Incloem el token arrel "/" i els tokens iguals al token de la tasca seleccionada
									incloure = true;
								} else {
									// Incloem tots els tokens pare del token de la tasca seleccionada
									Token subTokenRetroces = getTokenPare(tokenRetroces);
										while (subTokenRetroces != null) {
											if (tokenActual.equals(subTokenRetroces)) {
												incloure = true;
												break;
											}
											subTokenRetroces = getTokenPare(subTokenRetroces);
										}
									// Incloem tots els tokens fills del token de la tasca seleccionada
									if (!incloure) {
										Token subTokenActual = getTokenPare(tokenActual);
										while (subTokenActual != null) {
											if (tokenRetroces.equals(subTokenActual)) {
												incloure = true;
												break;
											}
											subTokenActual = getTokenPare(subTokenActual);
										}
									}
								}
							}
						 }
					 }
					 if (incloure) {
						expedientLogsRetrocedir.add(elog);
						incloure = false;
					}
//				} else {
//					expedientLogsRetrocedir.add(elog);
//				}
			}
		}
		return expedientLogsRetrocedir;
	}

	private Token getTokenPare(Token token) {
		Token t = token.getParent();
		if (t == null) {
			t = token.getProcessInstance().getSuperProcessToken();
		}
		return t;
	}

	private List<ProcessLog> getLogsJbpmPerRetrocedir(List<ExpedientLog> expedientLogs) {
		List<ProcessLog> logsJbpm = new ArrayList<ProcessLog>();
		
		// Utilitzam un set per a evitar repetits
		Set<ProcessLog> setlogsJbpm = new HashSet<ProcessLog>();		
		for (ExpedientLog elog: expedientLogs) {
			List<ProcessLog> logsJbpmPerAfegir = getLogsJbpmPerExpedientLog(elog);
			if (logsJbpmPerAfegir != null)
				setlogsJbpm.addAll(logsJbpmPerAfegir);
		}
		// Passam els logs a una llista, i la ordenam
		logsJbpm.addAll(setlogsJbpm);
		Collections.sort(
				logsJbpm,
				new Comparator<ProcessLog>() {
					public int compare(ProcessLog l1, ProcessLog l2) {
						return new Long(l1.getId()).compareTo(new Long(l2.getId()));
					}
				});
		
		return logsJbpm;
	}
	
	private List<ProcessLog> getJbpmLogsPerInstanciaProces(Long processInstanceId, boolean asc) {
		 
		List<ProcessLog> logsJbpm = new ArrayList<ProcessLog>();
		 
		WProcessInstance pi = jbpmHelper.getRootProcessInstance(processInstanceId.toString());
		List<WProcessInstance> processos = jbpmHelper.getProcessInstanceTree(pi.getId());
		for (WProcessInstance proces: processos) {
			Map<Token, List<ProcessLog>> logsPerInstanciaProces = jbpmHelper.getProcessInstanceLogs(
					proces.getId());
			for (Token token: logsPerInstanciaProces.keySet()) {
				for (ProcessLog plog: logsPerInstanciaProces.get(token))
					logsJbpm.add(plog);
			}
		}
		
		if (asc) {
			Collections.sort(
				logsJbpm,
				new Comparator<ProcessLog>() {
					public int compare(ProcessLog l1, ProcessLog l2) {
						return new Long(l1.getId()).compareTo(new Long(l2.getId()));
					}
				});
		} else {
			Collections.sort(
				logsJbpm,
				new Comparator<ProcessLog>() {
					public int compare(ProcessLog l1, ProcessLog l2) {
						return new Long(l2.getId()).compareTo(new Long(l1.getId()));
					}
				});
		}
		
		return logsJbpm;
	}
	
	private List<ProcessLog> getLogsJbpmPerExpedientLog(ExpedientLog expedientLog) {
		List<ProcessLog> logsJbpm = new ArrayList<ProcessLog>();
		List<ProcessLog> logsJbpmAfegir = new ArrayList<ProcessLog>();
		if (expedientLog.getJbpmLogId() != null && expedientLog.getEstat().equals(ExpedientRetroaccioEstat.NORMAL)) {
			// Obtenim els logs jBPM associats a la instància de procés i als seus
			// subprocessos
			logsJbpm = getJbpmLogsPerInstanciaProces(expedientLog.getProcessInstanceId(), true);
			
			int indexInici = -1;
			int indexFi = -1;
			int index = 0;
			Node join = null;
			for (ProcessLog plog: logsJbpm) {
				// L'índex inicial correspon al lloc a on es troba el log marcat per jbpmLogId
				if (plog.getId() == expedientLog.getJbpmLogId()) {
					indexInici = index;
				} else if (indexInici != -1 && plog instanceof TransitionLog) {
					// Comprovam si hi ha hagut una transició a un join
					TransitionLog trlog = (TransitionLog)plog;
					if (trlog.getDestinationNode().getNodeType() == Node.NodeType.Join) {
						join = trlog.getDestinationNode();
					}
				} else if (indexInici != -1 && plog instanceof MessageLog) {
					String message = ((MessageLog)plog).getMessage();
					if (message.startsWith(MESSAGE_LOG_PREFIX)) {
						indexFi = index;
						break;
					}
				}
				index++;
			}
			if (indexFi == -1)
				indexFi = logsJbpm.size();
			logsJbpmAfegir = logsJbpm.subList(indexInici, indexFi);
			
			// Si hi ha hagut una transició cap a un join, obtenim els logs de la continuació del join
			if (join == null) {
				return logsJbpmAfegir;
			} else {
				indexInici = -1;
				indexFi = -1;
				index = 0;
				for (ProcessLog plog: logsJbpm) {
					// L'índex inicial correspon al log del node del Join
					if (plog instanceof NodeLog) {
						NodeLog nlog = (NodeLog)plog;
						if (nlog.getNode().getId() == join.getId())
						indexInici = index;
					} else if (indexInici != -1 && plog instanceof MessageLog) {
						String message = ((MessageLog)plog).getMessage();
						if (message.startsWith(MESSAGE_LOG_PREFIX)) {
							indexFi = index;
							break;
						}
					}
					index++;
				}
				
				if (indexInici != -1) {
					if (indexFi == -1)
						indexFi = logsJbpm.size();
					logsJbpmAfegir.addAll(logsJbpm.subList(indexInici, indexFi));
				}
				return logsJbpmAfegir;
			}
		} else {
			return null;
		}
	}
	
	private void printLogMessage(
			List<ProcessLog> logs,
			ProcessLog log,
			int indent) {
		DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		
		String logInfo = "              ";
		for (int i = 0; i < indent; i++) {
			logInfo = logInfo + "║  ";
		}
		logInfo = logInfo + "╠═>";
		logInfo = logInfo + "[" + df.format(log.getDate()) + "] [" + log.getId() + "] " + log + "(" + log.getClass().getName() + ")";
		logger.info(logInfo);
		
		for (ProcessLog l: logs) {
			if (l.getParent() != null && l.getParent().getId() == log.getId()) {
				printLogMessage(logs, l, indent + 1);
			}
		}
	}

	private String getMessageLogPerTipus(ExpedientRetroaccioTipus tipus) {
		return MESSAGE_LOG_PREFIX + tipus.name();
	}
	
	private List<DocumentTasca> getDocumentsPerTaskInstance(TaskInstance taskInstance) {
		long processDefinitionId = taskInstance.getProcessInstance().getProcessDefinition().getId();
		Tasca tasca = tascaRepository.findByJbpmNameAndDefinicioProcesJbpmId(
				taskInstance.getTask().getName(),
				new Long(processDefinitionId).toString());
		return tasca.getDocuments();
	}
	
	private Node getForkNode(long processInstanceId, Node joinNode) {
		List<ProcessLog> logsJbpm = getJbpmLogsPerInstanciaProces(processInstanceId, false);
		if (logsJbpm != null && logsJbpm.size() > 0) {
			boolean trobat = false;
			Token token = null;
			for (ProcessLog plog: logsJbpm) {
				if (plog instanceof TransitionLog 
						&& ((TransitionLog) plog).getSourceNode().equals(joinNode)) {
					token = plog.getToken();
					trobat = true;
					continue;
				}
				if (trobat) {
					if (plog instanceof TransitionLog 
							&& ((TransitionLog) plog).getDestinationNode().getNodeType() == NodeType.Fork 
							&& ((TransitionLog) plog).getToken().equals(token)) {
						return ((TransitionLog) plog).getDestinationNode();
					}
				}
			}			
		} 
		return null;
	}

	private byte[] getContingutRecurs(String recurs) {
		try {
			InputStream inputStream = getClass().getResourceAsStream(recurs);
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			for (int readBytes = inputStream.read(); readBytes >= 0; readBytes = inputStream.read())
				outputStream.write(readBytes);
			byte[] byteData = outputStream.toByteArray();
			inputStream.close();
			outputStream.close();
			return byteData;
		} catch (Exception ex) {
			logger.error("Error al obtenir el recurs " + recurs, ex);
			return null;
		}
	}

	private List<CampTasca> getCampsPerTaskInstance(TaskInstance taskInstance) {
		long processDefinitionId = taskInstance.getProcessInstance().getProcessDefinition().getId();
		Tasca tasca = tascaRepository.findByJbpmNameAndDefinicioProcesJbpmId(
				taskInstance.getTask().getName(),
				new Long(processDefinitionId).toString());
		return tasca.getCamps();
	}

	private ExpedientLog getExpedientLogPerJbpmLogId(
			List<ExpedientLog> expedientLogs,
			long jbpmLogId) {
		for (ExpedientLog elog: expedientLogs) {
			if (elog.getJbpmLogId() != null && elog.getJbpmLogId().longValue() == jbpmLogId)
				return elog;
		}
		return null;
	}
	
	private List<InformacioRetroaccioDto> getLogs(
			Map<String, String> processos,
			ExpedientLog log,
			String parentProcessInstanceId,
			String piId,
			boolean detall) {
		// Obtenim el token de cada registre
		JbpmToken token = null;
		if (log.getJbpmLogId() != null) {
			token = getTokenByJbpmLogId(log.getJbpmLogId());
		}
		String tokenName = null;
		String processInstanceId = null;
		List<InformacioRetroaccioDto> resposta = new ArrayList<InformacioRetroaccioDto>();
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
							InformacioRetroaccioDto dto = new InformacioRetroaccioDto();
							dto.setId(log.getId());
							dto.setData(log.getData());
							dto.setUsuari(log.getUsuari());
							dto.setEstat(ExpedientRetroaccioEstat.IGNORAR.name());
							dto.setAccioTipus(ExpedientRetroaccioTipus.PROCES_LLAMAR_SUBPROCES.name());
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
			InformacioRetroaccioDto dto = new InformacioRetroaccioDto();
			dto.setId(log.getId());
			dto.setData(log.getData());
			dto.setUsuari(log.getUsuari());
			dto.setEstat(token == null ? ExpedientRetroaccioEstat.IGNORAR.name() : log.getEstat().name());
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

	private static final Log logger = LogFactory.getLog(RetroaccioHelper.class);
}
