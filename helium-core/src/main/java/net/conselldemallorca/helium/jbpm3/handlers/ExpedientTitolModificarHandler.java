/**
 * 
 */
package net.conselldemallorca.helium.jbpm3.handlers;

import net.conselldemallorca.helium.core.model.dto.ExpedientDto;
import net.conselldemallorca.helium.core.model.dto.ExpedientIniciantDto;
import net.conselldemallorca.helium.core.model.hibernate.Expedient;

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
		Expedient ex = ExpedientIniciantDto.getExpedient();
		String t = (String)getValorOVariable(executionContext, titol, varTitol);
		if (ex != null) {
			ex.setTitol(t);
		} else {
			ExpedientDto expedient = getExpedient(executionContext);
			if (expedient != null) {
				getExpedientService().editar(
						expedient.getEntorn().getId(),
						expedient.getId(),
						expedient.getNumero(),
						t,
						expedient.getResponsableCodi(),
						expedient.getDataInici(),
						expedient.getComentari(),
						(expedient.getEstat() != null) ? expedient.getEstat().getId() : null,
						expedient.getGeoPosX(),
						expedient.getGeoPosY(),
						expedient.getGeoReferencia(),
						expedient.getGrupCodi(),
						true);
			} else {
				throw new JbpmException("No s'ha trobat l'expedient per canviar el títol");
			}
		}
	}

	public void setTitol(String titol) {
		this.titol = titol;
	}
	public void setVarTitol(String varTitol) {
		this.varTitol = varTitol;
	}

}
