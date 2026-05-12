package es.caib.helium.logic.bpmn;

import es.caib.helium.disseny.api.HeliumApi;
import es.caib.helium.disseny.exception.HeliumHandlerException;
import lombok.Setter;

/**
 * Implementació del handler per a modificar l'estat de l'expedient.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Setter
public class ExpedientEstatModificarHandler implements es.caib.helium.disseny.handler.ExpedientEstatModificarHandler {

	private String estatCodi;
	private String varEstatCodi;

	@Override
	public void execute(HeliumApi heliumApi) throws HeliumHandlerException {
		heliumApi.expedientEstatModificar(
			heliumApi.getVariableDefaultValue(varEstatCodi, estatCodi));
	}

}
