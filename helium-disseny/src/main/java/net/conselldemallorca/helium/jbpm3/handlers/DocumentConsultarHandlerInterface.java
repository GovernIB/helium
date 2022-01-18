/**
 * 
 */
package net.conselldemallorca.helium.jbpm3.handlers;

import org.jbpm.graph.def.ActionHandler;
import org.jbpm.graph.exe.ExecutionContext;

/**
 * Handler per consultar el CSV i/o la URL de verificació de firmes d'un document firmat. A partir
 * del codi del document consulta la informació i posa el CSV i la URL de verificació de firmes
 * en les variables especificades per codi.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface DocumentConsultarHandlerInterface extends ActionHandler {

	public void execute(ExecutionContext executionContext) throws Exception;

	public void setDocument(String document);
	public void setVarDocument(String varDocument);
	public void setVarCsv(String varCsv);
	public void setVarUrl(String varUrl);
}
