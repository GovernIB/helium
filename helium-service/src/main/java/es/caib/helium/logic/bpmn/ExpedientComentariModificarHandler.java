package es.caib.helium.logic.bpmn;

import es.caib.helium.disseny.api.HeliumApi;
import es.caib.helium.disseny.exception.HeliumHandlerException;
import es.caib.helium.disseny.handler.HeliumBpmnHandler;
import lombok.Setter;
import org.flowable.common.engine.api.delegate.Expression;
import org.flowable.common.engine.impl.el.FixedValue;
import org.flowable.engine.delegate.DelegateExecution;

/**
 * Implementació del handler per a modificar el comentari de l'expedient.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Setter
public class ExpedientComentariModificarHandler extends AbstractHeliumActionHandler implements es.caib.helium.disseny.handler.ExpedientComentariModificarHandler {

	private FixedValue comentari;
	private FixedValue varComentari;

	@Override
	public void execute(HeliumApi heliumApi) throws HeliumHandlerException {
		Object comentariObj = comentari.getValue(heliumApi.getExecutionContext());
		heliumApi.setVariable("comentari", comentariObj != null? (String) comentariObj : null);
	}

}
