package net.conselldemallorca.helium.jbpm3.handlersmock;

import org.jbpm.graph.def.ActionHandler;
import org.jbpm.graph.exe.ExecutionContext;

/**
 * Handler per fer la integració amb portasignatures.
 * 
 * @author Miquel Angel Amengual <miquelaa@limit.es>
 */
@SuppressWarnings("serial")
public class PortasignaturesHandler implements ActionHandler {

	public void execute(ExecutionContext executionContext) throws Exception {}

	public void setVarResponsableCodi(String varResponsableCodi) {}
	public void setResponsableCodi(String responsableCodi) {}
	public void setVarDocument(String varDocument) {}
	public void setDocument(String document) {}
	public void setVarImportancia(String varImportancia) {}
	public void setImportancia(String importancia) {}
	public void setVarDataLimit(String varDataLimit) {}
	public void setDataLimit(String dataLimit) {}
	public void setVarTransicioOK(String varTransicioOK) {}
	public void setTransicioOK(String transicioOK) {}
	public void setVarTransicioKO(String varTransicioKO) {}
	public void setTransicioKO(String transicioKO) {}

}
