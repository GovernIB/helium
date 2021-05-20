/**
 * 
 */
package es.caib.helium.jbpm3.integracio;

import lombok.Getter;
import lombok.Setter;

/**
 * Excepció que indica problemes en l'execució d'un handler jBPM
 * tant intern de Helium com d'una deficnició de procés.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@SuppressWarnings("serial")
@Getter @Setter
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
