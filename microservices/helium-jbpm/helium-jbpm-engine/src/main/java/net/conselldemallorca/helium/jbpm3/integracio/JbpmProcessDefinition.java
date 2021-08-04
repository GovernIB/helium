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
import java.util.Map;
import java.util.zip.ZipInputStream;


/**
 * Una definició de procés multiversió
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class JbpmProcessDefinition implements WProcessDefinition {

	private String deploymentId;
	private String id;
	private String key;
	private String name;
	private int version;
	private String category;

//	private ProcessDefinition processDefinition;

	public JbpmProcessDefinition(ProcessDefinition processDefinition) {
//		this.processDefinition = processDefinition;
		deploymentId = new Long(processDefinition.getId()).toString();
		id = new Long(processDefinition.getId()).toString();
		key = processDefinition.getName();
		name = processDefinition.getName();
		version = processDefinition.getVersion();
	}

//	@Override
//	public String getId() {
//		return new Long(processDefinition.getId()).toString();
//	}
//
//	@Override
//	public String getKey() {
//		return processDefinition.getName();
//	}
//
//	@Override
//	public String getName() {
//		return processDefinition.getName();
//	}
//
//	@Override
//	public int getVersion() {
//		return processDefinition.getVersion();
//	}
//
//	@Override
//	public String getDeploymentId() {
//		return null;
//	}
//
//	@Override
//	public String getCategory() {
//		return null;
//	}
//
//	public ProcessDefinition getProcessDefinition() {
//		return processDefinition;
//	}
//	public void setProcessDefinition(ProcessDefinition processDefinition) {
//		this.processDefinition = processDefinition;
//	}
//
//	@Override
//	@SuppressWarnings({ "unchecked", "rawtypes" })
//	public List<WProcessDefinition> getProcessDefinitions() {
//		List pdl = new ArrayList();
//		pdl.add(processDefinition);
//		return pdl;
//	}

//	@Override
//	public WProcessDefinition parse(ZipInputStream zipInputStream ) throws Exception {
//		ProcessDefinition processDefinition = ProcessDefinition.parseParZipInputStream(zipInputStream);
//		JbpmProcessDefinition result = new JbpmProcessDefinition(processDefinition);
//		return result;
//	}
//
//
//	@Override
//	@SuppressWarnings("unchecked")
//	public Map<String, byte[]> getFiles() {
//		return this.getProcessDefinition().getFileDefinition().getBytesMap();
//	}
}
