/**
 * 
 */
package es.caib.helium.jbpm3.command;

import org.hibernate.SQLQuery;
import org.jbpm.JbpmContext;
import org.jbpm.command.AbstractBaseCommand;

/**
 * Command per a retornar l'id de la variable associat amb un log
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class HasStartBetweenLogsCommand extends AbstractBaseCommand {

	private static final long serialVersionUID = -190884754944405123L;
	private long begin;
	private long end;
	private long taskInstance;

	public HasStartBetweenLogsCommand(long begin, long end, long taskinstance) {
		super();
		this.begin = begin;
		this.end = end;
		this.taskInstance = taskinstance;
	}

	public Object execute(JbpmContext jbpmContext) throws Exception {
		SQLQuery q = jbpmContext.getSession().createSQLQuery(	"select	l.id_ " +
																"  from	jbpm_log l " +
																" where	l.id_ >= ? " +
																"   and l.id_ <= ? " +
																"   and l.class_ = '1' " +
																"	and	l.taskinstance_ = ?");
		q.setLong(0, begin);
		q.setLong(1, end);
		q.setLong(2, taskInstance);
		Number result = (Number)q.uniqueResult();
		if (result != null)
			return true;
		else
			return false;
	}

	public long getBegin() {
		return begin;
	}
	public void setBegin(long begin) {
		this.begin = begin;
	}
	public long getEnd() {
		return end;
	}
	public void setEnd(long end) {
		this.end = end;
	}
	public long getTaskInstance() {
		return taskInstance;
	}
	public void setTaskInstance(long taskInstance) {
		this.taskInstance = taskInstance;
	}

	@Override
	public String getAdditionalToStringInformation() {
	    return "begin=" + begin + ",end =" + end + "taskInstance=" + taskInstance;
	}

}
