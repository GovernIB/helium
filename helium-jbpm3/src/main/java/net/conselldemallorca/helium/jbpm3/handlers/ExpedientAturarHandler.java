/**
 * 
 */
package net.conselldemallorca.helium.jbpm3.handlers;

import net.conselldemallorca.helium.jbpm3.integracio.Jbpm3HeliumBridge;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientDto;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jbpm.JbpmException;
import org.jbpm.graph.exe.ExecutionContext;

/**
 * Handler per aturar un expedient.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@SuppressWarnings("serial")
public class ExpedientAturarHandler extends AbstractHeliumActionHandler implements ExpedientAturarHandlerInterface {

	private String motiu;
	private String varMotiu;



	public void execute(ExecutionContext executionContext) throws Exception {
		logger.debug("Inici execució handler aturar expedient");
		String m = (String)getValorOVariable(
				executionContext,
				motiu,
				varMotiu);
		ExpedientDto expedient = getExpedientActual(executionContext);
		logger.debug("Aturant l'expedient (exp=" + expedient.getIdentificacioPerLogs() + ", motiu=" + m + ")");
		try {
			Jbpm3HeliumBridge.getInstanceService().expedientAturar(
					getProcessInstanceId(executionContext),
					m);
		} catch (Exception ex) {
			throw new JbpmException("Error al modificar l'expedient", ex);
		}
		logger.debug("Handler aturar expedient finalitzat amb èxit");
	}

	public void setMotiu(String motiu) {
		this.motiu = motiu;
	}
	public void setVarMotiu(String varMotiu) {
		this.varMotiu = varMotiu;
	}

	private static final Log logger = LogFactory.getLog(ExpedientAturarHandler.class);

}
