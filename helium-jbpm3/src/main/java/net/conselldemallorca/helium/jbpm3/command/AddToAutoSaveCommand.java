/**
 * 
 */
package net.conselldemallorca.helium.jbpm3.command;

import org.jbpm.JbpmContext;
import org.jbpm.command.AbstractBaseCommand;
import org.jbpm.command.Command;

/**
 * Command per esborrar una instància de procés
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class AddToAutoSaveCommand extends AbstractBaseCommand {

	private static final long serialVersionUID = 5635105824309924910L;

	public static final int TIPUS_INSTANCIA_PROCES = 0;
	public static final int TIPUS_INSTANCIA_TASCA = 1;
	public static final int TIPUS_TOKEN = 2;

	private Command commandToExec;
	private long[] ids;
	private int tipus;

	public AddToAutoSaveCommand(Command command, long[] ids, int tipus) {
		this.commandToExec = command;
		this.ids = ids;
		this.tipus = tipus;
	}
	public AddToAutoSaveCommand(Command command, long id, int tipus) {
		this.commandToExec = command;
		this.ids = new long[] {id};
		this.tipus = tipus;
	}

	public Object execute(JbpmContext jbpmContext) throws Exception {
		Object o = commandToExec.execute(jbpmContext);
		for (long id: ids) {
			switch (tipus) {
			case TIPUS_INSTANCIA_PROCES:
				jbpmContext.addAutoSaveProcessInstance(jbpmContext.getProcessInstance(id));
				break;
			case TIPUS_INSTANCIA_TASCA:
				jbpmContext.addAutoSaveTaskInstance(jbpmContext.getTaskInstance(id));
				break;
			default:
				jbpmContext.addAutoSaveToken(jbpmContext.getToken(id));
			}
		}
		return o;
	}

	public long[] getIds() {
		return ids;
	}
	public void setIds(long[] ids) {
		this.ids = ids;
	}
	public Command getCommandToExec() {
		return commandToExec;
	}
	public void setCommandToExec(Command commandToExec) {
		this.commandToExec = commandToExec;
	}
	public int getTipus() {
		return tipus;
	}
	public void setTipus(int tipus) {
		this.tipus = tipus;
	}

	@Override
	public String getAdditionalToStringInformation() {
	    return "ids=" + ids;
	}

}
