/**
 * 
 */
package net.conselldemallorca.helium.jbpm3.handlers;

import java.util.List;

import org.jbpm.graph.def.ActionHandler;

/**
 * Interfície a implementar per a retrocedir un expedient
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface AccioExternaRetrocedirHandler extends ActionHandler {

	void retrocedir(List<String> parametres) throws Exception;

}
