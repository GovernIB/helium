/**
 * 
 */
package es.caib.helium.logic.intf.dto;

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
