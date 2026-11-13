package es.caib.helium.logic.bpmn;

import es.caib.helium.bpmn.api.HeliumApi;
import es.caib.helium.bpmn.exception.HeliumHandlerException;
import es.caib.helium.bpmn.handler.AbstractHeliumActionHandler;

import lombok.Setter;
import org.flowable.common.engine.api.delegate.Expression;

/**
 * Implementació del handler per a esborrar un document de l'expedient.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Setter
public class DocumentEsborrarHandler extends AbstractHeliumActionHandler implements es.caib.helium.disseny.handler.DocumentEsborrarHandler {

	private Expression documentCodi;
	private Expression varDocumentCodi;

	@Override
	public void execute(HeliumApi heliumApi) throws HeliumHandlerException {
		heliumApi.deleteDocument(
			heliumApi.getVariableDefaultValue(
				getValue(varDocumentCodi),
				getValue(documentCodi))
		);
	}

}
