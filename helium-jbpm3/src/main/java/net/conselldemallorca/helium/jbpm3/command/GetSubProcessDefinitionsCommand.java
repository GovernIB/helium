/**
 * 
 */
package net.conselldemallorca.helium.jbpm3.command;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.jbpm.JbpmContext;
import org.jbpm.command.AbstractGetObjectBaseCommand;
import org.jbpm.graph.def.ProcessDefinition;

/**
 * Command per obtenir els subprocessos donada la id d'una definició de procés
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class GetSubProcessDefinitionsCommand extends AbstractGetObjectBaseCommand {

	private static final long serialVersionUID = -1908847549444051495L;
	private long id;

	public GetSubProcessDefinitionsCommand() {}

	public GetSubProcessDefinitionsCommand(long id){
		super();
		this.id = id;
	}

	@SuppressWarnings("unchecked")
	public Object execute(JbpmContext jbpmContext) throws Exception {
		List<ProcessDefinition> definicionsProces = new ArrayList<ProcessDefinition>();
		List<Object[]> subprocessos = null;
		ProcessDefinition rootProcessDefinition = jbpmContext.getGraphSession().getProcessDefinition(id);
		if (rootProcessDefinition != null) {
			Session session = jbpmContext.getSession();
			Query query = session.createQuery(
					"select sp, ps.subProcessName " +
					"from org.jbpm.graph.node.ProcessState ps " +
					"left outer join ps.subProcessDefinition sp " +
					"where ps.processDefinition.id= :processDefinitionId " +
					"order by ps.id");
			query.setParameter("processDefinitionId", id);
			subprocessos = query.list();
		}
		if (subprocessos != null && !subprocessos.isEmpty()) {
			for (Object[] obj: subprocessos) {
				ProcessDefinition spd = (ProcessDefinition)obj[0];
				String spn = (String)obj[1];
				
				if (spd != null) {
					definicionsProces.add(spd);
				} else if (spn != null) {
					spd = jbpmContext.getGraphSession().findLatestProcessDefinition(spn);
					if (spd != null) 
						definicionsProces.add(spd);
				}
			}
//			for (ProcessState ps : subprocessos) {
//				if (ps.getSubProcessDefinition() != null) {
//					definicionsProces.add(ps.getSubProcessDefinition());
//				}
//			}
		}
		return definicionsProces;
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
	public GetSubProcessDefinitionsCommand id(long id) {
		setId(id);
	    return this;
	}

}
