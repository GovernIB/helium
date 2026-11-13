package es.caib.helium.logic.bpmn;

import es.caib.helium.bpmn.handler.AbstractHeliumActionHandler;
import es.caib.helium.bpmn.api.HeliumApi;
import es.caib.helium.bpmn.exception.HeliumHandlerException;
import lombok.Setter;
import org.flowable.common.engine.api.delegate.Expression;

/**
 * Implementació del handler per a modificar el número d'un expedient.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Setter
public class ExpedientNumeroModificarHandler extends AbstractHeliumActionHandler implements es.caib.helium.disseny.handler.ExpedientNumeroModificarHandler {

	private Expression numero;
	private Expression varNumero;

	@Override
	public void execute(HeliumApi heliumApi) throws HeliumHandlerException {
		heliumApi.expedientNumeroModificar(
			heliumApi.getVariableDefaultValue(
				getValue(varNumero),
				getValue(numero))
        );
	}

}
