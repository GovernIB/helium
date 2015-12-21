/**
 * 
 */
package net.conselldemallorca.helium.v3.core.api.exception;

/**
 * Excepció originada al processa accions jBPM.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@SuppressWarnings("serial")
public class JbpmException extends RuntimeException {

	private Long expedientId;
	private String expedientIdentificador;
	private Long expedientTipusId;
	private String processInstanceId;
	private String taskInstanceId;

	public JbpmException(
			Long expedientId,
			String expedientIdentificador,
			Long expedientTipusId,
			String processInstanceId,
			Throwable cause) {
		super(cause);
		this.expedientId = expedientId;
		this.expedientIdentificador = expedientIdentificador;
		this.expedientTipusId = expedientTipusId;
		this.processInstanceId = processInstanceId;
	}

	public JbpmException(
			Long expedientId,
			String expedientIdentificador,
			Long expedientTipusId,
			String processInstanceId,
			String taskInstanceId,
			Throwable cause) {
		super(cause);
		this.expedientId = expedientId;
		this.expedientIdentificador = expedientIdentificador;
		this.expedientTipusId = expedientTipusId;
		this.processInstanceId = processInstanceId;
		this.taskInstanceId = taskInstanceId;
	}

	public Long getExpedientId() {
		return expedientId;
	}
	public String getExpedientIdentificador() {
		return expedientIdentificador;
	}
	public Long getExpedientTipusId() {
		return expedientTipusId;
	}
	public String getProcessInstanceId() {
		return processInstanceId;
	}
	public String getTaskInstanceId() {
		return taskInstanceId;
	}

	@Override
	public String toString() {
		return "JbpmException (" +
				"expedientId=" + expedientId + ", " +
				"expedientIdentificador=" + expedientIdentificador + ", " +
				"expedientTipusId=" + expedientTipusId + ", " +
				"processInstanceId=" + processInstanceId + 
				((taskInstanceId != null) ? ", taskInstanceId=" + taskInstanceId + ")" : "");
	}

}
