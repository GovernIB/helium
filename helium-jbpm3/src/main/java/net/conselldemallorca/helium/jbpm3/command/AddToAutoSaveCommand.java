/**
 * 
 */
package net.conselldemallorca.helium.jbpm3.command;

import org.jbpm.JbpmContext;
import org.jbpm.command.AbstractBaseCommand;
import org.jbpm.command.Command;
import org.jbpm.graph.exe.ProcessInstance;
import org.jbpm.graph.exe.Token;
import org.jbpm.taskmgmt.exe.TaskInstance;

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
				ProcessInstance pi = jbpmContext.getProcessInstance(id);
				if (getAmbRetroaccio(jbpmContext, pi))
					jbpmContext.addAutoSaveProcessInstance(pi);
				break;
			case TIPUS_INSTANCIA_TASCA:
				TaskInstance ti = jbpmContext.getTaskInstance(id);
				if (getAmbRetroaccio(jbpmContext, ti.getProcessInstance()))
					jbpmContext.addAutoSaveTaskInstance(ti);
				break;
			default:
				Token t = jbpmContext.getToken(id);
				if (getAmbRetroaccio(jbpmContext, t.getProcessInstance()))
					jbpmContext.addAutoSaveToken(t);
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
	
	private Boolean getAmbRetroaccio(JbpmContext jbpmContext, ProcessInstance processInstance) {
		if (processInstance.getExpedient() == null) {
			return true;
		} else {
			return processInstance.getExpedient().isAmbRetroaccio();
		}
//		Query query = jbpmContext.getSession().createQuery(
//				"select te.ambRetroaccio " +
//				"  from	net.conselldemallorca.helium.core.model.hibernate.ExpedientTipus te, " +
//				" 		org.jbpm.graph.exe.ProcessInstanceExpedient exp " +
//				" where  exp.processInstanceId = :processInstanceId " +
//				"   and  exp.expedientTipusId = te.id ");
//		query.setParameter("processInstanceId", processInstanceId.toString());
//		
//		Boolean ambRetroaccio = (Boolean)query.uniqueResult();
//		if (ambRetroaccio == null)
//			return false;
//		return ambRetroaccio;
	}

	@Override
	public String getAdditionalToStringInformation() {
	    return "ids=" + ids;
	}

}
