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
 * Handler per modificar l'estat d'un expedient.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@SuppressWarnings("serial")
public class ExpedientGrupModificarHandler extends AbstractHeliumActionHandler implements ExpedientGrupModificarHandlerInterface {

	private String grup;
	private String varGrup;



	public void execute(ExecutionContext executionContext) throws Exception {
		Expedient ex = ExpedientIniciantDto.getExpedient();
		String gc = (String)getValorOVariable(executionContext, grup, varGrup);
		if (ex != null) {
			ex.setGrupCodi(gc);
		} else {
			ExpedientDto expedient = getExpedient(executionContext);
			if (expedient != null) {
				getExpedientService().editar(
						expedient.getEntorn().getId(),
						expedient.getId(),
						expedient.getNumero(),
						expedient.getTitol(),
						expedient.getResponsableCodi(),
						expedient.getDataInici(),
						expedient.getComentari(),
						expedient.getEstat().getId(),
						expedient.getGeoPosX(),
						expedient.getGeoPosY(),
						expedient.getGeoReferencia(),
						gc);
			} else {
				throw new JbpmException("No s'ha trobat l'expedient per canviar l'estat");
			}
		}
	}

	public void setGrup(String grup) {
		this.grup = grup;
	}
	public void setVarGrup(String varGrup) {
		this.varGrup = varGrup;
	}

}
