package es.caib.helium.logic.bpmn;

import es.caib.helium.bpmn.api.HeliumApi;
import es.caib.helium.bpmn.exception.HeliumHandlerException;
import lombok.Setter;

/**
 * Implementació del handler per a consultar la informació de l'expedient actual i desar-la a dins variables.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Setter
public class ExpedientConsultarHandler implements es.caib.helium.bpmn.handler.ExpedientConsultarHandler {

	private String varRegistreNumero;
	private String varTitol;
	private String varNumero;
	private String varDataInici;

	@Override
	public void execute(HeliumApi heliumApi) throws HeliumHandlerException {
		heliumApi.expedientConsultar(
			varRegistreNumero,
			varTitol,
			varNumero,
			varDataInici);
	}

}
