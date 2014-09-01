/**
 * 
 */
package net.conselldemallorca.helium.v3.core.api.exception;

import net.conselldemallorca.helium.v3.core.api.dto.PermisTipusEnumDto;

/**
 * Excepció que es llança quan l'objecte que s'intenta accedir
 * no es troba en l'estat adequat.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@SuppressWarnings("serial")
public class IllegalStateException extends RuntimeException {

	private Object objectId;
	private Class<?> objectClass;
	private String estatRequerit;

	public IllegalStateException(
			Object objectId,
			Class<?> objectClass,
			String estatRequerit) {
		super();
		this.objectId = objectId;
		this.objectClass = objectClass;
		this.estatRequerit = estatRequerit;
	}

	public Object getObjectId() {
		return objectId;
	}
	public Class<?> getObjectClass() {
		return objectClass;
	}
	public String getEstatRequerit() {
		return estatRequerit;
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
