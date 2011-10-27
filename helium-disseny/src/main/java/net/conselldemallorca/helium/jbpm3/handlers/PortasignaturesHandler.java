package net.conselldemallorca.helium.jbpm3.handlers;

import net.conselldemallorca.helium.jbpm3.handlers.PortasignaturesHandlerInterface;

import org.jbpm.graph.def.ActionHandler;
import org.jbpm.graph.exe.ExecutionContext;

/**
 * Handler per fer la integració amb portasignatures.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@SuppressWarnings("serial")
public class PortasignaturesHandler implements ActionHandler, PortasignaturesHandlerInterface {

	public void execute(ExecutionContext executionContext) throws Exception {}

	public void setVarResponsableCodi(String varResponsableCodi) {}
	public void setResponsableCodi(String responsableCodi) {}
	public void setPas1Responsables(String pas1Responsables) {}
	public void setVarPas1Responsables(String varPas1Responsables) {}
	public void setPas1MinSignataris(String pas1MinSignataris) {}
	public void setVarPas1MinSignataris(String varPas1MinSignataris) {}
	public void setPas2Responsables(String pas2Responsables) {}
	public void setVarPas2Responsables(String varPas2Responsables) {}
	public void setPas2MinSignataris(String pas2MinSignataris) {}
	public void setVarPas2MinSignataris(String varPas2MinSignataris) {}
	public void setPas3Responsables(String pas3Responsables) {}
	public void setVarPas3Responsables(String varPas3Responsables) {}
	public void setPas3MinSignataris(String pas3MinSignataris) {}
	public void setVarPas3MinSignataris(String varPas3MinSignataris) {}
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
