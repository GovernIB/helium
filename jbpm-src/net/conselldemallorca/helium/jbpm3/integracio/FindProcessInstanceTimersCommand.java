/**
 * 
 */
package net.conselldemallorca.helium.jbpm3.integracio;

import org.hibernate.Query;
import org.hibernate.Session;
import org.jbpm.JbpmContext;
import org.jbpm.command.AbstractBaseCommand;

/**
 * Command que retorna tots els Timers associats a una instància de procés
 * 
 * @author Josep Gayà <josepg@limit.es>
 */
public class FindProcessInstanceTimersCommand extends AbstractBaseCommand {

	private static final long serialVersionUID = -1908847549444051495L;
	private long id;



	public FindProcessInstanceTimersCommand(
			long id) {
		super();
		this.id = id;
	}

	public Object execute(JbpmContext jbpmContext) throws Exception {
		Session session = jbpmContext.getSession();
		Query query = session.createQuery(
				"from org.jbpm.job.Timer timer " +
				"where timer.processInstance.id = :processInstanceId " +
				"  and (timer.lockOwner is null " +
				"   or timer.repeat is not null)");
		query.setParameter("processInstanceId", id);
		return query.list();
	}

	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}

	@Override
	public String getAdditionalToStringInformation() {
	    return "id=" + id;
	}

	//methods for fluent programming
	public FindProcessInstanceTimersCommand id(long id) {
		setId(id);
	    return this;
	}

}
