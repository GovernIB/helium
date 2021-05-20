/**
 * 
 */
package es.caib.helium.jbpm3.handlers;

import java.util.HashSet;
import java.util.Set;

import net.conselldemallorca.helium.jbpm3.handlers.SwimlaneCopiarPareHandlerInterface;
import org.jbpm.JbpmException;
import org.jbpm.graph.exe.ExecutionContext;
import org.jbpm.graph.exe.Token;
import org.jbpm.taskmgmt.def.Swimlane;
import org.jbpm.taskmgmt.exe.PooledActor;
import org.jbpm.taskmgmt.exe.SwimlaneInstance;
import org.jbpm.taskmgmt.exe.TaskInstance;
import org.jbpm.taskmgmt.exe.TaskMgmtInstance;

/**
 * Handler per a copiar un swimlane de l'expedient actual a un altre
 * expedient destí.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@SuppressWarnings("serial")
public class SwimlaneCopiarPareHandler extends AbstractHeliumActionHandler implements SwimlaneCopiarPareHandlerInterface {

	private String swimlaneNom;
	private String varSwimlaneCodi;

	public void execute(ExecutionContext executionContext) throws Exception {
		Token tokenPare = executionContext.getProcessInstance().getSuperProcessToken();
		if (tokenPare != null) {
			String sn = (String)getValorOVariable(executionContext, swimlaneNom, varSwimlaneCodi);
			// Obtenim el swimlane del pare
			TaskMgmtInstance taskMgmtInstancePare = tokenPare.getProcessInstance().getTaskMgmtInstance();
			SwimlaneInstance sip = taskMgmtInstancePare.getSwimlaneInstance(sn);
			if (sip == null)
				throw new JbpmException("No s'ha trobat cap instància de swimlane al procés pare amb el nom " + sn);
			
			// Obtenim els actors
			String actorId = sip.getActorId();
			Set<PooledActor> pooledActors = new HashSet<PooledActor>(); 
			for (PooledActor actor: sip.getPooledActors())
				pooledActors.add(actor);
			
			// Obtenim el swimlane del fill
			TaskMgmtInstance taskMgmtInstanceFill = executionContext.getProcessInstance().getTaskMgmtInstance();
			SwimlaneInstance sif = taskMgmtInstanceFill.getSwimlaneInstance(sn);
			Swimlane sw = executionContext.getProcessInstance().getProcessDefinition().getTaskMgmtDefinition().getSwimlane(sn);
			
			// Si no existeix, cream una instància del swimlane
			if (sif == null) {
				if (sw == null)
					throw new JbpmException("No s'ha trobat cap swimlane a la definició de procés fill amb el nom " + sn);
				sif = taskMgmtInstanceFill.getInitializedSwimlaneInstance(executionContext, sw);
			}
			
			sif.setActorId(actorId);
			sif.setPooledActors(pooledActors);
			sif.setTaskMgmtInstance(taskMgmtInstanceFill);
			
			if (taskMgmtInstanceFill.getTaskInstances() != null)
				for (TaskInstance ti: taskMgmtInstanceFill.getTaskInstances()){
					if (ti.getSwimlaneInstance().equals(sif)){
						ti.copySwimlaneInstanceAssignment(sif);
					}
				}
		} else {
			throw new JbpmException("Aquest procés(" + executionContext.getProcessInstance().getId() + ") no té cap procés pare");
		}
	}

	public String getSwimlaneNom() {
		return swimlaneNom;
	}
	public void setSwimlaneNom(String swimlaneNom) {
		this.swimlaneNom = swimlaneNom;
	}
	public String getVarSwimlaneCodi() {
		return varSwimlaneCodi;
	}
	public void setVarSwimlaneCodi(String varSwimlaneCodi) {
		this.varSwimlaneCodi = varSwimlaneCodi;
	}
}
