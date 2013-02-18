package org.jbpm.graph.node;

import net.conselldemallorca.helium.jbpm3.integracio.Jbpm3HeliumBridge;
import net.conselldemallorca.helium.v3.core.api.dto.DefinicioProcesDto;
import net.conselldemallorca.helium.v3.core.api.dto.EntornDto;
import net.conselldemallorca.helium.v3.core.api.exception.EntornNotFoundException;

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
        if (subProcessVersion != null) {
          
          try {
            int version = Integer.parseInt(subProcessVersion);
            EntornDto entorn = Jbpm3HeliumBridge.getInstance().getEntornActual();
            if (entorn != null & entorn.getId() != null) {
            	DefinicioProcesDto definicioProces = Jbpm3HeliumBridge.getInstance().getDefinicioProcesAmbJbpmKeyIVersio(subProcessName, version);
            	if (definicioProces != null && !definicioProces.getEntorn().getId().equals(entorn.getId()))
            		throw new JpdlException("sub-process not accesible: " + subProcessElement.asXML());
            }
            // select that exact process definition as the subprocess definition
            subProcessDefinition = jbpmContext.getGraphSession().findProcessDefinition(subProcessName, version);

          } catch (NumberFormatException e) {
            throw new JpdlException("version in process-state was not a number: " + subProcessElement.asXML());
          }
          
        } else { // if only the name is specified
        	EntornDto entorn = Jbpm3HeliumBridge.getInstance().getEntornActual();
        	if (entorn != null & entorn.getId() != null) {
        		try {
	        		DefinicioProcesDto definicioProces = Jbpm3HeliumBridge.getInstance().getDarreraVersioAmbEntornIJbpmKey(
	            			entorn.getId(),
	            			subProcessName);
	            	if (definicioProces != null && !definicioProces.getEntorn().getId().equals(entorn.getId()))
	            		throw new JpdlException("sub-process not accesible: " + subProcessElement.asXML());
        		} catch (EntornNotFoundException ex) {
        			throw new JpdlException("no s'ha trobat l'entorn (id=" + entorn.getId() + ")");
        		}
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
