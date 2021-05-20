package es.caib.helium.jbpm3.command;

import java.util.ArrayList;
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

	private Long expedientTipusId;

	public GetProcesDefinitionNotUsedListCommand(Long expedientTipusId) {
		this.expedientTipusId = expedientTipusId;
	}

	@SuppressWarnings("unchecked")
	public Object execute(JbpmContext jbpmContext) throws Exception {
		
		setJbpmContext(jbpmContext);
		StringBuffer queryText = new StringBuffer(
				  "  select	pd" 
				+ "    from org.jbpm.graph.def.ProcessDefinition as pd,"
				+ "	   		net.conselldemallorca.helium.core.model.hibernate.DefinicioProces dp "
				+ "   where	pd.id not in ("
				+ "				select distinct p.processDefinition.id "
				+ "				  from org.jbpm.graph.exe.ProcessInstance as p"
				+ "			) "
//				// 1er nivell de subprocés
//				+ "		and pd.id not in ( "
//				+ "				select sp.id "
//				+ "				  from org.jbpm.graph.node.ProcessState ps "
//				+ "				  left outer join ps.subProcessDefinition sp "
//				+ "				 where ps.processDefinition.id in (select distinct p.processDefinition.id from org.jbpm.graph.exe.ProcessInstance as p) "
//				+ "				   and ps.subProcessDefinition is not null "
//				+ "			) "
				+ "     and pd.id = cast(dp.jbpmId as long) " 
				+ "     and dp.expedientTipus.id = :expedientTipusId "
				+ "		and pd.version != (select max(pd1.version) from org.jbpm.graph.def.ProcessDefinition as pd1 where pd.name = pd1.name) "
				+ "order by dp.jbpmKey asc, dp.versio desc");
		
//		"select sp " +
//		"from org.jbpm.graph.node.ProcessState ps " +
//		"left outer join ps.subProcessDefinition sp " +
//		"where ps.processDefinition.id= :processDefinitionId " +
//		"order by ps.id");

		Query query = jbpmContext.getSession().createQuery(queryText.toString()).setLong("expedientTipusId", expedientTipusId);
		List<ProcessDefinition> result = (List<ProcessDefinition>)query.list();
		
		query = jbpmContext.getSession().createQuery(
				  "  select	pd" 
				+ "    from org.jbpm.graph.def.ProcessDefinition as pd,"
				+ "	   		net.conselldemallorca.helium.core.model.hibernate.DefinicioProces dp "
				+ "   where	pd.id in ("
				+ "				select distinct p.processDefinition.id "
				+ "				  from org.jbpm.graph.exe.ProcessInstance as p"
				+ "			) "
				+ "     and pd.id = cast(dp.jbpmId as long) "
				+ "     and dp.expedientTipus.id = :expedientTipusId ")
				.setLong("expedientTipusId", expedientTipusId);
		List<ProcessDefinition> pares = (List<ProcessDefinition>)query.list();
		
		while (pares.size() > 0) {
			List<ProcessDefinition> fills = new ArrayList<ProcessDefinition>();
			int from = 0;
			do {
				int to = Math.min(500, pares.size());
//				queryText = new StringBuffer(
//						  "  select	pd" 
//						+ "    from org.jbpm.graph.def.ProcessDefinition as pd,"
//						+ "	   		net.conselldemallorca.helium.core.model.hibernate.DefinicioProces dp "
//						+ "   where	pd.id not in ("
//						+ "				select distinct p.processDefinition.id "
//						+ "				  from org.jbpm.graph.exe.ProcessInstance as p"
//						+ "			) "
//						// Subprocés
//						+ "		and pd.id not in ( "
//						+ "				select sp.id "
//						+ "				  from org.jbpm.graph.node.ProcessState ps "
//						+ "				  left outer join ps.subProcessDefinition sp "
//						+ "				 where ps.processDefinition.id in :pares "
//						+ "				   and ps.subProcessDefinition is not null "
//						+ "			) "
//						+ "     and pd.id = cast(dp.jbpmId as long) " 
//						+ "     and dp.expedientTipus.id = :expedientTipusId "
//						+ "		and pd.version != (select max(pd1.version) from org.jbpm.graph.def.ProcessDefinition as pd1 where pd.name = pd1.name)");
				query = jbpmContext.getSession().createQuery(
						  "  select	pd" 
						+ "    from org.jbpm.graph.def.ProcessDefinition as pd "
						+ "   where	pd.id in ( "
						+ "				select sp.id "
						+ "				  from org.jbpm.graph.node.ProcessState ps "
						+ "				  left outer join ps.subProcessDefinition sp "
						+ "				 where ps.processDefinition in :pares "
						+ "				   and ps.subProcessDefinition is not null "
						+ "			)")
						.setParameterList("pares", pares.subList(from, to));
				fills.addAll((List<ProcessDefinition>)query.list());
				from = to;
			} while (from < pares.size());

			result.removeAll(fills);
			pares = fills;
		}

//		List<ProcessDefinition> orderedResult = new ArrayList<ProcessDefinition>(result);
//		Collections.sort(orderedResult, new Comparator<ProcessDefinition>() {
//
//			@Override
//			public int compare(ProcessDefinition o1, ProcessDefinition o2) {
//				try {
//					int comp = o1.getName().compareTo(o2.getName());
//					if (comp == 0) {
//						comp = o1.getVersion() - o2.getVersion();
//					}
//					return comp;
//				} catch (Exception e) {
//					return -1;
//				}
//			}
//			
//		});
		// return retrieveProcessInstanceDetails(query.list());
		return result;
	}

	public List<?> retrieveProcessInstanceDetails(List<?> processDefinitionList) {
		Iterator<?> it = processDefinitionList.iterator();
		while (it.hasNext()) {
			retrieveProcessDefinition((ProcessDefinition) it.next());
		}
		return processDefinitionList;
	}

	public long getExpedientTipusId() {
		return expedientTipusId;
	}

	public void setExpedientTipusId(Long expedientTipusId) {
		this.expedientTipusId = expedientTipusId;
	}

	@Override
	public String getAdditionalToStringInformation() {
		return "expedientTipusId=" + expedientTipusId;
	}
}
