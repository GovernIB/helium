/**
 * 
 */
package net.conselldemallorca.helium.jbpm3.integracio;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;
import org.jbpm.JbpmContext;
import org.jbpm.command.AbstractGetObjectBaseCommand;
import org.jbpm.graph.exe.ProcessInstance;

/**
 * Command per obtenir la definició de procés de jBPM 3 donat el seu id
 * 
 * @author Josep Gayà <josepg@limit.es>
 */
public class GetProcessInstancesTreeCommand extends AbstractGetObjectBaseCommand {

	private static final long serialVersionUID = -1908847549444051495L;
	private long id;

	public GetProcessInstancesTreeCommand() {}

	public GetProcessInstancesTreeCommand(long id){
		super();
		this.id = id;
	}

	public Object execute(JbpmContext jbpmContext) throws Exception {
		List<ProcessInstance> resposta = new ArrayList<ProcessInstance>();
		afegirProcessInstanceAmbFills(
				jbpmContext.getSession(),
				jbpmContext.getProcessInstance(id),
				resposta);
		return resposta;
	}

	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}

	@Override
	public String getAdditionalToStringInformation() {
	    return "id=" + id;
	}

	//methods for fluent programming
	public GetProcessInstancesTreeCommand id(long id) {
		setId(id);
	    return this;
	}

	@SuppressWarnings("unchecked")
	private void afegirProcessInstanceAmbFills(
			Session session,
			ProcessInstance processInstance,
			List<ProcessInstance> llista) {
		llista.add(retrieveProcessInstance(processInstance));
		// Cerca els subprocessos d'un procés determinat
		List<ProcessInstance> fills = session.getNamedQuery("GraphSession.findSubProcessInstances").
				setEntity("processInstance", processInstance).
				list();
		for (ProcessInstance pi: fills) {
			pi.getProcessDefinition().getName();
			pi.getSuperProcessToken().getNode().getName();
			// Per a cada subprocés també hi afegeix els seus subprocessos
			afegirProcessInstanceAmbFills(session, pi, llista);
		}
	}

}
