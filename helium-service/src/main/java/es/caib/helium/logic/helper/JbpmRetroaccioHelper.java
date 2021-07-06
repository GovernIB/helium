package es.caib.helium.logic.helper;

import org.springframework.stereotype.Component;

import es.caib.helium.logic.intf.WTaskInstance;

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

//    public ProcessLog getProcessLogById(Long logId) {
//
//        return null;
//    }

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

//    public Node getNodeByName(Long processInstanceId, String nodeName) {
//
//        return null;
//    }

    public Boolean isProcessStateNodeJoinOrFork(Long processInstanceId, String nodeName) {

        return null;
    }

    public Boolean hasStartBetweenLogs(Long begin, Long end, Long taskInstanceId) {

        return null;
    }

    public WTaskInstance findEquivalentTaskInstance(Long tokenId, Long taskInstanceId) {

        return null;
    }

//    public Action getActionById(Long nodeId) {
//
//        return null;
//    }

    public Long getVariableIdFromVariableLog(Long variableLogId) {

        return null;
    }

    public Long getTaskIdFromVariableLog(Long variableLogId) {

        return null;
    }

//    public Map<Token, List<ProcessLog>> getProcessInstanceLogs(String processInstanceId) {
//
//        return null;
//    }
}
