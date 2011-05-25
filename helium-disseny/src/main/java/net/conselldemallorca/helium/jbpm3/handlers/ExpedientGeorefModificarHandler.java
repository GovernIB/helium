/**
 * 
 */
package net.conselldemallorca.helium.jbpm3.handlers;

import net.conselldemallorca.helium.jbpm3.handlers.ExpedientGeorefModificarHandlerInterface;

import org.jbpm.graph.def.ActionHandler;
import org.jbpm.graph.exe.ExecutionContext;

/**
 * Handler per modificar la georeferència de l'expedient.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@SuppressWarnings("serial")
public class ExpedientGeorefModificarHandler implements ActionHandler, ExpedientGeorefModificarHandlerInterface {

	public void execute(ExecutionContext executionContext) throws Exception {}

	public void setPosx(String posx) {}
	public void setVarPosx(String varPosx) {}
	public void setPosy(String posy) {}
	public void setVarPosy(String varPosy) {}
	public void setReferencia(String referencia) {}
	public void setVarReferencia(String varReferencia) {}

}
