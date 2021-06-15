/**
 * 
 */
package net.conselldemallorca.helium.jbpm3.handlers;

import net.conselldemallorca.helium.api.dto.ExpedientDto;
import net.conselldemallorca.helium.jbpm3.integracio.Jbpm3HeliumBridge;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jbpm.JbpmException;
import org.jbpm.graph.exe.ExecutionContext;

/**
 * Handler per modificar el número d'un expedient.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@SuppressWarnings("serial")
public class ExpedientNumeroModificarHandler extends AbstractHeliumActionHandler implements ExpedientNumeroModificarHandlerInterface {

	private String numero;
	private String varNumero;

	public void execute(ExecutionContext executionContext) throws Exception {
		logger.debug("Inici execució handler modificació número expedient");
		String n = (String)getValorOVariable(
				executionContext,
				numero,
				varNumero);
		ExpedientDto expedient = getExpedientActual(executionContext);
		ExpedientDto expedientRepetit = findExpedientAmbMateixTipusINumero(executionContext, n);
		if (expedientRepetit != null && expedientRepetit.getId().longValue() != expedient.getId().longValue())
			throw new JbpmException("Ja existeix un altre expedient d'aquest tipus amb el mateix número (" + n + ")");
		logger.debug("Modificant número de l'expedient (expedient=" + expedient.getIdentificacioPerLogs() + ", número=" + n + ")");
		try {
			Jbpm3HeliumBridge.getInstanceService().expedientModificarNumero(
					getProcessInstanceId(executionContext),
					n);
		} catch (Exception ex) {
			throw new JbpmException("Error al modificar l'expedient", ex);
		}
		logger.debug("Handler modificació número finalitzat amb èxit");
	}

	public void setNumero(String numero) {
		this.numero = numero;
	}
	public void setVarNumero(String varNumero) {
		this.varNumero = varNumero;
	}

	private static final Log logger = LogFactory.getLog(ExpedientNumeroModificarHandler.class);

}
