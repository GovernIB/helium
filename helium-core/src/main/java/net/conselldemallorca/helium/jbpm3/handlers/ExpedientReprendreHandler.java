/**
 * 
 */
package net.conselldemallorca.helium.jbpm3.handlers;

import net.conselldemallorca.helium.core.model.dto.ExpedientDto;

import org.jbpm.JbpmException;
import org.jbpm.graph.exe.ExecutionContext;

/**
 * Handler per a reprendre la tramitació d'un expedient aturat.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@SuppressWarnings("serial")
public class ExpedientReprendreHandler extends AbstractHeliumActionHandler implements ExpedientReprendreHandlerInterface {

	public void execute(ExecutionContext executionContext) throws Exception {
		ExpedientDto expedient = getExpedient(executionContext);
		if (expedient != null) {
			try {
				getExpedientService().reprendre(
						expedient.getProcessInstanceId(),
						null);
			} catch (Exception ex) {
				throw new JbpmException("No s'ha pogut reprendre l'expedient", ex);
			}
		} else {
			throw new JbpmException("No s'ha trobat l'expedient per reprendre");
		}
	}

}
