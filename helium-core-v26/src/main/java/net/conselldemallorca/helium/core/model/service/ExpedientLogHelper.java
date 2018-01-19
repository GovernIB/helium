/**
 * 
 */
package net.conselldemallorca.helium.core.model.service;

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
import java.util.Set;

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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import net.conselldemallorca.helium.core.common.JbpmVars;
import net.conselldemallorca.helium.core.helperv26.DocumentHelper;
import net.conselldemallorca.helium.core.helperv26.MesuresTemporalsHelper;
import net.conselldemallorca.helium.core.model.dao.CampDao;
import net.conselldemallorca.helium.core.model.dao.CampTascaDao;
import net.conselldemallorca.helium.core.model.dao.DefinicioProcesDao;
import net.conselldemallorca.helium.core.model.dao.EstatDao;
import net.conselldemallorca.helium.core.model.dao.ExpedientDao;
import net.conselldemallorca.helium.core.model.dao.ExpedientLogDao;
import net.conselldemallorca.helium.core.model.dao.TascaDao;
import net.conselldemallorca.helium.core.model.hibernate.Camp;
import net.conselldemallorca.helium.core.model.hibernate.Camp.TipusCamp;
import net.conselldemallorca.helium.core.model.hibernate.CampTasca;
import net.conselldemallorca.helium.core.model.hibernate.DefinicioProces;
import net.conselldemallorca.helium.core.model.hibernate.DocumentTasca;
import net.conselldemallorca.helium.core.model.hibernate.Estat;
import net.conselldemallorca.helium.core.model.hibernate.Expedient;
import net.conselldemallorca.helium.core.model.hibernate.ExpedientLog;
import net.conselldemallorca.helium.core.model.hibernate.ExpedientLog.ExpedientLogAccioTipus;
import net.conselldemallorca.helium.core.model.hibernate.ExpedientLog.ExpedientLogEstat;
import net.conselldemallorca.helium.core.model.hibernate.ExpedientLog.LogInfo;
import net.conselldemallorca.helium.core.model.hibernate.Tasca;
import net.conselldemallorca.helium.jbpm3.handlers.BasicActionHandler;
import net.conselldemallorca.helium.jbpm3.integracio.JbpmHelper;
import net.conselldemallorca.helium.jbpm3.integracio.JbpmProcessInstance;
import net.conselldemallorca.helium.jbpm3.integracio.JbpmTask;
import net.conselldemallorca.helium.jbpm3.integracio.JbpmToken;
import net.conselldemallorca.helium.core.model.exception.NotFoundException;
/**
 * Helper per a gestionar els logs dels expedients
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class ExpedientLogHelper {

	private static final String MESSAGE_LOG_PREFIX = "[H3l1um]";
	private static final String MESSAGE_LOGINFO_PREFIX = "[H3l1nf0]";

	private JbpmHelper jbpmDao;
	private ExpedientLogDao expedientLogDao;
	private ExpedientDao expedientDao;
	private DocumentHelper documentHelper;
	private CampDao campDao;
	private TascaDao tascaDao;
	private CampTascaDao campTascaDao;
	private DefinicioProcesDao definicioProcesDao;
	private EstatDao estatDao;
	private MesuresTemporalsHelper mesuresTemporalsHelper;
	private MessageSource messageSource;

	public ExpedientLog afegirLogExpedientPerTasca(
			String taskInstanceId,
			ExpedientLogAccioTipus tipus,
			String accioParams) {
		return afegirLogExpedientPerTasca(taskInstanceId, tipus, accioParams, null);
	}
	public ExpedientLog afegirLogExpedientPerTasca(
			String taskInstanceId,
			ExpedientLogAccioTipus tipus,
			String accioParams,
			String user) {
		
		JbpmTask task = jbpmDao.getTaskById(taskInstanceId);
		Expedient expedient = getExpedientPerProcessInstanceId(task.getProcessInstanceId());
		
		Long jbpmLogId = null; 
		if (expedient.isAmbRetroaccio()) {
			jbpmLogId = jbpmDao.addTaskInstanceMessageLog(
					taskInstanceId,
					getMessageLogPerTipus(tipus));
		}
		String usuari = "Timer";
		if (user != null) {
			usuari = user;
		} else {
			try {
				usuari = SecurityContextHolder.getContext().getAuthentication().getName();
			}catch (Exception e){}
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
		expedientLogDao.saveOrUpdate(expedientLog);
		return expedientLog;
	}
	public ExpedientLog afegirLogExpedientPerProces(
			String processInstanceId,
			ExpedientLogAccioTipus tipus,
			String accioParams) {
		Expedient expedient = getExpedientPerProcessInstanceId(processInstanceId);
		
		Long jbpmLogId = null;
		if (expedient.isAmbRetroaccio()) {
			jbpmLogId = jbpmDao.addProcessInstanceMessageLog(
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
		expedientLogDao.saveOrUpdate(expedientLog);
		return expedientLog;
	}
	public ExpedientLog afegirLogExpedientPerExpedient(
			Long expedientId,
			ExpedientLogAccioTipus tipus,
			String accioParams) {
		Expedient expedient = expedientDao.getById(expedientId, false);
		
		String processInstanceId = expedient.getProcessInstanceId();
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
		if (accioParams != null)
			expedientLog.setAccioParams(accioParams);
		expedientLogDao.saveOrUpdate(expedientLog);
		return expedientLog;
	}
	public long afegirProcessLogInfoExpedient(
			boolean ambRetroaccio,
			String processInstanceId,
			String message) {
		if (ambRetroaccio) {
			long jbpmLogId = jbpmDao.addProcessInstanceMessageLog(
					processInstanceId,
					MESSAGE_LOGINFO_PREFIX + "::" + message);
			return jbpmLogId;
		}
		return -1;
	}
	
	public void retrocedirFinsLog(ExpedientLog expedientLog, boolean retrocedirPerTasques, Long iniciadorId) {
		boolean debugRetroces = true;
		
//		ExpedientLog expedientLog = expedientLogDao.getById(expedientLogId, false);
		JbpmTask jtask = jbpmDao.getTaskById(expedientLog.getTargetId());
		
		// Variables per a desar la informació per a executar el node enter al final de tot
//		long nodeEnterObjectId = 0;
		Long nodeEnterTokenId = null;
				
		mesuresTemporalsHelper.mesuraIniciar("Retrocedir" + (retrocedirPerTasques ? " per tasques" : ""), "expedient", expedientLog.getExpedient().getTipus().getNom(), null, "findAmbExpedientIdOrdenatsPerData");
		List<ExpedientLog> expedientLogs = expedientLogDao.findAmbExpedientIdOrdenatsPerData(
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
//		LogObject currentLog = null;
		Node nodeDesti = null;
		boolean tascaActual = false;
		boolean retrocedirTascaActualCompleta = retrocedirPerTasques;
		
		if (debugRetroces)
			printLogs(logsJbpm);
		jbpmDao.addProcessInstanceMessageLog(
				expedientLog.getExpedient().getProcessInstanceId(),
				getMessageLogPerTipus(retrocedirPerTasques ? ExpedientLogAccioTipus.EXPEDIENT_RETROCEDIR_TASQUES : ExpedientLogAccioTipus.EXPEDIENT_RETROCEDIR));
		if (logsJbpm != null && !logsJbpm.isEmpty()) {
			mesuresTemporalsHelper.mesuraIniciar("Retrocedir" + (retrocedirPerTasques ? " per tasques" : ""), "expedient", expedientLog.getExpedient().getTipus().getNom(), null, "getAccionsJbpmPerRetrocedir");
			// Recull totes les accions executades a jBPM relacionats amb els logs
			Collection<LogObject> logObjects = getAccionsJbpmPerRetrocedir(
					expedientLogs,
					logsJbpm);
			if (debugRetroces) {
				for (LogObject logo: logObjects) {
					String logInfo =">>> [RETPRN] ";
					switch (logo.getTipus()) {
					case LogObject.LOG_OBJECT_PROCES:
						logInfo = logInfo + "PROCES ";
						break;
					case LogObject.LOG_OBJECT_TOKEN:
						logInfo = logInfo + "TOKEN ";
						break;
					case LogObject.LOG_OBJECT_TASK:
						logInfo = logInfo + "TASCA ";
						break;
					case LogObject.LOG_OBJECT_VARTASCA:
						logInfo = logInfo + "VARTASCA ";
						break;
					case LogObject.LOG_OBJECT_VARPROCES:
						logInfo = logInfo + "VARPROCES ";
						break;
					case LogObject.LOG_OBJECT_ACTION:
						logInfo = logInfo + "ACTION ";
						break;
					case LogObject.LOG_OBJECT_INFO:
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
			for (LogObject logo: logObjects) {
				if (logo.getTipus() == LogObject.LOG_OBJECT_ACTION) {
					String varName = BasicActionHandler.PARAMS_RETROCEDIR_VARIABLE_PREFIX + new Long(logo.getObjectId());
					String params = (String)jbpmDao.getProcessInstanceVariable(
							new Long(logo.getProcessInstanceId()).toString(),
							varName);
					paramsAccio.put(new Long(logo.getObjectId()), params);
				}
			}
			
			// comprovam si estem retrocedint únicament la tasca actual
			if (jtask != null) {
				JbpmProcessInstance pi = jbpmDao.getProcessInstance(String.valueOf(expedientLog.getProcessInstanceId()));
				currentToken = jbpmDao.getProcessLogById(expedientLog.getJbpmLogId()).getToken();
				Collection<TaskInstance> tis = pi.getProcessInstance().getTaskMgmtInstance().getUnfinishedTasks(currentToken);
				for (TaskInstance ti: tis) {
					if (ti.getId() == jtask.getTask().getId()){
						nodeDesti = ti.getTask().getTaskNode();
						tascaActual = true;
						if (!retrocedirPerTasques) {
							List<ExpedientLog> expedientTascaLogs = expedientLogDao.findLogsTascaById(expedientLog.getTargetId());
							if (expedientTascaLogs != null && expedientTascaLogs.size() == expedientLogs.size()) {
								retrocedirTascaActualCompleta = true;
							}
						}
						if (debugRetroces)
							logger.info(">>> [LOGTASK] Retroces de la tasca actual (" + nodeDesti + ")!");
						break;
					}
				}
			}
			
			mesuresTemporalsHelper.mesuraCalcular("Retrocedir" + (retrocedirPerTasques ? " per tasques" : ""), "expedient", expedientLog.getExpedient().getTipus().getNom(), null, "obtenir dades per retroces");
			mesuresTemporalsHelper.mesuraIniciar("Retrocedir" + (retrocedirPerTasques ? " per tasques" : ""), "expedient", expedientLog.getExpedient().getTipus().getNom(), null, "realitzar accions de retroces");
			// Executa les accions necessàries per a retrocedir l'expedient
			for (LogObject logo: logObjects) {
				boolean created = logo.getAccions().contains(LogObject.LOG_ACTION_CREATE);
				// boolean update = logo.getAccions().contains(LogObject.LOG_ACTION_UPDATE);
				boolean deleted = logo.getAccions().contains(LogObject.LOG_ACTION_DELETE);
				boolean started = logo.getAccions().contains(LogObject.LOG_ACTION_START);
				boolean ended = logo.getAccions().contains(LogObject.LOG_ACTION_END);
				boolean assigned = logo.getAccions().contains(LogObject.LOG_ACTION_ASSIGN);
				switch (logo.getTipus()) {
				case LogObject.LOG_OBJECT_PROCES:
					if (started && !ended) {
						if (debugRetroces)
							logger.info(">>> [RETLOG] Cancel·lar/finalitzar procés (" + logo.getName() + ")");
						jbpmDao.cancelProcessInstance(logo.getObjectId());
					} else if (!started && ended) {
						if (debugRetroces)
							logger.info(">>> [RETLOG] Desfer finalitzar procés (" + logo.getName() + ")");
						jbpmDao.revertProcessInstanceEnd(logo.getObjectId());
						JbpmProcessInstance jpi = jbpmDao.getProcessInstance(String.valueOf(logo.getProcessInstanceId()));
						if (debugRetroces)
							logger.info(">>> [RETLOG] Desfer finalitzar token (" + jpi.getProcessInstance().getRootToken().getFullName() + ")");
						jbpmDao.revertTokenEnd(jpi.getProcessInstance().getRootToken().getId());
					}
					break;
				case LogObject.LOG_OBJECT_TOKEN:
					if (debugRetroces) {
						JbpmToken jtok = jbpmDao.getTokenById(String.valueOf(logo.getObjectId()));
						logger.info(">>> [LOGTOKEN] Inici Retroces token (" + logo.getName() + ") - End: " + jtok.getToken().getEnd());
					}
					if (started && !ended) {
						if (debugRetroces)
							logger.info(">>> [RETLOG] Cancel·lar token (" + logo.getName() + ")");
						jbpmDao.cancelToken(logo.getObjectId());
						
						if (debugRetroces) {
							JbpmToken jtok = jbpmDao.getTokenById(String.valueOf(logo.getObjectId()));
							logger.info(">>> [LOGTOKEN] Retroces token cancelat (" + logo.getName() + ") - End: " + jtok.getToken().getEnd());
						}
					} else if (!started && ended) {
						if (debugRetroces)
							logger.info(">>> [RETLOG] Desfer finalitzar token (" + logo.getName() + ")");
						jbpmDao.revertTokenEnd(logo.getObjectId());
						
						if (debugRetroces) {
							JbpmToken jtok = jbpmDao.getTokenById(String.valueOf(logo.getObjectId()));
							logger.info(">>> [LOGTOKEN] Retroces revert token end (" + logo.getName() + ") - End: " + jtok.getToken().getEnd());
						}
					}
					if (!started) {
						// Només ha d'executar el node si no és una instància de procés
						// o un join o un fork
						String desti = (String)logo.getValorInicial();
						if (jbpmDao.isJoinNode(logo.getProcessInstanceId(), (String)logo.getValorInicial())) {
							Node joinNode = jbpmDao.getNodeByName(logo.getProcessInstanceId(), desti);
							Node forkNode = getForkNode(logo.getProcessInstanceId(), joinNode);
							if (forkNode != null)
								desti = forkNode.getName();
						} 
						
						if (debugRetroces) {
							JbpmToken jtok = jbpmDao.getTokenById(String.valueOf(logo.getObjectId()));
							logger.info(">>> [LOGTOKEN] Retroces abans token redirect (" + logo.getName() + ") - End: " + jtok.getToken().getEnd());
						}
						
//						boolean enterNode = (nodeRetrocedir == logo.getLogId());
//						JbpmTask jtask = jbpmDao.getTaskById(expedientLog.getTargetId());
						boolean enterNode = false;
						Node ndesti = jbpmDao.getNodeByName(logo.getProcessInstanceId(), desti);
						if (ndesti != null)
							enterNode = retrocedirPerTasques && (ndesti.getId() == jtask.getTask().getTask().getTaskNode().getId()); // és la tasca a la que volem retrocedir!!
						boolean executeNode = (!jbpmDao.isProcessStateNodeJoinOrFork( //(!jbpmDao.isProcessStateNode(
								logo.getProcessInstanceId(),
								(String)logo.getValorInicial()));
						if (enterNode) {
//							nodeEnterObjectId = logo.getObjectId();
							nodeEnterTokenId = logo.getTokenId();
						}
						if (debugRetroces)
							logger.info(">>> [RETLOG] Retornar token (name=" + logo.getName() + ") al node (name=" + desti + ", enter = " + enterNode + ", execute=" + executeNode + ")");
						jbpmDao.tokenRedirect(
								logo.getObjectId(),
								desti,
								true,
								enterNode,
								executeNode);
						
						if (debugRetroces) {
							JbpmToken jtok = jbpmDao.getTokenById(String.valueOf(logo.getObjectId()));
							logger.info(">>> [LOGTOKEN] Retroces després token redirect (" + logo.getName() + ") - End: " + jtok.getToken().getEnd());
						}
					}
					break;
				case LogObject.LOG_OBJECT_TASK:
					boolean tascaStarted = jbpmDao.hasStartBetweenLogs(beginLogId, endLogId, logo.getObjectId());
					if (!tascaStarted && assigned) {
						JbpmTask task = jbpmDao.findEquivalentTaskInstance(logo.getTokenId(), logo.getObjectId());
						String valor = (String)logo.getValorInicial();
						if (valor != null && !"".equals(valor)) {
							if (debugRetroces)
								logger.info(">>> [RETLOG] Reassignar tasca (" + task.getId() + ") a " + valor);
							if (valor.startsWith("[") && valor.endsWith("]")) {
								String[] actors = valor.substring(1, valor.length()-1).split(",");
								jbpmDao.setTaskInstancePooledActors(
										task.getId(),
										actors);
							} else {
								jbpmDao.setTaskInstanceActorId(
										task.getId(),
										valor);
							}
						}
					} else if (!tascaStarted && ended) {
						JbpmTask task = jbpmDao.findEquivalentTaskInstance(logo.getTokenId(), logo.getObjectId());
						if (debugRetroces)
							logger.info(">>> [RETLOG] Copiar variables de la tasca (id=" + logo.getObjectId() + ") a la tasca (id=" + task.getId() + ")");
						Map<String, Object> vars = jbpmDao.getTaskInstanceVariables(new Long(logo.getObjectId()).toString());
						jbpmDao.setTaskInstanceVariables(task.getId(), vars, true);
					}
					break;
				case LogObject.LOG_OBJECT_VARPROCES:
					if (logo.getName() != null) {
						String pid = new Long(logo.getProcessInstanceId()).toString();
						if (created && !deleted) {
							if (debugRetroces)
								logger.info(">>> [RETLOG] Esborrar variable " + logo.getName() + " del proces (" + pid + ")");
							jbpmDao.deleteProcessInstanceVariable(
									pid,
									logo.getName());
							if (logo.getName().startsWith(JbpmVars.PREFIX_DOCUMENT)) {
								documentHelper.esborrarDocument(
										null,
										pid,
										documentHelper.getDocumentCodiPerVariableJbpm(logo.getName()));
							}
						} else if (!created && deleted) {
							if (debugRetroces)
								logger.info(">>> [RETLOG] Crear variable " + logo.getName() + " del proces (" + pid + ") amb el valor (" + logo.getValorInicial() + ")");
							if (logo.getName().startsWith(JbpmVars.PREFIX_DOCUMENT)) {
								// Si existissin versions de documents no s'hauria de fer res
								Long documentStoreId = documentHelper.actualitzarDocument(
										null,
										pid,
										documentHelper.getDocumentCodiPerVariableJbpm(logo.getName()),
										"Document buit",
										new Date(),
										"document.pdf",
										getContingutRecurs("document_buit.pdf"),
										false);
								jbpmDao.setProcessInstanceVariable(
										pid,
										logo.getName(),
										documentStoreId);
							} else {
								jbpmDao.setProcessInstanceVariable(
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
										documentHelper.getDocumentCodiPerVariableJbpm(logo.getName()),
										"Document buit",
										new Date(),
										"document.pdf",
										getContingutRecurs("document_buit.pdf"),
										false);
								jbpmDao.setProcessInstanceVariable(
										pid,
										logo.getName(),
										documentStoreId);
							} else {
								jbpmDao.setProcessInstanceVariable(
										pid,
										logo.getName(),
										logo.getValorInicial());
							}
						}
					}
					break;
				case LogObject.LOG_OBJECT_VARTASCA:
					if (logo.getName() != null) {
						// Només processa el log si la tasca a la qual correspon la modificació
						// no s'ha iniciat a dins els logs que volem retrocedir
						boolean hiHaLogTasca = false;
						boolean logTascaStarted = false;
						for (LogObject lo: logObjects) {
							if (lo.getTipus() == LogObject.LOG_OBJECT_TASK && lo.getObjectId() == logo.getTaskInstanceId()) {
								hiHaLogTasca = true;
//								logTascaStarted = lo.getAccions().contains(LogObject.LOG_ACTION_START);
								logTascaStarted = jbpmDao.hasStartBetweenLogs(beginLogId, endLogId, logo.getTaskInstanceId());
								break;
							}
						}
						if (!hiHaLogTasca || (hiHaLogTasca && !logTascaStarted)) {
							JbpmTask task = jbpmDao.findEquivalentTaskInstance(logo.getTokenId(), logo.getTaskInstanceId());
								//String tid = new Long(logo.getTaskInstanceId()).toString();
							if (created && !deleted) {
								if (debugRetroces)
									logger.info(">>> [RETLOG] Esborrar variable " + logo.getName() + " de la tasca (" + task.getId() + ")");
								jbpmDao.deleteTaskInstanceVariable(
										task.getId(),
										logo.getName());
								// Si la variable ha estat creada mitjançant el DefaultControllerHandler fa un setVariableLocally
								Tasca tasca = tascaDao.findAmbActivityNameIProcessDefinitionId(
										task.getTaskName(),
										task.getProcessDefinitionId());
								CampTasca campTasca = campTascaDao.findAmbTascaCodi(
										tasca.getId(),
										logo.getName());
								if (campTasca != null) {
									jbpmDao.setTaskInstanceVariable(
											task.getId(),
											logo.getName(),
											null);
								}
								// Si la variable correspon a un document vol dir que també l'hem d'esborrar
								if (logo.getName().startsWith(JbpmVars.PREFIX_DOCUMENT)) {
									documentHelper.esborrarDocument(
											task.getId(),
											null,
											documentHelper.getDocumentCodiPerVariableJbpm(logo.getName()));
								}
							} else if (!created && deleted) {
								if (debugRetroces)
									logger.info(">>> [RETLOG] Crear variable " + logo.getName() + " de la tasca (" + task.getId() + ") amb el valor (" + logo.getValorInicial() + ")");
								if (logo.getName().startsWith(JbpmVars.PREFIX_DOCUMENT)) {
									// Si existissin versions de documents no s'hauria de fer res
									Long documentStoreId = documentHelper.actualitzarDocument(
											task.getId(),
											null,
											documentHelper.getDocumentCodiPerVariableJbpm(logo.getName()),
											"Document buit",
											new Date(),
											"document.pdf",
											getContingutRecurs("document_buit.pdf"),
											false);
									jbpmDao.setTaskInstanceVariable(
											task.getId(),
											logo.getName(),
											documentStoreId);
								} else {
									jbpmDao.setTaskInstanceVariable(
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
											documentHelper.getDocumentCodiPerVariableJbpm(logo.getName()),
											"Document buit",
											new Date(),
											"document.pdf",
											getContingutRecurs("document_buit.pdf"),
											false);
									jbpmDao.setTaskInstanceVariable(
											task.getId(),
											logo.getName(),
											documentStoreId);
								} else {
									jbpmDao.setTaskInstanceVariable(
											task.getId(),
											logo.getName(),
											logo.getValorInicial());
								}
							}
						}
					}
					break;
				case LogObject.LOG_OBJECT_ACTION:
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
					jbpmDao.retrocedirAccio(
							pid,
							logo.getName(),
							params,
							null);
					break;
				case LogObject.LOG_OBJECT_INFO:
					Expedient expedient = getExpedientPerProcessInstanceId(String.valueOf(logo.getProcessInstanceId()));
					
					LogInfo li = LogInfo.valueOf(logo.getName());
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
		// Retrocedeix les accions no jBPM
		/*Collections.reverse(expedientLogs);
		for (ExpedientLog elog: expedientLogs) {
			if (ExpedientLogAccioTipus.EXPEDIENT_ATURAR.equals(elog.getAccioTipus())) {
				elog.getExpedient().setInfoAturat(null);
			} else if (ExpedientLogAccioTipus.EXPEDIENT_REPRENDRE.equals(elog.getAccioTipus())) {
				elog.getExpedient().setInfoAturat(elog.getAccioParams());
			} else if (ExpedientLogAccioTipus.EXPEDIENT_RELACIO_AFEGIR.equals(elog.getAccioTipus())) {
				
			} else if (ExpedientLogAccioTipus.EXPEDIENT_RELACIO_ESBORRAR.equals(elog.getAccioTipus())) {
				
			}
		}*/
		
		// Si retrocedim la tasca actual...
		if (tascaActual && retrocedirTascaActualCompleta) {
//			Node ndesti = jbpmDao.getNodeByName(expedientLog.getProcessInstanceId(), desti);
			boolean enterNode = retrocedirPerTasques; //&& (nodeDesti.getId() == jtask.getTask().getTask().getTaskNode().getId()); // és la tasca a la que volem retrocedir!!
			boolean executeNode = (!jbpmDao.isProcessStateNodeJoinOrFork( //(!jbpmDao.isProcessStateNode(
					expedientLog.getProcessInstanceId(),
					nodeDesti.getName()));
			if (enterNode) {
//				nodeEnterObjectId =  currentToken.getId();
				nodeEnterTokenId = currentToken.getId();
			}
			if (debugRetroces)
				logger.info(">>> [RETLOG] Retornar token (name=" + currentToken.getName() + ") al node (name=" + nodeDesti.getName() + ", enter = " + enterNode + ", execute=" + executeNode + ")");
			jbpmDao.tokenRedirect(
					currentToken.getId(),
					nodeDesti.getName(),
					true,
					enterNode,
					executeNode);
			
			if (debugRetroces) {
				logger.info(">>> [LOGTOKEN] Retroces després token redirect (" + currentToken.getName() + ") - End: " + currentToken.getEnd());
			}
		}
		if (retrocedirPerTasques && nodeEnterTokenId != null) {//nodeEnterObjectId > 0) {
			JbpmTask task = jbpmDao.findEquivalentTaskInstance(nodeEnterTokenId, Long.valueOf(expedientLog.getTargetId()));
			TaskInstance ti = task.getTask();
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
			if (ExpedientLogEstat.NORMAL.equals(elog.getEstat()))
				if (retrocedirPerTasques) {
					elog.setEstat(ExpedientLogEstat.RETROCEDIT_TASQUES);
				} else {
					elog.setEstat(ExpedientLogEstat.RETROCEDIT);
				}
			if (elog.getId() != iniciadorId) elog.setIniciadorRetroces(iniciadorId);
			// Retrocediex la informació de l'expedient
		}
	}
	
	private List<CampTasca> getCampsPerTaskInstance(TaskInstance taskInstance) {
		long processDefinitionId = taskInstance.getProcessInstance().getProcessDefinition().getId();
		Tasca tasca = tascaDao.findAmbActivityNameIProcessDefinitionId(
				taskInstance.getTask().getName(),
				new Long(processDefinitionId).toString());
		return tasca.getCamps();
	}
	private List<DocumentTasca> getDocumentsPerTaskInstance(TaskInstance taskInstance) {
		long processDefinitionId = taskInstance.getProcessInstance().getProcessDefinition().getId();
		Tasca tasca = tascaDao.findAmbActivityNameIProcessDefinitionId(
				taskInstance.getTask().getName(),
				new Long(processDefinitionId).toString());
		return tasca.getDocuments();
	}

	public String getActorsPerReassignacioTasca(String taskInstanceId) {
		JbpmTask task = jbpmDao.getTaskById(taskInstanceId);
		String actors = "";
		if (task.getAssignee() != null) {
			actors = task.getAssignee();
		} else {
			if (task.getPooledActors().size() > 0) {
				StringBuilder sb = new StringBuilder();
				for (String actorId: task.getPooledActors()) {
					sb.append(actorId);
					sb.append(",");
				}
				actors = "[" + sb.substring(0, sb.length() -1) + "]";
			}
		}
		return actors;
	}

	public List<ExpedientLog> findLogsRetrocedits(Long expedientLogId) {
		List<ExpedientLog> resposta = new ArrayList<ExpedientLog>();
		ExpedientLog expedientLog = expedientLogDao.getById(expedientLogId, false);
		if (ExpedientLogAccioTipus.EXPEDIENT_RETROCEDIR.equals(expedientLog.getAccioTipus()) ||
			ExpedientLogAccioTipus.EXPEDIENT_RETROCEDIR_TASQUES.equals(expedientLog.getAccioTipus())) {
			List<ExpedientLog> logs = expedientLogDao.findAmbExpedientRetroceditIdOrdenatsPerData(
					expedientLog.getId());
			
			if (logs != null && logs.size() > 0) {
				resposta = logs;
			} else {
				// Aquí no ha d'entrar al retrocedir per tasques, degut a que sempre tindrà la
				// referència al log iniciador del retrocés
				List<ExpedientLog> expedientLogs = expedientLogDao.findAmbExpedientIdOrdenatsPerData(
						expedientLog.getExpedient().getId());
				Long idInicialExclos = null;
				Long idFinalExclos = null;
				for (ExpedientLog elog: expedientLogs) {
					if (elog.getId().equals(expedientLogId))
						break;
					if (ExpedientLogAccioTipus.EXPEDIENT_RETROCEDIR.equals(elog.getAccioTipus())) {
						idInicialExclos = new Long(elog.getAccioParams());
						idFinalExclos = elog.getId();
					}
				}
				Long idInicial = new Long(expedientLog.getAccioParams());
				for (ExpedientLog elog: expedientLogs) {
					if (elog.getId().equals(expedientLogId))
						break;
					if (elog.getId().longValue() >= idInicial.longValue() && ExpedientLogEstat.RETROCEDIT.equals(elog.getEstat())) {
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
	
	public JbpmToken getTokenByJbpmLogId(Long jbpmLogId){
		ProcessLog pl = jbpmDao.getProcessLogById(jbpmLogId);
		if (pl == null)
			return null;
		return new JbpmToken(pl.getToken());
	}

	public void printProcessInstanceLogs(String processInstanceId) {
		Map<Token, List<ProcessLog>> logsPerToken = jbpmDao.getProcessInstanceLogs(processInstanceId);
		for (Token token: logsPerToken.keySet()) {
			logger.info(">>> [PRILOG] " + token);
			printLogs(logsPerToken.get(token));
		}
	}
	public void printLogs(List<ProcessLog> logs) {
		for (ProcessLog log: logs) {
			if (log.getParent() == null) {
				printLogMessage(
						logs,
						log,
						0);
			}
		}
	}

	@Autowired
	public void setJbpmHelper(JbpmHelper jbpmDao) {
		this.jbpmDao = jbpmDao;
	}
	@Autowired
	public void setExpedientLogDao(ExpedientLogDao expedientLogDao) {
		this.expedientLogDao = expedientLogDao;
	}
	@Autowired
	public void setExpedientDao(ExpedientDao expedientDao) {
		this.expedientDao = expedientDao;
	}
	@Autowired
	public void setDocumentHelper(DocumentHelper documentHelper) {
		this.documentHelper = documentHelper;
	}
	@Autowired
	public void setCampDao(CampDao campDao) {
		this.campDao = campDao;
	}
	@Autowired
	public void setTascaDao(TascaDao tascaDao) {
		this.tascaDao = tascaDao;
	}
	@Autowired
	public void setCampTascaDao(CampTascaDao campTascaDao) {
		this.campTascaDao = campTascaDao;
	}
	@Autowired
	public void setDefinicioProcesDao(DefinicioProcesDao definicioProcesDao) {
		this.definicioProcesDao = definicioProcesDao;
	}
	@Autowired
	public void setEstatDao(EstatDao estatDao) {
		this.estatDao = estatDao;
	}
	@Autowired
	public void setMesuresTemporalsHelper(MesuresTemporalsHelper mesuresTemporalsHelper) {
		this.mesuresTemporalsHelper = mesuresTemporalsHelper;
	}
	@Autowired
	public void setMessageSource(MessageSource messageSource) {
		this.messageSource = messageSource;
	}

	private Expedient getExpedientPerProcessInstanceId(String processInstanceId) {
		JbpmProcessInstance pi = jbpmDao.getRootProcessInstance(processInstanceId);
		if (pi == null) 
			throw new NotFoundException( getMessage("error.expedientLogHelper.noExisteix.processInstance", new Object[]{processInstanceId}) );
		return expedientDao.findAmbProcessInstanceId(pi.getId());
	}

	private String getMessageLogPerTipus(ExpedientLogAccioTipus tipus) {
		return MESSAGE_LOG_PREFIX + tipus.name();
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
	
	private List<ProcessLog> getLogsJbpmPerExpedientLog(ExpedientLog expedientLog) {
		List<ProcessLog> logsJbpm = new ArrayList<ProcessLog>();
		List<ProcessLog> logsJbpmAfegir = new ArrayList<ProcessLog>();
		if (expedientLog.getJbpmLogId() != null && expedientLog.getEstat().equals(ExpedientLogEstat.NORMAL)) {
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
	
	private List<ProcessLog> getJbpmLogsPerInstanciaProces(Long processInstanceId, boolean asc) {
		 
		List<ProcessLog> logsJbpm = new ArrayList<ProcessLog>();
		 
		JbpmProcessInstance pi = jbpmDao.getRootProcessInstance(processInstanceId.toString());
		List<JbpmProcessInstance> processos = jbpmDao.getProcessInstanceTree(pi.getId());
		for (JbpmProcessInstance proces: processos) {
			Map<Token, List<ProcessLog>> logsPerInstanciaProces = jbpmDao.getProcessInstanceLogs(
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
	
	private Collection<LogObject> getAccionsJbpmPerRetrocedir(
			List<ExpedientLog> expedientLogs,
			List<ProcessLog> logsSorted) {
		Map<Long, LogObject> logObjects = new HashMap<Long, LogObject>();
		long currentMessageLogId = -1;
		for (ProcessLog plog: logsSorted) {
			if (plog instanceof MessageLog) {
				MessageLog mlog = (MessageLog)plog;
				if (mlog.getMessage().startsWith(MESSAGE_LOGINFO_PREFIX)) {
					
					Long objId = new Long(plog.getToken().getProcessInstance().getId());
					LogObject lobj = logObjects.get(objId);
					
					if (lobj == null) {
						String sTipus = mlog.getMessage().substring(mlog.getMessage().indexOf("::") + 2, mlog.getMessage().indexOf("#@#"));
						String info = mlog.getMessage().substring(mlog.getMessage().indexOf("#@#") + 3);
						
						LogInfo li = LogInfo.valueOf(sTipus);
						
						lobj = new LogObject(
								objId.longValue(),
								plog.getId(),
								//objId.toString(),
								li.name(),
								LogObject.LOG_OBJECT_INFO,
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
										lobj.setValorInicial(estatDao.getById(estatId, false));
									}
									break;
								case GEOPOSICIOX:
								case GEOPOSICIOY:
									lobj.setValorInicial(Double.parseDouble(info));
									break;
								default:
									break;
							}
							logObjects.put(objId, lobj);
							
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
				LogObject lobj = logObjects.get(objId);
				if (lobj == null) {
					lobj = new LogObject(
							objId.longValue(),
							plog.getId(),
							taskInstance.getName(),
							LogObject.LOG_OBJECT_TASK,
							plog.getToken().getProcessInstance().getId(),
							plog.getToken().getId());
					logObjects.put(objId, lobj);
				}
				if (plog instanceof TaskCreateLog)
					lobj.addAccio(LogObject.LOG_ACTION_START);
				if (plog instanceof TaskEndLog)
					lobj.addAccio(LogObject.LOG_ACTION_END);
				if (plog instanceof TaskAssignLog) {
					lobj.addAccio(LogObject.LOG_ACTION_ASSIGN);
					lobj.setValorInicial(((TaskAssignLog)plog).getTaskOldActorId());
					ExpedientLog expedientLog = getExpedientLogPerJbpmLogId(expedientLogs, currentMessageLogId);
					if (expedientLog != null && expedientLog.getAccioTipus().equals(ExpedientLogAccioTipus.TASCA_REASSIGNAR)) {
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
					DefinicioProces pDef = definicioProcesDao.findAmbJbpmId(String.valueOf(variableInstance.getProcessInstance().getProcessDefinition().getId()));
					Camp camp = campDao.findAmbDefinicioProcesICodi(
							pDef.getId(),
							codi);		
					if(camp != null){
						ignored = camp.isIgnored();
					}
					if (!ignored) {
						if (variableInstance.getName() != null || variableInstance.getValue() != null) {
							Long variableInstanceId = jbpmDao.getVariableIdFromVariableLog(plog.getId());
							Long taskInstanceId = jbpmDao.getTaskIdFromVariableLog(plog.getId());
							/*String idAddicional = null;
							if (variableInstance.getProcessInstance() != null)
								idAddicional = new Long(variableInstance.getProcessInstance().getId()).toString();
							if (taskInstanceId != null)
								idAddicional = taskInstanceId.toString();*/
							Long objId = new Long(variableInstanceId);
							LogObject lobj = logObjects.get(objId);
							if (lobj == null) {
								lobj = new LogObject(
										objId.longValue(),
										plog.getId(),
										variableInstance.getName(),
										(taskInstanceId != null) ? LogObject.LOG_OBJECT_VARTASCA : LogObject.LOG_OBJECT_VARPROCES,
										plog.getToken().getProcessInstance().getId(),
										plog.getToken().getId());
								if (taskInstanceId != null) {
									lobj.setTaskInstanceId(taskInstanceId.longValue());
									/*// El següent és per tractar els casos en que s'esborra una variable
									// a dins una instància de tasca i el nom de la variableInstance queda
									// amb valor null.
									if (lobj.getName() == null) {
										if (expedientLog.getAccioTipus().equals(ExpedientLogAccioTipus.TASCA_DOCUMENT_ESBORRAR))
											lobj.setName(DocumentHelper.PREFIX_VAR_DOCUMENT + expedientLog.getAccioParams());
									}*/
								}
								logObjects.put(objId, lobj);
							}
							if (plog instanceof VariableCreateLog)
								lobj.addAccio(LogObject.LOG_ACTION_CREATE);
							if (plog instanceof VariableUpdateLog) {
								VariableUpdateLog vulog = (VariableUpdateLog)plog;
								lobj.addAccio(LogObject.LOG_ACTION_UPDATE);
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
								lobj.addAccio(LogObject.LOG_ACTION_DELETE);
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
				LogObject lobj = logObjects.get(objId);
				if (lobj == null) {
					String tokenName = token.getName();
					if (tokenName == null && token.isRoot())
						tokenName = "[ROOT]";
					lobj = new LogObject(
							objId.longValue(),
							plog.getId(),
							tokenName,
							LogObject.LOG_OBJECT_TOKEN,
							plog.getToken().getProcessInstance().getId(),
							plog.getToken().getId());
					logObjects.put(objId, lobj);
				}
				if (plog instanceof TokenCreateLog)
					lobj.addAccio(LogObject.LOG_ACTION_START);
				if (plog instanceof TokenEndLog)
					lobj.addAccio(LogObject.LOG_ACTION_END);
				if (plog instanceof TransitionLog) {
					TransitionLog trlog = (TransitionLog)plog;
					lobj.addAccio(LogObject.LOG_ACTION_UPDATE);
					lobj.setValorInicial(trlog.getSourceNode().getName());
				}
			} else if (plog instanceof ProcessInstanceCreateLog || plog instanceof ProcessInstanceEndLog) {
				Long objId = new Long(plog.getToken().getProcessInstance().getId());
				LogObject lobj = logObjects.get(objId);
				if (lobj == null) {
					lobj = new LogObject(
							objId.longValue(),
							plog.getId(),
							objId.toString(),
							LogObject.LOG_OBJECT_PROCES,
							plog.getToken().getProcessInstance().getId(),
							plog.getToken().getId());
					logObjects.put(objId, lobj);
				}
				if (plog instanceof ProcessInstanceCreateLog)
					lobj.addAccio(LogObject.LOG_ACTION_START);
				if (plog instanceof ProcessInstanceEndLog)
					lobj.addAccio(LogObject.LOG_ACTION_END);
			} else if (plog instanceof ActionLog) {
				Long objId = ((ActionLog)plog).getAction().getId();
				LogObject lobj = logObjects.get(objId);
				if (lobj == null) {
					lobj = new LogObject(
							objId.longValue(),
							plog.getId(),
							((ActionLog)plog).getAction().getName(),
							LogObject.LOG_OBJECT_ACTION,
							plog.getToken().getProcessInstance().getId(),
							plog.getToken().getId());
					logObjects.put(objId, lobj);
				}
				lobj.addAccio(LogObject.LOG_ACTION_EXEC);
			}
		//}
		}
		List<LogObject> logsOrdenats = new ArrayList<LogObject>(logObjects.values());
		Collections.sort(
				logsOrdenats,
				new Comparator<LogObject>() {
					public int compare(LogObject o1, LogObject o2) {
						Long l1 = new Long(o1.getLogId());
						Long l2 = new Long(o2.getLogId());
						return l1.compareTo(l2);
					}
				});
		Collections.reverse(logsOrdenats);
		return logsOrdenats;
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

	@SuppressWarnings("unused")
	private class LogObject {
		public static final int LOG_OBJECT_PROCES = 0;
		public static final int LOG_OBJECT_TOKEN = 1;
		public static final int LOG_OBJECT_TASK = 2;
		public static final int LOG_OBJECT_VARTASCA = 3;
		public static final int LOG_OBJECT_VARPROCES = 4;
		public static final int LOG_OBJECT_ACTION = 5;
		public static final int LOG_OBJECT_INFO = 6;
		public static final String LOG_ACTION_CREATE = "C";
		public static final String LOG_ACTION_UPDATE = "U";
		public static final String LOG_ACTION_DELETE = "D";
		public static final String LOG_ACTION_START = "S";
		public static final String LOG_ACTION_END = "E";
		public static final String LOG_ACTION_ASSIGN = "A";
		public static final String LOG_ACTION_EXEC = "X";
		private long objectId;
		private long logId;
		private String name;
		private long processInstanceId;
		private long tokenId;
		private long taskInstanceId;
		private int tipus;
		private List<String> accions = new ArrayList<String>();
		private Object valorInicial = null;
		public LogObject(long objectId, long logId, String name, int tipus, long processInstanceId, long tokenId) {
			this.objectId = objectId;
			this.logId = logId;
			this.name = name;
			this.tipus = tipus;
			this.processInstanceId = processInstanceId;
			this.tokenId = tokenId;
		}
		public long getObjectId() {
			return objectId;
		}
		public void setObjectId(long objectId) {
			this.objectId = objectId;
		}
		public long getLogId() {
			return logId;
		}
		public void setLogId(long logId) {
			this.logId = logId;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public long getProcessInstanceId() {
			return processInstanceId;
		}
		public void setProcessInstanceId(long processInstanceId) {
			this.processInstanceId = processInstanceId;
		}
		public long getTokenId() {
			return tokenId;
		}
		public void setTokenId(long tokenId) {
			this.tokenId = tokenId;
		}
		public long getTaskInstanceId() {
			return taskInstanceId;
		}
		public void setTaskInstanceId(long taskInstanceId) {
			this.taskInstanceId = taskInstanceId;
		}
		public int getTipus() {
			return tipus;
		}
		public void setTipus(int tipus) {
			this.tipus = tipus;
		}
		public List<String> getAccions() {
			return accions;
		}
		public void setAccions(List<String> accions) {
			this.accions = accions;
		}
		public void addAccio(String accio) {
			accions.add(accio);
		}
		public Object getValorInicial() {
			return valorInicial;
		}
		public void setValorInicial(Object valorInicial) {
			if (this.valorInicial == null)
				this.valorInicial = valorInicial;
		}
	}

	protected String getMessage(String key, Object[] vars) {
		try {
			return messageSource.getMessage(
					key,
					vars,
					null);
		} catch (NoSuchMessageException ex) {
			return "???" + key + "???";
		}
	}

	protected String getMessage(String key) {
		return getMessage(key, null);
	}
	
	private static final Log logger = LogFactory.getLog(ExpedientLogHelper.class);

}
