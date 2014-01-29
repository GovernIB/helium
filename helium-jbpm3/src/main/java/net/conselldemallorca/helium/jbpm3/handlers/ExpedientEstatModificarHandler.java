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
public class ExpedientEstatModificarHandler extends AbstractHeliumActionHandler implements ExpedientEstatModificarHandlerInterface {

	private String estatCodi;
	private String varEstatCodi;



	public void execute(ExecutionContext executionContext) throws Exception {
		logger.debug("Inici execució handler modificació estat expedient");
		String ec = (String)getValorOVariable(
				executionContext,
				estatCodi,
				varEstatCodi);
		ExpedientDto expedient = getExpedientActual(executionContext);
		String estatActualCodi = "null";
		if (expedient.getEstat() != null)
			estatActualCodi = expedient.getEstat().getCodi();
		logger.debug("Modificant estat de l'expedient (exp=" + expedient.getIdentificacioPerLogs() + ", estatActual=" + estatActualCodi + ", estatNou=" + ec + ")");
		try {
			Jbpm3HeliumBridge.getInstanceService().expedientModificarEstat(
					getProcessInstanceId(executionContext),
					ec);
		} catch (Exception ex) {
			throw new JbpmException("Error al modificar l'expedient", ex);
		}
		logger.debug("Handler modificació estat finalitzat amb èxit");
	}

	public void setEstatCodi(String estatCodi) {
		this.estatCodi = estatCodi;
	}
	public void setVarEstatCodi(String varEstatCodi) {
		this.varEstatCodi = varEstatCodi;
	}

	private static final Log logger = LogFactory.getLog(ExpedientEstatModificarHandler.class);

}
