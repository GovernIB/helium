package es.caib.helium.logic.bpmn;

import es.caib.helium.bpmn.api.HeliumApi;
import es.caib.helium.bpmn.engine.WTaskInstance;
import es.caib.helium.bpmn.exception.BpmnException;
import es.caib.helium.bpmn.exception.HeliumHandlerException;
import es.caib.helium.bpmn.handler.AbstractHeliumActionHandler;
import es.caib.helium.bpmn.model.TerminiIniciatInfo;
import lombok.Setter;

/**
 * Handler per configurar una tasca o un timer donat un termini iniciat.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Setter
public class ConfigurarAmbTerminiHandler extends AbstractHeliumActionHandler implements es.caib.helium.disseny.handler.ConfigurarAmbTerminiHandler {
	private String terminiCodi;

	@Override
	public void execute(HeliumApi executionContext) throws HeliumHandlerException, BpmnException {
		// TODO: S'ha de implementar la amb uns substitució de org.jbpm.job.Timer
//		TerminiIniciatInfo terminiIniciat = executionContext.getTerminiIniciatAmbProcessInstanceITerminiCodi(
//			getProcessInstanceId(executionContext),
//			terminiCodi);
//		if (terminiIniciat != null) {
//			Timer timer = executionContext.getTimer();
//			if (timer != null)
//				timer.setDueDate(terminiIniciat.getDataFiAmbAturadaActual());
//			WTaskInstance taskInstance = executionContext.getTaskInstance();
//			if (taskInstance != null)
//				taskInstance.setDueDate(terminiIniciat.getDataFiAmbAturadaActual());
//			executionContext.configurarTerminiIniciatAmbDadesBpmn(
//				terminiIniciat.getId(),
//				(taskInstance != null) ? new Long(taskInstance.getId()).toString() : null,
//				(timer != null) ? new Long(timer.getId()) : null);
//		} else {
//			throw new BpmnException("No s'ha trobat cap termini iniciat (codi=" + terminiCodi + ")");
//		}
	}
}
