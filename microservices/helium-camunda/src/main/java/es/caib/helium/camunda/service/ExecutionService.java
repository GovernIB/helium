package es.caib.helium.camunda.service;

import es.caib.helium.camunda.model.WToken;

import java.util.Map;

public interface ExecutionService {

    /** Obté la informació del token per identificador.
     *
     * @param tokenId
     * @return
     */
    public WToken getTokenById(String tokenId);

    /** Consulta la llista de tokens actius per una instància de procés.
     *
     * @param processInstanceId
     * @return
     */
    public Map<String, WToken> getActiveTokens(String processInstanceId);

    /** Retorna una llista de tots els tokens d'una instància de procés.
     *
     * @param processInstanceId
     * @return
     */
    public Map<String, WToken> getAllTokens(String processInstanceId);

    /** Mètode per redirigir la execució cap a un altre token
     *
     * @param tokenId
     * @param nodeName
     * @param cancelTasks
     * @param enterNodeIfTask
     * @param executeNode
     */
    public void tokenRedirect(
            String tokenId,
            String nodeName,
            boolean cancelTasks,
            boolean enterNodeIfTask,
            boolean executeNode);

    /** Mètode per activar o desactivar un token.
     *
     * @param tokenId
     * @param activar
     * @return
     */
    public boolean tokenActivar(String tokenId, boolean activar);

    /** Mètode per enviar un senyal a un token per a que avanci per una transició.
     *
     * @param tokenId
     * @param signalName
     */
    public void signalToken(String tokenId, String signalName);

}
