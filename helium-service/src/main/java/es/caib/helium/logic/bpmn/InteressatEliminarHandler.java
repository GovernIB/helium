package es.caib.helium.logic.bpmn;

import es.caib.helium.disseny.api.HeliumApi;
import es.caib.helium.disseny.exception.HeliumHandlerException;
import lombok.Setter;

/**
 * Implementació del handler per a eliminar interessats d'un expedient.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Setter
public class InteressatEliminarHandler implements es.caib.helium.disseny.handler.InteressatEliminarHandler {

	private String codi;
	private String varCodi;

	@Override
	public void execute(HeliumApi heliumApi) throws HeliumHandlerException {
		heliumApi.interessatEliminar(
			heliumApi.getVariableDefaultValue(varCodi, codi));
	}

}
