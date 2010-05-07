/**
 * 
 */
package net.conselldemallorca.helium.jbpm3.handlers;

import net.conselldemallorca.helium.model.hibernate.TerminiIniciat;

import org.jbpm.JbpmException;
import org.jbpm.graph.exe.ExecutionContext;
import org.jbpm.job.Timer;
import org.jbpm.scheduler.SchedulerService;
import org.jbpm.scheduler.def.CreateTimerAction;
import org.jbpm.svc.Services;

/**
 * Handler per configurar un timer donat un termini iniciat.
 * 
 * @author Josep Gayà <josepg@limit.es>
 */
@SuppressWarnings("serial")
public class TimerConfigurarAmbTerminiHandler extends CreateTimerAction {

	private String terminiCodi;
	private String varTerminiCodi;



	public void execute(ExecutionContext executionContext) throws Exception {
		TerminiIniciarHandler handler = new TerminiIniciarHandler();
		TerminiIniciat termini = handler.getTerminiIniciatAmbCodi(
				executionContext,
				(String)getValorOVariable(executionContext, terminiCodi, varTerminiCodi));
		if (termini != null) {
			Timer timer = createTimer(executionContext);
			timer.setDueDate(termini.getDataFiAmbAturadaActual());
			SchedulerService schedulerService = (SchedulerService)Services.getCurrentService(Services.SERVICENAME_SCHEDULER);
			schedulerService.createTimer(timer);
			handler.getTerminiService().setTimerName(
					termini.getId(),
					getTimerName());
		} else {
			throw new JbpmException("No existeix cap termini iniciat amb aquest codi '" + terminiCodi + "'");
		}
	}

	public void setTerminiCodi(String terminiCodi) {
		this.terminiCodi = terminiCodi;
	}



	protected Object getValorOVariable(ExecutionContext executionContext, Object value, String var) {
		if (value != null)
			return value;
		if (var != null)
			return executionContext.getVariable(var);
		return null;
	}

}
