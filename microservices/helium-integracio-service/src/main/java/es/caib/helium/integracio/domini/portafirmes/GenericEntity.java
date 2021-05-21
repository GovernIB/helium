package es.caib.helium.integracio.domini.portafirmes;

import java.io.Serializable;

/**
 * Interfície base per les entitats d'Hibernate
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface GenericEntity<ID extends Serializable> {

	public ID getId();

}
