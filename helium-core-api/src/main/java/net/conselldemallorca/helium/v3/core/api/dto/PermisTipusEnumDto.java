/**
 * 
 */
package net.conselldemallorca.helium.v3.core.api.dto;

import java.io.Serializable;

/**
 * Tipus de principal per als permisos.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public enum PermisTipusEnumDto implements Serializable {
	READ,
	WRITE,
	DELETE,
	SUPERVISION,
	REASSIGNMENT,
	ADMINISTRATION
}
