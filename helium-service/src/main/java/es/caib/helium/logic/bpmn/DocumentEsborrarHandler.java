package es.caib.helium.logic.bpmn;

import es.caib.helium.disseny.api.HeliumApi;
import es.caib.helium.disseny.exception.HeliumHandlerException;
import lombok.Setter;

/**
 * Implementació del handler per a esborrar un document de l'expedient.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Setter
public class DocumentEsborrarHandler implements es.caib.helium.disseny.handler.DocumentEsborrarHandler {

	private String documentCodi;
	private String varDocumentCodi;

	@Override
	public void execute(HeliumApi heliumApi) throws HeliumHandlerException {
		heliumApi.deleteDocument(
			heliumApi.getVariableDefaultValue(varDocumentCodi, documentCodi));
	}

}
