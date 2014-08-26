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

	private Long objectId;
	private Class<?> objectClass;
	
	public NotFoundException(
			Long objectId,
			Class<?> objectClass) {
		super();
		this.objectId = objectId;
		this.objectClass = objectClass;
	}

	public Long getObjectId() {
		return objectId;
	}

	public Class<?> getObjectClass() {
		return objectClass;
	}

}
