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

	private Long processInstanceId;
	private Long tokenId;
	private String className;
	private String methodName;
	private String fileName;
	private int lineNumber;

	public ExecucioHandlerException(
			Long processInstanceId,
			Long tokenId,
			String className,
			String methodName,
			String fileName,
			int lineNumber,
			String message,
			Throwable cause) {
		super(message, cause);
		this.processInstanceId = processInstanceId;
		this.tokenId = tokenId;
		this.className = className;
		this.fileName = fileName;
		this.methodName = methodName;
		this.lineNumber = lineNumber;
	}

	public Long getProcessInstanceId() {
		return processInstanceId;
	}
	public void setProcessInstanceId(Long processInstanceId) {
		this.processInstanceId = processInstanceId;
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
		String id;
		if (processInstanceId != null)
			id = processInstanceId + ", " + tokenId;
		else
			id = "*expedient*en*creacio*";
		if (getCause() != null)
			return "Error al executar un handler de l'expedient [" + id + "]: " + getCause().getClass().getName() + ": " + getCause().getMessage();
		else
			return "Error al executar un handler de l'expedient [" + id + "]";
	}

}
