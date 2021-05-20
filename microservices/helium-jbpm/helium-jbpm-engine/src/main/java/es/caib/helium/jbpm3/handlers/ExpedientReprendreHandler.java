/**
 * 
 */
package es.caib.helium.jbpm3.handlers;

import es.caib.helium.api.dto.ExpedientDto;
import es.caib.helium.jbpm3.integracio.Jbpm3HeliumBridge;
import net.conselldemallorca.helium.jbpm3.handlers.ExpedientReprendreHandlerInterface;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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
		logger.debug("Inici execució handler reprendre expedient");
		ExpedientDto expedient = getExpedientActual(executionContext);
		logger.debug("Reprenent l'expedient (exp=" + expedient.getIdentificacioPerLogs() + ")");
		try {
			Jbpm3HeliumBridge.getInstanceService().expedientReprendre(
					getProcessInstanceId(executionContext));
		} catch (Exception ex) {
			throw new JbpmException("Error al modificar l'expedient", ex);
		}
		logger.debug("Handler reprendre expedient finalitzat amb èxit");
	}

	private static final Log logger = LogFactory.getLog(ExpedientReprendreHandler.class);

}
