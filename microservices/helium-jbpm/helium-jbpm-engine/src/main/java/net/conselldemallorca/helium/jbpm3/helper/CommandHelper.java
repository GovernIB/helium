package net.conselldemallorca.helium.jbpm3.helper;

import net.conselldemallorca.helium.jbpm3.command.AddToAutoSaveCommand;
import net.conselldemallorca.helium.jbpm3.integracio.ExecucioHandlerException;
import net.conselldemallorca.helium.jbpm3.integracio.Jbpm3HeliumBridge;
import org.jbpm.JbpmException;
import org.jbpm.command.Command;
import org.jbpm.command.CommandService;
import org.jbpm.command.GetProcessInstanceCommand;
import org.jbpm.graph.def.DelegationException;
import org.jbpm.graph.exe.ProcessInstance;
import org.jbpm.graph.exe.ProcessInstanceExpedient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CommandHelper {

    @Autowired
    private CommandService commandService;


    public Object executeCommandWithAutoSave(
            Command command,
            long id,
            int autoSaveTipus) throws RuntimeException {
        try {
            AddToAutoSaveCommand autoSaveCommand = new AddToAutoSaveCommand(
                    command,
                    id,
                    autoSaveTipus);
            return commandService.execute(autoSaveCommand);
        } catch (JbpmException ex) {
            //Inserim l'error a la tasca si aquesta s'ha executat en segon pla
            Jbpm3HeliumBridge.getInstanceService().setErrorTascaSegonPla(id, ex);
            //
            throw tractarExceptionJbpm(
                    ex,
                    id,
                    autoSaveTipus);
        }
    }

    public Object executeCommandWithAutoSave(
            Command command,
            long[] ids,
            int autoSaveTipus) throws RuntimeException {
        try {
            AddToAutoSaveCommand autoSaveCommand = new AddToAutoSaveCommand(
                    command,
                    ids,
                    autoSaveTipus);
            return commandService.execute(autoSaveCommand);
        } catch (JbpmException ex) {
            throw tractarExceptionJbpm(
                    ex,
                    (ids!= null && ids.length > 0) ? ids[0] : null,
                    autoSaveTipus);
        }
    }

    private RuntimeException tractarExceptionJbpm(
            JbpmException ex,
            long id,
            int autoSaveTipus) {
        Long taskInstanceId = null;
        Long tokenId = null;
        GetProcessInstanceCommand command = new GetProcessInstanceCommand();
        switch (autoSaveTipus) {
            case AddToAutoSaveCommand.TIPUS_INSTANCIA_PROCES:
                command.setProcessInstanceId(id);
                break;
            case AddToAutoSaveCommand.TIPUS_INSTANCIA_TASCA:
                taskInstanceId = new Long(id);
                command.setTaskInstanceId(id);
                break;
            case AddToAutoSaveCommand.TIPUS_TOKEN:
                tokenId = new Long(id);
                command.setTokenId(id);
                break;
        }
        ProcessInstance processInstance = (ProcessInstance)commandService.execute(command);
        Long processInstanceId = new Long(processInstance.getId());
        ProcessInstanceExpedient expedient = processInstance.getExpedient();
        if (ex.getCause() != null && ex.getCause() instanceof DelegationException && ex.getCause().getCause() != null) {
            for (StackTraceElement element: ex.getCause().getCause().getStackTrace()) {
                if (element.getMethodName().equals("execute")) {
                    return new ExecucioHandlerException(
                            (expedient != null) ? expedient.getId() : null,
                            (expedient != null) ? expedient.getTipus().getId() : null,
                            processInstanceId,
                            taskInstanceId,
                            tokenId,
                            element.getClassName(),
                            element.getMethodName(),
                            element.getFileName(),
                            element.getLineNumber(),
                            ex.getCause().getCause().getMessage(),
                            ex.getCause().getCause());
                }
            }
        }
        return ex;
    }
}
