/**
 * 
 */
package es.caib.helium.persist.entity;

import java.io.Serializable;

/**
 * Interfície base per les entitats d'Hibernate
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface GenericEntity<ID extends Serializable> {

	public ID getId();

}
