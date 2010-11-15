/**
 * 
 */
package net.conselldemallorca.helium.jbpm3.handlersmock;

import java.util.Date;

import org.jbpm.graph.def.ActionHandler;
import org.jbpm.graph.exe.ExecutionContext;

/**
 * Handler per a afegir un event a un expedient de la zona
 * personal del ciutadà.
 * 
 * @author Josep Gayà <josepg@limit.es>
 */
@SuppressWarnings("serial")
public class ZonaperExpedientEventHandler implements ActionHandler {

	public void execute(ExecutionContext executionContext) throws Exception {}

	public void setTitol(String titol) {}
	public void setVarTitol(String varTitol) {}
	public void setText(String text) {}
	public void setVarText(String varText) {}
	public void setTextSms(String textSms) {}
	public void setVarTextSms(String varTextSms) {}
	public void setEnllasConsulta(String enllasConsulta) {}
	public void setVarEnllasConsulta(String varEnllasConsulta) {}
	public void setData(Date data) {}
	public void setVarData(String varData) {}
	public void setDocumentCodi(String documentCodi) {}
	public void setVarDocumentCodi(String varDocumentCodi) {}
	public void setRedoseModel(String redoseModel) {}
	public void setVarRedoseModel(String varRedoseModel) {}
	public void setRedoseVersio(String redoseVersio) {}
	public void setVarRedoseVersio(String varRedoseVersio) {}

}
