/**
 * 
 */
package net.conselldemallorca.helium.model.hibernate;

import java.io.Serializable;

/**
 * Interfície base per les entitats d'Hibernate
 * 
 * @author Josep Gayà <josepg@limit.es>
 */
public interface GenericEntity<ID extends Serializable> {

	public ID getId();

}
