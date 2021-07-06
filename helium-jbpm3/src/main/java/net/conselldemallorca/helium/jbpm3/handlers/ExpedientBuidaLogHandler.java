/**
 * 
 */
package net.conselldemallorca.helium.jbpm3.handlers;

import net.conselldemallorca.helium.jbpm3.integracio.Jbpm3HeliumBridge;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jbpm.JbpmException;
import org.jbpm.graph.exe.ExecutionContext;

import es.caib.helium.logic.intf.dto.ExpedientDto;

/**
 * Handler per buidar els logs d'un expedient.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@SuppressWarnings("serial")
public class ExpedientBuidaLogHandler extends AbstractHeliumActionHandler implements ExpedientBuidaLogHandlerInterface {


	public void execute(ExecutionContext executionContext) throws Exception {
		logger.debug("Inici execució handler buidar logs expedient");
		ExpedientDto expedient = getExpedientActual(executionContext);
		logger.debug("Buidant logs de l'expedient (exp=" + expedient.getIdentificacioPerLogs() + ")");
		try {
			Jbpm3HeliumBridge.getInstanceService().expedientEliminaInformacioRetroaccio(
					getProcessInstanceId(executionContext));
		} catch (Exception ex) {
			throw new JbpmException("Error al buidar els logs de l'expedient", ex);
		}
		logger.debug("Handler buidar logs expedient finalitzat amb èxit");
	}

	private static final Log logger = LogFactory.getLog(ExpedientBuidaLogHandler.class);

}
