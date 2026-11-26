package es.caib.helium.bpmn.handler;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import es.caib.helium.bpmn.api.HeliumApi;
import es.caib.helium.bpmn.exception.BpmnException;
import es.caib.helium.bpmn.exception.HeliumHandlerException;
import es.caib.helium.bpmn.model.TerminiInfo;

/**
 * Handler base amb accés a la funcionalitat de Helium.
 *
 * <p>No depèn de cap motor de workflow: els paràmetres arriben ja resolts com a
 * {@code String} a través dels setters del handler.</p>
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
public abstract class AbstractHeliumActionHandler implements HeliumBpmnHandler {

	public static final String FORMAT_DATA = "dd/MM/yyyy";

	/** Termini iniciat a la instància de procés del handler, o null si no s'ha iniciat (veure {@link HeliumApi#getTerminiIniciat}). */
	protected TerminiInfo getTerminiIniciatAmbCodi(HeliumApi executionContext, String codi) throws BpmnException {
		return executionContext.getTerminiIniciat(codi);
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

	protected static boolean isBuit(String value) {
		return value == null || value.trim().isEmpty();
	}

	protected Boolean asBoolean(String value) {
		if (isBuit(value))
			return false;
		return Boolean.valueOf(value.trim());
	}

	protected Date asDate(String value) {
		if (isBuit(value))
			return null;
		try {
			return new SimpleDateFormat(FORMAT_DATA).parse(value.trim());
		} catch (ParseException ex) {
			throw new HeliumHandlerException("No s'ha pogut convertir a data el valor " + value);
		}
	}

	protected Integer asInteger(String value) {
		if (isBuit(value))
			return null;
		try {
			return Integer.valueOf(value.trim());
		} catch (NumberFormatException ex) {
			throw new HeliumHandlerException("No s'ha pogut convertir a enter el valor " + value);
		}
	}

}
