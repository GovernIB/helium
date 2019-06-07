/**
 * 
 */
package net.conselldemallorca.helium.jbpm3.handlers;

import org.jbpm.graph.def.ActionHandler;
import org.jbpm.graph.exe.ExecutionContext;

/**
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface InteressatModificarHandlerInterface extends ActionHandler {

	public void execute(ExecutionContext executionContext) throws Exception;

	public void setCodi(String codi);
	public void setVarCodi(String varCodi);
	public void setNom(String nom);
	public void setVarNom(String varNom);
	public void setNif(String nif);
	public void setVarNif(String varNif);
	public void setLlinatge1(String llinatge1);
	public void setVarLlinatge1(String varLlinatge1);
	public void setLlinatge2(String llinatge2);
	public void setVarLlinatge2(String varLlinatge2);
	public void setTipus(String tipus);
	public void setVarTipus(String varTipus);
	public void setEmail(String email);
	public void setVarEmail(String varEmail);
	public void setTelefon(String telefon);
	public void setVarTelefon(String varTelefon);

}
