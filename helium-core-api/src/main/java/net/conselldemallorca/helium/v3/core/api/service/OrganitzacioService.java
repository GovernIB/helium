package net.conselldemallorca.helium.v3.core.api.service;

import java.util.List;;

/**
 * Servei per a gestionar la informació de les taules JBPM_ID_*.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface OrganitzacioService {

	/**
	 * Retorna els diferents grups de la taula JBPM_ID_GROUP.
	 * 
	 * @return la llista amb els noms dels grups.
	 */
	List<String> findDistinctJbpmGroupNames();

}