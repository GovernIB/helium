package org.jbpm.graph.node;

import net.conselldemallorca.helium.core.model.dao.DaoProxy;
import net.conselldemallorca.helium.core.model.hibernate.DefinicioProces;
import net.conselldemallorca.helium.core.util.EntornActual;

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
            Long entornId = EntornActual.getEntornId();
            if (entornId != null) {
            	DefinicioProces definicioProces = DaoProxy.getInstance().getDefinicioProcesDao().findAmbJbpmKeyIVersio(subProcessName, version);
            	if (definicioProces != null && !definicioProces.getEntorn().getId().equals(entornId))
            		throw new JpdlException("sub-process not accesible: " + subProcessElement.asXML());
            }
            // select that exact process definition as the subprocess definition
            subProcessDefinition = jbpmContext.getGraphSession().findProcessDefinition(subProcessName, version);

          } catch (NumberFormatException e) {
            throw new JpdlException("version in process-state was not a number: " + subProcessElement.asXML());
          }
          
        } else { // if only the name is specified
        	Long entornId = EntornActual.getEntornId();
            if (entornId != null) {
            	DefinicioProces definicioProces = DaoProxy.getInstance().getDefinicioProcesDao().findDarreraVersioAmbEntornIJbpmKey(entornId, subProcessName);
            	if (definicioProces != null && !definicioProces.getEntorn().getId().equals(entornId))
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
