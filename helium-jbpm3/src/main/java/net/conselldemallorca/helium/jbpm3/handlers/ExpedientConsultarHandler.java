/**
 * 
 */
package net.conselldemallorca.helium.jbpm3.handlers;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jbpm.JbpmException;
import org.jbpm.graph.def.ActionHandler;
import org.jbpm.graph.exe.ExecutionContext;

import net.conselldemallorca.helium.jbpm3.handlers.tipus.ExpedientInfo;

/**
 * Handler per consultar informació d'un expedient i deixar la informació en variables.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@SuppressWarnings("serial")
public class ExpedientConsultarHandler extends BasicActionHandler implements ActionHandler, ExpedientConsultarHandlerInterface {

	private String varRegistreNumero;
	private String varTitol;
	private String varNumero;
	private String varDataInici;

	public void execute(ExecutionContext executionContext) throws Exception {
		logger.debug("Inici execució handler consultar expedient");
		ExpedientInfo expedientInfo = this.getExpedient(executionContext);
		if (expedientInfo == null) {
			throw new JbpmException("No s'ha trobat l'expedient especificat amb número de registre (" + varRegistreNumero + ")");
		}
		if (varRegistreNumero != null && !varRegistreNumero.isEmpty()) {
			this.setVariableGlobal(executionContext, varRegistreNumero, expedientInfo.getRegistreNumero());
		}
		if (varTitol != null && !varTitol.isEmpty()) {
			this.setVariableGlobal(executionContext, varTitol, expedientInfo.getTitol());
		}
		if (varNumero != null && !varNumero.isEmpty()) {
			this.setVariableGlobal(executionContext, varNumero, expedientInfo.getNumero());
		}
		if (varDataInici != null) {
			executionContext.setVariable(varDataInici, new java.sql.Timestamp(expedientInfo.getDataInici().getTime()));
		}
	}

	public String getVarRegistreNumero() {
		return varRegistreNumero;
	}

	public void setVarRegistreNumero(String varRegistreNumero) {
		this.varRegistreNumero = varRegistreNumero;
	}

	public String getVarTitol() {
		return varTitol;
	}

	public void setVarTitol(String varTitol) {
		this.varTitol = varTitol;
	}

	public String getVarNumero() {
		return varNumero;
	}

	public void setVarNumero(String varNumero) {
		this.varNumero = varNumero;
	}

	public String getVarDataInici() {
		return varDataInici;
	}

	public void setVarDataInici(String varDataInici) {
		this.varDataInici = varDataInici;
	}


	private static final Log logger = LogFactory.getLog(ExpedientConsultarHandler.class);


}
