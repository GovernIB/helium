/**
 *
 */
package es.caib.helium.logic.helper;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.flowable.engine.history.ProcessInstanceHistoryLog;
import org.flowable.task.api.history.HistoricTaskLogEntry;
import org.flowable.variable.api.history.HistoricVariableInstance;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.lowagie.text.Font;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;

import es.caib.helium.commons.dto.LogObjectDto;
import es.caib.helium.logic.intf.dto.engine.WMessageLog;
import es.caib.helium.logic.intf.dto.engine.WNode;
import es.caib.helium.logic.intf.dto.engine.WNode.WNodeType;
import es.caib.helium.logic.intf.dto.engine.WNodeLog;
import es.caib.helium.logic.intf.dto.engine.WProcessInstance;
import es.caib.helium.logic.intf.dto.engine.WProcessLog;
import es.caib.helium.logic.intf.dto.engine.WTaskInstance;
import es.caib.helium.logic.intf.dto.engine.WTaskLog;
import es.caib.helium.logic.intf.dto.engine.WToken;
import es.caib.helium.logic.intf.dto.engine.WTransitionLog;
import es.caib.helium.logic.intf.service.WorkflowEngineApi;
import es.caib.helium.persistence.common.jbpm.JbpmVars;
import es.caib.helium.persistence.entity.Camp;
import es.caib.helium.persistence.entity.CampTasca;
import es.caib.helium.persistence.entity.DefinicioProces;
import es.caib.helium.persistence.entity.Document;
import es.caib.helium.persistence.entity.DocumentTasca;
import es.caib.helium.persistence.entity.Expedient;
import es.caib.helium.persistence.entity.ExpedientLog;
import es.caib.helium.persistence.entity.ExpedientLog.ExpedientLogAccioTipus;
import es.caib.helium.persistence.entity.ExpedientLog.ExpedientLogEstat;
import es.caib.helium.persistence.entity.ExpedientLog.LogInfo;
import es.caib.helium.persistence.entity.ExpedientTipus;
import es.caib.helium.persistence.entity.Tasca;
import es.caib.helium.persistence.repository.CampRepository;
import es.caib.helium.persistence.repository.CampTascaRepository;
import es.caib.helium.persistence.repository.DefinicioProcesRepository;
import es.caib.helium.persistence.repository.DocumentRepository;
import es.caib.helium.persistence.repository.EstatRepository;
import es.caib.helium.persistence.repository.ExpedientLoggerRepository;
import es.caib.helium.persistence.repository.ExpedientRepository;
import es.caib.helium.persistence.repository.TascaRepository;

/**
 * Helper per a gestionar els logs dels expedients
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class ExpedientLoggerHelper {

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
	private WorkflowEngineApi workflowEngineApi;
	@Resource(name="documentHelperV3")
	private DocumentHelperV3 documentHelper;
	@Resource
	private ExpedientHelper expedientHelper;
	@Resource
	private ExpedientTipusHelper expedientTipusHelper;
	@Resource
	private HerenciaHelper herenciaHelper;

	@Resource
	private ExpedientRegistreHelper expedientRegistreHelper;
	@Resource
	private DocumentRepository documentRepository;


	public WToken getTokenByJbpmLogId(Long jbpmLogId){
		WProcessLog pl = workflowEngineApi.getProcessLogById(jbpmLogId);
		if (pl == null)
			return null;
		return (WToken) pl.getToken();
	}

	public long afegirProcessLogInfoExpedient(
			boolean ambRetroaccio,
			String processInstanceId,
			String message) {
		if (ambRetroaccio) {
			long jbpmLogId = workflowEngineApi.addProcessInstanceMessageLog(
					processInstanceId,
					MESSAGE_LOGINFO_PREFIX + "::" + message);
			return jbpmLogId;
		} else {
			return -1;
		}
	}
	public ExpedientLog afegirLogExpedientPerExpedient(
			Long expedientId,
			ExpedientLogAccioTipus tipus,
			String accioParams) {
		Expedient expedient = expedientRepository.getReferenceById(expedientId);
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
		expedientLog.setProcessInstanceId(processInstanceId);
		Long jbpmLogId = workflowEngineApi.addProcessInstanceMessageLog(
				expedientLog.getExpedient().getProcessInstanceId(),
				getMessageLogPerTipus(tipus));
		expedientLog.setJbpmLogId(jbpmLogId);
		if (accioParams != null)
			expedientLog.setAccioParams(accioParams);
		expedientLog.setJbpmLogId(jbpmLogId);
		expedientLoggerRepository.save(expedientLog);
		return expedientLog;
	}

	private Collection<LogObjectDto> getAccionsJbpmPerRetrocedir(
			List<ExpedientLog> expedientLogs,
			List<WProcessLog> logsSorted) {
		Map<String, LogObjectDto> LogObjectDtos = new HashMap<String, LogObjectDto>();
		String currentMessageLogId = null;
		for (WProcessLog plog: logsSorted) {
			if (plog instanceof WMessageLog) {
				WMessageLog mlog = (WMessageLog)plog;
				if (mlog.getMessage().startsWith(MESSAGE_LOGINFO_PREFIX)) {

					String objId = plog.getToken().getProcessInstanceId();
					LogObjectDto lobj = LogObjectDtos.get(objId);

					if (lobj == null) {
						String sTipus = mlog.getMessage().substring(mlog.getMessage().indexOf("::") + 2, mlog.getMessage().indexOf("#@#"));
						String info = mlog.getMessage().substring(mlog.getMessage().indexOf("#@#") + 3);

						LogInfo li = LogInfo.valueOf(sTipus);

						lobj = new LogObjectDto(
								objId,
								plog.getId(),
								//objId.toString(),
								li.name(),
								LogObjectDto.LOG_OBJECT_INFO,
								plog.getToken().getProcessInstanceId(),
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
										lobj.setValorInicial(estatRepository.getReferenceById(estatId));
									}
									break;
								case GEOPOSICIOX:
								case GEOPOSICIOY:
									lobj.setValorInicial(Double.parseDouble(info));
									break;
								default:
									break;
							}
							LogObjectDtos.put(objId, lobj);

						} catch (Exception e) {
							logger.error("ERROR: Error al obtenir el tipus de informació de l'expedient dels logs", e);
						}
					}
				} else {
					currentMessageLogId = plog.getId();
				}
			} else if (plog instanceof WTaskLog) {
				WTaskInstance taskInstance = ((WTaskLog)plog).getTaskInstance();
				String objId = taskInstance.getId();
				LogObjectDto lobj = LogObjectDtos.get(objId);
				if (lobj == null) {
					lobj = new LogObjectDto(
							objId,
							plog.getId(),
							taskInstance.getTaskName(),
							LogObjectDto.LOG_OBJECT_TASK,
							plog.getToken().getProcessInstanceId(),
							plog.getToken().getId());
					LogObjectDtos.put(objId, lobj);
				}
				if (plog instanceof HistoricTaskLogEntry)
					lobj.addAccio(LogObjectDto.LOG_ACTION_START);
//				if (plog instanceof TaskEndLog)
//					lobj.addAccio(LogObjectDto.LOG_ACTION_END);
//				if (plog instanceof TaskAssignLog) {
//					lobj.addAccio(LogObjectDto.LOG_ACTION_ASSIGN);
//					lobj.setValorInicial(((TaskAssignLog)plog).getTaskOldActorId());
//					ExpedientLog expedientLog = getExpedientLogPerJbpmLogId(expedientLogs, currentMessageLogId);
//					if (expedientLog != null && expedientLog.getAccioTipus().equals(ExpedientLogAccioTipus.TASCA_REASSIGNAR)) {
//						String params = expedientLog.getAccioParams();
//						if (params.indexOf("::") != -1)
//							lobj.setValorInicial(params.substring(0, params.indexOf("::")));
//					}
//				}
			} else if (plog instanceof HistoricVariableInstance) {
				HistoricVariableInstance variableInstance = ((HistoricVariableInstance)plog);
				boolean ignored = false;
				// Hi ha logs de variables que ténen el nom null i el valor null
				// No sé molt bé el motiu
				// El següent if els descarta
				if(variableInstance.getProcessInstanceId()!=null){

					// Cerca informació si la variable jbpm correspon a un document o una camp ignorat
					String codi = variableInstance.getVariableName();
																				 // variableInstance.getProcessInstance().getProcessDefinition().getId();
					DefinicioProces pDef = definicioProcesRepository.findByJbpmId(String.valueOf(variableInstance.getProcessInstanceId()));

//					Expedient expedient = expedientRepository.getReferenceById(
//							variableInstance.getProcessInstance().getExpedient().getId());
					Expedient expedient = expedientRepository.findByProcessInstanceId(variableInstance.getProcessInstanceId());
					ExpedientTipus expedientTipus = expedient != null ? expedient.getTipus() : null;
					Camp camp = null;
					if(codi.startsWith(JbpmVars.PREFIX_DOCUMENT)) {
						// Document
						codi = codi.substring((JbpmVars.PREFIX_DOCUMENT).length());
						// Cerca el document per veure si està marcat per ignorar
						Document document = null;
						if (expedientTipus != null && expedientTipus.isAmbInfoPropia()) {
							document = documentRepository.findByExpedientTipusAndCodi(
									expedientTipus.getId(),
									codi,
									expedientTipus.getExpedientTipusPare() != null);
						} else {
							document = documentRepository.findByDefinicioProcesAndCodi(
									pDef,
									codi);
						}
						if(document != null){
							ignored = document.isIgnored();
						}
					} else {
						// Variable
						if (expedientTipus != null && expedientTipus.isAmbInfoPropia()) {
							camp = campRepository.findByExpedientTipusAndCodi(
									expedientTipus.getId(),
									codi,
									true);
						} else {
							camp = campRepository.findByDefinicioProcesAndCodi(
									pDef,
									codi);
						}
						if(camp != null){
							ignored = camp.isIgnored();
						}
					}
					if (!ignored) {
						if (variableInstance.getVariableName() != null || variableInstance.getValue() != null) {
							String variableInstanceId = workflowEngineApi.getVariableIdFromVariableLog(plog.getId());
							String taskInstanceId = workflowEngineApi.getVariableIdFromVariableLog(plog.getId());
							LogObjectDto lobj = LogObjectDtos.get(variableInstanceId);
							if (lobj == null) {
								lobj = new LogObjectDto(
										variableInstanceId,
										plog.getId(),
										variableInstance.getVariableName(),
										(taskInstanceId != null) ? LogObjectDto.LOG_OBJECT_VARTASCA : LogObjectDto.LOG_OBJECT_VARPROCES,
										plog.getToken().getProcessInstanceId(),
										plog.getToken().getId());
								if (taskInstanceId != null) {
									lobj.setTaskInstanceId(taskInstanceId);
								}
								LogObjectDtos.put(variableInstanceId, lobj);
							}
//							if (plog instanceof VariableCreateLog)
//								lobj.addAccio(LogObjectDto.LOG_ACTION_CREATE);
//							if (plog instanceof VariableUpdateLog) {
//								VariableUpdateLog vulog = (VariableUpdateLog)plog;
//								lobj.addAccio(LogObjectDto.LOG_ACTION_UPDATE);
//								Object oldValue = vulog.getOldValue();
//								if (oldValue instanceof ByteArray) {
//									try {
//										oldValue = new ObjectInputStream(new ByteArrayInputStream(((ByteArray)vulog.getOldValue()).getBytes())).readObject();
//									} catch (Exception e) {
//										logger.error("Error obtenint el valor del ByteArray de la variable " + vulog.getVariableInstance().getName(), e);
//									}
//								} else if (oldValue instanceof String && camp != null && camp.getTipus() == TipusCamp.BOOLEAN) {
//									oldValue =  oldValue.equals("T") ? new Boolean(true) : new Boolean(false);
//								}
//								lobj.setValorInicial(oldValue);
//							}
//							if (plog instanceof VariableDeleteLog)
//								lobj.addAccio(LogObjectDto.LOG_ACTION_DELETE);
						}
					}
				}
//			} else if (plog instanceof TokenCreateLog || plog instanceof TokenEndLog || plog instanceof TransitionLog) {
//				Token token = plog.getToken();
//				if (plog instanceof TokenCreateLog)
//					token = ((TokenCreateLog)plog).getChild();
//				if (plog instanceof TokenEndLog)
//					token = ((TokenEndLog)plog).getChild();
//				Long objId = new Long(token.getId());
//				LogObjectDto lobj = LogObjectDtos.get(objId);
//				if (lobj == null) {
//					String tokenName = token.getName();
//					if (tokenName == null && token.isRoot())
//						tokenName = "[ROOT]";
//					lobj = new LogObjectDto(
//							objId.longValue(),
//							plog.getId(),
//							tokenName,
//							LogObjectDto.LOG_OBJECT_TOKEN,
//							plog.getToken().getProcessInstanceId(),
//							plog.getToken().getId());
//					LogObjectDtos.put(objId, lobj);
//				}
//				if (plog instanceof TokenCreateLog)
//					lobj.addAccio(LogObjectDto.LOG_ACTION_START);
//				if (plog instanceof TokenEndLog)
//					lobj.addAccio(LogObjectDto.LOG_ACTION_END);
//				if (plog instanceof TransitionLog) {
//					TransitionLog trlog = (TransitionLog)plog;
//					lobj.addAccio(LogObjectDto.LOG_ACTION_UPDATE);
//					lobj.setValorInicial(trlog.getSourceNode().getName());
//				}
			} else if (plog instanceof ProcessInstanceHistoryLog) {// || plog instanceof ProcessInstanceEndLog) {
				String objId = plog.getToken().getProcessInstanceId();
				LogObjectDto lobj = LogObjectDtos.get(objId);
				if (lobj == null) {
					lobj = new LogObjectDto(
							objId,
							plog.getId(),
							objId.toString(),
							LogObjectDto.LOG_OBJECT_PROCES,
							plog.getToken().getProcessInstanceId(),
							plog.getToken().getId());
					LogObjectDtos.put(objId, lobj);
				}
				if (plog instanceof ProcessInstanceHistoryLog)
					lobj.addAccio(LogObjectDto.LOG_ACTION_START);
//				if (plog instanceof ProcessInstanceEndLog)
//					lobj.addAccio(LogObjectDto.LOG_ACTION_END);
			}
//			else if (plog instanceof ActionLog) {
//				Long objId = ((ActionLog)plog).getAction().getId();
//				LogObjectDto lobj = LogObjectDtos.get(objId);
//				if (lobj == null) {
//					lobj = new LogObjectDto(
//							objId.longValue(),
//							plog.getId(),
//							((ActionLog)plog).getAction().getName(),
//							LogObjectDto.LOG_OBJECT_ACTION,
//							plog.getToken().getProcessInstanceId(),
//							plog.getToken().getId());
//					LogObjectDtos.put(objId, lobj);
//				}
//				lobj.addAccio(LogObjectDto.LOG_ACTION_EXEC);
//			}
		}
		List<LogObjectDto> logsOrdenats = new ArrayList<LogObjectDto>(LogObjectDtos.values());
		Collections.sort(
				logsOrdenats,
				new Comparator<LogObjectDto>() {
					public int compare(LogObjectDto o1, LogObjectDto o2) {
						Long l1 = Long.valueOf(o1.getLogId());
						Long l2 = Long.valueOf(o2.getLogId());
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
		WToken tokenRetroces = null;
		boolean incloure = false;
		boolean found = false;

		List<ExpedientLog> expedientLogsRetrocedir = new ArrayList<ExpedientLog>();
		for (ExpedientLog elog: expedientLogs) {
			// Obtenim el log seleccionat
			if (elog.getId().equals(expedientLogId)) {
				found = true;
				incloure = true;
				if (/*retrocedirPerTasques  && */elog.isTargetTasca()) {
					WToken jbpmTokenRetroces = getTokenByJbpmLogId(elog.getJbpmLogId());
					if (jbpmTokenRetroces != null) tokenRetroces = jbpmTokenRetroces;
				}
			}
			// Obtenim els logs a retrocedir
			if (found) {
				// Ara
//				if (retrocedirPerTasques) {
					 if ((elog.isTargetTasca() || elog.isTargetProces())
							 && tokenRetroces != null) {
						 // Si la tasca seleccionada es del token arrel, llavors
						 // totes les tasques posteriors s'han de incloure
						 if (tokenRetroces.isRoot() && tokenRetroces.getSuperRootTokenId() == null) { //processos.get(tokenRetroces.getProcessInstance().getId()) == null) {
							 incloure = true;
						 } else {
							WToken tokenActual = null;
							WToken jbpmTokenActual = getTokenByJbpmLogId(elog.getJbpmLogId());
							if (jbpmTokenActual != null) {

								if ((tokenActual.isRoot() && tokenActual.getSuperRootTokenId() == null)
										|| tokenActual.equals(tokenRetroces)) {
									// Incloem el token arrel "/" i els tokens iguals al token de la tasca seleccionada
									incloure = true;
								} else {
									// Incloem tots els tokens pare del token de la tasca seleccionada
									WToken subTokenRetroces = getTokenPare(tokenRetroces);
										while (subTokenRetroces != null) {
											if (tokenActual.equals(subTokenRetroces)) {
												incloure = true;
												break;
											}
											subTokenRetroces = getTokenPare(subTokenRetroces);
										}
									// Incloem tots els tokens fills del token de la tasca seleccionada
									if (!incloure) {
										WToken subTokenActual = getTokenPare(tokenActual);
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

	private WToken getTokenPare(WToken token) {
		WToken t = token.getParent();
		if (t == null) {
			t = token.getSuperToken();
		}
		return t;
	}

	private List<WProcessLog> getLogsJbpmPerRetrocedir(List<ExpedientLog> expedientLogs) {
		List<WProcessLog> logsJbpm = new ArrayList<WProcessLog>();

		// Utilitzam un set per a evitar repetits
		Set<WProcessLog> setlogsJbpm = new HashSet<WProcessLog>();
		for (ExpedientLog elog: expedientLogs) {
			List<WProcessLog> logsJbpmPerAfegir = getLogsJbpmPerExpedientLog(elog);
			if (logsJbpmPerAfegir != null)
				setlogsJbpm.addAll(logsJbpmPerAfegir);
		}
		// Passam els logs a una llista, i la ordenam
		logsJbpm.addAll(setlogsJbpm);
		Collections.sort(
				logsJbpm,
				new Comparator<WProcessLog>() {
					public int compare(WProcessLog l1, WProcessLog l2) {
						return new Long(l1.getId()).compareTo(new Long(l2.getId()));
					}
				});

		return logsJbpm;
	}

	private List<WProcessLog> getJbpmLogsPerInstanciaProces(String processInstanceId, boolean asc) {

		List<WProcessLog> logsJbpm = new ArrayList<WProcessLog>();

		WProcessInstance pi = workflowEngineApi.getRootProcessInstance(processInstanceId.toString());
		List<WProcessInstance> processos = workflowEngineApi.getProcessInstanceTree(pi.getId().toString());
		for (WProcessInstance proces: processos) {
			Map<WToken, List<WProcessLog>> logsPerInstanciaProces = workflowEngineApi.getProcessInstanceLogs(
					proces.getId().toString());
			for (WToken token: logsPerInstanciaProces.keySet()) {
				for (WProcessLog plog: logsPerInstanciaProces.get(token))
					logsJbpm.add(plog);
			}
		}

		if (asc) {
			Collections.sort(
				logsJbpm,
				new Comparator<WProcessLog>() {
					public int compare(WProcessLog l1, WProcessLog l2) {

						return Long.valueOf(l1.getId()).compareTo(Long.valueOf(l2.getId()));
					}
				});
		} else {
			Collections.sort(
				logsJbpm,
				new Comparator<WProcessLog>() {
					public int compare(WProcessLog l1, WProcessLog l2) {
						return Long.valueOf(l2.getId()).compareTo(Long.valueOf(l1.getId()));
					}
				});
		}

		return logsJbpm;
	}

	private List<WProcessLog> getLogsJbpmPerExpedientLog(ExpedientLog expedientLog) {
		List<WProcessLog> logsJbpm = new ArrayList<WProcessLog>();
		List<WProcessLog> logsJbpmAfegir = new ArrayList<WProcessLog>();
		if (expedientLog.getJbpmLogId() != null && expedientLog.getEstat().equals(ExpedientLogEstat.NORMAL)) {
			// Obtenim els logs jBPM associats a la instància de procés i als seus
			// subprocessos
			logsJbpm = getJbpmLogsPerInstanciaProces(expedientLog.getProcessInstanceId(), true);

			int indexInici = -1;
			int indexFi = -1;
			int index = 0;
			WNode join = null;
			for (WProcessLog plog: logsJbpm) {
				// L'índex inicial correspon al lloc a on es troba el log marcat per jbpmLogId
				if (plog.getId() == expedientLog.getJbpmLogId().toString()) {
					indexInici = index;
				} else if (indexInici != -1 && plog instanceof WTransitionLog) {
					// Comprovam si hi ha hagut una transició a un join
					WTransitionLog trlog = (WTransitionLog)plog;
					if (trlog.getDestinationNode().getNodeType() == WNode.WNodeType.Join) {
						join = trlog.getDestinationNode();
					}
				} else if (indexInici != -1 && plog instanceof WMessageLog) {
					String message = ((WMessageLog)plog).getMessage();
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
				for (WProcessLog plog: logsJbpm) {
					// L'índex inicial correspon al log del node del Join
					if (plog instanceof WNodeLog) {
						WNodeLog nlog = (WNodeLog)plog;
						if (nlog.getNode().getId() == join.getId())
						indexInici = index;
					} else if (indexInici != -1 && plog instanceof WMessageLog) {
						String message = ((WMessageLog)plog).getMessage();
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

	public void printLogs(List<Object> logs) {
//		for (ProcessLog log: logs) {
//			if (log.getParent() == null) {
//				printLogMessage(
//						logs,
//						log,
//						0);
//			}
//		}
	}

	private void printLogMessage(
			List<Object> logs,
			Object log,
			int indent) {
//		DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
//
//		StringBuilder logInfo = new StringBuilder("              ");
//		for (int i = 0; i < indent; i++) {
//			logInfo.append("║  ");
//		}
//		logInfo.append("╠═>");
//		logInfo.append("[").append(df.format(log.getDate())).append("] [").append(log.getId()).append("] ").append(log).append("(").append(log.getClass().getName()).append(")");
//		logger.info(logInfo.toString());
//
//		for (ProcessLog l: logs) {
//			if (l.getParent() != null && l.getParent().getId() == log.getId()) {
//				printLogMessage(logs, l, indent + 1);
//			}
//		}
	}

	private String getMessageLogPerTipus(ExpedientLogAccioTipus tipus) {
		return MESSAGE_LOG_PREFIX + tipus.name();
	}

	public void imprimirLogs(Long expedientId) {
//		List<ExpedientLog> expedientLogs = expedientLoggerRepository.findAmbExpedientIdOrdenatsPerData(expedientId);
//		List<ProcessLog> logsJbpm = getJbpmLogsPerInstanciaProces(expedientLogs);

//		Expedient expedient = expedientRepository.getReferenceById(expedientId);
//		String processInstanceId = expedient.getProcessInstanceId();

//		List<ProcessLog> logsJbpm = getJbpmLogsPerInstanciaProces(Long.parseLong(processInstanceId), true);
//		printLogs(logsJbpm);

//		List<JbpmProcessInstance> instanciesPoces = workflowEngineApi.getProcessInstanceTree(processInstanceId);
//		Set <ProcessLog>

	}

	public void retrocedirFinsLog(ExpedientLog expedientLog, boolean retrocedirPerTasques, Long iniciadorId) {
//		boolean debugRetroces = true;
//
////		ExpedientLog expedientLog = expedientLoggerRepository.getById(expedientLogId, false);
//		JbpmTask jtask = workflowEngineApi.getTaskById(expedientLog.getTargetId());
//
//		// Variables per a desar la informació per a executar el node enter al final de tot
////		long nodeEnterObjectId = 0;
//		Long nodeEnterTokenId = null;
//
//		mesuresTemporalsHelper.mesuraIniciar("Retrocedir" + (retrocedirPerTasques ? " per tasques" : ""), "expedient", expedientLog.getExpedient().getTipus().getNom(), null, "findAmbExpedientIdOrdenatsPerData");
//		List<ExpedientLog> expedientLogs = expedientLoggerRepository.findAmbExpedientIdOrdenatsPerData(
//				expedientLog.getExpedient().getId());
//		mesuresTemporalsHelper.mesuraCalcular("Retrocedir" + (retrocedirPerTasques ? " per tasques" : ""), "expedient", expedientLog.getExpedient().getTipus().getNom(), null, "findAmbExpedientIdOrdenatsPerData");
//		mesuresTemporalsHelper.mesuraIniciar("Retrocedir" + (retrocedirPerTasques ? " per tasques" : ""), "expedient", expedientLog.getExpedient().getTipus().getNom(), null, "filtraExpedientsLogPerRetrocedir");
//		expedientLogs = filtraExpedientsLogPerRetrocedir(expedientLogs, expedientLog.getId(), retrocedirPerTasques);
//		mesuresTemporalsHelper.mesuraCalcular("Retrocedir" + (retrocedirPerTasques ? " per tasques" : ""), "expedient", expedientLog.getExpedient().getTipus().getNom(), null, "filtraExpedientsLogPerRetrocedir");
//
//		// Retrocedeix els canvis al jBPM relacionats amb els logs
//		mesuresTemporalsHelper.mesuraIniciar("Retrocedir" + (retrocedirPerTasques ? " per tasques" : ""), "expedient", expedientLog.getExpedient().getTipus().getNom(), null, "getLogsJbpmPerRetrocedir");
//		List<ProcessLog> logsJbpm = getLogsJbpmPerRetrocedir(expedientLogs); //, expedientLogId);
//		mesuresTemporalsHelper.mesuraCalcular("Retrocedir" + (retrocedirPerTasques ? " per tasques" : ""), "expedient", expedientLog.getExpedient().getTipus().getNom(), null, "getLogsJbpmPerRetrocedir");
//
//		// Primer i últim log (rang a retrocedir)
//		long beginLogId = logsJbpm.get(0).getId();
//		long endLogId = logsJbpm.get(logsJbpm.size() - 1).getId();
//
//		// Info log actual
//		Token currentToken = null;
////		LogObjectDto currentLog = null;
//		Node nodeDesti = null;
//		boolean tascaActual = false;
//
//		if (debugRetroces)
//			printLogs(logsJbpm);
//		workflowEngineApi.addProcessInstanceMessageLog(
//				expedientLog.getExpedient().getProcessInstanceId(),
//				getMessageLogPerTipus(retrocedirPerTasques ? ExpedientLogAccioTipus.EXPEDIENT_RETROCEDIR_TASQUES : ExpedientLogAccioTipus.EXPEDIENT_RETROCEDIR));
//		if (logsJbpm != null && !logsJbpm.isEmpty()) {
//			mesuresTemporalsHelper.mesuraIniciar("Retrocedir" + (retrocedirPerTasques ? " per tasques" : ""), "expedient", expedientLog.getExpedient().getTipus().getNom(), null, "getAccionsJbpmPerRetrocedir");
//			// Recull totes les accions executades a jBPM relacionats amb els logs
//			Collection<LogObjectDto> LogObjectDtos = getAccionsJbpmPerRetrocedir(
//					expedientLogs,
//					logsJbpm);
//			if (debugRetroces) {
//				for (LogObjectDto logo: LogObjectDtos) {
//					String logInfo = ">>> [RETPRN] ";
//					switch (logo.getTipus()) {
//					case LogObjectDto.LOG_OBJECT_PROCES:
//						logInfo = logInfo + "PROCES ";
//						break;
//					case LogObjectDto.LOG_OBJECT_TOKEN:
//						logInfo = logInfo + "TOKEN ";
//						break;
//					case LogObjectDto.LOG_OBJECT_TASK:
//						logInfo = logInfo + "TASCA ";
//						break;
//					case LogObjectDto.LOG_OBJECT_VARTASCA:
//						logInfo = logInfo + "VARTASCA ";
//						break;
//					case LogObjectDto.LOG_OBJECT_VARPROCES:
//						logInfo = logInfo + "VARPROCES ";
//						break;
//					case LogObjectDto.LOG_OBJECT_ACTION:
//						logInfo = logInfo + "ACTION ";
//						break;
//					case LogObjectDto.LOG_OBJECT_INFO:
//						logInfo = logInfo + "INFO ";
//						break;
//					default:
//						logInfo = logInfo + "??? ";
//					}
//					logInfo = logInfo + "(" + logo.getName() + ") ";
//					for (String accio: logo.getAccions())
//						logInfo = logInfo + accio;
//					logger.info(logInfo);
//				}
//			}
//			mesuresTemporalsHelper.mesuraCalcular("Retrocedir" + (retrocedirPerTasques ? " per tasques" : ""), "expedient", expedientLog.getExpedient().getTipus().getNom(), null, "getAccionsJbpmPerRetrocedir");
//			mesuresTemporalsHelper.mesuraIniciar("Retrocedir" + (retrocedirPerTasques ? " per tasques" : ""), "expedient", expedientLog.getExpedient().getTipus().getNom(), null, "obtenir dades per retroces");
//
//			// Emmagatzema els paràmetres per a retrocedir cada acció per parella [processInstanceId, action_name]
//			Map<String, String> paramsAccio = new HashMap<String, String>();
//			String varName;
//			for (LogObjectDto logo: LogObjectDtos) {
//				if (logo.getTipus() == LogObjectDto.LOG_OBJECT_ACTION) {
//					String params;
//					String paramKey;
//
//					paramKey = logo.getProcessInstanceId() + "_" + logo.getName();
//
//					// Consulta les variables de retrocés guardades abans de la versió 3.2.106 amb action node id
//					varName = BasicActionHandler.PARAMS_RETROCEDIR_VARIABLE_PREFIX + new Long(logo.getObjectId());
//					params = (String)workflowEngineApi.getProcessInstanceVariable(
//							new Long(logo.getProcessInstanceId()).toString(),
//							varName);
//					if (params != null)
//						paramsAccio.put(paramKey, params);
//
//					// Consulta les variables de retrocés guardades a partir de la versió 3.2.106 amb actionName
//					varName = BasicActionHandler.PARAMS_RETROCEDIR_VARIABLE_PREFIX + logo.getName();
//					params = (String)workflowEngineApi.getProcessInstanceVariable(
//							new Long(logo.getProcessInstanceId()).toString(),
//							varName);
//					if (params != null && !paramsAccio.containsKey(paramKey))
//						paramsAccio.put(paramKey, params);
//				}
//			}
//			// Completa els paràmetres amb paràmetres que podrien tenir una relació amb un node Action d'una definició anterior a la versió 3.2.106
//			this.consultarParametresRetroaccio(
//					paramsAccio,
//					LogObjectDtos);
//
//			// comprovam si estem retrocedint únicament la tasca actual
//			if (jtask != null) {
//				JbpmProcessInstance pi = workflowEngineApi.getProcessInstance(String.valueOf(expedientLog.getProcessInstanceId()));
//				currentToken = workflowEngineApi.getProcessLogById(expedientLog.getJbpmLogId()).getToken();
//				Collection<TaskInstance> tis = pi.getProcessInstance().getTaskMgmtInstance().getUnfinishedTasks(currentToken);
//				for (TaskInstance ti: tis) {
//					if (ti.getId() == jtask.getTask().getId()){
//						nodeDesti = ti.getTask().getTaskNode();
//						tascaActual = true;
//						if (debugRetroces)
//							logger.info(">>> [LOGTASK] Retroces de la tasca actual (" + nodeDesti + ")!");
//						break;
//					}
//				}
//			}
//
//			mesuresTemporalsHelper.mesuraCalcular("Retrocedir" + (retrocedirPerTasques ? " per tasques" : ""), "expedient", expedientLog.getExpedient().getTipus().getNom(), null, "obtenir dades per retroces");
//			mesuresTemporalsHelper.mesuraIniciar("Retrocedir" + (retrocedirPerTasques ? " per tasques" : ""), "expedient", expedientLog.getExpedient().getTipus().getNom(), null, "realitzar accions de retroces");
//			// Executa les accions necessàries per a retrocedir l'expedient
//			for (LogObjectDto logo: LogObjectDtos) {
//				boolean created = logo.getAccions().contains(LogObjectDto.LOG_ACTION_CREATE);
//				boolean updated = logo.getAccions().contains(LogObjectDto.LOG_ACTION_UPDATE);
//				boolean deleted = logo.getAccions().contains(LogObjectDto.LOG_ACTION_DELETE);
//				boolean started = logo.getAccions().contains(LogObjectDto.LOG_ACTION_START);
//				boolean ended = logo.getAccions().contains(LogObjectDto.LOG_ACTION_END);
//				boolean assigned = logo.getAccions().contains(LogObjectDto.LOG_ACTION_ASSIGN);
//				switch (logo.getTipus()) {
//				case LogObjectDto.LOG_OBJECT_PROCES:
//					if (started && !ended) {
//						if (debugRetroces)
//							logger.info(">>> [RETLOG] Cancel·lar/finalitzar procés (" + logo.getName() + ")");
//						workflowEngineApi.cancelProcessInstance(logo.getObjectId());
//					} else if (!started && ended) {
//						if (debugRetroces)
//							logger.info(">>> [RETLOG] Desfer finalitzar procés (" + logo.getName() + ")");
//						workflowEngineApi.revertProcessInstanceEnd(logo.getObjectId());
//						JbpmProcessInstance jpi = workflowEngineApi.getProcessInstance(String.valueOf(logo.getProcessInstanceId()));
//						if (debugRetroces)
//							logger.info(">>> [RETLOG] Desfer finalitzar token (" + jpi.getProcessInstance().getRootToken().getFullName() + ")");
//						workflowEngineApi.revertTokenEnd(jpi.getProcessInstance().getRootToken().getId());
//					}
//					break;
//				case LogObjectDto.LOG_OBJECT_TOKEN:
//					if (debugRetroces) {
//						JbpmToken jtok = workflowEngineApi.getTokenById(String.valueOf(logo.getObjectId()));
//						logger.info(">>> [LOGTOKEN] Inici Retroces token (" + logo.getName() + ") - End: " + jtok.getToken().getEnd());
//					}
//					if (started && !ended) {
//						if (debugRetroces)
//							logger.info(">>> [RETLOG] Cancel·lar token (" + logo.getName() + ")");
//						workflowEngineApi.cancelToken(logo.getObjectId());
//
//						if (debugRetroces) {
//							JbpmToken jtok = workflowEngineApi.getTokenById(String.valueOf(logo.getObjectId()));
//							logger.info(">>> [LOGTOKEN] Retroces token cancelat (" + logo.getName() + ") - End: " + jtok.getToken().getEnd());
//						}
//					} else if (!started && ended) {
//						if (debugRetroces)
//							logger.info(">>> [RETLOG] Desfer finalitzar token (" + logo.getName() + ")");
//						workflowEngineApi.revertTokenEnd(logo.getObjectId());
//
//						if (debugRetroces) {
//							JbpmToken jtok = workflowEngineApi.getTokenById(String.valueOf(logo.getObjectId()));
//							logger.info(">>> [LOGTOKEN] Retroces revert token end (" + logo.getName() + ") - End: " + jtok.getToken().getEnd());
//						}
//					}
//					if (!started) {
//						// Només ha d'executar el node si no és una instància de procés
//						// o un join o un fork
//						String desti = (String)logo.getValorInicial();
//						if (workflowEngineApi.isJoinNode(logo.getProcessInstanceId(), (String)logo.getValorInicial())) {
//							Node joinNode = workflowEngineApi.getNodeByName(logo.getProcessInstanceId(), desti);
//							Node forkNode = getForkNode(logo.getProcessInstanceId(), joinNode);
//							if (forkNode != null)
//								desti = forkNode.getName();
//						}
//
//						if (debugRetroces) {
//							JbpmToken jtok = workflowEngineApi.getTokenById(String.valueOf(logo.getObjectId()));
//							logger.info(">>> [LOGTOKEN] Retroces abans token redirect (" + logo.getName() + ") - End: " + jtok.getToken().getEnd());
//						}
//
////						boolean enterNode = (nodeRetrocedir == logo.getLogId());
////						JbpmTask jtask = workflowEngineApi.getTaskById(expedientLog.getTargetId());
//						Node ndesti = workflowEngineApi.getNodeByName(logo.getProcessInstanceId(), desti);
//						boolean enterNode = retrocedirPerTasques && (jtask != null && ndesti.getId() == jtask.getTask().getTask().getTaskNode().getId()); // és la tasca a la que volem retrocedir!!
//						boolean executeNode = (!workflowEngineApi.isProcessStateNodeJoinOrFork( //(!workflowEngineApi.isProcessStateNode(
//								logo.getProcessInstanceId(),
//								(String)logo.getValorInicial()));
//						if (enterNode) {
////							nodeEnterObjectId = logo.getObjectId();
//							nodeEnterTokenId = logo.getTokenId();
//						}
//						if (debugRetroces)
//							logger.info(">>> [RETLOG] Retornar token (name=" + logo.getName() + ") al node (name=" + desti + ", enter = " + enterNode + ", execute=" + executeNode + ")");
//						workflowEngineApi.tokenRedirect(
//								logo.getObjectId(),
//								desti,
//								true,
//								enterNode,
//								executeNode);
//
//						if (debugRetroces) {
//							JbpmToken jtok = workflowEngineApi.getTokenById(String.valueOf(logo.getObjectId()));
//							logger.info(">>> [LOGTOKEN] Retroces després token redirect (" + logo.getName() + ") - End: " + jtok.getToken().getEnd());
//						}
//					}
//					break;
//				case LogObjectDto.LOG_OBJECT_TASK:
//					boolean tascaStarted = workflowEngineApi.hasStartBetweenLogs(beginLogId, endLogId, logo.getObjectId());
//					if (!tascaStarted && assigned) {
//						JbpmTask task = workflowEngineApi.findEquivalentTaskInstance(logo.getTokenId(), logo.getObjectId());
//						String valor = (String)logo.getValorInicial();
//						if (valor != null && !"".equals(valor)) {
//							if (debugRetroces)
//								logger.info(">>> [RETLOG] Reassignar tasca (" + task.getId() + ") a " + valor);
//							if (valor.startsWith("[") && valor.endsWith("]")) {
//								String[] actors = valor.substring(1, valor.length()-1).split(",");
//								workflowEngineApi.setTaskInstancePooledActors(
//										task.getId(),
//										actors);
//							} else {
//								workflowEngineApi.setTaskInstanceActorId(
//										task.getId(),
//										valor);
//							}
//						}
//					} else if (!tascaStarted && ended) {
//						JbpmTask task = workflowEngineApi.findEquivalentTaskInstance(logo.getTokenId(), logo.getObjectId());
//						if (debugRetroces)
//							logger.info(">>> [RETLOG] Copiar variables de la tasca (id=" + logo.getObjectId() + ") a la tasca (id=" + task.getId() + ")");
//						Map<String, Object> vars = workflowEngineApi.getTaskInstanceVariables(new Long(logo.getObjectId()).toString());
//						workflowEngineApi.setTaskInstanceVariables(task.getId(), vars, true);
//					}
//					break;
//				case LogObjectDto.LOG_OBJECT_VARPROCES:
//					if (logo.getName() != null) {
//						String pid = new Long(logo.getProcessInstanceId()).toString();
//						if ((created && !deleted && !updated) || (created && updated)) {
//							if (debugRetroces)
//								logger.info(">>> [RETLOG] Esborrar variable " + logo.getName() + " del proces (" + pid + ")");
//							workflowEngineApi.deleteProcessInstanceVariable(
//									pid,
//									logo.getName());
//							if (logo.getName().startsWith(JbpmVars.PREFIX_DOCUMENT)) {
//								documentHelper.esborrarDocument(
//										null,
//										pid,
//										DocumentHelperV3.getDocumentCodiPerVariableJbpm(logo.getName()));
//							}
//						} else if (!created && deleted) {
//							if (debugRetroces)
//								logger.info(">>> [RETLOG] Crear variable " + logo.getName() + " del proces (" + pid + ") amb el valor (" + logo.getValorInicial() + ")");
//							if (logo.getName().startsWith(JbpmVars.PREFIX_DOCUMENT)) {
//								// Si existissin versions de documents no s'hauria de fer res
//								Long documentStoreId = documentHelper.crearDocument(
//										null,
//										pid,
//										DocumentHelperV3.getDocumentCodiPerVariableJbpm(logo.getName()),
//										new Date(),
//										false,
//										null,
//										"document.pdf",
//										generaDocumentBuid(logo, "esborrat", "del procés "+pid),
//										null,
//										null,
//										null,
//										null).getId();
//								workflowEngineApi.setProcessInstanceVariable(
//										pid,
//										logo.getName(),
//										documentStoreId);
//							} else {
//								workflowEngineApi.setProcessInstanceVariable(
//										pid,
//										logo.getName(),
//										logo.getValorInicial());
//							}
//						} else if (updated && !created) {
//							if (debugRetroces)
//								logger.info(">>> [RETLOG] Actualitzar variable " + logo.getName() + " del proces (" + pid + ") amb el valor (" + logo.getValorInicial() + ")");
//							if (logo.getName().startsWith(JbpmVars.PREFIX_DOCUMENT)) {
//								// Si existissin versions de documents no s'hauria de fer res
//								Long documentStoreId = documentHelper.crearDocument(
//										null,
//										pid,
//										DocumentHelperV3.getDocumentCodiPerVariableJbpm(logo.getName()),
//										new Date(),
//										false,
//										null,
//										"document.pdf",
//										generaDocumentBuid(logo, "modificació", "del procés "+pid),
//										null,
//										null,
//										null,
//										null,
//										true,
//										null).getId();
//								workflowEngineApi.setProcessInstanceVariable(
//										pid,
//										logo.getName(),
//										documentStoreId);
//							} else {
//								workflowEngineApi.setProcessInstanceVariable(
//										pid,
//										logo.getName(),
//										logo.getValorInicial());
//							}
//						}
//					}
//					break;
//				case LogObjectDto.LOG_OBJECT_VARTASCA:
//					if (logo.getName() != null) {
//						// Només processa el log si la tasca a la qual correspon la modificació
//						// no s'ha iniciat a dins els logs que volem retrocedir
//						boolean hiHaLogTasca = false;
//						boolean logTascaStarted = false;
//						for (LogObjectDto lo: LogObjectDtos) {
//							if (lo.getTipus() == LogObjectDto.LOG_OBJECT_TASK && lo.getObjectId() == logo.getTaskInstanceId()) {
//								hiHaLogTasca = true;
//								logTascaStarted = workflowEngineApi.hasStartBetweenLogs(beginLogId, endLogId, logo.getTaskInstanceId());
//								break;
//							}
//						}
//						if (!hiHaLogTasca || (hiHaLogTasca && !logTascaStarted)) {
//							JbpmTask task = workflowEngineApi.findEquivalentTaskInstance(logo.getTokenId(), logo.getTaskInstanceId());
//								//String tid = new Long(logo.getTaskInstanceId()).toString();
//							if (created && !deleted) {
//								if (debugRetroces)
//									logger.info(">>> [RETLOG] Esborrar variable " + logo.getName() + " de la tasca (" + task.getId() + ")");
//								workflowEngineApi.deleteTaskInstanceVariable(
//										task.getId(),
//										logo.getName());
//								// Si la variable ha estat creada mitjançant el DefaultControllerHandler fa un setVariableLocally
//								Tasca tasca = tascaRepository.findByJbpmNameAndDefinicioProcesJbpmId(
//										task.getTaskName(),
//										task.getProcessDefinitionId());
//								Long expedientTipusId = expedientTipusHelper.findIdByProcessInstanceId(task.getProcessInstanceId());
//								CampTasca campTasca = campTascaRepository.findAmbTascaCodi(
//										tasca.getId(),
//										logo.getName(),
//										expedientTipusId);
//								if (campTasca != null) {
//									workflowEngineApi.setTaskInstanceVariable(
//											task.getId(),
//											logo.getName(),
//											null);
//								}
//								// Si la variable correspon a un document vol dir que també l'hem d'esborrar
//								if (logo.getName().startsWith(JbpmVars.PREFIX_DOCUMENT)) {
//									documentHelper.esborrarDocument(
//											task.getId(),
//											null,
//											DocumentHelperV3.getDocumentCodiPerVariableJbpm(logo.getName()));
//								}
//							} else if (!created && deleted) {
//								if (debugRetroces)
//									logger.info(">>> [RETLOG] Crear variable " + logo.getName() + " de la tasca (" + task.getId() + ") amb el valor (" + logo.getValorInicial() + ")");
//								if (logo.getName().startsWith(JbpmVars.PREFIX_DOCUMENT)) {
//									// Si existissin versions de documents no s'hauria de fer res
//									Long documentStoreId = documentHelper.crearDocument(
//											task.getId(),
//											task.getProcessInstanceId(),
//											DocumentHelperV3.getDocumentCodiPerVariableJbpm(logo.getName()),
//											new Date(),
//											false,
//											null,
//											"document.pdf",
//											generaDocumentBuid(logo, "esborrat", "de la tasca "+task.getId()),
//											null,
//											null,
//											null,
//											null).getId();
//									workflowEngineApi.setTaskInstanceVariable(
//											task.getId(),
//											logo.getName(),
//											documentStoreId);
//								} else {
//									workflowEngineApi.setTaskInstanceVariable(
//											task.getId(),
//											logo.getName(),
//											logo.getValorInicial());
//								}
//							} else if (!created && !deleted) {
//								if (debugRetroces)
//									logger.info(">>> [RETLOG] Actualitzar variable " + logo.getName() + " de la tasca (" + task.getId() + ") amb el valor (" + logo.getValorInicial() + ")");
//								if (logo.getName().startsWith(JbpmVars.PREFIX_DOCUMENT)) {
//									// Si existissin versions de documents no s'hauria de fer res
//									Long documentStoreId = documentHelper.crearDocument(
//											task.getId(),
//											new Long(logo.getProcessInstanceId()).toString(),
//											DocumentHelperV3.getDocumentCodiPerVariableJbpm(logo.getName()),
//											new Date(),
//											false,
//											null,
//											"document.pdf",
//											generaDocumentBuid(logo, "modificació", "de la tasca "+task.getId()),
//											null,
//											null,
//											null,
//											null).getId();
//									workflowEngineApi.setTaskInstanceVariable(
//											task.getId(),
//											logo.getName(),
//											documentStoreId);
//								} else {
//									workflowEngineApi.setTaskInstanceVariable(
//											task.getId(),
//											logo.getName(),
//											logo.getValorInicial());
//								}
//							}
//						}
//					}
//					break;
//				case LogObjectDto.LOG_OBJECT_ACTION:
//					if (debugRetroces)
//						logger.info(">>> [RETLOG] Executar accio inversa " + logo.getObjectId());
//					String pid = new Long(logo.getProcessInstanceId()).toString();
//					List<String> params = null;
//					String paramsStr = paramsAccio.get(logo.getProcessInstanceId() + "_" + logo.getName());
//					if (paramsStr != null) {
//						params = new ArrayList<String>();
//						String[] parts = paramsStr.split(BasicActionHandler.PARAMS_RETROCEDIR_SEPARADOR);
//						for (String part: parts) {
//							if (part.length() > 0)
//								params.add(part);
//						}
//					}
//					// Retrocedeix l'acció
//					workflowEngineApi.retrocedirAccio(
//							pid,
//							logo.getName(),
//							params,
//							herenciaHelper.getProcessDefinitionIdHeretadaAmbPid(pid)
//							);
//					break;
//				case LogObjectDto.LOG_OBJECT_INFO:
//					Expedient expedient = expedientHelper.findExpedientByProcessInstanceId(String.valueOf(logo.getProcessInstanceId()));
//
//					LogInfo li = LogInfo.valueOf(logo.getName());
//					switch (li) {
//						case NUMERO:
//							expedient.setNumero((String)logo.getValorInicial());
//							break;
//						case TITOL:
//							expedient.setTitol((String)logo.getValorInicial());
//							break;
//						case RESPONSABLE:
//							expedient.setResponsableCodi((String)logo.getValorInicial());
//							break;
//						case COMENTARI:
//							expedient.setComentari((String)logo.getValorInicial());
//							break;
//						case GEOREFERENCIA:
//							expedient.setGeoReferencia((String)logo.getValorInicial());
//							break;
//						case GRUP:
//							expedient.setGrupCodi((String)logo.getValorInicial());
//							break;
//						case INICI:
//							expedient.setDataInici((Date)logo.getValorInicial());
//							break;
//						case ESTAT:
//							expedient.setEstat((Estat)logo.getValorInicial());
//							break;
//						case GEOPOSICIOX:
//							expedient.setGeoPosX((Double)logo.getValorInicial());
//							break;
//						case GEOPOSICIOY:
//							expedient.setGeoPosY((Double)logo.getValorInicial());
//							break;
//						default:
//							break;
//					}
//					break;
//				}
//			}
//			mesuresTemporalsHelper.mesuraCalcular("Retrocedir" + (retrocedirPerTasques ? " per tasques" : ""), "expedient", expedientLog.getExpedient().getTipus().getNom(), null, "realitzar accions de retroces");
//		}
//
//		// Si retrocedim la tasca actual...
//		if (tascaActual) {
////			Node ndesti = workflowEngineApi.getNodeByName(expedientLog.getProcessInstanceId(), desti);
//			boolean enterNode = retrocedirPerTasques; //&& (nodeDesti.getId() == jtask.getTask().getTask().getTaskNode().getId()); // és la tasca a la que volem retrocedir!!
//			boolean executeNode = (!workflowEngineApi.isProcessStateNodeJoinOrFork( //(!workflowEngineApi.isProcessStateNode(
//					expedientLog.getProcessInstanceId(),
//					nodeDesti.getName()));
//			if (enterNode) {
//				nodeEnterTokenId = currentToken.getId();
//			}
//			if (debugRetroces)
//				logger.info(">>> [RETLOG] Retornar token (name=" + currentToken.getName() + ") al node (name=" + nodeDesti.getName() + ", enter = " + enterNode + ", execute=" + executeNode + ")");
//			workflowEngineApi.tokenRedirect(
//					currentToken.getId(),
//					nodeDesti.getName(),
//					true,
//					enterNode,
//					executeNode);
//
//			if (debugRetroces) {
//				logger.info(">>> [LOGTOKEN] Retroces després token redirect (" + currentToken.getName() + ") - End: " + currentToken.getEnd());
//			}
//		}
//		if (retrocedirPerTasques && nodeEnterTokenId != null) {
//			JbpmTask task = workflowEngineApi.findEquivalentTaskInstance(nodeEnterTokenId, Long.valueOf(expedientLog.getTargetId()));
//			TaskInstance ti = task.getTask();
//			ContextInstance ci = ti.getProcessInstance().getContextInstance();
//			for (CampTasca camp: getCampsPerTaskInstance(ti)) {
//				if (camp.isReadFrom()) {
//					if (debugRetroces)
//						logger.info(">>> [RETVAR] Carregar variable del procés " + camp.getCamp().getCodi() + " a la tasca " + task.getTaskName() + " (" + task.getId() + ")");
//					String codi = camp.getCamp().getCodi();
//					Object valor = ci.getVariable(codi);
//					if (valor != null) {
//						ti.setVariableLocally(
//								codi,
//								ci.getVariable(codi));
//					} else {
//						ti.deleteVariableLocally(codi);
//					}
//				}
//			}
//			for (DocumentTasca document: getDocumentsPerTaskInstance(ti)) {
//				String codi = JbpmVars.PREFIX_DOCUMENT + document.getDocument().getCodi();
//				if (!document.isReadOnly()) {
//					Object valor = ci.getVariable(JbpmVars.PREFIX_DOCUMENT + document.getDocument().getCodi());
//					if (valor != null) {
//						if (debugRetroces)
//							logger.info(">>> [RETDOC] Carregar document del procés " + codi + " a la tasca " + task.getTaskName() + " (" + task.getId() + ")");
//						ti.setVariableLocally(
//								codi,
//								ci.getVariable(codi));
//
//						if(!document.getDocument().isIgnored()) {
//							String pid = new Long(ti.getProcessInstance().getId()).toString();
//							documentHelper.esborrarDocument(
//									null,
//									pid,
//									DocumentHelperV3.getDocumentCodiPerVariableJbpm(codi));
//						}
//					}
//				}
//			}
//		}
//
//		for (ExpedientLog elog: expedientLogs) {
//			// Marca les accions com a retrocedides
//			if (ExpedientLogEstat.NORMAL.equals(elog.getEstat()))
//				if (retrocedirPerTasques) {
//					elog.setEstat(ExpedientLogEstat.RETROCEDIT_TASQUES);
//				} else {
//					elog.setEstat(ExpedientLogEstat.RETROCEDIT);
//				}
//			if (elog.getId() != iniciadorId) elog.setIniciadorRetroces(iniciadorId);
//			// Retrocediex la informació de l'expedient
//		}
	}

	public byte[] generaDocumentBuid(LogObjectDto logo, String tipusAccio, String origen) {

		try {

			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
			Date dataActual = Calendar.getInstance().getTime();

			com.lowagie.text.Document document = new com.lowagie.text.Document();
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			PdfWriter.getInstance(document, bos);
			document.open();

			Font fontTitol = new Font(Font.HELVETICA, 18, Font.BOLD, new Color(255, 149, 35));
			Paragraph p = new Paragraph("Avís d'HELIUM", fontTitol);
			p.setAlignment(Paragraph.ALIGN_CENTER);
			p.setSpacingAfter(10f);
			document.add(p);

			Paragraph p2 = new Paragraph("Retroacció de tipus "+tipusAccio+", corresponent al document amb codi "+logo.getName().replace("H3l1um#document.", "")+" "+origen+", realitzada el dia "+sdf.format(dataActual)+"h.");
			p2.setSpacingAfter(10f);
			document.add(p2);

			Paragraph peu = new Paragraph("No s'ha pogut recuperar el contingut previ del document. El contingut que esteu veient ha estat generat automàticament.");
			document.add(peu);
			document.close();
			bos.close();
			return bos.toByteArray();
		} catch (Exception ex) {
			logger.error("Error al generar document buid per retroacció de variable.", ex);
			return null;
		}
	}

	/** Completa el map de paràmetres <processInstanceId _ actionName, valor> amb els possibles paràmetres associats a accions
	 * de definicions anteriors. En la versió 3.2.106 s'ha detectat que si un expedient canvia de versió llavors els nodes
	 * action s'actualitzen i els paràmetres de retroacció amb un ID anterior no es troben bé.
	 *
	 * @param paramsAccio
	 * @param logObjectDtos
	 */
	private void consultarParametresRetroaccio(
			Map<String, String> paramsAccio,
			Collection<LogObjectDto> logObjectDtos) {

//		Set<String> processInstancesConsultats = new HashSet<String>();
//		Map<String, Object> variables;
//		String processInstanceId;
//		String actionNodeId;
//		Action action;
//		for (LogObjectDto logo: logObjectDtos) {
//			if (logo.getTipus() == LogObjectDto.LOG_OBJECT_ACTION) {
//				processInstanceId = String.valueOf(logo.getProcessInstanceId());
//				if (!processInstancesConsultats.contains(processInstanceId)) {
//					processInstancesConsultats.add(processInstanceId);
//					variables = workflowEngineApi.getProcessInstanceVariables(String.valueOf(logo.getProcessInstanceId()));
//					if (variables != null) {
//						for (String variableName : variables.keySet())
//							if (variableName.startsWith(BasicActionHandler.PARAMS_RETROCEDIR_VARIABLE_PREFIX)) {
//								// Recupera la informació del node
//								actionNodeId = variableName.substring(BasicActionHandler.PARAMS_RETROCEDIR_VARIABLE_PREFIX.length(), variableName.length());
//								action = NumberUtils.isNumber(actionNodeId) ? workflowEngineApi.getActionById(Long.valueOf(actionNodeId)) : null;
//								if (action != null) {
//									paramsAccio.put(logo.getProcessInstanceId() + "_" + action.getName(), String.valueOf(variables.get(variableName)));
//								}
//							}
//					}
//				}
//			}
//		}
	}

	public String getActorsPerReassignacioTasca(String taskInstanceId) {
		WTaskInstance task = workflowEngineApi.getTaskById(taskInstanceId);
		String actors = "";
		if (task.getActorId() != null) {
			actors = task.getActorId();
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

	private List<DocumentTasca> getDocumentsPerTaskInstance(WTaskInstance taskInstance) {
		String processDefinitionId = taskInstance.getProcessDefinitionId();
		Tasca tasca = tascaRepository.findByJbpmNameAndDefinicioProcesJbpmId(
				taskInstance.getTaskName(),
				processDefinitionId);
		return tasca.getDocuments();
	}

	private WNode getForkNode(String processInstanceId, Object joinNode) {
		List<WProcessLog> logsJbpm = getJbpmLogsPerInstanciaProces(processInstanceId, false);
		if (logsJbpm != null && logsJbpm.size() > 0) {
			boolean trobat = false;
			WToken token = null;
			for (WProcessLog plog: logsJbpm) {
				if (plog instanceof WTransitionLog
						&& ((WTransitionLog) plog).getSourceNode().equals(joinNode)) {
					token = plog.getToken();
					trobat = true;
					continue;
				}
				if (trobat) {
					if (plog instanceof WTransitionLog
							&& ((WTransitionLog) plog).getDestinationNode().getNodeType() == WNodeType.Fork
							&& ((WTransitionLog) plog).getToken().equals(token)) {
						return ((WTransitionLog) plog).getDestinationNode();
					}
				}
			}
		}
		return null;
	}

	public List<ExpedientLog> findLogsRetrocedits(Long expedientLogId) {
		List<ExpedientLog> resposta = new ArrayList<ExpedientLog>();
		ExpedientLog expedientLog = expedientLoggerRepository.getReferenceById(expedientLogId);
		if (ExpedientLogAccioTipus.EXPEDIENT_RETROCEDIR.equals(expedientLog.getAccioTipus()) ||
			ExpedientLogAccioTipus.EXPEDIENT_RETROCEDIR_TASQUES.equals(expedientLog.getAccioTipus())) {
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

	private List<CampTasca> getCampsPerTaskInstance(WTaskInstance taskInstance) {
		String processDefinitionId = taskInstance.getProcessDefinitionId();
		Tasca tasca = tascaRepository.findByJbpmNameAndDefinicioProcesJbpmId(
				taskInstance.getTaskName(),
				processDefinitionId);
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

	public ExpedientLog afegirLogExpedientPerTasca(
			WTaskInstance task,
			ExpedientLogAccioTipus tipus,
			String accioParams,
			String user) {
		return afegirLogExpedientPerTasca(task.getId(), null, tipus, accioParams, user);
	}

	public ExpedientLog afegirLogExpedientPerTasca(
			String taskInstanceId,
			ExpedientLogAccioTipus tipus,
			String accioParams) {
		return afegirLogExpedientPerTasca(taskInstanceId, null, tipus, accioParams, null);
	}

	public ExpedientLog afegirLogExpedientPerTasca(
			String taskInstanceId,
			Long expedientId,
			ExpedientLogAccioTipus tipus,
			String accioParams,
			String user) {

		WTaskInstance task = workflowEngineApi.getTaskById(taskInstanceId);
		Expedient expedient = null;
		if (expedientId == null) {
			expedient = expedientHelper.findExpedientByProcessInstanceId(task.getProcessInstanceId());
		} else {
			expedient = expedientRepository.getReferenceById(expedientId);
		}

		Long jbpmLogId = null;
		if (expedient.isAmbRetroaccio()) {
			jbpmLogId = workflowEngineApi.addTaskInstanceMessageLog(
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
		expedientLog.setProcessInstanceId(task.getProcessInstanceId());
		expedientLog.setJbpmLogId(jbpmLogId);
		if (accioParams != null)
			expedientLog.setAccioParams(accioParams);
		expedientLoggerRepository.save(expedientLog);
		return expedientLog;
	}

	public ExpedientLog afegirLogExpedientPerProces(
			String processInstanceId,
			ExpedientLogAccioTipus tipus,
			String accioParams) {

		Expedient expedient = expedientHelper.findExpedientByProcessInstanceId(processInstanceId);

		Long jbpmLogId = null;
		if (expedient.isAmbRetroaccio()) {
			jbpmLogId = workflowEngineApi.addProcessInstanceMessageLog(
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
		expedientLog.setProcessInstanceId(processInstanceId);
		expedientLog.setJbpmLogId(jbpmLogId);
		if (accioParams != null)
			expedientLog.setAccioParams(accioParams);
		expedientLoggerRepository.save(expedientLog);
		return expedientLog;
	}

	public List<Object> findLogIdTasquesById(List<String> tasquesIds) {
		return expedientLoggerRepository.findLogIdTasquesById(tasquesIds);
	}

	private static final Log logger = LogFactory.getLog(ExpedientLoggerHelper.class);
}
