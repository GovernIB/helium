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
import es.caib.helium.logic.intf.dto.PersonaDto;

/**
 * Handler per modificar el responsable d'un expedient.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@SuppressWarnings("serial")
public class ExpedientResponsableModificarHandler extends AbstractHeliumActionHandler implements ExpedientResponsableModificarHandlerInterface {

	private String responsableCodi;
	private String varResponsableCodi;



	public void execute(ExecutionContext executionContext) throws Exception {
		logger.debug("Inici execució handler modificació responsable expedient");
		String personaCodi = (String)getValorOVariable(
				executionContext,
				responsableCodi,
				varResponsableCodi);
		PersonaDto persona = Jbpm3HeliumBridge.getInstanceService().getPersonaAmbCodi(personaCodi);
		if (persona != null) {
			ExpedientDto expedient = getExpedientActual(executionContext);
			logger.debug("Modificant responsable de l'expedient (exp=" + expedient.getIdentificacioPerLogs() + ", responsable=" + personaCodi + ")");
			try {
				Jbpm3HeliumBridge.getInstanceService().expedientModificarResponsable(
						getProcessInstanceId(executionContext),
						persona.getCodi());
			} catch (Exception ex) {
				throw new JbpmException("Error al modificar l'expedient", ex);
			}
			logger.debug("Handler modificació responsable finalitzat amb èxit");
		} else {
			throw new JbpmException("No existeix cap persona amb aquest codi '" + personaCodi + "'");
		}
	}

	public void setResponsableCodi(String responsableCodi) {
		this.responsableCodi = responsableCodi;
	}
	public void setVarResponsableCodi(String varResponsableCodi) {
		this.varResponsableCodi = varResponsableCodi;
	}

	private static final Log logger = LogFactory.getLog(ExpedientComentariModificarHandler.class);

}
