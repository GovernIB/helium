package net.conselldemallorca.helium.v3.core.api.exception;

@SuppressWarnings("serial")
public class NoTrobatException extends RuntimeException {
	private Class<?> objectType;
	private Long objectId;
	
	public NoTrobatException(Class<?> objectType, Long objectId) {
		super();
		this.objectType = objectType;
		this.objectId = objectId;
	}

	public Class<?> getObjectType() {
		return objectType;
	}

	public Long getObjectId() {
		return objectId;
	}

}
