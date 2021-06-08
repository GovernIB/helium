package net.conselldemallorca.helium.jbpm3.handlers;

import es.caib.helium.api.dto.ExpedientDto;
import es.caib.helium.jbpm3.integracio.Jbpm3HeliumBridge;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jbpm.JbpmException;
import org.jbpm.graph.exe.ExecutionContext;

/**
 * Handler per finalitzar l'expedient.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 *
 */
public class ExpedientFinalitzarHandler extends AbstractHeliumActionHandler implements ExpedientFinalitzarHandlerInterface {

	public void execute(ExecutionContext executionContext) throws Exception {
		logger.debug("Inici execució handler finalitzar expedient");
		ExpedientDto expedient = getExpedientActual(executionContext);
		logger.debug("Finalitzant l'expedient (exp=" + expedient.getIdentificacioPerLogs() + ")");
		try {
			if(expedient != null )
				Jbpm3HeliumBridge.getInstanceService().finalitzarExpedient(expedient.getProcessInstanceId());
		} catch (Exception ex) {
			throw new JbpmException("Error al finalitzar expedient (ID= "+expedient.getId()+")", ex);
		}
	}

	private static final Log logger = LogFactory.getLog(ExpedientBuidaLogHandler.class);
	private static final long serialVersionUID = 1L;
}
