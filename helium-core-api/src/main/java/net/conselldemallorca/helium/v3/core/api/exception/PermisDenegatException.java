package net.conselldemallorca.helium.v3.core.api.exception;

import org.springframework.security.acls.model.Permission;

@SuppressWarnings("serial")
public class PermisDenegatException extends RuntimeException {
	private Object objectId;
	private Class<?> objectType;
	private Permission[] permisosRequerits;
	private Permission[] permisosDisponibles;
	
	public PermisDenegatException(Object objectId, Class<?> objectType,
			Permission[] permisosRequerits, Permission[] permisosDisponibles) {
		super(getPermissionMessage(objectId, objectType, permisosRequerits));
		
		this.objectId = objectId;
		this.objectType = objectType;
		this.permisosRequerits = permisosRequerits;
		this.permisosDisponibles = permisosDisponibles;
	}
	
	public PermisDenegatException(Object objectId, Class<?> objectType,
			Permission[] permisosRequerits) {
		super(getPermissionMessage(objectId, objectType, permisosRequerits));
		this.objectId = objectId;
		this.objectType = objectType;
		this.permisosRequerits = permisosRequerits;
	}
	
	private static String getPermissionMessage (Object objectId, Class<?> objectType, Permission[] permisosRequerits) {
		String message = "Permís denegat per l'objecte de tipus (" + objectType.getName() + ") amb ID (" + objectId + "). Es requereixen els següents permisos:";
		for (Permission permis: permisosRequerits) {
			message += permis.toString();
		}
		return message;
	}

	public Class<?> getObjectType() {
		return objectType;
	}

	public Object getObjectId() {
		return objectId;
	}

	public Permission[] getPermisosRequerits() {
		return permisosRequerits;
	}

	public Permission[] getPermisosDisponibles() {
		return permisosDisponibles;
	}

}
