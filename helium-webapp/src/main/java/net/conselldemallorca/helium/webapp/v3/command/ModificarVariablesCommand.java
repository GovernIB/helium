/**
 * 
 */
package net.conselldemallorca.helium.webapp.v3.command;


/**
 * Command pel canvi de versió d'un procés
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ModificarVariablesCommand {

	private String definicioProcesIdVar;
	private String definicioProcesIdVarValor;
	private String id;
	private String taskId;
	private String var;
	



	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTaskId() {
		return taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}



	public String getVar() {
		return var;
	}

	public void setVar(String var) {
		this.var = var;
	}

	public String getDefinicioProcesIdVarValor() {
		return definicioProcesIdVarValor;
	}

	public void setDefinicioProcesIdVarValor(String definicioProcesIdVarValor) {
		this.definicioProcesIdVarValor = definicioProcesIdVarValor;
	}

	public ModificarVariablesCommand() {}

	public String getDefinicioProcesIdVar() {
		return definicioProcesIdVar;
	}
	public void setDefinicioProcesIdVar(String definicioProcesIdVar) {
		this.definicioProcesIdVar = definicioProcesIdVar;
	}

}
