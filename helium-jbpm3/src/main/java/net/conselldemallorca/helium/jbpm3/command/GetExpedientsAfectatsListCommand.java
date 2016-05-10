package net.conselldemallorca.helium.jbpm3.command;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.hibernate.Query;
import org.jbpm.JbpmContext;
import org.jbpm.command.AbstractGetObjectBaseCommand;
import org.jbpm.graph.def.ProcessDefinition;
import org.jbpm.graph.exe.ProcessInstanceExpedient;

/**
 * This command can retrieve all process instances (e.g. for admin client).
 * 
 * You have the possibility to filter the command, therefor use the available
 * attributes
 * 
 * @author Bernd Ruecker (bernd.ruecker@camunda.com)
 */
public class GetExpedientsAfectatsListCommand extends AbstractGetObjectBaseCommand {

	private static final long serialVersionUID = -5601050489405283851L;

	private Long processDefinitionId;
	private Long expedientTipusId;
	
	public GetExpedientsAfectatsListCommand(
			Long expedientTipusId,
			Long processDefinitionId) {
		this.expedientTipusId = expedientTipusId;
		this.processDefinitionId = processDefinitionId;
	}

	@SuppressWarnings("unchecked")
	public Object execute(JbpmContext jbpmContext) throws Exception {
		setJbpmContext(jbpmContext);
		Set<ProcessInstanceExpedient> expedientsAfectats = new HashSet<ProcessInstanceExpedient>();
		
		StringBuffer queryText = new StringBuffer(
				  "  select	distinct ex" 
				+ "    from org.jbpm.graph.log.TransitionLog as tl,"
				+ "	   		org.jbpm.graph.exe.ProcessInstanceExpedient ex "
				+ "   where	tl.transition.from.processDefinition.id = :processDefinitionId "
				+ "		and	tl.token.processInstance.expedient.id = ex.id "
				+ "		and tl.token.processInstance.expedient.tipus.id = :expedientTipusId "
				+ "order by ex.id asc");
		Query query = jbpmContext.getSession().createQuery(queryText.toString())
				.setLong("expedientTipusId", expedientTipusId)
				.setLong("processDefinitionId", processDefinitionId);
		expedientsAfectats.addAll((List<ProcessInstanceExpedient>)query.list());
		
		queryText = new StringBuffer(
				  "  select	distinct ex" 
				+ "    from org.jbpm.graph.log.SignalLog as sl,"
				+ "	   		org.jbpm.graph.exe.ProcessInstanceExpedient ex "
				+ "   where	sl.transition.from.processDefinition.id = :processDefinitionId "
				+ "		and sl.token.processInstance.expedient.tipus.id = :expedientTipusId "
				+ "		and	sl.token.processInstance.expedient.id = ex.id "
				+ "order by ex.id asc");
		query = jbpmContext.getSession().createQuery(queryText.toString())
				.setLong("expedientTipusId", expedientTipusId)
				.setLong("processDefinitionId", processDefinitionId);
		expedientsAfectats.addAll((List<ProcessInstanceExpedient>)query.list());
		
		queryText = new StringBuffer(
				  "  select	distinct ex" 
				+ "    from org.jbpm.graph.log.NodeLog as nl,"
				+ "	   		org.jbpm.graph.exe.ProcessInstanceExpedient ex "
				+ "   where	nl.node.processDefinition.id = :processDefinitionId "
				+ "		and nl.token.processInstance.expedient.tipus.id = :expedientTipusId "
				+ "		and	nl.token.processInstance.expedient.id = ex.id "
				+ "order by ex.id asc");
		query = jbpmContext.getSession().createQuery(queryText.toString())
				.setLong("expedientTipusId", expedientTipusId)
				.setLong("processDefinitionId", processDefinitionId);
		expedientsAfectats.addAll((List<ProcessInstanceExpedient>)query.list());
		
		queryText = new StringBuffer(
				  "  select	distinct ex" 
				+ "    from org.jbpm.graph.log.ActionLog as al,"
				+ "	   		org.jbpm.graph.exe.ProcessInstanceExpedient ex "
				+ "   where	al.action.processDefinition.id = :processDefinitionId "
				+ "		and al.token.processInstance.expedient.tipus.id = :expedientTipusId "
				+ "		and	al.token.processInstance.expedient.id = ex.id "
				+ "order by ex.id asc");
		query = jbpmContext.getSession().createQuery(queryText.toString())
				.setLong("expedientTipusId", expedientTipusId)
				.setLong("processDefinitionId", processDefinitionId);
		expedientsAfectats.addAll((List<ProcessInstanceExpedient>)query.list());
		
		return new ArrayList<ProcessInstanceExpedient>(expedientsAfectats);
	}

	public List<?> retrieveProcessInstanceDetails(List<?> processDefinitionList) {
		Iterator<?> it = processDefinitionList.iterator();
		while (it.hasNext()) {
			retrieveProcessDefinition((ProcessDefinition) it.next());
		}
		return processDefinitionList;
	}

	public Long getProcessDefinitionId() {
		return processDefinitionId;
	}
	public void setProcessDefinitionId(Long processDefinitionId) {
		this.processDefinitionId = processDefinitionId;
	}

	public Long getExpedientTipusId() {
		return expedientTipusId;
	}
	public void setExpedientTipusId(Long expedientTipusId) {
		this.expedientTipusId = expedientTipusId;
	}

	@Override
	public String getAdditionalToStringInformation() {
		return "processDefinitionId=" + processDefinitionId + ",expedientTipusId=" + expedientTipusId;
	}
}
