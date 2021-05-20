package es.caib.helium.api.exception;

@SuppressWarnings("serial")
public class NoTrobatException extends RuntimeException {
	private Class<?> objectType;
	private Object objectId;
	
	public NoTrobatException(Class<?> objectType) {
		super("No s'ha trobat l'objecte de tipus " + objectType.getName());
		this.objectType = objectType;
	}
	
	public NoTrobatException(Class<?> objectType, Object objectId) {
		super("No s'ha trobat l'objecte de tipus (" + objectType.getSimpleName() + ") amb identificador (" + objectId + ")");
		this.objectType = objectType;
		this.objectId = objectId;
	}

	public Class<?> getObjectType() {
		return objectType;
	}

	public Object getObjectId() {
		return objectId;
	}

}
