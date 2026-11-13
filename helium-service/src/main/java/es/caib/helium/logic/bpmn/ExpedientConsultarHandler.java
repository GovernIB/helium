package es.caib.helium.logic.bpmn;

import es.caib.helium.bpmn.handler.AbstractHeliumActionHandler;
import es.caib.helium.bpmn.api.HeliumApi;
import es.caib.helium.bpmn.exception.HeliumHandlerException;
import lombok.Setter;
import org.flowable.common.engine.api.delegate.Expression;

/**
 * Implementació del handler per a consultar la informació de l'expedient actual i desar-la a dins variables.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Setter
public class ExpedientConsultarHandler extends AbstractHeliumActionHandler implements es.caib.helium.disseny.handler.ExpedientConsultarHandler {

	private Expression varRegistreNumero;
	private Expression varTitol;
	private Expression varNumero;
	private Expression varDataInici;

	@Override
	public void execute(HeliumApi heliumApi) throws HeliumHandlerException {
		heliumApi.expedientConsultar(
			getValue(varRegistreNumero),
			getValue(varTitol),
			getValue(varNumero),
			getValue(varDataInici));
	}

}
