/**
 * 
 */
package net.conselldemallorca.helium.jbpm3.handlers;

import net.conselldemallorca.helium.core.model.hibernate.TerminiIniciat;

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
		TerminiIniciarHandler handler = new TerminiIniciarHandler();
		TerminiIniciat termini = handler.getTerminiIniciatAmbCodi(
				executionContext,
				(String)getValorOVariable(executionContext, terminiCodi, varTerminiCodi));
		if (termini != null) {
			Timer timer = executionContext.getTimer();
			if (timer != null)
				timer.setDueDate(termini.getDataFiAmbAturadaActual());
			TaskInstance taskInstance = executionContext.getTaskInstance();
			if (taskInstance != null)
				taskInstance.setDueDate(termini.getDataFiAmbAturadaActual());
			handler.getTerminiService().configurarTerminiIniciatAmbDadesJbpm(
					termini.getId(),
					(taskInstance != null) ? new Long(taskInstance.getId()).toString() : null,
					(timer != null) ? new Long(timer.getId()) : null);
		} else {
			throw new JbpmException("No existeix cap termini iniciat amb aquest codi '" + terminiCodi + "'");
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

}
