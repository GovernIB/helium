/**
 * 
 */
package net.conselldemallorca.helium.jbpm3.integracio;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.conselldemallorca.helium.api.service.WDeployment;
import net.conselldemallorca.helium.api.service.WProcessDefinition;
import org.jbpm.graph.def.ProcessDefinition;

import java.util.ArrayList;
import java.util.List;


/**
 * Una definició de procés multiversió
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class JbpmDeployment implements WDeployment {

	private String id;
	private String name;
	private String category;
	private List<WProcessDefinition> processDefinitions;

	public JbpmDeployment(ProcessDefinition processDefinition) {
		id = new Long(processDefinition.getId()).toString();
		name = processDefinition.getName();
		processDefinitions = new ArrayList<WProcessDefinition>();
		processDefinitions.add(new JbpmProcessDefinition(processDefinition));
	}

}
