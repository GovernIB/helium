package es.caib.helium.logic.bpmn;

import es.caib.helium.bpmn.handler.AbstractHeliumActionHandler;
import es.caib.helium.bpmn.api.HeliumApi;
import es.caib.helium.bpmn.exception.HeliumHandlerException;
import lombok.Setter;
import org.flowable.common.engine.api.delegate.Expression;

/**
 * Implementació del handler per a modificar el grup d'un expedient.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Setter
public class ExpedientGrupModificarHandler extends AbstractHeliumActionHandler implements es.caib.helium.disseny.handler.ExpedientGrupModificarHandler {

	private Expression grup;
	private Expression varGrup;

	@Override
	public void execute(HeliumApi heliumApi) throws HeliumHandlerException {
		heliumApi.expedientGrupModificar(
			heliumApi.getVariableDefaultValue(
				getValue(varGrup),
				getValue(grup))
		);
	}

}
