package es.caib.helium.camunda.service;

import es.caib.helium.camunda.model.WToken;

import java.util.Map;

public class ExecutionServiceImpl implements ExecutionService {

    @Override
    public WToken getTokenById(String tokenId) {
        return null;
    }

    @Override
    public Map<String, WToken> getActiveTokens(String processInstanceId) {
        return null;
    }

    @Override
    public Map<String, WToken> getAllTokens(String processInstanceId) {
        return null;
    }

    @Override
    public void tokenRedirect(long tokenId, String nodeName, boolean cancelTasks, boolean enterNodeIfTask, boolean executeNode) {

    }

    @Override
    public boolean tokenActivar(long tokenId, boolean activar) {
        return false;
    }

    @Override
    public void signalToken(long tokenId, String transitionName) {

    }
}
