/**
 * 
 */
package net.conselldemallorca.helium.jbpm3.handlers;

import org.jbpm.graph.def.ActionHandler;
import org.jbpm.graph.exe.ExecutionContext;

/**
 * Handler per a interactuar amb el registre de sortida.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface PinbalConsultaGenericaHandlerInterface extends ActionHandler {

	public void execute(ExecutionContext executionContext) throws Exception;
	
	public void setVar_codi(String var_codi);
	public void setCodi(String codi);
	public void setServeiCodi(String serveiCodi);
	public void setVarServeiCodi(String varServeiCodi);
	public void setDocumentCodi(String documentCodi);
	public void setVarDocumentCodi(String varDocumentCodi);
	public void setFinalitat(String finalitat);
	public void setVarFinalitat(String varFinalitat);
	public void setConsentiment(String consentiment);
	public void setVarConsentiment(String varConsentiment);
	public void setInteressatCodi(String interessatCodi);
	public void setVarInteressatCodi(String varInteressatCodi);
	public void setVarTitularNom(String varTitularNom);
	public void setTitularNom(String titularNom);
	public void setVarTitularLlinatge1(String varTitularLlinatge1);
	public void setTitularLlinatge1(String titularLlinatge1);
	public void setVarTitularLlinatge2(String varTitularLlinatge2);
	public void setTitularLlinatge2(String titularLlinatge2);
	public void setVarTitularDocumentacio(String varTitularDocumentacio);
	public void setTitularDocumentacio(String titularDocumentacio);
	public void setDadesEspecifiques(String dadesEspecifiques) ;
	public void setVarDadesEspecifiques(String varDadesEspecifiques);
	public void setVarTitularTipusDocumentacio(String varTitularTipusDocumentacio);
	public void setTitularTipusDocumentacio(String titularTipusDocumentacio);
	public void setAsincrona(String asincrona);
	public void setVarAsincrona(String varAsincrona);
	public void setTransicioOK(String transicioOK);
	public void setVarTransicioOK(String varTransicioOK);
	public void setTransicioKO(String transicioKO);
	public void setVarTransicioKO(String varTransicioKO);
	
}
