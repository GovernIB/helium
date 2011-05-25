/**
 * 
 */
package net.conselldemallorca.helium.jbpm3.handlers;

import org.jbpm.graph.def.ActionHandler;
import org.jbpm.graph.exe.ExecutionContext;

/**
 * Handler per a adjuntar un document al procés.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface DocumentAdjuntarHandlerInterface extends ActionHandler {

	public void execute(ExecutionContext executionContext) throws Exception;



	public void setDocumentOrigen(String documentOrigen);
	public void setVarDocumentOrigen(String varDocumentOrigen);
	public void setTitol(String titol);
	public void setVarTitol(String varTitol);
	public void setData(String data);
	public void setVarData(String varData);
	public void setConcatenarTitol(String concatenarTitol);
	public void setEsborrarDocument(String esborrarDocument);

}
