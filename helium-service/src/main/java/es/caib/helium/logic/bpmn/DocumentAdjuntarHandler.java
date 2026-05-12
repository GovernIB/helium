package es.caib.helium.logic.bpmn;

import es.caib.helium.disseny.api.HeliumApi;
import es.caib.helium.disseny.exception.HeliumHandlerException;
import lombok.Setter;

/**
 * Implementació del handler per a adjuntar un document a l'expedient.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Setter
public class DocumentAdjuntarHandler implements es.caib.helium.disseny.handler.DocumentAdjuntarHandler {

	private String documentOrigen;
	private String varDocumentOrigen;
	private String titol;
	private String varTitol;
	private String data;
	private String varData;
	private String concatenarTitol;
	private String esborrarDocument;

	@Override
	public void execute(HeliumApi heliumApi) throws HeliumHandlerException {
		heliumApi.documentAdjuntar(
			heliumApi.getVariableDefaultValue(varDocumentOrigen, documentOrigen),
			heliumApi.getVariableDefaultValue(varTitol, titol),
			heliumApi.getVariableDefaultValueAsDate(varData, data),
			"true".equals(concatenarTitol),
			"true".equals(esborrarDocument));
	}

}
