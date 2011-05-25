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
 * Handler per modificar el número d'un expedient.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@SuppressWarnings("serial")
public class ExpedientNumeroModificarHandler extends AbstractHeliumActionHandler implements ExpedientNumeroModificarHandlerInterface {

	private String numero;
	private String varNumero;



	public void execute(ExecutionContext executionContext) throws Exception {
		Expedient ex = ExpedientIniciantDto.getExpedient();
		String n = (String)getValorOVariable(executionContext, numero, varNumero);
		if (ex != null) {
			ex.setNumero(n);
		} else {
			ExpedientDto expedient = getExpedient(executionContext);
			if (expedient != null) {
				getExpedientService().editar(
						expedient.getEntorn().getId(),
						expedient.getId(),
						n,
						expedient.getTitol(),
						expedient.getIniciadorCodi(),
						expedient.getResponsableCodi(),
						expedient.getDataInici(),
						expedient.getComentari(),
						expedient.getEstat().getId(),
						expedient.getGeoPosX(),
						expedient.getGeoPosY(),
						expedient.getGeoReferencia());
			} else {
				throw new JbpmException("No s'ha trobat l'expedient per canviar el número");
			}
		}
	}

	public void setNumero(String numero) {
		this.numero = numero;
	}
	public void setVarNumero(String varNumero) {
		this.varNumero = varNumero;
	}

}
