package net.conselldemallorca.helium.jbpm3.handlers;

import net.conselldemallorca.helium.api.dto.ExpedientDto;
import org.jbpm.graph.exe.ExecutionContext;

/**
 * Handler per crear un interessat
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@SuppressWarnings({"serial", "unused"})
public class InteressatEliminarHandler extends BasicActionHandler implements InteressatEliminarHandlerInterface {


	private String codi;
	private String varCodi;
	
	
	public void execute(ExecutionContext executionContext) {

		
		ExpedientDto expedient = getExpedientActual(executionContext);
		
		String codiParam = (String)getValorOVariable(
				executionContext,
				codi,
				varCodi);
	
		interessatEliminar(
				codiParam,
				expedient.getId());
		
	}

	public void setCodi(String codi) {
		this.codi = codi;
	}
	public void setVarCodi(String varCodi) {
		this.varCodi = varCodi;
	}
	
}