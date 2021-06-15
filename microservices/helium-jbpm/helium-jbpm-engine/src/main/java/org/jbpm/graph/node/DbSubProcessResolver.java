package org.jbpm.graph.node;

import net.conselldemallorca.helium.api.dto.EntornDto;
import net.conselldemallorca.helium.jbpm3.integracio.Jbpm3HeliumBridge;

import org.dom4j.Element;
import org.jbpm.JbpmContext;
import org.jbpm.graph.def.ProcessDefinition;
import org.jbpm.jpdl.JpdlException;

public class DbSubProcessResolver implements SubProcessResolver {

  private static final long serialVersionUID = 1L;

  @SuppressWarnings("deprecation")
public ProcessDefinition findSubProcess(Element subProcessElement) {
    ProcessDefinition subProcessDefinition = null;

    String subProcessName = subProcessElement.attributeValue("name");
    String subProcessVersion = subProcessElement.attributeValue("version");

    // if this parsing is done in the context of a process deployment, there is
    // a database connection to look up the subprocess.
    // when there is no jbpmSession, the definition will be left null... the
    // testcase can set it as appropriate.
    JbpmContext jbpmContext = JbpmContext.getCurrentJbpmContext();
    if (jbpmContext != null) {
      
      // now, we must be able to find the sub-process
      if (subProcessName != null) {
        
        // if the name and the version are specified
    	EntornDto entorn = Jbpm3HeliumBridge.getInstanceService().getEntornActual();
        if (subProcessVersion != null) {
          
          try {
            int version = Integer.parseInt(subProcessVersion);
            if (entorn != null & entorn.getId() != null) {
            	Long entornId = Jbpm3HeliumBridge.getInstanceService().getDefinicioProcesEntornAmbJbpmKeyIVersio(subProcessName, version);
            	if (entornId != null && !entornId.equals(entorn.getId()))
            		throw new JpdlException("sub-process not accesible: " + subProcessElement.asXML());
            }
            // select that exact process definition as the subprocess definition
            subProcessDefinition = jbpmContext.getGraphSession().findProcessDefinition(subProcessName, version);

          } catch (NumberFormatException e) {
            throw new JpdlException("version in process-state was not a number: " + subProcessElement.asXML());
          }
          
        } else { // if only the name is specified
        	if (entorn != null & entorn.getId() != null) {
        		Long entornId = Jbpm3HeliumBridge.getInstanceService().getDarreraVersioEntornAmbEntornIJbpmKey(
        				entorn.getId(),
        				subProcessName);
            	if (entornId != null && !entornId.equals(entorn.getId()))
            		throw new JpdlException("sub-process not accesible: " + subProcessElement.asXML());
            }
          // select the latest version of that process as the subprocess
          // definition
          subProcessDefinition = jbpmContext.getGraphSession().findLatestProcessDefinition(subProcessName);
        }
      } else {
        throw new JpdlException("no sub-process name specfied in process-state: " + subProcessElement.asXML());
      }
    }

    return subProcessDefinition;
  }
}
