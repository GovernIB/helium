package net.conselldemallorca.helium.jbpm3.command;

import java.util.Iterator;
import java.util.List;

import org.hibernate.Query;
import org.jbpm.JbpmContext;
import org.jbpm.command.AbstractGetObjectBaseCommand;
import org.jbpm.graph.def.ProcessDefinition;

/**
 * This command can retrieve all process instances (e.g. for admin client).
 * 
 * You have the possibility to filter the command, therefor use the available
 * attributes
 * 
 * @author Bernd Ruecker (bernd.ruecker@camunda.com)
 */
public class GetProcesDefinitionNotUsedListCommand extends AbstractGetObjectBaseCommand {

	private static final long serialVersionUID = -5601050489405283851L;

	private Long entornId;

	public GetProcesDefinitionNotUsedListCommand(Long entornId) {
		this.entornId = entornId;
	}

	public Object execute(JbpmContext jbpmContext) throws Exception {
		setJbpmContext(jbpmContext);
		StringBuffer queryText = new StringBuffer("select pd" + " from org.jbpm.graph.def.ProcessDefinition as pd,"
				+ "	   net.conselldemallorca.helium.core.model.hibernate.DefinicioProces dp "
				+ " where pd.id not in (select distinct p.processDefinition.id from org.jbpm.graph.exe.ProcessInstance as p) "
				+ " and pd.id = cast(dp.jbpmId as long) " + " and dp.entorn.id = :entornId "
				+ " order by dp.jbpmKey asc, dp.versio desc");

		Query query = jbpmContext.getSession().createQuery(queryText.toString()).setLong("entornId", entornId);

		// return retrieveProcessInstanceDetails(query.list());
		return query.list();
	}

	public List<?> retrieveProcessInstanceDetails(List<?> processDefinitionList) {
		Iterator<?> it = processDefinitionList.iterator();
		while (it.hasNext()) {
			retrieveProcessDefinition((ProcessDefinition) it.next());
		}
		return processDefinitionList;
	}

	public long getEntornId() {
		return entornId;
	}

	public void setEntornId(Long entornId) {
		this.entornId = entornId;
	}

	@Override
	public String getAdditionalToStringInformation() {
		return "entornId=" + entornId;
	}
}
