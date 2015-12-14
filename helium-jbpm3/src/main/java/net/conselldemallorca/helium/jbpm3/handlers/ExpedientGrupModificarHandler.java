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
 * Handler per modificar l'estat d'un expedient.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@SuppressWarnings("serial")
public class ExpedientGrupModificarHandler extends AbstractHeliumActionHandler implements ExpedientGrupModificarHandlerInterface {

	private String grup;
	private String varGrup;

	public void execute(ExecutionContext executionContext) throws Exception {
		logger.debug("Inici execució handler modificació grup expedient");
		String g = (String)getValorOVariable(
				executionContext,
				grup,
				varGrup);
		ExpedientDto expedient = getExpedientActual(executionContext);
		logger.debug("Modificant grup de l'expedient (exp=" + expedient.getIdentificacioPerLogs() + ", grup=" + g + ")");
		try {
			Jbpm3HeliumBridge.getInstanceService().expedientModificarGrup(
					getProcessInstanceId(executionContext),
					g);
		} catch (Exception ex) {
			throw new JbpmException("Error al modificar l'expedient", ex);
		}
		logger.debug("Handler modificació grup finalitzat amb èxit");
	}

	public void setGrup(String grup) {
		this.grup = grup;
	}
	public void setVarGrup(String varGrup) {
		this.varGrup = varGrup;
	}

	private static final Log logger = LogFactory.getLog(ExpedientGrupModificarHandler.class);

}
