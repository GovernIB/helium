package es.caib.helium.logic.bpmn;

import es.caib.helium.disseny.api.HeliumApi;
import es.caib.helium.disseny.exception.HeliumHandlerException;
import lombok.Setter;

/**
 * Implementació del handler per a modificar el número d'un expedient.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Setter
public class ExpedientNumeroModificarHandler implements es.caib.helium.disseny.handler.ExpedientNumeroModificarHandler {

	private String numero;
	private String varNumero;

	@Override
	public void execute(HeliumApi heliumApi) throws HeliumHandlerException {
		heliumApi.expedientNumeroModificar(heliumApi.getVariableDefaultValue(varNumero, numero));
	}

}
