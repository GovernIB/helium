/**
 * 
 */
package net.conselldemallorca.helium.jbpm3.handlers;

import net.conselldemallorca.helium.core.model.dto.ExpedientDto;
import net.conselldemallorca.helium.core.model.dto.ExpedientIniciantDto;
import net.conselldemallorca.helium.core.model.dto.PersonaDto;
import net.conselldemallorca.helium.core.model.hibernate.Expedient;

import org.jbpm.JbpmException;
import org.jbpm.graph.exe.ExecutionContext;

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
		PersonaDto persona = getPersonaAmbCodi((String)getValorOVariable(
				executionContext,
				responsableCodi,
				varResponsableCodi));
		if (persona != null) {
			Expedient ex = ExpedientIniciantDto.getExpedient();
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
							persona.getCodi(),
							expedient.getDataInici(),
							expedient.getComentari(),
							(expedient.getEstat() != null) ? expedient.getEstat().getId() : null,
							expedient.getGeoPosX(),
							expedient.getGeoPosY(),
							expedient.getGeoReferencia());
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
