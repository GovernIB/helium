/**
 * 
 */
package es.caib.helium.jbpm3.handlers;

import es.caib.helium.api.dto.TerminiIniciatDto;
import es.caib.helium.jbpm3.integracio.Jbpm3HeliumBridge;
import net.conselldemallorca.helium.jbpm3.handlers.ConfigurarAmbTerminiHandlerInterface;

import org.jbpm.JbpmException;
import org.jbpm.graph.exe.ExecutionContext;
import org.jbpm.job.Timer;
import org.jbpm.scheduler.def.CreateTimerAction;
import org.jbpm.taskmgmt.exe.TaskInstance;

/**
 * Handler per configurar una tasca o un timer donat un termini iniciat.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@SuppressWarnings("serial")
public class ConfigurarAmbTerminiHandler extends CreateTimerAction implements ConfigurarAmbTerminiHandlerInterface {

	private String terminiCodi;
	private String varTerminiCodi;



	public void execute(ExecutionContext executionContext) throws Exception {
		String tc = (String)getValorOVariable(
				executionContext,
				terminiCodi,
				varTerminiCodi);
		TerminiIniciatDto terminiIniciat = Jbpm3HeliumBridge.getInstanceService().getTerminiIniciatAmbProcessInstanceITerminiCodi(
				new Long(executionContext.getProcessDefinition().getId()).toString(),
				getProcessInstanceId(executionContext),
				tc);
		if (terminiIniciat != null) {
			Timer timer = executionContext.getTimer();
			if (timer != null)
				timer.setDueDate(terminiIniciat.getDataFiAmbAturadaActual());
			TaskInstance taskInstance = executionContext.getTaskInstance();
			if (taskInstance != null)
				taskInstance.setDueDate(terminiIniciat.getDataFiAmbAturadaActual());
			Jbpm3HeliumBridge.getInstanceService().configurarTerminiIniciatAmbDadesWf(
					terminiIniciat.getId(),
					(taskInstance != null) ? new Long(taskInstance.getId()).toString() : null,
					(timer != null) ? new Long(timer.getId()) : null);
		} else {
			throw new JbpmException("No s'ha trobat cap termini iniciat (codi=" + tc + ")");
		}
	}

	public void setTerminiCodi(String terminiCodi) {
		this.terminiCodi = terminiCodi;
	}
	public void setVarTerminiCodi(String varTerminiCodi) {
		this.varTerminiCodi = varTerminiCodi;
	}



	protected Object getValorOVariable(ExecutionContext executionContext, Object value, String var) {
		if (value != null)
			return value;
		if (var != null)
			return executionContext.getVariable(var);
		return null;
	}
	protected String getProcessInstanceId(ExecutionContext executionContext) {
		return new Long(executionContext.getProcessInstance().getId()).toString();
	}

}
