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
 * Handler per modificar el títol d'un expedient.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@SuppressWarnings("serial")
public class ExpedientTitolModificarHandler extends AbstractHeliumActionHandler implements ExpedientTitolModificarHandlerInterface {

	private String titol;
	private String varTitol;



	public void execute(ExecutionContext executionContext) throws Exception {
		logger.debug("Inici execució handler modificació títol expedient");
		String t = (String)getValorOVariable(
				executionContext,
				titol,
				varTitol);
		ExpedientDto expedient = getExpedientActual(executionContext);
		logger.debug("Modificant títol de l'expedient (exp=" + expedient.getIdentificacioPerLogs() + ", titol=" + t + ")");
		try {
			Jbpm3HeliumBridge.getInstance().expedientModificarTitol(
					getProcessInstanceId(executionContext),
					t);
		} catch (Exception ex) {
			throw new JbpmException("Error al modificar l'expedient", ex);
		}
		logger.debug("Handler modificació títol finalitzat amb èxit");
	}

	public void setTitol(String titol) {
		this.titol = titol;
	}
	public void setVarTitol(String varTitol) {
		this.varTitol = varTitol;
	}

	private static final Log logger = LogFactory.getLog(ExpedientTitolModificarHandler.class);

}
