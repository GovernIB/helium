package es.caib.helium.logic.bpmn;

import es.caib.helium.disseny.api.HeliumApi;
import es.caib.helium.disseny.exception.HeliumHandlerException;
import lombok.Setter;

/**
 * Implementació del handler per a modificar el comentari de l'expedient.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Setter
public class ExpedientComentariModificarHandler implements es.caib.helium.disseny.handler.ExpedientComentariModificarHandler {

	private String comentari;
	private String varComentari;

	@Override
	public void execute(HeliumApi heliumApi) throws HeliumHandlerException {
		heliumApi.expedientComentariModificar(
			heliumApi.getVariableDefaultValue(varComentari, comentari));
	}

}
