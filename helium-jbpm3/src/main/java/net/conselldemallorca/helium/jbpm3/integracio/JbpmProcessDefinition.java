/**
 * 
 */
package net.conselldemallorca.helium.jbpm3.integracio;

import java.util.ArrayList;
import java.util.List;

import org.jbpm.graph.def.ProcessDefinition;

import net.conselldemallorca.helium.core.api.WDeployment;
import net.conselldemallorca.helium.core.api.WProcessDefinition;



/**
 * Una definició de procés multiversió
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class JbpmProcessDefinition implements WProcessDefinition, WDeployment {

	private ProcessDefinition processDefinition;

	public JbpmProcessDefinition(ProcessDefinition processDefinition) {
		this.processDefinition = processDefinition;
	}

	
	@Override
	public String getId() {
		return new Long(processDefinition.getId()).toString();
	}
	
	@Override
	public String getKey() {
		return processDefinition.getName();
	}
	
	@Override
	public String getName() {
		return processDefinition.getName();
	}
	
	@Override
	public int getVersion() {
		return processDefinition.getVersion();
	}

	@Override
	public String getDeploymentId() {
		return null;
	}

	@Override
	public String getCategory() {
		return null;
	}
	
	@Override
	public ProcessDefinition getProcessDefinition() {
		return processDefinition;
	}
	public void setProcessDefinition(ProcessDefinition processDefinition) {
		this.processDefinition = processDefinition;
	}

	@Override
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public List<WProcessDefinition> getProcessDefinitions() {
		List pdl = new ArrayList();
		pdl.add(processDefinition);
		return pdl;
	}

}
