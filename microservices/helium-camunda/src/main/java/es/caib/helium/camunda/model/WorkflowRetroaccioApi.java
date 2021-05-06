package es.caib.helium.camunda.model;

/**
 * @author sion
 *
 */
public interface WorkflowRetroaccioApi {
	
//	public enum ExpedientRetroaccioTipus {
//		PROCES_VARIABLE_CREAR,			// 0
//		PROCES_VARIABLE_MODIFICAR,
//		PROCES_VARIABLE_ESBORRAR,
//		PROCES_DOCUMENT_AFEGIR,
//		PROCES_DOCUMENT_MODIFICAR,
//		PROCES_DOCUMENT_ESBORRAR,		// 5
//		PROCES_DOCUMENT_ADJUNTAR,
//		PROCES_SCRIPT_EXECUTAR,
//		PROCES_ACTUALITZAR,
//		TASCA_REASSIGNAR,
//		TASCA_FORM_GUARDAR,				// 10
//		TASCA_FORM_VALIDAR,
//		TASCA_FORM_RESTAURAR,
//		TASCA_ACCIO_EXECUTAR,
//		TASCA_DOCUMENT_AFEGIR,
//		TASCA_DOCUMENT_MODIFICAR,		// 15
//		TASCA_DOCUMENT_ESBORRAR,
//		TASCA_DOCUMENT_SIGNAR,
//		TASCA_COMPLETAR,
//		TASCA_SUSPENDRE,
//		TASCA_CONTINUAR,				// 20
//		TASCA_CANCELAR,
//		TASCA_MARCAR_FINALITZAR,
//		EXPEDIENT_INICIAR,
//		EXPEDIENT_MODIFICAR,
//		EXPEDIENT_ATURAR,
//		EXPEDIENT_REPRENDRE,			// 25
//		EXPEDIENT_RELACIO_AFEGIR,
//		EXPEDIENT_RELACIO_ESBORRAR,
//		EXPEDIENT_ACCIO,
//		EXPEDIENT_RETROCEDIR,
//		PROCES_DOCUMENT_SIGNAR,			// 30
//		EXPEDIENT_RETROCEDIR_TASQUES,
//		PROCES_LLAMAR_SUBPROCES,
//		EXPEDIENT_FINALITZAR,
//		EXPEDIENT_DESFINALITZAR}
//
//	public enum ExpedientRetroaccioEstat {
//		NORMAL,
//		RETROCEDIT,
//		IGNORAR,
//		BLOCAR,
//		RETROCEDIT_TASQUES}
//
//	public enum RetroaccioInfo {
//		NUMERO,
//		TITOL,
//		RESPONSABLE,
//		INICI,
//		COMENTARI,
//		ESTAT,
//		GEOPOSICIOX,
//		GEOPOSICIOY,
//		GEOREFERENCIA,
//		GRUP}
//
//	/**
//	 * Consulta els logs d'un expedient ordenats per data.
//	 *
//	 * @param expedientId
//	 *            Atribut id de l'expedient.
//	 * @param detall
//	 *            Indica si s'ha de retornar la informació detallada o no.
//	 * @return els logs de l'expedient organitzats per instància de procés.
//	 * @throws NoTrobatException
//	 *             Si no s'ha trobat cap expedient amb l'id especificat.
//	 * @throws PermisDenegatException
//	 *             Si no es tenen els permisos adequats.
//	 */
//	public SortedSet<Entry<InstanciaProcesDto, List<InformacioRetroaccioDto>>> findInformacioRetroaccioExpedientOrdenatPerData(
//			Long expedientId,
//			String instanciaProcesId,
//			boolean detall);
//
//	/**
//	 * Obté les tasques associades als logs de l'expedient.
//	 *
//	 * @param expedientId
//	 *            Atribut id de l'expedient.
//	 * @return el llistat de tasques.
//	 * @throws NoTrobatException
//	 *             Si no s'ha trobat cap expedient amb l'id especificat.
//	 * @throws PermisDenegatException
//	 *             Si no es tenen els permisos adequats.
//	 */
//	public Map<String, ExpedientTascaDto> findTasquesExpedientPerRetroaccio(Long expedientId);
//
//	/**
//	 * Fa un retrocés de l'expedient de totes les modificacions fetes a partir
//	 * del log especificat.
//	 *
//	 * @param expedientId
//	 *            Atribut id de l'expedient.
//	 * @param historicId
//	 *            Atribut id de la informació de retroaccio d'expedient.
//	 * @param retrocedirPerTasques
//	 *            Indica si el retrocés es per tasques.
//	 */
//	public void executaRetroaccio(
////			Long expedientId,
//			Long informacioRetroaccioId,
//			boolean retrocedirPerTasques);
//
//	/**
//	 * Elimina tots els logs associats a un expedient.
//	 *
//	 * @param expedientId
//	 *            Atribut id de l'expedient.
//	 * @throws NoTrobatException
//	 *             Si no s'ha trobat cap expedient amb l'id especificat.
//	 */
//	public void eliminaInformacioRetroaccio(String processInstanceId);
//
//	/**
//	 * Retorna els logs associats a una tasca de l'expedient.
//	 *
//	 * @param expedientId
//	 *            Atribut id de l'expedient.
//	 * @param logId
//	 *            Atribut id del log d'expedient.
//	 * @return la llista de logs.
//	 * @throws NoTrobatException
//	 *             Si no s'ha trobat cap expedient amb l'id especificat.
//	 */
//	public List<InformacioRetroaccioDto> findInformacioRetroaccioTascaOrdenatPerData(
//			Long informacioRetroaccioId);
//
//	/**
//	 * Obté els logs associats a una acció de retrocés ordenats per data.
//	 *
//	 * @param expedientId
//	 *            Atribut id de l'expedient.
//	 * @param logId
//	 *            Atribut id del log d'expedient.
//	 * @return la llista de logs.
//	 */
//	public List<InformacioRetroaccioDto> findInformacioRetroaccioAccioRetrocesOrdenatsPerData(
//			Long informacioRetroaccioId);
//
//	/**
//	 * Retorna la informació d'un registre de log de l'expedient.
//	 *
//	 * @param expedientId
//	 *            Atribut id de l'expedient.
//	 * @param logId
//	 *            Atribut id del log d'expedient.
//	 * @return la informació del log.
//	 * @throws NoTrobatException
//	 *             Si no s'ha trobat cap expedient amb l'id especificat.
//	 * @throws PermisDenegatException
//	 *             Si no es tenen els permisos adequats.
//	 */
//	public InformacioRetroaccioDto findInformacioRetroaccioById(
//			Long informacioRetroaccioId) throws NoTrobatException, PermisDenegatException;
//
////	public void afegirInformacioRetroaccio();
//
//	public Long afegirInformacioRetroaccioPerExpedient(
//			boolean ambRetroaccio,
//			String processInstanceId,
//			String message);
//
//	public Long afegirInformacioRetroaccioPerExpedient(
//			Long expedientId,
//			ExpedientRetroaccioTipus tipus,
//			String accioParams);
//
//	public Long afegirInformacioRetroaccioPerExpedient(
//			Long expedientId,
//			ExpedientRetroaccioTipus tipus,
//			String accioParams,
//			ExpedientRetroaccioEstat estat);
//
//	public Long afegirInformacioRetroaccioPerTasca(
//			String taskInstanceId,
//			ExpedientRetroaccioTipus tipus,
//			String accioParams);
//
//	public Long afegirInformacioRetroaccioPerTasca(
//			String taskInstanceId,
//			ExpedientRetroaccioTipus tipus,
//			String accioParams,
//			String user);
//
//	public Long afegirInformacioRetroaccioPerTasca(
//			String taskInstanceId,
//			Long expedientId,
//			ExpedientRetroaccioTipus tipus,
//			String accioParams,
//			String user);
//
//	public Long afegirInformacioRetroaccioPerProces(
//			String processInstanceId,
//			ExpedientRetroaccioTipus tipus,
//			String accioParams);
//
//	public Long afegirInformacioRetroaccioPerProces(
//			String processInstanceId,
//			ExpedientRetroaccioTipus tipus,
//			String accioParams,
//			ExpedientRetroaccioEstat estat);
//
//	public void actualitzaEstatInformacioRetroaccio(
//			Long informacioRetroaccioId,
//			ExpedientRetroaccioEstat estat);
//
//	public void actualitzaParametresAccioInformacioRetroaccio(
//			Long informacioRetroaccioId,
//			String parametresAccio);

/*
	// Retroacció
	public Map<Token, List<ProcessLog>> getProcessInstanceLogs(String processInstanceId);
	public long addProcessInstanceMessageLog(String processInstanceId, String message);
	public long addTaskInstanceMessageLog(String taskInstanceId, String message);
	public Long getVariableIdFromVariableLog(long variableLogId);
	public Long getTaskIdFromVariableLog(long variableLogId);
	public void cancelProcessInstance(long id);
	public void revertProcessInstanceEnd(long id);
	public void cancelToken(long id);
	public void revertTokenEnd(long id);
	public WTaskInstance findEquivalentTaskInstance(long tokenId, long taskInstanceId);
	public boolean isProcessStateNodeJoinOrFork(long processInstanceId, String nodeName);
	public boolean isJoinNode(long processInstanceId, String nodeName);
	public ProcessLog getProcessLogById(Long id);
	public Node getNodeByName(long processInstanceId, String nodeName);
	public boolean hasStartBetweenLogs(long begin, long end, long taskInstanceId);
	public void deleteProcessInstanceTreeLogs(String rootProcessInstanceId);
	public void setTaskInstanceActorId(String taskInstanceId, String actorId);
	public void setTaskInstancePooledActors(String taskInstanceId, String[] pooledActors);
 */
}
