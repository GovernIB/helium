/**
 * 
 */
package net.conselldemallorca.helium.webapp.v3.command;

/**
 * Command per executar scripts als expedients
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ExpedientEinesScriptCommand {

	private String script;
	private String scriptProcessId;

	public String getScript() {
		return script;
	}
	public void setScript(String script) {
		this.script = script;
	}
	public String getScriptProcessId() {
		return scriptProcessId;
	}
	public void setScriptProcessId(String scriptProcessId) {
		this.scriptProcessId = scriptProcessId;
	}
	
}
