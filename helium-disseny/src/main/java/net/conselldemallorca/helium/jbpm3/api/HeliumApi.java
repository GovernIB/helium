package net.conselldemallorca.helium.jbpm3.api;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.jbpm.JbpmException;

import net.conselldemallorca.helium.jbpm3.handlers.exception.HeliumHandlerException;
import net.conselldemallorca.helium.jbpm3.handlers.tipus.ActionInfo;
import net.conselldemallorca.helium.jbpm3.handlers.tipus.DadesNotificacio;
import net.conselldemallorca.helium.jbpm3.handlers.tipus.DadesRegistreEntrada;
import net.conselldemallorca.helium.jbpm3.handlers.tipus.DadesRegistreNotificacio;
import net.conselldemallorca.helium.jbpm3.handlers.tipus.DadesRegistreSortida;
import net.conselldemallorca.helium.jbpm3.handlers.tipus.DocumentInfo;
import net.conselldemallorca.helium.jbpm3.handlers.tipus.EventInfo;
import net.conselldemallorca.helium.jbpm3.handlers.tipus.ExpedientInfo;
import net.conselldemallorca.helium.jbpm3.handlers.tipus.FilaResultat;
import net.conselldemallorca.helium.jbpm3.handlers.tipus.Interessat;
import net.conselldemallorca.helium.jbpm3.handlers.tipus.JustificantRecepcioInfo;
import net.conselldemallorca.helium.jbpm3.handlers.tipus.NodeInfo;
import net.conselldemallorca.helium.jbpm3.handlers.tipus.ParellaCodiValor;
import net.conselldemallorca.helium.jbpm3.handlers.tipus.ProcessDefinitionInfo;
import net.conselldemallorca.helium.jbpm3.handlers.tipus.ProcessInstanceInfo;
import net.conselldemallorca.helium.jbpm3.handlers.tipus.RespostaEnviar;
import net.conselldemallorca.helium.jbpm3.handlers.tipus.RespostaRegistre;
import net.conselldemallorca.helium.jbpm3.handlers.tipus.TaskInfo;
import net.conselldemallorca.helium.jbpm3.handlers.tipus.TaskInstanceInfo;
import net.conselldemallorca.helium.jbpm3.handlers.tipus.TimerInfo;
import net.conselldemallorca.helium.jbpm3.handlers.tipus.TokenInfo;
import net.conselldemallorca.helium.jbpm3.handlers.tipus.TransitionInfo;

public interface HeliumApi {

	// Funcions de l'ExecutionContext
	/**
	 * @return Informació del token on s'executa la accio
	 */
	public TokenInfo getToken();
	/**
	 * @return Informació del node on s'executa la accio
	 */
	public NodeInfo getNode();
	/**
	 * @return Informació de la definicio de proces on s'executa la accio
	 */
	public ProcessDefinitionInfo getProcessDefinition();
	/**
	 * @return Informació de la instancia de proces on s'executa la accio
	 */
	public ProcessInstanceInfo getProcessInstance();
	/**
	 * @return Informació de la accio que s'executa
	 */
	public ActionInfo getAction();
	/**
	 * @return Informació de l'event on s'executa la accio
	 */
	public EventInfo getEvent();
	/**
	 * @return Informació de la transicio on s'executa la accio
	 */
	public TransitionInfo getTransition();
	/**
	 * @return Informació de la tasca on s'executa la accio
	 */
	public TaskInfo getTask();
	/**
	 * @return Informació de la instancia de tasca on s'executa la accio
	 */
	public TaskInstanceInfo getTaskInstance();
	/**
	 * @return Informació de la instancia de tasca del subproces on s'executa la accio
	 */
	public ProcessInstanceInfo getSubProcessInstance();
	/**
	 * @return Informació del timer on s'executa la accio
	 */
	public TimerInfo getTimer();
	
//	public ModuleDefinitionInfo getDefinition(Class clazz);
//	public ModuleInstanceInfo getInstance(Class clazz);
//	public ContextInstanceInfo getContextInstance();
//	public TaskMgmtInstanceInfo getTaskMgmtInstance();
//	public JbpmContextInfo getJbpmContext();
//	public void setAction(Action action);
//	public void setEvent(Event event);
//	public void setTransition(Transition transition);
//	public Node getTransitionSource();
//	public void setTransitionSource(Node transitionSource);
//	public GraphElement getEventSource();
//	public void setEventSource(GraphElement eventSource);
//	public void setTask(Task task);
//	public void setTaskInstance(TaskInstance taskInstance);
//	public void setSubProcessInstance(ProcessInstance subProcessInstance);
//	public void setTimer(Timer timer);
	
//	public void leaveNode();
//	public void leaveNode(String transitionName);
//	public void leaveNode(Transition transition);
	
	
	
	// Funciona del BasicActionHandler
//	public DocumentDisseny getDocumentDisseny(String codiDocument);
//	public void crearReferenciaDocumentInstanciaProcesPare(String varDocument);
//	public Tramit consultaTramit(
//			String numero,
//			String clau);
//
//	public byte[] obtenirArxiuGestorDocumental(String id);
//	public void documentGuardar(
//			String documentCodi,
//			Date data,
//			String arxiuNom,
//			byte[] arxiuContingut);
//	public void adjuntGuardar(
//            String nomDocument,
//            Date data,
//            String arxiuNom,
//            byte[] arxiuContingut);
//	
//	public Object getVariableGlobal(String varCodi);
//	public void setVariableGlobal(
//			String varCodi,
//			Object varValor);
//	public Object getVariableGlobalValor(String varCodi);
	
	/**
	 * Permet generar missatges que es mostraran en temps real, quan la acció s'executa en la 
	 * finalització d'una tasca en segon pla.
	 * 
	 * @param missatge Missatge a mostrar a l'usuari en l'execució d'una acció d'una tasca que s'executa en segon pla.
	 * @throws Exception en cas de no poder obtenir el token o la tasca on s'està executant la acció
	 */
	public void desarInformacioExecucio(String missatge) throws Exception;
	
	// Funcions Anàlisi
	/**
	 * Obté el valor d'una variable de jBPM.
	 * El context a on es consultarà la variable dependrà del lloc a on s'executi el handler 
	 * (Si s'executa a dins una instància de tasca s'obtendrà el valor de la variable de la instància de tasca).
	 * 
	 * @param varCodi Codi de la variable a llegir
	 * @return El valor de la variable. Null en cas que no existeixi la variable.
	 */
	public Object getVariable(String varCodi);
	/**
	 * Obté el text que es mostra a Helium d'una variable de jBPM. Depenent del tipus de variable, 
	 * el valor es formata o es consulta abans de mostrar-lo a l'usuari. 
	 * El context a on es consultarà la variable dependrà del lloc a on s'executi el handler 
	 * (Si s'executa a dins una instància de tasca s'obtendrà el valor de la variable de la instància de tasca).
	 * 
	 * @param varCodi Codi de la variable a llegir
	 * @return El valor de la variable formatat per mostrar a l'usuari. Null en cas que no existeixi la variable.
	 */
	public String getVariableText(String varCodi);
	
	/**
	 * Obté el valor d'una variable de jBPM de la instància de tasca.
	 * 
	 * @param varCodi Codi de la variable a llegir
	 * @return El valor de la variable. Null en cas que no existeixi la variable.
	 * @throws HeliumHandlerException Si l'execució d'aquest mètode es realitza a dins un handler no associat 
	 * a una instància de tasca es produirà una excepció
	 */
	public Object getVariableInstanciaTasca(String varCodi) throws HeliumHandlerException;
	/**
	 * Obté el text que es mostra a Helium d'una variable de jBPM de la instància de tasca. 
	 * Depenent del tipus de variable, el valor es formateja o es consulta abans de mostrar-lo a l'usuari.
	 * 
	 * @param varCodi Codi de la variable a llegir
	 * @return El valor de la variable formatat per mostrar a l'usuari. Null en cas que no existeixi la variable.
	 * @throws HeliumHandlerException Si l'execució d'aquest mètode es realitza a dins un handler no associat 
	 * a una instància de tasca es produirà una excepció
	 */
	public String getVariableInstanciaTascaText(String varCodi) throws HeliumHandlerException;
	/**
	 * Modifica el valor d'una variable jBPM de la instància de tasca
	 * 
	 * @param varCodi Codi de la variable a modificar
	 * @param varValor Nou valor de la variable
	 * @throws HeliumHandlerException Si l'execució d'aquest mètode es realitza a dins un handler no associat 
	 * a una instància de tasca es produirà una excepció
	 */
	public void setVariableInstanciaTasca(String varCodi, Object varValor) throws HeliumHandlerException;
	
	/**
	 * Obté el valor d'una variable de jBPM de la instància de procés
	 * 
	 * @param varCodi Codi de la variable a llegir
	 * @return El valor de la variable. Null en cas que no existeixi la variable.
	 */
	public Object getVariableInstanciaProces(String varCodi);
	/**
	 * Obté el text que es mostra a Helium d'una variable de jBPM de la instància de procés. 
	 * Depenent del tipus de variable, el valor es formateja o es consulta abans de mostrar-lo a l'usuari.
	 * 
	 * @param varCodi Codi de la variable a llegir
	 * @return El valor de la variable formatat per mostrar a l'usuari. Null en cas que no existeixi la variable.
	 */
	public String getVariableInstanciaProcesText(String varCodi);
	/**
	 * Modifica el valor d'una variable jBPM de la instància de procés
	 * 
	 * @param varCodi Codi de la variable a modificar
	 * @param varValor Nou valor de la variable
	 */
	public void setVariableInstanciaProces(String varCodi, Object varValor);

//	/**
//	 * Deprecated method.  
//	 * Use: {@link #setVariableInstanciaProces(String, Object) setVariableInstanciaProces}or {@link #setVariableInstanciaTasca(String, Object) setVariableInstanciaTasca} methods instead.
//	 * <pre> Ex. {@code setVariableInstanciaTasca(varName, new TerminiInfo(anys, mesos, dies))}</pre>
//	 * @param varName Nom de la variable on desar el termini
//	 * @param anys Anys de duració del termini
//	 * @param mesos Mesos de duració del termini
//	 * @param dies Dies de duració del termini
//	 * @throws HeliumHandlerException en el cas que s'executi la acció fora de l'ambit de tasca
//	 */
//	@Deprecated
//	public void terminiGuardar(
//			String varName,
//			int anys,
//			int mesos,
//			int dies) throws HeliumHandlerException;
	
	/**
	 * Obté la informació de l'expedient a on s'està executant el handler
	 * 
	 * @return Un objecte amb la informació de l'expedient
	 */
	public ExpedientInfo getExpedient();
	
	/**
	 * Llença un error de validació per a mostrar a l'usuari dins la tramitació d'una tasca. 
	 * 
	 * @param error El text de l'error que es vol mostrar a l'usuari
	 * @throws HeliumHandlerException Si l'execució d'aquest mètode es realitza a dins un handler no associat 
	 * a una instància de tasca es produirà una excepció
	 */
	public void errorValidacioInstanciaTasca(String error);
	
	/**
	 * Realitza una consulta de domini i retorna el resultat
	 * 
	 * @param codiDomini Codi del domini a consultar
	 * @param id Id del domini a consultar
	 * @param parametres Paràmetres per a la consulta de domini
	 * @return Una llista amb els resultats retornats per la consulta de domini
	 * @throws HeliumHandlerException Si no es troba el domini amb el codi indicat es produirà una excepció
	 */
	public List<FilaResultat> consultaDomini(
			String codiDomini,
			String id,
			Map<String, Object> parametres) throws HeliumHandlerException;
	
	/**
	 * Realitza una consulta de domini intern i retorna el resultat
	 * 
	 * @param id Id del domini a consultar
	 * @param parametres Paràmetres per a la consulta de domini
	 * @return Una llista amb els resultats retornats per la consulta de domini
	 * @throws HeliumHandlerException Si no es troba el domini amb el codi indicat es produirà una excepció
	 */
	public List<FilaResultat> consultaDominiIntern(
			String id,
			Map<String, Object> parametres) throws HeliumHandlerException;
	
	/**
	 * Realitza una consulta dels valors d'una enumeració
	 * 
	 * @param codiEnumeracio Codi de la enumeracio a consultar
	 * @return Una llista amb els resultats retornats per la consulta de domini
	 * @throws HeliumHandlerException Si no es troba la enumeracio amb el codi indicat es produirà una excepció
	 */
	public List<ParellaCodiValor> consultaEnumeracio(String codiEnumeracio) throws HeliumHandlerException;
	/**
	 * Realitza una consulta d'expedients donat un filtre i retorna el resultat. 
	 * Per motius de rendiment, el nombre d'expedients retornats no podrà excedir de 100
	 * 
	 * @param titol Part del títol de l'expedient
	 * @param numero Número de l'expedient
	 * @param dataInici1 Valor iniciar del filtre per data d'inici
	 * @param dataInici2 Valor final del filtre per data d'inici
	 * @param expedientTipus Retorna només expedients amb el codi de tipus d'expedient indicat
	 * @param estat Retorna només expedients amb el codi d'estat indicat
	 * @param iniciat Retorna només expedients en estat iniciat
	 * @param finalitzat Retorna només expedients en estat finalitzat
	 * @return Una llista amb els expedients trobats segons el criteri de consulta
	 */
	public List<ExpedientInfo> consultaExpedients(
			String titol,
			String numero,
			Date dataInici1,
			Date dataInici2,
			String expedientTipus,
			String estat,
			boolean iniciat,
			boolean finalitzat);
	
	/**
	 * Envia un correu electrònic amb la possibilitat d'adjuntar documents de l'expedient
	 * 
	 * @param recipients Llista de destinataris
	 * @param ccRecipients Llista de destinataris CC
	 * @param bccRecipients Llista de destinataris BCC
	 * @param subject Assumpte del missatge
	 * @param text Contingut del missatge
	 * @param attachments Llista amb els codis de document a adjuntar al missatge
	 * @throws HeliumHandlerException Si no es pot enviar el correu electronic es produirà una excepció
	 */
	public void enviarEmail(
			List<String> recipients,
			List<String> ccRecipients,
			List<String> bccRecipients,
			String subject,
			String text,
			List<String> attachments) throws HeliumHandlerException;
	
	/**
	 * Obté un document de l'expedient
	 * 
	 * @param documentCodi
	 * @return La informació del document
	 */
	public DocumentInfo getDocument(String documentCodi);
	
	/**
	 * Crea una anotació al registre d'entrada
	 * 
	 * @param dadesEntrada Objecte amb la informació per a efectuar l'anotació
	 * @param documentsEntrada codis de documents per a adjuntar a l'anotació
	 * @return La resposta a l'anotació de registre
	 */
	public RespostaRegistre registreEntrada(
			DadesRegistreEntrada dadesEntrada,
			List<String> documentsEntrada);
	/**
	 * Crea una anotació al registre de sortida
	 * 
	 * @param dadesSortida Objecte amb la informació per a efectuar l'anotació
	 * @param documentsSortida codis de documents per a adjuntar a l'anotació
	 * @return La resposta a l'anotació de registre
	 */
	public RespostaRegistre registreSortida(
			DadesRegistreSortida dadesSortida,
			List<String> documentsSortida);
	/**
	 * Crea una notificació telemàtica
	 * 
	 * @param dadesNotificacio Objecte amb la informació per a efectuar l'anotació
	 * @param documentsNotificacio codis de documents per a adjuntar a l'anotació
	 * @return La resposta a l'anotació de registre
	 */
	public RespostaRegistre registreNotificacio(
			DadesRegistreNotificacio dadesNotificacio,
			List<String> documentsNotificacio,
			boolean crearExpedient);
	/**
	 * Obté la el justificant de recepció d'una notificació telemàtica
	 * 
	 * @param registreNumero El número de registre de la notificació telemàtica
	 * @return La informació relativa al justificant de recepció
	 */
	public JustificantRecepcioInfo obtenirJustificantRecepcio(String registreNumero);
	
	/**
	 * Crea una relació entre l'expedient actual i l'expedient indicat
	 * 
	 * @param expedientId L'identificador de l'expedient a relacionar amb l'actual
	 */
	public void expedientRelacionar(Long expedientId);
	/**
	 * Atura la tramitació de l'expedient actual
	 * 
	 * @param motiu El motiu pel qual s'atura l'expedient
	 */
	public void expedientAturar(String motiu);
	/**
	 * Reprèn lla tramitació de l'expedient actual.
	 * 
	 * @throws HeliumHandlerException Si l'expedient no es troba aturat es produirà una excepció
	 */
	public void expedientReprendre() throws HeliumHandlerException;
	/**
	 * Executa una reindexació de l'expedient actual
	 */
	public void expedientReindexar();
	/**
	 * Redirigeix un token de l'expedient cap a un node determinat
	 * 
	 * @param token El token a redirigir
	 * @param nodeName El nom del node destí de la redirecció
	 * @param cancelarTasques Indica si s'han de cancel·lar les tasques actives del token abans de fer la redirecció
	 */
	public void expedientTokenRedirigir(
			Long tokenId,
			String nodeName,
			boolean cancelarTasques);
	
	/**
	 * Emmagatzema els paràmetres per a la retroacció
	 * 
	 * @param parametres Els paràmetres a guardar per la retroacció
	 */
	public void retrocedirGuardarParametres(List<String> parametres);
	
	public RespostaEnviar altaNotificacio(
			DadesNotificacio dadesNotificacio,
			Long expedientId) throws JbpmException;
	
	public void interessatCrear(
			Interessat interessat);
	
	public void interessatModificar(
			Interessat interessat);
	
	public void interessatEliminar(
			String codi,
			Long expedientId);
	
}