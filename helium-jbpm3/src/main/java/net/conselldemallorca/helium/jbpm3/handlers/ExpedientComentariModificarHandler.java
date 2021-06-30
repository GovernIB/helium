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
 * Handler per modificar el comentari d'un expedient.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@SuppressWarnings("serial")
public class ExpedientComentariModificarHandler extends AbstractHeliumActionHandler implements ExpedientComentariModificarHandlerInterface {

	private String comentari;
	private String varComentari;



	public void execute(ExecutionContext executionContext) throws Exception {
		logger.debug("Inici execució handler modificació comentari expedient");
		String c = (String)getValorOVariable(
				executionContext,
				comentari,
				varComentari);
		ExpedientDto expedient = getExpedientActual(executionContext);
		logger.debug("Modificant comentari de l'expedient (exp=" + expedient.getIdentificacioPerLogs() + ", comentari=" + c + ")");
		try {
			Jbpm3HeliumBridge.getInstanceService().expedientModificarComentari(
					getProcessInstanceId(executionContext),
					c);
		} catch (Exception ex) {
			throw new JbpmException("Error al modificar l'expedient", ex);
		}
		logger.debug("Handler modificació de comentari finalitzat amb èxit");
	}

	public void setComentari(String comentari) {
		this.comentari = comentari;
	}
	public void setVarComentari(String varComentari) {
		this.varComentari = varComentari;
	}

	private static final Log logger = LogFactory.getLog(ExpedientComentariModificarHandler.class);

}
