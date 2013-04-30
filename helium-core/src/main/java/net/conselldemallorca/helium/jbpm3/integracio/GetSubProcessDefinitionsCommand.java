/**
 * 
 */
package net.conselldemallorca.helium.jbpm3.integracio;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.jbpm.JbpmContext;
import org.jbpm.command.AbstractGetObjectBaseCommand;
import org.jbpm.graph.def.Node;
import org.jbpm.graph.def.ProcessDefinition;
import org.jbpm.graph.node.ProcessState;

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

	public Object execute(JbpmContext jbpmContext) throws Exception {
		List<ProcessDefinition> definicionsProces = new ArrayList<ProcessDefinition>();
		List<ProcessState> subprocessos = null;
		ProcessDefinition rootProcessDefinition = jbpmContext.getGraphSession().getProcessDefinition(id);
		if (rootProcessDefinition != null) {
			Session session = jbpmContext.getSession();
			Query query = session.createQuery(
					"from org.jbpm.graph.node.ProcessState ps " +
					"where ps.processDefinition.id= :processDefinitionId " +
					"order by ps.id");
			query.setParameter("processDefinitionId", id);
			subprocessos = query.list();
		}
		if (subprocessos != null && !subprocessos.isEmpty()) {
			for (ProcessState ps : subprocessos) {
				definicionsProces.add(ps.getSubProcessDefinition());
			}
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
