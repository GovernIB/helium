/**
 * 
 */
package net.conselldemallorca.helium.jbpm3.handlers;

import net.conselldemallorca.helium.integracio.plugins.persones.Persona;
import net.conselldemallorca.helium.model.dto.ExpedientDto;
import net.conselldemallorca.helium.model.hibernate.Expedient;
import net.conselldemallorca.helium.util.ExpedientIniciant;

import org.jbpm.JbpmException;
import org.jbpm.graph.exe.ExecutionContext;

/**
 * Handler per modificar el responsable d'un expedient.
 * 
 * @author Josep Gayà <josepg@limit.es>
 */
@SuppressWarnings("serial")
public class ExpedientResponsableModificarHandler extends AbstractHeliumActionHandler {

	private String responsableCodi;
	private String varResponsableCodi;



	public void execute(ExecutionContext executionContext) throws Exception {
		Persona persona = getPersonaAmbCodi((String)getValorOVariable(
				executionContext,
				responsableCodi,
				varResponsableCodi));
		if (persona != null) {
			Expedient ex = ExpedientIniciant.getExpedient();
			if (ex != null) {
				ex.setResponsableCodi(persona.getCodi());
			} else {
				ExpedientDto expedient = getExpedient(executionContext);
				if (expedient != null) {
					getExpedientService().editar(
							expedient.getEntorn().getId(),
							expedient.getId(),
							expedient.getNumero(),
							expedient.getTitol(),
							expedient.getIniciadorCodi(),
							persona.getCodi(),
							expedient.getDataInici(),
							expedient.getComentari(),
							expedient.getEstat().getId());
				} else {
					throw new JbpmException("No s'ha trobat l'expedient per canviar l'estat");
				}
			}
		} else {
			throw new JbpmException("No existeix cap persona amb aquest codi '" + responsableCodi + "'");
		}
	}

	public void setResponsableCodi(String responsableCodi) {
		this.responsableCodi = responsableCodi;
	}
	public void setVarResponsableCodi(String varResponsableCodi) {
		this.varResponsableCodi = varResponsableCodi;
	}

}
