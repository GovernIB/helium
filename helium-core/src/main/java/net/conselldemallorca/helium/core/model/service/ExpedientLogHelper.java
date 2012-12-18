/**
 * 
 */
package net.conselldemallorca.helium.core.model.service;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.conselldemallorca.helium.core.model.dao.CampDao;
import net.conselldemallorca.helium.core.model.dao.DefinicioProcesDao;
import net.conselldemallorca.helium.core.model.dao.ExpedientDao;
import net.conselldemallorca.helium.core.model.dao.ExpedientLogDao;
import net.conselldemallorca.helium.core.model.hibernate.Camp;
import net.conselldemallorca.helium.core.model.hibernate.DefinicioProces;
import net.conselldemallorca.helium.core.model.hibernate.Expedient;
import net.conselldemallorca.helium.core.model.hibernate.ExpedientLog;
import net.conselldemallorca.helium.core.model.hibernate.ExpedientLog.ExpedientLogAccioTipus;
import net.conselldemallorca.helium.core.model.hibernate.ExpedientLog.ExpedientLogEstat;
import net.conselldemallorca.helium.jbpm3.handlers.BasicActionHandler;
import net.conselldemallorca.helium.jbpm3.integracio.JbpmDao;
import net.conselldemallorca.helium.jbpm3.integracio.JbpmProcessInstance;
import net.conselldemallorca.helium.jbpm3.integracio.JbpmTask;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jbpm.context.exe.VariableInstance;
import org.jbpm.context.log.VariableCreateLog;
import org.jbpm.context.log.VariableDeleteLog;
import org.jbpm.context.log.VariableLog;
import org.jbpm.context.log.VariableUpdateLog;
import org.jbpm.graph.exe.Token;
import org.jbpm.graph.log.ActionLog;
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
import org.springframework.security.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * Helper per a gestionar els logs dels expedients
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class ExpedientLogHelper {

	private static final String MESSAGE_LOG_PREFIX = "[H3l1um]";

	private JbpmDao jbpmDao;
	private ExpedientLogDao expedientLogDao;
	private ExpedientDao expedientDao;
	private DocumentHelper documentHelper;
	private CampDao campDao;
	private DefinicioProcesDao definicioProcesDao;
	

	public ExpedientLog afegirLogExpedientPerTasca(
			String taskInstanceId,
			ExpedientLogAccioTipus tipus,
			String accioParams) {
		long jbpmLogId = jbpmDao.addTaskInstanceMessageLog(
				taskInstanceId,
				getMessageLogPerTipus(tipus));
		JbpmTask task = jbpmDao.getTaskById(taskInstanceId);
		Expedient expedient = getExpedientPerProcessInstanceId(task.getProcessInstanceId());
		String usuari = "Timer";
		try {
			usuari = SecurityContextHolder.getContext().getAuthentication().getName();
		}catch (Exception e){}
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
		long jbpmLogId = jbpmDao.addProcessInstanceMessageLog(
				processInstanceId,
				getMessageLogPerTipus(tipus));
		Expedient expedient = getExpedientPerProcessInstanceId(processInstanceId);
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

	public void retrocedirFinsLog(Long expedientLogId) {
		boolean debugRetroces = false;
		ExpedientLog expedientLog = expedientLogDao.getById(expedientLogId, false);
		List<ExpedientLog> expedientLogs = expedientLogDao.findAmbExpedientIdOrdenatsPerData(
				expedientLog.getExpedient().getId());
		// Retrocedeix els canvis al jBPM relacionats amb els logs
		List<ProcessLog> logsJbpm = getLogsJbpmPerRetrocedir(expedientLogs, expedientLogId);
		if (debugRetroces)
			printLogs(logsJbpm);
		jbpmDao.addProcessInstanceMessageLog(
				expedientLog.getExpedient().getProcessInstanceId(),
				getMessageLogPerTipus(ExpedientLogAccioTipus.EXPEDIENT_RETROCEDIR));
		if (logsJbpm != null && !logsJbpm.isEmpty()) {
			// Recull totes les accions executades a jBPM relacionats amb els logs
			Collection<LogObject> logObjects = getAccionsJbpmPerRetrocedir(
					expedientLogs,
					logsJbpm);
			if (debugRetroces) {
				for (LogObject logo: logObjects) {
					System.out.print(">>> [RETPRN] ");
					switch (logo.getTipus()) {
					case LogObject.LOG_OBJECT_PROCES:
						System.out.print("PROCES ");
						break;
					case LogObject.LOG_OBJECT_TOKEN:
						System.out.print("TOKEN ");
						break;
					case LogObject.LOG_OBJECT_TASK:
						System.out.print("TASCA ");
						break;
					case LogObject.LOG_OBJECT_VARTASCA:
						System.out.print("VARTASCA ");
						break;
					case LogObject.LOG_OBJECT_VARPROCES:
						System.out.print("VARPROCES ");
						break;
					case LogObject.LOG_OBJECT_ACTION:
						System.out.print("ACTION ");
						break;
					default:
						System.out.print("??? ");
					}
					System.out.print("(" + logo.getName() + ") ");
					for (String accio: logo.getAccions())
						System.out.print(accio);
					System.out.println();
				}
			}
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
							System.out.println(">>> [RETLOG] Cancel·lar/finalitzar procés (" + logo.getName() + ")");
						jbpmDao.cancelProcessInstance(logo.getObjectId());
					} else if (!started && ended) {
						if (debugRetroces)
							System.out.println(">>> [RETLOG] Desfer finalitzar procés (" + logo.getName() + ")");
						jbpmDao.revertProcessInstanceEnd(logo.getObjectId());
					}
					break;
				case LogObject.LOG_OBJECT_TOKEN:
					if (started && !ended) {
						if (debugRetroces)
							System.out.println(">>> [RETLOG] Cancel·lar token (" + logo.getName() + ")");
						jbpmDao.cancelToken(logo.getObjectId());
					} else if (!started && ended) {
						if (debugRetroces)
							System.out.println(">>> [RETLOG] Desfer finalitzar token (" + logo.getName() + ")");
						jbpmDao.revertTokenEnd(logo.getObjectId());
					}
					if (!started) {
						// Només ha d'executar el node si no és una instància de procés
						boolean executeNode = !jbpmDao.isProcessStateNode(
								logo.getProcessInstanceId(),
								(String)logo.getValorInicial());
						if (debugRetroces)
							System.out.println(">>> [RETLOG] Retornar token (name=" + logo.getName() + ") al node (name=" + logo.getValorInicial() + ", execute=" + executeNode + ")");
						jbpmDao.tokenRedirect(
								logo.getObjectId(),
								(String)logo.getValorInicial(),
								true,
								false,
								executeNode);
					}
					break;
				case LogObject.LOG_OBJECT_TASK:
					if (!started && assigned) {
						JbpmTask task = jbpmDao.findEquivalentTaskInstance(logo.getTokenId(), logo.getObjectId());
						String valor = (String)logo.getValorInicial();
						if (debugRetroces)
							System.out.println(">>> [RETLOG] Reassignar tasca (" + task.getId() + ") a " + valor);
						if (valor.contains(",")) {
							String[] actors = valor.split(",");
							jbpmDao.setTaskInstancePooledActors(
									task.getId(),
									actors);
						} else {
							jbpmDao.setTaskInstanceActorId(
									task.getId(),
									valor);
						}
					} else if (!started && ended) {
						JbpmTask task = jbpmDao.findEquivalentTaskInstance(logo.getTokenId(), logo.getObjectId());
						if (debugRetroces)
							System.out.println(">>> [RETLOG] Copiar variables de la tasca (id=" + logo.getObjectId() + ") a la tasca (id=" + task.getId() + ")");
						Map<String, Object> vars = jbpmDao.getTaskInstanceVariables(new Long(logo.getObjectId()).toString());
						jbpmDao.setTaskInstanceVariables(task.getId(), vars, true);
					}
					break;
				case LogObject.LOG_OBJECT_VARPROCES:
					if (logo.getName() != null) {
						String pid = new Long(logo.getProcessInstanceId()).toString();
						if (created && !deleted) {
							if (debugRetroces)
								System.out.println(">>> [RETLOG] Esborrar variable " + logo.getName() + " del proces (" + pid + ")");
							jbpmDao.deleteProcessInstanceVariable(
									pid,
									logo.getName());
							if (logo.getName().startsWith(DocumentHelper.PREFIX_VAR_DOCUMENT)) {
								documentHelper.esborrarDocument(
										null,
										pid,
										documentHelper.getDocumentCodiPerVariableJbpm(logo.getName()));
							}
						} else if (!created && deleted) {
							if (debugRetroces)
								System.out.println(">>> [RETLOG] Crear variable " + logo.getName() + " del proces (" + pid + ") amb el valor (" + logo.getValorInicial() + ")");
							if (logo.getName().startsWith(DocumentHelper.PREFIX_VAR_DOCUMENT)) {
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
								System.out.println(">>> [RETLOG] Actualitzar variable " + logo.getName() + " del proces (" + pid + ") amb el valor (" + logo.getValorInicial() + ")");
							if (logo.getName().startsWith(DocumentHelper.PREFIX_VAR_DOCUMENT)) {
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
								logTascaStarted = lo.getAccions().contains(LogObject.LOG_ACTION_START);
								break;
							}
						}
						if (!hiHaLogTasca || (hiHaLogTasca && !logTascaStarted)) {
							JbpmTask task = jbpmDao.findEquivalentTaskInstance(logo.getTokenId(), logo.getTaskInstanceId());
							//String tid = new Long(logo.getTaskInstanceId()).toString();
							if (created && !deleted) {
								if (debugRetroces)
									System.out.println(">>> [RETLOG] Esborrar variable " + logo.getName() + " de la tasca (" + task.getId() + ")");
								jbpmDao.deleteTaskInstanceVariable(
										task.getId(),
										logo.getName());
								if (logo.getName().startsWith(DocumentHelper.PREFIX_VAR_DOCUMENT)) {
									documentHelper.esborrarDocument(
											task.getId(),
											null,
											documentHelper.getDocumentCodiPerVariableJbpm(logo.getName()));
								}
							} else if (!created && deleted) {
								if (debugRetroces)
									System.out.println(">>> [RETLOG] Crear variable " + logo.getName() + " de la tasca (" + task.getId() + ") amb el valor (" + logo.getValorInicial() + ")");
								if (logo.getName().startsWith(DocumentHelper.PREFIX_VAR_DOCUMENT)) {
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
									System.out.println(">>> [RETLOG] Actualitzar variable " + logo.getName() + " de la tasca (" + task.getId() + ") amb el valor (" + logo.getValorInicial() + ")");
								if (logo.getName().startsWith(DocumentHelper.PREFIX_VAR_DOCUMENT)) {
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
						System.out.println(">>> [RETLOG] Executar accio inversa " + logo.getObjectId());
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
							params);
					break;
				}
			}
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
		// Marca les accions com a retrocedides
		//Collections.reverse(expedientLogs);
		boolean found = false;
		for (ExpedientLog elog: expedientLogs) {
			if (elog.equals(expedientLog))
				found = true;
			if (found && ExpedientLogEstat.NORMAL.equals(elog.getEstat()))
				elog.setEstat(ExpedientLogEstat.RETROCEDIT);
		}
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
				actors = sb.substring(0, sb.length() -1);
			}
		}
		return actors;
	}

	public List<ExpedientLog> findLogsRetrocedits(Long expedientLogId) {
		List<ExpedientLog> resposta = new ArrayList<ExpedientLog>();
		ExpedientLog expedientLog = expedientLogDao.getById(expedientLogId, false);
		if (ExpedientLogAccioTipus.EXPEDIENT_RETROCEDIR.equals(expedientLog.getAccioTipus())) {
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
		return resposta;
	}

	public void printProcessInstanceLogs(String processInstanceId) {
		Map<Token, List<ProcessLog>> logsPerToken = jbpmDao.getProcessInstanceLogs(processInstanceId);
		for (Token token: logsPerToken.keySet()) {
			System.out.println(">>> [PRILOG] " + token);
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
	public void setJbpmDao(JbpmDao jbpmDao) {
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



	private Expedient getExpedientPerProcessInstanceId(String processInstanceId) {
		JbpmProcessInstance pi = jbpmDao.getRootProcessInstance(processInstanceId);
		return expedientDao.findAmbProcessInstanceId(pi.getId());
	}

	private String getMessageLogPerTipus(ExpedientLogAccioTipus tipus) {
		return MESSAGE_LOG_PREFIX + tipus.name();
	}

	private List<ProcessLog> getLogsJbpmPerRetrocedir(
			List<ExpedientLog> expedientLogs,
			Long expedientLogId) {
		List<ProcessLog> logsJbpm = new ArrayList<ProcessLog>();
		boolean found = false;
		for (ExpedientLog elog: expedientLogs) {
			if (elog.getId().equals(expedientLogId))
				found = true;
			if (found) {
				List<ProcessLog> logsJbpmPerAfegir = getLogsJbpmPerExpedientLog(elog);
				if (logsJbpmPerAfegir != null)
					logsJbpm.addAll(logsJbpmPerAfegir);
			}
		}
		return logsJbpm;
	}
	private List<ProcessLog> getLogsJbpmPerExpedientLog(ExpedientLog expedientLog) {
		List<ProcessLog> logsJbpm = new ArrayList<ProcessLog>();
		if (expedientLog.getJbpmLogId() != null && expedientLog.getEstat().equals(ExpedientLogEstat.NORMAL)) {
			// Obtenim els logs jBPM associats a la instància de procés i als seus
			// subprocessos
			JbpmProcessInstance pi = jbpmDao.getRootProcessInstance(expedientLog.getProcessInstanceId().toString());
			List<JbpmProcessInstance> processos = jbpmDao.getProcessInstanceTree(pi.getId());
			for (JbpmProcessInstance proces: processos) {
				Map<Token, List<ProcessLog>> logsPerInstanciaProces = jbpmDao.getProcessInstanceLogs(
						proces.getId());
				for (Token token: logsPerInstanciaProces.keySet()) {
					for (ProcessLog plog: logsPerInstanciaProces.get(token))
						logsJbpm.add(plog);
				}
			}
			Collections.sort(
					logsJbpm,
					new Comparator<ProcessLog>() {
						public int compare(ProcessLog l1, ProcessLog l2) {
							return new Long(l1.getId()).compareTo(new Long(l2.getId()));
						}
					});
			int indexInici = -1;
			int indexFi = -1;
			int index = 0;
			for (ProcessLog plog: logsJbpm) {
				// L'índex inicial correspon al lloc a on es troba el log marcat per jbpmLogId
				if (plog.getId() == expedientLog.getJbpmLogId()) {
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
			if (indexFi == -1)
				indexFi = logsJbpm.size();
			return logsJbpm.subList(indexInici, indexFi);
		} else {
			return null;
		}
	}
	
	@Autowired
	public CampDao getCampDao() {
		return campDao;
	}
	@Autowired
	public void setCampDao(CampDao campDao) {
		this.campDao = campDao;
	}
	@Autowired
	public DefinicioProcesDao getDefinicioProcesDao() {
		return definicioProcesDao;
	}
	@Autowired
	public void setDefinicioProcesDao(DefinicioProcesDao definicioProcesDao) {
		this.definicioProcesDao = definicioProcesDao;
	}
	
	

	private Collection<LogObject> getAccionsJbpmPerRetrocedir(
			List<ExpedientLog> expedientLogs,
			List<ProcessLog> logsSorted) {
		Map<Long, LogObject> logObjects = new HashMap<Long, LogObject>();
		long currentMessageLogId = -1;
		for (ProcessLog plog: logsSorted) {
			if (plog instanceof MessageLog) {
				currentMessageLogId = plog.getId();
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
					if (expedientLog.getAccioTipus().equals(ExpedientLogAccioTipus.TASCA_REASSIGNAR)) {
						String params = expedientLog.getAccioParams();
						lobj.setValorInicial(params.substring(0, params.indexOf("::")));
					}
				}
			} else if (plog instanceof VariableLog) {
				VariableInstance variableInstance = ((VariableLog)plog).getVariableInstance();
				// Hi ha logs de variables que ténen el nom null i el valor null 
				// No sé molt bé el motiu
				// El següent if els descarta
				if(variableInstance.getProcessInstance()!=null){
				
				
					String codi = variableInstance.getName();
					DefinicioProces pDef = definicioProcesDao.findAmbJbpmId(String.valueOf(variableInstance.getProcessInstance().getProcessDefinition().getId()));
					Camp camp = campDao.findAmbDefinicioProcesICodi(
							pDef.getId(),
							codi);		
					if(camp != null && !camp.isIgnored()){
						
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
								lobj.setValorInicial(vulog.getOldValue());
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
		System.out.print("              ");
		for (int i = 0; i < indent; i++) {
			System.out.print("║  ");
		}
		System.out.print("╠═>");
		System.out.println("[" + df.format(log.getDate()) + "] [" + log.getId() + "] " + log + "(" + log.getClass().getName() + ")");
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

	private static final Log logger = LogFactory.getLog(ExpedientLogHelper.class);

}
