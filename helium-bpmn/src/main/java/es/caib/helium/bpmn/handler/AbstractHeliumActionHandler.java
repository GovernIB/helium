package es.caib.helium.bpmn.handler;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import es.caib.helium.bpmn.api.HeliumApi;
import es.caib.helium.bpmn.exception.BpmnException;
import es.caib.helium.bpmn.exception.HeliumHandlerException;
import es.caib.helium.bpmn.integracio.HeliumBridge;
import es.caib.helium.bpmn.model.TerminiInfo;
import org.flowable.common.engine.api.delegate.Expression;
import org.flowable.engine.delegate.DelegateExecution;

/**
 * Handler base amb accés a la funcionalitat de Helium
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
public abstract class AbstractHeliumActionHandler implements HeliumBpmnHandler {
	protected DelegateExecution execution;
	@Override
	public void execute(DelegateExecution execution) {
		try {
			this.execution = execution;
			this.execute(buildHeliumApi());
		} catch (BpmnException e) {
			throw new RuntimeException(e);
		}
	}

	private HeliumApi buildHeliumApi() {
		String processId = null;
		if(execution != null)
			processId = execution.getProcessInstanceId();

		return HeliumBridge
				.getInstance()
				.getBpmnHeliumService()
				.buildHeliumApi(processId, null);
	}

	protected TerminiInfo getTerminiIniciatAmbCodi(HeliumApi executionContext, String codi) throws BpmnException {
		return HeliumBridge.getInstanceService().getTerminiIniciatAmbCodi(executionContext, codi);
	}

	protected String getProcessInstanceId(HeliumApi executionContext) {
		return executionContext.getProcessInstance().getId();
	}

	protected String getTaskInstanceId(HeliumApi executionContext) {
		return executionContext.getTaskInstance().getId();
	}

	protected Date getVariableComData(HeliumApi executionContext,
	                                  String variable) throws BpmnException {
		Object obj = executionContext.getVariable(variable);
		if (obj == null)
			throw new BpmnException("No s'ha trobat l'objecte de tipus (" + Date.class.getSimpleName() + ") amb identificador (" + variable + ")");
		if (obj instanceof Date)
			return (Date) obj;
		throw new BpmnException("La variable amb el codi '" + variable + "' no és de tipus Date");
	}

	protected String getValue(Expression expression) {
		if(expression == null)
			return null;
		Object value = expression.getValue(this.execution);
		if(value == null)
			return null;
		return (String) value;
	}

	protected Boolean getValueAsBoolean(Expression expression) {
		String value = getValue(expression);
		if(value == null || value.trim().isEmpty())
			return false;
		return Boolean.valueOf(value);
	}

	protected Date getValueAsDate(Expression expression) {
		String value = getValue(expression);
		if (value == null || value.isEmpty())
			return null;
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
			return sdf.parse(value);
		} catch (ParseException ex) {
			throw new HeliumHandlerException("Couldn't parse date " + value);
		}
	}

	protected Integer getValueAsInteger(Expression expression) {
		String value = getValue(expression);
		if (value == null || value.isEmpty())
			return null;
		try {
    		return Integer.valueOf(value);
		} catch (NumberFormatException ex) {
			throw new NumberFormatException("Couldn't parse date " + value);
		}

	}

}
