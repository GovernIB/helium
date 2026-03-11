package es.caib.helium.commons.exception;

@SuppressWarnings("serial")
public class TascaNoDisponibleException extends HeliumException {
	private Object objectId;
	
	public TascaNoDisponibleException(Object objectId, String message, Throwable cause) {
		super("La tasca amb identificador (" + objectId + ") no està disponible" + ((message != null && !message.isEmpty()) ? ": " + message : ".") , cause);
		this.setObjectId(objectId);
	}

	public Object getObjectId() {
		return objectId;
	}

	public void setObjectId(Object objectId) {
		this.objectId = objectId;
	}
}
