/**
 * 
 */
package net.conselldemallorca.helium.v3.core.api.dto;

import java.io.Serializable;

/**
 * Interfície base per les entitats d'Hibernate
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface GenericEntityDto<ID extends Serializable> {

	public ID getId();

}
