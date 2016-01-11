/**
 * 
 */
package net.conselldemallorca.helium.jbpm3.integracio;

/**
 * Excepció que indica problemes en l'execució d'un handler jBPM
 * tant intern de Helium com d'una deficnició de procés.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@SuppressWarnings("serial")
public class ExecucioHandlerException extends RuntimeException {

	private Long expedientId;
	private Long expedientTipusId;
	private Long processInstanceId;
	private Long taskInstanceId;
	private Long tokenId;
	private String className;
	private String methodName;
	private String fileName;
	private int lineNumber;

	public ExecucioHandlerException(
			Long expedientId,
			Long expedientTipusId,
			Long processInstanceId,
			Long taskInstanceId,
			Long tokenId,
			String className,
			String methodName,
			String fileName,
			int lineNumber,
			String message,
			Throwable cause) {
		super(message, cause);
		this.expedientId = expedientId;
		this.expedientTipusId = expedientTipusId;
		this.processInstanceId = processInstanceId;
		this.taskInstanceId = taskInstanceId;
		this.tokenId = tokenId;
		this.className = className;
		this.fileName = fileName;
		this.methodName = methodName;
		this.lineNumber = lineNumber;
	}

	public Long getExpedientId() {
		return expedientId;
	}
	public void setExpedientId(Long expedientId) {
		this.expedientId = expedientId;
	}
	public Long getExpedientTipusId() {
		return expedientTipusId;
	}
	public void setExpedientTipusId(Long expedientTipusId) {
		this.expedientTipusId = expedientTipusId;
	}
	public Long getProcessInstanceId() {
		return processInstanceId;
	}
	public void setProcessInstanceId(Long processInstanceId) {
		this.processInstanceId = processInstanceId;
	}
	public Long getTaskInstanceId() {
		return taskInstanceId;
	}
	public void setTaskInstanceId(Long taskInstanceId) {
		this.taskInstanceId = taskInstanceId;
	}
	public Long getTokenId() {
		return tokenId;
	}
	public void setTokenId(Long tokenId) {
		this.tokenId = tokenId;
	}
	public String getClassName() {
		return className;
	}
	public void setClassName(String className) {
		this.className = className;
	}
	public String getMethodName() {
		return methodName;
	}
	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public int getLineNumber() {
		return lineNumber;
	}
	public void setLineNumber(int lineNumber) {
		this.lineNumber = lineNumber;
	}

	public String toString() {
		String expId = (expedientId != null) ? expedientId.toString() : "*expedient*en*creacio*";
		return "Error al executar un handler de l'expedient (" +
				"expedientId=" + expId + ", " +
				"expedientTipusId=" + expedientTipusId + ", " +
				"processInstanceId=" + processInstanceId + ", " +
				"taskInstanceId=" + taskInstanceId + ", " +
				"tokenId=" + tokenId + ")";
	}

}
