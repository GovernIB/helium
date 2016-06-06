/**
 * 
 */
package net.conselldemallorca.helium.v3.core.api.exception;

import org.apache.commons.lang.exception.ExceptionUtils;

/**
 * Excepció que es llança si hi ha algun error crindant al plugin.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@SuppressWarnings("serial")
public class TramitacioHandlerException extends HeliumException {
	
	private Class<?> classeHandler;
	private Long processInstanceId;
	private Long taskInstanceId;
	private Long tokenId;
	private String className;
	private String methodName;
	private String fileName;
	private int lineNumber;
	
	public TramitacioHandlerException(
			Long entornId, 
			String entornCodi,
			String entornNom, 
			Long expedientId, 
			String expedientTitol,
			String expedientNumero, 
			Long expedientTipusId,
			String expedientTipusCodi, 
			String expedientTipusNom,
			Long processInstanceId,
			Long taskInstanceId,
			Long tokenId,
			String className,
			String methodName,
			String fileName,
			int lineNumber,
			String message,
			Throwable cause) {
		super(entornId, 
			  entornCodi, 
			  entornNom, 
			  expedientId, 
			  expedientTitol,
			  expedientNumero, 
			  expedientTipusId, 
			  expedientTipusCodi,
			  expedientTipusNom,
			  getHandlerMessage(
					  	processInstanceId,
					  	taskInstanceId,
					  	tokenId,
						className,
						methodName,
						fileName,
						lineNumber,
						message,
						cause),
			  cause);
		this.processInstanceId = processInstanceId;
		this.taskInstanceId = taskInstanceId;
		this.tokenId = tokenId;
		this.className = className;
		this.fileName = fileName;
		this.methodName = methodName;
		this.lineNumber = lineNumber;
	}
	
	private static String getHandlerMessage(
			Long processInstanceId,
			Long taskInstanceId,
			Long tokenId,
			String className,
			String methodName,
			String fileName,
			int lineNumber,
			String message,
			Throwable cause) {
		String finalMessage = "Error en l'execució del Handler. " + message + ".";
		if (processInstanceId != null)
			finalMessage += " PID: " + processInstanceId + ".";
		if (taskInstanceId != null)
			finalMessage += " Task Instance: " + taskInstanceId + ".";
		if (tokenId != null)
			finalMessage += " Token: " + tokenId + ".";
		if (className != null)
			finalMessage += " Handler: " + className + ".";
		if (methodName != null)
			finalMessage += " Mètode: " + methodName + ".";
		if (fileName != null)
			finalMessage += " Fitxer: " + fileName + ".";
		finalMessage += " Num. línea: " + lineNumber + ".";
		
		finalMessage += cause.getMessage();
		
		return finalMessage;
	}
	
	public String getPublicMessage() {
		return "Error de tramitació en l'execució del Handler. " + ExceptionUtils.getRootCauseMessage(this.getCause());
	}

	public Class<?> getClasseHandler() {
		return classeHandler;
	}

	public Long getProcessInstanceId() {
		return processInstanceId;
	}

	public Long getTaskInstanceId() {
		return taskInstanceId;
	}

	public Long getTokenId() {
		return tokenId;
	}

	public String getClassName() {
		return className;
	}

	public String getMethodName() {
		return methodName;
	}

	public String getFileName() {
		return fileName;
	}

	public int getLineNumber() {
		return lineNumber;
	}
}
