package net.conselldemallorca.helium.core.helper;

import net.conselldemallorca.helium.core.api.WTaskInstance;
import org.jbpm.graph.def.Action;
import org.jbpm.graph.def.Node;
import org.jbpm.graph.exe.Token;
import org.jbpm.logging.log.ProcessLog;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class JbpmRetroaccioHelper {

    public void deleteProcessInstanceTreeLogs(String processInstanceId) {

    }

    public Long addProcessInstanceMessageLog(
            String processInstanceId,
            String message) {

        return null;
    }

    public Long addTaskInstanceMessageLog(
            String taskInstanceId,
            String message) {

        return null;
    }

    public ProcessLog getProcessLogById(Long logId) {

        return null;
    }

    public void cancelProcessInstance(Long id) {

    }

    public void revertProcessInstanceEnd(Long id) {

    }

    public void revertTokenEnd(Long id) {

    }

    public void cancelToken(Long id) {

    }

    public Boolean isJoinNode(Long processInstanceId, String nodeName) {

        return null;
    }

    public Node getNodeByName(Long processInstanceId, String nodeName) {

        return null;
    }

    public Boolean isProcessStateNodeJoinOrFork(Long processInstanceId, String nodeName) {

        return null;
    }

    public Boolean hasStartBetweenLogs(Long begin, Long end, Long taskInstanceId) {

        return null;
    }

    public WTaskInstance findEquivalentTaskInstance(Long tokenId, Long taskInstanceId) {

        return null;
    }

    public Action getActionById(Long nodeId) {

        return null;
    }

    public Long getVariableIdFromVariableLog(Long variableLogId) {

        return null;
    }

    public Long getTaskIdFromVariableLog(Long variableLogId) {

        return null;
    }

    public Map<Token, List<ProcessLog>> getProcessInstanceLogs(String processInstanceId) {

        return null;
    }
}
