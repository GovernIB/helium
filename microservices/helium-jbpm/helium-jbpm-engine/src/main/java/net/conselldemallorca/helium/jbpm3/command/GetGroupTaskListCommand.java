package net.conselldemallorca.helium.jbpm3.command;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Query;
import org.jbpm.JbpmContext;
import org.jbpm.command.AbstractGetObjectBaseCommand;
import org.jbpm.taskmgmt.exe.TaskInstance;

/**
 * Command per obtenir el llistat de tasques de grup
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class GetGroupTaskListCommand extends AbstractGetObjectBaseCommand {

	private static final long serialVersionUID = -1908847549444051495L;

	private List<Long> ids;
	private String actorId;
	private boolean getProcessInstanceIds = false;
	
	public GetGroupTaskListCommand() {}

	public GetGroupTaskListCommand(String actorId, List<Long> ids) {
		super();
		this.actorId = actorId;
		this.ids = ids;
	}

	public GetGroupTaskListCommand(String actorId, List<Long> ids, boolean getProcessInstanceIds) {
		super();
		this.actorId = actorId;
		this.ids = ids;
		this.getProcessInstanceIds = true;
	}

	public GetGroupTaskListCommand(String actorId) {
		super();
		this.actorId = actorId;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Object execute(JbpmContext jbpmContext) throws Exception {
		
		setJbpmContext(jbpmContext);
		List result = new ArrayList<TaskInstance>();
		
		if (ids != null) {
			if (ids.isEmpty()) {
				return result;
			}
			String hql = null;
			if (getProcessInstanceIds) {
				hql = "select ti.processInstance.id ";			
			} else {
				hql = "select ti ";
			}
			hql += 
			   " 	from org.jbpm.taskmgmt.exe.TaskInstance as ti "+
		       " 	where  " +
		       "	ti.isSuspended != true "+
		       " 	and ti.isOpen = true" +
		       "	and ti.id in (:ids)";
			
			Query query = jbpmContext.getSession().createQuery(hql);
			query = translateIn(ids, query, "ids", "ti.id");		
			
			result = query.list();
		}
		else {
			List actorIds = new ArrayList();
			actorIds.add(actorId);
		    result = jbpmContext.getGroupTaskList(actorIds);
		}
	    return result;
	    //return retrieveTaskInstanceDetails(result);
	}

	public String getParametersToString() {
		return "GetGroupTaskListCommand [ids=" + ids + ", actorId=" + actorId + "]";
	}

	public String getActorId() {
		return actorId;
	}
	public void setActorId(String actorId) {
		this.actorId = actorId;
	}

	@Override
	public String getAdditionalToStringInformation() {
	    return "actorId=" + actorId;
	}

	//methods for fluent programming
	public GetGroupTaskListCommand actorId(String actorId) {
		setActorId(actorId);
	    return this;
	}

	@SuppressWarnings("rawtypes")
	public List retrieveTaskInstanceDetails(List taskInstanceList) {
		for (Iterator iter = taskInstanceList.iterator(); iter.hasNext();) {
			retrieveTaskInstanceDetails((TaskInstance)iter.next());
		}
		return taskInstanceList;
	}

	private Query translateIn(final Collection<?> values, final Query query, final String param, final String namedParam) {
		final int size = values.size();
		final int mod = (size % 1000);
		final int numberOfIn = (size / 1000) + (mod == 0 ? 0 : 1);
		final List<String> params = new ArrayList<String>();

		final String regex = "([\\w\\.]+)\\s+(not\\s+)?in\\s*\\(\\s*:" + param + "\\s*\\)";

		final StringBuilder in = new StringBuilder(" $2 (");

		for (int i = 0; i < numberOfIn; i++) {
			final StringBuilder newNameParam = new StringBuilder();
			newNameParam.append(param);
			newNameParam.append(i);

			in.append("$1 in (:");
			in.append(newNameParam.toString());
			in.append(")");

			params.add(newNameParam.toString());

			if (i + 1 < numberOfIn) {
				in.append(" or ");
			}
		}

		in.append(") ");

		final String newQueryString = query.getQueryString().replaceAll(regex, in.toString());

		final Query newQuery = getJbpmContext().getSession().createQuery(newQueryString);
		
		final List<Object> copy = new ArrayList<Object>(values);

		for (int count = 0, from = 0, to = (size>1000) ? 1000 : size; count < numberOfIn; count++) {
			final String nameParam = params.get(count);
			newQuery.setParameterList(nameParam, copy.subList(from, to));

			from = to;

			if (count + 2 < numberOfIn) {
				to += 1000;
			}
			else if (count + 2 == numberOfIn) {
				to += (mod == 0) ? 1000 : mod;
			}
		}

		return newQuery;
	}
}
