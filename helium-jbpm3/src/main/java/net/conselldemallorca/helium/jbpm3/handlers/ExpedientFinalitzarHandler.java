package net.conselldemallorca.helium.jbpm3.handlers;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jbpm.JbpmException;
import org.jbpm.graph.exe.ExecutionContext;

import net.conselldemallorca.helium.jbpm3.integracio.Jbpm3HeliumBridge;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientDto;

/**
 * Handler per finalitzar l'expedient.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 *
 */
public class ExpedientFinalitzarHandler extends AbstractHeliumActionHandler implements ExpedientBuidaLogHandlerInterface {

	public void execute(ExecutionContext executionContext) throws Exception {
		logger.debug("Inici execució handler finalitzar expedient");
		ExpedientDto expedient = getExpedientActual(executionContext);
		logger.debug("Finalitzant l'expedient (exp=" + expedient.getIdentificacioPerLogs() + ")");
		try {
			if(expedient != null )
				Jbpm3HeliumBridge.getInstanceService().finalitzarExpedient(expedient.getId());
		} catch (Exception ex) {
			throw new JbpmException("Error al finalitzar expedient (ID= "+expedient.getId()+")", ex);
		}
	}

	private static final Log logger = LogFactory.getLog(ExpedientBuidaLogHandler.class);
	private static final long serialVersionUID = 1L;
}
