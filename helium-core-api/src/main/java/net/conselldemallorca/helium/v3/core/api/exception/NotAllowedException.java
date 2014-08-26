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

	private Long objectId;
	private Class<?> objectClass;
	private PermisTipusEnumDto permisTipus;
	
	public NotAllowedException(
			Long objectId,
			Class<?> objectClass,
			PermisTipusEnumDto permisTipus) {
		super();
		this.objectId = objectId;
		this.objectClass = objectClass;
		this.permisTipus = permisTipus;
	}

	public Long getObjectId() {
		return objectId;
	}
	public Class<?> getObjectClass() {
		return objectClass;
	}

	public PermisTipusEnumDto getPermisTipus() {
		return permisTipus;
	}

}
