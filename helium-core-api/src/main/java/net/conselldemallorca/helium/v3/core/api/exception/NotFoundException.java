/**
 * 
 */
package net.conselldemallorca.helium.v3.core.api.exception;

/**
 * Excepció que es llança quan l'objecte especificat no existeix.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@SuppressWarnings("serial")
public class NotFoundException extends RuntimeException {

	private Object objectId;
	private Class<?> objectClass;
	
	public NotFoundException(
			Object objectId,
			Class<?> objectClass) {
		super();
		this.objectId = objectId;
		this.objectClass = objectClass;
	}
	public NotFoundException(
			Class<?> objectClass) {
		super();
		this.objectClass = objectClass;
	}

	public Object getObjectId() {
		return objectId;
	}

	public Class<?> getObjectClass() {
		return objectClass;
	}

	public String getObjectInfo() {
		StringBuilder sb = new StringBuilder();
		if (objectClass != null)
			sb.append(objectClass.getClass().getName());
		else
			sb.append("null");
		if (objectId != null) {
			sb.append("#");
			sb.append(objectId.toString());
		}
		return sb.toString();
	}

}
