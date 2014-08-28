/**
 * 
 */
package net.conselldemallorca.helium.v3.core.api.exception;

import net.conselldemallorca.helium.v3.core.api.dto.PermisTipusEnumDto;

/**
 * Excepció que es llança quan l'usuari no te permisos per accedir
 * a l'objecte especificat.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@SuppressWarnings("serial")
public class NotAllowedException extends RuntimeException {

	private Object objectId;
	private Class<?> objectClass;
	private PermisTipusEnumDto permisTipus;

	public NotAllowedException(
			Object objectId,
			Class<?> objectClass,
			PermisTipusEnumDto permisTipus) {
		super();
		this.objectId = objectId;
		this.objectClass = objectClass;
		this.permisTipus = permisTipus;
	}

	public Object getObjectId() {
		return objectId;
	}
	public Class<?> getObjectClass() {
		return objectClass;
	}

	public PermisTipusEnumDto getPermisTipus() {
		return permisTipus;
	}

	public String getObjectInfo() {
		StringBuilder sb = new StringBuilder();
		if (objectClass != null)
			sb.append(objectClass.getClass().getName());
		else
			sb.append("null");
		sb.append("#");
		if (objectId != null)
			sb.append(objectId.toString());
		else
			sb.append("null");
		return sb.toString();
	}

}
