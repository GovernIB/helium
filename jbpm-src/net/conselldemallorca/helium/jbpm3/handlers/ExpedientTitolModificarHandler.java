/**
 * 
 */
package net.conselldemallorca.helium.jbpm3.handlers;

import net.conselldemallorca.helium.model.dto.ExpedientDto;
import net.conselldemallorca.helium.model.hibernate.Expedient;
import net.conselldemallorca.helium.util.ExpedientIniciant;

import org.jbpm.JbpmException;
import org.jbpm.graph.exe.ExecutionContext;

/**
 * Handler per modificar el títol d'un expedient.
 * 
 * @author Josep Gayà <josepg@limit.es>
 */
@SuppressWarnings("serial")
public class ExpedientTitolModificarHandler extends AbstractHeliumActionHandler {

	private String titol;
	private String varTitol;



	public void execute(ExecutionContext executionContext) throws Exception {
		Expedient ex = ExpedientIniciant.getExpedient();
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
						expedient.getIniciadorCodi(),
						expedient.getResponsableCodi(),
						expedient.getDataInici(),
						expedient.getComentari(),
						expedient.getEstat().getId(),
						expedient.getGeoPosX(),
						expedient.getGeoPosY(),
						expedient.getGeoReferencia());
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
