package net.conselldemallorca.helium.jbpm3.service;

import net.conselldemallorca.helium.api.dto.CampTascaDto;
import net.conselldemallorca.helium.api.dto.CampTipusDto;
import net.conselldemallorca.helium.api.dto.CampTipusIgnored;
import net.conselldemallorca.helium.api.dto.DocumentTascaDto;
import net.conselldemallorca.helium.api.dto.ExpedientLogDto;
import net.conselldemallorca.helium.api.dto.LogObjectDto;
import net.conselldemallorca.helium.api.service.JbpmRetroaccioApi;
import net.conselldemallorca.helium.api.service.WProcessInstance;
import net.conselldemallorca.helium.api.service.WTaskInstance;
import net.conselldemallorca.helium.api.service.WToken;
import net.conselldemallorca.helium.api.service.WorkflowBridgeService;
import net.conselldemallorca.helium.api.service.WorkflowEngineApi;
import net.conselldemallorca.helium.api.service.WorkflowRetroaccioApi.ExpedientRetroaccioEstat;
import net.conselldemallorca.helium.api.service.WorkflowRetroaccioApi.ExpedientRetroaccioTipus;
import net.conselldemallorca.helium.api.service.WorkflowRetroaccioApi.RetroaccioInfo;
import net.conselldemallorca.helium.jbpm3.command.*;
import net.conselldemallorca.helium.jbpm3.handlers.BasicActionHandler;
import net.conselldemallorca.helium.jbpm3.helper.CommandHelper;
import net.conselldemallorca.helium.jbpm3.integracio.JbpmTask;
import net.conselldemallorca.helium.jbpm3.integracio.JbpmToken;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.math.NumberUtils;
import org.jbpm.bytes.ByteArray;
import org.jbpm.command.CancelTokenCommand;
import org.jbpm.command.CommandService;
import org.jbpm.command.GetProcessInstanceCommand;
import org.jbpm.command.GetTaskInstanceCommand;
import org.jbpm.context.exe.ContextInstance;
import org.jbpm.context.exe.VariableInstance;
import org.jbpm.context.log.VariableCreateLog;
import org.jbpm.context.log.VariableDeleteLog;
import org.jbpm.context.log.VariableLog;
import org.jbpm.context.log.VariableUpdateLog;
import org.jbpm.graph.def.Action;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import static net.conselldemallorca.helium.jbpm3.integracio.JbpmVars.*;

@Slf4j
@Service
public class JbpmRetroaccioApiImpl implements JbpmRetroaccioApi {

    private static final String MESSAGE_LOG_PREFIX = "[H3l1um]";
    private static final String MESSAGE_LOGINFO_PREFIX = "[H3l1nf0]";

    @Autowired
    CommandService commandService;
    @Autowired
    CommandHelper commandHelper;
    @Autowired
    WorkflowEngineApi workflowEngineApi;
    @Autowired
    WorkflowBridgeService workflowBridgeService;


    public void deleteProcessInstanceTreeLogs(String rootProcessInstanceId) {
        final long id = Long.parseLong(rootProcessInstanceId);
        GetProcessInstancesTreeCommand command = new GetProcessInstancesTreeCommand(id);
        for (ProcessInstance pd: (List<ProcessInstance>)commandService.execute(command)) {
            DeleteProcessInstanceLogsCommand deleteCommand = new DeleteProcessInstanceLogsCommand(pd);
            commandService.execute(deleteCommand);
        }
    }

    public Long addProcessInstanceMessageLog(
            String processInstanceId,
            String message) {
        final long id = Long.parseLong(processInstanceId);
        AddProcessInstanceMessageLogCommand command = new AddProcessInstanceMessageLogCommand(id, message);
        long resultat = ((Long)commandService.execute(command)).longValue();
        return resultat;
    }

    public Long addTaskInstanceMessageLog(
            String taskInstanceId,
            String message) {
        final long id = Long.parseLong(taskInstanceId);
        AddTaskInstanceMessageLogCommand command = new AddTaskInstanceMessageLogCommand(id, message);
        long resultat = ((Long)commandService.execute(command)).longValue();
        return resultat;
    }

    public void retrocedirFinsLog(
            ExpedientLogDto expedientLog,
            List<ExpedientLogDto> expedientLogs,
            boolean retrocedirPerTasques,
            Long iniciadorId) {
        boolean debugRetroces = true;

        WTaskInstance jtask = workflowEngineApi.getTaskById(expedientLog.getTargetId());

        // Variables per a desar la informació per a executar el node enter al final de tot
        Long nodeEnterTokenId = null;

//        List<ExpedientLogDto> expedientLogs = expedientLoggerRepository.findAmbExpedientIdOrdenatsPerData(
//                expedientLog.getExpedientId());

        expedientLogs = filtraExpedientsLogPerRetrocedir(expedientLogs, expedientLog.getId(), retrocedirPerTasques);

        // Retrocedeix els canvis al jBPM relacionats amb els logs
        List<ProcessLog> logsJbpm = getLogsJbpmPerRetrocedir(expedientLogs); //, expedientLogId);

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
        addProcessInstanceMessageLog(
                expedientLog.getProcessInstanceId(),
                getMessageLogPerTipus(retrocedirPerTasques ? ExpedientRetroaccioTipus.EXPEDIENT_RETROCEDIR_TASQUES : ExpedientRetroaccioTipus.EXPEDIENT_RETROCEDIR));
        if (logsJbpm != null && !logsJbpm.isEmpty()) {
            // Recull totes les accions executades a jBPM relacionats amb els logs
            Collection<LogObjectDto> logObjectDtos = getAccionsJbpmPerRetrocedir(
                    expedientLogs,
                    logsJbpm);
            if (debugRetroces) {
                for (LogObjectDto logo: logObjectDtos) {
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
                    log.info(logInfo);
                }
            }

            // Emmagatzema els paràmetres per a retrocedir cada acció per parella [processInstanceId, action_name]
            Map<String, String> paramsAccio = new HashMap<String, String>();
            String varName;
            for (LogObjectDto logo: logObjectDtos) {
                if (logo.getTipus() == LogObjectDto.LOG_OBJECT_ACTION) {
                    String params;
                    String paramKey;

                    paramKey = logo.getProcessInstanceId() + "_" + logo.getName();

                    // Consulta les variables de retrocés guardades abans de la versió 3.2.106 amb action node id
                    varName = BasicActionHandler.PARAMS_RETROCEDIR_VARIABLE_PREFIX + new Long(logo.getObjectId());
                    params = (String)workflowEngineApi.getProcessInstanceVariable(
                            new Long(logo.getProcessInstanceId()).toString(),
                            varName);
                    if (params != null)
                        paramsAccio.put(paramKey, params);

                    // Consulta les variables de retrocés guardades a partir de la versió 3.2.106 amb actionName
                    varName = BasicActionHandler.PARAMS_RETROCEDIR_VARIABLE_PREFIX + logo.getName();
                    params = (String)workflowEngineApi.getProcessInstanceVariable(
                            new Long(logo.getProcessInstanceId()).toString(),
                            varName);
                    if (params != null && !paramsAccio.containsKey(paramKey))
                        paramsAccio.put(paramKey, params);
                }
            }
            // Completa els paràmetres amb paràmetres que podrien tenir una relació amb un node Action d'una definició anterior a la versió 3.2.106
            this.consultarParametresRetroaccio(
                    paramsAccio,
                    logObjectDtos);

            // comprovam si estem retrocedint únicament la tasca actual
            if (jtask != null) {
                WProcessInstance pi = workflowEngineApi.getProcessInstance(String.valueOf(expedientLog.getProcessInstanceId()));
                currentToken = getProcessLogById(expedientLog.getJbpmLogId()).getToken();
                Collection<TaskInstance> tis = ((ProcessInstance)pi.getProcessInstance()).getTaskMgmtInstance().getUnfinishedTasks(currentToken);
                for (TaskInstance ti: tis) {
                    if (ti.getId() == ((JbpmTask)jtask).getTaskInstance().getId()){
                        nodeDesti = ti.getTask().getTaskNode();
                        tascaActual = true;
                        if (debugRetroces)
                            log.info(">>> [LOGTASK] Retroces de la tasca actual (" + nodeDesti + ")!");
                        break;
                    }
                }
            }

            // Executa les accions necessàries per a retrocedir l'expedient
            for (LogObjectDto logo: logObjectDtos) {
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
                                log.info(">>> [RETLOG] Cancel·lar/finalitzar procés (" + logo.getName() + ")");
                            cancelProcessInstance(logo.getObjectId());
                        } else if (!started && ended) {
                            if (debugRetroces)
                                log.info(">>> [RETLOG] Desfer finalitzar procés (" + logo.getName() + ")");
                            revertProcessInstanceEnd(logo.getObjectId());
                            WProcessInstance jpi = workflowEngineApi.getProcessInstance(String.valueOf(logo.getProcessInstanceId()));
                            if (debugRetroces)
                                log.info(">>> [RETLOG] Desfer finalitzar token (" + ((ProcessInstance)jpi.getProcessInstance()).getRootToken().getFullName() + ")");
                            revertTokenEnd(((ProcessInstance)jpi.getProcessInstance()).getRootToken().getId());
                        }
                        break;
                    case LogObjectDto.LOG_OBJECT_TOKEN:
                        if (debugRetroces) {
                            WToken jtok = workflowEngineApi.getTokenById(String.valueOf(logo.getObjectId()));
                            log.info(">>> [LOGTOKEN] Inici Retroces token (" + logo.getName() + ") - End: " + jtok.getEnd());
                        }
                        if (started && !ended) {
                            if (debugRetroces)
                                log.info(">>> [RETLOG] Cancel·lar token (" + logo.getName() + ")");
                            cancelToken(logo.getObjectId());

                            if (debugRetroces) {
                                WToken jtok = workflowEngineApi.getTokenById(String.valueOf(logo.getObjectId()));
                                log.info(">>> [LOGTOKEN] Retroces token cancelat (" + logo.getName() + ") - End: " + jtok.getEnd());
                            }
                        } else if (!started && ended) {
                            if (debugRetroces)
                                log.info(">>> [RETLOG] Desfer finalitzar token (" + logo.getName() + ")");
                            revertTokenEnd(logo.getObjectId());

                            if (debugRetroces) {
                                WToken jtok = workflowEngineApi.getTokenById(String.valueOf(logo.getObjectId()));
                                log.info(">>> [LOGTOKEN] Retroces revert token end (" + logo.getName() + ") - End: " + jtok.getEnd());
                            }
                        }
                        if (!started) {
                            // Només ha d'executar el node si no és una instància de procés
                            // o un join o un fork
                            String desti = (String)logo.getValorInicial();
                            if (isJoinNode(logo.getProcessInstanceId(), (String)logo.getValorInicial())) {
                                Node joinNode = getNodeByName(logo.getProcessInstanceId(), desti);
                                Node forkNode = getForkNode(logo.getProcessInstanceId(), joinNode);
                                if (forkNode != null)
                                    desti = forkNode.getName();
                            }

                            if (debugRetroces) {
                                WToken jtok = workflowEngineApi.getTokenById(String.valueOf(logo.getObjectId()));
                                log.info(">>> [LOGTOKEN] Retroces abans token redirect (" + logo.getName() + ") - End: " + jtok.getEnd());
                            }

//						boolean enterNode = (nodeRetrocedir == logo.getLogId());
                            Node ndesti = getNodeByName(logo.getProcessInstanceId(), desti);
                            boolean enterNode = retrocedirPerTasques && (jtask != null && ndesti.getId() == ((JbpmTask)jtask).getTaskInstance().getTask().getTaskNode().getId()); // és la tasca a la que volem retrocedir!!
                            boolean executeNode = (!isProcessStateNodeJoinOrFork(
                                    logo.getProcessInstanceId(),
                                    (String)logo.getValorInicial()));
                            if (enterNode) {
//							nodeEnterObjectId = logo.getObjectId();
                                nodeEnterTokenId = logo.getTokenId();
                            }
                            if (debugRetroces)
                                log.info(">>> [RETLOG] Retornar token (name=" + logo.getName() + ") al node (name=" + desti + ", enter = " + enterNode + ", execute=" + executeNode + ")");
                            workflowEngineApi.tokenRedirect(
                                    String.valueOf(logo.getObjectId()),
                                    desti,
                                    true,
                                    enterNode,
                                    executeNode);

                            if (debugRetroces) {
                                WToken jtok = workflowEngineApi.getTokenById(String.valueOf(logo.getObjectId()));
                                log.info(">>> [LOGTOKEN] Retroces després token redirect (" + logo.getName() + ") - End: " + jtok.getEnd());
                            }
                        }
                        break;
                    case LogObjectDto.LOG_OBJECT_TASK:
                        boolean tascaStarted = hasStartBetweenLogs(beginLogId, endLogId, logo.getObjectId());
                        if (!tascaStarted && assigned) {
                            WTaskInstance task = findEquivalentTaskInstance(logo.getTokenId(), logo.getObjectId());
                            String valor = (String)logo.getValorInicial();
                            if (valor != null && !"".equals(valor)) {
                                if (debugRetroces)
                                    log.info(">>> [RETLOG] Reassignar tasca (" + task.getId() + ") a " + valor);
                                if (valor.startsWith("[") && valor.endsWith("]")) {
                                    String[] actors = valor.substring(1, valor.length()-1).split(",");
                                    workflowEngineApi.setTaskInstancePooledActors(
                                            task.getId(),
                                            actors);
                                } else {
                                    workflowEngineApi.setTaskInstanceActorId(
                                            task.getId(),
                                            valor);
                                }
                            }
                        } else if (!tascaStarted && ended) {
                            WTaskInstance task = findEquivalentTaskInstance(logo.getTokenId(), logo.getObjectId());
                            if (debugRetroces)
                                log.info(">>> [RETLOG] Copiar variables de la tasca (id=" + logo.getObjectId() + ") a la tasca (id=" + task.getId() + ")");
                            Map<String, Object> vars = workflowEngineApi.getTaskInstanceVariables(new Long(logo.getObjectId()).toString());
                            workflowEngineApi.setTaskInstanceVariables(task.getId(), vars, true);
                        }
                        break;
                    case LogObjectDto.LOG_OBJECT_VARPROCES:
                        if (logo.getName() != null) {
                            String pid = new Long(logo.getProcessInstanceId()).toString();
                            if (created && !deleted) {
                                if (debugRetroces)
                                    log.info(">>> [RETLOG] Esborrar variable " + logo.getName() + " del proces (" + pid + ")");
                                workflowEngineApi.deleteProcessInstanceVariable(
                                        pid,
                                        logo.getName());
                                if (logo.getName().startsWith(PREFIX_DOCUMENT)) {
                                    workflowBridgeService.documentExpedientEsborrar(
                                            null,
                                            pid,
                                            getDocumentCodiPerVariableJbpm(logo.getName()));
                                }
                            } else if (!created && deleted) {
                                if (debugRetroces)
                                    log.info(">>> [RETLOG] Crear variable " + logo.getName() + " del proces (" + pid + ") amb el valor (" + logo.getValorInicial() + ")");
                                if (logo.getName().startsWith(PREFIX_DOCUMENT)) {
                                    // Si existissin versions de documents no s'hauria de fer res
                                    Long documentStoreId = workflowBridgeService.documentExpedientCrear(
                                            null,
                                            pid,
                                            getDocumentCodiPerVariableJbpm(logo.getName()),
                                            new Date(),
                                            false,
                                            null,
                                            "document.pdf",
                                            getContingutRecurs("document_buit.pdf"));
                                    workflowEngineApi.setProcessInstanceVariable(
                                            pid,
                                            logo.getName(),
                                            documentStoreId);
                                } else {
                                    workflowEngineApi.setProcessInstanceVariable(
                                            pid,
                                            logo.getName(),
                                            logo.getValorInicial());
                                }
                            } else if (!created && !deleted) {
                                if (debugRetroces)
                                    log.info(">>> [RETLOG] Actualitzar variable " + logo.getName() + " del proces (" + pid + ") amb el valor (" + logo.getValorInicial() + ")");
                                if (logo.getName().startsWith(PREFIX_DOCUMENT)) {
                                    // Si existissin versions de documents no s'hauria de fer res
                                    Long documentStoreId = workflowBridgeService.documentExpedientCrear(
                                            null,
                                            pid,
                                            getDocumentCodiPerVariableJbpm(logo.getName()),
                                            new Date(),
                                            false,
                                            null,
                                            "document.pdf",
                                            getContingutRecurs("document_buit.pdf"));
                                    workflowEngineApi.setProcessInstanceVariable(
                                            pid,
                                            logo.getName(),
                                            documentStoreId);
                                } else {
                                    workflowEngineApi.setProcessInstanceVariable(
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
                            for (LogObjectDto lo: logObjectDtos) {
                                if (lo.getTipus() == LogObjectDto.LOG_OBJECT_TASK && lo.getObjectId() == logo.getTaskInstanceId()) {
                                    hiHaLogTasca = true;
                                    logTascaStarted = hasStartBetweenLogs(beginLogId, endLogId, logo.getTaskInstanceId());
                                    break;
                                }
                            }
                            if (!hiHaLogTasca || (hiHaLogTasca && !logTascaStarted)) {
                                WTaskInstance task = findEquivalentTaskInstance(logo.getTokenId(), logo.getTaskInstanceId());
                                //String tid = new Long(logo.getTaskInstanceId()).toString();
                                if (created && !deleted) {
                                    if (debugRetroces)
                                        log.info(">>> [RETLOG] Esborrar variable " + logo.getName() + " de la tasca (" + task.getId() + ")");
                                    workflowEngineApi.deleteTaskInstanceVariable(
                                            task.getId(),
                                            logo.getName());
                                    // Si la variable ha estat creada mitjançant el DefaultControllerHandler fa un setVariableLocally
                                    CampTascaDto campTasca = workflowBridgeService.getCampTascaPerInstanciaTasca(
                                            task.getTaskName(),
                                            task.getProcessDefinitionId(),
                                            task.getProcessInstanceId(),
                                            logo.getName());
                                    if (campTasca != null) {
                                        workflowEngineApi.setTaskInstanceVariable(
                                                task.getId(),
                                                logo.getName(),
                                                null);
                                    }
                                    // Si la variable correspon a un document vol dir que també l'hem d'esborrar
                                    if (logo.getName().startsWith(PREFIX_DOCUMENT)) {
                                        workflowBridgeService.documentExpedientEsborrar(
                                                task.getId(),
                                                null,
                                                getDocumentCodiPerVariableJbpm(logo.getName()));
                                    }
                                } else if (!created && deleted) {
                                    if (debugRetroces)
                                        log.info(">>> [RETLOG] Crear variable " + logo.getName() + " de la tasca (" + task.getId() + ") amb el valor (" + logo.getValorInicial() + ")");
                                    if (logo.getName().startsWith(PREFIX_DOCUMENT)) {
                                        // Si existissin versions de documents no s'hauria de fer res
                                        Long documentStoreId = workflowBridgeService.documentExpedientCrear(
                                                task.getId(),
                                                task.getProcessInstanceId(),
                                                getDocumentCodiPerVariableJbpm(logo.getName()),
                                                new Date(),
                                                false,
                                                null,
                                                "document.pdf",
                                                getContingutRecurs("document_buit.pdf"));
                                        workflowEngineApi.setTaskInstanceVariable(
                                                task.getId(),
                                                logo.getName(),
                                                documentStoreId);
                                    } else {
                                        workflowEngineApi.setTaskInstanceVariable(
                                                task.getId(),
                                                logo.getName(),
                                                logo.getValorInicial());
                                    }
                                } else if (!created && !deleted) {
                                    if (debugRetroces)
                                        log.info(">>> [RETLOG] Actualitzar variable " + logo.getName() + " de la tasca (" + task.getId() + ") amb el valor (" + logo.getValorInicial() + ")");
                                    if (logo.getName().startsWith(PREFIX_DOCUMENT)) {
                                        // Si existissin versions de documents no s'hauria de fer res
                                        Long documentStoreId = workflowBridgeService.documentExpedientCrear(
                                                task.getId(),
                                                new Long(logo.getProcessInstanceId()).toString(),
                                                getDocumentCodiPerVariableJbpm(logo.getName()),
                                                new Date(),
                                                false,
                                                null,
                                                "document.pdf",
                                                getContingutRecurs("document_buit.pdf"));
                                        workflowEngineApi.setTaskInstanceVariable(
                                                task.getId(),
                                                logo.getName(),
                                                documentStoreId);
                                    } else {
                                        workflowEngineApi.setTaskInstanceVariable(
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
                            log.info(">>> [RETLOG] Executar accio inversa " + logo.getObjectId());
                        String pid = new Long(logo.getProcessInstanceId()).toString();
                        List<String> params = null;
                        String paramsStr = paramsAccio.get(logo.getProcessInstanceId() + "_" + logo.getName());
                        if (paramsStr != null) {
                            params = new ArrayList<String>();
                            String[] parts = paramsStr.split(BasicActionHandler.PARAMS_RETROCEDIR_SEPARADOR);
                            for (String part: parts) {
                                if (part.length() > 0)
                                    params.add(part);
                            }
                        }
                        // Retrocedeix l'acció
                        workflowEngineApi.retrocedirAccio(
                                pid,
                                logo.getName(),
                                params,
                                workflowBridgeService.getProcessDefinitionIdHeretadaAmbPid(pid)
                        );
                        break;
                    case LogObjectDto.LOG_OBJECT_INFO:

                        RetroaccioInfo li = RetroaccioInfo.valueOf(logo.getName());
                        switch (li) {
                            case NUMERO:
                                workflowBridgeService.expedientModificarNumero(
                                        String.valueOf(logo.getProcessInstanceId()),
                                        (String)logo.getValorInicial());
                                break;
                            case TITOL:
                                workflowBridgeService.expedientModificarTitol(
                                        String.valueOf(logo.getProcessInstanceId()),
                                        (String)logo.getValorInicial());
                                break;
                            case RESPONSABLE:
                                workflowBridgeService.expedientModificarResponsable(
                                        String.valueOf(logo.getProcessInstanceId()),
                                        (String)logo.getValorInicial());
                                break;
                            case COMENTARI:
                                workflowBridgeService.expedientModificarComentari(
                                        String.valueOf(logo.getProcessInstanceId()),
                                        (String)logo.getValorInicial());
                                break;
                            case GRUP:
                                workflowBridgeService.expedientModificarGrup(
                                        String.valueOf(logo.getProcessInstanceId()),
                                        (String)logo.getValorInicial());
                                break;
                            case INICI:
                                workflowBridgeService.expedientModificarDataInici(
                                        String.valueOf(logo.getProcessInstanceId()),
                                        (Date)logo.getValorInicial());
                                break;
                            case ESTAT:
                                try {
//                                    Object oestat = logo.getValorInicial();
//                                    Field field = oestat.getClass().getDeclaredField("codi");
//                                    String estat = (String)field.get(oestat);
                                    workflowBridgeService.expedientModificarEstat(
                                            String.valueOf(logo.getProcessInstanceId()),
                                            (Long)logo.getValorInicial());
                                } catch (Exception e){
                                    log.error("No s'ha pogut recuperar l'estat de l'expedient amb PI: " + logo.getProcessInstanceId(), e);
                                }
                                break;
                            case GEOREFERENCIA:
                                workflowBridgeService.expedientModificarGeoreferencia(
                                        String.valueOf(logo.getProcessInstanceId()),
                                        (String)logo.getValorInicial());
                                break;
                            case GEOPOSICIOX:
                                workflowBridgeService.expedientModificarGeoX(
                                        String.valueOf(logo.getProcessInstanceId()),
                                        (Double)logo.getValorInicial());
                                break;
                            case GEOPOSICIOY:
                                workflowBridgeService.expedientModificarGeoY(
                                        String.valueOf(logo.getProcessInstanceId()),
                                        (Double)logo.getValorInicial());
                                break;
                            default:
                                break;
                        }
                        break;
                }
            }
        }

        // Si retrocedim la tasca actual...
        if (tascaActual) {
            boolean enterNode = retrocedirPerTasques; //&& (nodeDesti.getId() == jtask.getTask().getTask().getTaskNode().getId()); // és la tasca a la que volem retrocedir!!
            boolean executeNode = (!isProcessStateNodeJoinOrFork(
                    Long.parseLong(expedientLog.getProcessInstanceId()),
                    nodeDesti.getName()));
            if (enterNode) {
                nodeEnterTokenId = currentToken.getId();
            }
            if (debugRetroces)
                log.info(">>> [RETLOG] Retornar token (name=" + currentToken.getName() + ") al node (name=" + nodeDesti.getName() + ", enter = " + enterNode + ", execute=" + executeNode + ")");
            workflowEngineApi.tokenRedirect(
                    String.valueOf(currentToken.getId()),
                    nodeDesti.getName(),
                    true,
                    enterNode,
                    executeNode);

            if (debugRetroces) {
                log.info(">>> [LOGTOKEN] Retroces després token redirect (" + currentToken.getName() + ") - End: " + currentToken.getEnd());
            }
        }
        if (retrocedirPerTasques && nodeEnterTokenId != null) {
            WTaskInstance task = findEquivalentTaskInstance(nodeEnterTokenId, Long.valueOf(expedientLog.getTargetId()));
            TaskInstance ti = ((JbpmTask)task).getTaskInstance();
            ContextInstance ci = ti.getProcessInstance().getContextInstance();
//            List<CampTascaDto> campsTasca = workflowBridgeService.getCampsPerTaskInstance(
            List<CampTascaDto> campsTasca = workflowBridgeService.findCampsPerTaskInstance(
                    new Long(ti.getProcessInstance().getId()).toString(),
                    new Long(ti.getProcessInstance().getProcessDefinition().getId()).toString(),
                    ti.getTask().getName());
            for (CampTascaDto camp: campsTasca) {
                if (camp.isReadFrom()) {
                    if (debugRetroces)
                        log.info(">>> [RETVAR] Carregar variable del procés " + camp.getCamp().getCodi() + " a la tasca " + task.getTaskName() + " (" + task.getId() + ")");
                    String codi = camp.getCamp().getCodi();
                    Object valor = ci.getVariable(codi);
                    if (valor != null) {
                        ti.setVariableLocally(
                                codi,
                                ci.getVariable(codi));
                    } else {
                        ti.deleteVariableLocally(codi);
                    }
                }
            }
//            List<DocumentTascaDto> documents = workflowBridgeService.getDocumentsPerTaskInstance(
            List<DocumentTascaDto> documents = workflowBridgeService.findDocumentsPerTaskInstance(
                    new Long(ti.getProcessInstance().getId()).toString(),
                    new Long(ti.getProcessInstance().getProcessDefinition().getId()).toString(),
                    ti.getTask().getName());
            for (DocumentTascaDto document: documents) {
                String codi = PREFIX_DOCUMENT + document.getDocument().getCodi();
                if (!document.isReadOnly()) {
                    Object valor = ci.getVariable(PREFIX_DOCUMENT + document.getDocument().getCodi());
                    if (valor != null)
                        if (debugRetroces)
                            log.info(">>> [RETDOC] Carregar document del procés " + codi + " a la tasca " + task.getTaskName() + " (" + task.getId() + ")");
                    ti.setVariableLocally(
                            codi,
                            ci.getVariable(codi));
                }
            }
        }

        // TODO: Desar la informació modificada del log
        for (ExpedientLogDto elog: expedientLogs) {
            // Marca les accions com a retrocedides
            if (ExpedientRetroaccioEstat.NORMAL.equals(elog.getEstat()))
                if (retrocedirPerTasques) {
                    elog.setEstat(ExpedientRetroaccioEstat.RETROCEDIT_TASQUES);
                } else {
                    elog.setEstat(ExpedientRetroaccioEstat.RETROCEDIT);
                }
            if (elog.getId() != iniciadorId) elog.setIniciadorRetroces(iniciadorId);
            // TODO: Fi
        }
    }

    private List<ExpedientLogDto> filtraExpedientsLogPerRetrocedir(
            List<ExpedientLogDto> expedientLogs,
            Long expedientLogId,
            boolean retrocedirPerTasques) {
        Token tokenRetroces = null;
        boolean incloure = false;
        boolean found = false;

        List<ExpedientLogDto> expedientLogsRetrocedir = new ArrayList<ExpedientLogDto>();
        for (ExpedientLogDto elog: expedientLogs) {
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
                if ((elog.isTargetTasca() || elog.isTargetProces())
                        && tokenRetroces != null) {
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

    private JbpmToken getTokenByJbpmLogId(Long jbpmLogId){
        ProcessLog pl = getProcessLogById(jbpmLogId);
        if (pl == null)
            return null;
        return new JbpmToken(pl.getToken());
    }

    private List<ProcessLog> getLogsJbpmPerRetrocedir(List<ExpedientLogDto> expedientLogs) {
        List<ProcessLog> logsJbpm = new ArrayList<ProcessLog>();

        // Utilitzam un set per a evitar repetits
        Set<ProcessLog> setlogsJbpm = new HashSet<ProcessLog>();
        for (ExpedientLogDto elog: expedientLogs) {
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

    private List<ProcessLog> getLogsJbpmPerExpedientLog(ExpedientLogDto expedientLog) {
        List<ProcessLog> logsJbpm = new ArrayList<ProcessLog>();
        List<ProcessLog> logsJbpmAfegir = new ArrayList<ProcessLog>();
        if (expedientLog.getJbpmLogId() != null && expedientLog.getEstat().equals(ExpedientRetroaccioEstat.NORMAL)) {
            // Obtenim els logs jBPM associats a la instància de procés i als seus
            // subprocessos
            logsJbpm = getJbpmLogsPerInstanciaProces(Long.parseLong(expedientLog.getProcessInstanceId()), true);

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

    private List<ProcessLog> getJbpmLogsPerInstanciaProces(Long processInstanceId, boolean asc) {

        List<ProcessLog> logsJbpm = new ArrayList<ProcessLog>();

        WProcessInstance pi = workflowEngineApi.getRootProcessInstance(processInstanceId.toString());
        List<WProcessInstance> processos = workflowEngineApi.getProcessInstanceTree(pi.getId());
        for (WProcessInstance proces: processos) {
            Map<Token, List<ProcessLog>> logsPerInstanciaProces = getProcessInstanceLogs(
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

    private void printLogMessage(
            List<ProcessLog> logs,
            ProcessLog processLog,
            int indent) {
        DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

        StringBuilder logInfo = new StringBuilder("              ");
        for (int i = 0; i < indent; i++) {
            logInfo.append("║  ");
        }
        logInfo.append("╠═>");
        logInfo.append("[").append(df.format(processLog.getDate())).append("] [").append(processLog.getId()).append("] ").append(processLog).append("(").append(processLog.getClass().getName()).append(")");
        log.info(logInfo.toString());

        for (ProcessLog l: logs) {
            if (l.getParent() != null && l.getParent().getId() == processLog.getId()) {
                printLogMessage(logs, l, indent + 1);
            }
        }
    }

    private String getMessageLogPerTipus(ExpedientRetroaccioTipus tipus) {
        return MESSAGE_LOG_PREFIX + tipus.name();
    }

    private Collection<LogObjectDto> getAccionsJbpmPerRetrocedir(
            List<ExpedientLogDto> expedientLogs,
            List<ProcessLog> logsSorted) {
        Map<Long, LogObjectDto> LogObjectDtos = new HashMap<Long, LogObjectDto>();
        long currentMessageLogId = -1;
        for (ProcessLog plog: logsSorted) {
            if (plog instanceof MessageLog) {
                MessageLog mlog = (MessageLog)plog;
                if (mlog.getMessage().startsWith(MESSAGE_LOGINFO_PREFIX)) {

                    Long objId = new Long(plog.getToken().getProcessInstance().getId());
                    LogObjectDto lobj = LogObjectDtos.get(objId);

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
//                                        lobj.setValorInicial(estatRepository.findOne(estatId));
                                        lobj.setValorInicial(estatId);
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
                            log.error("ERROR: Error al obtenir el tipus de informació de l'expedient dels logs", e);
                        }
                    }
                } else {
                    currentMessageLogId = plog.getId();
                }
            } else if (plog instanceof TaskLog) {
                TaskInstance taskInstance = ((TaskLog)plog).getTaskInstance();
                Long objId = new Long(taskInstance.getId());
                LogObjectDto lobj = LogObjectDtos.get(objId);
                if (lobj == null) {
                    lobj = new LogObjectDto(
                            objId.longValue(),
                            plog.getId(),
                            taskInstance.getName(),
                            LogObjectDto.LOG_OBJECT_TASK,
                            plog.getToken().getProcessInstance().getId(),
                            plog.getToken().getId());
                    LogObjectDtos.put(objId, lobj);
                }
                if (plog instanceof TaskCreateLog)
                    lobj.addAccio(LogObjectDto.LOG_ACTION_START);
                if (plog instanceof TaskEndLog)
                    lobj.addAccio(LogObjectDto.LOG_ACTION_END);
                if (plog instanceof TaskAssignLog) {
                    lobj.addAccio(LogObjectDto.LOG_ACTION_ASSIGN);
                    lobj.setValorInicial(((TaskAssignLog)plog).getTaskOldActorId());
                    ExpedientLogDto expedientLog = getExpedientLogPerJbpmLogId(expedientLogs, currentMessageLogId);
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

                    // TODO: Consultar si la variable és ignorada
                    // Cerca informació si la variable jbpm correspon a un document o una camp ignorat
                    String codi = variableInstance.getName();

                    CampTipusIgnored campTipusIgnored = workflowBridgeService.getCampAndIgnored(
                            new Long(variableInstance.getProcessInstance().getProcessDefinition().getId()).toString(),
                            variableInstance.getProcessInstance().getExpedient().getId(),
                            codi);

                    ignored = campTipusIgnored.getIgnored();

//                    DefinicioProces pDef = definicioProcesRepository.findByJbpmId(new Long(variableInstance.getProcessInstance().getProcessDefinition().getId()).toString());
//                    Expedient expedient = expedientRepository.findOne(
//                            variableInstance.getProcessInstance().getExpedient().getId());
//                    ExpedientTipus expedientTipus = expedient != null ? expedient.getTipus() : null;
//                    Camp camp = null;
//                    if(codi.startsWith(JbpmVars.PREFIX_DOCUMENT)) {
//                        // Document
//                        codi = codi.substring((JbpmVars.PREFIX_DOCUMENT).length());
//                        // Cerca el document per veure si està marcat per ignorar
//                        Document document = null;
//                        if (expedientTipus != null && expedientTipus.isAmbInfoPropia()) {
//                            document = documentRepository.findByExpedientTipusAndCodi(
//                                    expedientTipus.getId(),
//                                    codi,
//                                    expedientTipus.getExpedientTipusPare() != null);
//                        } else {
//                            document = documentRepository.findByDefinicioProcesAndCodi(
//                                    pDef,
//                                    codi);
//                        }
//                        if(document != null){
//                            ignored = document.isIgnored();
//                        }
//                    } else {
//                        // Variable
//                        if (expedientTipus != null && expedientTipus.isAmbInfoPropia()) {
//                            camp = campRepository.findByExpedientTipusAndCodi(
//                                    expedientTipus.getId(),
//                                    codi,
//                                    true);
//                        } else {
//                            camp = campRepository.findByDefinicioProcesAndCodi(
//                                    pDef,
//                                    codi);
//                        }
//                        if(camp != null){
//                            ignored = camp.isIgnored();
//                        }
//                    }
                    // TODO: Fi
                    if (!ignored) {
                        if (variableInstance.getName() != null || variableInstance.getValue() != null) {
                            Long variableInstanceId = getVariableIdFromVariableLog(plog.getId());
                            Long taskInstanceId = getTaskIdFromVariableLog(plog.getId());
                            Long objId = new Long(variableInstanceId);
                            LogObjectDto lobj = LogObjectDtos.get(objId);
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
                                LogObjectDtos.put(objId, lobj);
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
                                        log.error("Error obtenint el valor del ByteArray de la variable " + vulog.getVariableInstance().getName(), e);
                                    }
//                                } else if (oldValue instanceof String && camp != null && camp.getTipus() == TipusCamp.BOOLEAN) {
                                } else if (oldValue instanceof String && CampTipusDto.BOOLEAN.equals(campTipusIgnored.getTipus())) {
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
                LogObjectDto lobj = LogObjectDtos.get(objId);
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
                    LogObjectDtos.put(objId, lobj);
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
                LogObjectDto lobj = LogObjectDtos.get(objId);
                if (lobj == null) {
                    lobj = new LogObjectDto(
                            objId.longValue(),
                            plog.getId(),
                            objId.toString(),
                            LogObjectDto.LOG_OBJECT_PROCES,
                            plog.getToken().getProcessInstance().getId(),
                            plog.getToken().getId());
                    LogObjectDtos.put(objId, lobj);
                }
                if (plog instanceof ProcessInstanceCreateLog)
                    lobj.addAccio(LogObjectDto.LOG_ACTION_START);
                if (plog instanceof ProcessInstanceEndLog)
                    lobj.addAccio(LogObjectDto.LOG_ACTION_END);
            } else if (plog instanceof ActionLog) {
                Long objId = ((ActionLog)plog).getAction().getId();
                LogObjectDto lobj = LogObjectDtos.get(objId);
                if (lobj == null) {
                    lobj = new LogObjectDto(
                            objId.longValue(),
                            plog.getId(),
                            ((ActionLog)plog).getAction().getName(),
                            LogObjectDto.LOG_OBJECT_ACTION,
                            plog.getToken().getProcessInstance().getId(),
                            plog.getToken().getId());
                    LogObjectDtos.put(objId, lobj);
                }
                lobj.addAccio(LogObjectDto.LOG_ACTION_EXEC);
            }
            //}
        }
        List<LogObjectDto> logsOrdenats = new ArrayList<LogObjectDto>(LogObjectDtos.values());
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

    private ExpedientLogDto getExpedientLogPerJbpmLogId(
            List<ExpedientLogDto> expedientLogs,
            long jbpmLogId) {
        for (ExpedientLogDto elog: expedientLogs) {
            if (elog.getJbpmLogId() != null && elog.getJbpmLogId().longValue() == jbpmLogId)
                return elog;
        }
        return null;
    }

    private void consultarParametresRetroaccio(
            Map<String, String> paramsAccio,
            Collection<LogObjectDto> logObjectDtos) {

        Set<String> processInstancesConsultats = new HashSet<String>();
        Map<String, Object> variables;
        String processInstanceId;
        String actionNodeId;
        Action action;
        for (LogObjectDto logo: logObjectDtos) {
            if (logo.getTipus() == LogObjectDto.LOG_OBJECT_ACTION) {
                processInstanceId = new Long(logo.getProcessInstanceId()).toString();
                if (!processInstancesConsultats.contains(processInstanceId)) {
                    processInstancesConsultats.add(processInstanceId);
                    variables = workflowEngineApi.getProcessInstanceVariables(new Long(logo.getProcessInstanceId()).toString());
                    if (variables != null) {
                        for (String variableName : variables.keySet())
                            if (variableName.startsWith(BasicActionHandler.PARAMS_RETROCEDIR_VARIABLE_PREFIX)) {
                                // Recupera la informació del node
                                actionNodeId = variableName.substring(BasicActionHandler.PARAMS_RETROCEDIR_VARIABLE_PREFIX.length(), variableName.length());
                                action = NumberUtils.isNumber(actionNodeId) ? getActionById(Long.valueOf(actionNodeId)) : null;
                                if (action != null) {
                                    paramsAccio.put(logo.getProcessInstanceId() + "_" + action.getName(), String.valueOf(variables.get(variableName)));
                                }
                            }
                    }
                }
            }
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
            log.error("Error al obtenir el recurs " + recurs, ex);
            return null;
        }
    }

    private ProcessLog getProcessLogById(Long logId) {
        GetProcessLogByIdCommand command = new GetProcessLogByIdCommand(logId.longValue());
        ProcessLog log = (ProcessLog)commandService.execute(command);
        return log;
    }


    private void revertProcessInstanceEnd(Long id) {
        RevertProcessInstanceEndCommand command = new RevertProcessInstanceEndCommand(id);
        commandHelper.executeCommandWithAutoSave(
                command,
                id,
                AddToAutoSaveCommand.TIPUS_INSTANCIA_PROCES);
    }

    private void revertTokenEnd(Long id) {
        GetTokenByIdCommand tokenCommand = new GetTokenByIdCommand(id);
        JbpmToken jtoken = new JbpmToken((Token)commandService.execute(tokenCommand));
        RevertTokenEndCommand command = new RevertTokenEndCommand(jtoken);
        commandHelper.executeCommandWithAutoSave(
                command,
                id,
                AddToAutoSaveCommand.TIPUS_TOKEN);
        jtoken.getToken().setAbleToReactivateParent(true);
    }

    private Boolean isJoinNode(Long processInstanceId, String nodeName) {
        GetProcessInstanceCommand command = new GetProcessInstanceCommand(processInstanceId);
        ProcessInstance pi = (ProcessInstance)commandService.execute(command);
        NodeType nodeType = null;
        try {
            nodeType = pi.getProcessDefinition().getNode(nodeName).getNodeType();
        } catch (Exception ex) {
            return false;
        } finally {
            //adminService.mesuraCalcular("jBPM isJoinNode", "jbpmDao");
        }

        return nodeType == NodeType.Join;
    }

    private Node getNodeByName(Long processInstanceId, String nodeName) {
        GetProcessInstanceCommand command = new GetProcessInstanceCommand(processInstanceId);
        ProcessInstance pi = (ProcessInstance)commandService.execute(command);
        Node node = pi.getProcessDefinition().getNode(nodeName);
        return node;
    }

    private Boolean isProcessStateNodeJoinOrFork(Long processInstanceId, String nodeName) {
        GetProcessInstanceCommand command = new GetProcessInstanceCommand(processInstanceId);
        ProcessInstance pi = (ProcessInstance)commandService.execute(command);
        Node node = pi.getProcessDefinition().getNode(nodeName);
        String nodeClassName = node.toString();
        NodeType nodeType = node.getNodeType();
        return (nodeClassName.startsWith("ProcessState") || nodeType == NodeType.Fork || nodeType == NodeType.Join);
    }

    private Boolean hasStartBetweenLogs(Long begin, Long end, Long taskInstanceId) {
        HasStartBetweenLogsCommand command = new HasStartBetweenLogsCommand(begin, end, taskInstanceId);
        Boolean hasStart = (Boolean)commandService.execute(command);
        return hasStart.booleanValue();
    }

    private WTaskInstance findEquivalentTaskInstance(Long tokenId, Long taskInstanceId) {
        GetTaskInstanceCommand commandGetTask = new GetTaskInstanceCommand(taskInstanceId);
        TaskInstance ti = (TaskInstance)commandService.execute(commandGetTask);
        FindTaskInstanceForTokenAndTaskCommand command = new FindTaskInstanceForTokenAndTaskCommand(tokenId, ti.getTask().getName());
        WTaskInstance resultat = new JbpmTask((TaskInstance)commandService.execute(command));
        if (resultat.getTaskInstance() == null)
            ((JbpmTask)resultat).setTaskInstance(ti);
        return resultat;
    }

    private Long getVariableIdFromVariableLog(Long variableLogId) {
        GetVariableIdFromVariableLogCommand command = new GetVariableIdFromVariableLogCommand(variableLogId);
        Long resultat = (Long)commandService.execute(command);
        return resultat;
    }

    private Long getTaskIdFromVariableLog(Long variableLogId) {
        GetTaskIdFromVariableLogCommand command = new GetTaskIdFromVariableLogCommand(variableLogId);
        Long resultat = (Long)commandService.execute(command);
        return resultat;
    }

    private Map<Token, List<ProcessLog>> getProcessInstanceLogs(String processInstanceId) {
        final long id = Long.parseLong(processInstanceId);
        FindProcessInstanceLogsCommand command = new FindProcessInstanceLogsCommand(id);
        Map resultat = (Map)commandService.execute(command);
        return resultat;
    }

    private Action getActionById(Long nodeId) {
        GetActionByIdCommand command = new GetActionByIdCommand(nodeId);
        Action action = (Action)commandService.execute(command);
        return action;
    }

    private void cancelProcessInstance(Long id) {
        CancelProcessInstanceCommand command = new CancelProcessInstanceCommand(id);
        commandHelper.executeCommandWithAutoSave(
                command,
                id,
                AddToAutoSaveCommand.TIPUS_INSTANCIA_PROCES);
    }

    private void cancelToken(Long id) {
        CancelTokenCommand command = new CancelTokenCommand(id);
        commandHelper.executeCommandWithAutoSave(
                command,
                id,
                AddToAutoSaveCommand.TIPUS_TOKEN);
    }

    private String getDocumentCodiPerVariableJbpm(String var) {
        if (var.startsWith(PREFIX_DOCUMENT)) {
            return var.substring(PREFIX_DOCUMENT.length());
        } else if (var.startsWith(PREFIX_ADJUNT)) {
            return var.substring(PREFIX_ADJUNT.length());
        } else if (var.startsWith(PREFIX_SIGNATURA)) {
            return var.substring(PREFIX_SIGNATURA.length());
        } else {
            return var;
        }
    }

}
