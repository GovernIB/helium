/**
 * 
 */
package es.caib.helium.jbpm3.handlers;

import java.util.List;

import org.jbpm.graph.def.ActionHandler;
import org.jbpm.graph.exe.ExecutionContext;

/**
 * Interfície a implementar per a retrocedir un expedient
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface AccioExternaRetrocedirHandler extends ActionHandler {

	void retrocedir(ExecutionContext executionContext, List<String> parametres) throws Exception;

}
