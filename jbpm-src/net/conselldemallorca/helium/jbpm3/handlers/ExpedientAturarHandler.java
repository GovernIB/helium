/**
 * 
 */
package net.conselldemallorca.helium.jbpm3.handlers;

import net.conselldemallorca.helium.model.dto.ExpedientDto;

import org.jbpm.JbpmException;
import org.jbpm.graph.exe.ExecutionContext;

/**
 * Handler per aturar un expedient.
 * 
 * @author Josep Gayà <josepg@limit.es>
 */
@SuppressWarnings("serial")
public class ExpedientAturarHandler extends AbstractHeliumActionHandler {

	private String motiu;
	private String varMotiu;



	public void execute(ExecutionContext executionContext) throws Exception {
		ExpedientDto expedient = getExpedient(executionContext);
		String m = (String)getValorOVariable(executionContext, motiu, varMotiu);
		if (expedient != null) {
			try {
				getExpedientService().aturar(
						expedient.getProcessInstanceId(),
						m);
			} catch (Exception ex) {
				throw new JbpmException("No s'ha pogut aturar l'expedient", ex);
			}
		} else {
			throw new JbpmException("No s'ha trobat l'expedient per aturar");
		}
	}

	public void setMotiu(String motiu) {
		this.motiu = motiu;
	}
	public void setVarMotiu(String varMotiu) {
		this.varMotiu = varMotiu;
	}

}
