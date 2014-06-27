package net.conselldemallorca.helium.jbpm3.command;

import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Query;
import org.jbpm.JbpmContext;
import org.jbpm.command.AbstractGetObjectBaseCommand;
import org.jbpm.command.GetProcessInstancesCommand;
import org.jbpm.graph.exe.ProcessInstance;

/**
 * This command can retrieve all process instances (e.g. for admin client).
 * 
 * You have the possibility to filter the command, therefor use the available attributes
 * 
 * @author Bernd Ruecker (bernd.ruecker@camunda.com)
 */
public class GetProcessInstancesEntornCommand extends AbstractGetObjectBaseCommand
{

  private static final long serialVersionUID = -5601050489405283851L;

  private String processDefinitionName;
  private Long entornId;
  
  public Object execute(JbpmContext jbpmContext) throws Exception
  {
    setJbpmContext(jbpmContext);
    StringBuffer queryText = new StringBuffer(
    		"select pi" + 
    		" from org.jbpm.graph.exe.ProcessInstance as pi,"
    		+ "	   net.conselldemallorca.helium.core.model.hibernate.Expedient e " +
    		" where pi.end = null " +
    		" and pi.processDefinition.name = :processDefinitionName " +
    		" and e.processInstanceId = pi.id " +
    		" and e.entorn.id = :entornId " +
    		" order by pi.start desc");

    Query query = jbpmContext.getSession().createQuery(queryText.toString())
    		.setString("processDefinitionName", processDefinitionName)
    		.setLong("entornId", entornId);
    
    return retrieveProcessInstanceDetails(query.list());
  }

  public List retrieveProcessInstanceDetails(List processInstanceList) {
	  Iterator it = processInstanceList.iterator();
	  while (it.hasNext()) {
		  retrieveProcessInstance((ProcessInstance)it.next());
	  }
	  return processInstanceList;
  }

  public String getProcessDefinitionName()
  {
    return processDefinitionName;
  }

  public void setProcessDefinitionName(String processName)
  {
    this.processDefinitionName = processName;
  }

  public long getEntornId()
  {
    return entornId;
  }

  public void setEntornId(Long entornId)
  {
    this.entornId = entornId;
  }


  @Override
  public String getAdditionalToStringInformation()
  {
    return "entornId=" + entornId
      + ";processDefinitionName=" + processDefinitionName;
  }
}
