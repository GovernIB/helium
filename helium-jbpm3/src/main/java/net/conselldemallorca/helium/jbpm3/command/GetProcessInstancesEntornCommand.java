package net.conselldemallorca.helium.jbpm3.command;

import java.util.Iterator;
import java.util.List;

import org.hibernate.Query;
import org.jbpm.JbpmContext;
import org.jbpm.command.AbstractGetObjectBaseCommand;
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
  private Long expedientTipusId;
  
  /** Si el tipus d'expedient és null ha de trobar els expedients de l'entorn relacionats amb les definicions
   * globals i si no és null llavors ha de filtrar els expedients segons el tipus d'expedient.
   */
  public Object execute(JbpmContext jbpmContext) throws Exception
  {
    setJbpmContext(jbpmContext);

    StringBuilder queryText = new StringBuilder();
    queryText.append(
    		"select pi" + 
    		" from org.jbpm.graph.exe.ProcessInstance as pi,"
    		+ "	   net.conselldemallorca.helium.core.model.hibernate.Expedient e ");
    if (expedientTipusId == null) {
    	queryText.append("	   ,net.conselldemallorca.helium.core.model.hibernate.DefinicioProces dp ");
    }
    queryText.append(
    		" where pi.end = null " +
    		" and pi.processDefinition.name = :processDefinitionName " +
    		" and e.id = pi.expedient.id " +
    		" and e.entorn.id = :entornId ");
    if (this.expedientTipusId != null) {
    	// Expedients del tipus d'expedient
    	queryText.append(
    		" and e.tipus.id = :expedientTipusId");
    } else {
    	// Processos relacionats amb una definició deprocés global
    	queryText.append(
        		" and pi.processDefinition.id = dp.jbpmId " +
                " and dp.expedientTipus is null ");    	
    }
    queryText.append(
    		" order by pi.start desc");

    Query query = jbpmContext.getSession().createQuery(queryText.toString())
    		.setString("processDefinitionName", processDefinitionName)
    		.setLong("entornId", entornId);
    if (this.expedientTipusId != null) {
    	query.setLong("expedientTipusId", this.expedientTipusId);
    }
    
    return retrieveProcessInstanceDetails(query.list());
  }

  public List<?> retrieveProcessInstanceDetails(List<?> processInstanceList) {
	  Iterator<?> it = processInstanceList.iterator();
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

  public Long getEntornId()
  {
    return entornId;
  }

  public void setEntornId(Long entornId)
  {
    this.entornId = entornId;
  }

  public Long getExpedientTipusId()
  {
    return expedientTipusId;
  }

  public void setExpedientTipusId(Long expedientTipusId)
  {
    this.expedientTipusId = expedientTipusId;
  }
  
  @Override
  public String getAdditionalToStringInformation()
  {
    return "entornId=" + entornId 
      + ";expedientTipusId=" + expedientTipusId
      + ";processDefinitionName=" + processDefinitionName;
  }

}
